package com.company.ces_productive.app;
import com.company.ces_productive.entity.Orders;
import com.company.ces_productive.entity.PaymentParam;
import io.jmix.core.DataManager;
import io.jmix.core.security.Authenticated;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class OrderAmountCorrectQuartz implements Job {
    private static final Logger log = LoggerFactory.getLogger(GetCourseAmountQuartz.class);
    @Autowired
    private DataManager dataManager;
    @Authenticated
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            log.info("Starting GetCourseAmountQuartz job execution...");
            List<Orders> orders = dataManager.load(Orders.class)
                    .query("select o from CES_Orders o where o.orderStatus = 'CREATED'")
                    .list();
            for (Orders order : orders) {
                BigDecimal orderAmount = order.getOrderAmount();
                BigDecimal priceAmount = order.getOrderGroup().getGroupDirection().getDirectionMinCost();
                if (!(orderAmount.compareTo(priceAmount) == 0)) {
                    List <PaymentParam> paymentParams = order.getOrderStudent().getStudPayParam();
                    for (PaymentParam paymentParam : paymentParams) {
                        if (paymentParam.getPayParamGroups().getId() == order.getOrderGroup().getId()) {
                            BigDecimal discount = paymentParam.getPayParamDiscontAmount();
                            Boolean discountMethod = paymentParam.getPayParamMethod();
                            if (discountMethod.equals(Boolean.FALSE)) {
                                BigDecimal percentAmount = (orderAmount.multiply(discount)).divide(BigDecimal.valueOf(100), RoundingMode.UP);
                                BigDecimal finalOrderAmount = orderAmount.subtract(percentAmount);
                                BigDecimal finalPriceAmount = priceAmount.subtract(percentAmount);
                                if (!(finalOrderAmount.compareTo(finalPriceAmount) == 0)) {
                                    order.setOrderAmount(priceAmount);
                                    dataManager.save(order);
                                }
                            } else {
                                BigDecimal percentAmount = paymentParam.getPayParamDiscontAmount();
                                BigDecimal finalOrderAmount = orderAmount.subtract(percentAmount);
                                BigDecimal finalPriceAmount = priceAmount.subtract(percentAmount);
                                if (!(finalOrderAmount.compareTo(finalPriceAmount) == 0)) {
                                    order.setOrderAmount(priceAmount);
                                    dataManager.save(order);
                                }
                            }
                        }
                    }
                }
            }
            log.info("GetCourseAmountQuartz job execution completed successfully.");
        } catch (Exception e) {
            log.error("Error occurred during GetCourseAmountQuartz job execution", e);
            throw new JobExecutionException(e);
        }
    }
}
