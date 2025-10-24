package net.risesoft.service.core.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.api.processadmin.IdentityApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.RuntimeApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.entity.ActRuDetail;
import net.risesoft.entity.Item;
import net.risesoft.entity.ProcessParam;
import net.risesoft.enums.ActRuDetailSignStatusEnum;
import net.risesoft.enums.ActRuDetailStatusEnum;
import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.org.Department;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.processadmin.ExecutionModel;
import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.model.processadmin.IdentityLinkModel;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.ActRuDetailRepository;
import net.risesoft.service.core.ActRuDetailService;
import net.risesoft.service.core.ItemService;
import net.risesoft.service.core.ProcessParamService;
import net.risesoft.service.event.Y9TodoCreatedEvent;
import net.risesoft.service.event.Y9TodoDeletedEvent;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class ActRuDetailServiceImpl implements ActRuDetailService {

    private static final Map<String, List<String>> SUB_NODE_MAP = new HashMap<>();

    private final ActRuDetailRepository actRuDetailRepository;

    private final HistoricTaskApi historictaskApi;

    private final ProcessParamService processParamService;

    private final ItemService itemService;

    private final TaskApi taskApi;

    private final IdentityApi identityApi;

    private final OrgUnitApi orgUnitApi;

    private final RuntimeApi runtimeApi;

    private final ProcessDefinitionApi processDefinitionApi;

    private final ActRuDetailService self;

    public ActRuDetailServiceImpl(
        ActRuDetailRepository actRuDetailRepository,
        HistoricTaskApi historictaskApi,
        ProcessParamService processParamService,
        ItemService itemService,
        TaskApi taskApi,
        IdentityApi identityApi,
        OrgUnitApi orgUnitApi,
        RuntimeApi runtimeApi,
        ProcessDefinitionApi processDefinitionApi,
        @Lazy ActRuDetailService self) {
        this.actRuDetailRepository = actRuDetailRepository;
        this.historictaskApi = historictaskApi;
        this.processParamService = processParamService;
        this.itemService = itemService;
        this.taskApi = taskApi;
        this.identityApi = identityApi;
        this.orgUnitApi = orgUnitApi;
        this.runtimeApi = runtimeApi;
        this.processDefinitionApi = processDefinitionApi;
        this.self = self;
    }

    @Override
    @Transactional
    public Y9Result<Object> claim(String taskId, String assignee) {
        List<ActRuDetail> ardList = actRuDetailRepository.findByTaskId(taskId);
        ardList.forEach(ard -> {
            ard.setLastTime(new Date());
            ard.setStatus(
                ard.getAssignee().equals(assignee) ? ActRuDetailStatusEnum.TODO : ActRuDetailStatusEnum.DOING);
            ard.setSignStatus(
                ard.getAssignee().equals(assignee) ? ActRuDetailSignStatusEnum.DONE : ActRuDetailSignStatusEnum.NONE);
            ard.setAssigneeName(
                orgUnitApi.getOrgUnit(Y9LoginUserHolder.getTenantId(), ard.getAssignee()).getData().getName());
            actRuDetailRepository.save(ard);

            if (!ard.getAssignee().equals(assignee)) {
                Y9Context.publishEvent(new Y9TodoDeletedEvent<>(ard));
            }
        });
        return Y9Result.success();
    }

    @Override
    @Transactional
    public void copy(String oldProcessSerialNumber, String newProcessSerialNumber, String newProcessInstanceId) {
        try {
            ProcessParam processParam = processParamService.findByProcessSerialNumber(newProcessSerialNumber);
            String itemId = processParam.getItemId();
            Item item = itemService.findById(itemId);
            List<ActRuDetail> list = actRuDetailRepository.findByProcessSerialNumber(oldProcessSerialNumber);
            List<ActRuDetail> listTemp = new ArrayList<>();
            ActRuDetail ard;
            for (ActRuDetail actRuDetail : list) {
                ard = new ActRuDetail();
                Y9BeanUtil.copyProperties(actRuDetail, ard);
                ard.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                ard.setProcessSerialNumber(newProcessSerialNumber);
                ard.setProcessInstanceId(newProcessInstanceId);
                ard.setStarted(true);
                ard.setItemId(processParam.getItemId());
                ard.setProcessDefinitionKey(item.getWorkflowGuid());
                ard.setStatus(ActRuDetailStatusEnum.DOING);
                ard.setTaskId("");
                listTemp.add(ard);
            }
            actRuDetailRepository.saveAll(listTemp);
        } catch (Exception e) {
            LOGGER.error("Copy act_ru_detail error", e);
        }
    }

    @Override
    public int countBySystemNameAndAssignee(String systemName, String assignee) {
        return actRuDetailRepository
            .countBySystemNameAndAssigneeAndEndedTrueAndDeletedFalseAndPlaceOnFileFalse(systemName, assignee);
    }

    @Override
    public int countByAssigneeAndStatus(String assignee, ActRuDetailStatusEnum status) {
        int count;
        if (ActRuDetailStatusEnum.TODO == status) {
            count = actRuDetailRepository.countByAssigneeAndStatusAndDeletedFalse(assignee, status);
        } else {
            count = actRuDetailRepository.countByAssigneeAndStatusAndEndedFalseAndDeletedFalse(assignee, status);
        }
        return count;
    }

    @Override
    public int countBySystemNameAndAssigneeAndStatus(String systemName, String assignee, ActRuDetailStatusEnum status) {
        int count;
        if (ActRuDetailStatusEnum.TODO == status) {
            count = actRuDetailRepository.countBySystemNameAndAssigneeAndStatusAndDeletedFalse(systemName, assignee,
                status);
        } else {
            count = actRuDetailRepository.countBySystemNameAndAssigneeAndStatusAndEndedFalseAndDeletedFalse(systemName,
                assignee, status);
        }
        return count;
    }

    @Override
    @Transactional
    public boolean deleteByExecutionId(String executionId) {
        List<ActRuDetail> list = getActRuDetailsByExecutionId(executionId);
        list.forEach(actRuDetail -> actRuDetail.setDeleted(true));
        actRuDetailRepository.saveAll(list);
        list.forEach(actRuDetail -> Y9Context.publishEvent(new Y9TodoDeletedEvent<>(actRuDetail)));
        return true;
    }

    /**
     * 放入回收站时，为待办状态的需要调用第三方接口删除待办
     *
     * @param processSerialNumber 流程序列号
     * @return boolean
     */
    @Override
    @Transactional
    public boolean deletedByProcessSerialNumber(String processSerialNumber) {
        try {
            List<ActRuDetail> listTemp = new ArrayList<>();
            actRuDetailRepository.findByProcessSerialNumber(processSerialNumber).forEach(actRuDetail -> {
                actRuDetail.setDeleted(true);
                listTemp.add(actRuDetail);
            });
            actRuDetailRepository.saveAll(listTemp);
            listTemp.forEach(actRuDetail -> Y9Context.publishEvent(new Y9TodoDeletedEvent<>(actRuDetail)));
            return true;
        } catch (Exception e) {
            LOGGER.error("Delete act_ru_detail error", e);
        }
        return false;
    }

    /**
     * 正常办结时，会先监听到任务的删除事件，任务的删除事件会调用第三方待办删除接口，所以这里不再需要调用第三方待办接口
     *
     * @param processInstanceId 流程实例id
     * @return boolean
     */
    @Override
    @Transactional
    public boolean endByProcessInstanceId(String processInstanceId) {
        List<ActRuDetail> list = actRuDetailRepository.findByProcessInstanceId(processInstanceId);
        List<ActRuDetail> listTemp = new ArrayList<>();
        for (ActRuDetail actRuDetail : list) {
            actRuDetail.setEnded(true);
            listTemp.add(actRuDetail);
        }
        actRuDetailRepository.saveAll(listTemp);
        return true;
    }

    @Override
    public ActRuDetail findByProcessInstanceIdAndAssigneeAndStatusEquals1(String processInstanceId, String assignee) {
        return actRuDetailRepository.findByProcessInstanceIdAndAssigneeAndStatus(processInstanceId, assignee,
            ActRuDetailStatusEnum.DOING);
    }

    @Override
    public ActRuDetail findByProcessSerialNumberAndAssigneeAndStatusEquals1(String processSerialNumber,
        String assignee) {
        List<ActRuDetail> list = actRuDetailRepository
            .findByProcessSerialNumberAndAssigneeAndStatus(processSerialNumber, assignee, ActRuDetailStatusEnum.DOING);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public ActRuDetail findByTaskIdAndAssignee(String taskId, String assignee) {
        return actRuDetailRepository.findByTaskIdAndAssignee(taskId, assignee);
    }

    private void initSubNodeMap(String processDefinitionId) {
        if (null != SUB_NODE_MAP.get(processDefinitionId)) {
            return;
        }
        List<String> subTaskDefKeys =
            processDefinitionApi.getSubProcessChildNode(Y9LoginUserHolder.getTenantId(), processDefinitionId)
                .getData()
                .stream()
                .map(TargetModel::getTaskDefKey)
                .collect(Collectors.toList());
        SUB_NODE_MAP.put(processDefinitionId, subTaskDefKeys);
    }

    @Override
    public List<ActRuDetail> listByProcessInstanceId(String processInstanceId) {
        return actRuDetailRepository.findByProcessInstanceId(processInstanceId);
    }

    @Override
    public List<ActRuDetail> listByProcessInstanceIdAndStatus(String processInstanceId, ActRuDetailStatusEnum status) {
        return actRuDetailRepository.findByProcessInstanceIdAndStatusOrderByCreateTimeAsc(processInstanceId, status);
    }

    @Override
    public List<ActRuDetail> listByProcessSerialNumber(String processSerialNumber) {
        return actRuDetailRepository.findByProcessSerialNumber(processSerialNumber);
    }

    @Override
    public List<ActRuDetail> listByProcessSerialNumberAndEnded(String processSerialNumber, boolean ended) {
        return actRuDetailRepository.findByProcessSerialNumberAndEnded(processSerialNumber, ended);
    }

    @Override
    public List<ActRuDetail> listByProcessSerialNumberAndStatus(String processSerialNumber,
        ActRuDetailStatusEnum status) {
        return actRuDetailRepository.findByProcessSerialNumberAndStatusOrderByCreateTimeAsc(processSerialNumber,
            status);
    }

    @Override
    public Page<ActRuDetail> pageByAssigneeAndStatus(String assignee, ActRuDetailStatusEnum status, int rows, int page,
        Sort sort) {
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        Page<ActRuDetail> pageList;
        if (ActRuDetailStatusEnum.TODO == status) {
            pageList = actRuDetailRepository.findByAssigneeAndStatusAndDeletedFalse(assignee, status, pageable);
        } else {
            pageList =
                actRuDetailRepository.findByAssigneeAndStatusAndEndedFalseAndDeletedFalse(assignee, status, pageable);
        }
        return pageList;
    }

    @Override
    public Page<ActRuDetail> pageBySystemName(String systemName, int rows, int page, Sort sort) {
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        return actRuDetailRepository.findBySystemNameNativeQuery(systemName, pageable);
    }

    @Override
    public Page<ActRuDetail> pageBySystemNameAndAssignee(String systemName, String assignee, int rows, int page,
        Sort sort) {
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        return actRuDetailRepository.findBySystemNameAndAssigneeAndDeletedFalse(systemName, assignee, pageable);
    }

    @Override
    public Page<ActRuDetail> pageBySystemNameAndAssigneeAndDeletedTrue(String systemName, String assignee, int rows,
        int page, Sort sort) {
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        return actRuDetailRepository.findBySystemNameAndAssigneeAndDeletedTrue(systemName, assignee, pageable);
    }

    @Override
    public Page<ActRuDetail> pageBySystemNameAndAssigneeAndEnded(String systemName, String assignee, boolean ended,
        int rows, int page, Sort sort) {
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        return actRuDetailRepository.findBySystemNameAndAssigneeAndDeletedFalse(systemName, assignee, ended, pageable);
    }

    @Override
    public Page<ActRuDetail> pageBySystemNameAndAssigneeAndStatus(String systemName, String assignee,
        ActRuDetailStatusEnum status, int rows, int page, Sort sort) {
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        Page<ActRuDetail> pageList;
        if (ActRuDetailStatusEnum.TODO == status) {
            pageList = actRuDetailRepository.findBySystemNameAndAssigneeAndStatusAndDeletedFalse(systemName, assignee,
                status, pageable);
        } else {
            pageList = actRuDetailRepository.findBySystemNameAndAssigneeAndStatusAndEndedFalseAndDeletedFalse(
                systemName, assignee, status, pageable);
        }
        return pageList;
    }

    @Override
    public Page<ActRuDetail> pageBySystemNameAndAssigneeAndStatusAndTaskDefKey(String systemName, String assignee,
        ActRuDetailStatusEnum status, String taskDefKey, int rows, int page, Sort sort) {
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        Page<ActRuDetail> pageList;
        if (ActRuDetailStatusEnum.TODO == status) {
            pageList = actRuDetailRepository.findBySystemNameAndTaskDefKeyAndAssigneeAndStatusAndDeletedFalse(
                systemName, taskDefKey, assignee, status, pageable);
        } else {
            pageList = actRuDetailRepository.findBySystemNameAndAssigneeAndStatusAndEndedFalseAndDeletedFalse(
                systemName, assignee, status, pageable);
        }
        return pageList;
    }

    @Override
    public Page<ActRuDetail> pageBySystemNameAndAssigneeAndStatusEquals1(String systemName, String assignee, int rows,
        int page, Sort sort) {
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        return actRuDetailRepository.findBySystemNameAndAssigneeAndStatusAndDeletedFalse(systemName, assignee,
            ActRuDetailStatusEnum.DOING, pageable);
    }

    @Override
    public Page<ActRuDetail> pageBySystemNameAndDeletedTrue(String systemName, int page, int rows, Sort sort) {
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        return actRuDetailRepository.findBySystemNameAndDeletedTrueNativeQuery(systemName, pageable);
    }

    @Override
    public Page<ActRuDetail> pageBySystemNameAndDeptIdAndDeletedTrue(String systemName, String deptId, boolean isBureau,
        int rows, int page, Sort sort) {
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        if (isBureau) {
            return actRuDetailRepository.findBySystemNameAndBureauIdAndDeletedTrue(systemName, deptId, pageable);
        }
        return actRuDetailRepository.findBySystemNameAndDeptIdAndDeletedTrue(systemName, deptId, pageable);
    }

    @Override
    public Page<ActRuDetail> pageBySystemNameAndDeptIdAndEnded(String systemName, String deptId, boolean isBureau,
        boolean ended, int rows, int page, Sort sort) {
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        Page<ActRuDetail> pageList;
        if (isBureau) {
            pageList = actRuDetailRepository.findBySystemNameAndBureauIdAndEndedAndDeletedFalseNativeQuery(systemName,
                deptId, ended, pageable);
        } else {
            pageList = actRuDetailRepository.findBySystemNameAndDeptIdAndEndedAndDeletedFalseNativeQuery(systemName,
                deptId, ended, pageable);
        }
        return pageList;
    }

    @Override
    public Page<ActRuDetail> pageBySystemNameAndEnded(String systemName, boolean ended, int page, int rows, Sort sort) {
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        return actRuDetailRepository.findBySystemNameAndEndedNativeQuery(systemName, ended, pageable);
    }

    @Override
    @Transactional
    public boolean placeOnFileByProcessSerialNumber(String processSerialNumber) {
        try {
            List<ActRuDetail> list = actRuDetailRepository.findByProcessSerialNumber(processSerialNumber);
            List<ActRuDetail> listTemp = new ArrayList<>();
            for (ActRuDetail actRuDetail : list) {
                actRuDetail.setPlaceOnFile(true);
                actRuDetail.setStatus(ActRuDetailStatusEnum.DOING);
                listTemp.add(actRuDetail);
            }
            actRuDetailRepository.saveAll(listTemp);
            return true;
        } catch (Exception e) {
            LOGGER.error("Place on file act_ru_detail error", e);
        }
        return false;
    }

    /**
     * 减签恢复会签时，之前为待办状态的需要调用第三方待办接口
     *
     * @param executionId 执行id
     */
    @Override
    @Transactional
    public void recoveryByExecutionId(String executionId) {
        List<ActRuDetail> list = getActRuDetailsByExecutionId(executionId);
        list.forEach(actRuDetail -> actRuDetail.setDeleted(false));
        actRuDetailRepository.saveAll(list);
        list.stream()
            .filter(actRuDetail -> actRuDetail.getStatus().equals(ActRuDetailStatusEnum.TODO))
            .forEach(actRuDetail -> Y9Context.publishEvent(new Y9TodoCreatedEvent<>(actRuDetail)));
    }

    /**
     * 根据执行ID获取对应的活动任务详情列表
     *
     * @param executionId 执行ID
     * @return 匹配的ActRuDetail列表
     */
    private List<ActRuDetail> getActRuDetailsByExecutionId(String executionId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        ExecutionModel executionModel = runtimeApi.getExecutionById(tenantId, executionId).getData();
        List<ActRuDetail> list = actRuDetailRepository.findByProcessInstanceId(executionModel.getProcessInstanceId());
        return list.stream().filter(actRuDetail -> {
            String taskExecutionId =
                historictaskApi.getById(tenantId, actRuDetail.getTaskId()).getData().getExecutionId();
            return taskExecutionId.equals(executionId);
        }).collect(Collectors.toList());
    }

    /**
     * 真办结后恢复待办，会产生新的任务，不需要在这里调用第三方待办接口
     *
     * @param processInstanceId 流程实例id
     * @return boolean
     */
    @Override
    @Transactional
    public boolean recoveryByProcessInstanceId(String processInstanceId) {
        List<ActRuDetail> listTemp = new ArrayList<>();
        actRuDetailRepository.findByProcessInstanceId(processInstanceId).forEach(actRuDetail -> {
            actRuDetail.setEnded(false);
            listTemp.add(actRuDetail);
        });
        actRuDetailRepository.saveAll(listTemp);
        return true;
    }

    /**
     * 删除后恢复待办，之前为待办状态的需要调用第三方待办接口
     *
     * @param processSerialNumber 流程序列号
     * @return boolean
     */
    @Override
    @Transactional
    public boolean recoveryByProcessSerialNumber(String processSerialNumber) {
        List<ActRuDetail> listTemp = new ArrayList<>();
        actRuDetailRepository.findByProcessSerialNumber(processSerialNumber).forEach(actRuDetail -> {
            actRuDetail.setDeleted(false);
            listTemp.add(actRuDetail);
        });
        actRuDetailRepository.saveAll(listTemp);
        listTemp.stream()
            .filter(actRuDetail -> actRuDetail.getStatus().equals(ActRuDetailStatusEnum.TODO))
            .forEach(actRuDetail -> Y9Context.publishEvent(new Y9TodoCreatedEvent<>(actRuDetail)));
        return true;
    }

    @Override
    @Transactional
    public Y9Result<Object> refuseClaim(String taskId, String assignee) {
        List<ActRuDetail> ardList = actRuDetailRepository.findByTaskId(taskId);
        StringBuilder names = new StringBuilder();
        ardList.forEach(ard -> {
            if (!ard.getAssignee().equals(assignee)) {
                if (StringUtils.isBlank(names)) {
                    names.append(ard.getAssigneeName());
                } else {
                    names.append("、").append(ard.getAssigneeName());
                }
            }
        });
        ardList.forEach(ard -> {
            ard.setStatus(
                ard.getAssignee().equals(assignee) ? ActRuDetailStatusEnum.DOING : ActRuDetailStatusEnum.TODO);
            ard.setSignStatus(
                ard.getAssignee().equals(assignee) ? ActRuDetailSignStatusEnum.REFUSE : ActRuDetailSignStatusEnum.TODO);
            ard.setLastTime(new Date());
            ard.setAssigneeName(ard.getAssignee().equals(assignee) ? ard.getAssigneeName() : names.toString());
            actRuDetailRepository.save(ard);
        });
        return Y9Result.success();
    }

    /**
     * 彻底删除流程实例时，为待办状态的需要调用第三方接口删除待办
     *
     * @param processInstanceId 流程实例id
     * @return boolean
     */
    @Override
    @Transactional
    public boolean removeByProcessInstanceId(String processInstanceId) {
        List<ActRuDetail> list = actRuDetailRepository.findByProcessInstanceId(processInstanceId);
        actRuDetailRepository.deleteAll(list);
        list.stream()
            .filter(actRuDetail -> actRuDetail.getStatus().equals(ActRuDetailStatusEnum.TODO))
            .forEach(actRuDetail -> Y9Context.publishEvent(new Y9TodoDeletedEvent<>(actRuDetail)));
        return true;
    }

    /**
     * 彻底删除流程实例时，为非删除状态且待办状态的需要调用第三方接口删除待办
     *
     * @param processSerialNumber 流程序列号
     * @return boolean
     */
    @Override
    @Transactional
    public boolean removeByProcessSerialNumber(String processSerialNumber) {
        List<ActRuDetail> list = actRuDetailRepository.findByProcessSerialNumber(processSerialNumber);
        actRuDetailRepository.deleteAll(list);
        list.stream()
            .filter(
                actRuDetail -> !actRuDetail.isDeleted() && actRuDetail.getStatus().equals(ActRuDetailStatusEnum.TODO))
            .forEach(actRuDetail -> Y9Context.publishEvent(new Y9TodoDeletedEvent<>(actRuDetail)));
        return true;
    }

    @Override
    @Transactional
    public boolean revokePlaceOnFileByProcessSerialNumber(String processSerialNumber, String todoPersonId) {
        try {
            List<ActRuDetail> list = actRuDetailRepository.findByProcessSerialNumber(processSerialNumber);
            List<ActRuDetail> listTemp = new ArrayList<>();
            for (ActRuDetail actRuDetail : list) {
                actRuDetail.setPlaceOnFile(false);
                actRuDetail.setEnded(false);
                if (StringUtils.isEmpty(todoPersonId)) {
                    actRuDetail.setStatus(ActRuDetailStatusEnum.TODO);
                } else {
                    if (todoPersonId.equals(actRuDetail.getAssignee())) {
                        actRuDetail.setStatus(ActRuDetailStatusEnum.TODO);
                        actRuDetail.setLastTime(new Date());
                    }
                }
                listTemp.add(actRuDetail);
            }
            actRuDetailRepository.saveAll(listTemp);
            return true;
        } catch (Exception e) {
            LOGGER.error("Revoke place on file act_ru_detail error", e);
        }
        return false;
    }

    @Override
    @Transactional
    public void saveOrUpdate(ActRuDetail actRuDetail) {
        initSubNodeMap(actRuDetail.getProcessDefinitionId());
        String processSerialNumber = actRuDetail.getProcessSerialNumber();
        String assignee = actRuDetail.getAssignee();
        ActRuDetail doing = this.findByProcessSerialNumberAndAssigneeAndStatusEquals1(processSerialNumber, assignee);
        ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
        OrgUnit sendDept =
            orgUnitApi.getOrgUnit(Y9LoginUserHolder.getTenantId(), actRuDetail.getSendDeptId()).getData();
        String sendDeptName = sendDept.getOrgType().equals(OrgTypeEnum.DEPARTMENT)
            ? ((Department)sendDept).getAliasName() : sendDept.getName();
        if (null != doing) {
            doing.setSendUserId(actRuDetail.getSendUserId());
            doing.setSendUserName(actRuDetail.getSendUserName());
            doing.setSendDeptId(actRuDetail.getSendDeptId());
            doing.setSendDeptName(sendDeptName);
            doing.setLastTime(actRuDetail.getLastTime());
            doing.setStatus(actRuDetail.getStatus());
            doing.setTaskId(actRuDetail.getTaskId());
            doing.setProcessInstanceId(actRuDetail.getProcessInstanceId());
            doing.setExecutionId(actRuDetail.getExecutionId());
            doing.setStarted(actRuDetail.isStarted());
            doing.setDueDate(processParam.getDueDate());
            doing.setProcessDefinitionId(actRuDetail.getProcessDefinitionId());
            doing.setAssigneeName(actRuDetail.getAssigneeName());
            doing.setTaskDefKey(actRuDetail.getTaskDefKey());
            doing.setTaskDefName(actRuDetail.getTaskDefName());
            doing.setSignStatus(
                null == actRuDetail.getSignStatus() ? ActRuDetailSignStatusEnum.NONE : actRuDetail.getSignStatus());
            doing.setSub(SUB_NODE_MAP.get(actRuDetail.getProcessDefinitionId())
                .stream()
                .anyMatch(taskDefKey -> taskDefKey.equals(actRuDetail.getTaskDefKey())));
            actRuDetailRepository.save(doing);
            if (actRuDetail.getStatus().equals(ActRuDetailStatusEnum.TODO)) {
                Y9Context.publishEvent(new Y9TodoCreatedEvent<>(doing));
            }
            return;
        }
        OrgUnit dept = orgUnitApi.getOrgUnit(Y9LoginUserHolder.getTenantId(), actRuDetail.getDeptId()).getData();
        String deptName = dept.getOrgType().equals(OrgTypeEnum.DEPARTMENT)
            && StringUtils.isNotBlank(((Department)dept).getAliasName()) ? ((Department)dept).getAliasName()
                : dept.getName();
        actRuDetail.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        actRuDetail.setDeptName(deptName);
        actRuDetail.setSendDeptName(sendDept.getName());
        actRuDetail.setSystemName(processParam.getSystemName());
        actRuDetail.setDueDate(processParam.getDueDate());
        actRuDetail.setDeleted(false);
        actRuDetail.setSub(SUB_NODE_MAP.get(actRuDetail.getProcessDefinitionId())
            .stream()
            .anyMatch(taskDefKey -> taskDefKey.equals(actRuDetail.getTaskDefKey())));
        OrgUnit bureau = orgUnitApi.getBureau(Y9LoginUserHolder.getTenantId(), actRuDetail.getDeptId()).getData();
        actRuDetail.setBureauId(bureau.getId());
        actRuDetail.setBureauName(bureau.getName());
        actRuDetail.setSignStatus(
            null == actRuDetail.getSignStatus() ? ActRuDetailSignStatusEnum.NONE : actRuDetail.getSignStatus());
        actRuDetailRepository.save(actRuDetail);
        Y9Context.publishEvent(new Y9TodoCreatedEvent<>(actRuDetail));
    }

    @Override
    @Transactional
    public void setRead(String id) {
        actRuDetailRepository.findById(id).ifPresent(actRuDetail -> {
            actRuDetail.setStarted(false);
            actRuDetailRepository.save(actRuDetail);
        });
    }

    @Override
    @Transactional
    public boolean syncByProcessInstanceId(String processInstanceId) {
        ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
        String systemName = processParam.getSystemName();
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<HistoricTaskInstanceModel> htiList =
            historictaskApi.findTaskByProcessInstanceIdOrByEndTimeAsc(tenantId, processInstanceId, "").getData();
        for (HistoricTaskInstanceModel hti : htiList) {
            String assignee = hti.getAssignee();
            String owner = hti.getOwner();
            TaskModel taskTemp = taskApi.findById(tenantId, hti.getId()).getData();
            if (StringUtils.isNotBlank(assignee)) {
                handleNotBlankAssignee(hti, systemName, assignee, owner, taskTemp);
            } else {
                handleBlankAssignee(hti, systemName, tenantId, taskTemp);
            }
        }
        return true;
    }

    /**
     * 处理办理人不为空的情况
     */
    private void handleNotBlankAssignee(HistoricTaskInstanceModel hti, String systemName, String assignee, String owner,
        TaskModel taskTemp) {
        if (StringUtils.isNotBlank(owner)) {
            // 处理有所有者的情况（恢复待办且恢复人员不是办理人员）
            saveOwnerDetails(hti, systemName, owner);
            saveAssigneeDetails(hti, assignee, taskTemp);
        } else {
            // 处理普通情况（恢复待办人员是当前任务办理人）
            saveAssigneeDetails(hti, assignee, taskTemp);
        }
    }

    /**
     * 处理办理人为空的情况（区长办件）
     */
    private void handleBlankAssignee(HistoricTaskInstanceModel hti, String systemName, String tenantId,
        TaskModel taskTemp) {
        ActRuDetail actRuDetail = createBaseActRuDetail(hti, systemName, taskTemp);
        String assignee = findAssigneeFromIdentityLinks(tenantId, hti.getId());
        actRuDetail.setAssignee(assignee);
        self.saveOrUpdate(actRuDetail);
    }

    /**
     * 保存所有者详情
     */
    private void saveOwnerDetails(HistoricTaskInstanceModel hti, String systemName, String owner) {
        ActRuDetail actRuDetail = new ActRuDetail();
        actRuDetail.setAssignee(owner);
        actRuDetail.setLastTime(hti.getEndTime());
        actRuDetail.setProcessDefinitionKey(hti.getProcessDefinitionId().split(":")[0]);
        actRuDetail.setSystemName(systemName);
        actRuDetail.setProcessInstanceId(hti.getProcessInstanceId());
        actRuDetail.setStatus(ActRuDetailStatusEnum.DOING);
        actRuDetail.setTaskId(hti.getId());
        actRuDetail.setStarted(true);
        actRuDetail.setEnded(false);
        self.saveOrUpdate(actRuDetail);
    }

    /**
     * 保存办理人详情
     */
    private void saveAssigneeDetails(HistoricTaskInstanceModel hti, String assignee, TaskModel taskTemp) {
        ActRuDetail actRuDetail = createBaseActRuDetail(hti, null, taskTemp);
        actRuDetail.setAssignee(assignee);
        self.saveOrUpdate(actRuDetail);
    }

    /**
     * 创建基础的 ActRuDetail 对象
     */
    private ActRuDetail createBaseActRuDetail(HistoricTaskInstanceModel hti, String systemName, TaskModel taskTemp) {
        ActRuDetail actRuDetail = new ActRuDetail();
        actRuDetail.setProcessDefinitionKey(hti.getProcessDefinitionId().split(":")[0]);
        actRuDetail.setSystemName(systemName);
        actRuDetail.setProcessInstanceId(hti.getProcessInstanceId());
        actRuDetail.setTaskId(hti.getId());
        // 设置状态和时间
        if (null != taskTemp) {
            actRuDetail.setStatus(ActRuDetailStatusEnum.TODO);
            actRuDetail.setLastTime(null);
        } else {
            actRuDetail.setStatus(ActRuDetailStatusEnum.DOING);
            actRuDetail.setLastTime(hti.getEndTime());
        }

        return actRuDetail;
    }

    /**
     * 从身份链接中查找办理人
     */
    private String findAssigneeFromIdentityLinks(String tenantId, String taskId) {
        List<IdentityLinkModel> identityLinkList = new ArrayList<>();
        try {
            identityLinkList = identityApi.getIdentityLinksForTask(tenantId, taskId).getData();
        } catch (Exception e) {
            LOGGER.error("Get identity links for task error", e);
        }
        String assignee = "";
        for (IdentityLinkModel il : identityLinkList) {
            if (StringUtils.isNotBlank(il.getUserId()) && "assignee".equals(il.getType())) {
                assignee = il.getUserId();
                break;
            }
        }
        return assignee;
    }

    @Override
    @Transactional
    public Y9Result<Object> todo2doing(String taskId, String assignee) {
        ActRuDetail todo = actRuDetailRepository.findByTaskIdAndAssignee(taskId, assignee);
        List<ActRuDetail> doingList = actRuDetailRepository.findByProcessSerialNumberAndAssigneeAndStatus(
            todo.getProcessSerialNumber(), assignee, ActRuDetailStatusEnum.DOING);
        if (doingList.isEmpty()) {
            todo.setStatus(ActRuDetailStatusEnum.DOING);
            todo.setLastTime(new Date());
            actRuDetailRepository.save(todo);
        } else {
            ActRuDetail doing = doingList.get(0);
            doing.setLastTime(new Date());
            doing.setTaskId(todo.getTaskId());
            doing.setTaskDefKey(todo.getTaskDefKey());
            doing.setTaskDefName(todo.getTaskDefName());
            doing.setExecutionId(todo.getExecutionId());
            doing.setSub(SUB_NODE_MAP.get(todo.getProcessDefinitionId())
                .stream()
                .anyMatch(taskDefKey -> taskDefKey.equals(todo.getTaskDefKey())));
            actRuDetailRepository.save(doing);
            actRuDetailRepository.delete(todo);
        }
        Y9Context.publishEvent(new Y9TodoDeletedEvent<>(todo));
        return Y9Result.success();
    }

    @Override
    @Transactional
    public Y9Result<Object> unClaim(String taskId) {
        List<ActRuDetail> ardList = actRuDetailRepository.findByTaskId(taskId);
        TaskModel task = taskApi.findById(Y9LoginUserHolder.getTenantId(), taskId).getData();
        StringBuilder names = new StringBuilder();
        ardList.forEach(ard -> {
            if (StringUtils.isBlank(names)) {
                names.append(ard.getAssigneeName());
            } else {
                names.append("、").append(ard.getAssigneeName());
            }
        });
        ardList.forEach(ard -> {
            ard.setStatus(ActRuDetailStatusEnum.TODO);
            ard.setSignStatus(ActRuDetailSignStatusEnum.TODO);
            ard.setLastTime(new Date());
            ard.setAssigneeName(names.toString());
            actRuDetailRepository.save(ard);

            if (!task.getAssignee().equals(ard.getAssignee())) {
                Y9Context.publishEvent(new Y9TodoCreatedEvent<>(ard));
            }
        });
        return Y9Result.success();
    }

}