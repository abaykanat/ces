package com.company.ces_productive.listener;

import com.company.ces_productive.app.CreateOrder;
import com.company.ces_productive.app.DocNumbGenerate;
import com.company.ces_productive.entity.*;
import io.jmix.core.DataManager;
import io.jmix.core.Id;
import io.jmix.core.event.EntityChangedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component("CESTWO_PaymentsEventListener")
public class PaymentsEventListener {

    @Autowired
    private DataManager dataManager;
    @Autowired
    private CreateOrder createOrder;
    @Autowired
    private DocNumbGenerate docNumbGenerate;

    public List<Payments> studPaymentList(Students student) {
        return dataManager.load(Payments.class)
                .query("select p from CES_Payments p where p.payStudent = :stud")
                .parameter("stud", student)
                .list();
    }

    @EventListener
    public void onPaymentsChangedBeforeCommit(EntityChangedEvent<Payments> event) {
        if (event.getType() == EntityChangedEvent.Type.CREATED) {
            Id<Payments> paymentId = event.getEntityId();
            Payments payment = dataManager.load(paymentId).one();
            if (payment.getPayPurpose() == OrderPurpose.SUBSCRIPTION) {                                                 //платеж по абонименту
                Students stud = payment.getPayOrder().getOrderStudent();
                Branches branch = payment.getPayBranch();
                BigDecimal cost = payment.getPayOrder().getOrderGroup().getGroupDirection().getDirectionMinCost();
                String orderNumber = payment.getPayOrder().getOrderNumber();
                Groups group = payment.getPayOrder().getOrderGroup();
                String branchCode = payment.getPayBranch().getBranchCode();
                String ordNum = docNumbGenerate.getNextNumber("ORD", branchCode);
                Orders ordStat = payment.getPayOrder();
                if (payment.getPayAmount().compareTo(payment.getPayOrder().getOrderAmount()) == 0) {                    //оплата сразу
                    List<PaymentParam> paymentParams = stud.getStudPayParam();
                    if (!paymentParams.isEmpty()) {
                        for (PaymentParam paymentParam : paymentParams) {
                            if (paymentParam.getPayParamGroups() == group) {
                                if (paymentParam.getPayParamDiscontAmount() == null) {
                                    LocalDate payPeriod = paymentParam.getPayParamPayDay().plusMonths(1);
                                    if (!orderNumber.contains("ORDDIF")) {
                                        createOrder.createNewOrder(ordNum, LocalDateTime.now(), branch, cost,
                                                OrderPurpose.SUBSCRIPTION, OrderStatus.CREATED, stud, payPeriod, group, null);
                                        paymentParam.setPayParamPayDay(payPeriod);
                                        dataManager.save(paymentParam);
                                    }
                                }
                                else {
                                    BigDecimal percentAmount = (cost.multiply((paymentParam.getPayParamDiscontAmount()))).divide(BigDecimal.valueOf(100), RoundingMode.UP);
                                    BigDecimal finalCost = cost.subtract(percentAmount);
                                    LocalDate payPeriod = paymentParam.getPayParamPayDay().plusMonths(1);
                                    if (!orderNumber.contains("ORDDIF")) {
                                        createOrder.createNewOrder(ordNum, LocalDateTime.now(), branch, finalCost,
                                                OrderPurpose.SUBSCRIPTION, OrderStatus.CREATED, stud, payPeriod, group, null);
                                        paymentParam.setPayParamPayDay(payPeriod);
                                        dataManager.save(paymentParam);
                                    }
                                }
                            }
                        }
                    } else {
                        LocalDate period = stud.getStudOrderPeriod().plusMonths(1);
                        if (stud.getStudDiscount() == null) {
                            if (!orderNumber.contains("ORDDIF")) {
                                createOrder.createNewOrder(ordNum, LocalDateTime.now(), branch, cost,
                                        OrderPurpose.SUBSCRIPTION, OrderStatus.CREATED, stud, period, group, null);
                                stud.setStudOrderPeriod(period);
                            }
                        } else {
                            if (!orderNumber.contains("ORDDIF")) {
                                BigDecimal percentAmount = (cost.multiply((stud.getStudDiscount()))).divide(BigDecimal.valueOf(100), RoundingMode.UP);
                                BigDecimal finalCost = cost.subtract(percentAmount);
                                createOrder.createNewOrder(ordNum, LocalDateTime.now(), branch, finalCost,
                                        OrderPurpose.SUBSCRIPTION, OrderStatus.CREATED, stud, period, group, null);
                                stud.setStudOrderPeriod(period);
                            }
                        }
                    }
                    ordStat.setOrderStatus(OrderStatus.PAID);
                    BigDecimal oldActualAmount = stud.getStudActualAmount();
                    BigDecimal payAmount = payment.getPayAmount();
                    stud.setStudActualAmount(oldActualAmount.add(payAmount));
                    dataManager.save(stud);
                }
                if (payment.getPayAmount().compareTo(payment.getPayOrder().getOrderAmount()) > 0){                      //оплата выше сумму
                    throw new IllegalStateException("Сумма платежа выше, чем сумма требования");
                }
                if (payment.getPayAmount().compareTo(payment.getPayOrder().getOrderAmount()) < 0){                      //оплата частично
                    if (payment.getPayOrder().getOrderStatus() == OrderStatus.CREATED) {
                        ordStat.setOrderStatus(OrderStatus.PART_PAID);
                        ordStat.setOrderPartAmount(ordStat.getOrderAmount().subtract(payment.getPayAmount()));
                        BigDecimal oldActualAmount = stud.getStudActualAmount();
                        BigDecimal payAmount = payment.getPayAmount();
                        stud.setStudActualAmount(oldActualAmount.add(payAmount));
                        dataManager.save(stud);
                    } else if (payment.getPayOrder().getOrderStatus() == OrderStatus.PART_PAID) {
                        List<Payments> studPaymentsList = studPaymentList(stud);
                        List<Payments> orderPaymentsList = new ArrayList<>();
                        for (Payments studPayment : studPaymentsList) {                                                 //получить список платежей по ордеру
                            if (studPayment.getPayOrder() == payment.getPayOrder()
                                    && studPayment.getPayOrder().getOrderStatus() == OrderStatus.PART_PAID) {
                                orderPaymentsList.add(studPayment);
                            }
                        }
                        BigDecimal totalPayAmount = BigDecimal.ZERO;
                        for (Payments orderPayment : orderPaymentsList) {
                            totalPayAmount = totalPayAmount.add(orderPayment.getPayAmount());
                        }
                        if (totalPayAmount.compareTo(payment.getPayOrder().getOrderAmount()) == 0) {                    //если платеж последний по ордеру, то закрываем ордер
                            List<PaymentParam> paymentParams = stud.getStudPayParam();
                            LocalDate period = stud.getStudOrderPeriod().plusMonths(1);
                            if (!paymentParams.isEmpty()) {
                                for (PaymentParam paymentParam : paymentParams) {
                                    if (paymentParam.getPayParamDiscontAmount() == null) {
                                        if (!orderNumber.contains("ORDDIF")) {
                                            LocalDate payPeriod = paymentParam.getPayParamPayDay().plusMonths(1);
                                            createOrder.createNewOrder(ordNum, LocalDateTime.now(), branch, cost,
                                                    OrderPurpose.SUBSCRIPTION, OrderStatus.CREATED, stud, payPeriod, group, null);
                                            paymentParam.setPayParamPayDay(payPeriod);
                                            dataManager.save(paymentParam);
                                        }
                                    }
                                    else {
                                        if (!orderNumber.contains("ORDDIF")) {
                                            BigDecimal percentAmount = (cost.multiply((paymentParam.getPayParamDiscontAmount()))).divide(BigDecimal.valueOf(100), RoundingMode.UP);
                                            BigDecimal finalCost = cost.subtract(percentAmount);
                                            LocalDate payPeriod = paymentParam.getPayParamPayDay().plusMonths(1);
                                            createOrder.createNewOrder(ordNum, LocalDateTime.now(), branch, finalCost,
                                                    OrderPurpose.SUBSCRIPTION, OrderStatus.CREATED, stud, payPeriod, group, null);
                                            paymentParam.setPayParamPayDay(payPeriod);
                                            dataManager.save(paymentParam);
                                        }
                                    }
                                }
                            } else {
                                if (stud.getStudDiscount() == null) {
                                    if (!orderNumber.contains("ORDDIF")) {
                                        createOrder.createNewOrder(ordNum, LocalDateTime.now(), branch, cost,
                                                OrderPurpose.SUBSCRIPTION, OrderStatus.CREATED, stud, period, group, null);
                                        stud.setStudOrderPeriod(period);
                                    }
                                } else {
                                    if (!orderNumber.contains("ORDDIF")) {
                                        BigDecimal percentAmount = (cost.multiply((stud.getStudDiscount()))).divide(BigDecimal.valueOf(100), RoundingMode.UP);
                                        BigDecimal finalCost = cost.subtract(percentAmount);
                                        createOrder.createNewOrder(ordNum, LocalDateTime.now(), branch, finalCost,
                                                OrderPurpose.SUBSCRIPTION, OrderStatus.CREATED, stud, period, group, null);
                                        stud.setStudOrderPeriod(period);
                                    }
                                }
                            }
                            ordStat.setOrderStatus(OrderStatus.PAID);
                            ordStat.setOrderPartAmount(ordStat.getOrderPartAmount().subtract(payment.getPayAmount()));
                            BigDecimal oldActualAmount = stud.getStudActualAmount();
                            BigDecimal payAmount = payment.getPayAmount();
                            stud.setStudActualAmount(oldActualAmount.add(payAmount));
                            stud.setStudOrderPeriod(period);
                            dataManager.save(stud);
                        }
                        if (totalPayAmount.compareTo(payment.getPayOrder().getOrderAmount()) > 0) {                     //если платеж выше, то выдаем ошибку
                            throw new IllegalStateException("Сумма платежа выше, чем сумма требования");
                        }
                        if (totalPayAmount.compareTo(payment.getPayOrder().getOrderAmount()) < 0) {                      //если платеж не последний, то идем дальше
                            ordStat.setOrderStatus(OrderStatus.PART_PAID);
                            ordStat.setOrderPartAmount(ordStat.getOrderPartAmount().subtract(payment.getPayAmount()));
                            BigDecimal oldActualAmount = stud.getStudActualAmount();
                            BigDecimal payAmount = payment.getPayAmount();
                            stud.setStudActualAmount(oldActualAmount.add(payAmount));
                            dataManager.save(stud);
                        }
                    }
                }
            }
            else {                                                                                                      //платеж по книге
                Orders ordStat = payment.getPayOrder();
                ordStat.setOrderStatus(OrderStatus.PAID);
                dataManager.save(ordStat);
            }
        }
    }
}