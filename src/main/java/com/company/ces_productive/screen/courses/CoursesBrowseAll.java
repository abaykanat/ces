package com.company.ces_productive.screen.courses;

import com.company.ces_productive.entity.Branches;
import com.company.ces_productive.entity.User;
import com.company.ces_productive.entity.courses.CourseType;
import com.company.ces_productive.screen.courses.calendar.*;
import com.vaadin.v7.shared.ui.calendar.CalendarState;
import io.jmix.core.DataManager;
import io.jmix.core.TimeSource;
import io.jmix.core.metamodel.datatype.DatatypeFormatter;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.ui.action.Action;
import io.jmix.ui.component.*;
import io.jmix.ui.component.Calendar;
import io.jmix.ui.model.CollectionContainer;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.screen.*;
import com.company.ces_productive.entity.courses.Courses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static com.company.ces_productive.screen.courses.calendar.CalendarNavigationMode.*;
import static com.company.ces_productive.screen.courses.calendar.RelativeDates.startOfWeek;

@UiController("CES_Courses.browseAll")
@UiDescriptor("courses-browseAll.xml")
public class CoursesBrowseAll extends StandardLookup<Courses> {
    @Autowired
    protected Calendar<LocalDateTime> calendar;
    @Autowired
    protected CollectionContainer<Courses> coursesCalendarDc;
    @Autowired
    protected CollectionLoader<Courses> coursesCalendarDl;
    @Autowired
    protected CheckBoxGroup<CourseType> typeMultiFilter;
    @Autowired
    protected RadioButtonGroup<CalendarMode> calendarMode;
    @Autowired
    protected TimeSource timeSource;
    @Autowired
    protected Label<String> calendarTitle;
    @Autowired
    protected CalendarNavigators calendarNavigators;
    @Autowired
    protected DatePicker<LocalDate> calendarNavigator;
    @Autowired
    private CurrentAuthentication currentAuthentication;
    @Autowired
    protected DatatypeFormatter datatypeFormatter;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private CollectionLoader<Branches> branchesesDl;
    @Autowired
    private EntityComboBox<Branches> branchField;
    @Autowired
    private CollectionLoader<User> usersDl;
    @Autowired
    private EntityComboBox<User> teacherField;
    @Autowired
    private GroupBoxLayout techerBox;


    @Subscribe
    protected void onInit(InitEvent event) {
        initTypeFilter();
        initSortCalendarEventsInMonthlyView();
        branchesesDl.load();
    }

    private void initTypeFilter() {
        typeMultiFilter.setOptionsEnum(CourseType.class);
        typeMultiFilter.setValue(EnumSet.allOf(CourseType.class));
        typeMultiFilter.setOptionIconProvider(o -> CourseTypeIcon.valueOf(o.getIcon()).source());
    }

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        current(CalendarMode.WEEK);
    }

    @SuppressWarnings("deprecation")
    private void initSortCalendarEventsInMonthlyView() {
        calendar.unwrap(com.vaadin.v7.ui.Calendar.class)
                .setEventSortOrder(CalendarState.EventSortOrder.START_DATE_DESC);
    }


    @Subscribe("calendar")
    protected void onCalendarCalendarDayClick(Calendar.CalendarDateClickEvent<LocalDateTime> event) {
        atDate(
                CalendarMode.DAY,
                event.getDate().toLocalDate()
        );
    }

    @Subscribe("calendar")
    protected void onCalendarCalendarWeekClick(Calendar.CalendarWeekClickEvent<LocalDateTime> event) {
        atDate(
                CalendarMode.WEEK,
                startOfWeek(
                        event.getYear(),
                        event.getWeek(),
                        currentAuthentication.getLocale()
                )
        );
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

    @Subscribe("calendarNavigator")
    protected void onCalendarRangePickerValueChange(HasValue.ValueChangeEvent<LocalDate> event) {
        if (event.isUserOriginated()) {
            atDate(calendarMode.getValue(), event.getValue());
        }
    }

    private void current(CalendarMode calendarMode) {
        change(calendarMode, AT_DATE, timeSource.now().toLocalDate());
    }

    private void atDate(CalendarMode calendarMode, LocalDate date) {
        change(calendarMode, AT_DATE, date);
    }

    private void next(CalendarMode calendarMode) {
        change(calendarMode, NEXT, calendarNavigator.getValue());
    }

    private void previous(CalendarMode calendarMode) {
        change(calendarMode, PREVIOUS, calendarNavigator.getValue());
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

    public List<Branches> getCurrBranches() {
        return dataManager.load(Branches.class)
                .query("select b from CES_Branches b")
                .list();
    }

    @Subscribe("calendarMode")
    protected void onCalendarRangeValueChange(HasValue.ValueChangeEvent event) {
        if (event.isUserOriginated()) {
            atDate((CalendarMode) event.getValue(), calendarNavigator.getValue());
        }
    }

    private void loadEvents() {
        coursesCalendarDl.setParameter("currBranch", getCurrBranches());
        coursesCalendarDl.setParameter("courseStartDate", LocalDateTime.now().minusDays(7));
        coursesCalendarDl.setParameter("courseEndDate", LocalDateTime.now().plusDays(30));
        coursesCalendarDl.setParameter("currTeacher", getCurrUsers());
        coursesCalendarDl.load();
    }

    public List<User> getCurrUsers() {
        return dataManager.load(User.class)
                .query("select u from CES_User u")
                .list();
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Filter for Visit Types
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

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

    @Subscribe("showCourseAction")
    public void onShowCourseAction(final Action.ActionPerformedEvent event) {
        List<Branches> branchesList = new ArrayList<>();
        branchesList.add(branchField.getValue());
        coursesCalendarDl.setParameter("currBranch", branchesList);
        coursesCalendarDl.setParameter("courseStartDate", LocalDateTime.now().minusDays(30));
        coursesCalendarDl.setParameter("courseEndDate", LocalDateTime.now().plusDays(30));
        coursesCalendarDl.setParameter("currTeacher", getCurrUsers());
        coursesCalendarDl.load();
        usersDl.setParameter("currBranch", branchesList);
        usersDl.load();
        techerBox.setVisible(true);
    }

    @Subscribe("showTeacherAction")
    public void onShowTeacherAction(final Action.ActionPerformedEvent event) {
        List<Branches> branchesList = new ArrayList<>();
        branchesList.add(branchField.getValue());
        List<User> userList = new ArrayList<>();
        userList.add(teacherField.getValue());
        coursesCalendarDl.setParameter("currBranch", branchesList);
        coursesCalendarDl.setParameter("courseStartDate", LocalDateTime.now().minusDays(30));
        coursesCalendarDl.setParameter("courseEndDate", LocalDateTime.now().plusDays(30));
        coursesCalendarDl.setParameter("currTeacher", userList);
        coursesCalendarDl.load();
    }

}