package net.risesoft.repository.jpa;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.GwLwLinkBw;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface GwLwLinkBwRepository extends JpaRepository<GwLwLinkBw, String>, JpaSpecificationExecutor<GwLwLinkBw> {

    @Modifying
    @Transactional(readOnly = false)
    void deleteByProcessSerialNumber(String processSerialNumber);

    List<GwLwLinkBw> findByProcessSerialNumber(String processSerialNumber);

    @Query("from GwLwLinkBw where processSerialNumber = ?1 and lwInfoUid = ?2 ")
    GwLwLinkBw findByProcessSerialNumberAndLwinfoUid(String processSerialNumber, String lwInfoUid);
}
