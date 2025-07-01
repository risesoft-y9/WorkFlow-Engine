package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.AfterWorkInfo;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface AfterWorkInfoRepository
    extends JpaRepository<AfterWorkInfo, String>, JpaSpecificationExecutor<AfterWorkInfo> {

    List<AfterWorkInfo> findByProcessSerialNumberOrderByRecordTimeDesc(String processSerialNumber);
}