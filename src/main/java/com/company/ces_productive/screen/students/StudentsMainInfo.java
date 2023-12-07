package com.company.ces_productive.screen.students;

import com.company.ces_productive.app.DocNumbGenerate;
import com.company.ces_productive.entity.*;
import com.company.ces_productive.screen.orders.OrdersBook;
import com.company.ces_productive.screen.payments.PaymentsEdit;
import io.jmix.core.DataManager;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.ui.Notifications;
import io.jmix.ui.ScreenBuilders;
import io.jmix.ui.action.Action;
import io.jmix.ui.component.DataGrid;
import io.jmix.ui.component.GroupTable;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@UiController("CES_Students.mainInfo")
@UiDescriptor("students-mainInfo.xml")
@LookupComponent("studentsesTable")
public class StudentsMainInfo extends StandardLookup<Students> {

    @Autowired
    private CollectionLoader<Students> studentsesDl;
    @Autowired
    private CurrentAuthentication currentAuthentication;
    @Autowired
    private GroupTable<Students> studentsesTable;
    @Autowired
    private DocNumbGenerate docNumbGenerate;
    @Autowired
    private ScreenBuilders screenBuilders;
    @Autowired
    private Notifications notifications;
    @Autowired
    private MessageBundle messageBundle;
    @Autowired
    private DataManager dataManager;

    public List<String> getUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        return roles;
    }

    public List<Branches> getCurrBranches() {
        return dataManager.load(Branches.class)
                .query("select b from CES_Branches b")
                .list();
    }

    public List<Branches> getCurrBranch() {
        return dataManager.load(Branches.class)
                .query("select b from CES_Branches b where b = :currUserBranch")
                .parameter("currUserBranch", ((User) currentAuthentication.getUser()).getBranch())
                .list();
    }

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        if (getUserRoles().contains("manager")) {
            studentsesDl.setParameter("currBranch", getCurrBranch());
        } else {
            studentsesDl.setParameter("currBranch", getCurrBranches());
        }
        studentsesDl.load();
    }

    @Subscribe("studentsesTable.payment")
    public void onStudentsesTablePayment(Action.ActionPerformedEvent event) {
        Students student = studentsesTable.getSingleSelected();
        if (student != null) {
            Screen paymentEditor = screenBuilders.editor(Payments.class, this)
                    .newEntity()
                    .withScreenClass(PaymentsEdit.class)
                    .withOpenMode(OpenMode.DIALOG)
                    .withInitializer(payments -> payments.setPayStudent(student))
                    .build();

            paymentEditor.addAfterCloseListener(afterCloseEvent -> {
                if (afterCloseEvent.closedWith(StandardOutcome.COMMIT)) {
                    getScreenData().loadAll();
                }
            });
            paymentEditor.show();
        } else {CreateNotification("Please, make student for pay to the list", "Payment action");}
    }

    @Subscribe("studentsesTable.book")
    public void onStudentsesTableBook(Action.ActionPerformedEvent event) {
        Students student = studentsesTable.getSingleSelected();
        if (student != null) {
            screenBuilders.editor(Orders.class, this)
                    .newEntity()
                    .withInitializer(orders -> {
                        String branchCode = student.getStudBranch().getBranchCode();
                        orders.setOrderNumber(docNumbGenerate.getNextNumber("ORD", branchCode));
                        orders.setOrderDateTime(LocalDateTime.now());
                        orders.setOrderPeriodEnd(LocalDate.now());
                        orders.setOrderStudent(student);
                        orders.setOrderStatus(OrderStatus.PAID);
                        orders.setOrderPurpose(OrderPurpose.BOOK);
                        orders.setOrderBranch(((User) currentAuthentication.getUser()).getBranch());
                    })
                    .withScreenClass(OrdersBook.class)
                    .withOpenMode(OpenMode.DIALOG)
                    .build()
                    .show();
        } else {CreateNotification("Please, make student for book to the list", "Book action");}
    }

    private void  CreateNotification (String messageKey, String actionName) {
        message(messageKey, actionName);
    }
    private void message(String messageKey, String actionName){
        notifications.create(Notifications.NotificationType.ERROR)
                .withCaption("Selection error")
                .withDescription(messageBundle.formatMessage(messageKey, actionName))
                .show();
    }
}