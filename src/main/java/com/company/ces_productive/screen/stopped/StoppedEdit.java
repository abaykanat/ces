package com.company.ces_productive.screen.stopped;

import com.company.ces_productive.app.CreateOrder;
import com.company.ces_productive.app.DocNumbGenerate;
import com.company.ces_productive.entity.*;
import com.company.ces_productive.entity.courses.Courses;
import io.jmix.core.DataManager;
import io.jmix.ui.Dialogs;
import io.jmix.ui.action.Action;
import io.jmix.ui.action.DialogAction;
import io.jmix.ui.component.DateField;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@UiController("CES_Stopped.edit")
@UiDescriptor("stopped-edit.xml")
@EditedEntityContainer("stoppedDc")
public class StoppedEdit extends StandardEditor<Stopped> {
    @Autowired
    private DataManager dataManager;
    @Autowired
    private DocNumbGenerate docNumbGenerate;
    @Autowired
    private CreateOrder createOrder;
    @Autowired
    private Dialogs dialogs;
    @Autowired
    private DateField<LocalDate> stoppedDateField;

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        stoppedDateField.setValue(LocalDate.now());
    }

    @Subscribe
    public void onAfterShow(final AfterShowEvent event) {
        Students student = getEditedEntity().getStoppedStudent();
        if (student.getStudActualAmount().compareTo(BigDecimal.valueOf(500)) < 0 || student.getStudActualAmount().compareTo(BigDecimal.valueOf(-500)) > 0 ) {
            student.setStudActualAmount(BigDecimal.ZERO);
        }
    }

    @Subscribe("windowCommitAndClose")
    public void onWindowCommitAndClose(Action.ActionPerformedEvent event) {
        Students student = getEditedEntity().getStoppedStudent();
        BigDecimal negativeFinalAmount = student.getStudActualAmount();
        if (negativeFinalAmount.compareTo(BigDecimal.ZERO) > 0) {
            List<Orders> studOrders = student.getStudOrders();
            for (Orders studOrder : studOrders) {
                if (studOrder.getOrderStatus() == OrderStatus.CREATED) {
                    dataManager.remove(studOrder);
                }
            }
        }
        if (negativeFinalAmount.compareTo(BigDecimal.ZERO) < 0) {
            List<Orders> studOrders = student.getStudOrders();
            if (!student.getStudOrderPeriod().isBefore(LocalDate.now())) {
                List<Visits> studVisits = student.getStudVisits();
                for (Visits studVisit : studVisits){
                    if (studVisit.getVisitStatus() == VisitStatus.ABSENT && !studVisit.getVisitEndDateTime().isBefore(student.getStudOrderPeriod().atStartOfDay())) {
                        student.setStudActualAmount(BigDecimal.ZERO);
                        dataManager.save(student);
                    }
                }
            }
            for (Orders studOrder : studOrders) {
                if (studOrder.getOrderStatus() == OrderStatus.CREATED) {
                    dataManager.remove(studOrder);
                }
            }
            String branchCode = student.getStudBranch().getBranchCode();
            BigDecimal finalAmount = negativeFinalAmount.multiply(BigDecimal.valueOf(-1));
            String ordNum = docNumbGenerate.getNextNumber("ORD", branchCode);
            createOrder.createNewOrder(ordNum, LocalDateTime.now(), student.getStudBranch(), finalAmount,
                    OrderPurpose.SUBSCRIPTION, OrderStatus.CREATED, student, LocalDate.now(), null, null);
        }
        if (negativeFinalAmount.compareTo(BigDecimal.ZERO) == 0) {
            List<Orders> studOrders = student.getStudOrders();
            for (Orders studOrder : studOrders) {
                if (studOrder.getOrderStatus() == OrderStatus.CREATED) {
                    dataManager.remove(studOrder);
                }
            }
        }
    }

    @Subscribe
    public void onBeforeCommitChanges(BeforeCommitChangesEvent event) {
        Students student = getEditedEntity().getStoppedStudent();
        dialogs.createOptionDialog()
                .withCaption("Confirmation")
                .withMessage("Do you want stopped:" + student.getStudLastName() + " " + student.getStudFirstName() + "?")
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