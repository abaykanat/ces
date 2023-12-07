package com.company.ces_productive.screen.parents;

import io.jmix.ui.screen.*;
import com.company.ces_productive.entity.Parents;

@UiController("CES_Parents.browseAll")
@UiDescriptor("parents-browseAll.xml")
@LookupComponent("parentsesTable")
public class ParentsBrowseAll extends StandardLookup<Parents> {
}