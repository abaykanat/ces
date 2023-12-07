package com.company.ces_productive.security;

import com.company.ces_productive.entity.*;
import com.company.ces_productive.entity.courses.Courses;
import io.jmix.reports.entity.*;
import io.jmix.reports.entity.wizard.ReportData;
import io.jmix.reports.entity.wizard.ReportRegion;
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
@ResourceRole(name = "ManagerRole", code = "manager")
public interface ManagerRole {
    @MenuPolicy(menuIds = {"CES_Courses.browse", "CES_Groups.browse", "themeSettingsScreen", "CES_Students.mainInfo", "CES_Books.managment", "CES_Expenses.browse", "CES_PivotIncomeExpense", "CES_Parents.browse", "CES_Students.browseManager"})
    @ScreenPolicy(screenIds = {"CES_Courses.browse", "CES_Groups.browse", "CES_LoginScreen", "CES_MainScreen", "CES_Orders.browse", "CES_Payments.browse", "CES_Students.edit", "CES_StudOrderPeriod.edit", "CES_BookType.browse", "CES_User.browse", "CES_Cabinets.browse", "themeSettingsScreen", "CES_Courses.createSch", "CES_Courses.Delete", "CES_Courses.editSch", "CES_Courses.edit", "CES_Freeze.edit", "CES_Stopped.edit", "CES_Payments.edit", "CES_Orders.book", "CES_Books.browse", "CES_Students.mainInfo", "CES_Direction.browse", "CES_Books.managment", "CES_Books.move", "CES_Books.AcceptBranch", "CES_Expenses.browse", "CES_ExpenseReason.browse", "CES_Expenses.edit", "CES_PivotIncomeExpense", "CES_Parents.browse", "CES_Parents.edit", "CES_Groups.edit", "report_Report.run", "report_ShowReportTable.screen", "report_Report.browse", "report_ReportGroup.browse", "report_ReportInputParameter.edit", "report_InputParameters.dialog", "report_InputParameters.fragment", "report_Report.edit", "report_ReportEditGeneral.fragment", "report_ReportEditLocales.fragment", "report_ReportEditParameters.fragment", "report_ReportEditSecurity.fragment", "report_ReportEditTemplates.fragment", "report_ReportEditValueFormats.fragment", "report_ReportExecution.browse", "report_ReportExecution.dialog", "report_ReportImport.dialog", "CES_Students.browseManager", "CES_Students.browse", "CES_Documentation.edit", "CES_PaymentParam.edit"})
    void screens();

    @EntityAttributePolicy(entityClass = Courses.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Courses.class, actions = EntityPolicyAction.ALL)
    void courses();

    @EntityAttributePolicy(entityClass = Freeze.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Freeze.class, actions = EntityPolicyAction.ALL)
    void freeze();

    @EntityPolicy(entityClass = Orders.class, actions = EntityPolicyAction.ALL)
    @EntityAttributePolicy(entityClass = Orders.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    void orders();

    @EntityAttributePolicy(entityClass = Payments.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Payments.class, actions = {EntityPolicyAction.READ, EntityPolicyAction.UPDATE, EntityPolicyAction.CREATE})
    void payments();

    @EntityAttributePolicy(entityClass = Stopped.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Stopped.class, actions = EntityPolicyAction.ALL)
    void stopped();

    @EntityPolicy(entityClass = Students.class, actions = EntityPolicyAction.ALL)
    @EntityAttributePolicy(entityClass = Students.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    void students();

    @EntityAttributePolicy(entityClass = Books.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Books.class, actions = EntityPolicyAction.ALL)
    void books();

    @EntityAttributePolicy(entityClass = Parents.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Parents.class, actions = {EntityPolicyAction.READ, EntityPolicyAction.UPDATE, EntityPolicyAction.CREATE})
    void parents();

    @EntityAttributePolicy(entityClass = Direction.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Direction.class, actions = EntityPolicyAction.READ)
    void direction();

    @EntityPolicy(entityClass = User.class, actions = EntityPolicyAction.READ)
    @EntityAttributePolicy(entityClass = User.class, attributes = {"id", "username", "firstName", "lastName", "branch", "direction", "active", "email"}, action = EntityAttributePolicyAction.VIEW)
    void user();

    @EntityAttributePolicy(entityClass = Cabinets.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Cabinets.class, actions = EntityPolicyAction.READ)
    void cabinets();

    @EntityAttributePolicy(entityClass = BookType.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = BookType.class, actions = EntityPolicyAction.READ)
    void bookType();

    @EntityAttributePolicy(entityClass = ManagerTasks.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = ManagerTasks.class, actions = EntityPolicyAction.ALL)
    void managerTasks();

    @EntityAttributePolicy(entityClass = DiscountReason.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = DiscountReason.class, actions = EntityPolicyAction.READ)
    void discountReason();

    @EntityAttributePolicy(entityClass = Branches.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Branches.class, actions = {EntityPolicyAction.READ, EntityPolicyAction.UPDATE})
    void branches();

    @EntityAttributePolicy(entityClass = ExpenseReason.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = ExpenseReason.class, actions = {EntityPolicyAction.READ, EntityPolicyAction.UPDATE, EntityPolicyAction.CREATE})
    void expenseReason();

    @EntityAttributePolicy(entityClass = Expenses.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Expenses.class, actions = {EntityPolicyAction.READ, EntityPolicyAction.UPDATE, EntityPolicyAction.CREATE})
    void expenses();

    @SpecificPolicy(resources = {"rest.fileDownload.enabled", "rest.fileUpload.enabled", "reports.rest.enabled"})
    void specific();

    @EntityAttributePolicy(entityClass = Groups.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Groups.class, actions = EntityPolicyAction.ALL)
    void groups();

    @EntityAttributePolicy(entityClass = Report.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Report.class, actions = EntityPolicyAction.ALL)
    void report();

    @EntityAttributePolicy(entityClass = ReportData.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = ReportData.class, actions = EntityPolicyAction.ALL)
    void reportData();

    @EntityAttributePolicy(entityClass = ReportExecution.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = ReportExecution.class, actions = EntityPolicyAction.ALL)
    void reportExecution();

    @EntityAttributePolicy(entityClass = ReportInputParameter.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = ReportInputParameter.class, actions = EntityPolicyAction.ALL)
    void reportInputParameter();

    @EntityAttributePolicy(entityClass = ReportValueFormat.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = ReportValueFormat.class, actions = EntityPolicyAction.ALL)
    void reportValueFormat();

    @EntityAttributePolicy(entityClass = ReportTemplate.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = ReportTemplate.class, actions = EntityPolicyAction.ALL)
    void reportTemplate();

    @EntityAttributePolicy(entityClass = ReportScreen.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = ReportScreen.class, actions = EntityPolicyAction.ALL)
    void reportScreen();

    @EntityAttributePolicy(entityClass = ReportRole.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = ReportRole.class, actions = EntityPolicyAction.ALL)
    void reportRole();

    @EntityAttributePolicy(entityClass = ReportRegion.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = ReportRegion.class, actions = EntityPolicyAction.ALL)
    void reportRegion();

    @EntityAttributePolicy(entityClass = ReportGroup.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = ReportGroup.class, actions = EntityPolicyAction.ALL)
    void reportGroup();

    @EntityAttributePolicy(entityClass = Documentation.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Documentation.class, actions = {EntityPolicyAction.READ, EntityPolicyAction.UPDATE})
    void documentation();

    @EntityAttributePolicy(entityClass = PaymentParam.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = PaymentParam.class, actions = EntityPolicyAction.ALL)
    void paymentParam();

    @EntityAttributePolicy(entityClass = Visits.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Visits.class, actions = EntityPolicyAction.READ)
    void visits();
}