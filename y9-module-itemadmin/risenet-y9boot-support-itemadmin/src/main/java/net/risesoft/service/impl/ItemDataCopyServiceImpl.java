package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.permission.RoleApi;
import net.risesoft.api.platform.resource.SystemApi;
import net.risesoft.api.processadmin.ProcessDataCopyApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.entity.CalendarConfig;
import net.risesoft.entity.DynamicRole;
import net.risesoft.entity.Item;
import net.risesoft.entity.ItemPermission;
import net.risesoft.entity.button.CommonButton;
import net.risesoft.entity.button.ItemButtonBind;
import net.risesoft.entity.button.ItemButtonRole;
import net.risesoft.entity.button.SendButton;
import net.risesoft.entity.form.Y9Form;
import net.risesoft.entity.form.Y9FormField;
import net.risesoft.entity.form.Y9FormItemBind;
import net.risesoft.entity.form.Y9FormOptionClass;
import net.risesoft.entity.form.Y9FormOptionValue;
import net.risesoft.entity.form.Y9Table;
import net.risesoft.entity.form.Y9TableField;
import net.risesoft.entity.opinion.ItemOpinionFrameBind;
import net.risesoft.entity.opinion.ItemOpinionFrameRole;
import net.risesoft.entity.opinion.OpinionFrame;
import net.risesoft.entity.organword.ItemOrganWordBind;
import net.risesoft.entity.organword.OrganWord;
import net.risesoft.entity.organword.OrganWordProperty;
import net.risesoft.entity.tab.ItemTabBind;
import net.risesoft.entity.tab.TabEntity;
import net.risesoft.entity.template.BookMarkBind;
import net.risesoft.entity.template.ItemPrintTemplateBind;
import net.risesoft.entity.template.ItemWordTemplateBind;
import net.risesoft.entity.template.PrintTemplate;
import net.risesoft.entity.template.TaoHongTemplate;
import net.risesoft.entity.template.TaoHongTemplateType;
import net.risesoft.entity.template.WordTemplate;
import net.risesoft.entity.view.ItemViewConf;
import net.risesoft.enums.ItemButtonTypeEnum;
import net.risesoft.enums.ItemPermissionEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.Role;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.platform.org.Organization;
import net.risesoft.model.platform.resource.System;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.ItemPermissionRepository;
import net.risesoft.service.BookMarkBindService;
import net.risesoft.service.CalendarConfigService;
import net.risesoft.service.CommonButtonService;
import net.risesoft.service.DynamicRoleService;
import net.risesoft.service.ItemDataCopyService;
import net.risesoft.service.SendButtonService;
import net.risesoft.service.TabEntityService;
import net.risesoft.service.config.ItemButtonBindService;
import net.risesoft.service.config.ItemButtonRoleService;
import net.risesoft.service.config.ItemOpinionFrameBindService;
import net.risesoft.service.config.ItemOpinionFrameRoleService;
import net.risesoft.service.config.ItemOrganWordBindService;
import net.risesoft.service.config.ItemOrganWordRoleService;
import net.risesoft.service.config.ItemTabBindService;
import net.risesoft.service.config.ItemViewConfService;
import net.risesoft.service.config.ItemWordTemplateBindService;
import net.risesoft.service.config.Y9FormItemBindService;
import net.risesoft.service.core.ItemService;
import net.risesoft.service.form.TableManagerService;
import net.risesoft.service.form.Y9FormFieldService;
import net.risesoft.service.form.Y9FormOptionClassService;
import net.risesoft.service.form.Y9FormService;
import net.risesoft.service.form.Y9TableFieldService;
import net.risesoft.service.form.Y9TableService;
import net.risesoft.service.opinion.OpinionFrameService;
import net.risesoft.service.organword.OrganWordPropertyService;
import net.risesoft.service.organword.OrganWordService;
import net.risesoft.service.template.PrintTemplateService;
import net.risesoft.service.template.TaoHongTemplateService;
import net.risesoft.service.template.TaoHongTemplateTypeService;
import net.risesoft.service.template.WordTemplateService;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.sqlddl.pojo.DbColumn;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@Slf4j
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class ItemDataCopyServiceImpl implements ItemDataCopyService {

    private final ItemService itemService;

    private final DynamicRoleService dynamicRoleService;

    private final ItemPermissionRepository itemPermissionRepository;

    private final Y9FormItemBindService y9FormItemBindService;

    private final Y9FormService y9FormService;

    private final Y9FormFieldService y9FormFieldService;

    private final Y9FormOptionClassService y9FormOptionClassService;

    private final Y9TableService y9TableService;

    private final Y9TableFieldService y9TableFieldService;

    private final TableManagerService tableManagerService;

    private final OpinionFrameService opinionFrameService;

    private final ItemOpinionFrameBindService itemOpinionFrameBindService;

    private final ItemOpinionFrameRoleService itemOpinionFrameRoleService;

    private final ItemViewConfService itemViewConfService;

    private final WordTemplateService wordTemplateService;

    private final ItemWordTemplateBindService itemWordTemplateBindService;

    private final BookMarkBindService bookMarkBindService;

    private final PrintTemplateService printTemplateService;

    private final TabEntityService tabEntityService;

    private final ItemTabBindService itemTabBindService;

    private final CalendarConfigService calendarConfigService;

    private final TaoHongTemplateService taoHongTemplateService;

    private final TaoHongTemplateTypeService taoHongTemplateTypeService;

    private final OrganWordService organWordService;

    private final OrganWordPropertyService organWordPropertyService;

    private final ItemOrganWordBindService itemOrganWordBindService;

    private final ItemOrganWordRoleService itemOrganWordRoleService;

    private final CommonButtonService commonButtonService;

    private final SendButtonService sendButtonService;

    private final ItemButtonBindService itemButtonBindService;

    private final ItemButtonRoleService itemButtonRoleService;

    private final RepositoryApi repositoryApi;

    private final SystemApi systemApi;

    private final RoleApi roleApi;

    private final OrgUnitApi orgUnitApi;

    private final ProcessDataCopyApi processDataCopyApi;

    private final ItemDataCopyService self;

    public ItemDataCopyServiceImpl(
        ItemService itemService,
        DynamicRoleService dynamicRoleService,
        ItemPermissionRepository itemPermissionRepository,
        Y9FormItemBindService y9FormItemBindService,
        Y9FormService y9FormService,
        Y9FormFieldService y9FormFieldService,
        Y9FormOptionClassService y9FormOptionClassService,
        Y9TableService y9TableService,
        Y9TableFieldService y9TableFieldService,
        TableManagerService tableManagerService,
        OpinionFrameService opinionFrameService,
        ItemOpinionFrameBindService itemOpinionFrameBindService,
        ItemOpinionFrameRoleService itemOpinionFrameRoleService,
        ItemViewConfService itemViewConfService,
        WordTemplateService wordTemplateService,
        ItemWordTemplateBindService itemWordTemplateBindService,
        BookMarkBindService bookMarkBindService,
        PrintTemplateService printTemplateService,
        TabEntityService tabEntityService,
        ItemTabBindService itemTabBindService,
        CalendarConfigService calendarConfigService,
        TaoHongTemplateService taoHongTemplateService,
        TaoHongTemplateTypeService taoHongTemplateTypeService,
        OrganWordService organWordService,
        OrganWordPropertyService organWordPropertyService,
        ItemOrganWordBindService itemOrganWordBindService,
        ItemOrganWordRoleService itemOrganWordRoleService,
        CommonButtonService commonButtonService,
        SendButtonService sendButtonService,
        ItemButtonBindService itemButtonBindService,
        ItemButtonRoleService itemButtonRoleService,
        RepositoryApi repositoryApi,
        SystemApi systemApi,
        RoleApi roleApi,
        OrgUnitApi orgUnitApi,
        ProcessDataCopyApi processDataCopyApi,
        @Lazy ItemDataCopyService self) {
        this.itemService = itemService;
        this.dynamicRoleService = dynamicRoleService;
        this.itemPermissionRepository = itemPermissionRepository;
        this.y9FormItemBindService = y9FormItemBindService;
        this.y9FormService = y9FormService;
        this.y9FormFieldService = y9FormFieldService;
        this.y9FormOptionClassService = y9FormOptionClassService;
        this.y9TableService = y9TableService;
        this.y9TableFieldService = y9TableFieldService;
        this.tableManagerService = tableManagerService;
        this.opinionFrameService = opinionFrameService;
        this.itemOpinionFrameBindService = itemOpinionFrameBindService;
        this.itemOpinionFrameRoleService = itemOpinionFrameRoleService;
        this.itemViewConfService = itemViewConfService;
        this.wordTemplateService = wordTemplateService;
        this.itemWordTemplateBindService = itemWordTemplateBindService;
        this.bookMarkBindService = bookMarkBindService;
        this.printTemplateService = printTemplateService;
        this.tabEntityService = tabEntityService;
        this.itemTabBindService = itemTabBindService;
        this.calendarConfigService = calendarConfigService;
        this.taoHongTemplateService = taoHongTemplateService;
        this.taoHongTemplateTypeService = taoHongTemplateTypeService;
        this.organWordService = organWordService;
        this.organWordPropertyService = organWordPropertyService;
        this.itemOrganWordBindService = itemOrganWordBindService;
        this.itemOrganWordRoleService = itemOrganWordRoleService;
        this.commonButtonService = commonButtonService;
        this.sendButtonService = sendButtonService;
        this.itemButtonBindService = itemButtonBindService;
        this.itemButtonRoleService = itemButtonRoleService;
        this.repositoryApi = repositoryApi;
        this.systemApi = systemApi;
        this.roleApi = roleApi;
        this.orgUnitApi = orgUnitApi;
        this.processDataCopyApi = processDataCopyApi;
        this.self = self;
    }

    @Override
    @Transactional
    public void copyCalendarConfig(String sourceTenantId, String targetTenantId) {
        /*
          1、查找源租户是否存在日历配置
         */
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        CalendarConfig calendarConfig = calendarConfigService.findByYear(String.valueOf(Calendar.YEAR));
        if (null == calendarConfig) {
            return;
        }
        /*
          2、复制源租户的日历配置到目标租户
         */
        Y9LoginUserHolder.setTenantId(targetTenantId);
        CalendarConfig targetCalendarConfig = calendarConfigService.findByYear(String.valueOf(Calendar.YEAR));
        if (null != targetCalendarConfig) {
            return;
        }
        calendarConfigService.saveOrUpdate(calendarConfig);
    }

    @Override
    @Transactional
    public void copyCommonButton(String sourceTenantId, String targetTenantId, String itemId) {
        // 1. 查找并复制源租户的普通按钮
        List<CommonButton> sourceButtons = getSourceCommonButtons(sourceTenantId);
        if (sourceButtons.isEmpty()) {
            return;
        }

        // 2. 复制普通按钮到目标租户
        copyCommonButtonsToTargetTenant(targetTenantId, sourceButtons);

        // 3. 复制按钮绑定关系和权限
        copyButtonBindingsAndPermissions(sourceTenantId, targetTenantId, itemId, ItemButtonTypeEnum.COMMON);
    }

    /**
     * 获取源租户的普通按钮列表
     */
    private List<CommonButton> getSourceCommonButtons(String sourceTenantId) {
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        return commonButtonService.listAll();
    }

    /**
     * 复制普通按钮到目标租户
     */
    private void copyCommonButtonsToTargetTenant(String targetTenantId, List<CommonButton> sourceButtons) {
        Y9LoginUserHolder.setTenantId(targetTenantId);
        for (CommonButton button : sourceButtons) {
            commonButtonService.saveOrUpdate(button);
        }
    }

    /**
     * 获取目标租户角色信息
     */
    private RoleInfo getTargetTenantRoleInfo(String targetTenantId) {
        System system = systemApi.getByName(Y9Context.getSystemName()).getData();
        Role tenantRole = roleApi.getRole(targetTenantId).getData();
        Role tenantSystemRole = roleApi.findByCustomIdAndParentId(system.getId(), tenantRole.getId()).getData();

        RoleInfo roleInfo = new RoleInfo();
        roleInfo.parentId = tenantSystemRole.getId();
        roleInfo.organization = orgUnitApi.getOrganization(targetTenantId, Y9LoginUserHolder.getPersonId()).getData();

        return roleInfo;
    }

    /**
     * 复制绑定角色
     */
    private void copyBindingRoles(ItemButtonBind binding, RoleInfo roleInfo, String targetTenantId) {
        List<ItemButtonRole> roleList = itemButtonRoleService.listByItemButtonId(binding.getId());

        for (ItemButtonRole role : roleList) {
            String roleId = role.getId();
            Role oldRole = roleApi.getRole(roleId).getData();
            if (oldRole != null && oldRole.getId() != null) {
                Role newRoleTemp = roleApi.findByCustomIdAndParentId(roleId, roleInfo.parentId).getData();
                String newRoleId;

                if (newRoleTemp == null || newRoleTemp.getId() == null) {
                    newRoleId = Y9IdGenerator.genId(IdType.SNOWFLAKE);
                    // 把申请人所在的租户机构添加到角色
                    roleApi.addPerson(roleInfo.organization.getId(), newRoleId, targetTenantId);
                } else {
                    newRoleId = newRoleTemp.getId();
                }

                itemButtonRoleService.saveOrUpdate(binding.getId(), newRoleId);
            }
        }
    }

    @Override
    @Transactional
    public void copyDynamicRole(String sourceTenantId, String targetTenantId) {
        /*
         * 1、在源租户查找动态角色
         */
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        List<DynamicRole> roleList = dynamicRoleService.listAll();
        /*
         * 2、复制动态角色到目标租户
         */
        Y9LoginUserHolder.setTenantId(targetTenantId);
        for (DynamicRole role : roleList) {
            role.setTenantId(targetTenantId);
            dynamicRoleService.saveOrUpdate(role);
        }
    }

    @Override
    @Transactional
    public void copyForm(String sourceTenantId, String targetTenantId, String itemId) {
        // 1. 复制源租户的字典类型和字典值到目标租户
        copyFormOptionData(sourceTenantId, targetTenantId);
        // 2. 检查目标租户是否已存在表单绑定，如果存在则直接返回
        if (isTargetFormBindingExists(targetTenantId, itemId)) {
            return;
        }
        // 3. 从源租户获取表单相关数据
        FormCopyData formCopyData = getSourceFormData(sourceTenantId, targetTenantId, itemId);
        // 4. 保存表单数据到目标租户
        saveFormDataToTargetTenant(targetTenantId, formCopyData);
    }

    /**
     * 复制表单选项数据（字典类型和字典值）
     */
    private void copyFormOptionData(String sourceTenantId, String targetTenantId) {
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        List<Y9FormOptionClass> optionClassList = y9FormOptionClassService.listAllOptionClass();
        List<Y9FormOptionValue> optionValueList = y9FormOptionClassService.listAllOptionValue();
        Y9LoginUserHolder.setTenantId(targetTenantId);
        for (Y9FormOptionClass optionClass : optionClassList) {
            y9FormOptionClassService.saveOptionClass(optionClass);
        }
        for (Y9FormOptionValue optionValue : optionValueList) {
            y9FormOptionClassService.saveOptionValue(optionValue);
        }
    }

    /**
     * 检查目标租户是否已存在表单绑定
     */
    private boolean isTargetFormBindingExists(String targetTenantId, String itemId) {
        Y9LoginUserHolder.setTenantId(targetTenantId);
        Item item = itemService.findById(itemId);
        String processDefinitionKey = item.getWorkflowGuid();
        ProcessDefinitionModel targetProcessDefinition =
            repositoryApi.getLatestProcessDefinitionByKey(targetTenantId, processDefinitionKey).getData();
        List<Y9FormItemBind> targetFormItemBindList =
            y9FormItemBindService.listByItemIdAndProcDefId(itemId, targetProcessDefinition.getId());
        return !targetFormItemBindList.isEmpty();
    }

    /**
     * 从源租户获取表单数据
     */
    private FormCopyData getSourceFormData(String sourceTenantId, String targetTenantId, String itemId) {
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        Item item = itemService.findById(itemId);
        String processDefinitionKey = item.getWorkflowGuid();
        ProcessDefinitionModel sourceProcessDefinition =
            repositoryApi.getLatestProcessDefinitionByKey(sourceTenantId, processDefinitionKey).getData();

        ProcessDefinitionModel targetProcessDefinition =
            repositoryApi.getLatestProcessDefinitionByKey(targetTenantId, processDefinitionKey).getData();

        List<Y9FormItemBind> sourceFormItemBindList =
            y9FormItemBindService.listByItemIdAndProcDefId(itemId, sourceProcessDefinition.getId());

        FormCopyData formCopyData = new FormCopyData();
        formCopyData.targetProcessDefinitionId = targetProcessDefinition.getId();

        // 处理每个绑定的表单
        for (Y9FormItemBind bind : sourceFormItemBindList) {
            processFormBinding(formCopyData, bind, targetProcessDefinition.getId(), targetTenantId);
        }

        return formCopyData;
    }

    /**
     * 处理表单绑定数据
     */
    private void processFormBinding(FormCopyData formCopyData, Y9FormItemBind bind, String targetProcessDefinitionId,
        String targetTenantId) {
        // 设置绑定关系
        bind.setProcessDefinitionId(targetProcessDefinitionId);
        bind.setTenantId(targetTenantId);
        formCopyData.formItemBinds.add(bind);

        // 处理表单
        String formId = bind.getFormId();
        Y9Form form = y9FormService.findById(formId);
        form.setTenantId(targetTenantId);
        formCopyData.forms.add(form);

        // 处理表单元素
        List<Y9FormField> formFields = y9FormFieldService.listByFormId(formId);
        formCopyData.formFields.addAll(formFields);

        // 处理表和表字段
        processTablesAndFields(formCopyData, formFields);
    }

    /**
     * 处理表和表字段数据
     */
    private void processTablesAndFields(FormCopyData formCopyData, List<Y9FormField> formFields) {
        for (Y9FormField formField : formFields) {
            Y9Table table = y9TableService.findById(formField.getTableId());

            if (!formCopyData.processedTableIds.contains(table.getId())) {
                // 表
                table.setOldTableName(null);
                formCopyData.tables.add(table);
                formCopyData.processedTableIds.add(table.getId());

                // 表字段
                List<Y9TableField> tableFields =
                    y9TableFieldService.listByTableIdOrderByDisplay(formField.getTableId());

                for (Y9TableField tableField : tableFields) {
                    if (!formCopyData.processedTableFieldIds.contains(tableField.getId())) {
                        tableField.setOldFieldName(null);
                        formCopyData.tableFields.add(tableField);
                        formCopyData.processedTableFieldIds.add(tableField.getId());
                    }
                }
            }
        }
    }

    /**
     * 保存表单数据到目标租户
     */
    private void saveFormDataToTargetTenant(String targetTenantId, FormCopyData formCopyData) {
        Y9LoginUserHolder.setTenantId(targetTenantId);

        // 保存绑定关系
        saveFormItemBinds(formCopyData.formItemBinds);

        // 保存表单
        saveForms(formCopyData.forms);

        // 保存表单元素
        saveFormFields(formCopyData.formFields);

        // 保存表和表字段，并创建数据库表
        saveTablesAndBuildDatabase(formCopyData);
    }

    /**
     * 保存表单绑定关系
     */
    private void saveFormItemBinds(List<Y9FormItemBind> formItemBinds) {
        for (Y9FormItemBind bind : formItemBinds) {
            Y9FormItemBind existingBind = y9FormItemBindService.getById(bind.getId());
            if (existingBind == null) {
                y9FormItemBindService.save(bind);
            }
        }
    }

    /**
     * 保存表单
     */
    private void saveForms(List<Y9Form> forms) {
        for (Y9Form form : forms) {
            y9FormService.saveOrUpdate(form);
        }
    }

    /**
     * 保存表单元素
     */
    private void saveFormFields(List<Y9FormField> formFields) {
        for (Y9FormField formField : formFields) {
            y9FormFieldService.saveOrUpdate(formField);
        }
    }

    /**
     * 保存表和表字段，并创建数据库表
     */
    private void saveTablesAndBuildDatabase(FormCopyData formCopyData) {
        for (Y9Table table : formCopyData.tables) {
            if (y9TableService.findById(table.getId()) != null) {
                continue; // 表已存在，跳过
            }

            // 保存表
            if (!saveTable(table)) {
                continue; // 保存表失败，跳过该表的后续处理
            }

            // 保存表字段并创建列信息
            List<DbColumn> dbColumns = new ArrayList<>();
            for (Y9TableField tableField : formCopyData.tableFields) {
                if (tableField.getTableId().equals(table.getId())) {
                    saveTableFieldIfNotExists(tableField);

                    // 构建数据库列信息
                    DbColumn dbColumn = buildDbColumn(tableField);
                    dbColumns.add(dbColumn);
                }
            }

            // 创建数据库表
            tableManagerService.buildTable(table, dbColumns);
        }
    }

    /**
     * 保存表，返回是否保存成功
     */
    private boolean saveTable(Y9Table table) {
        try {
            y9TableService.saveOrUpdate(table);
            return true;
        } catch (Exception e) {
            LOGGER.error("保存表失败: {}", table.getId(), e);
            return false;
        }
    }

    /**
     * 保存表字段（如果不存在）
     */
    private void saveTableFieldIfNotExists(Y9TableField tableField) {
        Y9TableField existingTableField = y9TableFieldService.findById(tableField.getId());
        if (existingTableField == null) {
            y9TableFieldService.saveOrUpdate(tableField);
        }
    }

    /**
     * 构建数据库列信息
     */
    private DbColumn buildDbColumn(Y9TableField tableField) {
        DbColumn dbColumn = new DbColumn();
        dbColumn.setColumnName(tableField.getFieldName());
        dbColumn.setIsPrimaryKey(tableField.getIsSystemField());
        dbColumn.setPrimaryKey(tableField.getIsSystemField() == 1);
        dbColumn.setNullable(tableField.getIsMayNull() == 1);
        dbColumn.setTypeName(tableField.getFieldType());
        dbColumn.setDataLength(tableField.getFieldLength());
        dbColumn.setComment(tableField.getFieldCnName());
        dbColumn.setColumnNameOld(tableField.getOldFieldName());
        dbColumn.setDataPrecision(0);
        dbColumn.setDataScale(0);
        dbColumn.setDataType(0);
        dbColumn.setIsNull(tableField.getIsMayNull());
        dbColumn.setTableName(tableField.getTableName());
        return dbColumn;
    }

    @Override
    @Transactional
    public void copyItem(String sourceTenantId, String targetTenantId, String itemId) {
        /*
         * 1、在目标租户查找事项，不存在才继续复制
         */
        Y9LoginUserHolder.setTenantId(targetTenantId);
        Item targetItem = itemService.findById(itemId);
        if (null != targetItem) {
            return;
        }
        /*
         * 2、在源租户查找事项
         */
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        Item sourceItem = itemService.findById(itemId);

        /*
         * 3、复制
         */
        Y9LoginUserHolder.setTenantId(targetTenantId);
        itemService.save(sourceItem);

    }

    @Override
    @Transactional
    public void copyItemViewConf(String sourceTenantId, String targetTenantId, String itemId) {
        /*
         * 1、查找目标租户该事项是否存在视图配置
         */
        Y9LoginUserHolder.setTenantId(targetTenantId);
        List<ItemViewConf> targetList = itemViewConfService.listByItemId(itemId);
        if (!targetList.isEmpty()) {
            return;
        }
        /*
         * 2、查找源租户该事项的视图配置
         */
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        List<ItemViewConf> sourceList = itemViewConfService.listByItemId(itemId);
        /*
         * 3、复制2中的结果到目标租户
         */
        Y9LoginUserHolder.setTenantId(targetTenantId);
        for (ItemViewConf conf : sourceList) {
            itemViewConfService.saveOrUpdate(conf);
        }
    }

    @Override
    @Transactional
    public void copyOpinionFrame(String sourceTenantId, String targetTenantId, String itemId) {
        // 1. 复制源租户的意见框到目标租户
        copyOpinionFrames(sourceTenantId, targetTenantId);

        // 2. 检查目标租户是否已存在绑定关系，如果存在则直接返回
        if (isTargetOpinionFrameBindingExists(targetTenantId, itemId)) {
            return;
        }

        // 3. 从源租户获取意见框绑定数据
        OpinionFrameCopyData copyData = getSourceOpinionFrameData(sourceTenantId, targetTenantId, itemId);

        // 4. 保存意见框绑定数据到目标租户
        saveOpinionFrameDataToTargetTenant(targetTenantId, copyData);
    }

    /**
     * 复制源租户的意见框到目标租户
     *
     * @param sourceTenantId 源租户ID
     * @param targetTenantId 目标租户ID
     */
    private void copyOpinionFrames(String sourceTenantId, String targetTenantId) {
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        List<OpinionFrame> sourceOpinionFrames = opinionFrameService.listAll();

        Y9LoginUserHolder.setTenantId(targetTenantId);
        for (OpinionFrame opinionFrame : sourceOpinionFrames) {
            opinionFrame.setTenantId(targetTenantId);
            opinionFrameService.saveOrUpdate(opinionFrame);
        }
    }

    /**
     * 检查目标租户是否已存在意见框绑定关系
     *
     * @param targetTenantId 目标租户ID
     * @param itemId 事项ID
     * @return true表示已存在绑定关系，false表示不存在
     */
    private boolean isTargetOpinionFrameBindingExists(String targetTenantId, String itemId) {
        Y9LoginUserHolder.setTenantId(targetTenantId);
        Item item = itemService.findById(itemId);
        String processDefinitionKey = item.getWorkflowGuid();
        ProcessDefinitionModel targetProcessDefinition =
            repositoryApi.getLatestProcessDefinitionByKey(targetTenantId, processDefinitionKey).getData();

        List<ItemOpinionFrameBind> targetBindings =
            itemOpinionFrameBindService.listByItemIdAndProcessDefinitionId(itemId, targetProcessDefinition.getId());

        return !targetBindings.isEmpty();
    }

    /**
     * 获取源租户的意见框绑定数据
     *
     * @param sourceTenantId 源租户ID
     * @param targetTenantId 目标租户ID
     * @param itemId 事项ID
     * @return 意见框复制数据
     */
    private OpinionFrameCopyData getSourceOpinionFrameData(String sourceTenantId, String targetTenantId,
        String itemId) {
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        Item item = itemService.findById(itemId);
        String processDefinitionKey = item.getWorkflowGuid();
        ProcessDefinitionModel sourceProcessDefinition =
            repositoryApi.getLatestProcessDefinitionByKey(sourceTenantId, processDefinitionKey).getData();

        ProcessDefinitionModel targetProcessDefinition =
            repositoryApi.getLatestProcessDefinitionByKey(targetTenantId, processDefinitionKey).getData();

        List<ItemOpinionFrameBind> sourceBindings =
            itemOpinionFrameBindService.listByItemIdAndProcessDefinitionId(itemId, sourceProcessDefinition.getId());

        OpinionFrameCopyData copyData = new OpinionFrameCopyData();
        copyData.targetProcessDefinitionId = targetProcessDefinition.getId();

        // 处理每个绑定关系
        for (ItemOpinionFrameBind binding : sourceBindings) {
            binding.setProcessDefinitionId(copyData.targetProcessDefinitionId);
            binding.setTenantId(targetTenantId);
            copyData.bindings.add(binding);

            // 获取绑定的角色信息
            List<ItemOpinionFrameRole> sourceRoles =
                itemOpinionFrameRoleService.listByItemOpinionFrameId(binding.getId());
            copyData.roles.addAll(sourceRoles);
        }

        return copyData;
    }

    /**
     * 保存意见框数据到目标租户
     *
     * @param targetTenantId 目标租户ID
     * @param copyData 复制数据
     */
    private void saveOpinionFrameDataToTargetTenant(String targetTenantId, OpinionFrameCopyData copyData) {
        Y9LoginUserHolder.setTenantId(targetTenantId);

        // 保存绑定关系
        for (ItemOpinionFrameBind binding : copyData.bindings) {
            ItemOpinionFrameBind existingBind = itemOpinionFrameBindService.getById(binding.getId());
            if (existingBind == null) {
                itemOpinionFrameBindService.save(binding);
            }
        }

        // 保存角色信息并创建租户角色
        createOpinionFrameRoles(copyData.roles, targetTenantId);
    }

    /**
     * 创建意见框角色并保存到目标租户
     *
     * @param roles 角色列表
     * @param targetTenantId 目标租户ID
     */
    private void createOpinionFrameRoles(List<ItemOpinionFrameRole> roles, String targetTenantId) {
        RoleInfo roleInfo = getTargetTenantRoleInfo(targetTenantId);

        for (ItemOpinionFrameRole role : roles) {
            String roleId = role.getRoleId();
            Role oldRole = roleApi.getRole(roleId).getData();

            if (oldRole != null && oldRole.getId() != null) {
                Role newRoleTemp = roleApi.findByCustomIdAndParentId(roleId, roleInfo.parentId).getData();
                String newRoleId;

                if (newRoleTemp == null || newRoleTemp.getId() == null) {
                    newRoleId = Y9IdGenerator.genId(IdType.SNOWFLAKE);
                    // 把申请人所在的租户机构添加到角色
                    roleApi.addPerson(roleInfo.organization.getId(), newRoleId, targetTenantId);
                } else {
                    newRoleId = newRoleTemp.getId();
                }

                itemOpinionFrameRoleService.saveOrUpdate(role.getItemOpinionFrameId(), newRoleId);
            }
        }
    }

    @Override
    @Transactional
    public void copyOrganWord(String sourceTenantId, String targetTenantId, String itemId) {
        // 1. 检查并复制源租户的编号和编号配置
        if (!checkAndCopyOrganWords(sourceTenantId, targetTenantId)) {
            return;
        }
        // 2. 复制绑定和授权到目标租户
        copyOrganWordBindingsAndPermissions(sourceTenantId, targetTenantId, itemId);
    }

    /**
     * 检查并复制源租户的编号和编号配置到目标租户
     *
     * @param sourceTenantId 源租户ID
     * @param targetTenantId 目标租户ID
     * @return true表示存在数据并已复制，false表示无数据
     */
    private boolean checkAndCopyOrganWords(String sourceTenantId, String targetTenantId) {
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        List<OrganWord> sourceOrganWords = organWordService.listAll();

        // 如果源租户没有编号数据，直接返回
        if (sourceOrganWords.isEmpty()) {
            return false;
        }
        List<OrganWordProperty> sourceOrganWordProperties = organWordPropertyService.listAll();
        // 复制到目标租户
        Y9LoginUserHolder.setTenantId(targetTenantId);
        for (OrganWord organWord : sourceOrganWords) {
            organWordService.save(organWord);
        }
        for (OrganWordProperty organWordProperty : sourceOrganWordProperties) {
            organWordPropertyService.save(organWordProperty);
        }

        return true;
    }

    /**
     * 复制编号绑定和权限到目标租户
     *
     * @param sourceTenantId 源租户ID
     * @param targetTenantId 目标租户ID
     * @param itemId 事项ID
     */
    private void copyOrganWordBindingsAndPermissions(String sourceTenantId, String targetTenantId, String itemId) {
        // 检查目标租户是否已存在绑定关系
        if (isTargetOrganWordBindingExists(targetTenantId, itemId)) {
            return;
        }

        // 获取源租户的绑定关系
        List<ItemOrganWordBind> sourceBindings = getSourceOrganWordBindings(sourceTenantId, itemId);
        if (sourceBindings.isEmpty()) {
            return;
        }

        // 获取目标租户系统角色信息
        RoleInfo roleInfo = getTargetTenantRoleInfo(targetTenantId);

        // 复制绑定关系和角色权限
        copyOrganWordBindingsAndRoles(targetTenantId, itemId, sourceBindings, roleInfo);
    }

    /**
     * 检查目标租户是否已存在编号绑定关系
     *
     * @param targetTenantId 目标租户ID
     * @param itemId 事项ID
     * @return true表示已存在绑定关系，false表示不存在
     */
    private boolean isTargetOrganWordBindingExists(String targetTenantId, String itemId) {
        Y9LoginUserHolder.setTenantId(targetTenantId);
        Item item = itemService.findById(itemId);
        String processDefinitionKey = item.getWorkflowGuid();
        ProcessDefinitionModel targetProcessDefinition =
            repositoryApi.getLatestProcessDefinitionByKey(targetTenantId, processDefinitionKey).getData();

        List<ItemOrganWordBind> targetBindings =
            itemOrganWordBindService.listByItemIdAndProcessDefinitionId(itemId, targetProcessDefinition.getId());

        return !targetBindings.isEmpty();
    }

    /**
     * 获取源租户的编号绑定关系
     *
     * @param sourceTenantId 源租户ID
     * @param itemId 事项ID
     * @return 源租户的编号绑定关系列表
     */
    private List<ItemOrganWordBind> getSourceOrganWordBindings(String sourceTenantId, String itemId) {
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        Item item = itemService.findById(itemId);
        String processDefinitionKey = item.getWorkflowGuid();
        ProcessDefinitionModel sourceProcessDefinition =
            repositoryApi.getLatestProcessDefinitionByKey(sourceTenantId, processDefinitionKey).getData();

        return itemOrganWordBindService.listByItemIdAndProcessDefinitionId(itemId, sourceProcessDefinition.getId());
    }

    /**
     * 复制编号绑定关系和角色权限
     *
     * @param targetTenantId 目标租户ID
     * @param itemId 事项ID
     * @param sourceBindings 源绑定关系列表
     * @param roleInfo 角色信息
     */
    private void copyOrganWordBindingsAndRoles(String targetTenantId, String itemId,
        List<ItemOrganWordBind> sourceBindings, RoleInfo roleInfo) {
        Y9LoginUserHolder.setTenantId(targetTenantId);
        Item item = itemService.findById(itemId);
        String processDefinitionKey = item.getWorkflowGuid();
        ProcessDefinitionModel targetProcessDefinition =
            repositoryApi.getLatestProcessDefinitionByKey(targetTenantId, processDefinitionKey).getData();
        String targetProcessDefinitionId = targetProcessDefinition.getId();

        for (ItemOrganWordBind binding : sourceBindings) {
            // 保存绑定关系
            binding.setProcessDefinitionId(targetProcessDefinitionId);
            itemOrganWordBindService.save(binding);

            // 复制绑定角色
            copyOrganWordBindingRoles(binding, roleInfo, targetTenantId);
        }
    }

    /**
     * 复制编号绑定的角色权限
     *
     * @param binding 绑定关系
     * @param roleInfo 角色信息
     * @param targetTenantId 目标租户ID
     */
    private void copyOrganWordBindingRoles(ItemOrganWordBind binding, RoleInfo roleInfo, String targetTenantId) {
        List<String> roleIdList = binding.getRoleIds();

        for (String roleId : roleIdList) {
            Role oldRole = roleApi.getRole(roleId).getData();
            if (oldRole != null && oldRole.getId() != null) {
                Role newRoleTemp = roleApi.findByCustomIdAndParentId(roleId, roleInfo.parentId).getData();
                String newRoleId;

                if (newRoleTemp == null || newRoleTemp.getId() == null) {
                    newRoleId = Y9IdGenerator.genId(IdType.SNOWFLAKE);
                    // 把申请人所在的租户机构添加到角色
                    roleApi.addPerson(roleInfo.organization.getId(), newRoleId, targetTenantId);
                } else {
                    newRoleId = newRoleTemp.getId();
                }

                itemOrganWordRoleService.saveOrUpdate(binding.getId(), newRoleId);
            }
        }
    }

    @Override
    @Transactional
    public void copyPerm(String sourceTenantId, String targetTenantId, String itemId, Map<String, String> roleIdMap) {
        /*
         * 1、先查目标租户该事项是否有授权，没有再复制授权
         */
        Y9LoginUserHolder.setTenantId(targetTenantId);
        Item item = itemService.findById(itemId);
        String proDefKey = item.getWorkflowGuid();
        ProcessDefinitionModel targetProcessDefinitionModel =
            repositoryApi.getLatestProcessDefinitionByKey(targetTenantId, proDefKey).getData();
        String targetProcessDefinitionId = targetProcessDefinitionModel.getId();
        List<ItemPermission> targetipList =
            itemPermissionRepository.findByItemIdAndProcessDefinitionId(itemId, targetProcessDefinitionId);
        if (!targetipList.isEmpty()) {
            return;
        }
        /*
         * 2、查找源租户该事项最新流程定义的权限
         */
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        ProcessDefinitionModel source =
            repositoryApi.getLatestProcessDefinitionByKey(sourceTenantId, proDefKey).getData();
        String sourceId = source.getId();
        List<ItemPermission> sourceipList =
            itemPermissionRepository.findByItemIdAndProcessDefinitionId(itemId, sourceId);
        ItemPermissionEnum roleType;
        for (ItemPermission itemPermission : sourceipList) {
            roleType = itemPermission.getRoleType();
            if (roleType == ItemPermissionEnum.ROLE_DYNAMIC) {
                itemPermission.setTenantId(targetTenantId);
                itemPermission.setProcessDefinitionId(targetProcessDefinitionId);
                targetipList.add(itemPermission);
            }
            if (roleType == ItemPermissionEnum.ROLE) {
                itemPermission.setRoleId(roleIdMap.get(itemPermission.getRoleId()));
                itemPermission.setTenantId(targetTenantId);
                itemPermission.setProcessDefinitionId(targetProcessDefinitionId);
                targetipList.add(itemPermission);
            }
        }
        /*
         * 3、保存源租户该事项最新流程定义的权限到目标租户
         */
        Y9LoginUserHolder.setTenantId(targetTenantId);
        itemPermissionRepository.saveAll(targetipList);
    }

    @Override
    @Transactional
    public void copyPrintTemplate(String sourceTenantId, String targetTenantId, String itemId) {
        /*
         * 1、查找源租户是否存在打印模板
         */
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        List<PrintTemplate> printList = printTemplateService.listAll();
        if (printList.isEmpty()) {
            return;
        }
        /*
         * 2、复制源租户的打印模板到目标租户
         */
        Y9LoginUserHolder.setTenantId(targetTenantId);
        for (PrintTemplate print : printList) {
            printTemplateService.saveOrUpdate(print);
        }
        /*
         * 3、复制该事项源租户的打印模板和事项的绑定关系到目标租户
         */
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        List<ItemPrintTemplateBind> bindList = printTemplateService.listTemplateBindByItemId(itemId);
        if (bindList.isEmpty()) {
            return;
        }

        Y9LoginUserHolder.setTenantId(targetTenantId);
        List<ItemPrintTemplateBind> targetBindList = printTemplateService.listTemplateBindByItemId(itemId);
        if (!targetBindList.isEmpty()) {
            return;
        }
        for (ItemPrintTemplateBind bind : bindList) {
            printTemplateService.saveBindTemplate(itemId, bind.getTemplateId(), bind.getTemplateName(),
                bind.getTemplateUrl(), bind.getTemplateType());
        }
    }

    @Override
    @Transactional
    public void copySendButton(String sourceTenantId, String targetTenantId, String itemId) {
        // 1. 查找并复制源租户的发送按钮
        List<SendButton> sourceButtons = getSourceSendButtons(sourceTenantId);
        if (sourceButtons.isEmpty()) {
            return;
        }
        // 2. 复制发送按钮到目标租户
        copySendButtonsToTargetTenant(targetTenantId, sourceButtons);

        // 3. 复制按钮绑定关系和权限（复用copyCommonButton中的方法）
        copyButtonBindingsAndPermissions(sourceTenantId, targetTenantId, itemId, ItemButtonTypeEnum.SEND);
    }

    /**
     * 获取源租户的发送按钮列表
     *
     * @param sourceTenantId 源租户ID
     * @return 发送按钮列表
     */
    private List<SendButton> getSourceSendButtons(String sourceTenantId) {
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        return sendButtonService.listAll();
    }

    /**
     * 复制发送按钮到目标租户
     *
     * @param targetTenantId 目标租户ID
     * @param sourceButtons 源发送按钮列表
     */
    private void copySendButtonsToTargetTenant(String targetTenantId, List<SendButton> sourceButtons) {
        Y9LoginUserHolder.setTenantId(targetTenantId);
        for (SendButton button : sourceButtons) {
            sendButtonService.saveOrUpdate(button);
        }
    }

    /**
     * 复制按钮绑定关系和权限（重载方法，支持指定按钮类型）
     *
     * @param sourceTenantId 源租户ID
     * @param targetTenantId 目标租户ID
     * @param itemId 事项ID
     * @param buttonType 按钮类型
     */
    private void copyButtonBindingsAndPermissions(String sourceTenantId, String targetTenantId, String itemId,
        ItemButtonTypeEnum buttonType) {
        // 检查目标租户是否已存在绑定关系
        if (isTargetBindingExists(targetTenantId, itemId, buttonType)) {
            return;
        }
        // 获取源租户的绑定关系
        List<ItemButtonBind> sourceBindings = getSourceButtonBindings(sourceTenantId, itemId, buttonType);
        if (sourceBindings.isEmpty()) {
            return;
        }
        // 获取目标租户系统角色信息
        RoleInfo roleInfo = getTargetTenantRoleInfo(targetTenantId);
        // 复制绑定关系和角色权限
        copyBindingsAndRoles(targetTenantId, itemId, sourceBindings, roleInfo);
    }

    /**
     * 检查目标租户是否已存在指定类型的绑定关系
     *
     * @param targetTenantId 目标租户ID
     * @param itemId 事项ID
     * @param buttonType 按钮类型
     * @return true表示已存在绑定关系，false表示不存在
     */
    private boolean isTargetBindingExists(String targetTenantId, String itemId, ItemButtonTypeEnum buttonType) {
        Y9LoginUserHolder.setTenantId(targetTenantId);
        Item item = itemService.findById(itemId);
        String processDefinitionKey = item.getWorkflowGuid();
        ProcessDefinitionModel targetProcessDefinition =
            repositoryApi.getLatestProcessDefinitionByKey(targetTenantId, processDefinitionKey).getData();

        List<ItemButtonBind> targetBindings = itemButtonBindService
            .listByItemIdAndButtonTypeAndProcessDefinitionId(itemId, buttonType, targetProcessDefinition.getId());

        return !targetBindings.isEmpty();
    }

    /**
     * 获取源租户指定类型的按钮绑定关系
     *
     * @param sourceTenantId 源租户ID
     * @param itemId 事项ID
     * @param buttonType 按钮类型
     * @return 按钮绑定关系列表
     */
    private List<ItemButtonBind> getSourceButtonBindings(String sourceTenantId, String itemId,
        ItemButtonTypeEnum buttonType) {
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        Item item = itemService.findById(itemId);
        String processDefinitionKey = item.getWorkflowGuid();
        ProcessDefinitionModel sourceProcessDefinition =
            repositoryApi.getLatestProcessDefinitionByKey(sourceTenantId, processDefinitionKey).getData();

        return itemButtonBindService.listByItemIdAndButtonTypeAndProcessDefinitionId(itemId, buttonType,
            sourceProcessDefinition.getId());
    }

    /**
     * 复制绑定关系和角色权限（重载方法，支持指定按钮类型）
     *
     * @param targetTenantId 目标租户ID
     * @param itemId 事项ID
     * @param sourceBindings 源绑定关系列表
     * @param roleInfo 角色信息
     */
    private void copyBindingsAndRoles(String targetTenantId, String itemId, List<ItemButtonBind> sourceBindings,
        RoleInfo roleInfo) {
        Y9LoginUserHolder.setTenantId(targetTenantId);
        Item item = itemService.findById(itemId);
        String processDefinitionKey = item.getWorkflowGuid();
        ProcessDefinitionModel targetProcessDefinition =
            repositoryApi.getLatestProcessDefinitionByKey(targetTenantId, processDefinitionKey).getData();
        String targetProcessDefinitionId = targetProcessDefinition.getId();

        for (ItemButtonBind binding : sourceBindings) {
            // 保存绑定关系
            binding.setProcessDefinitionId(targetProcessDefinitionId);
            itemButtonBindService.save(binding);

            // 复制绑定角色
            copyBindingRoles(binding, roleInfo, targetTenantId);
        }
    }

    @Override
    @Transactional
    public void copyTabEntity(String sourceTenantId, String targetTenantId, String itemId) {
        /*
         * 1、查找源租户是否存在页签
         */
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        List<TabEntity> tabList = tabEntityService.listAll();
        if (tabList.isEmpty()) {
            return;
        }
        /*
         * 2、复制源租户的页签到目标租户
         */
        Y9LoginUserHolder.setTenantId(targetTenantId);
        for (TabEntity tab : tabList) {
            tabEntityService.saveOrUpdate(tab);
        }
        /*
         * 3、复制该事项源租户的页签和事项的绑定关系到目标租户
         */
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        Item item = itemService.findById(itemId);
        String proDefKey = item.getWorkflowGuid();
        ProcessDefinitionModel sourceProcessDefinition =
            repositoryApi.getLatestProcessDefinitionByKey(sourceTenantId, proDefKey).getData();
        String sourceProcessDefinitionId = sourceProcessDefinition.getId();
        List<ItemTabBind> tabBindList =
            itemTabBindService.listByItemIdAndProcessDefinitionId(itemId, sourceProcessDefinitionId);
        if (tabBindList.isEmpty()) {
            return;
        }
        Y9LoginUserHolder.setTenantId(targetTenantId);
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String personId = person.getPersonId(), personName = person.getName();
        ProcessDefinitionModel targetProcessDefinition =
            repositoryApi.getLatestProcessDefinitionByKey(targetTenantId, proDefKey).getData();
        String targetProcessDefinitionId = targetProcessDefinition.getId();
        List<ItemTabBind> targetTabBindList =
            itemTabBindService.listByItemIdAndProcessDefinitionId(itemId, targetProcessDefinitionId);
        if (!targetTabBindList.isEmpty()) {
            return;
        }
        for (ItemTabBind bind : tabBindList) {
            ItemTabBind oldBind = itemTabBindService.getById(bind.getId());
            if (null == oldBind) {
                bind.setProcessDefinitionId(targetProcessDefinitionId);
                bind.setTenantId(targetTenantId);
                bind.setUserId(personId);
                bind.setUserName(personName);
                itemTabBindService.save(bind);
            }
        }
    }

    @Override
    @Transactional
    public void copyTaoHongTemplate(String sourceTenantId, String targetTenantId) {
        /*
         * 1、查找源租户是否存在套红模板
         */
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        List<TaoHongTemplate> sourcettList = taoHongTemplateService.listByTenantId(sourceTenantId, "");
        if (sourcettList.isEmpty()) {
            return;
        }
        List<TaoHongTemplateType> sourcetttList = taoHongTemplateTypeService.listAll();
        /*
         * 2、复制源租户的套红模板和模板类型到目标租户
         */
        Y9LoginUserHolder.setTenantId(targetTenantId);
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        OrgUnit orgUnit = orgUnitApi.getBureau(targetTenantId, person.getPersonId()).getData();
        String bureauId = orgUnit.getId(), bureauName = orgUnit.getName();
        for (TaoHongTemplate tt : sourcettList) {
            tt.setBureauGuid(bureauId);
            tt.setBureauName(bureauName);
            taoHongTemplateService.saveOrUpdate(tt);
        }
        for (TaoHongTemplateType ttt : sourcetttList) {
            ttt.setBureauId(bureauId);
            taoHongTemplateTypeService.saveOrUpdate(ttt);
        }
    }

    @Override
    @Transactional
    public Map<String, String> copyTenantRole(String sourceTenantId, String targetTenantId, String itemId) {
        /*
         * 1、在源租户查找权限中绑定的租户的角色
         */
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        Item item = itemService.findById(itemId);
        String proDefKey = item.getWorkflowGuid();
        ProcessDefinitionModel source =
            repositoryApi.getLatestProcessDefinitionByKey(sourceTenantId, proDefKey).getData();
        String sourceId = source.getId();
        List<ItemPermission> sourceipList =
            itemPermissionRepository.findByItemIdAndProcessDefinitionId(itemId, sourceId);
        ItemPermissionEnum roleType;
        List<String> roleIdList = new ArrayList<>();
        for (ItemPermission itemPermission : sourceipList) {
            roleType = itemPermission.getRoleType();
            if (roleType == ItemPermissionEnum.ROLE) {
                roleIdList.add(itemPermission.getRoleId());
            }
        }
        /*
         * 2、获取目标租户的事项管理系统的角色
         */
        System system = systemApi.getByName(Y9Context.getSystemName()).getData();
        Role tenantRole = roleApi.getRole(targetTenantId).getData();
        Role tenantSystemRole = roleApi.findByCustomIdAndParentId(system.getId(), tenantRole.getId()).getData();
        String parentId = tenantSystemRole.getId();
        /*
         * 3、把1中查出的角色复制到目标租户中去，父节点为2中获取的角色，目标租户创建新角色时，为了避免重复创建的问题，用源角色id作为新角色的customId，每次要创建的时候，查找一下是否存在
         * 因为所有角色在同一张表，确保id的唯一性，所以复制的角色要更改角色Id,并把新老角色id的对应关系传递给权限复制，用来替换调老的角色id
         */
        Map<String, String> roleIdMap = new HashMap<>(16);
        Role oldRole, newRoleTemp;
        String newRoleId;
        Organization organization =
            orgUnitApi.getOrganization(targetTenantId, Y9LoginUserHolder.getPersonId()).getData();
        for (String roleId : roleIdList) {
            oldRole = roleApi.getRole(roleId).getData();
            if (null != oldRole && null != oldRole.getId()) {
                newRoleTemp = roleApi.findByCustomIdAndParentId(roleId, parentId).getData();
                if (null == newRoleTemp || null == newRoleTemp.getId()) {
                    newRoleId = Y9IdGenerator.genId(IdType.SNOWFLAKE);
                    /* 把申请人所在的租户机构添加到角色 */
                    roleApi.addPerson(organization.getId(), newRoleId, targetTenantId);
                } else {
                    newRoleId = newRoleTemp.getId();
                    roleIdMap.put(roleId, newRoleId);
                }
            }
        }

        return roleIdMap;
    }

    @Override
    @Transactional
    public void copyWordTemplate(String sourceTenantId, String targetTenantId, String itemId) {
        /*
         * 1、查找源租户是否存在正文模板
         */
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        List<WordTemplate> wordList = wordTemplateService.listAll();
        if (wordList.isEmpty()) {
            return;
        }
        /*
         * 2、复制源租户的模板到目标租户
         */
        Y9LoginUserHolder.setTenantId(targetTenantId);
        for (WordTemplate word : wordList) {
            wordTemplateService.saveOrUpdate(word);
        }
        /*
         * 3、复制该事项源租户的模板事项以及模板和书签的绑定关系到目标租户
         */
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        Item item = itemService.findById(itemId);
        String proDefKey = item.getWorkflowGuid();
        ProcessDefinitionModel sourceProcessDefinition =
            repositoryApi.getLatestProcessDefinitionByKey(sourceTenantId, proDefKey).getData();
        String sourceProcessDefinitionId = sourceProcessDefinition.getId();
        ItemWordTemplateBind bind =
            itemWordTemplateBindService.findByItemIdAndProcessDefinitionId(itemId, sourceProcessDefinitionId);
        if (null == bind) {
            return;
        }
        Y9LoginUserHolder.setTenantId(targetTenantId);
        ProcessDefinitionModel targetProcessDefinition =
            repositoryApi.getLatestProcessDefinitionByKey(targetTenantId, proDefKey).getData();
        String targetProcessDefinitionId = targetProcessDefinition.getId();
        ItemWordTemplateBind targetBind =
            itemWordTemplateBindService.findByItemIdAndProcessDefinitionId(itemId, targetProcessDefinitionId);
        if (null != targetBind) {
            return;
        }
        /* 3、1事项和模板的绑定关系 */
        itemWordTemplateBindService.save(itemId, targetProcessDefinitionId, bind.getTemplateId());
        /* 3、2模板和模板中的书签绑定关系 */
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        List<BookMarkBind> bookMarkBindList = bookMarkBindService.listByWordTemplateId(bind.getTemplateId());

        Y9LoginUserHolder.setTenantId(targetTenantId);
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String personId = person.getPersonId(), personName = person.getName();
        for (BookMarkBind bookMarkBind : bookMarkBindList) {
            bookMarkBind.setUserId(personId);
            bookMarkBind.setUserName(personName);
            bookMarkBindService.saveOrUpdate(bookMarkBind);
        }
    }

    @Override
    public void dataCopy(String sourceTenantId, String targetTenantId, String itemId) {
        /* 复制流程模型并部署 */
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        Item item = itemService.findById(itemId);
        Y9Result<Object> result = processDataCopyApi.copyModel(sourceTenantId, targetTenantId, item.getWorkflowGuid());
        if (result.isSuccess()) {
            LOGGER.error("复制流程模型数据失败");
            return;
        }
        /* 一复制事项 */
        self.copyItem(sourceTenantId, targetTenantId, itemId);
        /* 二复制动态角色 */
        self.copyDynamicRole(sourceTenantId, targetTenantId);
        /* 三复制租户的角色 */
        Map<String, String> roleIdMap = self.copyTenantRole(sourceTenantId, targetTenantId, itemId);
        /* 四复制授权 */
        self.copyPerm(sourceTenantId, targetTenantId, itemId, roleIdMap);
        /* 五复制表单 */
        self.copyForm(sourceTenantId, targetTenantId, itemId);
        /* 六复制意见框及绑定关系和授权关系 */
        self.copyOpinionFrame(sourceTenantId, targetTenantId, itemId);
        /* 七复制事项视图配置 */
        self.copyItemViewConf(sourceTenantId, targetTenantId, itemId);
        /* 八复制正文模板及和事项的绑定关系以及模板中书签的绑定关系 */
        // self.copyWordTemplate(sourceTenantId, targetTenantId, itemId);
        /* 九复制打印模板及和事项的绑定关系 */
        // self.copyPrintTemplate(sourceTenantId, targetTenantId, itemId);
        /* 十复制页签及和事项的绑定关系 */
        // self.copyTabEntity(sourceTenantId, targetTenantId, itemId);
        /* 十一套红模板 */
        // self.copyTaoHongTemplate(sourceTenantId, targetTenantId);
        /* 十二日历配置 */
        self.copyCalendarConfig(sourceTenantId, targetTenantId);
        /* 十三编号配置 */
        // self.copyOrganWord(sourceTenantId, targetTenantId, itemId);
        /* 十四普通按钮 */
        self.copyCommonButton(sourceTenantId, targetTenantId, itemId);
        /* 十五发送按钮 */
        self.copySendButton(sourceTenantId, targetTenantId, itemId);
    }

    @Override
    public void dataCopy4System(String sourceTenantId, String targetTenantId, String systemName) throws Exception {
        List<Item> itemList = itemService.findBySystemName(systemName);
        for (Item item : itemList) {
            self.dataCopy(sourceTenantId, targetTenantId, item.getId());
        }
    }

    /**
     * 意见框复制数据容器类
     */
    private static class OpinionFrameCopyData {
        List<ItemOpinionFrameBind> bindings = new ArrayList<>();
        List<ItemOpinionFrameRole> roles = new ArrayList<>();
        String targetProcessDefinitionId;
    }

    /**
     * 表单复制数据容器类
     */
    private static class FormCopyData {
        List<Y9FormItemBind> formItemBinds = new ArrayList<>();
        List<Y9Form> forms = new ArrayList<>();
        List<Y9FormField> formFields = new ArrayList<>();
        List<Y9Table> tables = new ArrayList<>();
        List<Y9TableField> tableFields = new ArrayList<>();
        Set<String> processedTableIds = new HashSet<>();
        Set<String> processedTableFieldIds = new HashSet<>();
        String targetProcessDefinitionId;
    }

    /**
     * 角色信息容器类
     */
    private static class RoleInfo {
        String parentId;
        Organization organization;
    }
}