package net.risesoft.repository.jpa;

import net.risesoft.entity.TaskRelated;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface TaskRelatedRepository
        extends JpaRepository<TaskRelated, String>, JpaSpecificationExecutor<TaskRelated> {

    List<TaskRelated> findByTaskIdOrderByCreateTimeAsc(String taskId);

    TaskRelated findByTaskIdAndInfoType(String taskId,String infoType);
}
