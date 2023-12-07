package com.company.ces_productive.screen.payments;

import com.company.ces_productive.entity.OrderStatus;
import com.company.ces_productive.entity.Orders;
import io.jmix.core.DataManager;
import io.jmix.ui.action.Action;
import io.jmix.ui.component.GroupTable;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.screen.*;
import com.company.ces_productive.entity.Payments;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

@UiController("CES_Payments.browse")
@UiDescriptor("payments-browse.xml")
@LookupComponent("paymentsesTable")
public class PaymentsBrowse extends StandardLookup<Payments> {
    @Autowired
    private GroupTable<Payments> paymentsesTable;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private CollectionLoader<Payments> paymentsesDl;

    @Subscribe("paymentsesTable.remove")
    public void onPaymentsesTableRemove(final Action.ActionPerformedEvent event) {
        Payments payment = paymentsesTable.getSingleSelected();
        Orders order = Objects.requireNonNull(payment).getPayOrder();
        if (order != null) {
            if (order.getOrderPeriodEnd() != null) {
                order.setOrderStatus(OrderStatus.CREATED);
                dataManager.save(order);
            }
            dataManager.remove(payment);
            paymentsesDl.load();
        } else {
            dataManager.remove(payment);
            paymentsesDl.load();
        }
    }
}