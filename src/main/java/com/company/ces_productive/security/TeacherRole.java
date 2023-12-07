package com.company.ces_productive.security;

import com.company.ces_productive.entity.*;
import com.company.ces_productive.entity.courses.Courses;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.security.role.annotation.SpecificPolicy;
import io.jmix.securityui.role.annotation.MenuPolicy;
import io.jmix.securityui.role.annotation.ScreenPolicy;

import javax.annotation.Nonnull;

@Nonnull
@ResourceRole(name = "TeacherRole", code = "teacher")
public interface TeacherRole {
    @MenuPolicy(menuIds = {"CES_Courses.browse", "CES_Visitmark", "themeSettingsScreen", "CES_Groups.browseTeacher"})
    @ScreenPolicy(screenIds = {"CES_Courses.browse", "CES_Visitmark", "CES_LoginScreen", "themeSettingsScreen", "CES_Visits.browse", "CES_Visits.edit", "CES_MainScreen", "CES_Groups.edit", "CES_Groups.browseTeacher", "CES_Documentation.edit"})
    void screens();

    @EntityAttributePolicy(entityClass = Visits.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Visits.class, actions = EntityPolicyAction.ALL)
    void visits();

    @EntityAttributePolicy(entityClass = Students.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Students.class, actions = {EntityPolicyAction.READ, EntityPolicyAction.UPDATE})
    void students();

    @EntityAttributePolicy(entityClass = Courses.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Courses.class, actions = {EntityPolicyAction.READ, EntityPolicyAction.UPDATE, EntityPolicyAction.CREATE})
    void courses();

    @EntityAttributePolicy(entityClass = Groups.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Groups.class, actions = EntityPolicyAction.READ)
    void groups();

    @EntityAttributePolicy(entityClass = Direction.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Direction.class, actions = EntityPolicyAction.READ)
    void direction();

    @EntityAttributePolicy(entityClass = User.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = User.class, actions = EntityPolicyAction.READ)
    void user();

    @EntityAttributePolicy(entityClass = Cabinets.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Cabinets.class, actions = EntityPolicyAction.READ)
    void cabinets();

    @EntityAttributePolicy(entityClass = Branches.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Branches.class, actions = EntityPolicyAction.READ)
    void branches();

    @EntityAttributePolicy(entityClass = Stopped.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Stopped.class, actions = EntityPolicyAction.READ)
    void stopped();

    @EntityAttributePolicy(entityClass = Freeze.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Freeze.class, actions = EntityPolicyAction.READ)
    void freeze();

    @SpecificPolicy(resources = {"rest.fileDownload.enabled", "rest.fileUpload.enabled"})
    void specific();

    @EntityAttributePolicy(entityClass = Documentation.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Documentation.class, actions = {EntityPolicyAction.READ, EntityPolicyAction.UPDATE})
    void documentation();
}