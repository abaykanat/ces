package com.company.ces_productive.screen.students;

import com.company.ces_productive.app.DocNumbGenerate;
import com.company.ces_productive.app.GetCourseRealAmount;
import com.company.ces_productive.entity.*;
import com.company.ces_productive.entity.courses.Courses;
import com.company.ces_productive.screen.freeze.FreezeEdit;
import com.company.ces_productive.screen.orders.OrdersBook;
import com.company.ces_productive.screen.payments.PaymentsEdit;
import com.company.ces_productive.screen.stopped.StoppedEdit;
import com.haulmont.yarg.reporting.ReportOutputDocument;
import io.jmix.core.DataManager;
import io.jmix.core.SaveContext;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.reports.runner.ReportRunner;
import io.jmix.ui.Notifications;
import io.jmix.ui.ScreenBuilders;
import io.jmix.ui.action.Action;
import io.jmix.ui.component.Button;
import io.jmix.ui.component.GroupTable;
import io.jmix.ui.component.Table;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@UiController("CES_Students.browseManager")
@UiDescriptor("students-browseManager.xml")
@LookupComponent("studentsesTable")
public class StudentsBrowseManager extends StandardLookup<Students> {
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
    private CurrentAuthentication currentAuthentication;
    @Autowired
    private CollectionLoader<Students> studentsesDl;
    @Autowired
    private ReportRunner reportRunner;
    @Autowired
    private Button studentsesTableRunReportBtn;
    @Autowired
    private Button studentsTablePaymentBtn;
    @Autowired
    private Button studentsesTableBookBtn;
    @Autowired
    private Button studentsesTableFreezeBtn;
    @Autowired
    private Button studentsesTableStoppedBtn;
    @Autowired
    private Button studentsesTableCalcCourseAmountBtn;
    @Autowired
    private DataManager dataManager;

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        studentsesDl.setParameter("currUserBranch", ((User) currentAuthentication.getUser()).getBranch());
        studentsesDl.load();
    }

    @Subscribe("studentsesTable")
    public void onStudentsesTableSelection(Table.SelectionEvent<Students> event) {
        if (studentsesTable.getSingleSelected() != null) {
            studentsesTableRunReportBtn.setEnabled(true);
            studentsTablePaymentBtn.setEnabled(true);
            studentsesTableBookBtn.setEnabled(true);
            studentsesTableFreezeBtn.setEnabled(true);
            studentsesTableStoppedBtn.setEnabled(true);
            studentsesTableRunReportBtn.setEnabled(true);
            studentsesTableCalcCourseAmountBtn.setEnabled(true);
        } else {
            studentsesTableRunReportBtn.setEnabled(false);
            studentsTablePaymentBtn.setEnabled(false);
            studentsesTableBookBtn.setEnabled(false);
            studentsesTableFreezeBtn.setEnabled(false);
            studentsesTableStoppedBtn.setEnabled(false);
            studentsesTableRunReportBtn.setEnabled(false);
            studentsesTableCalcCourseAmountBtn.setEnabled(false);
        }
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
        } else {CreateNotification("Выберите студента из списка для осуществления платежа", "Payment action");}
    }

    @Subscribe("studentsesTable.book")
    public void onStudentsesTableBook(Action.ActionPerformedEvent event) {
        Students student = studentsesTable.getSingleSelected();
        if (student != null) {
            if (student.getStudGroups() != null) {
                screenBuilders.editor(Orders.class, this)
                        .newEntity()
                        .withInitializer(orders -> {
                            String branchCode = student.getStudBranch().getBranchCode();
                            orders.setOrderNumber(docNumbGenerate.getNextNumber("ORDBOOK", branchCode));
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
            } else {CreateNotification("Добавьте студента в группу для получения требования", "Group action");}
        } else {CreateNotification("Выберите студента из списка для продажи книги", "Book action");}
    }

    @Subscribe("studentsesTable.freeze")
    public void onStudentsesTableFreeze(Action.ActionPerformedEvent event) {
        Students student = studentsesTable.getSingleSelected();
        if (student != null) {
            Objects.requireNonNull(student).setStudStatus(StudentStatus.FREEZE);
            Screen freezeEditor = screenBuilders.editor(Freeze.class, this)
                    .newEntity()
                    .withInitializer(freeze -> {
                        freeze.setFreezeStartDate(LocalDate.now());
                        freeze.setFreezeStudent(student);
                    })
                    .withScreenClass(FreezeEdit.class)
                    .withOpenMode(OpenMode.DIALOG)
                    .build();
            freezeEditor.addAfterCloseListener(afterCloseEvent -> {
                if (afterCloseEvent.closedWith(StandardOutcome.COMMIT)) {
                    getScreenData().loadAll();
                }
            });
            freezeEditor.show();
        } else {CreateNotification("Выберите студента обучение которого хотите приостановить", "Freeze action");}
    }

    @Subscribe("studentsesTable.stopped")
    public void onStudentsesTableStopped(Action.ActionPerformedEvent event) {
        Students student = studentsesTable.getSingleSelected();
        if (student != null) {
            Objects.requireNonNull(student).setStudStatus(StudentStatus.STOPPED);
            Screen stoppedEditor = screenBuilders.editor(Stopped.class, this)
                    .newEntity()
                    .withInitializer(stopped -> stopped.setStoppedStudent(student))
                    .withScreenClass(StoppedEdit.class)
                    .withOpenMode(OpenMode.DIALOG)
                    .build();
            stoppedEditor.addAfterCloseListener(afterCloseEvent -> {
                if (afterCloseEvent.closedWith(StandardOutcome.COMMIT)) {
                    getScreenData().loadAll();
                }
            });
            stoppedEditor.show();
        } else {CreateNotification("Выберите студента, который хочет остановить обучение", "Stopped action");}
    }

    @Subscribe("studentsesTable.runReport")
    public void onStudentsesTableRunReport(Action.ActionPerformedEvent event) {
        Students student = studentsesTable.getSingleSelected();
        ReportOutputDocument document = reportRunner.byReportCode("GeneralAgreement")
                .addParam("Entities", student)
                .withTemplateCode("GENERAL_AGREEMENT")
                .run();
    }

    @Subscribe("studentsesTable.calcCourseAmount")
    public void onStudentsesTableCalcCourseAmount(final Action.ActionPerformedEvent event) {
        Students student = studentsesTable.getSingleSelected();
        if (student != null) {
            List<Groups> groups = student.getStudGroups();
            if (groups.isEmpty()) {
                student.setStudPeriodDesc("Отсутствует группа");
            }
            SaveContext saveContext = new SaveContext().setDiscardSaved(true);
            List<String> studentDescriptions = new ArrayList<>();
            boolean zeroCourseAmount = false;
            int groupCount = 0;
            Set<String> uniqueCourseNames = new HashSet<>();
            for (Groups group : groups) {
                List<Courses> courses = group.getGroupCourse();
                Map<String, List<Courses>> courseNames = courses.stream()
                        .filter(course -> course.getCourseCost().compareTo(BigDecimal.ZERO) > 0)
                        .collect(Collectors.groupingBy(Courses::getCourseName));
                List<String> courseNamesLists = courseNames.keySet().stream().toList();

                for (String courseNamesList : courseNamesLists) {
                    BigDecimal realCourseAmount = GetCourseRealAmount.getCourseRealAmount(student, group, courseNamesList).getAmount();
                    String realCourseReason = GetCourseRealAmount.getCourseRealAmount(student, group, courseNamesList).getReason();
                    if (realCourseAmount.compareTo(BigDecimal.ZERO) > 0) {
                        studentDescriptions.add("По группе " + group.getGroupName() + "| и расписанию " + courseNamesList + "| стоимость занятия = " + realCourseAmount + " тенге");
                        student.setStudPeriodDesc("Корректно");
                        uniqueCourseNames.add(courseNamesList);
                    } else {
                        studentDescriptions.add("По группе " + group.getGroupName() + "| и расписанию " + courseNamesList + "| не расчитана сумма занятия. " + realCourseReason);
                        student.setStudPeriodDesc(realCourseReason);
                        zeroCourseAmount = true;
                    }
                }
                groupCount++;
            }
            if (groupCount >= 2 && zeroCourseAmount) {
                student.setStudPeriodDesc("По одной из групп студента имеется ошибка. Детальная информация в карточке студента");
            }
            if (uniqueCourseNames.size() >= 2) {
                student.setStudPeriodDesc("По одной из групп студента имеется дублирующее расписание");
            }
            student.setStudDescription(String.join("\n", studentDescriptions));
            saveContext.saving(student);
            dataManager.save(saveContext);
            studentsesDl.load();
        } else {
            CreateNotification("Выберите студента, по которому необходимо расчитать стоимость занятии за сегодня", "Calculate action");
        }
    }
    private void  CreateNotification (String messageKey, String actionName) {
        message(messageKey, actionName);
    }
    private void message(String messageKey, String actionName){
        notifications.create(Notifications.NotificationType.ERROR)
                .withCaption("Ошибка выбора")
                .withDescription(messageBundle.formatMessage(messageKey, actionName))
                .show();
    }
}