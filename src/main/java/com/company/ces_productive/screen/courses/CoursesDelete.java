package com.company.ces_productive.screen.courses;

import com.company.ces_productive.entity.*;
import io.jmix.core.DataManager;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.ui.action.Action;
import io.jmix.ui.component.*;
import io.jmix.ui.model.CollectionContainer;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.screen.*;
import com.company.ces_productive.entity.courses.Courses;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@UiController("CES_Courses.Delete")
@UiDescriptor("courses-delete.xml")
@EditedEntityContainer("coursesDc")
public class CoursesDelete extends StandardEditor<Courses> {
    @Autowired
    private CollectionLoader<User> usersDl;
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
    private EntityPicker<User> courseTeacherField;
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
    private VBoxLayout singleDay;
    @Autowired
    private VBoxLayout weekDays;

    @Subscribe
    public void onInit(InitEvent event) {
        usersDl.setParameter("currUserBranch", ((User) currentAuthentication.getUser()).getBranch());
        usersDl.load();
        cabinetsesDl.setParameter("currUserBranch", ((User) currentAuthentication.getUser()).getBranch());
        cabinetsesDl.load();
    }

    @Subscribe
    public void onInitEntity(InitEntityEvent<Courses> event) {
        event.getEntity().setCourseManager((User) currentAuthentication.getUser());
        event.getEntity().setCourseBranch(((User) currentAuthentication.getUser()).getBranch());
    }

    public List<Courses> selectedCourse() {
        return coursesesDc.getItems().stream()
                .filter(courses -> courses.getCourseName().equals(String.valueOf(Objects.requireNonNull(selectSchedule.getValue()).getCourseName())))
                .filter(courses -> courses.getCourseStatus().equals(CourseStatus.NEW))
                .toList();
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
                if (courseNames.add(courseName)) {
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
        courseCabinetField.setValue(course.getCourseCabinet());
        courseTeacherField.setValue(course.getCourseTeacher());
        courseCountField.setValue(BigDecimal.valueOf(selectedCourse().size()));
        courseCostField.setValue(course.getCourseCost());
        courseBranchField.setValue(course.getCourseBranch());
        courseManagerField.setValue(course.getCourseManager());
        if (selectedCourse().size() == 1) {
            courseMode.setValue(CourseMode.SINGLE);
            singleDay.setVisible(true);
            LocalDate startDate = course.getCourseStartDate().toLocalDate();
            LocalDate endDate = course.getCourseEndDate().toLocalDate();
            courseStartDate.setValue(startDate);
            courseEndDate.setValue(endDate);
            singleStartTime.setValue(course.getCourseStartTime());
            singleEndTime.setValue(course.getCourseEndTime());
        } else {
            courseMode.setValue(CourseMode.REPEAT);
            weekDays.setVisible(true);
            for (Courses selectCourse : selectedCourse()) {
                if (selectCourse.getCourseStartDate().getDayOfWeek() == DayOfWeek.MONDAY) {
                    monday.setValue(Boolean.TRUE);
                    mondayStartTime.setValue(selectCourse.getCourseStartTime());
                    mondayEndTime.setValue(selectCourse.getCourseEndTime());
                }
                if (selectCourse.getCourseStartDate().getDayOfWeek() == DayOfWeek.THURSDAY) {
                    thursday.setValue(Boolean.TRUE);
                    thursdayStartTime.setValue(selectCourse.getCourseStartTime());
                    thursdayEndTime.setValue(selectCourse.getCourseEndTime());
                }
                if (selectCourse.getCourseStartDate().getDayOfWeek() == DayOfWeek.WEDNESDAY) {
                    wednesday.setValue(Boolean.TRUE);
                    wednesdayStartTime.setValue(selectCourse.getCourseStartTime());
                    wednesdayEndTime.setValue(selectCourse.getCourseEndTime());
                }
                if (selectCourse.getCourseStartDate().getDayOfWeek() == DayOfWeek.TUESDAY) {
                    tuesday.setValue(Boolean.TRUE);
                    tuesdayStartTime.setValue(selectCourse.getCourseStartTime());
                    tuesdayEndTime.setValue(selectCourse.getCourseEndTime());
                }
                if (selectCourse.getCourseStartDate().getDayOfWeek() == DayOfWeek.FRIDAY) {
                    friday.setValue(Boolean.TRUE);
                    fridayStartTime.setValue(selectCourse.getCourseStartTime());
                    fridayEndTime.setValue(selectCourse.getCourseEndTime());
                }
                if (selectCourse.getCourseStartDate().getDayOfWeek() == DayOfWeek.SATURDAY) {
                    saturday.setValue(Boolean.TRUE);
                    saturdayStartTime.setValue(selectCourse.getCourseStartTime());
                    saturdayEndTime.setValue(selectCourse.getCourseEndTime());
                }
                if (selectCourse.getCourseStartDate().getDayOfWeek() == DayOfWeek.SUNDAY) {
                    sunday.setValue(Boolean.TRUE);
                    sundayStartTime.setValue(selectCourse.getCourseStartTime());
                    sundayEndTime.setValue(selectCourse.getCourseEndTime());
                }
            }
        }
    }

    @Subscribe("deleteSchedule")
    public void onDeleteSchedule(Action.ActionPerformedEvent event) {
        List<Courses> oldCourses = selectedCourse();
        dataManager.remove(oldCourses);
        close(StandardOutcome.DISCARD);
    }

}