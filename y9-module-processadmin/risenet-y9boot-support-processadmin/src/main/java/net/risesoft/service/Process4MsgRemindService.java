package net.risesoft.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.extend.ItemMsgRemindApi;
import net.risesoft.api.itemadmin.OfficeDoneInfoApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.RemindInstanceApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ItemMsgRemindModel;
import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.itemadmin.RemindInstanceModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.app.y9processadmin.Y9ProcessAdminProperties;
import net.risesoft.y9.util.Y9Util;

/**
 * 消息提醒
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Slf4j
@RequiredArgsConstructor
@Service(value = "process4MsgRemindService")
public class Process4MsgRemindService {

    private final ItemMsgRemindApi itemMsgRemindApi;

    private final ProcessParamApi processParamApi;

    private final OrgUnitApi orgUnitApi;

    private final RemindInstanceApi remindInstanceApi;

    private final OfficeDoneInfoApi officeDoneInfoApi;

    private final Y9ProcessAdminProperties y9ProcessAdminProperties;

    /**
     * 流程办结消息提醒
     *
     * @param processParamModel 流程参数
     * @param personName 办结人姓名
     */
    public void processComplete(final ProcessParamModel processParamModel, String personName) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Boolean msgSwitch = y9ProcessAdminProperties.getMsgSwitch();
        if (msgSwitch == null || !msgSwitch) {
            return;
        }
        String tenantId = Y9LoginUserHolder.getTenantId();
        String processInstanceId = processParamModel.getProcessInstanceId();
        Date date = new Date();
        String allUserId = "";
        List<RemindInstanceModel> list = remindInstanceApi.findRemindInstanceByProcessInstanceIdAndRemindType(tenantId,
            processInstanceId, RemindInstanceModel.processComplete).getData();
        if (!list.isEmpty()) {
            for (RemindInstanceModel remind : list) {
                if (!allUserId.contains(remind.getUserId())) {
                    allUserId = Y9Util.genCustomStr(allUserId, remind.getUserId());
                }
            }
            String itemId = processParamModel.getItemId();
            String todoTaskUrlPrefix = processParamModel.getTodoTaskUrlPrefix();
            String url = todoTaskUrlPrefix + "?itemId=" + itemId + "&processInstanceId=" + processInstanceId
                + "&type=fromCplane";
            String title = processParamModel.getTitle();
            String content = "【" + title + "】";
            ItemMsgRemindModel info = new ItemMsgRemindModel();
            info.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            info.setItemId(processParamModel.getItemId());
            info.setMsgType(ItemMsgRemindModel.MSG_TYPE_PROCESS_COMPLETE);
            info.setProcessInstanceId(processInstanceId);
            info.setStartTime(sdf.format(date));
            info.setSystemName(processParamModel.getSystemName());
            info.setTitle(title);
            info.setTenantId(tenantId);
            info.setUrl(url);
            info.setUserName(personName);
            info.setTime(date.getTime());
            info.setReadUserId("");
            info.setAllUserId(allUserId);
            info.setContent(content);
            itemMsgRemindApi.saveMsgRemindInfo(tenantId, info);
        }
    }

    /**
     * 节点到达消息提醒
     *
     * @param task 任务
     * @param variables 变量
     */
    public void taskAssignment(DelegateTask task, Map<String, Object> variables) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String tenantId = (String)variables.get("tenantId");
        String processSerialNumber = (String)variables.get("processSerialNumber");
        Y9LoginUserHolder.setTenantId(tenantId);
        try {
            Boolean msgSwitch = y9ProcessAdminProperties.getMsgSwitch();
            if (msgSwitch == null || !msgSwitch) {
                return;
            }
            ProcessParamModel processParamModel =
                processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
            String assignee = task.getAssignee();
            String taskKey = task.getTaskDefinitionKey();
            String taskName = task.getName();
            String processInstanceId = task.getProcessInstanceId();
            OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
            String userName = orgUnit.getName();
            Date date = new Date();
            String allUserId = "";
            // 节点到达
            List<RemindInstanceModel> list =
                remindInstanceApi.findRemindInstanceByProcessInstanceIdAndArriveTaskKey(tenantId, processInstanceId,
                    taskKey + ":" + taskName).getData();
            if (!list.isEmpty()) {
                for (RemindInstanceModel remind : list) {
                    if (!allUserId.contains(remind.getUserId())) {
                        allUserId = Y9Util.genCustomStr(allUserId, remind.getUserId());
                    }
                }
                String itemId = processParamModel.getItemId();
                String todoTaskUrlPrefix = processParamModel.getTodoTaskUrlPrefix();
                String url = todoTaskUrlPrefix + "?itemId=" + itemId + "&processInstanceId=" + processInstanceId
                    + "&type=fromCplane";
                String title = processParamModel.getTitle();
                ItemMsgRemindModel info = new ItemMsgRemindModel();
                info.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                info.setItemId(processParamModel.getItemId());
                info.setMsgType(ItemMsgRemindModel.MSG_TYPE_NODE_ARRIVE);
                info.setProcessInstanceId(processInstanceId);
                info.setStartTime(sdf.format(date));
                info.setSystemName(processParamModel.getSystemName());
                info.setTitle(title);
                info.setTenantId(tenantId);
                info.setUrl(url);
                info.setUserName(userName);
                info.setTime(date.getTime());
                info.setReadUserId("");
                info.setAllUserId(allUserId);
                info.setContent(taskName);
                itemMsgRemindApi.saveMsgRemindInfo(tenantId, info);
            }
        } catch (Exception e) {
            LOGGER.error("taskAssignment error", e);
        }
    }

    /**
     * 任务完成,节点完成消息提醒
     *
     * @param task 任务
     * @param variables 变量
     */
    public void taskComplete(final DelegateTask task, final Map<String, Object> variables) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String tenantId = (String)variables.get("tenantId");
        String processSerialNumber = (String)variables.get("processSerialNumber");
        Y9LoginUserHolder.setTenantId(tenantId);
        try {
            Boolean msgSwitch = y9ProcessAdminProperties.getMsgSwitch();
            if (msgSwitch == null || !msgSwitch) {
                return;
            }
            ProcessParamModel processParamModel =
                processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
            String assignee = task.getAssignee();
            String taskId = task.getId();
            String taskKey = task.getTaskDefinitionKey();
            String taskName = task.getName();
            String processInstanceId = task.getProcessInstanceId();
            String userName;
            OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
            userName = orgUnit.getName();
            Date date = new Date();
            String allUserId = "";
            String title = processParamModel.getTitle();
            String itemId = processParamModel.getItemId();
            String todoTaskUrlPrefix = processParamModel.getTodoTaskUrlPrefix();
            String url = todoTaskUrlPrefix + "?itemId=" + itemId + "&processInstanceId=" + processInstanceId
                + "&type=fromCplane";

            // 任务完成，针对任务设置
            List<RemindInstanceModel> list = remindInstanceApi
                .findRemindInstanceByProcessInstanceIdAndTaskId(tenantId, processInstanceId, taskId).getData();
            if (!list.isEmpty()) {
                for (RemindInstanceModel remind : list) {
                    if (!allUserId.contains(remind.getUserId())) {
                        allUserId = Y9Util.genCustomStr(allUserId, remind.getUserId());
                    }
                }
                String content = "【" + title + "】";
                ItemMsgRemindModel info = new ItemMsgRemindModel();
                info.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                info.setItemId(processParamModel.getItemId());
                info.setMsgType(ItemMsgRemindModel.MSG_TYPE_TASK_COMPLETE);
                info.setProcessInstanceId(processInstanceId);
                info.setStartTime(sdf.format(date));
                info.setSystemName(processParamModel.getSystemName());
                info.setTitle(title);
                info.setTenantId(tenantId);
                info.setUrl(url);
                info.setUserName(userName);
                info.setTime(date.getTime());
                info.setReadUserId("");
                info.setAllUserId(allUserId);
                info.setContent(content);
                itemMsgRemindApi.saveMsgRemindInfo(tenantId, info);
            }

            // 任务完成提醒，针对人设置
            try {
                String personIds = itemMsgRemindApi.getRemindConfig(tenantId, assignee, "taskRemind").getData();
                if (StringUtils.isNotBlank(personIds)) {
                    String newPersonIds = "";
                    String[] ids = personIds.split(",");
                    OfficeDoneInfoModel officeDoneInfoModel =
                        officeDoneInfoApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    for (String id : ids) {
                        if (officeDoneInfoModel != null && officeDoneInfoModel.getAllUserId().contains(id)) {
                            newPersonIds = Y9Util.genCustomStr(newPersonIds, id);
                        }
                    }
                    if (StringUtils.isNotBlank(newPersonIds)) {
                        ItemMsgRemindModel info = new ItemMsgRemindModel();
                        info.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                        info.setItemId(processParamModel.getItemId());
                        info.setMsgType(ItemMsgRemindModel.MSG_TYPE_TASK_COMPLETE);
                        info.setProcessInstanceId(processInstanceId);
                        info.setStartTime(sdf.format(date));
                        info.setSystemName(processParamModel.getSystemName());
                        info.setTitle(title);
                        info.setTenantId(tenantId);
                        info.setUrl(url);
                        info.setUserName(userName);
                        info.setTime(date.getTime());
                        info.setReadUserId("");
                        info.setAllUserId(personIds);
                        info.setContent("");
                        itemMsgRemindApi.saveMsgRemindInfo(tenantId, info);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("taskComplete error", e);
            }

            // 节点完成
            list = remindInstanceApi.findRemindInstanceByProcessInstanceIdAndCompleteTaskKey(tenantId,
                processInstanceId, taskKey + ":" + taskName).getData();
            if (!list.isEmpty()) {
                for (RemindInstanceModel remind : list) {
                    if (!allUserId.contains(remind.getUserId())) {
                        allUserId = Y9Util.genCustomStr(allUserId, remind.getUserId());
                    }
                }
                date = new Date();
                ItemMsgRemindModel info = new ItemMsgRemindModel();
                info.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                info.setItemId(processParamModel.getItemId());
                info.setMsgType(ItemMsgRemindModel.MSG_TYPE_NODE_COMPLETE);
                info.setProcessInstanceId(processInstanceId);
                info.setStartTime(sdf.format(date));
                info.setSystemName(processParamModel.getSystemName());
                info.setTitle(title);
                info.setTenantId(tenantId);
                info.setUrl(url);
                info.setUserName(userName);
                info.setTime(date.getTime());
                info.setReadUserId("");
                info.setAllUserId(allUserId);
                info.setContent(taskName);
                itemMsgRemindApi.saveMsgRemindInfo(tenantId, info);
            }

        } catch (Exception e) {
            LOGGER.error("taskComplete error", e);
        }
    }

}
