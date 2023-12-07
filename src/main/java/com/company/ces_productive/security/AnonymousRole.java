package com.company.ces_productive.security;

import com.company.ces_productive.entity.*;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.securityui.role.annotation.MenuPolicy;
import io.jmix.securityui.role.annotation.ScreenPolicy;

@ResourceRole(name = "AnonymousRole", code = "anonymous-role")
public interface AnonymousRole {

    String CODE = "anonymous-role";

    @MenuPolicy(menuIds = {"CES_PasstokenresetScreen", "CES_PasstokenchangeScreen", "CES_MainScreenAnonymous", "CES_RegistrationScreen"})
    @ScreenPolicy(screenIds = {"CES_PasstokenresetScreen", "CES_MainScreenAnonymous", "CES_PasstokenchangeScreen", "CES_RegistrationScreen", "CES_Parents.edit", "CES_Students.edit"})
    void screens();

    @EntityAttributePolicy(entityClass = User.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = User.class, actions = {EntityPolicyAction.UPDATE, EntityPolicyAction.READ})
    void user();

    @EntityAttributePolicy(entityClass = PasswordResetToken.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = PasswordResetToken.class, actions = EntityPolicyAction.ALL)
    void passwordResetToken();

    @EntityPolicy(entityClass = Students.class, actions = EntityPolicyAction.CREATE)
    void students();

    @EntityPolicy(entityClass = Parents.class, actions = EntityPolicyAction.CREATE)
    void parents();

    @EntityAttributePolicy(entityClass = Branches.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Branches.class, actions = EntityPolicyAction.READ)
    void branches();
}