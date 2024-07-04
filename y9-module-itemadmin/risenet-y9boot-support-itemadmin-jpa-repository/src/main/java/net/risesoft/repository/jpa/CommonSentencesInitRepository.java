package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.CommonSentencesInit;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface CommonSentencesInitRepository
    extends JpaRepository<CommonSentencesInit, String>, JpaSpecificationExecutor<CommonSentencesInit> {

    /**
     * 根据人员id，获取初始化记录
     * 
     * @param userId
     * @return
     */
    @Query("from CommonSentencesInit t where t.userId=?1")
    List<CommonSentencesInit> findByUserId(String userId);
}
