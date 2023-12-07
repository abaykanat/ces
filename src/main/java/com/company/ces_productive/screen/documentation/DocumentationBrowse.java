package com.company.ces_productive.screen.documentation;

import io.jmix.ui.screen.*;
import com.company.ces_productive.entity.Documentation;

@UiController("CES_Documentation.browse")
@UiDescriptor("documentation-browse.xml")
@LookupComponent("table")
public class DocumentationBrowse extends MasterDetailScreen<Documentation> {
}