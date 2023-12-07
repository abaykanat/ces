package com.company.ces_productive.screen.registration;

import com.company.ces_productive.entity.*;
import com.company.ces_productive.screen.parents.ParentsEdit;
import com.company.ces_productive.screen.students.StudentsEdit;
import io.jmix.ui.Notifications;
import io.jmix.ui.ScreenBuilders;
import io.jmix.ui.component.Button;
import io.jmix.ui.component.EntityComboBox;
import io.jmix.ui.component.RadioButtonGroup;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.navigation.Route;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

@UiController("CES_RegistrationScreen")
@UiDescriptor("registration-screen.xml")
@Route(path = "registration")
public class RegistrationScreen extends Screen {
    @Autowired
    private RadioButtonGroup<RegistrationMode> registrationMode;
    @Autowired
    private ScreenBuilders screenBuilders;
    @Autowired
    private EntityComboBox<Branches> selectBranch;
    @Autowired
    private Notifications notifications;
    @Autowired
    private CollectionLoader<Branches> branchesesDl;

    @Subscribe
    public void onInit(final InitEvent event) {
        registrationMode.setValue(RegistrationMode.PARENT);
        branchesesDl.load();
    }

    @Subscribe("registrationStartBtn")
    public void onRegistrationStartBtnClick(final Button.ClickEvent event) {
        if (selectBranch.getValue() != null) {
            if (registrationMode.getValue() == RegistrationMode.PARENT) {
                Screen parentScreen = screenBuilders.editor(Parents.class, this)
                        .newEntity()
                        .withScreenClass(ParentsEdit.class)
                        .withOpenMode(OpenMode.DIALOG)
                        .withInitializer(parents -> {
                            parents.setParentBranch(selectBranch.getValue());
                        })
                        .build()
                        .show();
                parentScreen.addAfterCloseListener(afterCloseEvent -> {
                    if (afterCloseEvent.closedWith(StandardOutcome.COMMIT)) {
                        notifications.create(Notifications.NotificationType.HUMANIZED)
                                .withCaption("Успешно!")
                                .withDescription("Вы успшено прошли регистрацию")
                                .show();
                    }
                });
            } else {
                Screen studentScreen = screenBuilders.editor(Students.class, this)
                        .newEntity()
                        .withScreenClass(StudentsEdit.class)
                        .withOpenMode(OpenMode.DIALOG)
                        .withInitializer(students -> {
                            students.setStudOrderPeriod(LocalDate.now());
                            students.setStudStatus(StudentStatus.NEW);
                            students.setStudBranch(selectBranch.getValue());
                        })
                        .build()
                        .show();
                studentScreen.addAfterCloseListener(afterCloseEvent -> {
                    if (afterCloseEvent.closedWith(StandardOutcome.COMMIT)) {
                        notifications.create(Notifications.NotificationType.HUMANIZED)
                                .withCaption("Успешно!")
                                .withDescription("Вы успшено прошли регистрацию")
                                .show();
                    }
                });
            }
        } else {
            notifications.create(Notifications.NotificationType.ERROR)
                    .withCaption("Не выбран филиал")
                    .withDescription("Необходимо выбрать филиал перед началом регистрации")
                    .show();
        }
    }
}