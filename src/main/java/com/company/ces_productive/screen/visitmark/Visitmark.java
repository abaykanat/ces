package com.company.ces_productive.screen.visitmark;

import com.company.ces_productive.app.GetCourseRealAmount;
import com.company.ces_productive.app.CreateVisits;
import com.company.ces_productive.entity.*;
import com.company.ces_productive.entity.courses.CourseType;
import com.company.ces_productive.entity.courses.Courses;
import io.jmix.businesscalendar.model.BusinessCalendar;
import io.jmix.businesscalendar.repository.BusinessCalendarRepository;
import io.jmix.core.*;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.ui.Notifications;
import io.jmix.ui.action.Action;
import io.jmix.ui.component.Button;
import io.jmix.ui.component.DataGrid;
import io.jmix.ui.component.Table;
import io.jmix.ui.component.TextArea;
import io.jmix.ui.model.CollectionContainer;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.util.*;

@UiController("CES_Visitmark")
@UiDescriptor("VisitMark.xml")
public class Visitmark extends Screen {
    @Autowired
    private CollectionLoader<Courses> coursesDl;
    @Autowired
    private DataGrid<Courses> coursesTable;
    @Autowired
    private CreateVisits createVisits;
    @Autowired
    private CollectionLoader<Visits> visitsDl;
    @Autowired
    private Notifications notifications;
    @Autowired
    private CurrentAuthentication currentAuthentication;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private Table<Visits> visitsTable;
    @Autowired
    private Button visitEnd;
    @Autowired
    private CollectionContainer<Visits> visitsDc;
    @Autowired
    private TextArea<String> courseDesc;
    @Autowired
    private BusinessCalendarRepository businessCalendarRepository;

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        coursesDl.setParameter("currTeacher", currentAuthentication.getUser());
        coursesDl.setParameter("courseStartDate", LocalDateTime.now());
        coursesDl.setParameter("courseEndDate", LocalDateTime.now().minusDays(30));
        coursesDl.load();
        Courses course = coursesTable.getSingleSelected();
        visitsDl.setParameter("currCourse", course);
        visitsDl.setParameter("currTeacher", currentAuthentication.getUser());
        visitsDl.load();
        coursesTable.focus();
        List<Visits> visit = visitsDc.getItems().stream()
                .filter(visits -> visits.getVisitStatus().equals(VisitStatus.NOT_DEFINED))
                .toList();
        if (!visit.isEmpty()) {
            dataManager.remove(visit);
        }
    }

    @Install(to = "visitsTable", subject = "styleProvider")
    protected String visitTableStyleProvider(Visits visits, String property) {
        if (property == null) {
            visits.getVisitStatus();
        } else if (property.equals("visitStatus")) {
            return switch (visits.getVisitStatus()) {
                case ATTENDED -> "attended";
                case ABSENT -> "absent";
                case NOT_DEFINED -> "not_defined";
            };
        }
        return null;
    }

    @Subscribe("coursesTable")
    public void onCoursesTableItemClick(DataGrid.ItemClickEvent<Courses> event) {
        Courses selectCourse = event.getItem();
        visitsDl.setParameter("currCourse", selectCourse);
        visitsDl.load();
    }

    private void processVisit(Students student, Groups group, LocalDateTime startTime, LocalDateTime endTime, BigDecimal visitAmount, Courses course, User visitUser) {
        List<PaymentParam> paymentParams = student.getStudPayParam();
        if (paymentParams.isEmpty()) {
            addVisitBasedOnDiscount(student, startTime, endTime, visitAmount, course, visitUser, student.getStudDiscount());
        } else {
            for (PaymentParam paymentParam : paymentParams) {
                if (paymentParam.getPayParamGroups().getId().equals(group.getId())) {
                    addVisitBasedOnDiscount(student, startTime, endTime, visitAmount, course, visitUser, paymentParam.getPayParamDiscontAmount());
                }
            }
        }
    }

    private void addVisitBasedOnDiscount(Students student, LocalDateTime startTime, LocalDateTime endTime, BigDecimal visitAmount, Courses course, User visitUser, BigDecimal discount) {
        BigDecimal adjustedAmount = visitAmount;
        if (discount != null) {
            BigDecimal percentAmount = visitAmount.multiply(discount).divide(BigDecimal.valueOf(100), RoundingMode.UP);
            adjustedAmount = visitAmount.subtract(percentAmount);
        }
        createVisits.addVisit(VisitStatus.NOT_DEFINED, startTime, endTime, adjustedAmount, student, course, visitUser);
    }

    @Subscribe("start")
    public void onStart(Action.ActionPerformedEvent event) {
        Courses course = coursesTable.getSingleSelected();
        if (course == null) {
            notifications.create(Notifications.NotificationType.ERROR)
                    .withCaption("No course selected!")
                    .withDescription("You must select a course to mark attendance")
                    .show();
            return;
        }
        List<Visits> visit = visitsDc.getItems().stream()
                .filter(visits -> visits.getVisitStartDateTime().equals(course.getCourseStartDate())
                        && visits.getVisitStatus().equals(VisitStatus.NOT_DEFINED))
                .toList();
        if (!visit.isEmpty()) {
            notifications.create(Notifications.NotificationType.ERROR)
                    .withCaption("Checkout already started!")
                    .withDescription("It is necessary to mark the visits of students and click on the end button")
                    .show();
            return;
        }
        if (course.getCourseCost().equals(BigDecimal.ZERO)) {
            course.setCourseStatus(CourseStatus.HELD);
            notifications.create(Notifications.NotificationType.HUMANIZED)
                    .withCaption("Checkout is done!")
                    .withDescription("This lesson on 'English Club' for this group was successfully marked")
                    .show();
        } else {
            Groups group = course.getCourseGroup();
            LocalDateTime startTime = course.getCourseStartDate();
            LocalDateTime endTime = course.getCourseEndDate();
            User visitUser = course.getCourseTeacher();
            boolean proceed = true;
            for (Students student : Objects.requireNonNull(course.getCourseGroup().getGroupStudents())) {
                BigDecimal visitAmount = GetCourseRealAmount.getCourseRealAmount(student, group, course.getCourseName()).getAmount();
                String visitReason = GetCourseRealAmount.getCourseRealAmount(student, group, course.getCourseName()).getReason();
                if (visitAmount.equals(BigDecimal.ZERO)) {
                    List<PaymentParam> paymentParams = student.getStudPayParam();
                    for (PaymentParam paymentParam : paymentParams) {
                        Groups payGroup = paymentParam.getPayParamGroups();
                        if (payGroup.equals(group)) {
                            BigDecimal payDiscountAmount = paymentParam.getPayParamDiscontAmount();
                            if (!(payDiscountAmount.compareTo(BigDecimal.valueOf(100)) == 0)) {
                                notifications.create(Notifications.NotificationType.ERROR)
                                        .withCaption("Calculation error")
                                        .withDescription("The payment date for student " + student.getStudLastName() + " " + student.getStudFirstName()
                                                +" in the group has not been updated. Contact the branch manager for update" + "Reason:" + visitReason)
                                        .show();
                                proceed = false;
                                break;
                            }
                        }
                    }
                }
            }
            if (!proceed) {
                return;
            }
            for (Students student : Objects.requireNonNull(course.getCourseGroup().getGroupStudents())) {
                BigDecimal visitAmount = GetCourseRealAmount.getCourseRealAmount(student, group, course.getCourseName()).getAmount();
                switch (student.getStudStatus()) {
                    case STUDY:
                        processVisit(student, group, startTime, endTime, visitAmount, course, visitUser);
                        break;
                    case STOPPED:
                        BigDecimal courseCost = course.getCourseCost();
                        if (student.getStudActualAmount().compareTo(courseCost) > 0) {
                            processVisit(student, group, startTime, endTime, visitAmount, course, visitUser);
                        }
                        break;
                    case FREEZE:
                        List<Freeze> freezes = student.getStudFreeze();
                        for (Freeze freeze : freezes) {
                            LocalDate freezeEndDate = freeze.getFreezeEndDate();
                            if (!freezeEndDate.isAfter(LocalDate.now())) {
                                processVisit(student, group, startTime, endTime, visitAmount, course, visitUser);
                                student.setStudStatus(StudentStatus.STUDY);
                                dataManager.save(student);
                            }
                        }
                        break;
                }
            }
            course.setCourseStatus(CourseStatus.NOT_DONE);
            dataManager.save(course);
            visitsDl.load();
        }
    }

    public void CreateCourses(String courseName, CourseStatus courseStatus, Groups courseGroup, BigDecimal courseCost, BigDecimal courseCount,
                              LocalDate courseStartDate, LocalDate courseEndDate, LocalTime courseStartTime, LocalTime courseEndTime,
                              Cabinets courseCabinet, User courseTeacher, User courseManager, Branches courseBranch, CourseType type) {

        LocalDateTime StartDateTime = LocalDateTime.of(courseStartDate, courseStartTime);
        LocalDateTime EndDateTime = LocalDateTime.of(courseEndDate, courseEndTime);

        Courses courses = dataManager.create(Courses.class);
        courses.setCourseName(courseName);
        courses.setCourseStatus(courseStatus);
        courses.setCourseGroup(courseGroup);
        courses.setCourseCost(courseCost);
        courses.setCourseCount(courseCount);
        courses.setCourseStartDate(StartDateTime);
        courses.setCourseEndDate(EndDateTime);
        courses.setCourseStartTime(courseStartTime);
        courses.setCourseEndTime(courseEndTime);
        courses.setCourseCabinet(courseCabinet);
        courses.setCourseTeacher(courseTeacher);
        courses.setCourseManager(courseManager);
        courses.setCourseBranch(courseBranch);
        courses.setType(type);
        dataManager.save(courses);
    }

    public BusinessCalendar getBusinessCalendarByCurrentYear() {
        Collection<BusinessCalendar> allCalendars = businessCalendarRepository.getAllBusinessCalendars();
        int currentYear = Year.now().getValue();
        for (BusinessCalendar calendar : allCalendars) {
            String calendarCode = calendar.getCode();
            if (calendarCode.contains(String.valueOf(currentYear))) {
                return calendar;
            }
        }
        return null;
    }

    @Subscribe("end")
    public void onEnd(Action.ActionPerformedEvent event) {
        Courses course = coursesTable.getSingleSelected();
        if (course != null) {
            if (Objects.requireNonNull(visitsTable.getSingleSelected()).getVisitStatus() == VisitStatus.NOT_DEFINED) {
                notifications.create(Notifications.NotificationType.ERROR)
                        .withCaption("All visits must be filed!")
                        .withDescription("It is mandatory to note the visits of all students")
                        .show();
                return;
            }
            if (course.getCourseStatus() == CourseStatus.NOT_DONE) {
                List<Students> visitStudents = Objects.requireNonNull(course).getCourseGroup().getGroupStudents();
                for (Students visitStudent : visitStudents) {
                    List<Visits> visits = visitStudent.getStudVisits().stream()
                            .filter(visit -> visit.getVisitCourse().equals(course) && visit.getVisitStartDateTime().isEqual(course.getCourseStartDate()))
                            .toList();
                    for (Visits visit : visits) {
                        BigDecimal visitAmount = visit.getVisitAmount();
                        BigDecimal actualAmount = visitStudent.getStudActualAmount();
                        BigDecimal newActualAmount = actualAmount.subtract(visitAmount);
                        visitStudent.setStudActualAmount(newActualAmount);
                        dataManager.save(visitStudent);
                        }
                    }
                course.setCourseStatus(CourseStatus.HELD);
                course.setCourseDesc(courseDesc.getValue());
                dataManager.save(course);
                visitEnd.setEnabled(false);
                close(StandardOutcome.COMMIT);
                } else {
                notifications.create(Notifications.NotificationType.ERROR)
                        .withCaption("Course must be started!")
                        .withDescription("The mark is made on the course that was started")
                        .show();
            }
            visitsTable.setEditable(false);
            LocalDateTime maxCourseDate = course.getCourseGroup().getGroupCourse()
                    .stream()
                    .map(Courses::getCourseStartDate)
                    .filter(date -> date.isAfter(LocalDateTime.now()))
                    .max(LocalDateTime::compareTo)
                    .orElse(null);
            LocalDate maxDate = Objects.requireNonNull(maxCourseDate).toLocalDate();
            LocalDate maxMinusDate = maxDate.minusDays(40);
            if (LocalDate.now().isAfter(maxMinusDate)) {
                LocalDateTime oneWeekAgo = maxCourseDate.minusWeeks(1);
                List<Courses> oldCourses = course.getCourseGroup().getGroupCourse()
                        .stream()
                        .filter(c -> c.getCourseStartDate().isAfter(oneWeekAgo)
                                && (c.getCourseStartDate().isBefore(maxCourseDate) || c.getCourseStartDate().isEqual(maxCourseDate)))
                        .toList();
                for (Courses oldCourse : oldCourses) {
                    LocalDateTime newCourseStartDate = oldCourse.getCourseStartDate().plusWeeks(1);
                    LocalDateTime newCourseEndDate = oldCourse.getCourseEndDate().plusWeeks(1);
                    LocalDate newCourseDate = newCourseStartDate.toLocalDate();
                    if (Objects.requireNonNull(getBusinessCalendarByCurrentYear()).isBusinessDay(newCourseDate)) {
                        CreateCourses(oldCourse.getCourseName(), oldCourse.getCourseStatus(), oldCourse.getCourseGroup(), oldCourse.getCourseCost(), oldCourse.getCourseCount(),
                                newCourseStartDate.toLocalDate(), newCourseEndDate.toLocalDate(), newCourseStartDate.toLocalTime(), newCourseEndDate.toLocalTime(), oldCourse.getCourseCabinet(),
                                oldCourse.getCourseTeacher(), oldCourse.getCourseManager(), oldCourse.getCourseBranch(), oldCourse.getType());
                    }
                }
            }
        } else {
            notifications.create(Notifications.NotificationType.ERROR)
                    .withCaption("Select course")
                    .withDescription("Course must be selected in first table")
                    .show();
        }
    }
}