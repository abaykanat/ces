package com.company.ces_productive.screen.discountreason;

import io.jmix.ui.screen.*;
import com.company.ces_productive.entity.DiscountReason;

@UiController("CES_DiscountReason.browse")
@UiDescriptor("discount-reason-browse.xml")
@LookupComponent("table")
public class DiscountReasonBrowse extends MasterDetailScreen<DiscountReason> {
}