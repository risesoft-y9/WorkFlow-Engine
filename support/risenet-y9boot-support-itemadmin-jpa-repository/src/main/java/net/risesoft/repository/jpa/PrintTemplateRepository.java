package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.PrintTemplate;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface PrintTemplateRepository
    extends JpaRepository<PrintTemplate, String>, JpaSpecificationExecutor<PrintTemplate> {

    @Query("from PrintTemplate t order by t.uploadTime DESC")
    public List<PrintTemplate> findAllOrderByUploadTimeDesc();

    public List<PrintTemplate> findByFileNameContaining(String fileName);

}
