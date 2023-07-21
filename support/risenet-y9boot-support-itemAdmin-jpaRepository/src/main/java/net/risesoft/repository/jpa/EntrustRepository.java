package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.Entrust;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface EntrustRepository extends JpaRepository<Entrust, String>, JpaSpecificationExecutor<Entrust> {

    /**
     * 查找某个用户没有删除的委托对象
     * 
     * @param ownerId
     * @return
     */
    @Override
    @Query("from Entrust t order by t.updateTime desc")
    List<Entrust> findAll();

    /**
     * 查找某个用户没有删除的委托对象
     * 
     * @param ownerId
     * @return
     */
    @Query("from Entrust t where t.ownerId=?1 order by t.updateTime desc")
    List<Entrust> findAll(String ownerId);

    /**
     * 查找某个用户没有删除的委托对象-分页
     * 
     * @param ownerId
     * @param pageable
     * @return
     */
    @Query("from Entrust t where t.ownerId=?1 order by t.updateTime desc")
    Page<Entrust> findAll(String ownerId, Pageable pageable);

    List<Entrust> findByAssigneeIdOrderByStartTimeDesc(String assigneeId);

    /**
     * 查找某个人某个时间点的被委托对象
     * 
     * @param ownerId
     * @param dateTime
     * @return
     */
    @Query("from Entrust t where t.assigneeId=?1 and  (t.startTime<=?2  and t.endTime>=?2)")
    List<Entrust> findOneByAssigneeIdAndTime(String assigneeId, String dateTime);

    /**
     * 查找某个人某个事项没有删除的委托对象
     * 
     * @param ownerId
     * @param itemId
     * @param dateTime
     * @return
     */
    @Query("from Entrust t where t.ownerId=?1 and t.itemId=?2")
    Entrust findOneByOwnerIdAndItemId(String ownerId, String itemId);

    /**
     * 查找某个人某个事项某个时间点没有删除且启用的委托对象
     * 
     * @param ownerId
     * @param itemId
     * @param dateTime
     * @return
     */
    @Query("from Entrust t where t.ownerId=?1 and t.itemId=?2 and  (t.startTime<=?3  and t.endTime>=?3)")
    Entrust findOneByOwnerIdAndItemIdAndTime(String ownerId, String itemId, String dateTime);

    /**
     * 查找某个人某个事项没有删除的委托对象
     * 
     * @param ownerId
     * @param itemId
     * @return
     */
    @Query("select count(*) from Entrust t where t.ownerId=?1 and t.itemId=?2")
    Integer getCountByOwnerIdAndItemId(String ownerId, String itemId);
}
