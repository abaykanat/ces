package com.company.ces_productive.screen.courses;

import com.company.ces_productive.entity.*;
import com.company.ces_productive.entity.courses.CourseType;
import io.jmix.businesscalendar.model.BusinessCalendar;
import io.jmix.businesscalendar.repository.BusinessCalendarRepository;
import io.jmix.core.DataManager;
import io.jmix.core.FluentLoader;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.ui.Notifications;
import io.jmix.ui.action.Action;
import io.jmix.ui.component.*;
import io.jmix.ui.model.CollectionContainer;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.screen.*;
import com.company.ces_productive.entity.courses.Courses;
import org.springframework.beans.factory.annotation.Autowired;
import io.jmix.ui.navigation.Route;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@UiController("CES_Courses.editSch")
@UiDescriptor("courses-editSch.xml")
@EditedEntityContainer("coursesDc")
@Route(value = "courses/editSch", parentPrefix = "courses")
public class CoursesEditSch extends StandardEditor<Courses> {
    @Autowired
    private CurrentAuthentication currentAuthentication;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private RadioButtonGroup<CourseMode> courseMode;
    @Autowired
    private TextField<String> courseNameField;
    @Autowired
    private EntityPicker<Groups> courseGroupField;
    @Autowired
    private TextField<BigDecimal> courseCostField;
    @Autowired
    private TextField<BigDecimal> courseCountField;
    @Autowired
    private CheckBox monday;
    @Autowired
    private CheckBox tuesday;
    @Autowired
    private CheckBox wednesday;
    @Autowired
    private CheckBox thursday;
    @Autowired
    private CheckBox friday;
    @Autowired
    private CheckBox saturday;
    @Autowired
    private CheckBox sunday;
    @Autowired
    private DateField<LocalDate> courseStartDate;
    @Autowired
    private DateField<LocalDate> courseEndDate;
    @Autowired
    private TimeField<LocalTime> singleStartTime;
    @Autowired
    private TimeField<LocalTime> singleEndTime;
    @Autowired
    private TimeField<LocalTime> mondayStartTime;
    @Autowired
    private TimeField<LocalTime> mondayEndTime;
    @Autowired
    private TimeField<LocalTime> tuesdayStartTime;
    @Autowired
    private TimeField<LocalTime> tuesdayEndTime;
    @Autowired
    private TimeField<LocalTime> wednesdayStartTime;
    @Autowired
    private TimeField<LocalTime> wednesdayEndTime;
    @Autowired
    private TimeField<LocalTime> thursdayStartTime;
    @Autowired
    private TimeField<LocalTime> thursdayEndTime;
    @Autowired
    private TimeField<LocalTime> fridayStartTime;
    @Autowired
    private TimeField<LocalTime> fridayEndTime;
    @Autowired
    private TimeField<LocalTime> saturdayStartTime;
    @Autowired
    private TimeField<LocalTime> saturdayEndTime;
    @Autowired
    private TimeField<LocalTime> sundayStartTime;
    @Autowired
    private TimeField<LocalTime> sundayEndTime;
    @Autowired
    private EntityPicker<Cabinets> courseCabinetField;
    @Autowired
    private SuggestionField<User> courseTeacherField;
    @Autowired
    private EntityPicker<User> courseManagerField;
    @Autowired
    private EntityPicker<Branches> courseBranchField;
    @Autowired
    private CollectionLoader<Cabinets> cabinetsesDl;
    @Autowired
    private ComboBox<Courses> selectSchedule;
    @Autowired
    private CollectionContainer<Courses> coursesesDc;
    @Autowired
    private VBoxLayout weekDays;
    @Autowired
    private BusinessCalendarRepository businessCalendarRepository;
    @Autowired
    private Notifications notifications;
    @Autowired
    private Label<String> teacherConfDate;
    @Autowired
    private Label<String> roomConfDate;
    @Autowired
    private Label<String> roomConfDateText;
    @Autowired
    private Label<String> teacherConfDateText;

    @Subscribe
    public void onInit(InitEvent event) {
        cabinetsesDl.setParameter("currUserBranch", ((User) currentAuthentication.getUser()).getBranch());
        cabinetsesDl.load();
    }

    @Subscribe
    public void onInitEntity(InitEntityEvent<Courses> event) {
        event.getEntity().setCourseManager((User) currentAuthentication.getUser());
        event.getEntity().setCourseBranch(((User) currentAuthentication.getUser()).getBranch());
    }

    public List<Courses> selectedCourse() {
        List<Courses> result = new ArrayList<>();
        String selectedCourseName = String.valueOf(Objects.requireNonNull(selectSchedule.getValue()).getCourseName());
        for (Courses course : coursesesDc.getItems()) {
            if (course.getCourseName().equals(selectedCourseName) && course.getCourseStatus() == CourseStatus.NEW) {
                result.add(course);
            }
        }
        return result;
    }

    public LocalDate courseMaxEndDate() {
        return coursesesDc.getItems().stream()
                .filter(courses -> courses.getCourseName().equals(String.valueOf(Objects.requireNonNull(selectSchedule.getValue()).getCourseName())))
                .map(Courses::getCourseEndDate)
                .map(LocalDateTime::toLocalDate)
                .max(Comparator.naturalOrder())
                .orElse(null);
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

    public BusinessCalendar getBusinessCalendarByNextYear() {
        Collection<BusinessCalendar> allCalendars = businessCalendarRepository.getAllBusinessCalendars();
        int nextYear = Year.now().getValue() + 1;
        for (BusinessCalendar calendar : allCalendars) {
            String calendarCode = calendar.getCode();
            if (calendarCode.contains(String.valueOf(nextYear)) ) {
                return calendar;
            }
        }
        return null;
    }

    private LocalDateTime findTeacherScheduleConflict(User teacher, LocalDateTime startDateTime, LocalDateTime endDateTime, LocalTime startTime, LocalTime endTime, DayOfWeek currentDayOfWeek) {
        LocalDateTime currentDateTime = startDateTime;
        while (currentDateTime.isBefore(endDateTime) || currentDateTime.isEqual(endDateTime)) {
            if (currentDateTime.getDayOfWeek() == currentDayOfWeek) {
                FluentLoader.ByQuery<Courses> query = dataManager.load(Courses.class)
                        .query("select c from CES_Courses c " +
                                "where c.courseTeacher = :teacher " +
                                "and ((c.courseStartDate <= :endDateTime) " +
                                "and (c.courseEndDate >= :startDateTime) " +
                                "and (c.courseStartTime < :endTime) " +
                                "and (c.courseEndTime > :startTime))")
                        .parameter("teacher", teacher)
                        .parameter("startDateTime", currentDateTime)
                        .parameter("endDateTime", currentDateTime)
                        .parameter("startTime", startTime)
                        .parameter("endTime", endTime)
                        .maxResults(1);
                List<Courses> conflictingCourses = query.list();
                if (!conflictingCourses.isEmpty()) {
                    Courses conflictingCourse = conflictingCourses.get(0);
                    return conflictingCourse.getCourseEndDate();
                }
            }
            currentDateTime = currentDateTime.plusDays(1);
        }
        return null;
    }
    private LocalDateTime findClassroomScheduleConflict(Cabinets classroom, LocalDateTime startDateTime, LocalDateTime endDateTime, LocalTime startTime, LocalTime endTime, DayOfWeek currentDayOfWeek) {
        LocalDateTime currentDateTime = startDateTime;
        while (currentDateTime.isBefore(endDateTime) || currentDateTime.isEqual(endDateTime)) {
            if (currentDateTime.getDayOfWeek() == currentDayOfWeek) {
                FluentLoader.ByQuery<Courses> query = dataManager.load(Courses.class)
                        .query("select c from CES_Courses c " +
                                "where c.courseCabinet = :classroom " +
                                "and ((c.courseStartDate <= :endDateTime) " +
                                "and (c.courseEndDate >= :startDateTime) " +
                                "and (c.courseStartTime < :endTime) " +
                                "and (c.courseEndTime > :startTime))")
                        .parameter("classroom", classroom)
                        .parameter("startDateTime", currentDateTime)
                        .parameter("endDateTime", currentDateTime)
                        .parameter("startTime", startTime)
                        .parameter("endTime", endTime)
                        .maxResults(1);
                List<Courses> conflictingCourses = query.list();
                if (!conflictingCourses.isEmpty()) {
                    Courses conflictingCourse = conflictingCourses.get(0);
                    return conflictingCourse.getCourseEndDate();
                }
            }
            currentDateTime = currentDateTime.plusDays(1);
        }
        return null;
    }

    public void EditCourses(String courseName, CourseStatus courseStatus, Groups courseGroup, BigDecimal courseCost, BigDecimal courseCount,
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

    @Subscribe("courseGroupField")
    public void onCourseGroupFieldValueChange(HasValue.ValueChangeEvent<Groups> event) {
        Groups selectedGroup = courseGroupField.getValue();
        if (selectedGroup == null) {
            selectSchedule.setOptionsList(Collections.emptyList());
            selectSchedule.setEnabled(false);
            return;
        }
        Set<String> courseNames = new HashSet<>();
        List<Courses> distinctCourses = new ArrayList<>();
        for (Courses course : coursesesDc.getItems()) {
            if (course.getCourseGroup().equals(selectedGroup) && course.getCourseStatus().equals(CourseStatus.NEW)) {
                String courseName = course.getCourseName();
                if (!courseNames.contains(courseName)) {
                    courseNames.add(courseName);
                    distinctCourses.add(course);
                }
            }
        }
        selectSchedule.setOptionsList(distinctCourses);
        selectSchedule.setEnabled(!distinctCourses.isEmpty());
    }

    @Subscribe("selectSchedule")
    public void onSelectScheduleValueChange(HasValue.ValueChangeEvent<Courses> event) {
        Courses course = event.getValue();
        courseNameField.setValue(Objects.requireNonNull(course).getCourseName());
        courseNameField.setEnabled(true);
        courseCabinetField.setValue(course.getCourseCabinet());
        courseCabinetField.setEnabled(true);
        courseTeacherField.setValue(course.getCourseTeacher());
        courseTeacherField.setEnabled(true);
        courseCountField.setValue(BigDecimal.valueOf(selectedCourse().size()));
        courseCostField.setValue(course.getCourseCost());
        courseBranchField.setValue(course.getCourseBranch());
        courseManagerField.setValue(course.getCourseManager());
        courseStartDate.setValue(course.getCourseStartDate().toLocalDate());
        courseEndDate.setValue(courseMaxEndDate());
        if (selectedCourse().size() == 1) {
            courseMode.setValue(CourseMode.SINGLE);
            singleStartTime.setVisible(true);
            singleEndTime.setVisible(true);
            singleStartTime.setValue(course.getCourseStartTime());
            singleEndTime.setValue(course.getCourseEndTime());
        } else {
            courseMode.setValue(CourseMode.REPEAT);
            weekDays.setVisible(true);
            for (Courses selectCourse : selectedCourse()) {
                if (selectCourse.getCourseStartDate().getDayOfWeek() == DayOfWeek.MONDAY) {
                    monday.setValue(Boolean.TRUE);
                    mondayStartTime.setEnabled(true);
                    mondayEndTime.setEnabled(true);
                    mondayStartTime.setValue(selectCourse.getCourseStartTime());
                    mondayEndTime.setValue(selectCourse.getCourseEndTime());
                }
                if (selectCourse.getCourseStartDate().getDayOfWeek() == DayOfWeek.THURSDAY) {
                    thursday.setValue(Boolean.TRUE);
                    tuesdayStartTime.setEnabled(true);
                    tuesdayEndTime.setEnabled(true);
                    thursdayStartTime.setValue(selectCourse.getCourseStartTime());
                    thursdayEndTime.setValue(selectCourse.getCourseEndTime());
                }
                if (selectCourse.getCourseStartDate().getDayOfWeek() == DayOfWeek.WEDNESDAY) {
                    wednesday.setValue(Boolean.TRUE);
                    wednesdayStartTime.setEnabled(true);
                    wednesdayEndTime.setEnabled(true);
                    wednesdayStartTime.setValue(selectCourse.getCourseStartTime());
                    wednesdayEndTime.setValue(selectCourse.getCourseEndTime());
                }
                if (selectCourse.getCourseStartDate().getDayOfWeek() == DayOfWeek.TUESDAY) {
                    tuesday.setValue(Boolean.TRUE);
                    thursdayStartTime.setEnabled(true);
                    thursdayEndTime.setEnabled(true);
                    tuesdayStartTime.setValue(selectCourse.getCourseStartTime());
                    tuesdayEndTime.setValue(selectCourse.getCourseEndTime());
                }
                if (selectCourse.getCourseStartDate().getDayOfWeek() == DayOfWeek.FRIDAY) {
                    friday.setValue(Boolean.TRUE);
                    fridayStartTime.setEnabled(true);
                    fridayEndTime.setEnabled(true);
                    fridayStartTime.setValue(selectCourse.getCourseStartTime());
                    fridayEndTime.setValue(selectCourse.getCourseEndTime());
                }
                if (selectCourse.getCourseStartDate().getDayOfWeek() == DayOfWeek.SATURDAY) {
                    saturday.setValue(Boolean.TRUE);
                    saturdayStartTime.setEnabled(true);
                    saturdayEndTime.setEnabled(true);
                    saturdayStartTime.setValue(selectCourse.getCourseStartTime());
                    saturdayEndTime.setValue(selectCourse.getCourseEndTime());
                }
                if (selectCourse.getCourseStartDate().getDayOfWeek() == DayOfWeek.SUNDAY) {
                    sunday.setValue(Boolean.TRUE);
                    sundayStartTime.setEnabled(true);
                    sundayEndTime.setEnabled(true);
                    sundayStartTime.setValue(selectCourse.getCourseStartTime());
                    sundayEndTime.setValue(selectCourse.getCourseEndTime());
                }
            }
        }
    }

    @Install(to = "courseTeacherField", subject = "searchExecutor")
    private List<User> SuggestionFieldSearchExecutor(String searchString, Map<String, Object> searchParams) {
        return dataManager.load(User.class)
                .query("e.lastName like ?1 order by e.lastName", "(?i)%"
                        + searchString + "%")
                .list();
    }
    @Subscribe("courseTeacherField")
    public void onCourseTeacherFieldValueChange(HasValue.ValueChangeEvent<User> event) {
        User oldUser = Objects.requireNonNull(selectSchedule.getValue()).getCourseTeacher();
        User newUser = courseTeacherField.getValue();
        if (courseMode.getValue() == CourseMode.SINGLE && newUser != oldUser){
            LocalDateTime StartDateTime = LocalDateTime.of(Objects.requireNonNull(courseStartDate.getValue()), Objects.requireNonNull(singleStartTime.getValue()));
            LocalDateTime EndDateTime = LocalDateTime.of(Objects.requireNonNull(courseEndDate.getValue()), Objects.requireNonNull(singleEndTime.getValue()));
            LocalDateTime freeTeacherTime = findTeacherScheduleConflict(courseTeacherField.getValue(), StartDateTime, EndDateTime, singleStartTime.getValue(), singleEndTime.getValue(), null);
            if (freeTeacherTime != null) {
                teacherConfDate.setVisible(true);
                teacherConfDateText.setVisible(true);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
                String formatTeacherDateTime = freeTeacherTime.format(formatter);
                teacherConfDate.setValue(formatTeacherDateTime);
                teacherConfDate.setStyleName("bold");
            }
        }
        if (Boolean.TRUE.equals(monday.getValue()) && newUser != oldUser){
            LocalDateTime StartDateTime = LocalDateTime.of(Objects.requireNonNull(courseStartDate.getValue()), Objects.requireNonNull(mondayStartTime.getValue()));
            LocalDateTime EndDateTime = LocalDateTime.of(Objects.requireNonNull(courseEndDate.getValue()), Objects.requireNonNull(mondayEndTime.getValue()));
            LocalDateTime freeTeacherTime = findTeacherScheduleConflict(courseTeacherField.getValue(), StartDateTime, EndDateTime, mondayStartTime.getValue(), mondayEndTime.getValue(), DayOfWeek.MONDAY);
            if (freeTeacherTime != null) {
                teacherConfDate.setVisible(true);
                teacherConfDateText.setVisible(true);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
                String formatTeacherDateTime = freeTeacherTime.format(formatter);
                teacherConfDate.setValue(formatTeacherDateTime);
                teacherConfDate.setStyleName("bold");
            }
        }
        if (Boolean.TRUE.equals(tuesday.getValue()) && newUser != oldUser){
            LocalDateTime StartDateTime = LocalDateTime.of(Objects.requireNonNull(courseStartDate.getValue()), Objects.requireNonNull(tuesdayStartTime.getValue()));
            LocalDateTime EndDateTime = LocalDateTime.of(Objects.requireNonNull(courseEndDate.getValue()), Objects.requireNonNull(tuesdayEndTime.getValue()));
            LocalDateTime freeTeacherTime = findTeacherScheduleConflict(courseTeacherField.getValue(), StartDateTime, EndDateTime, tuesdayStartTime.getValue(), tuesdayEndTime.getValue(), DayOfWeek.TUESDAY);
            if (freeTeacherTime != null) {
                teacherConfDate.setVisible(true);
                teacherConfDateText.setVisible(true);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
                String formatTeacherDateTime = freeTeacherTime.format(formatter);
                teacherConfDate.setValue(formatTeacherDateTime);
                teacherConfDate.setStyleName("bold");
            }
        }
        if (Boolean.TRUE.equals(wednesday.getValue()) && newUser != oldUser){
            LocalDateTime StartDateTime = LocalDateTime.of(Objects.requireNonNull(courseStartDate.getValue()), Objects.requireNonNull(wednesdayStartTime.getValue()));
            LocalDateTime EndDateTime = LocalDateTime.of(Objects.requireNonNull(courseEndDate.getValue()), Objects.requireNonNull(wednesdayEndTime.getValue()));
            LocalDateTime freeTeacherTime = findTeacherScheduleConflict(courseTeacherField.getValue(), StartDateTime, EndDateTime, wednesdayStartTime.getValue(), wednesdayEndTime.getValue(), DayOfWeek.WEDNESDAY);
            if (freeTeacherTime != null) {
                teacherConfDate.setVisible(true);
                teacherConfDateText.setVisible(true);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
                String formatTeacherDateTime = freeTeacherTime.format(formatter);
                teacherConfDate.setValue(formatTeacherDateTime);
                teacherConfDate.setStyleName("bold");
            }
        }
        if (Boolean.TRUE.equals(thursday.getValue()) && newUser != oldUser){
            LocalDateTime StartDateTime = LocalDateTime.of(Objects.requireNonNull(courseStartDate.getValue()), Objects.requireNonNull(thursdayStartTime.getValue()));
            LocalDateTime EndDateTime = LocalDateTime.of(Objects.requireNonNull(courseEndDate.getValue()), Objects.requireNonNull(thursdayEndTime.getValue()));
            LocalDateTime freeTeacherTime = findTeacherScheduleConflict(courseTeacherField.getValue(), StartDateTime, EndDateTime, thursdayStartTime.getValue(), thursdayEndTime.getValue(), DayOfWeek.THURSDAY);
            if (freeTeacherTime != null) {
                teacherConfDate.setVisible(true);
                teacherConfDateText.setVisible(true);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
                String formatTeacherDateTime = freeTeacherTime.format(formatter);
                teacherConfDate.setValue(formatTeacherDateTime);
                teacherConfDate.setStyleName("bold");
            }
        }
        if (Boolean.TRUE.equals(friday.getValue()) && newUser != oldUser){
            LocalDateTime StartDateTime = LocalDateTime.of(Objects.requireNonNull(courseStartDate.getValue()), Objects.requireNonNull(fridayStartTime.getValue()));
            LocalDateTime EndDateTime = LocalDateTime.of(Objects.requireNonNull(courseEndDate.getValue()), Objects.requireNonNull(fridayEndTime.getValue()));
            LocalDateTime freeTeacherTime = findTeacherScheduleConflict(courseTeacherField.getValue(), StartDateTime, EndDateTime, fridayStartTime.getValue(), fridayEndTime.getValue(), DayOfWeek.FRIDAY);
            if (freeTeacherTime != null) {
                teacherConfDate.setVisible(true);
                teacherConfDateText.setVisible(true);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
                String formatTeacherDateTime = freeTeacherTime.format(formatter);
                teacherConfDate.setValue(formatTeacherDateTime);
                teacherConfDate.setStyleName("bold");
            }
        }
        if (Boolean.TRUE.equals(saturday.getValue()) && newUser != oldUser){
            LocalDateTime StartDateTime = LocalDateTime.of(Objects.requireNonNull(courseStartDate.getValue()), Objects.requireNonNull(saturdayStartTime.getValue()));
            LocalDateTime EndDateTime = LocalDateTime.of(Objects.requireNonNull(courseEndDate.getValue()), Objects.requireNonNull(saturdayEndTime.getValue()));
            LocalDateTime freeTeacherTime = findTeacherScheduleConflict(courseTeacherField.getValue(), StartDateTime, EndDateTime, saturdayStartTime.getValue(), saturdayEndTime.getValue(), DayOfWeek.SATURDAY);
            if (freeTeacherTime != null) {
                teacherConfDate.setVisible(true);
                teacherConfDateText.setVisible(true);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
                String formatTeacherDateTime = freeTeacherTime.format(formatter);
                teacherConfDate.setValue(formatTeacherDateTime);
                teacherConfDate.setStyleName("bold");
            }
        }
        if (Boolean.TRUE.equals(sunday.getValue()) && newUser != oldUser){
            LocalDateTime StartDateTime = LocalDateTime.of(Objects.requireNonNull(courseStartDate.getValue()), Objects.requireNonNull(sundayStartTime.getValue()));
            LocalDateTime EndDateTime = LocalDateTime.of(Objects.requireNonNull(courseEndDate.getValue()), Objects.requireNonNull(sundayEndTime.getValue()));
            LocalDateTime freeTeacherTime = findTeacherScheduleConflict(courseTeacherField.getValue(), StartDateTime, EndDateTime, sundayStartTime.getValue(), sundayEndTime.getValue(), DayOfWeek.SUNDAY);
            if (freeTeacherTime != null) {
                teacherConfDate.setVisible(true);
                teacherConfDateText.setVisible(true);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
                String formatTeacherDateTime = freeTeacherTime.format(formatter);
                teacherConfDate.setValue(formatTeacherDateTime);
                teacherConfDate.setStyleName("bold");
            }
        }
    }

    @Subscribe("courseCabinetField")
    public void onCourseCabinetFieldValueChange(HasValue.ValueChangeEvent<Cabinets> event) {
        Cabinets oldCabinets = Objects.requireNonNull(selectSchedule.getValue()).getCourseCabinet();
        Cabinets newCabinets = courseCabinetField.getValue();
        if (courseMode.getValue() == CourseMode.SINGLE  && newCabinets != oldCabinets){
            LocalDateTime StartDateTime = LocalDateTime.of(Objects.requireNonNull(courseStartDate.getValue()), Objects.requireNonNull(singleStartTime.getValue()));
            LocalDateTime EndDateTime = LocalDateTime.of(Objects.requireNonNull(courseEndDate.getValue()), Objects.requireNonNull(singleEndTime.getValue()));
            LocalDateTime freeCabinetTime = findClassroomScheduleConflict(courseCabinetField.getValue(), StartDateTime, EndDateTime, singleStartTime.getValue(), singleEndTime.getValue(), null);
            if (freeCabinetTime != null) {
                roomConfDate.setVisible(true);
                roomConfDateText.setVisible(true);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
                String formatRoomDateTime = freeCabinetTime.format(formatter);
                roomConfDate.setValue(formatRoomDateTime);
                roomConfDate.setStyleName("bold");
            }
        }
        if (Boolean.TRUE.equals(monday.getValue())  && newCabinets != oldCabinets){
            LocalDateTime StartDateTime = LocalDateTime.of(Objects.requireNonNull(courseStartDate.getValue()), Objects.requireNonNull(mondayStartTime.getValue()));
            LocalDateTime EndDateTime = LocalDateTime.of(Objects.requireNonNull(courseEndDate.getValue()), Objects.requireNonNull(mondayEndTime.getValue()));
            LocalDateTime freeCabinetTime = findClassroomScheduleConflict(courseCabinetField.getValue(), StartDateTime, EndDateTime, mondayStartTime.getValue(), mondayEndTime.getValue(), DayOfWeek.MONDAY);
            if (freeCabinetTime != null) {
                roomConfDate.setVisible(true);
                roomConfDateText.setVisible(true);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
                String formatRoomDateTime = freeCabinetTime.format(formatter);
                roomConfDate.setValue(formatRoomDateTime);
                roomConfDate.setStyleName("bold");
            }
        }
        if (Boolean.TRUE.equals(tuesday.getValue())  && newCabinets != oldCabinets){
            LocalDateTime StartDateTime = LocalDateTime.of(Objects.requireNonNull(courseStartDate.getValue()), Objects.requireNonNull(tuesdayStartTime.getValue()));
            LocalDateTime EndDateTime = LocalDateTime.of(Objects.requireNonNull(courseEndDate.getValue()), Objects.requireNonNull(tuesdayEndTime.getValue()));
            LocalDateTime freeCabinetTime = findClassroomScheduleConflict(courseCabinetField.getValue(), StartDateTime, EndDateTime, tuesdayStartTime.getValue(), tuesdayEndTime.getValue(), DayOfWeek.TUESDAY);
            if (freeCabinetTime != null) {
                roomConfDate.setVisible(true);
                roomConfDateText.setVisible(true);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
                String formatRoomDateTime = freeCabinetTime.format(formatter);
                roomConfDate.setValue(formatRoomDateTime);
                roomConfDate.setStyleName("bold");
            }
        }
        if (Boolean.TRUE.equals(wednesday.getValue())  && newCabinets != oldCabinets){
            LocalDateTime StartDateTime = LocalDateTime.of(Objects.requireNonNull(courseStartDate.getValue()), Objects.requireNonNull(wednesdayStartTime.getValue()));
            LocalDateTime EndDateTime = LocalDateTime.of(Objects.requireNonNull(courseEndDate.getValue()), Objects.requireNonNull(wednesdayEndTime.getValue()));
            LocalDateTime freeCabinetTime = findClassroomScheduleConflict(courseCabinetField.getValue(), StartDateTime, EndDateTime, wednesdayStartTime.getValue(), wednesdayEndTime.getValue(), DayOfWeek.WEDNESDAY);
            if (freeCabinetTime != null) {
                roomConfDate.setVisible(true);
                roomConfDateText.setVisible(true);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
                String formatRoomDateTime = freeCabinetTime.format(formatter);
                roomConfDate.setValue(formatRoomDateTime);
                roomConfDate.setStyleName("bold");
            }
        }
        if (Boolean.TRUE.equals(thursday.getValue())  && newCabinets != oldCabinets){
            LocalDateTime StartDateTime = LocalDateTime.of(Objects.requireNonNull(courseStartDate.getValue()), Objects.requireNonNull(thursdayStartTime.getValue()));
            LocalDateTime EndDateTime = LocalDateTime.of(Objects.requireNonNull(courseEndDate.getValue()), Objects.requireNonNull(thursdayEndTime.getValue()));
            LocalDateTime freeCabinetTime = findClassroomScheduleConflict(courseCabinetField.getValue(), StartDateTime, EndDateTime, thursdayStartTime.getValue(), thursdayEndTime.getValue(), DayOfWeek.THURSDAY);
            if (freeCabinetTime != null) {
                roomConfDate.setVisible(true);
                roomConfDateText.setVisible(true);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
                String formatRoomDateTime = freeCabinetTime.format(formatter);
                roomConfDate.setValue(formatRoomDateTime);
                roomConfDate.setStyleName("bold");
            }
        }
        if (Boolean.TRUE.equals(friday.getValue())  && newCabinets != oldCabinets){
            LocalDateTime StartDateTime = LocalDateTime.of(Objects.requireNonNull(courseStartDate.getValue()), Objects.requireNonNull(fridayStartTime.getValue()));
            LocalDateTime EndDateTime = LocalDateTime.of(Objects.requireNonNull(courseEndDate.getValue()), Objects.requireNonNull(fridayEndTime.getValue()));
            LocalDateTime freeCabinetTime = findClassroomScheduleConflict(courseCabinetField.getValue(), StartDateTime, EndDateTime, fridayStartTime.getValue(), fridayEndTime.getValue(), DayOfWeek.FRIDAY);
            if (freeCabinetTime != null) {
                roomConfDate.setVisible(true);
                roomConfDateText.setVisible(true);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
                String formatRoomDateTime = freeCabinetTime.format(formatter);
                roomConfDate.setValue(formatRoomDateTime);
                roomConfDate.setStyleName("bold");
            }
        }
        if (Boolean.TRUE.equals(saturday.getValue())  && newCabinets != oldCabinets){
            LocalDateTime StartDateTime = LocalDateTime.of(Objects.requireNonNull(courseStartDate.getValue()), Objects.requireNonNull(saturdayStartTime.getValue()));
            LocalDateTime EndDateTime = LocalDateTime.of(Objects.requireNonNull(courseEndDate.getValue()), Objects.requireNonNull(saturdayEndTime.getValue()));
            LocalDateTime freeCabinetTime = findClassroomScheduleConflict(courseCabinetField.getValue(), StartDateTime, EndDateTime, saturdayStartTime.getValue(), saturdayEndTime.getValue(), DayOfWeek.SATURDAY);
            if (freeCabinetTime != null) {
                roomConfDate.setVisible(true);
                roomConfDateText.setVisible(true);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
                String formatRoomDateTime = freeCabinetTime.format(formatter);
                roomConfDate.setValue(formatRoomDateTime);
                roomConfDate.setStyleName("bold");
            }
        }
        if (Boolean.TRUE.equals(sunday.getValue())  && newCabinets != oldCabinets){
            LocalDateTime StartDateTime = LocalDateTime.of(Objects.requireNonNull(courseStartDate.getValue()), Objects.requireNonNull(sundayStartTime.getValue()));
            LocalDateTime EndDateTime = LocalDateTime.of(Objects.requireNonNull(courseEndDate.getValue()), Objects.requireNonNull(sundayEndTime.getValue()));
            LocalDateTime freeCabinetTime = findClassroomScheduleConflict(courseCabinetField.getValue(), StartDateTime, EndDateTime, sundayStartTime.getValue(), sundayEndTime.getValue(), DayOfWeek.SUNDAY);
            if (freeCabinetTime != null) {
                roomConfDate.setVisible(true);
                roomConfDateText.setVisible(true);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
                String formatRoomDateTime = freeCabinetTime.format(formatter);
                roomConfDate.setValue(formatRoomDateTime);
                roomConfDate.setStyleName("bold");
            }
        }
    }

    @Subscribe("monday")
    public void onMondayValueChange(HasValue.ValueChangeEvent<Boolean> event) {
        if (Boolean.TRUE.equals(event.getValue())) {
            mondayStartTime.setEnabled(true);
            mondayEndTime.setEnabled(true);
        } else {
            mondayStartTime.setEnabled(false);
            mondayEndTime.setEnabled(false);
            mondayStartTime.clear();
            mondayEndTime.clear();
        }
    }
    @Subscribe("tuesday")
    public void onTuesdayValueChange(HasValue.ValueChangeEvent<Boolean> event) {
        if (Boolean.TRUE.equals(event.getValue())) {
            tuesdayStartTime.setEnabled(true);
            tuesdayEndTime.setEnabled(true);
        } else {
            tuesdayStartTime.setEnabled(false);
            tuesdayEndTime.setEnabled(false);
            tuesdayStartTime.clear();
            tuesdayEndTime.clear();
        }
    }
    @Subscribe("wednesday")
    public void onWednesdayValueChange(HasValue.ValueChangeEvent<Boolean> event) {
        if (Boolean.TRUE.equals(event.getValue())) {
            wednesdayStartTime.setEnabled(true);
            wednesdayEndTime.setEnabled(true);
        } else {
            wednesdayStartTime.setEnabled(false);
            wednesdayEndTime.setEnabled(false);
            wednesdayStartTime.clear();
            wednesdayEndTime.clear();
        }
    }
    @Subscribe("thursday")
    public void onThursdayValueChange(HasValue.ValueChangeEvent<Boolean> event) {
        if (Boolean.TRUE.equals(event.getValue())) {
            thursdayStartTime.setEnabled(true);
            thursdayEndTime.setEnabled(true);
        } else {
            thursdayStartTime.setEnabled(false);
            thursdayEndTime.setEnabled(false);
            thursdayStartTime.clear();
            thursdayEndTime.clear();
        }
    }
    @Subscribe("friday")
    public void onFridayValueChange(HasValue.ValueChangeEvent<Boolean> event) {
        if (Boolean.TRUE.equals(event.getValue())) {
            fridayStartTime.setEnabled(true);
            fridayEndTime.setEnabled(true);
        } else {
            fridayStartTime.setEnabled(false);
            fridayEndTime.setEnabled(false);
            fridayStartTime.clear();
            fridayEndTime.clear();
        }
    }
    @Subscribe("saturday")
    public void onSaturdayValueChange(HasValue.ValueChangeEvent<Boolean> event) {
        if (Boolean.TRUE.equals(event.getValue())) {
            saturdayStartTime.setEnabled(true);
            saturdayEndTime.setEnabled(true);
        } else {
            saturdayStartTime.setEnabled(false);
            saturdayEndTime.setEnabled(false);
            saturdayStartTime.clear();
            saturdayEndTime.clear();
        }
    }
    @Subscribe("sunday")
    public void onSundayValueChange(HasValue.ValueChangeEvent<Boolean> event) {
        if (Boolean.TRUE.equals(event.getValue())) {
            sundayStartTime.setEnabled(true);
            sundayEndTime.setEnabled(true);
        } else {
            sundayStartTime.setEnabled(false);
            sundayEndTime.setEnabled(false);
            sundayStartTime.clear();
            sundayEndTime.clear();
        }
    }

    @Subscribe("singleEndTime")
    public void onSingleEndTimeValueChange(HasValue.ValueChangeEvent<LocalTime> event) {
        LocalTime oldStartTime = Objects.requireNonNull(selectSchedule.getValue()).getCourseStartTime();
        LocalTime newStartTime = singleStartTime.getValue();
        LocalTime oldEndTime = selectSchedule.getValue().getCourseEndTime();
        LocalTime newEndTime = singleEndTime.getValue();
        if (courseMode.getValue() == CourseMode.SINGLE && (oldStartTime != newStartTime || oldEndTime != newEndTime)) {
            LocalDateTime StartDateTime = LocalDateTime.of(Objects.requireNonNull(courseStartDate.getValue()), Objects.requireNonNull(singleStartTime.getValue()));
            LocalDateTime EndDateTime = LocalDateTime.of(Objects.requireNonNull(courseEndDate.getValue()), Objects.requireNonNull(singleEndTime.getValue()));
            if (courseTeacherField.getValue() != null && courseCabinetField.getValue() != null) {
                LocalDateTime freeTeacherTime = findTeacherScheduleConflict(courseTeacherField.getValue(), StartDateTime, EndDateTime, singleStartTime.getValue(), singleEndTime.getValue(), null);
                LocalDateTime freeCabinetTime = findClassroomScheduleConflict(courseCabinetField.getValue(), StartDateTime, EndDateTime, singleStartTime.getValue(), singleEndTime.getValue(), null);
                if (freeTeacherTime != null) {
                    teacherConfDate.setVisible(true);
                    teacherConfDateText.setVisible(true);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
                    String formatTeacherDateTime = freeTeacherTime.format(formatter);
                    teacherConfDate.setValue(formatTeacherDateTime);
                    teacherConfDate.setStyleName("bold");
                }
                if (freeCabinetTime != null) {
                    roomConfDate.setVisible(true);
                    roomConfDateText.setVisible(true);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
                    String formatRoomDateTime = freeCabinetTime.format(formatter);
                    roomConfDate.setValue(formatRoomDateTime);
                    roomConfDate.setStyleName("bold");
                }
            } else {
                notifications.create(Notifications.NotificationType.ERROR)
                        .withCaption("Select Values")
                        .withDescription("Teacher and Room must be selected")
                        .show();
            }
        }
    }
    @Subscribe("mondayEndTime")
    public void onMondayEndTimeValueChange(HasValue.ValueChangeEvent<LocalTime> event) {
        LocalTime oldStartTime = Objects.requireNonNull(selectSchedule.getValue()).getCourseStartTime();
        LocalTime newStartTime = mondayStartTime.getValue();
        LocalTime oldEndTime = selectSchedule.getValue().getCourseEndTime();
        LocalTime newEndTime = mondayEndTime.getValue();
        if (Boolean.TRUE.equals(monday.getValue()) && (oldStartTime != newStartTime || oldEndTime != newEndTime)) {
            LocalDateTime StartDateTime = LocalDateTime.of(Objects.requireNonNull(courseStartDate.getValue()), Objects.requireNonNull(mondayStartTime.getValue()));
            LocalDateTime EndDateTime = LocalDateTime.of(Objects.requireNonNull(courseEndDate.getValue()), Objects.requireNonNull(mondayEndTime.getValue()));
            if (courseTeacherField.getValue() != null && courseCabinetField.getValue() != null) {
                LocalDateTime freeTeacherTime = findTeacherScheduleConflict(courseTeacherField.getValue(), StartDateTime, EndDateTime, mondayStartTime.getValue(), mondayEndTime.getValue(), DayOfWeek.MONDAY);
                LocalDateTime freeCabinetTime = findClassroomScheduleConflict(courseCabinetField.getValue(), StartDateTime, EndDateTime, mondayStartTime.getValue(), mondayEndTime.getValue(), DayOfWeek.MONDAY);
                if (freeTeacherTime != null) {
                    teacherConfDate.setVisible(true);
                    teacherConfDateText.setVisible(true);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
                    String formatTeacherDateTime = freeTeacherTime.format(formatter);
                    teacherConfDate.setValue(formatTeacherDateTime);
                    teacherConfDate.setStyleName("bold");
                }
                if (freeCabinetTime != null) {
                    roomConfDate.setVisible(true);
                    roomConfDateText.setVisible(true);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
                    String formatRoomDateTime = freeCabinetTime.format(formatter);
                    roomConfDate.setValue(formatRoomDateTime);
                    roomConfDate.setStyleName("bold");
                }
            } else {
                notifications.create(Notifications.NotificationType.ERROR)
                        .withCaption("Select Values")
                        .withDescription("Teacher and Room must be selected")
                        .show();
            }
        }
    }
    @Subscribe("tuesdayEndTime")
    public void onTuesdayEndTimeValueChange(HasValue.ValueChangeEvent<LocalTime> event) {
        LocalTime oldStartTime = Objects.requireNonNull(selectSchedule.getValue()).getCourseStartTime();
        LocalTime newStartTime = tuesdayStartTime.getValue();
        LocalTime oldEndTime = selectSchedule.getValue().getCourseEndTime();
        LocalTime newEndTime = tuesdayEndTime.getValue();
        if (Boolean.TRUE.equals(tuesday.getValue()) && (oldStartTime != newStartTime || oldEndTime != newEndTime)) {
            LocalDateTime StartDateTime = LocalDateTime.of(Objects.requireNonNull(courseStartDate.getValue()), Objects.requireNonNull(tuesdayStartTime.getValue()));
            LocalDateTime EndDateTime = LocalDateTime.of(Objects.requireNonNull(courseEndDate.getValue()), Objects.requireNonNull(tuesdayEndTime.getValue()));
            if (courseTeacherField.getValue() != null && courseCabinetField.getValue() != null) {
                LocalDateTime freeTeacherTime = findTeacherScheduleConflict(courseTeacherField.getValue(), StartDateTime, EndDateTime, tuesdayStartTime.getValue(), tuesdayEndTime.getValue(), DayOfWeek.TUESDAY);
                LocalDateTime freeCabinetTime = findClassroomScheduleConflict(courseCabinetField.getValue(), StartDateTime, EndDateTime, tuesdayStartTime.getValue(), tuesdayEndTime.getValue(), DayOfWeek.TUESDAY);
                if (freeTeacherTime != null) {
                    teacherConfDate.setVisible(true);
                    teacherConfDateText.setVisible(true);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
                    String formatTeacherDateTime = freeTeacherTime.format(formatter);
                    teacherConfDate.setValue(formatTeacherDateTime);
                    teacherConfDate.setStyleName("bold");
                }
                if (freeCabinetTime != null) {
                    roomConfDate.setVisible(true);
                    roomConfDateText.setVisible(true);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
                    String formatRoomDateTime = freeCabinetTime.format(formatter);
                    roomConfDate.setValue(formatRoomDateTime);
                    roomConfDate.setStyleName("bold");
                }
            } else {
                notifications.create(Notifications.NotificationType.ERROR)
                        .withCaption("Select Values")
                        .withDescription("Teacher and Room must be selected")
                        .show();
            }
        }
    }
    @Subscribe("wednesdayEndTime")
    public void onWednesdayEndTimeValueChange(HasValue.ValueChangeEvent<LocalTime> event) {
        LocalTime oldStartTime = Objects.requireNonNull(selectSchedule.getValue()).getCourseStartTime();
        LocalTime newStartTime = wednesdayStartTime.getValue();
        LocalTime oldEndTime = selectSchedule.getValue().getCourseEndTime();
        LocalTime newEndTime = wednesdayEndTime.getValue();
        if (Boolean.TRUE.equals(wednesday.getValue()) && (oldStartTime != newStartTime || oldEndTime != newEndTime)) {
            LocalDateTime StartDateTime = LocalDateTime.of(Objects.requireNonNull(courseStartDate.getValue()), Objects.requireNonNull(wednesdayStartTime.getValue()));
            LocalDateTime EndDateTime = LocalDateTime.of(Objects.requireNonNull(courseEndDate.getValue()), Objects.requireNonNull(wednesdayEndTime.getValue()));
            if (courseTeacherField.getValue() != null && courseCabinetField.getValue() != null) {
                LocalDateTime freeTeacherTime = findTeacherScheduleConflict(courseTeacherField.getValue(), StartDateTime, EndDateTime, wednesdayStartTime.getValue(), wednesdayEndTime.getValue(), DayOfWeek.WEDNESDAY);
                LocalDateTime freeCabinetTime = findClassroomScheduleConflict(courseCabinetField.getValue(), StartDateTime, EndDateTime, wednesdayStartTime.getValue(), wednesdayEndTime.getValue(), DayOfWeek.WEDNESDAY);
                if (freeTeacherTime != null) {
                    teacherConfDate.setVisible(true);
                    teacherConfDateText.setVisible(true);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
                    String formatTeacherDateTime = freeTeacherTime.format(formatter);
                    teacherConfDate.setValue(formatTeacherDateTime);
                    teacherConfDate.setStyleName("bold");
                }
                if (freeCabinetTime != null) {
                    roomConfDate.setVisible(true);
                    roomConfDateText.setVisible(true);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
                    String formatRoomDateTime = freeCabinetTime.format(formatter);
                    roomConfDate.setValue(formatRoomDateTime);
                    roomConfDate.setStyleName("bold");
                }
            } else {
                notifications.create(Notifications.NotificationType.ERROR)
                        .withCaption("Select Values")
                        .withDescription("Teacher and Room must be selected")
                        .show();
            }
        }
    }
    @Subscribe("thursdayEndTime")
    public void onThursdayEndTimeValueChange(HasValue.ValueChangeEvent<LocalTime> event) {
        LocalTime oldStartTime = Objects.requireNonNull(selectSchedule.getValue()).getCourseStartTime();
        LocalTime newStartTime = thursdayStartTime.getValue();
        LocalTime oldEndTime = selectSchedule.getValue().getCourseEndTime();
        LocalTime newEndTime = thursdayEndTime.getValue();
        if (Boolean.TRUE.equals(thursday.getValue()) && (oldStartTime != newStartTime || oldEndTime != newEndTime)) {
            LocalDateTime StartDateTime = LocalDateTime.of(Objects.requireNonNull(courseStartDate.getValue()), Objects.requireNonNull(thursdayStartTime.getValue()));
            LocalDateTime EndDateTime = LocalDateTime.of(Objects.requireNonNull(courseEndDate.getValue()), Objects.requireNonNull(thursdayEndTime.getValue()));
            if (courseTeacherField.getValue() != null && courseCabinetField.getValue() != null) {
                LocalDateTime freeTeacherTime = findTeacherScheduleConflict(courseTeacherField.getValue(), StartDateTime, EndDateTime, thursdayStartTime.getValue(), thursdayEndTime.getValue(), DayOfWeek.THURSDAY);
                LocalDateTime freeCabinetTime = findClassroomScheduleConflict(courseCabinetField.getValue(), StartDateTime, EndDateTime, thursdayStartTime.getValue(), thursdayEndTime.getValue(), DayOfWeek.THURSDAY);
                if (freeTeacherTime != null) {
                    teacherConfDate.setVisible(true);
                    teacherConfDateText.setVisible(true);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
                    String formatTeacherDateTime = freeTeacherTime.format(formatter);
                    teacherConfDate.setValue(formatTeacherDateTime);
                    teacherConfDate.setStyleName("bold");
                }
                if (freeCabinetTime != null) {
                    roomConfDate.setVisible(true);
                    roomConfDateText.setVisible(true);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
                    String formatRoomDateTime = freeCabinetTime.format(formatter);
                    roomConfDate.setValue(formatRoomDateTime);
                    roomConfDate.setStyleName("bold");
                }
            } else {
                notifications.create(Notifications.NotificationType.ERROR)
                        .withCaption("Select Values")
                        .withDescription("Teacher and Room must be selected")
                        .show();
            }
        }
    }
    @Subscribe("fridayEndTime")
    public void onFridayEndTimeValueChange(HasValue.ValueChangeEvent<LocalTime> event) {
        LocalTime oldStartTime = Objects.requireNonNull(selectSchedule.getValue()).getCourseStartTime();
        LocalTime newStartTime = fridayStartTime.getValue();
        LocalTime oldEndTime = selectSchedule.getValue().getCourseEndTime();
        LocalTime newEndTime = fridayEndTime.getValue();
        if (Boolean.TRUE.equals(friday.getValue()) && (oldStartTime != newStartTime || oldEndTime != newEndTime)) {
            LocalDateTime StartDateTime = LocalDateTime.of(Objects.requireNonNull(courseStartDate.getValue()), Objects.requireNonNull(fridayStartTime.getValue()));
            LocalDateTime EndDateTime = LocalDateTime.of(Objects.requireNonNull(courseEndDate.getValue()), Objects.requireNonNull(fridayEndTime.getValue()));
            if (courseTeacherField.getValue() != null && courseCabinetField.getValue() != null) {
                LocalDateTime freeTeacherTime = findTeacherScheduleConflict(courseTeacherField.getValue(), StartDateTime, EndDateTime, fridayStartTime.getValue(), fridayEndTime.getValue(), DayOfWeek.FRIDAY);
                LocalDateTime freeCabinetTime = findClassroomScheduleConflict(courseCabinetField.getValue(), StartDateTime, EndDateTime, fridayStartTime.getValue(), fridayEndTime.getValue(), DayOfWeek.FRIDAY);
                if (freeTeacherTime != null) {
                    teacherConfDate.setVisible(true);
                    teacherConfDateText.setVisible(true);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
                    String formatTeacherDateTime = freeTeacherTime.format(formatter);
                    teacherConfDate.setValue(formatTeacherDateTime);
                    teacherConfDate.setStyleName("bold");
                }
                if (freeCabinetTime != null) {
                    roomConfDate.setVisible(true);
                    roomConfDateText.setVisible(true);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
                    String formatRoomDateTime = freeCabinetTime.format(formatter);
                    roomConfDate.setValue(formatRoomDateTime);
                    roomConfDate.setStyleName("bold");
                }
            } else {
                notifications.create(Notifications.NotificationType.ERROR)
                        .withCaption("Select Values")
                        .withDescription("Teacher and Room must be selected")
                        .show();
            }
        }
    }
    @Subscribe("saturdayEndTime")
    public void onSaturdayEndTimeValueChange(HasValue.ValueChangeEvent<LocalTime> event) {
        LocalTime oldStartTime = Objects.requireNonNull(selectSchedule.getValue()).getCourseStartTime();
        LocalTime newStartTime = saturdayStartTime.getValue();
        LocalTime oldEndTime = selectSchedule.getValue().getCourseEndTime();
        LocalTime newEndTime = saturdayEndTime.getValue();
        if (Boolean.TRUE.equals(saturday.getValue()) && (oldStartTime != newStartTime || oldEndTime != newEndTime)) {
            LocalDateTime StartDateTime = LocalDateTime.of(Objects.requireNonNull(courseStartDate.getValue()), Objects.requireNonNull(saturdayStartTime.getValue()));
            LocalDateTime EndDateTime = LocalDateTime.of(Objects.requireNonNull(courseEndDate.getValue()), Objects.requireNonNull(saturdayEndTime.getValue()));
            if (courseTeacherField.getValue() != null && courseCabinetField.getValue() != null) {
                LocalDateTime freeTeacherTime = findTeacherScheduleConflict(courseTeacherField.getValue(), StartDateTime, EndDateTime, saturdayStartTime.getValue(), saturdayEndTime.getValue(), DayOfWeek.SATURDAY);
                LocalDateTime freeCabinetTime = findClassroomScheduleConflict(courseCabinetField.getValue(), StartDateTime, EndDateTime, saturdayStartTime.getValue(), saturdayEndTime.getValue(), DayOfWeek.SATURDAY);
                if (freeTeacherTime != null) {
                    teacherConfDate.setVisible(true);
                    teacherConfDateText.setVisible(true);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
                    String formatTeacherDateTime = freeTeacherTime.format(formatter);
                    teacherConfDate.setValue(formatTeacherDateTime);
                    teacherConfDate.setStyleName("bold");
                }
                if (freeCabinetTime != null) {
                    roomConfDate.setVisible(true);
                    roomConfDateText.setVisible(true);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
                    String formatRoomDateTime = freeCabinetTime.format(formatter);
                    roomConfDate.setValue(formatRoomDateTime);
                    roomConfDate.setStyleName("bold");
                }
            } else {
                notifications.create(Notifications.NotificationType.ERROR)
                        .withCaption("Select Values")
                        .withDescription("Teacher and Room must be selected")
                        .show();
            }
        }
    }
    @Subscribe("sundayEndTime")
    public void onSundayEndTimeValueChange(HasValue.ValueChangeEvent<LocalTime> event) {
        LocalTime oldStartTime = Objects.requireNonNull(selectSchedule.getValue()).getCourseStartTime();
        LocalTime newStartTime = sundayStartTime.getValue();
        LocalTime oldEndTime = selectSchedule.getValue().getCourseEndTime();
        LocalTime newEndTime = sundayEndTime.getValue();
        if (Boolean.TRUE.equals(sunday.getValue()) && (oldStartTime != newStartTime || oldEndTime != newEndTime)) {
            LocalDateTime StartDateTime = LocalDateTime.of(Objects.requireNonNull(courseStartDate.getValue()), Objects.requireNonNull(sundayStartTime.getValue()));
            LocalDateTime EndDateTime = LocalDateTime.of(Objects.requireNonNull(courseEndDate.getValue()), Objects.requireNonNull(sundayEndTime.getValue()));
            if (courseTeacherField.getValue() != null && courseCabinetField.getValue() != null) {
                LocalDateTime freeTeacherTime = findTeacherScheduleConflict(courseTeacherField.getValue(), StartDateTime, EndDateTime, sundayStartTime.getValue(), sundayEndTime.getValue(), DayOfWeek.SUNDAY);
                LocalDateTime freeCabinetTime = findClassroomScheduleConflict(courseCabinetField.getValue(), StartDateTime, EndDateTime, sundayStartTime.getValue(), sundayEndTime.getValue(), DayOfWeek.SUNDAY);
                if (freeTeacherTime != null) {
                    teacherConfDate.setVisible(true);
                    teacherConfDateText.setVisible(true);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
                    String formatTeacherDateTime = freeTeacherTime.format(formatter);
                    teacherConfDate.setValue(formatTeacherDateTime);
                    teacherConfDate.setStyleName("bold");
                }
                if (freeCabinetTime != null) {
                    roomConfDate.setVisible(true);
                    roomConfDateText.setVisible(true);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
                    String formatRoomDateTime = freeCabinetTime.format(formatter);
                    roomConfDate.setValue(formatRoomDateTime);
                    roomConfDate.setStyleName("bold");
                }
            } else {
                notifications.create(Notifications.NotificationType.ERROR)
                        .withCaption("Select Values")
                        .withDescription("Teacher and Room must be selected")
                        .show();
            }
        }
    }

    @Subscribe("editSchedule")
    public void onEditSchedule(Action.ActionPerformedEvent event) {
        YearMonth startYearMonth = YearMonth.from(Objects.requireNonNull(courseStartDate.getValue()));
        YearMonth endYearMonth = YearMonth.from(Objects.requireNonNull(courseEndDate.getValue()));
        long monthsBetween = ChronoUnit.MONTHS.between(startYearMonth, endYearMonth);
        long groupCourseDuration = Objects.requireNonNull(courseGroupField.getValue()).getGroupDirection().getDirectionDuration();
        if (monthsBetween > groupCourseDuration) {
            notifications.create(Notifications.NotificationType.ERROR)
                    .withCaption("Duration exceeded")
                    .withDescription("The duration of the course should be no more than "+groupCourseDuration+" months")
                    .show();
            return;
        }

        CourseType courseType = Objects.requireNonNull(courseGroupField.getValue()).getGroupDirection().getDirectionType();
        BigDecimal courseCost = courseCostField.getValue();
        BigDecimal sngCourseCount = BigDecimal.ONE;

        if (courseMode.getValue() == CourseMode.SINGLE) {
            EditCourses(courseNameField.getValue(), CourseStatus.NEW, courseGroupField.getValue(), courseCost, sngCourseCount,
                    courseStartDate.getValue(), courseEndDate.getValue(), singleStartTime.getValue(), singleEndTime.getValue(),
                    courseCabinetField.getValue(), courseTeacherField.getValue(), courseManagerField.getValue(), courseBranchField.getValue(), courseType);
        }
        if (courseMode.getValue() == CourseMode.REPEAT) {
            LocalDate startDate = courseStartDate.getValue();
            LocalDate endDate = courseEndDate.getValue();
            List<LocalDate> lessonDates = new ArrayList<>();
            while (Objects.requireNonNull(startDate).isBefore(Objects.requireNonNull(endDate)) || Objects.requireNonNull(startDate).equals(Objects.requireNonNull(endDate))) {
                if (Boolean.TRUE.equals(monday.getValue()) && mondayStartTime != null && mondayEndTime != null) {
                    if (startDate.getDayOfWeek().equals(DayOfWeek.MONDAY)) {
                        if (startDate.getYear() == Year.now().getValue()) {
                            if (Objects.requireNonNull(getBusinessCalendarByCurrentYear()).isBusinessDay(startDate)) {
                                lessonDates.add(startDate);
                            }
                        } else {
                            if (Objects.requireNonNull(getBusinessCalendarByNextYear()).isBusinessDay(startDate)) {
                                lessonDates.add(startDate);
                            }
                        }
                    }
                }
                if (Boolean.TRUE.equals(tuesday.getValue()) && tuesdayStartTime != null && tuesdayEndTime != null) {
                    if (startDate.getDayOfWeek().equals(DayOfWeek.TUESDAY)) {
                        if (startDate.getYear() == Year.now().getValue()) {
                            if (Objects.requireNonNull(getBusinessCalendarByCurrentYear()).isBusinessDay(startDate)) {
                                lessonDates.add(startDate);
                            }
                        } else {
                            if (Objects.requireNonNull(getBusinessCalendarByNextYear()).isBusinessDay(startDate)) {
                                lessonDates.add(startDate);
                            }
                        }
                    }
                }
                if (Boolean.TRUE.equals(wednesday.getValue()) && wednesdayStartTime != null && wednesdayEndTime != null) {
                    if (startDate.getDayOfWeek().equals(DayOfWeek.WEDNESDAY)) {
                        if (startDate.getYear() == Year.now().getValue()) {
                            if (Objects.requireNonNull(getBusinessCalendarByCurrentYear()).isBusinessDay(startDate)) {
                                lessonDates.add(startDate);
                            }
                        } else {
                            if (Objects.requireNonNull(getBusinessCalendarByNextYear()).isBusinessDay(startDate)) {
                                lessonDates.add(startDate);
                            }
                        }
                    }
                }
                if (Boolean.TRUE.equals(thursday.getValue()) && thursdayStartTime != null && thursdayEndTime != null) {
                    if (startDate.getDayOfWeek().equals(DayOfWeek.THURSDAY)) {
                        if (startDate.getYear() == Year.now().getValue()) {
                            if (Objects.requireNonNull(getBusinessCalendarByCurrentYear()).isBusinessDay(startDate)) {
                                lessonDates.add(startDate);
                            }
                        } else {
                            if (Objects.requireNonNull(getBusinessCalendarByNextYear()).isBusinessDay(startDate)) {
                                lessonDates.add(startDate);
                            }
                        }
                    }
                }
                if (Boolean.TRUE.equals(friday.getValue()) && fridayStartTime != null && fridayEndTime != null) {
                    if (startDate.getDayOfWeek().equals(DayOfWeek.FRIDAY)) {
                        if (startDate.getYear() == Year.now().getValue()) {
                            if (Objects.requireNonNull(getBusinessCalendarByCurrentYear()).isBusinessDay(startDate)) {
                                lessonDates.add(startDate);
                            }
                        } else {
                            if (Objects.requireNonNull(getBusinessCalendarByNextYear()).isBusinessDay(startDate)) {
                                lessonDates.add(startDate);
                            }
                        }
                    }
                }
                if (Boolean.TRUE.equals(saturday.getValue()) && saturdayStartTime != null && saturdayEndTime != null) {
                    if (startDate.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
                        if (startDate.getYear() == Year.now().getValue()) {
                            if (Objects.requireNonNull(getBusinessCalendarByCurrentYear()).isBusinessDay(startDate)) {
                                lessonDates.add(startDate);
                            }
                        } else {
                            if (Objects.requireNonNull(getBusinessCalendarByNextYear()).isBusinessDay(startDate)) {
                                lessonDates.add(startDate);
                            }
                        }
                    }
                }
                if (Boolean.TRUE.equals(sunday.getValue()) && sundayStartTime != null && sundayEndTime != null) {
                    if (startDate.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
                        if (startDate.getYear() == Year.now().getValue()) {
                            if (Objects.requireNonNull(getBusinessCalendarByCurrentYear()).isBusinessDay(startDate)) {
                                lessonDates.add(startDate);
                            }
                        } else {
                            if (Objects.requireNonNull(getBusinessCalendarByNextYear()).isBusinessDay(startDate)) {
                                lessonDates.add(startDate);
                            }
                        }
                    }
                }
                startDate = startDate.plusDays(1);
            }
            BigDecimal lessonCount = BigDecimal.valueOf(lessonDates.size());
            for (LocalDate lessonDate : lessonDates) {
                if (lessonDate.getDayOfWeek() == DayOfWeek.MONDAY) {
                    EditCourses(courseNameField.getValue(), CourseStatus.NEW, courseGroupField.getValue(), courseCost, lessonCount,
                            lessonDate, lessonDate, mondayStartTime.getValue(), mondayEndTime.getValue(),
                            courseCabinetField.getValue(), courseTeacherField.getValue(), courseManagerField.getValue(), courseBranchField.getValue(), courseType);
                }
                if (lessonDate.getDayOfWeek() == DayOfWeek.TUESDAY) {
                    EditCourses(courseNameField.getValue(), CourseStatus.NEW, courseGroupField.getValue(), courseCost, lessonCount,
                            lessonDate, lessonDate, tuesdayStartTime.getValue(), tuesdayEndTime.getValue(),
                            courseCabinetField.getValue(), courseTeacherField.getValue(), courseManagerField.getValue(), courseBranchField.getValue(), courseType);
                }
                if (lessonDate.getDayOfWeek() == DayOfWeek.WEDNESDAY) {
                    EditCourses(courseNameField.getValue(), CourseStatus.NEW, courseGroupField.getValue(), courseCost, lessonCount,
                            lessonDate, lessonDate, wednesdayStartTime.getValue(), wednesdayEndTime.getValue(),
                            courseCabinetField.getValue(), courseTeacherField.getValue(), courseManagerField.getValue(), courseBranchField.getValue(), courseType);
                }
                if (lessonDate.getDayOfWeek() == DayOfWeek.THURSDAY) {
                    EditCourses(courseNameField.getValue(), CourseStatus.NEW, courseGroupField.getValue(), courseCost, lessonCount,
                            lessonDate, lessonDate, thursdayStartTime.getValue(), thursdayEndTime.getValue(),
                            courseCabinetField.getValue(), courseTeacherField.getValue(), courseManagerField.getValue(), courseBranchField.getValue(), courseType);
                }
                if (lessonDate.getDayOfWeek() == DayOfWeek.FRIDAY) {
                    EditCourses(courseNameField.getValue(), CourseStatus.NEW, courseGroupField.getValue(), courseCost, lessonCount,
                            lessonDate, lessonDate, fridayStartTime.getValue(), fridayEndTime.getValue(),
                            courseCabinetField.getValue(), courseTeacherField.getValue(), courseManagerField.getValue(), courseBranchField.getValue(), courseType);
                }
                if (lessonDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
                    EditCourses(courseNameField.getValue(), CourseStatus.NEW, courseGroupField.getValue(), courseCost, lessonCount,
                            lessonDate, lessonDate, saturdayStartTime.getValue(), saturdayEndTime.getValue(),
                            courseCabinetField.getValue(), courseTeacherField.getValue(), courseManagerField.getValue(), courseBranchField.getValue(), courseType);
                }
                if (lessonDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    EditCourses(courseNameField.getValue(), CourseStatus.NEW, courseGroupField.getValue(), courseCost, lessonCount,
                            lessonDate, lessonDate, sundayStartTime.getValue(), sundayEndTime.getValue(),
                            courseCabinetField.getValue(), courseTeacherField.getValue(), courseManagerField.getValue(), courseBranchField.getValue(), courseType);
                }
            }
        }
        List<Courses> oldCourses = selectedCourse();
        dataManager.remove(oldCourses);
        close(StandardOutcome.DISCARD);
    }
}