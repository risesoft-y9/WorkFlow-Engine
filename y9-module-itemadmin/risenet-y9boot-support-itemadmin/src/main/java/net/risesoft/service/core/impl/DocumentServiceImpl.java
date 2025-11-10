package net.risesoft.service.core.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.Y9FlowableHolder;
import net.risesoft.api.platform.org.CustomGroupApi;
import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.platform.permission.cache.PositionRoleApi;
import net.risesoft.api.platform.resource.AppApi;
import net.risesoft.api.processadmin.ConditionParserApi;
import net.risesoft.api.processadmin.HistoricActivityApi;
import net.risesoft.api.processadmin.HistoricProcessApi;
import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.ProcessTodoApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.api.processadmin.RuntimeApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.consts.ItemConsts;
import net.risesoft.consts.UtilConsts;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.entity.ActRuDetail;
import net.risesoft.entity.DynamicRole;
import net.risesoft.entity.ErrorLog;
import net.risesoft.entity.Item;
import net.risesoft.entity.ItemPermission;
import net.risesoft.entity.ItemTaskConf;
import net.risesoft.entity.ProcessParam;
import net.risesoft.entity.SignDeptDetail;
import net.risesoft.entity.TaskVariable;
import net.risesoft.entity.button.ItemButtonBind;
import net.risesoft.entity.documentword.Y9Word;
import net.risesoft.entity.form.Y9Form;
import net.risesoft.entity.form.Y9FormItemBind;
import net.risesoft.entity.form.Y9FormItemMobileBind;
import net.risesoft.entity.template.ItemPrintTemplateBind;
import net.risesoft.enums.DynamicRoleKindsEnum;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.enums.ItemButtonTypeEnum;
import net.risesoft.enums.ItemPermissionEnum;
import net.risesoft.enums.SignDeptDetailStatusEnum;
import net.risesoft.enums.SignStatusEnum;
import net.risesoft.enums.TodoTaskEventActionEnum;
import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.enums.platform.permission.AuthorityEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.DocUserChoiseModel;
import net.risesoft.model.itemadmin.ErrorLogModel;
import net.risesoft.model.itemadmin.ItemButtonModel;
import net.risesoft.model.itemadmin.ItemFormModel;
import net.risesoft.model.itemadmin.ItemListModel;
import net.risesoft.model.itemadmin.OpenDataModel;
import net.risesoft.model.itemadmin.SignDeptDetailModel;
import net.risesoft.model.itemadmin.SignTaskConfigModel;
import net.risesoft.model.itemadmin.StartProcessResultModel;
import net.risesoft.model.itemadmin.TodoTaskEventModel;
import net.risesoft.model.itemadmin.core.DocumentDetailModel;
import net.risesoft.model.platform.org.CustomGroup;
import net.risesoft.model.platform.org.CustomGroupMember;
import net.risesoft.model.platform.org.Department;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.platform.org.Person;
import net.risesoft.model.platform.org.Position;
import net.risesoft.model.platform.resource.App;
import net.risesoft.model.platform.resource.Resource;
import net.risesoft.model.processadmin.FlowElementModel;
import net.risesoft.model.processadmin.HistoricActivityInstanceModel;
import net.risesoft.model.processadmin.HistoricProcessInstanceModel;
import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.model.processadmin.ProcessInstanceModel;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.nosql.elastic.entity.OfficeDoneInfo;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.pojo.Y9Result;
import net.risesoft.query.platform.CustomGroupMemberQuery;
import net.risesoft.repository.form.Y9FormRepository;
import net.risesoft.repository.jpa.ItemRepository;
import net.risesoft.repository.jpa.ItemTaskConfRepository;
import net.risesoft.repository.jpa.TaskVariableRepository;
import net.risesoft.repository.template.ItemPrintTemplateBindRepository;
import net.risesoft.service.AssociatedFileService;
import net.risesoft.service.AsyncHandleService;
import net.risesoft.service.ButtonService;
import net.risesoft.service.DynamicRoleMemberService;
import net.risesoft.service.DynamicRoleService;
import net.risesoft.service.ErrorLogService;
import net.risesoft.service.OfficeDoneInfoService;
import net.risesoft.service.OfficeFollowService;
import net.risesoft.service.Process4SearchService;
import net.risesoft.service.RoleService;
import net.risesoft.service.SignDeptDetailService;
import net.risesoft.service.SpeakInfoService;
import net.risesoft.service.attachment.AttachmentService;
import net.risesoft.service.config.ItemButtonBindService;
import net.risesoft.service.config.ItemPermissionService;
import net.risesoft.service.config.ItemStartNodeRoleService;
import net.risesoft.service.config.ItemTaskConfService;
import net.risesoft.service.config.Y9FormItemBindService;
import net.risesoft.service.core.ActRuDetailService;
import net.risesoft.service.core.DocumentService;
import net.risesoft.service.core.ItemService;
import net.risesoft.service.core.ProcessParamService;
import net.risesoft.service.event.Y9TodoUpdateEvent;
import net.risesoft.service.form.Y9FormService;
import net.risesoft.service.impl.ActivitiOptServiceImpl;
import net.risesoft.service.word.Y9WordService;
import net.risesoft.util.CommonOpt;
import net.risesoft.util.ItemButton;
import net.risesoft.util.ListUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9.util.Y9Util;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private static final String TRUE_STR_KEY = "true:";
    private static final String ITEMID_URL_KEY = "itemId=";
    private final ActivitiOptServiceImpl activitiOptService;
    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final ItemTaskConfService taskConfService;
    private final ItemPermissionService itemPermissionService;
    private final Y9FormItemBindService y9FormItemBindService;
    private final ItemButtonBindService buttonItemBindService;
    private final TaskApi taskApi;
    private final CustomGroupApi customGroupApi;
    private final ProcessDefinitionApi processDefinitionApi;
    private final VariableApi variableApi;
    private final OrgUnitApi orgUnitApi;
    private final RepositoryApi repositoryApi;
    private final PositionApi positionApi;
    private final PositionRoleApi positionRoleApi;
    private final DepartmentApi departmentApi;
    private final HistoricProcessApi historicProcessApi;
    private final HistoricTaskApi historictaskApi;
    private final RuntimeApi runtimeApi;
    private final ProcessParamService processParamService;
    private final ProcessTodoApi processTodoApi;
    private final ItemPrintTemplateBindRepository itemPrintTemplateBindRepository;
    private final OfficeDoneInfoService officeDoneInfoService;
    private final TaskVariableRepository taskVariableRepository;
    private final AsyncHandleService asyncHandleService;
    private final Y9FormRepository y9FormRepository;
    private final Process4SearchService process4SearchService;
    private final ErrorLogService errorLogService;
    private final ItemStartNodeRoleService itemStartNodeRoleService;
    private final ItemTaskConfRepository taskConfRepository;
    private final DynamicRoleMemberService dynamicRoleMemberService;
    private final DynamicRoleService dynamicRoleService;
    private final ConditionParserApi conditionParserApi;
    private final Y9FormService y9FormService;
    private final RoleService roleService;
    private final HistoricActivityApi historicActivityApi;
    private final ActRuDetailService actRuDetailService;
    private final SignDeptDetailService signDeptDetailService;
    private final AppApi appApi;
    private final ButtonService buttonService;
    private final AttachmentService attachmentService;
    private final Y9WordService y9WordService;
    private final AssociatedFileService associatedFileService;
    private final SpeakInfoService speakInfoService;
    private final OfficeFollowService officeFollowService;

    @Override
    public DocumentDetailModel add(String itemId) {
        String userId = Y9FlowableHolder.getOrgUnitId(), tenantId = Y9LoginUserHolder.getTenantId();
        DocumentDetailModel model = new DocumentDetailModel();
        Item item = itemService.findById(itemId);
        String processDefinitionKey = item.getWorkflowGuid();
        model.setTenantId(tenantId);
        model.setItemId(itemId);
        model.setProcessDefinitionKey(processDefinitionKey);
        ProcessDefinitionModel pdModel =
            repositoryApi.getLatestProcessDefinitionByKey(tenantId, processDefinitionKey).getData();
        String processDefinitionId = pdModel.getId();
        String taskDefKey = itemStartNodeRoleService.getStartTaskDefKey(itemId);
        model.setProcessDefinitionId(processDefinitionId);
        model.setTaskDefKey(taskDefKey);
        model.setActivitiUser(userId);
        model.setCurrentUser(Y9FlowableHolder.getOrgUnit().getName());
        model.setItembox(ItemBoxTypeEnum.ADD.getValue());
        model.setProcessSerialNumber(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        model.setProcessInstanceId("");
        model.setTaskId("");

        model = genDocumentModel(itemId, processDefinitionKey, processDefinitionId, taskDefKey, model);
        model = menuControl4Add(model);
        return model;
    }

    @Override
    public OpenDataModel add4Old(String itemId, boolean mobile) {
        String userId = Y9FlowableHolder.getOrgUnitId(), tenantId = Y9LoginUserHolder.getTenantId();
        OpenDataModel model = new OpenDataModel();
        Item item = itemService.findById(itemId);
        String processDefinitionKey = item.getWorkflowGuid();
        model.setTenantId(tenantId);
        model.setItemId(itemId);
        model.setProcessDefinitionKey(processDefinitionKey);
        ProcessDefinitionModel pdModel =
            repositoryApi.getLatestProcessDefinitionByKey(tenantId, processDefinitionKey).getData();
        String processDefinitionId = pdModel.getId();
        String taskDefKey = itemStartNodeRoleService.getStartTaskDefKey(itemId);
        model.setProcessDefinitionId(processDefinitionId);
        model.setTaskDefKey(taskDefKey);
        model.setActivitiUser(userId);
        model.setCurrentUser(Y9FlowableHolder.getOrgUnit().getName());
        model.setItembox(ItemBoxTypeEnum.ADD.getValue());
        model.setProcessSerialNumber(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        model.setProcessInstanceId("");
        model.setTaskId("");

        model = genDocumentModel(itemId, processDefinitionKey, processDefinitionId, taskDefKey, mobile, model);
        model = menuControl(model);
        return model;
    }

    /*
     * 向userIds中添加内容
     *
     * @param userIds
     * @param userId 人员Guid
     * @return
     */
    private String addUserId(String userIds, String userId) {
        /*
         * 由于串行、并行的时候人员存在顺序的，所以写在这里，保证人员顺序
         */
        if (StringUtils.isNotBlank(userIds)) {
            if (!userIds.contains(userId)) {
                userIds = userIds + SysVariables.SEMICOLON + userId;
            }
        } else {
            userIds = userId;
        }
        return userIds;
    }

    @Override
    public DocumentDetailModel addWithStartTaskDefKey(String itemId, String startTaskDefKey, boolean mobile) {
        String userId = Y9FlowableHolder.getOrgUnitId(), tenantId = Y9LoginUserHolder.getTenantId();
        DocumentDetailModel model = new DocumentDetailModel();
        Item item = itemService.findById(itemId);
        model.setItemId(itemId);
        model.setProcessDefinitionKey(item.getWorkflowGuid());
        String processDefinitionKey = item.getWorkflowGuid();
        ProcessDefinitionModel pdModel =
            repositoryApi.getLatestProcessDefinitionByKey(tenantId, processDefinitionKey).getData();
        String processDefinitionId = pdModel.getId();
        model.setItembox(ItemBoxTypeEnum.ADD.getValue());
        model.setProcessDefinitionId(processDefinitionId);
        model.setTaskDefKey(startTaskDefKey);
        model.setActivitiUser(userId);
        model.setStartor(userId);
        model.setCurrentUser(Y9FlowableHolder.getOrgUnit().getName());
        model.setProcessSerialNumber(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        model.setProcessInstanceId("");
        model.setMobile(mobile);

        this.genTabModel(itemId, processDefinitionKey, processDefinitionId, startTaskDefKey, false, model);
        this.menuControl4Add(model);
        return model;
    }

    @Override
    public void complete(String taskId) throws Exception {
        String tenantId = Y9LoginUserHolder.getTenantId();
        TaskModel task = taskApi.findById(tenantId, taskId).getData();
        String processInstanceId = task.getProcessInstanceId();
        /*
         * 1办结流程
         */
        runtimeApi.complete(tenantId, Y9FlowableHolder.getOrgUnitId(), processInstanceId, taskId);
    }

    @Override
    public void completeSub(String taskId, List<String> userList) throws Exception {
        String tenantId = Y9LoginUserHolder.getTenantId();
        /*
         * 1办结流程
         */
        runtimeApi.completeSub(tenantId, Y9FlowableHolder.getOrgUnitId(), taskId, userList);
    }

    @Override
    public DocUserChoiseModel docUserChoise(String itemId, String processDefinitionKey, String processDefinitionId,
        String taskId, String taskDefKey, String processInstanceId) {
        DocUserChoiseModel model = new DocUserChoiseModel();
        String tenantId = Y9LoginUserHolder.getTenantId();

        String multiInstance = processDefinitionApi.getNodeType(tenantId, processDefinitionId, taskDefKey).getData();
        Map<String, Object> tabMap =
            itemPermissionService.getTabMap(itemId, processDefinitionId, taskDefKey, processInstanceId, taskId);

        model.setExistDepartment((Boolean)tabMap.get("existDepartment"));
        model.setExistPosition((Boolean)tabMap.get("existPosition"));
        model.setSignTask(false);
        if (SysVariables.COMMON.equals(multiInstance)) {
            ItemTaskConf itemTaskConf = taskConfRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId,
                processDefinitionId, taskDefKey);
            if (itemTaskConf != null && itemTaskConf.getSignTask()) {
                model.setSignTask(true);
            }
        }
        Y9Page<CustomGroup> pageList =
            customGroupApi.pageCustomGroupByPersonId(tenantId, Y9LoginUserHolder.getPersonId(), new Y9PageQuery(1, 1));
        model.setExistCustomGroup(pageList != null && pageList.getTotal() > 0);
        model.setMultiInstance(multiInstance);
        model.setProcessDefinitionId(processDefinitionId);
        model.setTenantId(tenantId);
        model.setItemId(itemId);
        model.setRouteToTask(taskDefKey);
        boolean isSubProcess = processDefinitionApi.isSubProcess(tenantId, processDefinitionId, taskDefKey).getData();
        model.setType(isSubProcess ? "SubProcess" : "UserTask");
        model.setSponsorStatus(false);
        if (SysVariables.PARALLEL.equals(multiInstance)) {// 并行节点，查询是否具有主协办状态
            boolean sponsorStatus = taskConfService.getSponserStatus(itemId, processDefinitionId, taskDefKey);
            model.setSponsorStatus(sponsorStatus);
        }
        return model;
    }

    @Override
    public OpenDataModel edit(String itembox, String taskId, String processInstanceId, String itemId, boolean mobile) {
        OpenDataModel model = new OpenDataModel();
        String tenantId = Y9LoginUserHolder.getTenantId();
        // 处理监控办理状态
        if (ItemBoxTypeEnum.MONITOR_DOING.getValue().equals(itembox)) {
            itembox = ItemBoxTypeEnum.DOING.getValue();
        }
        model.setMeeting(false);
        ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
        String startor = processParam.getStartor();
        // 根据不同的任务箱类型处理数据
        ProcessInstanceData processInstanceData =
            handleProcessInstanceData(itembox, taskId, processInstanceId, itemId, processParam, tenantId, model);
        // 设置基础模型数据
        model.setTitle(processParam.getTitle());
        model.setStartor(startor);
        model.setItembox(itembox);
        model.setCurrentUser(Y9FlowableHolder.getOrgUnit().getName());
        model.setProcessSerialNumber(processInstanceData.processSerialNumber);
        model.setProcessDefinitionKey(processInstanceData.processDefinitionKey);
        model.setProcessDefinitionId(processInstanceData.processDefinitionId);
        model.setProcessInstanceId(processInstanceId);
        model.setTaskDefKey(processInstanceData.taskDefinitionKey);
        model.setTaskId(processInstanceData.taskId);
        model.setActivitiUser(processInstanceData.activitiUser);
        model.setItemId(processInstanceData.itemId);
        // 生成文档模型和菜单控制
        model = genDocumentModel(processInstanceData.itemId, processInstanceData.processDefinitionKey,
            processInstanceData.processDefinitionId, processInstanceData.taskDefinitionKey, mobile, model);
        model = menuControl(model);
        return model;
    }

    /**
     * 处理流程实例数据
     */
    private ProcessInstanceData handleProcessInstanceData(String itembox, String taskId, String processInstanceId,
        String itemId, ProcessParam processParam, String tenantId, OpenDataModel model) {

        ProcessInstanceData data = new ProcessInstanceData();
        data.processSerialNumber = "";
        data.processDefinitionId = "";
        data.taskDefinitionKey = "";
        data.processDefinitionKey = "";
        data.activitiUser = "";
        data.taskId = taskId;
        data.itemId = itemId;

        if (itembox.equalsIgnoreCase(ItemBoxTypeEnum.TODO.getValue())) {
            handleTodoBox(data, taskId, processParam, tenantId, model);
        } else if (itembox.equalsIgnoreCase(ItemBoxTypeEnum.DOING.getValue())
            || itembox.equalsIgnoreCase(ItemBoxTypeEnum.DONE.getValue())) {
            handleDoingDoneBox(data, processInstanceId, taskId, processParam, tenantId, model);
        }

        return data;
    }

    /**
     * 处理待办箱数据
     */
    private void handleTodoBox(ProcessInstanceData data, String taskId, ProcessParam processParam, String tenantId,
        OpenDataModel model) {
        TaskModel task = taskApi.findById(tenantId, taskId).getData();
        data.processInstanceId = task.getProcessInstanceId();
        data.processSerialNumber = processParam.getProcessSerialNumber();
        data.processDefinitionId = task.getProcessDefinitionId();
        data.taskDefinitionKey = task.getTaskDefinitionKey();
        data.processDefinitionKey = data.processDefinitionId.split(SysVariables.COLON)[0];
        data.activitiUser = task.getAssignee();
        data.taskId = taskId;

        if (StringUtils.isBlank(data.itemId)) {
            data.itemId = processParam.getItemId();
        }

        // 设为已读
        if (StringUtils.isBlank(task.getFormKey())) {
            task.setFormKey("0");
            taskApi.saveTask(tenantId, task);
            Y9Context.publishEvent(new Y9TodoUpdateEvent<>(new TodoTaskEventModel(TodoTaskEventActionEnum.SET_NEW_TODO,
                tenantId, data.processInstanceId, taskId, "0")));
        }

        // 获取第一节点任务key
        setStartTaskDefKey(model, tenantId, data.processDefinitionId);

        OfficeDoneInfo officeDoneInfo = officeDoneInfoService.findByProcessInstanceId(data.processInstanceId);
        model.setMeeting(officeDoneInfo != null && "1".equals(officeDoneInfo.getMeeting()));
    }

    /**
     * 处理在办箱和办结箱数据
     */
    private void handleDoingDoneBox(ProcessInstanceData data, String processInstanceId, String taskId,
        ProcessParam processParam, String tenantId, OpenDataModel model) {

        HistoricProcessInstanceModel hpi = historicProcessApi.getById(tenantId, processInstanceId).getData();
        OfficeDoneInfo officeDoneInfo = officeDoneInfoService.findByProcessInstanceId(processInstanceId);

        if (hpi == null) {
            if (officeDoneInfo == null) {
                String year = getYear(processParam.getCreateTime());
                hpi = historicProcessApi.getByIdAndYear(tenantId, processInstanceId, year).getData();
                data.processDefinitionId = hpi.getProcessDefinitionId();
                data.processDefinitionKey = data.processDefinitionId.split(SysVariables.COLON)[0];
            } else {
                data.processDefinitionId = officeDoneInfo.getProcessDefinitionId();
                data.processDefinitionKey = officeDoneInfo.getProcessDefinitionKey();
            }
        } else {
            data.processDefinitionId = hpi.getProcessDefinitionId();
            data.processDefinitionKey = data.processDefinitionId.split(SysVariables.COLON)[0];
        }

        model.setMeeting(officeDoneInfo != null && "1".equals(officeDoneInfo.getMeeting()));
        data.processSerialNumber = processParam.getProcessSerialNumber();

        if (StringUtils.isNotEmpty(taskId)) {
            if (taskId.contains(SysVariables.COMMA)) {
                data.taskId = taskId.split(SysVariables.COMMA)[0];
            }
            TaskModel taskTemp = taskApi.findById(tenantId, data.taskId).getData();
            data.taskDefinitionKey = taskTemp.getTaskDefinitionKey();
        }
    }

    private String getYear(Date createTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(createTime);
        return String.valueOf(calendar.get(Calendar.YEAR));
    }

    /**
     * 设置起始任务定义key
     */
    private void setStartTaskDefKey(OpenDataModel model, String tenantId, String processDefinitionId) {
        String startTaskDefKey = "";
        String startNode =
            processDefinitionApi.getStartNodeKeyByProcessDefinitionId(tenantId, processDefinitionId).getData();
        List<TargetModel> nodeList =
            processDefinitionApi.getTargetNodes(tenantId, processDefinitionId, startNode).getData();

        for (TargetModel map : nodeList) {
            startTaskDefKey = Y9Util.genCustomStr(startTaskDefKey, map.getTaskDefKey());
        }
        model.setStartTaskDefKey(startTaskDefKey);
    }

    @Override
    public DocumentDetailModel editDraft(String processSerialNumber, String itemId, boolean mobile) {
        String tenantId = Y9LoginUserHolder.getTenantId(), orgUnitId = Y9FlowableHolder.getOrgUnitId();
        DocumentDetailModel model = new DocumentDetailModel();
        Item item = itemService.findById(itemId);
        model.setItemId(itemId);
        model.setProcessDefinitionKey(item.getWorkflowGuid());
        String processDefinitionKey = item.getWorkflowGuid();
        String processDefinitionId =
            repositoryApi.getLatestProcessDefinitionByKey(tenantId, processDefinitionKey).getData().getId();
        String taskDefKey = itemStartNodeRoleService.getStartTaskDefKey(itemId);
        ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
        model.setCustomItem(processParam.getCustomItem());
        model.setProcessDefinitionId(processDefinitionId);
        model.setProcessInstanceId("");
        model.setProcessSerialNumber(processSerialNumber);
        model.setTaskDefKey(taskDefKey);
        model.setTitle(processParam.getTitle());
        model.setActivitiUser(orgUnitId);
        model.setCurrentUser(Y9FlowableHolder.getOrgUnit().getName());
        model.setItembox(ItemBoxTypeEnum.DRAFT.getValue());

        this.setNum(model);
        this.genDocumentModel(itemId, processDefinitionKey, processDefinitionId, taskDefKey, model);
        this.menuControl4Draft(model);
        return model;
    }

    @Override
    public DocumentDetailModel editChaoSong(String id, String processInstanceId, boolean mobile, String itembox) {
        DocumentDetailModel model = new DocumentDetailModel();
        String tenantId = Y9LoginUserHolder.getTenantId();
        String taskId = "";
        List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
        if (!taskList.isEmpty()) {
            taskId = taskList.get(0).getId();
            TaskModel task = taskApi.findById(tenantId, taskId).getData();
            processInstanceId = task.getProcessInstanceId();
        }
        String processSerialNumber, processDefinitionId, taskDefinitionKey = "", processDefinitionKey,
            activitiUser = "", startor;
        ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
        HistoricProcessInstanceModel hpi = historicProcessApi.getById(tenantId, processInstanceId).getData();
        if (hpi == null) {
            OfficeDoneInfo officeDoneInfo = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
            if (officeDoneInfo == null) {
                String year = getYear(processParam.getCreateTime());
                hpi = historicProcessApi.getByIdAndYear(tenantId, processInstanceId, year).getData();
                processDefinitionId = hpi.getProcessDefinitionId();
                processDefinitionKey = processDefinitionId.split(SysVariables.COLON)[0];
            } else {
                processDefinitionId = officeDoneInfo.getProcessDefinitionId();
                processDefinitionKey = officeDoneInfo.getProcessDefinitionKey();
            }
        } else {
            processDefinitionId = hpi.getProcessDefinitionId();
            processDefinitionKey = processDefinitionId.split(SysVariables.COLON)[0];
        }
        startor = processParam.getStartor();
        processSerialNumber = processParam.getProcessSerialNumber();
        if (StringUtils.isNotEmpty(taskId)) {
            if (taskId.contains(SysVariables.COMMA)) {
                taskId = taskId.split(SysVariables.COMMA)[0];
            }
            TaskModel taskTemp = taskApi.findById(tenantId, taskId).getData();
            taskDefinitionKey = taskTemp.getTaskDefinitionKey();
        }
        OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, Y9FlowableHolder.getOrgUnitId()).getData();
        model.setId(id);
        model.setTitle(processParam.getTitle());
        model.setStartor(startor);
        model.setItembox(itembox);
        model.setCurrentUser(orgUnit.getName());
        model.setProcessDefinitionKey(processDefinitionKey);
        model.setProcessSerialNumber(processSerialNumber);
        model.setProcessDefinitionId(processDefinitionId);
        model.setProcessInstanceId(processInstanceId);
        model.setTaskDefKey(taskDefinitionKey);
        model.setTaskId(taskId);
        model.setActivitiUser(activitiUser);
        model.setItemId(processParam.getItemId());
        model.setMobile(mobile);

        this.setNum(model);
        this.genDocumentModel(processParam.getItemId(), processDefinitionKey, processDefinitionId, taskDefinitionKey,
            model);
        this.menuControl4ChaoSong(model);
        return model;
    }

    @Override
    public DocumentDetailModel editDoing(String processInstanceId, String documentId, boolean isAdmin,
        ItemBoxTypeEnum itemBox, boolean mobile) {
        DocumentDetailModel model = new DocumentDetailModel();
        String processSerialNumber, processDefinitionId, taskDefinitionKey = "", processDefinitionKey,
            activitiUser = "", itemId, taskId = "";
        String startor;
        String tenantId = Y9LoginUserHolder.getTenantId();
        model.setMeeting(false);
        model.setDocumentId(documentId);
        ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
        startor = processParam.getStartor();
        HistoricProcessInstanceModel hpi = historicProcessApi.getById(tenantId, processInstanceId).getData();
        OfficeDoneInfo officeDoneInfo = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
        processDefinitionId = hpi.getProcessDefinitionId();
        processDefinitionKey = processDefinitionId.split(SysVariables.COLON)[0];
        assert officeDoneInfo != null;
        processSerialNumber = processParam.getProcessSerialNumber();
        itemId = processParam.getItemId();
        List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
        if (!taskList.isEmpty()) {
            if (processSerialNumber.equals(documentId)) {
                taskId = taskList.get(0).getId();
            } else {
                SignDeptDetail signDeptDetail = signDeptDetailService.findById(documentId);
                Optional<TaskModel> taskModel = taskList.stream()
                    .filter(task -> task.getExecutionId().equals(signDeptDetail.getExecutionId()))
                    .findFirst();
                if (taskModel.isPresent()) {
                    taskId = taskModel.get().getId();
                }
            }
        } else {
            // callActivity
            List<HistoricActivityInstanceModel> haiList =
                historicActivityApi.getByProcessInstanceId(tenantId, processInstanceId).getData();
            HistoricActivityInstanceModel last = haiList.stream().reduce((first, second) -> second).orElse(null);
            assert last != null;
            taskDefinitionKey = last.getActivityId();
        }
        model.setTitle(processParam.getTitle());
        model.setStartor(startor);
        model.setItembox(itemBox.getValue());
        model.setCurrentUser(Y9FlowableHolder.getOrgUnit().getName());
        model.setProcessSerialNumber(processSerialNumber);
        model.setProcessDefinitionKey(processDefinitionKey);
        model.setProcessDefinitionId(processDefinitionId);
        model.setProcessInstanceId(processInstanceId);
        model.setTaskDefKey(taskDefinitionKey);
        model.setTaskId(taskId);
        model.setActivitiUser(activitiUser);
        model.setItemId(itemId);
        model.setMobile(mobile);

        this.setNum(model);
        this.genTabModel(itemId, processDefinitionKey, processDefinitionId, taskDefinitionKey, isAdmin, model);
        if (!isAdmin) {
            this.menuControl4Doing(model);
        } else {
            this.menuControl4DoingAdmin(model);
        }
        return model;
    }

    @Override
    public DocumentDetailModel editDone(String processInstanceId, String documentId, boolean isAdmin,
        ItemBoxTypeEnum itemBox, boolean mobile) {
        DocumentDetailModel model = new DocumentDetailModel();
        String processSerialNumber, processDefinitionId, taskDefinitionKey = "", processDefinitionKey,
            activityUser = "", itemId;
        String starter;
        String tenantId = Y9LoginUserHolder.getTenantId();
        ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
        starter = processParam.getStartor();
        OfficeDoneInfo officeDoneInfo = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
        if (officeDoneInfo == null) {
            String year = getYear(processParam.getCreateTime());
            HistoricProcessInstanceModel hpi =
                historicProcessApi.getByIdAndYear(tenantId, processInstanceId, year).getData();
            processDefinitionId = hpi.getProcessDefinitionId();
            processDefinitionKey = processDefinitionId.split(SysVariables.COLON)[0];
        } else {
            processDefinitionId = officeDoneInfo.getProcessDefinitionId();
            processDefinitionKey = officeDoneInfo.getProcessDefinitionKey();
        }
        processSerialNumber = processParam.getProcessSerialNumber();
        itemId = processParam.getItemId();
        model.setDocumentId(documentId);
        model.setTitle(processParam.getTitle());
        model.setStartor(starter);
        model.setItembox(itemBox.getValue());
        model.setCurrentUser(Y9FlowableHolder.getOrgUnit().getName());
        model.setProcessSerialNumber(processSerialNumber);
        model.setProcessDefinitionKey(processDefinitionKey);
        model.setProcessDefinitionId(processDefinitionId);
        model.setProcessInstanceId(processInstanceId);
        model.setActivitiUser(activityUser);
        model.setItemId(itemId);
        model.setMobile(mobile);

        this.setNum(model);
        this.genTabModel(itemId, processDefinitionKey, processDefinitionId, taskDefinitionKey, isAdmin, model);
        this.menuControl4Done(model);
        return model;
    }

    @Override
    public DocumentDetailModel editRecycle(String processInstanceId, boolean mobile) {
        DocumentDetailModel model = new DocumentDetailModel();
        String processSerialNumber, processDefinitionId, taskDefinitionKey = "", processDefinitionKey,
            activitiUser = "", itemId;
        String startor;
        String tenantId = Y9LoginUserHolder.getTenantId();
        ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
        startor = processParam.getStartor();
        OfficeDoneInfo officeDoneInfo = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
        if (officeDoneInfo == null) {
            String year = getYear(processParam.getCreateTime());
            HistoricProcessInstanceModel hpi =
                historicProcessApi.getByIdAndYear(tenantId, processInstanceId, year).getData();
            processDefinitionId = hpi.getProcessDefinitionId();
            processDefinitionKey = processDefinitionId.split(SysVariables.COLON)[0];
        } else {
            processDefinitionId = officeDoneInfo.getProcessDefinitionId();
            processDefinitionKey = officeDoneInfo.getProcessDefinitionKey();
        }
        processSerialNumber = processParam.getProcessSerialNumber();
        itemId = processParam.getItemId();
        model.setTitle(processParam.getTitle());
        model.setStartor(startor);
        model.setItembox(ItemBoxTypeEnum.RECYCLE.getValue());
        model.setCurrentUser(Y9FlowableHolder.getOrgUnit().getName());
        model.setProcessSerialNumber(processSerialNumber);
        model.setProcessDefinitionKey(processDefinitionKey);
        model.setProcessDefinitionId(processDefinitionId);
        model.setProcessInstanceId(processInstanceId);
        model.setActivitiUser(activitiUser);
        model.setItemId(itemId);
        model.setMobile(mobile);

        this.genTabModel(itemId, processDefinitionKey, processDefinitionId, taskDefinitionKey, mobile, model);
        this.menuControl4Recycle(model);
        return model;
    }

    @Override
    public DocumentDetailModel editTodo(String taskId, boolean mobile) {
        DocumentDetailModel model = new DocumentDetailModel();
        String processSerialNumber, processDefinitionId, taskDefinitionKey, processDefinitionKey, activitiUser, itemId,
            starter;
        String tenantId = Y9LoginUserHolder.getTenantId();
        model.setMeeting(false);
        TaskModel task = taskApi.findById(tenantId, taskId).getData();
        String processInstanceId = task.getProcessInstanceId();
        ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
        starter = processParam.getStartor();
        itemId = processParam.getItemId();
        processSerialNumber = processParam.getProcessSerialNumber();
        processDefinitionId = task.getProcessDefinitionId();
        taskDefinitionKey = task.getTaskDefinitionKey();
        processDefinitionKey = processDefinitionId.split(SysVariables.COLON)[0];
        activitiUser = task.getAssignee();
        /*
         * 设为已读
         */
        if (StringUtils.isBlank(task.getFormKey())) {
            task.setFormKey("0");
            taskApi.saveTask(tenantId, task);
        }
        // 获取第一节点任务key,可能多个,用于非权限表单时，是否是起草节点，用来开启编辑所有表单所有字段的权限
        String startTaskDefKey = "";
        String startNode =
            processDefinitionApi.getStartNodeKeyByProcessDefinitionId(tenantId, processDefinitionId).getData();
        List<TargetModel> nodeList =
            processDefinitionApi.getTargetNodes(tenantId, processDefinitionId, startNode).getData();
        for (TargetModel map : nodeList) {
            startTaskDefKey = Y9Util.genCustomStr(startTaskDefKey, map.getTaskDefKey());
        }
        model.setStartTaskDefKey(startTaskDefKey);
        model.setTitle(processParam.getTitle());
        model.setStartor(starter);
        model.setCurrentUser(Y9FlowableHolder.getOrgUnit().getName());
        model.setProcessSerialNumber(processSerialNumber);
        model.setProcessDefinitionKey(processDefinitionKey);
        model.setProcessDefinitionId(processDefinitionId);
        model.setProcessInstanceId(processInstanceId);
        model.setTaskDefKey(taskDefinitionKey);
        model.setTaskId(taskId);
        model.setActivitiUser(activitiUser);
        model.setItemId(itemId);
        model.setItembox(ItemBoxTypeEnum.TODO.getValue());
        model.setMobile(mobile);

        this.setNum(model);
        this.genTabModel(itemId, processDefinitionKey, processDefinitionId, taskDefinitionKey, false, model);
        this.menuControl4Todo(model);
        return model;
    }

    private void setNum(DocumentDetailModel model) {
        Integer fileNum = attachmentService.fileCounts(model.getProcessSerialNumber());
        Y9Word y9Word = y9WordService.getByProcessSerialNumber(model.getProcessSerialNumber());
        int speakInfoNum =
            speakInfoService.getNotReadCount(Y9LoginUserHolder.getPersonId(), model.getProcessInstanceId());
        int associatedFileNum = associatedFileService.countAssociatedFile(model.getProcessSerialNumber());
        int follow = officeFollowService.countByProcessInstanceId(model.getProcessInstanceId());
        model.setFileNum(fileNum);
        model.setAssociatedFileNum(associatedFileNum);
        model.setDocNum(y9Word != null && y9Word.getId() != null ? 1 : 0);
        model.setFollow(follow > 0);
        model.setSpeakInfoNum(speakInfoNum);
    }

    @Override
    public Y9Result<String> forwarding(String taskId, String sponsorHandle, String userChoice, String routeToTaskId,
        String sponsorGuid) {
        String processInstanceId = "";
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), orgUnitId = Y9FlowableHolder.getOrgUnitId();
            TaskModel task = taskApi.findById(tenantId, taskId).getData();
            processInstanceId = task.getProcessInstanceId();
            ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
            List<String> userList = new ArrayList<>(parseUserChoice(userChoice));
            int num = userList.size();
            // 验证用户列表
            Y9Result<String> validationResult = validateUserList(userList, num);
            if (!validationResult.isSuccess()) {
                return validationResult;
            }
            OrgUnit orgUnit = Y9FlowableHolder.getOrgUnit();
            // 得到要发送节点的multiInstance，PARALLEL表示并行，SEQUENTIAL表示串行
            FlowElementModel flowElementModel =
                processDefinitionApi.getNode(tenantId, task.getProcessDefinitionId(), routeToTaskId).getData();
            Map<String, Object> variables =
                CommonOpt.setVariables(orgUnitId, orgUnit.getName(), routeToTaskId, userList, flowElementModel);
            // 处理并行发送逻辑
            if (shouldHandleParallelAsync(flowElementModel.getMultiInstance(), num)) {
                handleParallelForwarding(tenantId, orgUnit, processInstanceId, processParam, sponsorHandle, sponsorGuid,
                    taskId, flowElementModel, variables, userList, num);
            } else {
                // 处理主办办理逻辑
                if (StringUtils.isNotBlank(sponsorHandle) && UtilConsts.TRUE.equals(sponsorHandle)) {
                    List<TaskModel> taskNextList =
                        taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    if (taskNextList.size() > 10) {
                        // 协办人数超过10人，启用异步处理
                        handleSponsorAsyncForwarding(tenantId, orgUnit, processInstanceId, processParam, sponsorHandle,
                            sponsorGuid, taskId, flowElementModel, variables, userList, num);
                    } else {
                        asyncHandleService.forwarding4Gfg(processInstanceId, processParam, sponsorHandle, sponsorGuid,
                            taskId, flowElementModel, variables, userList);
                    }
                } else {
                    asyncHandleService.forwarding4Gfg(processInstanceId, processParam, sponsorHandle, sponsorGuid,
                        taskId, flowElementModel, variables, userList);
                }
            }
            return Y9Result.success(processInstanceId, "发送成功!");
        } catch (Exception e) {
            LOGGER.error("发送失败！", e);
            handleForwardingError(processInstanceId, taskId, e);
            return Y9Result.failure("发送失败!");
        }
    }

    /**
     * 验证用户列表
     */
    private Y9Result<String> validateUserList(List<String> userList, int num) {
        if (num > 100) {
            return Y9Result.failure("发送人数过多!");
        }
        if (userList.isEmpty()) {
            return Y9Result.failure("未匹配到发送人!");
        }
        return Y9Result.success();
    }

    /**
     * 判断是否需要异步处理并行发送
     */
    private boolean shouldHandleParallelAsync(String multiInstance, int userCount) {
        return SysVariables.PARALLEL.equals(multiInstance) && userCount > 20;
    }

    /**
     * 处理并行转发逻辑
     */
    private void handleParallelForwarding(String tenantId, OrgUnit orgUnit, String processInstanceId,
        ProcessParam processParam, String sponsorHandle, String sponsorGuid, String taskId,
        FlowElementModel flowElementModel, Map<String, Object> variables, List<String> userList, int num) {
        TaskVariable taskVariable = createOrUpdateTaskVariable(taskId, processInstanceId, num);
        taskVariableRepository.save(taskVariable);
        asyncHandleService.forwarding(tenantId, orgUnit, processInstanceId, processParam, sponsorHandle, sponsorGuid,
            taskId, flowElementModel, variables, userList);
    }

    /**
     * 处理主办异步转发逻辑
     */
    private void handleSponsorAsyncForwarding(String tenantId, OrgUnit orgUnit, String processInstanceId,
        ProcessParam processParam, String sponsorHandle, String sponsorGuid, String taskId,
        FlowElementModel flowElementModel, Map<String, Object> variables, List<String> userList, int num) {
        TaskVariable taskVariable = createOrUpdateTaskVariable(taskId, processInstanceId, num);
        taskVariableRepository.save(taskVariable);
        asyncHandleService.forwarding(tenantId, orgUnit, processInstanceId, processParam, sponsorHandle, sponsorGuid,
            taskId, flowElementModel, variables, userList);
    }

    /**
     * 创建或更新任务变量
     */
    private TaskVariable createOrUpdateTaskVariable(String taskId, String processInstanceId, int userCount) {
        TaskVariable taskVariable = taskVariableRepository.findByTaskIdAndKeyName(taskId, ItemConsts.ISFORWARDING_KEY);
        if (taskVariable == null) {
            taskVariable = new TaskVariable();
            taskVariable.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            taskVariable.setProcessInstanceId(processInstanceId);
            taskVariable.setTaskId(taskId);
            taskVariable.setKeyName(ItemConsts.ISFORWARDING_KEY);
        }
        taskVariable.setText(TRUE_STR_KEY + userCount);
        return taskVariable;
    }

    /**
     * 处理转发错误
     */
    private void handleForwardingError(String processInstanceId, String taskId, Exception e) {
        try {
            final Writer result = new StringWriter();
            final PrintWriter print = new PrintWriter(result);
            e.printStackTrace(print);
            String msg = result.toString();
            // 保存任务发送错误日志
            ErrorLog errorLog = new ErrorLog();
            errorLog.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            errorLog.setErrorFlag(ErrorLogModel.ERROR_FLAG_FORWRDING);
            errorLog.setErrorType(ErrorLogModel.ERROR_TASK);
            errorLog.setExtendField("发送少数人失败");
            errorLog.setProcessInstanceId(processInstanceId);
            errorLog.setTaskId(taskId);
            errorLog.setText(msg);
            errorLogService.saveErrorLog(errorLog);
        } catch (Exception e2) {
            LOGGER.error("保存任务发送错误日志失败！", e2);
        }
    }

    @Override
    public OpenDataModel genDocumentModel(String itemId, String processDefinitionKey, String processDefinitionId,
        String taskDefinitionKey, boolean mobile, OpenDataModel model) {
        // Y9表单
        String formIds = "";
        String showOtherFlag = "";
        String formNames = "";
        if (mobile) {
            List<Y9FormItemMobileBind> y9FormTaskBinds = y9FormItemBindService
                .listByItemIdAndProcDefIdAndTaskDefKey4Mobile(itemId, processDefinitionId, taskDefinitionKey);
            model.setFormId("");
            model.setFormName("");
            model.setFormJson("");
            if (!y9FormTaskBinds.isEmpty()) {
                Y9FormItemMobileBind bind = y9FormTaskBinds.get(0);
                model.setFormId(bind.getFormId());
                String formName = bind.getFormName();
                if (formName.contains("(")) {
                    formName = formName.substring(0, formName.indexOf("("));
                }
                model.setFormName(formName);
                Y9Form y9Form = y9FormRepository.findById(bind.getFormId()).orElse(null);
                assert y9Form != null;
                model.setFormJson(y9Form.getFormJson());
            }
            return model;
        }
        List<Y9FormItemBind> y9FormTaskBinds =
            y9FormItemBindService.listByItemIdAndProcDefIdAndTaskDefKey(itemId, processDefinitionId, taskDefinitionKey);
        List<Map<String, String>> list = new ArrayList<>();
        if (!y9FormTaskBinds.isEmpty()) {
            for (Y9FormItemBind bind : y9FormTaskBinds) {
                formIds = Y9Util.genCustomStr(formIds, bind.getFormId());
                String formName = bind.getFormName();
                if (formName.contains("(")) {
                    formName = formName.substring(0, formName.indexOf("("));
                }
                formNames = Y9Util.genCustomStr(formNames, formName);
                Map<String, String> map = new HashMap<>(16);
                map.put("formId", bind.getFormId());
                map.put("formName", formName);
                list.add(map);
            }
            showOtherFlag = y9FormItemBindService.getShowOther(y9FormTaskBinds);
        }
        model.setFormList(list);
        model.setFormIds(formIds);
        model.setFormNames(formNames);
        model.setShowOtherFlag(showOtherFlag);
        // 获取打印表单
        ItemPrintTemplateBind bind = itemPrintTemplateBindRepository.findByItemId(itemId);
        if (bind != null) {
            model.setPrintFormId(bind.getTemplateId());
            model.setPrintFormType(bind.getTemplateType());
        }
        return model;
    }

    @Override
    public DocumentDetailModel genDocumentModel(String itemId, String processDefinitionKey, String processDefinitionId,
        String taskDefinitionKey, DocumentDetailModel model) {
        List<Y9FormItemBind> y9FormTaskBinds =
            y9FormItemBindService.listByItemIdAndProcDefIdAndTaskDefKey(itemId, processDefinitionId, taskDefinitionKey);
        String showOtherFlag = "";
        List<ItemFormModel> list = new ArrayList<>();
        if (!y9FormTaskBinds.isEmpty()) {
            ItemFormModel itemFormModel;
            for (Y9FormItemBind fib : y9FormTaskBinds) {
                itemFormModel = new ItemFormModel();
                String formName = fib.getFormName();
                if (formName.contains("(")) {
                    formName = formName.substring(0, formName.indexOf("("));
                }
                itemFormModel.setFormId(fib.getFormId());
                itemFormModel.setFormName(formName);
                list.add(itemFormModel);
            }
            showOtherFlag = y9FormItemBindService.getShowOther(y9FormTaskBinds);
        }
        model.setFormList(list);
        model.setShowOtherFlag(showOtherFlag);
        // 打印表单
        ItemPrintTemplateBind bind = itemPrintTemplateBindRepository.findByItemId(itemId);
        if (bind != null) {
            model.setPrintFormId(bind.getTemplateId());
            model.setPrintFormType(bind.getTemplateType());
        }
        return model;
    }

    @Override
    public DocumentDetailModel genTabModel(String itemId, String processDefinitionKey, String processDefinitionId,
        String taskDefinitionKey, boolean isAdmin, DocumentDetailModel model) {
        // 处理表单列表
        handleFormList(itemId, processDefinitionId, taskDefinitionKey, model);
        // 处理签注意见状态
        handleSignStatus(model, isAdmin);
        return model;
    }

    /**
     * 处理表单列表
     */
    private void handleFormList(String itemId, String processDefinitionId, String taskDefinitionKey,
        DocumentDetailModel model) {
        String showOtherFlag = "";
        List<ItemFormModel> formList = new ArrayList<>();
        if (model.isMobile()) {
            // 处理移动端表单
            List<Y9FormItemMobileBind> mobileFormBinds = y9FormItemBindService
                .listByItemIdAndProcDefIdAndTaskDefKey4Mobile(itemId, processDefinitionId, taskDefinitionKey);
            formList.addAll(convertMobileFormBinds(mobileFormBinds));
        } else {
            // 处理PC端表单
            List<Y9FormItemBind> formBinds = y9FormItemBindService.listByItemIdAndProcDefIdAndTaskDefKey(itemId,
                processDefinitionId, taskDefinitionKey);
            formList.addAll(convertFormBinds(formBinds));
            showOtherFlag = y9FormItemBindService.getShowOther(formBinds);
        }
        model.setFormList(formList);
        model.setShowOtherFlag(showOtherFlag);
    }

    /**
     * 转换移动端表单绑定为ItemFormModel列表
     */
    private List<ItemFormModel> convertMobileFormBinds(List<Y9FormItemMobileBind> mobileFormBinds) {
        List<ItemFormModel> formList = new ArrayList<>();
        for (Y9FormItemMobileBind fib : mobileFormBinds) {
            ItemFormModel itemFormModel = new ItemFormModel();
            String formName = extractFormName(fib.getFormName());
            itemFormModel.setFormId(fib.getFormId());
            itemFormModel.setFormName(formName);
            // 设置表单JSON数据
            Optional<Y9Form> y9Form = y9FormRepository.findById(fib.getFormId());
            y9Form.ifPresent(form -> itemFormModel.setFormJson(form.getFormJson()));
            formList.add(itemFormModel);
        }
        return formList;
    }

    /**
     * 转换PC端表单绑定为ItemFormModel列表
     */
    private List<ItemFormModel> convertFormBinds(List<Y9FormItemBind> formBinds) {
        List<ItemFormModel> formList = new ArrayList<>();
        for (Y9FormItemBind fib : formBinds) {
            ItemFormModel itemFormModel = new ItemFormModel();
            String formName = extractFormName(fib.getFormName());
            itemFormModel.setFormId(fib.getFormId());
            itemFormModel.setFormName(formName);
            formList.add(itemFormModel);
        }
        return formList;
    }

    /**
     * 提取表单名称（去除括号后的内容）
     */
    private String extractFormName(String formName) {
        return formName.contains("(") ? formName.substring(0, formName.indexOf("(")) : formName;
    }

    /**
     * 处理签注意见状态
     */
    private void handleSignStatus(DocumentDetailModel model, boolean isAdmin) {
        List<SignDeptDetail> signList = signDeptDetailService.findByProcessSerialNumber(model.getProcessSerialNumber());
        Integer signStatus;
        // 根据不同情况设置签注意见状态
        if (model.getItembox().equals(ItemBoxTypeEnum.TODO.getValue())) {
            signStatus = getTodoSignStatus(model);
        } else {
            signStatus = getNonTodoSignStatus(model, signList, isAdmin);
        }
        model.setSignStatus(signStatus);
        // 设置会签意见汇总列表
        setSignDeptDetailList(model, signList);
    }

    /**
     * 获取待办状态下的签注意见状态
     */
    private Integer getTodoSignStatus(DocumentDetailModel model) {
        ActRuDetail todo =
            actRuDetailService.findByTaskIdAndAssignee(model.getTaskId(), Y9FlowableHolder.getOrgUnitId());
        return todo != null && todo.isSub() ? SignStatusEnum.SUB.getValue() : SignStatusEnum.NONE.getValue();
    }

    /**
     * 获取非待办状态下的签注意见状态
     */
    private Integer getNonTodoSignStatus(DocumentDetailModel model, List<SignDeptDetail> signList, boolean isAdmin) {
        // documentId不为空且为流程序列号时，打开的是主办，不显示签注意见纸
        if (StringUtils.isNotBlank(model.getDocumentId())
            && model.getDocumentId().equals(model.getProcessSerialNumber())) {
            return SignStatusEnum.NONE.getValue();
        }
        // 其他情况根据会签列表和用户角色确定状态
        if (!signList.isEmpty()) {
            ActRuDetail doing = actRuDetailService.findByProcessSerialNumberAndAssigneeAndStatusEquals1(
                model.getProcessSerialNumber(), Y9FlowableHolder.getOrgUnitId());
            if (isAdmin) {
                return SignStatusEnum.ADMIN.getValue();
            } else if (doing == null) {
                return SignStatusEnum.SUB.getValue();
            } else {
                return doing.isSub() ? SignStatusEnum.SUB.getValue() : SignStatusEnum.MAIN.getValue();
            }
        }
        return SignStatusEnum.NOTSTART.getValue();
    }

    /**
     * 设置会签意见汇总列表
     */
    private void setSignDeptDetailList(DocumentDetailModel model, List<SignDeptDetail> signList) {
        List<SignDeptDetailModel> modelList = new ArrayList<>();
        signList.stream().filter(s -> s.getStatus().equals(SignDeptDetailStatusEnum.DONE)).forEach(sdd -> {
            SignDeptDetailModel ssdModel = new SignDeptDetailModel();
            Y9BeanUtil.copyProperties(sdd, ssdModel);
            modelList.add(ssdModel);
        });
        model.setSignDeptDetailList(modelList);
    }

    private void getAllPosition(List<Position> list, String deptId) {
        List<Department> deptList = departmentApi.listByParentId(Y9LoginUserHolder.getTenantId(), deptId).getData();
        List<Position> list0 = positionApi.listByParentId(Y9LoginUserHolder.getTenantId(), deptId).getData();
        if (!list0.isEmpty()) {
            list.addAll(list0);
        }
        for (Department dept : deptList) {
            getAllPosition(list, dept.getId());
        }
    }

    @Override
    public List<ItemButtonModel> getButtons(String taskId) {
        DocumentDetailModel model = new DocumentDetailModel();
        String itemId, processDefinitionId, taskDefinitionKey;
        String tenantId = Y9LoginUserHolder.getTenantId();
        TaskModel task = taskApi.findById(tenantId, taskId).getData();
        String processInstanceId = task.getProcessInstanceId();
        ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
        itemId = processParam.getItemId();
        processDefinitionId = task.getProcessDefinitionId();
        taskDefinitionKey = task.getTaskDefinitionKey();
        model.setItemId(itemId);
        model.setProcessInstanceId(processInstanceId);
        model.setProcessDefinitionId(processDefinitionId);
        model.setTaskDefKey(taskDefinitionKey);
        model.setTaskId(taskId);
        model.setProcessSerialNumber(processParam.getProcessSerialNumber());
        this.menuControl4Todo(model);
        return model.getButtonList();
    }

    @Override
    public String getFirstItem() {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), userId = Y9FlowableHolder.getOrgUnitId();
            List<App> list = appApi.listAccessAppForPosition(tenantId, userId, AuthorityEnum.BROWSE).getData();
            String url;
            for (Resource r : list) {
                url = r.getUrl();
                if (StringUtils.isBlank(url)) {
                    continue;
                }
                if (!url.contains(ITEMID_URL_KEY)) {
                    continue;
                }
                return url.split(ITEMID_URL_KEY)[1];
            }
        } catch (Exception e) {
            LOGGER.error("获取第一个任务失败！", e);
        }
        return "";
    }

    @Override
    public String getFormIdByItemId(String itemId, String processDefinitionKey) {
        String formIds = "";
        String processDefinitionId =
            repositoryApi.getLatestProcessDefinitionByKey(Y9LoginUserHolder.getTenantId(), processDefinitionKey)
                .getData()
                .getId();
        List<Y9FormItemBind> binds =
            y9FormItemBindService.listByItemIdAndProcDefIdAndTaskDefKey(itemId, processDefinitionId, "");
        if (!binds.isEmpty()) {
            for (Y9FormItemBind bind : binds) {
                formIds = Y9Util.genCustomStr(formIds, bind.getFormId());
            }
        }
        return formIds;
    }

    public List<OrgUnit> getUserChoice(String itemId, String processDefinitionId, String taskDefinitionKey,
        String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<ItemPermission> permissions = itemPermissionService
            .listByItemIdAndProcessDefinitionIdAndTaskDefKeyExtra(itemId, processDefinitionId, taskDefinitionKey);
        List<OrgUnit> orgUnitList = new ArrayList<>();
        for (ItemPermission permission : permissions) {
            switch (permission.getRoleType()) {
                case DEPARTMENT:
                case POSITION:
                case USER:
                    handleOrgUnitPermission(orgUnitList, tenantId, permission);
                    break;
                case ROLE:
                    handleRolePermission(orgUnitList, tenantId, permission);
                    break;
                case ROLE_DYNAMIC:
                    handleDynamicRolePermission(orgUnitList, permission, processInstanceId);
                    break;
                default:
                    // 处理未知权限类型，可选择记录日志或忽略
                    break;
            }
        }
        return orgUnitList;
    }

    /**
     * 处理组织单元权限（部门、岗位、用户）
     */
    private void handleOrgUnitPermission(List<OrgUnit> orgUnitList, String tenantId, ItemPermission permission) {
        OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, permission.getRoleId()).getData();
        if (orgUnit != null) {
            orgUnitList.add(orgUnit);
        }
    }

    /**
     * 处理角色权限
     */
    private void handleRolePermission(List<OrgUnit> orgUnitList, String tenantId, ItemPermission permission) {
        List<Position> positionList = positionRoleApi.listPositionsByRoleId(tenantId, permission.getRoleId()).getData();
        orgUnitList.addAll(positionList);
    }

    /**
     * 处理动态角色权限
     */
    private void handleDynamicRolePermission(List<OrgUnit> orgUnitList, ItemPermission permission,
        String processInstanceId) {
        DynamicRole dynamicRole = dynamicRoleService.getById(permission.getRoleId());
        if (dynamicRole == null) {
            return;
        }
        List<OrgUnit> positionList = new ArrayList<>();
        List<OrgUnit> deptList = new ArrayList<>();
        if (dynamicRole.getKinds() == null || dynamicRole.getKinds().equals(DynamicRoleKindsEnum.NONE)) {
            // 动态角色种类为【无】或null时，针对岗位或部门
            List<OrgUnit> orgUnits =
                dynamicRoleMemberService.listByDynamicRoleIdAndProcessInstanceId(dynamicRole, processInstanceId);
            separateOrgUnitsByType(orgUnits, positionList, deptList);
        } else {
            // 动态角色种类为【角色】或【部门配置分类】时，针对岗位
            List<Position> positions = dynamicRoleMemberService
                .listPositionByDynamicRoleIdAndProcessInstanceId(dynamicRole, processInstanceId);
            positionList.addAll(positions);
        }
        orgUnitList.addAll(positionList);
        orgUnitList.addAll(deptList);
    }

    /**
     * 根据组织单元类型分离岗位和部门
     */
    private void separateOrgUnitsByType(List<OrgUnit> orgUnits, List<OrgUnit> positionList, List<OrgUnit> deptList) {
        for (OrgUnit orgUnit : orgUnits) {
            if (orgUnit.getOrgType().equals(OrgTypeEnum.POSITION)) {
                positionList.add(orgUnit);
            } else if (orgUnit.getOrgType().equals(OrgTypeEnum.DEPARTMENT)) {
                deptList.add(orgUnit);
            }
        }
    }

    @Override
    public List<ItemListModel> listItems() {
        List<ItemListModel> listMap = new ArrayList<>();
        try {
            String userId = Y9FlowableHolder.getOrgUnitId();
            String tenantId = Y9LoginUserHolder.getTenantId();
            List<App> list = appApi.listAccessAppForPosition(tenantId, userId, AuthorityEnum.BROWSE).getData();
            ItemListModel model;
            String url;
            long todoCount;
            for (Resource r : list) {
                model = new ItemListModel();
                url = r.getUrl();
                if (StringUtils.isBlank(url)) {
                    continue;
                }
                if (!url.contains(ITEMID_URL_KEY)) {
                    continue;
                }
                String itemId = url.split(ITEMID_URL_KEY)[1];
                model.setId(r.getId());
                model.setUrl(itemId);
                model.setItemId(itemId);
                model.setAppIcon("");
                model.setTodoCount(0);
                Item item = itemRepository.findById(itemId).orElse(null);
                model.setName(r.getName());
                model.setItemName(r.getName());
                if (item != null && item.getId() != null) {
                    model.setName(item.getName());
                    model.setItemName(item.getName());
                    todoCount = processTodoApi
                        .getTodoCountByUserIdAndProcessDefinitionKey(tenantId, userId, item.getWorkflowGuid())
                        .getData();
                    model.setTodoCount((int)todoCount);
                    model.setAppIcon(StringUtils.isBlank(item.getIconData()) ? "" : item.getIconData());
                    model.setProcessDefinitionKey(item.getWorkflowGuid());
                    listMap.add(model);
                }
            }
        } catch (Exception e) {
            LOGGER.error("获取任务列表失败！", e);
        }
        return listMap;
    }

    @Override
    public List<ItemListModel> listMyItems() {
        List<ItemListModel> listMap = new ArrayList<>();
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), userId = Y9FlowableHolder.getOrgUnitId();
            List<App> list = appApi.listAccessAppForPosition(tenantId, userId, AuthorityEnum.BROWSE).getData();
            ItemListModel model;
            String url;
            for (Resource r : list) {
                model = new ItemListModel();
                url = r.getUrl();
                if (StringUtils.isBlank(url)) {
                    continue;
                }
                if (!url.contains(ITEMID_URL_KEY)) {
                    continue;
                }
                String itemId = url.split(ITEMID_URL_KEY)[1];
                model.setId(r.getId());
                model.setItemId(itemId);
                model.setAppIcon("");
                Item item = itemRepository.findById(itemId).orElse(null);
                model.setName(r.getName());
                model.setItemName(r.getName());
                if (item != null && item.getId() != null) {
                    model.setName(item.getName());
                    model.setItemName(item.getName());
                    model.setAppIcon(StringUtils.isBlank(item.getIconData()) ? "" : item.getIconData());
                    model.setProcessDefinitionKey(item.getWorkflowGuid());
                    listMap.add(model);
                }
            }
        } catch (Exception e) {
            LOGGER.error("获取我的任务列表失败！", e);
        }
        return listMap;
    }

    @Override
    public OpenDataModel menuControl(OpenDataModel model) {
        String itemId = model.getItemId(), processDefinitionId = model.getProcessDefinitionId(),
            taskDefKey = model.getTaskDefKey(), taskId = model.getTaskId(), itemBox = model.getItembox();
        String tenantId = Y9LoginUserHolder.getTenantId(), orgUnitId = Y9FlowableHolder.getOrgUnitId();
        Map<String, Object> buttonMap = buttonService.showButton(itemId, taskId, itemBox);
        String[] buttonIds = (String[])buttonMap.get("buttonIds");
        String[] buttonNames = (String[])buttonMap.get("buttonNames");
        String sponsorHandle = (String)buttonMap.get("sponsorHandle");
        int[] buttonOrders = (int[])buttonMap.get("buttonOrders");
        boolean[] isButtonShow = (boolean[])buttonMap.get("isButtonShow");
        ButtonControlData controlData = new ButtonControlData();
        // 生成按钮数组
        processButtons(controlData, buttonOrders, isButtonShow, itemId, processDefinitionId, taskDefKey, buttonNames,
            buttonIds, tenantId, orgUnitId);
        // 设置模型数据
        setModelData(model, controlData, buttonMap, sponsorHandle);
        return model;
    }

    /**
     * 处理按钮生成逻辑
     */
    private void processButtons(ButtonControlData controlData, int[] buttonOrders, boolean[] isButtonShow,
        String itemId, String processDefinitionId, String taskDefKey, String[] buttonNames, String[] buttonIds,
        String tenantId, String orgUnitId) {
        for (int i = buttonOrders.length - 1; i >= 0; i--) {
            int buttonIndex = buttonOrders[i] - 1;
            // 处理保存按钮（索引为0）
            if (buttonIndex == 0 && isButtonShow[0]) {
                handleSaveButton(controlData, itemId, processDefinitionId, taskDefKey, tenantId, orgUnitId);
            }
            // 处理发送按钮（索引为1）
            else if (buttonIndex == 1 && isButtonShow[1] && StringUtils.isNotBlank(taskDefKey)) {
                handleSendButton(controlData, itemId, processDefinitionId, taskDefKey, buttonNames, buttonIds,
                    buttonIndex, tenantId, orgUnitId);
            }
            // 处理重定向按钮（索引为15）
            else if (buttonIndex == 15 && isButtonShow[15]) {
                handleRepositionButton(controlData, processDefinitionId, tenantId);
            }
            // 处理其他按钮
            else if (buttonIndex != 1 && isButtonShow[buttonIndex]) {
                handleOtherButton(controlData, buttonNames, buttonIds, buttonIndex);
            }
        }
    }

    /**
     * 处理其他按钮
     */
    private void handleOtherButton(ButtonControlData controlData, String[] buttonNames, String[] buttonIds,
        int buttonIndex) {
        Map<String, Object> buttonInfo = new HashMap<>(16);
        buttonInfo.put(ItemConsts.MENUNAME_KEY, buttonNames[buttonIndex]);
        buttonInfo.put(ItemConsts.MENU_KEY, buttonIds[buttonIndex]);
        controlData.menuName = Y9Util.genCustomStr(controlData.menuName, buttonNames[buttonIndex]);
        controlData.menuKey = Y9Util.genCustomStr(controlData.menuKey, buttonIds[buttonIndex]);
        controlData.menuMap.add(buttonInfo);
    }

    /**
     * 设置模型数据
     */
    private void setModelData(OpenDataModel model, ButtonControlData controlData, Map<String, Object> buttonMap,
        String sponsorHandle) {
        model.setSendMap(controlData.sendMap);
        model.setMenuMap(controlData.menuMap);
        model.setSendName(controlData.sendName);
        model.setSendKey(controlData.sendKey);
        model.setMenuName(controlData.menuName);
        model.setMenuKey(controlData.menuKey);
        model.setRepositionMap(controlData.repositionMap);
        model.setTaskDefNameJson(controlData.taskDefNameJson);
        model.setSponsorHandle(sponsorHandle);
        model.setLastPerson4RefuseClaim(buttonMap.get("isLastPerson4RefuseClaim") != null
            ? (Boolean)buttonMap.get("isLastPerson4RefuseClaim") : false);
        model.setMultiInstance(buttonMap.get("multiInstance") != null ? (String)buttonMap.get("multiInstance") : "");
        model.setNextNode(buttonMap.get("nextNode") != null ? (Boolean)buttonMap.get("nextNode") : false);
    }

    /**
     * 处理保存按钮逻辑
     */
    private void handleSaveButton(ButtonControlData controlData, String itemId, String processDefinitionId,
        String taskDefKey, String tenantId, String orgUnitId) {
        List<ItemButtonBind> bibList =
            buttonItemBindService.listContainRoleId(itemId, ItemButtonTypeEnum.COMMON, processDefinitionId, taskDefKey);
        for (ItemButtonBind bind : bibList) {
            String buttonName = bind.getButtonName();
            String buttonCustomId = bind.getButtonCustomId();
            // 跳过发送按钮
            if ("发送".equals(buttonName)) {
                continue;
            }
            // 检查用户权限并添加按钮
            if (hasButtonPermission(bind.getRoleIds(), tenantId, orgUnitId)) {
                addButtonToControlData(controlData, buttonName, buttonCustomId);
            }
        }
    }

    /**
     * 处理发送按钮逻辑
     */
    private void handleSendButton(ButtonControlData controlData, String itemId, String processDefinitionId,
        String taskDefKey, String[] buttonNames, String[] buttonIds, int buttonIndex, String tenantId,
        String orgUnitId) {
        // 检查是否有自定义"发送"按钮
        boolean haveSendButton =
            checkCustomSendButton(controlData, itemId, processDefinitionId, taskDefKey, tenantId, orgUnitId);
        if (!haveSendButton) {
            // 添加默认发送按钮
            Map<String, Object> defaultSendButton = new HashMap<>(16);
            defaultSendButton.put(ItemConsts.MENUNAME_KEY, buttonNames[buttonIndex]);
            defaultSendButton.put(ItemConsts.MENU_KEY, buttonIds[buttonIndex]);
            controlData.menuName = Y9Util.genCustomStr(controlData.menuName, buttonNames[buttonIndex]);
            controlData.menuKey = Y9Util.genCustomStr(controlData.menuKey, buttonIds[buttonIndex]);
            controlData.menuMap.add(defaultSendButton);
            // 添加发送下面的路由
            addSendRoutes(controlData, processDefinitionId, taskDefKey, tenantId);
            // 添加自定义按钮到发送
            addCustomSendButtons(controlData, itemId, processDefinitionId, taskDefKey, tenantId, orgUnitId);
        }
    }

    /**
     * 检查是否存在自定义发送按钮
     */
    private boolean checkCustomSendButton(ButtonControlData controlData, String itemId, String processDefinitionId,
        String taskDefKey, String tenantId, String orgUnitId) {
        List<ItemButtonBind> bibList =
            buttonItemBindService.listContainRoleId(itemId, ItemButtonTypeEnum.COMMON, processDefinitionId, taskDefKey);
        for (ItemButtonBind bib : bibList) {
            if ("发送".equals(bib.getButtonName())) {
                return handleSendButtonPermission(controlData, bib, tenantId, orgUnitId);
            }
        }
        return false;
    }

    /**
     * 处理发送按钮权限检查
     */
    private boolean handleSendButtonPermission(ButtonControlData controlData, ItemButtonBind bib, String tenantId,
        String orgUnitId) {
        List<String> roleIds = bib.getRoleIds();
        // 如果没有角色限制，直接添加按钮
        if (roleIds.isEmpty()) {
            addButtonToControlData(controlData, bib.getButtonName(), bib.getButtonCustomId());
            return true;
        }
        // 检查用户是否有权限访问该按钮
        if (hasButtonPermission(roleIds, tenantId, orgUnitId)) {
            addButtonToControlData(controlData, bib.getButtonName(), bib.getButtonCustomId());
            return true;
        }
        return false;
    }

    /**
     * 将按钮添加到控制数据中
     */
    private void addButtonToControlData(ButtonControlData controlData, String buttonName, String buttonCustomId) {
        Map<String, Object> buttonInfo = new HashMap<>(16);
        buttonInfo.put(ItemConsts.MENUNAME_KEY, buttonName);
        buttonInfo.put(ItemConsts.MENU_KEY, buttonCustomId);
        controlData.menuName = Y9Util.genCustomStr(controlData.menuName, buttonName);
        controlData.menuKey = Y9Util.genCustomStr(controlData.menuKey, buttonCustomId);
        controlData.menuMap.add(buttonInfo);
    }

    /**
     * 添加发送路由
     */
    private void addSendRoutes(ButtonControlData controlData, String processDefinitionId, String taskDefKey,
        String tenantId) {
        List<TargetModel> routeToTasks =
            processDefinitionApi.getTargetNodes(tenantId, processDefinitionId, taskDefKey).getData();
        for (TargetModel target : routeToTasks) {
            // 退回、路由网关不显示在发送下面
            if (!"退回".equals(target.getTaskDefName())
                && !ItemConsts.EXCLUSIVE_GATEWAY_KEY.equals(target.getTaskDefName())) {
                controlData.sendName = Y9Util.genCustomStr(controlData.sendName, target.getTaskDefName());
                controlData.sendKey = Y9Util.genCustomStr(controlData.sendKey, target.getTaskDefKey());
                Map<String, Object> routeInfo = new HashMap<>(16);
                routeInfo.put(ItemConsts.SENDNAME_KEY, target.getTaskDefName());
                routeInfo.put(ItemConsts.SENDKEY_KEY, target.getTaskDefKey());
                controlData.sendMap.add(routeInfo);
            }
        }
    }

    /**
     * 添加自定义发送按钮
     */
    private void addCustomSendButtons(ButtonControlData controlData, String itemId, String processDefinitionId,
        String taskDefKey, String tenantId, String orgUnitId) {
        List<ItemButtonBind> bibList =
            buttonItemBindService.listContainRoleId(itemId, ItemButtonTypeEnum.SEND, processDefinitionId, taskDefKey);
        for (ItemButtonBind bind : bibList) {
            List<String> roleIds = bind.getRoleIds();
            String buttonName = bind.getButtonName(), buttonCustomId = bind.getButtonCustomId();
            if (roleIds.isEmpty()) {
                Map<String, Object> buttonInfo = new HashMap<>(16);
                controlData.sendName = Y9Util.genCustomStr(controlData.sendName, buttonName);
                controlData.sendKey = Y9Util.genCustomStr(controlData.sendKey, buttonCustomId);
                buttonInfo.put(ItemConsts.SENDNAME_KEY, buttonName);
                buttonInfo.put(ItemConsts.SENDKEY_KEY, buttonCustomId);
                controlData.sendMap.add(buttonInfo);
            } else {
                for (String roleId : roleIds) {
                    boolean hasRole = positionRoleApi.hasRole(tenantId, roleId, orgUnitId).getData();
                    if (hasRole) {
                        Map<String, Object> buttonInfo = new HashMap<>(16);
                        controlData.sendName = Y9Util.genCustomStr(controlData.sendName, buttonName);
                        controlData.sendKey = Y9Util.genCustomStr(controlData.sendKey, buttonCustomId);
                        buttonInfo.put(ItemConsts.SENDNAME_KEY, buttonName);
                        buttonInfo.put(ItemConsts.SENDKEY_KEY, buttonCustomId);
                        controlData.sendMap.add(buttonInfo);
                        break;
                    }
                }
            }
        }
    }

    /**
     * 处理重定向按钮逻辑
     */
    private void handleRepositionButton(ButtonControlData controlData, String processDefinitionId, String tenantId) {
        List<TargetModel> taskNodes = processDefinitionApi.getNodes(tenantId, processDefinitionId).getData();
        for (TargetModel node : taskNodes) {
            // 流程不显示在重定向按钮下面
            if (!"流程".equals(node.getTaskDefName())) {
                controlData.repositionName = Y9Util.genCustomStr(controlData.repositionName, node.getTaskDefName());
                controlData.repositionKey = Y9Util.genCustomStr(controlData.repositionKey, node.getTaskDefKey());
                Map<String, Object> repositionInfo = new HashMap<>(16);
                repositionInfo.put("repositionName", node.getTaskDefName());
                repositionInfo.put("repositionKey", node.getTaskDefKey());
                controlData.repositionMap.add(repositionInfo);
            }
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            controlData.taskDefNameJson = mapper.writeValueAsString(controlData.repositionMap);
        } catch (JsonProcessingException e) {
            LOGGER.error("解析重定向按钮失败！", e);
            controlData.taskDefNameJson = "[]";
        }
    }

    @Override
    public DocumentDetailModel menuControl4Add(DocumentDetailModel model) {
        String tenantId = Y9LoginUserHolder.getTenantId(), orgUnitId = Y9FlowableHolder.getOrgUnitId();
        String itemId = model.getItemId(), processDefinitionId = model.getProcessDefinitionId(),
            taskDefKey = model.getTaskDefKey();
        List<ItemButtonModel> buttonList = buttonService.showButton4Add(itemId);
        // 处理保存按钮相关的自定义普通按钮
        if (buttonList.contains(ItemButton.baoCun)) {
            handleCommonButtons(buttonList, itemId, processDefinitionId, taskDefKey, tenantId, orgUnitId);
        }
        // 处理发送按钮相关的路由和自定义按钮
        if (buttonList.contains(ItemButton.faSong)) {
            handleSendButtons(buttonList, itemId, processDefinitionId, taskDefKey, tenantId, orgUnitId);
        }
        model.setButtonList(buttonList);
        return model;
    }

    /**
     * 处理保存按钮相关的自定义普通按钮
     */
    private void handleCommonButtons(List<ItemButtonModel> buttonList, String itemId, String processDefinitionId,
        String taskDefKey, String tenantId, String orgUnitId) {
        List<ItemButtonBind> commonButtons =
            buttonItemBindService.listContainRoleId(itemId, ItemButtonTypeEnum.COMMON, processDefinitionId, taskDefKey);
        for (ItemButtonBind bind : commonButtons) {
            String buttonName = bind.getButtonName(), buttonCustomId = bind.getButtonCustomId();
            List<String> roleIds = bind.getRoleIds();
            // 检查用户是否有权限访问该按钮
            if (hasButtonPermission(roleIds, tenantId, orgUnitId)) {
                buttonList.add(new ItemButtonModel(buttonCustomId, buttonName, ItemButtonTypeEnum.COMMON));
            }
        }
    }

    /**
     * 处理发送按钮相关的路由和自定义按钮
     */
    private void handleSendButtons(List<ItemButtonModel> buttonList, String itemId, String processDefinitionId,
        String taskDefKey, String tenantId, String orgUnitId) {
        // 添加发送下面的路由
        addSendRoutes(buttonList, processDefinitionId, taskDefKey, tenantId);
        // 添加自定义按钮到发送
        addCustomSendButtons(buttonList, itemId, processDefinitionId, taskDefKey, tenantId, orgUnitId);
    }

    /**
     * 添加发送路由按钮
     */
    private void addSendRoutes(List<ItemButtonModel> buttonList, String processDefinitionId, String taskDefKey,
        String tenantId) {
        List<TargetModel> routeToTasks =
            processDefinitionApi.getTargetNodes(tenantId, processDefinitionId, taskDefKey).getData();
        for (TargetModel target : routeToTasks) {
            // 退回、路由网关不显示在发送下面
            if (!"退回".equals(target.getTaskDefName())
                && !ItemConsts.EXCLUSIVE_GATEWAY_KEY.equals(target.getTaskDefName())) {
                buttonList
                    .add(new ItemButtonModel(target.getTaskDefKey(), target.getTaskDefName(), ItemButtonTypeEnum.SEND));
            }
        }
    }

    /**
     * 添加自定义发送按钮
     */
    private void addCustomSendButtons(List<ItemButtonModel> buttonList, String itemId, String processDefinitionId,
        String taskDefKey, String tenantId, String orgUnitId) {
        List<ItemButtonBind> sendButtons =
            buttonItemBindService.listContainRoleId(itemId, ItemButtonTypeEnum.SEND, processDefinitionId, taskDefKey);
        for (ItemButtonBind bind : sendButtons) {
            List<String> roleIds = bind.getRoleIds();
            String buttonName = bind.getButtonName(), buttonCustomId = bind.getButtonCustomId();
            // 检查用户是否有权限访问该按钮
            if (hasButtonPermission(roleIds, tenantId, orgUnitId)) {
                buttonList.add(new ItemButtonModel(buttonCustomId, buttonName, ItemButtonTypeEnum.SEND));
            }
        }
    }

    /**
     * 检查用户是否有按钮访问权限
     */
    private boolean hasButtonPermission(List<String> roleIds, String tenantId, String orgUnitId) {
        // 如果角色ID列表为空，表示无权限限制
        if (roleIds.isEmpty()) {
            return true;
        }
        // 检查用户是否具有任一角色权限
        return roleIds.stream().anyMatch(roleId -> {
            try {
                return positionRoleApi.hasRole(tenantId, roleId, orgUnitId).getData();
            } catch (Exception e) {
                LOGGER.error("检查角色权限失败: roleId={}, orgUnitId={}", roleId, orgUnitId, e);
                return false;
            }
        });
    }

    @Override
    public DocumentDetailModel menuControl4Draft(DocumentDetailModel model) {
        String tenantId = Y9LoginUserHolder.getTenantId(), orgUnitId = Y9FlowableHolder.getOrgUnitId();
        String itemId = model.getItemId(), processDefinitionId = model.getProcessDefinitionId(),
            taskDefKey = model.getTaskDefKey();
        List<ItemButtonModel> buttonList = buttonService.showButton4Draft(itemId);
        // 处理保存按钮相关的自定义普通按钮
        if (buttonList.contains(ItemButton.baoCun)) {
            handleCommonButtons(buttonList, itemId, processDefinitionId, taskDefKey, tenantId, orgUnitId);
        }
        // 处理发送按钮相关的路由和自定义按钮
        if (buttonList.contains(ItemButton.faSong)) {
            handleSendButtons(buttonList, itemId, processDefinitionId, taskDefKey, tenantId, orgUnitId);
        }
        model.setButtonList(buttonList);
        return model;
    }

    @Override
    public DocumentDetailModel menuControl4Todo(DocumentDetailModel model) {
        String tenantId = Y9LoginUserHolder.getTenantId(), orgUnitId = Y9FlowableHolder.getOrgUnitId();
        String itemId = model.getItemId(), processDefinitionId = model.getProcessDefinitionId(),
            taskDefKey = model.getTaskDefKey(), taskId = model.getTaskId();
        List<ItemButtonModel> buttonList = buttonService.showButton4Todo(model);
        // 处理保存按钮相关的自定义普通按钮
        if (buttonList.contains(ItemButton.baoCun)) {
            handleCommonButtonsForTodo(buttonList, itemId, processDefinitionId, taskDefKey, tenantId, orgUnitId);
        }
        // 处理发送按钮相关的路由和自定义按钮
        if (buttonList.contains(ItemButton.faSong) && StringUtils.isNotBlank(taskDefKey)) {
            handleSendButtonsForTodo(buttonList, itemId, processDefinitionId, taskDefKey, tenantId, orgUnitId);
        }
        // 处理退回按钮
        if (buttonList.contains(ItemButton.tuiHui)) {
            handleRollbackButtons(buttonList, taskId, processDefinitionId, taskDefKey, tenantId);
        }
        model.setButtonList(buttonList);
        return model;
    }

    /**
     * 处理保存按钮相关的自定义普通按钮（待办场景）
     */
    private void handleCommonButtonsForTodo(List<ItemButtonModel> buttonList, String itemId, String processDefinitionId,
        String taskDefKey, String tenantId, String orgUnitId) {
        List<ItemButtonBind> commonButtons =
            buttonItemBindService.listContainRoleId(itemId, ItemButtonTypeEnum.COMMON, processDefinitionId, taskDefKey);
        commonButtons.stream().filter(bind -> !"发送".equals(bind.getButtonName())).forEach(bind -> {
            List<String> roleIds = bind.getRoleIds();
            if (roleIds.isEmpty() || roleIds.stream()
                .anyMatch(roleId -> positionRoleApi.hasRole(tenantId, roleId, orgUnitId).getData())) {
                buttonList.add(
                    new ItemButtonModel(bind.getButtonCustomId(), bind.getButtonName(), ItemButtonTypeEnum.COMMON));
            }
        });
    }

    /**
     * 处理发送按钮相关的路由和自定义按钮（待办场景）
     */
    private void handleSendButtonsForTodo(List<ItemButtonModel> buttonList, String itemId, String processDefinitionId,
        String taskDefKey, String tenantId, String orgUnitId) {
        // 检查是否有自定义"发送"按钮
        AtomicBoolean haveSendButton = new AtomicBoolean(false);
        List<ItemButtonBind> commonButtons =
            buttonItemBindService.listContainRoleId(itemId, ItemButtonTypeEnum.COMMON, processDefinitionId, taskDefKey);
        commonButtons.stream().filter(bib -> "发送".equals(bib.getButtonName())).forEach(bib -> {
            List<String> roleIds = bib.getRoleIds();
            if (roleIds.isEmpty() || roleIds.stream()
                .anyMatch(roleId -> positionRoleApi.hasRole(tenantId, roleId, orgUnitId).getData())) {
                buttonList
                    .add(new ItemButtonModel(bib.getButtonCustomId(), bib.getButtonName(), ItemButtonTypeEnum.COMMON));
                haveSendButton.set(true);
            }
        });
        // 如果没有自定义发送按钮，则添加默认发送按钮和路由
        if (!haveSendButton.get()) {
            addSendRoutes(buttonList, processDefinitionId, taskDefKey, tenantId);
            addCustomSendButtons(buttonList, itemId, processDefinitionId, taskDefKey, tenantId, orgUnitId);
        }
    }

    /**
     * 处理退回按钮
     */
    private void handleRollbackButtons(List<ItemButtonModel> buttonList, String taskId, String processDefinitionId,
        String taskDefKey, String tenantId) {
        TaskModel task = taskApi.findById(tenantId, taskId).getData();
        Boolean isSub = processDefinitionApi.isSubProcessChildNode(tenantId, processDefinitionId, taskDefKey).getData();
        List<HistoricTaskInstanceModel> results =
            historictaskApi.getByProcessInstanceId(tenantId, task.getProcessInstanceId(), "").getData();
        if (isSub) {
            handleSubProcessRollback(buttonList, task, results, tenantId);
        } else {
            handleMainProcessRollback(buttonList, task, results, tenantId);
        }
    }

    /**
     * 处理子流程退回按钮
     */
    private void handleSubProcessRollback(List<ItemButtonModel> buttonList, TaskModel task,
        List<HistoricTaskInstanceModel> results, String tenantId) {
        String executionId = task.getExecutionId();
        results.stream().filter(r -> r.getExecutionId().equals(executionId) && null != r.getEndTime()).forEach(r -> {
            String taskName = r.getName() + "({0})";
            OrgUnit position = null;
            if (StringUtils.isNotBlank(r.getAssignee())) {
                position = orgUnitApi.getOrgUnit(tenantId, r.getAssignee()).getData();
            }
            taskName = MessageFormat.format(taskName, null == position ? "无" : position.getName());
            ItemButtonModel itemButtonModel = new ItemButtonModel(r.getTaskDefinitionKey(), taskName,
                ItemButtonTypeEnum.ROLLBACK, List.of(r.getAssignee()), "", null);
            if (!buttonList.contains(itemButtonModel)) {
                buttonList.add(itemButtonModel);
            }
        });
    }

    /**
     * 处理主流程退回按钮
     */
    private void handleMainProcessRollback(List<ItemButtonModel> buttonList, TaskModel task,
        List<HistoricTaskInstanceModel> results, String tenantId) {
        List<TargetModel> subNodeList =
            processDefinitionApi.getSubProcessChildNode(tenantId, task.getProcessDefinitionId()).getData();
        results.stream()
            .filter(hisTask -> null != hisTask.getEndTime() && StringUtils.isNotBlank(hisTask.getAssignee()))
            .forEach(hisTask -> {
                AtomicBoolean isSubNode = new AtomicBoolean(false);
                subNodeList.forEach(s -> {
                    if (s.getTaskDefKey().equals(hisTask.getTaskDefinitionKey())) {
                        isSubNode.set(true);
                    }
                });
                if (!isSubNode.get()) {
                    String taskName = hisTask.getName() + "({0})";
                    List<Person> personList = new ArrayList<>();
                    if (StringUtils.isNotBlank(hisTask.getAssignee())) {
                        personList = positionApi.listPersonsByPositionId(tenantId, hisTask.getAssignee()).getData();
                    }
                    taskName = MessageFormat.format(taskName,
                        personList.isEmpty() ? "无" : personList.stream().findFirst().get().getName());
                    ItemButtonModel itemButtonModel = new ItemButtonModel(hisTask.getTaskDefinitionKey(), taskName,
                        ItemButtonTypeEnum.ROLLBACK, List.of(hisTask.getAssignee()), "", null);
                    if (!buttonList.contains(itemButtonModel)) {
                        buttonList.add(itemButtonModel);
                    }
                }
            });
    }

    @Override
    public void menuControl4ChaoSong(DocumentDetailModel model) {
        List<ItemButtonModel> buttonList = buttonService.showButton4ChaoSong(model);
        model.setButtonList(buttonList);
    }

    @Override
    public void menuControl4Doing(DocumentDetailModel model) {
        List<ItemButtonModel> buttonList = buttonService.showButton4Doing(model);
        if (model.getItembox().equals(ItemBoxTypeEnum.MONITOR_DOING.getValue())) {
            String documentId = model.getDocumentId();
            if (documentId.equals(model.getProcessSerialNumber())) {
                buttonList.add(ItemButton.chongDingWei);
            } else {
                SignDeptDetail signDeptDetail = signDeptDetailService.findById(documentId);
                if (signDeptDetail.getStatus().equals(SignDeptDetailStatusEnum.DOING)) {
                    buttonList.add(ItemButton.chongDingWei);
                }
            }
        }
        model.setButtonList(buttonList);
    }

    @Override
    public void menuControl4DoingAdmin(DocumentDetailModel model) {
        model.setButtonList(buttonService.showButton4DoingAdmin(model));
    }

    @Override
    public DocumentDetailModel menuControl4Done(DocumentDetailModel model) {
        List<ItemButtonModel> buttonList = buttonService.showButton4Done(model);
        model.setButtonList(buttonList);
        return model;
    }

    @Override
    public DocumentDetailModel menuControl4Recycle(DocumentDetailModel model) {
        List<ItemButtonModel> buttonList = buttonService.showButton4Recycle();
        model.setButtonList(buttonList);
        return model;
    }

    @Override
    public List<String> parseUserChoice(String userChoice) {
        String users = "";
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (StringUtils.isBlank(userChoice)) {
            return new ArrayList<>();
        }
        String[] userChoices = userChoice.split(SysVariables.SEMICOLON);
        for (String choice : userChoices) {
            String[] parts = choice.split(SysVariables.COLON);
            int principalType = ItemPermissionEnum.POSITION.getValue();
            String userId = choice;
            if (parts.length == 2) {
                principalType = Integer.parseInt(parts[0]);
                userId = parts[1];
            }
            users = processUserChoiceByType(users, tenantId, principalType, userId);
        }
        List<String> result = Y9Util.stringToList(users, SysVariables.SEMICOLON);
        ListUtil.removeDuplicateWithOrder(result);
        return result;
    }

    /**
     * 根据用户类型处理用户选择
     */
    private String processUserChoiceByType(String users, String tenantId, int principalType, String userId) {
        switch (ItemPermissionEnum.valueOf(principalType)) {
            case POSITION:
                return processPositionUser(users, tenantId, userId);
            case DEPARTMENT:
                return processDepartmentUser(users, userId);
            case GROUP_CUSTOM:
                return processCustomGroupUser(users, tenantId, userId);
            default:
                return users;
        }
    }

    /**
     * 处理岗位类型用户
     */
    private String processPositionUser(String users, String tenantId, String userId) {
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, userId).getData();
        if (orgUnit != null) {
            users = addUserId(users, userId);
        }
        return users;
    }

    /**
     * 处理部门类型用户
     */
    private String processDepartmentUser(String users, String deptId) {
        List<Position> positionList = new ArrayList<>();
        getAllPosition(positionList, deptId);
        for (Position position : positionList) {
            users = addUserId(users, position.getId());
        }
        return users;
    }

    /**
     * 处理自定义组类型用户
     */
    private String processCustomGroupUser(String users, String tenantId, String groupId) {
        List<CustomGroupMember> members =
            customGroupApi.listCustomGroupMember(tenantId, new CustomGroupMemberQuery(groupId, OrgTypeEnum.POSITION))
                .getData();
        for (CustomGroupMember member : members) {
            OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, member.getMemberId()).getData();
            if (orgUnit != null && StringUtils.isNotBlank(orgUnit.getId())) {
                users = addUserId(users, orgUnit.getId());
            }
        }
        return users;
    }

    public Y9Result<TargetModel> parserRouteToTaskId(String itemId, String processSerialNumber,
        String processDefinitionId, String taskDefKey, String taskId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Y9Result<TargetModel> result = Y9Result.failure("解析目标路由失败");
        try {
            List<TargetModel> targetNodes =
                processDefinitionApi.getTargetNodes(tenantId, processDefinitionId, taskDefKey).getData();
            // 检查目标节点是否存在
            if (targetNodes == null || targetNodes.isEmpty()) {
                return Y9Result.failure("目标路由不存在");
            }
            // 如果只有一个目标节点，直接返回
            if (targetNodes.size() == 1) {
                result.setData(targetNodes.get(0));
                result.setSuccess(true);
                result.setMsg("解析目标路由成功");
                return result;
            }
            // 获取表单绑定信息
            List<Y9FormItemBind> binds =
                y9FormItemBindService.listByItemIdAndProcDefIdAndTaskDefKey(itemId, processDefinitionId, taskDefKey);
            if (binds == null || binds.isEmpty()) {
                return Y9Result.failure("未找到表单绑定信息");
            }
            // 获取表单变量数据
            Map<String, Object> variables =
                y9FormService.getFormData4Var(binds.get(0).getFormId(), processSerialNumber);
            if (variables == null) {
                return Y9Result.failure("获取表单数据失败");
            }
            // 处理数值类型的变量
            processNumericVariables(variables);
            LOGGER.info("表单变量数据: {}", Y9JsonUtil.writeValueAsString(variables));
            // 根据条件表达式筛选符合条件的目标节点
            List<TargetModel> matchedTargetNodes = filterTargetNodesByCondition(tenantId, targetNodes, variables);
            // 处理匹配结果
            return handleMatchedTargetNodes(result, matchedTargetNodes, tenantId, taskId, variables);

        } catch (Exception e) {
            LOGGER.error("解析目标路由失败！", e);
            return Y9Result.failure("解析目标路由时发生异常: " + e.getMessage());
        }
    }

    /**
     * 处理数值类型的变量
     */
    private void processNumericVariables(Map<String, Object> variables) {
        for (String columnName : variables.keySet()) {
            Object value = variables.get(columnName);
            if (value != null) {
                String str = StringUtils.replace(value.toString(), ".", "");
                // 判断是否为数值
                if (StringUtils.isNumeric(str)) {
                    try {
                        if (value.toString().contains(".")) {
                            LOGGER.info("处理Double类型变量: {}", value);
                            variables.put(columnName, Double.valueOf(value.toString()));
                        } else {
                            LOGGER.info("处理Integer类型变量: {}", value);
                            variables.put(columnName, Integer.parseInt(value.toString()));
                        }
                    } catch (NumberFormatException e) {
                        LOGGER.warn("数值转换失败: columnName={}, value={}", columnName, value);
                    }
                }
            }
        }
    }

    /**
     * 根据条件表达式筛选符合条件的目标节点
     */
    private List<TargetModel> filterTargetNodesByCondition(String tenantId, List<TargetModel> targetNodes,
        Map<String, Object> variables) {
        List<TargetModel> matchedTargetNodes = new ArrayList<>();
        for (TargetModel targetNode : targetNodes) {
            try {
                boolean matches =
                    conditionParserApi.parser(tenantId, targetNode.getConditionExpression(), variables).getData();
                if (matches) {
                    matchedTargetNodes.add(targetNode);
                }
            } catch (Exception e) {
                LOGGER.error("条件表达式解析失败: conditionExpression={}", targetNode.getConditionExpression(), e);
            }
        }
        return matchedTargetNodes;
    }

    /**
     * 处理匹配到的目标节点
     */
    private Y9Result<TargetModel> handleMatchedTargetNodes(Y9Result<TargetModel> result,
        List<TargetModel> matchedTargetNodes, String tenantId, String taskId, Map<String, Object> variables) {
        if (matchedTargetNodes.isEmpty()) {
            result.setMsg("未找到符合要求的目标路由");
            return result;
        }
        if (matchedTargetNodes.size() > 1) {
            result.setMsg("符合要求的目标路由过多");
            return result;
        }
        // 设置流程变量
        if (StringUtils.isNotBlank(taskId)) {
            try {
                variableApi.setVariables(tenantId, taskId, variables);
            } catch (Exception e) {
                LOGGER.error("设置流程变量失败: taskId={}", taskId, e);
            }
        }
        result.setData(matchedTargetNodes.get(0));
        result.setMsg("解析目标路由成功");
        result.setSuccess(true);
        return result;
    }

    @Override
    public Y9Result<List<String>> parserUser(String itemId, String processDefinitionId, String routeToTaskId,
        String routeToTaskName, String processInstanceId, String multiInstance) {
        Y9Result<List<String>> result = Y9Result.failure("解析人员失败");
        List<OrgUnit> orgUnitList =
            roleService.listPermUser4SubmitTo(itemId, processDefinitionId, routeToTaskId, processInstanceId);
        if (orgUnitList.isEmpty()) {
            result.setMsg("目标路由【" + routeToTaskName + "】未授权人员");
            return result;
        }
        if (SysVariables.COMMON.equals(multiInstance) && orgUnitList.size() > 1) {
            result.setMsg("目标路由【" + routeToTaskName + "】授权人员过多");
            return result;
        }
        List<String> userList = new ArrayList<>();
        for (OrgUnit orgUnit : orgUnitList) {
            userList.add(orgUnit.getId());
        }
        result.setData(userList);
        result.setSuccess(true);
        return result;
    }

    @Override
    public Y9Result<String> saveAndForwarding(String itemId, String processSerialNumber, String processDefinitionKey,
        String userChoice, String sponsorGuid, String routeToTaskId, Map<String, Object> variables) {
        try {
            // 参数验证
            if (StringUtils.isBlank(itemId) || StringUtils.isBlank(processSerialNumber)
                || StringUtils.isBlank(processDefinitionKey) || StringUtils.isBlank(userChoice)) {
                return Y9Result.failure("必要参数不能为空");
            }
            // 解析用户选择
            List<String> userList = new ArrayList<>(parseUserChoice(userChoice));
            int num = userList.size();
            // 验证用户数量
            if (num > 100) {
                return Y9Result.failure("发送人数过多");
            }
            if (userList.isEmpty()) {
                return Y9Result.failure("未匹配到发送人");
            }
            // 启动流程
            StartProcessResultModel model = startProcess(itemId, processSerialNumber, processDefinitionKey);
            if (model == null || StringUtils.isBlank(model.getTaskId())) {
                return Y9Result.failure("流程启动失败");
            }
            String taskId = model.getTaskId();
            String tenantId = Y9LoginUserHolder.getTenantId();
            // 设置流程变量
            if (variables != null && !variables.isEmpty()) {
                variableApi.setVariables(tenantId, taskId, variables);
                handleParallelSubprocess(variables, tenantId, model, routeToTaskId, userList);
            }
            // 执行发送操作
            return start4Forwarding(taskId, routeToTaskId, sponsorGuid, userList);
        } catch (Exception e) {
            LOGGER.error("保存并发送流程失败: itemId={}, processSerialNumber={}", itemId, processSerialNumber, e);
            return Y9Result.failure("操作失败: " + e.getMessage());
        }
    }

    /**
     * 处理并行子流程逻辑
     */
    private void handleParallelSubprocess(Map<String, Object> variables, String tenantId, StartProcessResultModel model,
        String routeToTaskId, List<String> userList) {
        Object subprocessNumObj = variables.get(ItemConsts.SUBPROCESSNUM_KEY);
        if (subprocessNumObj != null && !userList.isEmpty()) {
            try {
                String type =
                    processDefinitionApi.getNodeType(tenantId, model.getProcessDefinitionId(), routeToTaskId).getData();
                if (SysVariables.PARALLEL.equals(type)) {
                    int subProcessNum = Integer.parseInt(subprocessNumObj.toString());
                    if (subProcessNum > 1 && userList.size() == 1) {
                        String userId = userList.get(0);
                        // 补充并行任务用户，同时避免超过人数限制
                        for (int i = 1; i < subProcessNum && userList.size() < 100; i++) {
                            userList.add(userId);
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.warn("处理并行子流程失败", e);
            }
        }
    }

    @Override
    public Y9Result<String> saveAndForwardingByTaskKey(String itemId, String processSerialNumber,
        String processDefinitionKey, String userChoice, String sponsorGuid, String routeToTaskId,
        String startRouteToTaskId, Map<String, Object> variables) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<String> userList = new ArrayList<>(parseUserChoice(userChoice));
        int num = userList.size();
        boolean tooMuch = num > 100;
        if (tooMuch) {
            return Y9Result.failure("发送人数过多");
        }
        Map<String, Object> startMap =
            startProcessByTaskKey(itemId, processSerialNumber, processDefinitionKey, startRouteToTaskId, List.of());
        String taskId = (String)startMap.get("taskId");
        if (!variables.isEmpty()) {
            variableApi.setVariables(tenantId, taskId, variables);
        }
        return start4Forwarding(taskId, routeToTaskId, sponsorGuid, userList);
    }

    @Override
    public Y9Result<Object> saveAndSubmitTo(String itemId, String processSerialNumber) {
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = Y9FlowableHolder.getOrgUnitId();
        OrgUnit orgUnit = Y9FlowableHolder.getOrgUnit();
        try {
            Item item = itemService.findById(itemId);
            String processDefinitionKey = item.getWorkflowGuid();
            ProcessDefinitionModel processDefinitionModel =
                repositoryApi.getLatestProcessDefinitionByKey(tenantId, processDefinitionKey).getData();
            String processDefinitionId = processDefinitionModel.getId();
            String taskDefKey = itemStartNodeRoleService.getStartTaskDefKey(itemId);
            Y9Result<TargetModel> routeToTaskIdResult =
                parserRouteToTaskId(itemId, processSerialNumber, processDefinitionId, taskDefKey, "");
            if (!routeToTaskIdResult.isSuccess()) {
                return Y9Result.failure(routeToTaskIdResult.getMsg());
            }
            String routeToTaskId = routeToTaskIdResult.getData().getTaskDefKey(),
                routeToTaskName = routeToTaskIdResult.getData().getTaskDefName();
            FlowElementModel flowElementModel =
                processDefinitionApi.getNode(tenantId, processDefinitionId, routeToTaskId).getData();
            Y9Result<List<String>> userResult = parserUser(itemId, processDefinitionId, routeToTaskId, routeToTaskName,
                "", flowElementModel.getMultiInstance());
            if (!userResult.isSuccess()) {
                return Y9Result.failure(userResult.getMsg());
            }
            StartProcessResultModel model = startProcess(itemId, processSerialNumber, processDefinitionKey);
            String taskId = model.getTaskId(), processInstanceId = model.getProcessInstanceId();
            ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
            List<String> userList = userResult.getData();
            Map<String, Object> variables =
                CommonOpt.setVariables(userId, orgUnit.getName(), routeToTaskId, userList, flowElementModel);
            asyncHandleService.forwarding4Task(processInstanceId, processParam, "", "", taskId, flowElementModel,
                variables, userList);
            return Y9Result.successMsg("提交成功");
        } catch (Exception e) {
            LOGGER.error("提交失败！异常：", e);
        }
        return Y9Result.failure("提交失败！");
    }

    public SignTaskConfigModel signTaskConfig(String itemId, String processDefinitionId, String taskDefinitionKey,
        String processSerialNumber) {
        SignTaskConfigModel model = new SignTaskConfigModel();
        model.setSignTask(false);
        model.setUserChoice("");
        model.setOnePerson(false);
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String multiInstance =
                processDefinitionApi.getNodeType(tenantId, processDefinitionId, taskDefinitionKey).getData();
            // 如果不是COMMON类型，直接返回默认配置
            if (!SysVariables.COMMON.equals(multiInstance)) {
                return model;
            }
            ItemTaskConf itemTaskConf = taskConfRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId,
                processDefinitionId, taskDefinitionKey);
            ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
            // 处理签收任务逻辑
            if (itemTaskConf != null && itemTaskConf.getSignTask()) {
                return handleSignTask(model, processParam, itemId, processDefinitionId, taskDefinitionKey, tenantId);
            } else {
                // 处理非签收任务逻辑
                return handleNonSignTask(model, itemId, processDefinitionId, taskDefinitionKey, processParam);
            }
        } catch (Exception e) {
            LOGGER.error("获取签收任务配置失败！", e);
            return model;
        }
    }

    /**
     * 处理签收任务逻辑
     */
    private SignTaskConfigModel handleSignTask(SignTaskConfigModel model, ProcessParam processParam, String itemId,
        String processDefinitionId, String taskDefinitionKey, String tenantId) {
        model.setSignTask(true);
        boolean searchPerson = true;
        // 查找已分配的人员
        if (processParam != null && StringUtils.isNotBlank(processParam.getProcessInstanceId())) {
            List<HistoricTaskInstanceModel> hisTaskList = historictaskApi
                .findTaskByProcessInstanceIdOrByEndTimeAsc(tenantId, processParam.getProcessInstanceId(), "")
                .getData();
            for (HistoricTaskInstanceModel hisTask : hisTaskList) {
                if (hisTask.getTaskDefinitionKey().equals(taskDefinitionKey)
                    && StringUtils.isNotBlank(hisTask.getAssignee())) {
                    searchPerson = false;
                    model.setUserChoice("6:" + hisTask.getAssignee());
                    break;
                }
            }
        }
        // 如果未找到已分配人员，则从权限配置中获取
        if (searchPerson) {
            List<OrgUnit> orgUnitList = getUserChoice(itemId, processDefinitionId, taskDefinitionKey,
                processParam != null ? processParam.getProcessInstanceId() : "");
            if (orgUnitList.isEmpty()) {
                model.setSignTask(false);
            } else {
                model.setUserChoice(buildUserChoice(orgUnitList));
            }
        }
        return model;
    }

    /**
     * 处理非签收任务逻辑
     */
    private SignTaskConfigModel handleNonSignTask(SignTaskConfigModel model, String itemId, String processDefinitionId,
        String taskDefinitionKey, ProcessParam processParam) {
        List<OrgUnit> orgUnitList = getUserChoice(itemId, processDefinitionId, taskDefinitionKey,
            processParam != null ? processParam.getProcessInstanceId() : "");
        // 只有一个人且为岗位时，设置直接发送
        if (orgUnitList.size() == 1 && orgUnitList.get(0).getOrgType().equals(OrgTypeEnum.POSITION)) {
            model.setOnePerson(true);
            model.setUserChoice("6:" + orgUnitList.get(0).getId());
        }
        return model;
    }

    /**
     * 构建用户选择字符串
     */
    private String buildUserChoice(List<OrgUnit> orgUnitList) {
        StringBuilder userChoice = new StringBuilder();
        for (OrgUnit orgUnit : orgUnitList) {
            int type = 0;
            if (orgUnit.getOrgType().equals(OrgTypeEnum.DEPARTMENT)) {
                type = 2;
            } else if (orgUnit.getOrgType().equals(OrgTypeEnum.POSITION)) {
                type = 6;
            }

            if (userChoice.length() > 0) {
                userChoice.append(";");
            }
            userChoice.append(type).append(":").append(orgUnit.getId());
        }
        return userChoice.toString();
    }

    @Override
    public Y9Result<String> start4Forwarding(String taskId, String routeToTaskId, String sponsorGuid,
        List<String> userList) {
        String processInstanceId = "";
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), userId = Y9FlowableHolder.getOrgUnitId();
            OrgUnit orgUnit = Y9FlowableHolder.getOrgUnit();
            TaskModel task = taskApi.findById(tenantId, taskId).getData();
            processInstanceId = task.getProcessInstanceId();
            ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
            // 得到要发送节点的multiInstance，PARALLEL表示并行，SEQUENTIAL表示串行
            FlowElementModel flowElementModel =
                processDefinitionApi.getNode(tenantId, task.getProcessDefinitionId(), routeToTaskId).getData();
            Map<String, Object> variables =
                CommonOpt.setVariables(userId, orgUnit.getName(), routeToTaskId, userList, flowElementModel);
            int num = userList.size();
            // 并行发送超过20人时，启用异步后台处理。
            boolean tooMuch = num > 20;
            if (SysVariables.PARALLEL.equals(flowElementModel.getMultiInstance()) && tooMuch) {
                TaskVariable taskVariable =
                    taskVariableRepository.findByTaskIdAndKeyName(taskId, ItemConsts.ISFORWARDING_KEY);
                if (taskVariable == null) {
                    taskVariable = new TaskVariable();
                    taskVariable.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                    taskVariable.setProcessInstanceId(processInstanceId);
                    taskVariable.setTaskId(taskId);
                    taskVariable.setKeyName(ItemConsts.ISFORWARDING_KEY);
                }
                taskVariable.setText(TRUE_STR_KEY + num);
                taskVariableRepository.save(taskVariable);
                asyncHandleService.forwarding(tenantId, orgUnit, processInstanceId, processParam, "", sponsorGuid,
                    taskId, flowElementModel, variables, userList);
            } else {
                assert processParam != null;
                asyncHandleService.forwarding4Task(processInstanceId, processParam, "true", sponsorGuid, taskId,
                    flowElementModel, variables, userList);
            }
            return Y9Result.success(processInstanceId, "发送成功!");
        } catch (Exception e) {
            LOGGER.error("公文发送失败！", e);
            try {
                final Writer result = new StringWriter();
                final PrintWriter print = new PrintWriter(result);
                e.printStackTrace(print);
                String msg = result.toString();
                // 保存任务发送错误日志
                ErrorLog errorLog = new ErrorLog();
                errorLog.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                errorLog.setErrorFlag(ErrorLogModel.ERROR_FLAG_FORWRDING);
                errorLog.setErrorType(ErrorLogModel.ERROR_TASK);
                errorLog.setExtendField("启动流程发送少数人失败");
                errorLog.setProcessInstanceId(processInstanceId);
                errorLog.setTaskId(taskId);
                errorLog.setText(msg);
                errorLogService.saveErrorLog(errorLog);
            } catch (Exception e2) {
                LOGGER.error("保存任务发送错误日志失败！", e2);
            }
        }
        return Y9Result.failure("发送失败!");
    }

    @Override
    public StartProcessResultModel startProcess(String itemId, String processSerialNumber,
        String processDefinitionKey) {
        StartProcessResultModel model = null;
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            Map<String, Object> vars = new HashMap<>(16);
            Item item = itemRepository.findById(itemId).orElse(null);
            vars.put(ItemConsts.TENANTID_KEY, tenantId);
            String startTaskDefKey = itemStartNodeRoleService.getStartTaskDefKey(itemId);
            vars.put(ItemConsts.ROUTETOTASKID_KEY, startTaskDefKey);
            vars.put("_FLOWABLE_SKIP_EXPRESSION_ENABLED", true);
            assert item != null;
            if (item.isShowSubmitButton()) {
                ProcessDefinitionModel processDefinitionModel =
                    repositoryApi.getLatestProcessDefinitionByKey(tenantId, processDefinitionKey).getData();
                List<Y9FormItemBind> binds = y9FormItemBindService.listByItemIdAndProcDefIdAndTaskDefKey(itemId,
                    processDefinitionModel.getId(), "");
                Map<String, Object> variables =
                    y9FormService.getFormData4Var(binds.get(0).getFormId(), processSerialNumber);
                for (String columnName : variables.keySet()) {
                    String str = StringUtils.replace(variables.get(columnName).toString(), ".", "");
                    if (StringUtils.isNumeric(str)) {// 是数值
                        if (variables.get(columnName).toString().contains(".")) {
                            LOGGER.info("**********startProcess_Double:{}", variables.get(columnName).toString());
                            variables.put(columnName, Double.valueOf(variables.get(columnName).toString()));
                        } else {
                            LOGGER.info("**********startProcess_Integer:{}", variables.get(columnName).toString());
                            variables.put(columnName, Integer.parseInt(variables.get(columnName).toString()));
                        }
                    }
                }
                vars.putAll(variables);
            }
            TaskModel task = activitiOptService.startProcess(processSerialNumber, processDefinitionKey,
                item.getSystemName(), List.of(), vars);
            ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
            processParam.setProcessInstanceId(task.getProcessInstanceId());
            processParam.setStartor(Y9FlowableHolder.getOrgUnitId());
            processParam.setStartorName(Y9FlowableHolder.getOrgUnit().getName());
            processParam.setSended("true");
            // 保存流程信息到ES
            process4SearchService.saveToDataCenter(tenantId, processParam, Y9FlowableHolder.getOrgUnit());
            processParamService.saveOrUpdate(processParam);
            // 异步处理数据
            asyncHandleService.startProcessHandle(tenantId, processSerialNumber, task.getId(),
                task.getProcessInstanceId(), processParam.getSearchTerm());
            model = new StartProcessResultModel();
            model.setProcessDefinitionId(task.getProcessDefinitionId());
            model.setProcessInstanceId(task.getProcessInstanceId());
            model.setProcessSerialNumber(processSerialNumber);
            model.setTaskId(task.getId());
            model.setTaskDefKey(task.getTaskDefinitionKey());
        } catch (Exception e) {
            LOGGER.error("当前人启动流程失败！异常：", e);
        }
        return model;
    }

    @Override
    public StartProcessResultModel startProcess(String itemId, String processSerialNumber, String processDefinitionKey,
        String userIds) {
        StartProcessResultModel model = null;
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            OrgUnit orgUnit = Y9FlowableHolder.getOrgUnit();
            Map<String, Object> vars = new HashMap<>(16);
            Item item = itemRepository.findById(itemId).orElse(null);
            vars.put(ItemConsts.TENANTID_KEY, tenantId);
            String startTaskDefKey = itemStartNodeRoleService.getStartTaskDefKey(itemId);
            vars.put(ItemConsts.ROUTETOTASKID_KEY, startTaskDefKey);

            CommonOpt.setVariables(orgUnit.getId(), orgUnit.getName(), "", Arrays.asList(userIds.split(",")),
                processSerialNumber, null, vars);
            assert item != null;
            ProcessInstanceModel piModel = runtimeApi
                .startProcessInstanceByKey(tenantId, orgUnit.getId(), processDefinitionKey, item.getSystemName(), vars)
                .getData();
            // 获取运行的任务节点,这里没有考虑启动节点下一个用户任务节点是多实例的情况
            String processInstanceId = piModel.getId();
            TaskModel task = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData().get(0);

            ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
            processParam.setProcessInstanceId(task.getProcessInstanceId());
            processParam.setStartor(Y9FlowableHolder.getOrgUnitId());
            processParam.setStartorName(Y9FlowableHolder.getOrgUnit().getName());
            processParam.setSended("true");
            // 保存流程信息到ES
            process4SearchService.saveToDataCenter(tenantId, processParam, Y9FlowableHolder.getOrgUnit());

            processParamService.saveOrUpdate(processParam);

            // 异步处理数据
            asyncHandleService.startProcessHandle(tenantId, processSerialNumber, task.getId(),
                task.getProcessInstanceId(), processParam.getSearchTerm());
            model = new StartProcessResultModel();
            model.setProcessDefinitionId(task.getProcessDefinitionId());
            model.setProcessInstanceId(task.getProcessInstanceId());
            model.setProcessSerialNumber(processSerialNumber);
            model.setTaskId(task.getId());
            model.setTaskDefKey(task.getTaskDefinitionKey());
            return model;
        } catch (Exception e) {
            LOGGER.error("启动流程失败！异常：", e);
        }
        return model;
    }

    @Override
    public Map<String, Object> startProcessByTaskKey(String itemId, String processSerialNumber,
        String processDefinitionKey, String startRouteToTaskId, List<String> startOrgUnitIdList) {
        Map<String, Object> map = new HashMap<>(16);
        map.put(UtilConsts.SUCCESS, false);
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            Map<String, Object> vars = new HashMap<>(16);
            Item item = itemRepository.findById(itemId).orElse(null);
            vars.put(ItemConsts.TENANTID_KEY, tenantId);
            vars.put(SysVariables.ROUTE_TO_TASK_ID, startRouteToTaskId);
            assert item != null;
            TaskModel task = activitiOptService.startProcess(processSerialNumber, processDefinitionKey,
                item.getSystemName(), startOrgUnitIdList, vars);
            map.put("processInstanceId", task.getProcessInstanceId());
            map.put("processSerialNumber", processSerialNumber);
            map.put("processDefinitionId", task.getProcessDefinitionId());
            map.put("taskId", task.getId());
            map.put("taskDefKey", task.getTaskDefinitionKey());

            ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
            processParam.setProcessInstanceId(task.getProcessInstanceId());
            processParam.setStartor(Y9FlowableHolder.getOrgUnitId());
            processParam.setStartorName(Y9FlowableHolder.getOrgUnit().getName());
            processParam.setSended("true");
            // 保存流程信息到ES
            process4SearchService.saveToDataCenter(tenantId, processParam, Y9FlowableHolder.getOrgUnit());

            processParamService.saveOrUpdate(processParam);

            // 异步处理数据
            asyncHandleService.startProcessHandle(tenantId, processSerialNumber, task.getId(),
                task.getProcessInstanceId(), processParam.getSearchTerm());

            map.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            LOGGER.error("根据任务key启动流程失败！", e);
        }
        return map;
    }

    @Override
    public StartProcessResultModel startProcessByTheTaskKey(String itemId, String processSerialNumber,
        String processDefinitionKey, String startTaskDefKey, List<String> startOrgUnitIdList) {
        StartProcessResultModel model = null;
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            startTaskDefKey = StringUtils.isBlank(startTaskDefKey) ? itemStartNodeRoleService.getStartTaskDefKey(itemId)
                : startTaskDefKey;
            Map<String, Object> vars = new HashMap<>(16);
            Item item = itemRepository.findById(itemId).orElse(null);
            vars.put(ItemConsts.TENANTID_KEY, tenantId);
            vars.put(ItemConsts.ROUTETOTASKID_KEY, startTaskDefKey);
            vars.put("_FLOWABLE_SKIP_EXPRESSION_ENABLED", true);
            assert item != null;
            if (item.isShowSubmitButton()) {
                ProcessDefinitionModel processDefinitionModel =
                    repositoryApi.getLatestProcessDefinitionByKey(tenantId, processDefinitionKey).getData();
                List<Y9FormItemBind> binds = y9FormItemBindService.listByItemIdAndProcDefIdAndTaskDefKey(itemId,
                    processDefinitionModel.getId(), "");
                Map<String, Object> variables =
                    y9FormService.getFormData4Var(binds.get(0).getFormId(), processSerialNumber);
                for (String columnName : variables.keySet()) {
                    String str = StringUtils.replace(variables.get(columnName).toString(), ".", "");
                    if (StringUtils.isNumeric(str)) {// 是数值
                        if (variables.get(columnName).toString().contains(".")) {
                            LOGGER.info("*********startProcess_Double:{}", variables.get(columnName).toString());
                            variables.put(columnName, Double.valueOf(variables.get(columnName).toString()));
                        } else {
                            LOGGER.info("*********startProcess_Integer:{}", variables.get(columnName).toString());
                            variables.put(columnName, Integer.parseInt(variables.get(columnName).toString()));
                        }
                    }
                }
                vars.putAll(variables);
            }
            TaskModel task = activitiOptService.startProcess(processSerialNumber, processDefinitionKey,
                item.getSystemName(), startOrgUnitIdList, vars);
            ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
            processParam.setProcessInstanceId(task.getProcessInstanceId());
            processParam.setStartor(Y9FlowableHolder.getOrgUnitId());
            processParam.setStartorName(Y9FlowableHolder.getOrgUnit().getName());
            processParam.setSended("true");
            // 保存流程信息到ES
            process4SearchService.saveToDataCenter(tenantId, processParam, Y9FlowableHolder.getOrgUnit());
            processParamService.saveOrUpdate(processParam);
            model = new StartProcessResultModel();
            model.setProcessDefinitionId(task.getProcessDefinitionId());
            model.setProcessInstanceId(task.getProcessInstanceId());
            model.setProcessSerialNumber(processSerialNumber);
            model.setTaskId(task.getId());
            model.setTaskDefKey(task.getTaskDefinitionKey());
        } catch (Exception e) {
            LOGGER.error("启动流程失败！异常打印：", e);
        }
        return model;
    }

    @Override
    public Y9Result<Object> submitTo(String processSerialNumber, String taskId) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), userId = Y9FlowableHolder.getOrgUnitId();
            OrgUnit orgUnit = Y9FlowableHolder.getOrgUnit();
            ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
            String itemId = processParam.getItemId();
            TaskModel task = taskApi.findById(tenantId, taskId).getData();
            if (null == task || null == task.getId()) {
                return Y9Result.failure("该件已被处理。");
            }
            String processDefinitionId = task.getProcessDefinitionId(), taskDefKey = task.getTaskDefinitionKey(),
                processInstanceId = task.getProcessInstanceId();
            Y9Result<TargetModel> routeToTaskIdResult =
                parserRouteToTaskId(itemId, processSerialNumber, processDefinitionId, taskDefKey, taskId);
            if (!routeToTaskIdResult.isSuccess()) {
                return Y9Result.failure(routeToTaskIdResult.getMsg());
            }
            String routeToTaskId = routeToTaskIdResult.getData().getTaskDefKey(),
                routeToTaskName = routeToTaskIdResult.getData().getTaskDefName();
            FlowElementModel flowElementModel =
                processDefinitionApi.getNode(tenantId, processDefinitionId, routeToTaskId).getData();
            Y9Result<List<String>> userResult = parserUser(itemId, processDefinitionId, routeToTaskId, routeToTaskName,
                processInstanceId, flowElementModel.getMultiInstance());
            if (!userResult.isSuccess()) {
                return Y9Result.failure(userResult.getMsg());
            }
            List<String> userList = userResult.getData();
            Map<String, Object> variables =
                CommonOpt.setVariables(userId, orgUnit.getName(), routeToTaskId, userList, flowElementModel);
            String subProcessStr =
                variableApi.getVariableByProcessInstanceId(tenantId, processInstanceId, ItemConsts.SUBPROCESSNUM_KEY)
                    .getData();
            if (subProcessStr != null && SysVariables.PARALLEL.equals(flowElementModel.getMultiInstance())) {// 并行节点才执行
                // xxx并行子流程，userChoice只传了一个岗位id,根据subProcessNum，添加同一个岗位id,生成多个并行任务。
                String userChoice = userList.get(0);
                int subProcessNum = Integer.parseInt(subProcessStr);
                if (subProcessNum > 1 && userList.size() == 1) {
                    for (int i = 1; i < subProcessNum; i++) {
                        userList.add(userChoice);
                    }
                }
            }
            variables.put(SysVariables.USERS, userList);
            asyncHandleService.forwarding4Task(processInstanceId, processParam, "true", "", taskId, flowElementModel,
                variables, userList);
            return Y9Result.successMsg("提交成功");
        } catch (Exception e) {
            LOGGER.error("启动流程并提交失败！", e);
        }
        return Y9Result.failure("提交失败！！");
    }

    /**
     * 按钮控制数据封装类
     */
    private static class ButtonControlData {
        String menuName = "";
        String menuKey = "";
        String sendName = "";
        String sendKey = "";
        String repositionName = "";
        String repositionKey = "";
        String taskDefNameJson = "";
        List<Map<String, Object>> sendMap = new ArrayList<>();
        List<Map<String, Object>> menuMap = new ArrayList<>();
        List<Map<String, Object>> repositionMap = new ArrayList<>();
    }

    /**
     * 流程实例数据封装类
     */
    private static class ProcessInstanceData {
        String processSerialNumber;
        String processDefinitionId;
        String taskDefinitionKey;
        String processDefinitionKey;
        String activitiUser;
        String taskId;
        String itemId;
        String processInstanceId;
    }

}
