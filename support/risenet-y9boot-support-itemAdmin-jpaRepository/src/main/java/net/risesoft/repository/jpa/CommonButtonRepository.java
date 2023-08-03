package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.CommonButton;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface CommonButtonRepository
    extends JpaRepository<CommonButton, String>, JpaSpecificationExecutor<CommonButton> {

    @Override
    @Query("from CommonButton t order by t.createTime asc")
    List<CommonButton> findAll();

    CommonButton findByCustomId(String customId);
}
