package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.ProcessTrack;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface ProcessTrackRepository extends JpaRepository<ProcessTrack, String>, JpaSpecificationExecutor<ProcessTrack> {

    @Query("from ProcessTrack t where t.taskId=?1 order by t.endTime desc")
    List<ProcessTrack> findByTaskId(String taskId);

    @Query("from ProcessTrack t where t.taskId=?1 and t.endTime=?2")
    List<ProcessTrack> findByTaskIdAndEndTimeIsNull(String taskId, String endTime);

    @Query("from ProcessTrack t where t.taskId=?1 order by t.startTime asc")
    List<ProcessTrack> findByTaskIdAsc(String taskId);
}
