package net.risesoft.repository.form;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.form.Y9FormOptionClass;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface Y9FormOptionClassRepository extends JpaRepository<Y9FormOptionClass, String>, JpaSpecificationExecutor<Y9FormOptionClass> {

    List<Y9FormOptionClass> findByNameContaining(String name);

    Y9FormOptionClass findByType(String type);

}
