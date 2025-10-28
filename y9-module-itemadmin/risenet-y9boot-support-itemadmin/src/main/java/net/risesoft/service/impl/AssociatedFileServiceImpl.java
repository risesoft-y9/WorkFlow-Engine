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

import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.entity.AssociatedFile;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.AssociatedFileModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.nosql.elastic.entity.OfficeDoneInfo;
import net.risesoft.repository.jpa.AssociatedFileRepository;
import net.risesoft.service.AssociatedFileService;
import net.risesoft.service.OfficeDoneInfoService;
import net.risesoft.service.UtilService;
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

    private final UtilService utilService;

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
    public void deleteAllAssociatedFile(String processSerialNumber, String delIds) {
        AssociatedFile associatedFile = associatedFileRepository.findByProcessSerialNumber(processSerialNumber);
        if (associatedFile != null && associatedFile.getId() != null) {
            String newAssociatedId = getAssociatedId(delIds, associatedFile);
            associatedFile.setCreateTime(new Date());
            associatedFile.setAssociatedId(newAssociatedId);
            associatedFileRepository.save(associatedFile);
        }
    }

    @Transactional
    @Override
    public void deleteAssociatedFile(String processSerialNumber, String delId) {
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
        List<AssociatedFileModel> list = new ArrayList<>();
        AssociatedFile associatedFile = associatedFileRepository.findByProcessSerialNumber(processSerialNumber);
        if (associatedFile == null) {
            return list;
        }
        String associatedId = associatedFile.getAssociatedId();
        if (StringUtils.isBlank(associatedId)) {
            return list;
        }
        String[] associatedIds = associatedId.split(SysVariables.COMMA);
        String tenantId = Y9LoginUserHolder.getTenantId();
        for (String id : associatedIds) {
            AssociatedFileModel model = buildAssociatedFileModel(id, tenantId);
            if (model != null) {
                list.add(model);
            }
        }
        return list.stream().sorted().collect(Collectors.toList());
    }

    /**
     * 构建关联文件模型
     */
    private AssociatedFileModel buildAssociatedFileModel(String processInstanceId, String tenantId) {
        AssociatedFileModel model = new AssociatedFileModel();
        try {
            OfficeDoneInfo officeDoneInfo = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
            if (officeDoneInfo == null) {
                return null;
            }
            populateBasicInfo(model, officeDoneInfo);
            if (StringUtils.isBlank(officeDoneInfo.getEndTime())) {
                populateTaskInfo(model, officeDoneInfo, tenantId);
            }
            return model;
        } catch (Exception e) {
            LOGGER.error("构建关联文件模型失败, processInstanceId: {}", processInstanceId, e);
            return null;
        }
    }

    /**
     * 填充基本信息
     */
    private void populateBasicInfo(AssociatedFileModel model, OfficeDoneInfo officeDoneInfo) {
        String processInstanceId = officeDoneInfo.getProcessInstanceId();
        String startTime = officeDoneInfo.getStartTime().substring(0, 16);
        String documentTitle = StringUtils.isBlank(officeDoneInfo.getTitle()) ? "无标题" : officeDoneInfo.getTitle();
        String level = officeDoneInfo.getUrgency();
        String number = officeDoneInfo.getDocNumber();
        String completer = officeDoneInfo.getUserComplete();

        model.setItemName(officeDoneInfo.getItemName());
        model.setProcessSerialNumber(officeDoneInfo.getProcessSerialNumber());
        model.setDocumentTitle(documentTitle);
        model.setProcessInstanceId(processInstanceId);
        model.setProcessDefinitionId(officeDoneInfo.getProcessDefinitionId());
        model.setProcessDefinitionKey(officeDoneInfo.getProcessDefinitionKey());
        model.setStartTime(startTime);
        model.setEndTime(
            StringUtils.isBlank(officeDoneInfo.getEndTime()) ? "--" : officeDoneInfo.getEndTime().substring(0, 16));
        model.setTaskDefinitionKey("");
        model.setTaskAssignee(completer);
        model.setCreatUserName(officeDoneInfo.getCreatUserName());
        model.setItemId(officeDoneInfo.getItemId());
        model.setLevel(level == null ? "" : level);
        model.setNumber(number == null ? "" : number);
        model.setItembox(ItemBoxTypeEnum.DONE.getValue());
        model.setStartTimes(officeDoneInfo.getStartTime());
    }

    /**
     * 填充任务信息
     */
    private void populateTaskInfo(AssociatedFileModel model, OfficeDoneInfo officeDoneInfo, String tenantId) {
        try {
            String processInstanceId = officeDoneInfo.getProcessInstanceId();
            List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();

            if (taskList != null && !taskList.isEmpty()) {
                String assigneeNames = utilService.getAssigneeNames(taskList, null);
                List<String> listTemp = utilService.getItemBoxAndTaskId(taskList);

                model.setTaskDefinitionKey(taskList.get(0).getTaskDefinitionKey());
                model.setTaskId(listTemp.get(0).equals(ItemBoxTypeEnum.TODO.getValue()) ? listTemp.get(1) : "");
                model.setTaskAssignee(assigneeNames);
                model.setItembox(listTemp.get(0));
            }
        } catch (Exception e) {
            LOGGER.warn("填充任务信息失败, processInstanceId: {}", officeDoneInfo.getProcessInstanceId(), e);
        }
    }

    @Transactional
    @Override
    public void saveAssociatedFile(String processSerialNumber, String processInstanceIds) {
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
    }

}
