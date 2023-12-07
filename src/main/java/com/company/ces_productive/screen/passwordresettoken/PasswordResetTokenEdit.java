package com.company.ces_productive.screen.passwordresettoken;

import io.jmix.ui.navigation.Route;
import io.jmix.ui.screen.*;
import com.company.ces_productive.entity.PasswordResetToken;

@UiController("CES_PasswordResetToken.edit")
@UiDescriptor("password-reset-token-edit.xml")
@EditedEntityContainer("passwordResetTokenDc")
@Route(path = "pass-reset")
public class PasswordResetTokenEdit extends StandardEditor<PasswordResetToken> {
}