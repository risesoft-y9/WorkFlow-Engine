package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.OfficeFollow;
import net.risesoft.entity.ProcessParam;
import net.risesoft.entity.RemindInstance;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.itemadmin.OfficeFollowModel;
import net.risesoft.model.platform.Position;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.nosql.elastic.entity.OfficeDoneInfo;
import net.risesoft.repository.jpa.OfficeFollowRepository;
import net.risesoft.service.OfficeDoneInfoService;
import net.risesoft.service.OfficeFollowService;
import net.risesoft.service.ProcessParamService;
import net.risesoft.service.RemindInstanceService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9.util.Y9Util;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service(value = "officeFollowService")
public class OfficeFollowServiceImpl implements OfficeFollowService {

    @Autowired
    private OfficeFollowRepository officeFollowRepository;

    @Autowired
    private TaskApi taskManager;

    @Autowired
    private PositionApi positionManager;

    @Autowired
    private ProcessParamService processParamService;

    @Autowired
    private RemindInstanceService remindInstanceService;

    @Autowired
    private OfficeDoneInfoService officeDoneInfoService;

    @Override
    public int countByProcessInstanceId(String processInstanceId) {
        return officeFollowRepository.countByProcessInstanceIdAndUserId(processInstanceId,
            Y9LoginUserHolder.getPositionId());
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteByProcessInstanceId(String processInstanceId) {
        officeFollowRepository.deleteByProcessInstanceId(processInstanceId);
    }

    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> delOfficeFollow(String processInstanceIds) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, true);
        map.put("msg", "取消关注成功");
        try {
            String[] ids = processInstanceIds.split(",");
            for (String processInstanceId : ids) {
                officeFollowRepository.deleteByProcessInstanceId(processInstanceId, Y9LoginUserHolder.getPositionId());
            }
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "取消关注失败");
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 当并行的时候，会获取到多个task，为了并行时当前办理人显示多人，而不是显示多条记录，需要分开分别进行处理
     *
     * @return
     */
    private List<String> getAssigneeIdsAndAssigneeNames(List<TaskModel> taskList) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String positionId = Y9LoginUserHolder.getPositionId();
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
                        Position position = positionManager.get(tenantId, assignee).getData();
                        if (position != null) {
                            assigneeNames = position.getName();
                        }
                        i += 1;
                        if (assignee.contains(positionId)) {
                            itembox = ItemBoxTypeEnum.TODO.getValue();
                            taskId = task.getId();
                        }
                    }
                } else {
                    String assignee = task.getAssignee();
                    if (StringUtils.isNotBlank(assignee)) {
                        if (i < 5) {
                            assigneeIds = Y9Util.genCustomStr(assigneeIds, assignee, SysVariables.COMMA);
                            Position position = positionManager.get(tenantId, assignee).getData();
                            if (position != null) {
                                assigneeNames = Y9Util.genCustomStr(assigneeNames, position.getName(), "、");
                            }
                            i += 1;
                        }
                        if (assignee.contains(positionId)) {
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
        return officeFollowRepository.countByUserId(Y9LoginUserHolder.getPositionId());
    }

    @Override
    public Map<String, Object> getFollowListBySystemName(String systemName, String searchName, int page, int rows) {
        String positionId = Y9LoginUserHolder.getPositionId(), tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> resultMap = new HashMap<String, Object>(16);
        SimpleDateFormat sdf5 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<OfficeFollowModel> list = new ArrayList<OfficeFollowModel>();
        try {
            Pageable pageable = PageRequest.of(page - 1, rows, Sort.by(Sort.Direction.DESC, "startTime"));
            Page<OfficeFollow> followList = null;
            if (StringUtils.isBlank(searchName)) {
                followList = officeFollowRepository.findBySystemNameAndUserId(systemName, positionId, pageable);
            } else {
                searchName = "%" + searchName + "%";
                followList =
                    officeFollowRepository.findBySystemNameAndParamsLike(systemName, positionId, searchName, pageable);
            }
            for (OfficeFollow officeFollow : followList.getContent()) {
                try {
                    String processInstanceId = officeFollow.getProcessInstanceId();
                    OfficeDoneInfo officeDoneInfo = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
                    officeFollow.setStartTime(sdf5.format(sdf.parse(officeDoneInfo.getStartTime())));
                    officeFollow
                        .setMsgremind((officeDoneInfo.getMeeting() != null && officeDoneInfo.getMeeting().equals("1"))
                            ? true : false);
                    ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
                    List<TaskModel> taskList =
                        taskManager.findByProcessInstanceId(tenantId, officeFollow.getProcessInstanceId());
                    if (CollectionUtils.isNotEmpty(taskList)) {
                        List<String> listTemp = getAssigneeIdsAndAssigneeNames(taskList);
                        String taskIds = listTemp.get(0);
                        String assigneeNames = listTemp.get(2);
                        officeFollow.setTaskId(
                            listTemp.get(3).equals(ItemBoxTypeEnum.DOING.getValue()) ? taskIds : listTemp.get(4));
                        officeFollow.setTaskName(
                            StringUtils.isEmpty(taskList.get(0).getName()) ? "" : taskList.get(0).getName());
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
            resultMap.put("rows", list);
            resultMap.put("totalpage", followList.getTotalPages());
            resultMap.put("currpage", page);
            resultMap.put("total", followList.getTotalElements());
            resultMap.put(UtilConsts.SUCCESS, true);
            resultMap.put("msg", "列表获取成功");
        } catch (Exception e) {
            resultMap.put(UtilConsts.SUCCESS, false);
            resultMap.put("msg", "列表获取失败");
            e.printStackTrace();
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> getOfficeFollowList(String searchName, int page, int rows) {
        String positionId = Y9LoginUserHolder.getPositionId(), tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> resultMap = new HashMap<String, Object>(16);
        SimpleDateFormat sdf5 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<OfficeFollowModel> list = new ArrayList<OfficeFollowModel>();
        try {
            Pageable pageable = PageRequest.of(page - 1, rows, Sort.by(Sort.Direction.DESC, "startTime"));
            Page<OfficeFollow> followList = null;
            if (StringUtils.isBlank(searchName)) {
                followList = officeFollowRepository.findByUserId(positionId, pageable);
            } else {
                searchName = "%" + searchName + "%";
                followList = officeFollowRepository.findByParamsLike(positionId, searchName, pageable);
            }
            for (OfficeFollow officeFollow : followList.getContent()) {
                try {
                    String processInstanceId = officeFollow.getProcessInstanceId();
                    officeFollow.setStartTime(sdf5.format(sdf.parse(officeFollow.getStartTime())));
                    List<TaskModel> taskList =
                        taskManager.findByProcessInstanceId(tenantId, officeFollow.getProcessInstanceId());
                    if (CollectionUtils.isNotEmpty(taskList)) {
                        List<String> listTemp = getAssigneeIdsAndAssigneeNames(taskList);
                        String taskIds = listTemp.get(0);
                        String assigneeNames = listTemp.get(2);
                        officeFollow.setTaskId(
                            listTemp.get(3).equals(ItemBoxTypeEnum.DOING.getValue()) ? taskIds : listTemp.get(4));
                        officeFollow.setTaskName(
                            StringUtils.isEmpty(taskList.get(0).getName()) ? "" : taskList.get(0).getName());
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
            resultMap.put("rows", list);
            resultMap.put("totalpage", followList.getTotalPages());
            resultMap.put("currpage", page);
            resultMap.put("total", followList.getTotalElements());
            resultMap.put(UtilConsts.SUCCESS, true);
            resultMap.put("msg", "列表获取成功");
        } catch (Exception e) {
            resultMap.put(UtilConsts.SUCCESS, false);
            resultMap.put("msg", "列表获取失败");
            e.printStackTrace();
        }
        return resultMap;
    }

    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> saveOfficeFollow(OfficeFollow officeFollow) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, true);
        map.put("msg", "关注成功");
        try {
            if (officeFollow != null && officeFollow.getGuid() != null) {
                OfficeFollow follow = officeFollowRepository
                    .findByProcessInstanceIdAndUserId(officeFollow.getProcessInstanceId(), officeFollow.getUserId());
                if (follow == null) {
                    officeFollowRepository.save(officeFollow);
                }
            }
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "关注失败");
            e.printStackTrace();
        }
        return map;
    }

    @Override
    @Transactional(readOnly = false)
    public void updateTitle(String processInstanceId, String documentTitle) {
        try {
            List<OfficeFollow> list = officeFollowRepository.findByProcessInstanceId(processInstanceId);
            List<OfficeFollow> newList = new ArrayList<OfficeFollow>();
            for (OfficeFollow follow : list) {
                follow.setDocumentTitle(documentTitle);
                newList.add(follow);
            }
            if (newList.size() > 0) {
                officeFollowRepository.saveAll(newList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
