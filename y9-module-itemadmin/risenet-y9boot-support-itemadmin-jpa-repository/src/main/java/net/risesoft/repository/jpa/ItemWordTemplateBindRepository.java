package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.ItemWordTemplateBind;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface ItemWordTemplateBindRepository
    extends JpaRepository<ItemWordTemplateBind, String>, JpaSpecificationExecutor<ItemWordTemplateBind> {

    ItemWordTemplateBind findByItemIdAndProcessDefinitionId(String itemId, String processDefinitionId);

    ItemWordTemplateBind findByItemIdAndProcessDefinitionIdAndTemplateId(String itemId, String processDefinitionId,String templateId);

    ItemWordTemplateBind findByItemIdAndProcessDefinitionIdAndBindValue(String itemId, String processDefinitionId,
        String bindValue);

    ItemWordTemplateBind findByItemIdAndTemplateId(String itemId, String templateId);

    List<ItemWordTemplateBind> findByItemIdOrderByBindValueAsc(String itemId);

    List<ItemWordTemplateBind> findByItemIdAndProcessDefinitionIdOrderByBindStatus(String itemId,
        String processDefinitionId);

    @Transactional
    void deleteByItemId(String itemId);
}