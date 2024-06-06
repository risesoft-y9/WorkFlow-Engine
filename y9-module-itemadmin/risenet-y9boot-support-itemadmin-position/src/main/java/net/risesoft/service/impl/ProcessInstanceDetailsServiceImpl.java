package net.risesoft.service.impl;

import lombok.RequiredArgsConstructor;
import net.risesoft.entity.Opinion;
import net.risesoft.entity.ProcessInstance;
import net.risesoft.entity.ProcessInstanceDetails;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ProcessInstanceDetailsModel;
import net.risesoft.repository.jpa.ProcessInstanceDetailsRepository;
import net.risesoft.repository.jpa.ProcessInstanceRepository;
import net.risesoft.service.OpinionService;
import net.risesoft.service.ProcessInstanceDetailsService;
import net.risesoft.service.SpmApproveItemService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9.util.Y9Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
public class ProcessInstanceDetailsServiceImpl implements ProcessInstanceDetailsService {

    private final SpmApproveItemService spmApproveitemService;

    private final ProcessInstanceRepository processInstanceRepository;

    private final ProcessInstanceDetailsRepository processInstanceDetailsRepository;

    private final OpinionService opinionService;

    @Override
    @Transactional
    public boolean deleteProcessInstance(String processInstanceId) {
        try {
            processInstanceRepository.deleteByProcessInstanceId(processInstanceId);
            processInstanceDetailsRepository.deleteByProcessInstanceId(processInstanceId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public Map<String, Object> processInstanceList(String userId, String title, int page, int rows) {
        Map<String, Object> ret_map = new HashMap<String, Object>();
        ret_map.put("success", true);
        try {
            List<Map<String, Object>> listMap = new ArrayList<>();
            Sort sort = Sort.by(Sort.Direction.DESC, "startTime");
            PageRequest pageable = PageRequest.of(page - 1, rows, sort);
            Page<ProcessInstance> pageList = null;
            if (StringUtils.isNotBlank(title)) {
                pageList =
                    processInstanceRepository.findByUserIdAndTitle("%" + userId + "%", "%" + title + "%", pageable);
            } else {
                pageList = processInstanceRepository.findByUserId("%" + userId + "%", pageable);
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (ProcessInstance obj : pageList.getContent()) {
                Map<String, Object> mapData = new HashMap<String, Object>();
                List<Map<String, Object>> listData = new ArrayList<>();
                List<ProcessInstanceDetails> list =
                    processInstanceDetailsRepository.findByProcessInstanceId(obj.getProcessInstanceId());
                mapData.put("itembox", "done");
                mapData.put("endTime", obj.getEndTime() != null ? sdf.format(obj.getEndTime()) : "");
                for (ProcessInstanceDetails details : list) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("id", details.getId());
                    map.put("assigneeName", details.getAssigneeName());
                    map.put("senderName", details.getSenderName());
                    map.put("opinionContent", details.getOpinionContent());
                    map.put("taskName", details.getTaskName());
                    map.put("title", details.getTitle());
                    map.put("url", details.getUrl());
                    map.put("startTime", sdf.format(details.getStartTime()));
                    map.put("endTime", details.getEndTime() != null ? sdf.format(details.getEndTime()) : "");
                    map.put("itembox", details.getItembox());
                    if (details.getEndTime() == null) {
                        mapData.put("itembox", "doing");
                        mapData.put("endTime", "");
                    }
                    listData.add(map);
                }
                mapData.put("processInstanceId", obj.getProcessInstanceId());
                mapData.put("itemName", obj.getAppCnName());
                mapData.put("title", obj.getTitle());
                mapData.put("number", obj.getSerialNumber());
                mapData.put("startTime", sdf.format(obj.getStartTime()));
                mapData.put("url", obj.getUrl());
                mapData.put("itemInfo", listData);
                listMap.add(mapData);
            }
            ret_map.put("rows", listMap);
            ret_map.put("currpage", page);
            ret_map.put("totalpages", pageList != null ? pageList.getTotalPages() : 1);
            ret_map.put("total", pageList != null ? pageList.getTotalElements() : 0);
        } catch (Exception e) {
            ret_map.put("success", false);
            e.printStackTrace();
        }
        return ret_map;
    }

    @Override
    @Transactional
    public boolean save(ProcessInstanceDetailsModel model) {
        try {
            SpmApproveItem spmApproveItem = spmApproveitemService.findById(model.getItemId());
            model.setAppCnName(spmApproveItem != null ? spmApproveItem.getName() : "");
            model.setAppName(spmApproveItem.getSystemName());

            ProcessInstanceDetails details = new ProcessInstanceDetails();
            Y9BeanUtil.copyProperties(model, details);

            ProcessInstance processInstance =
                processInstanceRepository.findByProcessInstanceId(details.getProcessInstanceId());
            if (processInstance == null || processInstance.getId() == null) {
                processInstance = new ProcessInstance();
                processInstance.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                processInstance.setStartTime(details.getStartTime());
            }
            processInstance.setAppCnName(details.getAppCnName());
            processInstance.setItemId(details.getItemId());
            processInstance.setAppName(details.getAppName());
            processInstance.setEndTime(null);
            processInstance.setIsDeleted(0);
            processInstance.setProcessInstanceId(details.getProcessInstanceId());
            processInstance.setProcessSerialNumber(details.getProcessSerialNumber());
            processInstance.setSerialNumber(details.getSerialNumber());
            processInstance.setSystemCnName(details.getSystemCnName());
            processInstance.setSystemName(details.getSystemName());
            processInstance.setTitle(details.getTitle());
            processInstance.setUrl(details.getUrl());
            processInstance.setUserName(details.getUserName());
            String assignee = processInstance.getAssignee();
            String ASSIGNEE_1 = details.getAssigneeId();
            if (assignee == null) {
                assignee = ASSIGNEE_1;
            } else {
                if (ASSIGNEE_1 != null && !assignee.contains(ASSIGNEE_1)) {
                    assignee = Y9Util.genCustomStr(assignee, ASSIGNEE_1);
                }
            }
            processInstance.setAssignee(assignee);
            processInstanceRepository.save(processInstance);
            if (StringUtils.isBlank(details.getId())) {
                details.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            }
            processInstanceDetailsRepository.save(details);
        } catch (BeansException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    @Transactional
    public boolean updateProcessInstanceDetails(String processInstanceId, String taskId, String itembox, Date endTime) {
        try {
            ProcessInstanceDetails details =
                processInstanceDetailsRepository.findByProcessInstanceIdAndTaskId(processInstanceId, taskId);
            if (details != null && details.getId() != null) {
                List<Opinion> opinion = opinionService.findByTaskIdAndPositionIdAndProcessTrackIdIsNull(taskId,
                    Y9LoginUserHolder.getPositionId());
                String opinionStr = opinion.size() > 0 ? opinion.get(0).getContent() : "";
                details.setEndTime(endTime);
                details.setItembox(itembox);
                details.setOpinionContent(opinionStr);
                processInstanceDetailsRepository.save(details);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
