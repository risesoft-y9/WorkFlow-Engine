package net.risesoft.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.api.org.OrgUnitApi;
import net.risesoft.api.org.PersonApi;
import net.risesoft.api.org.PositionApi;
import net.risesoft.api.permission.PersonRoleApi;
import net.risesoft.api.processadmin.HistoricProcessApi;
import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.entity.ItemOpinionFrameBind;
import net.risesoft.entity.Opinion;
import net.risesoft.entity.OpinionHistory;
import net.risesoft.entity.ProcessParam;
import net.risesoft.entity.ProcessTrack;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.OpinionHistoryModel;
import net.risesoft.model.itemadmin.OpinionModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.PersonExt;
import net.risesoft.model.platform.Position;
import net.risesoft.model.processadmin.HistoricProcessInstanceModel;
import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.jpa.OpinionHistoryRepository;
import net.risesoft.repository.jpa.OpinionRepository;
import net.risesoft.service.AsyncHandleService;
import net.risesoft.service.ItemOpinionFrameBindService;
import net.risesoft.service.OpinionService;
import net.risesoft.service.ProcessParamService;
import net.risesoft.service.ProcessTrackService;
import net.risesoft.service.SpmApproveItemService;
import net.risesoft.util.CommentUtil;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service(value = "opinionService")
public class OpinionServiceImpl implements OpinionService {

    @Autowired
    private OpinionRepository opinionRepository;

    @Autowired
    private ItemOpinionFrameBindService itemOpinionFrameBindService;

    @Autowired
    private ProcessTrackService processTrackService;

    @Autowired
    private PersonRoleApi personRoleApi;

    @Autowired
    private PersonApi personApi;

    @Autowired
    private TaskApi taskManager;

    @Autowired
    private HistoricTaskApi historicTaskApi;

    @Autowired
    private SpmApproveItemService spmApproveItemService;

    @Autowired
    private RepositoryApi repositoryManager;

    @Autowired
    private OrgUnitApi orgUnitManager;

    @Autowired
    private HistoricProcessApi historicProcessManager;

    @Autowired
    private ProcessParamService processParamService;

    @Autowired
    private AsyncHandleService asyncHandleService;

    @Autowired
    private OpinionHistoryRepository opinionHistoryRepository;

    @Autowired
    private PositionApi positionManager;

    @Autowired
    private VariableApi variableApi;

    @Override
    public Boolean checkSignOpinion(String processSerialNumber, String taskId) {
        Boolean isSign = false;
        Integer count = 0;
        if (StringUtils.isEmpty(taskId)) {
            count = this.findByProcSerialNumber(processSerialNumber);
            if (count > 0) {
                isSign = true;
            }
            return isSign;
        }

        count = this.getCountByTaskId(taskId);
        if (count > 0) {
            isSign = true;
        }
        return isSign;
    }

    @Override
    @Transactional(readOnly = false)
    public void copy(String oldProcessSerialNumber, String oldOpinionFrameMark, String newProcessSerialNumber, String newOpinionFrameMark, String newProcessInstanceId, String newTaskId) throws Exception {
        try {
            List<Opinion> oldOpinionList = this.findByProcessSerialNumber(oldProcessSerialNumber);
            for (Opinion oldOpinion : oldOpinionList) {
                oldOpinion.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                oldOpinion.setOpinionFrameMark(newOpinionFrameMark);
                oldOpinion.setProcessSerialNumber(newProcessSerialNumber);
                this.save(oldOpinion);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public int countOpinionHistory(String processSerialNumber, String opinionFrameMark) {
        return opinionHistoryRepository.countByProcessSerialNumberAndOpinionFrameMark(processSerialNumber, opinionFrameMark);
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(String id) {
        Opinion oldOpinion = opinionRepository.findById(id).orElse(null);
        opinionRepository.delete(oldOpinion);
        asyncHandleService.saveOpinionHistory(Y9LoginUserHolder.getTenantId(), oldOpinion, "2");
    }

    @Override
    public List<Opinion> findByProcessSerialNumber(String processSerialNumber) {
        return opinionRepository.findByProcessSerialNumber(processSerialNumber);
    }

    @Override
    public int findByProcSerialNumber(String processSerialNumber) {
        return opinionRepository.findByProcSerialNumber(processSerialNumber);
    }

    @Override
    public Opinion findByPsnsAndTaskIdAndOfidAndUserId(String processSerialNumber, String taskId, String opinionFrameId, String userId) {
        return opinionRepository.findByPsnsAndTaskIdAndOfidAndUserId(processSerialNumber, taskId, opinionFrameId, userId);
    }

    @Override
    public List<Opinion> findByTaskId(String taskId) {
        return opinionRepository.findByTaskId(taskId);
    }

    @Override
    public List<Opinion> findByTaskIdAndPositionIdAndProcessTrackIdIsNull(String taskId, String positionId) {
        return opinionRepository.findByTaskIdAndPositionIdAndProcessTrackIdIsNull(taskId, positionId);
    }

    @Override
    public List<Opinion> findByTaskIdAndProcessTrackId(String taskId, String processTrackId) {
        return opinionRepository.findByTaskIdAndProcessTrackIdOrderByCreateDateDesc(taskId, processTrackId);
    }

    @Override
    public List<Opinion> findByTaskIdAndUserIdAndProcessTrackIdIsNull(String taskId, String userId) {
        return opinionRepository.findByTaskIdAndUserIdAndProcessTrackIdIsNull(taskId, userId);
    }

    @Override
    public Opinion findOne(String id) {
        return opinionRepository.findById(id).orElse(null);
    }

    @Override
    public Integer getCount4Personal(String processSerialNumber, String category, String userId) {
        return opinionRepository.getCount4Personal(processSerialNumber, category, userId);
    }

    @Override
    public Integer getCount4Personal(String processSerialNumber, String taskId, String opinionFrameId, String userId) {
        return opinionRepository.getCount4Personal(processSerialNumber, taskId, opinionFrameId, userId);
    }

    @Override
    public int getCountByTaskId(String taskId) {
        return opinionRepository.getCountByTaskId(taskId);
    }

    @Override
    public List<OpinionHistoryModel> opinionHistoryList(String processSerialNumber, String opinionFrameMark) {
        List<OpinionHistoryModel> resList = new ArrayList<OpinionHistoryModel>();
        try {
            List<OpinionHistory> list = opinionHistoryRepository.findByProcessSerialNumberAndOpinionFrameMark(processSerialNumber, opinionFrameMark);
            List<Opinion> list1 = opinionRepository.findByProcSerialNumberAndOpinionFrameMark(processSerialNumber, opinionFrameMark);
            for (OpinionHistory his : list) {
                OpinionHistoryModel historyModel = new OpinionHistoryModel();
                Y9BeanUtil.copyProperties(his, historyModel);
                resList.add(historyModel);
            }
            for (Opinion opinion : list1) {
                OpinionHistoryModel history = new OpinionHistoryModel();
                history.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                history.setContent(opinion.getContent());
                history.setCreateDate(opinion.getCreateDate());
                history.setSaveDate("");
                history.setDeptId(opinion.getDeptId());
                history.setDeptName(opinion.getDeptName());
                history.setModifyDate(opinion.getModifyDate());
                history.setOpinionFrameMark(opinion.getOpinionFrameMark());
                history.setOpinionType("");
                history.setProcessInstanceId(opinion.getProcessInstanceId());
                history.setProcessSerialNumber(opinion.getProcessSerialNumber());
                history.setTaskId(opinion.getTaskId());
                history.setTenantId(opinion.getTenantId());
                history.setUserId(opinion.getUserId());
                history.setUserName(opinion.getUserName());
                resList.add(history);
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Collections.sort(resList, new Comparator<OpinionHistoryModel>() {
                @Override
                public int compare(OpinionHistoryModel o1, OpinionHistoryModel o2) {
                    try {
                        String startTime1 = o1.getCreateDate();
                        String startTime2 = o2.getCreateDate();
                        long time1 = sdf.parse(startTime1).getTime();
                        long time2 = sdf.parse(startTime2).getTime();
                        if (time1 > time2) {
                            return 1;
                        } else if (time1 == time2) {
                            String modifyDate1 = o1.getModifyDate();
                            String modifyDate2 = o2.getModifyDate();
                            if (StringUtils.isBlank(modifyDate1)) {
                                return -1;
                            } else if (StringUtils.isBlank(modifyDate2)) {
                                return 1;
                            } else {
                                long time11 = sdf.parse(modifyDate1).getTime();
                                long time22 = sdf.parse(modifyDate2).getTime();
                                if (time11 > time22) {
                                    return 1;
                                } else {
                                    return -1;
                                }
                            }
                        } else {
                            return -1;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return -1;
                }
            });
        } catch (BeansException e) {
            e.printStackTrace();
        }
        return resList;
    }

    @Override
    public List<Map<String, Object>> personCommentList(String processSerialNumber, String taskId, String itembox, String opinionFrameMark, String itemId, String taskDefinitionKey, String activitiUser) {
        List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
        try {
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            String tenantId = Y9LoginUserHolder.getTenantId(), personId = person.getPersonId();
            Map<String, Object> addableMap = new HashMap<String, Object>(16);
            // 代录意见权限
            addableMap.put("addAgent", false);
            addableMap.put("addable", true);
            addableMap.put("opinionFrameMark", opinionFrameMark);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            List<Opinion> list = opinionRepository.findByProcSerialNumberAndOpinionFrameMark(processSerialNumber, opinionFrameMark);
            if (itembox.equalsIgnoreCase(ItemBoxTypeEnum.DRAFT.getValue()) || itembox.equalsIgnoreCase(ItemBoxTypeEnum.ADD.getValue())) {
                if (list.size() >= 1) {
                    addableMap.put("addable", true);
                    for (Opinion opinion : list) {
                        Map<String, Object> map = new HashMap<String, Object>(16);
                        opinion.setContent(CommentUtil.replaceEnter2Br(opinion.getContent()));
                        map.put("date", sdf2.format(sdf.parse(opinion.getCreateDate())));
                        opinion.setModifyDate(sdf1.format(sdf.parse(opinion.getModifyDate())));
                        opinion.setCreateDate(sdf1.format(sdf.parse(opinion.getCreateDate())));
                        if (personId.equals(opinion.getUserId())) {
                            map.put("editable", true);
                            addableMap.put("addable", false);
                        }
                        OpinionModel opinionModel = new OpinionModel();
                        Y9BeanUtil.copyProperties(opinion, opinionModel);
                        if (StringUtils.isNotBlank(opinion.getPositionId()) && StringUtils.isBlank(opinion.getPositionName())) {
                            Position position = positionManager.getPosition(tenantId, opinion.getPositionId()).getData();
                            opinionModel.setPositionName(position != null ? position.getName() : "");
                        }
                        PersonExt personExt = personApi.getPersonExtByPersonId(tenantId, opinionModel.getUserId()).getData();
                        opinionModel.setSign(personExt != null ? personExt.getSign() : null);
                        map.put("opinion", opinionModel);
                        resList.add(map);
                    }
                    boolean addable = (boolean)addableMap.get("addable");
                    if (!addable) {
                        // 没有意见框编辑权限时，增加代录权限
                        // boolean hasRole1 = personRoleApi.hasRole(Y9LoginUserHolder.getTenantId(), "itemAdmin", "",
                        // "代录意见角色", person.getPersonId()).getData();
                        // if (hasRole1) {
                        // addableMap.put("addAgent", true);
                        // }
                    }
                    resList.add(addableMap);
                    return resList;
                }
                /**
                 * 当前意见框,不存在意见，则判断是否可以签写意见
                 */
                addableMap.put("addable", false);
                SpmApproveItem item = spmApproveItemService.findById(itemId);
                String proDefKey = item.getWorkflowGuid();
                ProcessDefinitionModel latestpd = repositoryManager.getLatestProcessDefinitionByKey(tenantId, proDefKey);
                String processDefinitionId = latestpd.getId();
                ItemOpinionFrameBind bind = itemOpinionFrameBindService.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOpinionFrameMark(itemId, processDefinitionId, taskDefinitionKey, opinionFrameMark);
                if (null != bind) {
                    // 是否必填意见，与addable一起判定，都为true时提示必填。
                    addableMap.put("signOpinion", bind.isSignOpinion());
                    List<String> roleIds = bind.getRoleIds();
                    if (!roleIds.isEmpty()) {
                        for (String roleId : roleIds) {
                            Boolean hasRole = personRoleApi.hasRole(tenantId, roleId, personId).getData();
                            if (hasRole) {
                                addableMap.put("addable", true);
                                break;
                            }
                        }
                    } else {
                        addableMap.put("addable", true);
                    }
                }
                boolean addable = (boolean)addableMap.get("addable");
                if (!addable) {
                    // 没有意见框编辑权限时，增加代录权限
                    // boolean hasRole1 = personRoleApi.hasRole(Y9LoginUserHolder.getTenantId(), "itemAdmin", "",
                    // "代录意见角色", person.getPersonId()).getData();
                    // if (hasRole1) {
                    // addableMap.put("addAgent", true);
                    // }
                }
            } else if (itembox.equalsIgnoreCase(ItemBoxTypeEnum.TODO.getValue())) {
                /**
                 * 用户未签收前打开公文时(办理人为空)，只读所有意见
                 */
                if (StringUtils.isBlank(activitiUser)) {
                    addableMap.put("addable", false);
                    for (Opinion opinion : list) {
                        Map<String, Object> map = new HashMap<String, Object>(16);
                        opinion.setContent(CommentUtil.replaceEnter2Br(opinion.getContent()));
                        if (!opinion.getCreateDate().equals(opinion.getModifyDate())) {
                            map.put("isEdit", true);
                        }
                        map.put("date", sdf2.format(sdf.parse(opinion.getCreateDate())));
                        opinion.setModifyDate(sdf1.format(sdf.parse(opinion.getModifyDate())));
                        opinion.setCreateDate(sdf1.format(sdf.parse(opinion.getCreateDate())));
                        map.put("editable", false);
                        OpinionModel opinionModel = new OpinionModel();
                        Y9BeanUtil.copyProperties(opinion, opinionModel);
                        if (StringUtils.isNotBlank(opinion.getPositionId()) && StringUtils.isBlank(opinion.getPositionName())) {
                            Position position = positionManager.getPosition(tenantId, opinion.getPositionId()).getData();
                            opinionModel.setPositionName(position != null ? position.getName() : "");
                        }
                        PersonExt personExt = personApi.getPersonExtByPersonId(tenantId, opinionModel.getUserId()).getData();
                        opinionModel.setSign(personExt != null ? personExt.getSign() : null);
                        map.put("opinion", opinionModel);
                        resList.add(map);
                    }
                    resList.add(addableMap);
                    return resList;
                }
                TaskModel task = taskManager.findById(tenantId, taskId);
                String takeBack = variableApi.getVariableLocal(tenantId, taskId, SysVariables.TAKEBACK);
                for (Opinion opinion : list) {
                    Map<String, Object> map = new HashMap<String, Object>(16);
                    opinion.setContent(CommentUtil.replaceEnter2Br(opinion.getContent()));
                    if (!opinion.getCreateDate().equals(opinion.getModifyDate())) {
                        map.put("isEdit", true);
                    }
                    map.put("date", sdf2.format(sdf.parse(opinion.getCreateDate())));
                    opinion.setModifyDate(sdf1.format(sdf.parse(opinion.getModifyDate())));
                    opinion.setCreateDate(sdf1.format(sdf.parse(opinion.getCreateDate())));
                    OpinionModel opinionModel = new OpinionModel();
                    Y9BeanUtil.copyProperties(opinion, opinionModel);
                    if (StringUtils.isNotBlank(opinion.getPositionId()) && StringUtils.isBlank(opinion.getPositionName())) {
                        Position position = positionManager.getPosition(tenantId, opinion.getPositionId()).getData();
                        opinionModel.setPositionName(position != null ? position.getName() : "");
                    }
                    PersonExt personExt = personApi.getPersonExtByPersonId(tenantId, opinionModel.getUserId()).getData();
                    opinionModel.setSign(personExt != null ? personExt.getSign() : null);
                    map.put("opinion", opinionModel);
                    map.put("editable", false);

                    if (taskId.equals(opinion.getTaskId())) {
                        if (personId.equals(opinion.getUserId())) {
                            map.put("editable", true);
                            addableMap.put("addable", false);
                        }
                    } else {// 收回件可编辑意见
                        if (takeBack != null && Boolean.valueOf(takeBack) && Y9LoginUserHolder.getPersonId().equals(opinion.getUserId())) {// 收回件
                            List<HistoricTaskInstanceModel> tlist = historicTaskApi.findTaskByProcessInstanceIdOrByEndTimeAsc(tenantId, task.getProcessInstanceId(), "");
                            for (int i = tlist.size() - 1; i >= 0; i--) {
                                HistoricTaskInstanceModel model = tlist.get(i);
                                if (model.getEndTime() != null && model.getId().equals(opinion.getTaskId())) {// 找到收回前的上一个任务
                                    map.put("editable", true);
                                    addableMap.put("addable", false);
                                    break;
                                }
                            }
                        }
                    }
                    resList.add(map);
                }
                /**
                 * 当前意见框,当前人员可以新增意见时，要判断当前人员是否有在该意见框签意见的权限
                 */
                Boolean addableTemp = (Boolean)addableMap.get("addable");
                if (addableTemp) {
                    addableMap.put("addable", false);
                    ItemOpinionFrameBind bind = itemOpinionFrameBindService.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOpinionFrameMark(itemId, task.getProcessDefinitionId(), taskDefinitionKey, opinionFrameMark);
                    if (null != bind) {
                        // 是否必填意见，与addable一起判定，都为true时提示必填。
                        addableMap.put("signOpinion", bind.isSignOpinion());
                        List<String> roleIds = bind.getRoleIds();
                        if (roleIds.isEmpty()) {
                            addableMap.put("addable", true);
                        } else {
                            for (String roleId : roleIds) {
                                Boolean hasRole = false;
                                /**
                                 * 处理todo时，当前任务为委托产生时的情况-开始
                                 */
                                if (itembox.equalsIgnoreCase(ItemBoxTypeEnum.TODO.getValue())) {
                                    /*boolean isEntrust = entrustDetailService.haveEntrustDetailByTaskId(taskId);
                                    if (isEntrust) {
                                        EntrustDetail entrustDetail = entrustDetailService.findByTaskId(taskId);
                                        String ownerId = entrustDetail.getOwnerId();
                                        *//**
                                            * 把当前人换为委托改任务的人，委托人有意见签写意见，当前人就有签写意见的权限
                                            *//*
                                              hasRole = positionRoleApi.hasRole(tenantId, roleId, ownerId);
                                              if (hasRole) {
                                               addableMap.put("addable", true);
                                               continue;
                                              }
                                              } else {*/
                                    hasRole = personRoleApi.hasRole(tenantId, roleId, personId).getData();
                                    if (hasRole) {
                                        addableMap.put("addable", true);
                                        continue;
                                    }
                                    // }
                                } else {
                                    hasRole = personRoleApi.hasRole(tenantId, roleId, personId).getData();
                                    if (hasRole) {
                                        addableMap.put("addable", true);
                                        continue;
                                    }
                                }
                            }
                        }
                    }
                }
                // 代录权限控制
                if (StringUtils.isNotBlank(taskId)) {
                    boolean hasRole = personRoleApi.hasRole(Y9LoginUserHolder.getTenantId(), "itemAdmin", "", "代录意见角色", person.getPersonId()).getData();
                    if (hasRole) {
                        // 没有意见框编辑权限时，增加代录权限
                        Boolean addable = (Boolean)addableMap.get("addable");
                        if (!addable) {
                            addableMap.put("addAgent", true);
                        }
                    }
                }
            } else if (itembox.equalsIgnoreCase(ItemBoxTypeEnum.DONE.getValue()) || itembox.equalsIgnoreCase(ItemBoxTypeEnum.DOING.getValue())) {
                addableMap.put("addable", false);
                for (Opinion opinion : list) {
                    Map<String, Object> map = new HashMap<String, Object>(16);
                    opinion.setContent(CommentUtil.replaceEnter2Br(opinion.getContent()));
                    if (!opinion.getCreateDate().equals(opinion.getModifyDate())) {
                        map.put("isEdit", true);
                    }
                    map.put("date", sdf2.format(sdf.parse(opinion.getCreateDate())));
                    opinion.setModifyDate(sdf1.format(sdf.parse(opinion.getModifyDate())));
                    opinion.setCreateDate(sdf1.format(sdf.parse(opinion.getCreateDate())));
                    OpinionModel opinionModel = new OpinionModel();
                    Y9BeanUtil.copyProperties(opinion, opinionModel);
                    if (StringUtils.isNotBlank(opinion.getPositionId()) && StringUtils.isBlank(opinion.getPositionName())) {
                        Position position = positionManager.getPosition(tenantId, opinion.getPositionId()).getData();
                        opinionModel.setPositionName(position != null ? position.getName() : "");
                    }
                    PersonExt personExt = personApi.getPersonExtByPersonId(tenantId, opinionModel.getUserId()).getData();
                    opinionModel.setSign(personExt != null ? personExt.getSign() : null);
                    map.put("opinion", opinionModel);
                    map.put("editable", false);
                    resList.add(map);
                }
            } else if (itembox.equalsIgnoreCase(ItemBoxTypeEnum.YUEJIAN.getValue())) {
                // 是否已办结
                boolean isEnd = false;
                try {
                    ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
                    // 办结件，阅件不可填写意见
                    if (processParam != null) {
                        HistoricProcessInstanceModel historicProcessInstanceModel = historicProcessManager.getById(tenantId, processParam.getProcessInstanceId());
                        boolean b = historicProcessInstanceModel == null || (historicProcessInstanceModel != null && historicProcessInstanceModel.getEndTime() != null);
                        if (b) {
                            addableMap.put("addable", false);
                            isEnd = true;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                for (Opinion opinion : list) {
                    Map<String, Object> map = new HashMap<String, Object>(16);
                    opinion.setContent(CommentUtil.replaceEnter2Br(opinion.getContent()));
                    if (!opinion.getCreateDate().equals(opinion.getModifyDate())) {
                        map.put("isEdit", true);
                    }
                    map.put("date", sdf2.format(sdf.parse(opinion.getCreateDate())));
                    opinion.setModifyDate(sdf1.format(sdf.parse(opinion.getModifyDate())));
                    opinion.setCreateDate(sdf1.format(sdf.parse(opinion.getCreateDate())));
                    OpinionModel opinionModel = new OpinionModel();
                    Y9BeanUtil.copyProperties(opinion, opinionModel);
                    if (StringUtils.isNotBlank(opinion.getPositionId()) && StringUtils.isBlank(opinion.getPositionName())) {
                        Position position = positionManager.getPosition(tenantId, opinion.getPositionId()).getData();
                        opinionModel.setPositionName(position != null ? position.getName() : "");
                    }
                    PersonExt personExt = personApi.getPersonExtByPersonId(tenantId, opinionModel.getUserId()).getData();
                    opinionModel.setSign(personExt != null ? personExt.getSign() : null);
                    map.put("opinion", opinionModel);
                    map.put("editable", false);
                    if (personId.equals(opinion.getUserId()) && !isEnd) {
                        map.put("editable", true);
                        addableMap.put("addable", false);
                    }
                    resList.add(map);
                }
                /**
                 * 当前意见框,当前人员可以新增意见时，要判断当前人员是否有在该意见框签意见的权限
                 */
                Boolean addableTemp = (Boolean)addableMap.get("addable");
                if (addableTemp) {
                    addableMap.put("addable", false);
                    TaskModel task = taskManager.findById(tenantId, taskId);
                    ItemOpinionFrameBind bind = itemOpinionFrameBindService.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOpinionFrameMark(itemId, task.getProcessDefinitionId(), taskDefinitionKey, opinionFrameMark);
                    if (null != bind) {
                        List<String> roleIds = bind.getRoleIds();
                        if (roleIds.isEmpty()) {
                            addableMap.put("addable", true);
                        } else {
                            for (String roleId : roleIds) {
                                Boolean hasRole = personRoleApi.hasRole(tenantId, roleId, personId).getData();
                                if (hasRole) {
                                    addableMap.put("addable", true);
                                    continue;
                                }
                            }
                        }
                    }
                }
            }
            resList.add(addableMap);
        } catch (

        ParseException e) {
            e.printStackTrace();
        }
        return resList;
    }

    @Override
    @Transactional(readOnly = false)
    public void save(List<Opinion> entities) {
        opinionRepository.saveAll(entities);
    }

    @Override
    @Transactional(readOnly = false)
    public void save(Opinion entity) {
        opinionRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = false)
    public Opinion saveOrUpdate(Opinion entity) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId();
        Position position = Y9LoginUserHolder.getPosition();
        String positionId = Y9LoginUserHolder.getPositionId();
        String userName = person.getName();
        String personId = person.getPersonId();
        String id = entity.getId();
        if (StringUtils.isBlank(id)) {
            Opinion o = new Opinion();
            o.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            o.setUserId(personId);
            o.setUserName(userName);
            o.setDeptId(position.getParentId());
            OrgUnit orgUnit = orgUnitManager.getOrgUnit(tenantId, position.getParentId()).getData();
            o.setDeptName(orgUnit.getName());
            o.setProcessSerialNumber(entity.getProcessSerialNumber());
            o.setProcessInstanceId(entity.getProcessInstanceId());
            o.setTaskId(entity.getTaskId());
            o.setOpinionFrameMark(entity.getOpinionFrameMark());
            o.setTenantId(StringUtils.isNotBlank(entity.getTenantId()) ? entity.getTenantId() : tenantId);
            o.setContent(entity.getContent());
            o.setCreateDate(sdf.format(new Date()));
            o.setModifyDate(sdf.format(new Date()));
            o.setPositionId(positionId);
            o.setPositionName(position.getName());
            if (StringUtils.isNotBlank(entity.getTaskId())) {
                try {
                    List<ProcessTrack> list = processTrackService.findByTaskIdAndEndTimeIsNull(entity.getTaskId());
                    // 处理恢复待办后,填写意见错位问题,意见显示在自定义历程上
                    if (list.size() > 0) {
                        o.setProcessTrackId(list.get(0).getId());
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                /*try {
                    HistoricTaskInstanceModel historicTaskInstanceModel = historicTaskManager.getById(tenantId, entity.getTaskId());
                    EntrustDetail entrustDetail = entrustDetailService.findByTaskId(historicTaskInstanceModel.getId());
                    if (entrustDetail != null) {
                        if (historicTaskInstanceModel.getAssignee().contains(positionId)) {
                            String idTemp = entrustDetail.getOwnerId();
                            Person p = personManager.getPerson(tenantId, idTemp);
                            if (isAgent != 1) {
                                o.setUserName(userName + "(" + p.getName() + "委托)");
                            } else {
                                o.setAgentUserName(userName + "(" + p.getName() + "委托)");
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            }
            opinionRepository.save(o);
            asyncHandleService.sendMsgRemind(tenantId, positionId, entity.getProcessSerialNumber(), entity.getContent());
            return o;
        }
        Opinion opinion = opinionRepository.findById(id).orElse(null);
        Opinion oldOpinion = new Opinion();
        Y9BeanUtil.copyProperties(opinion, oldOpinion);
        opinion.setUserId(person.getPersonId());
        opinion.setUserName(userName);
        opinion.setTaskId(entity.getTaskId());
        opinion.setModifyDate(sdf.format(new Date()));
        opinion.setContent(entity.getContent());
        opinion.setProcessInstanceId(entity.getProcessInstanceId());
        opinion.setTenantId(StringUtils.isNotBlank(entity.getTenantId()) ? entity.getTenantId() : tenantId);
        OrgUnit orgUnit0 = orgUnitManager.getOrgUnit(tenantId, position.getParentId()).getData();
        opinion.setDeptId(position.getParentId());
        opinion.setDeptName(orgUnit0.getName());
        opinion.setPositionId(positionId);
        opinion.setPositionName(position.getName());
        /*if (StringUtils.isNotBlank(entity.getTaskId())) {
            try {
                HistoricTaskInstanceModel historicTaskInstanceModel = historicTaskManager.getById(tenantId, entity.getTaskId());
                EntrustDetail entrustDetail = entrustDetailService.findByTaskId(historicTaskInstanceModel.getId());
                if (entrustDetail != null) {
                    if (historicTaskInstanceModel.getAssignee().contains(positionId)) {
                        String idTemp = entrustDetail.getOwnerId();
                        Person p = personManager.getPerson(tenantId, idTemp);
                        if (isAgent != 1) {
                            opinion.setUserName(userName + "(" + p.getName() + "委托)");
                        } else {
                            opinion.setAgentUserName(userName + "(" + p.getName() + "委托)");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
        opinionRepository.save(opinion);
        asyncHandleService.sendMsgRemind(tenantId, positionId, entity.getProcessSerialNumber(), entity.getContent());
        // 修改意见保存历史记录
        asyncHandleService.saveOpinionHistory(Y9LoginUserHolder.getTenantId(), oldOpinion, "1");
        return opinion;
    }

    @Override
    @Transactional(readOnly = false)
    public void update(String processSerialNumber, String processInstanceId, String taskId) {
        opinionRepository.update(processInstanceId, taskId, processSerialNumber);
    }

}
