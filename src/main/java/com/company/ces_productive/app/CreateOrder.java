package com.company.ces_productive.app;
import com.company.ces_productive.entity.*;
import io.jmix.core.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class CreateOrder {
    @Autowired
    private DataManager dataManager;
    public void createNewOrder(String orderNumber, LocalDateTime orderDateTime, Branches orderBranch,
                               BigDecimal orderAmount, OrderPurpose orderPurpose,
                               OrderStatus orderStatus, Students orderStudent, LocalDate orderPeriodEnd, Groups orderGroup, Books orderBook)
    {
        Orders orders = dataManager.create(Orders.class);
        orders.setOrderNumber(orderNumber);
        orders.setOrderDateTime(orderDateTime);
        orders.setOrderBranch(orderBranch);
        orders.setOrderAmount(orderAmount);
        orders.setOrderPurpose(orderPurpose);
        orders.setOrderStatus(orderStatus);
        orders.setOrderStudent(orderStudent);
        orders.setOrderPeriodEnd(orderPeriodEnd);
        orders.setOrderGroup(orderGroup);
        orders.setOrderBook(orderBook);
        dataManager.save(orders);
    }

    public void createNewPayParameter(Students student, Groups groups, LocalDate payDay, DiscountReason reason, BigDecimal discount) {
        PaymentParam paymentParam = dataManager.create(PaymentParam.class);
        paymentParam.setPayParamStudent(student);
        paymentParam.setPayParamGroups(groups);
        paymentParam.setPayParamPayDay(payDay);
        paymentParam.setPayParamDiscount(reason);
        paymentParam.setPayParamDiscontAmount(discount);
        dataManager.save(paymentParam);
    }
}
