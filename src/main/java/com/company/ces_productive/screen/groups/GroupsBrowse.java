package com.company.ces_productive.screen.groups;

import com.company.ces_productive.app.GetCourseRealAmount;
import com.company.ces_productive.entity.*;
import com.company.ces_productive.entity.courses.Courses;
import io.jmix.core.DataManager;
import io.jmix.core.SaveContext;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.ui.Notifications;
import io.jmix.ui.action.Action;
import io.jmix.ui.component.Button;
import io.jmix.ui.component.GroupTable;
import io.jmix.ui.component.Table;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@UiController("CES_Groups.browse")
@UiDescriptor("groups-browse.xml")
@LookupComponent("groupsesTable")
public class GroupsBrowse extends StandardLookup<Groups> {
    @Autowired
    private CurrentAuthentication currentAuthentication;
    @Autowired
    private CollectionLoader<Groups> groupsesDl;
    @Autowired
    private GroupTable<Groups> groupsesTable;
    @Autowired
    private Notifications notifications;
    @Autowired
    private MessageBundle messageBundle;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private Button closeBtn;
    @Autowired
    private GroupTable<Students> studentsesTable;
    @Autowired
    private Button studentsesTableCalcCourseAmountBtn;

    @Subscribe
    public void onInit(final InitEvent event) {
        groupsesDl.setParameter("currUserBranch", ((User) currentAuthentication.getUser()).getBranch());
        groupsesDl.setParameter("status", GroupStatus.OPEN);
        groupsesDl.load();
    }

    @Subscribe("studentsesTable")
    public void onStudentsesTableSelection(final Table.SelectionEvent<Students> event) {
        if (studentsesTable.getSingleSelected() != null) {
            studentsesTableCalcCourseAmountBtn.setEnabled(true);
        } else {
            studentsesTableCalcCourseAmountBtn.setEnabled(false);
        }
    }

    @Subscribe("groupsesTable.close")
    public void onGroupsesTableClose(Action.ActionPerformedEvent event) {
        Groups group = groupsesTable.getSingleSelected();
        if (group != null) {
            List<Courses> courses = group.getGroupCourse();
            List<Courses> newCourses = new ArrayList<>();
            for (Courses course : courses) {
                if (course.getCourseStatus() == CourseStatus.NEW) {
                    newCourses.add(course);
                }
            }
            if (group.getGroupStudents().isEmpty() && newCourses.isEmpty()) {
                group.setGroupStatus(GroupStatus.OVERDUE);
                dataManager.save(group);
                groupsesDl.load();
            } else {
                CreateNotification("Необходимо удалить все действующие расписания и/или удалить всех студентов из группы", "Stop");
            }
        } else {
            CreateNotification("Для начала необходимо выбрать группу для удаления", "Select");
        }
    }
    @Subscribe("groupsesTable")
    public void onGroupsesTableSelection(final Table.SelectionEvent<Groups> event) {
        closeBtn.setEnabled(true);
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
            groupsesDl.load();
        } else {
            CreateNotification("Выберите студента, по которому необходимо расчитать стоимость занятии за сегодня", "Calculate action");
        }
    }
    private void CreateNotification(String messageKey, String actionName) {
        message(messageKey, actionName);
    }

    private void message(String messageKey, String actionName) {
        notifications.create(Notifications.NotificationType.ERROR)
                .withCaption("Ошибка закрытия группы")
                .withDescription(messageBundle.formatMessage(messageKey, actionName))
                .show();
    }
}