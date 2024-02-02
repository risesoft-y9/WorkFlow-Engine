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

import net.risesoft.api.org.PersonApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.OfficeFollow;
import net.risesoft.entity.ProcessParam;
import net.risesoft.entity.RemindInstance;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.platform.Person;
import net.risesoft.model.itemadmin.OfficeFollowModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.jpa.OfficeFollowRepository;
import net.risesoft.service.OfficeFollowService;
import net.risesoft.service.ProcessParamService;
import net.risesoft.service.RemindInstanceService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9.util.Y9Util;

import y9.client.rest.processadmin.TaskApiClient;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service(value = "officeFollowService")
public class OfficeFollowServiceImpl implements OfficeFollowService {

    @Autowired
    private OfficeFollowRepository officeFollowRepository;

    @Autowired
    private TaskApiClient taskManager;

    @Autowired
    private PersonApi personManager;

    @Autowired
    private ProcessParamService processParamService;

    @Autowired
    private RemindInstanceService remindInstanceService;

    @Override
    public int countByProcessInstanceId(String processInstanceId) {
        return officeFollowRepository.countByProcessInstanceIdAndUserId(processInstanceId,
            Y9LoginUserHolder.getPersonId());
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
                officeFollowRepository.deleteByProcessInstanceId(processInstanceId, Y9LoginUserHolder.getPersonId());
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
        String userId = Y9LoginUserHolder.getPersonId();
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
                        Person personTemp = personManager.getPerson(tenantId, assignee).getData();
                        if (personTemp != null) {
                            assigneeNames = personTemp.getName();
                        }
                        i += 1;
                        if (assignee.contains(userId)) {
                            itembox = ItemBoxTypeEnum.TODO.getValue();
                            taskId = task.getId();
                        }
                    }
                } else {
                    String assignee = task.getAssignee();
                    if (StringUtils.isNotBlank(assignee)) {
                        if (i < 5) {
                            assigneeIds = Y9Util.genCustomStr(assigneeIds, assignee, SysVariables.COMMA);
                            Person personTemp = personManager.getPerson(tenantId, assignee).getData();
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
        return officeFollowRepository.countByUserId(Y9LoginUserHolder.getPersonId());
    }

    @Override
    public Map<String, Object> getOfficeFollowList(String searchName, int page, int rows) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String userId = userInfo.getPersonId(), tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> resultMap = new HashMap<String, Object>(16);
        SimpleDateFormat sdf5 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<OfficeFollowModel> list = new ArrayList<OfficeFollowModel>();
        try {
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
                        taskManager.findByProcessInstanceId(tenantId, officeFollow.getProcessInstanceId());
                    if (CollectionUtils.isNotEmpty(taskList)) {
                        List<String> listTemp = getAssigneeIdsAndAssigneeNames(taskList);
                        String taskIds = listTemp.get(0);
                        String assigneeNames = listTemp.get(2);
                        officeFollow.setTaskId(
                            new HashMap<String, String>(16).equals(listTemp.get(3)) ? taskIds : listTemp.get(4));
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
