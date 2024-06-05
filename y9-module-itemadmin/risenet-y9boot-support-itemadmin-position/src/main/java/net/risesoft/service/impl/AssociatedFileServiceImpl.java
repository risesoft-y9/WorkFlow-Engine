package net.risesoft.service.impl;

import lombok.RequiredArgsConstructor;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.processadmin.HistoricProcessApi;
import net.risesoft.api.processadmin.IdentityApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.AssociatedFile;
import net.risesoft.entity.ProcessParam;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.Position;
import net.risesoft.model.processadmin.HistoricProcessInstanceModel;
import net.risesoft.model.processadmin.IdentityLinkModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.nosql.elastic.entity.OfficeDoneInfo;
import net.risesoft.repository.jpa.AssociatedFileRepository;
import net.risesoft.service.AssociatedFileService;
import net.risesoft.service.OfficeDoneInfoService;
import net.risesoft.service.ProcessParamService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class AssociatedFileServiceImpl implements AssociatedFileService {

    private final AssociatedFileRepository associatedFileRepository;

    private final HistoricProcessApi historicProcessManager;

    private final ProcessParamService processParamService;

    private final OfficeDoneInfoService officeDoneInfoService;

    private final TaskApi taskApi;

    private final PositionApi positionManager;

    private final IdentityApi identityManager;

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
            e.printStackTrace();
        }
        return 0;
    }

    @Transactional()
    @Override
    public boolean deleteAllAssociatedFile(String processSerialNumber, String delIds) {
        try {
            AssociatedFile associatedFile = associatedFileRepository.findByProcessSerialNumber(processSerialNumber);
            if (associatedFile != null && associatedFile.getId() != null) {
                String associatedId = associatedFile.getAssociatedId();
                String newAssociatedId = "";
                String[] associatedIds = associatedId.split(SysVariables.COMMA);
                String[] delAssociatedIds = delIds.split(SysVariables.COMMA);
                for (String id : associatedIds) {
                    Boolean isDel = false;
                    for (String delId : delAssociatedIds) {
                        if (id.equals(delId)) {
                            isDel = true;
                        }
                    }
                    if (!isDel) {
                        newAssociatedId = Y9Util.genCustomStr(newAssociatedId, id);
                    }
                }
                associatedFile.setCreateTime(new Date());
                associatedFile.setAssociatedId(newAssociatedId);
                associatedFileRepository.save(associatedFile);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Transactional()
    @Override
    public boolean deleteAssociatedFile(String processSerialNumber, String delId) {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private final List<String> getAssigneeIdsAndAssigneeNames(List<TaskModel> taskList) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9LoginUserHolder.getPositionId();
        String taskIds = "", assigneeIds = "", assigneeNames = "", itembox = ItemBoxTypeEnum.DOING.getValue(),
            taskId = "";
        List<String> list = new ArrayList<String>();
        int i = 0;
        if (taskList.size() > 0) {
            for (TaskModel task : taskList) {
                if (StringUtils.isEmpty(taskIds)) {
                    taskIds = task.getId();
                    String assignee = task.getAssignee();
                    if (StringUtils.isNotBlank(assignee)) {
                        assigneeIds = assignee;
                        Position personTemp = positionManager.get(tenantId, assignee).getData();
                        if (personTemp != null) {
                            assigneeNames = personTemp.getName();
                        }
                        i += 1;
                        if (assignee.contains(userId)) {
                            itembox = ItemBoxTypeEnum.TODO.getValue();
                            taskId = task.getId();
                        }
                    } else {// 处理单实例未签收的当前办理人显示
                        List<IdentityLinkModel> iList = identityManager.getIdentityLinksForTask(tenantId, task.getId());
                        if (!iList.isEmpty()) {
                            int j = 0;
                            for (IdentityLinkModel identityLink : iList) {
                                String assigneeId = identityLink.getUserId();
                                Position ownerUser =
                                    positionManager.get(Y9LoginUserHolder.getTenantId(), assigneeId).getData();
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
                            Position personTemp = positionManager.get(tenantId, assignee).getData();
                            if (personTemp != null) {
                                assigneeNames = Y9Util.genCustomStr(assigneeNames, personTemp.getName(), "、");
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

    @Override
    public Map<String, Object> getAssociatedFileAllList(String processSerialNumber) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, true);
        map.put("msg", "获取成功");
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
            AssociatedFile associatedFile = associatedFileRepository.findByProcessSerialNumber(processSerialNumber);
            if (associatedFile != null) {
                String associatedId = associatedFile.getAssociatedId();
                if (StringUtils.isNotBlank(associatedId)) {
                    String[] associatedIds = associatedId.split(SysVariables.COMMA);
                    for (String id : associatedIds) {
                        Map<String, Object> mapTemp = new HashMap<String, Object>(16);
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
                            mapTemp.put("itemName", hpim.getItemName());
                            mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber1);
                            mapTemp.put(SysVariables.DOCUMENTTITLE, documentTitle);
                            mapTemp.put("processInstanceId", processInstanceId);
                            mapTemp.put("processDefinitionId", processDefinitionId);
                            mapTemp.put("processDefinitionKey", hpim.getProcessDefinitionKey());
                            mapTemp.put("startTime", startTime);
                            mapTemp.put("endTime",
                                StringUtils.isBlank(hpim.getEndTime()) ? "--" : hpim.getEndTime().substring(0, 16));
                            mapTemp.put("taskDefinitionKey", "");
                            mapTemp.put("taskAssignee", completer);
                            mapTemp.put("creatUserName", hpim.getCreatUserName());
                            mapTemp.put("itemId", hpim.getItemId());
                            mapTemp.put("level", level == null ? "" : level);
                            mapTemp.put("number", number == null ? "" : number);
                            mapTemp.put("itembox", ItemBoxTypeEnum.DONE.getValue());
                            // 排序用
                            mapTemp.put("startTimes", hpim.getStartTime());
                            if (StringUtils.isBlank(hpim.getEndTime())) {
                                List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId);
                                List<String> listTemp = getAssigneeIdsAndAssigneeNames(taskList);
                                String taskIds = listTemp.get(0), assigneeIds = listTemp.get(1),
                                    assigneeNames = listTemp.get(2);
                                mapTemp.put("taskDefinitionKey", taskList.get(0).getTaskDefinitionKey());
                                mapTemp.put("taskId", listTemp.get(3).equals(ItemBoxTypeEnum.DOING.getValue()) ? taskIds
                                    : listTemp.get(4));
                                mapTemp.put("taskAssigneeId", assigneeIds);
                                mapTemp.put("taskAssignee", assigneeNames);
                                mapTemp.put("itembox", listTemp.get(3));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        items.add(mapTemp);
                    }
                }
                Collections.sort(items, new Comparator<Map<String, Object>>() {
                    @Override
                    public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            Date startTime1 = sdf.parse((String)o1.get("startTimes"));
                            Date startTime2 = sdf.parse((String)o2.get("startTimes"));
                            if (startTime1.getTime() < startTime2.getTime()) {
                                return 1;
                            } else if (startTime1.getTime() == startTime2.getTime()) {
                                return 0;
                            } else {
                                return -1;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return -1;
                    }
                });
            }
            map.put("rows", items);
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "关联文件列表获取成功");
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "获取失败");
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public Map<String, Object> getAssociatedFileList(String processSerialNumber) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, true);
        map.put("msg", "获取成功");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
            AssociatedFile associatedFile = associatedFileRepository.findByProcessSerialNumber(processSerialNumber);
            if (associatedFile != null) {
                String associatedId = associatedFile.getAssociatedId();
                if (StringUtils.isNotBlank(associatedId)) {
                    String[] associatedIds = associatedId.split(SysVariables.COMMA);
                    for (String id : associatedIds) {
                        Map<String, Object> mapTemp = new HashMap<String, Object>(16);
                        try {
                            HistoricProcessInstanceModel hpim = historicProcessManager.getById(tenantId, id);
                            ProcessParam processParam = processParamService.findByProcessInstanceId(id);
                            String startTime = "";
                            String endTime1 = "";
                            String endTime = "";
                            if (hpim != null) {
                                startTime = sdf.format(hpim.getStartTime());
                                endTime1 = sdf1.format(hpim.getEndTime());
                                endTime = sdf.format(hpim.getEndTime());
                            } else {
                                OfficeDoneInfo officeDoneInfoModel = officeDoneInfoService.findByProcessInstanceId(id);
                                if (officeDoneInfoModel != null) {
                                    startTime = sdf.format(sdf1.parse(officeDoneInfoModel.getStartTime()));
                                    endTime1 = officeDoneInfoModel.getEndTime();
                                    endTime = sdf.format(sdf1.parse(officeDoneInfoModel.getEndTime()));
                                } else {
                                    String year = processParam.getCreateTime().substring(0, 4);
                                    HistoricProcessInstanceModel hpi =
                                        historicProcessManager.getByIdAndYear(tenantId, id, year);
                                    startTime = sdf.format(hpi.getStartTime());
                                    endTime1 = sdf1.format(hpi.getEndTime());
                                    endTime = sdf.format(hpi.getEndTime());
                                }
                            }
                            String processSerialNumber1 = processParam.getProcessSerialNumber();
                            String itemId = processParam.getItemId();
                            String itemName = processParam.getItemName();
                            String documentTitle = processParam.getTitle();
                            String user4Complete =
                                StringUtils.isBlank(processParam.getCompleter()) ? "无" : processParam.getCompleter();
                            mapTemp.put("itemName", itemName);
                            mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber1);
                            mapTemp.put(SysVariables.DOCUMENTTITLE, documentTitle);
                            mapTemp.put("processInstanceId", id);
                            mapTemp.put("startTime", startTime);
                            mapTemp.put("endTime", endTime);
                            mapTemp.put("endTimes", endTime1);
                            mapTemp.put("user4Complete", user4Complete);
                            mapTemp.put("itemId", itemId);
                            mapTemp.put("level", processParam.getCustomLevel());
                            mapTemp.put("number", processParam.getCustomNumber());
                            items.add(mapTemp);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                Collections.sort(items, new Comparator<Map<String, Object>>() {
                    @Override
                    public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            Date endTimes1 = sdf.parse((String)o1.get("endTimes"));
                            Date endTimes2 = sdf.parse((String)o2.get("endTimes"));
                            if (endTimes1.getTime() < endTimes2.getTime()) {
                                return 1;
                            } else if (endTimes1.getTime() == endTimes2.getTime()) {
                                return 0;
                            } else {
                                return -1;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }
                });
            }
            map.put("rows", items);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "获取失败");
            e.printStackTrace();
        }
        return map;
    }

    @Transactional()
    @Override
    public boolean saveAssociatedFile(String processSerialNumber, String processInstanceIds) {
        try {
            AssociatedFile associatedFile = associatedFileRepository.findByProcessSerialNumber(processSerialNumber);
            if (associatedFile == null || associatedFile.getId() == null) {
                associatedFile = new AssociatedFile();
                associatedFile.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                associatedFile.setCreateTime(new Date());
                associatedFile.setAssociatedId(processInstanceIds);
                associatedFile.setProcessSerialNumber(processSerialNumber);
                associatedFile.setUserId(Y9LoginUserHolder.getPositionId());
                associatedFile.setUserName(Y9LoginUserHolder.getPosition().getName());
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
                associatedFile.setUserId(Y9LoginUserHolder.getPositionId());
                associatedFile.setUserName(Y9LoginUserHolder.getPosition().getName());
                associatedFile.setCreateTime(new Date());
                associatedFile.setAssociatedId(newAssociatedId);
            }
            associatedFileRepository.save(associatedFile);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
