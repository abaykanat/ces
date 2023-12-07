package com.company.ces_productive.screen.courses;

import com.company.ces_productive.entity.Cabinets;
import com.company.ces_productive.entity.User;
import io.jmix.core.DataManager;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.screen.*;
import com.company.ces_productive.entity.courses.Courses;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@UiController("CES_Courses.edit")
@UiDescriptor("courses-edit.xml")
@EditedEntityContainer("coursesDc")
public class CoursesEdit extends StandardEditor<Courses> {
    @Autowired
    private CollectionLoader<Cabinets> cabinetsesDl;
    @Autowired
    private CurrentAuthentication currentAuthentication;
    @Autowired
    private DataManager dataManager;

    @Subscribe
    public void onInit(InitEvent event) {
        cabinetsesDl.setParameter("currBranch", ((User) currentAuthentication.getUser()).getBranch());
        cabinetsesDl.load();
    }

    @Install(to = "courseTeacherField", subject = "searchExecutor")
    private List<User> SuggestionFieldSearchExecutor(String searchString, Map<String, Object> searchParams) {
        return dataManager.load(User.class)
                .query("e.lastName like ?1 order by e.lastName", "(?i)%"
                        + searchString + "%")
                .list();
    }
}