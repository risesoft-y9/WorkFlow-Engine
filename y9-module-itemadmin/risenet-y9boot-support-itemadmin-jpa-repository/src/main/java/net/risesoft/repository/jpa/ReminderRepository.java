package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.Reminder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface ReminderRepository extends JpaRepository<Reminder, String>, JpaSpecificationExecutor<Reminder> {

    Page<Reminder> findByProcInstId(String processInstanceId, Pageable pageable);

    Page<Reminder> findBySenderIdAndTaskIdIn(String senderId, List<String> taskIds, Pageable pageable);

    Page<Reminder> findByTaskId(String taskId, Pageable pageable);
}
