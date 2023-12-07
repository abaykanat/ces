package com.company.ces_productive.screen.groups;

import io.jmix.core.security.CurrentAuthentication;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.screen.*;
import com.company.ces_productive.entity.Groups;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("CES_Groups.browseTeacher")
@UiDescriptor("groups-browseTeacher.xml")
@LookupComponent("groupsesTable")
public class GroupsBrowseTeacher extends StandardLookup<Groups> {
    @Autowired
    private CollectionLoader<Groups> groupsesDl;
    @Autowired
    private CurrentAuthentication currentAuthentication;

    @Subscribe
    public void onInit(final InitEvent event) {
        groupsesDl.setParameter("currUser", currentAuthentication.getUser());
        groupsesDl.load();
    }
}