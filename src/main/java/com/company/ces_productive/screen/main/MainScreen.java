package com.company.ces_productive.screen.main;

import com.company.ces_productive.entity.Branches;
import com.company.ces_productive.entity.Documentation;
import com.company.ces_productive.entity.ManagerTasks;
import com.company.ces_productive.entity.User;
import com.company.ces_productive.entity.courses.CourseType;
import com.company.ces_productive.entity.courses.Courses;
import com.company.ces_productive.screen.courses.calendar.*;
import com.vaadin.v7.shared.ui.calendar.CalendarState;
import io.jmix.core.DataManager;
import io.jmix.core.TimeSource;
import io.jmix.core.metamodel.datatype.DatatypeFormatter;

import io.jmix.core.security.CurrentAuthentication;
import io.jmix.ui.ScreenTools;
import io.jmix.ui.UiComponents;
import io.jmix.ui.component.*;
import io.jmix.ui.component.Calendar;
import io.jmix.ui.component.mainwindow.Drawer;
import io.jmix.ui.icon.JmixIcon;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.navigation.Route;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.company.ces_productive.screen.courses.calendar.CalendarNavigationMode.*;

@UiController("CES_MainScreen")
@UiDescriptor("main-screen.xml")
@Route(path = "main", root = true)
public class MainScreen extends Screen implements Window.HasWorkArea {

    @Autowired
    private ScreenTools screenTools;
    @Autowired
    private AppWorkArea workArea;
    @Autowired
    private Drawer drawer;
    @Autowired
    private Button collapseDrawerButton;
    @Autowired
    private CollectionLoader<Courses> coursesCalendarDl;
    @Autowired
    private Calendar<LocalDateTime> calendar;
    @Autowired
    private RadioButtonGroup<CalendarMode> calendarMode;
    @Autowired
    private TimeSource timeSource;
    @Autowired
    private CalendarNavigators calendarNavigators;
    @Autowired
    private DatatypeFormatter datatypeFormatter;
    @Autowired
    private Label calendarTitle;
    @Autowired
    private DatePicker<LocalDate> calendarNavigator;
    @Autowired
    private CheckBoxGroup<CourseType> typeMultiFilter;
    @Autowired
    private CollectionLoader<ManagerTasks> managerTasksesDl;
    @Autowired
    private UiComponents uiComponents;
    @Autowired
    private CollectionLoader<Documentation> documentationsDl;
    @Autowired
    private CurrentAuthentication currentAuthentication;
    @Autowired
    private DataManager dataManager;

    @Override
    public AppWorkArea getWorkArea() {
        return workArea;
    }

    @Subscribe("collapseDrawerButton")
    private void onCollapseDrawerButtonClick(Button.ClickEvent event) {
        drawer.toggle();
        if (drawer.isCollapsed()) {
            collapseDrawerButton.setIconFromSet(JmixIcon.CHEVRON_RIGHT);
        } else {
            collapseDrawerButton.setIconFromSet(JmixIcon.CHEVRON_LEFT);
        }
    }

    @Subscribe
    public void onAfterShow(AfterShowEvent event) {
        screenTools.openDefaultScreen(
                UiControllerUtils.getScreenContext(this).getScreens());
        screenTools.handleRedirect();
    }

    @Subscribe
    protected void onInit(InitEvent event) {
         initTypeFilter();
         initSortCalendarEventsInMonthlyView();
    }

    private void initTypeFilter() {
        typeMultiFilter.setOptionsEnum(CourseType.class);
        typeMultiFilter.setValue(EnumSet.allOf(CourseType.class));
        typeMultiFilter.setOptionIconProvider(o -> CourseTypeIcon.valueOf(o.getIcon()).source());
    }

    private void initSortCalendarEventsInMonthlyView() {
        calendar.unwrap(com.vaadin.v7.ui.Calendar.class)
                .setEventSortOrder(CalendarState.EventSortOrder.START_DATE_DESC);
    }

    public List<String> getUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        return roles;
    }

    public List<User> getCurrUser() {
        return dataManager.load(User.class)
                .query("select u from CES_User u where u = :currUserBranch")
                .parameter("currUserBranch", currentAuthentication.getUser())
                .list();
    }
    public List<User> getCurrUsers() {
        return dataManager.load(User.class)
                .query("select u from CES_User u")
                .list();
    }

    public List<Branches> getCurrBranch() {
        return dataManager.load(Branches.class)
                .query("select b from CES_Branches b where b = :currBranch")
                .parameter("currBranch", ((User) currentAuthentication.getUser()).getBranch())
                .list();
    }
    public List<Branches> getCurrBranches() {
        return dataManager.load(Branches.class)
                .query("select b from CES_Branches b")
                .list();
    }

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        if (getUserRoles().contains("teacher")) {
            coursesCalendarDl.setParameter("currBranch", getCurrBranches());
            coursesCalendarDl.setParameter("currUser", getCurrUser());
            coursesCalendarDl.setParameter("courseStartDate", LocalDateTime.now().minusDays(3));
            coursesCalendarDl.setParameter("courseEndDate", LocalDateTime.now().plusDays(3));
        }
        else {
            coursesCalendarDl.setParameter("currBranch", getCurrBranch());
            coursesCalendarDl.setParameter("currUser", getCurrUsers());
            coursesCalendarDl.setParameter("courseStartDate", LocalDateTime.now().minusDays(3));
            coursesCalendarDl.setParameter("courseEndDate", LocalDateTime.now().plusDays(3));
        }
        coursesCalendarDl.removeParameter("type");
        coursesCalendarDl.load();

        if (getUserRoles().contains("manager")) {
            managerTasksesDl.setParameter("currRole", "manager");
        }
        else {
            managerTasksesDl.setParameter("currRole", "admin");
        }
        managerTasksesDl.load();

        if (getUserRoles().contains("manager")) {
            documentationsDl.setParameter("currRole", "manager");
        }
        else if (getUserRoles().contains("teacher")) {
            documentationsDl.setParameter("currRole", "teacher");
        }
        else if (getUserRoles().contains("accountant")) {
            documentationsDl.setParameter("currRole", "accountant");
        }
        else if (getUserRoles().contains("hr")) {
            documentationsDl.setParameter("currRole", "hr");
        }
        else if (getUserRoles().contains("hr")) {
            documentationsDl.setParameter("currRole", "metodist");
        }
        else {
            documentationsDl.setParameter("currRole", "admin");
        }
        documentationsDl.load();

        current(CalendarMode.WEEK);
    }

    @Install(to = "managerTasksesTable.completed", subject = "columnGenerator")
    private Component managerTasksesTableCompletedColumnGenerator(ManagerTasks managerTasks) {
        CheckBox completed = uiComponents.create(CheckBox.class);
        completed.setValue(managerTasks.getTaskName() == null);
        return completed;
    }

    @Subscribe("navigatorPrevious")
    protected void onNavigatorPreviousClick(Button.ClickEvent event) {
        previous(calendarMode.getValue());
    }

    @Subscribe("navigatorNext")
    protected void onNavigatorNextClick(Button.ClickEvent event) {
        next(calendarMode.getValue());
    }

    @Subscribe("navigatorCurrent")
    protected void onNavigatorCurrentClick(Button.ClickEvent event) {
        current(calendarMode.getValue());
    }

    private void previous(CalendarMode calendarMode) {
        change(calendarMode, PREVIOUS, calendarNavigator.getValue());
    }

    private void next(CalendarMode calendarMode) {
        change(calendarMode, NEXT, calendarNavigator.getValue());
    }

    private void current(CalendarMode calendarMode) {
        change(calendarMode, AT_DATE, timeSource.now().toLocalDate());
    }

    private void change(CalendarMode calendarMode, CalendarNavigationMode navigationMode, LocalDate referenceDate) {
        this.calendarMode.setValue(calendarMode);

        calendarNavigators
                .forMode(
                        CalendarScreenAdjustment.of(calendar, calendarNavigator, calendarTitle),
                        datatypeFormatter,
                        calendarMode
                )
                .navigate(navigationMode, referenceDate);

        loadEvents();
    }

    private void loadEvents() {
        if (getUserRoles().contains("teacher")) {
            coursesCalendarDl.setParameter("currBranch", getCurrBranches());
            coursesCalendarDl.setParameter("currUser", getCurrUser());
            coursesCalendarDl.setParameter("courseStartDate", LocalDateTime.now().minusDays(30));
            coursesCalendarDl.setParameter("courseEndDate", LocalDateTime.now().plusDays(30));
        }
        else {
            coursesCalendarDl.setParameter("currBranch", getCurrBranch());
            coursesCalendarDl.setParameter("currUser", getCurrUsers());
            coursesCalendarDl.setParameter("courseStartDate", LocalDateTime.now().minusDays(30));
            coursesCalendarDl.setParameter("courseEndDate", LocalDateTime.now().plusDays(30));
        }
        coursesCalendarDl.load();
    }

    @Subscribe("typeMultiFilter")
    protected void onTypeMultiFilterValueChange(HasValue.ValueChangeEvent event) {
        if (event.getValue() == null) {
            coursesCalendarDl.removeParameter("type");
        } else if (CollectionUtils.isEmpty((Set<CourseType>) event.getValue())) {
            coursesCalendarDl.setParameter("type", Collections.singleton(""));
        } else {
            coursesCalendarDl.setParameter("type", event.getValue());
        }
        loadEvents();
    }
}
