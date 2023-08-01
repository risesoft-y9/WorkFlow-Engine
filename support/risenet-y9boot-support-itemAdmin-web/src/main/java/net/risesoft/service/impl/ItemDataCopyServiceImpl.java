package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.api.org.OrgUnitApi;
import net.risesoft.api.org.PersonApi;
import net.risesoft.api.permission.RoleApi;
import net.risesoft.entity.BookMarkBind;
import net.risesoft.entity.CalendarConfig;
import net.risesoft.entity.CommonButton;
import net.risesoft.entity.DynamicRole;
import net.risesoft.entity.ItemButtonBind;
import net.risesoft.entity.ItemButtonRole;
import net.risesoft.entity.ItemOpinionFrameBind;
import net.risesoft.entity.ItemOpinionFrameRole;
import net.risesoft.entity.ItemOrganWordBind;
import net.risesoft.entity.ItemPermission;
import net.risesoft.entity.ItemPrintTemplateBind;
import net.risesoft.entity.ItemTabBind;
import net.risesoft.entity.ItemViewConf;
import net.risesoft.entity.ItemWordTemplateBind;
import net.risesoft.entity.OpinionFrame;
import net.risesoft.entity.OrganWord;
import net.risesoft.entity.OrganWordProperty;
import net.risesoft.entity.PrintTemplate;
import net.risesoft.entity.SendButton;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.entity.TabEntity;
import net.risesoft.entity.TaoHongTemplate;
import net.risesoft.entity.TaoHongTemplateType;
import net.risesoft.entity.WordTemplate;
import net.risesoft.entity.Y9FormItemBind;
import net.risesoft.entity.form.Y9Form;
import net.risesoft.entity.form.Y9FormField;
import net.risesoft.entity.form.Y9FormOptionClass;
import net.risesoft.entity.form.Y9FormOptionValue;
import net.risesoft.entity.form.Y9Table;
import net.risesoft.entity.form.Y9TableField;
import net.risesoft.entity.form.Y9ValidType;
import net.risesoft.enums.ItemButtonTypeEnum;
import net.risesoft.enums.ItemPermissionEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.OrgUnit;
import net.risesoft.model.Organization;
import net.risesoft.model.Role;
import net.risesoft.model.System;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.jpa.ItemPermissionRepository;
import net.risesoft.service.BookMarkBindService;
import net.risesoft.service.CalendarConfigService;
import net.risesoft.service.CommonButtonService;
import net.risesoft.service.DynamicRoleService;
import net.risesoft.service.ItemButtonBindService;
import net.risesoft.service.ItemButtonRoleService;
import net.risesoft.service.ItemDataCopyService;
import net.risesoft.service.ItemOpinionFrameBindService;
import net.risesoft.service.ItemOpinionFrameRoleService;
import net.risesoft.service.ItemOrganWordBindService;
import net.risesoft.service.ItemOrganWordRoleService;
import net.risesoft.service.ItemTabBindService;
import net.risesoft.service.ItemViewConfService;
import net.risesoft.service.ItemWordTemplateBindService;
import net.risesoft.service.OpinionFrameService;
import net.risesoft.service.OrganWordPropertyService;
import net.risesoft.service.OrganWordService;
import net.risesoft.service.PrintTemplateService;
import net.risesoft.service.SendButtonService;
import net.risesoft.service.SpmApproveItemService;
import net.risesoft.service.TabEntityService;
import net.risesoft.service.TaoHongTemplateService;
import net.risesoft.service.TaoHongTemplateTypeService;
import net.risesoft.service.WordTemplateService;
import net.risesoft.service.Y9FormItemBindService;
import net.risesoft.service.form.TableManagerService;
import net.risesoft.service.form.Y9FormFieldService;
import net.risesoft.service.form.Y9FormOptionClassService;
import net.risesoft.service.form.Y9FormService;
import net.risesoft.service.form.Y9TableFieldService;
import net.risesoft.service.form.Y9TableService;
import net.risesoft.service.form.Y9ValidTypeService;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.sqlddl.DbColumn;

import y9.client.platform.resource.SystemApiClient;
import y9.client.rest.processadmin.RepositoryApiClient;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service(value = "itemDataCopyService")
public class ItemDataCopyServiceImpl implements ItemDataCopyService {

    @Autowired
    private SpmApproveItemService itemService;

    @Autowired
    private DynamicRoleService dynamicRoleService;

    @Autowired
    private ItemPermissionRepository itemPermissionRepository;

    @Autowired
    private Y9FormItemBindService y9FormItemBindService;

    @Autowired
    private Y9FormService y9FormService;

    @Autowired
    private Y9FormFieldService y9FormFieldService;

    @Autowired
    private Y9FormOptionClassService y9FormOptionClassService;

    @Autowired
    private Y9ValidTypeService y9ValidTypeService;

    @Autowired
    private Y9TableService y9TableService;

    @Autowired
    private Y9TableFieldService y9TableFieldService;

    @Autowired
    private TableManagerService tableManagerService;

    @Autowired
    private OpinionFrameService opinionFrameService;

    @Autowired
    private ItemOpinionFrameBindService itemOpinionFrameBindService;

    @Autowired
    private ItemOpinionFrameRoleService itemOpinionFrameRoleService;

    @Autowired
    private ItemViewConfService itemViewConfService;

    @Autowired
    private WordTemplateService wordTemplateService;

    @Autowired
    private ItemWordTemplateBindService itemWordTemplateBindService;

    @Autowired
    private BookMarkBindService bookMarkBindService;

    @Autowired
    private PrintTemplateService printTemplateService;

    @Autowired
    private TabEntityService tabEntityService;

    @Autowired
    private ItemTabBindService itemTabBindService;

    @Autowired
    private CalendarConfigService calendarConfigService;

    @Autowired
    private TaoHongTemplateService taoHongTemplateService;

    @Autowired
    private TaoHongTemplateTypeService taoHongTemplateTypeService;

    @Autowired
    private OrganWordService organWordService;

    @Autowired
    private OrganWordPropertyService organWordPropertyService;

    @Autowired
    private ItemOrganWordBindService itemOrganWordBindService;

    @Autowired
    private ItemOrganWordRoleService itemOrganWordRoleService;

    @Autowired
    private CommonButtonService commonButtonService;

    @Autowired
    private SendButtonService sendButtonService;

    @Autowired
    private ItemButtonBindService itemButtonBindService;

    @Autowired
    private ItemButtonRoleService itemButtonRoleService;

    @Autowired
    private RepositoryApiClient repositoryManager;

    @Autowired
    private SystemApiClient systemEntityManager;

    @Autowired
    private RoleApi roleManager;

    @Autowired
    private OrgUnitApi orgUnitManager;

    @Autowired
    private PersonApi personManager;

    @Override
    @Transactional(readOnly = false)
    public void copyCalendarConfig(String sourceTenantId, String targetTenantId) {
        /**
         * 1、查找源租户是否存在日历配置
         */
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        CalendarConfig sourcecc = calendarConfigService.findByYear(String.valueOf(Calendar.YEAR));
        if (null == sourcecc) {
            return;
        }
        /**
         * 2、复制源租户的日历配置到目标租户
         */
        Y9LoginUserHolder.setTenantId(targetTenantId);
        CalendarConfig targetcc = calendarConfigService.findByYear(String.valueOf(Calendar.YEAR));
        if (null != targetcc) {
            return;
        }
        calendarConfigService.saveOrUpdate(sourcecc);
    }

    @Override
    @Transactional(readOnly = false)
    public void copyCommonButton(String sourceTenantId, String targetTenantId, String itemId) {
        /**
         * 1、查找源租户是否存在普通按钮
         */
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        List<CommonButton> sourcecbList = commonButtonService.findAll();
        if (sourcecbList.isEmpty()) {
            return;
        }
        /**
         * 2、复制源租户的普通按钮到目标租户
         */
        Y9LoginUserHolder.setTenantId(targetTenantId);
        for (CommonButton cb : sourcecbList) {
            commonButtonService.saveOrUpdate(cb);
        }
        /**
         * 3、复制该事项源租户的普通按钮和事项的绑定关系以及权限到目标租户
         */
        /**
         * 3.1、先查找绑定关系
         */
        Y9LoginUserHolder.setTenantId(targetTenantId);
        SpmApproveItem item = itemService.findById(itemId);
        String proDefKey = item.getWorkflowGuid();
        ProcessDefinitionModel targetpd = repositoryManager.getLatestProcessDefinitionByKey(targetTenantId, proDefKey);
        String targetpdId = targetpd.getId();

        List<ItemButtonBind> targetBindList = itemButtonBindService.findList(itemId, ItemButtonTypeEnum.COMMON.getValue(), targetpdId);
        if (!targetBindList.isEmpty()) {
            return;
        }
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        ProcessDefinitionModel sourcepd = repositoryManager.getLatestProcessDefinitionByKey(sourceTenantId, proDefKey);
        String sourcepdId = sourcepd.getId();
        List<ItemButtonBind> sourceBindList = itemButtonBindService.findList(itemId, ItemButtonTypeEnum.COMMON.getValue(), sourcepdId);
        if (sourceBindList.isEmpty()) {
            return;
        }
        /**
         * 3.2、获取目标租户的事项管理系统的角色
         */
        System system = systemEntityManager.getByName(Y9Context.getSystemName());
        Role tenantRole = roleManager.getRole(targetTenantId);
        Role tenantSystemRole = roleManager.findByCustomIdAndParentId(system.getId(), tenantRole.getId());
        String parentId = tenantSystemRole.getId();

        /**
         * 3.3、先保存绑定关系再更新绑定的角色
         * 把绑定的角色复制到目标租户中去，父节点为3.2中获取的角色，目标租户创建新角色时，为了避免重复创建的问题，用源角色id作为新角色的customId，每次要创建的时候，查找一下是否存在
         * 因为所有角色在同一张表，确保id的唯一性，所以复制的角色要更改角色Id,并把新老角色id的对应关系传递给权限复制，用来替换调老的角色id
         */
        List<ItemButtonRole> roleList = null;
        Role oldRole = null, newRoleTemp = null;
        String newRoleId = null, roleId = null;
        Organization organization = orgUnitManager.getOrganization(targetTenantId, Y9LoginUserHolder.getPersonId());
        for (ItemButtonBind bind : sourceBindList) {
            /** 保存绑定关系 */
            bind.setProcessDefinitionId(targetpdId);
            itemButtonBindService.save(bind);

            /** 更新绑定角色 */
            roleList = itemButtonRoleService.findByItemButtonId(bind.getId());
            for (ItemButtonRole role : roleList) {
                roleId = role.getId();
                oldRole = roleManager.getRole(roleId);
                if (null != oldRole && null != oldRole.getId()) {
                    newRoleTemp = roleManager.findByCustomIdAndParentId(roleId, parentId);
                    if (null == newRoleTemp || null == newRoleTemp.getId()) {
                        newRoleId = Y9IdGenerator.genId(IdType.SNOWFLAKE);
                        // TODO
                        /** 把申请人所在的租户机构添加到角色 */
                        roleManager.addPerson(organization.getId(), newRoleId, targetTenantId);
                    } else {
                        newRoleId = newRoleTemp.getId();
                    }

                    itemButtonRoleService.saveOrUpdate(bind.getId(), newRoleId);
                }
            }
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void copyDynamicRole(String sourceTenantId, String targetTenantId) {
        /**
         * 1、在源租户查找动态角色
         */
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        List<DynamicRole> roleList = dynamicRoleService.findAll();
        /**
         * 2、复制动态角色到目标租户
         */
        Y9LoginUserHolder.setTenantId(targetTenantId);
        for (DynamicRole role : roleList) {
            role.setTenantId(targetTenantId);
            dynamicRoleService.saveOrUpdate(role);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void copyForm(String sourceTenantId, String targetTenantId, String itemId) {
        /**
         * 1、源租户的字典类型和字典值，
         */
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        List<Y9FormOptionClass> targetOptionClassList = y9FormOptionClassService.findAllOptionClass();
        List<Y9FormOptionValue> targetOptionValueList = y9FormOptionClassService.findAllOptionValue();
        Y9LoginUserHolder.setTenantId(targetTenantId);
        for (Y9FormOptionClass y9FormOptionClass : targetOptionClassList) {
            y9FormOptionClassService.saveOptionClass(y9FormOptionClass);
        }
        for (Y9FormOptionValue y9FormOptionValue : targetOptionValueList) {
            y9FormOptionClassService.saveOptionValue(y9FormOptionValue);
        }

        /**
         * 2、源租户的表单元素的验证规则
         */
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        List<Y9ValidType> sourceY9ValidTypeList = y9ValidTypeService.findAll();
        List<Y9ValidType> targetY9ValidTypeList = new ArrayList<>();
        for (Y9ValidType validType : sourceY9ValidTypeList) {
            validType.setTenantId(targetTenantId);
            targetY9ValidTypeList.add(validType);
        }
        Y9LoginUserHolder.setTenantId(targetTenantId);
        for (Y9ValidType validType : targetY9ValidTypeList) {
            y9ValidTypeService.saveOrUpdate(validType);
        }
        /**
         * 3、 先查目标租户该事项是否有绑定表单，没有再复制授权
         */
        Y9LoginUserHolder.setTenantId(targetTenantId);
        SpmApproveItem item = itemService.findById(itemId);
        String proDefKey = item.getWorkflowGuid();
        ProcessDefinitionModel targetpd = repositoryManager.getLatestProcessDefinitionByKey(targetTenantId, proDefKey);
        String targetpdId = targetpd.getId();
        List<Y9FormItemBind> targetFormItemBindList = y9FormItemBindService.findByItemIdAndProcDefId(itemId, targetpdId);
        if (!targetFormItemBindList.isEmpty()) {
            return;
        }
        /**
         * 4、查找源租户该事项最新流程定义的绑定的表单，查找表单对应的所有元素并在第三部保存至目标租户
         */
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        ProcessDefinitionModel sourcepd = repositoryManager.getLatestProcessDefinitionByKey(sourceTenantId, proDefKey);
        String sourcepdId = sourcepd.getId();
        List<Y9FormItemBind> sourceFormItemBindList = y9FormItemBindService.findByItemIdAndProcDefId(itemId, sourcepdId);
        List<Y9Form> targetFormList = new ArrayList<>();
        List<Y9FormField> targetY9FormElementList = new ArrayList<>();
        List<Y9Table> targetY9TableList = new ArrayList<>();
        StringBuilder targetY9TableIdsb = new StringBuilder();
        List<Y9TableField> targetY9TableFieldList = new ArrayList<>();
        StringBuilder targetY9TableFieldIdsb = new StringBuilder();
        Y9Form sourceY9Form = null;
        String sourceFormId = null;
        for (Y9FormItemBind bind : sourceFormItemBindList) {
            /**
             * 4.1、绑定关系
             */
            bind.setProcessDefinitionId(targetpdId);
            bind.setTenantId(targetTenantId);
            targetFormItemBindList.add(bind);

            /**
             * 4.2、表单
             */
            sourceFormId = bind.getFormId();
            sourceY9Form = y9FormService.findById(sourceFormId);
            sourceY9Form.setTenantId(targetTenantId);
            targetFormList.add(sourceY9Form);
            /**
             * 4.3、表单元素
             */
            targetY9FormElementList = y9FormFieldService.findByFormId(sourceFormId);
            /**
             * 4.4、表单元素权限先不复制，因为牵扯到读写的角色，后面需要再复制
             */
            /**
             * 4.5、表和表字段
             */
            for (Y9FormField y9FormElement : targetY9FormElementList) {
                Y9Table y9TableTemp = y9TableService.findById(y9FormElement.getTableId());
                if (!targetY9TableIdsb.toString().contains(y9TableTemp.getId())) {
                    /** 表 */
                    y9TableTemp.setOldTableName(null);
                    targetY9TableList.add(y9TableTemp);
                    targetY9TableIdsb.append(y9TableTemp.getId());
                    /** 表字段 */
                    List<Y9TableField> y9TableFieldListTemp = y9TableFieldService.searchFieldsByTableId(y9FormElement.getTableId());
                    for (Y9TableField y9TableField : y9TableFieldListTemp) {
                        if (!targetY9TableFieldIdsb.toString().contains(y9TableField.getId())) {
                            y9TableField.setOldFieldName(null);
                            targetY9TableFieldList.add(y9TableField);
                            targetY9TableFieldIdsb.append(y9TableField.getId());
                        }
                    }
                }
            }
        }

        /**
         * 5、保存源租户该事项最新流程定义的权限到目标租户
         */
        Y9LoginUserHolder.setTenantId(targetTenantId);
        for (Y9FormItemBind bind : targetFormItemBindList) {
            Y9FormItemBind y9FormItemBindTemp = y9FormItemBindService.findOne(bind.getId());
            if (null == y9FormItemBindTemp) {
                y9FormItemBindService.save(bind);
            }
        }

        for (Y9Form y9Form : targetFormList) {
            y9FormService.saveOrUpdate(y9Form);
        }

        for (Y9FormField y9FormElement : targetY9FormElementList) {
            y9FormFieldService.saveOrUpdate(y9FormElement);
        }

        for (Y9Table y9Table : targetY9TableList) {
            Y9Table y9TableTemp = y9TableService.findById(y9Table.getId());
            if (null == y9TableTemp) {
                /** 保存表元素数据 */
                try {
                    y9TableService.saveOrUpdate(y9Table);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                /** 保存表字段元素数据并创建列信息 */
                List<DbColumn> dbcs = new ArrayList<>();
                for (Y9TableField y9TableField : targetY9TableFieldList) {
                    if (y9TableField.getTableId().equals(y9Table.getId())) {
                        Y9TableField y9TableFieldTemp = y9TableFieldService.findById(y9TableField.getId());
                        if (null == y9TableFieldTemp) {
                            y9TableFieldService.saveOrUpdate(y9TableField);
                        }
                    }
                    DbColumn dbColumn = new DbColumn();
                    dbColumn.setColumnName(y9TableField.getFieldName());
                    dbColumn.setIsPrimaryKey(y9TableField.getIsSystemField());
                    dbColumn.setPrimaryKey(y9TableField.getIsSystemField() == 1 ? true : false);
                    dbColumn.setNullable(y9TableField.getIsMayNull() == 1 ? true : false);
                    dbColumn.setTypeName(y9TableField.getFieldType());
                    dbColumn.setDataLength(y9TableField.getFieldLength());
                    dbColumn.setComment(y9TableField.getFieldCnName());
                    dbColumn.setColumnNameOld(y9TableField.getOldFieldName());
                    dbColumn.setDataPrecision(0);
                    dbColumn.setDataScale(0);
                    dbColumn.setDataType(0);
                    dbColumn.setIsNull(y9TableField.getIsMayNull());
                    dbColumn.setTableName(y9TableField.getTableName());
                    dbcs.add(dbColumn);
                }
                /** 创建数据库表 */
                tableManagerService.buildTable(y9Table, dbcs);
            }
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void copyItem(String sourceTenantId, String targetTenantId, String itemId) {
        /**
         * 1、在目标租户查找事项，不存在才继续复制
         */
        Y9LoginUserHolder.setTenantId(targetTenantId);
        SpmApproveItem targetItem = itemService.findById(itemId);
        if (null != targetItem) {
            return;
        }
        /**
         * 2、在源租户查找事项
         */
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        SpmApproveItem sourceItem = itemService.findById(itemId);

        /**
         * 3、复制
         */
        Y9LoginUserHolder.setTenantId(targetTenantId);
        itemService.save(sourceItem);

    }

    @Override
    @Transactional(readOnly = false)
    public void copyItemViewConf(String sourceTenantId, String targetTenantId, String itemId) {
        /**
         * 1、查找目标租户该事项是否存在视图配置
         */
        Y9LoginUserHolder.setTenantId(targetTenantId);
        List<ItemViewConf> targetList = itemViewConfService.findByItemId(itemId);
        if (!targetList.isEmpty()) {
            return;
        }
        /**
         * 2、查找源租户该事项的视图配置
         */
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        List<ItemViewConf> sourceList = itemViewConfService.findByItemId(itemId);
        /**
         * 3、复制2中的结果到目标租户
         */
        Y9LoginUserHolder.setTenantId(targetTenantId);
        for (ItemViewConf conf : sourceList) {
            itemViewConfService.saveOrUpdate(conf);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void copyOpinionFrame(String sourceTenantId, String targetTenantId, String itemId) {
        /**
         * 1、先复制意见框
         */
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        List<OpinionFrame> ofList = opinionFrameService.findAll();

        Y9LoginUserHolder.setTenantId(targetTenantId);
        for (OpinionFrame of : ofList) {
            of.setTenantId(targetTenantId);
            opinionFrameService.saveOrUpdate(of);
        }

        /**
         * 2、先判断目标租户的事项是否绑定了意见框，绑定了则不再复制
         */
        Y9LoginUserHolder.setTenantId(targetTenantId);
        SpmApproveItem item = itemService.findById(itemId);
        String proDefKey = item.getWorkflowGuid();
        ProcessDefinitionModel targetpd = repositoryManager.getLatestProcessDefinitionByKey(targetTenantId, proDefKey);
        String targetpdId = targetpd.getId();
        List<ItemOpinionFrameBind> targetBindList = itemOpinionFrameBindService.findByItemIdAndProcessDefinitionId(itemId, targetpdId);
        if (!targetBindList.isEmpty()) {
            return;
        }
        /**
         * 3、查找源租户该事项最新流程定义绑定的意见框及角色
         */
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        ProcessDefinitionModel sourcepd = repositoryManager.getLatestProcessDefinitionByKey(sourceTenantId, proDefKey);
        String sourcepdId = sourcepd.getId();
        List<ItemOpinionFrameBind> sourceBindList = itemOpinionFrameBindService.findByItemIdAndProcessDefinitionId(itemId, sourcepdId);
        List<ItemOpinionFrameRole> targetRoleList = new ArrayList<>();
        for (ItemOpinionFrameBind bind : sourceBindList) {
            bind.setProcessDefinitionId(targetpdId);
            bind.setTenantId(targetTenantId);
            targetBindList.add(bind);

            List<ItemOpinionFrameRole> sourceRoleListTemp = itemOpinionFrameRoleService.findByItemOpinionFrameId(bind.getId());
            targetRoleList.addAll(sourceRoleListTemp);
        }
        /**
         * 4、保存源租户该事项最新流程定义的绑定的意见框
         */
        Y9LoginUserHolder.setTenantId(targetTenantId);
        for (ItemOpinionFrameBind bind : targetBindList) {
            ItemOpinionFrameBind bindTemp = itemOpinionFrameBindService.findOne(bind.getId());
            if (null == bindTemp) {
                itemOpinionFrameBindService.save(bind);
            }
        }

        /**
         * 5、保存源租户该事项最新流程定义的绑定的角色并把角色在租户角色中创建
         */
        System system = systemEntityManager.getByName(Y9Context.getSystemName());
        Role tenantRole = roleManager.getRole(targetTenantId);
        Role tenantSystemRole = roleManager.findByCustomIdAndParentId(system.getId(), tenantRole.getId());
        String parentId = tenantSystemRole.getId();
        Role oldRole = null, newRoleTemp = null;
        String newRoleId = null, roleId = null;
        Organization organization = orgUnitManager.getOrganization(targetTenantId, Y9LoginUserHolder.getPersonId());
        for (ItemOpinionFrameRole iRole : targetRoleList) {
            roleId = iRole.getRoleId();
            oldRole = roleManager.getRole(roleId);
            if (null != oldRole && null != oldRole.getId()) {
                newRoleTemp = roleManager.findByCustomIdAndParentId(roleId, parentId);
                if (null == newRoleTemp || null == newRoleTemp.getId()) {
                    newRoleId = Y9IdGenerator.genId(IdType.SNOWFLAKE);
                    // TODO
                    /** 把申请人所在的租户机构添加到角色 */
                    roleManager.addPerson(organization.getId(), newRoleId, targetTenantId);
                } else {
                    newRoleId = newRoleTemp.getId();
                }
            }

            itemOpinionFrameRoleService.saveOrUpdate(iRole.getItemOpinionFrameId(), newRoleId);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void copyOrganWord(String sourceTenantId, String targetTenantId, String itemId) {
        /**
         * 1、查找源租户是否存在编号
         */
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        List<OrganWord> sourceowList = organWordService.findAll();
        if (sourceowList.isEmpty()) {
            return;
        }
        List<OrganWordProperty> sourceowpList = organWordPropertyService.findAll();
        /**
         * 2、复制源租户的编号和编号配置到目标租户
         */
        Y9LoginUserHolder.setTenantId(targetTenantId);
        for (OrganWord ow : sourceowList) {
            organWordService.save(ow);
        }
        for (OrganWordProperty owp : sourceowpList) {
            organWordPropertyService.save(owp);
        }

        /**
         * 3、复制绑定和授权到目标租户
         */
        Y9LoginUserHolder.setTenantId(targetTenantId);
        SpmApproveItem item = itemService.findById(itemId);
        String proDefKey = item.getWorkflowGuid();
        ProcessDefinitionModel targetpd = repositoryManager.getLatestProcessDefinitionByKey(targetTenantId, proDefKey);
        String targetpdId = targetpd.getId();
        List<ItemOrganWordBind> targetBindList = itemOrganWordBindService.findByItemIdAndProcessDefinitionId(itemId, targetpdId);
        if (!targetBindList.isEmpty()) {
            return;
        }
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        ProcessDefinitionModel sourcepd = repositoryManager.getLatestProcessDefinitionByKey(targetTenantId, proDefKey);
        String sourcepdId = sourcepd.getId();
        List<ItemOrganWordBind> sourceBindList = itemOrganWordBindService.findByItemIdAndProcessDefinitionId(itemId, sourcepdId);
        if (sourceBindList.isEmpty()) {
            return;
        }
        /**
         * 3.2、获取目标租户的事项管理系统的角色
         */
        System system = systemEntityManager.getByName(Y9Context.getSystemName());
        Role tenantRole = roleManager.getRole(targetTenantId);
        Role tenantSystemRole = roleManager.findByCustomIdAndParentId(system.getId(), tenantRole.getId());
        String parentId = tenantSystemRole.getId();

        /**
         * 3.3、先保存绑定关系再更新绑定的角色
         * 把绑定的角色复制到目标租户中去，父节点为3.2中获取的角色，目标租户创建新角色时，为了避免重复创建的问题，用源角色id作为新角色的customId，每次要创建的时候，查找一下是否存在
         * 因为所有角色在同一张表，确保id的唯一性，所以复制的角色要更改角色Id,并把新老角色id的对应关系传递给权限复制，用来替换调老的角色id
         */
        Role oldRole = null, newRoleTemp = null;
        String newRoleId = null;
        List<String> roleIdList = null;
        Organization organization = orgUnitManager.getOrganization(targetTenantId, Y9LoginUserHolder.getPersonId());
        for (ItemOrganWordBind bind : sourceBindList) {
            /** 保存绑定关系 */
            bind.setProcessDefinitionId(targetpdId);
            itemOrganWordBindService.save(bind);

            /** 更新绑定角色 */
            roleIdList = bind.getRoleIds();
            for (String roleId : roleIdList) {
                oldRole = roleManager.getRole(roleId);
                if (null != oldRole && null != oldRole.getId()) {
                    newRoleTemp = roleManager.findByCustomIdAndParentId(roleId, parentId);
                    if (null == newRoleTemp || null == newRoleTemp.getId()) {
                        newRoleId = Y9IdGenerator.genId(IdType.SNOWFLAKE);
                        // TODO
                        /** 把申请人所在的租户机构添加到角色 */
                        roleManager.addPerson(organization.getId(), newRoleId, targetTenantId);
                    } else {
                        newRoleId = newRoleTemp.getId();
                    }

                    itemOrganWordRoleService.saveOrUpdate(bind.getId(), newRoleId);
                }
            }
        }

    }

    @Override
    @Transactional(readOnly = false)
    public void copyPerm(String sourceTenantId, String targetTenantId, String itemId, Map<String, String> roleIdMap) {
        /**
         * 1、先查目标租户该事项是否有授权，没有再复制授权
         */
        Y9LoginUserHolder.setTenantId(targetTenantId);
        SpmApproveItem item = itemService.findById(itemId);
        String proDefKey = item.getWorkflowGuid();
        ProcessDefinitionModel targetpd = repositoryManager.getLatestProcessDefinitionByKey(targetTenantId, proDefKey);
        String targetpdId = targetpd.getId();
        List<ItemPermission> targetipList = itemPermissionRepository.findByItemIdAndProcessDefinitionId(itemId, targetpdId);
        if (!targetipList.isEmpty()) {
            return;
        }

        /**
         * 2、查找源租户该事项最新流程定义的权限
         */
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        ProcessDefinitionModel sourcepd = repositoryManager.getLatestProcessDefinitionByKey(sourceTenantId, proDefKey);
        String sourcepdId = sourcepd.getId();
        List<ItemPermission> sourceipList = itemPermissionRepository.findByItemIdAndProcessDefinitionId(itemId, sourcepdId);
        int roleType = 0;
        for (ItemPermission itemPermission : sourceipList) {
            roleType = itemPermission.getRoleType();
            if (roleType == ItemPermissionEnum.DYNAMICROLE.getValue()) {
                itemPermission.setTenantId(targetTenantId);
                itemPermission.setProcessDefinitionId(targetpdId);
                targetipList.add(itemPermission);
            }
            if (roleType == ItemPermissionEnum.ROLE.getValue()) {
                itemPermission.setRoleId(roleIdMap.get(itemPermission.getRoleId()));
                itemPermission.setTenantId(targetTenantId);
                itemPermission.setProcessDefinitionId(targetpdId);
                targetipList.add(itemPermission);
            }
        }
        /**
         * 3、保存源租户该事项最新流程定义的权限到目标租户
         */
        Y9LoginUserHolder.setTenantId(targetTenantId);
        for (ItemPermission itemPermission : targetipList) {
            itemPermissionRepository.save(itemPermission);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void copyPrintTemplate(String sourceTenantId, String targetTenantId, String itemId) {
        /**
         * 1、查找源租户是否存在打印模板
         */
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        List<PrintTemplate> printList = printTemplateService.findAll();
        if (printList.isEmpty()) {
            return;
        }
        /**
         * 2、复制源租户的打印模板到目标租户
         */
        Y9LoginUserHolder.setTenantId(targetTenantId);
        for (PrintTemplate print : printList) {
            printTemplateService.saveOrUpdate(print);
        }
        /**
         * 3、复制该事项源租户的打印模板和事项的绑定关系到目标租户
         */
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        List<ItemPrintTemplateBind> bindList = printTemplateService.getTemplateBindList(itemId);
        if (bindList.isEmpty()) {
            return;
        }

        Y9LoginUserHolder.setTenantId(targetTenantId);
        List<ItemPrintTemplateBind> targetBindList = printTemplateService.getTemplateBindList(itemId);
        if (!targetBindList.isEmpty()) {
            return;
        }
        for (ItemPrintTemplateBind bind : bindList) {
            printTemplateService.saveBindTemplate(itemId, bind.getTemplateId(), bind.getTemplateName(), bind.getTemplateUrl(), bind.getTemplateType());
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void copySendButton(String sourceTenantId, String targetTenantId, String itemId) {
        /**
         * 1、查找源租户是否存在发送按钮
         */
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        List<SendButton> sourcesbList = sendButtonService.findAll();
        if (sourcesbList.isEmpty()) {
            return;
        }
        /**
         * 2、复制源租户的发送按钮到目标租户
         */
        Y9LoginUserHolder.setTenantId(targetTenantId);
        for (SendButton sb : sourcesbList) {
            sendButtonService.saveOrUpdate(sb);
        }
        /**
         * 3、复制该事项源租户的发送按钮和事项的绑定关系以及权限到目标租户
         */
        /**
         * 3.1、先查找绑定关系
         */
        Y9LoginUserHolder.setTenantId(targetTenantId);
        SpmApproveItem item = itemService.findById(itemId);
        String proDefKey = item.getWorkflowGuid();
        ProcessDefinitionModel targetpd = repositoryManager.getLatestProcessDefinitionByKey(targetTenantId, proDefKey);
        String targetpdId = targetpd.getId();

        List<ItemButtonBind> targetBindList = itemButtonBindService.findList(itemId, ItemButtonTypeEnum.SEND.getValue(), targetpdId);
        if (!targetBindList.isEmpty()) {
            return;
        }
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        ProcessDefinitionModel sourcepd = repositoryManager.getLatestProcessDefinitionByKey(sourceTenantId, proDefKey);
        String sourcepdId = sourcepd.getId();
        List<ItemButtonBind> sourceBindList = itemButtonBindService.findList(itemId, ItemButtonTypeEnum.SEND.getValue(), sourcepdId);
        if (sourceBindList.isEmpty()) {
            return;
        }
        /**
         * 3.2、获取目标租户的事项管理系统的角色
         */
        System system = systemEntityManager.getByName(Y9Context.getSystemName());
        Role tenantRole = roleManager.getRole(targetTenantId);
        Role tenantSystemRole = roleManager.findByCustomIdAndParentId(system.getId(), tenantRole.getId());
        String parentId = tenantSystemRole.getId();

        /**
         * 3.3、先保存绑定关系再更新绑定的角色
         * 把绑定的角色复制到目标租户中去，父节点为3.2中获取的角色，目标租户创建新角色时，为了避免重复创建的问题，用源角色id作为新角色的customId，每次要创建的时候，查找一下是否存在
         * 因为所有角色在同一张表，确保id的唯一性，所以复制的角色要更改角色Id,并把新老角色id的对应关系传递给权限复制，用来替换调老的角色id
         */
        List<ItemButtonRole> roleList = null;
        Role oldRole = null, newRoleTemp = null;
        String newRoleId = null, roleId = null;
        Organization organization = orgUnitManager.getOrganization(targetTenantId, Y9LoginUserHolder.getPersonId());
        for (ItemButtonBind bind : sourceBindList) {
            bind.setProcessDefinitionId(targetpdId);
            itemButtonBindService.save(bind);

            /** 更新绑定角色 */
            roleList = itemButtonRoleService.findByItemButtonId(bind.getId());
            for (ItemButtonRole role : roleList) {
                roleId = role.getId();
                oldRole = roleManager.getRole(roleId);
                if (null != oldRole && null != oldRole.getId()) {
                    newRoleTemp = roleManager.findByCustomIdAndParentId(roleId, parentId);
                    if (null == newRoleTemp || null == newRoleTemp.getId()) {
                        newRoleId = Y9IdGenerator.genId(IdType.SNOWFLAKE);
                        // TODO
                        /** 把申请人所在的租户机构添加到角色 */
                        roleManager.addPerson(organization.getId(), newRoleId, targetTenantId);
                    } else {
                        newRoleId = newRoleTemp.getId();
                    }

                    itemButtonRoleService.saveOrUpdate(bind.getId(), newRoleId);
                }
            }
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void copyTabEntity(String sourceTenantId, String targetTenantId, String itemId) {
        /**
         * 1、查找源租户是否存在页签
         */
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        List<TabEntity> tabList = tabEntityService.findAll();
        if (tabList.isEmpty()) {
            return;
        }
        /**
         * 2、复制源租户的页签到目标租户
         */
        Y9LoginUserHolder.setTenantId(targetTenantId);
        for (TabEntity tab : tabList) {
            tabEntityService.saveOrUpdate(tab);
        }
        /**
         * 3、复制该事项源租户的页签和事项的绑定关系到目标租户
         */
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        SpmApproveItem item = itemService.findById(itemId);
        String proDefKey = item.getWorkflowGuid();
        ProcessDefinitionModel sourcepd = repositoryManager.getLatestProcessDefinitionByKey(sourceTenantId, proDefKey);
        String sourcepdId = sourcepd.getId();
        List<ItemTabBind> tabBindList = itemTabBindService.findByItemIdAndProcessDefinitionId(itemId, sourcepdId);
        if (tabBindList.isEmpty()) {
            return;
        }

        Y9LoginUserHolder.setTenantId(targetTenantId);
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String personId = userInfo.getPersonId(), personName = userInfo.getName();
        ProcessDefinitionModel targetpd = repositoryManager.getLatestProcessDefinitionByKey(targetTenantId, proDefKey);
        String targetpdId = targetpd.getId();
        List<ItemTabBind> targetTabBindList = itemTabBindService.findByItemIdAndProcessDefinitionId(itemId, targetpdId);
        if (!targetTabBindList.isEmpty()) {
            return;
        }
        for (ItemTabBind bind : tabBindList) {
            ItemTabBind oldBind = itemTabBindService.findOne(bind.getId());
            if (null == oldBind) {
                bind.setProcessDefinitionId(targetpdId);
                bind.setTenantId(targetTenantId);
                bind.setUserId(personId);
                bind.setUserName(personName);
                itemTabBindService.save(bind);
            }
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void copyTaoHongTemplate(String sourceTenantId, String targetTenantId) {
        /**
         * 1、查找源租户是否存在套红模板
         */
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        List<TaoHongTemplate> sourcettList = taoHongTemplateService.findByTenantId(sourceTenantId, "");
        if (sourcettList.isEmpty()) {
            return;
        }
        List<TaoHongTemplateType> sourcetttList = taoHongTemplateTypeService.findAll();
        /**
         * 2、复制源租户的套红模板和模板类型到目标租户
         */
        Y9LoginUserHolder.setTenantId(targetTenantId);
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        OrgUnit orgUnit = personManager.getBureau(targetTenantId, userInfo.getPersonId());
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
    @Transactional(readOnly = false)
    public Map<String, String> copyTenantRole(String sourceTenantId, String targetTenantId, String itemId) {
        /**
         * 1、在源租户查找权限中绑定的租户的角色
         */
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        SpmApproveItem item = itemService.findById(itemId);
        String proDefKey = item.getWorkflowGuid();
        ProcessDefinitionModel sourcepd = repositoryManager.getLatestProcessDefinitionByKey(sourceTenantId, proDefKey);
        String sourcepdId = sourcepd.getId();
        List<ItemPermission> sourceipList = itemPermissionRepository.findByItemIdAndProcessDefinitionId(itemId, sourcepdId);
        int roleType = 0;
        List<String> roleIdList = new ArrayList<>();
        for (ItemPermission itemPermission : sourceipList) {
            roleType = itemPermission.getRoleType();
            if (roleType == ItemPermissionEnum.ROLE.getValue()) {
                roleIdList.add(itemPermission.getRoleId());
            }
        }
        /**
         * 2、获取目标租户的事项管理系统的角色
         */
        System system = systemEntityManager.getByName(Y9Context.getSystemName());
        Role tenantRole = roleManager.getRole(targetTenantId);
        Role tenantSystemRole = roleManager.findByCustomIdAndParentId(system.getId(), tenantRole.getId());
        String parentId = tenantSystemRole.getId();
        /**
         * 3、把1中查出的角色复制到目标租户中去，父节点为2中获取的角色，目标租户创建新角色时，为了避免重复创建的问题，用源角色id作为新角色的customId，每次要创建的时候，查找一下是否存在
         * 因为所有角色在同一张表，确保id的唯一性，所以复制的角色要更改角色Id,并把新老角色id的对应关系传递给权限复制，用来替换调老的角色id
         */
        Map<String, String> roleIdMap = new HashMap<>(16);
        Role oldRole = null, newRoleTemp = null;
        String newRoleId = null;
        Organization organization = orgUnitManager.getOrganization(targetTenantId, Y9LoginUserHolder.getPersonId());
        for (String roleId : roleIdList) {
            oldRole = roleManager.getRole(roleId);
            if (null != oldRole && null != oldRole.getId()) {
                newRoleTemp = roleManager.findByCustomIdAndParentId(roleId, parentId);
                if (null == newRoleTemp || null == newRoleTemp.getId()) {
                    newRoleId = Y9IdGenerator.genId(IdType.SNOWFLAKE);
                    // TODO
                    /** 把申请人所在的租户机构添加到角色 */
                    roleManager.addPerson(organization.getId(), newRoleId, targetTenantId);
                } else {
                    newRoleId = newRoleTemp.getId();
                    roleIdMap.put(roleId, newRoleId);
                }
            }
        }

        return roleIdMap;
    }

    @Override
    @Transactional(readOnly = false)
    public void copyWordTemplate(String sourceTenantId, String targetTenantId, String itemId) {
        /**
         * 1、查找源租户是否存在正文模板
         */
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        List<WordTemplate> wordList = wordTemplateService.findAll();
        if (wordList.isEmpty()) {
            return;
        }
        /**
         * 2、复制源租户的模板到目标租户
         */
        Y9LoginUserHolder.setTenantId(targetTenantId);
        for (WordTemplate word : wordList) {
            wordTemplateService.saveOrUpdate(word);
        }
        /**
         * 3、复制该事项源租户的模板事项以及模板和书签的绑定关系到目标租户
         */
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        SpmApproveItem item = itemService.findById(itemId);
        String proDefKey = item.getWorkflowGuid();
        ProcessDefinitionModel sourcepd = repositoryManager.getLatestProcessDefinitionByKey(sourceTenantId, proDefKey);
        String sourcepdId = sourcepd.getId();
        ItemWordTemplateBind bind = itemWordTemplateBindService.findByItemIdAndProcessDefinitionId(itemId, sourcepdId);
        if (null == bind) {
            return;
        }
        Y9LoginUserHolder.setTenantId(targetTenantId);
        ProcessDefinitionModel targetpd = repositoryManager.getLatestProcessDefinitionByKey(targetTenantId, proDefKey);
        String targetpdId = targetpd.getId();
        ItemWordTemplateBind targetBind = itemWordTemplateBindService.findByItemIdAndProcessDefinitionId(itemId, targetpdId);
        if (null != targetBind) {
            return;
        }
        /** 3、1事项和模板的绑定关系 */
        itemWordTemplateBindService.save(itemId, targetpdId, bind.getTemplateId());
        /** 3、2模板和模板中的书签绑定关系 */
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        List<BookMarkBind> bookMarkBindList = bookMarkBindService.findByWordTemplateId(bind.getTemplateId());

        Y9LoginUserHolder.setTenantId(targetTenantId);
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String personId = userInfo.getPersonId(), personName = userInfo.getName();
        for (BookMarkBind bookMarkBind : bookMarkBindList) {
            bookMarkBind.setUserId(personId);
            bookMarkBind.setUserName(personName);

            bookMarkBindService.saveOrUpdate(bookMarkBind);
        }
    }

    @Override
    public void dataCopy(String sourceTenantId, String targetTenantId, String itemId) throws Exception {
        /** 复制流程模型并部署 */
        Y9LoginUserHolder.setTenantId(sourceTenantId);
        /** 一复制事项 */
        this.copyItem(sourceTenantId, targetTenantId, itemId);
        /** 二复制动态角色 */
        this.copyDynamicRole(sourceTenantId, targetTenantId);
        /** 三复制租户的角色 */
        Map<String, String> roleIdMap = this.copyTenantRole(sourceTenantId, targetTenantId, itemId);
        /** 四复制授权 */
        this.copyPerm(sourceTenantId, targetTenantId, itemId, roleIdMap);
        /** 五复制表单 */
        this.copyForm(sourceTenantId, targetTenantId, itemId);
        /** 六复制意见框及绑定关系和授权关系 */
        this.copyOpinionFrame(sourceTenantId, targetTenantId, itemId);
        /** 七复制事项视图配置 */
        this.copyItemViewConf(sourceTenantId, targetTenantId, itemId);
        /** 八复制正文模板及和事项的绑定关系以及模板中书签的绑定关系 */
        this.copyWordTemplate(sourceTenantId, targetTenantId, itemId);
        /** 九复制打印模板及和事项的绑定关系 */
        this.copyPrintTemplate(sourceTenantId, targetTenantId, itemId);
        /** 十复制页签及和事项的绑定关系 */
        this.copyTabEntity(sourceTenantId, targetTenantId, itemId);
        /** 十一套红模板 */
        this.copyTaoHongTemplate(sourceTenantId, targetTenantId);
        /** 十二日历配置 */
        this.copyCalendarConfig(sourceTenantId, targetTenantId);
        /** 十三编号配置 */
        this.copyOrganWord(sourceTenantId, targetTenantId, itemId);
        /** 十四普通按钮 */
        this.copyCommonButton(sourceTenantId, targetTenantId, itemId);
        /** 十五发送按钮 */
        this.copySendButton(sourceTenantId, targetTenantId, itemId);
    }

    @Override
    public void dataCopy4System(String sourceTenantId, String targetTenantId, String systemName) throws Exception {
        List<SpmApproveItem> itemList = itemService.findBySystemName(systemName);
        for (SpmApproveItem item : itemList) {
            this.dataCopy(sourceTenantId, targetTenantId, item.getId());
        }
    }
}