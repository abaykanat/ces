package com.company.ces_productive.screen.branches;

import io.jmix.ui.screen.*;
import com.company.ces_productive.entity.Branches;

@UiController("CES_Branches.browse")
@UiDescriptor("branches-browse.xml")
@LookupComponent("table")
public class BranchesBrowse extends MasterDetailScreen<Branches> {
}