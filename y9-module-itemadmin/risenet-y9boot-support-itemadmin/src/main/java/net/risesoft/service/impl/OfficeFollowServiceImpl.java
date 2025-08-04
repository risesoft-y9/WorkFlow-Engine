package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.entity.OfficeFollow;
import net.risesoft.entity.ProcessParam;
import net.risesoft.entity.RemindInstance;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.itemadmin.OfficeFollowModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.nosql.elastic.entity.OfficeDoneInfo;
import net.risesoft.pojo.Y9Page;
import net.risesoft.repository.jpa.OfficeFollowRepository;
import net.risesoft.service.OfficeDoneInfoService;
import net.risesoft.service.OfficeFollowService;
import net.risesoft.service.ProcessParamService;
import net.risesoft.service.RemindInstanceService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9.util.Y9Util;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class OfficeFollowServiceImpl implements OfficeFollowService {

    private final OfficeFollowRepository officeFollowRepository;

    private final TaskApi taskApi;

    private final OrgUnitApi orgUnitApi;

    private final ProcessParamService processParamService;

    private final RemindInstanceService remindInstanceService;

    private final OfficeDoneInfoService officeDoneInfoService;

    @Override
    public int countByProcessInstanceId(String processInstanceId) {
        return officeFollowRepository.countByProcessInstanceIdAndUserId(processInstanceId,
            Y9LoginUserHolder.getOrgUnitId());
    }

    @Override
    @Transactional
    public void delOfficeFollow(String processInstanceIds) {
        String[] ids = processInstanceIds.split(",");
        for (String processInstanceId : ids) {
            officeFollowRepository.deleteByProcessInstanceId(processInstanceId, Y9LoginUserHolder.getOrgUnitId());
        }
    }

    @Override
    @Transactional
    public void deleteByProcessInstanceId(String processInstanceId) {
        officeFollowRepository.deleteByProcessInstanceId(processInstanceId);
    }

    /**
     * 当并行的时候，会获取到多个task，为了并行时当前办理人显示多人，而不是显示多条记录，需要分开分别进行处理
     *
     * @return
     */
    private final List<String> getAssigneeIdsAndAssigneeNames(List<TaskModel> taskList) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String orgUnitId = Y9LoginUserHolder.getOrgUnitId();
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
                        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
                        if (orgUnit != null) {
                            assigneeNames = orgUnit.getName();
                        }
                        i += 1;
                        if (assignee.contains(orgUnitId)) {
                            itembox = ItemBoxTypeEnum.TODO.getValue();
                            taskId = task.getId();
                        }
                    }
                } else {
                    String assignee = task.getAssignee();
                    if (StringUtils.isNotBlank(assignee)) {
                        if (i < 5) {
                            assigneeIds = Y9Util.genCustomStr(assigneeIds, assignee, SysVariables.COMMA);
                            OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
                            if (orgUnit != null) {
                                assigneeNames = Y9Util.genCustomStr(assigneeNames, orgUnit.getName(), "、");
                            }
                            i += 1;
                        }
                        if (assignee.contains(orgUnitId)) {
                            itembox = ItemBoxTypeEnum.TODO.getValue();
                            taskId = task.getId();
                        }
                    }
                }
            }
            boolean b = taskList.size() > 5;
            if (b) {
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

    @Override
    public int getFollowCount() {
        return officeFollowRepository.countByUserId(Y9LoginUserHolder.getOrgUnitId());
    }

    @Override
    public Y9Page<OfficeFollowModel> pageBySearchName(String searchName, int page, int rows) {
        String userId = Y9LoginUserHolder.getOrgUnitId(), tenantId = Y9LoginUserHolder.getTenantId();
        SimpleDateFormat sdf5 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<OfficeFollowModel> list = new ArrayList<>();
        Pageable pageable = PageRequest.of(page - 1, rows, Sort.by(Sort.Direction.DESC, "startTime"));
        Page<OfficeFollow> followList = null;
        if (StringUtils.isBlank(searchName)) {
            followList = officeFollowRepository.findByUserId(userId, pageable);
        } else {
            searchName = "%" + searchName + "%";
            followList = officeFollowRepository.findByParamsLike(userId, searchName, pageable);
        }
        for (OfficeFollow officeFollow : followList.getContent()) {
            try {
                String processInstanceId = officeFollow.getProcessInstanceId();
                officeFollow.setStartTime(sdf5.format(sdf.parse(officeFollow.getStartTime())));
                List<TaskModel> taskList =
                    taskApi.findByProcessInstanceId(tenantId, officeFollow.getProcessInstanceId()).getData();
                if (CollectionUtils.isNotEmpty(taskList)) {
                    List<String> listTemp = getAssigneeIdsAndAssigneeNames(taskList);
                    String taskIds = listTemp.get(0);
                    String assigneeNames = listTemp.get(2);
                    officeFollow.setTaskId(
                        listTemp.get(3).equals(ItemBoxTypeEnum.DOING.getValue()) ? taskIds : listTemp.get(4));
                    officeFollow
                        .setTaskName(StringUtils.isEmpty(taskList.get(0).getName()) ? "" : taskList.get(0).getName());
                    officeFollow.setItembox(listTemp.get(3));
                    officeFollow.setTaskAssignee(StringUtils.isEmpty(assigneeNames) ? "" : assigneeNames);
                } else {
                    officeFollow.setTaskId("");
                    officeFollow.setItembox(ItemBoxTypeEnum.DONE.getValue());
                    ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
                    officeFollow.setTaskAssignee(processParam != null ? processParam.getCompleter() : "");
                }
                officeFollow.setMsgremind(false);
                RemindInstance remindInstance = remindInstanceService.getRemindInstance(processInstanceId);
                // 流程实例是否设置消息提醒
                if (remindInstance != null) {
                    officeFollow.setMsgremind(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            OfficeFollowModel model = new OfficeFollowModel();
            Y9BeanUtil.copyProperties(officeFollow, model);
            list.add(model);
        }
        return Y9Page.success(page, followList.getTotalPages(), followList.getTotalElements(), list);
    }

    @Override
    public Y9Page<OfficeFollowModel> pageBySystemNameAndSearchName(String systemName, String searchName, int page,
        int rows) {
        String userId = Y9LoginUserHolder.getOrgUnitId(), tenantId = Y9LoginUserHolder.getTenantId();
        SimpleDateFormat sdf5 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<OfficeFollowModel> list = new ArrayList<>();
        Pageable pageable = PageRequest.of(page - 1, rows, Sort.by(Sort.Direction.DESC, "startTime"));
        Page<OfficeFollow> followList = null;
        if (StringUtils.isBlank(searchName)) {
            followList = officeFollowRepository.findBySystemNameAndUserId(systemName, userId, pageable);
        } else {
            searchName = "%" + searchName + "%";
            followList = officeFollowRepository.findBySystemNameAndParamsLike(systemName, userId, searchName, pageable);
        }
        for (OfficeFollow officeFollow : followList.getContent()) {
            try {
                String processInstanceId = officeFollow.getProcessInstanceId();
                OfficeDoneInfo officeDoneInfo = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
                officeFollow.setStartTime(sdf5.format(sdf.parse(officeDoneInfo.getStartTime())));
                officeFollow
                    .setMsgremind(officeDoneInfo.getMeeting() != null && officeDoneInfo.getMeeting().equals("1"));
                ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
                List<TaskModel> taskList =
                    taskApi.findByProcessInstanceId(tenantId, officeFollow.getProcessInstanceId()).getData();
                if (CollectionUtils.isNotEmpty(taskList)) {
                    List<String> listTemp = getAssigneeIdsAndAssigneeNames(taskList);
                    String taskIds = listTemp.get(0);
                    String assigneeNames = listTemp.get(2);
                    officeFollow.setTaskId(
                        listTemp.get(3).equals(ItemBoxTypeEnum.DOING.getValue()) ? taskIds : listTemp.get(4));
                    officeFollow
                        .setTaskName(StringUtils.isEmpty(taskList.get(0).getName()) ? "" : taskList.get(0).getName());
                    officeFollow.setItembox(listTemp.get(3));
                    officeFollow.setTaskAssignee(StringUtils.isEmpty(assigneeNames) ? "" : assigneeNames);
                } else {
                    officeFollow.setTaskId("");
                    officeFollow.setItembox(ItemBoxTypeEnum.DONE.getValue());
                    officeFollow.setTaskAssignee(processParam != null ? processParam.getCompleter() : "");

                }
                officeFollow.setSendDept(processParam.getStartorName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            OfficeFollowModel model = new OfficeFollowModel();
            Y9BeanUtil.copyProperties(officeFollow, model);
            list.add(model);
        }
        return Y9Page.success(page, followList.getTotalPages(), followList.getTotalElements(), list);
    }

    @Override
    @Transactional
    public void saveOfficeFollow(OfficeFollow officeFollow) {
        if (officeFollow != null && officeFollow.getGuid() != null) {
            OfficeFollow follow = officeFollowRepository
                .findByProcessInstanceIdAndUserId(officeFollow.getProcessInstanceId(), officeFollow.getUserId());
            if (follow == null) {
                officeFollowRepository.save(officeFollow);
            }
        }
    }

    @Override
    @Transactional
    public void updateTitle(String processInstanceId, String documentTitle) {
        try {
            List<OfficeFollow> list = officeFollowRepository.findByProcessInstanceId(processInstanceId);
            List<OfficeFollow> newList = new ArrayList<>();
            for (OfficeFollow follow : list) {
                follow.setDocumentTitle(documentTitle);
                newList.add(follow);
            }
            if (!newList.isEmpty()) {
                officeFollowRepository.saveAll(newList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
