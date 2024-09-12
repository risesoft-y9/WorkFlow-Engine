package net.risesoft.service.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.customgroup.CustomGroupApi;
import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.platform.permission.PositionResourceApi;
import net.risesoft.api.platform.permission.PositionRoleApi;
import net.risesoft.api.platform.permission.RoleApi;
import net.risesoft.api.processadmin.ConditionParserApi;
import net.risesoft.api.processadmin.HistoricProcessApi;
import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.ProcessTodoApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.api.processadmin.RuntimeApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.api.todo.TodoTaskApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.ErrorLog;
import net.risesoft.entity.ItemButtonBind;
import net.risesoft.entity.ItemPermission;
import net.risesoft.entity.ItemPrintTemplateBind;
import net.risesoft.entity.ItemTaskConf;
import net.risesoft.entity.ProcessParam;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.entity.TaskVariable;
import net.risesoft.entity.Y9FormItemBind;
import net.risesoft.entity.Y9FormItemMobileBind;
import net.risesoft.entity.form.Y9Form;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.enums.ItemButtonTypeEnum;
import net.risesoft.enums.ItemPermissionEnum;
import net.risesoft.enums.platform.AuthorityEnum;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.DocUserChoiseModel;
import net.risesoft.model.itemadmin.ErrorLogModel;
import net.risesoft.model.itemadmin.ItemListModel;
import net.risesoft.model.itemadmin.OpenDataModel;
import net.risesoft.model.itemadmin.SignTaskConfigModel;
import net.risesoft.model.itemadmin.StartProcessResultModel;
import net.risesoft.model.platform.CustomGroup;
import net.risesoft.model.platform.CustomGroupMember;
import net.risesoft.model.platform.Department;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Position;
import net.risesoft.model.platform.Resource;
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
import net.risesoft.repository.form.Y9FormRepository;
import net.risesoft.repository.jpa.ItemTaskConfRepository;
import net.risesoft.repository.jpa.PrintTemplateItemBindRepository;
import net.risesoft.repository.jpa.SpmApproveItemRepository;
import net.risesoft.repository.jpa.TaskVariableRepository;
import net.risesoft.service.AsyncHandleService;
import net.risesoft.service.DocumentService;
import net.risesoft.service.DynamicRoleMemberService;
import net.risesoft.service.ErrorLogService;
import net.risesoft.service.OfficeDoneInfoService;
import net.risesoft.service.Process4SearchService;
import net.risesoft.service.ProcessParamService;
import net.risesoft.service.RoleService;
import net.risesoft.service.SpmApproveItemService;
import net.risesoft.service.config.ItemButtonBindService;
import net.risesoft.service.config.ItemPermissionService;
import net.risesoft.service.config.ItemStartNodeRoleService;
import net.risesoft.service.config.ItemTaskConfService;
import net.risesoft.service.config.Y9FormItemBindService;
import net.risesoft.service.form.Y9FormService;
import net.risesoft.util.ButtonUtil;
import net.risesoft.util.CommonOpt;
import net.risesoft.util.ListUtil;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9Util;

/*
 * @author qinman
 *
 * @author zhangchongjie
 *
 * @date 2022/12/20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final ActivitiOptServiceImpl activitiOptService;

    private final SpmApproveItemService spmApproveitemService;

    private final SpmApproveItemRepository spmApproveitemRepository;

    private final ItemTaskConfService taskConfService;

    private final ItemPermissionService itemPermissionService;

    private final Y9FormItemBindService y9FormItemBindService;

    private final ItemButtonBindService buttonItemBindService;

    private final TodoTaskApi todoTaskApi;

    private final TaskApi taskApi;

    private final CustomGroupApi customGroupApi;

    private final ProcessDefinitionApi processDefinitionApi;

    private final VariableApi variableApi;

    private final OrgUnitApi orgUnitApi;

    private final RepositoryApi repositoryApi;

    private final PositionApi positionApi;

    private final RoleApi roleApi;

    private final PositionRoleApi positionRoleApi;

    private final DepartmentApi departmentApi;

    private final PositionResourceApi positionResourceApi;

    private final HistoricProcessApi historicProcessApi;

    private final HistoricTaskApi historictaskApi;

    private final RuntimeApi runtimeApi;

    private final ProcessParamService processParamService;

    private final ProcessTodoApi processTodoApi;

    private final PrintTemplateItemBindRepository printTemplateItemBindRepository;

    private final OfficeDoneInfoService officeDoneInfoService;

    private final TaskVariableRepository taskVariableRepository;

    private final AsyncHandleService asyncHandleService;

    private final Y9FormRepository y9FormRepository;

    private final Process4SearchService process4SearchService;

    private final ErrorLogService errorLogService;

    private final ItemStartNodeRoleService itemStartNodeRoleService;

    private final ItemTaskConfRepository taskConfRepository;

    private final DynamicRoleMemberService dynamicRoleMemberService;

    private final ConditionParserApi conditionParserApi;

    private final Y9FormService y9FormService;

    private final RoleService roleService;

    @Override
    public OpenDataModel add(String itemId, boolean mobile) {
        String userId = Y9LoginUserHolder.getOrgUnitId(), tenantId = Y9LoginUserHolder.getTenantId();
        OpenDataModel model = new OpenDataModel();
        if (StringUtils.isNotBlank(itemId)) {
            SpmApproveItem item = spmApproveitemService.findById(itemId);
            model.setItemId(itemId);
            model.setProcessDefinitionKey(item.getWorkflowGuid());
            String processDefinitionKey = item.getWorkflowGuid();
            ProcessDefinitionModel pdModel =
                repositoryApi.getLatestProcessDefinitionByKey(tenantId, processDefinitionKey).getData();
            String processDefinitionId = pdModel.getId();
            String taskDefKey = itemStartNodeRoleService.getStartTaskDefKey(itemId);
            model = this.genDocumentModel(itemId, processDefinitionKey, "", taskDefKey, mobile, model);
            model =
                this.menuControl(itemId, processDefinitionId, taskDefKey, "", model, ItemBoxTypeEnum.ADD.getValue());
            model.setProcessDefinitionId(processDefinitionId);
            model.setTaskDefKey(taskDefKey);
            model.setActivitiUser(userId);
            model.setCurrentUser(Y9LoginUserHolder.getOrgUnit().getName());
            model.setItembox(ItemBoxTypeEnum.ADD.getValue());
            model.setProcessSerialNumber(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            model.setProcessInstanceId("");
        }
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
    public void complete(String taskId) throws Exception {
        String tenantId = Y9LoginUserHolder.getTenantId();
        TaskModel task = taskApi.findById(tenantId, taskId).getData();
        String processInstanceId = task.getProcessInstanceId();
        /*
         * 1办结流程
         */
        runtimeApi.complete(tenantId, Y9LoginUserHolder.getOrgUnitId(), processInstanceId, taskId);
    }

    @Override
    public DocUserChoiseModel docUserChoise(String itemId, String processDefinitionKey, String processDefinitionId,
        String taskId, String taskDefKey, String processInstanceId) {
        DocUserChoiseModel model = new DocUserChoiseModel();
        String tenantId = Y9LoginUserHolder.getTenantId();
        String multiInstance = processDefinitionApi.getNodeType(tenantId, processDefinitionId, taskDefKey).getData();
        Map<String, Object> tabMap =
            itemPermissionService.getTabMap(itemId, processDefinitionId, taskDefKey, processInstanceId);

        model.setExistDepartment((Boolean)tabMap.get("existDepartment"));
        model.setExistPosition((Boolean)tabMap.get("existPosition"));

        Y9Page<CustomGroup> pagelist =
            customGroupApi.pageCustomGroupByPersonId(tenantId, Y9LoginUserHolder.getPersonId(), new Y9PageQuery(1, 1));
        model.setExistCustomGroup(pagelist != null && pagelist.getTotal() > 0);
        model.setMultiInstance(multiInstance);
        model.setProcessDefinitionId(processDefinitionId);
        model.setTenantId(tenantId);
        model.setItemId(itemId);
        model.setRouteToTask(taskDefKey);

        boolean sponsorStatus = taskConfService.getSponserStatus(itemId, processDefinitionId, taskDefKey);
        model.setSponsorStatus(sponsorStatus);
        return model;
    }

    @Override
    public OpenDataModel edit(String itembox, String taskId, String processInstanceId, String itemId, boolean mobile) {
        OpenDataModel model = new OpenDataModel();
        String processSerialNumber = "", processDefinitionId = "", taskDefinitionKey = "", processDefinitionKey = "",
            activitiUser = "";
        String itemboxStr = itembox;
        String startor;
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (ItemBoxTypeEnum.MONITORDOING.getValue().equals(itembox)) {
            itembox = ItemBoxTypeEnum.DOING.getValue();
        }
        model.setMeeting(false);
        ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
        startor = processParam.getStartor();
        if (itembox.equalsIgnoreCase(ItemBoxTypeEnum.TODO.getValue())) {
            TaskModel task = taskApi.findById(tenantId, taskId).getData();
            processInstanceId = task.getProcessInstanceId();
            processSerialNumber = processParam.getProcessSerialNumber();
            processDefinitionId = task.getProcessDefinitionId();
            taskDefinitionKey = task.getTaskDefinitionKey();
            processDefinitionKey = processDefinitionId.split(SysVariables.COLON)[0];
            activitiUser = task.getAssignee();
            if (StringUtils.isBlank(itemId)) {
                itemId = processParam.getItemId();
            }
            /*
             * 设为已读
             */
            if (StringUtils.isBlank(task.getFormKey())) {
                task.setFormKey("0");
                taskApi.saveTask(tenantId, task);
                try {
                    todoTaskApi.setIsNewTodo(tenantId, taskId, "0");
                } catch (Exception e) {
                    LOGGER.error("setIsNewTodo error", e);
                }
            }
            // 获取第一节点任务key,可能多个
            String startTaskDefKey = "";
            String startNode =
                processDefinitionApi.getStartNodeKeyByProcessDefinitionId(tenantId, processDefinitionId).getData();
            List<TargetModel> nodeList =
                processDefinitionApi.getTargetNodes(tenantId, processDefinitionId, startNode).getData();
            for (TargetModel map : nodeList) {
                startTaskDefKey = Y9Util.genCustomStr(startTaskDefKey, map.getTaskDefKey());
            }
            model.setStartTaskDefKey(startTaskDefKey);
            OfficeDoneInfo officeDoneInfo = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
            model.setMeeting(officeDoneInfo.getMeeting() != null && officeDoneInfo.getMeeting().equals("1"));
        } else if (itembox.equalsIgnoreCase(ItemBoxTypeEnum.DOING.getValue())
            || itembox.equalsIgnoreCase(ItemBoxTypeEnum.DONE.getValue())) {
            HistoricProcessInstanceModel hpi = historicProcessApi.getById(tenantId, processInstanceId).getData();
            OfficeDoneInfo officeDoneInfo = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
            if (hpi == null) {
                if (officeDoneInfo == null) {
                    String year = processParam.getCreateTime().substring(0, 4);
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
            assert officeDoneInfo != null;
            model.setMeeting(officeDoneInfo.getMeeting() != null && officeDoneInfo.getMeeting().equals("1"));
            processSerialNumber = processParam.getProcessSerialNumber();
            if (StringUtils.isNotEmpty(taskId)) {
                if (taskId.contains(SysVariables.COMMA)) {
                    taskId = taskId.split(SysVariables.COMMA)[0];
                }
                TaskModel taskTemp = taskApi.findById(tenantId, taskId).getData();
                taskDefinitionKey = taskTemp.getTaskDefinitionKey();
            }
        }
        model.setTitle(processParam.getTitle());
        model.setStartor(startor);
        model.setItembox(itembox);
        model.setCurrentUser(Y9LoginUserHolder.getOrgUnit().getName());
        model.setProcessSerialNumber(processSerialNumber);
        model.setProcessDefinitionKey(processDefinitionKey);
        model.setProcessDefinitionId(processDefinitionId);
        model.setProcessInstanceId(processInstanceId);
        model.setTaskDefKey(taskDefinitionKey);
        model.setTaskId(taskId);
        model.setActivitiUser(activitiUser);
        model.setItemId(itemId);

        model =
            this.genDocumentModel(itemId, processDefinitionKey, processDefinitionId, taskDefinitionKey, mobile, model);
        model = this.menuControl(itemId, processDefinitionId, taskDefinitionKey, taskId, model, itemboxStr);
        return model;
    }

    /*
     * Description:
     *
     * @param taskId
     * @param sponsorHandle
     * @param userChoice
     * @param routeToTaskId
     * @param sponsorGuid
     * @return
     */
    @Override
    public Y9Result<String> forwarding(String taskId, String sponsorHandle, String userChoice, String routeToTaskId,
        String sponsorGuid) {
        String processInstanceId = "";
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), orgUnitId = Y9LoginUserHolder.getOrgUnitId();
            TaskModel task = taskApi.findById(tenantId, taskId).getData();
            processInstanceId = task.getProcessInstanceId();
            ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
            List<String> userList = new ArrayList<>(this.parseUserChoice(userChoice));
            int num = userList.size();
            boolean tooMuch = num > 100;
            if (tooMuch) {
                return Y9Result.failure("发送人数过多");
            }
            OrgUnit orgUnit = Y9LoginUserHolder.getOrgUnit();
            // 得到要发送节点的multiInstance，PARALLEL表示并行，SEQUENTIAL表示串行
            String multiInstance =
                processDefinitionApi.getNodeType(tenantId, task.getProcessDefinitionId(), routeToTaskId).getData();
            Map<String, Object> variables =
                CommonOpt.setVariables(orgUnitId, orgUnit.getName(), routeToTaskId, userList, multiInstance);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            /*
             * 并行发送超过20人时，启用异步后台处理。
             */
            tooMuch = num > 20;
            if (SysVariables.PARALLEL.equals(multiInstance) && tooMuch) {
                TaskVariable taskVariable = taskVariableRepository.findByTaskIdAndKeyName(taskId, "isForwarding");
                Date date = new Date();
                if (taskVariable == null) {
                    taskVariable = new TaskVariable();
                    taskVariable.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                    taskVariable.setProcessInstanceId(processInstanceId);
                    taskVariable.setTaskId(taskId);
                    taskVariable.setKeyName("isForwarding");
                    taskVariable.setCreateTime(sdf.format(date));
                }
                taskVariable.setUpdateTime(sdf.format(date));
                taskVariable.setText("true:" + num);
                taskVariableRepository.save(taskVariable);
                asyncHandleService.forwarding(tenantId, orgUnit, processInstanceId, processParam, sponsorHandle,
                    sponsorGuid, taskId, multiInstance, variables, userList);
            } else if (SysVariables.SUBPROCESS.equals(multiInstance)) {
                Map<String, Object> vars = new HashMap<>(16);
                vars.put("parentTaskId", taskId);
                taskApi.createWithVariables(tenantId, orgUnitId, routeToTaskId, vars, userList);
            } else {
                // 判断是否是主办办理，如果是，需要将协办未办理的的任务默认办理
                if (StringUtils.isNotBlank(sponsorHandle) && UtilConsts.TRUE.equals(sponsorHandle)) {
                    List<TaskModel> taskNextList1 =
                        taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    /*
                     * 如果协办人数超过10人，启用异步后台处理。
                     */
                    tooMuch = taskNextList1.size() > 10;
                    if (tooMuch) {
                        TaskVariable taskVariable =
                            taskVariableRepository.findByTaskIdAndKeyName(taskId, "isForwarding");
                        Date date = new Date();
                        if (taskVariable == null) {
                            taskVariable = new TaskVariable();
                            taskVariable.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                            taskVariable.setProcessInstanceId(processInstanceId);
                            taskVariable.setTaskId(taskId);
                            taskVariable.setKeyName("isForwarding");
                            taskVariable.setCreateTime(sdf.format(date));
                        }
                        taskVariable.setUpdateTime(sdf.format(date));
                        taskVariable.setText("true:" + num);
                        taskVariableRepository.save(taskVariable);
                        asyncHandleService.forwarding(tenantId, orgUnit, processInstanceId, processParam, sponsorHandle,
                            sponsorGuid, taskId, multiInstance, variables, userList);
                    } else {
                        asyncHandleService.forwarding4Task(processInstanceId, processParam, sponsorHandle, sponsorGuid,
                            taskId, multiInstance, variables, userList);
                    }
                } else {
                    asyncHandleService.forwarding4Task(processInstanceId, processParam, sponsorHandle, sponsorGuid,
                        taskId, multiInstance, variables, userList);
                }
            }
            return Y9Result.success(processInstanceId, "发送成功!");
        } catch (Exception e) {
            LOGGER.error("公文发送失败！");
            try {
                final Writer result = new StringWriter();
                final PrintWriter print = new PrintWriter(result);
                e.printStackTrace(print);
                String msg = result.toString();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = sdf.format(new Date());
                // 保存任务发送错误日志
                ErrorLog errorLog = new ErrorLog();
                errorLog.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                errorLog.setCreateTime(time);
                errorLog.setErrorFlag(ErrorLogModel.ERROR_FLAG_FORWRDING);
                errorLog.setErrorType(ErrorLogModel.ERROR_TASK);
                errorLog.setExtendField("发送少数人失败");
                errorLog.setProcessInstanceId(processInstanceId);
                errorLog.setTaskId(taskId);
                errorLog.setText(msg);
                errorLog.setUpdateTime(time);
                errorLogService.saveErrorLog(errorLog);
            } catch (Exception e2) {
                LOGGER.error("保存任务发送错误日志失败！", e2);
            }
        }
        return Y9Result.failure("发送失败!");
    }

    @Override
    public OpenDataModel genDocumentModel(String itemId, String processDefinitionKey, String processDefinitionId,
        String taskDefinitionKey, boolean mobile, OpenDataModel model) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (StringUtils.isBlank(processDefinitionId)) {
            processDefinitionId =
                repositoryApi.getLatestProcessDefinitionByKey(tenantId, processDefinitionKey).getData().getId();
        }
        // Y9表单
        String formIds = "";
        String showOtherFlag = "";
        String formNames = "";
        if (mobile) {
            List<Y9FormItemMobileBind> eformTaskBinds = y9FormItemBindService
                .listByItemIdAndProcDefIdAndTaskDefKey4Mobile(itemId, processDefinitionId, taskDefinitionKey);
            model.setFormId("");
            model.setFormName("");
            model.setFormJson("");
            if (!eformTaskBinds.isEmpty()) {
                Y9FormItemMobileBind eftb = eformTaskBinds.get(0);
                model.setFormId(eftb.getFormId());
                String formName = eftb.getFormName();
                boolean b = formName.contains("(");
                if (b) {
                    formName = formName.substring(0, formName.indexOf("("));
                }
                model.setFormName(formName);
                Y9Form y9Form = y9FormRepository.findById(eftb.getFormId()).orElse(null);
                assert y9Form != null;
                model.setFormJson(y9Form.getFormJson());
            }
            return model;
        }
        List<Y9FormItemBind> eformTaskBinds =
            y9FormItemBindService.listByItemIdAndProcDefIdAndTaskDefKey(itemId, processDefinitionId, taskDefinitionKey);
        List<Map<String, String>> list = new ArrayList<>();
        if (!eformTaskBinds.isEmpty()) {
            for (Y9FormItemBind eftb : eformTaskBinds) {
                formIds = Y9Util.genCustomStr(formIds, eftb.getFormId());
                String formName = eftb.getFormName();
                if (formName.contains("(")) {
                    formName = formName.substring(0, formName.indexOf("("));
                }
                formNames = Y9Util.genCustomStr(formNames, formName);
                Map<String, String> map = new HashMap<>(16);
                map.put("formId", eftb.getFormId());
                map.put("formName", formName);
                list.add(map);
            }
            showOtherFlag = y9FormItemBindService.getShowOther(eformTaskBinds);
        }
        model.setFormList(list);
        model.setFormIds(formIds);
        model.setFormNames(formNames);
        model.setShowOtherFlag(showOtherFlag);
        // 获取打印表单
        String printFormId = "";
        String printFormType = "";
        ItemPrintTemplateBind bind = printTemplateItemBindRepository.findByItemId(itemId);
        if (bind != null) {
            printFormId = bind.getTemplateId();
            printFormType = bind.getTemplateType();
        }
        model.setPrintFormId(printFormId);
        model.setPrintFormType(printFormType);
        return model;
    }

    private void getAllPosition(List<Position> list, String deptId) {
        List<Department> deptList = departmentApi.listByParentId(Y9LoginUserHolder.getTenantId(), deptId).getData();
        List<Position> list0 = positionApi.listByParentId(Y9LoginUserHolder.getTenantId(), deptId).getData();
        if (!list0.isEmpty()) {
            list.addAll(list0);
        }
        for (Department dept : deptList) {
            this.getAllPosition(list, dept.getId());
        }
    }

    @Override
    public String getFirstItem() {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), userId = Y9LoginUserHolder.getOrgUnitId();
            String resourceId = "";
            List<Resource> list =
                positionResourceApi.listSubResources(tenantId, userId, AuthorityEnum.BROWSE, resourceId).getData();
            String url;
            for (Resource r : list) {
                url = r.getUrl();
                if (StringUtils.isBlank(url)) {
                    continue;
                }
                if (!url.contains("itemId=")) {
                    continue;
                }
                return url.split("itemId=")[1];
            }
        } catch (Exception e) {
            LOGGER.error("获取第一个任务失败！", e);
        }
        return "";
    }

    @Override
    public String getFormIdByItemId(String itemId, String processDefinitionKey) {
        String formIds = "";
        String processDefinitionId = repositoryApi
            .getLatestProcessDefinitionByKey(Y9LoginUserHolder.getTenantId(), processDefinitionKey).getData().getId();
        List<Y9FormItemBind> eformTaskBinds =
            y9FormItemBindService.listByItemIdAndProcDefIdAndTaskDefKey(itemId, processDefinitionId, "");
        if (!eformTaskBinds.isEmpty()) {
            for (Y9FormItemBind eftb : eformTaskBinds) {
                formIds = Y9Util.genCustomStr(formIds, eftb.getFormId());
            }
        }
        return formIds;
    }

    public List<OrgUnit> getUserChoice(String itemId, String processDefinitionId, String taskDefinitionKey,
        String processSerialNumber) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<ItemPermission> list = itemPermissionService.listByItemIdAndProcessDefinitionIdAndTaskDefKeyExtra(itemId,
            processDefinitionId, taskDefinitionKey);
        List<OrgUnit> orgUnitList = new ArrayList<>();
        for (ItemPermission o : list) {
            if (Objects.equals(o.getRoleType(), ItemPermissionEnum.DEPARTMENT.getValue())
                || Objects.equals(o.getRoleType(), ItemPermissionEnum.POSITION.getValue())
                || Objects.equals(o.getRoleType(), ItemPermissionEnum.USER.getValue())) {
                OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, o.getRoleId()).getData();
                if (null != orgUnit) {
                    orgUnitList.add(orgUnit);
                }
            } else if (Objects.equals(o.getRoleType(), ItemPermissionEnum.ROLE.getValue())) {
                List<OrgUnit> deptList =
                    roleApi.listOrgUnitsById(tenantId, o.getRoleId(), OrgTypeEnum.DEPARTMENT).getData();
                List<OrgUnit> personList =
                    roleApi.listOrgUnitsById(tenantId, o.getRoleId(), OrgTypeEnum.POSITION).getData();
                orgUnitList.addAll(deptList);
                orgUnitList.addAll(personList);
            } else if (Objects.equals(o.getRoleType(), ItemPermissionEnum.DYNAMICROLE.getValue())) {
                List<OrgUnit> ouList = dynamicRoleMemberService.listByDynamicRoleIdAndProcessInstanceId(o.getRoleId(),
                    processSerialNumber);
                for (OrgUnit orgUnit : ouList) {
                    if (orgUnit.getOrgType().equals(OrgTypeEnum.POSITION)) {
                        OrgUnit user = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnit.getId()).getData();
                        if (user != null && !user.getDisabled()) {
                            orgUnitList.add(user);
                        }
                    } else {
                        orgUnitList.add(orgUnit);
                    }
                }
            }
        }
        return orgUnitList;
    }

    @Override
    public List<ItemListModel> listItems() {
        List<ItemListModel> listMap = new ArrayList<>();
        try {
            String userId = Y9LoginUserHolder.getOrgUnitId();
            String tenantId = Y9LoginUserHolder.getTenantId();
            String resourceId = "";
            //////////////////////////////////
            List<Resource> list =
                positionResourceApi.listSubResources(tenantId, userId, AuthorityEnum.BROWSE, resourceId).getData();
            ItemListModel model;
            String url;
            long todoCount;
            for (Resource r : list) {
                model = new ItemListModel();
                url = r.getUrl();
                if (StringUtils.isBlank(url)) {
                    continue;
                }
                if (!url.contains("itemId=")) {
                    continue;
                }
                String itemId = url.split("itemId=")[1];
                model.setId(r.getId());
                model.setUrl(itemId);
                model.setItemId(itemId);
                model.setAppIcon("");
                model.setTodoCount(0);
                SpmApproveItem spmApproveitem = spmApproveitemRepository.findById(itemId).orElse(null);
                model.setName(r.getName());
                model.setItemName(r.getName());
                if (spmApproveitem != null && spmApproveitem.getId() != null) {
                    model.setName(spmApproveitem.getName());
                    model.setItemName(spmApproveitem.getName());
                    todoCount = processTodoApi
                        .getTodoCountByUserIdAndProcessDefinitionKey(tenantId, userId, spmApproveitem.getWorkflowGuid())
                        .getData();
                    model.setTodoCount((int)todoCount);
                    model.setAppIcon(
                        StringUtils.isBlank(spmApproveitem.getIconData()) ? "" : spmApproveitem.getIconData());
                    model.setProcessDefinitionKey(spmApproveitem.getWorkflowGuid());
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
            String tenantId = Y9LoginUserHolder.getTenantId(), userId = Y9LoginUserHolder.getOrgUnitId();
            String resourceId = "";
            List<Resource> list =
                positionResourceApi.listSubResources(tenantId, userId, AuthorityEnum.BROWSE, resourceId).getData();
            ItemListModel model;
            String url;
            for (Resource r : list) {
                model = new ItemListModel();
                url = r.getUrl();
                if (StringUtils.isBlank(url)) {
                    continue;
                }
                if (!url.contains("itemId=")) {
                    continue;
                }
                String itemId = url.split("itemId=")[1];
                model.setId(r.getId());
                model.setItemId(itemId);
                model.setAppIcon("");
                SpmApproveItem spmApproveitem = spmApproveitemRepository.findById(itemId).orElse(null);
                model.setName(r.getName());
                model.setItemName(r.getName());
                if (spmApproveitem != null && spmApproveitem.getId() != null) {
                    model.setName(spmApproveitem.getName());
                    model.setItemName(spmApproveitem.getName());
                    model.setAppIcon(
                        StringUtils.isBlank(spmApproveitem.getIconData()) ? "" : spmApproveitem.getIconData());
                    model.setProcessDefinitionKey(spmApproveitem.getWorkflowGuid());
                    listMap.add(model);
                }
            }
        } catch (Exception e) {
            LOGGER.error("获取我的任务列表失败！", e);
        }
        return listMap;
    }

    @Override
    public OpenDataModel menuControl(String itemId, String processDefinitionId, String taskDefKey, String taskId,
        OpenDataModel model, String itembox) {
        ButtonUtil buttonUtil = new ButtonUtil();
        String tenantId = Y9LoginUserHolder.getTenantId(), orgUnitId = Y9LoginUserHolder.getOrgUnitId();
        Map<String, Object> map = buttonUtil.showButton(itemId, taskId, itembox);
        String[] buttonIds = (String[])map.get("buttonIds");
        String[] buttonNames = (String[])map.get("buttonNames");
        String sponsorHandle = (String)map.get("sponsorHandle");
        int[] buttonOrders = (int[])map.get("buttonOrders");
        boolean[] isButtonShow = (boolean[])map.get("isButtonShow");
        String menuName = "";
        String menuKey = "";
        String sendName = "";
        String sendKey = "";
        String repositionName = "";
        String repositionKey = "";
        List<Map<String, Object>> sendMap = new ArrayList<>();
        List<Map<String, Object>> menuMap = new ArrayList<>();
        List<Map<String, Object>> repositionMap = new ArrayList<>();
        List<ItemButtonBind> bibList;
        // 生成按钮数组
        for (int i = buttonOrders.length - 1; i >= 0; i--) {
            int k = buttonOrders[i] - 1;
            /*
             * 如果显示保存按钮，那么说明是待办，把自定义普通按钮加在保存按钮的前面
             */
            if (k == 0 && isButtonShow[0]) {
                bibList = buttonItemBindService.listContainRoleId(itemId, ItemButtonTypeEnum.COMMON.getValue(),
                    processDefinitionId, taskDefKey);
                for (ItemButtonBind bind : bibList) {
                    String buttonName = bind.getButtonName(), buttonCustomId = bind.getButtonCustomId();
                    if (!"发送".equals(buttonName)) {
                        List<String> roleIds = bind.getRoleIds();
                        if (roleIds.isEmpty()) {
                            Map<String, Object> mapTemp = new HashMap<>(16);
                            mapTemp.put("menuName", buttonName);
                            mapTemp.put("menuKey", buttonCustomId);
                            menuName = Y9Util.genCustomStr(menuName, buttonName);
                            menuKey = Y9Util.genCustomStr(menuKey, buttonCustomId);
                            menuMap.add(mapTemp);
                        } else {
                            for (String roleId : roleIds) {
                                boolean hasRole = positionRoleApi.hasRole(tenantId, roleId, orgUnitId).getData();
                                if (hasRole) {
                                    Map<String, Object> mapTemp = new HashMap<>(16);
                                    mapTemp.put("menuName", buttonName);
                                    mapTemp.put("menuKey", buttonCustomId);
                                    menuName = Y9Util.genCustomStr(menuName, buttonName);
                                    menuKey = Y9Util.genCustomStr(menuKey, buttonCustomId);
                                    menuMap.add(mapTemp);
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            /*
             * 假如发送按钮显示的话，去获取发送下面的路由
             */
            if (k == 1 && isButtonShow[1] && StringUtils.isNotBlank(taskDefKey)) {

                /*
                 * 假如有自定义“发送”按钮的话,就不显示默认的发送按钮
                 */
                boolean haveSendButton = false;
                bibList = buttonItemBindService.listContainRoleId(itemId, ItemButtonTypeEnum.COMMON.getValue(),
                    processDefinitionId, taskDefKey);
                bibFor:
                for (ItemButtonBind bib : bibList) {
                    if ("发送".equals(bib.getButtonName())) {
                        List<String> roleIds = bib.getRoleIds();
                        if (roleIds.isEmpty()) {
                            Map<String, Object> mapTemp = new HashMap<>(16);
                            mapTemp.put("menuName", bib.getButtonName());
                            mapTemp.put("menuKey", bib.getButtonCustomId());
                            menuName = Y9Util.genCustomStr(menuName, bib.getButtonName());
                            menuKey = Y9Util.genCustomStr(menuKey, bib.getButtonCustomId());
                            menuMap.add(mapTemp);

                            haveSendButton = true;
                            break;
                        } else {
                            for (String roleId : roleIds) {
                                boolean hasrole = positionRoleApi.hasRole(tenantId, roleId, orgUnitId).getData();
                                if (hasrole) {
                                    Map<String, Object> mapTemp = new HashMap<>(16);
                                    mapTemp.put("menuName", bib.getButtonName());
                                    mapTemp.put("menuKey", bib.getButtonCustomId());
                                    menuName = Y9Util.genCustomStr(menuName, bib.getButtonName());
                                    menuKey = Y9Util.genCustomStr(menuKey, bib.getButtonCustomId());
                                    menuMap.add(mapTemp);

                                    haveSendButton = true;
                                    break bibFor;
                                }
                            }
                        }
                    }
                }
                if (!haveSendButton) {
                    /*
                     * 没有配置自定义“发送”按钮的话，添加上默认的“发送”按钮
                     */
                    Map<String, Object> map1 = new HashMap<>(16);
                    map1.put("menuName", buttonNames[k]);
                    map1.put("menuKey", buttonIds[k]);
                    menuName = Y9Util.genCustomStr(menuName, buttonNames[k]);
                    menuKey = Y9Util.genCustomStr(menuKey, buttonIds[k]);
                    menuMap.add(map1);
                    /*
                     * 添加发送下面的路由
                     */
                    List<TargetModel> routeToTasks =
                        processDefinitionApi.getTargetNodes(tenantId, processDefinitionId, taskDefKey).getData();
                    for (TargetModel m : routeToTasks) {
                        Map<String, Object> map2 = new HashMap<>(16);
                        // 退回、路由网关不显示在发送下面
                        if (!"退回".equals(m.getTaskDefName()) && !"Exclusive Gateway".equals(m.getTaskDefName())) {
                            sendName = Y9Util.genCustomStr(sendName, m.getTaskDefName());
                            sendKey = Y9Util.genCustomStr(sendKey, m.getTaskDefKey());
                            map2.put("sendName", m.getTaskDefName());
                            map2.put("sendKey", m.getTaskDefKey());
                            sendMap.add(map2);
                        }
                    }
                    /*
                     * 添加自定义按钮到发送
                     */
                    bibList = buttonItemBindService.listContainRoleId(itemId, ItemButtonTypeEnum.SEND.getValue(),
                        processDefinitionId, taskDefKey);
                    for (ItemButtonBind bind : bibList) {
                        List<String> roleIds = bind.getRoleIds();
                        String buttonName = bind.getButtonName(), buttonCustomId = bind.getButtonCustomId();
                        if (roleIds.isEmpty()) {
                            Map<String, Object> mapTemp = new HashMap<>(16);
                            sendName = Y9Util.genCustomStr(sendName, buttonName);
                            sendKey = Y9Util.genCustomStr(sendKey, buttonCustomId);
                            mapTemp.put("sendName", buttonName);
                            mapTemp.put("sendKey", buttonCustomId);
                            sendMap.add(mapTemp);
                        } else {
                            for (String roleId : roleIds) {
                                boolean hasrole = positionRoleApi.hasRole(tenantId, roleId, orgUnitId).getData();
                                if (hasrole) {
                                    Map<String, Object> mapTemp = new HashMap<>(16);
                                    sendName = Y9Util.genCustomStr(sendName, buttonName);
                                    sendKey = Y9Util.genCustomStr(sendKey, buttonCustomId);
                                    mapTemp.put("sendName", buttonName);
                                    mapTemp.put("sendKey", buttonCustomId);
                                    sendMap.add(mapTemp);
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            /*
             * 假如重定向按钮显示的话，去获取路由
             */
            String taskDefNameJson;
            if (k == 15 && isButtonShow[15]) {
                List<TargetModel> taskNodes =
                    processDefinitionApi.getNodes(tenantId, processDefinitionId, false).getData();
                for (TargetModel node : taskNodes) {
                    Map<String, Object> map3 = new HashMap<>(16);
                    // 流程不显示在重定向按钮下面
                    if (!"流程".equals(node.getTaskDefName())) {
                        repositionName = Y9Util.genCustomStr(repositionName, node.getTaskDefName());
                        repositionKey = Y9Util.genCustomStr(repositionKey, node.getTaskDefKey());
                        map3.put("repositionName", node.getTaskDefName());
                        map3.put("repositionKey", node.getTaskDefKey());
                        repositionMap.add(map3);
                    }
                }
                model.setRepositionMap(repositionMap);
                ObjectMapper mapper = new ObjectMapper();
                try {
                    taskDefNameJson = mapper.writeValueAsString(repositionMap);
                } catch (JsonProcessingException e) {
                    LOGGER.error("解析重定向按钮失败！", e);
                    taskDefNameJson = "[]";
                }
                model.setTaskDefNameJson(taskDefNameJson);
            }

            if (k != 1 && isButtonShow[k]) {
                Map<String, Object> map1 = new HashMap<>(16);
                map1.put("menuName", buttonNames[k]);
                map1.put("menuKey", buttonIds[k]);
                menuName = Y9Util.genCustomStr(menuName, buttonNames[k]);
                menuKey = Y9Util.genCustomStr(menuKey, buttonIds[k]);
                menuMap.add(map1);
            }
        }
        model.setSendMap(sendMap);
        model.setMenuMap(menuMap);
        model.setSendName(sendName);
        model.setSendKey(sendKey);
        model.setMenuName(menuName);
        model.setMenuKey(menuKey);

        model.setSponsorHandle(sponsorHandle);
        model.setLastPerson4RefuseClaim(
            map.get("isLastPerson4RefuseClaim") != null ? (Boolean)map.get("isLastPerson4RefuseClaim") : false);
        model.setMultiInstance(map.get("multiInstance") != null ? (String)map.get("multiInstance") : "");
        model.setNextNode(map.get("nextNode") != null ? (Boolean)map.get("nextNode") : false);
        return model;
    }

    @Override
    public List<String> parseUserChoice(String userChoice) {
        String users = "";
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (StringUtils.isNotBlank(userChoice)) {
            String[] userChoices = userChoice.split(SysVariables.SEMICOLON);
            for (String s : userChoices) {
                String[] s2 = s.split(SysVariables.COLON);
                int principalType = Integer.parseInt(s2[0]);
                String userIdTemp = s2[1];
                if (principalType == ItemPermissionEnum.POSITION.getValue()) {
                    OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, s2[1]).getData();
                    if (null == orgUnit) {
                        continue;
                    }
                    users = this.addUserId(users, userIdTemp);
                } else if (principalType == ItemPermissionEnum.DEPARTMENT.getValue()) {
                    List<Position> employeeList = new ArrayList<>();
                    this.getAllPosition(employeeList, s2[1]);
                    for (Position pTemp : employeeList) {
                        users = this.addUserId(users, pTemp.getId());
                    }
                } else if (principalType == ItemPermissionEnum.CUSTOMGROUP.getValue()) {
                    List<CustomGroupMember> list = customGroupApi.listCustomGroupMemberByGroupIdAndMemberType(tenantId,
                        Y9LoginUserHolder.getPersonId(), s2[1], OrgTypeEnum.POSITION).getData();
                    for (CustomGroupMember pTemp : list) {
                        OrgUnit orgUnit =
                            orgUnitApi.getOrgUnitPersonOrPosition(tenantId, pTemp.getMemberId()).getData();
                        if (orgUnit != null && StringUtils.isNotBlank(orgUnit.getId())) {
                            users = this.addUserId(users, orgUnit.getId());
                        }
                    }
                }
            }
        }
        List<String> result = Y9Util.stringToList(users, SysVariables.SEMICOLON);
        ListUtil.removeDuplicateWithOrder(result);
        return result;
    }

    public Y9Result<TargetModel> parserRouteToTaskId(String itemId, String processSerialNumber,
        String processDefinitionId, String taskDefKey, String taskId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Y9Result<TargetModel> result = Y9Result.failure("解析目标路由失败");
        try {
            List<TargetModel> targetNodes =
                processDefinitionApi.getTargetNodes(tenantId, processDefinitionId, taskDefKey).getData();
            if (targetNodes.isEmpty()) {
                return Y9Result.failure("目标路由不存在");
            }
            if (1 == targetNodes.size()) {
                result.setData(targetNodes.get(0));
                result.setSuccess(true);
                return result;
            }
            List<Y9FormItemBind> eformTaskBinds =
                y9FormItemBindService.listByItemIdAndProcDefIdAndTaskDefKey(itemId, processDefinitionId, taskDefKey);
            Map<String, Object> variables =
                y9FormService.getFormData4Var(eformTaskBinds.get(0).getFormId(), processSerialNumber);
            List<TargetModel> targetNodesTemp = new ArrayList<>();
            for (TargetModel targetNode : targetNodes) {
                for (String columnName : variables.keySet()) {
                    String str = StringUtils.replace(variables.get(columnName).toString(), ".", "");
                    // 是数值
                    if (StringUtils.isNumeric(str)) {
                        if (variables.get(columnName).toString().contains(".")) {
                            LOGGER.info("*************************Double:" + variables.get(columnName).toString());
                            variables.put(columnName, Double.valueOf(variables.get(columnName).toString()));
                        } else {
                            LOGGER.info("*************************Integer:" + variables.get(columnName).toString());
                            variables.put(columnName, Integer.parseInt(variables.get(columnName).toString()));
                        }
                    }
                }
                LOGGER.info("*************************Y9JsonUtil:" + Y9JsonUtil.writeValueAsString(variables));
                boolean b =
                    conditionParserApi.parser(tenantId, targetNode.getConditionExpression(), variables).getData();
                if (b) {
                    targetNodesTemp.add(targetNode);
                }
            }
            if (targetNodesTemp.isEmpty()) {
                result.setMsg("未找到符合要求的目标路由");
                return result;
            }
            if (targetNodesTemp.size() > 1) {
                result.setMsg("符合要求的目标路由过多");
                return result;
            }
            if (StringUtils.isNotBlank(taskId)) {
                variableApi.setVariables(tenantId, taskId, variables);
            }
            result.setData(targetNodesTemp.get(0));
            result.setMsg("解析目标路由成功");
            result.setSuccess(true);
        } catch (Exception e) {
            LOGGER.error("解析目标路由失败！", e);
        }
        return result;
    }

    @Override
    public Y9Result<List<String>> parserUser(String itemId, String processDefinitionId, String routeToTaskId,
        String routeToTaskName, String processInstanceId, String multiInstance) {
        Y9Result<List<String>> result = Y9Result.failure("解析人员失败");
        List<OrgUnit> orgUnitList =
            roleService.listPermUser4SUbmitTo(itemId, processDefinitionId, routeToTaskId, processInstanceId);
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
    public Y9Result<String> reposition(String taskId, String userChoice) {
        try {
            // 在一个事务中保存。taskId为空则创建新流程。
            String tenantId = Y9LoginUserHolder.getTenantId();
            TaskModel task = taskApi.findById(tenantId, taskId).getData();
            String processInstanceId = task.getProcessInstanceId();
            List<String> userAndDeptIdList = new ArrayList<>();
            userChoice = userChoice.substring(2);
            userAndDeptIdList.add(userChoice);
            // 得到要发送节点的multiInstance，PARALLEL表示并行，SEQUENTIAL表示串行
            String multiInstance = processDefinitionApi
                .getNodeType(tenantId, task.getProcessDefinitionId(), task.getTaskDefinitionKey()).getData();
            Map<String, Object> variables = new HashMap<>(16);
            variables.put(SysVariables.USER, userChoice);
            if (SysVariables.PARALLEL.equals(multiInstance)) {
                List<TaskModel> taskNextList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                // 查找未打开过的件，将未打开过的件重定位
                for (TaskModel taskNext : taskNextList) {
                    if (StringUtils.isBlank(taskNext.getFormKey()) || taskNext.getFormKey().equals("1")) {
                        taskId = taskNext.getId();
                        break;
                    }
                }
                for (TaskModel taskNext : taskNextList) {
                    if (!(taskId.equals(taskNext.getId()))) {
                        // 如果任务办理人未打开过文件，重定位时，给该任务加个任务变量，让历程无该办件人记录
                        if (StringUtils.isBlank(taskNext.getFormKey()) || taskNext.getFormKey().equals("1")) {
                            Map<String, Object> val = new HashMap<>();
                            val.put("val", SysVariables.REPOSITION);
                            variableApi.setVariableLocal(tenantId, taskNext.getId(), SysVariables.REPOSITION, val);
                            taskApi.complete(tenantId, taskNext.getId());
                            continue;
                        }
                        // 如果打开过文件，重定位时，办结办件人的任务，让历程有该办件人记录
                        taskApi.complete(tenantId, taskNext.getId());
                    }
                }
                TaskModel task1 = taskApi.findById(tenantId, taskId).getData();

                task1.setAssignee(userChoice);
                taskApi.saveTask(tenantId, task1);
                // 重定位后设置新的主办人
                Map<String, Object> val = new HashMap<>();
                val.put("val", userChoice.split(SysVariables.COLON)[0]);
                variableApi.setVariableLocal(tenantId, task1.getId(), SysVariables.PARALLELSPONSOR, val);
            } else if (SysVariables.SEQUENTIAL.equals(multiInstance)) {
                // 串行
                task.setAssignee(userChoice);
                taskApi.saveTask(tenantId, task);
            } else {// 其他
                task.setAssignee(userChoice);
                taskApi.saveTask(tenantId, task);
            }
            return Y9Result.successMsg("重定位成功");
        } catch (Exception e) {
            LOGGER.error("重定位失败！", e);
            return Y9Result.failure("重定位失败！");
        }
    }

    /*
     * Description:
     *
     * @param itemId
     * @param processSerialNumber
     * @param processDefinitionKey
     * @param userChoice
     * @param sponsorGuid
     * @param routeToTaskId
     * @param variables
     * @return
     */
    @Override
    public Y9Result<String> saveAndForwarding(String itemId, String processSerialNumber, String processDefinitionKey,
        String userChoice, String sponsorGuid, String routeToTaskId, Map<String, Object> variables) {
        List<String> userList = new ArrayList<>(this.parseUserChoice(userChoice));
        int num = userList.size();
        boolean tooMuch = num > 100;
        if (tooMuch) {
            return Y9Result.failure("发送人数过多");
        }
        StartProcessResultModel model = this.startProcess(itemId, processSerialNumber, processDefinitionKey);
        String taskId = model.getTaskId();
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (!variables.isEmpty()) {
            variableApi.setVariables(tenantId, taskId, variables);
            if (variables.get("subProcessNum") != null) {// xxx并行子流程，userChoice只传了一个岗位id,根据subProcessNum，添加同一个岗位id,生成多个并行任务。
                String type =
                    processDefinitionApi.getNodeType(tenantId, model.getProcessDefinitionId(), routeToTaskId).getData();
                if (SysVariables.PARALLEL.equals(type)) {// 并行节点才执行
                    String userId = userList.get(0);
                    Integer subProcessNum = Integer.parseInt(variables.get("subProcessNum").toString());
                    if (subProcessNum > 1 && userList.size() == 1) {
                        for (int i = 1; i < subProcessNum; i++) {
                            userList.add(userId);
                        }
                    }
                }
            }
        }
        return this.start4Forwarding(taskId, routeToTaskId, sponsorGuid, userList);
    }

    @Override
    public Y9Result<String> saveAndForwardingByTaskKey(String itemId, String processSerialNumber,
        String processDefinitionKey, String userChoice, String sponsorGuid, String routeToTaskId,
        String startRouteToTaskId, Map<String, Object> variables) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<String> userList = new ArrayList<>(this.parseUserChoice(userChoice));
        int num = userList.size();
        boolean tooMuch = num > 100;
        if (tooMuch) {
            return Y9Result.failure("发送人数过多");
        }
        Map<String, Object> map1 =
            this.startProcessByTaskKey(itemId, processSerialNumber, processDefinitionKey, startRouteToTaskId);
        String taskId = (String)map1.get("taskId");
        if (!variables.isEmpty()) {
            variableApi.setVariables(tenantId, taskId, variables);
        }
        return this.start4Forwarding(taskId, routeToTaskId, sponsorGuid, userList);
    }

    @Override
    public Y9Result<Object> saveAndSubmitTo(String itemId, String processSerialNumber) {
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = Y9LoginUserHolder.getOrgUnitId();
        OrgUnit orgUnit = Y9LoginUserHolder.getOrgUnit();
        try {
            SpmApproveItem item = spmApproveitemService.findById(itemId);
            String processDefinitionKey = item.getWorkflowGuid();
            ProcessDefinitionModel processDefinitionModel =
                repositoryApi.getLatestProcessDefinitionByKey(tenantId, processDefinitionKey).getData();
            String processDefinitionId = processDefinitionModel.getId();
            String taskDefKey = itemStartNodeRoleService.getStartTaskDefKey(itemId);
            Y9Result<TargetModel> routeToTaskIdResult =
                this.parserRouteToTaskId(itemId, processSerialNumber, processDefinitionId, taskDefKey, "");
            if (!routeToTaskIdResult.isSuccess()) {
                return Y9Result.failure(routeToTaskIdResult.getMsg());
            }
            String routeToTaskId = routeToTaskIdResult.getData().getTaskDefKey(),
                routeToTaskName = routeToTaskIdResult.getData().getTaskDefName();
            String multiInstance =
                processDefinitionApi.getNodeType(tenantId, processDefinitionId, routeToTaskId).getData();
            Y9Result<List<String>> userResult =
                this.parserUser(itemId, processDefinitionId, routeToTaskId, routeToTaskName, "", multiInstance);
            if (!userResult.isSuccess()) {
                return Y9Result.failure(userResult.getMsg());
            }
            StartProcessResultModel model = this.startProcess(itemId, processSerialNumber, processDefinitionKey);
            String taskId = model.getTaskId(), processInstanceId = model.getProcessInstanceId();
            ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
            List<String> userList = userResult.getData();
            Map<String, Object> variables =
                CommonOpt.setVariables(userId, orgUnit.getName(), routeToTaskId, userList, multiInstance);
            asyncHandleService.forwarding4Task(processInstanceId, processParam, "", "", taskId, multiInstance,
                variables, userList);
            return Y9Result.successMsg("提交成功");
        } catch (Exception e) {
            LOGGER.error("提交失败！", e);
        }
        return Y9Result.failure("提交失败！");
    }

    @Override
    public SignTaskConfigModel signTaskConfig(String itemId, String processDefinitionId, String taskDefinitionKey,
        String processSerialNumber) {
        SignTaskConfigModel model = new SignTaskConfigModel();
        try {
            // signTask为true,则直接发送
            model.setSignTask(false);
            model.setUserChoice("");
            model.setOnePerson(false);
            boolean searchPerson = true;
            String tenantId = Y9LoginUserHolder.getTenantId();
            String multiInstance =
                processDefinitionApi.getNodeType(tenantId, processDefinitionId, taskDefinitionKey).getData();
            if (SysVariables.COMMON.equals(multiInstance)) {
                ItemTaskConf itemTaskConf = taskConfRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId,
                    processDefinitionId, taskDefinitionKey);
                ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
                // 判断是否是抢占式签收任务
                if (itemTaskConf != null && itemTaskConf.getSignTask()) {
                    model.setSignTask(true);
                    if (processParam != null && StringUtils.isNotBlank(processParam.getProcessInstanceId())) {
                        List<HistoricTaskInstanceModel> hisTaskList =
                            historictaskApi.findTaskByProcessInstanceIdOrByEndTimeAsc(tenantId,
                                processParam.getProcessInstanceId(), "").getData();
                        for (HistoricTaskInstanceModel hisTask : hisTaskList) {
                            // 获取相同任务
                            if (hisTask.getTaskDefinitionKey().equals(taskDefinitionKey)) {
                                searchPerson = false;
                                model.setUserChoice("6:" + hisTask.getAssignee());
                                break;
                            }
                        }
                    }
                    if (searchPerson) {
                        List<OrgUnit> orgUnitList = this.getUserChoice(itemId, processDefinitionId, taskDefinitionKey,
                            processParam != null ? processParam.getProcessInstanceId() : "");
                        if (orgUnitList.isEmpty()) {
                            model.setSignTask(false);
                        } else {
                            String userChoice = "";
                            for (OrgUnit orgUnit : orgUnitList) {
                                int type = 0;
                                if (orgUnit.getOrgType().equals(OrgTypeEnum.DEPARTMENT)) {
                                    type = 2;
                                } else if (orgUnit.getOrgType().equals(OrgTypeEnum.POSITION)) {
                                    type = 6;
                                }
                                if (StringUtils.isEmpty(userChoice)) {
                                    userChoice = type + ":" + orgUnit.getId();
                                } else {
                                    userChoice += ";" + type + ":" + orgUnit.getId();
                                }
                            }
                            model.setUserChoice(userChoice);
                        }
                    }
                } else {// signTask为false且onePerson为true则直接发送
                    List<OrgUnit> orgUnitList = this.getUserChoice(itemId, processDefinitionId, taskDefinitionKey,
                        processParam != null ? processParam.getProcessInstanceId() : "");
                    // 只有一个人，则直接返回人员发送
                    if (orgUnitList.size() == 1 && orgUnitList.get(0).getOrgType().equals(OrgTypeEnum.POSITION)) {
                        model.setOnePerson(true);
                        model.setUserChoice("6:" + orgUnitList.get(0).getId());
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("获取失败！", e);
        }
        return model;
    }

    /*
     * 启动流程发送
     *
     * @param taskId
     * @param routeToTaskId
     * @param sponsorGuid
     * @param userList
     * @return
     */
    @Override
    public Y9Result<String> start4Forwarding(String taskId, String routeToTaskId, String sponsorGuid,
        List<String> userList) {
        String processInstanceId = "";
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), userId = Y9LoginUserHolder.getOrgUnitId();
            OrgUnit orgUnit = Y9LoginUserHolder.getOrgUnit();
            TaskModel task = taskApi.findById(tenantId, taskId).getData();
            processInstanceId = task.getProcessInstanceId();
            ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
            // 得到要发送节点的multiInstance，PARALLEL表示并行，SEQUENTIAL表示串行
            String multiInstance =
                processDefinitionApi.getNodeType(tenantId, task.getProcessDefinitionId(), routeToTaskId).getData();
            Map<String, Object> variables =
                CommonOpt.setVariables(userId, orgUnit.getName(), routeToTaskId, userList, multiInstance);
            // 子流程信息
            if (multiInstance.equals(SysVariables.CALLACTIVITY)) {
                Map<String, Object> initDataMap = new HashMap<>(16);
                initDataMap.put(SysVariables.PARENTPROCESSSERIALNUMBER,
                    processParam != null ? processParam.getProcessSerialNumber() : "");
                variables.put(SysVariables.INITDATAMAP, initDataMap);
            }
            int num = userList.size();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 并行发送超过20人时，启用异步后台处理。
            boolean tooMuch = num > 20;
            if (SysVariables.PARALLEL.equals(multiInstance) && tooMuch) {
                TaskVariable taskVariable = taskVariableRepository.findByTaskIdAndKeyName(taskId, "isForwarding");
                Date date = new Date();
                if (taskVariable == null) {
                    taskVariable = new TaskVariable();
                    taskVariable.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                    taskVariable.setProcessInstanceId(processInstanceId);
                    taskVariable.setTaskId(taskId);
                    taskVariable.setKeyName("isForwarding");
                    taskVariable.setCreateTime(sdf.format(date));
                }
                taskVariable.setUpdateTime(sdf.format(date));
                taskVariable.setText("true:" + num);
                taskVariableRepository.save(taskVariable);
                asyncHandleService.forwarding(tenantId, orgUnit, processInstanceId, processParam, "", sponsorGuid,
                    taskId, multiInstance, variables, userList);
            } else if (SysVariables.SUBPROCESS.equals(multiInstance)) {
                Map<String, Object> vars = new HashMap<>(16);
                vars.put("parentTaskId", taskId);
                taskApi.createWithVariables(tenantId, orgUnit.getId(), routeToTaskId, vars, userList);
            } else {
                assert processParam != null;
                asyncHandleService.forwarding4Task(processInstanceId, processParam, "true", sponsorGuid, taskId,
                    multiInstance, variables, userList);
            }
            return Y9Result.success(processInstanceId, "发送成功!");
        } catch (Exception e) {
            LOGGER.error("公文发送失败！");
            try {
                final Writer result = new StringWriter();
                final PrintWriter print = new PrintWriter(result);
                e.printStackTrace(print);
                String msg = result.toString();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = sdf.format(new Date());
                // 保存任务发送错误日志
                ErrorLog errorLog = new ErrorLog();
                errorLog.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                errorLog.setCreateTime(time);
                errorLog.setErrorFlag(ErrorLogModel.ERROR_FLAG_FORWRDING);
                errorLog.setErrorType(ErrorLogModel.ERROR_TASK);
                errorLog.setExtendField("启动流程发送少数人失败");
                errorLog.setProcessInstanceId(processInstanceId);
                errorLog.setTaskId(taskId);
                errorLog.setText(msg);
                errorLog.setUpdateTime(time);
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
            SpmApproveItem item = spmApproveitemRepository.findById(itemId).orElse(null);
            vars.put("tenantId", tenantId);
            String startTaskDefKey = itemStartNodeRoleService.getStartTaskDefKey(itemId);
            vars.put("routeToTaskId", startTaskDefKey);
            vars.put("_FLOWABLE_SKIP_EXPRESSION_ENABLED", true);
            assert item != null;
            if (item.isShowSubmitButton()) {
                ProcessDefinitionModel processDefinitionModel =
                    repositoryApi.getLatestProcessDefinitionByKey(tenantId, processDefinitionKey).getData();
                List<Y9FormItemBind> eformTaskBinds = y9FormItemBindService
                    .listByItemIdAndProcDefIdAndTaskDefKey(itemId, processDefinitionModel.getId(), "");
                Map<String, Object> variables =
                    y9FormService.getFormData4Var(eformTaskBinds.get(0).getFormId(), processSerialNumber);
                for (String columnName : variables.keySet()) {
                    String str = StringUtils.replace(variables.get(columnName).toString(), ".", "");
                    if (StringUtils.isNumeric(str)) {// 是数值
                        if (variables.get(columnName).toString().contains(".")) {
                            LOGGER.info(
                                "*************************startProcess_Double:" + variables.get(columnName).toString());
                            variables.put(columnName, Double.valueOf(variables.get(columnName).toString()));
                        } else {
                            LOGGER.info("*************************startProcess_Integer:"
                                + variables.get(columnName).toString());
                            variables.put(columnName, Integer.parseInt(variables.get(columnName).toString()));
                        }
                    }
                }
                vars.putAll(variables);
            }
            TaskModel task =
                activitiOptService.startProcess(processSerialNumber, processDefinitionKey, item.getSystemName(), vars);
            ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
            processParam.setProcessInstanceId(task.getProcessInstanceId());
            processParam.setStartor(Y9LoginUserHolder.getOrgUnitId());
            processParam.setStartorName(Y9LoginUserHolder.getOrgUnit().getName());
            processParam.setSended("true");
            // 保存流程信息到ES
            process4SearchService.saveToDataCenter(tenantId, processParam, Y9LoginUserHolder.getOrgUnit());

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
            LOGGER.error("启动流程失败！", e);
        }
        return model;
    }

    @Override
    public StartProcessResultModel startProcess(String itemId, String processSerialNumber, String processDefinitionKey,
        String userIds) {
        StartProcessResultModel model = null;
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            OrgUnit orgUnit = Y9LoginUserHolder.getOrgUnit();
            Map<String, Object> vars = new HashMap<>(16);
            SpmApproveItem item = spmApproveitemRepository.findById(itemId).orElse(null);
            vars.put("tenantId", tenantId);
            String startTaskDefKey = itemStartNodeRoleService.getStartTaskDefKey(itemId);
            vars.put("routeToTaskId", startTaskDefKey);

            vars = CommonOpt.setVariables(orgUnit.getId(), orgUnit.getName(), "", Arrays.asList(userIds.split(",")),
                processSerialNumber, "", vars);
            assert item != null;
            ProcessInstanceModel piModel = runtimeApi
                .startProcessInstanceByKey(tenantId, orgUnit.getId(), processDefinitionKey, item.getSystemName(), vars)
                .getData();
            // 获取运行的任务节点,这里没有考虑启动节点下一个用户任务节点是多实例的情况
            String processInstanceId = piModel.getId();
            TaskModel task = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData().get(0);

            ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
            processParam.setProcessInstanceId(task.getProcessInstanceId());
            processParam.setStartor(Y9LoginUserHolder.getOrgUnitId());
            processParam.setStartorName(Y9LoginUserHolder.getOrgUnit().getName());
            processParam.setSended("true");
            // 保存流程信息到ES
            process4SearchService.saveToDataCenter(tenantId, processParam, Y9LoginUserHolder.getOrgUnit());

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
            LOGGER.error("启动流程失败！", e);
        }
        return model;
    }

    @Override
    public Map<String, Object> startProcessByTaskKey(String itemId, String processSerialNumber,
        String processDefinitionKey, String startRouteToTaskId) {
        Map<String, Object> map = new HashMap<>(16);
        map.put(UtilConsts.SUCCESS, false);
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            Map<String, Object> vars = new HashMap<>(16);
            SpmApproveItem item = spmApproveitemRepository.findById(itemId).orElse(null);
            vars.put("tenantId", tenantId);
            vars.put(SysVariables.ROUTETOTASKID, startRouteToTaskId);
            assert item != null;
            TaskModel task =
                activitiOptService.startProcess(processSerialNumber, processDefinitionKey, item.getSystemName(), vars);
            map.put("processInstanceId", task.getProcessInstanceId());
            map.put("processSerialNumber", processSerialNumber);
            map.put("processDefinitionId", task.getProcessDefinitionId());
            map.put("taskId", task.getId());
            map.put("taskDefKey", task.getTaskDefinitionKey());

            ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
            processParam.setProcessInstanceId(task.getProcessInstanceId());
            processParam.setStartor(Y9LoginUserHolder.getOrgUnitId());
            processParam.setStartorName(Y9LoginUserHolder.getOrgUnit().getName());
            processParam.setSended("true");
            // 保存流程信息到ES
            process4SearchService.saveToDataCenter(tenantId, processParam, Y9LoginUserHolder.getOrgUnit());

            processParamService.saveOrUpdate(processParam);

            // 异步处理数据
            asyncHandleService.startProcessHandle(tenantId, processSerialNumber, task.getId(),
                task.getProcessInstanceId(), processParam.getSearchTerm());

            map.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            LOGGER.error("启动流程失败！", e);
        }
        return map;
    }

    @Override
    public Y9Result<Object> submitTo(String processSerialNumber, String taskId) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), userId = Y9LoginUserHolder.getOrgUnitId();
            OrgUnit orgUnit = Y9LoginUserHolder.getOrgUnit();
            ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
            String itemId = processParam.getItemId();
            TaskModel task = taskApi.findById(tenantId, taskId).getData();
            if (null == task || null == task.getId()) {
                return Y9Result.failure("该件已被处理。");
            }
            String processDefinitionId = task.getProcessDefinitionId(), taskDefKey = task.getTaskDefinitionKey(),
                processInstanceId = task.getProcessInstanceId();
            Y9Result<TargetModel> routeToTaskIdResult =
                this.parserRouteToTaskId(itemId, processSerialNumber, processDefinitionId, taskDefKey, taskId);
            if (!routeToTaskIdResult.isSuccess()) {
                return Y9Result.failure(routeToTaskIdResult.getMsg());
            }
            String routeToTaskId = routeToTaskIdResult.getData().getTaskDefKey(),
                routeToTaskName = routeToTaskIdResult.getData().getTaskDefName();
            String multiInstance =
                processDefinitionApi.getNodeType(tenantId, processDefinitionId, routeToTaskId).getData();
            Y9Result<List<String>> userResult = this.parserUser(itemId, processDefinitionId, routeToTaskId,
                routeToTaskName, processInstanceId, multiInstance);
            if (!userResult.isSuccess()) {
                return Y9Result.failure(userResult.getMsg());
            }
            List<String> userList = userResult.getData();
            Map<String, Object> variables =
                CommonOpt.setVariables(userId, orgUnit.getName(), routeToTaskId, userList, multiInstance);
            String subProcessStr =
                variableApi.getVariableByProcessInstanceId(tenantId, processInstanceId, "subProcessNum").getData();
            if (subProcessStr != null) {// xxx并行子流程，userChoice只传了一个岗位id,根据subProcessNum，添加同一个岗位id,生成多个并行任务。
                if (SysVariables.PARALLEL.equals(multiInstance)) {// 并行节点才执行
                    String userChoice = userList.get(0);
                    Integer subProcessNum = Integer.parseInt(subProcessStr);
                    if (subProcessNum > 1 && userList.size() == 1) {
                        for (int i = 1; i < subProcessNum; i++) {
                            userList.add(userChoice);
                        }
                    }
                }
            }
            variables.put(SysVariables.USERS, userList);
            asyncHandleService.forwarding4Task(processInstanceId, processParam, "true", "", taskId, multiInstance,
                variables, userList);
            return Y9Result.successMsg("提交成功");
        } catch (Exception e) {
            LOGGER.error("提交失败！", e);
        }
        return Y9Result.failure("提交失败！");
    }

}
