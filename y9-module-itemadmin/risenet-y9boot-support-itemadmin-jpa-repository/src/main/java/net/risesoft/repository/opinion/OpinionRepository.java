package net.risesoft.repository.opinion;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.opinion.Opinion;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface OpinionRepository extends JpaRepository<Opinion, String>, JpaSpecificationExecutor<Opinion> {

    @Query("select count(*) from Opinion t where t.processSerialNumber = ?1")
    int findByProcSerialNumber(String processSerialNumber);

    List<Opinion> findByProcessSerialNumberAndOpinionFrameMarkOrderByCreateTimeAsc(String processSerialNumber,
        String opinionFrameMark);

    List<Opinion> findByProcessInstanceIdOrderByCreateTimeAsc(String processInstanceId);

    List<Opinion> findByProcessSerialNumberOrderByCreateTimeAsc(String processSerialNumber);

    List<Opinion> findByTaskIdOrderByCreateTimeAsc(String taskId);

    List<Opinion> findByTaskIdAndPositionIdAndProcessTrackIdIsNull(String taskId, String positionId);

    List<Opinion> findByTaskIdAndProcessTrackIdOrderByCreateTimeDesc(String taskId, String processTrackId);

    @Query("select count(t.id) from Opinion t where t.taskId=?1")
    int getCountByTaskId(String taskId);

    @Modifying
    @Transactional
    @Query("update Opinion t set t.processInstanceId=?1,t.taskId=?2 where t.processSerialNumber=?3")
    int update(String processInstanceId, String taskId, String processSerialNumber);
}
