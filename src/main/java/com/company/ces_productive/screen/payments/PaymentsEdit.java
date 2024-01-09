package com.company.ces_productive.screen.payments;

import com.company.ces_productive.app.DocNumbGenerate;
import com.company.ces_productive.entity.*;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.ui.Dialogs;
import io.jmix.ui.action.DialogAction;
import io.jmix.ui.component.HasValue;
import io.jmix.ui.component.TextField;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@UiController("CES_Payments.edit")
@UiDescriptor("payments-edit.xml")
@EditedEntityContainer("paymentsDc")
public class PaymentsEdit extends StandardEditor<Payments> {
    @Autowired
    private CurrentAuthentication currentAuthentication;
    @Autowired
    private DocNumbGenerate docNumbGenerate;
    @Autowired
    private CollectionLoader<Orders> ordersesDl;
    @Autowired
    private Dialogs dialogs;
    @Autowired
    private TextField<BigDecimal> realAmountField;

    @Subscribe
    public void onInitEntity(InitEntityEvent<Payments> event) {
        ordersesDl.setParameter("currStudent", event.getEntity().getPayStudent());
        ordersesDl.setParameter("currStatus", OrderStatus.PAID);
        ordersesDl.load();

        event.getEntity().setPayUser((User) currentAuthentication.getUser());
        event.getEntity().setPayBranch(((User) currentAuthentication.getUser()).getBranch());
        String branchCode = getEditedEntity().getPayStudent().getStudBranch().getBranchCode();
        String payNum = docNumbGenerate.getNextNumber("PAY", branchCode);
        event.getEntity().setPayNumber(payNum);
        event.getEntity().setPayDateTime(LocalDate.now());
        event.getEntity().setPayPurpose(OrderPurpose.SUBSCRIPTION);
    }

    @Subscribe("payOrderField")
    public void onPayOrderFieldValueChange(HasValue.ValueChangeEvent<Orders> event) {
        if (Objects.requireNonNull(event.getValue()).getOrderStatus() == OrderStatus.PART_PAID) {
            realAmountField.setValue(event.getValue().getOrderPartAmount());
        } else {
            realAmountField.setValue(Objects.requireNonNull(event.getValue()).getOrderAmount());
        }
    }

    @Subscribe
    public void onBeforeCommitChanges(BeforeCommitChangesEvent event) {
        Students student = getEditedEntity().getPayStudent();
        dialogs.createOptionDialog()
                .withCaption("Подтверждение")
                .withMessage("Принимаем оплату по студенту " + student.getStudLastName() + " " + student.getStudFirstName() + "?")
                .withActions(
                        new DialogAction(DialogAction.Type.OK).withHandler(e -> {
                            event.resume();
                        }),
                        new DialogAction(DialogAction.Type.CANCEL)
                )
                .show();
        event.preventCommit();
    }
}