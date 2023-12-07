package com.company.ces_productive.screen.direction;

import com.company.ces_productive.entity.User;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.ui.component.GroupTable;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.screen.*;
import com.company.ces_productive.entity.Direction;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("CES_Direction.browse")
@UiDescriptor("direction-browse.xml")
@LookupComponent("directionsTable")
public class DirectionBrowse extends StandardLookup<Direction> {
    @Autowired
    private CollectionLoader<Direction> directionsDl;
    @Autowired
    private CurrentAuthentication currentAuthentication;
    @Autowired
    private GroupTable<Direction> directionsTable;

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        directionsDl.setParameter("currUserBranch", ((User) currentAuthentication.getUser()).getBranch());
        directionsDl.load();
    }

}