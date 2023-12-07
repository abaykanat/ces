package com.company.ces_productive.screen.students;

import com.company.ces_productive.entity.*;
import io.jmix.core.DataManager;
import io.jmix.ui.Notifications;
import io.jmix.ui.action.Action;
import io.jmix.ui.component.EntityComboBox;
import io.jmix.ui.component.EntityPicker;
import io.jmix.ui.component.HasValue;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;

@UiController("CES_StudOrderPeriod.edit")
@UiDescriptor("studOrderPeriod-edit.xml")
@EditedEntityContainer("studentsDc")

public class StudOrderPeriodEdit extends StandardEditor<Students> {
    @Autowired
    private Notifications notifications;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private EntityComboBox<Branches> studBranchField;
    @Autowired
    private EntityPicker<User> studManagerField;
    @Autowired
    private CollectionLoader<Freeze> freezesDl;
    @Autowired
    private CollectionLoader<Stopped> stoppedsDl;
    @Autowired
    private CollectionLoader<PaymentParam> paymentParamsDl;
    @Autowired
    private CollectionLoader<User> usersDl;

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        Students students = getEditedEntity();
        freezesDl.setParameter("freezeStud", students);
        freezesDl.load();
        stoppedsDl.setParameter("stoppedStud", students);
        stoppedsDl.load();
        paymentParamsDl.setParameter("currStudent", students);
        paymentParamsDl.load();
    }

    @Subscribe("studBranchField")
    public void onStudBranchFieldValueChange(final HasValue.ValueChangeEvent<Branches> event) {
        usersDl.setParameter("selectedBranch", studBranchField.getValue());
        usersDl.load();
    }

    @Subscribe("changeBranch")
    public void onChangeBranch(final Action.ActionPerformedEvent event) {
        Branches newBranch = studBranchField.getValue();
        User newUser = studManagerField.getValue();
        Students student = getEditedEntity();
        List<Groups> studGroups = student.getStudGroups();
        if (studGroups.isEmpty() || newUser == null){
            List<Orders> studOrders = student.getStudOrders();
            for (Orders studOrder : studOrders) {
                if (!studOrder.getOrderPayment().isEmpty()) {
                    studOrder.setOrderBranch(newBranch);
                    dataManager.save(studOrder);
                }
            }
            List<Payments> studPayments = student.getStudPayments();
            for (Payments studPayment : studPayments) {
                if (studPayment.getPayOrder() != null) {
                    studPayment.setPayBranch(newBranch);
                    dataManager.save(studPayment);
                }
            }
            student.setStudBranch(newBranch);
            student.setStudManager(newUser);
            dataManager.save(student);
            closeWithDiscard();
        } else {
            notifications.create(Notifications.NotificationType.ERROR)
                    .withCaption("Наличие группы или не выбран новый менеджер")
                    .withDescription("Перед переводом необходимо вывести студента из всех групп и назначить нового менеджера в новой группе")
                    .show();
        }
    }
}
