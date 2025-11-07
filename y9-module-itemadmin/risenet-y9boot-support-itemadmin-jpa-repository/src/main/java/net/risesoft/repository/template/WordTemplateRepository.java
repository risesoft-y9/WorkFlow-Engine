package net.risesoft.repository.template;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import net.risesoft.entity.template.WordTemplate;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface WordTemplateRepository
    extends JpaRepository<WordTemplate, String>, JpaSpecificationExecutor<WordTemplate> {

    List<WordTemplate> findByBureauId(String bureauId, Sort sort);

    List<WordTemplate> findByBureauIdAndFileNameContaining(String bureauId, String fileName, Sort sort);
}
