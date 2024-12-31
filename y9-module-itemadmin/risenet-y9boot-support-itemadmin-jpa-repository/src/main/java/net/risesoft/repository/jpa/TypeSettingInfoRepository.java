package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.TypeSettingInfo;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface TypeSettingInfoRepository
    extends JpaRepository<TypeSettingInfo, String>, JpaSpecificationExecutor<TypeSettingInfo> {

    @Modifying
    @Transactional
    void deleteByProcessSerialNumberAndIdNotIn(String processSerialNumber, List<String> ids);

    List<TypeSettingInfo> findByProcessSerialNumberOrderByTabIndexAsc(String processSerialNumber);

    @Query("select max(s.tabIndex) from TypeSettingInfo s where s.processSerialNumber = ?1")
    Integer getMaxTabIndex(String processSerialNumber);
}
