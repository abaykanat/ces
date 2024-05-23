package com.company.ces_productive.screen.pivotincomeexpense;

import com.company.ces_productive.entity.*;
import com.company.ces_productive.entity.courses.Courses;
import io.jmix.core.DataManager;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.pivottable.component.PivotTable;
import io.jmix.pivottable.component.PivotTableExtension;
import io.jmix.pivottable.component.impl.PivotExcelExporter;
import io.jmix.pivottable.component.impl.PivotTableExtensionImpl;
import io.jmix.ui.Notifications;
import io.jmix.ui.action.Action;
import io.jmix.ui.component.*;
import io.jmix.ui.model.CollectionContainer;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.model.KeyValueCollectionLoader;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@UiController("CES_PivotIncomeExpense")
@UiDescriptor("pivot-Income-Expense.xml")
public class PivotIncomeExpense extends Screen {
    @Autowired
    private DataManager dataManager;
    @Autowired
    private CurrentAuthentication currentAuthentication;
    @Autowired
    private CollectionLoader<Students> studentsesDl;
    @Autowired
    private CollectionLoader<Payments> paymentsDl;
    @Autowired
    private CollectionLoader<Expenses> expensesDl;
    @Autowired
    private CollectionLoader<Books> booksDl;
    @Autowired
    private KeyValueCollectionLoader studChartsDl;
    @Autowired
    private KeyValueCollectionLoader payChartsDl;
    @Autowired
    private KeyValueCollectionLoader expChartDl;
    @Autowired
    private KeyValueCollectionLoader bookChartDl;
    @Autowired
    private DateField<LocalDate> startDate;
    @Autowired
    private DateField<LocalDate> endDate;
    @Autowired
    private ComboBox<String> periodSelect;
    @Autowired
    private EntityComboBox<Branches> branchSelect;
    @Autowired
    private Notifications notifications;
    @Autowired
    private MessageBundle messageBundle;
    @Autowired
    private TabSheet reportsTabs;
    @Autowired
    private PivotTable studentsTable;
    @Autowired
    private PivotTable incomesTable;
    @Autowired
    private PivotTable expenseTable;
    @Autowired
    private PivotTable booksTable;
    @Autowired
    private PivotTable teacherTblTable;
    private PivotTableExtension extensionBal;
    private PivotTableExtension extensionStud;
    private PivotTableExtension extensionPay;
    private PivotTableExtension extensionExp;
    private PivotTableExtension extensionBook;
    private PivotTableExtension extensionTabel;
    @Autowired
    protected ObjectProvider<PivotExcelExporter> excelExporterObjectProvider;
    @Autowired
    private KeyValueCollectionLoader balanceDl;
    @Autowired
    private PivotTable balanceTable;
    @Autowired
    private CollectionLoader<Courses> coursesesDl;
    @Autowired
    private CollectionContainer<Courses> coursesesDc;
    @Autowired
    private KeyValueCollectionLoader studentsPriceDl;

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

    public LocalDate getBookStartDate() {
        return dataManager.load(Books.class)
                .query("SELECT e FROM CES_Books e WHERE e.bookAcceptDate = (SELECT MIN(e2.bookAcceptDate) FROM CES_Books e2)")
                .one().getBookAcceptDate();
    }
    public LocalDate getBookEndDate() {
        return dataManager.load(Books.class)
                .query("SELECT e FROM CES_Books e WHERE e.bookAcceptDate = (SELECT MAX(e2.bookAcceptDate) FROM CES_Books e2)")
                .one().getBookAcceptDate();
    }

    @Subscribe
    public void onInit(InitEvent event) {
        if (getUserRoles().contains("manager")) {
            balanceDl.setParameter("currBranch", getCurrBranch());
        }
        else {
            balanceDl.setParameter("currBranch", getCurrBranches());
        }
        balanceDl.load();

        if (getUserRoles().contains("manager")) {
            payChartsDl.setParameter("currBranch", getCurrBranch());
            payChartsDl.setParameter("startDate", LocalDate.now().minusDays(7));
            payChartsDl.setParameter("endDate", LocalDate.now());
        }
        else {
            payChartsDl.setParameter("currBranch", getCurrBranches());
            payChartsDl.setParameter("startDate", LocalDate.now().minusDays(7));
            payChartsDl.setParameter("endDate", LocalDate.now());
        }
        payChartsDl.load();

       if (getUserRoles().contains("manager")) {
            expChartDl.setParameter("currBranch", getCurrBranch());
            expChartDl.setParameter("startDate", LocalDate.now().minusDays(7));
            expChartDl.setParameter("endDate", LocalDate.now());
        }
        else {
            expChartDl.setParameter("currBranch", getCurrBranches());
            expChartDl.setParameter("startDate", LocalDate.now().minusDays(7));
            expChartDl.setParameter("endDate", LocalDate.now());
        }
        expChartDl.load();

        if (getUserRoles().contains("manager")) {
            studChartsDl.setParameter("currBranch", getCurrBranch());
            studChartsDl.setParameter("startDate", LocalDate.now().minusDays(7));
            studChartsDl.setParameter("endDate", LocalDate.now());
        }
        else {
            studChartsDl.setParameter("currBranch", getCurrBranches());
            studChartsDl.setParameter("startDate", LocalDate.now().minusDays(7));
            studChartsDl.setParameter("endDate", LocalDate.now());
        }
        studChartsDl.load();

        if (getUserRoles().contains("manager")) {
            studentsPriceDl.setParameter("currBranch", getCurrBranch());
        } else {
            studentsPriceDl.setParameter("currBranch", getCurrBranches());
        }
        studentsPriceDl.load();

        if (getUserRoles().contains("manager")) {
            bookChartDl.setParameter("currBranch", getCurrBranch());
            bookChartDl.setParameter("startDate", getBookStartDate());
            bookChartDl.setParameter("endDate", getBookEndDate());
        }
        else {
            bookChartDl.setParameter("currBranch", getCurrBranches());
            bookChartDl.setParameter("startDate", getBookStartDate());
            bookChartDl.setParameter("endDate", getBookEndDate());
        }
        bookChartDl.load();
        extensionBal = new PivotTableExtensionImpl(balanceTable, excelExporterObjectProvider.getObject());
        extensionStud = new PivotTableExtensionImpl(studentsTable, excelExporterObjectProvider.getObject());
        extensionPay = new PivotTableExtensionImpl(incomesTable, excelExporterObjectProvider.getObject());
        extensionExp = new PivotTableExtensionImpl(expenseTable, excelExporterObjectProvider.getObject());
        extensionBook = new PivotTableExtensionImpl(booksTable, excelExporterObjectProvider.getObject());
        extensionTabel = new PivotTableExtensionImpl(teacherTblTable, excelExporterObjectProvider.getObject());

        List<String> list = new ArrayList<>();
        list.add("Day");
        list.add("Week");
        list.add("Month");
        list.add("Three month");
        list.add("Six month");
        periodSelect.setOptionsList(list);
    }

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        if (getUserRoles().contains("manager")) {
            studentsesDl.setParameter("currBranch", getCurrBranch());
            branchSelect.setValue(((User) currentAuthentication.getUser()).getBranch());
            branchSelect.setEnabled(false);
        }
        else {
            studentsesDl.setParameter("currBranch", getCurrBranches());
        }
        studentsesDl.setParameter("startDate", LocalDate.now().minusDays(7));
        studentsesDl.setParameter("endDate", LocalDate.now());
        studentsesDl.load();

        if (getUserRoles().contains("manager")) {
            paymentsDl.setParameter("currBranch", getCurrBranch());
        }
        else {
            paymentsDl.setParameter("currBranch", getCurrBranches());
        }
        paymentsDl.setParameter("startDate", LocalDate.now().minusDays(7));
        paymentsDl.setParameter("endDate", LocalDate.now());
        paymentsDl.load();

        if (getUserRoles().contains("manager")) {
            expensesDl.setParameter("currBranch", getCurrBranch());
        }
        else {
            expensesDl.setParameter("currBranch", getCurrBranches());
        }
        expensesDl.setParameter("startDate", LocalDate.now().minusDays(7));
        expensesDl.setParameter("endDate", LocalDate.now());
        expensesDl.load();

        if (getUserRoles().contains("manager")) {
            booksDl.setParameter("currBranch", getCurrBranch());
        }
        else {
            booksDl.setParameter("currBranch", getCurrBranches());
        }
        booksDl.setParameter("startDate", getBookStartDate());
        booksDl.setParameter("endDate", getBookEndDate());
        booksDl.load();

        if (getUserRoles().contains("manager")) {
            coursesesDl.setParameter("currBranch", getCurrBranch());
        }
        else {
            coursesesDl.setParameter("currBranch", getCurrBranches());
        }
        coursesesDl.setParameter("startDate", LocalDateTime.now().minusDays(30));
        coursesesDl.setParameter("endDate", LocalDateTime.now());
        coursesesDl.load();
        coursesesDc.getMutableItems().forEach(course -> {
            LocalDateTime courseStartDate = course.getCourseStartDate();
            LocalDate startDate = courseStartDate.toLocalDate();
            course.setCourseStartDate(startDate.atStartOfDay());
        });
    }

    @Subscribe("periodSelect")
    public void onPeriodSelectValueChange(HasValue.ValueChangeEvent event) {
        if (Objects.requireNonNull(periodSelect.getValue()).equals("Day")) {
            startDate.setValue(LocalDate.now());
            endDate.setValue(LocalDate.now());
        }
        if (Objects.requireNonNull(periodSelect.getValue()).equals("Week")) {
            startDate.setValue(LocalDate.now());
            endDate.setValue(LocalDate.now().minusDays(7));
        }
        if (periodSelect.getValue().equals("Month")) {
            startDate.setValue(LocalDate.now());
            endDate.setValue(LocalDate.now().minusDays(30));
        }
        if (periodSelect.getValue().equals("Three month")) {
            startDate.setValue(LocalDate.now());
            endDate.setValue(LocalDate.now().minusDays(90));
        }
        if (periodSelect.getValue().equals("Six month")) {
            startDate.setValue(LocalDate.now());
            endDate.setValue(LocalDate.now().minusDays(180));
        }
    }

    @Subscribe("showBtnAction")
    public void onShowBtnAction(Action.ActionPerformedEvent event) {
        if (startDate.getValue()!=null && endDate.getValue()!=null) {
            Collection<Branches> branch = branchSelect.getLookupSelectedItems();
            if (Objects.requireNonNull(reportsTabs.getSelectedTab()).getName().equals("paymentsTab")) {
                if (branchSelect.getValue() != null) {
                    paymentsDl.setParameter("currBranch", branch);
                    expensesDl.setParameter("currBranch", branch);
                } else  {
                    paymentsDl.setParameter("currBranch", getCurrBranches());
                    expensesDl.setParameter("currBranch", getCurrBranches());
                }
                paymentsDl.setParameter("startDate", endDate.getValue());
                paymentsDl.setParameter("endDate", startDate.getValue());
                paymentsDl.load();
                expensesDl.setParameter("startDate", endDate.getValue());
                expensesDl.setParameter("endDate", startDate.getValue());
                expensesDl.load();
                if (branchSelect.getValue() != null) {
                    payChartsDl.setParameter("currBranch", branch);
                    expChartDl.setParameter("currBranch", branch);
                } else {
                    payChartsDl.setParameter("currBranch", getCurrBranches());
                    expensesDl.setParameter("currBranch", getCurrBranches());
                }
                payChartsDl.setParameter("startDate", endDate.getValue());
                payChartsDl.setParameter("endDate", startDate.getValue());
                payChartsDl.load();
                expChartDl.setParameter("startDate", endDate.getValue());
                expChartDl.setParameter("endDate", startDate.getValue());
                expChartDl.load();
            }
            if (reportsTabs.getSelectedTab().getName().equals("expensesTab")) {
                if (branchSelect.getValue() != null) {
                    expensesDl.setParameter("currBranch", branch);
                    paymentsDl.setParameter("currBranch", branch);
                } else {
                    expensesDl.setParameter("currBranch", getCurrBranches());
                    paymentsDl.setParameter("currBranch", getCurrBranches());
                }
                expensesDl.setParameter("startDate", endDate.getValue());
                expensesDl.setParameter("endDate", startDate.getValue());
                expensesDl.load();
                paymentsDl.setParameter("startDate", endDate.getValue());
                paymentsDl.setParameter("endDate", startDate.getValue());
                paymentsDl.load();
                if (branchSelect.getValue() != null) {
                    expChartDl.setParameter("currBranch", branch);
                    payChartsDl.setParameter("currBranch", branch);
                } else {
                    expChartDl.setParameter("currBranch", getCurrBranches());
                    payChartsDl.setParameter("currBranch", getCurrBranches());
                }
                expChartDl.setParameter("startDate", endDate.getValue());
                expChartDl.setParameter("endDate", startDate.getValue());
                expChartDl.load();
                payChartsDl.setParameter("startDate", endDate.getValue());
                payChartsDl.setParameter("endDate", startDate.getValue());
                payChartsDl.load();
            }
            if (reportsTabs.getSelectedTab().getName().equals("studentsTab")) {
                if (branchSelect.getValue() != null) {
                    studentsesDl.setParameter("currBranch", branch);
                } else {
                    studentsesDl.setParameter("currBranch", getCurrBranches());
                }
                studentsesDl.setParameter("startDate", endDate.getValue());
                studentsesDl.setParameter("endDate", startDate.getValue());
                studentsesDl.load();
                if (branchSelect.getValue() != null) {
                    studChartsDl.setParameter("currBranch", branch);
                } else {
                    studChartsDl.setParameter("currBranch", getCurrBranches());
                }
                studChartsDl.setParameter("startDate", endDate.getValue());
                studChartsDl.setParameter("endDate", startDate.getValue());
                studChartsDl.load();
            }
            if (reportsTabs.getSelectedTab().getName().equals("booksTab")) {
                if (branchSelect.getValue() != null) {
                    booksDl.setParameter("currBranch", branch);
                } else {
                    booksDl.setParameter("currBranch", getCurrBranches());
                }
                booksDl.setParameter("startDate", endDate.getValue());
                booksDl.setParameter("endDate", startDate.getValue());
                booksDl.load();
                if (branchSelect.getValue() != null) {
                    bookChartDl.setParameter("currBranch", branch);
                } else {
                    bookChartDl.setParameter("currBranch", getCurrBranches());
                }
                bookChartDl.setParameter("startDate", endDate.getValue());
                bookChartDl.setParameter("endDate", startDate.getValue());
                bookChartDl.load();
            }
            if (reportsTabs.getSelectedTab().getName().equals("tabelTab")) {
                if (branchSelect.getValue() != null) {
                    coursesesDl.setParameter("currBranch", branch);
                } else {
                    coursesesDl.setParameter("currBranch", getCurrBranches());
                }
                coursesesDl.setParameter("startDate", LocalDateTime.of(endDate.getValue(), LocalTime.MIN));
                coursesesDl.setParameter("endDate", LocalDateTime.of(startDate.getValue(), LocalTime.MAX));
                coursesesDl.load();
                coursesesDc.getMutableItems().forEach(course -> {
                    LocalDateTime courseStartDate = course.getCourseStartDate();
                    LocalDate startDate = courseStartDate.toLocalDate();
                    course.setCourseStartDate(startDate.atStartOfDay());
                });
            }
        } else {
            CreateNotification("Please, fill the date fields or take period", "Show action");
        }
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

    @Subscribe("refreshBalance")
    public void onRefreshBalance(Action.ActionPerformedEvent event) {
        balanceDl.load();
    }

    @Subscribe("exportBtnBal")
    public void onExportBtnBal(Action.ActionPerformedEvent event) {
        extensionBal.exportTableToXls();
        extensionBal.setFileName("Balance_Info");
    }

    @Subscribe("exportBtnPay")
    public void onExportBtnPay(Action.ActionPerformedEvent event) {
        extensionPay.exportTableToXls();
        extensionPay.setFileName("Payment_All_Info");
    }

    @Subscribe("exportBtnExp")
    public void onExportBtnExp(Action.ActionPerformedEvent event) {
        extensionExp.exportTableToXls();
        extensionExp.setFileName("Expense_All_Info");
    }

    @Subscribe("exportBtnStud")
    public void onExportBtnStud(Action.ActionPerformedEvent event) {
        extensionStud.exportTableToXls();
        extensionStud.setFileName("Students_All_Info");
    }

    @Subscribe("exportBtnBook")
    public void onExportBtnBook(Action.ActionPerformedEvent event) {
        extensionBook.exportTableToXls();
        extensionBook.setFileName("Book_All_Info");
    }

    @Subscribe("exportTabelPivotBtn")
    public void onExportTabelPivotBtnClick(Button.ClickEvent event) {
        extensionTabel.exportTableToXls();
        extensionTabel.setFileName("Tabel_All_Info");
    }
}