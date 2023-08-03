package net.risesoft.repository.jpa;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import net.risesoft.entity.WorkOrderEntity;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface WorkOrderRepository
    extends JpaRepository<WorkOrderEntity, String>, JpaSpecificationExecutor<WorkOrderEntity> {

    @Query("SELECT" + "	count(DISTINCT w.guid) as num," + "	CASE WHEN w.handleType = '0' THEN 'draft'"
        + "		 WHEN w.handleType = '1' THEN 'todo'" + "		 WHEN w.handleType = '2' THEN 'doing'"
        + "		 WHEN w.handleType = '3' THEN 'done'" + "		 ELSE 'notexist' end as type" + " FROM"
        + "	WorkOrderEntity w" + " GROUP BY" + "	w.handleType")
    List<Map<String, Object>> countByAdmin();

    @Query("SELECT" + "	count(DISTINCT w.guid) as num," + "	CASE WHEN w.handleType = '0' THEN 'draft'"
        + "		 WHEN w.handleType = '1' THEN 'todo'" + "		 WHEN w.handleType = '2' THEN 'doing'"
        + "		 WHEN w.handleType = '3' THEN 'done'" + "		 ELSE 'notexist' end as type" + " FROM"
        + "	WorkOrderEntity w" + " where w.userId = ?1 " + " GROUP BY" + "	w.handleType")
    List<Map<String, Object>> countByUserId(String userId);

    WorkOrderEntity findByGuid(String processSerialNumber);

    Page<WorkOrderEntity> findByHandleType(String handleType, Pageable pageable);

    Page<WorkOrderEntity> findByUserIdAndHandleType(String userId, String handleType, Pageable pageable);

    @Query("SELECT" + "	count(DISTINCT w.guid)" + " FROM" + "	WorkOrderEntity w where w.handleType = '1'")
    int getAdminTodoCount();

}
