package com.company.ces_productive.screen.paymentparam;

import com.company.ces_productive.app.CreateOrder;
import com.company.ces_productive.app.DocNumbGenerate;
import com.company.ces_productive.app.GetCourseRealAmount;
import com.company.ces_productive.entity.*;
import com.company.ces_productive.entity.courses.Courses;
import io.jmix.core.DataManager;
import io.jmix.ui.Dialogs;
import io.jmix.ui.Notifications;
import io.jmix.ui.action.Action;
import io.jmix.ui.action.DialogAction;
import io.jmix.ui.component.*;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@UiController("CES_PaymentParam.edit")
@UiDescriptor("payment-param-edit.xml")
@EditedEntityContainer("paymentParamDc")
public class PaymentParamEdit extends StandardEditor<PaymentParam> {

    @Autowired
    private DateField<LocalDate> payParamPayDayField;
    @Autowired
    private CreateOrder createOrder;
    @Autowired
    private DocNumbGenerate docNumbGenerate;
    @Autowired
    private TextField<BigDecimal> payParamDiscontAmountField;
    @Autowired
    private EntityComboBox<DiscountReason> payParamDiscountReasonField;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private Dialogs dialogs;
    @Autowired
    private EntityPicker<Groups> payParamGroupsField;
    @Autowired
    private CheckBox payParamMethodField;
    @Autowired
    private Notifications notifications;

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        if (Boolean.FALSE.equals(payParamMethodField.getValue())) {
            String payParamDiscontAmountCaption = payParamDiscontAmountField.getCaption();
            payParamDiscontAmountField.setCaption(payParamDiscontAmountCaption+" %");
        }
    }

    @Subscribe("payParamDiscontAmountField")
    public void onPayParamDiscontAmountFieldValueChange(final HasValue.ValueChangeEvent<BigDecimal> event) {
        if (payParamDiscontAmountField.getValue() == null) {
            payParamDiscountReasonField.clear();
        }
    }


    @Subscribe("payParamDiscountReasonField")
    public void onPayParamDiscountReasonFieldValueChange(final HasValue.ValueChangeEvent<DiscountReason> event) {
        if (payParamDiscountReasonField.getValue() == null) {
            payParamDiscontAmountField.clear();
        }
    }

    @Subscribe("payParamMethodField")
    public void onPayParamMethodFieldValueChange(final HasValue.ValueChangeEvent<Boolean> event) {
        String payParamDiscontAmountCaption = payParamDiscontAmountField.getCaption();
        if (Boolean.FALSE.equals(payParamMethodField.getValue()) && payParamDiscontAmountCaption != null) {
            payParamDiscontAmountField.setCaption(payParamDiscontAmountCaption+" %");
            if (payParamDiscontAmountCaption.contains("тенге")) {
                payParamDiscontAmountCaption = payParamDiscontAmountCaption.replace("тенге", "%");
                payParamDiscontAmountField.setCaption(payParamDiscontAmountCaption);
            }
        } else if (Boolean.TRUE.equals(payParamMethodField.getValue()) && payParamDiscontAmountCaption != null) {
            payParamDiscontAmountField.setCaption(payParamDiscontAmountCaption+" тенге");
            if (payParamDiscontAmountCaption.contains("%")) {
                payParamDiscontAmountCaption = payParamDiscontAmountCaption.replace("%", "тенге");
                payParamDiscontAmountField.setCaption(payParamDiscontAmountCaption);
            }
        }
    }

    @Subscribe("newParamSave")
    public void onNewParamSave(final Action.ActionPerformedEvent event) {
        PaymentParam paymentParam = getEditedEntity();
        Students student = paymentParam.getPayParamStudent();
        LocalDate currPeriod = LocalDate.now();
        LocalDate newPeriod = payParamPayDayField.getValue();
        BigDecimal payParamDiscontAmountValue = payParamDiscontAmountField.getValue();
        DiscountReason payParamDiscountReasonValue = payParamDiscountReasonField.getValue();
        Boolean payParamMethod = payParamMethodField.getValue();
        if (Boolean.TRUE.equals(payParamMethod) && payParamDiscontAmountValue != null) {
            if (payParamDiscontAmountValue.compareTo(BigDecimal.valueOf(100)) <= 0) {
                notifications.create(Notifications.NotificationType.ERROR)
                        .withCaption("Ошибка по скидке")
                        .withDescription("Сумма скидки фиксированной суммой не может быть меньше или равно 100 тенге")
                        .show();
                return;
            }
        } else if (Boolean.FALSE.equals(payParamMethod) && payParamDiscontAmountValue != null) {
            if (payParamDiscontAmountValue.compareTo(BigDecimal.valueOf(100)) > 0) {
                notifications.create(Notifications.NotificationType.ERROR)
                        .withCaption("Ошибка по скидке")
                        .withDescription("Сумма скидки в процентах не может быть больше 100%")
                        .show();
                return;
            }
        }
        if (Objects.requireNonNull(newPeriod).isAfter(currPeriod) && newPeriod.isBefore(currPeriod.plusMonths(1))) {
            dialogs.createOptionDialog()
                    .withCaption("ПОДТВЕРЖДЕНИЕ")
                    .withMessage("Вы сдвинули дату платежа. По занятиям, которые  не были проведены до " + newPeriod + " будет создано новое платежное требование для оплаты. Вы уверены?")
                    .withActions(
                            new DialogAction(DialogAction.Type.YES, Action.Status.PRIMARY).withHandler(e -> {
                                List<Groups> studentGroups = student.getStudGroups();
                                for (Groups studentGroup : studentGroups) {
                                    List<Courses> courses = studentGroup.getGroupCourse();
                                    List<LocalDate> bethCourseCount = new ArrayList<>();
                                    boolean shouldCreateOrder = false;
                                    BigDecimal totalLastAmount = BigDecimal.ZERO;
                                    for (Courses course : courses) {
                                        if (!course.getCourseCost().equals(BigDecimal.ZERO)) {
                                            BigDecimal courseCost = GetCourseRealAmount.getCourseRealAmount(student, studentGroup, course.getCourseName()).getAmount();
                                            if (!courseCost.equals(BigDecimal.ZERO)) {
                                                LocalDateTime courseDateTime = course.getCourseStartDate();
                                                LocalDate courseDate = LocalDate.of(courseDateTime.getYear(), courseDateTime.getMonth(), courseDateTime.getDayOfMonth());
                                                if (courseDate.isBefore(newPeriod) && courseDate.isAfter(LocalDate.now()) && course.getCourseStatus() != CourseStatus.HELD) {
                                                    bethCourseCount.add(courseDate);
                                                }
                                            }
                                            if (!bethCourseCount.isEmpty()) {
                                                shouldCreateOrder = true;
                                                if (payParamDiscontAmountValue == null || payParamDiscontAmountValue.compareTo(BigDecimal.ZERO) == 0) {
                                                    totalLastAmount = totalLastAmount.add(BigDecimal.valueOf(bethCourseCount.size()).multiply(courseCost));
                                                } else {
                                                    BigDecimal percentAmount = BigDecimal.ZERO;
                                                    if (payParamDiscontAmountValue.compareTo(BigDecimal.ZERO) > 0 && payParamDiscontAmountValue.compareTo(BigDecimal.valueOf(100)) < 0) {
                                                        percentAmount = courseCost.multiply(student.getStudDiscount()).divide(BigDecimal.valueOf(100), RoundingMode.UP);
                                                    } else {
                                                        percentAmount = courseCost.subtract(student.getStudDiscount());
                                                    }
                                                    totalLastAmount = totalLastAmount.add(BigDecimal.valueOf(bethCourseCount.size()).multiply(courseCost).subtract(percentAmount));
                                                }
                                            }
                                        }
                                    }
                                    if (shouldCreateOrder) {
                                        String branchCode = studentGroup.getGroupBranch().getBranchCode();
                                        String ordNum = docNumbGenerate.getNextNumber("ORDDIF", branchCode);
                                        createOrder.createNewOrder(ordNum, LocalDateTime.now(), studentGroup.getGroupBranch(), totalLastAmount,
                                                OrderPurpose.SUBSCRIPTION, OrderStatus.CREATED, student, newPeriod, studentGroup, null);
                                    }
                                }
                                paymentParam.setPayParamPayDay(newPeriod);
                                paymentParam.setPayParamDiscount(payParamDiscountReasonValue);
                                paymentParam.setPayParamDiscontAmount(payParamDiscontAmountValue);
                                dataManager.save(paymentParam);
                                List<Orders> orders = student.getStudOrders();
                                for (Orders order : orders) {
                                    if (order.getOrderStatus() == OrderStatus.CREATED && order.getOrderGroup().equals(payParamGroupsField.getValue())) {
                                        if (Boolean.TRUE.equals(payParamMethodField.getValue())) {
                                            BigDecimal disVisitAmount = order.getOrderAmount().subtract(payParamDiscontAmountValue);
                                            order.setOrderAmount(disVisitAmount);
                                            order.setOrderDateTime(LocalDateTime.now());
                                            order.setOrderPeriodEnd(newPeriod);
                                            dataManager.save(order);
                                        } else if (Boolean.FALSE.equals(payParamMethodField.getValue())) {
                                            BigDecimal percentAmount = order.getOrderAmount().multiply(payParamDiscontAmountValue).divide(BigDecimal.valueOf(100), RoundingMode.UP);
                                            BigDecimal disVisitAmount = order.getOrderAmount().subtract(percentAmount);
                                            order.setOrderAmount(disVisitAmount);
                                            order.setOrderDateTime(LocalDateTime.now());
                                            order.setOrderPeriodEnd(newPeriod);
                                            dataManager.save(order);
                                        }
                                    }
                                }
                                student.setStudOrderPeriod(LocalDate.EPOCH);
                                student.setStudDiscount(BigDecimal.ZERO);
                                student.setStudDiscountReason(null);
                                dataManager.save(student);
                            }),
                            new DialogAction(DialogAction.Type.NO)
                    )
                    .show();
        } else if (newPeriod.isBefore(currPeriod) && newPeriod.isAfter(currPeriod.minusMonths(1))) {
            List<Orders> orders = student.getStudOrders();
            if (payParamDiscontAmountValue != null && payParamDiscontAmountValue.compareTo(BigDecimal.ZERO) > 0) {
                for (Orders order : orders) {
                    if (order.getOrderStatus() == OrderStatus.CREATED && order.getOrderGroup().equals(payParamGroupsField.getValue())) {
                        if (Boolean.TRUE.equals(payParamMethodField.getValue())) {
                            BigDecimal disVisitAmount = order.getOrderAmount().subtract(payParamDiscontAmountValue);
                            order.setOrderAmount(disVisitAmount);
                            order.setOrderDateTime(LocalDateTime.now());
                            order.setOrderPeriodEnd(newPeriod);
                            dataManager.save(order);
                        } else if (Boolean.FALSE.equals(payParamMethodField.getValue())) {
                            BigDecimal discountAmount = (payParamDiscontAmountValue).divide(BigDecimal.valueOf(100), RoundingMode.UP);
                            BigDecimal percentAmount = order.getOrderAmount().multiply(discountAmount);
                            BigDecimal disVisitAmount = order.getOrderAmount().subtract(percentAmount);
                            order.setOrderAmount(disVisitAmount);
                            order.setOrderDateTime(LocalDateTime.now());
                            order.setOrderPeriodEnd(newPeriod);
                            dataManager.save(order);
                        }
                    }
                }
                paymentParam.setPayParamDiscount(payParamDiscountReasonValue);
                paymentParam.setPayParamDiscontAmount(payParamDiscontAmountValue);
            } else {
                for (Orders order : orders) {
                    if (order.getOrderStatus() == OrderStatus.CREATED && order.getOrderGroup().equals(payParamGroupsField.getValue())) {
                        BigDecimal orderAmount = order.getOrderGroup().getGroupCost();
                        order.setOrderAmount(orderAmount);
                        order.setOrderDateTime(LocalDateTime.now());
                        order.setOrderPeriodEnd(newPeriod);
                        dataManager.save(order);
                    }
                }
                paymentParam.setPayParamDiscount(null);
                paymentParam.setPayParamDiscontAmount(BigDecimal.ZERO);
            }
            paymentParam.setPayParamPayDay(newPeriod);
            dataManager.save(paymentParam);
        } else if (newPeriod.isAfter(currPeriod.plusMonths(1))) {
            notifications.create(Notifications.NotificationType.ERROR)
                    .withCaption("Ошибка переноса даты платежа")
                    .withDescription("Максимальная дата платежа не может быть больше одного месяца с текущей даты")
                    .show();
        } else if (newPeriod.isBefore(currPeriod.minusMonths(1))) {
            notifications.create(Notifications.NotificationType.ERROR)
                    .withCaption("Ошибка переноса даты платежа")
                    .withDescription("Минимальная дата платежа не может быть меньше месяца с текущей даты")
                    .show();
        }
        closeWithDiscard();
    }
}