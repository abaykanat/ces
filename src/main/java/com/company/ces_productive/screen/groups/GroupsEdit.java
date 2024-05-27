package com.company.ces_productive.screen.groups;

import com.company.ces_productive.app.CreateOrder;
import com.company.ces_productive.app.DocNumbGenerate;
import com.company.ces_productive.entity.*;
import com.company.ces_productive.listener.GroupsEventListener;
import io.jmix.core.DataManager;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.ui.Dialogs;
import io.jmix.ui.Notifications;
import io.jmix.ui.UiComponents;
import io.jmix.ui.action.Action;
import io.jmix.ui.action.DialogAction;
import io.jmix.ui.app.inputdialog.DialogActions;
import io.jmix.ui.app.inputdialog.DialogOutcome;
import io.jmix.ui.app.inputdialog.InputParameter;
import io.jmix.ui.component.*;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@UiController("CES_Groups.edit")
@UiDescriptor("groups-edit.xml")
@EditedEntityContainer("groupsDc")
public class GroupsEdit extends StandardEditor<Groups> {
    @Autowired
    private CollectionLoader<User> usersDl;
    @Autowired
    private CurrentAuthentication currentAuthentication;
    @Autowired
    private TextField<BigDecimal> groupCostField;
    @Autowired
    private TextField<BigDecimal> groupCountField;
    @Autowired
    private Table<Students> groupStudentsTable;
    @Autowired
    private Dialogs dialogs;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private Button groupStudentsTableTransferStudentsBtn;
    @Autowired
    private UiComponents uiComponents;
    @Autowired
    private Notifications notifications;
    @Autowired
    private MessageBundle messageBundle;
    @Autowired
    private GroupsEventListener groupsEventListener;
    @Autowired
    private DocNumbGenerate docNumbGenerate;
    @Autowired
    private CreateOrder createOrder;

    @Subscribe
    public void onInitEntity(InitEntityEvent<Groups> event) {
        usersDl.load();
        event.getEntity().setGroupBranch(((User) currentAuthentication.getUser()).getBranch());
        event.getEntity().setGroupStatus(GroupStatus.OPEN);
    }

    @Install(to = "groupTeacherField", subject = "searchExecutor")
    private List<User> SuggestionFieldSearchExecutor(String searchString, Map<String, Object> searchParams) {
        return dataManager.load(User.class)
                .query("e.lastName like ?1 order by e.lastName", "(?i)%"
                        + searchString + "%")
                .list();
    }

    @Subscribe("groupDirectionField")
    public void onGroupDirectionFieldValueChange(HasValue.ValueChangeEvent<Direction> event) {
        groupCostField.setValue(Objects.requireNonNull(event.getValue()).getDirectionMinCost());
        groupCountField.setValue(event.getValue().getDirectionCount());
    }

    @Subscribe("groupStudentsTable.exclude")
    public void onGroupStudentsTableExclude(final Action.ActionPerformedEvent event) {
        Students student = groupStudentsTable.getSingleSelected();
        if (student == null) {
            return;
        }
        if (student.getStudStatus() == StudentStatus.STUDY || student.getStudStatus() == StudentStatus.FREEZE) {
            dialogs.createOptionDialog()
                    .withCaption("Подтверждение")
                    .withMessage("Вы действительно хотели: " + student.getStudLastName() + " " + student.getStudFirstName() + " исключить из данной группы?")
                    .withActions(
                            new DialogAction(DialogAction.Type.YES, Action.Status.PRIMARY).withHandler(e -> {
                                List<Orders> studOrders = new ArrayList<>(student.getStudOrders());
                                List<PaymentParam> removePayParams = new ArrayList<>();
                                List<Groups> studGroups = new ArrayList<>(student.getStudGroups());
                                for (Orders studOrder : studOrders) {
                                    if (studOrder.getOrderStatus() == OrderStatus.CREATED && studOrder.getOrderGroup().equals(getEditedEntity())
                                            && !studOrder.getOrderNumber().equals("ORDDIF")) {
                                        List<PaymentParam> payParams = student.getStudPayParam();
                                        for (PaymentParam payParam : payParams) {
                                            if (payParam.getPayParamGroups().getId().equals(studOrder.getOrderGroup().getId())) {
                                                removePayParams.add(payParam);
                                            }
                                        }
                                        dataManager.remove(studOrder);
                                    }
                                }
                                if (studGroups.size() == 1) {
                                    BigDecimal negativeFinalAmount = student.getStudActualAmount();
                                    if (negativeFinalAmount.compareTo(BigDecimal.ZERO) < 0) {
                                        String branchCode = student.getStudBranch().getBranchCode();
                                        BigDecimal finalAmount = negativeFinalAmount.negate();
                                        String ordNum = docNumbGenerate.getNextNumber("ORDDIF", branchCode);
                                        createOrder.createNewOrder(ordNum, LocalDateTime.now(), student.getStudBranch(), finalAmount,
                                                OrderPurpose.SUBSCRIPTION, OrderStatus.CREATED, student, LocalDate.now(), null, null);
                                    }
                                    student.setStudStatus(StudentStatus.STOPPED);
                                    dataManager.save(student);
                                }
                                getEditedEntity().getGroupStudents().remove(student);
                                student.setStudPeriodDesc("Выведен из группы: " + LocalDate.now());
                                student.setStudDescription("Выведен из группы: " + getEditedEntity().getGroupName() + " " + LocalDateTime.now());
                                dataManager.save(getEditedEntity());
                                for (PaymentParam removePayParam : removePayParams) {
                                    dataManager.remove(removePayParam);
                                }
                                closeWithDiscard();
                            }),
                            new DialogAction(DialogAction.Type.NO).withHandler(e -> closeWithDiscard())
                    )
                    .show();
        } else {
            getEditedEntity().getGroupStudents().remove(student);
            dataManager.save(getEditedEntity());
            closeWithDiscard();
        }
    }

    @Subscribe("groupStudentsTable")
    public void onGroupStudentsTableSelection(final Table.SelectionEvent<Students> event) {
        if (!groupStudentsTable.getSelected().isEmpty()) {
            groupStudentsTableTransferStudentsBtn.setEnabled(true);
        }
    }

    private void  CreateNotification (String messageKey, String actionName) {
        message(messageKey, actionName);
    }
    private void message(String messageKey, String actionName){
        notifications.create(Notifications.NotificationType.ERROR)
                .withCaption("Ошибка выбора")
                .withDescription(messageBundle.formatMessage(messageKey, actionName))
                .show();
    }

    @Subscribe("groupStudentsTable.transferStudents")
    public void onGroupStudentsTableTransferStudents(final Action.ActionPerformedEvent event) {
        dialogs.createInputDialog(this)
                .withCaption("Перевод студент в другую группу")
                .withParameters(
                        InputParameter.parameter("students")
                                .withField(() -> {
                                    MultiSelectList<Students> field = uiComponents.create(MultiSelectList.NAME);
                                    List<Students> studList = new ArrayList<>(groupStudentsTable.getSelected());
                                    field.setOptionsList(studList);
                                    field.setEnabled(false);
                                    field.setDescription("Студенты");
                                    return field;
                                }),
                        InputParameter.parameter("group")
                                .withField(()-> {
                                    ComboBox<Groups> field = uiComponents.create(ComboBox.of(Groups.class));
                                    field.setOptionsList(dataManager.load(Groups.class)
                                            .query("select g from CES_Groups g where g.groupBranch = :branch " +
                                                    "and g.groupDirection = :direction and g.groupStatus = :status and g <> :group")
                                            .parameter("branch", ((User) currentAuthentication.getUser()).getBranch())
                                            .parameter("direction", getEditedEntity().getGroupDirection())
                                            .parameter("status", GroupStatus.OPEN)
                                            .parameter("group", getEditedEntity())
                                            .list());
                                    field.setCaption("Новая группа");
                                    field.setWidthFull();
                                    return field;
                                })
                )
                .withActions(DialogActions.OK_CANCEL)
                .withCloseListener(closeEvent -> {
                        if (closeEvent.closedWith(DialogOutcome.OK)) {
                            List<Students> selectStudents = new ArrayList<>(groupStudentsTable.getSelected());
                            Groups currGroup = getEditedEntity();
                            Groups selectGroup = closeEvent.getValue("group");
                            if (selectStudents.isEmpty() || selectGroup == null) {
                                CreateNotification("Не все данные предоставлены", "Data Missing");
                                return;
                            }
                            for (Students selectStudent : selectStudents) {
                                if (Objects.equals(currGroup.getGroupCost(), selectGroup.getGroupCost())) {
                                    List<PaymentParam> studPayParams = selectStudent.getStudPayParam();
                                    for (PaymentParam studPayParam : studPayParams) {
                                        Groups paramGroup = studPayParam.getPayParamGroups();
                                        if (paramGroup.getId().equals(currGroup.getId())) {
                                            studPayParam.setPayParamGroups(selectGroup);
                                            dataManager.save(studPayParam);
                                        }
                                        List<Orders> studOrders = selectStudent.getStudOrders();
                                        for (Orders studOrder : studOrders) {
                                            if (studOrder.getOrderStatus() == OrderStatus.CREATED && studOrder.getOrderGroup().equals(getEditedEntity())) {
                                                studOrder.setOrderGroup(selectGroup);
                                                BigDecimal selectDirAmount = selectGroup.getGroupDirection().getDirectionMinCost();
                                                BigDecimal studDiscount = studPayParam.getPayParamDiscontAmount();
                                                if (!(studDiscount.compareTo(BigDecimal.ZERO) == 0)) {
                                                    BigDecimal percentAmount = selectDirAmount.multiply(studDiscount).divide(BigDecimal.valueOf(100), RoundingMode.UP);
                                                    BigDecimal finalAmount = selectDirAmount.subtract(percentAmount);
                                                    studOrder.setOrderAmount(finalAmount);
                                                } else {
                                                    studOrder.setOrderAmount(selectDirAmount);
                                                }
                                                dataManager.save(studOrder);
                                            }
                                        }
                                    }
                                }
                            }
                            List<Students> currGroupStuds = currGroup.getGroupStudents();
                            currGroupStuds.size();
                            currGroupStuds.removeAll(selectStudents);
                            dataManager.save(currGroup);
                            groupsEventListener.setExecuteCode(false);
                            List<Students> selectGroupStuds = selectGroup.getGroupStudents();
                            selectGroupStuds.size();
                            selectGroupStuds.addAll(selectStudents);
                            dataManager.save(selectGroup);
                            groupsEventListener.setExecuteCode(true);
                        }
                })
                .show();
        closeWithDiscard();
    }
}