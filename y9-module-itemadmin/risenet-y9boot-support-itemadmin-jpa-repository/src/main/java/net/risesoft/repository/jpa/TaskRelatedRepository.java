package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.TaskRelated;

@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface TaskRelatedRepository
        extends JpaRepository<TaskRelated, String>, JpaSpecificationExecutor<TaskRelated> {

    List<TaskRelated> findByTaskIdOrderByCreateTimeAsc(String taskId);

    TaskRelated findByTaskIdAndInfoType(String taskId,String infoType);

    List<TaskRelated> findByProcessSerialNumberOrderByCreateTimeDesc(String processSerialNumber);
}
