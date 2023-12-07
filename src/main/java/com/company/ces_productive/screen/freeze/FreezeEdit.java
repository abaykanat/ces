package com.company.ces_productive.screen.freeze;

import com.company.ces_productive.entity.StudentStatus;
import com.company.ces_productive.entity.Students;
import io.jmix.core.DataManager;
import io.jmix.ui.Dialogs;
import io.jmix.ui.Notifications;
import io.jmix.ui.action.Action;
import io.jmix.ui.action.DialogAction;
import io.jmix.ui.screen.*;
import com.company.ces_productive.entity.Freeze;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

@UiController("CES_Freeze.edit")
@UiDescriptor("freeze-edit.xml")
@EditedEntityContainer("freezeDc")
public class FreezeEdit extends StandardEditor<Freeze> {
    @Autowired
    private DataManager dataManager;
    @Autowired
    private Dialogs dialogs;
    @Autowired
    private Notifications notifications;

    @Subscribe("windowCommitAndClose")
    public void onWindowCommitAndClose(Action.ActionPerformedEvent event) {
        Students student = getEditedEntity().getFreezeStudent();
        student.setStudStatus(StudentStatus.FREEZE);
        dataManager.save(student);
    }

    @Subscribe
    public void onBeforeCommitChanges(BeforeCommitChangesEvent event) {
        Students student = getEditedEntity().getFreezeStudent();
        List<Freeze> freezes = student.getStudFreeze();
        if (!freezes.isEmpty()) {
            for (Freeze freeze : freezes) {
                LocalDate freezeEndDate = freeze.getFreezeEndDate();
                if (freezeEndDate.isAfter(LocalDate.now())) {
                    notifications.create(Notifications.NotificationType.ERROR)
                            .withCaption("Ошибка")
                            .withDescription("Обучение студента уже приостановлена. ")
                            .show();
                } else {
                    dialogs.createOptionDialog()
                            .withCaption("Подтверждение")
                            .withMessage("Вы точно хотите приостановить обучение по студенту:" + student.getStudLastName() + " " + student.getStudFirstName() + "?")
                            .withActions(
                                    new DialogAction(DialogAction.Type.OK).withHandler(e -> {
                                        event.resume();
                                    }),
                                    new DialogAction(DialogAction.Type.CANCEL)
                            )
                            .show();
                    event.preventCommit();
                }
            }
        } else {
            dialogs.createOptionDialog()
                    .withCaption("Подтверждение")
                    .withMessage("Вы точно хотите приостановить обучение по студенту:" + student.getStudLastName() + " " + student.getStudFirstName() + "?")
                    .withActions(
                            new DialogAction(DialogAction.Type.OK).withHandler(e -> {
                                event.resume();
                            }),
                            new DialogAction(DialogAction.Type.CANCEL)
                    )
                    .show();
            event.preventCommit();
        }
    }
}