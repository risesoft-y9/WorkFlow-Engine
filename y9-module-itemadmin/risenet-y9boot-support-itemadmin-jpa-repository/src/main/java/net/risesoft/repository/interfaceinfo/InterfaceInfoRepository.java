package net.risesoft.repository.interfaceinfo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.interfaceinfo.InterfaceInfo;

/**
 * @author zhangchongjie
 * @date 2024/05/23
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface InterfaceInfoRepository
    extends JpaRepository<InterfaceInfo, String>, JpaSpecificationExecutor<InterfaceInfo> {

    List<InterfaceInfo> findByInterfaceNameLike(String interfaceName);
}
