package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.MergeFile;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface MergeFileRepository extends JpaRepository<MergeFile, String>, JpaSpecificationExecutor<MergeFile> {

    List<MergeFile> findByIdIn(String[] ids);

    List<MergeFile> findBySourceFileId(String sourceFileId);
}
