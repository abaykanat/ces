package com.company.ces_productive.screen.students;

import com.company.ces_productive.app.StudNumbGenerate;
import com.company.ces_productive.entity.*;
import io.jmix.core.DataManager;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.ui.Notifications;
import io.jmix.ui.ScreenBuilders;
import io.jmix.ui.action.Action;
import io.jmix.ui.component.*;
import io.jmix.ui.model.CollectionContainer;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@UiController("CES_Students.edit")
@UiDescriptor("students-edit.xml")
@EditedEntityContainer("studentsDc")
public class StudentsEdit extends StandardEditor<Students> {
    @Autowired
    private CurrentAuthentication currentAuthentication;
    @Autowired
    private CollectionLoader<Freeze> freezesDl;
    @Autowired
    private CollectionLoader<Stopped> stoppedsDl;
    @Autowired
    private StudNumbGenerate studNumbGenerate;
    @Autowired
    private CollectionLoader<DiscountReason> discountReasonsDl;
    @Autowired
    private TextField<String> studIDField;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private Button cancelFreezeBtn;
    @Autowired
    private CollectionContainer<Freeze> freezesDc;
    @Autowired
    private Notifications notifications;
    @Autowired
    private ScreenBuilders screenBuilders;
    @Autowired
    private Table<PaymentParam> paymentParamsTable;
    @Autowired
    private TextField<BigDecimal> studDiscountField;
    @Autowired
    private DateField<LocalDate> studOrderPeriodField;
    @Autowired
    private CollectionLoader<PaymentParam> paymentParamsDl;
    @Autowired
    private EntityComboBox<DiscountReason> studDiscountReason;

    @Subscribe
    public void onInitEntity(InitEntityEvent<Students> event) {
        event.getEntity().setStudOrderPeriod(LocalDate.now());
        event.getEntity().setStudStatus(StudentStatus.NEW);
        event.getEntity().setStudBeginDate(LocalDate.now());
        event.getEntity().setStudManager((User) currentAuthentication.getUser());
        event.getEntity().setStudBranch(((User) currentAuthentication.getUser()).getBranch());
    }

    @Subscribe("studDiscountReason")
    public void onStudDiscountReasonValueChange(HasValue.ValueChangeEvent<DiscountReason> event) {
        discountReasonsDl.load();
    }

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        String branchCode = ((User) currentAuthentication.getUser()).getBranch().getBranchCode();
        String studNum = studNumbGenerate.getNextNumber(branchCode);
        studIDField.setValue(studNum);
        Students students = getEditedEntity();
        freezesDl.setParameter("freezeStud", students);
        freezesDl.load();
        stoppedsDl.setParameter("stoppedStud", students);
        stoppedsDl.load();
        paymentParamsDl.setParameter("currStudent", students);
        paymentParamsDl.load();
        List<Freeze> freezes = freezesDc.getItems().stream()
                .filter(freeze -> freeze.getFreezeEndDate().isAfter(LocalDate.now()) || freeze.getFreezeEndDate().isEqual(LocalDate.now()))
                .toList();
        if (!freezes.isEmpty()) {
            cancelFreezeBtn.setEnabled(true);
        }
    }

    @Subscribe
    public void onAfterShow(final AfterShowEvent event) {
        if (Objects.requireNonNull(paymentParamsTable.getItems()).size() == 0) {
            studDiscountField.setVisible(true);
            studDiscountReason.setVisible(true);
            studOrderPeriodField.setVisible(true);
        }
    }

    @Subscribe("freezesTable.cancelFreeze")
    public void onFreezesTableCancelFreeze(Action.ActionPerformedEvent event) {
        List<Freeze> freezes = freezesDc.getItems().stream()
                .filter(freeze -> freeze.getFreezeEndDate().isAfter(LocalDate.now()))
                .toList();
        for (Freeze freeze : freezes) {
            if (freeze.getFreezeEndDate().isAfter(LocalDate.now()) || freeze.getFreezeEndDate().isEqual(LocalDate.now())) {
                freeze.setFreezeEndDate(LocalDate.now());
                dataManager.save(freeze);
            }
        }
        Students student = getEditedEntity();
        if (student.getStudStatus() == StudentStatus.FREEZE) {
            student.setStudStatus(StudentStatus.STUDY);
            dataManager.save(student);
        }
        cancelFreezeBtn.setEnabled(false);
    }

    @Subscribe("showParent")
    public void onShowParentClick(final Button.ClickEvent event) {
        Parents parent = getEditedEntity().getStudParent();
        if (parent != null) {
            screenBuilders.editor(Parents.class, this)
                    .editEntity(parent)
                    .build()
                    .show();
        }
        else {
            notifications.create(Notifications.NotificationType.WARNING)
                    .withCaption("Внимание!")
                    .withDescription("У данного студента не указан родитель. Укажите родителя")
                    .show();
        }
    }
}