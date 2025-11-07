package net.risesoft.service.chaosong.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.persistence.criteria.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.Y9FlowableHolder;
import net.risesoft.api.platform.org.CustomGroupApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.OrganizationApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.processadmin.HistoricProcessApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.consts.ItemConsts;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.entity.ChaoSong;
import net.risesoft.entity.ErrorLog;
import net.risesoft.entity.ProcessParam;
import net.risesoft.enums.ChaoSongStatusEnum;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.enums.ItemPermissionEnum;
import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ChaoSong4DataBaseModel;
import net.risesoft.model.itemadmin.ErrorLogModel;
import net.risesoft.model.itemadmin.OpenDataModel;
import net.risesoft.model.platform.org.CustomGroupMember;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.platform.org.Position;
import net.risesoft.model.processadmin.HistoricProcessInstanceModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.nosql.elastic.entity.OfficeDoneInfo;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.query.platform.CustomGroupMemberQuery;
import net.risesoft.repository.jpa.ChaoSongRepository;
import net.risesoft.service.ErrorLogService;
import net.risesoft.service.OfficeDoneInfoService;
import net.risesoft.service.OfficeFollowService;
import net.risesoft.service.chaosong.ChaoSongService;
import net.risesoft.service.core.DocumentService;
import net.risesoft.service.core.ProcessParamService;
import net.risesoft.specification.ChaoSongSpecification;
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
public class ChaoSongServiceImpl implements ChaoSongService {

    private final ChaoSongRepository chaoSongRepository;

    private final DocumentService documentService;

    private final ProcessParamService processParamService;

    private final TaskApi taskApi;

    private final HistoricProcessApi historicProcessApi;

    private final OrganizationApi organizationApi;

    private final PositionApi positionApi;

    private final OrgUnitApi orgUnitApi;

    private final OfficeDoneInfoService officeDoneInfoService;

    private final OfficeFollowService officeFollowService;

    private final ErrorLogService errorLogService;

    private final CustomGroupApi customGroupApi;

    @Override
    @Transactional
    public void changeChaoSongState(String id, String type) {
        String opinionState = "";
        if (ItemBoxTypeEnum.ADD.getValue().equals(type)) {
            opinionState = "1";
        }
        ChaoSong chaoSong = chaoSongRepository.findById(id).orElse(null);
        assert chaoSong != null;
        chaoSong.setOpinionState(opinionState);
        chaoSongRepository.save(chaoSong);
    }

    @Override
    @Transactional
    public void changeStatus(String id) {
        ChaoSong chaoSong = chaoSongRepository.findById(id).orElse(null);
        if (chaoSong != null) {
            chaoSong.setStatus(ChaoSongStatusEnum.READ);
            chaoSong.setReadTime(Y9DateTimeUtils.formatCurrentDateTime());
            chaoSongRepository.save(chaoSong);
        }
    }

    @Override
    @Transactional
    public void changeStatus(String[] ids) {
        for (String id : ids) {
            ChaoSong chaoSong = chaoSongRepository.findById(id).orElse(null);
            if (chaoSong != null) {
                chaoSong.setStatus(ChaoSongStatusEnum.READ);
                chaoSong.setReadTime(Y9DateTimeUtils.formatCurrentDateTime());
                chaoSongRepository.save(chaoSong);
            }
        }
    }

    @Override
    public int countAllByUserId(String userId) {
        return chaoSongRepository.countByUserId(userId);
    }

    @Override
    public int countByProcessInstanceId(String userId, String processInstanceId) {
        return chaoSongRepository.countBySenderIdIsNotAndProcessInstanceId(userId, processInstanceId);
    }

    @Override
    public int countByUserIdAndProcessInstanceId(String userId, String processInstanceId) {
        return chaoSongRepository.countBySenderIdAndProcessInstanceId(userId, processInstanceId);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        chaoSongRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByIds(String[] ids) {
        for (String id : ids) {
            chaoSongRepository.deleteById(id);
        }
    }

    @Override
    @Transactional
    public boolean deleteByProcessInstanceId(String processInstanceId) {
        chaoSongRepository.deleteByProcessInstanceId(processInstanceId);
        return true;
    }

    @Override
    public OpenDataModel detail(String processInstanceId, Integer status, boolean mobile) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        // 获取任务信息
        TaskInfo taskInfo = getTaskInfo(tenantId, processInstanceId);
        // 获取流程定义信息
        ProcessDefinitionInfo processDefInfo = getProcessDefinitionInfo(tenantId, taskInfo.getProcessInstanceId());
        // 获取任务定义key
        String taskDefinitionKey = getTaskDefinitionKey(tenantId, taskInfo.getTaskId());
        // 构建模型数据
        return buildOpenDataModel(tenantId, mobile, taskInfo, processDefInfo, taskDefinitionKey);
    }

    private TaskInfo getTaskInfo(String tenantId, String processInstanceId) {
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setItembox(ItemBoxTypeEnum.DOING.getValue());
        taskInfo.setTaskId("");

        List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
        if (taskList.isEmpty()) {
            taskInfo.setItembox(ItemBoxTypeEnum.DONE.getValue());
        }

        if (ItemBoxTypeEnum.DOING.getValue().equals(taskInfo.getItembox())) {
            taskInfo.setTaskId(taskList.get(0).getId());
            TaskModel task = taskApi.findById(tenantId, taskInfo.getTaskId()).getData();
            taskInfo.setProcessInstanceId(task.getProcessInstanceId());
        } else {
            taskInfo.setProcessInstanceId(processInstanceId);
        }

        return taskInfo;
    }

    private ProcessDefinitionInfo getProcessDefinitionInfo(String tenantId, String processInstanceId) {
        ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
        HistoricProcessInstanceModel hpi = historicProcessApi.getById(tenantId, processInstanceId).getData();

        ProcessDefinitionInfo processDefInfo = new ProcessDefinitionInfo();

        if (hpi == null) {
            OfficeDoneInfo officeDoneInfo = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
            if (officeDoneInfo == null) {
                String year = processParam.getCreateTime().substring(0, 4);
                hpi = historicProcessApi.getByIdAndYear(tenantId, processInstanceId, year).getData();
                processDefInfo.setProcessDefinitionId(hpi.getProcessDefinitionId());
                processDefInfo
                    .setProcessDefinitionKey(processDefInfo.getProcessDefinitionId().split(SysVariables.COLON)[0]);
            } else {
                processDefInfo.setProcessDefinitionId(officeDoneInfo.getProcessDefinitionId());
                processDefInfo.setProcessDefinitionKey(officeDoneInfo.getProcessDefinitionKey());
            }
        } else {
            processDefInfo.setProcessDefinitionId(hpi.getProcessDefinitionId());
            processDefInfo
                .setProcessDefinitionKey(processDefInfo.getProcessDefinitionId().split(SysVariables.COLON)[0]);
        }

        processDefInfo.setStartor(processParam.getStartor());
        processDefInfo.setProcessSerialNumber(processParam.getProcessSerialNumber());

        return processDefInfo;
    }

    private String getTaskDefinitionKey(String tenantId, String taskId) {
        if (StringUtils.isNotEmpty(taskId)) {
            if (taskId.contains(SysVariables.COMMA)) {
                taskId = taskId.split(SysVariables.COMMA)[0];
            }
            TaskModel taskTemp = taskApi.findById(tenantId, taskId).getData();
            return taskTemp.getTaskDefinitionKey();
        }
        return "";
    }

    private OpenDataModel buildOpenDataModel(String tenantId, boolean mobile, TaskInfo taskInfo,
        ProcessDefinitionInfo processDefInfo, String taskDefinitionKey) {
        OpenDataModel model = new OpenDataModel();
        ProcessParam processParam = processParamService.findByProcessInstanceId(taskInfo.getProcessInstanceId());
        OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, Y9FlowableHolder.getOrgUnitId()).getData();
        model.setTitle(processParam.getTitle());
        model.setStartor(processDefInfo.getStartor());
        model.setItembox(taskInfo.getItembox());
        model.setCurrentUser(orgUnit.getName());
        model.setProcessDefinitionKey(processDefInfo.getProcessDefinitionKey());
        model.setProcessSerialNumber(processDefInfo.getProcessSerialNumber());
        model.setProcessDefinitionId(processDefInfo.getProcessDefinitionId());
        model.setProcessInstanceId(taskInfo.getProcessInstanceId());
        model.setTaskDefKey(taskDefinitionKey);
        model.setTaskId(taskInfo.getTaskId());
        model.setActivitiUser("");
        model.setItemId(processParam.getItemId());
        model = documentService.genDocumentModel(processParam.getItemId(), processDefInfo.getProcessDefinitionKey(),
            processDefInfo.getProcessDefinitionId(), taskDefinitionKey, mobile, model);
        // 设置菜单信息
        String menuName = "打印,抄送,关注,返回";
        String menuKey = "17,18,follow,03";
        model.setMenuName(menuName);
        model.setMenuKey(menuKey);
        return model;
    }

    @Override
    public ChaoSong getById(String id) {
        return chaoSongRepository.findById(id).orElse(null);
    }

    @Override
    public int getDone4OpinionCountByUserId(String userId) {
        return chaoSongRepository.countByUserIdAndOpinionState(userId, "1");
    }

    @Override
    public int getDoneCountByUserId(String userId) {
        return chaoSongRepository.countByUserIdAndStatus(userId, 1);
    }

    @Override
    public int getTodoCountByUserId(String userId) {
        return chaoSongRepository.countByUserIdAndStatus(userId, 2);
    }

    @Override
    public Y9Page<ChaoSong4DataBaseModel> pageByProcessInstanceIdAndUserName(String processInstanceId, String userName,
        int rows, int page) {
        if (page < 1) {
            page = 1;
        }
        Sort sort = Sort.by(Sort.Direction.DESC, ItemConsts.CREATETIME_KEY);
        PageRequest pageable = PageRequest.of(page - 1, rows, sort);
        String senderId = Y9FlowableHolder.getOrgUnitId();
        ChaoSongSpecification<ChaoSong> specification =
            new ChaoSongSpecification<>(processInstanceId, null, null, userName, null, null, null, null, null, null);
        // 手动添加 senderId 不等于当前用户的条件
        Page<ChaoSong> pageList = chaoSongRepository.findAll((Specification<ChaoSong>)(root, query, builder) -> {
            Predicate basePredicate = specification.toPredicate(root, query, builder);
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(basePredicate);
            predicates.add(builder.notEqual(root.get(ItemConsts.SENDERID_KEY), senderId));
            return builder.and(predicates.toArray(new Predicate[0]));
        }, pageable);
        // 复用数据转换逻辑
        List<ChaoSong4DataBaseModel> list =
            buildChaoSongModelList(pageList.getContent(), processInstanceId, page, rows);
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), list);
    }

    private List<ChaoSong4DataBaseModel> buildChaoSongModelList(List<ChaoSong> csList, String processInstanceId,
        int page, int rows) {
        AtomicInteger startRow = new AtomicInteger((page - 1) * rows);
        return csList.stream().map(info -> {
            ChaoSong4DataBaseModel model = new ChaoSong4DataBaseModel();
            Y9BeanUtil.copyProperties(info, model);
            model.setId(info.getId());
            model.setProcessInstanceId(processInstanceId);
            model.setSenderName(info.getSenderName());
            model.setSendDeptName(info.getSendDeptName());
            model.setUserName(info.getUserName());
            model.setUserDeptName(info.getUserDeptName());
            model.setTitle(info.getTitle());
            model.setSerialNumber(startRow.incrementAndGet());
            try {
                if (StringUtils.isBlank(info.getReadTime())) {
                    model.setReadTime("");
                } else {
                    model.setReadTime(
                        Y9DateTimeUtils.formatDateTimeMinute(Y9DateTimeUtils.parseDateTime(info.getReadTime())));
                }
                model.setCreateTime(
                    Y9DateTimeUtils.formatDateTimeMinute(Y9DateTimeUtils.parseDateTime(info.getCreateTime())));
            } catch (Exception e) {
                LOGGER.error("根据ProcessInstanceId和UserName获取抄送信息数据失败", e);
            }
            return model;
        }).collect(Collectors.toList());
    }

    @Override
    public Y9Page<ChaoSong4DataBaseModel> pageBySenderIdAndProcessInstanceId(String senderId, String processInstanceId,
        String userName, int rows, int page) {
        if (page < 1) {
            page = 1;
        }
        Sort sort = Sort.by(Sort.Direction.DESC, ItemConsts.CREATETIME_KEY);
        PageRequest pageable = PageRequest.of(page - 1, rows, sort);
        ChaoSongSpecification<ChaoSong> specification = new ChaoSongSpecification<>(processInstanceId, null, null,
            userName, senderId, null, null, null, null, null);
        Page<ChaoSong> pageList = chaoSongRepository.findAll(specification, pageable);
        List<ChaoSong4DataBaseModel> list =
            buildChaoSongModelList(pageList.getContent(), processInstanceId, page, rows);
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), list);
    }

    @Override
    public Y9Page<ChaoSong4DataBaseModel> pageDoneList(String orgUnitId, String documentTitle, int rows, int page) {
        if (page < 1) {
            page = 1;
        }
        Sort sort = Sort.by(Sort.Direction.DESC, ItemConsts.CREATETIME_KEY);
        PageRequest pageable = PageRequest.of(page - 1, rows, sort);
        ChaoSongSpecification<ChaoSong> chaoSongSpecification = new ChaoSongSpecification<>(null, null, orgUnitId, null,
            null, null, documentTitle, ChaoSongStatusEnum.READ.getValue(), null, null);
        Page<ChaoSong> pageList = chaoSongRepository.findAll(chaoSongSpecification, pageable);
        List<ChaoSong4DataBaseModel> list =
            buildDoneChaoSongModelList(pageList.getContent(), Y9LoginUserHolder.getTenantId(), page, rows);
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), list);
    }

    private List<ChaoSong4DataBaseModel> buildDoneChaoSongModelList(List<ChaoSong> csList, String tenantId, int page,
        int rows) {
        AtomicInteger num = new AtomicInteger((page - 1) * rows);
        return csList.stream().map(cs -> {
            ChaoSong4DataBaseModel model = new ChaoSong4DataBaseModel();
            Y9BeanUtil.copyProperties(cs, model);
            String processInstanceId = cs.getProcessInstanceId();
            int serialNumber = num.incrementAndGet();
            model.setSerialNumber(serialNumber);
            model.setId(cs.getId());
            model.setProcessInstanceId(processInstanceId);
            model.setSenderName(cs.getSenderName());
            model.setSendDeptName(cs.getSendDeptName());
            model.setTitle(cs.getTitle());
            model.setStatus(cs.getStatus());
            model.setItemId(cs.getItemId());
            model.setItemName(cs.getItemName());
            model.setBanjie(false);
            try {
                model
                    .setReadTime(Y9DateTimeUtils.formatDateTimeMinute(Y9DateTimeUtils.parseDateTime(cs.getReadTime())));
                model.setCreateTime(Y9DateTimeUtils.formatDateTime(Y9DateTimeUtils.parseDateTime(cs.getCreateTime())));
                HistoricProcessInstanceModel hpi = historicProcessApi.getById(tenantId, processInstanceId).getData();
                model.setBanjie(hpi == null || hpi.getEndTime() != null);
                OfficeDoneInfo officeDoneInfo = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
                model.setProcessDefinitionId(officeDoneInfo != null ? officeDoneInfo.getProcessDefinitionId() : "");
                model.setProcessSerialNumber(officeDoneInfo.getProcessSerialNumber());
                int countFollow = officeFollowService.countByProcessInstanceId(processInstanceId);
                model.setFollow(countFollow > 0);
                ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
                model.setNumber(processParam.getCustomNumber());
                model.setLevel(processParam.getCustomLevel());
            } catch (Exception e) {
                LOGGER.error("获取办结抄送信息数据失败", e);
            }
            return model;
        }).collect(Collectors.toList());
    }

    @Override
    public Y9Page<ChaoSong4DataBaseModel> pageMyChaoSongList(String searchName, String itemId, String userName,
        Integer state, String year, int rows, int page) {
        String userId = Y9FlowableHolder.getOrgUnitId();
        if (page < 1) {
            page = 1;
        }
        Sort sort = Sort.by(Sort.Direction.DESC, ItemConsts.CREATETIME_KEY);
        PageRequest pageable = PageRequest.of(page - 1, rows, sort);
        ChaoSongSpecification<ChaoSong> chaoSongSpecification =
            new ChaoSongSpecification<>(null, itemId, null, userName, userId, null, searchName, state, year, null);
        Page<ChaoSong> pageList = chaoSongRepository.findAll(chaoSongSpecification, pageable);
        List<ChaoSong4DataBaseModel> list = buildMyChaoSongModelList(pageList.getContent(), page, rows);
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), list);
    }

    private List<ChaoSong4DataBaseModel> buildMyChaoSongModelList(List<ChaoSong> csList, int page, int rows) {
        AtomicInteger num = new AtomicInteger((page - 1) * rows);
        return csList.stream().map(cs -> {
            ChaoSong4DataBaseModel model = new ChaoSong4DataBaseModel();
            Y9BeanUtil.copyProperties(cs, model);
            String processInstanceId = cs.getProcessInstanceId();
            model.setSerialNumber(num.incrementAndGet());
            model.setId(cs.getId());
            model.setProcessInstanceId(processInstanceId);
            model.setSenderName(cs.getSenderName());
            model.setSendDeptName(cs.getSendDeptName());
            model.setStatus(cs.getStatus());
            model.setItemId(cs.getItemId());
            model.setItemName(cs.getItemName());
            model.setBanjie(false);
            try {
                OfficeDoneInfo hpi = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
                if (hpi != null) {
                    model.setProcessSerialNumber(hpi.getProcessSerialNumber());
                    model.setTitle(hpi.getTitle());
                    model.setUserId(cs.getUserId());
                    model.setUserName(cs.getUserName());
                    model.setUserDeptName(cs.getUserDeptName());
                    model.setSystemName(hpi.getSystemName());
                    model.setProcessDefinitionId(hpi.getProcessDefinitionId());
                    model.setBanjie(hpi.getEndTime() != null);
                    model.setNumber(hpi.getDocNumber());
                    model.setLevel(hpi.getUrgency());
                }
                model.setCreateTime(
                    Y9DateTimeUtils.formatDateTimeMinute(Y9DateTimeUtils.parseDateTime(cs.getCreateTime())));
                model.setReadTime(StringUtils.isNotBlank(cs.getReadTime())
                    ? Y9DateTimeUtils.formatDateTimeMinute(Y9DateTimeUtils.parseDateTime(cs.getReadTime())) : "--");
            } catch (Exception e) {
                LOGGER.error("获取列表失败", e);
            }
            return model;
        }).collect(Collectors.toList());
    }

    @Override
    public Y9Page<ChaoSong4DataBaseModel> pageOpinionChaosongByUserId(String userId, String documentTitle, int rows,
        int page) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (page < 1) {
            page = 1;
        }
        Sort sort = Sort.by(Sort.Direction.DESC, ItemConsts.CREATETIME_KEY);
        PageRequest pageable = PageRequest.of(page - 1, rows, sort);
        ChaoSongSpecification<ChaoSong> chaoSongSpecification =
            new ChaoSongSpecification<>(null, null, userId, null, null, documentTitle, null, null, null, "1");
        Page<ChaoSong> pageList = chaoSongRepository.findAll(chaoSongSpecification, pageable);
        List<ChaoSong4DataBaseModel> list = buildOpinionChaoSongModelList(pageList.getContent(), tenantId, page, rows);
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), list);
    }

    private List<ChaoSong4DataBaseModel> buildOpinionChaoSongModelList(List<ChaoSong> csList, String tenantId, int page,
        int rows) {
        AtomicInteger num = new AtomicInteger((page - 1) * rows);
        return csList.stream().map(cs -> {
            ChaoSong4DataBaseModel model = new ChaoSong4DataBaseModel();
            Y9BeanUtil.copyProperties(cs, model);
            String processInstanceId = cs.getProcessInstanceId();
            model.setSerialNumber(num.incrementAndGet());
            model.setId(cs.getId());
            model.setProcessInstanceId(processInstanceId);
            model.setSenderName(cs.getSenderName());
            model.setSendDeptName(cs.getSendDeptName());
            model.setTitle(cs.getTitle());
            model.setStatus(cs.getStatus());
            model.setItemId(cs.getItemId());
            model.setItemName(cs.getItemName());
            model.setBanjie(false);
            try {
                model.setCreateTime(
                    Y9DateTimeUtils.formatDateTimeMinute(Y9DateTimeUtils.parseDateTime(cs.getCreateTime())));
                model.setReadTime(StringUtils.isNotBlank(cs.getReadTime())
                    ? Y9DateTimeUtils.formatDateTimeMinute(Y9DateTimeUtils.parseDateTime(cs.getReadTime())) : "--");
                HistoricProcessInstanceModel hpi = historicProcessApi.getById(tenantId, processInstanceId).getData();
                model.setBanjie(hpi == null || hpi.getEndTime() != null);
                OfficeDoneInfo officeDoneInfo = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
                model.setProcessDefinitionId(officeDoneInfo != null ? officeDoneInfo.getProcessDefinitionId() : "");
                model.setProcessSerialNumber(officeDoneInfo.getProcessSerialNumber());
                int countFollow = officeFollowService.countByProcessInstanceId(processInstanceId);
                model.setFollow(countFollow > 0);
                ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
                model.setNumber(processParam.getCustomNumber());
                model.setLevel(processParam.getCustomLevel());
            } catch (Exception e) {
                LOGGER.error("获取当前人抄送信息数据失败", e);
            }
            return model;
        }).collect(Collectors.toList());
    }

    @Override
    public Y9Page<ChaoSong4DataBaseModel> pageTodoList(String orgUnitId, String documentTitle, int rows, int page) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (page < 1) {
            page = 1;
        }
        Sort sort = Sort.by(Sort.Direction.DESC, ItemConsts.CREATETIME_KEY);
        PageRequest pageable = PageRequest.of(page - 1, rows, sort);
        ChaoSongSpecification<ChaoSong> chaoSongSpecification = new ChaoSongSpecification<>(null, null, orgUnitId, null,
            null, null, documentTitle, ChaoSongStatusEnum.UNREAD.getValue(), null, null);
        Page<ChaoSong> pageList = chaoSongRepository.findAll(chaoSongSpecification, pageable);
        List<ChaoSong4DataBaseModel> list = buildTodoChaoSongModelList(pageList.getContent(), tenantId, page, rows);
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), list);
    }

    private List<ChaoSong4DataBaseModel> buildTodoChaoSongModelList(List<ChaoSong> csList, String tenantId, int page,
        int rows) {
        AtomicInteger num = new AtomicInteger((page - 1) * rows);
        return csList.stream().map(cs -> {
            ChaoSong4DataBaseModel model = new ChaoSong4DataBaseModel();
            Y9BeanUtil.copyProperties(cs, model);
            String processInstanceId = cs.getProcessInstanceId();
            model.setSerialNumber(num.incrementAndGet());
            model.setId(cs.getId());
            model.setProcessInstanceId(processInstanceId);
            model.setSenderName(cs.getSenderName());
            model.setSendDeptName(cs.getSendDeptName());
            model.setTitle(cs.getTitle());
            model.setStatus(cs.getStatus());
            model.setItemId(cs.getItemId());
            model.setItemName(cs.getItemName());
            model.setBanjie(false);
            try {
                HistoricProcessInstanceModel hpi = historicProcessApi.getById(tenantId, processInstanceId).getData();
                model.setBanjie(hpi == null || hpi.getEndTime() != null);
                OfficeDoneInfo officeDoneInfo = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
                model.setProcessDefinitionId(officeDoneInfo != null ? officeDoneInfo.getProcessDefinitionId() : "");
                model.setProcessSerialNumber(officeDoneInfo.getProcessSerialNumber());
                int countFollow = officeFollowService.countByProcessInstanceId(processInstanceId);
                model.setFollow(countFollow > 0);
                ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
                model.setNumber(processParam.getCustomNumber());
                model.setLevel(processParam.getCustomLevel());
                model.setCreateTime(
                    Y9DateTimeUtils.formatDateTimeMinute(Y9DateTimeUtils.parseDateTime(cs.getCreateTime())));
            } catch (Exception e) {
                LOGGER.error("获取待办抄送信息数据失败", e);
            }
            return model;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ChaoSong save(ChaoSong chaoSong) {
        return chaoSongRepository.save(chaoSong);
    }

    @Override
    @Transactional
    public void save(List<ChaoSong> chaoSongList) {
        chaoSongRepository.saveAll(chaoSongList);
    }

    @Override
    @Transactional
    public Y9Result<Object> save(String processInstanceId, String users, String isSendSms, String isShuMing,
        String smsContent, String smsPersonId) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String currentUserId = Y9FlowableHolder.getOrgUnitId();
            OrgUnit currentOrgUnit = Y9FlowableHolder.getOrgUnit();
            ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
            String title = processParam.getTitle();
            String itemId = processParam.getItemId();
            String itemName = processParam.getItemName();
            List<String> userIdList = parseUserList(tenantId, users);
            List<ChaoSong> chaoSongList = buildChaoSongList(tenantId, currentUserId, currentOrgUnit, processInstanceId,
                title, itemId, itemName, userIdList);
            // 保存抄送信息
            chaoSongRepository.saveAll(chaoSongList);
            return Y9Result.successMsg("抄送成功");
        } catch (Exception e) {
            handleError(processInstanceId, e);
            return Y9Result.failure("抄送失败");
        }
    }

    /**
     * 解析用户列表
     */
    private List<String> parseUserList(String tenantId, String users) {
        String[] orgUnitList = users.split(";");
        List<String> userIdListAdd = new ArrayList<>();
        for (String orgUnitStr : orgUnitList) {
            String[] orgUnitArr = orgUnitStr.split(":");
            Integer type = Integer.valueOf(orgUnitArr[0]);
            String orgUnitId = orgUnitArr[1];

            switch (ItemPermissionEnum.valueOf(type)) {
                case DEPARTMENT:
                    List<Position> positions = positionApi.listByParentId(tenantId, orgUnitId).getData();
                    positions.forEach(position -> userIdListAdd.add(position.getId()));
                    break;
                case POSITION:
                    userIdListAdd.add(orgUnitId);
                    break;
                case GROUP_CUSTOM:
                    List<CustomGroupMember> members = customGroupApi
                        .listCustomGroupMember(tenantId, new CustomGroupMemberQuery(orgUnitId, OrgTypeEnum.POSITION))
                        .getData();
                    for (CustomGroupMember member : members) {
                        OrgUnit user = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, member.getMemberId()).getData();
                        if (user != null && StringUtils.isNotBlank(user.getId())) {
                            userIdListAdd.add(user.getId());
                        }
                    }
                    break;
                default:
                    LOGGER.warn("不支持的权限类型：{}", type);
            }
        }
        return userIdListAdd;
    }

    /**
     * 构建抄送信息列表
     */
    private List<ChaoSong> buildChaoSongList(String tenantId, String currentUserId, OrgUnit currentOrgUnit,
        String processInstanceId, String title, String itemId, String itemName, List<String> userIdList) {
        List<ChaoSong> chaoSongList = new ArrayList<>();
        OrgUnit dept = getDepartment(tenantId, currentOrgUnit);
        for (String userId : userIdList) {
            OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, userId).getData();
            if (orgUnit != null) {
                ChaoSong chaoSong = createChaoSong(tenantId, currentUserId, currentOrgUnit, dept, processInstanceId,
                    title, itemId, itemName, orgUnit);
                chaoSongList.add(chaoSong);
            }
        }
        return chaoSongList;
    }

    /**
     * 获取部门信息
     */
    private OrgUnit getDepartment(String tenantId, OrgUnit currentOrgUnit) {
        OrgUnit dept = orgUnitApi.getOrgUnit(tenantId, currentOrgUnit.getParentId()).getData();
        if (null == dept || null == dept.getId()) {
            dept = organizationApi.get(tenantId, currentOrgUnit.getParentId()).getData();
        }
        return dept;
    }

    /**
     * 创建单个抄送信息
     */
    private ChaoSong createChaoSong(String tenantId, String currentUserId, OrgUnit currentOrgUnit, OrgUnit dept,
        String processInstanceId, String title, String itemId, String itemName, OrgUnit orgUnit) {
        ChaoSong chaoSong = new ChaoSong();
        chaoSong.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        chaoSong.setCreateTime(Y9DateTimeUtils.formatCurrentDateTime());
        chaoSong.setProcessInstanceId(processInstanceId);
        chaoSong.setSenderId(currentUserId);
        chaoSong.setSenderName(currentOrgUnit.getName());
        chaoSong.setSendDeptId(dept.getId());
        chaoSong.setSendDeptName(dept.getName());
        chaoSong.setStatus(ChaoSongStatusEnum.UNREAD);
        chaoSong.setTenantId(tenantId);
        chaoSong.setTitle(title);
        chaoSong.setUserId(orgUnit.getId());
        chaoSong.setUserName(orgUnit.getName());
        OrgUnit department = orgUnitApi.getOrgUnit(tenantId, orgUnit.getParentId()).getData();
        if (department != null) {
            chaoSong.setUserDeptId(department.getId());
            chaoSong.setUserDeptName(department.getName());
        }
        chaoSong.setItemId(itemId);
        chaoSong.setItemName(itemName);
        return chaoSong;
    }

    /**
     * 处理异常信息
     */
    private void handleError(String processInstanceId, Exception e) {
        try {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            String errorMsg = stringWriter.toString();
            ErrorLog errorLog = new ErrorLog();
            errorLog.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            errorLog.setErrorFlag(ErrorLogModel.ERROR_FLAG_SAVE_CHAOSONG);
            errorLog.setErrorType(ErrorLogModel.ERROR_PROCESS_INSTANCE);
            errorLog.setExtendField("抄送保存失败");
            errorLog.setProcessInstanceId(processInstanceId);
            errorLog.setTaskId("");
            errorLog.setText(errorMsg);
            errorLogService.saveErrorLog(errorLog);
        } catch (Exception ex) {
            LOGGER.error("保存抄送失败信息异常", ex);
        }
    }

    @Override
    public Y9Page<ChaoSong4DataBaseModel> searchAllByUserId(String searchName, String itemId, String userName,
        Integer state, String year, Integer page, Integer rows) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9FlowableHolder.getOrgUnitId();
        if (page == null || page < 1) {
            page = 1;
        }
        if (rows == null) {
            rows = 10;
        }
        Sort sort = Sort.by(Sort.Direction.DESC, ItemConsts.CREATETIME_KEY);
        PageRequest pageable = PageRequest.of(page - 1, rows, sort);
        ChaoSongSpecification<ChaoSong> chaoSongSpecification =
            new ChaoSongSpecification<>(null, itemId, userId, userName, null, null, searchName, state, year, null);
        Page<ChaoSong> pageList = chaoSongRepository.findAll(chaoSongSpecification, pageable);
        List<ChaoSong4DataBaseModel> list = buildSearchChaoSongModelList(pageList.getContent(), tenantId, page, rows);
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), list);
    }

    private List<ChaoSong4DataBaseModel> buildSearchChaoSongModelList(List<ChaoSong> csList, String tenantId, int page,
        int rows) {
        AtomicInteger num = new AtomicInteger((page - 1) * rows);
        return csList.stream().map(cs -> {
            ChaoSong4DataBaseModel model = new ChaoSong4DataBaseModel();
            Y9BeanUtil.copyProperties(cs, model);
            String processInstanceId = cs.getProcessInstanceId();
            model.setSerialNumber(num.incrementAndGet());
            model.setId(cs.getId());
            model.setProcessInstanceId(processInstanceId);
            model.setSenderName(cs.getSenderName());
            model.setSendDeptName(cs.getSendDeptName());
            model.setTitle(cs.getTitle());
            model.setStatus(cs.getStatus());
            model.setItemId(cs.getItemId());
            model.setItemName(cs.getItemName());
            model.setBanjie(false);
            try {
                model.setCreateTime(
                    Y9DateTimeUtils.formatDateTimeMinute(Y9DateTimeUtils.parseDateTime(cs.getCreateTime())));
                model.setReadTime(StringUtils.isNotBlank(cs.getReadTime())
                    ? Y9DateTimeUtils.formatDateTimeMinute(Y9DateTimeUtils.parseDateTime(cs.getReadTime())) : "--");
                ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
                model.setNumber(processParam.getCustomNumber());
                model.setLevel(processParam.getCustomLevel());
                model.setProcessSerialNumber(processParam.getProcessSerialNumber());
                HistoricProcessInstanceModel hpi = historicProcessApi.getById(tenantId, processInstanceId).getData();
                model.setBanjie(hpi == null || hpi.getEndTime() != null);
                OfficeDoneInfo officeDoneInfo = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
                model.setProcessDefinitionId(officeDoneInfo != null ? officeDoneInfo.getProcessDefinitionId() : "");
                int countFollow = officeFollowService.countByProcessInstanceId(processInstanceId);
                model.setFollow(countFollow > 0);
            } catch (Exception e) {
                LOGGER.error("获取抄送列表失败", e);
            }
            return model;
        }).collect(Collectors.toList());
    }

    @Override
    public Y9Page<ChaoSong4DataBaseModel> searchAllList(String searchName, String itemId, String senderName,
        String userName, Integer state, String year, Integer page, Integer rows) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (page == null || page < 1) {
            page = 1;
        }
        if (rows == null) {
            rows = 10;
        }
        Sort sort = Sort.by(Sort.Direction.DESC, ItemConsts.CREATETIME_KEY);
        PageRequest pageable = PageRequest.of(page - 1, rows, sort);
        ChaoSongSpecification<ChaoSong> chaoSongSpecification =
            new ChaoSongSpecification<>(null, itemId, null, userName, null, senderName, searchName, state, year, null);
        Page<ChaoSong> pageList = chaoSongRepository.findAll(chaoSongSpecification, pageable);
        List<ChaoSong4DataBaseModel> list =
            buildSearchAllChaoSongModelList(pageList.getContent(), tenantId, page, rows);
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), list);
    }

    private List<ChaoSong4DataBaseModel> buildSearchAllChaoSongModelList(List<ChaoSong> csList, String tenantId,
        int page, int rows) {
        AtomicInteger num = new AtomicInteger((page - 1) * rows);
        return csList.stream().map(cs -> {
            ChaoSong4DataBaseModel model = new ChaoSong4DataBaseModel();
            Y9BeanUtil.copyProperties(cs, model);
            String processInstanceId = cs.getProcessInstanceId();
            model.setSerialNumber(num.incrementAndGet());
            model.setId(cs.getId());
            model.setProcessInstanceId(processInstanceId);
            model.setSenderName(cs.getSenderName());
            model.setSendDeptName(cs.getSendDeptName());
            model.setTitle(cs.getTitle());
            model.setStatus(cs.getStatus());
            model.setItemId(cs.getItemId());
            model.setItemName(cs.getItemName());
            model.setBanjie(false);
            model.setUserName(cs.getUserName());
            model.setUserDeptName(cs.getUserDeptName());
            try {
                model.setCreateTime(
                    Y9DateTimeUtils.formatDateTimeMinute(Y9DateTimeUtils.parseDateTime(cs.getCreateTime())));
                model.setReadTime(StringUtils.isNotBlank(cs.getReadTime())
                    ? Y9DateTimeUtils.formatDateTimeMinute(Y9DateTimeUtils.parseDateTime(cs.getReadTime())) : "--");
                ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
                model.setNumber(processParam.getCustomNumber());
                model.setLevel(processParam.getCustomLevel());
                model.setProcessSerialNumber(processParam.getProcessSerialNumber());
                HistoricProcessInstanceModel hpi = historicProcessApi.getById(tenantId, processInstanceId).getData();
                model.setBanjie(hpi == null || hpi.getEndTime() != null);
                OfficeDoneInfo officeDoneInfo = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
                model.setProcessDefinitionId(officeDoneInfo != null ? officeDoneInfo.getProcessDefinitionId() : "");
                int countFollow = officeFollowService.countByProcessInstanceId(processInstanceId);
                model.setFollow(countFollow > 0);
            } catch (Exception e) {
                LOGGER.error("获取抄送列表失败", e);
            }
            return model;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateTitle(String processInstanceId, String documentTitle) {
        try {
            List<ChaoSong> list = chaoSongRepository.findByProcessInstanceId(processInstanceId);
            List<ChaoSong> newList = new ArrayList<>();
            for (ChaoSong info : list) {
                info.setTitle(documentTitle);
                newList.add(info);
            }
            if (!newList.isEmpty()) {
                chaoSongRepository.saveAll(newList);
            }
        } catch (Exception e) {
            LOGGER.error("更新抄送标题失败", e);
        }
    }

    // 内部类用于封装相关信息
    @Setter
    @Getter
    private static class TaskInfo {
        private String itembox;
        private String taskId;
        private String processInstanceId;
    }

    @Setter
    @Getter
    private static class ProcessDefinitionInfo {
        private String processDefinitionId;
        private String processDefinitionKey;
        private String startor;
        private String processSerialNumber;
    }
}
