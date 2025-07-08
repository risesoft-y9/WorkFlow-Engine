package net.risesoft.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import net.risesoft.entity.DynamicRole;
import net.risesoft.entity.ItemPermission;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.entity.form.Y9Form;
import net.risesoft.entity.form.Y9FormField;
import net.risesoft.entity.form.Y9FormItemBind;
import net.risesoft.entity.form.Y9Table;
import net.risesoft.entity.form.Y9TableField;
import net.risesoft.entity.opinion.ItemOpinionFrameBind;
import net.risesoft.entity.opinion.OpinionFrame;
import net.risesoft.entity.template.ItemPrintTemplateBind;
import net.risesoft.entity.view.ItemViewConf;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.form.Y9FormFieldRepository;
import net.risesoft.repository.form.Y9FormRepository;
import net.risesoft.repository.form.Y9TableFieldRepository;
import net.risesoft.repository.form.Y9TableRepository;
import net.risesoft.repository.jpa.DynamicRoleRepository;
import net.risesoft.repository.jpa.ItemOpinionFrameBindRepository;
import net.risesoft.repository.jpa.ItemPermissionRepository;
import net.risesoft.repository.jpa.ItemViewConfRepository;
import net.risesoft.repository.jpa.OpinionFrameRepository;
import net.risesoft.repository.jpa.PrintTemplateItemBindRepository;
import net.risesoft.repository.jpa.SpmApproveItemRepository;
import net.risesoft.repository.jpa.Y9FormItemBindRepository;
import net.risesoft.service.SyncYearTableService;
import net.risesoft.service.form.TableManagerService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.sqlddl.pojo.DbColumn;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@Service(value = "initTableDataService")
public class InitTableDataService {

    /**
     * 动态角色id
     */
    public static final String DYNAMIC_ROLE_ID = "11111111-1111-1111-1111-111111111113";
    /**
     * 事项id
     */
    public static final String ITEM_ID = "11111111-1111-1111-1111-111111111111";
    /**
     * 意见框id
     */
    public static final String OPINION_FRAME_ID = "11111111-1111-1111-1111-111111111112";
    /**
     * 意见框标识
     */
    public static final String OPINION_FRAME_MARK = "personalComment";
    /**
     * 意见框名称
     */
    public static final String OPINION_FRAME_NAME = "个人意见";
    /**
     * 打印表单id
     */
    public static final String PRINT_FORM_ID = "11111111-1111-1111-1111-111111111116";
    /**
     * 打印表单名称
     */
    public static final String PRINT_FORM_NAME = "表单信息(打印)";
    /**
     * 流程定义key
     */
    public static final String PROCESSDEFINITIONKEY = "ziyouliucheng";
    /**
     * 系统中文名，事项名称
     */
    public static final String SYSTEMCNNAME = "办件";
    /**
     * 系统英文名
     */
    public static final String SYSTEMNAME = "banjian";
    /**
     * 表单id
     */
    public static final String Y9_FORM_ID = "11111111-1111-1111-1111-111111111115";
    /**
     * 表单名称
     */
    public static final String Y9_FORM_NAME = "表单信息";
    /**
     * 业务表中文名称
     */
    public static final String Y9_TABLE_CNNAME = "办件信息表";
    /**
     * 业务表id
     */
    public static final String Y9_TABLE_ID = "11111111-1111-1111-1111-111111111114";
    /**
     * 业务表名称
     */
    public static final String Y9_TABLE_NAME = "y9_form_ziyoubanjian";
    private final JdbcTemplate jdbcTemplate4Tenant;

    private final SyncYearTableService syncYearTableService;

    private final SpmApproveItemRepository spmApproveItemRepository;

    private final OpinionFrameRepository opinionFrameRepository;

    private final DynamicRoleRepository dynamicRoleRepository;

    private final Y9TableRepository y9TableRepository;

    private final Y9TableFieldRepository y9TableFieldRepository;

    private final Y9FormRepository y9FormRepository;

    private final Y9FormFieldRepository y9FormFieldRepository;

    private final Y9FormItemBindRepository y9FormItemBindRepository;

    private final ItemPermissionRepository itemPermissionRepository;

    private final ItemOpinionFrameBindRepository itemOpinionFrameBindRepository;

    private final PrintTemplateItemBindRepository printTemplateItemBindRepository;

    private final ItemViewConfRepository itemViewConfRepository;

    private final TableManagerService tableManagerService;

    private final Y9Properties y9Config;

    public InitTableDataService(@Qualifier("jdbcTemplate4Tenant") JdbcTemplate jdbcTemplate4Tenant,
        SyncYearTableService syncYearTableService, SpmApproveItemRepository spmApproveItemRepository,
        OpinionFrameRepository opinionFrameRepository, DynamicRoleRepository dynamicRoleRepository,
        Y9TableRepository y9TableRepository, Y9TableFieldRepository y9TableFieldRepository,
        Y9FormRepository y9FormRepository, Y9FormFieldRepository y9FormFieldRepository,
        Y9FormItemBindRepository y9FormItemBindRepository, ItemPermissionRepository itemPermissionRepository,
        ItemOpinionFrameBindRepository itemOpinionFrameBindRepository,
        PrintTemplateItemBindRepository printTemplateItemBindRepository, ItemViewConfRepository itemViewConfRepository,
        TableManagerService tableManagerService, Y9Properties y9Config) {
        this.jdbcTemplate4Tenant = jdbcTemplate4Tenant;
        this.syncYearTableService = syncYearTableService;
        this.spmApproveItemRepository = spmApproveItemRepository;
        this.opinionFrameRepository = opinionFrameRepository;
        this.dynamicRoleRepository = dynamicRoleRepository;
        this.y9TableRepository = y9TableRepository;
        this.y9TableFieldRepository = y9TableFieldRepository;
        this.y9FormRepository = y9FormRepository;
        this.y9FormFieldRepository = y9FormFieldRepository;
        this.y9FormItemBindRepository = y9FormItemBindRepository;
        this.itemPermissionRepository = itemPermissionRepository;
        this.itemOpinionFrameBindRepository = itemOpinionFrameBindRepository;
        this.printTemplateItemBindRepository = printTemplateItemBindRepository;
        this.itemViewConfRepository = itemViewConfRepository;
        this.tableManagerService = tableManagerService;
        this.y9Config = y9Config;
    }

    private void createDynamicRole() {
        DynamicRole dynamicRole = dynamicRoleRepository.findById(DYNAMIC_ROLE_ID).orElse(null);
        if (null == dynamicRole) {
            dynamicRole = new DynamicRole();
            dynamicRole.setId(DYNAMIC_ROLE_ID);
            dynamicRole.setName("当前组织架构");
            dynamicRole.setClassPath("net.risesoft.service.dynamicrole.impl.CurrentOrg");
            dynamicRole.setTenantId(Y9LoginUserHolder.getTenantId());
            dynamicRoleRepository.save(dynamicRole);
        }
    }

    private void createItem() {
        SpmApproveItem item = spmApproveItemRepository.findById(ITEM_ID).orElse(null);
        if (null == item) {
            item = new SpmApproveItem();
            item.setId(ITEM_ID);
            item.setWorkflowGuid(PROCESSDEFINITIONKEY);
            item.setAppUrl(y9Config.getCommon().getFlowableBaseUrl() + "?itemId=" + ITEM_ID);
            item.setName(SYSTEMCNNAME);
            item.setSysLevel(SYSTEMCNNAME);
            item.setSystemName(SYSTEMNAME);
            item.setCreateDate(new Date());
            item.setIsOnline("0");
            item.setTodoTaskUrlPrefix(y9Config.getCommon().getFlowableBaseUrl());
            spmApproveItemRepository.save(item);
        }
    }

    private void createItemForm(String processDefinitionId) {
        List<Y9FormItemBind> list = y9FormItemBindRepository.findByItemIdAndProcDefId(ITEM_ID, processDefinitionId);
        if (list.isEmpty()) {
            Y9FormItemBind bind = new Y9FormItemBind();
            bind.setFormId(Y9_FORM_ID);
            bind.setFormName(Y9_FORM_NAME);
            bind.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            bind.setItemId(ITEM_ID);
            bind.setItemName(SYSTEMCNNAME);
            bind.setProcessDefinitionId(processDefinitionId);
            bind.setShowFileTab(true);
            bind.setShowHistoryTab(true);
            bind.setShowDocumentTab(true);
            bind.setTabIndex(1);
            bind.setTenantId(Y9LoginUserHolder.getTenantId());
            y9FormItemBindRepository.save(bind);
        }
    }

    private void createItemOpinionFrame(String processDefinitionId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<ItemOpinionFrameBind> list = itemOpinionFrameBindRepository
            .findByItemIdAndProcessDefinitionIdOrderByCreateDateAsc(ITEM_ID, processDefinitionId);
        if (list.isEmpty()) {
            ItemOpinionFrameBind newoftrb = new ItemOpinionFrameBind();
            newoftrb.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            newoftrb.setCreateDate(sdf.format(new Date()));
            newoftrb.setOpinionFrameMark(OPINION_FRAME_MARK);
            newoftrb.setOpinionFrameName(OPINION_FRAME_NAME);
            newoftrb.setItemId(ITEM_ID);
            newoftrb.setTaskDefKey("");
            newoftrb.setTenantId(Y9LoginUserHolder.getTenantId());
            newoftrb.setProcessDefinitionId(processDefinitionId);
            newoftrb.setSignOpinion(false);
            itemOpinionFrameBindRepository.save(newoftrb);
        }
    }

    private void createItemPermission(String processDefinitionId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<ItemPermission> list =
            itemPermissionRepository.findByItemIdAndProcessDefinitionId(ITEM_ID, processDefinitionId);
        if (list.isEmpty()) {
            ItemPermission newip = new ItemPermission();
            newip.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            newip.setItemId(ITEM_ID);
            newip.setProcessDefinitionId(processDefinitionId);
            newip.setRoleId(DYNAMIC_ROLE_ID);
            newip.setRoleType(4);
            newip.setTaskDefKey("");
            newip.setTenantId(Y9LoginUserHolder.getTenantId());
            newip.setCreatDate(sdf.format(new Date()));
            itemPermissionRepository.save(newip);
        }
    }

    private void createItemPrintTemplate() {
        ItemPrintTemplateBind printTemplateItemBind = printTemplateItemBindRepository.findByItemId(ITEM_ID);
        if (printTemplateItemBind == null) {
            printTemplateItemBind = new ItemPrintTemplateBind();
            printTemplateItemBind.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            printTemplateItemBind.setTenantId(Y9LoginUserHolder.getTenantId());
            printTemplateItemBind.setItemId(ITEM_ID);
            printTemplateItemBind.setTemplateId(PRINT_FORM_ID);
            printTemplateItemBind.setTemplateName(PRINT_FORM_NAME);
            printTemplateItemBind.setTemplateType("2");
            printTemplateItemBindRepository.save(printTemplateItemBind);
        }
    }

    private void createItemViewConf() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<ItemViewConf> list = itemViewConfRepository.findByItemIdOrderByTabIndexAsc(ITEM_ID);
        if (list.isEmpty()) {
            String date = sdf.format(new Date());
            List<ItemViewConf> list0 = new ArrayList<>();
            ItemViewConf newConf = new ItemViewConf();
            int i = 0;
            newConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setColumnName("serialNumber").setDisPlayName("序号")
                .setDisPlayWidth("60").setDisPlayAlign("center").setTabIndex(i++).setItemId(ITEM_ID).setTableName("")
                .setViewType(ItemBoxTypeEnum.DRAFT.getValue()).setCreateTime(date);
            list0.add(newConf);
            newConf = new ItemViewConf();
            newConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setColumnName("level").setDisPlayName("紧急程度")
                .setDisPlayWidth("120").setDisPlayAlign("center").setTabIndex(i++).setItemId(ITEM_ID)
                .setTableName(Y9_TABLE_NAME).setViewType(ItemBoxTypeEnum.DRAFT.getValue()).setCreateTime(date);
            list0.add(newConf);
            newConf = new ItemViewConf();
            newConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setColumnName("number").setDisPlayName("文件编号")
                .setDisPlayWidth("180").setDisPlayAlign("center").setTabIndex(i++).setItemId(ITEM_ID)
                .setTableName(Y9_TABLE_NAME).setViewType(ItemBoxTypeEnum.DRAFT.getValue()).setCreateTime(date);
            list0.add(newConf);
            newConf = new ItemViewConf();
            newConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setColumnName("title").setDisPlayName("文件标题")
                .setDisPlayWidth("auto").setDisPlayAlign("left").setTabIndex(i++).setItemId(ITEM_ID)
                .setTableName(Y9_TABLE_NAME).setViewType(ItemBoxTypeEnum.DRAFT.getValue()).setCreateTime(date);
            list0.add(newConf);
            newConf = new ItemViewConf();
            newConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setColumnName("createDate").setDisPlayName("创建时间")
                .setDisPlayWidth("150").setDisPlayAlign("center").setTabIndex(i++).setItemId(ITEM_ID)
                .setTableName(Y9_TABLE_NAME).setViewType(ItemBoxTypeEnum.DRAFT.getValue()).setCreateTime(date);
            list0.add(newConf);
            newConf = new ItemViewConf();
            newConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setColumnName("opt").setDisPlayName("操作")
                .setDisPlayWidth("180").setDisPlayAlign("center").setTabIndex(i).setItemId(ITEM_ID).setTableName("")
                .setViewType(ItemBoxTypeEnum.DRAFT.getValue()).setCreateTime(date);
            list0.add(newConf);
            itemViewConfRepository.saveAll(list0);
            i = 0;
            list0 = new ArrayList<>();
            newConf = new ItemViewConf();
            newConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setColumnName("serialNumber").setDisPlayName("序号")
                .setDisPlayWidth("60").setDisPlayAlign("center").setTabIndex(i++).setItemId(ITEM_ID).setTableName("")
                .setViewType(ItemBoxTypeEnum.TODO.getValue()).setCreateTime(date);
            list0.add(newConf);
            newConf = new ItemViewConf();
            newConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setColumnName("number").setDisPlayName("文件编号")
                .setDisPlayWidth("180").setDisPlayAlign("center").setTabIndex(i++).setItemId(ITEM_ID)
                .setTableName(Y9_TABLE_NAME).setViewType(ItemBoxTypeEnum.TODO.getValue()).setCreateTime(date);
            list0.add(newConf);
            newConf = new ItemViewConf();
            newConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setColumnName("level").setDisPlayName("紧急程度")
                .setDisPlayWidth("90").setDisPlayAlign("center").setTabIndex(i++).setItemId(ITEM_ID)
                .setTableName(Y9_TABLE_NAME).setViewType(ItemBoxTypeEnum.TODO.getValue()).setCreateTime(date);
            list0.add(newConf);
            newConf = new ItemViewConf();
            newConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setColumnName("title").setDisPlayName("文件标题")
                .setDisPlayWidth("auto").setDisPlayAlign("left").setTabIndex(i++).setItemId(ITEM_ID)
                .setTableName(Y9_TABLE_NAME).setViewType(ItemBoxTypeEnum.TODO.getValue()).setCreateTime(date);
            list0.add(newConf);
            newConf = new ItemViewConf();
            newConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setColumnName("taskName").setDisPlayName("办理环节")
                .setDisPlayWidth("120").setDisPlayAlign("center").setTabIndex(i++).setItemId(ITEM_ID).setTableName("")
                .setViewType(ItemBoxTypeEnum.TODO.getValue()).setCreateTime(date);
            list0.add(newConf);
            newConf = new ItemViewConf();
            newConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setColumnName("taskCreateTime").setDisPlayName("发送时间")
                .setDisPlayWidth("150").setDisPlayAlign("center").setTabIndex(i++).setItemId(ITEM_ID).setTableName("")
                .setViewType(ItemBoxTypeEnum.TODO.getValue()).setCreateTime(date);
            list0.add(newConf);
            newConf = new ItemViewConf();
            newConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setColumnName("taskSender").setDisPlayName("发送人")
                .setDisPlayWidth("120").setDisPlayAlign("center").setTabIndex(i++).setItemId(ITEM_ID).setTableName("")
                .setViewType(ItemBoxTypeEnum.TODO.getValue()).setCreateTime(date);
            list0.add(newConf);
            newConf = new ItemViewConf();
            newConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setColumnName("opt").setDisPlayName("操作")
                .setDisPlayWidth("300").setDisPlayAlign("center").setTabIndex(i).setItemId(ITEM_ID).setTableName("")
                .setViewType(ItemBoxTypeEnum.TODO.getValue()).setCreateTime(date);
            list0.add(newConf);
            itemViewConfRepository.saveAll(list0);
            i = 0;
            list0 = new ArrayList<>();
            newConf = new ItemViewConf();
            newConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setColumnName("serialNumber").setDisPlayName("序号")
                .setDisPlayWidth("60").setDisPlayAlign("center").setTabIndex(i++).setItemId(ITEM_ID).setTableName("")
                .setViewType(ItemBoxTypeEnum.DOING.getValue()).setCreateTime(date);
            list0.add(newConf);
            newConf = new ItemViewConf();
            newConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setColumnName("number").setDisPlayName("文件编号")
                .setDisPlayWidth("180").setDisPlayAlign("center").setTabIndex(i++).setItemId(ITEM_ID)
                .setTableName(Y9_TABLE_NAME).setViewType(ItemBoxTypeEnum.DOING.getValue()).setCreateTime(date);
            list0.add(newConf);
            newConf = new ItemViewConf();
            newConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setColumnName("level").setDisPlayName("紧急程度")
                .setDisPlayWidth("90").setDisPlayAlign("center").setTabIndex(i++).setItemId(ITEM_ID)
                .setTableName(Y9_TABLE_NAME).setViewType(ItemBoxTypeEnum.DOING.getValue()).setCreateTime(date);
            list0.add(newConf);
            newConf = new ItemViewConf();
            newConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setColumnName("title").setDisPlayName("文件标题")
                .setDisPlayWidth("auto").setDisPlayAlign("left").setTabIndex(i++).setItemId(ITEM_ID)
                .setTableName(Y9_TABLE_NAME).setViewType(ItemBoxTypeEnum.DOING.getValue()).setCreateTime(date);
            list0.add(newConf);
            newConf = new ItemViewConf();
            newConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setColumnName("taskName").setDisPlayName("办理环节")
                .setDisPlayWidth("120").setDisPlayAlign("center").setTabIndex(i++).setItemId(ITEM_ID).setTableName("")
                .setViewType(ItemBoxTypeEnum.DOING.getValue()).setCreateTime(date);
            list0.add(newConf);
            newConf = new ItemViewConf();
            newConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setColumnName("taskCreateTime").setDisPlayName("发送时间")
                .setDisPlayWidth("150").setDisPlayAlign("center").setTabIndex(i++).setItemId(ITEM_ID).setTableName("")
                .setViewType(ItemBoxTypeEnum.DOING.getValue()).setCreateTime(date);
            list0.add(newConf);
            newConf = new ItemViewConf();
            newConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setColumnName("taskAssignee").setDisPlayName("当前办理人")
                .setDisPlayWidth("150").setDisPlayAlign("center").setTabIndex(i++).setItemId(ITEM_ID).setTableName("")
                .setViewType(ItemBoxTypeEnum.DOING.getValue()).setCreateTime(date);
            list0.add(newConf);
            newConf = new ItemViewConf();
            newConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setColumnName("opt").setDisPlayName("操作")
                .setDisPlayWidth("300").setDisPlayAlign("center").setTabIndex(i).setItemId(ITEM_ID).setTableName("")
                .setViewType(ItemBoxTypeEnum.DOING.getValue()).setCreateTime(date);
            list0.add(newConf);
            itemViewConfRepository.saveAll(list0);
            i = 0;
            list0 = new ArrayList<>();
            newConf = new ItemViewConf();
            newConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setColumnName("serialNumber").setDisPlayName("序号")
                .setDisPlayWidth("60").setDisPlayAlign("center").setTabIndex(i++).setItemId(ITEM_ID).setTableName("")
                .setViewType(ItemBoxTypeEnum.DONE.getValue()).setCreateTime(date);
            list0.add(newConf);
            newConf = new ItemViewConf();
            newConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setColumnName("number").setDisPlayName("文件编号")
                .setDisPlayWidth("180").setDisPlayAlign("center").setTabIndex(i++).setItemId(ITEM_ID)
                .setTableName(Y9_TABLE_NAME).setViewType(ItemBoxTypeEnum.DONE.getValue()).setCreateTime(date);
            list0.add(newConf);
            newConf = new ItemViewConf();
            newConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setColumnName("level").setDisPlayName("紧急程度")
                .setDisPlayWidth("90").setDisPlayAlign("center").setTabIndex(i++).setItemId(ITEM_ID)
                .setTableName(Y9_TABLE_NAME).setViewType(ItemBoxTypeEnum.DONE.getValue()).setCreateTime(date);
            list0.add(newConf);
            newConf = new ItemViewConf();
            newConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setColumnName("title").setDisPlayName("文件标题")
                .setDisPlayWidth("auto").setDisPlayAlign("left").setTabIndex(i++).setItemId(ITEM_ID)
                .setTableName(Y9_TABLE_NAME).setViewType(ItemBoxTypeEnum.DONE.getValue()).setCreateTime(date);
            list0.add(newConf);
            newConf = new ItemViewConf();
            newConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setColumnName("user4Complete").setDisPlayName("办结人")
                .setDisPlayWidth("150").setDisPlayAlign("center").setTabIndex(i++).setItemId(ITEM_ID).setTableName("")
                .setViewType(ItemBoxTypeEnum.DONE.getValue()).setCreateTime(date);
            list0.add(newConf);
            newConf = new ItemViewConf();
            newConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setColumnName("endTime").setDisPlayName("办结时间")
                .setDisPlayWidth("150").setDisPlayAlign("center").setTabIndex(i++).setItemId(ITEM_ID).setTableName("")
                .setViewType(ItemBoxTypeEnum.DONE.getValue()).setCreateTime(date);
            list0.add(newConf);
            newConf = new ItemViewConf();
            newConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setColumnName("opt").setDisPlayName("操作")
                .setDisPlayWidth("200").setDisPlayAlign("center").setTabIndex(i).setItemId(ITEM_ID).setTableName("")
                .setViewType(ItemBoxTypeEnum.DONE.getValue()).setCreateTime(date);
            list0.add(newConf);
            itemViewConfRepository.saveAll(list0);
        }

    }

    private void createOpinionFrame() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        OpinionFrame opinionFrame = opinionFrameRepository.findById(OPINION_FRAME_ID).orElse(null);
        if (null == opinionFrame) {
            opinionFrame = new OpinionFrame();
            opinionFrame.setId(OPINION_FRAME_ID);
            opinionFrame.setMark(OPINION_FRAME_MARK);
            opinionFrame.setCreateDate(sdf.format(new Date()));
            opinionFrame.setName(OPINION_FRAME_NAME);
            opinionFrame.setTenantId(Y9LoginUserHolder.getTenantId());
            opinionFrame.setDeleted(0);
            opinionFrameRepository.save(opinionFrame);
        }
    }

    private void createPrintForm() {
        Y9Form y9Form = y9FormRepository.findById(PRINT_FORM_ID).orElse(null);
        if (null == y9Form) {
            y9Form = new Y9Form();
            y9Form.setId(PRINT_FORM_ID);
            y9Form.setFormName(PRINT_FORM_NAME);
            y9Form.setSystemCnName(SYSTEMCNNAME);
            y9Form.setSystemName(SYSTEMNAME);
            y9Form.setUpdateTime(new Date());
            y9Form.setTenantId(Y9LoginUserHolder.getTenantId());
            y9Form.setFormJson(
                "{\"list\":[{\"type\":\"component\",\"icon\":\"icon-component\",\"options\":{\"customClass\":\"formheader\",\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"template\":\"<div style=\\\"text-align: center;\\\">办件信息</div>\",\"required\":false,\"remoteFunc\":\"func_1622619081352\",\"remoteOption\":\"option_1622619081352\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"办件信息\",\"key\":\"1622619081352\",\"model\":\"\",\"rules\":[]},{\"type\":\"input\",\"icon\":\"icon-input\",\"options\":{\"width\":\"100%\",\"defaultValue\":\"\",\"required\":false,\"requiredMessage\":\"\",\"dataType\":\"\",\"dataTypeCheck\":false,\"dataTypeMessage\":\"\",\"pattern\":\"\",\"patternCheck\":false,\"patternMessage\":\"\",\"placeholder\":\"\",\"customClass\":\"\",\"disabled\":false,\"labelWidth\":100,\"isLabelWidth\":false,\"hidden\":true,\"dataBind\":true,\"showPassword\":false,\"remoteFunc\":\"func_1623031603653\",\"remoteOption\":\"option_1623031603653\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"\",\"key\":\"1623031603653\",\"model\":\"guid\",\"rules\":[],\"bindTable\":\"y9_form_ziyoubanjian\"},{\"type\":\"input\",\"icon\":\"icon-input\",\"options\":{\"width\":\"100%\",\"defaultValue\":\"\",\"required\":false,\"requiredMessage\":\"\",\"dataType\":\"\",\"dataTypeCheck\":false,\"dataTypeMessage\":\"\",\"pattern\":\"\",\"patternCheck\":false,\"patternMessage\":\"\",\"placeholder\":\"\",\"customClass\":\"\",\"disabled\":false,\"labelWidth\":100,\"isLabelWidth\":false,\"hidden\":true,\"dataBind\":true,\"showPassword\":false,\"remoteFunc\":\"func_1623031607288\",\"remoteOption\":\"option_1623031607288\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"单行文本\",\"key\":\"1623031607288\",\"model\":\"processInstanceId\",\"rules\":[]},{\"type\":\"report\",\"icon\":\"icon-table1\",\"options\":{\"customClass\":\"table\",\"hidden\":false,\"borderWidth\":1.5,\"borderColor\":\"#000000\",\"width\":\"690px\",\"remoteFunc\":\"func_1622518382735\",\"remoteOption\":\"option_1622518382735\",\"tableColumn\":false},\"rows\":[{\"columns\":[{\"type\":\"td\",\"list\":[{\"type\":\"component\",\"icon\":\"icon-component\",\"options\":{\"customClass\":\"label_name\",\"labelWidth\":0,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"template\":\"<div style=\\\"text-align: center;\\\">文件编号</div>\",\"required\":false,\"remoteFunc\":\"func_1622619484999\",\"remoteOption\":\"option_1622619484999\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"\",\"key\":\"1622619484999\",\"model\":\"\",\"rules\":[]}],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"80px\",\"height\":\"\"},\"key\":\"kwj6q28x\",\"rules\":[]},{\"type\":\"td\",\"list\":[{\"type\":\"input\",\"icon\":\"icon-input\",\"options\":{\"width\":\"100%\",\"defaultValue\":\"$_number\",\"required\":false,\"requiredMessage\":\"\",\"dataType\":\"\",\"dataTypeCheck\":false,\"dataTypeMessage\":\"\",\"pattern\":\"\",\"patternCheck\":false,\"patternMessage\":\"\",\"placeholder\":\"\",\"customClass\":\"\",\"readonly\":true,\"disabled\":false,\"labelWidth\":100,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"showPassword\":false,\"remoteFunc\":\"func_1623058442021\",\"remoteOption\":\"option_1623058442021\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"单行文本\",\"key\":\"1623058442021\",\"model\":\"number\",\"rules\":[],\"bindTable\":\"y9_form_ziyoubanjian\"}],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\"},\"key\":\"laz0ay9t\",\"rules\":[]},{\"type\":\"td\",\"list\":[{\"type\":\"component\",\"icon\":\"icon-component\",\"options\":{\"customClass\":\"label_name\",\"labelWidth\":70,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"template\":\"<div style=\\\"text-align: center;\\\">类型</div>\",\"required\":false,\"remoteFunc\":\"func_1622619560645\",\"remoteOption\":\"option_1622619560645\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"类型\",\"key\":\"1622619560645\",\"model\":\"\",\"rules\":[]}],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"80px\",\"height\":\"\"},\"key\":\"erum9hdu\",\"rules\":[]},{\"type\":\"td\",\"list\":[{\"type\":\"input\",\"icon\":\"icon-input\",\"options\":{\"width\":\"100%\",\"defaultValue\":\"自由办件\",\"required\":false,\"requiredMessage\":\"\",\"dataType\":\"\",\"dataTypeCheck\":false,\"dataTypeMessage\":\"\",\"pattern\":\"\",\"patternCheck\":false,\"patternMessage\":\"\",\"placeholder\":\"\",\"customClass\":\"\",\"readonly\":true,\"disabled\":false,\"labelWidth\":100,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"showPassword\":false,\"remoteFunc\":\"func_1623049264602\",\"remoteOption\":\"option_1623049264602\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"单行文本\",\"key\":\"1623049264602\",\"model\":\"type\",\"rules\":[],\"bindTable\":\"y9_form_ziyoubanjian\"}],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"80px\",\"height\":\"\"},\"key\":\"f74fih4i\",\"rules\":[]},{\"type\":\"td\",\"list\":[{\"type\":\"component\",\"icon\":\"icon-component\",\"options\":{\"customClass\":\"label_name\",\"labelWidth\":70,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"template\":\"<div style=\\\"text-align: center;\\\">字号</div>\",\"required\":false,\"remoteFunc\":\"func_1622620205114\",\"remoteOption\":\"option_1622620205114\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"\",\"key\":\"1622620205114\",\"model\":\"\",\"rules\":[]}],\"options\":{\"customClass\":\"td_12\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"80px\",\"height\":\"\"},\"key\":\"io2ijobw\",\"rules\":[]},{\"type\":\"td\",\"list\":[{\"type\":\"input\",\"icon\":\"icon-input\",\"options\":{\"width\":\"100%\",\"defaultValue\":\"$_zihao\",\"required\":false,\"requiredMessage\":\"\",\"dataType\":\"\",\"dataTypeCheck\":false,\"dataTypeMessage\":\"\",\"pattern\":\"\",\"patternCheck\":false,\"patternMessage\":\"\",\"placeholder\":\"\",\"customClass\":\"\",\"readonly\":true,\"disabled\":false,\"labelWidth\":100,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"showPassword\":false,\"remoteFunc\":\"func_1623058458466\",\"remoteOption\":\"option_1623058458466\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"单行文本\",\"key\":\"1623058458466\",\"model\":\"wordSize\",\"rules\":[],\"bindTable\":\"y9_form_ziyoubanjian\"}],\"options\":{\"customClass\":\"td_right_border\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"150px\",\"height\":\"\"},\"key\":\"i4jrow3s\",\"rules\":[]}]},{\"columns\":[{\"type\":\"td\",\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\"},\"list\":[{\"type\":\"component\",\"icon\":\"icon-component\",\"options\":{\"customClass\":\"label_name\",\"labelWidth\":70,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"template\":\"<div style=\\\"text-align: center;\\\">文件标题</div>\",\"required\":false,\"remoteFunc\":\"func_1622620587996\",\"remoteOption\":\"option_1622620587996\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"\",\"key\":\"1622620587996\",\"model\":\"\",\"rules\":[]}],\"key\":\"t3op0byh\",\"rules\":[]},{\"type\":\"td\",\"list\":[{\"type\":\"input\",\"icon\":\"icon-input\",\"options\":{\"width\":\"100%\",\"defaultValue\":\"\",\"required\":true,\"requiredMessage\":\"请输入文件标题。\",\"dataType\":\"\",\"dataTypeCheck\":false,\"dataTypeMessage\":\"\",\"pattern\":\"\",\"patternCheck\":false,\"patternMessage\":\"\",\"placeholder\":\"\",\"customClass\":\"\",\"disabled\":false,\"labelWidth\":100,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"showPassword\":false,\"remoteFunc\":\"func_1622518410686\",\"remoteOption\":\"option_1622518410686\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"单行文本\",\"key\":\"1622518410686\",\"model\":\"title\",\"rules\":[{\"required\":true,\"message\":\"请输入文件标题。\"}],\"bindTable\":\"y9_form_ziyoubanjian\"}],\"options\":{\"customClass\":\"td_right_border\",\"colspan\":5,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"markCol\":0,\"markRow\":0},\"key\":\"rhnhg09l\",\"rules\":[]},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":1,\"markRow\":0},\"key\":\"gll2o2rj\",\"rules\":[]},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":2,\"markRow\":0},\"key\":\"pv8s5nee\",\"rules\":[]},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":2,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"markCol\":3,\"markRow\":0,\"invisible\":true},\"key\":\"x1420xkf\",\"rules\":[]},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":4,\"markRow\":0},\"key\":\"6mqjsfvc\"}]},{\"columns\":[{\"type\":\"td\",\"list\":[{\"type\":\"component\",\"icon\":\"icon-component\",\"options\":{\"customClass\":\"label_name\",\"labelWidth\":70,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"template\":\"<div style=\\\"text-align: center;\\\">创建部门</div>\",\"required\":false,\"remoteFunc\":\"func_1622620622928\",\"remoteOption\":\"option_1622620622928\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"创建部门\",\"key\":\"1622620622928\",\"model\":\"\",\"rules\":[]}],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\"},\"key\":\"vxrmj5zb\",\"rules\":[]},{\"type\":\"td\",\"list\":[{\"type\":\"input\",\"icon\":\"icon-input\",\"options\":{\"width\":\"100%\",\"defaultValue\":\"$_deptName\",\"required\":false,\"requiredMessage\":\"\",\"dataType\":\"\",\"dataTypeCheck\":false,\"dataTypeMessage\":\"\",\"pattern\":\"\",\"patternCheck\":false,\"patternMessage\":\"\",\"placeholder\":\"\",\"customClass\":\"\",\"readonly\":true,\"disabled\":false,\"labelWidth\":100,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"showPassword\":false,\"remoteFunc\":\"func_1623058477924\",\"remoteOption\":\"option_1623058477924\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"单行文本\",\"key\":\"1623058477924\",\"model\":\"department\",\"rules\":[],\"bindTable\":\"y9_form_ziyoubanjian\"}],\"options\":{\"customClass\":\"\",\"colspan\":2,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"markCol\":0,\"markRow\":0},\"key\":\"cqwjdr37\",\"rules\":[]},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":1,\"markRow\":0},\"key\":\"nizqs2un\"},{\"type\":\"td\",\"list\":[{\"type\":\"component\",\"icon\":\"icon-component\",\"options\":{\"customClass\":\"label_name\",\"labelWidth\":70,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"template\":\"<div style=\\\"text-align: center;\\\">创建人</div>\",\"required\":false,\"remoteFunc\":\"func_1622620627563\",\"remoteOption\":\"option_1622620627563\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"\",\"key\":\"1622620627563\",\"model\":\"\",\"rules\":[]}],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\"},\"key\":\"lzbmolxr\",\"rules\":[]},{\"type\":\"td\",\"list\":[{\"type\":\"input\",\"icon\":\"icon-input\",\"options\":{\"width\":\"100%\",\"defaultValue\":\"$_userName\",\"required\":false,\"requiredMessage\":\"\",\"dataType\":\"\",\"dataTypeCheck\":false,\"dataTypeMessage\":\"\",\"pattern\":\"\",\"patternCheck\":false,\"patternMessage\":\"\",\"placeholder\":\"\",\"customClass\":\"\",\"readonly\":true,\"disabled\":false,\"labelWidth\":100,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"showPassword\":false,\"remoteFunc\":\"func_1623058505798\",\"remoteOption\":\"option_1623058505798\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"单行文本\",\"key\":\"1623058505798\",\"model\":\"creater\",\"rules\":[],\"bindTable\":\"y9_form_ziyoubanjian\"}],\"options\":{\"customClass\":\"td_right_border\",\"colspan\":2,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"markCol\":0,\"markRow\":0},\"key\":\"79c002x5\",\"rules\":[]},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":1,\"markRow\":0},\"key\":\"qlafytm9\"}]},{\"columns\":[{\"type\":\"td\",\"list\":[{\"type\":\"component\",\"icon\":\"icon-component\",\"options\":{\"customClass\":\"label_name\",\"labelWidth\":70,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"template\":\"<div style=\\\"text-align: center;\\\">创建日期</div>\",\"required\":false,\"remoteFunc\":\"func_1622620629399\",\"remoteOption\":\"option_1622620629399\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"创建日期\",\"key\":\"1622620629399\",\"model\":\"\",\"rules\":[]}],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\"},\"key\":\"gkwmyweu\",\"rules\":[]},{\"type\":\"td\",\"list\":[{\"type\":\"input\",\"icon\":\"icon-input\",\"options\":{\"width\":\"100%\",\"defaultValue\":\"$_createDate\",\"required\":false,\"requiredMessage\":\"\",\"dataType\":\"\",\"dataTypeCheck\":false,\"dataTypeMessage\":\"\",\"pattern\":\"\",\"patternCheck\":false,\"patternMessage\":\"\",\"placeholder\":\"\",\"customClass\":\"\",\"readonly\":true,\"disabled\":false,\"labelWidth\":100,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"showPassword\":false,\"remoteFunc\":\"func_1623058529522\",\"remoteOption\":\"option_1623058529522\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"单行文本\",\"key\":\"1623058529522\",\"model\":\"createDate\",\"rules\":[],\"bindTable\":\"y9_form_ziyoubanjian\"}],\"options\":{\"customClass\":\"\",\"colspan\":2,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"markCol\":0,\"markRow\":0,\"invisible\":false},\"key\":\"3au18nns\",\"rules\":[]},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":1,\"markRow\":0},\"key\":\"af7vu9uk\"},{\"type\":\"td\",\"list\":[{\"type\":\"component\",\"icon\":\"icon-component\",\"options\":{\"customClass\":\"label_name\",\"labelWidth\":70,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"template\":\"<div style=\\\"text-align: center;\\\">联系方式</div>\",\"required\":false,\"remoteFunc\":\"func_1623119814666\",\"remoteOption\":\"option_1623119814666\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"\",\"key\":\"1623119814666\",\"model\":\"\",\"rules\":[]}],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":false,\"markCol\":0,\"markRow\":0},\"key\":\"5h4aq9io\",\"rules\":[]},{\"type\":\"td\",\"list\":[{\"type\":\"input\",\"icon\":\"icon-input\",\"options\":{\"width\":\"100%\",\"defaultValue\":\"$_mobile\",\"required\":false,\"requiredMessage\":\"\",\"dataType\":\"\",\"dataTypeCheck\":false,\"dataTypeMessage\":\"\",\"pattern\":\"\",\"patternCheck\":false,\"patternMessage\":\"\",\"placeholder\":\"\",\"customClass\":\"\",\"disabled\":false,\"labelWidth\":100,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"showPassword\":false,\"remoteFunc\":\"func_1622604893427\",\"remoteOption\":\"option_1622604893427\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"单行文本\",\"key\":\"1622604893427\",\"model\":\"contact\",\"rules\":[],\"bindTable\":\"y9_form_ziyoubanjian\"}],\"options\":{\"customClass\":\"td_right_border\",\"colspan\":2,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":false,\"markCol\":0,\"markRow\":0},\"key\":\"v9o626w8\",\"rules\":[]},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":1,\"markRow\":0},\"key\":\"cedoyzx9\"}]},{\"columns\":[{\"type\":\"td\",\"list\":[{\"type\":\"component\",\"icon\":\"icon-component\",\"options\":{\"customClass\":\"label_name\",\"labelWidth\":70,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"template\":\"<div style=\\\"text-align: center;\\\">文件概要</div>\",\"required\":false,\"remoteFunc\":\"func_1623119829222\",\"remoteOption\":\"option_1623119829222\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"文件概要\",\"key\":\"1623119829222\",\"model\":\"\",\"rules\":[]}],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\"},\"key\":\"c6zapnmm\",\"rules\":[]},{\"type\":\"td\",\"list\":[{\"type\":\"textarea\",\"icon\":\"icon-diy-com-textarea\",\"options\":{\"width\":\"\",\"defaultValue\":\"\",\"fieldPermission\":\"\",\"readonly\":false,\"required\":false,\"requiredMessage\":\"\",\"disabled\":false,\"pattern\":\"\",\"patternMessage\":\"\",\"validatorCheck\":false,\"validator\":\"\",\"placeholder\":\"\",\"customClass\":\"\",\"labelWidth\":100,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"clearable\":false,\"maxlength\":\"\",\"showWordLimit\":false,\"rows\":2,\"autosize\":true,\"customProps\":{},\"remoteFunc\":\"func_6fqv454m\",\"remoteOption\":\"option_6fqv454m\",\"tableColumn\":false,\"hideLabel\":true,\"subform\":false},\"events\":{\"onChange\":\"\",\"onFocus\":\"\",\"onBlur\":\"\"},\"name\":\"多行文本\",\"key\":\"6fqv454m\",\"model\":\"outline\",\"rules\":[],\"bindTable\":\"y9_form_ziyoubanjian\"}],\"options\":{\"customClass\":\"td_right_border\",\"colspan\":5,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"markCol\":0,\"markRow\":0},\"key\":\"b4sh7qp4\",\"rules\":[]},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":1,\"markRow\":0},\"key\":\"ln1rgosr\"},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":2,\"markRow\":0},\"key\":\"upzrwt8z\"},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":3,\"markRow\":0},\"key\":\"kty20t84\"},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":4,\"markRow\":0},\"key\":\"me37fxgi\"}]},{\"columns\":[{\"type\":\"td\",\"list\":[{\"type\":\"component\",\"icon\":\"icon-component\",\"options\":{\"customClass\":\"label_name\",\"labelWidth\":70,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"template\":\"<div style=\\\"text-align: center;\\\">发送对象</div>\",\"required\":false,\"remoteFunc\":\"func_1622620634986\",\"remoteOption\":\"option_1622620634986\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"发送对象\",\"key\":\"1622620634986\",\"model\":\"\",\"rules\":[]}],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\"},\"key\":\"8tv1zu1n\"},{\"type\":\"td\",\"list\":[{\"type\":\"input\",\"icon\":\"icon-input\",\"options\":{\"width\":\"\",\"defaultValue\":\"\",\"fieldPermission\":\"\",\"readonly\":false,\"required\":false,\"requiredMessage\":\"\",\"dataType\":\"\",\"dataTypeCheck\":false,\"dataTypeMessage\":\"\",\"pattern\":\"\",\"patternCheck\":false,\"patternMessage\":\"\",\"validatorCheck\":false,\"validator\":\"\",\"placeholder\":\"\",\"customClass\":\"\",\"disabled\":false,\"labelWidth\":100,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"showPassword\":false,\"remoteFunc\":\"func_em58mw4k\",\"remoteOption\":\"option_em58mw4k\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\",\"onFocus\":\"\",\"onBlur\":\"\"},\"name\":\"单行文本\",\"key\":\"em58mw4k\",\"model\":\"send\",\"rules\":[],\"bindTable\":\"y9_form_ziyoubanjian\"}],\"options\":{\"customClass\":\"td_right_border\",\"colspan\":5,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"markCol\":0,\"markRow\":0},\"key\":\"vh76l5jo\",\"rules\":[]},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":1,\"markRow\":0},\"key\":\"vt8dw177\"},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":2,\"markRow\":0},\"key\":\"ujqdwxnj\"},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":3,\"markRow\":0},\"key\":\"b1moimfz\"},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":4,\"markRow\":0},\"key\":\"eekc7l0o\"}]},{\"columns\":[{\"type\":\"td\",\"list\":[{\"type\":\"component\",\"icon\":\"icon-component\",\"options\":{\"customClass\":\"label_name\",\"labelWidth\":70,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"template\":\"<div style=\\\"text-align: center;\\\">紧急程度</div>\",\"required\":false,\"remoteFunc\":\"func_1622620637117\",\"remoteOption\":\"option_1622620637117\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"紧急程度\",\"key\":\"1622620637117\",\"model\":\"\",\"rules\":[]}],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"100px\",\"height\":\"\"},\"key\":\"9qqbzxnp\",\"rules\":[]},{\"type\":\"td\",\"list\":[{\"type\":\"select\",\"icon\":\"icon-select\",\"options\":{\"defaultValue\":\"一般\",\"fieldPermission\":\"\",\"optionData\":\"报销类别(baoxiaoleibie)\",\"multiple\":false,\"disabled\":false,\"clearable\":false,\"placeholder\":\"\",\"required\":false,\"requiredMessage\":\"\",\"validatorCheck\":false,\"validator\":\"\",\"showLabel\":false,\"width\":\"\",\"options\":[{\"value\":\"一般\"},{\"value\":\"紧急\"},{\"value\":\"重要\"}],\"remote\":false,\"remoteType\":\"func\",\"remoteOption\":\"option_ifro72rr\",\"filterable\":false,\"remoteOptions\":[],\"props\":{\"value\":\"value\",\"label\":\"label\"},\"remoteFunc\":\"getOptionData\",\"customClass\":\"\",\"labelWidth\":100,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\",\"onFocus\":\"\",\"onBlur\":\"\"},\"name\":\"下拉选择框\",\"key\":\"ifro72rr\",\"model\":\"level\",\"rules\":[],\"bindTable\":\"y9_form_ziyoubanjian\"}],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":false,\"markCol\":0,\"markRow\":1},\"key\":\"debvsswi\",\"rules\":[]},{\"type\":\"td\",\"list\":[{\"type\":\"component\",\"icon\":\"icon-component\",\"options\":{\"customClass\":\"label_name\",\"labelWidth\":70,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"template\":\"<div style=\\\"text-align: center;\\\">签发人</div>\",\"required\":false,\"remoteFunc\":\"func_1622620316591\",\"remoteOption\":\"option_1622620316591\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"签发人\",\"key\":\"1622620316591\",\"model\":\"\",\"rules\":[]}],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\"},\"key\":\"p1yc1nuu\",\"rules\":[]},{\"type\":\"td\",\"list\":[{\"type\":\"input\",\"icon\":\"icon-input\",\"options\":{\"width\":\"100%\",\"defaultValue\":\"\",\"required\":false,\"requiredMessage\":\"\",\"dataType\":\"number\",\"dataTypeCheck\":false,\"dataTypeMessage\":\"\",\"pattern\":\"\",\"patternCheck\":false,\"patternMessage\":\"\",\"placeholder\":\"\",\"customClass\":\"\",\"disabled\":false,\"labelWidth\":100,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"showPassword\":false,\"remoteFunc\":\"func_1622518488297\",\"remoteOption\":\"option_1622518488297\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"单行文本\",\"key\":\"1622518488297\",\"model\":\"signAndIssue\",\"rules\":[],\"bindTable\":\"y9_form_ziyoubanjian\"}],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\"},\"key\":\"xybg4l94\",\"rules\":[]},{\"type\":\"td\",\"list\":[{\"type\":\"component\",\"icon\":\"icon-component\",\"options\":{\"customClass\":\"label_name\",\"labelWidth\":70,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"template\":\"<div style=\\\"text-align: center;\\\">签发日期</div>\",\"required\":false,\"remoteFunc\":\"func_1622620389681\",\"remoteOption\":\"option_1622620389681\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"签发日期\",\"key\":\"1622620389681\",\"model\":\"\",\"rules\":[]}],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\"},\"key\":\"kjr0eaz7\",\"rules\":[]},{\"type\":\"td\",\"list\":[{\"type\":\"date\",\"icon\":\"icon-date\",\"options\":{\"defaultValue\":\"\",\"fieldPermission\":\"\",\"bindDatabase\":true,\"readonly\":false,\"disabled\":false,\"editable\":true,\"clearable\":true,\"placeholder\":\"\",\"startPlaceholder\":\"\",\"endPlaceholder\":\"\",\"type\":\"date\",\"format\":\"YYYY-MM-DD\",\"timestamp\":false,\"required\":false,\"requiredMessage\":\"\",\"validatorCheck\":false,\"validator\":\"\",\"width\":\"\",\"customClass\":\"\",\"labelWidth\":100,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"remoteFunc\":\"func_za06mp2r\",\"remoteOption\":\"option_za06mp2r\",\"tableColumn\":false,\"hideLabel\":true,\"subform\":false},\"events\":{\"onChange\":\"\",\"onFocus\":\"\",\"onBlur\":\"\"},\"name\":\"日期选择器\",\"key\":\"za06mp2r\",\"model\":\"dateOfIssue\",\"rules\":[],\"bindTable\":\"y9_form_ziyoubanjian\"}],\"options\":{\"customClass\":\"td_right_border\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\"},\"key\":\"ewgt6p2c\",\"rules\":[]}]},{\"columns\":[{\"type\":\"td\",\"list\":[{\"type\":\"component\",\"icon\":\"icon-component\",\"options\":{\"customClass\":\"label_name\",\"labelWidth\":70,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"template\":\"<div style=\\\"text-align: center;\\\">备注</div>\",\"required\":false,\"remoteFunc\":\"func_1622620639888\",\"remoteOption\":\"option_1622620639888\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"备注\",\"key\":\"1622620639888\",\"model\":\"\",\"rules\":[]}],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\"},\"key\":\"w4gwxmjc\",\"rules\":[]},{\"type\":\"td\",\"list\":[{\"type\":\"textarea\",\"icon\":\"icon-diy-com-textarea\",\"options\":{\"width\":\"\",\"defaultValue\":\"\",\"fieldPermission\":\"\",\"readonly\":false,\"required\":false,\"requiredMessage\":\"\",\"disabled\":false,\"pattern\":\"\",\"patternMessage\":\"\",\"validatorCheck\":false,\"validator\":\"\",\"placeholder\":\"\",\"customClass\":\"\",\"labelWidth\":100,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"clearable\":false,\"maxlength\":\"500\",\"showWordLimit\":false,\"rows\":5,\"autosize\":true,\"customProps\":{},\"remoteFunc\":\"func_k5pauftn\",\"remoteOption\":\"option_k5pauftn\",\"tableColumn\":false,\"hideLabel\":true,\"subform\":false},\"events\":{\"onChange\":\"\",\"onFocus\":\"\",\"onBlur\":\"\"},\"name\":\"多行文本\",\"key\":\"k5pauftn\",\"model\":\"remarks\",\"rules\":[],\"bindTable\":\"y9_form_ziyoubanjian\"}],\"options\":{\"customClass\":\"td_right_border\",\"colspan\":5,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"markCol\":0,\"markRow\":0},\"key\":\"m02pov8b\",\"rules\":[]},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":1,\"markRow\":0},\"key\":\"j6l0143b\"},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":2,\"markRow\":0},\"key\":\"yn1q0gxb\"},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":3,\"markRow\":0},\"key\":\"xxgfdlr3\",\"rules\":[]},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":4,\"markRow\":0},\"key\":\"tdf8j37g\"}]},{\"columns\":[{\"type\":\"td\",\"list\":[{\"type\":\"component\",\"icon\":\"icon-component\",\"options\":{\"customClass\":\"label_name\",\"labelWidth\":70,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"template\":\"<div style=\\\"text-align: center;\\\">意见指示</div>\",\"required\":false,\"remoteFunc\":\"func_1622620641650\",\"remoteOption\":\"option_1622620641650\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"意见指示\",\"key\":\"1622620641650\",\"model\":\"\",\"rules\":[]}],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\"},\"key\":\"3qfxn08o\",\"rules\":[]},{\"type\":\"td\",\"list\":[{\"name\":\"个人意见\",\"el\":\"custom-opinion\",\"options\":{\"defaultValue\":{},\"customClass\":\"\",\"labelWidth\":0,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"required\":false,\"minHeight\":\"\",\"pattern\":\"\",\"remoteFunc\":\"func_ndx7o69w\",\"remoteOption\":\"option_ndx7o69w\",\"tableColumn\":false,\"hideLabel\":true,\"subform\":false},\"type\":\"custom\",\"icon\":\"icon-extend\",\"key\":\"ndx7o69w\",\"model\":\"custom_opinion@personalComment\",\"rules\":[]}],\"options\":{\"customClass\":\"td_right_border\",\"colspan\":5,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"markCol\":0,\"markRow\":0},\"key\":\"vyp91xlq\",\"rules\":[]},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":1,\"markRow\":0},\"key\":\"pmuquuh6\"},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":2,\"markRow\":0},\"key\":\"x8neojtr\"},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":3,\"markRow\":0},\"key\":\"me4c2fth\"},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":4,\"markRow\":0},\"key\":\"h3s6cgvv\"}]}],\"name\":\"表格布局\",\"key\":\"1622518382735\",\"model\":\"report_1622518382735\",\"rules\":[]}],\"config\":{\"ui\":\"element\",\"size\":\"small\",\"width\":\"100%\",\"layout\":\"horizontal\",\"labelCol\":3,\"hideLabel\":false,\"labelWidth\":100,\"customClass\":\"form\",\"labelPosition\":\"right\",\"hideErrorMessage\":false,\"styleSheets\":\"\\n.formheader{\\n  width:750px;\\n  margin: auto;\\n}\\n.formheader .el-form-item__content{\\n  text-align: center;\\n  font-size: 38px;\\n  margin: 50px 0 10px 0;\\n  letter-spacing: 5px;\\n  color:#000;\\n} \\n\\n.form .el-form-item__content{\\n  line-height: 40px;\\n}\\n.form{\\n  background-color:#fff;\\n  margin: auto;\\n  padding-bottom: 30px;\\n}\\n.form .el-textarea__inner{\\n  resize: none;\\n  border:none;\\n  box-shadow: none;\\n  padding:0;\\n  color: #000;\\n  min-height: 40px !important;\\n}\\n.form .el-date-editor.el-input, .el-date-editor.el-input__inner{\\n  width:100%;\\n   height: 35px;\\n  line-height: 35px;\\n}\\n.form .el-input__wrapper{\\n  border:none;\\n  box-shadow: none;\\n}\\n\\n.form .el-input__inner{\\n  padding:0;\\n  width:100%;\\n  color: #000;\\n}\\n.form .el-input--prefix .el-input__inner{\\n  padding-left: 0px;\\n}\\n.form .el-input--suffix .el-input__inner{\\n  padding-right:0;\\n}\\n.form .el-input--prefix .el-input__inner{\\n  padding-left: 30px;\\n}\\n.form .el-input--small .el-input__inner {\\n  height: 35px;\\n  line-height: 35px;\\n}\\n.label_name .el-form-item__content{\\n  text-align: center;\\n  padding:0;\\n  color:#000;\\n  line-height: 40px;\\n  width:100%;\\n  margin-left: 0;\\n}\\n.table{\\n  margin: auto;\\n  border-left: 0px;\\n}\\n.table .td_right_border{\\n  border-right: 0px;\\n}\\n.td_right_border{\\n  border-right: 0px;\\n}\\n.form .el-input.is-disabled .el-input__inner {\\n  background-color:#fff;\\n  color: #000;\\n  -webkit-text-fill-color: #000;\\n}\\n.form .el-select{\\n  --el-select-disabled-border: none;\\n}\\n\\n\\n.form .el-input.is-disabled .el-input__wrapper {\\n  background-color: #fff;\\n  box-shadow: none;\\n}\\n\\n.form .el-textarea.is-disabled .el-textarea__inner{\\n   background-color:#fff;\\n   color: #000;\\n}\\n\\n\\n.form .el-divider--horizontal {\\n    display: block;\\n    height: 1px;\\n    width: 690px;\\n    margin: 30px auto 20px;\\n}\\n.form .el-divider__text{\\n    font-weight: 500;\\n    font-size: 20px;\\n    line-height: 20px;\\n}\\n\\n\",\"dataSource\":[{\"key\":\"yow40f7g\",\"name\":\"初始化数据\",\"url\":\"https://vue.youshengyun.com:8443/flowableUI/vue/y9form/getInitData\",\"method\":\"GET\",\"auto\":false,\"params\":{},\"headers\":{},\"responseFunc\":\"let initdata = res.data;\\nlet data = {};\\ndata.number = initdata.number;\\ndata.wordSize = initdata.zihao;\\ndata.department = initdata.deptName;\\ndata.creater = initdata.userName;\\ndata.createDate = initdata.createDate;\\ndata.contact = initdata.mobile;\\nthis.setData(data);\\nreturn res;\",\"requestFunc\":\"return config;\"}],\"eventScript\":[{\"key\":\"mounted\",\"name\":\"mounted\",\"func\":\"\"}],\"initDataUrl\":\"\"}}");
            y9FormRepository.save(y9Form);

            List<Y9FormField> list = new ArrayList<>();
            Y9FormField newField = new Y9FormField();
            newField.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldCnName("主键").setFieldName("guid")
                .setFieldType("varchar(50)").setFormId(PRINT_FORM_ID).setTableId(Y9_TABLE_ID)
                .setTableName(Y9_TABLE_NAME);
            list.add(newField);
            newField = new Y9FormField();
            newField.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldCnName("文件编号").setFieldName("number")
                .setFieldType("varchar(50)").setFormId(PRINT_FORM_ID).setTableId(Y9_TABLE_ID)
                .setTableName(Y9_TABLE_NAME);
            list.add(newField);
            newField = new Y9FormField();
            newField.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldCnName("类型").setFieldName("type")
                .setFieldType("varchar(20)").setFormId(PRINT_FORM_ID).setTableId(Y9_TABLE_ID)
                .setTableName(Y9_TABLE_NAME);
            list.add(newField);
            newField = new Y9FormField();
            newField.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldCnName("字号").setFieldName("wordSize")
                .setFieldType("varchar(20)").setFormId(PRINT_FORM_ID).setTableId(Y9_TABLE_ID)
                .setTableName(Y9_TABLE_NAME);
            list.add(newField);
            newField = new Y9FormField();
            newField.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldCnName("标题").setFieldName("title")
                .setFieldType("varchar(500)").setFormId(PRINT_FORM_ID).setTableId(Y9_TABLE_ID)
                .setTableName(Y9_TABLE_NAME);
            list.add(newField);
            newField = new Y9FormField();
            newField.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldCnName("创建部门").setFieldName("department")
                .setFieldType("varchar(50)").setFormId(PRINT_FORM_ID).setTableId(Y9_TABLE_ID)
                .setTableName(Y9_TABLE_NAME);
            list.add(newField);
            newField = new Y9FormField();
            newField.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldCnName("创建人").setFieldName("creater")
                .setFieldType("varchar(50)").setFormId(PRINT_FORM_ID).setTableId(Y9_TABLE_ID)
                .setTableName(Y9_TABLE_NAME);
            list.add(newField);
            newField = new Y9FormField();
            newField.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldCnName("创建时间").setFieldName("createDate")
                .setFieldType("varchar(50)").setFormId(PRINT_FORM_ID).setTableId(Y9_TABLE_ID)
                .setTableName(Y9_TABLE_NAME);
            list.add(newField);
            newField = new Y9FormField();
            newField.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldCnName("联系方式").setFieldName("contact")
                .setFieldType("varchar(20)").setFormId(PRINT_FORM_ID).setTableId(Y9_TABLE_ID)
                .setTableName(Y9_TABLE_NAME);
            list.add(newField);
            newField = new Y9FormField();
            newField.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldCnName("主题词").setFieldName("motive")
                .setFieldType("varchar(2000)").setFormId(PRINT_FORM_ID).setTableId(Y9_TABLE_ID)
                .setTableName(Y9_TABLE_NAME);
            list.add(newField);
            newField = new Y9FormField();
            newField.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldCnName("发送对象").setFieldName("send")
                .setFieldType("varchar(500)").setFormId(PRINT_FORM_ID).setTableId(Y9_TABLE_ID)
                .setTableName(Y9_TABLE_NAME);
            list.add(newField);
            newField = new Y9FormField();
            newField.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldCnName("紧急程度").setFieldName("level")
                .setFieldType("varchar(20)").setFormId(PRINT_FORM_ID).setTableId(Y9_TABLE_ID)
                .setTableName(Y9_TABLE_NAME);
            list.add(newField);
            newField = new Y9FormField();
            newField.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldCnName("签发人").setFieldName("signAndIssue")
                .setFieldType("varchar(50)").setFormId(PRINT_FORM_ID).setTableId(Y9_TABLE_ID)
                .setTableName(Y9_TABLE_NAME);
            list.add(newField);
            newField = new Y9FormField();
            newField.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldCnName("签发日期").setFieldName("dateOfIssue")
                .setFieldType("varchar(50)").setFormId(PRINT_FORM_ID).setTableId(Y9_TABLE_ID)
                .setTableName(Y9_TABLE_NAME);
            list.add(newField);
            newField = new Y9FormField();
            newField.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldCnName("备注").setFieldName("remarks")
                .setFieldType("varchar(500)").setFormId(PRINT_FORM_ID).setTableId(Y9_TABLE_ID)
                .setTableName(Y9_TABLE_NAME);
            list.add(newField);
            newField = new Y9FormField();
            newField.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldCnName("文件概要").setFieldName("outline")
                .setFieldType("varchar(2000)").setFormId(PRINT_FORM_ID).setTableId(Y9_TABLE_ID)
                .setTableName(Y9_TABLE_NAME);
            list.add(newField);
            y9FormFieldRepository.saveAll(list);
        }
    }

    private void createY9Form() {
        Y9Form y9Form = y9FormRepository.findById(Y9_FORM_ID).orElse(null);
        if (null == y9Form) {
            y9Form = new Y9Form();
            y9Form.setId(Y9_FORM_ID);
            y9Form.setFormName(Y9_FORM_NAME);
            y9Form.setSystemCnName(SYSTEMCNNAME);
            y9Form.setSystemName(SYSTEMNAME);
            y9Form.setUpdateTime(new Date());
            y9Form.setTenantId(Y9LoginUserHolder.getTenantId());
            y9Form.setFormJson(
                "{\"list\":[{\"type\":\"component\",\"icon\":\"icon-component\",\"options\":{\"customClass\":\"formheader\",\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"template\":\"<div style=\\\"text-align: center;\\\">办件信息</div>\",\"required\":false,\"remoteFunc\":\"func_1622619081352\",\"remoteOption\":\"option_1622619081352\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"办件信息\",\"key\":\"1622619081352\",\"model\":\"\",\"rules\":[]},{\"type\":\"input\",\"icon\":\"icon-input\",\"options\":{\"width\":\"100%\",\"defaultValue\":\"\",\"required\":false,\"requiredMessage\":\"\",\"dataType\":\"\",\"dataTypeCheck\":false,\"dataTypeMessage\":\"\",\"pattern\":\"\",\"patternCheck\":false,\"patternMessage\":\"\",\"placeholder\":\"\",\"customClass\":\"\",\"disabled\":false,\"labelWidth\":100,\"isLabelWidth\":false,\"hidden\":true,\"dataBind\":true,\"showPassword\":false,\"remoteFunc\":\"func_1623031603653\",\"remoteOption\":\"option_1623031603653\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"\",\"key\":\"1623031603653\",\"model\":\"guid\",\"rules\":[],\"bindTable\":\"y9_form_ziyoubanjian\"},{\"type\":\"input\",\"icon\":\"icon-input\",\"options\":{\"width\":\"100%\",\"defaultValue\":\"\",\"required\":false,\"requiredMessage\":\"\",\"dataType\":\"\",\"dataTypeCheck\":false,\"dataTypeMessage\":\"\",\"pattern\":\"\",\"patternCheck\":false,\"patternMessage\":\"\",\"placeholder\":\"\",\"customClass\":\"\",\"disabled\":false,\"labelWidth\":100,\"isLabelWidth\":false,\"hidden\":true,\"dataBind\":true,\"showPassword\":false,\"remoteFunc\":\"func_1623031607288\",\"remoteOption\":\"option_1623031607288\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"单行文本\",\"key\":\"1623031607288\",\"model\":\"processInstanceId\",\"rules\":[]},{\"type\":\"report\",\"icon\":\"icon-table1\",\"options\":{\"customClass\":\"table\",\"hidden\":false,\"borderWidth\":2,\"borderColor\":\"#F70434\",\"width\":\"690px\",\"remoteFunc\":\"func_1622518382735\",\"remoteOption\":\"option_1622518382735\",\"tableColumn\":false},\"rows\":[{\"columns\":[{\"type\":\"td\",\"list\":[{\"type\":\"component\",\"icon\":\"icon-component\",\"options\":{\"customClass\":\"label_name\",\"labelWidth\":0,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"template\":\"<div style=\\\"text-align: center;\\\">文件编号</div>\",\"required\":false,\"remoteFunc\":\"func_1622619484999\",\"remoteOption\":\"option_1622619484999\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"\",\"key\":\"1622619484999\",\"model\":\"\",\"rules\":[]}],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"80px\",\"height\":\"\"},\"key\":\"kwj6q28x\",\"rules\":[]},{\"type\":\"td\",\"list\":[{\"type\":\"input\",\"icon\":\"icon-input\",\"options\":{\"width\":\"100%\",\"defaultValue\":\"$_number\",\"required\":false,\"requiredMessage\":\"\",\"dataType\":\"\",\"dataTypeCheck\":false,\"dataTypeMessage\":\"\",\"pattern\":\"\",\"patternCheck\":false,\"patternMessage\":\"\",\"placeholder\":\"\",\"customClass\":\"\",\"readonly\":true,\"disabled\":false,\"labelWidth\":100,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"showPassword\":false,\"remoteFunc\":\"func_1623058442021\",\"remoteOption\":\"option_1623058442021\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"单行文本\",\"key\":\"1623058442021\",\"model\":\"number\",\"rules\":[],\"bindTable\":\"y9_form_ziyoubanjian\"}],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\"},\"key\":\"laz0ay9t\",\"rules\":[]},{\"type\":\"td\",\"list\":[{\"type\":\"component\",\"icon\":\"icon-component\",\"options\":{\"customClass\":\"label_name\",\"labelWidth\":70,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"template\":\"<div style=\\\"text-align: center;\\\">类型</div>\",\"required\":false,\"remoteFunc\":\"func_1622619560645\",\"remoteOption\":\"option_1622619560645\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"类型\",\"key\":\"1622619560645\",\"model\":\"\",\"rules\":[]}],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"80px\",\"height\":\"\"},\"key\":\"erum9hdu\",\"rules\":[]},{\"type\":\"td\",\"list\":[{\"type\":\"input\",\"icon\":\"icon-input\",\"options\":{\"width\":\"100%\",\"defaultValue\":\"自由办件\",\"required\":false,\"requiredMessage\":\"\",\"dataType\":\"\",\"dataTypeCheck\":false,\"dataTypeMessage\":\"\",\"pattern\":\"\",\"patternCheck\":false,\"patternMessage\":\"\",\"placeholder\":\"\",\"customClass\":\"\",\"readonly\":true,\"disabled\":false,\"labelWidth\":100,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"showPassword\":false,\"remoteFunc\":\"func_1623049264602\",\"remoteOption\":\"option_1623049264602\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"单行文本\",\"key\":\"1623049264602\",\"model\":\"type\",\"rules\":[],\"bindTable\":\"y9_form_ziyoubanjian\"}],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"80px\",\"height\":\"\"},\"key\":\"f74fih4i\",\"rules\":[]},{\"type\":\"td\",\"list\":[{\"type\":\"component\",\"icon\":\"icon-component\",\"options\":{\"customClass\":\"label_name\",\"labelWidth\":70,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"template\":\"<div style=\\\"text-align: center;\\\">字号</div>\",\"required\":false,\"remoteFunc\":\"func_1622620205114\",\"remoteOption\":\"option_1622620205114\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"\",\"key\":\"1622620205114\",\"model\":\"\",\"rules\":[]}],\"options\":{\"customClass\":\"td_12\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"80px\",\"height\":\"\"},\"key\":\"io2ijobw\",\"rules\":[]},{\"type\":\"td\",\"list\":[{\"type\":\"input\",\"icon\":\"icon-input\",\"options\":{\"width\":\"100%\",\"defaultValue\":\"$_zihao\",\"required\":false,\"requiredMessage\":\"\",\"dataType\":\"\",\"dataTypeCheck\":false,\"dataTypeMessage\":\"\",\"pattern\":\"\",\"patternCheck\":false,\"patternMessage\":\"\",\"placeholder\":\"\",\"customClass\":\"\",\"readonly\":true,\"disabled\":false,\"labelWidth\":100,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"showPassword\":false,\"remoteFunc\":\"func_1623058458466\",\"remoteOption\":\"option_1623058458466\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"单行文本\",\"key\":\"1623058458466\",\"model\":\"wordSize\",\"rules\":[],\"bindTable\":\"y9_form_ziyoubanjian\"}],\"options\":{\"customClass\":\"td_right_border\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"150px\",\"height\":\"\"},\"key\":\"i4jrow3s\",\"rules\":[]}]},{\"columns\":[{\"type\":\"td\",\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\"},\"list\":[{\"type\":\"component\",\"icon\":\"icon-component\",\"options\":{\"customClass\":\"label_name\",\"labelWidth\":70,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"template\":\"<div style=\\\"text-align: center;\\\">文件标题</div>\",\"required\":false,\"remoteFunc\":\"func_1622620587996\",\"remoteOption\":\"option_1622620587996\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"\",\"key\":\"1622620587996\",\"model\":\"\",\"rules\":[]}],\"key\":\"t3op0byh\",\"rules\":[]},{\"type\":\"td\",\"list\":[{\"type\":\"input\",\"icon\":\"icon-input\",\"options\":{\"width\":\"100%\",\"defaultValue\":\"\",\"required\":true,\"requiredMessage\":\"请输入文件标题。\",\"dataType\":\"\",\"dataTypeCheck\":false,\"dataTypeMessage\":\"\",\"pattern\":\"\",\"patternCheck\":false,\"patternMessage\":\"\",\"placeholder\":\"\",\"customClass\":\"\",\"disabled\":false,\"labelWidth\":100,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"showPassword\":false,\"remoteFunc\":\"func_1622518410686\",\"remoteOption\":\"option_1622518410686\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"单行文本\",\"key\":\"1622518410686\",\"model\":\"title\",\"rules\":[{\"required\":true,\"message\":\"请输入文件标题。\"}],\"bindTable\":\"y9_form_ziyoubanjian\"}],\"options\":{\"customClass\":\"td_right_border\",\"colspan\":5,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"markCol\":0,\"markRow\":0},\"key\":\"rhnhg09l\",\"rules\":[]},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":1,\"markRow\":0},\"key\":\"gll2o2rj\",\"rules\":[]},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":2,\"markRow\":0},\"key\":\"pv8s5nee\",\"rules\":[]},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":2,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"markCol\":3,\"markRow\":0,\"invisible\":true},\"key\":\"x1420xkf\",\"rules\":[]},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":4,\"markRow\":0},\"key\":\"6mqjsfvc\"}]},{\"columns\":[{\"type\":\"td\",\"list\":[{\"type\":\"component\",\"icon\":\"icon-component\",\"options\":{\"customClass\":\"label_name\",\"labelWidth\":70,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"template\":\"<div style=\\\"text-align: center;\\\">创建部门</div>\",\"required\":false,\"remoteFunc\":\"func_1622620622928\",\"remoteOption\":\"option_1622620622928\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"创建部门\",\"key\":\"1622620622928\",\"model\":\"\",\"rules\":[]}],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\"},\"key\":\"vxrmj5zb\",\"rules\":[]},{\"type\":\"td\",\"list\":[{\"type\":\"input\",\"icon\":\"icon-input\",\"options\":{\"width\":\"100%\",\"defaultValue\":\"$_deptName\",\"required\":false,\"requiredMessage\":\"\",\"dataType\":\"\",\"dataTypeCheck\":false,\"dataTypeMessage\":\"\",\"pattern\":\"\",\"patternCheck\":false,\"patternMessage\":\"\",\"placeholder\":\"\",\"customClass\":\"\",\"readonly\":true,\"disabled\":false,\"labelWidth\":100,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"showPassword\":false,\"remoteFunc\":\"func_1623058477924\",\"remoteOption\":\"option_1623058477924\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"单行文本\",\"key\":\"1623058477924\",\"model\":\"department\",\"rules\":[],\"bindTable\":\"y9_form_ziyoubanjian\"}],\"options\":{\"customClass\":\"\",\"colspan\":2,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"markCol\":0,\"markRow\":0},\"key\":\"cqwjdr37\",\"rules\":[]},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":1,\"markRow\":0},\"key\":\"nizqs2un\"},{\"type\":\"td\",\"list\":[{\"type\":\"component\",\"icon\":\"icon-component\",\"options\":{\"customClass\":\"label_name\",\"labelWidth\":70,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"template\":\"<div style=\\\"text-align: center;\\\">创建人</div>\",\"required\":false,\"remoteFunc\":\"func_1622620627563\",\"remoteOption\":\"option_1622620627563\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"\",\"key\":\"1622620627563\",\"model\":\"\",\"rules\":[]}],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\"},\"key\":\"lzbmolxr\",\"rules\":[]},{\"type\":\"td\",\"list\":[{\"type\":\"input\",\"icon\":\"icon-input\",\"options\":{\"width\":\"100%\",\"defaultValue\":\"$_userName\",\"required\":false,\"requiredMessage\":\"\",\"dataType\":\"\",\"dataTypeCheck\":false,\"dataTypeMessage\":\"\",\"pattern\":\"\",\"patternCheck\":false,\"patternMessage\":\"\",\"placeholder\":\"\",\"customClass\":\"\",\"readonly\":true,\"disabled\":false,\"labelWidth\":100,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"showPassword\":false,\"remoteFunc\":\"func_1623058505798\",\"remoteOption\":\"option_1623058505798\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"单行文本\",\"key\":\"1623058505798\",\"model\":\"creater\",\"rules\":[],\"bindTable\":\"y9_form_ziyoubanjian\"}],\"options\":{\"customClass\":\"td_right_border\",\"colspan\":2,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"markCol\":0,\"markRow\":0},\"key\":\"79c002x5\",\"rules\":[]},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":1,\"markRow\":0},\"key\":\"qlafytm9\"}]},{\"columns\":[{\"type\":\"td\",\"list\":[{\"type\":\"component\",\"icon\":\"icon-component\",\"options\":{\"customClass\":\"label_name\",\"labelWidth\":70,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"template\":\"<div style=\\\"text-align: center;\\\">创建日期</div>\",\"required\":false,\"remoteFunc\":\"func_1622620629399\",\"remoteOption\":\"option_1622620629399\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"创建日期\",\"key\":\"1622620629399\",\"model\":\"\",\"rules\":[]}],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\"},\"key\":\"gkwmyweu\",\"rules\":[]},{\"type\":\"td\",\"list\":[{\"type\":\"input\",\"icon\":\"icon-input\",\"options\":{\"width\":\"100%\",\"defaultValue\":\"$_createDate\",\"required\":false,\"requiredMessage\":\"\",\"dataType\":\"\",\"dataTypeCheck\":false,\"dataTypeMessage\":\"\",\"pattern\":\"\",\"patternCheck\":false,\"patternMessage\":\"\",\"placeholder\":\"\",\"customClass\":\"\",\"readonly\":true,\"disabled\":false,\"labelWidth\":100,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"showPassword\":false,\"remoteFunc\":\"func_1623058529522\",\"remoteOption\":\"option_1623058529522\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"单行文本\",\"key\":\"1623058529522\",\"model\":\"createDate\",\"rules\":[],\"bindTable\":\"y9_form_ziyoubanjian\"}],\"options\":{\"customClass\":\"\",\"colspan\":2,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"markCol\":0,\"markRow\":0,\"invisible\":false},\"key\":\"3au18nns\",\"rules\":[]},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":1,\"markRow\":0},\"key\":\"af7vu9uk\"},{\"type\":\"td\",\"list\":[{\"type\":\"component\",\"icon\":\"icon-component\",\"options\":{\"customClass\":\"label_name\",\"labelWidth\":70,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"template\":\"<div style=\\\"text-align: center;\\\">联系方式</div>\",\"required\":false,\"remoteFunc\":\"func_1623119814666\",\"remoteOption\":\"option_1623119814666\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"\",\"key\":\"1623119814666\",\"model\":\"\",\"rules\":[]}],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":false,\"markCol\":0,\"markRow\":0},\"key\":\"5h4aq9io\",\"rules\":[]},{\"type\":\"td\",\"list\":[{\"type\":\"input\",\"icon\":\"icon-input\",\"options\":{\"width\":\"100%\",\"defaultValue\":\"$_mobile\",\"required\":false,\"requiredMessage\":\"\",\"dataType\":\"\",\"dataTypeCheck\":false,\"dataTypeMessage\":\"\",\"pattern\":\"\",\"patternCheck\":false,\"patternMessage\":\"\",\"placeholder\":\"\",\"customClass\":\"\",\"disabled\":false,\"labelWidth\":100,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"showPassword\":false,\"remoteFunc\":\"func_1622604893427\",\"remoteOption\":\"option_1622604893427\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"单行文本\",\"key\":\"1622604893427\",\"model\":\"contact\",\"rules\":[],\"bindTable\":\"y9_form_ziyoubanjian\"}],\"options\":{\"customClass\":\"td_right_border\",\"colspan\":2,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":false,\"markCol\":0,\"markRow\":0},\"key\":\"v9o626w8\",\"rules\":[]},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":1,\"markRow\":0},\"key\":\"cedoyzx9\"}]},{\"columns\":[{\"type\":\"td\",\"list\":[{\"type\":\"component\",\"icon\":\"icon-component\",\"options\":{\"customClass\":\"label_name\",\"labelWidth\":70,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"template\":\"<div style=\\\"text-align: center;\\\">文件概要</div>\",\"required\":false,\"remoteFunc\":\"func_1623119829222\",\"remoteOption\":\"option_1623119829222\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"文件概要\",\"key\":\"1623119829222\",\"model\":\"\",\"rules\":[]}],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\"},\"key\":\"c6zapnmm\",\"rules\":[]},{\"type\":\"td\",\"list\":[{\"type\":\"textarea\",\"icon\":\"icon-diy-com-textarea\",\"options\":{\"width\":\"\",\"defaultValue\":\"\",\"fieldPermission\":\"\",\"readonly\":false,\"required\":false,\"requiredMessage\":\"\",\"disabled\":false,\"pattern\":\"\",\"patternMessage\":\"\",\"validatorCheck\":false,\"validator\":\"\",\"placeholder\":\"\",\"customClass\":\"\",\"labelWidth\":100,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"clearable\":false,\"maxlength\":\"500\",\"showWordLimit\":false,\"rows\":5,\"autosize\":true,\"customProps\":{},\"remoteFunc\":\"func_55qfuh0o\",\"remoteOption\":\"option_55qfuh0o\",\"tableColumn\":false,\"hideLabel\":true,\"subform\":false},\"events\":{\"onChange\":\"\",\"onFocus\":\"\",\"onBlur\":\"\"},\"name\":\"多行文本\",\"key\":\"55qfuh0o\",\"model\":\"outline\",\"rules\":[],\"bindTable\":\"y9_form_ziyoubanjian\"}],\"options\":{\"customClass\":\"td_right_border\",\"colspan\":5,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"markCol\":0,\"markRow\":0},\"key\":\"b4sh7qp4\",\"rules\":[]},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":1,\"markRow\":0},\"key\":\"ln1rgosr\"},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":2,\"markRow\":0},\"key\":\"upzrwt8z\"},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":3,\"markRow\":0},\"key\":\"kty20t84\"},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":4,\"markRow\":0},\"key\":\"me37fxgi\"}]},{\"columns\":[{\"type\":\"td\",\"list\":[{\"type\":\"component\",\"icon\":\"icon-component\",\"options\":{\"customClass\":\"label_name\",\"labelWidth\":70,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"template\":\"<div style=\\\"text-align: center;\\\">发送对象</div>\",\"required\":false,\"remoteFunc\":\"func_1622620634986\",\"remoteOption\":\"option_1622620634986\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"发送对象\",\"key\":\"1622620634986\",\"model\":\"\",\"rules\":[]}],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\"},\"key\":\"8tv1zu1n\"},{\"type\":\"td\",\"list\":[{\"type\":\"input\",\"icon\":\"icon-input\",\"options\":{\"width\":\"\",\"defaultValue\":\"\",\"fieldPermission\":\"\",\"readonly\":false,\"required\":false,\"requiredMessage\":\"\",\"dataType\":\"\",\"dataTypeCheck\":false,\"dataTypeMessage\":\"\",\"pattern\":\"\",\"patternCheck\":false,\"patternMessage\":\"\",\"validatorCheck\":false,\"validator\":\"\",\"placeholder\":\"\",\"customClass\":\"\",\"disabled\":false,\"labelWidth\":100,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"showPassword\":false,\"remoteFunc\":\"func_em58mw4k\",\"remoteOption\":\"option_em58mw4k\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\",\"onFocus\":\"\",\"onBlur\":\"\"},\"name\":\"单行文本\",\"key\":\"em58mw4k\",\"model\":\"send\",\"rules\":[],\"bindTable\":\"y9_form_ziyoubanjian\"}],\"options\":{\"customClass\":\"td_right_border\",\"colspan\":5,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"markCol\":0,\"markRow\":0},\"key\":\"vh76l5jo\",\"rules\":[]},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":1,\"markRow\":0},\"key\":\"vt8dw177\"},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":2,\"markRow\":0},\"key\":\"ujqdwxnj\"},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":3,\"markRow\":0},\"key\":\"b1moimfz\"},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":4,\"markRow\":0},\"key\":\"eekc7l0o\"}]},{\"columns\":[{\"type\":\"td\",\"list\":[{\"type\":\"component\",\"icon\":\"icon-component\",\"options\":{\"customClass\":\"label_name\",\"labelWidth\":70,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"template\":\"<div style=\\\"text-align: center;\\\">紧急程度</div>\",\"required\":false,\"remoteFunc\":\"func_1622620637117\",\"remoteOption\":\"option_1622620637117\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"紧急程度\",\"key\":\"1622620637117\",\"model\":\"\",\"rules\":[]}],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"100px\",\"height\":\"\"},\"key\":\"9qqbzxnp\",\"rules\":[]},{\"type\":\"td\",\"list\":[{\"type\":\"select\",\"icon\":\"icon-select\",\"options\":{\"defaultValue\":\"一般\",\"fieldPermission\":\"\",\"optionData\":\"报销类别(baoxiaoleibie)\",\"multiple\":false,\"disabled\":false,\"clearable\":false,\"placeholder\":\"\",\"required\":false,\"requiredMessage\":\"\",\"validatorCheck\":false,\"validator\":\"\",\"showLabel\":false,\"width\":\"\",\"options\":[{\"value\":\"一般\"},{\"value\":\"紧急\"},{\"value\":\"重要\"}],\"remote\":false,\"remoteType\":\"func\",\"remoteOption\":\"option_ifro72rr\",\"filterable\":false,\"remoteOptions\":[],\"props\":{\"value\":\"value\",\"label\":\"label\"},\"remoteFunc\":\"getOptionData\",\"customClass\":\"\",\"labelWidth\":100,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\",\"onFocus\":\"\",\"onBlur\":\"\"},\"name\":\"下拉选择框\",\"key\":\"ifro72rr\",\"model\":\"level\",\"rules\":[],\"bindTable\":\"y9_form_ziyoubanjian\"}],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":false,\"markCol\":0,\"markRow\":1},\"key\":\"debvsswi\",\"rules\":[]},{\"type\":\"td\",\"list\":[{\"type\":\"component\",\"icon\":\"icon-component\",\"options\":{\"customClass\":\"label_name\",\"labelWidth\":70,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"template\":\"<div style=\\\"text-align: center;\\\">签发人</div>\",\"required\":false,\"remoteFunc\":\"func_1622620316591\",\"remoteOption\":\"option_1622620316591\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"签发人\",\"key\":\"1622620316591\",\"model\":\"\",\"rules\":[]}],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\"},\"key\":\"p1yc1nuu\",\"rules\":[]},{\"type\":\"td\",\"list\":[{\"type\":\"input\",\"icon\":\"icon-input\",\"options\":{\"width\":\"100%\",\"defaultValue\":\"\",\"required\":false,\"requiredMessage\":\"\",\"dataType\":\"number\",\"dataTypeCheck\":false,\"dataTypeMessage\":\"\",\"pattern\":\"\",\"patternCheck\":false,\"patternMessage\":\"\",\"placeholder\":\"\",\"customClass\":\"\",\"disabled\":false,\"labelWidth\":100,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"showPassword\":false,\"remoteFunc\":\"func_1622518488297\",\"remoteOption\":\"option_1622518488297\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"单行文本\",\"key\":\"1622518488297\",\"model\":\"signAndIssue\",\"rules\":[],\"bindTable\":\"y9_form_ziyoubanjian\"}],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\"},\"key\":\"xybg4l94\",\"rules\":[]},{\"type\":\"td\",\"list\":[{\"type\":\"component\",\"icon\":\"icon-component\",\"options\":{\"customClass\":\"label_name\",\"labelWidth\":70,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"template\":\"<div style=\\\"text-align: center;\\\">签发日期</div>\",\"required\":false,\"remoteFunc\":\"func_1622620389681\",\"remoteOption\":\"option_1622620389681\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"签发日期\",\"key\":\"1622620389681\",\"model\":\"\",\"rules\":[]}],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\"},\"key\":\"kjr0eaz7\",\"rules\":[]},{\"type\":\"td\",\"list\":[{\"type\":\"date\",\"icon\":\"icon-date\",\"options\":{\"defaultValue\":\"\",\"fieldPermission\":\"\",\"bindDatabase\":true,\"readonly\":false,\"disabled\":false,\"editable\":true,\"clearable\":true,\"placeholder\":\"\",\"startPlaceholder\":\"\",\"endPlaceholder\":\"\",\"type\":\"date\",\"format\":\"YYYY-MM-DD\",\"timestamp\":false,\"required\":false,\"requiredMessage\":\"\",\"validatorCheck\":false,\"validator\":\"\",\"width\":\"\",\"customClass\":\"\",\"labelWidth\":100,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"remoteFunc\":\"func_za06mp2r\",\"remoteOption\":\"option_za06mp2r\",\"tableColumn\":false,\"hideLabel\":true,\"subform\":false},\"events\":{\"onChange\":\"\",\"onFocus\":\"\",\"onBlur\":\"\"},\"name\":\"日期选择器\",\"key\":\"za06mp2r\",\"model\":\"dateOfIssue\",\"rules\":[],\"bindTable\":\"y9_form_ziyoubanjian\"}],\"options\":{\"customClass\":\"td_right_border\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\"},\"key\":\"ewgt6p2c\",\"rules\":[]}]},{\"columns\":[{\"type\":\"td\",\"list\":[{\"type\":\"component\",\"icon\":\"icon-component\",\"options\":{\"customClass\":\"label_name\",\"labelWidth\":70,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"template\":\"<div style=\\\"text-align: center;\\\">备注</div>\",\"required\":false,\"remoteFunc\":\"func_1622620639888\",\"remoteOption\":\"option_1622620639888\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"备注\",\"key\":\"1622620639888\",\"model\":\"\",\"rules\":[]}],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\"},\"key\":\"w4gwxmjc\",\"rules\":[]},{\"type\":\"td\",\"list\":[{\"type\":\"textarea\",\"icon\":\"icon-diy-com-textarea\",\"options\":{\"width\":\"\",\"defaultValue\":\"\",\"fieldPermission\":\"\",\"readonly\":false,\"required\":false,\"requiredMessage\":\"\",\"disabled\":false,\"pattern\":\"\",\"patternMessage\":\"\",\"validatorCheck\":false,\"validator\":\"\",\"placeholder\":\"\",\"customClass\":\"\",\"labelWidth\":100,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"clearable\":false,\"maxlength\":\"500\",\"showWordLimit\":false,\"rows\":5,\"autosize\":true,\"customProps\":{},\"remoteFunc\":\"func_ee3f6vyf\",\"remoteOption\":\"option_ee3f6vyf\",\"tableColumn\":false,\"hideLabel\":true,\"subform\":false},\"events\":{\"onChange\":\"\",\"onFocus\":\"\",\"onBlur\":\"\"},\"name\":\"多行文本\",\"key\":\"ee3f6vyf\",\"model\":\"remarks\",\"rules\":[],\"bindTable\":\"y9_form_ziyoubanjian\"}],\"options\":{\"customClass\":\"td_right_border\",\"colspan\":5,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"markCol\":0,\"markRow\":0},\"key\":\"m02pov8b\",\"rules\":[]},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":1,\"markRow\":0},\"key\":\"j6l0143b\"},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":2,\"markRow\":0},\"key\":\"yn1q0gxb\"},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":3,\"markRow\":0},\"key\":\"xxgfdlr3\",\"rules\":[]},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":4,\"markRow\":0},\"key\":\"tdf8j37g\"}]},{\"columns\":[{\"type\":\"td\",\"list\":[{\"type\":\"component\",\"icon\":\"icon-component\",\"options\":{\"customClass\":\"label_name\",\"labelWidth\":70,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"template\":\"<div style=\\\"text-align: center;\\\">意见指示</div>\",\"required\":false,\"remoteFunc\":\"func_1622620641650\",\"remoteOption\":\"option_1622620641650\",\"tableColumn\":false,\"hideLabel\":true},\"events\":{\"onChange\":\"\"},\"name\":\"意见指示\",\"key\":\"1622620641650\",\"model\":\"\",\"rules\":[]}],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\"},\"key\":\"3qfxn08o\",\"rules\":[]},{\"type\":\"td\",\"list\":[{\"name\":\"个人意见\",\"el\":\"custom-opinion\",\"options\":{\"defaultValue\":{},\"customClass\":\"\",\"labelWidth\":0,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"required\":false,\"minHeight\":\"\",\"pattern\":\"\",\"remoteFunc\":\"func_ndx7o69w\",\"remoteOption\":\"option_ndx7o69w\",\"tableColumn\":false,\"hideLabel\":true,\"subform\":false},\"type\":\"custom\",\"icon\":\"icon-extend\",\"key\":\"ndx7o69w\",\"model\":\"custom_opinion@personalComment\",\"rules\":[]}],\"options\":{\"customClass\":\"td_right_border\",\"colspan\":5,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"markCol\":0,\"markRow\":0},\"key\":\"vyp91xlq\",\"rules\":[]},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":1,\"markRow\":0},\"key\":\"pmuquuh6\"},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":2,\"markRow\":0},\"key\":\"x8neojtr\"},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":3,\"markRow\":0},\"key\":\"me4c2fth\"},{\"type\":\"td\",\"list\":[],\"options\":{\"customClass\":\"\",\"colspan\":1,\"rowspan\":1,\"align\":\"left\",\"valign\":\"top\",\"width\":\"\",\"height\":\"\",\"invisible\":true,\"markCol\":4,\"markRow\":0},\"key\":\"h3s6cgvv\"}]}],\"name\":\"表格布局\",\"key\":\"1622518382735\",\"model\":\"report_1622518382735\",\"rules\":[]},{\"name\":\"附件列表\",\"el\":\"custom-file\",\"icon\":\"el-icon-document\",\"options\":{\"defaultValue\":{},\"customClass\":\"\",\"labelWidth\":0,\"isLabelWidth\":false,\"hidden\":false,\"dataBind\":true,\"required\":false,\"pattern\":\"\",\"remoteFunc\":\"func_1623396951612\",\"remoteOption\":\"option_1623396951612\",\"tableColumn\":false,\"hideLabel\":true},\"type\":\"custom\",\"key\":\"1623396951612\",\"model\":\"custom_file\",\"rules\":[]}],\"config\":{\"ui\":\"element\",\"size\":\"small\",\"width\":\"750px\",\"layout\":\"horizontal\",\"labelCol\":3,\"hideLabel\":false,\"labelWidth\":100,\"customClass\":\"form\",\"labelPosition\":\"right\",\"hideErrorMessage\":false,\"styleSheets\":\"\\n.formheader{\\n  width:750px;\\n  margin: auto;\\n}\\n.formheader .el-form-item__content{\\n  text-align: center;\\n  font-size: 38px;\\n  margin: 50px 0 30px 0;\\n  letter-spacing: 5px;\\n  color:red;\\n} \\n\\n.form .el-form-item__content{\\n  line-height: 40px;\\n}\\n.form{\\n  background-color:#fff;\\n  margin: auto;\\n  box-shadow: 0px 2px 14px 0px rgba(0,0,0,0.15);\\n  padding-bottom: 30px;\\n}\\n.form .el-textarea__inner{\\n  resize: none;\\n  border:none;\\n  box-shadow: none;\\n  padding:0;\\n  color: #000;\\n  min-height: 40px !important;\\n}\\n.form .el-date-editor.el-input, .el-date-editor.el-input__inner{\\n  width:100%;\\n  height: 35px;\\n  line-height: 35px;\\n}\\n.form .el-input__wrapper{\\n  border:none;\\n  box-shadow: none;\\n}\\n\\n.form .el-input__inner{\\n  padding:0;\\n  width:100%;\\n  color: #000;\\n}\\n.form .el-input--prefix .el-input__inner{\\n  padding-left: 0px;\\n}\\n.form .el-input--suffix .el-input__inner{\\n  padding-right:0;\\n}\\n.form .el-input--prefix .el-input__inner{\\n  padding-left: 30px;\\n}\\n.form .el-input--small .el-input__inner {\\n  height: 35px;\\n  line-height: 35px;\\n}\\n.label_name .el-form-item__content{\\n  text-align: center;\\n  padding:0;\\n  color:red;\\n  font-weight: bold;\\n  line-height: 40px;\\n  width:100%;\\n  margin-left: 0;\\n}\\n.table{\\n  margin: auto;\\n  border-left: 0px;\\n}\\n.table .td_right_border{\\n  border-right: 0px;\\n}\\n.td_right_border{\\n  border-right: 0px;\\n}\\n.form .el-input.is-disabled .el-input__inner {\\n  background-color:#fff;\\n  color: #000;\\n  -webkit-text-fill-color: #000;\\n}\\n.form .el-select{\\n  --el-select-disabled-border: none;\\n}\\n\\n\\n.form .el-input.is-disabled .el-input__wrapper {\\n  background-color: #fff;\\n  box-shadow: none;\\n}\\n\\n.form .el-textarea.is-disabled .el-textarea__inner{\\n   background-color:#fff;\\n   color: #000;\\n}\\n\\n\\n.form .el-divider--horizontal {\\n    display: block;\\n    height: 1px;\\n    width: 690px;\\n    margin: 30px auto 20px;\\n}\\n.form .el-divider__text{\\n    font-weight: 500;\\n    font-size: 20px;\\n    line-height: 20px;\\n}\\n.form .el-form-item--small.el-form-item{\\n  margin-bottom: 0px;\\n}\\n.form .el-form-item--small .el-form-item__error {\\n  padding-top: 0px;\\n  margin-top: -8px;\\n}\\n\",\"dataSource\":[{\"key\":\"yow40f7g\",\"name\":\"初始化数据\",\"url\":\"https://vue.youshengyun.com:8443/flowableUI/vue/y9form/getInitData\",\"method\":\"GET\",\"auto\":false,\"params\":{},\"headers\":{},\"responseFunc\":\"let initdata = res.data;\\nlet data = {};\\ndata.number = initdata.number;\\ndata.wordSize = initdata.zihao;\\ndata.department = initdata.deptName;\\ndata.creater = initdata.userName;\\ndata.createDate = initdata.createDate;\\ndata.contact = initdata.mobile;\\nthis.setData(data);\\nreturn res;\",\"requestFunc\":\"return config;\"}],\"eventScript\":[{\"key\":\"mounted\",\"name\":\"mounted\",\"func\":\"\"}],\"initDataUrl\":\"\"}}");
            y9FormRepository.save(y9Form);
            List<Y9FormField> list = new ArrayList<>();
            Y9FormField newField = new Y9FormField();
            newField.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldCnName("主键").setFieldName("guid")
                .setFieldType("varchar(50)").setFormId(Y9_FORM_ID).setTableId(Y9_TABLE_ID).setTableName(Y9_TABLE_NAME);
            list.add(newField);
            newField = new Y9FormField();
            newField.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldCnName("文件编号").setFieldName("number")
                .setFieldType("varchar(50)").setFormId(Y9_FORM_ID).setTableId(Y9_TABLE_ID).setTableName(Y9_TABLE_NAME);
            list.add(newField);
            newField = new Y9FormField();
            newField.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldCnName("类型").setFieldName("type")
                .setFieldType("varchar(20)").setFormId(Y9_FORM_ID).setTableId(Y9_TABLE_ID).setTableName(Y9_TABLE_NAME);
            list.add(newField);
            newField = new Y9FormField();
            newField.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldCnName("字号").setFieldName("wordSize")
                .setFieldType("varchar(20)").setFormId(Y9_FORM_ID).setTableId(Y9_TABLE_ID).setTableName(Y9_TABLE_NAME);
            list.add(newField);
            newField = new Y9FormField();
            newField.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldCnName("标题").setFieldName("title")
                .setFieldType("varchar(500)").setFormId(Y9_FORM_ID).setTableId(Y9_TABLE_ID).setTableName(Y9_TABLE_NAME);
            list.add(newField);
            newField = new Y9FormField();
            newField.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldCnName("创建部门").setFieldName("department")
                .setFieldType("varchar(50)").setFormId(Y9_FORM_ID).setTableId(Y9_TABLE_ID).setTableName(Y9_TABLE_NAME);
            list.add(newField);
            newField = new Y9FormField();
            newField.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldCnName("创建人").setFieldName("creater")
                .setFieldType("varchar(50)").setFormId(Y9_FORM_ID).setTableId(Y9_TABLE_ID).setTableName(Y9_TABLE_NAME);
            list.add(newField);
            newField = new Y9FormField();
            newField.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldCnName("创建时间").setFieldName("createDate")
                .setFieldType("varchar(50)").setFormId(Y9_FORM_ID).setTableId(Y9_TABLE_ID).setTableName(Y9_TABLE_NAME);
            list.add(newField);
            newField = new Y9FormField();
            newField.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldCnName("联系方式").setFieldName("contact")
                .setFieldType("varchar(20)").setFormId(Y9_FORM_ID).setTableId(Y9_TABLE_ID).setTableName(Y9_TABLE_NAME);
            list.add(newField);
            newField = new Y9FormField();
            newField.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldCnName("主题词").setFieldName("motive")
                .setFieldType("varchar(2000)").setFormId(Y9_FORM_ID).setTableId(Y9_TABLE_ID)
                .setTableName(Y9_TABLE_NAME);
            list.add(newField);
            newField = new Y9FormField();
            newField.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldCnName("发送对象").setFieldName("send")
                .setFieldType("varchar(500)").setFormId(Y9_FORM_ID).setTableId(Y9_TABLE_ID).setTableName(Y9_TABLE_NAME);
            list.add(newField);
            newField = new Y9FormField();
            newField.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldCnName("紧急程度").setFieldName("level")
                .setFieldType("varchar(20)").setFormId(Y9_FORM_ID).setTableId(Y9_TABLE_ID).setTableName(Y9_TABLE_NAME);
            list.add(newField);
            newField = new Y9FormField();
            newField.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldCnName("签发人").setFieldName("signAndIssue")
                .setFieldType("varchar(50)").setFormId(Y9_FORM_ID).setTableId(Y9_TABLE_ID).setTableName(Y9_TABLE_NAME);
            list.add(newField);
            newField = new Y9FormField();
            newField.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldCnName("签发日期").setFieldName("dateOfIssue")
                .setFieldType("varchar(50)").setFormId(Y9_FORM_ID).setTableId(Y9_TABLE_ID).setTableName(Y9_TABLE_NAME);
            list.add(newField);
            newField = new Y9FormField();
            newField.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldCnName("备注").setFieldName("remarks")
                .setFieldType("varchar(500)").setFormId(Y9_FORM_ID).setTableId(Y9_TABLE_ID).setTableName(Y9_TABLE_NAME);
            list.add(newField);
            newField = new Y9FormField();
            newField.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldCnName("文件概要").setFieldName("outline")
                .setFieldType("varchar(2000)").setFormId(Y9_FORM_ID).setTableId(Y9_TABLE_ID)
                .setTableName(Y9_TABLE_NAME);
            list.add(newField);
            y9FormFieldRepository.saveAll(list);
        }
    }

    private void createY9Table() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Y9Table y9Table = y9TableRepository.findById(Y9_TABLE_ID).orElse(null);
        if (null == y9Table) {
            y9Table = new Y9Table();
            y9Table.setId(Y9_TABLE_ID);
            y9Table.setCreateTime(sdf.format(new Date()));
            y9Table.setSystemCnName(SYSTEMCNNAME);
            y9Table.setSystemName(SYSTEMNAME);
            y9Table.setTableName(Y9_TABLE_NAME);
            y9Table.setTableType(1);
            y9Table.setTableCnName(Y9_TABLE_CNNAME);
            y9TableRepository.save(y9Table);
            List<DbColumn> dbcs = new ArrayList<>();
            List<Y9TableField> list = new ArrayList<>();
            Y9TableField field = new Y9TableField();
            int i = 0;
            field.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldName("guid").setFieldCnName("主键")
                .setFieldType("varchar(50)").setFieldLength(50).setIsMayNull(0).setIsSystemField(1).setDisplayOrder(i++)
                .setState(1).setTableId(Y9_TABLE_ID).setTableName(Y9_TABLE_NAME);
            list.add(field);
            dbcs.add(tableField2DbColumn(field));
            field = new Y9TableField();
            field.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldName("number").setFieldCnName("文件编号")
                .setFieldType("varchar(50)").setFieldLength(50).setDisplayOrder(i++).setState(1).setTableId(Y9_TABLE_ID)
                .setTableName(Y9_TABLE_NAME);
            list.add(field);
            dbcs.add(tableField2DbColumn(field));
            field = new Y9TableField();
            field.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldName("type").setFieldCnName("类型")
                .setFieldType("varchar(20)").setFieldLength(20).setDisplayOrder(i++).setState(1).setTableId(Y9_TABLE_ID)
                .setTableName(Y9_TABLE_NAME);
            list.add(field);
            dbcs.add(tableField2DbColumn(field));
            field = new Y9TableField();
            field.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldName("wordSize").setFieldCnName("字号")
                .setFieldType("varchar(20)").setFieldLength(20).setDisplayOrder(i++).setState(1).setTableId(Y9_TABLE_ID)
                .setTableName(Y9_TABLE_NAME);
            list.add(field);
            dbcs.add(tableField2DbColumn(field));
            field = new Y9TableField();
            field.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldName("title").setFieldCnName("标题")
                .setFieldType("varchar(500)").setFieldLength(500).setDisplayOrder(i++).setState(1)
                .setTableId(Y9_TABLE_ID).setTableName(Y9_TABLE_NAME);
            list.add(field);
            dbcs.add(tableField2DbColumn(field));
            field = new Y9TableField();
            field.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldName("department").setFieldCnName("创建部门")
                .setFieldType("varchar(50)").setFieldLength(50).setDisplayOrder(i++).setState(1).setTableId(Y9_TABLE_ID)
                .setTableName(Y9_TABLE_NAME);
            list.add(field);
            dbcs.add(tableField2DbColumn(field));
            field = new Y9TableField();
            field.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldName("creater").setFieldCnName("创建人")
                .setFieldType("varchar(50)").setFieldLength(50).setDisplayOrder(i++).setState(1).setTableId(Y9_TABLE_ID)
                .setTableName(Y9_TABLE_NAME);
            list.add(field);
            dbcs.add(tableField2DbColumn(field));
            field = new Y9TableField();
            field.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldName("createDate").setFieldCnName("创建时间")
                .setFieldType("varchar(50)").setFieldLength(50).setDisplayOrder(i++).setState(1).setTableId(Y9_TABLE_ID)
                .setTableName(Y9_TABLE_NAME);
            list.add(field);
            dbcs.add(tableField2DbColumn(field));
            field = new Y9TableField();
            field.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldName("contact").setFieldCnName("联系方式")
                .setFieldType("varchar(20)").setFieldLength(20).setDisplayOrder(i++).setState(1).setTableId(Y9_TABLE_ID)
                .setTableName(Y9_TABLE_NAME);
            list.add(field);
            dbcs.add(tableField2DbColumn(field));
            field = new Y9TableField();
            field.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldName("motive").setFieldCnName("主题词")
                .setFieldType("varchar(2000)").setFieldLength(2000).setDisplayOrder(i++).setState(1)
                .setTableId(Y9_TABLE_ID).setTableName(Y9_TABLE_NAME);
            list.add(field);
            dbcs.add(tableField2DbColumn(field));
            field = new Y9TableField();
            field.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldName("send").setFieldCnName("发送对象")
                .setFieldType("varchar(500)").setFieldLength(500).setDisplayOrder(i++).setState(1)
                .setTableId(Y9_TABLE_ID).setTableName(Y9_TABLE_NAME);
            list.add(field);
            dbcs.add(tableField2DbColumn(field));
            field = new Y9TableField();
            field.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldName("level").setFieldCnName("紧急程度")
                .setFieldType("varchar(50)").setFieldLength(50).setDisplayOrder(i++).setState(1).setTableId(Y9_TABLE_ID)
                .setTableName(Y9_TABLE_NAME);
            list.add(field);
            dbcs.add(tableField2DbColumn(field));
            field = new Y9TableField();
            field.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldName("signAndIssue").setFieldCnName("签发人")
                .setFieldType("varchar(50)").setFieldLength(50).setDisplayOrder(i++).setState(1).setTableId(Y9_TABLE_ID)
                .setTableName(Y9_TABLE_NAME);
            list.add(field);
            dbcs.add(tableField2DbColumn(field));
            field = new Y9TableField();
            field.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldName("dateOfIssue").setFieldCnName("签发日期")
                .setFieldType("varchar(50)").setFieldLength(50).setDisplayOrder(i++).setState(1).setTableId(Y9_TABLE_ID)
                .setTableName(Y9_TABLE_NAME);
            list.add(field);
            dbcs.add(tableField2DbColumn(field));
            field = new Y9TableField();
            field.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldName("remarks").setFieldCnName("备注")
                .setFieldType("varchar(500)").setFieldLength(500).setDisplayOrder(i++).setState(1)
                .setTableId(Y9_TABLE_ID).setTableName(Y9_TABLE_NAME);
            list.add(field);
            dbcs.add(tableField2DbColumn(field));
            field = new Y9TableField();
            field.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE)).setFieldName("outline").setFieldCnName("文件概要")
                .setFieldType("varchar(2000)").setFieldLength(2000).setDisplayOrder(i).setState(1)
                .setTableId(Y9_TABLE_ID).setTableName(Y9_TABLE_NAME);
            list.add(field);
            dbcs.add(tableField2DbColumn(field));
            y9TableFieldRepository.saveAll(list);
            tableManagerService.buildTable(y9Table, dbcs);
        }
    }

    private void createYearTable() {
        Date date = new Date();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy");
        String year = sdf1.format(date);
        String year0 = String.valueOf((Integer.parseInt(year) + 1));
        syncYearTableService.syncYearTable(year);
        syncYearTableService.syncYearTable(year0);
    }

    /**
     * 初始化办件数据
     *
     * @param tenantId 租户id
     */
    public void init(String tenantId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        createYearTable();// 创建年度表结构
        createItem();// 创建事项
        createOpinionFrame();// 创建个人意见框
        createDynamicRole();// 创建动态角色

        createY9Table();// 创建业务表
        createY9Form();// 创建表单
        createPrintForm();// 创建打印表单

        // 获取流程定义id
        String sql = "SELECT ID_ FROM act_re_procdef WHERE KEY_ = '" + PROCESSDEFINITIONKEY
            + "' AND VERSION_ = (SELECT MAX(VERSION_) FROM act_re_procdef WHERE KEY_ = '" + PROCESSDEFINITIONKEY + "')";
        List<Map<String, Object>> qlist = jdbcTemplate4Tenant.queryForList(sql);
        String processDefinitionId = qlist.get(0).get("ID_").toString();

        createItemForm(processDefinitionId);// 事项表单绑定
        createItemPermission(processDefinitionId);// 事项权限配置
        createItemOpinionFrame(processDefinitionId);// 事项意见框绑定
        createItemPrintTemplate();// 事项打印表单绑定
        createItemViewConf();// 事项视图配置
    }

    private DbColumn tableField2DbColumn(Y9TableField y9TableField) {
        DbColumn dbColumn = new DbColumn();
        dbColumn.setColumnName(y9TableField.getFieldName());
        dbColumn.setIsPrimaryKey(y9TableField.getIsSystemField());
        dbColumn.setPrimaryKey(y9TableField.getIsSystemField() == 1);
        dbColumn.setNullable(y9TableField.getIsMayNull() == 1);
        dbColumn.setTypeName(y9TableField.getFieldType());
        dbColumn.setDataLength(y9TableField.getFieldLength());
        dbColumn.setComment(y9TableField.getFieldCnName());
        dbColumn.setColumnNameOld(y9TableField.getOldFieldName());
        dbColumn.setDataPrecision(0);
        dbColumn.setDataScale(0);
        dbColumn.setDataType(0);
        dbColumn.setIsNull(y9TableField.getIsMayNull());
        dbColumn.setTableName(y9TableField.getTableName());
        return dbColumn;
    }

}
