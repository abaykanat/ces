package com.company.ces_productive.screen.passtokenreset;

import com.company.ces_productive.entity.PasswordResetToken;
import com.company.ces_productive.entity.User;
import io.jmix.core.DataManager;
import io.jmix.email.EmailException;
import io.jmix.email.EmailInfo;
import io.jmix.email.EmailInfoBuilder;
import io.jmix.email.Emailer;
import io.jmix.ui.action.Action;
import io.jmix.ui.component.Button;
import io.jmix.ui.component.Label;
import io.jmix.ui.component.TextField;

import io.jmix.ui.navigation.Route;
import io.jmix.ui.navigation.UrlRouting;
import io.jmix.ui.screen.Screen;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.UUID;


@UiController("CES_PasstokenresetScreen")
@UiDescriptor("passTokenReset-screen.xml")
@Route(path = "password-reset")
public class PassTokenResetScreen extends Screen {
    @Autowired
    private TextField<String> emailField;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private Label<String> errorLabel;
    @Autowired
    private Label<String> successLabel;
    @Autowired
    private Button emailBtn;
    @Autowired
    private Emailer emailer;
    @Autowired
    private UrlRouting urlRouting;

    private void sendByEmail(String email, String link) throws EmailException {
        EmailInfo emailInfo = EmailInfoBuilder.create()
                .setAddresses(email)
                .setSubject("Установка пароля для app.ceskz.com")
                .setFrom("appceskz@gmail.com")
                .setBody("Необходимо пройти по следующей ссылке и установить новый пароль: https://app.ceskz.com/#anonymous/password-change?id="+link)
                .build();
        emailer.sendEmail(emailInfo);
    }

    public User findUserByEmail(String inputEmail) {
        return dataManager.load(User.class)
                .query("SELECT u FROM CES_User u WHERE u.email = :userEmail")
                .parameter("userEmail", inputEmail)
                .optional()
                .orElse(null);
    }

    private String generateToken() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    @Subscribe("email")
    public void onEmail(Action.ActionPerformedEvent event) {
        User user = findUserByEmail(emailField.getValue());
        if (user != null) {
            PasswordResetToken resetToken = dataManager.create(PasswordResetToken.class);
            resetToken.setUserId(user);
            resetToken.setToken(generateToken());
            resetToken.setExpirationDate(LocalDateTime.now().plusHours(1));
            dataManager.save(resetToken);
            String newLink = urlRouting.getRouteGenerator().getEditorRoute(resetToken);
            int equalsIndex = newLink.indexOf("=");
            String userLink = newLink.substring(equalsIndex + 1);
            resetToken.setToken(userLink);
            dataManager.save(resetToken);
            emailBtn.setEnabled(false);
            successLabel.setVisible(true);
            errorLabel.setVisible(false);
            try {
                sendByEmail(user.getEmail(), userLink);
            } catch (EmailException e) {
                throw new RuntimeException("Email sent error:", e);
            }
        } else {
            errorLabel.setVisible(true);
        }
    }
}