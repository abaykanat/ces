package com.company.ces_productive.screen.expenses;

import com.company.ces_productive.app.DocNumbGenerate;
import com.company.ces_productive.entity.Branches;
import com.company.ces_productive.entity.User;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.ui.component.DateField;
import io.jmix.ui.component.EntityPicker;
import io.jmix.ui.component.TextField;
import io.jmix.ui.screen.*;
import com.company.ces_productive.entity.Expenses;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

@UiController("CES_Expenses.edit")
@UiDescriptor("expenses-edit.xml")
@EditedEntityContainer("expensesDc")
public class ExpensesEdit extends StandardEditor<Expenses> {
    @Autowired
    private EntityPicker<Branches> expensesBranchField;
    @Autowired
    private EntityPicker<User> expensesUserField;
    @Autowired
    private DocNumbGenerate docNumbGenerate;
    @Autowired
    private CurrentAuthentication currentAuthentication;
    @Autowired
    private DateField<LocalDate> expensesDateField;
    @Autowired
    private TextField<String> expNumberField;


    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        expensesDateField.setValue(LocalDate.now());
        expensesBranchField.setValue(((User) currentAuthentication.getUser()).getBranch());
        expensesUserField.setValue((User) currentAuthentication.getUser());

        String branchCode = getEditedEntity().getExpensesUser().getBranch().getBranchCode();
        String expNum = docNumbGenerate.getNextNumber("EXP", branchCode);
        expNumberField.setValue(expNum);
    }

}