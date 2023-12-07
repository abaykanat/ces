package com.company.ces_productive.screen.passtokenchange;

import com.company.ces_productive.entity.PasswordResetToken;
import com.company.ces_productive.entity.User;
import io.jmix.core.DataManager;
import io.jmix.ui.action.Action;
import io.jmix.ui.component.Button;
import io.jmix.ui.component.Label;
import io.jmix.ui.component.TextField;
import io.jmix.ui.navigation.NavigationState;
import io.jmix.ui.navigation.Route;
import io.jmix.ui.navigation.UrlRouting;
import io.jmix.ui.screen.Screen;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import java.util.Objects;


@UiController("CES_PasstokenchangeScreen")
@UiDescriptor("passTokenChange-screen.xml")
@Route(path = "password-change")
public class PassTokenChangeScreen extends Screen {
    @Autowired
    private Button changePassBtn;
    @Autowired
    private Label<String> errorLabel;
    @Autowired
    private Label<String> successLabel;
    @Autowired
    private TextField<String> newPassField;
    @Autowired
    private TextField<String> newPassReturnField;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UrlRouting urlRouting;
    @Autowired
    private Button getUrlBtn;

    public String getCurrentUrlToken () {
        NavigationState linkToken = urlRouting.getState();
        String fullToken = linkToken.getParamsString();
        int equalsIndex = fullToken.indexOf("=");
        return fullToken.substring(equalsIndex + 1);
    }

    public PasswordResetToken findTokenByTokenValue(String token) {
        return dataManager.load(PasswordResetToken.class)
                .query("SELECT t FROM CES_PasswordResetToken t WHERE t.token = :token")
                .parameter("token", token)
                .optional()
                .orElse(null);
    }

    private void showTokenError() {
        showError("Неправильная ссылка или токен истек");
        changePassBtn.setEnabled(false);
    }

    private void showError(String message) {
        errorLabel.setValue(message);
        errorLabel.setVisible(true);
    }

    private void showSuccess() {
        successLabel.setValue("Пароль успешно изменен");
        successLabel.setVisible(true);
        changePassBtn.setEnabled(false);
    }

    public void deleteToken(PasswordResetToken token) {
        dataManager.remove(token);
    }
    @Subscribe("getUrlAction")
    public void ongetUrlAction(final Action.ActionPerformedEvent event) {
        String token = getCurrentUrlToken();
        PasswordResetToken resetToken = findTokenByTokenValue(token);
        if (resetToken == null || resetToken.getExpirationDate().isBefore(LocalDateTime.now())) {
            showTokenError();
            getUrlBtn.setEnabled(false);
        } else {
            getUrlBtn.setVisible(false);
            newPassField.setVisible(true);
            newPassReturnField.setVisible(true);
            changePassBtn.setVisible(true);
        }
    }

    @Subscribe("changePassAction")
    public void onChangePassAction(final Action.ActionPerformedEvent event) {
        String token = getCurrentUrlToken();
        PasswordResetToken resetToken = findTokenByTokenValue(token);
        if (resetToken == null || resetToken.getExpirationDate().isBefore(LocalDateTime.now())) {
            showTokenError();
            return;
        }
        String newPassword = newPassField.getValue();
        String confirmPassword = newPassReturnField.getValue();
        if (!Objects.equals(newPassword, confirmPassword)) {
            showError("Пароли не совпадают");
            return;
        }
        User user = resetToken.getUserId();
        user.setPassword(passwordEncoder.encode(newPassword));
        dataManager.save(user);
        deleteToken(resetToken);
        showSuccess();
    }
}