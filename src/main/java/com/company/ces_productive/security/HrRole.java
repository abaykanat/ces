package com.company.ces_productive.security;

import com.company.ces_productive.entity.Branches;
import com.company.ces_productive.entity.Documentation;
import com.company.ces_productive.entity.PasswordResetToken;
import com.company.ces_productive.entity.User;
import io.jmix.audit.entity.UserSession;
import io.jmix.bpm.entity.UserModel;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.securitydata.entity.ResourceRoleEntity;
import io.jmix.securitydata.entity.RoleAssignmentEntity;
import io.jmix.securityui.model.ResourceRoleModel;
import io.jmix.securityui.role.annotation.MenuPolicy;
import io.jmix.securityui.role.annotation.ScreenPolicy;

import javax.annotation.Nonnull;

@Nonnull
@ResourceRole(name = "HrRole", code = "hr")
public interface HrRole {

    @MenuPolicy(menuIds = {"userSessions.browse", "CES_User.browse"})
    @ScreenPolicy(screenIds = {"userSessions.browse", "CES_User.browse", "sec_ResourceRoleModel.browse", "CES_User.edit", "sec_ResourceRoleModel.lookup", "sec_ResourceRoleModel.edit", "sec_RoleAssignmentFragment", "sec_RoleAssignmentScreen", "ChangePasswordDialog", "ResetPasswordDialog", "CES_Documentation.edit"})
    void screens();

    @EntityAttributePolicy(entityClass = ResourceRoleEntity.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = ResourceRoleEntity.class, actions = EntityPolicyAction.ALL)
    void resourceRoleEntity();

    @EntityAttributePolicy(entityClass = RoleAssignmentEntity.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = RoleAssignmentEntity.class, actions = EntityPolicyAction.ALL)
    void roleAssignmentEntity();

    @EntityAttributePolicy(entityClass = UserModel.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    void userModel();

    @EntityAttributePolicy(entityClass = UserSession.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = UserSession.class, actions = EntityPolicyAction.ALL)
    void userSession();

    @EntityAttributePolicy(entityClass = User.class, attributes = {"username", "password", "firstName", "lastName", "email", "active", "joiningDate", "branch", "direction", "timeZoneId"}, action = EntityAttributePolicyAction.MODIFY)
    @EntityAttributePolicy(entityClass = User.class, attributes = {"id", "version"}, action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = User.class, actions = {EntityPolicyAction.READ, EntityPolicyAction.UPDATE, EntityPolicyAction.CREATE})
    void user();

    @EntityAttributePolicy(entityClass = ResourceRoleModel.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = ResourceRoleModel.class, actions = EntityPolicyAction.ALL)
    void resourceRoleModel();

    @EntityAttributePolicy(entityClass = Branches.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Branches.class, actions = EntityPolicyAction.READ)
    void branches();

    @EntityAttributePolicy(entityClass = Documentation.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Documentation.class, actions = {EntityPolicyAction.READ, EntityPolicyAction.UPDATE})
    void documentation();

    @EntityAttributePolicy(entityClass = PasswordResetToken.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = PasswordResetToken.class, actions = EntityPolicyAction.ALL)
    void passwordResetToken();
}