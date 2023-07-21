package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.RemindInstance;
import net.risesoft.enums.ItemRemindTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.jpa.RemindInstanceRepository;
import net.risesoft.service.RemindInstanceService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service(value = "remindInstanceService")
public class RemindInstanceServiceImpl implements RemindInstanceService {

    @Autowired
    private RemindInstanceRepository remindInstanceRepository;

    @Override
    public List<RemindInstance> findRemindInstance(String processInstanceId) {
        return remindInstanceRepository.findByProcessInstanceId(processInstanceId);
    }

    @Override
    public List<RemindInstance> findRemindInstanceByProcessInstanceIdAndArriveTaskKey(String processInstanceId, String taskKey) {
        return remindInstanceRepository.findByProcessInstanceIdAndArriveTaskKeyLike(processInstanceId, "%" + taskKey + "%");
    }

    @Override
    public List<RemindInstance> findRemindInstanceByProcessInstanceIdAndCompleteTaskKey(String processInstanceId, String taskKey) {
        return remindInstanceRepository.findByProcessInstanceIdAndCompleteTaskKeyLike(processInstanceId, "%" + taskKey + "%");
    }

    @Override
    public List<RemindInstance> findRemindInstanceByProcessInstanceIdAndRemindType(String processInstanceId, String remindType) {
        return remindInstanceRepository.findByProcessInstanceIdAndRemindTypeLike(processInstanceId, "%" + remindType + "%");
    }

    @Override
    public List<RemindInstance> findRemindInstanceByProcessInstanceIdAndTaskId(String processInstanceId, String taskId) {
        return remindInstanceRepository.findByProcessInstanceIdAndTaskId(processInstanceId, taskId);
    }

    @Override
    public RemindInstance getRemindInstance(String processInstanceId) {
        return remindInstanceRepository.findByProcessInstanceIdAndUserId(processInstanceId, Y9LoginUserHolder.getPositionId());
    }

    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> saveRemindInstance(String processInstanceId, String taskIds, Boolean process, String arriveTaskKey, String completeTaskKey) {
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        retMap.put("msg", "保存失败");
        retMap.put(UtilConsts.SUCCESS, false);
        try {
            String userId = Y9LoginUserHolder.getPositionId();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            RemindInstance remindInstance = remindInstanceRepository.findByProcessInstanceIdAndUserId(processInstanceId, userId);
            // 删除
            if (StringUtils.isBlank(taskIds) && !process && StringUtils.isBlank(arriveTaskKey) && StringUtils.isBlank(completeTaskKey)) {
                if (remindInstance != null) {
                    remindInstanceRepository.delete(remindInstance);
                }
                retMap.put("msg", "保存成功");
                retMap.put(UtilConsts.SUCCESS, true);
                return retMap;
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
            if (process) {
                remindType = Y9Util.genCustomStr(remindType, ItemRemindTypeEnum.PROCESSCOMPLETE.getValue());
            }
            if (remindInstance != null) {
                remindInstance.setTaskId(taskIds);
                remindInstance.setRemindType(remindType);
                remindInstance.setArriveTaskKey(arriveTaskKey);
                remindInstance.setCompleteTaskKey(completeTaskKey);
                remindInstance.setCreateTime(sdf.format(new Date()));
                remindInstanceRepository.save(remindInstance);
                retMap.put("msg", "保存成功");
                retMap.put(UtilConsts.SUCCESS, true);
                return retMap;
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
            retMap.put("msg", "保存成功");
            retMap.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retMap;
    }

}
