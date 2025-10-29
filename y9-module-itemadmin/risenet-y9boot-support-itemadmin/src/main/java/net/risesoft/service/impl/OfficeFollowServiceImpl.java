package net.risesoft.service.impl;

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
import lombok.extern.slf4j.Slf4j;

import net.risesoft.Y9FlowableHolder;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.entity.OfficeFollow;
import net.risesoft.entity.ProcessParam;
import net.risesoft.entity.RemindInstance;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.itemadmin.OfficeFollowModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.nosql.elastic.entity.OfficeDoneInfo;
import net.risesoft.pojo.Y9Page;
import net.risesoft.repository.jpa.OfficeFollowRepository;
import net.risesoft.service.OfficeDoneInfoService;
import net.risesoft.service.OfficeFollowService;
import net.risesoft.service.RemindInstanceService;
import net.risesoft.service.UtilService;
import net.risesoft.service.core.ProcessParamService;
import net.risesoft.util.Y9DateTimeUtils;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class OfficeFollowServiceImpl implements OfficeFollowService {

    private final OfficeFollowRepository officeFollowRepository;

    private final TaskApi taskApi;

    private final ProcessParamService processParamService;

    private final RemindInstanceService remindInstanceService;

    private final OfficeDoneInfoService officeDoneInfoService;

    private final UtilService utilService;

    @Override
    public int countByProcessInstanceId(String processInstanceId) {
        return officeFollowRepository.countByProcessInstanceIdAndUserId(processInstanceId,
            Y9FlowableHolder.getOrgUnitId());
    }

    @Override
    @Transactional
    public void delOfficeFollow(String processInstanceIds) {
        String[] ids = processInstanceIds.split(",");
        for (String processInstanceId : ids) {
            officeFollowRepository.deleteByProcessInstanceId(processInstanceId, Y9FlowableHolder.getOrgUnitId());
        }
    }

    @Override
    @Transactional
    public void deleteByProcessInstanceId(String processInstanceId) {
        officeFollowRepository.deleteByProcessInstanceId(processInstanceId);
    }

    @Override
    public int getFollowCount() {
        return officeFollowRepository.countByUserId(Y9FlowableHolder.getOrgUnitId());
    }

    @Override
    public Y9Page<OfficeFollowModel> pageBySearchName(String searchName, int page, int rows) {
        String userId = Y9FlowableHolder.getOrgUnitId();
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<OfficeFollowModel> resultList = new ArrayList<>();

        Pageable pageable = PageRequest.of(page - 1, rows, Sort.by(Sort.Direction.DESC, "startTime"));
        Page<OfficeFollow> followPage = getOfficeFollowPage(userId, searchName, pageable);

        for (OfficeFollow officeFollow : followPage.getContent()) {
            try {
                processOfficeFollow(officeFollow, tenantId);
                OfficeFollowModel model = new OfficeFollowModel();
                Y9BeanUtil.copyProperties(officeFollow, model);
                resultList.add(model);
            } catch (Exception e) {
                // 记录具体异常信息，便于问题排查
                LOGGER.error("处理关注事项失败，流程实例ID: {}", officeFollow.getProcessInstanceId(), e);
            }
        }

        return Y9Page.success(page, followPage.getTotalPages(), followPage.getTotalElements(), resultList);
    }

    /**
     * 根据搜索条件获取关注事项分页数据
     *
     * @param userId 用户ID
     * @param searchName 搜索名称
     * @param pageable 分页参数
     * @return 关注事项分页数据
     */
    private Page<OfficeFollow> getOfficeFollowPage(String userId, String searchName, Pageable pageable) {
        if (StringUtils.isBlank(searchName)) {
            return officeFollowRepository.findByUserId(userId, pageable);
        } else {
            String searchPattern = "%" + searchName + "%";
            return officeFollowRepository.findByParamsLike(userId, searchPattern, pageable);
        }
    }

    /**
     * 处理单个关注事项数据
     *
     * @param officeFollow 关注事项实体
     * @param tenantId 租户ID
     */
    private void processOfficeFollow(OfficeFollow officeFollow, String tenantId) {
        // 格式化开始时间
        formatStartTime(officeFollow);

        // 处理任务相关信息
        processTaskInfo(officeFollow, tenantId);

        // 处理消息提醒状态
        processMessageRemind(officeFollow);
    }

    /**
     * 格式化开始时间
     *
     * @param officeFollow 关注事项实体
     */
    private void formatStartTime(OfficeFollow officeFollow) {
        try {
            officeFollow.setStartTime(
                Y9DateTimeUtils.formatDateTimeMinute(Y9DateTimeUtils.parseDateTime(officeFollow.getStartTime())));
        } catch (Exception e) {
            LOGGER.warn("格式化开始时间失败，时间值: {}", officeFollow.getStartTime(), e);
        }
    }

    /**
     * 处理任务相关信息
     *
     * @param officeFollow 关注事项实体
     * @param tenantId 租户ID
     */
    private void processTaskInfo(OfficeFollow officeFollow, String tenantId) {
        String processInstanceId = officeFollow.getProcessInstanceId();
        List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();

        if (CollectionUtils.isNotEmpty(taskList)) {
            processActiveTask(officeFollow, taskList);
        } else {
            processCompletedTask(officeFollow, processInstanceId);
        }
    }

    /**
     * 处理活动任务信息
     *
     * @param officeFollow 关注事项实体
     * @param taskList 任务列表
     */
    private void processActiveTask(OfficeFollow officeFollow, List<TaskModel> taskList) {
        List<String> boxAndTaskId = utilService.getItemBoxAndTaskId(taskList);
        String assigneeNames = utilService.getAssigneeNames(taskList, null);

        officeFollow.setTaskId(boxAndTaskId.get(0).equals(ItemBoxTypeEnum.TODO.getValue()) ? boxAndTaskId.get(1) : "");
        officeFollow.setTaskName(StringUtils.isEmpty(taskList.get(0).getName()) ? "" : taskList.get(0).getName());
        officeFollow.setItembox(boxAndTaskId.get(0));
        officeFollow.setTaskAssignee(StringUtils.isEmpty(assigneeNames) ? "" : assigneeNames);
    }

    /**
     * 处理已完成任务信息
     *
     * @param officeFollow 关注事项实体
     * @param processInstanceId 流程实例ID
     */
    private void processCompletedTask(OfficeFollow officeFollow, String processInstanceId) {
        officeFollow.setTaskId("");
        officeFollow.setItembox(ItemBoxTypeEnum.DONE.getValue());

        ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
        officeFollow.setTaskAssignee(processParam != null ? processParam.getCompleter() : "");
    }

    /**
     * 处理消息提醒状态
     *
     * @param officeFollow 关注事项实体
     */
    private void processMessageRemind(OfficeFollow officeFollow) {
        officeFollow.setMsgremind(false);
        RemindInstance remindInstance = remindInstanceService.getRemindInstance(officeFollow.getProcessInstanceId());
        // 流程实例是否设置消息提醒
        if (remindInstance != null) {
            officeFollow.setMsgremind(true);
        }
    }

    @Override
    public Y9Page<OfficeFollowModel> pageBySystemNameAndSearchName(String systemName, String searchName, int page,
        int rows) {
        String userId = Y9FlowableHolder.getOrgUnitId();
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<OfficeFollowModel> resultList = new ArrayList<>();

        Pageable pageable = PageRequest.of(page - 1, rows, Sort.by(Sort.Direction.DESC, "startTime"));
        Page<OfficeFollow> followPage = getOfficeFollowPageBySystemName(systemName, userId, searchName, pageable);

        for (OfficeFollow officeFollow : followPage.getContent()) {
            try {
                processOfficeFollowWithSystemName(officeFollow, tenantId);
                OfficeFollowModel model = new OfficeFollowModel();
                Y9BeanUtil.copyProperties(officeFollow, model);
                resultList.add(model);
            } catch (Exception e) {
                LOGGER.error("处理系统关注事项失败，流程实例ID: {}", officeFollow.getProcessInstanceId(), e);
            }
        }

        return Y9Page.success(page, followPage.getTotalPages(), followPage.getTotalElements(), resultList);
    }

    /**
     * 根据系统名称和搜索条件获取关注事项分页数据
     *
     * @param systemName 系统名称
     * @param userId 用户ID
     * @param searchName 搜索名称
     * @param pageable 分页参数
     * @return 关注事项分页数据
     */
    private Page<OfficeFollow> getOfficeFollowPageBySystemName(String systemName, String userId, String searchName,
        Pageable pageable) {
        if (StringUtils.isBlank(searchName)) {
            return officeFollowRepository.findBySystemNameAndUserId(systemName, userId, pageable);
        } else {
            String searchPattern = "%" + searchName + "%";
            return officeFollowRepository.findBySystemNameAndParamsLike(systemName, userId, searchPattern, pageable);
        }
    }

    /**
     * 处理带系统名称的单个关注事项数据
     *
     * @param officeFollow 关注事项实体
     * @param tenantId 租户ID
     */
    private void processOfficeFollowWithSystemName(OfficeFollow officeFollow, String tenantId) {
        // 格式化开始时间
        formatStartTime(officeFollow);

        // 处理消息提醒状态（基于系统信息）
        processMessageRemindWithSystemInfo(officeFollow);

        // 处理任务相关信息
        processTaskInfoWithSystem(officeFollow, tenantId);
    }

    /**
     * 处理带系统信息的消息提醒状态
     *
     * @param officeFollow 关注事项实体
     */
    private void processMessageRemindWithSystemInfo(OfficeFollow officeFollow) {
        try {
            String processInstanceId = officeFollow.getProcessInstanceId();
            OfficeDoneInfo officeDoneInfo = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
            officeFollow.setMsgremind(officeDoneInfo.getMeeting() != null && officeDoneInfo.getMeeting().equals("1"));
        } catch (Exception e) {
            LOGGER.warn("处理消息提醒状态失败，流程实例ID: {}", officeFollow.getProcessInstanceId(), e);
            officeFollow.setMsgremind(false);
        }
    }

    /**
     * 处理带系统信息的任务相关信息
     *
     * @param officeFollow 关注事项实体
     * @param tenantId 租户ID
     */
    private void processTaskInfoWithSystem(OfficeFollow officeFollow, String tenantId) {
        String processInstanceId = officeFollow.getProcessInstanceId();
        ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);

        List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();

        if (CollectionUtils.isNotEmpty(taskList)) {
            processActiveTask(officeFollow, taskList);
        } else {
            processCompletedTaskWithSystem(officeFollow, processParam);
        }

        // 设置发送部门信息
        if (processParam != null) {
            officeFollow.setSendDept(processParam.getStartorName());
        }
    }

    /**
     * 处理带系统信息的已完成任务信息
     *
     * @param officeFollow 关注事项实体
     * @param processParam 流程参数
     */
    private void processCompletedTaskWithSystem(OfficeFollow officeFollow, ProcessParam processParam) {
        officeFollow.setTaskId("");
        officeFollow.setItembox(ItemBoxTypeEnum.DONE.getValue());
        officeFollow.setTaskAssignee(processParam != null ? processParam.getCompleter() : "");
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
            LOGGER.error("更新标题失败，流程实例ID: {};标题：{}", processInstanceId, documentTitle, e);
        }
    }
}