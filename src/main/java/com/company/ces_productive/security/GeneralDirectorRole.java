package com.company.ces_productive.security;

import com.company.ces_productive.entity.*;
import com.company.ces_productive.entity.courses.Courses;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.securityui.role.annotation.MenuPolicy;
import io.jmix.securityui.role.annotation.ScreenPolicy;

@ResourceRole(name = "GeneralDirectorRole", code = GeneralDirectorRole.CODE)
public interface GeneralDirectorRole {
    String CODE = "general-director-role";

    @EntityAttributePolicy(entityClass = BookType.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = BookType.class, actions = EntityPolicyAction.READ)
    void bookType();

    @EntityAttributePolicy(entityClass = Branches.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Branches.class, actions = EntityPolicyAction.READ)
    void branches();

    @EntityAttributePolicy(entityClass = Books.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Books.class, actions = EntityPolicyAction.READ)
    void books();

    @EntityPolicy(entityClass = Courses.class, actions = EntityPolicyAction.READ)
    @EntityAttributePolicy(entityClass = Courses.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    void courses();

    @EntityAttributePolicy(entityClass = Cabinets.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Cabinets.class, actions = EntityPolicyAction.READ)
    void cabinets();

    @EntityAttributePolicy(entityClass = Direction.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Direction.class, actions = EntityPolicyAction.READ)
    void direction();

    @EntityAttributePolicy(entityClass = DiscountReason.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = DiscountReason.class, actions = EntityPolicyAction.READ)
    void discountReason();

    @EntityAttributePolicy(entityClass = ExpenseReason.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = ExpenseReason.class, actions = EntityPolicyAction.READ)
    void expenseReason();

    @EntityAttributePolicy(entityClass = Documentation.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Documentation.class, actions = EntityPolicyAction.READ)
    void documentation();

    @EntityAttributePolicy(entityClass = Freeze.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Freeze.class, actions = EntityPolicyAction.READ)
    void freeze();

    @EntityAttributePolicy(entityClass = Expenses.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Expenses.class, actions = EntityPolicyAction.READ)
    void expenses();

    @EntityAttributePolicy(entityClass = Groups.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Groups.class, actions = EntityPolicyAction.READ)
    void groups();

    @EntityAttributePolicy(entityClass = Orders.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Orders.class, actions = EntityPolicyAction.READ)
    void orders();

    @EntityAttributePolicy(entityClass = Parents.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Parents.class, actions = EntityPolicyAction.READ)
    void parents();

    @EntityAttributePolicy(entityClass = ManagerTasks.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = ManagerTasks.class, actions = EntityPolicyAction.READ)
    void managerTasks();

    @EntityAttributePolicy(entityClass = PasswordResetToken.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = PasswordResetToken.class, actions = EntityPolicyAction.READ)
    void passwordResetToken();

    @EntityAttributePolicy(entityClass = Payments.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Payments.class, actions = EntityPolicyAction.READ)
    void payments();

    @EntityAttributePolicy(entityClass = PaymentParam.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = PaymentParam.class, actions = EntityPolicyAction.READ)
    void paymentParam();

    @EntityAttributePolicy(entityClass = User.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = User.class, actions = EntityPolicyAction.READ)
    void user();

    @EntityAttributePolicy(entityClass = Stopped.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Stopped.class, actions = EntityPolicyAction.READ)
    void stopped();

    @EntityAttributePolicy(entityClass = Students.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Students.class, actions = EntityPolicyAction.READ)
    void students();

    @EntityAttributePolicy(entityClass = Visits.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Visits.class, actions = EntityPolicyAction.READ)
    void visits();

    @MenuPolicy(menuIds = {"CES_Books.managment", "CES_Expenses.browse", "CES_PivotIncomeExpense", "CES_Students.mainInfo", "CES_DashboardScreen"})
    @ScreenPolicy(screenIds = {"CES_Books.managment", "CES_Expenses.browse", "CES_PivotIncomeExpense", "CES_Students.mainInfo", "CES_DashboardScreen"})
    void screens();
}