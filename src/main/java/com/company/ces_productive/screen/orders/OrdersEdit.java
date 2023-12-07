package com.company.ces_productive.screen.orders;

import io.jmix.ui.screen.*;
import com.company.ces_productive.entity.Orders;

@UiController("CES_Orders.edit")
@UiDescriptor("orders-edit.xml")
@EditedEntityContainer("ordersDc")
public class OrdersEdit extends StandardEditor<Orders> {
}