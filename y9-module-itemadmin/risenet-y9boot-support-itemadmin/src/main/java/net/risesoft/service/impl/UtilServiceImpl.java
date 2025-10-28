package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.IdentityApi;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.itemadmin.SignDeptDetailModel;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.processadmin.IdentityLinkModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.service.UtilService;
import net.risesoft.y9.Y9LoginUserHolder;

@Slf4j
@RequiredArgsConstructor
@Service
public class UtilServiceImpl implements UtilService {

    private final OrgUnitApi orgUnitApi;

    private final IdentityApi identityApi;

    @Override
    public String getAssigneeNames(List<TaskModel> taskList, SignDeptDetailModel signDeptDetail) {
        if (taskList.isEmpty()) {
            return "";
        }
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<String> assigneeNameList = new ArrayList<>();
        for (TaskModel task : taskList) {
            if (null != signDeptDetail && !task.getExecutionId().equals(signDeptDetail.getExecutionId())) {
                continue;
            }
            String name = getTaskAssigneeName(task, tenantId);
            if (name != null && !name.isEmpty()) {
                assigneeNameList.add(name);
            }
        }
        return formatAssigneeNames(assigneeNameList, taskList.size());
    }

    /**
     * 格式化办理人名称列表
     *
     * @param assigneeNames 办理人名称列表
     * @param totalCount 总任务数
     * @return 格式化后的字符串
     */
    private String formatAssigneeNames(List<String> assigneeNames, int totalCount) {
        if (assigneeNames.isEmpty()) {
            return "";
        }
        int maxDisplayCount = 5;
        StringBuilder result = new StringBuilder();
        // 如果只有一个办理人，直接返回
        if (assigneeNames.size() == 1) {
            return assigneeNames.get(0);
        }
        // 如果办理人数不超过最大显示数，全部显示
        if (assigneeNames.size() <= maxDisplayCount) {
            return String.join("、", assigneeNames);
        }
        // 超过最大显示数，截取并添加"等"提示
        for (int i = 0; i < maxDisplayCount; i++) {
            if (result.length() > 0) {
                result.append("、");
            }
            result.append(assigneeNames.get(i));
        }
        result.append("等，共").append(totalCount).append("人");
        return result.toString();
    }

    /**
     * 获取任务办理人名称
     *
     * @param task 任务对象
     * @param tenantId 租户ID
     * @return 办理人名称
     */
    private String getTaskAssigneeName(TaskModel task, String tenantId) {
        if (StringUtils.isNotBlank(task.getAssignee())) {
            return getAssignedTaskAssigneeName(task, tenantId);
        } else {
            return getUnassignedTaskAssigneeNames(task, tenantId);
        }
    }

    /**
     * 处理已分配的任务，获取办理人名称
     *
     * @param task 任务对象
     * @param tenantId 租户ID
     * @return 办理人名称，如果获取失败返回null
     */
    private String getAssignedTaskAssigneeName(TaskModel task, String tenantId) {
        String assignee = task.getAssignee();
        if (StringUtils.isNotBlank(assignee)) {
            OrgUnit personTemp = this.orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
            if (personTemp != null) {
                return personTemp.getName();
            }
        }
        return null;
    }

    /**
     * 处理未分配的任务，获取候选办理人名称
     *
     * @param task 任务对象
     * @param tenantId 租户ID
     * @return 候选办理人名称列表
     */
    private String getUnassignedTaskAssigneeNames(TaskModel task, String tenantId) {
        StringBuilder assigneeNames = new StringBuilder();
        List<IdentityLinkModel> identityLinks =
            this.identityApi.getIdentityLinksForTask(tenantId, task.getId()).getData();

        if (!identityLinks.isEmpty()) {
            int maxDisplayCount = Math.min(identityLinks.size(), 5);
            for (int j = 0; j < maxDisplayCount; j++) {
                IdentityLinkModel identityLink = identityLinks.get(j);
                String assigneeId = identityLink.getUserId();
                OrgUnit ownerUser =
                    this.orgUnitApi.getOrgUnitPersonOrPosition(Y9LoginUserHolder.getTenantId(), assigneeId).getData();
                if (assigneeNames.length() > 0) {
                    assigneeNames.append("、");
                }
                assigneeNames.append(ownerUser.getName());
            }

            if (identityLinks.size() > 5) {
                assigneeNames.append("等，共").append(identityLinks.size()).append("人");
            }
        }
        return assigneeNames.toString();
    }

    @Override
    public List<String> getItemBoxAndTaskId(List<TaskModel> taskList) {
        String userId = Y9LoginUserHolder.getPersonId();
        String itembox = ItemBoxTypeEnum.DOING.getValue(), taskId = "";
        List<String> list = new ArrayList<>();
        for (TaskModel task : taskList) {
            String assignee = task.getAssignee();
            if (StringUtils.isNotBlank(assignee)) {
                if (assignee.contains(userId)) {
                    itembox = ItemBoxTypeEnum.TODO.getValue();
                    taskId = task.getId();
                }
            }
        }
        list.add(itembox);
        list.add(taskId);
        return list;
    }
}