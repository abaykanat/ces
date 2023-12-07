package com.company.ces_productive.screen.orders;

import com.company.ces_productive.app.DocNumbGenerate;
import com.company.ces_productive.entity.*;
import io.jmix.core.DataManager;
import io.jmix.core.SaveContext;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.ui.component.*;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@UiController("CES_Orders.book")
@UiDescriptor("orders-book.xml")
@EditedEntityContainer("ordersDc")
public class OrdersBook extends StandardEditor<Orders> {
    @Autowired
    private TextField<BigDecimal> orderAmountField;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private DocNumbGenerate docNumbGenerate;
    @Autowired
    private ComboBox<PaymentMode> payMode;
    @Autowired
    private CurrentAuthentication currentAuthentication;
    @Autowired
    private EntityPicker<Books> orderBookField;

    @Subscribe("orderBookField")
    public void onOrderBookFieldValueChange(HasValue.ValueChangeEvent<Books> event) {
        Books books = event.getValue();
        orderAmountField.setValue(Objects.requireNonNull(books).getBookType().getBookTypeCost());
        books.setBookSoldUser((User) currentAuthentication.getUser());
    }

    @Subscribe("commitAndCloseBtn")
    public void onCommitAndCloseBtnClick(Button.ClickEvent event) {
        Orders order = getEditedEntity();
        order.setOrderBook(orderBookField.getValue());
        String branchCode = order.getOrderStudent().getStudBranch().getBranchCode();
        String payNumber = docNumbGenerate.getNextNumber("PAY", branchCode);
        CreateBookPayment(payNumber, LocalDate.now(), order.getOrderAmount(), payMode.getValue(), order.getOrderStudent(),
                order, order.getOrderBook().getBookSoldUser(), order.getOrderBranch());
        Books book = order.getOrderBook();
        SaveContext saveContext = new SaveContext();
            book.setBookStudent(order.getOrderStudent());
            book.setBookOrder(order);
            book.setBookStatus(BookStatus.SOLD);
        saveContext.saving(book);
        dataManager.save(saveContext);
    }

    private void CreateBookPayment (String payNumber, LocalDate payDateTime, BigDecimal payAmount,
                                    PaymentMode payMode, Students payStudent, Orders payOrder, User payUser, Branches payBranch)
    {
        Payments payments = dataManager.create(Payments.class);
        payments.setPayNumber(payNumber);
        payments.setPayDateTime(payDateTime);
        payments.setPayAmount(payAmount);
        payments.setPayPurpose(OrderPurpose.BOOK);
        payments.setPayMode(payMode);
        payments.setPayStudent(payStudent);
        payments.setPayOrder(payOrder);
        payments.setPayUser(payUser);
        payments.setPayBranch(payBranch);
        dataManager.save(payments);
    }
}