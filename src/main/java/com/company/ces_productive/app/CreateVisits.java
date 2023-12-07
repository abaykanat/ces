package com.company.ces_productive.app;

import com.company.ces_productive.entity.*;
import com.company.ces_productive.entity.courses.Courses;
import io.jmix.core.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class CreateVisits {
    @Autowired
    private DataManager dataManager;

    public void addVisit(VisitStatus visitStatus, LocalDateTime visitStartDateTime, LocalDateTime visitEndDateTime,
                         BigDecimal visitAmount, Students visitStud, Courses visitCourse, User visitUser) {
        Visits visits = dataManager.create(Visits.class);
        visits.setVisitStatus(visitStatus);
        visits.setVisitStartDateTime(visitStartDateTime);
        visits.setVisitEndDateTime(visitEndDateTime);
        visits.setVisitAmount(visitAmount);
        visits.setVisitStudent(visitStud);
        visits.setVisitCourse(visitCourse);
        visits.setVisitUser(visitUser);
        dataManager.save(visits);
    }

}