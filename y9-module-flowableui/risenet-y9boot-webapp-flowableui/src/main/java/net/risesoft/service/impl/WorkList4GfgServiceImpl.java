package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.*;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.tenant.TenantApi;
import net.risesoft.api.processadmin.*;
import net.risesoft.enums.*;
import net.risesoft.model.itemadmin.*;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Position;
import net.risesoft.model.processadmin.IdentityLinkModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.service.WorkDayService;
import net.risesoft.service.WorkList4GfgService;
import net.risesoft.util.SysVariables;
import net.risesoft.util.ToolUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9Util;

/**
 * @author qinman
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkList4GfgServiceImpl implements WorkList4GfgService {

    private static final Map<String, Object> map = new HashMap<>();
    private final ItemApi itemApi;
    private final VariableApi variableApi;
    private final HistoricTaskApi historicTaskApi;
    private final ProcessParamApi processParamApi;
    private final ProcessDefinitionApi processDefinitionApi;
    private final FormDataApi formDataApi;
    private final ItemTodoApi itemTodoApi;
    private final ItemDoingApi itemDoingApi;
    private final ItemDoneApi itemDoneApi;
    private final ItemRecycleApi itemRecycleApi;
    private final ItemHaveDoneApi itemHaveDoneApi;
    private final ItemAllApi itemAllApi;
    private final TaskApi taskApi;
    private final OrgUnitApi orgUnitApi;
    private final IdentityApi identityApi;
    private final TaskRelatedApi taskRelatedApi;
    private final WorkDayService workDayService;
    private final SignDeptDetailApi signDeptDetailApi;
    private final UrgeInfoApi urgeInfoApi;
    private final OptionClassApi optionClassApi;
    private final TenantApi tenantApi;

    @Override
    public Y9Page<Map<String, Object>> allList(String itemId, String searchMapStr, boolean isOrg, Integer page,
        Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            Y9Page<ActRuDetailModel> itemPage;
            if (StringUtils.isBlank(searchMapStr)) {
                if (isOrg) {
                    itemPage = itemAllApi.findBySystemName(tenantId, positionId, item.getSystemName(), page, rows);
                } else {
                    itemPage =
                        itemAllApi.findByUserIdAndSystemName(tenantId, positionId, item.getSystemName(), page, rows);
                }
            } else {
                if (isOrg) {
                    itemPage = itemAllApi.searchBySystemName(tenantId, positionId, item.getSystemName(), searchMapStr,
                        page, rows);
                } else {
                    itemPage = itemAllApi.searchByUserIdAndSystemName(tenantId, positionId, item.getSystemName(),
                        searchMapStr, page, rows);
                }
            }
            List<ActRuDetailModel> list = itemPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<ActRuDetailModel> taslList = objectMapper.convertValue(list, new TypeReference<>() {});
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp;
            ProcessParamModel processParam;
            String processInstanceId;
            Map<String, Object> formData;
            for (ActRuDetailModel ardModel : taslList) {
                mapTemp = new HashMap<>(16);
                String taskId = ardModel.getTaskId();
                processInstanceId = ardModel.getProcessInstanceId();
                try {
                    String processSerialNumber = ardModel.getProcessSerialNumber();
                    mapTemp.put("id", processSerialNumber);
                    mapTemp.put("serialNumber", ++serialNumber);
                    mapTemp.put("canOpen", true);
                    mapTemp.put("children", List.of());
                    mapTemp.put("actRuDetailId", ardModel.getId());
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    mapTemp.put("executionId", ardModel.getExecutionId());
                    processParam = processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    mapTemp.put("systemCNName", processParam.getSystemCnName());
                    mapTemp.put("bureauName", processParam.getHostDeptName());
                    mapTemp.put("itemId", processParam.getItemId());
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("taskId", taskId);
                    formData = formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
                    mapTemp.putAll(handleFormData(formData));
                    List<UrgeInfoModel> urgeInfoList =
                        urgeInfoApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                    List<TaskRelatedModel> taskRelatedList;
                    if (Objects.equals(ardModel.getStatus(), ActRuDetailStatusEnum.TODO.getValue()) && !isOrg) {
                        mapTemp.put(SysVariables.ITEMBOX, ItemBoxTypeEnum.TODO.getValue());
                        taskRelatedList = getTaskRelated4Todo(ardModel, formData);
                        mapTemp.putAll(getTaskNameAndUserName4Todo(ardModel));
                    } else {
                        List<SignDeptDetailModel> signDeptDetailList =
                            signDeptDetailApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                        if (!ardModel.isEnded()) {
                            mapTemp.put(SysVariables.ITEMBOX,
                                isOrg ? ItemBoxTypeEnum.MONITOR_DOING.getValue() : ItemBoxTypeEnum.DOING.getValue());
                            taskRelatedList =
                                getTaskRelated4Doing(processSerialNumber, "", formData, ardModel.isSub(), urgeInfoList);
                            List<TaskModel> taskList =
                                taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                            mapTemp.putAll(getTaskNameAndUserName4Doing(processParam, taskList, signDeptDetailList));
                            mapTemp.put("children", getChildren(ardModel, mapTemp, formData, taskList, urgeInfoList,
                                signDeptDetailList, false));
                        } else {
                            mapTemp.put(SysVariables.ITEMBOX,
                                isOrg ? ItemBoxTypeEnum.MONITOR_DONE.getValue() : ItemBoxTypeEnum.DONE.getValue());
                            taskRelatedList = getTaskRelated4Done(ardModel, formData, ardModel.isSub(), urgeInfoList);
                            mapTemp.putAll(getTaskNameAndUserName4Done(processParam));
                            mapTemp.put("children", getChildren(ardModel, mapTemp, formData, List.of(), urgeInfoList,
                                signDeptDetailList, false));
                        }
                    }
                    mapTemp.put(SysVariables.TASKRELATEDLIST, taskRelatedList);
                } catch (Exception e) {
                    LOGGER.error("获取已办列表失败" + processInstanceId, e);
                }
                items.add(mapTemp);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取待办异常", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

    @Override
    public Y9Page<Map<String, Object>> allTodoList(QueryParamModel queryParamModel) {
        Y9Page<ActRuDetailModel> itemPage;
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            itemPage = itemTodoApi.findByUserId(tenantId, positionId, queryParamModel);
            List<ActRuDetailModel> list = itemPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<ActRuDetailModel> taslList = objectMapper.convertValue(list, new TypeReference<>() {});
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (queryParamModel.getPage() - 1) * queryParamModel.getRows();
            Map<String, Object> mapTemp;
            ProcessParamModel processParam;
            String processInstanceId;
            Map<String, Object> formData;
            for (ActRuDetailModel ardModel : taslList) {
                mapTemp = new HashMap<>(16);
                String taskId = ardModel.getTaskId();
                processInstanceId = ardModel.getProcessInstanceId();
                try {
                    String processSerialNumber = ardModel.getProcessSerialNumber();
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    processParam = processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                    mapTemp.put("serialNumber", ++serialNumber);
                    mapTemp.put("id", processSerialNumber);
                    mapTemp.put("actRuDetailId", ardModel.getId());
                    mapTemp.put("systemCNName", processParam.getSystemCnName());
                    mapTemp.put("number", processParam.getCustomNumber());
                    mapTemp.put("title", processParam.getTitle());
                    mapTemp.put("bureauName", processParam.getHostDeptName());
                    mapTemp.put("taskName", ardModel.getTaskDefName());
                    mapTemp.put("itemId", processParam.getItemId());
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("taskId", taskId);
                    mapTemp.put("taskAssignee", ardModel.getAssigneeName());
                    formData = formDataApi.getData(tenantId, ardModel.getItemId(), processSerialNumber).getData();
                    mapTemp.put(SysVariables.TASKRELATEDLIST, getTaskRelated4Todo(ardModel, formData));
                    mapTemp.put(SysVariables.ITEMBOX, ItemBoxTypeEnum.TODO.getValue());
                } catch (Exception e) {
                    LOGGER.error("获取待办列表失败" + processInstanceId, e);
                }
                items.add(mapTemp);
            }
            return Y9Page.success(queryParamModel.getPage(), itemPage.getTotalPages(), itemPage.getTotal(), items,
                "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取待办列表失败", e);
        }
        return Y9Page.success(queryParamModel.getPage(), 0, 0, new ArrayList<>(), "获取列表失败");
    }

    @Override
    public Y9Page<Map<String, Object>> doingList4All(String itemId, String searchMapStr, Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            Y9Page<ActRuDetailModel> itemPage;
            if (StringUtils.isBlank(searchMapStr)) {
                itemPage = itemDoingApi.findBySystemName(tenantId, item.getSystemName(), page, rows);
            } else {
                itemPage = itemDoingApi.searchBySystemName(tenantId, item.getSystemName(), searchMapStr, page, rows);
            }
            List<ActRuDetailModel> list = itemPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<ActRuDetailModel> taslList = objectMapper.convertValue(list, new TypeReference<>() {});
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp;
            ProcessParamModel processParam;
            String processInstanceId;
            Map<String, Object> formData;
            for (ActRuDetailModel ardModel : taslList) {
                mapTemp = new HashMap<>(16);
                String taskId = ardModel.getTaskId();
                processInstanceId = ardModel.getProcessInstanceId();
                try {
                    String processSerialNumber = ardModel.getProcessSerialNumber();
                    mapTemp.put("id", processSerialNumber);
                    mapTemp.put("serialNumber", ++serialNumber);
                    mapTemp.put("canOpen", true);
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    processParam = processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    List<SignDeptDetailModel> signDeptDetailList =
                        signDeptDetailApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                    mapTemp.putAll(getTaskNameAndUserName4Doing(processParam, taskList, signDeptDetailList));
                    mapTemp.put("systemCNName", processParam.getSystemCnName());
                    mapTemp.put("bureauName", processParam.getHostDeptName());
                    mapTemp.put("itemId", processParam.getItemId());
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("processDefinitionId", ardModel.getProcessDefinitionId());
                    mapTemp.put("taskId", taskId);
                    formData = formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
                    mapTemp.putAll(handleFormData(formData));
                    List<UrgeInfoModel> urgeInfoList =
                        urgeInfoApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                    mapTemp.put(SysVariables.TASKRELATEDLIST, getTaskRelated4Doing(processSerialNumber,
                        ardModel.getExecutionId(), formData, false, urgeInfoList));
                    mapTemp.put(SysVariables.ITEMBOX, ItemBoxTypeEnum.MONITOR_DOING.getValue());
                    mapTemp.put("children",
                        getChildren(ardModel, mapTemp, formData, taskList, urgeInfoList, signDeptDetailList, true));
                } catch (Exception e) {
                    LOGGER.error("获取在办列表失败" + processInstanceId, e);
                }
                items.add(mapTemp);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取待办异常", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

    @Override
    public Y9Page<Map<String, Object>> doingList4Dept(String itemId, boolean isBureau, String searchMapStr,
        Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            Position position = Y9LoginUserHolder.getPosition();
            OrgUnit bureau = orgUnitApi.getBureau(tenantId, positionId).getData();
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            Y9Page<ActRuDetailModel> itemPage;
            String deptId = isBureau ? bureau.getId() : position.getParentId();
            if (StringUtils.isBlank(searchMapStr)) {
                itemPage = itemDoingApi.findByDeptIdAndSystemName(tenantId, deptId, isBureau, item.getSystemName(),
                    page, rows);
            } else {
                itemPage = itemDoingApi.searchByDeptIdAndSystemName(tenantId, deptId, isBureau, item.getSystemName(),
                    searchMapStr, page, rows);
            }
            List<ActRuDetailModel> list = itemPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<ActRuDetailModel> taslList = objectMapper.convertValue(list, new TypeReference<>() {});
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp;
            ProcessParamModel processParam;
            String processInstanceId;
            Map<String, Object> formData;
            for (ActRuDetailModel ardModel : taslList) {
                mapTemp = new HashMap<>(16);
                String taskId = ardModel.getTaskId();
                processInstanceId = ardModel.getProcessInstanceId();
                try {
                    String processSerialNumber = ardModel.getProcessSerialNumber();
                    mapTemp.put("id", processSerialNumber);
                    mapTemp.put("serialNumber", ++serialNumber);
                    mapTemp.put("canOpen", true);
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    processParam = processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    List<SignDeptDetailModel> signDeptDetailList =
                        signDeptDetailApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                    List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    boolean isSignDept = signDeptDetailList.stream()
                        .anyMatch(signDeptDetailModel -> signDeptDetailModel.getDeptId().equals(bureau.getId()));
                    mapTemp.putAll(getTaskNameAndUserName4Doing(processParam, taskList, signDeptDetailList));
                    mapTemp.put("systemCNName", processParam.getSystemCnName());
                    mapTemp.put("bureauName", processParam.getHostDeptName());
                    mapTemp.put("itemId", processParam.getItemId());
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("processDefinitionId", ardModel.getProcessDefinitionId());
                    mapTemp.put("taskId", taskId);
                    /*
                     * 暂时取表单所有字段数据
                     */
                    formData = formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
                    mapTemp.putAll(handleFormData(formData));
                    mapTemp.put(SysVariables.ITEMBOX, ItemBoxTypeEnum.MONITOR_DOING.getValue());
                    List<UrgeInfoModel> urgeInfoList =
                        urgeInfoApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                    mapTemp.put(SysVariables.TASKRELATEDLIST,
                        getTaskRelated4Doing(processSerialNumber, "", formData, isSignDept, urgeInfoList));
                    mapTemp.put("children",
                        getChildren(ardModel, mapTemp, formData, taskList, urgeInfoList, signDeptDetailList, true));
                } catch (Exception e) {
                    LOGGER.error("获取在办列表失败" + processInstanceId, e);
                }
                items.add(mapTemp);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取待办异常", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

    @Override
    public Y9Page<Map<String, Object>> doingList4DuBan(String itemId, String searchMapStr, Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            Y9Page<ActRuDetailModel> itemPage =
                itemDoingApi.searchBySystemName(tenantId, item.getSystemName(), searchMapStr, page, rows);
            List<ActRuDetailModel> list = itemPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<ActRuDetailModel> taslList = objectMapper.convertValue(list, new TypeReference<>() {});
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp;
            ProcessParamModel processParam;
            String processInstanceId;
            Map<String, Object> formData;
            for (ActRuDetailModel ardModel : taslList) {
                mapTemp = new HashMap<>(16);
                String taskId = ardModel.getTaskId();
                processInstanceId = ardModel.getProcessInstanceId();
                try {
                    String processSerialNumber = ardModel.getProcessSerialNumber();
                    mapTemp.put("id", processSerialNumber);
                    mapTemp.put("serialNumber", ++serialNumber);
                    mapTemp.put("canOpen", true);
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    processParam = processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    List<SignDeptDetailModel> signDeptDetailList =
                        signDeptDetailApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                    mapTemp.putAll(getTaskNameAndUserName4Doing(processParam, taskList, signDeptDetailList));
                    mapTemp.put("systemCNName", processParam.getSystemCnName());
                    mapTemp.put("bureauName", processParam.getHostDeptName());
                    mapTemp.put("itemId", processParam.getItemId());
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("taskId", taskId);
                    formData = formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
                    mapTemp.putAll(handleFormData(formData));
                    mapTemp.put(SysVariables.ITEMBOX, ItemBoxTypeEnum.MONITOR_DOING.getValue());
                    /*
                     *催办信息
                     */
                    List<UrgeInfoModel> urgeInfoList =
                        urgeInfoApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                    mapTemp.put(SysVariables.TASKRELATEDLIST,
                        getTaskRelated4Doing(processSerialNumber, "", formData, false, urgeInfoList));
                    mapTemp.put("children",
                        getChildren(ardModel, mapTemp, formData, taskList, urgeInfoList, signDeptDetailList, true));
                } catch (Exception e) {
                    LOGGER.error("获取在办列表失败" + processInstanceId, e);
                }
                items.add(mapTemp);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取待办异常", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

    @Override
    public Y9Page<Map<String, Object>> doneList(String itemId, Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            Y9Page<ActRuDetailModel> itemPage =
                itemDoneApi.findByUserIdAndSystemName(tenantId, positionId, item.getSystemName(), page, rows);
            List<ActRuDetailModel> list = itemPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<ActRuDetailModel> taslList = objectMapper.convertValue(list, new TypeReference<>() {});
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp;
            ProcessParamModel processParam;
            String processInstanceId;
            Map<String, Object> formData;
            for (ActRuDetailModel ardModel : taslList) {
                mapTemp = new HashMap<>(16);
                String taskId = ardModel.getTaskId();
                processInstanceId = ardModel.getProcessInstanceId();
                try {
                    String processSerialNumber = ardModel.getProcessSerialNumber();
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    processParam = processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    mapTemp.put("serialNumber", ++serialNumber);
                    mapTemp.put("taskId", taskId);
                    mapTemp.put("id", processSerialNumber);
                    mapTemp.put("systemCNName", processParam.getSystemCnName());
                    mapTemp.put("bureauName", processParam.getHostDeptName());
                    mapTemp.put("taskName", "已办结");
                    mapTemp.put("itemId", processParam.getItemId());
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("completer",
                        StringUtils.isBlank(processParam.getCompleter()) ? "无" : processParam.getCompleter());
                    /*
                     * 暂时取表单所有字段数据
                     */
                    formData = formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
                    mapTemp.putAll(handleFormData(formData));
                    mapTemp.put(SysVariables.ITEMBOX, ItemBoxTypeEnum.DONE.getValue());
                } catch (Exception e) {
                    LOGGER.error("获取待办列表失败" + processInstanceId, e);
                }
                items.add(mapTemp);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取待办异常", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

    @Override
    public Y9Page<Map<String, Object>> doneList4All(String itemId, String searchMapStr, Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            Y9Page<ActRuDetailModel> itemPage;
            if (StringUtils.isBlank(searchMapStr)) {
                itemPage = itemDoneApi.findBySystemName(tenantId, item.getSystemName(), page, rows);
            } else {
                itemPage = itemDoneApi.searchBySystemName(tenantId, item.getSystemName(), searchMapStr, page, rows);
            }
            List<ActRuDetailModel> list = itemPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<ActRuDetailModel> taslList = objectMapper.convertValue(list, new TypeReference<>() {});
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp;
            ProcessParamModel processParam;
            String processInstanceId;
            Map<String, Object> formData;
            for (ActRuDetailModel ardModel : taslList) {
                mapTemp = new HashMap<>(16);
                String taskId = ardModel.getTaskId();
                processInstanceId = ardModel.getProcessInstanceId();
                try {
                    String processSerialNumber = ardModel.getProcessSerialNumber();
                    mapTemp.put("id", processSerialNumber);
                    mapTemp.put("serialNumber", ++serialNumber);
                    mapTemp.put("canOpen", true);
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    processParam = processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    mapTemp.put("taskId", taskId);
                    mapTemp.put("systemCNName", processParam.getSystemCnName());
                    mapTemp.put("bureauName", processParam.getHostDeptName());
                    mapTemp.put("taskName", "已办结");
                    mapTemp.put("itemId", processParam.getItemId());
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("completer",
                        StringUtils.isBlank(processParam.getCompleter()) ? "无" : processParam.getCompleter());
                    formData = formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
                    mapTemp.putAll(handleFormData(formData));
                    List<UrgeInfoModel> urgeInfoList =
                        urgeInfoApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                    mapTemp.put(SysVariables.TASKRELATEDLIST,
                        getTaskRelated4Done(ardModel, formData, false, urgeInfoList));
                    mapTemp.put(SysVariables.ITEMBOX, ItemBoxTypeEnum.MONITOR_DONE.getValue());
                    List<SignDeptDetailModel> signDeptDetailList = signDeptDetailApi
                        .findByProcessSerialNumber(Y9LoginUserHolder.getTenantId(), ardModel.getProcessSerialNumber())
                        .getData();
                    mapTemp.put("children",
                        getChildren(ardModel, mapTemp, formData, List.of(), urgeInfoList, signDeptDetailList, true));
                } catch (Exception e) {
                    LOGGER.error("获取待办列表失败" + processInstanceId, e);
                }
                items.add(mapTemp);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取待办异常", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

    @Override
    public Y9Page<Map<String, Object>> doneList4Dept(String itemId, boolean isBureau, String searchMapStr, Integer page,
        Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            Position position = Y9LoginUserHolder.getPosition();
            OrgUnit bureau = orgUnitApi.getBureau(tenantId, positionId).getData();
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            Y9Page<ActRuDetailModel> itemPage;
            String deptId = isBureau ? bureau.getId() : position.getParentId();
            if (StringUtils.isBlank(searchMapStr)) {
                itemPage =
                    itemDoneApi.findByDeptIdAndSystemName(tenantId, deptId, isBureau, item.getSystemName(), page, rows);
            } else {
                itemPage = itemDoneApi.searchByDeptIdAndSystemName(tenantId, deptId, isBureau, item.getSystemName(),
                    searchMapStr, page, rows);
            }
            List<ActRuDetailModel> list = itemPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<ActRuDetailModel> taslList = objectMapper.convertValue(list, new TypeReference<>() {});
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp;
            ProcessParamModel processParam;
            String processInstanceId;
            Map<String, Object> formData;
            for (ActRuDetailModel ardModel : taslList) {
                mapTemp = new HashMap<>(16);
                String taskId = ardModel.getTaskId();
                processInstanceId = ardModel.getProcessInstanceId();
                try {
                    String processSerialNumber = ardModel.getProcessSerialNumber();
                    mapTemp.put("id", processSerialNumber);
                    mapTemp.put("serialNumber", ++serialNumber);
                    mapTemp.put("canOpen", true);
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    processParam = processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    mapTemp.put("taskId", taskId);
                    mapTemp.put("systemCNName", processParam.getSystemCnName());
                    mapTemp.put("bureauName", processParam.getHostDeptName());
                    mapTemp.put("taskName", "已办结");
                    mapTemp.put("itemId", processParam.getItemId());
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("completer",
                        StringUtils.isBlank(processParam.getCompleter()) ? "无" : processParam.getCompleter());
                    /*
                     * 暂时取表单所有字段数据
                     */
                    formData = formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
                    mapTemp.putAll(handleFormData(formData));
                    mapTemp.put(SysVariables.ITEMBOX, isBureau ? ItemBoxTypeEnum.MONITOR_DONE_BUREAU.getValue()
                        : ItemBoxTypeEnum.MONITOR_DONE_DEPT.getValue());
                    List<SignDeptDetailModel> signDeptDetailList =
                        signDeptDetailApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                    boolean isSignDept = signDeptDetailList.stream()
                        .anyMatch(signDeptDetailModel -> signDeptDetailModel.getDeptId().equals(bureau.getId()));
                    List<UrgeInfoModel> urgeInfoList =
                        urgeInfoApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                    mapTemp.put(SysVariables.TASKRELATEDLIST,
                        getTaskRelated4Done(ardModel, formData, isSignDept, urgeInfoList));
                    mapTemp.put("children",
                        getChildren(ardModel, mapTemp, formData, List.of(), urgeInfoList, signDeptDetailList, true));
                } catch (Exception e) {
                    LOGGER.error("获取待办列表失败" + processInstanceId, e);
                }
                items.add(mapTemp);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取待办异常", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

    /**
     * 当并行的时候，会获取到多个task，为了并行时当前办理人显示多人，而不是显示多条记录，需要分开分别进行处理
     *
     * @return List<String>
     */
    private List<String> getAssigneeIdsAndAssigneeNames(List<TaskModel> taskList) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String assigneeNames = "";
        List<String> list = new ArrayList<>();
        int i = 0;
        for (TaskModel task : taskList) {
            if (StringUtils.isEmpty(assigneeNames)) {
                String assignee = task.getAssignee();
                if (StringUtils.isNotBlank(assignee)) {
                    OrgUnit personTemp = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
                    if (personTemp != null) {
                        assigneeNames = personTemp.getName();
                        i += 1;
                    }
                } else {// 处理单实例未签收的当前办理人显示
                    List<IdentityLinkModel> iList =
                        identityApi.getIdentityLinksForTask(tenantId, task.getId()).getData();
                    if (!iList.isEmpty()) {
                        int j = 0;
                        for (IdentityLinkModel identityLink : iList) {
                            String assigneeId = identityLink.getUserId();
                            OrgUnit ownerUser = orgUnitApi
                                .getOrgUnitPersonOrPosition(Y9LoginUserHolder.getTenantId(), assigneeId).getData();
                            if (j < 5) {
                                assigneeNames = Y9Util.genCustomStr(assigneeNames, ownerUser.getName(), "、");
                            } else {
                                assigneeNames = assigneeNames + "等，共" + iList.size() + "人";
                                break;
                            }
                            j++;
                        }
                    }
                }
            } else {
                String assignee = task.getAssignee();
                if (i < 5) {
                    if (StringUtils.isNotBlank(assignee)) {
                        OrgUnit personTemp = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
                        if (personTemp != null) {
                            // 并行时，领导选取时存在顺序，因此这里也存在顺序
                            assigneeNames = Y9Util.genCustomStr(assigneeNames, personTemp.getName(), "、");
                            i += 1;
                        }
                    }
                }
            }
        }
        if (taskList.size() > 5) {
            assigneeNames += "等，共" + taskList.size() + "人";
        }
        list.add(assigneeNames);
        return list;
    }

    /**
     * 获取流程的会签信息
     *
     * @param ardModel 流转信息
     * @param parentMap 主办流程相关数据
     * @param taskList 当前流程正在运行的所有任务
     * @param urgeInfoList 当前流程的所有催办信息
     * @return List<Map < String, Object>> 会签信息
     */
    private List<Map<String, Object>> getChildren(ActRuDetailModel ardModel, Map<String, Object> parentMap,
        Map<String, Object> formData, List<TaskModel> taskList, List<UrgeInfoModel> urgeInfoList,
        List<SignDeptDetailModel> signDeptDetailList, boolean isAdmin) {
        List<Map<String, Object>> childrenList = new ArrayList<>();
        AtomicInteger count = new AtomicInteger(0);
        signDeptDetailList.forEach(sdd -> {
            List<String> taskNameAndAssigneeNames = getTaskNameAndUserName4SignDept(taskList, sdd);
            Map<String, Object> childrenMap = new HashMap<>(parentMap);
            childrenMap.put("id", sdd.getId());
            childrenMap.put("isSub", true);
            childrenMap.put("canOpen", false);
            childrenMap.put("serialNumber", count.incrementAndGet());
            childrenMap.put("taskName", taskNameAndAssigneeNames.get(0));
            childrenMap.put("taskAssignee", taskNameAndAssigneeNames.get(1));
            childrenMap.put("children", List.of());
            childrenMap.put("status", sdd.getStatus());
            childrenMap.put("bureauName", sdd.getDeptName());
            childrenMap.put(SysVariables.PROCESSSERIALNUMBER, ardModel.getProcessSerialNumber());
            childrenMap.put("executionId", sdd.getExecutionId());
            if (ardModel.isEnded()) {
                childrenMap.put(SysVariables.TASKRELATEDLIST,
                    getTaskRelated4Done(ardModel, formData, true, urgeInfoList));
            } else {
                childrenMap.put(SysVariables.TASKRELATEDLIST, getTaskRelated4Doing(ardModel.getProcessSerialNumber(),
                    sdd.getExecutionId(), formData, true, urgeInfoList));
            }
            if (isAdmin) {
                childrenMap.put("canOpen", true);
            } else {
                if (!ardModel.isSub()) {
                    childrenMap.put("canOpen", true);
                } else {
                    if (sdd.getExecutionId().equals(ardModel.getExecutionId())) {
                        childrenMap.put("canOpen", true);
                    }
                }
            }
            childrenList.add(childrenMap);
        });
        return childrenList;
    }

    private Map<String, Object> getTaskNameAndUserName4Doing(ProcessParamModel processParam, List<TaskModel> taskList,
        List<SignDeptDetailModel> signDeptDetailList) {
        String tenantId = Y9LoginUserHolder.getTenantId(), processInstanceId = processParam.getProcessInstanceId();
        String userName = "--", taskName = "--";
        Map<String, Object> map = new HashMap<>();
        boolean isSign = signDeptDetailList.stream()
            .anyMatch(sdd -> sdd.getStatus().equals(SignDeptDetailStatusEnum.DOING.getValue()));
        // 当前节点如果是子流程的节点
        if (isSign) {
            taskName = signDeptDetailList.get(0).getTaskName();
            userName = signDeptDetailList.get(0).getSenderName();
        } else {
            if (taskList.isEmpty()) {
                taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            }
            List<String> listTemp = getAssigneeIdsAndAssigneeNames(taskList);
            taskName = taskList.get(0).getName();
            userName = listTemp.get(0);
        }
        map.put("taskName", taskName);
        map.put("taskAssignee", userName);
        return map;
    }

    private Map<String, Object> getTaskNameAndUserName4Done(ProcessParamModel processParam) {
        Map<String, Object> map = new HashMap<>();
        map.put("taskName", "已办结");
        map.put("taskAssignee", processParam.getCompleter());
        return map;
    }

    /**
     * 返回会签流程的当前办理人和当前办理环节
     *
     * @param taskList 当前流程正在运行的所有任务
     * @param signDeptDetail 会签详情
     * @return List<String>
     */
    private List<String> getTaskNameAndUserName4SignDept(List<TaskModel> taskList, SignDeptDetailModel signDeptDetail) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String taskName = "", assigneeNames = "";
        List<String> list = new ArrayList<>();
        int i = 0;
        for (TaskModel task : taskList) {
            if (!task.getExecutionId().equals(signDeptDetail.getExecutionId())) {
                continue;
            }
            taskName = task.getName();
            if (StringUtils.isEmpty(assigneeNames)) {
                String assignee = task.getAssignee();
                if (StringUtils.isNotBlank(assignee)) {
                    OrgUnit personTemp = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
                    if (personTemp != null) {
                        assigneeNames = personTemp.getName();
                        i += 1;
                    }
                } else {// 处理单实例未签收的当前办理人显示
                    List<IdentityLinkModel> iList =
                        identityApi.getIdentityLinksForTask(tenantId, task.getId()).getData();
                    if (!iList.isEmpty()) {
                        int j = 0;
                        for (IdentityLinkModel identityLink : iList) {
                            String assigneeId = identityLink.getUserId();
                            OrgUnit ownerUser = orgUnitApi
                                .getOrgUnitPersonOrPosition(Y9LoginUserHolder.getTenantId(), assigneeId).getData();
                            if (j < 5) {
                                assigneeNames = Y9Util.genCustomStr(assigneeNames, ownerUser.getName(), "、");
                            } else {
                                assigneeNames = assigneeNames + "等，共" + iList.size() + "人";
                                break;
                            }
                            j++;
                        }
                    }
                }
            } else {
                String assignee = task.getAssignee();
                if (i < 5) {
                    if (StringUtils.isNotBlank(assignee)) {
                        OrgUnit personTemp = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
                        if (personTemp != null) {
                            assigneeNames = Y9Util.genCustomStr(assigneeNames, personTemp.getName(), "、");
                            i += 1;
                        }
                    }
                }
            }
        }
        if (taskList.size() > 5) {
            assigneeNames += "等，共" + taskList.size() + "人";
        }
        list.add(StringUtils.isNotBlank(taskName) ? taskName
            : signDeptDetail.getStatus().equals(SignDeptDetailStatusEnum.DELETED_DONE.getValue()) ? "会签结束(减签)"
                : "会签结束");
        list.add(StringUtils.isBlank(assigneeNames) ? "无" : assigneeNames);
        return list;
    }

    private Map<String, Object> getTaskNameAndUserName4Todo(ActRuDetailModel ardModel) {
        Map<String, Object> map = new HashMap<>();
        map.put("taskName", ardModel.getTaskDefName());
        map.put("taskAssignee", ardModel.getAssigneeName());
        return map;
    }

    /**
     * 1、红绿灯
     *
     * @param formData
     * @return
     */
    private List<TaskRelatedModel> getTaskRelated4Dbsx(Map<String, Object> formData) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            List<TaskRelatedModel> taskRelatedList = new ArrayList<>();
            // 督办时限：红绿灯,这里根据表单中的字段督办时限来判断
            Object dbsx = formData.get(TableColumnEnum.DBSX.getValue());
            if (ToolUtil.isObjectNotNullAndStringNotEmpty(dbsx)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                TaskRelatedModel taskRelatedModel =
                    workDayService.getLightColor(new Date(), sdf.parse(String.valueOf(dbsx)));
                if (null != taskRelatedModel) {
                    taskRelatedList.add(taskRelatedModel);
                }
            }
            return taskRelatedList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return List.of();
    }

    /**
     * 1、红绿灯 2、条码号 3、紧急程度 4、非联网登记 5、催办 6、办文说明
     *
     * @param processSerialNumber
     * @param executionId
     * @param formData
     * @param isChildren
     * @param urgeInfoList
     * @return
     */
    private List<TaskRelatedModel> getTaskRelated4Doing(String processSerialNumber, String executionId,
        Map<String, Object> formData, boolean isChildren, List<UrgeInfoModel> urgeInfoList) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            List<TaskRelatedModel> returnList = new ArrayList<>();
            List<TaskRelatedModel> taskRelatedList =
                taskRelatedApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
            List<TaskRelatedModel> banwenshumingList;
            // 办文说明
            if (isChildren) {
                banwenshumingList = taskRelatedList.stream()
                    .filter(t -> Integer.parseInt(t.getInfoType()) == Integer
                        .parseInt(TaskRelatedEnum.BANWENSHUOMING.getValue()) && t.getExecutionId().equals(executionId))
                    .collect(Collectors.toList());
            } else {
                banwenshumingList = taskRelatedList.stream()
                    .filter(t -> Integer.parseInt(t.getInfoType()) == Integer
                        .parseInt(TaskRelatedEnum.BANWENSHUOMING.getValue()) && !t.isSub())
                    .collect(Collectors.toList());
            }
            if (!banwenshumingList.isEmpty()) {
                returnList.add(banwenshumingList.get(0));
            }
            // 红绿灯
            returnList.addAll(getTaskRelated4Dbsx(formData));
            // 条码号、紧急程度、非联网登记
            returnList.addAll(getTaskRelated4Public(formData));
            // 催办
            returnList.addAll(getTaskRelated4Urge(urgeInfoList, isChildren, executionId));
            // 复原
            returnList.addAll(getTaskRelated4FuYuan(processSerialNumber, taskRelatedList));
            return returnList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return List.of();
    }

    /**
     * 条码号、紧急程度、非联网登记、催办
     *
     * @param ardModel
     * @param formData
     * @param isChildren
     * @param urgeInfoList
     * @return
     */
    private List<TaskRelatedModel> getTaskRelated4Done(ActRuDetailModel ardModel, Map<String, Object> formData,
        boolean isChildren, List<UrgeInfoModel> urgeInfoList) {
        List<TaskRelatedModel> taskRelatedList = new ArrayList<>();
        taskRelatedList.addAll(getTaskRelated4Public(formData));
        taskRelatedList.addAll(getTaskRelated4Urge(urgeInfoList, isChildren, ardModel.getExecutionId()));
        // 复原
        taskRelatedList.addAll(getTaskRelated4FuYuan(ardModel.getProcessSerialNumber(), List.of()));
        return taskRelatedList;
    }

    /**
     * 复和原的状态
     *
     * @param processSerialNumber
     * @param processRelatedList 流程所有关联信息
     * @return
     */
    private List<TaskRelatedModel> getTaskRelated4FuYuan(String processSerialNumber,
        List<TaskRelatedModel> processRelatedList) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<TaskRelatedModel> taskRelatedList = new ArrayList<>();
        try {
            if (processRelatedList.isEmpty()) {
                processRelatedList = taskRelatedApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
            }
            taskRelatedList = processRelatedList.stream()
                .filter(taskRelatedModel -> TaskRelatedEnum.FU.getValue().equals(taskRelatedModel.getInfoType())
                    || TaskRelatedEnum.YUAN.getValue().equals(taskRelatedModel.getInfoType()))
                .collect(Collectors.toList());
            return taskRelatedList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return List.of();
    }

    /**
     * 所有列表的状态 1、条码号 2、紧急程度 3、非联网登记
     *
     * @param formData
     * @return
     */
    private List<TaskRelatedModel> getTaskRelated4Public(Map<String, Object> formData) {
        try {
            List<TaskRelatedModel> taskRelatedList = new ArrayList<>();
            // 条码号
            Object thm = formData.get(TableColumnEnum.TMH.getValue());
            if (ToolUtil.isObjectNotNullAndStringNotEmpty(thm)) {
                taskRelatedList.add(new TaskRelatedModel(TaskRelatedEnum.TMH.getValue(), String.valueOf(thm)));
            }
            // 紧急程度
            Object jjcd = formData.get(TableColumnEnum.JJCD.getValue());
            if (ToolUtil.isObjectNotNullAndStringNotEmpty(jjcd)) {
                Integer jjcdInt = Integer.parseInt(String.valueOf(jjcd));
                if (jjcdInt.equals(JjcdEnum.TEJI.getValue())) {
                    taskRelatedList.add(new TaskRelatedModel(TaskRelatedEnum.JJCD.getValue(), String.valueOf(jjcd)));
                }
            }
            // 非联网登记
            Object flwdj = formData.get(TableColumnEnum.FLWDJ.getValue());
            if (ToolUtil.isObjectNotNullAndStringNotEmpty(flwdj)) {
                if (String.valueOf(flwdj).equals(FlwdjEnum.YES.getValue())) {
                    taskRelatedList.add(new TaskRelatedModel(TaskRelatedEnum.FLWDJ.getValue(), ""));
                }
            }
            // 发文文号
            Object fwwh = formData.get(TableColumnEnum.FWWH.getValue());
            if (ToolUtil.isObjectNotNullAndStringNotEmpty(fwwh)) {
                taskRelatedList.add(new TaskRelatedModel(TaskRelatedEnum.FWWH.getValue(), String.valueOf(fwwh)));
            }
            // 撤销发文文号
            Object fwwh_delete = formData.get(TableColumnEnum.FWWH_DELETE.getValue());
            if (ToolUtil.isObjectNotNullAndStringNotEmpty(fwwh_delete) && String.valueOf(fwwh_delete).equals("1")) {
                taskRelatedList
                    .add(new TaskRelatedModel(TaskRelatedEnum.FWWH_DELETE.getValue(), String.valueOf(fwwh_delete)));
            }
            return taskRelatedList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return List.of();
    }

    /**
     * 1、红绿灯 2、条码号 3、紧急程度 4、非联网登记 5、催办
     *
     * @param ardModel
     * @param formData
     * @return
     */
    private List<TaskRelatedModel> getTaskRelated4Recycle(ActRuDetailModel ardModel, Map<String, Object> formData,
        boolean isAdmin) {
        try {
            List<TaskRelatedModel> taskRelatedList = new ArrayList<>();
            // 1、红绿灯
            taskRelatedList.addAll(getTaskRelated4Dbsx(formData));
            // 2、条码号 3、紧急程度 4、非联网登记
            taskRelatedList.addAll(getTaskRelated4Public(formData));
            // 5、催办
            taskRelatedList.addAll(
                getTaskRelated4Urge(ardModel.getProcessSerialNumber(), ardModel.isSub(), ardModel.getExecutionId()));
            // 6、原、复
            taskRelatedList.addAll(getTaskRelated4FuYuan(ardModel.getProcessSerialNumber(), List.of()));
            return taskRelatedList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return List.of();
    }

    /**
     * 1、红绿灯 2、条码号 3、紧急程度 4、非联网登记 5、新 6、催办 7、多步退回 8、办文说明
     *
     * @param ardModel
     * @param formData
     * @return
     */
    private List<TaskRelatedModel> getTaskRelated4Todo(ActRuDetailModel ardModel, Map<String, Object> formData) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            List<TaskRelatedModel> processRelatedList =
                taskRelatedApi.findByProcessSerialNumber(tenantId, ardModel.getProcessSerialNumber()).getData();
            List<TaskRelatedModel> taskRelatedList = processRelatedList.stream()
                .filter(prl -> prl.getTaskId().equals(ardModel.getTaskId())).collect(Collectors.toList());
            // 7、多步退回 8、办文说明
            taskRelatedList = taskRelatedList.stream()
                .filter(
                    t -> Integer.parseInt(t.getInfoType()) < Integer.parseInt(TaskRelatedEnum.ACTIONNAME.getValue()))
                .collect(Collectors.toList());
            // 1、红绿灯
            taskRelatedList.addAll(getTaskRelated4Dbsx(formData));
            // 2、条码号 3、紧急程度 4、非联网登记
            taskRelatedList.addAll(getTaskRelated4Public(formData));
            // 5、新
            if (ardModel.isStarted()) {
                taskRelatedList.add(0, new TaskRelatedModel(TaskRelatedEnum.NEWTODO.getValue(), "新"));
            }
            // 6、催办
            taskRelatedList.addAll(
                getTaskRelated4Urge(ardModel.getProcessSerialNumber(), ardModel.isSub(), ardModel.getExecutionId()));
            // 复、原
            taskRelatedList.addAll(getTaskRelated4FuYuan(ardModel.getProcessSerialNumber(), processRelatedList));
            return taskRelatedList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return List.of();
    }

    /**
     * 所有列表的状态，主看主，子看子，监控所有件的管理员看所有 1、催办
     *
     * @param urgeInfoList
     * @param isChildren
     * @param executionId
     * @return
     */
    private List<TaskRelatedModel> getTaskRelated4Urge(List<UrgeInfoModel> urgeInfoList, boolean isChildren,
        String executionId) {
        try {
            List<TaskRelatedModel> taskRelatedList = new ArrayList<>();
            if (isChildren) {
                urgeInfoList = urgeInfoList.stream()
                    .filter(urgeInfo -> urgeInfo.isSub() && urgeInfo.getExecutionId().equals(executionId))
                    .collect(Collectors.toList());
            } else {
                urgeInfoList = urgeInfoList.stream().filter(urgeInfo -> !urgeInfo.isSub()).collect(Collectors.toList());
            }
            if (!urgeInfoList.isEmpty()) {
                taskRelatedList.add(
                    new TaskRelatedModel(TaskRelatedEnum.URGE.getValue(), Y9JsonUtil.writeValueAsString(urgeInfoList)));
            }
            return taskRelatedList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return List.of();
    }

    /**
     * 所有列表的状态，主看主，子看子，监控所有件的管理员看所有 1、催办
     *
     * @param processSerialNumber
     * @param isSub
     * @param executionId
     * @return
     */
    private List<TaskRelatedModel> getTaskRelated4Urge(String processSerialNumber, boolean isSub, String executionId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<TaskRelatedModel> taskRelatedList = new ArrayList<>();
        try {
            // 催办
            List<UrgeInfoModel> urgeInfoList =
                urgeInfoApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
            if (isSub) {
                urgeInfoList = urgeInfoList.stream()
                    .filter(urgeInfo -> urgeInfo.isSub() && urgeInfo.getExecutionId().equals(executionId))
                    .collect(Collectors.toList());
            } else {
                urgeInfoList = urgeInfoList.stream().filter(urgeInfo -> !urgeInfo.isSub()).collect(Collectors.toList());
            }
            if (!urgeInfoList.isEmpty()) {
                taskRelatedList.add(
                    new TaskRelatedModel(TaskRelatedEnum.URGE.getValue(), Y9JsonUtil.writeValueAsString(urgeInfoList)));
            }
            return taskRelatedList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return List.of();
    }

    private Map<String, Object> handleFormData(Map<String, Object> formData) {
        if (map.isEmpty()) {
            optionClassApi.findAll(Y9LoginUserHolder.getTenantId()).getData().forEach(item -> {
                map.put(item.getType() + "." + item.getCode(), item.getName());
            });
        }
        Map<String, Object> formDataTemp = new HashMap<>(formData);
        for (Map.Entry<String, Object> entry : formDataTemp.entrySet()) {
            if (null != entry.getValue()) {
                if (StringUtils.isNotBlank(entry.getValue().toString())
                    && null != map.get(entry.getKey() + "." + entry.getValue())) {
                    entry.setValue(map.get(entry.getKey() + "." + entry.getValue()));
                }
            } else {
                entry.setValue(map.get(entry.getKey() + "."));
            }
        }
        return formDataTemp;
    }

    @Override
    public Y9Page<Map<String, Object>> haveDoneList(String itemId, String searchMapStr, Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            Y9Page<ActRuDetailModel> itemPage;
            if (StringUtils.isBlank(searchMapStr)) {
                itemPage =
                    itemHaveDoneApi.findByUserIdAndSystemName(tenantId, positionId, item.getSystemName(), page, rows);
            } else {
                itemPage = itemHaveDoneApi.searchByUserIdAndSystemName(tenantId, positionId, item.getSystemName(),
                    searchMapStr, page, rows);
            }
            List<ActRuDetailModel> list = itemPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<ActRuDetailModel> taslList = objectMapper.convertValue(list, new TypeReference<>() {});
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp;
            ProcessParamModel processParam;
            String processInstanceId;
            Map<String, Object> formData;
            for (ActRuDetailModel ardModel : taslList) {
                mapTemp = new HashMap<>(16);
                processInstanceId = ardModel.getProcessInstanceId();
                try {
                    String processSerialNumber = ardModel.getProcessSerialNumber();
                    processParam = processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    mapTemp.put("id", processSerialNumber);
                    mapTemp.put("isSub", false);
                    mapTemp.put("canOpen", true);
                    mapTemp.put("serialNumber", ++serialNumber);
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    mapTemp.put("systemCNName", processParam.getSystemCnName());
                    mapTemp.put("bureauName", processParam.getHostDeptName());
                    mapTemp.put("itemId", processParam.getItemId());
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("executionId", ardModel.getExecutionId());
                    formData = formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
                    mapTemp.putAll(handleFormData(formData));
                    mapTemp.put(SysVariables.ITEMBOX, StringUtils.isBlank(processParam.getCompleter())
                        ? ItemBoxTypeEnum.DOING.getValue() : ItemBoxTypeEnum.DONE.getValue());
                    mapTemp.put("processDefinitionId", ardModel.getProcessDefinitionId());
                    List<SignDeptDetailModel> signDeptDetailList =
                        signDeptDetailApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                    List<UrgeInfoModel> urgeInfoList =
                        urgeInfoApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                    List<TaskRelatedModel> taskRelatedList;
                    if (ardModel.isEnded()) {
                        taskRelatedList = getTaskRelated4Done(ardModel, formData, false, urgeInfoList);
                        mapTemp.putAll(getTaskNameAndUserName4Done(processParam));
                        mapTemp.put("children", getChildren(ardModel, mapTemp, formData, List.of(), urgeInfoList,
                            signDeptDetailList, false));
                    } else {
                        List<TaskModel> taskList =
                            taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                        taskRelatedList = getTaskRelated4Doing(processSerialNumber, ardModel.getExecutionId(), formData,
                            false, urgeInfoList);
                        mapTemp.putAll(getTaskNameAndUserName4Doing(processParam, taskList, signDeptDetailList));
                        mapTemp.put("children", getChildren(ardModel, mapTemp, formData, taskList, urgeInfoList,
                            signDeptDetailList, false));
                    }
                    mapTemp.put(SysVariables.TASKRELATEDLIST, taskRelatedList);
                } catch (Exception e) {
                    LOGGER.error("获取已办列表失败" + processInstanceId, e);
                }
                items.add(mapTemp);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取已办异常", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

    @Override
    public Y9Page<Map<String, Object>> recycleList(String itemId, String searchMapStr, Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            Y9Page<ActRuDetailModel> itemPage;
            if (StringUtils.isBlank(searchMapStr)) {
                itemPage =
                    itemRecycleApi.findByUserIdAndSystemName(tenantId, positionId, item.getSystemName(), page, rows);
            } else {
                itemPage = itemRecycleApi.searchByUserIdAndSystemName(tenantId, positionId, item.getSystemName(),
                    searchMapStr, page, rows);
            }
            List<ActRuDetailModel> list = itemPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<ActRuDetailModel> taslList = objectMapper.convertValue(list, new TypeReference<>() {});
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp;
            ProcessParamModel processParam;
            String processInstanceId;
            Map<String, Object> formData;
            for (ActRuDetailModel ardModel : taslList) {
                mapTemp = new HashMap<>(16);
                String taskId = ardModel.getTaskId();
                processInstanceId = ardModel.getProcessInstanceId();
                try {
                    String processSerialNumber = ardModel.getProcessSerialNumber();
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    processParam = processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    mapTemp.put("id", processSerialNumber);
                    mapTemp.put("taskId", taskId);
                    mapTemp.put("serialNumber", ++serialNumber);
                    mapTemp.put("systemCNName", processParam.getSystemCnName());
                    mapTemp.put("bureauName", processParam.getHostDeptName());
                    mapTemp.put("itemId", processParam.getItemId());
                    mapTemp.put("processInstanceId", processInstanceId);
                    List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    mapTemp.put("taskName", taskList.get(0).getName());
                    List<String> listTemp = getAssigneeIdsAndAssigneeNames(taskList);
                    mapTemp.put("taskAssignee", listTemp.get(0));
                    /*
                     * 暂时取表单所有字段数据
                     */
                    formData = formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
                    mapTemp.putAll(handleFormData(formData));
                    mapTemp.put(SysVariables.ITEMBOX, ItemBoxTypeEnum.DONE.getValue());
                    mapTemp.put(SysVariables.TASKRELATEDLIST, getTaskRelated4Recycle(ardModel, formData, false));
                } catch (Exception e) {
                    LOGGER.error("获取回收站列表失败" + processInstanceId, e);
                }
                items.add(mapTemp);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取待办异常", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

    @Override
    public Y9Page<Map<String, Object>> recycleList4All(String itemId, String searchMapStr, Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            Y9Page<ActRuDetailModel> itemPage;
            if (StringUtils.isBlank(searchMapStr)) {
                itemPage = itemRecycleApi.findBySystemName(tenantId, item.getSystemName(), page, rows);
            } else {
                itemPage = itemRecycleApi.searchBySystemName(tenantId, item.getSystemName(), searchMapStr, page, rows);
            }
            List<ActRuDetailModel> list = itemPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<ActRuDetailModel> taslList = objectMapper.convertValue(list, new TypeReference<>() {});
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp;
            ProcessParamModel processParam;
            String processInstanceId;
            Map<String, Object> formData;
            for (ActRuDetailModel ardModel : taslList) {
                mapTemp = new HashMap<>(16);
                processInstanceId = ardModel.getProcessInstanceId();
                try {
                    String processSerialNumber = ardModel.getProcessSerialNumber();
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    processParam = processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    mapTemp.put("id", processSerialNumber);
                    mapTemp.put("serialNumber", ++serialNumber);
                    mapTemp.put("systemCNName", processParam.getSystemCnName());
                    mapTemp.put("bureauName", processParam.getHostDeptName());
                    mapTemp.put("itemId", processParam.getItemId());
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("taskId", "");
                    mapTemp.put("taskAssignee", "无");
                    mapTemp.put("taskName", "已办结");
                    if (!ardModel.isEnded()) {
                        List<TaskModel> taskList =
                            taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                        mapTemp.put("taskName", taskList.get(0).getName());
                        List<String> listTemp = getAssigneeIdsAndAssigneeNames(taskList);
                        mapTemp.put("taskAssignee", listTemp.get(0));
                    }
                    formData = formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
                    mapTemp.putAll(handleFormData(formData));
                    mapTemp.put(SysVariables.ITEMBOX, ItemBoxTypeEnum.MONITOR_RECYCLE.getValue());
                    mapTemp.put(SysVariables.TASKRELATEDLIST, getTaskRelated4Recycle(ardModel, formData, true));
                } catch (Exception e) {
                    LOGGER.error("获取回收站列表失败" + processInstanceId, e);
                }
                items.add(mapTemp);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取异常", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

    @Override
    public Y9Page<Map<String, Object>> recycleList4Dept(String itemId, boolean isBureau, String searchMapStr,
        Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            Position position = Y9LoginUserHolder.getPosition();
            OrgUnit bureau = orgUnitApi.getBureau(tenantId, positionId).getData();
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            Y9Page<ActRuDetailModel> itemPage;
            String deptId = isBureau ? bureau.getId() : position.getParentId();
            if (StringUtils.isBlank(searchMapStr)) {
                itemPage = itemRecycleApi.findByDeptIdAndSystemName(tenantId, deptId, isBureau, item.getSystemName(),
                    page, rows);
            } else {
                itemPage = itemRecycleApi.searchByDeptIdAndSystemName(tenantId, deptId, isBureau, item.getSystemName(),
                    searchMapStr, page, rows);
            }
            List<ActRuDetailModel> list = itemPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<ActRuDetailModel> taslList = objectMapper.convertValue(list, new TypeReference<>() {});
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp;
            ProcessParamModel processParam;
            String processInstanceId;
            Map<String, Object> formData;
            for (ActRuDetailModel ardModel : taslList) {
                mapTemp = new HashMap<>(16);
                String taskId = ardModel.getTaskId();
                processInstanceId = ardModel.getProcessInstanceId();
                try {
                    String processSerialNumber = ardModel.getProcessSerialNumber();
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    processParam = processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    mapTemp.put("id", processSerialNumber);
                    mapTemp.put("taskId", taskId);
                    mapTemp.put("serialNumber", ++serialNumber);
                    mapTemp.put("systemCNName", processParam.getSystemCnName());
                    mapTemp.put("bureauName", processParam.getHostDeptName());
                    mapTemp.put("itemId", processParam.getItemId());
                    mapTemp.put("processInstanceId", processInstanceId);
                    List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    mapTemp.put("taskName", taskList.get(0).getName());
                    List<String> listTemp = getAssigneeIdsAndAssigneeNames(taskList);
                    mapTemp.put("taskAssignee", listTemp.get(0));
                    /*
                     * 暂时取表单所有字段数据
                     */
                    formData = formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
                    mapTemp.putAll(handleFormData(formData));
                    mapTemp.put(SysVariables.ITEMBOX, ItemBoxTypeEnum.DONE.getValue());
                    mapTemp.put(SysVariables.TASKRELATEDLIST, getTaskRelated4Recycle(ardModel, formData, false));
                } catch (Exception e) {
                    LOGGER.error("获取部门回收站列表失败" + processInstanceId, e);
                }
                items.add(mapTemp);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取待办异常", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

    /**
     * 整点执行的任务
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void refreshMapScheduled() {
        tenantApi.listAllTenants().getData().forEach(tenant -> {
            optionClassApi.findAll(tenant.getId()).getData().forEach(item -> {
                map.put(item.getType() + "." + item.getCode(), item.getName());
            });
        });
    }

    @Override
    public Y9Page<Map<String, Object>> todoList(String itemId, String searchMapStr, Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            Y9Page<ActRuDetailModel> itemPage;
            if (StringUtils.isBlank(searchMapStr)) {
                itemPage =
                    itemTodoApi.findByUserIdAndSystemName(tenantId, positionId, item.getSystemName(), page, rows);
            } else {
                itemPage = itemTodoApi.searchByUserIdAndSystemName(tenantId, positionId, item.getSystemName(),
                    searchMapStr, page, rows);
            }
            List<ActRuDetailModel> list = itemPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<ActRuDetailModel> taslList = objectMapper.convertValue(list, new TypeReference<>() {});
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp;
            ProcessParamModel processParam;
            String processInstanceId;
            Map<String, Object> formData;
            for (ActRuDetailModel ardModel : taslList) {
                mapTemp = new HashMap<>(16);
                String taskId = ardModel.getTaskId();
                processInstanceId = ardModel.getProcessInstanceId();
                try {
                    String processSerialNumber = ardModel.getProcessSerialNumber();
                    processParam = processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                    mapTemp.put("id", processSerialNumber);
                    mapTemp.put("actRuDetailId", ardModel.getId());
                    mapTemp.put("serialNumber", ++serialNumber);
                    mapTemp.put("systemCNName", processParam.getSystemCnName());
                    mapTemp.put("bureauName", processParam.getHostDeptName());
                    mapTemp.put("taskName", ardModel.getTaskDefName());
                    mapTemp.put("itemId", processParam.getItemId());
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("taskId", taskId);
                    mapTemp.put("taskAssignee", ardModel.getAssigneeName());
                    formData = formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
                    mapTemp.putAll(handleFormData(formData));
                    mapTemp.put(SysVariables.TASKRELATEDLIST, getTaskRelated4Todo(ardModel, formData));
                    mapTemp.put(SysVariables.ITEMBOX, ItemBoxTypeEnum.TODO.getValue());
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                } catch (Exception e) {
                    LOGGER.error("获取待办列表失败" + processInstanceId, e);
                }
                items.add(mapTemp);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取待办异常", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

    @Override
    public Y9Page<Map<String, Object>> todoList4Other(String itemId, String searchMapStr, Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            Y9Page<ActRuDetailModel> itemPage = itemTodoApi.searchByUserIdAndSystemName4Other(tenantId, positionId,
                item.getSystemName(), searchMapStr, page, rows);
            List<ActRuDetailModel> list = itemPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<ActRuDetailModel> taslList = objectMapper.convertValue(list, new TypeReference<>() {});
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp;
            ProcessParamModel processParam;
            String processInstanceId;
            Map<String, Object> formData;
            for (ActRuDetailModel ardModel : taslList) {
                mapTemp = new HashMap<>(16);
                String taskId = ardModel.getTaskId();
                processInstanceId = ardModel.getProcessInstanceId();
                try {
                    String processSerialNumber = ardModel.getProcessSerialNumber();
                    processParam = processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                    mapTemp.put("id", processSerialNumber);
                    mapTemp.put("actRuDetailId", ardModel.getId());
                    mapTemp.put("serialNumber", ++serialNumber);
                    mapTemp.put("systemCNName", processParam.getSystemCnName());
                    mapTemp.put("bureauName", processParam.getHostDeptName());
                    mapTemp.put("taskName", ardModel.getTaskDefName());
                    mapTemp.put("itemId", processParam.getItemId());
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("taskId", taskId);
                    mapTemp.put("taskAssignee", ardModel.getAssigneeName());
                    formData = formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
                    mapTemp.putAll(handleFormData(formData));
                    mapTemp.put(SysVariables.TASKRELATEDLIST, getTaskRelated4Todo(ardModel, formData));
                    mapTemp.put(SysVariables.ITEMBOX, ItemBoxTypeEnum.TODO.getValue());
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                } catch (Exception e) {
                    LOGGER.error("获取待办列表失败" + processInstanceId, e);
                }
                items.add(mapTemp);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取待办异常", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

    @Override
    public Y9Page<Map<String, Object>> todoList4TaskDefKey(String itemId, String taskDefKey, String searchMapStr,
        Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            Y9Page<ActRuDetailModel> itemPage;
            if (StringUtils.isBlank(searchMapStr)) {
                itemPage = itemTodoApi.findByUserIdAndSystemNameAndTaskDefKey(tenantId, positionId,
                    item.getSystemName(), taskDefKey, page, rows);
            } else {
                itemPage = itemTodoApi.searchByUserIdAndSystemNameAndTaskDefKey(tenantId, positionId,
                    item.getSystemName(), taskDefKey, searchMapStr, page, rows);
            }
            List<ActRuDetailModel> list = itemPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<ActRuDetailModel> taslList = objectMapper.convertValue(list, new TypeReference<>() {});
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp;
            ProcessParamModel processParam;
            String processInstanceId;
            Map<String, Object> formData;
            for (ActRuDetailModel ardModel : taslList) {
                mapTemp = new HashMap<>(16);
                String taskId = ardModel.getTaskId();
                processInstanceId = ardModel.getProcessInstanceId();
                try {
                    String processSerialNumber = ardModel.getProcessSerialNumber();
                    processParam = processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                    mapTemp.put("id", processSerialNumber);
                    mapTemp.put("actRuDetailId", ardModel.getId());
                    mapTemp.put("systemCNName", processParam.getSystemCnName());
                    mapTemp.put("serialNumber", ++serialNumber);
                    mapTemp.put("bureauName", processParam.getHostDeptName());
                    mapTemp.put("taskName", ardModel.getTaskDefName());
                    mapTemp.put("itemId", processParam.getItemId());
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("taskId", taskId);
                    mapTemp.put("taskAssignee", ardModel.getAssigneeName());
                    formData = formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
                    mapTemp.putAll(handleFormData(formData));
                    mapTemp.put(SysVariables.TASKRELATEDLIST, getTaskRelated4Todo(ardModel, formData));
                    mapTemp.put(SysVariables.ITEMBOX, ItemBoxTypeEnum.TODO.getValue());
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                } catch (Exception e) {
                    LOGGER.error("获取待办列表失败" + processInstanceId, e);
                }
                items.add(mapTemp);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取待办异常", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }
}
