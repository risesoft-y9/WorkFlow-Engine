package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.ItemMappingConf;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface ItemMappingConfRepository
    extends JpaRepository<ItemMappingConf, String>, JpaSpecificationExecutor<ItemMappingConf> {

    @Modifying
    @Transactional(readOnly = false)
    void deleteByMappingId(String mappingId);

    List<ItemMappingConf> findByItemIdAndMappingIdOrderByCreateTimeDesc(String itemId, String mappingId);

    ItemMappingConf findTopByItemIdAndSysTypeOrderByCreateTimeDesc(String itemId, String sysType);

}
