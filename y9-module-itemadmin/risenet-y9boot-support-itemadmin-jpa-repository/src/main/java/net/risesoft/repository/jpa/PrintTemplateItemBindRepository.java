package net.risesoft.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.ItemPrintTemplateBind;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface PrintTemplateItemBindRepository
    extends JpaRepository<ItemPrintTemplateBind, String>, JpaSpecificationExecutor<ItemPrintTemplateBind> {

    @Query(" from ItemPrintTemplateBind p where p.itemId=?1")
    ItemPrintTemplateBind findByItemId(String itemId);

    @Query(" from ItemPrintTemplateBind p where p.itemId=?1 and p.templateId=?2")
    ItemPrintTemplateBind findByItemIdAndTemplateId(String itemId, String templateId);

    @Transactional
    void deleteByItemId(String itemId);

}
