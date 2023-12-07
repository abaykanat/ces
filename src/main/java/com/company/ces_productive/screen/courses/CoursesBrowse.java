package com.company.ces_productive.screen.courses;

import com.company.ces_productive.entity.Branches;
import com.company.ces_productive.entity.User;
import com.company.ces_productive.entity.courses.CourseType;
import com.company.ces_productive.screen.courses.calendar.CalendarMode;
import com.company.ces_productive.screen.courses.calendar.CalendarNavigationMode;
import com.company.ces_productive.screen.courses.calendar.CalendarNavigators;
import com.company.ces_productive.screen.courses.calendar.CalendarScreenAdjustment;
import com.company.ces_productive.screen.courses.calendar.CourseTypeIcon;
import com.company.ces_productive.entity.courses.Courses;
import com.vaadin.v7.shared.ui.calendar.CalendarState;
import io.jmix.core.DataManager;
import io.jmix.core.Messages;
import io.jmix.core.TimeSource;
import io.jmix.core.metamodel.datatype.DatatypeFormatter;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.ui.Notifications;
import io.jmix.ui.ScreenBuilders;
import io.jmix.ui.action.Action;
import io.jmix.ui.component.Button;
import io.jmix.ui.component.Calendar;
import io.jmix.ui.component.CheckBoxGroup;
import io.jmix.ui.component.DatePicker;
import io.jmix.ui.component.HasValue;
import io.jmix.ui.component.Label;
import io.jmix.ui.component.RadioButtonGroup;
import io.jmix.ui.model.CollectionContainer;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.model.DataContext;
import io.jmix.ui.screen.LookupComponent;
import io.jmix.ui.screen.MessageBundle;
import io.jmix.ui.screen.OpenMode;
import io.jmix.ui.screen.Screen;
import io.jmix.ui.screen.StandardLookup;
import io.jmix.ui.screen.StandardOutcome;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.company.ces_productive.screen.courses.calendar.CalendarNavigationMode.*;
import static com.company.ces_productive.screen.courses.calendar.RelativeDates.startOfWeek;

@UiController("CES_Courses.browse")
@UiDescriptor("courses-browse.xml")
public class CoursesBrowse extends StandardLookup<Courses> {

    @Autowired
    protected Calendar<LocalDateTime> calendar;
    @Autowired
    protected ScreenBuilders screenBuilders;
    @Autowired
    protected CollectionContainer<Courses> coursesCalendarDc;
    @Autowired
    protected CollectionLoader<Courses> coursesCalendarDl;
    @Autowired
    protected DataContext dataContext;
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
    protected Notifications notifications;
    @Autowired
    protected MessageBundle messageBundle;
    @Autowired
    protected Messages messages;
    @Autowired
    private DataManager dataManager;

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


    @Subscribe("calendarMode")
    protected void onCalendarRangeValueChange(HasValue.ValueChangeEvent event) {
        if (event.isUserOriginated()) {
            atDate((CalendarMode) event.getValue(), calendarNavigator.getValue());
        }
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

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Calendar Visit Event Click
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Subscribe("calendar")
    protected void onCalendarCalendarEventClick(Calendar.CalendarEventClickEvent<LocalDateTime> event) {

        Screen courseEditor = screenBuilders.editor(Courses.class, this)
                .editEntity((Courses) event.getEntity())
                .withOpenMode(OpenMode.DIALOG)
                .build();

        courseEditor.addAfterCloseListener(afterCloseEvent -> {
            if (afterCloseEvent.closedWith(StandardOutcome.COMMIT)) {
                getScreenData().loadAll();
            }
        });

        courseEditor.show();
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

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Visit Changes through Calendar Event Adjustments
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Subscribe("calendar")
    protected void onCalendarCalendarEventResize(Calendar.CalendarEventResizeEvent<LocalDateTime> event) {
        updateVisit(event.getEntity(), event.getNewStart(), event.getNewEnd());
    }

    @Subscribe("calendar")
    protected void onCalendarCalendarEventMove(Calendar.CalendarEventMoveEvent<LocalDateTime> event) {
        updateVisit(event.getEntity(), event.getNewStart(), event.getNewEnd());
    }

    private void updateVisit(Object entity, LocalDateTime newStart, LocalDateTime newEnd) {
        Courses courses = (Courses) entity;
        courses.setCourseStartDate(newStart);
        courses.setCourseEndDate(newEnd);
        dataContext.commit();
        notifications.create(Notifications.NotificationType.TRAY)
                .withCaption(
                        messageBundle.formatMessage(
                                        "courseUpdated",
                                messages.getMessage(courses.getType()),
                                courses.getCourseName()
                        )
                )
                .show();
    }

    @Subscribe("createCourseSchedule")
    public void onCreateCourseSchedule(Action.ActionPerformedEvent event) {
        Screen createCourse = screenBuilders.editor(Courses.class, this)
                .newEntity()
                .withScreenClass(CoursesCreateSch.class)
                .withOpenMode(OpenMode.DIALOG)
                .build();
        createCourse.addAfterCloseListener(afterCloseEvent -> {
            if(afterCloseEvent.closedWith(StandardOutcome.DISCARD)) {
                getScreenData().loadAll();
            }
        });
        createCourse.show();
    }

    @Subscribe("editCourseSchedule")
    public void onEditCourseSchedule(Action.ActionPerformedEvent event) {
        Screen createCourse = screenBuilders.editor(Courses.class, this)
                .newEntity()
                .withScreenClass(CoursesEditSch.class)
                .withOpenMode(OpenMode.DIALOG)
                .build();
        createCourse.addAfterCloseListener(afterCloseEvent -> {
            if(afterCloseEvent.closedWith(StandardOutcome.DISCARD)) {
                getScreenData().loadAll();
            }
        });
        createCourse.show();
    }

    @Subscribe("deleteCourseSchedule")
    public void onDeleteCourseSchedule(Action.ActionPerformedEvent event) {
        Screen createCourse = screenBuilders.editor(Courses.class, this)
                .newEntity()
                .withScreenClass(CoursesDelete.class)
                .withOpenMode(OpenMode.DIALOG)
                .build();
        createCourse.addAfterCloseListener(afterCloseEvent -> {
            if(afterCloseEvent.closedWith(StandardOutcome.DISCARD)) {
                getScreenData().loadAll();
            }
        });
        createCourse.show();
    }
}