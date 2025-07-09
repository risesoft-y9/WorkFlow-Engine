package net.risesoft.repository.template;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.template.BookMarkBind;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface BookMarkBindRepository
    extends JpaRepository<BookMarkBind, String>, JpaSpecificationExecutor<BookMarkBind> {

    @Modifying
    @Transactional(readOnly = false)
    void deleteByWordTemplateIdAndBookMarkName(String wordTemplateId, String bookMarkName);

    List<BookMarkBind> findByWordTemplateId(String wordTemplateId);

    BookMarkBind findByWordTemplateIdAndBookMarkName(String wordTemplateId, String bookMarkName);
}
