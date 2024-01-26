package net.risesoft.repository.jpa;

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
}