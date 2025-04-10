package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.api.processadmin.IdentityApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.RuntimeApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.entity.ActRuDetail;
import net.risesoft.entity.ProcessParam;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.enums.ActRuDetailSignStatusEnum;
import net.risesoft.enums.ActRuDetailStatusEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.processadmin.ExecutionModel;
import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.model.processadmin.IdentityLinkModel;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.ActRuDetailRepository;
import net.risesoft.service.ActRuDetailService;
import net.risesoft.service.ProcessParamService;
import net.risesoft.service.SpmApproveItemService;
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
@Transactional(readOnly = true)
public class ActRuDetailServiceImpl implements ActRuDetailService {

    private static final Map<String, List<String>> SUB_NODE_MAP = new HashMap<>();

    private final ActRuDetailRepository actRuDetailRepository;

    private final HistoricTaskApi historictaskApi;

    private final ProcessParamService processParamService;

    private final SpmApproveItemService itemService;

    private final TaskApi taskApi;

    private final IdentityApi identityApi;

    private final OrgUnitApi orgUnitApi;

    private final RuntimeApi runtimeApi;

    private final ProcessDefinitionApi processDefinitionApi;

    @Override
    @Transactional
    public void copy(String oldProcessSerialNumber, String newProcessSerialNumber, String newProcessInstanceId) {
        try {
            ProcessParam processParam = processParamService.findByProcessSerialNumber(newProcessSerialNumber);
            String itemId = processParam.getItemId();
            SpmApproveItem item = itemService.findById(itemId);
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
                ard.setStatus(ActRuDetailStatusEnum.DOING.getValue());
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
    public int countBySystemNameAndAssigneeAndStatus(String systemName, String assignee, int status) {
        int count;
        if (ActRuDetailStatusEnum.TODO.getValue() == status) {
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
    public boolean deletedByProcessSerialNumber(String processSerialNumber) {
        try {
            List<ActRuDetail> list = actRuDetailRepository.findByProcessSerialNumber(processSerialNumber);
            List<ActRuDetail> listTemp = new ArrayList<>();
            for (ActRuDetail actRuDetail : list) {
                actRuDetail.setDeleted(true);
                listTemp.add(actRuDetail);
            }
            actRuDetailRepository.saveAll(listTemp);
            return true;
        } catch (Exception e) {
            LOGGER.error("Delete act_ru_detail error", e);
        }
        return false;
    }

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
    @Transactional
    public boolean deleteByExecutionId(String executionId) {
        ExecutionModel executionModel =
            runtimeApi.getExecutionById(Y9LoginUserHolder.getTenantId(), executionId).getData();
        List<ActRuDetail> list = actRuDetailRepository.findByProcessInstanceId(executionModel.getProcessInstanceId());
        list = list.stream()
            .filter(actRuDetail -> historictaskApi.getById(Y9LoginUserHolder.getTenantId(), actRuDetail.getTaskId())
                .getData().getExecutionId().equals(executionId))
            .collect(Collectors.toList());
        list.forEach(actRuDetail -> actRuDetail.setDeleted(true));
        actRuDetailRepository.saveAll(list);
        return true;
    }

    @Override
    @Transactional
    public boolean endByProcessSerialNumber(String processSerialNumber) {
        List<ActRuDetail> list = actRuDetailRepository.findByProcessSerialNumber(processSerialNumber);
        List<ActRuDetail> listTemp = new ArrayList<>();
        for (ActRuDetail actRuDetail : list) {
            actRuDetail.setStatus(1);
            actRuDetail.setEnded(true);
            listTemp.add(actRuDetail);
        }
        actRuDetailRepository.saveAll(listTemp);
        return true;
    }

    @Override
    public ActRuDetail findByProcessInstanceIdAndAssigneeAndStatusEquals1(String processInstanceId, String assignee) {
        return actRuDetailRepository.findByProcessInstanceIdAndAssigneeAndStatus(processInstanceId, assignee,
            ActRuDetailStatusEnum.DOING.getValue());
    }

    @Override
    public ActRuDetail findByProcessSerialNumberAndAssigneeAndStatusEquals1(String processSerialNumber,
        String assignee) {
        List<ActRuDetail> list = actRuDetailRepository.findByProcessSerialNumberAndAssigneeAndStatus(
            processSerialNumber, assignee, ActRuDetailStatusEnum.DOING.getValue());
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public ActRuDetail findByTaskIdAndAssignee(String taskId, String assignee) {
        return actRuDetailRepository.findByTaskIdAndAssignee(taskId, assignee);
    }

    @Override
    public List<ActRuDetail> listByProcessInstanceId(String processInstanceId) {
        return actRuDetailRepository.findByProcessInstanceId(processInstanceId);
    }

    @Override
    public List<ActRuDetail> listByProcessInstanceIdAndStatus(String processInstanceId, int status) {
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
    public List<ActRuDetail> listByProcessSerialNumberAndStatus(String processSerialNumber, int status) {
        return actRuDetailRepository.findByProcessSerialNumberAndStatusOrderByCreateTimeAsc(processSerialNumber,
            status);
    }

    @Override
    public Page<ActRuDetail> pageBySystemNameAndAssigneeAndEnded(String systemName, String assignee, boolean ended,
        int rows, int page, Sort sort) {
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        return actRuDetailRepository.findBySystemNameAndAssigneeAndDeletedFalse(systemName, assignee, ended, pageable);
    }

    @Override
    public Page<ActRuDetail> pageBySystemNameAndEnded(String systemName, boolean ended, int page, int rows, Sort sort) {
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        return actRuDetailRepository.findBySystemNameAndEndedNativeQuery(systemName, ended, pageable);
    }

    @Override
    public Page<ActRuDetail> pageBySystemNameAndAssigneeAndDeletedTrue(String systemName, String assignee, int rows,
        int page, Sort sort) {
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        return actRuDetailRepository.findBySystemNameAndAssigneeAndDeletedTrue(systemName, assignee, pageable);
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
    public Page<ActRuDetail> pageByAssigneeAndStatus(String assignee, int status, int rows, int page, Sort sort) {
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        Page<ActRuDetail> pageList;
        if (ActRuDetailStatusEnum.TODO.getValue() == status) {
            pageList = actRuDetailRepository.findByAssigneeAndStatusAndDeletedFalse(assignee, status, pageable);
        } else {
            pageList =
                actRuDetailRepository.findByAssigneeAndStatusAndEndedFalseAndDeletedFalse(assignee, status, pageable);
        }
        return pageList;
    }

    @Override
    public Page<ActRuDetail> pageBySystemNameAndAssigneeAndStatus(String systemName, String assignee, int status,
        int rows, int page, Sort sort) {
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        Page<ActRuDetail> pageList;
        if (ActRuDetailStatusEnum.TODO.getValue() == status) {
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
        int status, String taskDefKey, int rows, int page, Sort sort) {
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        Page<ActRuDetail> pageList;
        if (ActRuDetailStatusEnum.TODO.getValue() == status) {
            pageList = actRuDetailRepository.findBySystemNameAndTaskDefKeyAndAssigneeAndStatusAndDeletedFalse(
                systemName, taskDefKey, assignee, status, pageable);
        } else {
            pageList = actRuDetailRepository.findBySystemNameAndAssigneeAndStatusAndEndedFalseAndDeletedFalse(
                systemName, assignee, status, pageable);
        }
        return pageList;
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
    public Page<ActRuDetail> pageBySystemNameAndAssignee(String systemName, String assignee, int rows, int page,
        Sort sort) {
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        return actRuDetailRepository.findBySystemNameAndAssigneeAndDeletedFalse(systemName, assignee, pageable);
    }

    @Override
    public Page<ActRuDetail> pageBySystemName(String systemName, int rows, int page, Sort sort) {
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        return actRuDetailRepository.findBySystemNameNativeQuery(systemName, pageable);
    }

    @Override
    public Page<ActRuDetail> pageBySystemNameAndAssigneeAndStatusEquals1(String systemName, String assignee, int rows,
        int page, Sort sort) {
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        return actRuDetailRepository.findBySystemNameAndAssigneeAndStatusAndDeletedFalse(systemName, assignee,
            ActRuDetailStatusEnum.DOING.getValue(), pageable);
    }

    @Override
    @Transactional
    public boolean placeOnFileByProcessSerialNumber(String processSerialNumber) {
        try {
            List<ActRuDetail> list = actRuDetailRepository.findByProcessSerialNumber(processSerialNumber);
            List<ActRuDetail> listTemp = new ArrayList<>();
            for (ActRuDetail actRuDetail : list) {
                actRuDetail.setPlaceOnFile(true);
                actRuDetail.setStatus(1);
                listTemp.add(actRuDetail);
            }
            actRuDetailRepository.saveAll(listTemp);
            return true;
        } catch (Exception e) {
            LOGGER.error("Place on file act_ru_detail error", e);
        }
        return false;
    }

    @Override
    @Transactional
    public boolean recoveryByProcessInstanceId(String processInstanceId) {
        List<ActRuDetail> list = actRuDetailRepository.findByProcessInstanceId(processInstanceId);
        List<ActRuDetail> listTemp = new ArrayList<>();
        for (ActRuDetail actRuDetail : list) {
            actRuDetail.setEnded(false);
            listTemp.add(actRuDetail);
        }
        actRuDetailRepository.saveAll(listTemp);
        return true;
    }

    @Override
    @Transactional
    public boolean recoveryByProcessSerialNumber(String processSerialNumber) {
        List<ActRuDetail> list = actRuDetailRepository.findByProcessSerialNumber(processSerialNumber);
        List<ActRuDetail> listTemp = new ArrayList<>();
        for (ActRuDetail actRuDetail : list) {
            actRuDetail.setDeleted(false);
            listTemp.add(actRuDetail);
        }
        actRuDetailRepository.saveAll(listTemp);
        return true;
    }

    @Override
    @Transactional
    public void recoveryByExecutionId(String executionId) {
        ExecutionModel executionModel =
            runtimeApi.getExecutionById(Y9LoginUserHolder.getTenantId(), executionId).getData();
        List<ActRuDetail> list = actRuDetailRepository.findByProcessInstanceId(executionModel.getProcessInstanceId());
        list = list.stream()
            .filter(actRuDetail -> historictaskApi.getById(Y9LoginUserHolder.getTenantId(), actRuDetail.getTaskId())
                .getData().getExecutionId().equals(executionId))
            .collect(Collectors.toList());
        list.forEach(actRuDetail -> actRuDetail.setDeleted(false));
        actRuDetailRepository.saveAll(list);
    }

    @Override
    @Transactional
    public boolean recoveryTodoByProcessSerialNumber(String processSerialNumber, String todoPersonId) {
        try {
            List<ActRuDetail> list = actRuDetailRepository.findByProcessSerialNumber(processSerialNumber);
            List<ActRuDetail> listTemp = new ArrayList<>();
            for (ActRuDetail actRuDetail : list) {
                actRuDetail.setEnded(false);
                if (StringUtils.isEmpty(todoPersonId)) {
                    actRuDetail.setStatus(0);
                } else {
                    if (todoPersonId.equals(actRuDetail.getAssignee())) {
                        actRuDetail.setStatus(0);
                        actRuDetail.setLastTime(new Date());
                    }
                }
                listTemp.add(actRuDetail);
            }
            actRuDetailRepository.saveAll(listTemp);
            return true;
        } catch (Exception e) {
            LOGGER.error("Recovery todo act_ru_detail error", e);
        }
        return false;
    }

    @Override
    @Transactional
    public boolean removeByProcessInstanceId(String processInstanceId) {
        List<ActRuDetail> list = actRuDetailRepository.findByProcessInstanceId(processInstanceId);
        actRuDetailRepository.deleteAll(list);
        return true;
    }

    @Override
    @Transactional
    public boolean removeByProcessSerialNumber(String processSerialNumber) {
        List<ActRuDetail> list = actRuDetailRepository.findByProcessSerialNumber(processSerialNumber);
        actRuDetailRepository.deleteAll(list);
        return true;
    }

    @Override
    @Transactional
    public boolean removeByProcessSerialNumberAndAssignee(String processSerialNumber, String assignee) {
        ActRuDetail actRuDetail =
            actRuDetailRepository.findByProcessSerialNumberAndAssignee(processSerialNumber, assignee);
        if (null != actRuDetail) {
            actRuDetailRepository.delete(actRuDetail);
        }
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
                    actRuDetail.setStatus(0);
                } else {
                    if (todoPersonId.equals(actRuDetail.getAssignee())) {
                        actRuDetail.setStatus(0);
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

    private void initSubNodeMap(String processDefinitionId) {
        if (null == SUB_NODE_MAP.get(processDefinitionId)) {
            List<String> subTaskDefKeys =
                processDefinitionApi.getSubProcessChildNode(Y9LoginUserHolder.getTenantId(), processDefinitionId)
                    .getData().stream().map(TargetModel::getTaskDefKey).collect(Collectors.toList());
            SUB_NODE_MAP.put(processDefinitionId, subTaskDefKeys);
        }
    }

    @Override
    @Transactional
    public boolean saveOrUpdate(ActRuDetail actRuDetail) {
        initSubNodeMap(actRuDetail.getProcessDefinitionId());
        String processSerialNumber = actRuDetail.getProcessSerialNumber();
        String assignee = actRuDetail.getAssignee();
        ActRuDetail doing = this.findByProcessSerialNumberAndAssigneeAndStatusEquals1(processSerialNumber, assignee);
        ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
        if (null != doing) {
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
            doing.setSignStatus(null == actRuDetail.getSignStatus() ? ActRuDetailSignStatusEnum.NONE.getValue()
                : actRuDetail.getSignStatus());
            doing.setSub(SUB_NODE_MAP.get(actRuDetail.getProcessDefinitionId()).stream()
                .anyMatch(taskDefKey -> taskDefKey.equals(actRuDetail.getTaskDefKey())));
            actRuDetailRepository.save(doing);
            return true;
        }

        actRuDetail.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        OrgUnit dept = orgUnitApi.getOrgUnit(Y9LoginUserHolder.getTenantId(), actRuDetail.getDeptId()).getData();
        actRuDetail.setDeptName(dept.getName());
        actRuDetail.setSystemName(processParam.getSystemName());
        actRuDetail.setDueDate(processParam.getDueDate());
        actRuDetail.setDeleted(false);
        actRuDetail.setSub(SUB_NODE_MAP.get(actRuDetail.getProcessDefinitionId()).stream()
            .anyMatch(taskDefKey -> taskDefKey.equals(actRuDetail.getTaskDefKey())));
        OrgUnit bureau = orgUnitApi.getBureau(Y9LoginUserHolder.getTenantId(), actRuDetail.getDeptId()).getData();
        actRuDetail.setBureauId(bureau.getId());
        actRuDetail.setBureauName(bureau.getName());
        actRuDetail.setSignStatus(null == actRuDetail.getSignStatus() ? ActRuDetailSignStatusEnum.NONE.getValue()
            : actRuDetail.getSignStatus());
        actRuDetailRepository.save(actRuDetail);
        return true;
    }

    @Override
    @Transactional
    public Y9Result<Object> claim(String taskId, String assignee) {
        List<ActRuDetail> ardList = actRuDetailRepository.findByTaskId(taskId);
        ardList.forEach(ard -> {
            ard.setLastTime(new Date());
            ard.setStatus(ard.getAssignee().equals(assignee) ? ActRuDetailStatusEnum.TODO.getValue()
                : ActRuDetailStatusEnum.DOING.getValue());
            ard.setSignStatus(ard.getAssignee().equals(assignee) ? ActRuDetailSignStatusEnum.DONE.getValue()
                : ActRuDetailSignStatusEnum.NONE.getValue());
            ard.setAssigneeName(
                orgUnitApi.getOrgUnit(Y9LoginUserHolder.getTenantId(), ard.getAssignee()).getData().getName());
        });
        return Y9Result.success();
    }

    @Override
    @Transactional
    public Y9Result<Object> unClaim(String taskId, String assignee) {
        List<ActRuDetail> ardList = actRuDetailRepository.findByTaskId(taskId);
        StringBuffer names = new StringBuffer();
        ardList.forEach(ard -> {
            if (StringUtils.isBlank(names)) {
                names.append(ard.getAssigneeName());
            } else {
                names.append("、").append(ard.getAssigneeName());
            }
        });
        ardList.forEach(ard -> {
            ard.setStatus(ActRuDetailStatusEnum.TODO.getValue());
            ard.setSignStatus(ActRuDetailSignStatusEnum.TODO.getValue());
            ard.setLastTime(new Date());
            ard.setAssigneeName(names.toString());
            actRuDetailRepository.save(ard);
        });
        return Y9Result.success();
    }

    @Override
    @Transactional
    public Y9Result<Object> refuseClaim(String taskId, String assignee) {
        List<ActRuDetail> ardList = actRuDetailRepository.findByTaskId(taskId);
        StringBuffer names = new StringBuffer();
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
            ard.setStatus(ard.getAssignee().equals(assignee) ? ActRuDetailStatusEnum.DOING.getValue()
                : ActRuDetailStatusEnum.TODO.getValue());
            ard.setSignStatus(ard.getAssignee().equals(assignee) ? ActRuDetailSignStatusEnum.REFUSE.getValue()
                : ActRuDetailSignStatusEnum.TODO.getValue());
            ard.setLastTime(new Date());
            ard.setAssigneeName(ard.getAssignee().equals(assignee) ? ard.getAssigneeName() : names.toString());
            actRuDetailRepository.save(ard);
        });
        return Y9Result.success();
    }

    @Override
    @Transactional
    public boolean createTodo(ActRuDetail actRuDetail) {
        initSubNodeMap(actRuDetail.getProcessDefinitionId());
        String processSerialNumber = actRuDetail.getProcessSerialNumber();
        String assignee = actRuDetail.getAssignee();
        ActRuDetail oldActRuDetail = actRuDetailRepository.findByTaskIdAndAssignee(processSerialNumber, assignee);
        ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
        if (null != oldActRuDetail) {
            oldActRuDetail.setLastTime(actRuDetail.getLastTime());
            oldActRuDetail.setStatus(actRuDetail.getStatus());
            if (StringUtils.isNotBlank(actRuDetail.getTaskId())) {
                oldActRuDetail.setTaskId(actRuDetail.getTaskId());
            }
            oldActRuDetail.setProcessInstanceId(actRuDetail.getProcessInstanceId());
            oldActRuDetail.setExecutionId(actRuDetail.getExecutionId());
            oldActRuDetail.setStarted(actRuDetail.isStarted());
            oldActRuDetail.setDueDate(processParam.getDueDate());
            oldActRuDetail.setProcessDefinitionId(actRuDetail.getProcessDefinitionId());
            oldActRuDetail.setAssigneeName(actRuDetail.getAssigneeName());
            oldActRuDetail.setTaskDefKey(actRuDetail.getTaskDefKey());
            oldActRuDetail.setTaskDefName(actRuDetail.getTaskDefName());
            oldActRuDetail.setSignStatus(null == actRuDetail.getSignStatus() ? ActRuDetailSignStatusEnum.NONE.getValue()
                : actRuDetail.getSignStatus());
            oldActRuDetail.setSub(SUB_NODE_MAP.get(actRuDetail.getProcessDefinitionId()).stream()
                .anyMatch(taskDefKey -> taskDefKey.equals(actRuDetail.getTaskDefKey())));
            actRuDetailRepository.save(oldActRuDetail);
            return true;
        }

        actRuDetail.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        OrgUnit dept = orgUnitApi.getOrgUnit(Y9LoginUserHolder.getTenantId(), actRuDetail.getDeptId()).getData();
        actRuDetail.setDeptName(dept.getName());
        actRuDetail.setSystemName(processParam.getSystemName());
        actRuDetail.setDueDate(processParam.getDueDate());
        actRuDetail.setDeleted(false);
        actRuDetail.setSub(SUB_NODE_MAP.get(actRuDetail.getProcessDefinitionId()).stream()
            .anyMatch(taskDefKey -> taskDefKey.equals(actRuDetail.getTaskDefKey())));
        OrgUnit bureau = orgUnitApi.getBureau(Y9LoginUserHolder.getTenantId(), actRuDetail.getDeptId()).getData();
        actRuDetail.setBureauId(bureau.getId());
        actRuDetail.setBureauName(bureau.getName());
        actRuDetail.setSignStatus(null == actRuDetail.getSignStatus() ? ActRuDetailSignStatusEnum.NONE.getValue()
            : actRuDetail.getSignStatus());
        actRuDetailRepository.save(actRuDetail);
        return true;
    }

    @Override
    @Transactional
    public Y9Result<Object> todo2doing(String taskId, String assignee) {
        ActRuDetail todo = actRuDetailRepository.findByTaskIdAndAssignee(taskId, assignee);
        List<ActRuDetail> doingList = actRuDetailRepository.findByProcessSerialNumberAndAssigneeAndStatus(
            todo.getProcessSerialNumber(), assignee, ActRuDetailStatusEnum.DOING.getValue());
        if (doingList.isEmpty()) {
            todo.setStatus(ActRuDetailStatusEnum.DOING.getValue());
            todo.setLastTime(new Date());
            actRuDetailRepository.save(todo);
            return Y9Result.success();
        }
        ActRuDetail doing = doingList.get(0);
        doing.setLastTime(new Date());
        doing.setTaskId(todo.getTaskId());
        doing.setTaskDefKey(todo.getTaskDefKey());
        doing.setTaskDefName(todo.getTaskDefName());
        doing.setExecutionId(todo.getExecutionId());
        doing.setSub(SUB_NODE_MAP.get(todo.getProcessDefinitionId()).stream()
            .anyMatch(taskDefKey -> taskDefKey.equals(todo.getTaskDefKey())));
        actRuDetailRepository.save(doing);
        actRuDetailRepository.delete(todo);
        return Y9Result.success();
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
        String systemName = processParam.getSystemName(), tenantId = Y9LoginUserHolder.getTenantId();
        List<HistoricTaskInstanceModel> htiList =
            historictaskApi.findTaskByProcessInstanceIdOrByEndTimeAsc(tenantId, processInstanceId, "").getData();
        ActRuDetail actRuDetail;
        String assignee, owner;
        TaskModel taskTemp;
        for (HistoricTaskInstanceModel hti : htiList) {
            actRuDetail = new ActRuDetail();
            assignee = hti.getAssignee();
            if (StringUtils.isNotBlank(assignee)) {
                /*
                 * 1owner不为空，是恢复待办且恢复的人员不是办理人员的情况，要取出owner,并保存
                 * owner的Status为1，并判断当前taskId是不是正在运行，正在运行的话assignee的Status为0否则为1(因为恢复待办的时候，没有把历史任务的结束时间设为null)
                 */
                owner = hti.getOwner();
                if (StringUtils.isNotBlank(owner)) {
                    /* 先保存owner */
                    actRuDetail.setAssignee(owner);
                    actRuDetail.setLastTime(hti.getEndTime());
                    actRuDetail.setProcessDefinitionKey(hti.getProcessDefinitionId().split(":")[0]);
                    actRuDetail.setSystemName(systemName);
                    actRuDetail.setProcessInstanceId(hti.getProcessInstanceId());
                    actRuDetail.setStatus(1);
                    actRuDetail.setTaskId(hti.getId());
                    actRuDetail.setStarted(true);
                    actRuDetail.setEnded(false);
                    this.saveOrUpdate(actRuDetail);

                    /* 再保存assignee */
                    taskTemp = taskApi.findById(tenantId, hti.getId()).getData();
                    if (null != taskTemp) {
                        actRuDetail.setStatus(0);
                        actRuDetail.setLastTime(null);
                    } else {
                        actRuDetail.setStatus(1);
                        actRuDetail.setLastTime(hti.getEndTime());
                    }
                    actRuDetail.setAssignee(assignee);
                    actRuDetail.setProcessDefinitionKey(hti.getProcessDefinitionId().split(":")[0]);
                    actRuDetail.setProcessInstanceId(hti.getProcessInstanceId());
                    actRuDetail.setTaskId(hti.getId());
                    this.saveOrUpdate(actRuDetail);
                } else {
                    /*
                     * 2assignee不为null也有可能是恢复待办的人员是当前任务的办理人，这个时候要查出当前任务是否正在运行，正在运行
                     * Status为0，lastTime为null;当前任务不存在，Status为1，，lastTime为endTime
                     */
                    taskTemp = taskApi.findById(tenantId, hti.getId()).getData();
                    if (null != taskTemp) {
                        actRuDetail.setStatus(0);
                        actRuDetail.setLastTime(null);
                    } else {
                        actRuDetail.setStatus(1);
                        actRuDetail.setLastTime(hti.getEndTime());
                    }
                    actRuDetail.setAssignee(assignee);
                    actRuDetail.setProcessDefinitionKey(hti.getProcessDefinitionId().split(":")[0]);
                    actRuDetail.setProcessInstanceId(hti.getProcessInstanceId());
                    actRuDetail.setTaskId(hti.getId());
                    this.saveOrUpdate(actRuDetail);
                }
            } else {
                /* 2办理人为空，说明是区长办件，可以从历史的参与人查找对应任务的办理人 */
                taskTemp = taskApi.findById(tenantId, hti.getId()).getData();
                if (null != taskTemp) {
                    actRuDetail.setStatus(0);
                    actRuDetail.setLastTime(null);
                } else {
                    actRuDetail.setStatus(1);
                    actRuDetail.setLastTime(hti.getEndTime());
                }
                List<IdentityLinkModel> identityLinkList = new ArrayList<>();
                try {
                    identityLinkList = identityApi.getIdentityLinksForTask(tenantId, hti.getId()).getData();
                } catch (Exception e) {
                    LOGGER.error("Get identity links for task error", e);
                }
                for (IdentityLinkModel il : identityLinkList) {
                    if (StringUtils.isNotBlank(il.getUserId()) && "assignee".equals(il.getType())) {
                        assignee = il.getUserId();
                        break;
                    }
                }
                actRuDetail.setAssignee(assignee);
                actRuDetail.setProcessDefinitionKey(hti.getProcessDefinitionId().split(":")[0]);
                actRuDetail.setProcessInstanceId(hti.getProcessInstanceId());
                actRuDetail.setTaskId(hti.getId());
                this.saveOrUpdate(actRuDetail);
            }
        }
        return true;
    }

}