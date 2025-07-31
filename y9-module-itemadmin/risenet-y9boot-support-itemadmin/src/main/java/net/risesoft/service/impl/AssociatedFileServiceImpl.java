package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.IdentityApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.entity.AssociatedFile;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.AssociatedFileModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.processadmin.IdentityLinkModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.nosql.elastic.entity.OfficeDoneInfo;
import net.risesoft.repository.jpa.AssociatedFileRepository;
import net.risesoft.service.AssociatedFileService;
import net.risesoft.service.OfficeDoneInfoService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class AssociatedFileServiceImpl implements AssociatedFileService {

    private final AssociatedFileRepository associatedFileRepository;

    private final OfficeDoneInfoService officeDoneInfoService;

    private final TaskApi taskApi;

    private final OrgUnitApi orgUnitApi;

    private final IdentityApi identityApi;

    @Override
    public int countAssociatedFile(String processSerialNumber) {
        try {
            AssociatedFile associatedFile = associatedFileRepository.findByProcessSerialNumber(processSerialNumber);
            if (associatedFile != null && StringUtils.isNotBlank(associatedFile.getAssociatedId())) {
                String associatedId = associatedFile.getAssociatedId();
                String[] associatedIds = associatedId.split(SysVariables.COMMA);
                return associatedIds.length;
            }
        } catch (Exception e) {
            LOGGER.error("Error in countAssociatedFile", e);
        }
        return 0;
    }

    @Transactional
    @Override
    public boolean deleteAllAssociatedFile(String processSerialNumber, String delIds) {
        AssociatedFile associatedFile = associatedFileRepository.findByProcessSerialNumber(processSerialNumber);
        if (associatedFile != null && associatedFile.getId() != null) {
            String newAssociatedId = getAssociatedId(delIds, associatedFile);
            associatedFile.setCreateTime(new Date());
            associatedFile.setAssociatedId(newAssociatedId);
            associatedFileRepository.save(associatedFile);
        }
        return true;
    }

    @Transactional
    @Override
    public boolean deleteAssociatedFile(String processSerialNumber, String delId) {
        AssociatedFile associatedFile = associatedFileRepository.findByProcessSerialNumber(processSerialNumber);
        if (associatedFile != null && associatedFile.getId() != null) {
            String associatedId = associatedFile.getAssociatedId();
            String newAssociatedId = "";
            String[] associatedIds = associatedId.split(SysVariables.COMMA);
            for (String id : associatedIds) {
                if (!delId.contains(id)) {
                    newAssociatedId = Y9Util.genCustomStr(newAssociatedId, id);
                }
            }
            associatedFile.setCreateTime(new Date());
            associatedFile.setAssociatedId(newAssociatedId);
            associatedFileRepository.save(associatedFile);
        }
        return true;
    }

    private List<String> getAssigneeIdsAndAssigneeNames(List<TaskModel> taskList) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9LoginUserHolder.getOrgUnitId();
        String taskIds = "", assigneeIds = "", assigneeNames = "", itembox = ItemBoxTypeEnum.DOING.getValue(),
            taskId = "";
        List<String> list = new ArrayList<>();
        int i = 0;
        if (!taskList.isEmpty()) {
            for (TaskModel task : taskList) {
                if (StringUtils.isEmpty(taskIds)) {
                    taskIds = task.getId();
                    String assignee = task.getAssignee();
                    if (StringUtils.isNotBlank(assignee)) {
                        assigneeIds = assignee;

                        OrgUnit orgUnitTemp = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
                        if (orgUnitTemp != null) {
                            assigneeNames = orgUnitTemp.getName();
                        }
                        i += 1;
                        if (assignee.contains(userId)) {
                            itembox = ItemBoxTypeEnum.TODO.getValue();
                            taskId = task.getId();
                        }
                    } else {// 处理单实例未签收的当前办理人显示
                        List<IdentityLinkModel> iList =
                            identityApi.getIdentityLinksForTask(tenantId, task.getId()).getData();
                        if (!iList.isEmpty()) {
                            int j = 0;
                            for (IdentityLinkModel identityLink : iList) {
                                String assigneeId = identityLink.getUserId();
                                OrgUnit ownerUser =
                                    orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assigneeId).getData();
                                if (j < 5) {
                                    assigneeNames = Y9Util.genCustomStr(assigneeNames, ownerUser.getName(), "、");
                                    assigneeIds = Y9Util.genCustomStr(assigneeIds, assigneeId, SysVariables.COMMA);
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
                    if (StringUtils.isNotBlank(assignee)) {
                        if (i < 5) {
                            assigneeIds = Y9Util.genCustomStr(assigneeIds, assignee, SysVariables.COMMA);
                            OrgUnit orgUnitTemp = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
                            if (orgUnitTemp != null) {
                                assigneeNames = Y9Util.genCustomStr(assigneeNames, orgUnitTemp.getName(), "、");
                            }
                            i += 1;
                        }
                        if (assignee.contains(userId)) {
                            itembox = ItemBoxTypeEnum.TODO.getValue();
                            taskId = task.getId();
                        }
                    }
                }
            }
            if (taskList.size() > 5) {
                assigneeNames += "等，共" + taskList.size() + "人";
            }
        }
        list.add(taskIds);
        list.add(assigneeIds);
        list.add(assigneeNames);
        list.add(itembox);
        list.add(taskId);
        return list;
    }

    private String getAssociatedId(String delIds, AssociatedFile associatedFile) {
        String associatedId = associatedFile.getAssociatedId();
        String newAssociatedId = "";
        String[] associatedIds = associatedId.split(SysVariables.COMMA);
        String[] delAssociatedIds = delIds.split(SysVariables.COMMA);
        for (String id : associatedIds) {
            boolean isDel = false;
            for (String delId : delAssociatedIds) {
                if (id.equals(delId)) {
                    isDel = true;
                    break;
                }
            }
            if (!isDel) {
                newAssociatedId = Y9Util.genCustomStr(newAssociatedId, id);
            }
        }
        return newAssociatedId;
    }

    @Override
    public List<AssociatedFileModel> listAssociatedFileAll(String processSerialNumber) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<AssociatedFileModel> list = new ArrayList<>();
        AssociatedFile associatedFile = associatedFileRepository.findByProcessSerialNumber(processSerialNumber);
        if (associatedFile != null) {
            String associatedId = associatedFile.getAssociatedId();
            if (StringUtils.isNotBlank(associatedId)) {
                String[] associatedIds = associatedId.split(SysVariables.COMMA);
                for (String id : associatedIds) {
                    AssociatedFileModel model = new AssociatedFileModel();
                    try {
                        OfficeDoneInfo hpim = officeDoneInfoService.findByProcessInstanceId(id);
                        String processInstanceId = hpim.getProcessInstanceId();
                        String processDefinitionId = hpim.getProcessDefinitionId();
                        String startTime = hpim.getStartTime().substring(0, 16);
                        String processSerialNumber1 = hpim.getProcessSerialNumber();
                        String documentTitle = StringUtils.isBlank(hpim.getTitle()) ? "无标题" : hpim.getTitle();
                        String level = hpim.getUrgency();
                        String number = hpim.getDocNumber();
                        String completer = hpim.getUserComplete();
                        model.setItemName(hpim.getItemName());
                        model.setProcessSerialNumber(processSerialNumber1);
                        model.setDocumentTitle(documentTitle);
                        model.setProcessInstanceId(processInstanceId);
                        model.setProcessDefinitionId(processDefinitionId);
                        model.setProcessDefinitionKey(hpim.getProcessDefinitionKey());
                        model.setStartTime(startTime);
                        model.setEndTime(
                            StringUtils.isBlank(hpim.getEndTime()) ? "--" : hpim.getEndTime().substring(0, 16));
                        model.setTaskDefinitionKey("");
                        model.setTaskAssignee(completer);
                        model.setCreatUserName(hpim.getCreatUserName());
                        model.setItemId(hpim.getItemId());
                        model.setLevel(level == null ? "" : level);
                        model.setNumber(number == null ? "" : number);
                        model.setItembox(ItemBoxTypeEnum.DONE.getValue());
                        model.setStartTimes(hpim.getStartTime());
                        if (StringUtils.isBlank(hpim.getEndTime())) {
                            List<TaskModel> taskList =
                                taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                            List<String> listTemp = getAssigneeIdsAndAssigneeNames(taskList);
                            String taskIds = listTemp.get(0), assigneeIds = listTemp.get(1),
                                assigneeNames = listTemp.get(2);

                            model.setTaskDefinitionKey(taskList.get(0).getTaskDefinitionKey());
                            model.setTaskId(
                                listTemp.get(3).equals(ItemBoxTypeEnum.DOING.getValue()) ? taskIds : listTemp.get(4));
                            model.setTaskAssigneeId(assigneeIds);
                            model.setTaskAssignee(assigneeNames);
                            model.setItembox(listTemp.get(3));
                        }
                    } catch (Exception e) {
                        LOGGER.error("Error in getAssociatedFileAllList", e);
                    }
                    list.add(model);
                }
            }
            list = list.stream().sorted().collect(Collectors.toList());
        }
        return list;
    }

    @Transactional
    @Override
    public boolean saveAssociatedFile(String processSerialNumber, String processInstanceIds) {
        AssociatedFile associatedFile = associatedFileRepository.findByProcessSerialNumber(processSerialNumber);
        if (associatedFile == null || associatedFile.getId() == null) {
            associatedFile = new AssociatedFile();
            associatedFile.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            associatedFile.setCreateTime(new Date());
            associatedFile.setAssociatedId(processInstanceIds);
            associatedFile.setProcessSerialNumber(processSerialNumber);
            associatedFile.setUserId(Y9LoginUserHolder.getOrgUnitId());
            associatedFile.setUserName(Y9LoginUserHolder.getOrgUnit().getName());
            associatedFile.setTenantId(Y9LoginUserHolder.getTenantId());
        } else {
            String associatedId = associatedFile.getAssociatedId();
            String newAssociatedId = "";
            if (StringUtils.isNotBlank(associatedId)) {
                String[] associatedIds = processInstanceIds.split(SysVariables.COMMA);
                for (String id : associatedIds) {
                    if (!associatedId.contains(id)) {
                        newAssociatedId = Y9Util.genCustomStr(newAssociatedId, id);
                    }
                }
            } else {
                newAssociatedId = processInstanceIds;
            }
            newAssociatedId = Y9Util.genCustomStr(associatedId, newAssociatedId);
            associatedFile.setUserId(Y9LoginUserHolder.getOrgUnitId());
            associatedFile.setUserName(Y9LoginUserHolder.getOrgUnit().getName());
            associatedFile.setCreateTime(new Date());
            associatedFile.setAssociatedId(newAssociatedId);
        }
        associatedFileRepository.save(associatedFile);
        return true;
    }

}
