package com.company.ces_productive.screen.expensereason;

import io.jmix.ui.screen.*;
import com.company.ces_productive.entity.ExpenseReason;

@UiController("CES_ExpenseReason.browse")
@UiDescriptor("expense-reason-browse.xml")
@LookupComponent("table")
public class ExpenseReasonBrowse extends MasterDetailScreen<ExpenseReason> {
}