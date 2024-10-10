package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.ChaoSong;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface ChaoSongRepository extends JpaRepository<ChaoSong, String>, JpaSpecificationExecutor<ChaoSong> {

    @Query("select count(t.id) from ChaoSong t where t.processInstanceId=?1")
    int countByProcessInstanceId(String processInstanceId);

    @Query("select count(t.id) from ChaoSong t where t.senderId=?1 and t.processInstanceId=?2")
    int countByUserIdAndProcessInstanceId(String userId, String processInstanceId);

    List<ChaoSong> findByProcessInstanceId(String processInstanceId);

    Page<ChaoSong> findByProcessInstanceIdOrderByCreateTimeDesc(String processInstanceId, Pageable pageable);

    Page<ChaoSong> findBySenderIdAndProcessInstanceIdOrderByCreateTimeDesc(String senderId, String processInstanceId,
        Pageable pageable);

    List<ChaoSong> findByTaskId(String taskId, Sort sort);

    @Query("from ChaoSong t where t.userId in ?1 and t.itemId=?2 and t.status=1")
    List<ChaoSong> getDoneByUserIdAndItemId(List<String> userIds, String itemId);

    @Query("from ChaoSong t where t.userId=?1 and t.itemId=?2 and t.status=1")
    List<ChaoSong> getDoneByUserIdAndItemId(String userId, String itemId);

    @Query("from ChaoSong t where t.userId in ?1 and t.systemName=?2 and t.status=1")
    List<ChaoSong> getDoneByUserIdAndSystemName(List<String> userIds, String systemName);

    @Query("from ChaoSong t where t.userId=?1 and t.systemName=?2 and t.status=1")
    List<ChaoSong> getDoneByUserIdAndSystemName(String userId, String systemName);

    @Query("from ChaoSong t where t.userId=?1 and t.title like ?2 and t.status=1")
    Page<ChaoSong> getDoneByUserIdAndTitleLike(String userId, String name, Pageable pageable);

    @Query("select count(t.id) from ChaoSong t where t.userId=?1 and t.status=1")
    int getDoneCountByUserId(String userId);

    @Query("select count(t.id) from ChaoSong t where t.userId=?1 and t.itemId=?2 and t.status=1")
    int getDoneCountByUserIdAndItemId(String userId, String itemId);

    @Query("select count(t.id) from ChaoSong t where t.userId=?1 and t.systemName=?2 and t.status=1")
    int getDoneCountByUserIdAndSystemName(String userId, String systemName);

    @Query("from ChaoSong t where t.userId=?1 and t.status=1")
    Page<ChaoSong> getDoneListByUserId(String userId, Pageable pageable);

    @Query("from ChaoSong t where t.userId=?1 and t.itemId=?2 and t.status=1")
    Page<ChaoSong> getDoneListByUserIdAndItemId(String userId, String itemId, Pageable pageable);

    @Query("from ChaoSong t where t.userId=?1 and t.systemName=?2 and t.status=1")
    Page<ChaoSong> getDoneListByUserIdAndSystemName(String userId, String systemName, Pageable pageable);

    @Query("from ChaoSong t where t.processInstanceId=?1 order by t.createTime asc")
    List<ChaoSong> getListByProcessInstanceId(String processInstanceId);

    @Query("from ChaoSong t where t.userId=?1 and t.opinionState = '1'")
    Page<ChaoSong> getOpinionChaosongByUserId(String userId, Pageable pageable);

    @Query("from ChaoSong t where t.userId=?1 and t.opinionState = '1' and t.title like ?2")
    Page<ChaoSong> getOpinionChaosongByUserIdAndTitleLike(String userId, String documentTitle, Pageable pageable);

    @Query("from ChaoSong t where t.userId=?1 and t.processInstanceId=?2 and t.status =2 order by t.createTime asc")
    List<ChaoSong> getTodoByUserIdAndProcessInstanceId(String userId, String processInstanceId);

    @Query("from ChaoSong t where t.userId=?1 and t.title like ?2 and t.status =2")
    Page<ChaoSong> getTodoByUserIdAndTitleLike(String userId, String name, Pageable pageable);

    @Query("from ChaoSong t where t.userId in ?1 and t.itemId=?2 and t.status =2")
    Page<ChaoSong> getTodoByUserIdsAndItemId(List<String> userId, String itemId, Pageable pageable);

    @Query("from ChaoSong t where t.userId in ?1 and t.systemName=?2 and t.status =2")
    Page<ChaoSong> getTodoByUserIdsAndSystemName(List<String> userIds, String systemName, Pageable pageable);

    @Query("select count(t.id) from ChaoSong t where t.userId=?1 and t.status=2")
    int getTodoCount4NewByUserId(String userId);

    @Query("select count(t.id) from ChaoSong t where t.userId=?1 and t.status =2")
    int getTodoCountByUserId(String userId);

    @Query("select count(t.id) from ChaoSong t where t.userId=?1 and t.itemId=?2 and t.status =2")
    int getTodoCountByUserIdAndItemId(String userId, String itemId);

    @Query("select count(t.id) from ChaoSong t where t.userId=?1 and t.systemName=?2 and t.status =2")
    int getTodoCountByUserIdAndSystemName(String userId, String systemName);

    @Query("from ChaoSong t where t.userId=?1 and t.status =2")
    Page<ChaoSong> getTodoListByUserId(String userId, Pageable pageable);

    @Query("from ChaoSong t where t.userId=?1 and t.itemId=?2 and t.status =2")
    Page<ChaoSong> getTodoListByUserIdAndItemId(String userId, String itemId, Pageable pageable);

    @Query("from ChaoSong t where t.userId=?1 and t.itemId=?2 and t.status =2 and t.title like ?3")
    Page<ChaoSong> getTodoListByUserIdAndItemIdAndTitle(String userId, String itemId, String title, Pageable pageable);

    @Query("from ChaoSong t where t.userId=?1 and t.systemName=?2 and t.status =2")
    Page<ChaoSong> getTodoListByUserIdAndSystemName(String userId, String systemName, Pageable pageable);

    @Query("from ChaoSong t where t.userId=?1 and t.systemName=?2 and t.status =2  and t.title like ?3")
    Page<ChaoSong> getTodoListByUserIdAndSystemNameAndTitle(String userId, String systemName, String title,
        Pageable pageable);

    @Query("from ChaoSong t where t.processInstanceId=?1 and t.status =1 order by t.readTime desc")
    List<ChaoSong> listAllByProcessInstanceIdOrderByReadTime(String processInstanceId);

    @Modifying
    @Transactional(readOnly = false)
    @Query("update ChaoSong t set t.opinionState=?2 where t.id=?1")
    void updateOpinionState(String id, String opinionState);

    @Modifying
    @Transactional(readOnly = false)
    @Query("update ChaoSong t set t.title=?2 where t.processInstanceId=?1")
    void updateTitle(String processInstanceId, String documentTitle);

}
