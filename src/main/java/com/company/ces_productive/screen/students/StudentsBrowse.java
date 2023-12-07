package com.company.ces_productive.screen.students;

import com.company.ces_productive.entity.Branches;
import com.company.ces_productive.entity.User;
import io.jmix.core.DataManager;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.screen.*;
import com.company.ces_productive.entity.Students;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;


@UiController("CES_Students.browse")
@UiDescriptor("students-browse.xml")
@LookupComponent("studentsesTable")
public class StudentsBrowse extends StandardLookup<Students> {
    @Autowired
    private CollectionLoader<Students> studentsesDl;
    @Autowired
    private CurrentAuthentication currentAuthentication;
    @Autowired
    private DataManager dataManager;

    public List<String> getUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        return roles;
    }

    public List<Branches> getCurrBranches() {
        return dataManager.load(Branches.class)
                .query("select b from CES_Branches b")
                .list();
    }

    public List<Branches> getCurrBranch() {
        return dataManager.load(Branches.class)
                .query("select b from CES_Branches b where b = :currBranch")
                .parameter("currBranch", ((User) currentAuthentication.getUser()).getBranch())
                .list();
    }

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        if (getUserRoles().contains("manager")) {
            studentsesDl.setParameter("currUserBranch", getCurrBranch());
            studentsesDl.load();
        } else {
            studentsesDl.setParameter("currUserBranch", getCurrBranches());
            studentsesDl.load();
        }
    }
}