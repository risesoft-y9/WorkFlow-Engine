package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.CustomProcessInfo;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface CustomProcessInfoRepository
    extends JpaRepository<CustomProcessInfo, String>, JpaSpecificationExecutor<CustomProcessInfo> {

    @Modifying
    @Transactional(readOnly = false)
    void deleteByProcessSerialNumber(String processSerialNumber);

    List<CustomProcessInfo> findByProcessSerialNumberOrderByTabIndexAsc(String processSerialNumber);

}
