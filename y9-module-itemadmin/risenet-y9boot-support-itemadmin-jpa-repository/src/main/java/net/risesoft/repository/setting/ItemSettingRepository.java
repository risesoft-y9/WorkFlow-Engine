package net.risesoft.repository.setting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.settings.ItemSetting;

/**
 * @author qinman
 * @date 2025/08/28
 */
@Repository
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface ItemSettingRepository extends JpaRepository<ItemSetting, String> {

}
