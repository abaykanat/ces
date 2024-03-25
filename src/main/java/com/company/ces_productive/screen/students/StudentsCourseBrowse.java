package com.company.ces_productive.screen.students;

import com.company.ces_productive.entity.*;
import com.company.ces_productive.entity.courses.Courses;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.ui.component.Button;
import io.jmix.ui.component.DateField;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

@UiController("CES_StudentsCourse.browse")
@UiDescriptor("studentsCourse-browse.xml")
public class StudentsCourseBrowse extends StandardLookup<Students> {
    @Autowired
    private CollectionLoader<Groups> groupCourseStudDl;
    @Autowired
    private CurrentAuthentication currentAuthentication;
    @Autowired
    private DateField<LocalDate> selectedDate;
    @Autowired
    private CollectionLoader<Courses> coursesDl;

    @Subscribe
    public void onInit(final InitEvent event) {
        groupCourseStudDl.setParameter("currUserBranch", ((User) currentAuthentication.getUser()).getBranch());
        groupCourseStudDl.setParameter("status", GroupStatus.OPEN);
        groupCourseStudDl.setParameter("currDate", LocalDate.now().plusDays(1));
        groupCourseStudDl.load();
        coursesDl.setParameter("currUserBranch", ((User) currentAuthentication.getUser()).getBranch());
        coursesDl.setParameter("currDate", LocalDate.now().plusDays(1));
        coursesDl.load();
        selectedDate.setValue(LocalDate.now());
    }

    @Install(to = "coursesTable", subject = "styleProvider")
    protected String coursesTableStyleProvider(Courses courses, String property) {
        if (property == null) {
            courses.getCourseStatus();
        } else if (property.equals("courseStatus")) {
            return switch (courses.getCourseStatus()) {
                case NEW -> "new";
                case NOT_DONE -> "not_done";
                case HELD -> "held";
            };
        }
        return null;
    }

    @Install(to = "coursesesTable", subject = "styleProvider")
    protected String coursesesTableStyleProvider(Courses courses, String property) {
        if (property == null) {
            courses.getCourseStatus();
        } else if (property.equals("courseStatus")) {
            return switch (courses.getCourseStatus()) {
                case NEW -> "new";
                case NOT_DONE -> "not_done";
                case HELD -> "held";
            };
        }
        return null;
    }

    @Subscribe("selectDateBtn")
    public void onSelectDateBtnClick(final Button.ClickEvent event) {
        groupCourseStudDl.setParameter("currUserBranch", ((User) currentAuthentication.getUser()).getBranch());
        groupCourseStudDl.setParameter("status", GroupStatus.OPEN);
        groupCourseStudDl.setParameter("currDate", selectedDate.getValue().plusDays(1));
        groupCourseStudDl.load();
        coursesDl.setParameter("currUserBranch", ((User) currentAuthentication.getUser()).getBranch());
        coursesDl.setParameter("currDate", selectedDate.getValue().plusDays(1));
        coursesDl.load();
    }
}