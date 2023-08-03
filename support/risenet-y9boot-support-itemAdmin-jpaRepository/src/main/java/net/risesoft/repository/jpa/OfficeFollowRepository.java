package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.OfficeFollow;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface OfficeFollowRepository
    extends JpaRepository<OfficeFollow, String>, JpaSpecificationExecutor<OfficeFollow> {

    Integer countByProcessInstanceIdAndUserId(String processInstanceId, String userId);

    Integer countByUserId(String userId);

    @Modifying
    @Transactional(readOnly = false)
    @Query("delete OfficeFollow t where t.processInstanceId=?1")
    void deleteByProcessInstanceId(String processInstanceId);

    @Modifying
    @Transactional(readOnly = false)
    @Query("delete OfficeFollow t where t.processInstanceId=?1 and t.userId=?2")
    void deleteByProcessInstanceId(String processInstanceId, String userId);

    @Query("from OfficeFollow h where h.userId = ?1 and (h.documentTitle like ?2 or h.numbers like ?2) ")
    public Page<OfficeFollow> findByParamsLike(String userId, String searchName, Pageable pageable);

    List<OfficeFollow> findByProcessInstanceId(String processInstanceId);

    OfficeFollow findByProcessInstanceIdAndUserId(String processInstanceId, String userId);

    @Query("from OfficeFollow h where h.userId = ?1")
    public Page<OfficeFollow> findByUserId(String userId, Pageable pageable);

}
