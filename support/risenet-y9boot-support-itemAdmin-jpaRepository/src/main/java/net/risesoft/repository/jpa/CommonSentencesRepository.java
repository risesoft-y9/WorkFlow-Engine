package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.CommonSentences;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface CommonSentencesRepository
    extends JpaRepository<CommonSentences, String>, JpaSpecificationExecutor<CommonSentences> {

    /**
     * 根据人员id获取常用语
     * 
     * @param userId
     * @return
     */
    @Query("from CommonSentences t where t.userId=?1 order by t.tabIndex")
    public List<CommonSentences> findByUserId(String userId);

    /**
     * 根据人员id，序号获取常用语
     * 
     * @param userId
     * @param tabIndex
     * @return
     */
    @Query("from CommonSentences t where t.userId=?1 and t.tabIndex=?2")
    public CommonSentences findByUserIdAndTabIndex(String userId, int tabIndex);
}
