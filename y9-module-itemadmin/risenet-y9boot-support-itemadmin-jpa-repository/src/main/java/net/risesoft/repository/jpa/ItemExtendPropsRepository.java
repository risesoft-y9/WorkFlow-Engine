package net.risesoft.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.ItemExtendProps;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ItemExtendPropsRepository extends PagingAndSortingRepository<ItemExtendProps, String>,
    JpaRepository<ItemExtendProps, String>, JpaSpecificationExecutor<ItemExtendProps> {

    /**
     * 根据itemId删除
     *
     * @param itemId
     */
    @Transactional(readOnly = false)
    @Modifying
    void deleteByItemId(String itemId);

    ItemExtendProps findByItemId(String itemId);
}