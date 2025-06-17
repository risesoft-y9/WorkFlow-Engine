package net.risesoft.repository.jpa;

import net.risesoft.entity.InterfaceInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author zhangchongjie
 * @date 2024/05/23
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface InterfaceInfoRepository
        extends JpaRepository<InterfaceInfo, String>, JpaSpecificationExecutor<InterfaceInfo> {

    List<InterfaceInfo> findByInterfaceNameLike(String interfaceName);
}
