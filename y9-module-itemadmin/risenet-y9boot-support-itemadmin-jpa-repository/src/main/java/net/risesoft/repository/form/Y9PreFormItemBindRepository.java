package net.risesoft.repository.form;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.form.Y9PreFormItemBind;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface Y9PreFormItemBindRepository
    extends JpaRepository<Y9PreFormItemBind, String>, JpaSpecificationExecutor<Y9PreFormItemBind> {

    Y9PreFormItemBind findByItemId(String itemId);

    @Transactional
    void deleteByItemId(String itemId);

}
