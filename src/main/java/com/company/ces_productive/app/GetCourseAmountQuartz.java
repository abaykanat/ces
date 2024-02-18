package com.company.ces_productive.app;

import com.company.ces_productive.entity.CourseStatus;
import com.company.ces_productive.entity.Groups;
import com.company.ces_productive.entity.Students;
import com.company.ces_productive.entity.courses.Courses;
import io.jmix.core.DataManager;
import io.jmix.core.SaveContext;
import io.jmix.core.security.Authenticated;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class GetCourseAmountQuartz implements Job {
    @Autowired
    private DataManager dataManager;

    private static final Logger log = LoggerFactory.getLogger(GetCourseAmountQuartz.class);

    @Authenticated
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            log.info("Starting GetCourseAmountQuartz job execution...");
            List<Students> students = dataManager.load(Students.class)
                    .query("select s from CES_Students s where s.studGroups is not empty")
                    .list();
            SaveContext saveContext = new SaveContext().setDiscardSaved(true);
            for (Students student : students) {
                List<Groups> groups = student.getStudGroups();
                if (groups.isEmpty()) {
                    student.setStudPeriodDesc("Отсутствует группа");
                }
                List<String> studentDescriptions = new ArrayList<>();
                boolean zeroCourseAmount = false;
                int groupCount = 0;
                Set<String> uniqueCourseNames = new HashSet<>();
                for (Groups group : groups) {
                    List<Courses> courses = group.getGroupCourse();
                    Map<String, List<Courses>> courseNames = courses.stream()
                            .filter(course -> course.getCourseCost().compareTo(BigDecimal.ZERO) > 0
                                    && course.getCourseStatus().equals(CourseStatus.NEW))
                            .collect(Collectors.groupingBy(Courses::getCourseName));
                    List<String> courseNamesLists = courseNames.keySet().stream().toList();

                    for (String courseNamesList : courseNamesLists) {
                        BigDecimal realCourseAmount = GetCourseRealAmount.getCourseRealAmount(student, group, courseNamesList).getAmount();
                        String realCourseReason = GetCourseRealAmount.getCourseRealAmount(student, group, courseNamesList).getReason();
                        if (realCourseAmount.compareTo(BigDecimal.ZERO) > 0) {
                            studentDescriptions.add("По группе " + group.getGroupName() + "| и расписанию " + courseNamesList + "| стоимость занятия = " + realCourseAmount + " тенге");
                            student.setStudPeriodDesc("Корректно");
                            uniqueCourseNames.add(courseNamesList);
                        } else {
                            studentDescriptions.add("По группе " + group.getGroupName() + "| и расписанию " + courseNamesList + "| не расчитана сумма занятия. " + realCourseReason);
                            student.setStudPeriodDesc(realCourseReason);
                            zeroCourseAmount = true;
                        }
                    }
                    groupCount++;
                }
                if (groupCount >= 2 && zeroCourseAmount) {
                    student.setStudPeriodDesc("По одной из групп студента имеется ошибка. Детальная информация в карточке студента");
                }
                if (uniqueCourseNames.size() >= 2) {
                    student.setStudPeriodDesc("По одной из групп студента имеется дублирующее расписание");
                }
                student.setStudDescription(String.join("\n", studentDescriptions));
                saveContext.saving(student);
            }
            dataManager.save(saveContext);
            log.info("GetCourseAmountQuartz job execution completed successfully.");
        } catch (Exception e) {
            log.error("Error occurred during GetCourseAmountQuartz job execution", e);
            throw new JobExecutionException(e);
        }
    }
}
