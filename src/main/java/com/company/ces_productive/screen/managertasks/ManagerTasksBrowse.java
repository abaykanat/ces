package com.company.ces_productive.screen.managertasks;

import io.jmix.ui.screen.*;
import com.company.ces_productive.entity.ManagerTasks;

@UiController("CES_ManagerTasks.browse")
@UiDescriptor("manager-tasks-browse.xml")
@LookupComponent("table")
public class ManagerTasksBrowse extends MasterDetailScreen<ManagerTasks> {
}