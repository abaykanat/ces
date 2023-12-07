package com.company.ces_productive.screen.direction;

import io.jmix.ui.screen.*;
import com.company.ces_productive.entity.Direction;

@UiController("CES_DirectionEdit.browse")
@UiDescriptor("directionEdit-browse.xml")
@LookupComponent("table")
public class DirectionEditBrowse extends MasterDetailScreen<Direction> {
}