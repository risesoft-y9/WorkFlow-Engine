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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
import net.risesoft.model.itemadmin.ErrorLogModel;
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
import net.risesoft.service.ItemButtonBindService;
import net.risesoft.service.ItemPermissionService;
import net.risesoft.service.ItemStartNodeRoleService;
import net.risesoft.service.ItemTaskConfService;
import net.risesoft.service.OfficeDoneInfoService;
import net.risesoft.service.Process4SearchService;
import net.risesoft.service.ProcessParamService;
import net.risesoft.service.RoleService;
import net.risesoft.service.SpmApproveItemService;
import net.risesoft.service.Y9FormItemBindService;
import net.risesoft.service.form.Y9FormService;
import net.risesoft.util.ButtonUtil;
import net.risesoft.util.CommonOpt;
import net.risesoft.util.ListUtil;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service(value = "documentService")
public class DocumentServiceImpl implements DocumentService {

    private static final Logger log = LoggerFactory.getLogger(DocumentServiceImpl.class);

    @Autowired
    private ActivitiOptServiceImpl activitiOptService;

    @Autowired
    private SpmApproveItemService spmApproveitemService;

    @Autowired
    private SpmApproveItemRepository spmApproveitemRepository;

    @Autowired
    private ItemTaskConfService taskConfService;

    @Autowired
    private ItemPermissionService itemPermissionService;

    @Autowired
    private Y9FormItemBindService y9FormItemBindService;

    @Autowired
    private ItemButtonBindService buttonItemBindService;

    @Autowired
    private TodoTaskApi rpcTodoTaskManager;

    @Autowired
    private TaskApi taskManager;

    @Autowired
    private CustomGroupApi customGroupApi;

    @Autowired
    private ProcessDefinitionApi processDefinitionManager;

    @Autowired
    private VariableApi variableManager;

    @Autowired
    private OrgUnitApi orgUnitManager;

    @Autowired
    private RepositoryApi repositoryManager;

    @Autowired
    private PositionApi positionManager;

    @Autowired
    private RoleApi roleManager;

    @Autowired
    private PositionRoleApi positionRoleApi;

    @Autowired
    private DepartmentApi departmentManager;

    @Autowired
    private PositionResourceApi positionResourceApi;

    @Autowired
    private HistoricProcessApi historicProcessManager;

    @Autowired
    private HistoricTaskApi historicTaskManager;

    @Autowired
    private RuntimeApi runtimeManager;

    @Autowired
    private ProcessParamService processParamService;

    @Autowired
    private ProcessTodoApi todoManager;

    @Autowired
    private PrintTemplateItemBindRepository printTemplateItemBindRepository;

    @Autowired
    private OfficeDoneInfoService officeDoneInfoService;

    @Autowired
    private TaskVariableRepository taskVariableRepository;

    @Autowired
    private AsyncHandleService asyncHandleService;

    @Autowired
    private Y9FormRepository y9FormRepository;

    @Autowired
    private Process4SearchService process4SearchService;

    @Autowired
    private ErrorLogService errorLogService;

    @Autowired
    private ItemStartNodeRoleService itemStartNodeRoleService;

    @Autowired
    private ItemTaskConfRepository taskConfRepository;

    @Autowired
    private DynamicRoleMemberService dynamicRoleMemberService;

    @Autowired
    private ConditionParserApi conditionParserApi;

    @Autowired
    private Y9FormService y9FormService;

    @Autowired
    private RoleService roleService;

    @Override
    public Map<String, Object> add(String itemId, boolean mobile, Map<String, Object> returnMap) {
        try {
            String userId = Y9LoginUserHolder.getPositionId(), tenantId = Y9LoginUserHolder.getTenantId();
            if (StringUtils.isNotBlank(itemId)) {
                returnMap = spmApproveitemService.findById(itemId, returnMap);
                String processDefinitionKey = (String)returnMap.get("processDefinitionKey");
                ProcessDefinitionModel pdModel = repositoryManager.getLatestProcessDefinitionByKey(tenantId, processDefinitionKey);
                String processDefinitionId = pdModel.getId();
                if (StringUtils.isBlank(processDefinitionKey)) {
                    returnMap.put("msg", "当前事项没有绑定流程！");
                    returnMap.put(UtilConsts.SUCCESS, false);
                    return returnMap;
                }
                String taskDefKey = itemStartNodeRoleService.getStartTaskDefKey(itemId);
                String activitiUser = userId;
                returnMap = this.genDocumentModel(itemId, processDefinitionKey, "", taskDefKey, mobile, returnMap);
                returnMap = this.menuControl(itemId, processDefinitionId, taskDefKey, "", returnMap, ItemBoxTypeEnum.ADD.getValue());
                returnMap.put("processDefinitionId", processDefinitionId);
                returnMap.put("processDefinitionKey", processDefinitionKey);
                returnMap.put("taskDefKey", taskDefKey);
                returnMap.put("activitiUser", activitiUser);
                returnMap.put("currentUser", Y9LoginUserHolder.getPosition().getName());
                returnMap.put("itembox", ItemBoxTypeEnum.ADD.getValue());
                returnMap.put("processSerialNumber", Y9IdGenerator.genId(IdType.SNOWFLAKE));
                returnMap.put("processInstanceId", "");
            }
        } catch (Exception e) {
            returnMap.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        return returnMap;
    }

    /**
     * 向userIds中添加内容
     *
     * @param userIds
     * @param userId 人员Guid
     * @return
     */
    private String addUserId(String userIds, String userId) {
        /**
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
        TaskModel task = taskManager.findById(tenantId, taskId);
        String processInstanceId = task.getProcessInstanceId();

        /**
         * 1办结流程
         */
        runtimeManager.complete4Position(tenantId, Y9LoginUserHolder.getPositionId(), processInstanceId, taskId);
    }

    @Override
    public Map<String, Object> docUserChoise(String itemId, String processDefinitionKey, String processDefinitionId, String taskId, String taskDefKey, String processInstanceId) {
        Map<String, Object> returnMap = new HashMap<>(16);
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String multiInstance = processDefinitionManager.getNodeType(tenantId, processDefinitionId, taskDefKey);
            Map<String, Object> tabMap = itemPermissionService.getTabMap(itemId, processDefinitionId, taskDefKey, processInstanceId);
            returnMap.put("existPosition", tabMap.get("existPosition"));
            returnMap.put("existDepartment", tabMap.get("existDepartment"));

            Y9Page<CustomGroup> pagelist = customGroupApi.pageCustomGroupByPersonId(tenantId, Y9LoginUserHolder.getPersonId(), new Y9PageQuery(1, 1));
            returnMap.put("existCustomGroup", (pagelist != null && pagelist.getTotal() > 0) ? true : false);

            returnMap.put("multiInstance", multiInstance);
            returnMap.put("processDefinitionId", processDefinitionId);
            returnMap.put("tenantId", tenantId);
            returnMap.put("itemId", itemId);
            boolean sponsorStatus = taskConfService.getSponserStatus(itemId, processDefinitionId, taskDefKey);
            returnMap.put("isSponsorStatus", sponsorStatus);
            returnMap.put("routeToTask", taskDefKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnMap;
    }

    @Override
    public Map<String, Object> edit(String itembox, String taskId, String processInstanceId, String itemId, boolean mobile) {
        Map<String, Object> returnMap = new HashMap<>(16);
        String processSerialNumber = "", processDefinitionId = "", taskDefinitionKey = "", processDefinitionKey = "", activitiUser = "";
        String itemboxStr = itembox;
        String startor = "";
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (ItemBoxTypeEnum.MONITORDOING.getValue().equals(itembox)) {
            itembox = ItemBoxTypeEnum.DOING.getValue();
        }
        returnMap.put("meeting", false);
        ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
        startor = processParam.getStartor();
        if (itembox.equalsIgnoreCase(ItemBoxTypeEnum.TODO.getValue())) {
            TaskModel task = taskManager.findById(tenantId, taskId);
            processInstanceId = task.getProcessInstanceId();
            processSerialNumber = processParam.getProcessSerialNumber();
            processDefinitionId = task.getProcessDefinitionId();
            taskDefinitionKey = task.getTaskDefinitionKey();
            processDefinitionKey = processDefinitionId.split(SysVariables.COLON)[0];
            activitiUser = task.getAssignee();
            if (StringUtils.isBlank(itemId)) {
                itemId = processParam.getItemId();
            }
            /**
             * 设为已读
             */
            if (StringUtils.isBlank(task.getFormKey())) {
                task.setFormKey("0");
                taskManager.saveTask(tenantId, task);
                try {
                    rpcTodoTaskManager.setIsNewTodo(tenantId, taskId, "0");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 获取第一节点任务key,可能多个
            String startTaskDefKey = "";
            String startNode = processDefinitionManager.getStartNodeKeyByProcessDefinitionId(tenantId, processDefinitionId);
            List<Map<String, String>> nodeList = processDefinitionManager.getTargetNodes(tenantId, processDefinitionId, startNode);
            for (Map<String, String> map : nodeList) {
                startTaskDefKey = Y9Util.genCustomStr(startTaskDefKey, map.get("taskDefKey"));
            }
            returnMap.put("startTaskDefKey", startTaskDefKey);
            OfficeDoneInfo officeDoneInfo = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
            returnMap.put("meeting", (officeDoneInfo.getMeeting() != null && officeDoneInfo.getMeeting().equals("1")) ? true : false);
        } else if (itembox.equalsIgnoreCase(ItemBoxTypeEnum.DOING.getValue()) || itembox.equalsIgnoreCase(ItemBoxTypeEnum.DONE.getValue())) {
            HistoricProcessInstanceModel hpi = historicProcessManager.getById(tenantId, processInstanceId);
            OfficeDoneInfo officeDoneInfo = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
            if (hpi == null) {
                if (officeDoneInfo == null) {
                    String year = processParam.getCreateTime().substring(0, 4);
                    hpi = historicProcessManager.getByIdAndYear(tenantId, processInstanceId, year);
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
            returnMap.put("meeting", (officeDoneInfo.getMeeting() != null && officeDoneInfo.getMeeting().equals("1")) ? true : false);
            processSerialNumber = processParam.getProcessSerialNumber();
            if (StringUtils.isNotEmpty(taskId)) {
                if (taskId.contains(SysVariables.COMMA)) {
                    taskId = taskId.split(SysVariables.COMMA)[0];
                }
                TaskModel taskTemp = taskManager.findById(tenantId, taskId);
                taskDefinitionKey = taskTemp.getTaskDefinitionKey();
            }
        }
        returnMap.put("title", processParam.getTitle());
        returnMap.put("startor", startor);
        returnMap.put("itembox", itembox);
        returnMap.put("control", itemboxStr);
        returnMap.put("currentUser", Y9LoginUserHolder.getPosition().getName());
        returnMap.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
        returnMap.put("processDefinitionKey", processDefinitionKey);
        returnMap.put("processDefinitionId", processDefinitionId);
        returnMap.put("processInstanceId", processInstanceId);
        returnMap.put("taskDefKey", taskDefinitionKey);
        returnMap.put("taskId", taskId);
        returnMap.put(SysVariables.ACTIVITIUSER, activitiUser);
        returnMap = spmApproveitemService.findById(itemId, returnMap);
        returnMap = this.genDocumentModel(itemId, processDefinitionKey, processDefinitionId, taskDefinitionKey, mobile, returnMap);
        returnMap = this.menuControl(itemId, processDefinitionId, taskDefinitionKey, taskId, returnMap, itemboxStr);
        return returnMap;
    }

    /**
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
    public Map<String, Object> forwarding(String taskId, String sponsorHandle, String userChoice, String routeToTaskId, String sponsorGuid) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        String processInstanceId = "";
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            TaskModel task = taskManager.findById(tenantId, taskId);
            processInstanceId = task.getProcessInstanceId();
            ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
            List<String> userList = new ArrayList<String>();
            userList.addAll(this.parseUserChoice(userChoice));
            int num = userList.size();
            boolean tooMuch = num > 100;
            if (tooMuch) {
                map.put(UtilConsts.SUCCESS, false);
                map.put("msg", "发送人数过多");
                return map;
            }
            Position position = Y9LoginUserHolder.getPosition();
            // 得到要发送节点的multiInstance，PARALLEL表示并行，SEQUENTIAL表示串行
            String multiInstance = processDefinitionManager.getNodeType(tenantId, task.getProcessDefinitionId(), routeToTaskId);
            Map<String, Object> variables = CommonOpt.setVariables(positionId, position.getName(), routeToTaskId, userList, multiInstance);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            /**
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
                taskVariable.setText("true:" + String.valueOf(num));
                taskVariableRepository.save(taskVariable);
                asyncHandleService.forwarding(tenantId, position, processInstanceId, processParam, sponsorHandle, sponsorGuid, taskId, multiInstance, variables, userList);
            } else if (SysVariables.SUBPROCESS.equals(multiInstance)) {
                Map<String, Object> vars = new HashMap<String, Object>(16);
                vars.put("parentTaskId", taskId);
                taskManager.createWithVariables(tenantId, positionId, routeToTaskId, vars, userList);
            } else {
                // 判断是否是主办办理，如果是，需要将协办未办理的的任务默认办理
                if (StringUtils.isNotBlank(sponsorHandle) && UtilConsts.TRUE.equals(sponsorHandle)) {
                    List<TaskModel> taskNextList1 = taskManager.findByProcessInstanceId(tenantId, processInstanceId);
                    /**
                     * 如果协办人数超过10人，启用异步后台处理。
                     */
                    tooMuch = taskNextList1.size() > 10;
                    if (tooMuch) {
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
                        taskVariable.setText("true:" + String.valueOf(num));
                        taskVariableRepository.save(taskVariable);
                        asyncHandleService.forwarding(tenantId, position, processInstanceId, processParam, sponsorHandle, sponsorGuid, taskId, multiInstance, variables, userList);
                    } else {
                        asyncHandleService.forwarding4Task(processInstanceId, processParam, sponsorHandle, sponsorGuid, taskId, multiInstance, variables, userList);
                    }
                } else {
                    asyncHandleService.forwarding4Task(processInstanceId, processParam, sponsorHandle, sponsorGuid, taskId, multiInstance, variables, userList);
                }
            }
            map.put("processInstanceId", processInstanceId);
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "发送成功!");
        } catch (Exception e) {
            log.error("公文发送失败！");
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "发送失败!");
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
                e2.printStackTrace();
            }
        }
        return map;
    }

    @Override
    public Map<String, Object> genDocumentModel(String itemId, String processDefinitionKey, String processDefinitionId, String taskDefinitionKey, boolean mobile, Map<String, Object> returnMap) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (StringUtils.isBlank(processDefinitionId)) {
            processDefinitionId = repositoryManager.getLatestProcessDefinitionByKey(tenantId, processDefinitionKey).getId();
        }
        // Y9表单
        String formIds = "";
        String showOtherFlag = "";
        String formNames = "";
        if (mobile) {
            List<Y9FormItemMobileBind> eformTaskBinds = y9FormItemBindService.findByItemIdAndProcDefIdAndTaskDefKey4Mobile(itemId, processDefinitionId, taskDefinitionKey);
            returnMap.put("formId", "");
            returnMap.put("formName", "");
            returnMap.put("formJson", "");
            if (eformTaskBinds.size() > 0) {
                Y9FormItemMobileBind eftb = eformTaskBinds.get(0);
                returnMap.put("formId", eftb.getFormId());
                String formName = eftb.getFormName();
                boolean b = formName.contains("(");
                if (b) {
                    formName = formName.substring(0, formName.indexOf("("));
                }
                returnMap.put("formName", formName);
                Y9Form y9Form = y9FormRepository.findById(eftb.getFormId()).orElse(null);
                returnMap.put("formJson", y9Form.getFormJson());
            }
            return returnMap;
        }
        List<Y9FormItemBind> eformTaskBinds = y9FormItemBindService.findByItemIdAndProcDefIdAndTaskDefKey(itemId, processDefinitionId, taskDefinitionKey);
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        if (eformTaskBinds.size() > 0) {
            for (Y9FormItemBind eftb : eformTaskBinds) {
                formIds = Y9Util.genCustomStr(formIds, eftb.getFormId());
                String formName = eftb.getFormName();
                if (formName.contains("(")) {
                    formName = formName.substring(0, formName.indexOf("("));
                }
                formNames = Y9Util.genCustomStr(formNames, formName);
                Map<String, String> map = new HashMap<String, String>(16);
                map.put("formId", eftb.getFormId());
                map.put("formName", formName);
                list.add(map);
            }
            showOtherFlag = y9FormItemBindService.getShowOther(eformTaskBinds);
        }
        returnMap.put("formList", list);
        returnMap.put("formIds", formIds);
        returnMap.put("formNames", formNames);
        returnMap.put("showOtherFlag", showOtherFlag);
        // 获取打印表单
        String printFormId = "";
        String printFormType = "";
        ItemPrintTemplateBind bind = printTemplateItemBindRepository.findByItemId(itemId);
        if (bind != null) {
            printFormId = bind.getTemplateId();
            printFormType = bind.getTemplateType();
        }
        returnMap.put("printFormId", printFormId);
        returnMap.put("printFormType", printFormType);
        return returnMap;
    }

    private void getAllPosition(List<Position> list, String deptId) {
        List<Department> deptList = departmentManager.listSubDepartments(Y9LoginUserHolder.getTenantId(), deptId).getData();
        List<Position> list0 = departmentManager.listPositions(Y9LoginUserHolder.getTenantId(), deptId).getData();
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
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            String resourceId = "";
            List<Resource> list = positionResourceApi.listSubResources(tenantId, positionId, AuthorityEnum.BROWSE, resourceId).getData();
            String url = "";
            for (Resource r : list) {
                url = r.getUrl();
                if (StringUtils.isBlank(url)) {
                    continue;
                }
                if (!url.contains("itemId=")) {
                    continue;
                }
                String itemId = url.split("itemId=")[1];
                return itemId;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public String getFormIdByItemId(String itemId, String processDefinitionKey) {
        String formIds = "";
        String processDefinitionId = repositoryManager.getLatestProcessDefinitionByKey(Y9LoginUserHolder.getTenantId(), processDefinitionKey).getId();
        List<Y9FormItemBind> eformTaskBinds = y9FormItemBindService.findByItemIdAndProcDefIdAndTaskDefKey(itemId, processDefinitionId, "");
        if (eformTaskBinds.size() > 0) {
            for (Y9FormItemBind eftb : eformTaskBinds) {
                formIds = Y9Util.genCustomStr(formIds, eftb.getFormId());
            }
        }
        return formIds;
    }

    @Override
    public List<Map<String, Object>> getItemList() {
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        try {
            String positionId = Y9LoginUserHolder.getPositionId();
            String tenantId = Y9LoginUserHolder.getTenantId();
            String resourceId = "";
            //////////////////////////////////
            List<Resource> list = positionResourceApi.listSubResources(tenantId, positionId, AuthorityEnum.BROWSE, resourceId).getData();
            Map<String, Object> map = null;
            String url = "";
            long todoCount = 0;
            for (Resource r : list) {
                map = new HashMap<String, Object>(16);
                url = r.getUrl();
                if (StringUtils.isBlank(url)) {
                    continue;
                }
                if (!url.contains("itemId=")) {
                    continue;
                }
                String itemId = url.split("itemId=")[1];
                map.put("id", r.getId());
                map.put("url", itemId);
                map.put("iconData", "");
                map.put("todoCount", 0);
                SpmApproveItem spmApproveitem = spmApproveitemRepository.findById(itemId).orElse(null);
                map.put("name", r.getName());
                if (spmApproveitem != null && spmApproveitem.getId() != null) {
                    map.put("name", spmApproveitem.getName());
                    todoCount = todoManager.getTodoCountByPositionIdAndProcessDefinitionKey(tenantId, positionId, spmApproveitem.getWorkflowGuid());
                    map.put("todoCount", todoCount);
                    map.put("iconData", StringUtils.isBlank(spmApproveitem.getIconData()) ? "" : spmApproveitem.getIconData());
                    listMap.add(map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listMap;
    }

    @Override
    public List<Map<String, Object>> getMyItemList() {
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            String resourceId = "";
            List<Resource> list = positionResourceApi.listSubResources(tenantId, positionId, AuthorityEnum.BROWSE, resourceId).getData();
            Map<String, Object> map = null;
            String url = "";
            for (Resource r : list) {
                map = new HashMap<String, Object>(16);
                url = r.getUrl();
                if (StringUtils.isBlank(url)) {
                    continue;
                }
                if (!url.contains("itemId=")) {
                    continue;
                }
                String itemId = url.split("itemId=")[1];
                map.put("id", r.getId());
                map.put("itemId", itemId);
                map.put("iconData", "");
                SpmApproveItem spmApproveitem = spmApproveitemRepository.findById(itemId).orElse(null);
                map.put("name", r.getName());
                if (spmApproveitem != null && spmApproveitem.getId() != null) {
                    map.put("name", spmApproveitem.getName());
                    map.put("iconData", StringUtils.isBlank(spmApproveitem.getIconData()) ? "" : spmApproveitem.getIconData());
                    listMap.add(map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listMap;
    }

    public List<OrgUnit> getUserChoice(String itemId, String processDefinitionId, String taskDefinitionKey, String processSerialNumber) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<ItemPermission> list = itemPermissionService.findByItemIdAndProcessDefinitionIdAndTaskDefKeyExtra(itemId, processDefinitionId, taskDefinitionKey);
        List<OrgUnit> orgUnitList = new ArrayList<OrgUnit>();
        for (ItemPermission o : list) {
            if (o.getRoleType() == ItemPermissionEnum.DEPARTMENT.getValue()) {
                OrgUnit orgUnit = orgUnitManager.getOrgUnit(tenantId, o.getRoleId()).getData();
                if (null != orgUnit) {
                    orgUnitList.add(orgUnit);
                }
            } else if (o.getRoleType() == ItemPermissionEnum.POSITION.getValue()) {
                OrgUnit orgUnit = orgUnitManager.getOrgUnit(tenantId, o.getRoleId()).getData();
                if (null != orgUnit) {
                    orgUnitList.add(orgUnit);
                }
            } else if (o.getRoleType() == ItemPermissionEnum.ROLE.getValue()) {
                List<OrgUnit> deptList = roleManager.listOrgUnitsById(tenantId, o.getRoleId(), OrgTypeEnum.DEPARTMENT).getData();
                List<OrgUnit> personList = roleManager.listOrgUnitsById(tenantId, o.getRoleId(), OrgTypeEnum.POSITION).getData();
                orgUnitList.addAll(deptList);
                orgUnitList.addAll(personList);
            } else if (o.getRoleType() == ItemPermissionEnum.DYNAMICROLE.getValue()) {
                List<OrgUnit> ouList = dynamicRoleMemberService.getOrgUnitList(o.getRoleId(), processSerialNumber);
                for (OrgUnit orgUnit : ouList) {
                    // if (orgUnit.getOrgType().equals(OrgTypeEnum.DEPARTMENT) ||
                    // orgUnit.getOrgType().equals(OrgTypeEnum.POSITION)) {
                    if (orgUnit.getOrgType().equals(OrgTypeEnum.POSITION)) {
                        Position position = positionManager.getPosition(tenantId, orgUnit.getId()).getData();
                        if (position != null && !position.getDisabled()) {
                            orgUnitList.add(orgUnit);
                        }
                    } else {
                        orgUnitList.add(orgUnit);
                    }
                    // }
                }
            }
        }
        return orgUnitList;
    }

    @Override
    public Map<String, Object> menuControl(String itemId, String processDefinitionId, String taskDefKey, String taskId, Map<String, Object> returnMap, String itembox) {
        ButtonUtil buttonUtil = new ButtonUtil();
        String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
        Map<String, Object> map = buttonUtil.showButton(itemId, taskId, itembox);
        String[] buttonIds = (String[])map.get("buttonIds");
        String[] buttonNames = (String[])map.get("buttonNames");
        String sponsorHandle = (String)map.get("sponsorHandle");
        int[] buttonOrders = (int[])map.get("buttonOrders");
        boolean[] isButtonShow = (boolean[])map.get("isButtonShow");
        boolean selectMenu = true;
        String menuName = "";
        String menuKey = "";
        String sendName = "";
        String sendKey = "";
        String repositionName = "";
        String repositionKey = "";
        List<Map<String, Object>> sendMap = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> menuMap = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> repositionMap = new ArrayList<Map<String, Object>>();
        List<ItemButtonBind> bibList = new ArrayList<>();
        // 生成按钮数组
        for (int i = buttonOrders.length - 1; i >= 0; i--) {
            int k = buttonOrders[i] - 1;
            /**
             * 如果显示保存按钮，那么说明是待办，把自定义普通按钮加在保存按钮的前面
             */
            if (k == 0 && isButtonShow[0]) {
                bibList = buttonItemBindService.findListContainRoleId(itemId, ItemButtonTypeEnum.COMMON.getValue(), processDefinitionId, taskDefKey);
                for (ItemButtonBind bind : bibList) {
                    String buttonName = bind.getButtonName(), buttonCustomId = bind.getButtonCustomId();
                    if (!"发送".equals(buttonName)) {
                        List<String> roleIds = bind.getRoleIds();
                        if (roleIds.isEmpty()) {
                            Map<String, Object> mapTemp = new HashMap<String, Object>(16);
                            mapTemp.put("menuName", buttonName);
                            mapTemp.put("menuKey", buttonCustomId);
                            menuName = Y9Util.genCustomStr(menuName, buttonName);
                            menuKey = Y9Util.genCustomStr(menuKey, buttonCustomId);
                            menuMap.add(mapTemp);
                        } else {
                            for (String roleId : roleIds) {
                                boolean hasRole = positionRoleApi.hasRole(tenantId, roleId, positionId).getData();
                                if (hasRole) {
                                    Map<String, Object> mapTemp = new HashMap<String, Object>(16);
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

            /**
             * 假如发送按钮显示的话，去获取发送下面的路由
             */
            if (k == 1 && isButtonShow[1] && StringUtils.isNotBlank(taskDefKey)) {

                /**
                 * 假如有自定义“发送”按钮的话,就不显示默认的发送按钮
                 */
                Boolean haveSendButton = false;
                bibList = buttonItemBindService.findListContainRoleId(itemId, ItemButtonTypeEnum.COMMON.getValue(), processDefinitionId, taskDefKey);
                bibFor:
                for (ItemButtonBind bib : bibList) {
                    if ("发送".equals(bib.getButtonName())) {
                        List<String> roleIds = bib.getRoleIds();
                        if (roleIds.isEmpty()) {
                            Map<String, Object> mapTemp = new HashMap<String, Object>(16);
                            mapTemp.put("menuName", bib.getButtonName());
                            mapTemp.put("menuKey", bib.getButtonCustomId());
                            menuName = Y9Util.genCustomStr(menuName, bib.getButtonName());
                            menuKey = Y9Util.genCustomStr(menuKey, bib.getButtonCustomId());
                            menuMap.add(mapTemp);

                            haveSendButton = true;
                            break;
                        } else {
                            for (String roleId : roleIds) {
                                boolean hasrole = positionRoleApi.hasRole(tenantId, roleId, positionId).getData();
                                if (hasrole) {
                                    Map<String, Object> mapTemp = new HashMap<String, Object>(16);
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
                    /**
                     * 没有配置自定义“发送”按钮的话，添加上默认的“发送”按钮
                     */
                    Map<String, Object> map1 = new HashMap<String, Object>(16);
                    map1.put("menuName", buttonNames[k]);
                    map1.put("menuKey", buttonIds[k]);
                    menuName = Y9Util.genCustomStr(menuName, buttonNames[k]);
                    menuKey = Y9Util.genCustomStr(menuKey, buttonIds[k]);
                    menuMap.add(map1);
                    /**
                     * 添加发送下面的路由
                     */
                    List<Map<String, String>> routeToTasks = processDefinitionManager.getTargetNodes(tenantId, processDefinitionId, taskDefKey);
                    for (Map<String, String> m : routeToTasks) {
                        Map<String, Object> map2 = new HashMap<String, Object>(16);
                        // 退回、路由网关不显示在发送下面
                        if (!m.get("taskDefName").equals("退回") && !m.get("taskDefName").equals("Exclusive Gateway")) {
                            sendName = Y9Util.genCustomStr(sendName, m.get("taskDefName"));
                            sendKey = Y9Util.genCustomStr(sendKey, m.get("taskDefKey"));
                            map2.put("sendName", m.get("taskDefName"));
                            map2.put("sendKey", m.get("taskDefKey"));
                            sendMap.add(map2);
                        }
                    }
                    /**
                     * 添加自定义按钮到发送
                     */
                    bibList = buttonItemBindService.findListContainRoleId(itemId, ItemButtonTypeEnum.SEND.getValue(), processDefinitionId, taskDefKey);
                    for (ItemButtonBind bind : bibList) {
                        List<String> roleIds = bind.getRoleIds();
                        String buttonName = bind.getButtonName(), buttonCustomId = bind.getButtonCustomId();
                        if (roleIds.isEmpty()) {
                            Map<String, Object> mapTemp = new HashMap<String, Object>(16);
                            sendName = Y9Util.genCustomStr(sendName, buttonName);
                            sendKey = Y9Util.genCustomStr(sendKey, buttonCustomId);
                            mapTemp.put("sendName", buttonName);
                            mapTemp.put("sendKey", buttonCustomId);
                            sendMap.add(mapTemp);
                        } else {
                            for (String roleId : roleIds) {
                                boolean hasrole = positionRoleApi.hasRole(tenantId, roleId, positionId).getData();
                                if (hasrole) {
                                    Map<String, Object> mapTemp = new HashMap<String, Object>(16);
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

            /**
             * 假如重定向按钮显示的话，去获取路由
             */
            String taskDefNameJson = "";
            if (k == 15 && isButtonShow[15]) {
                List<Map<String, Object>> taskNodes = processDefinitionManager.getNodes(tenantId, processDefinitionId, false);
                for (Map<String, Object> node : taskNodes) {
                    Map<String, Object> map3 = new HashMap<String, Object>(16);
                    // 流程不显示在重定向按钮下面
                    if (!node.get("taskDefName").equals("流程")) {
                        repositionName = Y9Util.genCustomStr(repositionName, node.get("taskDefName").toString());
                        repositionKey = Y9Util.genCustomStr(repositionKey, node.get("taskDefKey").toString());
                        map3.put("repositionName", node.get("taskDefName").toString());
                        map3.put("repositionKey", node.get("taskDefKey").toString());
                        repositionMap.add(map3);
                    }
                }
                returnMap.put("repositionMap", repositionMap);
                ObjectMapper mapper = new ObjectMapper();
                try {
                    taskDefNameJson = mapper.writeValueAsString(repositionMap);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    taskDefNameJson = "[]";
                }
                returnMap.put("taskDefNameJson", taskDefNameJson);
            }

            if (k != 1 && isButtonShow[k]) {
                Map<String, Object> map1 = new HashMap<String, Object>(16);
                map1.put("menuName", buttonNames[k]);
                map1.put("menuKey", buttonIds[k]);
                menuName = Y9Util.genCustomStr(menuName, buttonNames[k]);
                menuKey = Y9Util.genCustomStr(menuKey, buttonIds[k]);
                menuMap.add(map1);
            }
        }
        returnMap.put("sendMap", sendMap);
        returnMap.put("menuMap", menuMap);

        returnMap.put("sendName", sendName);
        returnMap.put("sendKey", sendKey);
        returnMap.put("menuName", menuName);
        returnMap.put("menuKey", menuKey);
        returnMap.put("sponsorHandle", sponsorHandle);
        returnMap.put("selectMenu", selectMenu);
        returnMap.put("isLastPerson4RefuseClaim", map.get("isLastPerson4RefuseClaim"));
        returnMap.put("multiInstance", map.get("multiInstance"));
        returnMap.put("nextNode", map.get("nextNode"));
        return returnMap;
    }

    public Y9Result<Map<String, String>> parserRouteToTaskId(String itemId, String processSerialNumber, String processDefinitionId, String taskDefKey, String taskId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Y9Result<Map<String, String>> result = Y9Result.failure("解析目标路由失败");
        try {
            List<Map<String, String>> targetNodes = processDefinitionManager.getTargetNodes(tenantId, processDefinitionId, taskDefKey);
            if (targetNodes.isEmpty()) {
                result.setMsg("目标路由不存在");
                return result;
            }
            if (1 == targetNodes.size()) {
                result.setData(targetNodes.get(0));
                result.setSuccess(true);
                return result;
            }
            List<Y9FormItemBind> eformTaskBinds = y9FormItemBindService.findByItemIdAndProcDefIdAndTaskDefKey(itemId, processDefinitionId, taskDefKey);
            Map<String, Object> variables = y9FormService.getFormData4Var(eformTaskBinds.get(0).getFormId(), processSerialNumber);
            List<Map<String, String>> targetNodesTemp = new ArrayList<>();
            for (Map<String, String> targetNode : targetNodes) {
                boolean b = conditionParserApi.parser(tenantId, targetNode.get(SysVariables.CONDITIONEXPRESSION), variables);
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
                variableManager.setVariables(tenantId, taskId, variables);
            }
            result.setData(targetNodesTemp.get(0));
            result.setMsg("解析目标路由成功");
            result.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public Y9Result<List<String>> parserUser(String itemId, String processDefinitionId, String routeToTaskId, String routeToTaskName, String processInstanceId, String multiInstance) {
        Y9Result<List<String>> result = Y9Result.failure("解析人员失败");
        List<OrgUnit> orgUnitList = roleService.findPermUser4SUbmitTo(itemId, processDefinitionId, routeToTaskId, processInstanceId);
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
    public List<String> parseUserChoice(String userChoice) {
        String users = "";
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (StringUtils.isNotBlank(userChoice)) {
            String[] userChoices = userChoice.split(SysVariables.SEMICOLON);
            for (String s : userChoices) {
                String[] s2 = s.split(SysVariables.COLON);
                Integer principalType = Integer.parseInt(s2[0]);
                String userIdTemp = s2[1];
                if (principalType == ItemPermissionEnum.POSITION.getValue()) {
                    Position position = positionManager.getPosition(tenantId, s2[1]).getData();
                    if (null == position) {
                        continue;
                    }
                    users = this.addUserId(users, userIdTemp);
                } else if (principalType == ItemPermissionEnum.DEPARTMENT.getValue()) {
                    List<Position> employeeList = new ArrayList<Position>();
                    this.getAllPosition(employeeList, s2[1]);
                    for (Position pTemp : employeeList) {
                        users = this.addUserId(users, pTemp.getId());
                    }
                } else if (principalType == ItemPermissionEnum.CUSTOMGROUP.getValue()) {
                    List<CustomGroupMember> list = customGroupApi.listCustomGroupMemberByGroupIdAndMemberType(tenantId, Y9LoginUserHolder.getPersonId(), s2[1], OrgTypeEnum.POSITION).getData();
                    for (CustomGroupMember pTemp : list) {
                        Position position = positionManager.getPosition(tenantId, pTemp.getMemberId()).getData();
                        if (position != null && StringUtils.isNotBlank(position.getId())) {
                            users = this.addUserId(users, position.getId());
                        }
                    }
                }
            }
        }
        List<String> result = Y9Util.stringToList(users, SysVariables.SEMICOLON);
        ListUtil.removeDuplicateWithOrder(result);
        return result;
    }

    @Override
    public Map<String, Object> reposition(String taskId, String userChoice) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, true);
        try {
            // 在一个事务中保存。taskId为空则创建新流程。
            String tenantId = Y9LoginUserHolder.getTenantId();
            TaskModel task = taskManager.findById(tenantId, taskId);
            String processInstanceId = task.getProcessInstanceId();
            List<String> userAndDeptIdList = new ArrayList<String>();
            userChoice = userChoice.substring(2, userChoice.length());
            userAndDeptIdList.add(userChoice);
            // 得到要发送节点的multiInstance，PARALLEL表示并行，SEQUENTIAL表示串行
            String multiInstance = processDefinitionManager.getNodeType(tenantId, task.getProcessDefinitionId(), task.getTaskDefinitionKey());
            Map<String, Object> variables = new HashMap<String, Object>(16);
            variables.put(SysVariables.USER, userChoice);
            if (SysVariables.PARALLEL.equals(multiInstance)) {
                List<TaskModel> taskNextList = taskManager.findByProcessInstanceId(tenantId, processInstanceId);
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
                            Map<String, Object> val = new HashMap<String, Object>();
                            val.put("val", SysVariables.REPOSITION);
                            variableManager.setVariableLocal(tenantId, taskNext.getId(), SysVariables.REPOSITION, val);
                            taskManager.complete(tenantId, taskNext.getId());
                            continue;
                        }
                        // 如果打开过文件，重定位时，办结办件人的任务，让历程有该办件人记录
                        taskManager.complete(tenantId, taskNext.getId());
                    }
                }
                TaskModel task1 = taskManager.findById(tenantId, taskId);

                task1.setAssignee(userChoice);
                taskManager.saveTask(tenantId, task1);
                // 重定位后设置新的主办人
                Map<String, Object> val = new HashMap<String, Object>();
                val.put("val", userChoice.split(SysVariables.COLON)[0]);
                variableManager.setVariableLocal(tenantId, task1.getId(), SysVariables.PARALLELSPONSOR, val);
            } else if (SysVariables.SEQUENTIAL.equals(multiInstance)) {
                // 串行
                task.setAssignee(userChoice);
                taskManager.saveTask(tenantId, task);
            } else {// 其他
                task.setAssignee(userChoice);
                taskManager.saveTask(tenantId, task);
            }
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        return map;
    }

    /**
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
    public Map<String, Object> saveAndForwarding(String itemId, String processSerialNumber, String processDefinitionKey, String userChoice, String sponsorGuid, String routeToTaskId, Map<String, Object> variables) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        List<String> userList = new ArrayList<String>();
        userList.addAll(this.parseUserChoice(userChoice));
        int num = userList.size();
        boolean tooMuch = num > 100;
        if (tooMuch) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "发送人数过多");
            return map;
        }
        Map<String, Object> map1 = this.startProcess(itemId, processSerialNumber, processDefinitionKey);
        String taskId = (String)map1.get("taskId");
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (!variables.isEmpty()) {
            variableManager.setVariables(tenantId, taskId, variables);
        }
        map = this.start4Forwarding(taskId, routeToTaskId, sponsorGuid, userList);
        return map;
    }

    @Override
    public Map<String, Object> saveAndForwardingByTaskKey(String itemId, String processSerialNumber, String processDefinitionKey, String userChoice, String sponsorGuid, String routeToTaskId, String startRouteToTaskId, Map<String, Object> variables) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<String> userList = new ArrayList<String>();
        userList.addAll(this.parseUserChoice(userChoice));
        int num = userList.size();
        boolean tooMuch = num > 100;
        if (tooMuch) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "发送人数过多");
            return map;
        }
        Map<String, Object> map1 = this.startProcessByTaskKey(itemId, processSerialNumber, processDefinitionKey, startRouteToTaskId);
        String taskId = (String)map1.get("taskId");
        if (!variables.isEmpty()) {
            variableManager.setVariables(tenantId, taskId, variables);
        }
        map = this.start4Forwarding(taskId, routeToTaskId, sponsorGuid, userList);
        return map;
    }

    @Override
    public Map<String, Object> saveAndSubmitTo(String itemId, String processSerialNumber) {
        String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
        Position position = Y9LoginUserHolder.getPosition();
        Map<String, Object> map = new HashMap<>();
        map.put(UtilConsts.SUCCESS, false);
        map.put("msg", "提交失败");
        try {
            SpmApproveItem item = spmApproveitemService.findById(itemId);
            String processDefinitionKey = item.getWorkflowGuid();
            ProcessDefinitionModel processDefinitionModel = repositoryManager.getLatestProcessDefinitionByKey(tenantId, processDefinitionKey);
            String processDefinitionId = processDefinitionModel.getId();
            String taskDefKey = itemStartNodeRoleService.getStartTaskDefKey(itemId);
            Y9Result<Map<String, String>> routeToTaskIdResult = this.parserRouteToTaskId(itemId, processSerialNumber, processDefinitionId, taskDefKey, "");
            if (!routeToTaskIdResult.isSuccess()) {
                map.put("msg", routeToTaskIdResult.getMsg());
                return map;
            }
            String routeToTaskId = routeToTaskIdResult.getData().get(SysVariables.TASKDEFKEY), routeToTaskName = routeToTaskIdResult.getData().get(SysVariables.REALTASKDEFNAME);
            String multiInstance = processDefinitionManager.getNodeType(tenantId, processDefinitionId, routeToTaskId);
            Y9Result<List<String>> userResult = this.parserUser(itemId, processDefinitionId, routeToTaskId, routeToTaskName, "", multiInstance);
            if (!userResult.isSuccess()) {
                map.put("msg", userResult.getMsg());
                return map;
            }
            Map<String, Object> startProcessMap = this.startProcess(itemId, processSerialNumber, processDefinitionKey);
            String taskId = (String)startProcessMap.get("taskId"), processInstanceId = (String)startProcessMap.get("processInstanceId");
            ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
            List<String> userList = userResult.getData();
            Map<String, Object> variables = CommonOpt.setVariables(positionId, position.getName(), routeToTaskId, userList, multiInstance);
            asyncHandleService.forwarding4Task(processInstanceId, processParam, "", "", taskId, multiInstance, variables, userList);
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "提交成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public Map<String, Object> signTaskConfig(String itemId, String processDefinitionId, String taskDefinitionKey, String processSerialNumber) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "获取成功");
            // signTask为true,则直接发送
            map.put("signTask", false);
            map.put("userChoice", "");
            map.put("onePerson", false);
            boolean searchPerson = true;
            String tenantId = Y9LoginUserHolder.getTenantId();
            String multiInstance = processDefinitionManager.getNodeType(tenantId, processDefinitionId, taskDefinitionKey);
            if (SysVariables.COMMON.equals(multiInstance)) {
                ItemTaskConf itemTaskConf = taskConfRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, processDefinitionId, taskDefinitionKey);
                ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
                // 判断是否是抢占式签收任务
                if (itemTaskConf != null && itemTaskConf.getSignTask()) {
                    map.put("signTask", true);
                    if (processParam != null && StringUtils.isNotBlank(processParam.getProcessInstanceId())) {
                        List<HistoricTaskInstanceModel> hisTaskList = historicTaskManager.findTaskByProcessInstanceIdOrByEndTimeAsc(tenantId, processParam.getProcessInstanceId(), "");
                        for (HistoricTaskInstanceModel hisTask : hisTaskList) {
                            // 获取相同任务
                            if (hisTask.getTaskDefinitionKey().equals(taskDefinitionKey)) {
                                searchPerson = false;
                                map.put("userChoice", "6:" + hisTask.getAssignee());
                                break;
                            }
                        }
                    }
                    if (searchPerson) {
                        List<OrgUnit> orgUnitList = this.getUserChoice(itemId, processDefinitionId, taskDefinitionKey, processParam != null ? processParam.getProcessInstanceId() : "");
                        if (orgUnitList.isEmpty()) {
                            map.put("signTask", false);
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
                            map.put("userChoice", userChoice);
                            if (userChoice.equals("")) {
                                map.put("signTask", false);
                            }
                        }
                    }
                } else {// signTask为false且onePerson为true则直接发送
                    List<OrgUnit> orgUnitList = this.getUserChoice(itemId, processDefinitionId, taskDefinitionKey, processParam != null ? processParam.getProcessInstanceId() : "");
                    // 只有一个人，则直接返回人员发送
                    if (orgUnitList.size() == 1 && orgUnitList.get(0).getOrgType().equals(OrgTypeEnum.POSITION)) {
                        map.put("userChoice", "6:" + orgUnitList.get(0).getId());
                        map.put("onePerson", true);
                    }
                }
            }
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "获取失败");
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 启动流程发送
     *
     * @param taskId
     * @param routeToTaskId
     * @param sponsorGuid
     * @param userList
     * @return
     */
    public Map<String, Object> start4Forwarding(String taskId, String routeToTaskId, String sponsorGuid, List<String> userList) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        String processInstanceId = "";
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            Position position = Y9LoginUserHolder.getPosition();
            TaskModel task = taskManager.findById(tenantId, taskId);
            processInstanceId = task.getProcessInstanceId();
            ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
            // 得到要发送节点的multiInstance，PARALLEL表示并行，SEQUENTIAL表示串行
            String multiInstance = processDefinitionManager.getNodeType(tenantId, task.getProcessDefinitionId(), routeToTaskId);
            Map<String, Object> variables = CommonOpt.setVariables(positionId, position.getName(), routeToTaskId, userList, multiInstance);
            // 子流程信息
            if (multiInstance.equals(SysVariables.CALLACTIVITY)) {
                Map<String, Object> initDataMap = new HashMap<String, Object>(16);
                initDataMap.put(SysVariables.PARENTPROCESSSERIALNUMBER, processParam != null ? processParam.getProcessSerialNumber() : "");
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
                taskVariable.setText("true:" + String.valueOf(num));
                taskVariableRepository.save(taskVariable);
                asyncHandleService.forwarding(tenantId, position, processInstanceId, processParam, "", sponsorGuid, taskId, multiInstance, variables, userList);
            } else if (SysVariables.SUBPROCESS.equals(multiInstance)) {
                Map<String, Object> vars = new HashMap<String, Object>(16);
                vars.put("parentTaskId", taskId);
                taskManager.createWithVariables(tenantId, position.getId(), routeToTaskId, vars, userList);
            } else {
                asyncHandleService.forwarding4Task(processInstanceId, processParam, "", sponsorGuid, taskId, multiInstance, variables, userList);
            }
            map.put("processInstanceId", processInstanceId);
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "发送成功!");
        } catch (Exception e) {
            log.error("公文发送失败！");
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "发送失败!");
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
                e2.printStackTrace();
            }
        }
        return map;
    }

    @Override
    public Map<String, Object> startProcess(String itemId, String processSerialNumber, String processDefinitionKey) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, false);
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            Map<String, Object> vars = new HashMap<String, Object>(16);
            SpmApproveItem item = spmApproveitemRepository.findById(itemId).orElse(null);
            vars.put("tenantId", tenantId);
            String startTaskDefKey = itemStartNodeRoleService.getStartTaskDefKey(itemId);
            vars.put("routeToTaskId", startTaskDefKey);
            if (item.isShowSubmitButton()) {
                ProcessDefinitionModel processDefinitionModel = repositoryManager.getLatestProcessDefinitionByKey(tenantId, processDefinitionKey);
                List<Y9FormItemBind> eformTaskBinds = y9FormItemBindService.findByItemIdAndProcDefIdAndTaskDefKey(itemId, processDefinitionModel.getId(), "");
                Map<String, Object> variables = y9FormService.getFormData4Var(eformTaskBinds.get(0).getFormId(), processSerialNumber);
                vars.putAll(variables);
            }
            TaskModel task = activitiOptService.startProcess(processSerialNumber, processDefinitionKey, item.getSystemName(), vars);
            ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
            processParam.setProcessInstanceId(task.getProcessInstanceId());
            processParam.setStartor(Y9LoginUserHolder.getPositionId());
            processParam.setStartorName(Y9LoginUserHolder.getPosition().getName());
            processParam.setSended("true");
            // 保存流程信息到ES
            process4SearchService.saveToDataCenter(tenantId, processParam, Y9LoginUserHolder.getPosition());

            processParamService.saveOrUpdate(processParam);

            // 异步处理数据
            asyncHandleService.startProcessHandle(tenantId, processSerialNumber, task.getId(), task.getProcessInstanceId(), processParam.getSearchTerm());

            map.put("processInstanceId", task.getProcessInstanceId());
            map.put("processSerialNumber", processSerialNumber);
            map.put("processDefinitionId", task.getProcessDefinitionId());
            map.put("taskId", task.getId());
            map.put("taskDefKey", task.getTaskDefinitionKey());
            map.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public Map<String, Object> startProcess(String itemId, String processSerialNumber, String processDefinitionKey, String positionIds) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, false);
        map.put("msg", "启动失败");
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            Position position = Y9LoginUserHolder.getPosition();
            Map<String, Object> vars = new HashMap<String, Object>(16);
            SpmApproveItem item = spmApproveitemRepository.findById(itemId).orElse(null);
            vars.put("tenantId", tenantId);
            String startTaskDefKey = itemStartNodeRoleService.getStartTaskDefKey(itemId);
            vars.put("routeToTaskId", startTaskDefKey);

            vars = CommonOpt.setVariables(position.getId(), position.getName(), "", Arrays.asList(positionIds.split(",")), processSerialNumber, "", vars);
            ProcessInstanceModel piModel = runtimeManager.startProcessInstanceByKey(tenantId, position.getId(), processDefinitionKey, item.getSystemName(), vars);
            // 获取运行的任务节点,这里没有考虑启动节点下一个用户任务节点是多实例的情况
            String processInstanceId = piModel.getId();
            TaskModel task = taskManager.findByProcessInstanceId(tenantId, processInstanceId).get(0);

            ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
            processParam.setProcessInstanceId(task.getProcessInstanceId());
            processParam.setStartor(Y9LoginUserHolder.getPositionId());
            processParam.setStartorName(Y9LoginUserHolder.getPosition().getName());
            processParam.setSended("true");
            // 保存流程信息到ES
            process4SearchService.saveToDataCenter(tenantId, processParam, Y9LoginUserHolder.getPosition());

            processParamService.saveOrUpdate(processParam);

            // 异步处理数据
            asyncHandleService.startProcessHandle(tenantId, processSerialNumber, task.getId(), task.getProcessInstanceId(), processParam.getSearchTerm());

            map.put("processInstanceId", task.getProcessInstanceId());
            map.put("processSerialNumber", processSerialNumber);
            map.put("processDefinitionId", task.getProcessDefinitionId());
            map.put("taskId", task.getId());
            map.put("taskDefKey", task.getTaskDefinitionKey());
            map.put("msg", "启动成功");
            map.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public Map<String, Object> startProcessByTaskKey(String itemId, String processSerialNumber, String processDefinitionKey, String startRouteToTaskId) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, false);
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            Map<String, Object> vars = new HashMap<String, Object>(16);
            SpmApproveItem item = spmApproveitemRepository.findById(itemId).orElse(null);
            vars.put("tenantId", tenantId);
            vars.put(SysVariables.ROUTETOTASKID, startRouteToTaskId);
            TaskModel task = activitiOptService.startProcess(processSerialNumber, processDefinitionKey, item.getSystemName(), vars);
            map.put("processInstanceId", task.getProcessInstanceId());
            map.put("processSerialNumber", processSerialNumber);
            map.put("processDefinitionId", task.getProcessDefinitionId());
            map.put("taskId", task.getId());
            map.put("taskDefKey", task.getTaskDefinitionKey());

            ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
            processParam.setProcessInstanceId(task.getProcessInstanceId());
            processParam.setStartor(Y9LoginUserHolder.getPositionId());
            processParam.setStartorName(Y9LoginUserHolder.getPosition().getName());
            processParam.setSended("true");
            // 保存流程信息到ES
            process4SearchService.saveToDataCenter(tenantId, processParam, Y9LoginUserHolder.getPosition());

            processParamService.saveOrUpdate(processParam);

            // 异步处理数据
            asyncHandleService.startProcessHandle(tenantId, processSerialNumber, task.getId(), task.getProcessInstanceId(), processParam.getSearchTerm());

            map.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public Map<String, Object> submitTo(String processSerialNumber, String taskId) {
        Map<String, Object> map = new HashMap<>();
        map.put(UtilConsts.SUCCESS, false);
        map.put("msg", "提交失败");
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            Position position = Y9LoginUserHolder.getPosition();
            ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
            String itemId = processParam.getItemId();
            TaskModel task = taskManager.findById(tenantId, taskId);
            if (null == task || null == task.getId()) {
                map.put("msg", "该件已被处理。");
                return map;
            }
            String processDefinitionId = task.getProcessDefinitionId(), taskDefKey = task.getTaskDefinitionKey(), processInstanceId = task.getProcessInstanceId();
            Y9Result<Map<String, String>> routeToTaskIdResult = this.parserRouteToTaskId(itemId, processSerialNumber, processDefinitionId, taskDefKey, taskId);
            if (!routeToTaskIdResult.isSuccess()) {
                map.put("msg", routeToTaskIdResult.getMsg());
                return map;
            }
            String routeToTaskId = routeToTaskIdResult.getData().get(SysVariables.TASKDEFKEY), routeToTaskName = routeToTaskIdResult.getData().get(SysVariables.REALTASKDEFNAME);
            String multiInstance = processDefinitionManager.getNodeType(tenantId, processDefinitionId, routeToTaskId);
            Y9Result<List<String>> userResult = this.parserUser(itemId, processDefinitionId, routeToTaskId, routeToTaskName, processInstanceId, multiInstance);
            if (!userResult.isSuccess()) {
                map.put("msg", userResult.getMsg());
                return map;
            }
            List<String> userList = userResult.getData();
            Map<String, Object> variables = CommonOpt.setVariables(positionId, position.getName(), routeToTaskId, userList, multiInstance);
            asyncHandleService.forwarding4Task(processInstanceId, processParam, "", "", taskId, multiInstance, variables, userList);
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "提交成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

}
