package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.GwLwInfo;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface GwLwInfoRepository extends JpaRepository<GwLwInfo, String>, JpaSpecificationExecutor<GwLwInfo> {

    @Modifying
    @Transactional(readOnly = false)
    void deleteByProcessSerialNumber(String processSerialNumber);

    List<GwLwInfo> findByProcessSerialNumber(String processSerialNumber);
}
