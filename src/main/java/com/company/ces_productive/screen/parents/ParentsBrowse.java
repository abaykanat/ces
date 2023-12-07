package com.company.ces_productive.screen.parents;

import com.company.ces_productive.entity.User;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.screen.*;
import com.company.ces_productive.entity.Parents;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("CES_Parents.browse")
@UiDescriptor("parents-browse.xml")
@LookupComponent("parentsesTable")
public class ParentsBrowse extends StandardLookup<Parents> {
    @Autowired
    private CollectionLoader<Parents> parentsesDl;
    @Autowired
    private CurrentAuthentication currentAuthentication;

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        parentsesDl.setParameter("currBranch", ((User) currentAuthentication.getUser()).getBranch());
        parentsesDl.setParameter("currSecBranch", ((User) currentAuthentication.getUser()).getBranch());
        parentsesDl.load();
    }
}