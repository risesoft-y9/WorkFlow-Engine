package net.risesoft.repository.jpa;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.Reminder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface ReminderRepository extends JpaRepository<Reminder, String>, JpaSpecificationExecutor<Reminder> {

    @Query("From Reminder r where r.taskId in ?1 and r.senderId=?2")
    public List<Reminder> findAllByTaskIdsAndSenderId(Collection<String> taskIds, String senderId);

    @Query("From Reminder r where r.taskId in ?1 ")
    public List<Reminder> findAllByTastId(Collection<String> userIds);

    Page<Reminder> findByprocInstId(String processInstanceId, Pageable pageable);

    Page<Reminder> findBySenderIdAndTaskIdIn(String senderId, List<String> taskIds, Pageable pageable);

    @Query("From Reminder r where r.taskId =?1 ")
    public Reminder findByTaskId(String taskId);

    Page<Reminder> findByTaskId(String taskId, Pageable pageable);

    public Reminder findByTaskIdAndSenderId(String taskId, String senderId);

    @Query("From Reminder r where r.taskId = ?1 and r.reminderSendType=?2 order by r.createTime asc")
    public List<Reminder> findByTastIdAndReminderSendType(String taskId, String reminderSendType);

    @Transactional(readOnly = false)
    @Modifying
    @Query("update Reminder r set r.readTime=?1 where r.taskId=?2 and r.reminderSendType=?3")
    public int updateReadTime(Date readTime, String taskId, String type);
}
