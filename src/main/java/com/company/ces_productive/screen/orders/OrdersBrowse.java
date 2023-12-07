package com.company.ces_productive.screen.orders;

import com.company.ces_productive.entity.OrderStatus;
import com.company.ces_productive.entity.PaymentParam;
import io.jmix.core.DataManager;
import io.jmix.ui.Notifications;
import io.jmix.ui.action.Action;
import io.jmix.ui.component.GroupTable;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.screen.*;
import com.company.ces_productive.entity.Orders;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@UiController("CES_Orders.browse")
@UiDescriptor("orders-browse.xml")
@LookupComponent("ordersesTable")
public class OrdersBrowse extends StandardLookup<Orders> {
    @Autowired
    private GroupTable<Orders> ordersesTable;
    @Autowired
    private Notifications notifications;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private CollectionLoader<Orders> ordersesDl;

    @Subscribe("ordersesTable.remove")
    public void onOrdersesTableRemove(final Action.ActionPerformedEvent event) {
        Orders order = ordersesTable.getSingleSelected();
        OrderStatus status = Objects.requireNonNull(order).getOrderStatus();
        if (status == OrderStatus.PAID || status == OrderStatus.PART_PAID) {
            notifications.create(Notifications.NotificationType.ERROR)
                    .withCaption("Ошибка удаления")
                    .withDescription("В первую очень необходимо удалить платеж по данному требованию")
                    .show();
        } else {
            LocalDate period = order.getOrderStudent().getStudOrderPeriod();
            if (period.isEqual(LocalDate.EPOCH)) {
                List<PaymentParam> payParams = order.getOrderStudent().getStudPayParam();
                for (PaymentParam payParam : payParams) {
                    if (order.getOrderGroup().getId().equals(payParam.getPayParamGroups().getId())) {
                        payParam.setPayParamPayDay(period.minusMonths(1));
                    }
                }
            } else {
                order.getOrderStudent().setStudOrderPeriod(period.minusMonths(1));
            }
            dataManager.remove(order);
            ordersesDl.load();
        }
    }
}