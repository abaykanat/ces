package com.company.ces_productive.screen.cabinets;

import io.jmix.ui.screen.*;
import com.company.ces_productive.entity.Cabinets;

@UiController("CES_Cabinets.browse")
@UiDescriptor("cabinets-browse.xml")
@LookupComponent("table")
public class CabinetsBrowse extends MasterDetailScreen<Cabinets> {

}