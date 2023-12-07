package com.company.ces_productive.screen.visits;

import io.jmix.ui.screen.*;
import com.company.ces_productive.entity.Visits;

@UiController("CES_Visits.browse")
@UiDescriptor("visits-browse.xml")
@LookupComponent("visitsesTable")
public class VisitsBrowse extends StandardLookup<Visits> {
}