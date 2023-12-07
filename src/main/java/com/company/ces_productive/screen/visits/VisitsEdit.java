package com.company.ces_productive.screen.visits;

import io.jmix.ui.screen.*;
import com.company.ces_productive.entity.Visits;

@UiController("CES_Visits.edit")
@UiDescriptor("visits-edit.xml")
@EditedEntityContainer("visitsDc")
public class VisitsEdit extends StandardEditor<Visits> {
}