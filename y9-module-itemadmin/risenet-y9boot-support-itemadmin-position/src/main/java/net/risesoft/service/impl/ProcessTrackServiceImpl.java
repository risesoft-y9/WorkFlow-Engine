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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.api.processadmin.HistoricVariableApi;
import net.risesoft.api.processadmin.IdentityApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.entity.Opinion;
import net.risesoft.entity.ProcessParam;
import net.risesoft.entity.ProcessTrack;
import net.risesoft.entity.TransactionHistoryWord;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.Position;
import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.model.processadmin.HistoricVariableInstanceModel;
import net.risesoft.model.processadmin.IdentityLinkModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.nosql.elastic.entity.OfficeDoneInfo;
import net.risesoft.repository.jpa.OpinionRepository;
import net.risesoft.repository.jpa.ProcessTrackRepository;
import net.risesoft.service.OfficeDoneInfoService;
import net.risesoft.service.ProcessParamService;
import net.risesoft.service.ProcessTrackService;
import net.risesoft.service.TransactionHistoryWordService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service(value = "processTrackService")
public class ProcessTrackServiceImpl implements ProcessTrackService {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private ProcessTrackRepository processTrackRepository;

    @Autowired
    private OpinionRepository opinionRepository;

    @Autowired
    private TransactionHistoryWordService transactionHistoryWordService;

    @Autowired
    private HistoricVariableApi historicVariableManager;

    @Autowired
    private PositionApi positionManager;

    @Autowired
    private HistoricTaskApi historicTaskManager;

    @Autowired
    private TaskApi taskManager;

    @Autowired
    private IdentityApi identityManager;

    @Autowired
    private OfficeDoneInfoService officeDoneInfoService;

    @Autowired
    private ProcessParamService processParamService;

    @Override
    @Transactional(readOnly = false)
    public void deleteById(String id) {
        processTrackRepository.deleteById(id);
    }

    @Override
    public List<ProcessTrack> findByTaskId(String taskId) {
        return processTrackRepository.findByTaskId(taskId);
    }

    @Override
    public List<ProcessTrack> findByTaskIdAndEndTimeIsNull(String taskId) {
        return processTrackRepository.findByTaskIdAndEndTimeIsNull(taskId, "");
    }

    @Override
    public List<ProcessTrack> findByTaskIdAsc(String taskId) {
        return processTrackRepository.findByTaskIdAsc(taskId);
    }

    @Override
    public ProcessTrack findOne(String id) {
        return processTrackRepository.findById(id).orElse(null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> getListMap(String processInstanceId) {
        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        // 由于需要获取call Activity类型的节点，将查询方法改为如下
        List<HistoricTaskInstanceModel> results = historicTaskManager.getByProcessInstanceId(tenantId, processInstanceId, "");
        String year = "";
        if (results == null || results.size() == 0) {
            OfficeDoneInfo officeDoneInfoModel = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
            if (officeDoneInfoModel != null && officeDoneInfoModel.getProcessInstanceId() != null) {
                year = officeDoneInfoModel.getStartTime().substring(0, 4);
                results = historicTaskManager.getByProcessInstanceId(tenantId, processInstanceId, year);
            } else {
                ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
                year = processParam != null ? processParam.getCreateTime().substring(0, 4) : "";
                results = historicTaskManager.getByProcessInstanceId(tenantId, processInstanceId, year);
            }
        }
        for (int i = 0; i < results.size(); i++) {
            HistoricTaskInstanceModel hai = results.get(i);
            if (hai == null) {
                continue;
            }
            String id = hai.getId(), taskId = hai.getId();
            Map<String, Object> map = new HashMap<String, Object>(16);
            map.put("id", id);
            // 收件人
            map.put("assignee", "");
            // 任务名称
            map.put("name", hai.getName());
            // 描述
            map.put("description", "");
            // 意见
            map.put("opinion", "");
            // 历史正文版本
            TransactionHistoryWord hword = transactionHistoryWordService.getTransactionHistoryWordByTaskId(taskId);
            if (null != hword) {
                map.put("historyVersion", hword.getVersion());
            }
            map.put("taskId", taskId);
            // 收件人
            String assignee = hai.getAssignee();
            if (StringUtils.isNotBlank(assignee)) {
                Position employee = positionManager.getPosition(Y9LoginUserHolder.getTenantId(), assignee).getData();
                if (employee != null) {
                    map.put("assigneeId", assignee);
                    // 承办人id,用于数据中心保存
                    map.put("undertakerId", assignee);
                    String ownerId = hai.getOwner();
                    String employeeName = employee.getName();
                    // 恢复待办，如不是办结人恢复，Owner有值，需显示Owner
                    if (StringUtils.isNotBlank(ownerId)) {
                        Position ownerUser = positionManager.getPosition(Y9LoginUserHolder.getTenantId(), ownerId).getData();
                        employeeName = ownerUser.getName();
                        map.put("undertakerId", ownerUser.getId());
                    }
                    /*EntrustDetail entrustDetail = entrustDetailService.findByTaskId(taskId);
                    // 出差委托标识
                    if (entrustDetail != null) {
                        String owner4Entrust = entrustDetail.getOwnerId();
                        Position owner = positionManager.getPosition(tenantId, owner4Entrust);
                        employeeName = employeeName + "(" + owner.getName() + "委托)";
                    }*/
                    HistoricVariableInstanceModel zhuBan = null;
                    try {
                        zhuBan = historicVariableManager.getByTaskIdAndVariableName(tenantId, taskId, SysVariables.PARALLELSPONSOR, year);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (zhuBan != null) {
                        map.put("assignee", employeeName + "(主办)");
                    } else {
                        map.put("assignee", employeeName);
                    }
                }
            } else {// 处理单实例未签收的办理人显示
                List<IdentityLinkModel> iList = identityManager.getIdentityLinksForTask(tenantId, taskId);
                if (!iList.isEmpty()) {
                    StringBuffer assignees = new StringBuffer();
                    int j = 0;
                    for (IdentityLinkModel identityLink : iList) {
                        String assigneeId = identityLink.getUserId();
                        Position ownerUser = positionManager.getPosition(Y9LoginUserHolder.getTenantId(), assigneeId).getData();
                        if (j < 5) {
                            assignees = Y9Util.genCustomStr(assignees, ownerUser.getName(), "、");
                        } else {
                            assignees.append("等，共" + iList.size() + "人");
                            break;
                        }
                        j++;
                    }
                    map.put("assignee", assignees.toString());
                }
            }
            Integer newToDo = 0;
            if (hai.getEndTime() == null) {
                TaskModel taskModel = taskManager.findById(tenantId, taskId);
                newToDo = StringUtils.isBlank(taskModel.getFormKey()) ? 1 : (Integer.parseInt(taskModel.getFormKey()));
            }
            map.put("newToDo", newToDo);

            // 是否被强制办结任务标识
            map.put("endFlag", StringUtils.isBlank(hai.getTenantId()) ? "" : hai.getTenantId());

            // 描述
            String description = hai.getDeleteReason();
            if (null != description && !(description.equals("MI_END"))) {
                map.put("description", description);
                if (description.contains("Delete MI execution")) {
                    HistoricVariableInstanceModel taskSenderModel = historicVariableManager.getByTaskIdAndVariableName(tenantId, hai.getId(), SysVariables.TASKSENDER, year);
                    if (taskSenderModel != null) {
                        String taskSender = taskSenderModel.getValue() == null ? "" : (String)taskSenderModel.getValue();
                        map.put("description", "该任务由" + taskSender + "删除");
                        // 并行退回以减签的方式退回，需获取退回原因,替换减签的描述
                        HistoricVariableInstanceModel rollBackReason = historicVariableManager.getByTaskIdAndVariableName(tenantId, hai.getId(), "rollBackReason", year);
                        if (rollBackReason != null) {
                            map.put("description", rollBackReason.getValue());
                        }
                        // 发送办结协办任务使用减签方式办结，需要设置description为空
                        if (StringUtils.isNotBlank(hai.getTenantId())) {
                            map.put("description", "");
                        }
                    }
                }
            }
            // 意见
            List<Opinion> opinion = opinionRepository.findByTaskIdAndPositionIdAndProcessTrackIdIsNull(taskId, StringUtils.isBlank(assignee) ? "" : assignee);
            map.put("opinion", opinion.size() > 0 ? opinion.get(0).getContent() : "");
            map.put("startTime", hai.getStartTime() == null ? "" : sdf.format(hai.getStartTime()));
            try {
                map.put("startTimes", hai.getStartTime() == null ? 0 : sdf.parse(sdf.format(hai.getStartTime())).getTime());
            } catch (Exception e2) {
                e2.printStackTrace();
            }

            /**
             * 手动设置流程办结的时候, 流程最后一个任务结束的时间就是第一个手动设置的流程跟踪的时间
             */
            List<ProcessTrack> ptList = new ArrayList<ProcessTrack>();
            Date endTime1 = hai.getEndTime();
            ptList = this.findByTaskId(taskId);
            if (ptList.size() >= 1) {
                map.put("endTime", endTime1 == null ? "" : sdf.format(endTime1));
                try {
                    map.put("endTimes", endTime1 == null ? 0 : sdf.parse(sdf.format(endTime1)).getTime());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                map.put("time", longTime(hai.getStartTime(), endTime1));
            } else {
                map.put("endTime", endTime1 == null ? "" : sdf.format(endTime1));
                try {
                    map.put("endTimes", endTime1 == null ? 0 : sdf.parse(sdf.format(endTime1)).getTime());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                map.put("time", longTime(hai.getStartTime(), endTime1));
            }
            items.add(map);

            for (ProcessTrack pt : ptList) {
                Map<String, Object> mapTemp = new HashMap<String, Object>(16);
                mapTemp.put("id", id);
                mapTemp.put("assignee", pt.getReceiverName() == null ? "" : pt.getReceiverName());
                mapTemp.put("name", pt.getTaskDefName() == null ? "" : pt.getTaskDefName());
                mapTemp.put("description", pt.getDescribed() == null ? "" : pt.getDescribed());
                List<Opinion> opinionProcessTrack = opinionRepository.findByTaskIdAndProcessTrackIdOrderByCreateDateDesc(taskId, pt.getId());
                mapTemp.put("opinion", opinionProcessTrack.isEmpty() ? "" : opinionProcessTrack.get(0).getContent());
                mapTemp.put("historyVersion", pt.getDocVersion() == null ? "" : pt.getDocVersion());
                mapTemp.put("taskId", taskId);
                mapTemp.put("isChaoSong", pt.getIsChaoSong() == null ? "" : pt.getIsChaoSong());
                mapTemp.put("startTime", pt.getStartTime() == null ? "" : pt.getStartTime());
                mapTemp.put("endTime", pt.getEndTime() == null ? "" : pt.getEndTime());
                try {
                    mapTemp.put("startTimes", sdf.parse(pt.getStartTime()).getTime());
                    mapTemp.put("endTimes", StringUtils.isBlank(pt.getEndTime()) ? 0 : sdf.parse(pt.getEndTime()).getTime());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                try {
                    if (StringUtils.isBlank(pt.getEndTime())) {
                        mapTemp.put("time", "");
                    } else {
                        mapTemp.put("time", longTime(sdf.parse(pt.getStartTime()), sdf.parse(pt.getEndTime())));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                items.add(mapTemp);
            }
        }
        Collections.sort(items, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                try {
                    long startTime1 = (long)o1.get("startTimes");
                    long startTime2 = (long)o2.get("startTimes");
                    if (startTime1 > startTime2) {
                        return 1;
                    } else if (startTime1 == startTime2) {
                        long endTime1 = (long)o1.get("endTimes");
                        long endTime2 = (long)o2.get("endTimes");
                        if (endTime1 == 0) {
                            return 1;
                        }
                        if (endTime2 == 0) {
                            return -1;
                        }
                        if (endTime1 > endTime2) {// 开始时间相等的才排序
                            return 1;
                        }
                        if (endTime1 == endTime2) {// 开始时间相等的才排序
                            return 0;
                        }
                        return -1;
                    } else {
                        return -1;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return -1;
            }
        });
        String name = (String)items.get(items.size() - 1).get("name");
        String seq = "串行办理";
        if (seq.equals(name)) {
            HistoricVariableInstanceModel users = historicVariableManager.getByProcessInstanceIdAndVariableName(tenantId, processInstanceId, SysVariables.USERS, "");
            List<String> list = users != null ? (ArrayList<String>)users.getValue() : new ArrayList<String>();
            boolean start = false;
            String assigneeId = (String)items.get(items.size() - 1).get("assigneeId");
            for (Object obj : list) {
                String user = obj.toString();
                if (StringUtils.isNotBlank(assigneeId)) {
                    if (user.contains(assigneeId)) {
                        start = true;
                        continue;
                    }
                    if (start) {
                        Map<String, Object> map = new HashMap<String, Object>(16);
                        Position employee = positionManager.getPosition(Y9LoginUserHolder.getTenantId(), user).getData();
                        map.put("assignee", employee.getName());
                        map.put("name", "串行办理");
                        map.put("endTime", "");
                        map.put("description", "");
                        map.put("opinion", "");
                        map.put("time", "");
                        map.put("startTime", "未开始");
                        items.add(map);
                    }
                }
            }
        }
        return items;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> getListMap4Simple(String processInstanceId) {
        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<HistoricTaskInstanceModel> results = historicTaskManager.getByProcessInstanceId(tenantId, processInstanceId, "");
        String year = "";
        if (results == null || results.size() == 0) {
            OfficeDoneInfo officeDoneInfoModel = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
            if (officeDoneInfoModel != null && officeDoneInfoModel.getProcessInstanceId() != null) {
                year = officeDoneInfoModel.getStartTime().substring(0, 4);
                results = historicTaskManager.getByProcessInstanceId(tenantId, processInstanceId, year);
            } else {
                ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
                year = processParam != null ? processParam.getCreateTime().substring(0, 4) : "";
                results = historicTaskManager.getByProcessInstanceId(tenantId, processInstanceId, year);
            }
        }
        for (int i = 0; i < results.size(); i++) {
            HistoricTaskInstanceModel hai = results.get(i);
            if (hai == null) {
                continue;
            }
            String taskId = hai.getId();
            Map<String, Object> map = new HashMap<String, Object>(16);
            // 收件人
            map.put("assignee", "");
            // 任务名称
            map.put("name", hai.getName());
            // 收件人
            String assignee = hai.getAssignee();
            if (StringUtils.isNotBlank(assignee)) {
                Position employee = positionManager.getPosition(Y9LoginUserHolder.getTenantId(), assignee).getData();
                if (employee != null) {
                    String ownerId = hai.getOwner();
                    String employeeName = employee.getName();
                    // 恢复待办，如不是办结人恢复，Owner有值，需显示Owner
                    if (StringUtils.isNotBlank(ownerId)) {
                        Position ownerUser = positionManager.getPosition(Y9LoginUserHolder.getTenantId(), ownerId).getData();
                        employeeName = ownerUser.getName();
                    }
                    HistoricVariableInstanceModel zhuBan = historicVariableManager.getByTaskIdAndVariableName(tenantId, taskId, SysVariables.PARALLELSPONSOR, year);
                    if (zhuBan != null) {
                        map.put("assignee", employeeName + "(主办)");
                    } else {
                        map.put("assignee", employeeName);
                    }
                    map.put("assigneeId", assignee);
                }
            } else {// 处理单实例未签收的办理人显示
                List<IdentityLinkModel> iList = identityManager.getIdentityLinksForTask(tenantId, taskId);
                if (!iList.isEmpty()) {
                    StringBuffer assignees = new StringBuffer();
                    int j = 0;
                    for (IdentityLinkModel identityLink : iList) {
                        String assigneeId = identityLink.getUserId();
                        Position ownerUser = positionManager.getPosition(Y9LoginUserHolder.getTenantId(), assigneeId).getData();
                        if (j < 5) {
                            assignees = Y9Util.genCustomStr(assignees, ownerUser.getName(), "、");
                        } else {
                            assignees.append("等，共" + iList.size() + "人");
                            break;
                        }
                        j++;
                    }
                    map.put("assignee", assignees.toString());
                }
            }
            map.put("startTime", hai.getStartTime() == null ? "" : sdf.format(hai.getStartTime()));
            // 是否被强制办结任务标识
            map.put("endFlag", StringUtils.isBlank(hai.getTenantId()) ? "" : hai.getTenantId());
            /**
             * 手动设置流程办结的时候, 流程最后一个任务结束的时间就是第一个手动设置的流程跟踪的时间
             */
            List<ProcessTrack> ptList = new ArrayList<ProcessTrack>();
            Date endTime1 = hai.getEndTime();
            ptList = this.findByTaskId(taskId);
            if (ptList.size() >= 1) {
                map.put("endTime", endTime1 == null ? "" : sdf.format(endTime1));
                map.put("time", longTime(hai.getStartTime(), endTime1));
            } else {
                map.put("endTime", endTime1 == null ? "" : sdf.format(endTime1));
                map.put("time", longTime(hai.getStartTime(), endTime1));
            }
            items.add(map);

            for (ProcessTrack pt : ptList) {
                Map<String, Object> mapTemp = new HashMap<String, Object>(16);
                mapTemp.put("assignee", pt.getReceiverName() == null ? "" : pt.getReceiverName());
                mapTemp.put("name", pt.getTaskDefName() == null ? "" : pt.getTaskDefName());
                mapTemp.put("startTime", pt.getStartTime() == null ? "" : pt.getStartTime());
                mapTemp.put("endTime", pt.getEndTime() == null ? "" : pt.getEndTime());
                try {
                    if (StringUtils.isBlank(pt.getEndTime())) {
                        mapTemp.put("time", "");
                    } else {
                        mapTemp.put("time", longTime(sdf.parse(pt.getStartTime()), sdf.parse(pt.getEndTime())));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                items.add(mapTemp);
            }
        }
        Collections.sort(items, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                try {
                    Date startTime1 = sdf.parse((String)o1.get("startTime"));
                    Date startTime2 = sdf.parse((String)o2.get("startTime"));

                    if (startTime1.getTime() > startTime2.getTime()) {
                        return 1;
                    } else if (startTime1.getTime() == startTime2.getTime()) {
                        Date date1 = ("").equals(o1.get("endTime")) ? new Date() : sdf.parse((String)o1.get("endTime"));
                        Date date2 = "".equals((o2.get("endTime"))) ? new Date() : sdf.parse((String)o2.get("endTime"));
                        if (date1.getTime() > date2.getTime()) {// 开始时间相等的才排序
                            return 1;
                        }
                        if (date1.getTime() == date2.getTime()) {
                            return 0;
                        }
                        return -1;
                    } else {
                        return -1;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return -1;
            }
        });
        String name = (String)items.get(items.size() - 1).get("name");
        String seq = "串行办理";
        if (name.equals(seq)) {
            HistoricVariableInstanceModel users = historicVariableManager.getByProcessInstanceIdAndVariableName(tenantId, processInstanceId, SysVariables.USERS, "");
            List<String> list = users != null ? (ArrayList<String>)users.getValue() : new ArrayList<String>();
            boolean start = false;
            String assigneeId = (String)items.get(items.size() - 1).get("assigneeId");
            for (Object obj : list) {
                String user = obj.toString();
                if (StringUtils.isNotBlank(assigneeId)) {
                    if (user.contains(assigneeId)) {
                        start = true;
                        continue;
                    }
                    if (start) {
                        Map<String, Object> map = new HashMap<String, Object>(16);
                        Position employee = positionManager.getPosition(Y9LoginUserHolder.getTenantId(), user).getData();
                        map.put("assignee", employee.getName());
                        map.put("name", "串行办理");
                        map.put("endTime", "");
                        map.put("time", "");
                        map.put("startTime", "未开始");
                        items.add(map);
                    }
                }
            }
        }
        return items;
    }

    private String longTime(Date startTime, Date endTime) {
        if (endTime == null) {
            return "";
        } else {
            Date d1 = endTime;
            Date d2 = startTime;
            long time = d1.getTime() - d2.getTime();
            time = time / 1000;
            int s = (int)(time % 60);
            int m = (int)(time / 60 % 60);
            int h = (int)(time / 3600 % 24);
            int d = (int)(time / 86400);
            String str = d + "天" + h + "小时" + m + "分" + s + "秒";
            return str;
        }
    }

    @Override
    @Transactional(readOnly = false)
    public ProcessTrack saveOrUpdate(ProcessTrack pt) {
        String id = pt.getId();
        if (StringUtils.isNotEmpty(id)) {
            ProcessTrack oldpt = processTrackRepository.findById(id).orElse(null);
            oldpt.setEndTime(sdf.format(new Date()));
            oldpt.setDescribed(pt.getDescribed());
            return processTrackRepository.save(oldpt);
        }
        ProcessTrack newpt = new ProcessTrack();
        newpt.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        newpt.setProcessInstanceId(pt.getProcessInstanceId());
        newpt.setTaskId(pt.getTaskId());
        newpt.setTaskDefName(pt.getTaskDefName());
        newpt.setSenderName(pt.getSenderName());
        newpt.setReceiverName(pt.getReceiverName());
        newpt.setTaskDefName(pt.getTaskDefName());
        newpt.setStartTime(pt.getStartTime());
        newpt.setEndTime(pt.getEndTime());
        newpt.setDescribed(pt.getDescribed());
        processTrackRepository.save(newpt);
        return newpt;
    }
}
