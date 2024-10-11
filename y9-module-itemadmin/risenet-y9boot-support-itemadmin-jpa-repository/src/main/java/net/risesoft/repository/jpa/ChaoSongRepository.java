package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.ChaoSong;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface ChaoSongRepository extends JpaRepository<ChaoSong, String>, JpaSpecificationExecutor<ChaoSong> {

    int countBySenderIdAndProcessInstanceId(String senderId, String processInstanceId);

    int countBySenderIdIsNotAndProcessInstanceId(String senderId, String processInstanceId);

    int countByUserId(String userId);

    int countByUserIdAndOpinionState(String userId, String opinionState);

    int countByUserIdAndProcessInstanceId(String userId, String processInstanceId);

    int countByUserIdAndStatus(String userId, Integer status);

    @Modifying
    @Transactional(readOnly = false)
    void deleteByProcessInstanceId(String processInstanceId);

    List<ChaoSong> findByProcessInstanceId(String processInstanceId);

}
