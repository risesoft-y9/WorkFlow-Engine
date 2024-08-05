package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import net.risesoft.entity.WordTemplate;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface WordTemplateRepository
    extends JpaRepository<WordTemplate, String>, JpaSpecificationExecutor<WordTemplate> {
    @Override
    @Query("from WordTemplate t order by t.uploadTime desc")
    List<WordTemplate> findAll();

    List<WordTemplate> findByBureauIdOrderByUploadTimeDesc(String bureauId);

    List<WordTemplate> findByBureauIdAndFileNameContainingOrderByUploadTimeDesc(String bureauId, String fileName);
}
