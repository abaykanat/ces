package com.company.ces_productive.screen.user;

import com.company.ces_productive.entity.PasswordResetToken;
import com.company.ces_productive.entity.User;
import com.company.ces_productive.security.HrRole;
import io.jmix.core.DataManager;
import io.jmix.email.EmailException;
import io.jmix.email.EmailInfo;
import io.jmix.email.EmailInfoBuilder;
import io.jmix.email.Emailer;
import io.jmix.ui.Notifications;
import io.jmix.ui.action.Action;
import io.jmix.ui.component.Button;
import io.jmix.ui.component.GroupTable;
import io.jmix.ui.component.Table;
import io.jmix.ui.navigation.Route;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@UiController("CES_User.browse")
@UiDescriptor("user-browse.xml")
@LookupComponent("usersTable")
@Route("users")
public class UserBrowse extends StandardLookup<User> {

    @Autowired
    private Emailer emailer;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private GroupTable<User> usersTable;
    @Autowired
    private Notifications notifications;
    @Autowired
    private Button usersTableSendPasswordBtn;

    private void sendByEmail(String email, String link) throws EmailException {
        EmailInfo emailInfo = EmailInfoBuilder.create()
                .setAddresses(email)
                .setSubject("Установка пароля для app.ceskz.com")
                .setFrom("appceskz@gmail.com")
                .setBody("Необходимо пройти по следующей ссылке и установить новый пароль: https://app.ceskz.com/#anonymous/password-change?id="+link)
                .build();
        emailer.sendEmail(emailInfo);
    }

    private String generateToken() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    @Subscribe("usersTable")
    public void onUsersTableSelection(final Table.SelectionEvent<User> event) {
        if (usersTable.getSingleSelected() != null) {
            usersTableSendPasswordBtn.setEnabled(true);
        } else {
            usersTableSendPasswordBtn.setEnabled(false);
        }
    }

    @Subscribe("usersTable.sendPassword")
    public void onUsersTableSendPassword(final Action.ActionPerformedEvent event) {
        User user = usersTable.getSingleSelected();
        if (user != null) {
            String newToken = generateToken();
            PasswordResetToken resetToken = dataManager.create(PasswordResetToken.class);
            resetToken.setUserId(user);
            resetToken.setToken(newToken);
            resetToken.setExpirationDate(LocalDateTime.now().plusHours(1));
            dataManager.save(resetToken);
            try {
                sendByEmail(user.getEmail(), newToken);
            } catch (EmailException e) {
                throw new RuntimeException("Email sent error:", e);
            }
        } else {
            notifications.create(Notifications.NotificationType.ERROR)
                    .withCaption("Ошибка выбора")
                    .withDescription("Выберите пользователя из списка для отправки ссылки")
                    .show();
        }
    }
}