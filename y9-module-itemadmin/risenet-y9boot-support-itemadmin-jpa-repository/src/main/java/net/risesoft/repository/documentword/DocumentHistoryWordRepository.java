package net.risesoft.repository.documentword;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.documentword.DocumentHistoryWord;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Repository
public interface DocumentHistoryWordRepository
    extends JpaRepository<DocumentHistoryWord, String>, JpaSpecificationExecutor<DocumentHistoryWord> {

    List<DocumentHistoryWord> findByProcessSerialNumberAndWordTypeOrderByUpdateDateAsc(String processSerialNumber,
        String wordType);
}
