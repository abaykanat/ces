package com.company.ces_productive.screen.paymentparam;

import com.company.ces_productive.app.CreateOrder;
import com.company.ces_productive.app.DocNumbGenerate;
import com.company.ces_productive.entity.*;
import com.company.ces_productive.entity.courses.Courses;
import io.jmix.core.DataManager;
import io.jmix.ui.Dialogs;
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

    public BigDecimal getCourseRealAmount(Groups group, Students student) {
        List<Courses> courses = group.getGroupCourse();
        List<PaymentParam> paymentParams = student.getStudPayParam();
        for (PaymentParam paymentParam : paymentParams) {
            if (paymentParam.getPayParamGroups().getId().equals(group.getId())) {
                List<Orders> paidOrders = student.getStudOrders().stream()
                        .filter(orders -> orders.getOrderGroup().getId().equals(group.getId()) && orders.getOrderStatus() == OrderStatus.PAID)
                        .toList();
                int orderCount = paidOrders.size();
                LocalDate baseDate = paymentParam.getPayParamPayDay();
                LocalDate payParamDate = baseDate.minusMonths(orderCount);
                LocalDate newParamDate = payParamDate.plusMonths(1);
                BigDecimal directionAmount = group.getGroupDirection().getDirectionMinCost();
                List<Courses> filteredCourses = courses.stream()
                        .filter(course -> (course.getCourseStartDate().isEqual(payParamDate.atStartOfDay()) || course.getCourseStartDate().isAfter(payParamDate.atStartOfDay()))
                                && course.getCourseStartDate().isBefore(newParamDate.atStartOfDay()))
                        .toList();
                int courseCount = filteredCourses.size();
                if (courseCount == 0) {
                    return BigDecimal.ZERO;
                }
                return directionAmount.divide(BigDecimal.valueOf(courseCount), RoundingMode.UP);
            }
        }
        return BigDecimal.ZERO;
    }

    @Subscribe("newParamSave")
    public void onNewParamSave(final Action.ActionPerformedEvent event) {
        PaymentParam paymentParam = getEditedEntity();
        Students student = paymentParam.getPayParamStudent();
        LocalDate currPeriod = LocalDate.now();
        LocalDate newPeriod = payParamPayDayField.getValue();
        BigDecimal payParamDiscontAmountValue = payParamDiscontAmountField.getValue();
        DiscountReason payParamDiscountReasonValue = payParamDiscountReasonField.getValue();
        if (Objects.requireNonNull(newPeriod).isAfter(currPeriod)) {
            dialogs.createOptionDialog()
                    .withCaption("ПОДТВЕРЖДЕНИЕ")
                    .withMessage("Вы сдвинули дату платежа. По занятиям, которые  не были проведены до " + newPeriod + " будет создано новое платежное требование для оплаты. Вы уверены?")
                    .withActions(
                            new DialogAction(DialogAction.Type.YES, Action.Status.PRIMARY).withHandler(e -> {
                                List<Groups> studentGroups = student.getStudGroups();
                                for (Groups studentGroup : studentGroups) {
                                    BigDecimal courseCost = getCourseRealAmount(studentGroup, student);
                                    if (!courseCost.equals(BigDecimal.ZERO)) {
                                        List<Courses> courses = studentGroup.getGroupCourse();
                                        List<LocalDate> bethCourseCount = new ArrayList<>();
                                        for (Courses course : courses) {
                                            LocalDateTime courseDateTime = course.getCourseStartDate();
                                            LocalDate courseDate = LocalDate.of(courseDateTime.getYear(), courseDateTime.getMonth(), courseDateTime.getDayOfMonth());
                                            if (courseDate.isBefore(newPeriod) && course.getCourseStatus() != CourseStatus.HELD) {
                                                bethCourseCount.add(courseDate);
                                            }
                                        }
                                        if (!bethCourseCount.isEmpty()) {
                                            BigDecimal lastAmount;
                                            String branchCode = studentGroup.getGroupBranch().getBranchCode();
                                            String ordNum = docNumbGenerate.getNextNumber("ORDDIF", branchCode);
                                            if (payParamDiscontAmountValue == null) {
                                                lastAmount = BigDecimal.valueOf(bethCourseCount.size()).multiply(courseCost);
                                            } else {
                                                BigDecimal percentAmount = courseCost.multiply(student.getStudDiscount()).divide(BigDecimal.valueOf(100), RoundingMode.UP);
                                                lastAmount = BigDecimal.valueOf(bethCourseCount.size()).multiply(courseCost).subtract(percentAmount);
                                            }
                                            createOrder.createNewOrder(ordNum, LocalDateTime.now(), studentGroup.getGroupBranch(), lastAmount,
                                                    OrderPurpose.SUBSCRIPTION, OrderStatus.CREATED, student, newPeriod, studentGroup, null);
                                        }
                                    }
                                }
                                paymentParam.setPayParamPayDay(newPeriod);
                                paymentParam.setPayParamDiscount(payParamDiscountReasonValue);
                                paymentParam.setPayParamDiscontAmount(payParamDiscontAmountValue);
                                dataManager.save(paymentParam);
                                List<Orders> orders = student.getStudOrders();
                                for (Orders order : orders) {
                                    if (order.getOrderStatus() == OrderStatus.CREATED && order.getOrderGroup().equals(payParamGroupsField.getValue())) {
                                        BigDecimal percentAmount = order.getOrderAmount().multiply(payParamDiscontAmountValue).divide(BigDecimal.valueOf(100), RoundingMode.UP);
                                        BigDecimal disVisitAmount = order.getOrderAmount().subtract(percentAmount);
                                        order.setOrderAmount(disVisitAmount);
                                        order.setOrderDateTime(LocalDateTime.now());
                                        order.setOrderPeriodEnd(newPeriod);
                                        dataManager.save(order);
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
        } else {
            List<Orders> orders = student.getStudOrders();
            if (payParamDiscontAmountValue != null && payParamDiscontAmountValue.compareTo(BigDecimal.ZERO) > 0) {
                for (Orders order : orders) {
                    if (order.getOrderStatus() == OrderStatus.CREATED && order.getOrderGroup().equals(payParamGroupsField.getValue())) {
                        BigDecimal percentAmount = order.getOrderAmount().multiply(payParamDiscontAmountValue).divide(BigDecimal.valueOf(100), RoundingMode.UP);
                        BigDecimal disVisitAmount = order.getOrderAmount().subtract(percentAmount);
                        order.setOrderAmount(disVisitAmount);
                        order.setOrderDateTime(LocalDateTime.now());
                        order.setOrderPeriodEnd(newPeriod);
                        dataManager.save(order);
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
        }
        closeWithDiscard();
    }
}