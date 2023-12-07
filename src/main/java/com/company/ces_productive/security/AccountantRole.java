package com.company.ces_productive.security;

import com.company.ces_productive.entity.*;
import com.company.ces_productive.entity.courses.Courses;
import io.jmix.bpmui.model.BusinessRuleTaskModel;
import io.jmix.businesscalendar.entities.BusinessDayEntity;
import io.jmix.businesscalendar.entities.CalendarEntity;
import io.jmix.businesscalendar.entities.HolidayEntity;
import io.jmix.businesscalendarui.model.*;
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
@ResourceRole(name = "AccountantRole", code = "accountant")
public interface AccountantRole {
    @MenuPolicy(menuIds = {"CES_Branches.browse", "CES_Cabinets.browse", "CES_BookType.browse", "CES_Books.managment", "CES_Orders.browse", "CES_Payments.browse", "themeSettingsScreen", "CES_Students.mainInfo", "CES_ManagerTasks.browse", "CES_DiscountReason.browse", "CES_ExpenseReason.browse", "CES_DirectionEdit.browse", "CES_PivotIncomeExpense", "buscal_BusinessCalendarModel.browse", "CES_Courses.browseAdm", "CES_Groups.browseAll", "CES_Students.browseAll", "CES_Parents.browseAll", "CES_Documentation", "CES_DashboardScreen"})
    @ScreenPolicy(screenIds = {"CES_Branches.browse", "CES_Cabinets.browse", "CES_BookType.browse", "CES_Books.managment", "CES_Orders.browse", "CES_Payments.browse", "themeSettingsScreen", "CES_LoginScreen", "CES_Courses.createSch", "CES_Teacherjobtime", "CES_MainScreen", "CES_User.edit", "CES_Books.move", "CES_Books.edit", "CES_Books.accept", "CES_Books.browse", "CES_Freeze.edit", "CES_Orders.edit", "CES_Orders.book", "CES_Visits.edit", "CES_Courses.editSch", "CES_Courses.edit", "CES_Stopped.edit", "CES_Payments.edit", "CES_Students.edit", "CES_StudOrderPeriod.edit", "ui_UiDataFilterConfigurationModel.fragment", "bulkEditorWindow", "ui_LayoutAnalyzerScreen", "singleFileUploadDialog", "ui_AddConditionScreen", "ui_JpqlFilterCondition.edit", "ui_PropertyFilterCondition.edit", "ui_GroupFilterCondition.edit", "ui_FilterConfigurationModel.fragment", "sec_RoleFilterFragment", "sec_ResourceRoleModel.lookup", "sec_ResourceRoleModel.edit", "sec_RowLevelRoleModel.edit", "sec_RowLevelRoleModel.lookup", "ResetPasswordDialog", "ui_MBeanInspectScreen", "ui_MBeanAttribute.edit", "ChangePasswordDialog", "sec_ScreenResourcePolicyModel.edit", "sec_MenuResourcePolicyModel.edit", "sec_ScreenResourcePolicyModel.create", "sec_MenuResourcePolicyModel.create", "sec_EntityResourcePolicyModel.create", "sec_EntityAttributeResourcePolicyModel.create", "ui_MBeanOperationResultScreen", "sec_EntityAttributeResourcePolicyModel.edit", "ui_MBeanOperationFragment", "sec_GraphQLResourcePolicyModel.edit", "sec_EntityResourcePolicyModel.edit", "sec_ResourcePolicyModel.edit", "sec_SpecificResourcePolicyModel.edit", "notFoundScreen", "inputDialog", "selectValueDialog", "sec_RoleAssignmentScreen", "sec_RoleAssignmentFragment", "entityInfoWindow", "backgroundWorkProgressScreen", "sec_RowLevelPolicyModel.edit", "entityInspector.edit", "sec_UserSubstitutionEntity.edit", "sec_UserSubstitutionsFragment", "sec_UserSubstitutionsScreen", "ui_DateIntervalDialog", "snapshotDiff", "buscal_BusinessCalendarModel.edit", "buscal_HolidayModel.edit", "buscal_BusinessDayModel.edit", "buscal_AdditionalBusinessDayEdit.edit", "bpm_JobData.edit", "bpm_DispatcherPropertiesFragment", "bpm_LanePropertiesFragment", "bpm_FieldsFragment", "FieldModel.edit", "bpm_ProcessPropertiesFragment", "bpm_BasicPropertiesFragment", "bpm_ErrorRefPropertiesFragment", "bpm_FormDataPropertiesFragment", "bpm_UserTaskPropertiesFragment", "bpm_FormField.edit", "bpm_FormField.lookup", "bpm_FormParam.edit", "bpm_SignalRefPropertiesFragment", "bpm_ErrorEventPropertiesFragment", "bpm_MessageRefPropertiesFragment", "bpm_ScriptTaskPropertiesFragment", "bpm_ScriptEditor", "bpm_StartEventPropertiesFragment", "bpm_SubProcessPropertiesFragment", "bpm_TimerEventPropertiesFragment", "bpm_FormOutcomeEdit", "bpm_ParticipantPropertiesFragment", "bpm_ServiceTaskPropertiesFragment", "bpm_SignalEventPropertiesFragment", "bpm_CallActivityPropertiesFragment", "bpm_InOutBindingModel.edit", "bpm_MessageEventPropertiesFragment", "bpm_SequenceFlowPropertiesFragment", "bpm_TaskListenerModel.edit", "bpm_EventListener.edit", "bpm_MultiInstanceLoopCharacteristicsPropertiesFragment", "bpm_SendEmailTaskPropertiesFragment", "bpm_EmailAttachmentFragment", "bpm_EmailAttachmentEdit", "bpm_EmailContentEdit", "bpm_EmailAddressEdit", "bpm_GroupCandidatesEdit", "bpm_CandidateUsersEdit", "bpm_EntityDataTaskPropertiesFragment", "bpm_JpqlParametersFragment", "JpqlParameterEdit", "bpm_EntityAttributesFragment", "EntityAttributeEdit", "bpm_ErrorDefinition.edit", "bpm_ProcessVariable.edit", "bpm_BusinessRuleTaskPropertiesFragment", "bpm_SignalDefinition.edit", "bpm_TimerDescriptionPropertiesFragment", "bpm_AssignmentDetailsPropertiesFragment", "bpm_ExecutionListenersFragment", "ExecutionListenerModel.edit", "bpm_MessageDefinition.edit", "bpm_TerminateEndEventPropertiesFragment", "dshbrd_Widget.edit", "bpm_ExtensionPropertyModel.edit", "bpm_ExtensionPropertiesFragment", "dshbrd_DashboardView.screen", "dshbrd_PersistentDashboard.edit", "dshbrd_CssLayoutCreation.dialog", "dshbrd_GridCreation.dialog", "dshbrd_Style.dialog", "bpm_UserGroup.edit", "dshbrd_Canvas.fragment", "dshbrd_CanvasEditor.fragment", "bpm_DynamicStartProcessForm", "bpm_DynamicTaskProcessForm", "dshbrd_Expand.dialog", "dshbrd_Weight.dialog", "dshbrd_Colspan.dialog", "dshbrd_Palette.fragment", "bpm_TaskReassignScreen", "ui_JsonChartFragment", "dshbrd_ResponsiveCreation.dialog", "bpm_ContentStorage.edit", "bpm_ContentStorage.browse", "dshbrd_Parameters.fragment", "dshbrd_Parameter.edit", "bpm_DefaultTaskProcessForm", "dshbrd_EntityValue.fragment", "dshbrd_EntityListValue.fragment", "dshbrd_EntityValue.screen", "dshbrd_EnumValue.fragment", "dshbrd_SimpleValue.fragment", "bpm_ProcessInstanceEditHistoryFragment", "bpm_ProcessInstanceData.edit", "bpm_ProcessInstanceEditRuntimeFragment", "bpm_DecisionDetailsScreen", "bpm_BpmnDiagramViewerFragment", "bpm_VariableInstanceData.edit", "dshbrd_DashboardGroup.edit", "dshbrd_DashboardGroup.browse", "bpm_DefaultStartProcessForm", "bpm_DmnDecisionTable.lookup", "bpm_DmnDecisionTableEdit", "dshbrd_WidgetTemplate.edit", "bpm_SuspendProcessDefinitionScreen", "bpm_ActivateProcessDefinitionScreen", "bpm_ProcessDefinition.edit", "bpm_ProcessDefinitionData.lookup", "dshbrd_WidgetTemplateGroup.browse", "dshbrd_WidgetTemplateGroup.edit", "bpm_HitPolicySelectScreen", "bpm_InputEntryEdit", "bpm_InputDefinitionEdit", "bpm_OutputDefinitionEdit", "bpm_ProcessInstanceMigrationScreen", "ResendMessage", "email_SendingMessage.attachments", "ntf_UserInAppNotification.browse", "ntf_InAppNotification.view", "ntf_InAppNotification.edit", "ntf_CreateNotification.screen", "report_ReportGroup.edit", "report_InputParameters.fragment", "report_InputParameters.dialog", "report_Report.edit", "report_ReportEditLocales.fragment", "report_ReportEditParameters.fragment", "report_ReportEditGeneral.fragment", "report_ReportEditSecurity.fragment", "report_ReportEditValueFormats.fragment", "report_ReportEditTemplates.fragment", "report_ReportWizardCreator", "report_SaveStep.fragment", "report_DetailsStep.fragment", "report_RegionStep.fragment", "report_QueryStep.fragment", "report_QueryParameter.edit", "report_Region.edit", "report_ReportEntityTree.lookup", "report_EntityTree.fragment", "report_ReportExecution.dialog", "report_ReportExecution.browse", "report_ReportImport.dialog", "report_ChartEdit.fragment", "report_TableEdit.fragment", "report_PivotTableEdit.fragment", "report_ReportTemplate.edit", "report_PivotTableProperty.edit", "report_PivotTableAggregation.edit", "quartz_JobModel.edit", "report_ReportInputParameter.edit", "quartz_TriggerModel.edit", "report_BandDefinitionEditor.fragment", "report_ScriptEditor.dialog", "report_ReportValueFormat.edit", "ui_PivotTableScreen", "ui_PivotTableFragment", "CES_User.browse", "CES_Books.AcceptBranch", "CES_Students.mainInfo", "CES_ManagerTasks.browse", "CES_DiscountReason.browse", "CES_ExpenseReason.browse", "CES_DirectionEdit.browse", "CES_PivotIncomeExpense", "CES_Expenses.edit", "buscal_BusinessCalendarModel.browse", "CES_Groups.edit", "CES_Courses.browseAdm", "CES_Groups.browseAll", "CES_Students.browseAll", "CES_Parents.browseAll", "CES_Documentation.edit", "CES_Parents.edit", "CES_Documentation", "CES_PaymentParam.edit", "CES_DashboardScreen", "CES_Students.browse"})
    void screens();

    @EntityAttributePolicy(entityClass = BookType.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = BookType.class, actions = EntityPolicyAction.ALL)
    void bookType();

    @EntityAttributePolicy(entityClass = Books.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Books.class, actions = EntityPolicyAction.ALL)
    void books();

    @EntityAttributePolicy(entityClass = Branches.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Branches.class, actions = EntityPolicyAction.ALL)
    void branches();

    @EntityAttributePolicy(entityClass = Cabinets.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Cabinets.class, actions = EntityPolicyAction.ALL)
    void cabinets();

    @EntityAttributePolicy(entityClass = Direction.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Direction.class, actions = EntityPolicyAction.ALL)
    void direction();

    @EntityAttributePolicy(entityClass = Orders.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Orders.class, actions = EntityPolicyAction.ALL)
    void orders();

    @EntityAttributePolicy(entityClass = Payments.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Payments.class, actions = EntityPolicyAction.ALL)
    void payments();

    @EntityAttributePolicy(entityClass = Stopped.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Stopped.class, actions = EntityPolicyAction.ALL)
    void stopped();

    @EntityAttributePolicy(entityClass = Freeze.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Freeze.class, actions = EntityPolicyAction.ALL)
    void freeze();

    @EntityAttributePolicy(entityClass = Visits.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Visits.class, actions = EntityPolicyAction.ALL)
    void visits();

    @EntityAttributePolicy(entityClass = Courses.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Courses.class, actions = EntityPolicyAction.ALL)
    void courses();

    @EntityAttributePolicy(entityClass = Groups.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Groups.class, actions = EntityPolicyAction.ALL)
    void groups();

    @SpecificPolicy(resources = {"rest.fileDownload.enabled", "rest.fileUpload.enabled"})
    void specific();

    @EntityAttributePolicy(entityClass = ManagerTasks.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = ManagerTasks.class, actions = EntityPolicyAction.ALL)
    void managerTasks();

    @EntityAttributePolicy(entityClass = DiscountReason.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = DiscountReason.class, actions = EntityPolicyAction.ALL)
    void discountReason();

    @EntityPolicy(entityClass = User.class, actions = EntityPolicyAction.READ)
    @EntityAttributePolicy(entityClass = User.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    void user();

    @EntityAttributePolicy(entityClass = ExpenseReason.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = ExpenseReason.class, actions = EntityPolicyAction.ALL)
    void expenseReason();

    @EntityAttributePolicy(entityClass = BusinessDayEntity.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = BusinessDayEntity.class, actions = EntityPolicyAction.ALL)
    void businessDayEntity();

    @EntityAttributePolicy(entityClass = BusinessDayModel.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = BusinessDayModel.class, actions = EntityPolicyAction.ALL)
    void businessDayModel();

    @EntityAttributePolicy(entityClass = BusinessCalendarModel.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = BusinessCalendarModel.class, actions = EntityPolicyAction.ALL)
    void businessCalendarModel();

    @EntityAttributePolicy(entityClass = BusinessRuleTaskModel.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = BusinessRuleTaskModel.class, actions = EntityPolicyAction.ALL)
    void businessRuleTaskModel();

    @EntityAttributePolicy(entityClass = CalendarEntity.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = CalendarEntity.class, actions = EntityPolicyAction.ALL)
    void calendarEntity();

    @EntityAttributePolicy(entityClass = HolidayEntity.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = HolidayEntity.class, actions = EntityPolicyAction.ALL)
    void holidayEntity();

    @EntityAttributePolicy(entityClass = HolidayModel.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = HolidayModel.class, actions = EntityPolicyAction.ALL)
    void holidayModel();

    @EntityAttributePolicy(entityClass = ScheduledBusinessDayModel.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = ScheduledBusinessDayModel.class, actions = EntityPolicyAction.ALL)
    void scheduledBusinessDayModel();

    @EntityAttributePolicy(entityClass = AdditionalBusinessDayModel.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = AdditionalBusinessDayModel.class, actions = EntityPolicyAction.ALL)
    void additionalBusinessDayModel();

    @EntityAttributePolicy(entityClass = Documentation.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Documentation.class, actions = {EntityPolicyAction.READ, EntityPolicyAction.UPDATE})
    void documentation();

    @EntityAttributePolicy(entityClass = Parents.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Parents.class, actions = EntityPolicyAction.ALL)
    void parents();

    @EntityAttributePolicy(entityClass = Students.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Students.class, actions = EntityPolicyAction.ALL)
    void students();

    @EntityAttributePolicy(entityClass = PaymentParam.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = PaymentParam.class, actions = EntityPolicyAction.ALL)
    void paymentParam();
}