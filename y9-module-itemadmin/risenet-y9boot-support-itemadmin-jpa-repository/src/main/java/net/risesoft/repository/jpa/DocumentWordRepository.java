package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.DocumentWord;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Repository
public interface DocumentWordRepository
    extends JpaRepository<DocumentWord, String>, JpaSpecificationExecutor<DocumentWord> {

    DocumentWord findByProcessSerialNumberAndWordTypeAndType(String processSerialNumber, String wordType, Integer type);

    @Query("FROM DocumentWord where processSerialNumber =?1 and wordType =?2 order by type desc")
    List<DocumentWord> findByProcessSerialNumberAndWordTypeOderByTypeDesc(String processSerialNumber, String wordType);
}
