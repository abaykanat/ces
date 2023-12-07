package com.company.ces_productive.screen.expenses;

import com.company.ces_productive.entity.Branches;
import com.company.ces_productive.entity.User;
import io.jmix.core.DataManager;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.screen.*;
import com.company.ces_productive.entity.Expenses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;

@UiController("CES_Expenses.browse")
@UiDescriptor("expenses-browse.xml")
@LookupComponent("expensesesTable")
public class ExpensesBrowse extends StandardLookup<Expenses> {
    @Autowired
    private CollectionLoader<Expenses> expensesesDl;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private CurrentAuthentication currentAuthentication;

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
                .query("select b from CES_Branches b where b = :currUserBranch")
                .parameter("currUserBranch", ((User) currentAuthentication.getUser()).getBranch())
                .list();
    }

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        if (getUserRoles().contains("manager")) {
            expensesesDl.setParameter("currBranch", getCurrBranch());
        } else {
            expensesesDl.setParameter("currBranch", getCurrBranches());
        }
        expensesesDl.load();
    }
}