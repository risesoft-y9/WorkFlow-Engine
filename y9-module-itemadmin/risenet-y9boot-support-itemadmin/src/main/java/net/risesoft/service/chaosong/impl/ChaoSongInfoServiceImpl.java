package net.risesoft.service.chaosong.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

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
import net.risesoft.entity.ErrorLog;
import net.risesoft.entity.ProcessParam;
import net.risesoft.enums.ChaoSongStatusEnum;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.enums.ItemPermissionEnum;
import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ChaoSongModel;
import net.risesoft.model.itemadmin.ErrorLogModel;
import net.risesoft.model.itemadmin.OpenDataModel;
import net.risesoft.model.platform.org.CustomGroupMember;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.platform.org.Position;
import net.risesoft.model.processadmin.HistoricProcessInstanceModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.nosql.elastic.entity.ChaoSongInfo;
import net.risesoft.nosql.elastic.entity.OfficeDoneInfo;
import net.risesoft.nosql.elastic.repository.ChaoSongInfoRepository;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.query.platform.CustomGroupMemberQuery;
import net.risesoft.service.ErrorLogService;
import net.risesoft.service.OfficeDoneInfoService;
import net.risesoft.service.OfficeFollowService;
import net.risesoft.service.chaosong.ChaoSongInfoService;
import net.risesoft.service.core.DocumentService;
import net.risesoft.service.core.ProcessParamService;
import net.risesoft.util.Y9DateTimeUtils;
import net.risesoft.util.Y9EsIndexConst;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChaoSongInfoServiceImpl implements ChaoSongInfoService {

    private final ChaoSongInfoRepository chaoSongInfoRepository;

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

    private final ElasticsearchOperations elasticsearchOperations;

    private final CustomGroupApi customGroupApi;

    @Override
    public void changeChaoSongState(String id, String type) {
        String opinionState = "";
        if (ItemBoxTypeEnum.ADD.getValue().equals(type)) {
            opinionState = "1";
        }
        ChaoSongInfo chaoSongInfo = chaoSongInfoRepository.findById(id).orElse(null);
        assert chaoSongInfo != null;
        chaoSongInfo.setOpinionState(opinionState);
        chaoSongInfoRepository.save(chaoSongInfo);
    }

    @Override
    public void setRead(String id) {
        ChaoSongInfo chaoSong = chaoSongInfoRepository.findById(id).orElse(null);
        if (chaoSong != null && !ChaoSongStatusEnum.READ.getValue().equals(chaoSong.getStatus())) {
            ChaoSongInfo origin = new ChaoSongInfo();
            Y9BeanUtil.copyProperties(chaoSong, origin);
            chaoSong.setStatus(ChaoSongStatusEnum.READ.getValue());
            chaoSong.setReadTime(Y9DateTimeUtils.formatCurrentDateTime());
            chaoSongInfoRepository.save(chaoSong);
            Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(origin, chaoSong));
        }
    }

    @Override
    public void setRead(String[] ids) {
        for (String id : ids) {
            setRead(id);
        }
    }

    @Override
    public int countByProcessInstanceId(String userId, String processInstanceId) {
        return chaoSongInfoRepository.countBySenderIdIsNotAndProcessInstanceId(userId, processInstanceId);
    }

    @Override
    public int countByUserIdAndProcessInstanceId(String userId, String processInstanceId) {
        return chaoSongInfoRepository.countBySenderIdAndProcessInstanceId(userId, processInstanceId);
    }

    @Override
    public void deleteById(String id) {
        Optional<ChaoSongInfo> chaoSongInfo = chaoSongInfoRepository.findById(id);
        if (chaoSongInfo.isPresent()) {
            chaoSongInfoRepository.delete(chaoSongInfo.get());
            Y9Context.publishEvent(new Y9EntityDeletedEvent<>(chaoSongInfo.get()));
        }
    }

    @Override
    public void deleteByIds(String[] ids) {
        Arrays.stream(ids).forEach(this::deleteById);
    }

    @Override
    public boolean deleteByProcessInstanceId(String processInstanceId) {
        List<ChaoSongInfo> list = chaoSongInfoRepository.findByProcessInstanceId(processInstanceId);
        list.forEach(chaoSongInfo -> deleteById(chaoSongInfo.getId()));
        return true;
    }

    @Override
    public OpenDataModel detail(String processInstanceId, Integer status, boolean mobile) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        // 获取流程状态和任务信息
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
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(processParam.getCreateTime());
                String year = String.valueOf(calendar.get(Calendar.YEAR));
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
        String taskDefinitionKey = "";
        if (StringUtils.isNotEmpty(taskId)) {
            if (taskId.contains(SysVariables.COMMA)) {
                taskId = taskId.split(SysVariables.COMMA)[0];
            }
            TaskModel taskTemp = taskApi.findById(tenantId, taskId).getData();
            taskDefinitionKey = taskTemp.getTaskDefinitionKey();
        }
        return taskDefinitionKey;
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
        this.documentService.genDocumentModel(processParam.getItemId(), processDefInfo.getProcessDefinitionKey(),
            processDefInfo.getProcessDefinitionId(), taskDefinitionKey, mobile, model);
        // 设置菜单信息
        String menuName = "打印,抄送,关注,返回";
        String menuKey = "17,18,follow,03";
        model.setMenuName(menuName);
        model.setMenuKey(menuKey);
        return model;
    }

    @Override
    public ChaoSongInfo getById(String id) {
        return chaoSongInfoRepository.findById(id).orElse(null);
    }

    @Override
    public int getDone4OpinionCountByUserId(String userId) {
        return chaoSongInfoRepository.countByUserIdAndOpinionStateAndTenantId(userId, "1",
            Y9LoginUserHolder.getTenantId());
    }

    @Override
    public int getDoneCountByUserId(String userId) {
        return chaoSongInfoRepository.countByUserIdAndStatus(userId, 1);
    }

    @Override
    public int getTodoCountByUserId(String userId) {
        return chaoSongInfoRepository.countByUserIdAndStatus(userId, 2);
    }

    @Override
    public Y9Page<ChaoSongModel> pageByProcessInstanceIdAndUserName(String processInstanceId, String userName, int rows,
        int page) {
        Page<ChaoSongInfo> pageList = searchChaoSongInfo(processInstanceId, userName, rows, page);
        List<ChaoSongModel> list = buildChaoSongModelList(pageList.getContent(), processInstanceId, page, rows);
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), list);
    }

    private Page<ChaoSongInfo> searchChaoSongInfo(String processInstanceId, String userName, int rows, int page) {
        if (page < 1) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, rows, Direction.DESC, ItemConsts.CREATETIME_KEY);
        Criteria criteria = new Criteria(ItemConsts.TENANTID_KEY).is(Y9LoginUserHolder.getTenantId())
            .and(ItemConsts.PROCESSINSTANCEID_KEY)
            .is(processInstanceId);
        String senderId = Y9FlowableHolder.getOrgUnitId();
        criteria.subCriteria(new Criteria(ItemConsts.SENDERID_KEY).not().is(senderId));
        if (StringUtils.isNotBlank(userName)) {
            criteria.subCriteria(new Criteria(ItemConsts.USERNAME_KEY).contains(userName));
        }
        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        query.setTrackTotalHits(true);
        IndexCoordinates index = IndexCoordinates.of(Y9EsIndexConst.CHAONSONG_INFO);
        SearchHits<ChaoSongInfo> searchHits = elasticsearchOperations.search(query, ChaoSongInfo.class, index);
        List<ChaoSongInfo> csList = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        return new PageImpl<>(csList, pageable, searchHits.getTotalHits());
    }

    @Override
    public Y9Page<ChaoSongModel> pageBySenderIdAndProcessInstanceId(String senderId, String processInstanceId,
        String userName, int rows, int page) {
        if (page < 1) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, rows, Direction.DESC, ItemConsts.CREATETIME_KEY);
        Criteria criteria = new Criteria(ItemConsts.TENANTID_KEY).is(Y9LoginUserHolder.getTenantId())
            .and(ItemConsts.SENDERID_KEY)
            .is(senderId)
            .and(ItemConsts.PROCESSINSTANCEID_KEY)
            .is(processInstanceId);
        if (StringUtils.isNotBlank(userName)) {
            criteria.subCriteria(new Criteria(ItemConsts.USERNAME_KEY).contains(userName));
        }
        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        query.setTrackTotalHits(true);
        IndexCoordinates index = IndexCoordinates.of(Y9EsIndexConst.CHAONSONG_INFO);
        SearchHits<ChaoSongInfo> searchHits = elasticsearchOperations.search(query, ChaoSongInfo.class, index);
        List<ChaoSongInfo> csList = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        Page<ChaoSongInfo> pageList = new PageImpl<>(csList, pageable, searchHits.getTotalHits());
        List<ChaoSongModel> list = buildChaoSongModelList(pageList.getContent(), processInstanceId, page, rows);
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), list);
    }

    @Override
    public Y9Page<ChaoSongModel> pageDoneList(String orgUnitId, String documentTitle, int rows, int page) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (page < 1) {
            page = 1;
        }
        // 查询数据
        Page<ChaoSongInfo> pageList = searchDoneChaoSongInfo(orgUnitId, documentTitle, page, rows);
        // 构建返回列表
        List<ChaoSongModel> list = buildDoneChaoSongModelList(pageList.getContent(), tenantId, page, rows);
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), list);
    }

    private Page<ChaoSongInfo> searchDoneChaoSongInfo(String orgUnitId, String documentTitle, int page, int rows) {
        Pageable pageable = PageRequest.of(page - 1, rows, Direction.DESC, ItemConsts.CREATETIME_KEY);
        Criteria criteria = new Criteria(ItemConsts.TENANTID_KEY).is(Y9LoginUserHolder.getTenantId())
            .and(ItemConsts.USERID_KEY)
            .is(orgUnitId)
            .and(ItemConsts.STATUS_KEY)
            .is(ChaoSongStatusEnum.READ.getValue());
        if (StringUtils.isNotBlank(documentTitle)) {
            criteria.subCriteria(new Criteria(ItemConsts.TITLE_KEY).contains(documentTitle));
        }
        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        query.setTrackTotalHits(true);
        IndexCoordinates index = IndexCoordinates.of(Y9EsIndexConst.CHAONSONG_INFO);
        SearchHits<ChaoSongInfo> searchHits = elasticsearchOperations.search(query, ChaoSongInfo.class, index);
        List<ChaoSongInfo> csList = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        return new PageImpl<>(csList, pageable, searchHits.getTotalHits());
    }

    @Override
    public Y9Page<ChaoSongModel> pageMyChaoSongList(String searchName, String itemId, String userName, String state,
        String year, int rows, int page) {
        String userId = Y9FlowableHolder.getOrgUnitId();
        if (page < 1) {
            page = 1;
        }
        // 查询数据
        Page<ChaoSongInfo> pageList = searchChaoSongInfoByUser(Y9LoginUserHolder.getTenantId(), userId, searchName,
            itemId, userName, state, year, page, rows);
        // 构建返回列表
        List<ChaoSongModel> list = buildMyChaoSongModelList(pageList.getContent(), page, rows);
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), list);
    }

    private List<ChaoSongModel> buildMyChaoSongModelList(List<ChaoSongInfo> csList, int page, int rows) {
        AtomicInteger num = new AtomicInteger((page - 1) * rows);
        return csList.stream().map(chaoSongInfo -> {
            ChaoSongModel chaoSongModel = new ChaoSongModel();
            Y9BeanUtil.copyProperties(chaoSongInfo, chaoSongModel);
            String processInstanceId = chaoSongInfo.getProcessInstanceId();
            chaoSongModel.setSerialNumber(num.incrementAndGet());
            chaoSongModel.setId(chaoSongInfo.getId());
            chaoSongModel.setProcessInstanceId(processInstanceId);
            chaoSongModel.setSenderName(chaoSongInfo.getSenderName());
            chaoSongModel.setSendDeptName(chaoSongInfo.getSendDeptName());
            chaoSongModel.setStatus(chaoSongInfo.getStatus());
            chaoSongModel.setItemId(chaoSongInfo.getItemId());
            chaoSongModel.setItemName(chaoSongInfo.getItemName());
            chaoSongModel.setBanjie(false);
            try {
                OfficeDoneInfo hpi = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
                if (hpi != null) {
                    chaoSongModel.setProcessSerialNumber(hpi.getProcessSerialNumber());
                    chaoSongModel.setTitle(hpi.getTitle());
                    chaoSongModel.setUserId(chaoSongInfo.getUserId());
                    chaoSongModel.setUserName(chaoSongInfo.getUserName());
                    chaoSongModel.setUserDeptName(chaoSongInfo.getUserDeptName());
                    chaoSongModel.setSystemName(hpi.getSystemName());
                    chaoSongModel.setProcessDefinitionId(hpi.getProcessDefinitionId());
                    chaoSongModel.setNumber(hpi.getDocNumber());
                    chaoSongModel.setLevel(hpi.getUrgency());
                }
                chaoSongModel.setCreateTime(
                    Y9DateTimeUtils.formatDateTimeMinute(Y9DateTimeUtils.parseDateTime(chaoSongInfo.getCreateTime())));
                chaoSongModel.setReadTime(StringUtils.isNotBlank(chaoSongInfo.getReadTime())
                    ? Y9DateTimeUtils.formatDateTimeMinute(Y9DateTimeUtils.parseDateTime(chaoSongInfo.getReadTime()))
                    : "--");
                if (hpi != null) {
                    boolean banjie = hpi.getEndTime() != null;
                    if (banjie) {
                        chaoSongModel.setBanjie(true);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("获取列表失败", e);
            }
            return chaoSongModel;
        }).collect(Collectors.toList());
    }

    @Override
    public Y9Page<ChaoSongModel> pageOpinionChaosongByUserId(String userId, String documentTitle, int rows, int page) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<ChaoSongInfo> csList;
        if (page < 1) {
            page = 1;
        }
        List<ChaoSongModel> list = new ArrayList<>();
        Pageable pageable = PageRequest.of(page - 1, rows, Direction.DESC, ItemConsts.CREATETIME_KEY);
        Criteria criteria = new Criteria(ItemConsts.TENANTID_KEY).is(Y9LoginUserHolder.getTenantId())
            .and(ItemConsts.USERID_KEY)
            .is(userId)
            .and("opinionState")
            .is("1");
        if (StringUtils.isNotBlank(documentTitle)) {
            criteria.subCriteria(new Criteria(ItemConsts.TITLE_KEY).contains(documentTitle));
        }
        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        query.setTrackTotalHits(true);
        IndexCoordinates index = IndexCoordinates.of(Y9EsIndexConst.CHAONSONG_INFO);
        SearchHits<ChaoSongInfo> searchHits = elasticsearchOperations.search(query, ChaoSongInfo.class, index);
        List<ChaoSongInfo> list0 = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        Page<ChaoSongInfo> pageList = new PageImpl<>(list0, pageable, searchHits.getTotalHits());
        csList = pageList.getContent();
        HistoricProcessInstanceModel hpi;
        ProcessParam processParam;
        int num = (page - 1) * rows;
        for (ChaoSongInfo cs : csList) {
            ChaoSongModel model = new ChaoSongModel();
            Y9BeanUtil.copyProperties(cs, model);
            String processInstanceId = cs.getProcessInstanceId();
            model.setSerialNumber(num + 1);
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
                hpi = historicProcessApi.getById(tenantId, processInstanceId).getData();
                boolean banjie = hpi == null || hpi.getEndTime() != null;
                if (banjie) {
                    model.setBanjie(true);
                }
                OfficeDoneInfo officeDoneInfo = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
                model.setProcessDefinitionId(officeDoneInfo != null ? officeDoneInfo.getProcessDefinitionId() : "");
                model.setProcessSerialNumber(officeDoneInfo.getProcessSerialNumber());
                int countFollow = officeFollowService.countByProcessInstanceId(processInstanceId);
                model.setFollow(countFollow > 0);
                processParam = processParamService.findByProcessInstanceId(processInstanceId);
                model.setNumber(processParam.getCustomNumber());
                model.setLevel(processParam.getCustomLevel());
            } catch (Exception e) {
                LOGGER.error("获取抄送信息数据失败", e);
            }
            num += 1;
            list.add(model);
        }
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), list);
    }

    @Override
    public Y9Page<ChaoSongModel> pageTodoList(String orgUnitId, String documentTitle, int rows, int page) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (page < 1) {
            page = 1;
        }
        // 查询数据
        Page<ChaoSongInfo> pageList = searchTodoChaoSongInfo(orgUnitId, documentTitle, page, rows);
        // 构建返回列表
        List<ChaoSongModel> list = buildTodoChaoSongModelList(pageList.getContent(), tenantId, page, rows);
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), list);
    }

    private Page<ChaoSongInfo> searchTodoChaoSongInfo(String orgUnitId, String documentTitle, int page, int rows) {
        Pageable pageable = PageRequest.of(page - 1, rows, Direction.DESC, ItemConsts.CREATETIME_KEY);
        Criteria criteria = new Criteria(ItemConsts.TENANTID_KEY).is(Y9LoginUserHolder.getTenantId())
            .and(ItemConsts.USERID_KEY)
            .is(orgUnitId)
            .and(ItemConsts.STATUS_KEY)
            .is(2);
        if (StringUtils.isNotBlank(documentTitle)) {
            criteria.subCriteria(new Criteria(ItemConsts.TITLE_KEY).contains(documentTitle));
        }
        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        query.setTrackTotalHits(true);
        IndexCoordinates index = IndexCoordinates.of(Y9EsIndexConst.CHAONSONG_INFO);
        SearchHits<ChaoSongInfo> searchHits = elasticsearchOperations.search(query, ChaoSongInfo.class, index);
        List<ChaoSongInfo> csList = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        return new PageImpl<>(csList, pageable, searchHits.getTotalHits());
    }

    @Override
    public ChaoSongInfo save(ChaoSongInfo chaoSong) {
        chaoSong = chaoSongInfoRepository.save(chaoSong);
        Y9Context.publishEvent(new Y9EntityCreatedEvent<>(chaoSong));
        return chaoSong;
    }

    @Override
    public void save(List<ChaoSongInfo> chaoSongList) {
        chaoSongList.forEach(this::save);
    }

    @Override
    public Y9Result<Object> save(String processInstanceId, String users, String isSendSms, String isShuMing,
        String smsContent, String smsPersonId) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String currUserId = Y9FlowableHolder.getOrgUnitId();
            OrgUnit currOrgUnit = Y9FlowableHolder.getOrgUnit();
            ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
            String title = processParam.getTitle();
            String itemId = processParam.getItemId();
            String itemName = processParam.getItemName();
            // 解析用户列表
            List<String> userIdListAdd = parseUserList(tenantId, users);
            // 构建抄送信息列表
            List<ChaoSongInfo> csList = buildChaoSongInfoList(tenantId, currUserId, currOrgUnit, processInstanceId,
                title, itemId, itemName, userIdListAdd);
            // 保存抄送信息
            this.save(csList);
            return Y9Result.successMsg("抄送成功");
        } catch (Exception e) {
            handleError(processInstanceId, e);
            return Y9Result.failure("抄送失败");
        }
    }

    private List<String> parseUserList(String tenantId, String users) {
        String[] orgUnitList = users.split(";");
        List<String> userIdListAdd = new ArrayList<>();

        for (String orgUnitStr : orgUnitList) {
            String[] orgUnitArr = orgUnitStr.split(":");
            ItemPermissionEnum type = ItemPermissionEnum.valueOf(Integer.parseInt(orgUnitArr[0]));
            String orgUnitId = orgUnitArr[1];
            switch (type) {
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
                    LOGGER.error("不支持的权限类型：{}", type);
            }
        }
        return userIdListAdd;
    }

    private List<ChaoSongModel> buildTodoChaoSongModelList(List<ChaoSongInfo> csList, String tenantId, int page,
        int rows) {
        AtomicInteger num = new AtomicInteger((page - 1) * rows);
        return csList.stream().map(chaoSongInfo -> {
            ChaoSongModel chaoSongModel = new ChaoSongModel();
            Y9BeanUtil.copyProperties(chaoSongInfo, chaoSongModel);
            String processInstanceId = chaoSongInfo.getProcessInstanceId();
            int serialNumber = num.incrementAndGet();
            chaoSongModel.setSerialNumber(serialNumber);
            chaoSongModel.setId(chaoSongInfo.getId());
            chaoSongModel.setProcessInstanceId(processInstanceId);
            chaoSongModel.setSenderName(chaoSongInfo.getSenderName());
            chaoSongModel.setSendDeptName(chaoSongInfo.getSendDeptName());
            chaoSongModel.setTitle(chaoSongInfo.getTitle());
            chaoSongModel.setStatus(chaoSongInfo.getStatus());
            chaoSongModel.setItemId(chaoSongInfo.getItemId());
            chaoSongModel.setItemName(chaoSongInfo.getItemName());
            chaoSongModel.setBanjie(false);
            try {
                chaoSongModel.setCreateTime(
                    Y9DateTimeUtils.formatDateTimeMinute(Y9DateTimeUtils.parseDateTime(chaoSongInfo.getCreateTime())));
                HistoricProcessInstanceModel hpi = historicProcessApi.getById(tenantId, processInstanceId).getData();
                chaoSongModel.setBanjie(hpi == null || hpi.getEndTime() != null);
                OfficeDoneInfo officeDoneInfo = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
                chaoSongModel
                    .setProcessDefinitionId(officeDoneInfo != null ? officeDoneInfo.getProcessDefinitionId() : "");
                chaoSongModel.setProcessSerialNumber(officeDoneInfo.getProcessSerialNumber());
                int countFollow = officeFollowService.countByProcessInstanceId(processInstanceId);
                chaoSongModel.setFollow(countFollow > 0);
                ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
                chaoSongModel.setNumber(processParam.getCustomNumber());
                chaoSongModel.setLevel(processParam.getCustomLevel());
            } catch (Exception e) {
                LOGGER.error("获取抄送待办数据失败", e);
            }
            return chaoSongModel;
        }).collect(Collectors.toList());
    }

    private List<ChaoSongInfo> buildChaoSongInfoList(String tenantId, String currUserId, OrgUnit currOrgUnit,
        String processInstanceId, String title, String itemId, String itemName, List<String> userIdListAdd) {
        OrgUnit dept = getDepartment(tenantId, currOrgUnit);
        List<ChaoSongInfo> csList = new ArrayList<>();
        for (String userId : userIdListAdd) {
            OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, userId).getData();
            if (orgUnit != null) {
                ChaoSongInfo cs = createChaoSongInfo(tenantId, currUserId, currOrgUnit, dept, processInstanceId, title,
                    itemId, itemName, orgUnit);
                csList.add(cs);
            }
        }
        return csList;
    }

    private List<ChaoSongModel> buildChaoSongModelList(List<ChaoSongInfo> csList, String processInstanceId, int page,
        int rows) {
        AtomicInteger num = new AtomicInteger((page - 1) * rows);
        return csList.stream().map(chaoSongInfo -> {
            ChaoSongModel chaoSongModel = new ChaoSongModel();
            Y9BeanUtil.copyProperties(chaoSongInfo, chaoSongModel);
            chaoSongModel.setSerialNumber(num.incrementAndGet());
            chaoSongModel.setId(chaoSongInfo.getId());
            chaoSongModel.setProcessInstanceId(processInstanceId);
            chaoSongModel.setSenderName(chaoSongInfo.getSenderName());
            chaoSongModel.setSendDeptName(chaoSongInfo.getSendDeptName());
            chaoSongModel.setUserName(chaoSongInfo.getUserName());
            chaoSongModel.setUserDeptName(chaoSongInfo.getUserDeptName());
            chaoSongModel.setTitle(chaoSongInfo.getTitle());
            try {
                chaoSongModel.setCreateTime(
                    Y9DateTimeUtils.formatDateTimeMinute(Y9DateTimeUtils.parseDateTime(chaoSongInfo.getCreateTime())));
                chaoSongModel.setReadTime(StringUtils.isNotBlank(chaoSongInfo.getReadTime())
                    ? Y9DateTimeUtils.formatDateTimeMinute(Y9DateTimeUtils.parseDateTime(chaoSongInfo.getReadTime()))
                    : "--");
            } catch (Exception e) {
                LOGGER.error("抄送时间转换错误", e);
            }
            return chaoSongModel;
        }).collect(Collectors.toList());
    }

    private List<ChaoSongModel> buildDoneChaoSongModelList(List<ChaoSongInfo> csList, String tenantId, int page,
        int rows) {
        AtomicInteger num = new AtomicInteger((page - 1) * rows);
        return csList.stream().map(chaoSongInfo -> {
            ChaoSongModel chaoSongModel = new ChaoSongModel();
            Y9BeanUtil.copyProperties(chaoSongInfo, chaoSongModel);
            String processInstanceId = chaoSongInfo.getProcessInstanceId();
            chaoSongModel.setSerialNumber(num.incrementAndGet());
            chaoSongModel.setId(chaoSongInfo.getId());
            chaoSongModel.setProcessInstanceId(processInstanceId);
            chaoSongModel.setSenderName(chaoSongInfo.getSenderName());
            chaoSongModel.setSendDeptName(chaoSongInfo.getSendDeptName());
            chaoSongModel.setTitle(chaoSongInfo.getTitle());
            chaoSongModel.setStatus(chaoSongInfo.getStatus());
            chaoSongModel.setItemId(chaoSongInfo.getItemId());
            chaoSongModel.setItemName(chaoSongInfo.getItemName());
            chaoSongModel.setBanjie(false);
            try {
                chaoSongModel.setCreateTime(
                    Y9DateTimeUtils.formatDateTimeMinute(Y9DateTimeUtils.parseDateTime(chaoSongInfo.getCreateTime())));
                chaoSongModel.setReadTime(StringUtils.isNotBlank(chaoSongInfo.getReadTime())
                    ? Y9DateTimeUtils.formatDateTimeMinute(Y9DateTimeUtils.parseDateTime(chaoSongInfo.getReadTime()))
                    : "--");
                HistoricProcessInstanceModel hpi = historicProcessApi.getById(tenantId, processInstanceId).getData();
                chaoSongModel.setBanjie(hpi == null || hpi.getEndTime() != null);
                OfficeDoneInfo officeDoneInfo = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
                chaoSongModel
                    .setProcessDefinitionId(officeDoneInfo != null ? officeDoneInfo.getProcessDefinitionId() : "");
                chaoSongModel.setProcessSerialNumber(officeDoneInfo.getProcessSerialNumber());
                int countFollow = officeFollowService.countByProcessInstanceId(processInstanceId);
                chaoSongModel.setFollow(countFollow > 0);
                ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
                chaoSongModel.setNumber(processParam.getCustomNumber());
                chaoSongModel.setLevel(processParam.getCustomLevel());
            } catch (Exception e) {
                LOGGER.error("获取办结的抄送数据失败", e);
            }
            return chaoSongModel;
        }).collect(Collectors.toList());
    }

    private OrgUnit getDepartment(String tenantId, OrgUnit currOrgUnit) {
        OrgUnit dept = orgUnitApi.getOrgUnit(tenantId, currOrgUnit.getParentId()).getData();
        if (null == dept || null == dept.getId()) {
            dept = organizationApi.get(tenantId, currOrgUnit.getParentId()).getData();
        }
        return dept;
    }

    private ChaoSongInfo createChaoSongInfo(String tenantId, String currUserId, OrgUnit currOrgUnit, OrgUnit dept,
        String processInstanceId, String title, String itemId, String itemName, OrgUnit orgUnit) {
        ChaoSongInfo cs = new ChaoSongInfo();
        cs.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        cs.setCreateTime(Y9DateTimeUtils.formatCurrentDateTime());
        cs.setProcessInstanceId(processInstanceId);
        cs.setSenderId(currUserId);
        cs.setSenderName(currOrgUnit.getName());
        cs.setSendDeptId(dept.getId());
        cs.setSendDeptName(dept.getName());
        cs.setStatus(2);
        cs.setTenantId(tenantId);
        cs.setTitle(title);
        cs.setUserId(orgUnit.getId());
        cs.setUserName(orgUnit.getName());
        OrgUnit department = orgUnitApi.getOrgUnit(tenantId, orgUnit.getParentId()).getData();
        if (department != null) {
            cs.setUserDeptId(department.getId());
            cs.setUserDeptName(department.getName());
        }
        cs.setItemId(itemId);
        cs.setItemName(itemName);
        return cs;
    }

    private void handleError(String processInstanceId, Exception e) {
        final Writer result = new StringWriter();
        final PrintWriter print = new PrintWriter(result);
        e.printStackTrace(print);
        try {
            String msg = result.toString();
            String time = Y9DateTimeUtils.formatCurrentDateTime();
            ErrorLog errorLog = new ErrorLog();
            errorLog.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            errorLog.setErrorFlag(ErrorLogModel.ERROR_FLAG_SAVE_CHAOSONG);
            errorLog.setErrorType(ErrorLogModel.ERROR_PROCESS_INSTANCE);
            errorLog.setExtendField("抄送保存失败");
            errorLog.setProcessInstanceId(processInstanceId);
            errorLog.setTaskId("");
            errorLog.setText(msg);
            errorLogService.saveErrorLog(errorLog);
        } catch (Exception e2) {
            LOGGER.error("保存抄送失败异常,错误信息为：", e2);
        }
    }

    @Override
    public Y9Page<ChaoSongModel> searchAllByUserId(String searchName, String itemId, String userName, String state,
        String year, Integer page, Integer rows) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9FlowableHolder.getOrgUnitId();
        if (page < 1) {
            page = 1;
        }
        // 查询数据
        Page<ChaoSongInfo> pageList =
            searchChaoSongInfoByUser(tenantId, userId, searchName, itemId, userName, state, year, page, rows);
        // 构建返回列表
        List<ChaoSongModel> list = buildChaoSongModelList(pageList.getContent(), tenantId, page, rows);
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), list);
    }

    private Page<ChaoSongInfo> searchChaoSongInfoByUser(String tenantId, String userId, String searchName,
        String itemId, String userName, String state, String year, Integer page, Integer rows) {
        Pageable pageable = PageRequest.of(page - 1, rows, Direction.DESC, ItemConsts.CREATETIME_KEY);
        Criteria criteria = new Criteria(ItemConsts.TENANTID_KEY).is(tenantId).and(ItemConsts.USERID_KEY).is(userId);
        if (StringUtils.isNotBlank(searchName)) {
            criteria.subCriteria(new Criteria(ItemConsts.TITLE_KEY).contains(searchName));
        }
        if (StringUtils.isNotBlank(itemId)) {
            criteria.subCriteria(new Criteria(ItemConsts.ITEMID_KEY).is(itemId));
        }
        if (StringUtils.isNotBlank(userName)) {
            criteria.subCriteria(new Criteria(ItemConsts.SENDERNAME_KEY).contains(userName));
        }
        if (StringUtils.isNotBlank(state)) {
            criteria.subCriteria(new Criteria(ItemConsts.STATUS_KEY).is(Integer.parseInt(state)));
        }
        if (StringUtils.isNotBlank(year)) {
            criteria.subCriteria(new Criteria(ItemConsts.CREATETIME_KEY).contains(year));
        }
        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        query.setTrackTotalHits(true);
        IndexCoordinates index = IndexCoordinates.of(Y9EsIndexConst.CHAONSONG_INFO);
        SearchHits<ChaoSongInfo> searchHits = elasticsearchOperations.search(query, ChaoSongInfo.class, index);
        List<ChaoSongInfo> csList = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        return new PageImpl<>(csList, pageable, searchHits.getTotalHits());
    }

    @Override
    public Y9Page<ChaoSongModel> searchAllList(String searchName, String itemId, String senderName, String userName,
        String state, String year, Integer page, Integer rows) {

        String tenantId = Y9LoginUserHolder.getTenantId();

        if (page < 1) {
            page = 1;
        }

        // 查询数据
        Page<ChaoSongInfo> pageList =
            searchChaoSongInfo(searchName, itemId, senderName, userName, state, year, page, rows);

        // 构建返回列表
        List<ChaoSongModel> list = buildChaoSongModelList(pageList.getContent(), tenantId, page, rows);

        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), list);
    }

    private Page<ChaoSongInfo> searchChaoSongInfo(String searchName, String itemId, String senderName, String userName,
        String state, String year, Integer page, Integer rows) {
        Pageable pageable = PageRequest.of(page - 1, rows, Direction.DESC, ItemConsts.CREATETIME_KEY);
        Criteria criteria = new Criteria(ItemConsts.TENANTID_KEY).is(Y9LoginUserHolder.getTenantId());
        if (StringUtils.isNotBlank(searchName)) {
            criteria.subCriteria(new Criteria(ItemConsts.TITLE_KEY).contains(searchName));
        }
        if (StringUtils.isNotBlank(itemId)) {
            criteria.subCriteria(new Criteria(ItemConsts.ITEMID_KEY).is(itemId));
        }
        if (StringUtils.isNotBlank(userName)) {
            criteria.subCriteria(new Criteria(ItemConsts.USERNAME_KEY).contains(userName));
        }
        if (StringUtils.isNotBlank(senderName)) {
            criteria.subCriteria(new Criteria(ItemConsts.SENDERNAME_KEY).contains(senderName));
        }
        if (StringUtils.isNotBlank(state)) {
            criteria.subCriteria(new Criteria(ItemConsts.STATUS_KEY).is(Integer.parseInt(state)));
        }
        if (StringUtils.isNotBlank(year)) {
            criteria.subCriteria(new Criteria(ItemConsts.CREATETIME_KEY).contains(year));
        }
        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        query.setTrackTotalHits(true);
        IndexCoordinates index = IndexCoordinates.of(Y9EsIndexConst.CHAONSONG_INFO);
        SearchHits<ChaoSongInfo> searchHits = elasticsearchOperations.search(query, ChaoSongInfo.class, index);
        List<ChaoSongInfo> csList = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        return new PageImpl<>(csList, pageable, searchHits.getTotalHits());
    }

    @Override
    public void updateTitle(String processInstanceId, String documentTitle) {
        List<ChaoSongInfo> list = chaoSongInfoRepository.findByProcessInstanceId(processInstanceId);
        list.forEach(chaoSong -> {
            ChaoSongInfo origin = new ChaoSongInfo();
            Y9BeanUtil.copyProperties(chaoSong, origin);
            chaoSong.setTitle(documentTitle);
            chaoSongInfoRepository.save(chaoSong);
            Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(origin, chaoSong));
        });
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
