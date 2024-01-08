package com.company.ces_productive.app;

import com.company.ces_productive.entity.courses.Courses;
import io.jmix.businesscalendar.model.BusinessCalendar;
import io.jmix.businesscalendar.repository.BusinessCalendarRepository;
import io.jmix.core.DataManager;
import io.jmix.core.security.Authenticated;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Year;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CourseDeleteQuartz implements Job {
    @Autowired
    private DataManager dataManager;
    @Autowired
    private BusinessCalendarRepository businessCalendarRepository;

    private static final Logger log = LoggerFactory.getLogger(GetCourseAmountQuartz.class);

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

    @Authenticated
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            log.info("Starting GetCourseAmountQuartz job execution...");
            List<Courses> courses = dataManager.load(Courses.class)
                    .query("select c from CES_Courses c")
                    .list();
            List<Courses> deleteCourses = new ArrayList<>();
            for (Courses course : courses) {
                if (!(getBusinessCalendarByCurrentYear().isBusinessDay(course.getCourseStartDate().toLocalDate()))) {
                    deleteCourses.add(course);
                }
            }
            dataManager.remove(deleteCourses);
            log.info("GetCourseAmountQuartz job execution completed successfully.");
        } catch (Exception e) {
            log.error("Error occurred during GetCourseAmountQuartz job execution", e);
            throw new JobExecutionException(e);
        }
    }
}