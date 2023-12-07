package com.company.ces_productive.screen.students;

import com.company.ces_productive.app.GetCourseRealAmount;
import com.company.ces_productive.entity.*;
import com.company.ces_productive.entity.courses.Courses;
import io.jmix.core.DataManager;
import io.jmix.core.SaveContext;
import io.jmix.ui.Dialogs;
import io.jmix.ui.Notifications;
import io.jmix.ui.ScreenBuilders;
import io.jmix.ui.UiComponents;
import io.jmix.ui.action.Action;
import io.jmix.ui.app.inputdialog.DialogActions;
import io.jmix.ui.app.inputdialog.DialogOutcome;
import io.jmix.ui.app.inputdialog.InputParameter;
import io.jmix.ui.component.Button;
import io.jmix.ui.component.ComboBox;
import io.jmix.ui.component.GroupTable;
import io.jmix.ui.component.Table;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@UiController("CES_Students.browseAll")
@UiDescriptor("students-browseAll.xml")
@LookupComponent("studentsesTable")
public class StudentsBrowseAll extends StandardLookup<Students> {
    @Autowired
    private GroupTable<Students> studentsesTable;
    @Autowired
    private ScreenBuilders screenBuilders;
    @Autowired
    private Notifications notifications;
    @Autowired
    private MessageBundle messageBundle;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private Dialogs dialogs;
    @Autowired
    private UiComponents uiComponents;
    @Autowired
    private Button studentsesTableChangeBranchBtn;
    @Autowired
    private Button studentsesTableChangeBalanceBtn;
    @Autowired
    private CollectionLoader<Students> studentsesDl;
    @Autowired
    private Button studentsesTableCalcCourseAmountBtn;

    @Subscribe("studentsesTable")
    public void onStudentsesTableSelection(final Table.SelectionEvent<Students> event) {
        if (studentsesTable.getSingleSelected() != null) {
            studentsesTableChangeBranchBtn.setEnabled(true);
            studentsesTableChangeBalanceBtn.setEnabled(true);
            studentsesTableCalcCourseAmountBtn.setEnabled(true);
        } else {
            studentsesTableChangeBranchBtn.setEnabled(false);
            studentsesTableChangeBalanceBtn.setEnabled(false);
            studentsesTableCalcCourseAmountBtn.setEnabled(false);
        }
    }

    public BigDecimal getCourseRealAmount(Groups group, Students student) {
        List<Courses> courses = group.getGroupCourse().stream()
                .filter(course -> course.getCourseStatus().equals(CourseStatus.NEW))
                .toList();
        List<Orders> paidOrders = student.getStudOrders().stream()
                .filter(orders -> orders.getOrderGroup().getId().equals(group.getId()) && orders.getOrderStatus() == OrderStatus.PAID && !orders.getOrderNumber().contains("ORDDIF"))
                .toList();
        int orderCount = paidOrders.size()-1;
        if (orderCount == -1) {
            orderCount = 0;
        }
        for (PaymentParam paymentParam : student.getStudPayParam()) {
            LocalDate payDayDate = paymentParam.getPayParamPayDay().minusMonths(orderCount);
            LocalDate plusPayDayDate = payDayDate.plusDays(7);
            if (!plusPayDayDate.isBefore(LocalDate.now().minusMonths(1)) && !payDayDate.isAfter(LocalDate.now().plusMonths(1))) {
                if (paymentParam.getPayParamGroups().getId().equals(group.getId())) {
                    BigDecimal amount = calculateRealAmount(orderCount, group, courses, paymentParam.getPayParamPayDay());
                    if (amount != null) {
                        return amount;
                    }
                }
            }
        }
        return BigDecimal.ZERO;
    }
    private BigDecimal calculateRealAmount(int orderCount, Groups group, List<Courses> courses, LocalDate baseDate) {
        LocalDate payDate = baseDate.minusMonths(orderCount);
        LocalDate newDate = payDate.plusMonths(1);
        BigDecimal directionAmount = group.getGroupDirection().getDirectionMinCost();
        Optional<Courses> minDateCourses = courses.stream()
                .min(Comparator.comparing(Courses::getCourseStartDate));
        if (minDateCourses.isPresent()) {
            Courses minDateCourse = minDateCourses.get();
            LocalDate minDate = minDateCourse.getCourseStartDate().toLocalDate();
            if (payDate.equals(minDate) || payDate.isAfter(minDate)) {
                List<Courses> filteredCourses = courses.stream()
                        .filter(course -> (course.getCourseStartDate().isEqual(payDate.atStartOfDay()) || course.getCourseStartDate().isAfter(payDate.atStartOfDay()))
                                && course.getCourseStartDate().isBefore(newDate.atStartOfDay()))
                        .toList();
                int courseCount = filteredCourses.size();
                if (courseCount == 0) {
                    return null;
                }
                return directionAmount.divide(BigDecimal.valueOf(courseCount), RoundingMode.UP);
            } else if (payDate.isBefore(minDate)) {
                List<Courses> filteredCourses = courses.stream()
                        .filter(course -> (course.getCourseStartDate().isEqual(LocalDate.now().atStartOfDay()) || course.getCourseStartDate().isAfter(LocalDate.now().atStartOfDay()))
                                && course.getCourseStartDate().isBefore(LocalDate.now().atStartOfDay().plusMonths(1)))
                        .toList();
                int courseCount = filteredCourses.size();
                if (courseCount == 0) {
                    return null;
                }
                return directionAmount.divide(BigDecimal.valueOf(courseCount), RoundingMode.UP);
            }
        } else {
            return null;
        }
        return null;
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

    @Subscribe("studentsesTable.changeBranch")
    public void onStudentsesTableChangeBranch(final Action.ActionPerformedEvent event) {
        Students student = studentsesTable.getSingleSelected();
        if (student != null) {
            Screen studChangeBranchEditor = screenBuilders.editor(Students.class, this)
                    .editEntity(student)
                    .withScreenClass(StudOrderPeriodEdit.class)
                    .withOpenMode(OpenMode.DIALOG)
                    .build();
            studChangeBranchEditor.addAfterCloseListener(afterCloseEvent -> {
                if (afterCloseEvent.closedWith(StandardOutcome.COMMIT)) {
                    getScreenData().loadAll();
                }
            });
            studChangeBranchEditor.show();
        } else {
            CreateNotification("Выберите студента для которого хотите изменить филиал", "Branch action");
        }
    }

    @Subscribe("studentsesTable.changeBalance")
    public void onStudentsesTableChangeBalance(final Action.ActionPerformedEvent event) {
        Students student = studentsesTable.getSingleSelected();
        if (student != null) {
            dialogs.createInputDialog(this)
                    .withCaption("Изменение баланса студента")
                    .withParameters(
                            InputParameter.entityParameter("student", Students.class)
                                    .withCaption("Студент")
                                    .withDefaultValue(studentsesTable.getSingleSelected()),
                            InputParameter.parameter("group")
                                    .withField(()-> {
                                        ComboBox<Groups> field = uiComponents.create(ComboBox.of(Groups.class));
                                        field.setOptionsList(dataManager.load(Groups.class)
                                                .query("select g from CES_Groups g where g.groupStudents = :student")
                                                .parameter("student", studentsesTable.getSingleSelected())
                                                .list());
                                        field.setCaption("Группа");
                                        field.setWidthFull();
                                        return field;
                                    })
                                    .withDefaultValue(Objects.requireNonNull(studentsesTable.getSingleSelected()).getStudGroups()),
                            InputParameter.doubleParameter("quantity")
                                    .withCaption("Кол-во занятии")
                    )
                    .withActions(DialogActions.OK_CANCEL)
                    .withCloseListener(closeEvent -> {
                        if (closeEvent.closedWith(DialogOutcome.OK)) {
                            Double restQuantity = closeEvent.getValue("quantity");
                            Students selectStudent = closeEvent.getValue("student");
                            Groups selectGroup = closeEvent.getValue("group");
                            if (restQuantity == null || selectStudent == null || selectGroup == null) {
                                CreateNotification("Не все данные предоставлены", "Data Missing");
                                return;
                            }
                            Optional<Courses> course = Objects.requireNonNull(selectGroup).getGroupCourse().stream()
                                    .filter(courses -> courses.getCourseCost().compareTo(BigDecimal.ZERO)>0)
                                    .findAny();
                            if (course.isPresent()) {
                                BigDecimal visitAmount = getCourseRealAmount(selectGroup, student);
                                BigDecimal selectedAmount = visitAmount.multiply(BigDecimal.valueOf(restQuantity));
                                BigDecimal actualAmount = selectStudent.getStudActualAmount();
                                BigDecimal realAmount = actualAmount.add(selectedAmount);
                                selectStudent.setStudActualAmount(realAmount);
                                dataManager.save(selectStudent);
                            } else {
                                CreateNotification("По данной группе нет активных занятии. Невозможно рассчитать реальную сумму занятии", "Course Missing");
                            }
                        }
                    })
                    .show();
        } else {
            CreateNotification("Выберите студента по которому хотите изменить баланс", "Balance action");
        }
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
}