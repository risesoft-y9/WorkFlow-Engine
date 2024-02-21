package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.ExtranetEformItemBind;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface ExtranetEformItemBindRepository
    extends JpaRepository<ExtranetEformItemBind, String>, JpaSpecificationExecutor<ExtranetEformItemBind> {

    /**
     * 根据事项id获取数据
     * 
     * @param itemId
     * @return
     */
    @Query("From ExtranetEformItemBind t where t.itemId=?1 order by t.tabIndex asc")
    List<ExtranetEformItemBind> findByItemId(String itemId);

    /**
     * 获取最大序号
     * 
     * @param itemId
     * @return
     */
    @Query("select max(tabIndex) from ExtranetEformItemBind where itemId=?1")
    Integer getMaxTabIndex(String itemId);

}
