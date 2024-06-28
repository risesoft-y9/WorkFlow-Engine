package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Opinion;
import net.risesoft.entity.ProcessInstance;
import net.risesoft.entity.ProcessInstanceDetails;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ProcessCooperationModel;
import net.risesoft.model.itemadmin.ProcessInstanceDetailsModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.repository.jpa.ProcessInstanceDetailsRepository;
import net.risesoft.repository.jpa.ProcessInstanceRepository;
import net.risesoft.service.OpinionService;
import net.risesoft.service.ProcessInstanceDetailsService;
import net.risesoft.service.SpmApproveItemService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9.util.Y9Util;

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
    public Y9Page<ProcessCooperationModel> processInstanceList(String userId, String title, int page, int rows) {
        List<ProcessCooperationModel> listMap = new ArrayList<>();
        Sort sort = Sort.by(Sort.Direction.DESC, "startTime");
        PageRequest pageable = PageRequest.of(page - 1, rows, sort);
        Page<ProcessInstance> pageList = null;
        if (StringUtils.isNotBlank(title)) {
            pageList = processInstanceRepository.findByUserIdAndTitle("%" + userId + "%", "%" + title + "%", pageable);
        } else {
            pageList = processInstanceRepository.findByUserId("%" + userId + "%", pageable);
        }
        for (ProcessInstance obj : pageList.getContent()) {
            List<ProcessInstanceDetailsModel> listData = new ArrayList<>();
            List<ProcessInstanceDetails> list =
                processInstanceDetailsRepository.findByProcessInstanceId(obj.getProcessInstanceId());

            ProcessCooperationModel process = new ProcessCooperationModel();
            process.setItembox("done");
            process.setProcessInstanceId(obj.getProcessInstanceId());
            process.setItemName(obj.getAppCnName());
            process.setTitle(obj.getTitle());
            process.setUrl(obj.getUrl());
            process.setNumber(obj.getSerialNumber());
            process.setStartTime(obj.getStartTime());
            process.setEndTime(obj.getEndTime());

            for (ProcessInstanceDetails details : list) {

                ProcessInstanceDetailsModel detail = new ProcessInstanceDetailsModel();
                detail.setId(details.getId());
                detail.setAssigneeName(details.getAssigneeName());
                detail.setSenderName(details.getSenderName());
                detail.setOpinionContent(details.getOpinionContent());
                detail.setTaskName(details.getTaskName());
                detail.setTitle(details.getTitle());
                detail.setUrl(details.getUrl());
                detail.setStartTime(details.getStartTime());
                detail.setEndTime(details.getEndTime());
                detail.setItembox(details.getItembox());
                if (details.getEndTime() == null) {
                    detail.setItembox("doing");
                    detail.setEndTime(null);
                }
                listData.add(detail);
            }
            process.setItemInfo(listData);
            listMap.add(process);
        }
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), listMap);
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
            String assigneeNowId = details.getAssigneeId();
            if (assignee == null) {
                assignee = assigneeNowId;
            } else {
                if (assigneeNowId != null && !assignee.contains(assigneeNowId)) {
                    assignee = Y9Util.genCustomStr(assignee, assigneeNowId);
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
                String opinionStr = !opinion.isEmpty() ? opinion.get(0).getContent() : "";
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
