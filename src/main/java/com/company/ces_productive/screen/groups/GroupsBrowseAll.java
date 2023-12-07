package com.company.ces_productive.screen.groups;

import com.company.ces_productive.app.GetCourseRealAmount;
import com.company.ces_productive.entity.Students;
import com.company.ces_productive.entity.courses.Courses;
import io.jmix.core.DataManager;
import io.jmix.core.SaveContext;
import io.jmix.ui.Notifications;
import io.jmix.ui.action.Action;
import io.jmix.ui.component.Button;
import io.jmix.ui.component.GroupTable;
import io.jmix.ui.component.Table;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.model.CollectionPropertyContainer;
import io.jmix.ui.screen.*;
import com.company.ces_productive.entity.Groups;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@UiController("CES_Groups.browseAll")
@UiDescriptor("groups-browseAll.xml")
@LookupComponent("groupsesTable")
public class GroupsBrowseAll extends StandardLookup<Groups> {
    @Autowired
    private DataManager dataManager;
    @Autowired
    private GroupTable<Students> studentsesTable;
    @Autowired
    private CollectionLoader<Groups> groupsesDl;
    @Autowired
    private Notifications notifications;
    @Autowired
    private MessageBundle messageBundle;
    @Autowired
    private Button studentsesTableCalcCourseAmountBtn;

    @Subscribe("studentsesTable")
    public void onStudentsesTableSelection(final Table.SelectionEvent<Students> event) {
        if (studentsesTable.getSingleSelected() != null) {
            studentsesTableCalcCourseAmountBtn.setEnabled(true);
        } else {
            studentsesTableCalcCourseAmountBtn.setEnabled(false);
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
            groupsesDl.load();
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