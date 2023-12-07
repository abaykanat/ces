package com.company.ces_productive.screen.passwordresettoken;

import io.jmix.ui.screen.*;
import com.company.ces_productive.entity.PasswordResetToken;

@UiController("CES_PasswordResetToken.browse")
@UiDescriptor("password-reset-token-browse.xml")
@LookupComponent("passwordResetTokensTable")
public class PasswordResetTokenBrowse extends StandardLookup<PasswordResetToken> {
}