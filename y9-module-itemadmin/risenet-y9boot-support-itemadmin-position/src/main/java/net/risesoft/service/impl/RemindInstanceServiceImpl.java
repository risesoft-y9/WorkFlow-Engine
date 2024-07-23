package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.RemindInstance;
import net.risesoft.enums.ItemRemindTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.RemindInstanceRepository;
import net.risesoft.service.RemindInstanceService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class RemindInstanceServiceImpl implements RemindInstanceService {

    private final RemindInstanceRepository remindInstanceRepository;

    @Override
    public RemindInstance getRemindInstance(String processInstanceId) {
        return remindInstanceRepository.findByProcessInstanceIdAndUserId(processInstanceId,
            Y9LoginUserHolder.getPositionId());
    }

    @Override
    public List<RemindInstance> listByProcessInstanceId(String processInstanceId) {
        return remindInstanceRepository.findByProcessInstanceId(processInstanceId);
    }

    @Override
    public List<RemindInstance> listByProcessInstanceIdAndArriveTaskKey(String processInstanceId, String taskKey) {
        return remindInstanceRepository.findByProcessInstanceIdAndArriveTaskKeyLike(processInstanceId,
            "%" + taskKey + "%");
    }

    @Override
    public List<RemindInstance> listByProcessInstanceIdAndCompleteTaskKey(String processInstanceId, String taskKey) {
        return remindInstanceRepository.findByProcessInstanceIdAndCompleteTaskKeyLike(processInstanceId,
            "%" + taskKey + "%");
    }

    @Override
    public List<RemindInstance> listByProcessInstanceIdAndRemindType(String processInstanceId, String remindType) {
        return remindInstanceRepository.findByProcessInstanceIdAndRemindTypeLike(processInstanceId,
            "%" + remindType + "%");
    }

    @Override
    public List<RemindInstance> listByProcessInstanceIdAndTaskId(String processInstanceId, String taskId) {
        return remindInstanceRepository.findByProcessInstanceIdAndTaskId(processInstanceId, taskId);
    }

    @Override
    @Transactional
    public Y9Result<String> saveRemindInstance(String processInstanceId, String taskIds, Boolean process,
        String arriveTaskKey, String completeTaskKey) {
        try {
            String userId = Y9LoginUserHolder.getPositionId();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            RemindInstance remindInstance =
                remindInstanceRepository.findByProcessInstanceIdAndUserId(processInstanceId, userId);
            // 删除
            if (StringUtils.isBlank(taskIds) && !Boolean.TRUE.equals(process) && StringUtils.isBlank(arriveTaskKey)
                && StringUtils.isBlank(completeTaskKey)) {
                if (remindInstance != null) {
                    remindInstanceRepository.delete(remindInstance);
                }
                return Y9Result.successMsg("保存成功");
            }
            String remindType = "";
            if (StringUtils.isNotBlank(taskIds)) {
                remindType = Y9Util.genCustomStr(remindType, ItemRemindTypeEnum.TASKCOMPLETE.getValue());
            }
            if (StringUtils.isNotBlank(arriveTaskKey)) {
                remindType = Y9Util.genCustomStr(remindType, ItemRemindTypeEnum.NODEARRIVE.getValue());
            }
            if (StringUtils.isNotBlank(completeTaskKey)) {
                remindType = Y9Util.genCustomStr(remindType, ItemRemindTypeEnum.NODECOMPLETE.getValue());
            }
            if (Boolean.TRUE.equals(process)) {
                remindType = Y9Util.genCustomStr(remindType, ItemRemindTypeEnum.PROCESSCOMPLETE.getValue());
            }
            if (remindInstance != null) {
                remindInstance.setTaskId(taskIds);
                remindInstance.setRemindType(remindType);
                remindInstance.setArriveTaskKey(arriveTaskKey);
                remindInstance.setCompleteTaskKey(completeTaskKey);
                remindInstance.setCreateTime(sdf.format(new Date()));
                remindInstanceRepository.save(remindInstance);
                return Y9Result.successMsg("保存成功");
            }
            remindInstance = new RemindInstance();
            remindInstance.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            remindInstance.setTaskId(taskIds);
            remindInstance.setTenantId(Y9LoginUserHolder.getTenantId());
            remindInstance.setProcessInstanceId(processInstanceId);
            remindInstance.setUserId(userId);
            remindInstance.setRemindType(remindType);
            remindInstance.setArriveTaskKey(arriveTaskKey);
            remindInstance.setCompleteTaskKey(completeTaskKey);
            remindInstance.setCreateTime(sdf.format(new Date()));
            remindInstanceRepository.save(remindInstance);
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("保存失败");
    }

}
