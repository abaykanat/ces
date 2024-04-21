package com.company.ces_productive.screen.students;

import com.company.ces_productive.app.CreateOrder;
import com.company.ces_productive.app.DocNumbGenerate;
import com.company.ces_productive.app.StudNumbGenerate;
import com.company.ces_productive.entity.*;
import io.jmix.core.DataManager;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.ui.Dialogs;
import io.jmix.ui.Notifications;
import io.jmix.ui.ScreenBuilders;
import io.jmix.ui.action.Action;
import io.jmix.ui.action.DialogAction;
import io.jmix.ui.component.*;
import io.jmix.ui.component.data.TableItems;
import io.jmix.ui.model.CollectionContainer;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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
    @Autowired
    private Button groupsesTableSaveGroupBtn;
    @Autowired
    private Button groupsesTableAddBtn;

    @Autowired
    private Dialogs dialogs;
    @Autowired
    private Table<Groups> groupsesTable;
    @Autowired
    private CreateOrder createOrder;
    @Autowired
    private DocNumbGenerate docNumbGenerate;

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
        } else {
            notifications.create(Notifications.NotificationType.WARNING)
                    .withCaption("Внимание!")
                    .withDescription("У данного студента не указан родитель. Укажите родителя")
                    .show();
        }
    }

    @Install(to = "groupsesTable.add", subject = "afterCloseHandler")
    private void groupsesTableAddAfterCloseHandler(final AfterCloseEvent afterCloseEvent) {
        groupsesTableSaveGroupBtn.setVisible(true);
        groupsesTableAddBtn.setVisible(false);
    }

    @Subscribe("groupsesTable.saveGroup")
    public void onGroupsesTableSaveGroup(final Action.ActionPerformedEvent event) {
        dialogs.createOptionDialog()
                .withCaption("Подтверждение")
                .withMessage("Вы действительно хотите добавить "+ getEditedEntity().getStudLastName() +" "+ getEditedEntity().getStudFirstName() +" в новую группу?")
                .withActions(
                        new DialogAction(DialogAction.Type.YES)
                                .withHandler(e -> {
                                    TableItems<Groups> groupItems = groupsesTable.getItems();
                                    List<Groups> newGroups = new ArrayList<>(Objects.requireNonNull(groupItems).getItems());
                                    List<Groups> oldGroups = dataManager.load(Groups.class)
                                            .query("select g from CES_Groups g where :student member of g.groupStudents")
                                            .parameter("student", Collections.singletonList(getEditedEntity()))
                                            .list();
                                    if (!oldGroups.isEmpty()) {
                                        for (Groups group : newGroups) {
                                            String branchCode = getEditedEntity().getStudBranch().getBranchCode();
                                            String ordNum = docNumbGenerate.getNextNumber("ORD", branchCode);
                                            boolean isStudentInGroup = oldGroups.stream().anyMatch(g -> g.equals(group));
                                            if (!isStudentInGroup) {
                                                group.getGroupStudents().add(getEditedEntity());
                                                getEditedEntity().getStudGroups().add(group);
                                                createOrder.createNewOrder(ordNum, LocalDateTime.now(), getEditedEntity().getStudBranch(), group.getGroupCost(),
                                                        OrderPurpose.SUBSCRIPTION, OrderStatus.CREATED, getEditedEntity(), LocalDate.now(), group, null);
                                                createOrder.createNewPayParameter(getEditedEntity(), group, LocalDate.now(), null, BigDecimal.ZERO);
                                            }
                                        }
                                    } else {
                                        for (Groups group : newGroups) {
                                            String branchCode = getEditedEntity().getStudBranch().getBranchCode();
                                            String ordNum = docNumbGenerate.getNextNumber("ORD", branchCode);
                                            group.getGroupStudents().add(getEditedEntity());
                                            getEditedEntity().getStudGroups().add(group);
                                            createOrder.createNewOrder(ordNum, LocalDateTime.now(), getEditedEntity().getStudBranch(), group.getGroupCost(),
                                                    OrderPurpose.SUBSCRIPTION, OrderStatus.CREATED, getEditedEntity(), LocalDate.now(), group, null);
                                            createOrder.createNewPayParameter(getEditedEntity(), group, LocalDate.now(), null, BigDecimal.ZERO);
                                        }
                                    }
                                    getEditedEntity().setStudStatus(StudentStatus.STUDY);
                                    dataManager.save(getEditedEntity());
                                    closeWithDiscard();
                                }),
                        new DialogAction(DialogAction.Type.NO)
                )
                .show();
    }
}