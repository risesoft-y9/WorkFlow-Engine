package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.OptionClassApi;
import net.risesoft.api.itemadmin.SignDeptDetailApi;
import net.risesoft.api.itemadmin.TaskRelatedApi;
import net.risesoft.api.itemadmin.UrgeInfoApi;
import net.risesoft.api.itemadmin.core.ItemApi;
import net.risesoft.api.itemadmin.core.ProcessParamApi;
import net.risesoft.api.itemadmin.form.FormDataApi;
import net.risesoft.api.itemadmin.worklist.ItemAllApi;
import net.risesoft.api.itemadmin.worklist.ItemDoingApi;
import net.risesoft.api.itemadmin.worklist.ItemDoneApi;
import net.risesoft.api.itemadmin.worklist.ItemHaveDoneApi;
import net.risesoft.api.itemadmin.worklist.ItemRecycleApi;
import net.risesoft.api.itemadmin.worklist.ItemTodoApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.tenant.TenantApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.consts.FlowableUiConsts;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.enums.ActRuDetailStatusEnum;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.enums.SignDeptDetailStatusEnum;
import net.risesoft.enums.TaskRelatedEnum;
import net.risesoft.model.itemadmin.QueryParamModel;
import net.risesoft.model.itemadmin.SignDeptDetailModel;
import net.risesoft.model.itemadmin.TaskRelatedModel;
import net.risesoft.model.itemadmin.UrgeInfoModel;
import net.risesoft.model.itemadmin.core.ActRuDetailModel;
import net.risesoft.model.itemadmin.core.ItemModel;
import net.risesoft.model.itemadmin.core.ProcessParamModel;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.platform.org.Position;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.service.UtilService;
import net.risesoft.service.WorkListService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;

/**
 * @author qinman
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkListServiceImpl implements WorkListService {

    private static final Map<String, Object> map = new HashMap<>();
    private final ItemApi itemApi;
    private final ProcessParamApi processParamApi;
    private final FormDataApi formDataApi;
    private final ItemTodoApi itemTodoApi;
    private final ItemDoingApi itemDoingApi;
    private final ItemDoneApi itemDoneApi;
    private final ItemRecycleApi itemRecycleApi;
    private final ItemHaveDoneApi itemHaveDoneApi;
    private final ItemAllApi itemAllApi;
    private final TaskApi taskApi;
    private final OrgUnitApi orgUnitApi;
    private final TaskRelatedApi taskRelatedApi;
    private final SignDeptDetailApi signDeptDetailApi;
    private final UrgeInfoApi urgeInfoApi;
    private final OptionClassApi optionClassApi;
    private final TenantApi tenantApi;
    private final UtilService utilService;

    @Override
    public Y9Page<Map<String, Object>> allList(String itemId, String searchMapStr, boolean isOrg, Integer page,
        Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String positionId = Y9LoginUserHolder.getPositionId();
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
            List<ActRuDetailModel> actRuDetailList = itemPage.getRows();
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            for (ActRuDetailModel ardModel : actRuDetailList) {
                Map<String, Object> itemMap = buildAllListItem(ardModel, tenantId, itemId, isOrg, ++serialNumber);
                items.add(itemMap);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取所有与当前人相关的办件列表成功！");
        } catch (Exception e) {
            LOGGER.error("获取所有与当前人相关的办件列表失败，异常：", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取所有与当前人相关的办件列表失败！");
    }

    private Map<String, Object> buildAllListItem(ActRuDetailModel ardModel, String tenantId, String itemId,
        boolean isOrg, int serialNumber) {
        Map<String, Object> mapTemp = new HashMap<>(16);
        String processInstanceId = ardModel.getProcessInstanceId();
        String taskId = ardModel.getTaskId();
        try {
            String processSerialNumber = ardModel.getProcessSerialNumber();
            mapTemp.put("id", processSerialNumber);
            mapTemp.put(FlowableUiConsts.SERIALNUMBER_KEY, serialNumber);
            mapTemp.put(FlowableUiConsts.CANOPEN_KEY, true);
            mapTemp.put(FlowableUiConsts.CHILDREN_KEY, List.of());
            mapTemp.put("actRuDetailId", ardModel.getId());
            mapTemp.put(SysVariables.PROCESS_SERIAL_NUMBER, processSerialNumber);
            mapTemp.put(FlowableUiConsts.EXECUTIONID_KEY, ardModel.getExecutionId());
            ProcessParamModel processParam =
                processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            mapTemp.put(FlowableUiConsts.SYSTEMCNNAME_KEY, processParam.getSystemCnName());
            mapTemp.put(FlowableUiConsts.BUREAUNAME_KEY, processParam.getHostDeptName());
            mapTemp.put(FlowableUiConsts.ITEMID_KEY, processParam.getItemId());
            mapTemp.put(FlowableUiConsts.PROCESSINSTANCEID_KEY, processInstanceId);
            mapTemp.put(FlowableUiConsts.TASKID_KEY, taskId);
            Map<String, Object> formData = formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
            mapTemp.putAll(handleFormData(formData));
            List<UrgeInfoModel> urgeInfoList =
                urgeInfoApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
            List<TaskRelatedModel> taskRelatedList;
            if (Objects.equals(ardModel.getStatus(), ActRuDetailStatusEnum.TODO) && !isOrg) {
                mapTemp.put(SysVariables.ITEM_BOX, ItemBoxTypeEnum.TODO.getValue());
                taskRelatedList = getTaskRelated4Todo(ardModel);
                mapTemp.putAll(getTaskNameAndUserName4Todo(ardModel));
            } else {
                List<SignDeptDetailModel> signDeptDetailList =
                    signDeptDetailApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                if (!ardModel.isEnded()) {
                    mapTemp.put(SysVariables.ITEM_BOX,
                        isOrg ? ItemBoxTypeEnum.MONITOR_DOING.getValue() : ItemBoxTypeEnum.DOING.getValue());
                    taskRelatedList = getTaskRelated4Doing(processSerialNumber, "", ardModel.isSub(), urgeInfoList);
                    List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    mapTemp.putAll(getTaskNameAndUserName4Doing(processParam, taskList, signDeptDetailList));
                    mapTemp.put(FlowableUiConsts.CHILDREN_KEY,
                        getChildren(ardModel, mapTemp, taskList, urgeInfoList, signDeptDetailList, false));
                } else {
                    mapTemp.put(SysVariables.ITEM_BOX,
                        isOrg ? ItemBoxTypeEnum.MONITOR_DONE.getValue() : ItemBoxTypeEnum.DONE.getValue());
                    taskRelatedList = getTaskRelated4Done(ardModel, ardModel.isSub(), urgeInfoList);
                    mapTemp.putAll(getTaskNameAndUserName4Done(processParam));
                    mapTemp.put(FlowableUiConsts.CHILDREN_KEY,
                        getChildren(ardModel, mapTemp, List.of(), urgeInfoList, signDeptDetailList, false));
                }
            }
            mapTemp.put(SysVariables.TASK_RELATED_LIST, taskRelatedList);
        } catch (Exception e) {
            LOGGER.error("获取所有与当前人相关的办件列表失败，processInstanceId：{}", processInstanceId, e);
        }
        return mapTemp;
    }

    @Override
    public Y9Page<Map<String, Object>> allTodoList(QueryParamModel queryParamModel) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String positionId = Y9LoginUserHolder.getPositionId();
            Y9Page<ActRuDetailModel> itemPage = itemTodoApi.findByUserId(tenantId, positionId, queryParamModel);
            List<ActRuDetailModel> actRuDetailList = itemPage.getRows();
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (queryParamModel.getPage() - 1) * queryParamModel.getRows();
            for (ActRuDetailModel ardModel : actRuDetailList) {
                Map<String, Object> itemMap = buildAllTodoItem(ardModel, tenantId, ++serialNumber);
                items.add(itemMap);
            }
            return Y9Page.success(queryParamModel.getPage(), itemPage.getTotalPages(), itemPage.getTotal(), items,
                "获取所有待办件列表成功！");
        } catch (Exception e) {
            LOGGER.error("获取所有待办列表失败，异常：", e);
        }
        return Y9Page.success(queryParamModel.getPage(), 0, 0, new ArrayList<>(), "获取所有待办列表失败！");
    }

    private Map<String, Object> buildAllTodoItem(ActRuDetailModel ardModel, String tenantId, int serialNumber) {
        Map<String, Object> mapTemp = new HashMap<>(16);
        String processInstanceId = ardModel.getProcessInstanceId();
        String taskId = ardModel.getTaskId();
        try {
            String processSerialNumber = ardModel.getProcessSerialNumber();
            mapTemp.put(SysVariables.PROCESS_SERIAL_NUMBER, processSerialNumber);
            ProcessParamModel processParam =
                processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
            mapTemp.put(FlowableUiConsts.SERIALNUMBER_KEY, serialNumber);
            mapTemp.put("id", processSerialNumber);
            mapTemp.put("actRuDetailId", ardModel.getId());
            mapTemp.put(FlowableUiConsts.SYSTEMCNNAME_KEY, processParam.getSystemCnName());
            mapTemp.put("number", processParam.getCustomNumber());
            mapTemp.put("title", processParam.getTitle());
            mapTemp.put(FlowableUiConsts.BUREAUNAME_KEY, processParam.getHostDeptName());
            mapTemp.put(FlowableUiConsts.TASKNAME_KEY, ardModel.getTaskDefName());
            mapTemp.put(FlowableUiConsts.ITEMID_KEY, processParam.getItemId());
            mapTemp.put(FlowableUiConsts.PROCESSINSTANCEID_KEY, processInstanceId);
            mapTemp.put(FlowableUiConsts.TASKID_KEY, taskId);
            mapTemp.put(FlowableUiConsts.TASKASSIGNEE_KEY, ardModel.getAssigneeName());
            mapTemp.put(SysVariables.TASK_RELATED_LIST, getTaskRelated4Todo(ardModel));
            mapTemp.put(SysVariables.ITEM_BOX, ItemBoxTypeEnum.TODO.getValue());
        } catch (Exception e) {
            LOGGER.error("获取所有待办列表失败，异常processInstanceId：{}", processInstanceId, e);
        }
        return mapTemp;
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
            List<ActRuDetailModel> actRuDetailList = itemPage.getRows();
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            for (ActRuDetailModel ardModel : actRuDetailList) {
                Map<String, Object> itemMap = buildDoingList4AllItem(ardModel, tenantId, itemId, ++serialNumber);
                items.add(itemMap);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取所有在办件列表成功！");
        } catch (Exception e) {
            LOGGER.error("获取所有在办件列表失败，异常：", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取所有在办件列表失败！");
    }

    private Map<String, Object> buildDoingList4AllItem(ActRuDetailModel ardModel, String tenantId, String itemId,
        int serialNumber) {
        Map<String, Object> mapTemp = new HashMap<>(16);
        String processInstanceId = ardModel.getProcessInstanceId();
        String taskId = ardModel.getTaskId();
        try {
            String processSerialNumber = ardModel.getProcessSerialNumber();
            mapTemp.put("id", processSerialNumber);
            mapTemp.put(FlowableUiConsts.SERIALNUMBER_KEY, serialNumber);
            mapTemp.put(FlowableUiConsts.CANOPEN_KEY, true);
            mapTemp.put(SysVariables.PROCESS_SERIAL_NUMBER, processSerialNumber);
            ProcessParamModel processParam =
                processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            List<SignDeptDetailModel> signDeptDetailList =
                signDeptDetailApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
            mapTemp.putAll(getTaskNameAndUserName4Doing(processParam, taskList, signDeptDetailList));
            mapTemp.put(FlowableUiConsts.SYSTEMCNNAME_KEY, processParam.getSystemCnName());
            mapTemp.put(FlowableUiConsts.BUREAUNAME_KEY, processParam.getHostDeptName());
            mapTemp.put(FlowableUiConsts.ITEMID_KEY, processParam.getItemId());
            mapTemp.put(FlowableUiConsts.PROCESSINSTANCEID_KEY, processInstanceId);
            mapTemp.put(FlowableUiConsts.PROCESSDEFINITIONID_KEY, ardModel.getProcessDefinitionId());
            mapTemp.put(FlowableUiConsts.TASKID_KEY, taskId);
            Map<String, Object> formData = formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
            mapTemp.putAll(handleFormData(formData));
            List<UrgeInfoModel> urgeInfoList =
                urgeInfoApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
            mapTemp.put(SysVariables.TASK_RELATED_LIST,
                getTaskRelated4Doing(processSerialNumber, ardModel.getExecutionId(), false, urgeInfoList));
            mapTemp.put(SysVariables.ITEM_BOX, ItemBoxTypeEnum.MONITOR_DOING.getValue());
            mapTemp.put(FlowableUiConsts.CHILDREN_KEY,
                getChildren(ardModel, mapTemp, taskList, urgeInfoList, signDeptDetailList, true));
        } catch (Exception e) {
            LOGGER.error("获取所有在办件列表失败，processInstanceId：{}", processInstanceId, e);
        }
        return mapTemp;
    }

    @Override
    public Y9Page<Map<String, Object>> doingList4Dept(String itemId, boolean isBureau, String searchMapStr,
        Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String positionId = Y9LoginUserHolder.getPositionId();
            Position position = Y9LoginUserHolder.getPosition();
            OrgUnit bureau = orgUnitApi.getBureau(tenantId, positionId).getData();
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            String deptId = isBureau ? bureau.getId() : position.getParentId();
            Y9Page<ActRuDetailModel> itemPage;
            if (StringUtils.isBlank(searchMapStr)) {
                itemPage = itemDoingApi.findByDeptIdAndSystemName(tenantId, deptId, isBureau, item.getSystemName(),
                    page, rows);
            } else {
                itemPage = itemDoingApi.searchByDeptIdAndSystemName(tenantId, deptId, isBureau, item.getSystemName(),
                    searchMapStr, page, rows);
            }
            List<ActRuDetailModel> actRuDetailList = itemPage.getRows();
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            for (ActRuDetailModel ardModel : actRuDetailList) {
                Map<String, Object> itemMap =
                    buildDoingList4DeptItem(ardModel, tenantId, itemId, bureau, ++serialNumber);
                items.add(itemMap);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取部门在办件列表成功!");
        } catch (Exception e) {
            LOGGER.error("获取部门在办件失败，异常：", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取部门在办件列表失败!!");
    }

    private Map<String, Object> buildDoingList4DeptItem(ActRuDetailModel ardModel, String tenantId, String itemId,
        OrgUnit bureau, int serialNumber) {
        Map<String, Object> mapTemp = new HashMap<>(16);
        String processInstanceId = ardModel.getProcessInstanceId();
        String taskId = ardModel.getTaskId();
        try {
            String processSerialNumber = ardModel.getProcessSerialNumber();
            mapTemp.put("id", processSerialNumber);
            mapTemp.put(FlowableUiConsts.SERIALNUMBER_KEY, serialNumber);
            mapTemp.put(FlowableUiConsts.CANOPEN_KEY, true);
            mapTemp.put(SysVariables.PROCESS_SERIAL_NUMBER, processSerialNumber);
            ProcessParamModel processParam =
                processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            List<SignDeptDetailModel> signDeptDetailList =
                signDeptDetailApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
            List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            boolean isSignDept = signDeptDetailList.stream()
                .anyMatch(signDeptDetailModel -> signDeptDetailModel.getDeptId().equals(bureau.getId()));
            mapTemp.putAll(getTaskNameAndUserName4Doing(processParam, taskList, signDeptDetailList));
            mapTemp.put(FlowableUiConsts.SYSTEMCNNAME_KEY, processParam.getSystemCnName());
            mapTemp.put(FlowableUiConsts.BUREAUNAME_KEY, processParam.getHostDeptName());
            mapTemp.put(FlowableUiConsts.ITEMID_KEY, processParam.getItemId());
            mapTemp.put(FlowableUiConsts.PROCESSINSTANCEID_KEY, processInstanceId);
            mapTemp.put(FlowableUiConsts.PROCESSDEFINITIONID_KEY, ardModel.getProcessDefinitionId());
            mapTemp.put(FlowableUiConsts.TASKID_KEY, taskId);
            Map<String, Object> formData = formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
            mapTemp.putAll(handleFormData(formData));
            mapTemp.put(SysVariables.ITEM_BOX, ItemBoxTypeEnum.MONITOR_DOING.getValue());
            List<UrgeInfoModel> urgeInfoList =
                urgeInfoApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
            mapTemp.put(SysVariables.TASK_RELATED_LIST,
                getTaskRelated4Doing(processSerialNumber, "", isSignDept, urgeInfoList));
            mapTemp.put(FlowableUiConsts.CHILDREN_KEY,
                getChildren(ardModel, mapTemp, taskList, urgeInfoList, signDeptDetailList, true));
        } catch (Exception e) {
            LOGGER.error("获取部门在办件列表失败，processInstanceId：{}", processInstanceId, e);
        }

        return mapTemp;
    }

    @Override
    public Y9Page<Map<String, Object>> doingList4DuBan(String itemId, String searchMapStr, Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            Y9Page<ActRuDetailModel> itemPage =
                itemDoingApi.searchBySystemName(tenantId, item.getSystemName(), searchMapStr, page, rows);
            List<ActRuDetailModel> actRuDetailList = itemPage.getRows();
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            for (ActRuDetailModel ardModel : actRuDetailList) {
                Map<String, Object> itemMap = buildDoingList4DuBanItem(ardModel, tenantId, itemId, ++serialNumber);
                items.add(itemMap);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取督办列表成功!");
        } catch (Exception e) {
            LOGGER.error("获取督办列表失败，异常：", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取督办列表失败！");
    }

    private Map<String, Object> buildDoingList4DuBanItem(ActRuDetailModel ardModel, String tenantId, String itemId,
        int serialNumber) {
        Map<String, Object> mapTemp = new HashMap<>(16);
        String processInstanceId = ardModel.getProcessInstanceId();
        String taskId = ardModel.getTaskId();
        try {
            String processSerialNumber = ardModel.getProcessSerialNumber();
            mapTemp.put("id", processSerialNumber);
            mapTemp.put(FlowableUiConsts.SERIALNUMBER_KEY, serialNumber);
            mapTemp.put(FlowableUiConsts.CANOPEN_KEY, true);
            mapTemp.put(SysVariables.PROCESS_SERIAL_NUMBER, processSerialNumber);
            ProcessParamModel processParam =
                processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            List<SignDeptDetailModel> signDeptDetailList =
                signDeptDetailApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
            mapTemp.putAll(getTaskNameAndUserName4Doing(processParam, taskList, signDeptDetailList));
            mapTemp.put(FlowableUiConsts.SYSTEMCNNAME_KEY, processParam.getSystemCnName());
            mapTemp.put(FlowableUiConsts.BUREAUNAME_KEY, processParam.getHostDeptName());
            mapTemp.put(FlowableUiConsts.ITEMID_KEY, processParam.getItemId());
            mapTemp.put(FlowableUiConsts.PROCESSINSTANCEID_KEY, processInstanceId);
            mapTemp.put(FlowableUiConsts.TASKID_KEY, taskId);
            Map<String, Object> formData = formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
            mapTemp.putAll(handleFormData(formData));
            mapTemp.put(SysVariables.ITEM_BOX, ItemBoxTypeEnum.MONITOR_DOING.getValue());
            List<UrgeInfoModel> urgeInfoList =
                urgeInfoApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
            mapTemp.put(SysVariables.TASK_RELATED_LIST,
                getTaskRelated4Doing(processSerialNumber, "", false, urgeInfoList));
            mapTemp.put(FlowableUiConsts.CHILDREN_KEY,
                getChildren(ardModel, mapTemp, taskList, urgeInfoList, signDeptDetailList, true));
        } catch (Exception e) {
            LOGGER.error("获取督办列表失败,processInstanceId:{}", processInstanceId, e);
        }
        return mapTemp;
    }

    @Override
    public Y9Page<Map<String, Object>> doneList(String itemId, Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String positionId = Y9LoginUserHolder.getPositionId();
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            Y9Page<ActRuDetailModel> itemPage =
                itemDoneApi.findByUserIdAndSystemName(tenantId, positionId, item.getSystemName(), page, rows);
            List<ActRuDetailModel> actRuDetailList = itemPage.getRows();
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            for (ActRuDetailModel ardModel : actRuDetailList) {
                Map<String, Object> itemMap = buildDoneListItem(ardModel, tenantId, itemId, ++serialNumber);
                items.add(itemMap);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取办结列表成功");
        } catch (Exception e) {
            LOGGER.error("获取待办异常", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取办结列表失败");
    }

    private Map<String, Object> buildDoneListItem(ActRuDetailModel ardModel, String tenantId, String itemId,
        int serialNumber) {
        Map<String, Object> mapTemp = new HashMap<>(16);
        String processInstanceId = ardModel.getProcessInstanceId();
        String taskId = ardModel.getTaskId();
        try {
            String processSerialNumber = ardModel.getProcessSerialNumber();
            mapTemp.put(SysVariables.PROCESS_SERIAL_NUMBER, processSerialNumber);
            ProcessParamModel processParam =
                processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            mapTemp.put(FlowableUiConsts.SERIALNUMBER_KEY, serialNumber);
            mapTemp.put(FlowableUiConsts.TASKID_KEY, taskId);
            mapTemp.put("id", processSerialNumber);
            mapTemp.put(FlowableUiConsts.SYSTEMCNNAME_KEY, processParam.getSystemCnName());
            mapTemp.put(FlowableUiConsts.BUREAUNAME_KEY, processParam.getHostDeptName());
            mapTemp.put(FlowableUiConsts.TASKNAME_KEY, "已办结");
            mapTemp.put(FlowableUiConsts.ITEMID_KEY, processParam.getItemId());
            mapTemp.put(FlowableUiConsts.PROCESSINSTANCEID_KEY, processInstanceId);
            mapTemp.put(FlowableUiConsts.COMPLETER_KEY,
                StringUtils.isBlank(processParam.getCompleter()) ? "无" : processParam.getCompleter());
            Map<String, Object> formData = formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
            mapTemp.putAll(handleFormData(formData));
            mapTemp.put(SysVariables.ITEM_BOX, ItemBoxTypeEnum.DONE.getValue());
        } catch (Exception e) {
            LOGGER.error("获取办结列表失败,processInstanceId:{}", processInstanceId, e);
        }
        return mapTemp;
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
            List<ActRuDetailModel> actRuDetailList = itemPage.getRows();
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            for (ActRuDetailModel ardModel : actRuDetailList) {
                Map<String, Object> itemMap = buildDoneList4AllItem(ardModel, tenantId, itemId, ++serialNumber);
                items.add(itemMap);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取所有办结件列表成功");
        } catch (Exception e) {
            LOGGER.error("获取所有办结件失败，异常：", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取所有办结件列表失败");
    }

    private Map<String, Object> buildDoneList4AllItem(ActRuDetailModel ardModel, String tenantId, String itemId,
        int serialNumber) {
        Map<String, Object> mapTemp = new HashMap<>(16);
        String processInstanceId = ardModel.getProcessInstanceId();
        String taskId = ardModel.getTaskId();
        try {
            String processSerialNumber = ardModel.getProcessSerialNumber();
            mapTemp.put("id", processSerialNumber);
            mapTemp.put(FlowableUiConsts.SERIALNUMBER_KEY, serialNumber);
            mapTemp.put(FlowableUiConsts.CANOPEN_KEY, true);
            mapTemp.put(SysVariables.PROCESS_SERIAL_NUMBER, processSerialNumber);
            ProcessParamModel processParam =
                processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            mapTemp.put(FlowableUiConsts.TASKID_KEY, taskId);
            mapTemp.put(FlowableUiConsts.SYSTEMCNNAME_KEY, processParam.getSystemCnName());
            mapTemp.put(FlowableUiConsts.BUREAUNAME_KEY, processParam.getHostDeptName());
            mapTemp.put(FlowableUiConsts.TASKNAME_KEY, "已办结");
            mapTemp.put(FlowableUiConsts.ITEMID_KEY, processParam.getItemId());
            mapTemp.put(FlowableUiConsts.PROCESSINSTANCEID_KEY, processInstanceId);
            mapTemp.put(FlowableUiConsts.COMPLETER_KEY,
                StringUtils.isBlank(processParam.getCompleter()) ? "无" : processParam.getCompleter());
            Map<String, Object> formData = formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
            mapTemp.putAll(handleFormData(formData));
            List<UrgeInfoModel> urgeInfoList =
                urgeInfoApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
            mapTemp.put(SysVariables.TASK_RELATED_LIST, getTaskRelated4Done(ardModel, false, urgeInfoList));
            mapTemp.put(SysVariables.ITEM_BOX, ItemBoxTypeEnum.MONITOR_DONE.getValue());
            List<SignDeptDetailModel> signDeptDetailList = signDeptDetailApi
                .findByProcessSerialNumber(Y9LoginUserHolder.getTenantId(), ardModel.getProcessSerialNumber())
                .getData();
            mapTemp.put(FlowableUiConsts.CHILDREN_KEY,
                getChildren(ardModel, mapTemp, List.of(), urgeInfoList, signDeptDetailList, true));
        } catch (Exception e) {
            LOGGER.error("获取所有办结件列表失败{}", processInstanceId, e);
        }
        return mapTemp;
    }

    @Override
    public Y9Page<Map<String, Object>> doneList4Dept(String itemId, boolean isBureau, String searchMapStr, Integer page,
        Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String positionId = Y9LoginUserHolder.getPositionId();
            Position position = Y9LoginUserHolder.getPosition();
            OrgUnit bureau = orgUnitApi.getBureau(tenantId, positionId).getData();
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            String deptId = isBureau ? bureau.getId() : position.getParentId();
            Y9Page<ActRuDetailModel> itemPage;
            if (StringUtils.isBlank(searchMapStr)) {
                itemPage =
                    itemDoneApi.findByDeptIdAndSystemName(tenantId, deptId, isBureau, item.getSystemName(), page, rows);
            } else {
                itemPage = itemDoneApi.searchByDeptIdAndSystemName(tenantId, deptId, isBureau, item.getSystemName(),
                    searchMapStr, page, rows);
            }
            List<ActRuDetailModel> actRuDetailList = itemPage.getRows();
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            for (ActRuDetailModel ardModel : actRuDetailList) {
                Map<String, Object> itemMap =
                    buildDoneList4DeptItem(ardModel, tenantId, itemId, bureau, isBureau, ++serialNumber);
                items.add(itemMap);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取委办局或部门待办列表成功");
        } catch (Exception e) {
            LOGGER.error("获取委办局或部门待办列表失败，异常：", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取委办局或部门待办列表失败");
    }

    private Map<String, Object> buildDoneList4DeptItem(ActRuDetailModel ardModel, String tenantId, String itemId,
        OrgUnit bureau, boolean isBureau, int serialNumber) {
        Map<String, Object> mapTemp = new HashMap<>(16);
        String processInstanceId = ardModel.getProcessInstanceId();
        String taskId = ardModel.getTaskId();
        try {
            String processSerialNumber = ardModel.getProcessSerialNumber();
            mapTemp.put("id", processSerialNumber);
            mapTemp.put(FlowableUiConsts.SERIALNUMBER_KEY, serialNumber);
            mapTemp.put(FlowableUiConsts.CANOPEN_KEY, true);
            mapTemp.put(SysVariables.PROCESS_SERIAL_NUMBER, processSerialNumber);
            ProcessParamModel processParam =
                processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            mapTemp.put(FlowableUiConsts.TASKID_KEY, taskId);
            mapTemp.put(FlowableUiConsts.SYSTEMCNNAME_KEY, processParam.getSystemCnName());
            mapTemp.put(FlowableUiConsts.BUREAUNAME_KEY, processParam.getHostDeptName());
            mapTemp.put(FlowableUiConsts.TASKNAME_KEY, "已办结");
            mapTemp.put(FlowableUiConsts.ITEMID_KEY, processParam.getItemId());
            mapTemp.put(FlowableUiConsts.PROCESSINSTANCEID_KEY, processInstanceId);
            mapTemp.put(FlowableUiConsts.COMPLETER_KEY,
                StringUtils.isBlank(processParam.getCompleter()) ? "无" : processParam.getCompleter());
            Map<String, Object> formData = formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
            mapTemp.putAll(handleFormData(formData));
            mapTemp.put(SysVariables.ITEM_BOX, isBureau ? ItemBoxTypeEnum.MONITOR_DONE_BUREAU.getValue()
                : ItemBoxTypeEnum.MONITOR_DONE_DEPT.getValue());
            List<SignDeptDetailModel> signDeptDetailList =
                signDeptDetailApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
            boolean isSignDept = signDeptDetailList.stream()
                .anyMatch(signDeptDetailModel -> signDeptDetailModel.getDeptId().equals(bureau.getId()));
            List<UrgeInfoModel> urgeInfoList =
                urgeInfoApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
            mapTemp.put(SysVariables.TASK_RELATED_LIST, getTaskRelated4Done(ardModel, isSignDept, urgeInfoList));
            mapTemp.put(FlowableUiConsts.CHILDREN_KEY,
                getChildren(ardModel, mapTemp, List.of(), urgeInfoList, signDeptDetailList, true));
        } catch (Exception e) {
            LOGGER.error("获取委办局或部门待办列表失败，processInstanceId：{}", processInstanceId, e);
        }
        return mapTemp;
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
        List<TaskModel> taskList, List<UrgeInfoModel> urgeInfoList, List<SignDeptDetailModel> signDeptDetailList,
        boolean isAdmin) {
        List<Map<String, Object>> childrenList = new ArrayList<>();
        AtomicInteger count = new AtomicInteger(0);
        signDeptDetailList.forEach(sdd -> {
            Map<String, Object> childrenMap = new HashMap<>(parentMap);
            childrenMap.put("id", sdd.getId());
            childrenMap.put("isSub", true);
            childrenMap.put(FlowableUiConsts.CANOPEN_KEY, false);
            childrenMap.put(FlowableUiConsts.SERIALNUMBER_KEY, count.incrementAndGet());
            childrenMap.put(FlowableUiConsts.TASKNAME_KEY, getTaskName4SignDept(taskList, sdd));
            childrenMap.put(FlowableUiConsts.TASKASSIGNEE_KEY, utilService.getAssigneeNames(taskList, sdd));
            childrenMap.put(FlowableUiConsts.CHILDREN_KEY, List.of());
            childrenMap.put("status", sdd.getStatus());
            childrenMap.put(FlowableUiConsts.BUREAUNAME_KEY, sdd.getDeptName());
            childrenMap.put(SysVariables.PROCESS_SERIAL_NUMBER, ardModel.getProcessSerialNumber());
            childrenMap.put(FlowableUiConsts.EXECUTIONID_KEY, sdd.getExecutionId());
            if (ardModel.isEnded()) {
                childrenMap.put(SysVariables.TASK_RELATED_LIST, getTaskRelated4Done(ardModel, true, urgeInfoList));
            } else {
                childrenMap.put(SysVariables.TASK_RELATED_LIST,
                    getTaskRelated4Doing(ardModel.getProcessSerialNumber(), sdd.getExecutionId(), true, urgeInfoList));
            }
            if (isAdmin) {
                childrenMap.put(FlowableUiConsts.CANOPEN_KEY, true);
            } else {
                if (!ardModel.isSub()) {
                    childrenMap.put(FlowableUiConsts.CANOPEN_KEY, true);
                } else {
                    if (sdd.getExecutionId().equals(ardModel.getExecutionId())) {
                        childrenMap.put(FlowableUiConsts.CANOPEN_KEY, true);
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
        String userName, taskName;
        Map<String, Object> map = new HashMap<>();
        boolean isSign =
            signDeptDetailList.stream().anyMatch(sdd -> sdd.getStatus().equals(SignDeptDetailStatusEnum.DOING));
        // 当前节点如果是子流程的节点
        if (isSign) {
            taskName = signDeptDetailList.get(0).getTaskName();
            userName = signDeptDetailList.get(0).getSenderName();
        } else {
            if (taskList.isEmpty()) {
                taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            }
            taskName = taskList.get(0).getName();
            userName = utilService.getAssigneeNames(taskList, null);
        }
        map.put(FlowableUiConsts.TASKNAME_KEY, taskName);
        map.put(FlowableUiConsts.TASKASSIGNEE_KEY, userName);
        return map;
    }

    private Map<String, Object> getTaskNameAndUserName4Done(ProcessParamModel processParam) {
        Map<String, Object> map = new HashMap<>();
        map.put(FlowableUiConsts.TASKNAME_KEY, "已办结");
        map.put(FlowableUiConsts.TASKASSIGNEE_KEY, processParam.getCompleter());
        return map;
    }

    private String getTaskName4SignDept(List<TaskModel> taskList, SignDeptDetailModel signDeptDetail) {
        String taskName = "";
        for (TaskModel task : taskList) {
            if (!task.getExecutionId().equals(signDeptDetail.getExecutionId())) {
                continue;
            }
            taskName = task.getName();
        }
        return StringUtils.isNotBlank(taskName) ? taskName
            : signDeptDetail.getStatus().equals(SignDeptDetailStatusEnum.DELETED_DONE) ? "会签结束(减签)" : "会签结束";
    }

    private Map<String, Object> getTaskNameAndUserName4Todo(ActRuDetailModel ardModel) {
        Map<String, Object> map = new HashMap<>();
        map.put(FlowableUiConsts.TASKNAME_KEY, ardModel.getTaskDefName());
        map.put(FlowableUiConsts.TASKASSIGNEE_KEY, ardModel.getAssigneeName());
        return map;
    }

    /**
     * 1、红绿灯 2、条码号 3、紧急程度 4、非联网登记 5、催办 6、办文说明
     *
     * @param processSerialNumber 流程序列号
     * @param executionId 执行id
     * @param isChildren 是否子流程
     * @param urgeInfoList 催办信息
     * @return List<TaskRelatedModel>
     */
    private List<TaskRelatedModel> getTaskRelated4Doing(String processSerialNumber, String executionId,
        boolean isChildren, List<UrgeInfoModel> urgeInfoList) {
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
            // 催办
            returnList.addAll(getTaskRelated4Urge(urgeInfoList, isChildren, executionId));
            // 复原
            returnList.addAll(getTaskRelated4FuYuan(processSerialNumber, taskRelatedList));
            return returnList;
        } catch (Exception e) {
            LOGGER.error("流程序列号processSerialNumber={}", processSerialNumber, e);
        }
        return List.of();
    }

    /**
     * 催办、复原
     *
     * @param ardModel 流转信息
     * @param isChildren 是否子流程
     * @param urgeInfoList 催办信息
     * @return List<TaskRelatedModel>
     */
    private List<TaskRelatedModel> getTaskRelated4Done(ActRuDetailModel ardModel, boolean isChildren,
        List<UrgeInfoModel> urgeInfoList) {
        List<TaskRelatedModel> taskRelatedList = new ArrayList<>();
        // 催办
        taskRelatedList.addAll(getTaskRelated4Urge(urgeInfoList, isChildren, ardModel.getExecutionId()));
        // 复原
        taskRelatedList.addAll(getTaskRelated4FuYuan(ardModel.getProcessSerialNumber(), List.of()));
        return taskRelatedList;
    }

    /**
     * 复和原的状态
     *
     * @param processSerialNumber 流程序列号
     * @param processRelatedList 流程所有关联信息
     * @return List<TaskRelatedModel>
     */
    private List<TaskRelatedModel> getTaskRelated4FuYuan(String processSerialNumber,
        List<TaskRelatedModel> processRelatedList) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<TaskRelatedModel> taskRelatedList;
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
            LOGGER.error("根据任务id查找任务相关信息失败", e);
        }
        return List.of();
    }

    /**
     * 5、催办 6、原、复
     *
     * @param ardModel 流转信息
     * @return List<TaskRelatedModel>
     */
    private List<TaskRelatedModel> getTaskRelated4Recycle(ActRuDetailModel ardModel) {
        try {
            List<TaskRelatedModel> taskRelatedList = new ArrayList<>();
            // 5、催办
            taskRelatedList.addAll(
                getTaskRelated4Urge(ardModel.getProcessSerialNumber(), ardModel.isSub(), ardModel.getExecutionId()));
            // 6、原、复
            taskRelatedList.addAll(getTaskRelated4FuYuan(ardModel.getProcessSerialNumber(), List.of()));
            return taskRelatedList;
        } catch (Exception e) {
            LOGGER.error("流程序列号（processSerialNumber）：{}", ardModel.getProcessSerialNumber(), e);
        }
        return List.of();
    }

    /**
     * 1、红绿灯 2、条码号 3、紧急程度 4、非联网登记 5、新 6、催办 7、多步退回 8、办文说明
     *
     * @param ardModel 流程流转信息
     * @return List<TaskRelatedModel>
     */
    private List<TaskRelatedModel> getTaskRelated4Todo(ActRuDetailModel ardModel) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            List<TaskRelatedModel> processRelatedList =
                taskRelatedApi.findByProcessSerialNumber(tenantId, ardModel.getProcessSerialNumber()).getData();
            List<TaskRelatedModel> taskRelatedList = processRelatedList.stream()
                .filter(prl -> prl.getTaskId().equals(ardModel.getTaskId()))
                .collect(Collectors.toList());
            // 7、多步退回 8、办文说明
            taskRelatedList = taskRelatedList.stream()
                .filter(
                    t -> Integer.parseInt(t.getInfoType()) < Integer.parseInt(TaskRelatedEnum.ACTIONNAME.getValue()))
                .collect(Collectors.toList());
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
            LOGGER.error("序列号processSerialNumber={}", ardModel.getProcessSerialNumber(), e);
        }
        return List.of();
    }

    /**
     * 所有列表的状态，主看主，子看子，监控所有件的管理员看所有 1、催办
     *
     * @param urgeInfoList 催办信息
     * @param isChildren 是否子流程
     * @param executionId 执行ID
     * @return List<TaskRelatedModel>
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
            LOGGER.error("processSerialNumber：{}", urgeInfoList.get(0).getProcessSerialNumber(), e);
        }
        return List.of();
    }

    /**
     * 所有列表的状态，主看主，子看子，监控所有件的管理员看所有 1、催办
     *
     * @param processSerialNumber 流程序列号
     * @param isSub 是否子流程
     * @param executionId 执行ID
     * @return List<TaskRelatedModel>
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
            LOGGER.error("processSerialNumber={}", processSerialNumber, e);
        }
        return List.of();
    }

    private Map<String, Object> handleFormData(Map<String, Object> formData) {
        if (map.isEmpty()) {
            optionClassApi.findAll(Y9LoginUserHolder.getTenantId())
                .getData()
                .forEach(item -> map.put(item.getType() + "." + item.getCode(), item.getName()));
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
            String tenantId = Y9LoginUserHolder.getTenantId();
            String positionId = Y9LoginUserHolder.getPositionId();
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            Y9Page<ActRuDetailModel> itemPage;
            if (StringUtils.isBlank(searchMapStr)) {
                itemPage =
                    itemHaveDoneApi.findByUserIdAndSystemName(tenantId, positionId, item.getSystemName(), page, rows);
            } else {
                itemPage = itemHaveDoneApi.searchByUserIdAndSystemName(tenantId, positionId, item.getSystemName(),
                    searchMapStr, page, rows);
            }
            List<ActRuDetailModel> actRuDetailList = itemPage.getRows();
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            for (ActRuDetailModel ardModel : actRuDetailList) {
                Map<String, Object> itemMap = buildHaveDoneListItem(ardModel, tenantId, itemId, ++serialNumber);
                items.add(itemMap);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取已办列表成功!");
        } catch (Exception e) {
            LOGGER.error("获取已办列表失败，出现异常：", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取已办列表失败");
    }

    private Map<String, Object> buildHaveDoneListItem(ActRuDetailModel ardModel, String tenantId, String itemId,
        int serialNumber) {
        Map<String, Object> mapTemp = new HashMap<>(16);
        String processInstanceId = ardModel.getProcessInstanceId();
        try {
            String processSerialNumber = ardModel.getProcessSerialNumber();
            ProcessParamModel processParam =
                processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            mapTemp.put("id", processSerialNumber);
            mapTemp.put("isSub", false);
            mapTemp.put(FlowableUiConsts.CANOPEN_KEY, true);
            mapTemp.put(FlowableUiConsts.SERIALNUMBER_KEY, serialNumber);
            mapTemp.put(SysVariables.PROCESS_SERIAL_NUMBER, processSerialNumber);
            mapTemp.put(FlowableUiConsts.SYSTEMCNNAME_KEY, processParam.getSystemCnName());
            mapTemp.put(FlowableUiConsts.BUREAUNAME_KEY, processParam.getHostDeptName());
            mapTemp.put(FlowableUiConsts.ITEMID_KEY, processParam.getItemId());
            mapTemp.put(FlowableUiConsts.PROCESSINSTANCEID_KEY, processInstanceId);
            mapTemp.put(FlowableUiConsts.EXECUTIONID_KEY, ardModel.getExecutionId());
            Map<String, Object> formData = formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
            mapTemp.putAll(handleFormData(formData));
            mapTemp.put(SysVariables.ITEM_BOX, StringUtils.isBlank(processParam.getCompleter())
                ? ItemBoxTypeEnum.DOING.getValue() : ItemBoxTypeEnum.DONE.getValue());
            mapTemp.put(FlowableUiConsts.PROCESSDEFINITIONID_KEY, ardModel.getProcessDefinitionId());
            List<SignDeptDetailModel> signDeptDetailList =
                signDeptDetailApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
            List<UrgeInfoModel> urgeInfoList =
                urgeInfoApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
            List<TaskRelatedModel> taskRelatedList;
            if (ardModel.isEnded()) {
                taskRelatedList = getTaskRelated4Done(ardModel, false, urgeInfoList);
                mapTemp.putAll(getTaskNameAndUserName4Done(processParam));
                mapTemp.put(FlowableUiConsts.CHILDREN_KEY,
                    getChildren(ardModel, mapTemp, List.of(), urgeInfoList, signDeptDetailList, false));
            } else {
                List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                taskRelatedList =
                    getTaskRelated4Doing(processSerialNumber, ardModel.getExecutionId(), false, urgeInfoList);
                mapTemp.putAll(getTaskNameAndUserName4Doing(processParam, taskList, signDeptDetailList));
                mapTemp.put(FlowableUiConsts.CHILDREN_KEY,
                    getChildren(ardModel, mapTemp, taskList, urgeInfoList, signDeptDetailList, false));
            }
            mapTemp.put(SysVariables.TASK_RELATED_LIST, taskRelatedList);
        } catch (Exception e) {
            LOGGER.error("获取已办列表失败，processInstanceId：{}", processInstanceId, e);
        }
        return mapTemp;
    }

    @Override
    public Y9Page<Map<String, Object>> recycleList(String itemId, String searchMapStr, Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String positionId = Y9LoginUserHolder.getPositionId();
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            Y9Page<ActRuDetailModel> itemPage;
            if (StringUtils.isBlank(searchMapStr)) {
                itemPage =
                    itemRecycleApi.findByUserIdAndSystemName(tenantId, positionId, item.getSystemName(), page, rows);
            } else {
                itemPage = itemRecycleApi.searchByUserIdAndSystemName(tenantId, positionId, item.getSystemName(),
                    searchMapStr, page, rows);
            }
            List<ActRuDetailModel> actRuDetailList = itemPage.getRows();
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            for (ActRuDetailModel ardModel : actRuDetailList) {
                Map<String, Object> itemMap = buildRecycleListItem(ardModel, tenantId, itemId, ++serialNumber);
                items.add(itemMap);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取回收站列表成功");
        } catch (Exception e) {
            LOGGER.error("获取回收站列表失败，异常：", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取回收站列表失败");
    }

    private Map<String, Object> buildRecycleListItem(ActRuDetailModel ardModel, String tenantId, String itemId,
        int serialNumber) {
        Map<String, Object> mapTemp = new HashMap<>(16);
        String processInstanceId = ardModel.getProcessInstanceId();
        String taskId = ardModel.getTaskId();
        try {
            String processSerialNumber = ardModel.getProcessSerialNumber();
            mapTemp.put(SysVariables.PROCESS_SERIAL_NUMBER, processSerialNumber);
            ProcessParamModel processParam =
                processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            mapTemp.put("id", processSerialNumber);
            mapTemp.put(FlowableUiConsts.TASKID_KEY, taskId);
            mapTemp.put(FlowableUiConsts.SERIALNUMBER_KEY, serialNumber);
            mapTemp.put(FlowableUiConsts.SYSTEMCNNAME_KEY, processParam.getSystemCnName());
            mapTemp.put(FlowableUiConsts.BUREAUNAME_KEY, processParam.getHostDeptName());
            mapTemp.put(FlowableUiConsts.ITEMID_KEY, processParam.getItemId());
            mapTemp.put(FlowableUiConsts.PROCESSINSTANCEID_KEY, processInstanceId);
            List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            mapTemp.put(FlowableUiConsts.TASKNAME_KEY, taskList.get(0).getName());
            mapTemp.put(FlowableUiConsts.TASKASSIGNEE_KEY, utilService.getAssigneeNames(taskList, null));
            Map<String, Object> formData = formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
            mapTemp.putAll(handleFormData(formData));
            mapTemp.put(SysVariables.ITEM_BOX, ItemBoxTypeEnum.DONE.getValue());
            mapTemp.put(SysVariables.TASK_RELATED_LIST, getTaskRelated4Recycle(ardModel));
        } catch (Exception e) {
            LOGGER.error("获取回收站列表失败，processInstanceId：{}", processInstanceId, e);
        }
        return mapTemp;
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
            List<ActRuDetailModel> actRuDetailList = itemPage.getRows();
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            for (ActRuDetailModel ardModel : actRuDetailList) {
                Map<String, Object> itemMap = buildRecycleList4AllItem(ardModel, tenantId, itemId, ++serialNumber);
                items.add(itemMap);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取所有回收站列表成功！");
        } catch (Exception e) {
            LOGGER.error("获取所有回收站列表失败，出现异常：", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取所有回收站列表失败!!");
    }

    private Map<String, Object> buildRecycleList4AllItem(ActRuDetailModel ardModel, String tenantId, String itemId,
        int serialNumber) {
        Map<String, Object> mapTemp = new HashMap<>(16);
        String processInstanceId = ardModel.getProcessInstanceId();
        try {
            String processSerialNumber = ardModel.getProcessSerialNumber();
            mapTemp.put(SysVariables.PROCESS_SERIAL_NUMBER, processSerialNumber);
            ProcessParamModel processParam =
                processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            mapTemp.put("id", processSerialNumber);
            mapTemp.put(FlowableUiConsts.SERIALNUMBER_KEY, serialNumber);
            mapTemp.put(FlowableUiConsts.SYSTEMCNNAME_KEY, processParam.getSystemCnName());
            mapTemp.put(FlowableUiConsts.BUREAUNAME_KEY, processParam.getHostDeptName());
            mapTemp.put(FlowableUiConsts.ITEMID_KEY, processParam.getItemId());
            mapTemp.put(FlowableUiConsts.PROCESSINSTANCEID_KEY, processInstanceId);
            mapTemp.put(FlowableUiConsts.TASKID_KEY, "");
            mapTemp.put(FlowableUiConsts.TASKASSIGNEE_KEY, "无");
            mapTemp.put(FlowableUiConsts.TASKNAME_KEY, "已办结");
            if (!ardModel.isEnded()) {
                List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                mapTemp.put(FlowableUiConsts.TASKNAME_KEY, taskList.get(0).getName());
                mapTemp.put(FlowableUiConsts.TASKASSIGNEE_KEY, utilService.getAssigneeNames(taskList, null));
            }
            Map<String, Object> formData = formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
            mapTemp.putAll(handleFormData(formData));
            mapTemp.put(SysVariables.ITEM_BOX, ItemBoxTypeEnum.MONITOR_RECYCLE.getValue());
            mapTemp.put(SysVariables.TASK_RELATED_LIST, getTaskRelated4Recycle(ardModel));
        } catch (Exception e) {
            LOGGER.error("获取所有回收站列表失败，processInstanceId：{}", processInstanceId, e);
        }
        return mapTemp;
    }

    @Override
    public Y9Page<Map<String, Object>> recycleList4Dept(String itemId, boolean isBureau, String searchMapStr,
        Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String positionId = Y9LoginUserHolder.getPositionId();
            Position position = Y9LoginUserHolder.getPosition();
            OrgUnit bureau = orgUnitApi.getBureau(tenantId, positionId).getData();
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            String deptId = isBureau ? bureau.getId() : position.getParentId();
            Y9Page<ActRuDetailModel> itemPage;
            if (StringUtils.isBlank(searchMapStr)) {
                itemPage = itemRecycleApi.findByDeptIdAndSystemName(tenantId, deptId, isBureau, item.getSystemName(),
                    page, rows);
            } else {
                itemPage = itemRecycleApi.searchByDeptIdAndSystemName(tenantId, deptId, isBureau, item.getSystemName(),
                    searchMapStr, page, rows);
            }
            List<ActRuDetailModel> actRuDetailList = itemPage.getRows();
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            for (ActRuDetailModel ardModel : actRuDetailList) {
                Map<String, Object> itemMap = buildRecycleList4DeptItem(ardModel, tenantId, itemId, ++serialNumber);
                items.add(itemMap);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取部门回收站列表成功！");
        } catch (Exception e) {
            LOGGER.error("获取部门回收站失败，出现异常：", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取部门回收站列表失败");
    }

    private Map<String, Object> buildRecycleList4DeptItem(ActRuDetailModel ardModel, String tenantId, String itemId,
        int serialNumber) {
        Map<String, Object> mapTemp = new HashMap<>(16);
        String processInstanceId = ardModel.getProcessInstanceId();
        String taskId = ardModel.getTaskId();
        try {
            String processSerialNumber = ardModel.getProcessSerialNumber();
            mapTemp.put(SysVariables.PROCESS_SERIAL_NUMBER, processSerialNumber);
            ProcessParamModel processParam =
                processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            mapTemp.put("id", processSerialNumber);
            mapTemp.put(FlowableUiConsts.TASKID_KEY, taskId);
            mapTemp.put(FlowableUiConsts.SERIALNUMBER_KEY, serialNumber);
            mapTemp.put(FlowableUiConsts.SYSTEMCNNAME_KEY, processParam.getSystemCnName());
            mapTemp.put(FlowableUiConsts.BUREAUNAME_KEY, processParam.getHostDeptName());
            mapTemp.put(FlowableUiConsts.ITEMID_KEY, processParam.getItemId());
            mapTemp.put(FlowableUiConsts.PROCESSINSTANCEID_KEY, processInstanceId);
            List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            mapTemp.put(FlowableUiConsts.TASKNAME_KEY, taskList.get(0).getName());
            mapTemp.put(FlowableUiConsts.TASKASSIGNEE_KEY, utilService.getAssigneeNames(taskList, null));
            Map<String, Object> formData = formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
            mapTemp.putAll(handleFormData(formData));
            mapTemp.put(SysVariables.ITEM_BOX, ItemBoxTypeEnum.DONE.getValue());
            mapTemp.put(SysVariables.TASK_RELATED_LIST, getTaskRelated4Recycle(ardModel));
        } catch (Exception e) {
            LOGGER.error("获取部门回收站列表失败，processInstanceId：{}", processInstanceId, e);
        }
        return mapTemp;
    }

    /**
     * 整点执行的任务
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void refreshMapScheduled() {
        tenantApi.listAllTenants()
            .getData()
            .forEach(tenant -> optionClassApi.findAll(tenant.getId())
                .getData()
                .forEach(item -> map.put(item.getType() + "." + item.getCode(), item.getName())));
    }

    @Override
    public Y9Page<Map<String, Object>> todoList(String itemId, String searchMapStr, Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String positionId = Y9LoginUserHolder.getPositionId();
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            Y9Page<ActRuDetailModel> itemPage;
            if (StringUtils.isBlank(searchMapStr)) {
                itemPage =
                    itemTodoApi.findByUserIdAndSystemName(tenantId, positionId, item.getSystemName(), page, rows);
            } else {
                itemPage = itemTodoApi.searchByUserIdAndSystemName(tenantId, positionId, item.getSystemName(),
                    searchMapStr, page, rows);
            }
            List<ActRuDetailModel> actRuDetailList = itemPage.getRows();
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            for (ActRuDetailModel ardModel : actRuDetailList) {
                Map<String, Object> itemMap = buildTodoListItem(ardModel, tenantId, itemId, ++serialNumber);
                items.add(itemMap);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取待办件列表成功！！！");
        } catch (Exception e) {
            LOGGER.error("获取待办数据列表失败，出现异常：", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取待办件列表失败！");
    }

    private Map<String, Object> buildTodoListItem(ActRuDetailModel ardModel, String tenantId, String itemId,
        int serialNumber) {
        Map<String, Object> mapTemp = new HashMap<>(16);
        String processInstanceId = ardModel.getProcessInstanceId();
        String taskId = ardModel.getTaskId();
        try {
            String processSerialNumber = ardModel.getProcessSerialNumber();
            ProcessParamModel processParam =
                processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
            mapTemp.put("id", processSerialNumber);
            mapTemp.put(FlowableUiConsts.ACTRUDETAILID_KEY, ardModel.getId());
            mapTemp.put(FlowableUiConsts.SERIALNUMBER_KEY, serialNumber);
            mapTemp.put(FlowableUiConsts.SYSTEMCNNAME_KEY, processParam.getSystemCnName());
            mapTemp.put(FlowableUiConsts.BUREAUNAME_KEY, processParam.getHostDeptName());
            mapTemp.put(FlowableUiConsts.TASKNAME_KEY, ardModel.getTaskDefName());
            mapTemp.put(FlowableUiConsts.ITEMID_KEY, processParam.getItemId());
            mapTemp.put(FlowableUiConsts.PROCESSINSTANCEID_KEY, processInstanceId);
            mapTemp.put(FlowableUiConsts.TASKID_KEY, taskId);
            mapTemp.put(FlowableUiConsts.TASKASSIGNEE_KEY, ardModel.getAssigneeName());
            Map<String, Object> formData = formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
            mapTemp.putAll(handleFormData(formData));
            mapTemp.put(SysVariables.TASK_RELATED_LIST, getTaskRelated4Todo(ardModel));
            mapTemp.put(SysVariables.ITEM_BOX, ItemBoxTypeEnum.TODO.getValue());
            mapTemp.put(SysVariables.PROCESS_SERIAL_NUMBER, processSerialNumber);
        } catch (Exception e) {
            LOGGER.error("获取待办列表数据失败，processInstanceId == {}", processInstanceId, e);
        }
        return mapTemp;
    }

    @Override
    public Y9Page<Map<String, Object>> todoList4Other(String itemId, String searchMapStr, Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String positionId = Y9LoginUserHolder.getPositionId();
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            Y9Page<ActRuDetailModel> itemPage = itemTodoApi.searchByUserIdAndSystemName4Other(tenantId, positionId,
                item.getSystemName(), searchMapStr, page, rows);
            List<ActRuDetailModel> actRuDetailList = itemPage.getRows();
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            for (ActRuDetailModel ardModel : actRuDetailList) {
                Map<String, Object> itemMap = buildTodoList4OtherItem(ardModel, tenantId, itemId, ++serialNumber);
                items.add(itemMap);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取待办列表成功!!");
        } catch (Exception e) {
            LOGGER.error("todoList4Other方法获取待办列表失败，出现异常：", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取待办件列表失败！！！");
    }

    private Map<String, Object> buildTodoList4OtherItem(ActRuDetailModel ardModel, String tenantId, String itemId,
        int serialNumber) {
        Map<String, Object> mapTemp = new HashMap<>(16);
        String processInstanceId = ardModel.getProcessInstanceId();
        String taskId = ardModel.getTaskId();
        try {
            String processSerialNumber = ardModel.getProcessSerialNumber();
            ProcessParamModel processParam =
                processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
            mapTemp.put("id", processSerialNumber);
            mapTemp.put(FlowableUiConsts.ACTRUDETAILID_KEY, ardModel.getId());
            mapTemp.put(FlowableUiConsts.SERIALNUMBER_KEY, serialNumber);
            mapTemp.put(FlowableUiConsts.SYSTEMCNNAME_KEY, processParam.getSystemCnName());
            mapTemp.put(FlowableUiConsts.BUREAUNAME_KEY, processParam.getHostDeptName());
            mapTemp.put(FlowableUiConsts.TASKNAME_KEY, ardModel.getTaskDefName());
            mapTemp.put(FlowableUiConsts.ITEMID_KEY, processParam.getItemId());
            mapTemp.put(FlowableUiConsts.PROCESSINSTANCEID_KEY, processInstanceId);
            mapTemp.put(FlowableUiConsts.TASKID_KEY, taskId);
            mapTemp.put(FlowableUiConsts.TASKASSIGNEE_KEY, ardModel.getAssigneeName());
            Map<String, Object> formData = formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
            mapTemp.putAll(handleFormData(formData));
            mapTemp.put(SysVariables.TASK_RELATED_LIST, getTaskRelated4Todo(ardModel));
            mapTemp.put(SysVariables.ITEM_BOX, ItemBoxTypeEnum.TODO.getValue());
            mapTemp.put(SysVariables.PROCESS_SERIAL_NUMBER, processSerialNumber);
        } catch (Exception e) {
            LOGGER.error("获取待办件列表失败，processInstanceId：{}", processInstanceId, e);
        }

        return mapTemp;
    }

    @Override
    public Y9Page<Map<String, Object>> todoList4TaskDefKey(String itemId, String taskDefKey, String searchMapStr,
        Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String positionId = Y9LoginUserHolder.getPositionId();
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            Y9Page<ActRuDetailModel> itemPage;
            if (StringUtils.isBlank(searchMapStr)) {
                itemPage = itemTodoApi.findByUserIdAndSystemNameAndTaskDefKey(tenantId, positionId,
                    item.getSystemName(), taskDefKey, page, rows);
            } else {
                itemPage = itemTodoApi.searchByUserIdAndSystemNameAndTaskDefKey(tenantId, positionId,
                    item.getSystemName(), taskDefKey, searchMapStr, page, rows);
            }
            List<ActRuDetailModel> actRuDetailList = itemPage.getRows();
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            for (ActRuDetailModel ardModel : actRuDetailList) {
                Map<String, Object> itemMap = buildTodoList4TaskDefKeyItem(ardModel, tenantId, itemId, ++serialNumber);
                items.add(itemMap);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取待办列表成功!");
        } catch (Exception e) {
            LOGGER.error("获取待办列表失败todoList4TaskDefKey，异常：", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "todoList4TaskDefKey获取待办列表失败！");
    }

    private Map<String, Object> buildTodoList4TaskDefKeyItem(ActRuDetailModel ardModel, String tenantId, String itemId,
        int serialNumber) {
        Map<String, Object> mapTemp = new HashMap<>(16);
        String processInstanceId = ardModel.getProcessInstanceId();
        String taskId = ardModel.getTaskId();
        try {
            String processSerialNumber = ardModel.getProcessSerialNumber();
            ProcessParamModel processParam =
                processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
            mapTemp.put("id", processSerialNumber);
            mapTemp.put(FlowableUiConsts.ACTRUDETAILID_KEY, ardModel.getId());
            mapTemp.put(FlowableUiConsts.SYSTEMCNNAME_KEY, processParam.getSystemCnName());
            mapTemp.put(FlowableUiConsts.SERIALNUMBER_KEY, serialNumber);
            mapTemp.put(FlowableUiConsts.BUREAUNAME_KEY, processParam.getHostDeptName());
            mapTemp.put(FlowableUiConsts.TASKNAME_KEY, ardModel.getTaskDefName());
            mapTemp.put(FlowableUiConsts.ITEMID_KEY, processParam.getItemId());
            mapTemp.put(FlowableUiConsts.PROCESSINSTANCEID_KEY, processInstanceId);
            mapTemp.put(FlowableUiConsts.TASKID_KEY, taskId);
            mapTemp.put(FlowableUiConsts.TASKASSIGNEE_KEY, ardModel.getAssigneeName());

            Map<String, Object> formData = formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
            mapTemp.putAll(handleFormData(formData));
            mapTemp.put(SysVariables.TASK_RELATED_LIST, getTaskRelated4Todo(ardModel));
            mapTemp.put(SysVariables.ITEM_BOX, ItemBoxTypeEnum.TODO.getValue());
            mapTemp.put(SysVariables.PROCESS_SERIAL_NUMBER, processSerialNumber);
        } catch (Exception e) {
            LOGGER.error("todoList4TaskDefKey方法获取待办列表失败，processInstanceId={}", processInstanceId, e);
        }
        return mapTemp;
    }
}
