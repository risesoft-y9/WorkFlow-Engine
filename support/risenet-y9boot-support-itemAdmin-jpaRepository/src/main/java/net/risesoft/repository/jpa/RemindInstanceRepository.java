package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.RemindInstance;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface RemindInstanceRepository extends JpaRepository<RemindInstance, String>, JpaSpecificationExecutor<RemindInstance> {

    List<RemindInstance> findByProcessInstanceId(String processInstanceId);

    List<RemindInstance> findByProcessInstanceIdAndArriveTaskKeyLike(String processInstanceId, String taskKey);

    List<RemindInstance> findByProcessInstanceIdAndCompleteTaskKeyLike(String processInstanceId, String taskKey);

    List<RemindInstance> findByProcessInstanceIdAndRemindTypeLike(String processInstanceId, String remindType);

    List<RemindInstance> findByProcessInstanceIdAndTaskId(String processInstanceId, String taskId);

    RemindInstance findByProcessInstanceIdAndUserId(String processInstanceId, String userId);

}
