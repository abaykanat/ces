package com.company.ces_productive.screen.documentation;

import io.jmix.ui.screen.*;
import com.company.ces_productive.entity.Documentation;

@UiController("CES_Documentation.edit")
@UiDescriptor("documentation-edit.xml")
@EditedEntityContainer("documentationDc")
public class DocumentationEdit extends StandardEditor<Documentation> {
}