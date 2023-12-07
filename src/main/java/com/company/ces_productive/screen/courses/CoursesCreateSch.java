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
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.screen.*;
import com.company.ces_productive.entity.courses.Courses;
import org.springframework.beans.factory.annotation.Autowired;
import io.jmix.ui.navigation.Route;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@UiController("CES_Courses.createSch")
@UiDescriptor("courses-createSch.xml")
@EditedEntityContainer("coursesDc")
@Route(value = "courses/createSch", parentPrefix = "courses")
public class CoursesCreateSch extends StandardEditor<Courses> {
    @Autowired
    private CurrentAuthentication currentAuthentication;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private VBoxLayout weekDays;
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
    private Notifications notifications;
    @Autowired
    private BusinessCalendarRepository businessCalendarRepository;
    @Autowired
    private Label<String> teacherConfDate;
    @Autowired
    private Label<String> roomConfDate;
    @Autowired
    private Label<String> roomConfDateText;
    @Autowired
    private Label<String> teacherConfDateText;
    @Autowired
    private CheckBox isFreeCost;
    @Autowired
    private Button createScheduleBtn;

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
    /*private LocalDate getNextWorkingDay(LocalDate date) {
        while (!Objects.requireNonNull(getBusinessCalendarByCurrentYear()).isBusinessDay(date)) {
            date = date.plusDays(1);
        }
        return date;
    }*/

    private void addCourseCount(LocalDate startDate, LocalDate endDate, Boolean thisDayIs, DayOfWeek thisDay) {
        List<LocalDate> lessonDates = new ArrayList<>();
        while (Objects.requireNonNull(startDate).isBefore(Objects.requireNonNull(endDate))) {
            if (Boolean.TRUE.equals(thisDayIs)) {
                if (startDate.getDayOfWeek().equals(thisDay)) {
                    lessonDates.add(startDate);
                }
            }
            startDate = startDate.plusDays(1);
        }
        BigDecimal courseCount = BigDecimal.valueOf(lessonDates.size());
        BigDecimal courseCountReal = courseCountField.getValue();
        courseCountField.setValue(Objects.requireNonNull(courseCountReal).add(courseCount));
    }
    private void subtractCourseCount(LocalDate startDate, LocalDate endDate, Boolean thisDayIs, DayOfWeek thisDay) {
        List<LocalDate> lessonDates = new ArrayList<>();
        while (Objects.requireNonNull(startDate).isBefore(Objects.requireNonNull(endDate))) {
            if (Boolean.FALSE.equals(thisDayIs)) {
                if (startDate.getDayOfWeek().equals(thisDay)) {
                    lessonDates.add(startDate);
                }
            }
            startDate = startDate.plusDays(1);
        }
        BigDecimal courseCount = BigDecimal.valueOf(lessonDates.size());
        BigDecimal courseCountReal = courseCountField.getValue();
        courseCountField.setValue(Objects.requireNonNull(courseCountReal).subtract(courseCount));
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

    public BigDecimal getCourseCost () {
        if (isFreeCost.isChecked()) {
            return BigDecimal.ZERO;
        } else {
            return Objects.requireNonNull(courseGroupField.getValue()).getGroupCost()
                    .divide(courseGroupField.getValue().getGroupDirection().getDirectionCount(), RoundingMode.UP);
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

    @Subscribe("courseGroupField")
    public void onCourseGroupFieldFieldValueChange(ValuePicker.FieldValueChangeEvent<Groups> event) {
        dataManager.load(Groups.class)
                .query("select g.user from CES_Groups g where g.groupBranch = :currentBranch and g.groupStatus in :actualGroupStatus")
                .parameter("currentBranch", ((User) currentAuthentication.getUser()).getBranch())
                .parameter("actualGroupStatus", GroupStatus.OPEN)
                .parameter("actualGroupStatus", GroupStatus.FULL)
                .list();

    }
    @Subscribe("courseGroupField")
    public void onCourseGroupFieldValueChange(HasValue.ValueChangeEvent<Groups> event) {
        courseTeacherField.setValue(Objects.requireNonNull(event.getValue()).getGroupTeacher());
        courseNameField.setValue(event.getValue().getGroupName());
        courseNameField.setEnabled(true);
        courseCabinetField.setEnabled(true);
        courseTeacherField.setEnabled(true);
    }

    @Install(to = "courseTeacherField", subject = "searchExecutor")
    private List<User> SuggestionFieldSearchExecutor(String searchString, Map<String, Object> searchParams) {
        return dataManager.load(User.class)
                .query("e.lastName like ?1 order by e.lastName", "(?i)%"
                        + searchString + "%")
                .list();
    }

    @Subscribe("courseCabinetField")
    public void onCourseCabinetFieldValueChange(HasValue.ValueChangeEvent<Cabinets> event) {
        String coursesName = courseNameField.getValue();
        String teacherName = Objects.requireNonNull(courseTeacherField.getValue()).getLastName();
        String branchName = ((User) currentAuthentication.getUser()).getBranch().getBranchName();
        courseNameField.setValue(branchName+": "+teacherName+ ": " +coursesName);
    }

    @Subscribe("courseTeacherField")
    public void onCourseTeacherFieldValueChange(HasValue.ValueChangeEvent<User> event) {
        String coursesName = courseNameField.getValue();
        String teacherName = Objects.requireNonNull(event.getValue()).getLastName();
        courseNameField.setValue(teacherName+ ": " +coursesName);
    }

    @Subscribe("courseStartDate")
    public void onCourseStartDateValueChange(final HasValue.ValueChangeEvent<LocalDate> event) {
        long courseMonth = Objects.requireNonNull(courseGroupField.getValue()).getGroupDirection().getDirectionDuration();
        courseEndDate.setValue(Objects.requireNonNull(courseStartDate.getValue()).plusMonths(courseMonth));
    }

    @Subscribe("courseEndDate")
    public void onCourseEndDateValueChange(HasValue.ValueChangeEvent<LocalDate> event) {
        courseMode.setEnabled(true);
    }

    @Subscribe("courseMode")
    public void onCourseModeValueChange(HasValue.ValueChangeEvent<CourseMode> event) {
        if (event.getValue() == CourseMode.SINGLE) {
            singleStartTime.setEnabled(true);
            singleStartTime.setVisible(true);
            singleEndTime.setEnabled(true);
            singleEndTime.setVisible(true);
            weekDays.setVisible(false);
            weekDays.setEnabled(false);
        } else {
            singleStartTime.setEnabled(false);
            singleStartTime.setVisible(false);
            singleEndTime.setEnabled(false);
            singleEndTime.setVisible(false);
            weekDays.setVisible(true);
            weekDays.setEnabled(true);
            courseCostField.setEnabled(false);
            courseCountField.setEnabled(false);
            courseCountField.setValue(BigDecimal.ZERO);
            YearMonth startYearMonth = YearMonth.from(Objects.requireNonNull(courseStartDate.getValue()));
            YearMonth endYearMonth = YearMonth.from(Objects.requireNonNull(courseEndDate.getValue()));
            long monthsBetween = ChronoUnit.MONTHS.between(startYearMonth, endYearMonth);
            long groupCourseDuration = Objects.requireNonNull(courseGroupField.getValue()).getGroupDirection().getDirectionDuration();
            if (monthsBetween > groupCourseDuration+0.5) {
                notifications.create(Notifications.NotificationType.ERROR)
                        .withCaption("Длительность расписания")
                        .withDescription("По данной группе длительность расписания не может быть больше "+groupCourseDuration+" месяцев")
                        .show();
                courseEndDate.clear();
                courseStartDate.clear();
                courseStartDate.setEnabled(false);
                courseEndDate.setEnabled(false);
                weekDays.setEnabled(false);
                courseMode.setEnabled(false);
                createScheduleBtn.setEnabled(false);
            } else {
                courseStartDate.setEnabled(false);
                courseEndDate.setEnabled(false);
            }
        }
    }

    @Subscribe("monday")
    public void onMondayValueChange(HasValue.ValueChangeEvent<Boolean> event) {
        LocalDate startDate = courseStartDate.getValue();
        LocalDate endDate = courseEndDate.getValue();
        if (startDate == null || endDate == null) {
            notifications.create(Notifications.NotificationType.ERROR)
                    .withCaption("Заполните даты")
                    .withDescription("Необходимо заполнить дату начала и окончания по расписанию")
                    .show();
        }
        else if (Boolean.TRUE.equals(event.getValue())) {
            mondayStartTime.setEnabled(true);
            mondayEndTime.setEnabled(true);
            addCourseCount(startDate, endDate, monday.getValue(), DayOfWeek.MONDAY);
        } else {
            mondayStartTime.setEnabled(false);
            mondayEndTime.setEnabled(false);
            subtractCourseCount(startDate, endDate, monday.getValue(), DayOfWeek.MONDAY);
        }
    }

    @Subscribe("tuesday")
    public void onTuesdayValueChange(HasValue.ValueChangeEvent<Boolean> event) {
        LocalDate startDate = courseStartDate.getValue();
        LocalDate endDate = courseEndDate.getValue();
        if (startDate == null || endDate == null) {
            notifications.create(Notifications.NotificationType.ERROR)
                    .withCaption("Заполните даты")
                    .withDescription("Необходимо заполнить дату начала и окончания по расписанию")
                    .show();
        }
        else if (Boolean.TRUE.equals(event.getValue())) {
            tuesdayStartTime.setEnabled(true);
            tuesdayEndTime.setEnabled(true);
            addCourseCount(startDate, endDate, tuesday.getValue(), DayOfWeek.TUESDAY);
        } else {
            tuesdayStartTime.setEnabled(false);
            tuesdayEndTime.setEnabled(false);
            subtractCourseCount(startDate, endDate, tuesday.getValue(), DayOfWeek.TUESDAY);
        }
    }

    @Subscribe("wednesday")
    public void onWednesdayValueChange(HasValue.ValueChangeEvent<Boolean> event) {
        LocalDate startDate = courseStartDate.getValue();
        LocalDate endDate = courseEndDate.getValue();
        if (startDate == null || endDate == null) {
            notifications.create(Notifications.NotificationType.ERROR)
                    .withCaption("Заполните даты")
                    .withDescription("Необходимо заполнить дату начала и окончания по расписанию")
                    .show();
        }
        else if (Boolean.TRUE.equals(event.getValue())) {
            wednesdayStartTime.setEnabled(true);
            wednesdayEndTime.setEnabled(true);
            addCourseCount(startDate, endDate, wednesday.getValue(), DayOfWeek.WEDNESDAY);
        } else {
            wednesdayStartTime.setEnabled(false);
            wednesdayEndTime.setEnabled(false);
            subtractCourseCount(startDate, endDate,  wednesday.getValue(), DayOfWeek.WEDNESDAY);
        }
    }

    @Subscribe("thursday")
    public void onThursdayValueChange(HasValue.ValueChangeEvent<Boolean> event) {
        LocalDate startDate = courseStartDate.getValue();
        LocalDate endDate = courseEndDate.getValue();
        if (startDate == null || endDate == null) {
            notifications.create(Notifications.NotificationType.ERROR)
                    .withCaption("Заполните даты")
                    .withDescription("Необходимо заполнить дату начала и окончания по расписанию")
                    .show();
        }
        else if (Boolean.TRUE.equals(event.getValue())) {
            thursdayStartTime.setEnabled(true);
            thursdayEndTime.setEnabled(true);
            addCourseCount(startDate, endDate, thursday.getValue(), DayOfWeek.THURSDAY);
        } else {
            thursdayStartTime.setEnabled(false);
            thursdayEndTime.setEnabled(false);
            subtractCourseCount(startDate, endDate,  thursday.getValue(), DayOfWeek.THURSDAY);
        }
    }

    @Subscribe("friday")
    public void onFridayValueChange(HasValue.ValueChangeEvent<Boolean> event) {
        LocalDate startDate = courseStartDate.getValue();
        LocalDate endDate = courseEndDate.getValue();
        if (startDate == null || endDate == null) {
            notifications.create(Notifications.NotificationType.ERROR)
                    .withCaption("Заполните даты")
                    .withDescription("Необходимо заполнить дату начала и окончания по расписанию")
                    .show();
        }
        else if (Boolean.TRUE.equals(event.getValue())) {
            fridayStartTime.setEnabled(true);
            fridayEndTime.setEnabled(true);
            addCourseCount(startDate, endDate, friday.getValue(), DayOfWeek.FRIDAY);
        } else {
            fridayStartTime.setEnabled(false);
            fridayEndTime.setEnabled(false);
            subtractCourseCount(startDate, endDate,  friday.getValue(), DayOfWeek.FRIDAY);
        }
    }

    @Subscribe("saturday")
    public void onSaturdayValueChange(HasValue.ValueChangeEvent<Boolean> event) {
        LocalDate startDate = courseStartDate.getValue();
        LocalDate endDate = courseEndDate.getValue();
        if (startDate == null || endDate == null) {
            notifications.create(Notifications.NotificationType.ERROR)
                    .withCaption("Заполните даты")
                    .withDescription("Необходимо заполнить дату начала и окончания по расписанию")
                    .show();
        }
        else if (Boolean.TRUE.equals(event.getValue())) {
            saturdayStartTime.setEnabled(true);
            saturdayEndTime.setEnabled(true);
            addCourseCount(startDate, endDate, saturday.getValue(), DayOfWeek.SATURDAY);
        } else {
            saturdayStartTime.setEnabled(false);
            saturdayEndTime.setEnabled(false);
            subtractCourseCount(startDate, endDate,  saturday.getValue(), DayOfWeek.SATURDAY);
        }
    }

    @Subscribe("sunday")
    public void onSundayValueChange(HasValue.ValueChangeEvent<Boolean> event) {
        LocalDate startDate = courseStartDate.getValue();
        LocalDate endDate = courseEndDate.getValue();
        if (startDate == null || endDate == null) {
            notifications.create(Notifications.NotificationType.ERROR)
                    .withCaption("Заполните даты")
                    .withDescription("Необходимо заполнить дату начала и окончания по расписанию")
                    .show();
        }
        else if (Boolean.TRUE.equals(event.getValue())) {
            sundayStartTime.setEnabled(true);
            sundayEndTime.setEnabled(true);
            addCourseCount(startDate, endDate, sunday.getValue(), DayOfWeek.SUNDAY);
        } else {
            sundayStartTime.setEnabled(false);
            sundayEndTime.setEnabled(false);
            subtractCourseCount(startDate, endDate,  sunday.getValue(), DayOfWeek.SUNDAY);
        }
    }

    @Subscribe("singleEndTime")
    public void onSingleEndTimeValueChange(HasValue.ValueChangeEvent<LocalTime> event) {
        if (courseStartDate != null && courseEndDate != null && singleStartTime != null && singleEndTime != null) {
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
                        .withCaption("Выберите параметры")
                        .withDescription("Необходимо выбрать кабинет и/или преподавателя")
                        .show();
            }
        } else {
            notifications.create(Notifications.NotificationType.ERROR)
                    .withCaption("Заполните даты")
                    .withDescription("Необходимо заполнить дату начала и окончания по расписанию")
                    .show();
        }
    }

    @Subscribe("mondayEndTime")
    public void onMondayEndTimeValueChange(HasValue.ValueChangeEvent<LocalTime> event) {
        if (courseStartDate != null && courseEndDate != null && mondayStartTime != null && mondayEndTime != null) {
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
                        .withCaption("Выберите параметры")
                        .withDescription("Необходимо выбрать кабинет и/или преподавателя")
                        .show();
            }
        } else {
            notifications.create(Notifications.NotificationType.ERROR)
                    .withCaption("Заполните даты")
                    .withDescription("Необходимо заполнить дату начала и окончания по расписанию")
                    .show();
        }
    }
    @Subscribe("tuesdayEndTime")
    public void onTuesdayEndTimeValueChange(HasValue.ValueChangeEvent<LocalTime> event) {
        if (courseStartDate != null && courseEndDate != null && tuesdayStartTime != null && tuesdayEndTime != null) {
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
                        .withCaption("Выберите параметры")
                        .withDescription("Необходимо выбрать кабинет и/или преподавателя")
                        .show();
            }
        } else {
            notifications.create(Notifications.NotificationType.ERROR)
                    .withCaption("Заполните даты")
                    .withDescription("Необходимо заполнить дату начала и окончания по расписанию")
                    .show();
        }
    }
    @Subscribe("wednesdayEndTime")
    public void onWednesdayEndTimeValueChange(HasValue.ValueChangeEvent<LocalTime> event) {
        if (courseStartDate != null && courseEndDate != null && wednesdayStartTime != null && wednesdayEndTime != null) {
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
                        .withCaption("Выберите параметры")
                        .withDescription("Необходимо выбрать кабинет и/или преподавателя")
                        .show();
            }
        } else {
            notifications.create(Notifications.NotificationType.ERROR)
                    .withCaption("Заполните даты")
                    .withDescription("Необходимо заполнить дату начала и окончания по расписанию")
                    .show();
        }
    }
    @Subscribe("thursdayEndTime")
    public void onThursdayEndTimeValueChange(HasValue.ValueChangeEvent<LocalTime> event) {
        if (courseStartDate != null && courseEndDate != null && thursdayStartTime != null && thursdayEndTime != null) {
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
                        .withCaption("Выберите параметры")
                        .withDescription("Необходимо выбрать кабинет и/или преподавателя")
                        .show();
            }
        } else {
            notifications.create(Notifications.NotificationType.ERROR)
                    .withCaption("Заполните даты")
                    .withDescription("Необходимо заполнить дату начала и окончания по расписанию")
                    .show();
        }
    }
    @Subscribe("fridayEndTime")
    public void onFridayEndTimeValueChange(HasValue.ValueChangeEvent<LocalTime> event) {
        if (courseStartDate != null && courseEndDate != null && fridayStartTime != null && fridayEndTime != null) {
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
                        .withCaption("Выберите параметры")
                        .withDescription("Необходимо выбрать кабинет и/или преподавателя")
                        .show();
            }
        } else {
            notifications.create(Notifications.NotificationType.ERROR)
                    .withCaption("Заполните даты")
                    .withDescription("Необходимо заполнить дату начала и окончания по расписанию")
                    .show();
        }
    }
    @Subscribe("saturdayEndTime")
    public void onSaturdayEndTimeValueChange(HasValue.ValueChangeEvent<LocalTime> event) {
        if (courseStartDate != null && courseEndDate != null && saturdayStartTime != null && saturdayEndTime != null) {
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
                        .withCaption("Выберите параметры")
                        .withDescription("Необходимо выбрать кабинет и/или преподавателя")
                        .show();
            }
        } else {
            notifications.create(Notifications.NotificationType.ERROR)
                    .withCaption("Заполните даты")
                    .withDescription("Необходимо заполнить дату начала и окончания по расписанию")
                    .show();
        }
    }
    @Subscribe("sundayEndTime")
    public void onSundayEndTimeValueChange(HasValue.ValueChangeEvent<LocalTime> event) {
        if (courseStartDate != null && courseEndDate != null && sundayStartTime != null && sundayEndTime != null) {
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
                        .withCaption("Выберите параметры")
                        .withDescription("Необходимо выбрать кабинет и/или преподавателя")
                        .show();
            }
        } else {
            notifications.create(Notifications.NotificationType.ERROR)
                    .withCaption("Заполните даты")
                    .withDescription("Необходимо заполнить дату начала и окончания по расписанию")
                    .show();
        }
    }

    @Subscribe("createSchedule")
    public void onCreateSchedule(Action.ActionPerformedEvent event) {
        if (isFreeCost.isChecked()) {
            String coursesName = courseNameField.getValue();
            courseNameField.setValue(coursesName+ ":" +"БЕСПЛАТНЫЙ");
        } else {
            String coursesName = courseNameField.getValue();
            String courseStartEndDate = courseStartDate.getValue() + ":" + courseEndDate.getValue();
            courseNameField.setValue(coursesName + ":" + courseStartEndDate);
        }
        if (courseEndDate.getValue() == null || courseStartDate.getValue() == null) {
            notifications.create(Notifications.NotificationType.ERROR)
                    .withCaption("Заполните даты")
                    .withDescription("Необходимо заполнить дату начала и окончания по расписанию")
                    .show();
        } else {
            CourseType courseType = Objects.requireNonNull(courseGroupField.getValue()).getGroupDirection().getDirectionType();
            BigDecimal courseCost = getCourseCost();
            BigDecimal sngCourseCount = BigDecimal.ONE;
            if (courseMode.getValue() == CourseMode.SINGLE) {
                CreateCourses(courseNameField.getValue(), CourseStatus.NEW, courseGroupField.getValue(), courseCost, sngCourseCount,
                        courseStartDate.getValue(), courseStartDate.getValue(), singleStartTime.getValue(), singleEndTime.getValue(),
                        courseCabinetField.getValue(), courseTeacherField.getValue(), courseManagerField.getValue(), courseBranchField.getValue(), courseType);
            }
            if (courseMode.getValue() == CourseMode.REPEAT) {
                LocalDate startDate = courseStartDate.getValue();
                LocalDate endDate = courseEndDate.getValue();
                List<LocalDate> lessonDates = new ArrayList<>();
                while (Objects.requireNonNull(startDate).isBefore(Objects.requireNonNull(endDate)) || Objects.requireNonNull(startDate).equals(Objects.requireNonNull(endDate))) {
                    if (Boolean.TRUE.equals(monday.getValue()) && mondayStartTime != null && mondayEndTime != null) {
                        if (startDate.getDayOfWeek().equals(DayOfWeek.MONDAY)) {
                            if (Objects.requireNonNull(getBusinessCalendarByCurrentYear()).isBusinessDay(startDate)) {
                                lessonDates.add(startDate);
                            } /*else {
                                LocalDate nextWorkingDay = getNextWorkingDay(startDate);
                                lessonDates.add(nextWorkingDay);
                                if (nextWorkingDay.getDayOfWeek() == DayOfWeek.TUESDAY) {
                                    tuesdayStartTime.setValue(mondayStartTime.getValue());
                                    tuesdayEndTime.setValue(mondayEndTime.getValue());
                                }
                                if (nextWorkingDay.getDayOfWeek() == DayOfWeek.WEDNESDAY) {
                                    wednesdayStartTime.setValue(mondayStartTime.getValue());
                                    wednesdayEndTime.setValue(mondayEndTime.getValue());
                                }
                                if (nextWorkingDay.getDayOfWeek() == DayOfWeek.THURSDAY) {
                                    thursdayStartTime.setValue(mondayStartTime.getValue());
                                    thursdayEndTime.setValue(mondayEndTime.getValue());
                                }
                                if (nextWorkingDay.getDayOfWeek() == DayOfWeek.FRIDAY) {
                                    fridayStartTime.setValue(mondayStartTime.getValue());
                                    fridayEndTime.setValue(mondayEndTime.getValue());
                                }
                                if (nextWorkingDay.getDayOfWeek() == DayOfWeek.SATURDAY) {
                                    saturdayStartTime.setValue(mondayStartTime.getValue());
                                    saturdayEndTime.setValue(mondayEndTime.getValue());
                                }
                                if (nextWorkingDay.getDayOfWeek() == DayOfWeek.SUNDAY) {
                                    sundayStartTime.setValue(mondayStartTime.getValue());
                                    sundayEndTime.setValue(mondayEndTime.getValue());
                                }
                            }*/
                        }
                    }
                    if (Boolean.TRUE.equals(tuesday.getValue()) && tuesdayStartTime != null && tuesdayEndTime != null) {
                        if (startDate.getDayOfWeek().equals(DayOfWeek.TUESDAY)) {
                            if (Objects.requireNonNull(getBusinessCalendarByCurrentYear()).isBusinessDay(startDate)) {
                                lessonDates.add(startDate);
                            } /*else {
                                LocalDate nextWorkingDay = getNextWorkingDay(startDate);
                                lessonDates.add(nextWorkingDay);
                                if (nextWorkingDay.getDayOfWeek() == DayOfWeek.MONDAY) {
                                    mondayStartTime.setValue(tuesdayStartTime.getValue());
                                    mondayEndTime.setValue(tuesdayEndTime.getValue());
                                }
                                if (nextWorkingDay.getDayOfWeek() == DayOfWeek.WEDNESDAY) {
                                    wednesdayStartTime.setValue(tuesdayStartTime.getValue());
                                    wednesdayEndTime.setValue(tuesdayEndTime.getValue());
                                }
                                if (nextWorkingDay.getDayOfWeek() == DayOfWeek.THURSDAY) {
                                    thursdayStartTime.setValue(tuesdayStartTime.getValue());
                                    thursdayEndTime.setValue(tuesdayEndTime.getValue());
                                }
                                if (nextWorkingDay.getDayOfWeek() == DayOfWeek.FRIDAY) {
                                    fridayStartTime.setValue(tuesdayStartTime.getValue());
                                    fridayEndTime.setValue(tuesdayEndTime.getValue());
                                }
                                if (nextWorkingDay.getDayOfWeek() == DayOfWeek.SATURDAY) {
                                    saturdayStartTime.setValue(tuesdayStartTime.getValue());
                                    saturdayEndTime.setValue(tuesdayEndTime.getValue());
                                }
                                if (nextWorkingDay.getDayOfWeek() == DayOfWeek.SUNDAY) {
                                    sundayStartTime.setValue(tuesdayStartTime.getValue());
                                    sundayEndTime.setValue(tuesdayEndTime.getValue());
                                }
                            }*/
                        }
                    }
                    if (Boolean.TRUE.equals(wednesday.getValue()) && wednesdayStartTime != null && wednesdayEndTime != null) {
                        if (startDate.getDayOfWeek().equals(DayOfWeek.WEDNESDAY)) {
                            if (Objects.requireNonNull(getBusinessCalendarByCurrentYear()).isBusinessDay(startDate)) {
                                lessonDates.add(startDate);
                            } /*else {
                                LocalDate nextWorkingDay = getNextWorkingDay(startDate);
                                lessonDates.add(nextWorkingDay);
                                if (nextWorkingDay.getDayOfWeek() == DayOfWeek.MONDAY) {
                                    mondayStartTime.setValue(wednesdayStartTime.getValue());
                                    mondayEndTime.setValue(wednesdayEndTime.getValue());
                                }
                                if (nextWorkingDay.getDayOfWeek() == DayOfWeek.TUESDAY) {
                                    tuesdayStartTime.setValue(wednesdayStartTime.getValue());
                                    tuesdayEndTime.setValue(wednesdayEndTime.getValue());
                                }
                                if (nextWorkingDay.getDayOfWeek() == DayOfWeek.THURSDAY) {
                                    thursdayStartTime.setValue(wednesdayStartTime.getValue());
                                    thursdayEndTime.setValue(wednesdayEndTime.getValue());
                                }
                                if (nextWorkingDay.getDayOfWeek() == DayOfWeek.FRIDAY) {
                                    fridayStartTime.setValue(wednesdayStartTime.getValue());
                                    fridayEndTime.setValue(wednesdayEndTime.getValue());
                                }
                                if (nextWorkingDay.getDayOfWeek() == DayOfWeek.SATURDAY) {
                                    saturdayStartTime.setValue(wednesdayStartTime.getValue());
                                    saturdayEndTime.setValue(wednesdayEndTime.getValue());
                                }
                                if (nextWorkingDay.getDayOfWeek() == DayOfWeek.SUNDAY) {
                                    sundayStartTime.setValue(wednesdayStartTime.getValue());
                                    sundayEndTime.setValue(wednesdayEndTime.getValue());
                                }
                            }*/
                        }
                    }
                    if (Boolean.TRUE.equals(thursday.getValue()) && thursdayStartTime != null && thursdayEndTime != null) {
                        if (startDate.getDayOfWeek().equals(DayOfWeek.THURSDAY)) {
                            if (Objects.requireNonNull(getBusinessCalendarByCurrentYear()).isBusinessDay(startDate)) {
                                lessonDates.add(startDate);
                            } /*else {
                                LocalDate nextWorkingDay = getNextWorkingDay(startDate);
                                lessonDates.add(nextWorkingDay);
                                if (nextWorkingDay.getDayOfWeek() == DayOfWeek.MONDAY) {
                                    mondayStartTime.setValue(thursdayStartTime.getValue());
                                    mondayEndTime.setValue(thursdayEndTime.getValue());
                                }
                                if (nextWorkingDay.getDayOfWeek() == DayOfWeek.TUESDAY) {
                                    tuesdayStartTime.setValue(thursdayStartTime.getValue());
                                    tuesdayEndTime.setValue(thursdayEndTime.getValue());
                                }
                                if (nextWorkingDay.getDayOfWeek() == DayOfWeek.WEDNESDAY) {
                                    wednesdayStartTime.setValue(thursdayStartTime.getValue());
                                    wednesdayEndTime.setValue(thursdayEndTime.getValue());
                                }
                                if (nextWorkingDay.getDayOfWeek() == DayOfWeek.FRIDAY) {
                                    fridayStartTime.setValue(thursdayStartTime.getValue());
                                    fridayEndTime.setValue(thursdayEndTime.getValue());
                                }
                                if (nextWorkingDay.getDayOfWeek() == DayOfWeek.SATURDAY) {
                                    saturdayStartTime.setValue(thursdayStartTime.getValue());
                                    saturdayEndTime.setValue(thursdayEndTime.getValue());
                                }
                                if (nextWorkingDay.getDayOfWeek() == DayOfWeek.SUNDAY) {
                                    sundayStartTime.setValue(thursdayStartTime.getValue());
                                    sundayEndTime.setValue(thursdayEndTime.getValue());
                                }
                            }*/
                        }
                    }
                    if (Boolean.TRUE.equals(friday.getValue()) && fridayStartTime != null && fridayEndTime != null) {
                        if (startDate.getDayOfWeek().equals(DayOfWeek.FRIDAY)) {
                            if (Objects.requireNonNull(getBusinessCalendarByCurrentYear()).isBusinessDay(startDate)) {
                                lessonDates.add(startDate);
                            } /*else {
                                LocalDate nextWorkingDay = getNextWorkingDay(startDate);
                                lessonDates.add(nextWorkingDay);
                                if (nextWorkingDay.getDayOfWeek() == DayOfWeek.MONDAY) {
                                    mondayStartTime.setValue(fridayStartTime.getValue());
                                    mondayEndTime.setValue(fridayEndTime.getValue());
                                }
                                if (nextWorkingDay.getDayOfWeek() == DayOfWeek.TUESDAY) {
                                    tuesdayStartTime.setValue(fridayStartTime.getValue());
                                    tuesdayEndTime.setValue(fridayEndTime.getValue());
                                }
                                if (nextWorkingDay.getDayOfWeek() == DayOfWeek.WEDNESDAY) {
                                    wednesdayStartTime.setValue(fridayStartTime.getValue());
                                    wednesdayEndTime.setValue(fridayEndTime.getValue());
                                }
                                if (nextWorkingDay.getDayOfWeek() == DayOfWeek.THURSDAY) {
                                    thursdayStartTime.setValue(fridayStartTime.getValue());
                                    thursdayEndTime.setValue(fridayEndTime.getValue());
                                }
                                if (nextWorkingDay.getDayOfWeek() == DayOfWeek.SATURDAY) {
                                    saturdayStartTime.setValue(fridayStartTime.getValue());
                                    saturdayEndTime.setValue(fridayEndTime.getValue());
                                }
                                if (nextWorkingDay.getDayOfWeek() == DayOfWeek.SUNDAY) {
                                    sundayStartTime.setValue(fridayStartTime.getValue());
                                    sundayEndTime.setValue(fridayEndTime.getValue());
                                }
                            }*/
                        }
                    }
                    if (Boolean.TRUE.equals(saturday.getValue()) && saturdayStartTime != null && saturdayEndTime != null) {
                        if (startDate.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
                            if (Objects.requireNonNull(getBusinessCalendarByCurrentYear()).isBusinessDay(startDate)) {
                                lessonDates.add(startDate);
                            } /*else {
                                LocalDate nextWorkingDay = getNextWorkingDay(startDate);
                                lessonDates.add(nextWorkingDay);
                                if (nextWorkingDay.getDayOfWeek() == DayOfWeek.MONDAY) {
                                    mondayStartTime.setValue(saturdayStartTime.getValue());
                                    mondayEndTime.setValue(saturdayEndTime.getValue());
                                }
                                if (nextWorkingDay.getDayOfWeek() == DayOfWeek.TUESDAY) {
                                    tuesdayStartTime.setValue(saturdayStartTime.getValue());
                                    tuesdayEndTime.setValue(saturdayEndTime.getValue());
                                }
                                if (nextWorkingDay.getDayOfWeek() == DayOfWeek.WEDNESDAY) {
                                    wednesdayStartTime.setValue(saturdayStartTime.getValue());
                                    wednesdayEndTime.setValue(saturdayEndTime.getValue());
                                }
                                if (nextWorkingDay.getDayOfWeek() == DayOfWeek.THURSDAY) {
                                    thursdayStartTime.setValue(saturdayStartTime.getValue());
                                    thursdayEndTime.setValue(saturdayEndTime.getValue());
                                }
                                if (nextWorkingDay.getDayOfWeek() == DayOfWeek.FRIDAY) {
                                    fridayStartTime.setValue(saturdayStartTime.getValue());
                                    fridayEndTime.setValue(saturdayEndTime.getValue());
                                }
                                if (nextWorkingDay.getDayOfWeek() == DayOfWeek.SUNDAY) {
                                    sundayStartTime.setValue(saturdayStartTime.getValue());
                                    sundayEndTime.setValue(saturdayEndTime.getValue());
                                }
                            }*/
                        }
                    }
                    if (Boolean.TRUE.equals(sunday.getValue()) && sundayStartTime != null && sundayEndTime != null) {
                        if (startDate.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
                            if (Objects.requireNonNull(getBusinessCalendarByCurrentYear()).isBusinessDay(startDate)) {
                                lessonDates.add(startDate);
                            } /*else {
                                LocalDate nextWorkingDay = getNextWorkingDay(startDate);
                                lessonDates.add(nextWorkingDay);
                                if (nextWorkingDay.getDayOfWeek() == DayOfWeek.MONDAY) {
                                    mondayStartTime.setValue(sundayStartTime.getValue());
                                    mondayEndTime.setValue(sundayEndTime.getValue());
                                }
                                if (nextWorkingDay.getDayOfWeek() == DayOfWeek.TUESDAY) {
                                    tuesdayStartTime.setValue(sundayStartTime.getValue());
                                    tuesdayEndTime.setValue(sundayEndTime.getValue());
                                }
                                if (nextWorkingDay.getDayOfWeek() == DayOfWeek.WEDNESDAY) {
                                    wednesdayStartTime.setValue(sundayStartTime.getValue());
                                    wednesdayEndTime.setValue(sundayEndTime.getValue());
                                }
                                if (nextWorkingDay.getDayOfWeek() == DayOfWeek.THURSDAY) {
                                    thursdayStartTime.setValue(sundayStartTime.getValue());
                                    thursdayEndTime.setValue(sundayEndTime.getValue());
                                }
                                if (nextWorkingDay.getDayOfWeek() == DayOfWeek.FRIDAY) {
                                    fridayStartTime.setValue(sundayStartTime.getValue());
                                    fridayEndTime.setValue(sundayEndTime.getValue());
                                }
                                if (nextWorkingDay.getDayOfWeek() == DayOfWeek.WEDNESDAY) {
                                    wednesdayStartTime.setValue(sundayStartTime.getValue());
                                    wednesdayEndTime.setValue(sundayEndTime.getValue());
                                }
                            }*/
                        }
                    }
                    startDate = startDate.plusDays(1);
                }
                BigDecimal lessonCount = BigDecimal.valueOf(lessonDates.size());
                for (LocalDate lessonDate : lessonDates) {
                    if (lessonDate.getDayOfWeek() == DayOfWeek.MONDAY) {
                        CreateCourses(courseNameField.getValue(), CourseStatus.NEW, courseGroupField.getValue(), courseCost, lessonCount,
                                lessonDate, lessonDate, mondayStartTime.getValue(), mondayEndTime.getValue(),
                                courseCabinetField.getValue(), courseTeacherField.getValue(), courseManagerField.getValue(), courseBranchField.getValue(), courseType);
                    }
                    if (lessonDate.getDayOfWeek() == DayOfWeek.TUESDAY) {
                        CreateCourses(courseNameField.getValue(), CourseStatus.NEW, courseGroupField.getValue(), courseCost, lessonCount,
                                lessonDate, lessonDate, tuesdayStartTime.getValue(), tuesdayEndTime.getValue(),
                                courseCabinetField.getValue(), courseTeacherField.getValue(), courseManagerField.getValue(), courseBranchField.getValue(), courseType);
                    }
                    if (lessonDate.getDayOfWeek() == DayOfWeek.WEDNESDAY) {
                        CreateCourses(courseNameField.getValue(), CourseStatus.NEW, courseGroupField.getValue(), courseCost, lessonCount,
                                lessonDate, lessonDate, wednesdayStartTime.getValue(), wednesdayEndTime.getValue(),
                                courseCabinetField.getValue(), courseTeacherField.getValue(), courseManagerField.getValue(), courseBranchField.getValue(), courseType);
                    }
                    if (lessonDate.getDayOfWeek() == DayOfWeek.THURSDAY) {
                        CreateCourses(courseNameField.getValue(), CourseStatus.NEW, courseGroupField.getValue(), courseCost, lessonCount,
                                lessonDate, lessonDate, thursdayStartTime.getValue(), thursdayEndTime.getValue(),
                                courseCabinetField.getValue(), courseTeacherField.getValue(), courseManagerField.getValue(), courseBranchField.getValue(), courseType);
                    }
                    if (lessonDate.getDayOfWeek() == DayOfWeek.FRIDAY) {
                        CreateCourses(courseNameField.getValue(), CourseStatus.NEW, courseGroupField.getValue(), courseCost, lessonCount,
                                lessonDate, lessonDate, fridayStartTime.getValue(), fridayEndTime.getValue(),
                                courseCabinetField.getValue(), courseTeacherField.getValue(), courseManagerField.getValue(), courseBranchField.getValue(), courseType);
                    }
                    if (lessonDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
                        CreateCourses(courseNameField.getValue(), CourseStatus.NEW, courseGroupField.getValue(), courseCost, lessonCount,
                                lessonDate, lessonDate, saturdayStartTime.getValue(), saturdayEndTime.getValue(),
                                courseCabinetField.getValue(), courseTeacherField.getValue(), courseManagerField.getValue(), courseBranchField.getValue(), courseType);
                    }
                    if (lessonDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                        CreateCourses(courseNameField.getValue(), CourseStatus.NEW, courseGroupField.getValue(), courseCost, lessonCount,
                                lessonDate, lessonDate, sundayStartTime.getValue(), sundayEndTime.getValue(),
                                courseCabinetField.getValue(), courseTeacherField.getValue(), courseManagerField.getValue(), courseBranchField.getValue(), courseType);
                    }
                }
            }
            close(StandardOutcome.DISCARD);
        }
    }
}