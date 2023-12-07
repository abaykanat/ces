package com.company.ces_productive.security;

import com.company.ces_productive.entity.Branches;
import com.company.ces_productive.entity.Groups;
import com.company.ces_productive.entity.Students;
import com.company.ces_productive.entity.courses.Courses;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.securityui.role.annotation.MenuPolicy;
import io.jmix.securityui.role.annotation.ScreenPolicy;

@ResourceRole(name = "MetodistRole", code = MetodistRole.CODE)
public interface MetodistRole {
    String CODE = "metodist";

    @MenuPolicy(menuIds = {"CES_Courses.browseAdm", "CES_Groups.browseAll", "CES_Courses.browseAll"})
    @ScreenPolicy(screenIds = {"CES_Groups.browse", "CES_Courses.browseAdm", "CES_Groups.browseAll", "CES_Courses.browseAll"})
    void screens();

    @EntityAttributePolicy(entityClass = Courses.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Courses.class, actions = {EntityPolicyAction.READ, EntityPolicyAction.UPDATE, EntityPolicyAction.CREATE})
    void courses();

    @EntityAttributePolicy(entityClass = Groups.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Groups.class, actions = {EntityPolicyAction.READ, EntityPolicyAction.UPDATE, EntityPolicyAction.CREATE})
    void groups();

    @EntityAttributePolicy(entityClass = Branches.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Branches.class, actions = {EntityPolicyAction.CREATE, EntityPolicyAction.READ, EntityPolicyAction.UPDATE})
    void branches();

    @EntityAttributePolicy(entityClass = Students.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Students.class, actions = EntityPolicyAction.READ)
    void students();
}