package net.risesoft.repository.form;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.form.Y9ValidType;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface Y9ValidTypeRepository extends JpaRepository<Y9ValidType, String>, JpaSpecificationExecutor<Y9ValidType> {

    List<Y9ValidType> findByValidCnNameLike(String validCnName);

    List<Y9ValidType> findByValidType(String validType);

    List<Y9ValidType> findByValidTypeAndValidCnNameLike(String validType, String validCnName);
}
