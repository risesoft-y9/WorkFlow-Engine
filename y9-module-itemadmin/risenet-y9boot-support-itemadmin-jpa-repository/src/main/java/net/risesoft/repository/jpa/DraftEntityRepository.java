package net.risesoft.repository.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.DraftEntity;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Repository
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface DraftEntityRepository
    extends JpaRepository<DraftEntity, String>, JpaSpecificationExecutor<DraftEntity> {

    /**
     * 获取草稿箱计数
     *
     * @param userId
     * @return
     */
    Integer countByCreaterIdAndDelFlagFalse(String userId);

    /**
     * 获取删除的草稿箱
     *
     * @param userId
     * @return
     */
    Integer countByCreaterIdAndDelFlagTrue(String userId);

    /**
     * 根据事项id获取草稿箱计数
     *
     * @param userId
     * @param itemId
     * @return
     */
    Integer countByItemIdAndCreaterIdAndDelFlagFalse(String itemId, String userId);

    /**
     * 草稿箱回收站计数
     *
     * @param itemId
     * @param userId
     * @return
     */
    Integer countByItemIdAndCreaterIdAndDelFlagTrue(String itemId, String userId);

    /**
     * 根据事项id获取草稿箱计数
     *
     * @param userId
     * @param systemName
     * @return
     */
    Integer countByTypeAndCreaterIdAndDelFlagFalse(String systemName, String userId);

    /**
     * 回收站数量
     *
     * @param systemName
     * @param userId
     * @return
     */
    Integer countByTypeAndCreaterIdAndDelFlagTrue(String systemName, String userId);

    /**
     * 根据流程编号删除
     *
     * @param processSerialNumber
     */
    @Transactional(readOnly = false)
    @Modifying
    @Query("delete DraftEntity t where t.processSerialNumber =?1")
    void deleteByProcessSerialNumber(String processSerialNumber);

    /**
     * 根据用户Id获取未删除草稿
     *
     * @param userId
     * @param pageable
     * @return
     */
    Page<DraftEntity> findByCreaterIdAndDelFlagFalse(String userId, Pageable pageable);

    /**
     * 根据用户Id获取搜索的未删除草稿
     *
     * @param userId
     * @param title
     * @param pageable
     * @return
     */
    Page<DraftEntity> findByCreaterIdAndTitleLikeAndDelFlagFalse(String userId, String title, Pageable pageable);

    /**
     * 根据获取删除草稿
     *
     * @param userId
     * @param title
     * @param pageable
     * @return
     */
    Page<DraftEntity> findByCreaterIdAndTitleLikeAndDelFlagTrue(String userId, String title, Pageable pageable);

    /**
     * 根据事项Id获取未删除草稿
     *
     * @param userId
     * @param itemId
     * @param pageable
     * @return
     */
    Page<DraftEntity> findByItemIdAndCreaterIdAndDelFlagFalse(String itemId, String userId, Pageable pageable);

    /**
     * 获取回收站（岗位）
     *
     * @param itemId
     * @param userId
     * @param pageable
     * @return
     */
    Page<DraftEntity> findByItemIdAndCreaterIdAndDelFlagTrue(String itemId, String userId, Pageable pageable);

    /**
     * 根据事项Id获取搜索的未删除草稿
     *
     * @param userId
     * @param title
     * @param itemId
     * @param pageable
     * @return
     */
    Page<DraftEntity> findByItemIdAndCreaterIdAndTitleLikeAndDelFlagFalse(String itemId, String userId, String title,
        Pageable pageable);

    /**
     * 根据获取删除草稿
     *
     * @param userId
     * @param title
     * @param itemId
     * @param pageable
     * @return
     */
    Page<DraftEntity> findByItemIdAndCreaterIdAndTitleLikeAndDelFlagTrue(String itemId, String userId, String title,
        Pageable pageable);

    /**
     * 根据processSerialNumber获取草稿
     *
     * @param processSerialNumber
     * @return
     */
    @Query("from DraftEntity t where t.processSerialNumber=?1")
    DraftEntity findByProcessSerialNumber(String processSerialNumber);

    /**
     * 根据事项Id获取搜索的未删除草稿
     *
     * @param userId
     * @param title
     * @param systemName
     * @param pageable
     * @return
     */
    Page<DraftEntity> findByTypeAndCreaterIdAndTitleLikeAndDelFlagFalse(String systemName, String userId, String title,
        Pageable pageable);

    /**
     * 根据获取删除草稿
     *
     * @param userId
     * @param title
     * @param systemName
     * @param pageable
     * @return
     */
    Page<DraftEntity> findByTypeAndCreaterIdAndTitleLikeAndDelFlagTrue(String systemName, String userId, String title,
        Pageable pageable);

}
