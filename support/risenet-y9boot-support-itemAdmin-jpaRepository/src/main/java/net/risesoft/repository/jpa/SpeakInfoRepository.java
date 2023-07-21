package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import net.risesoft.entity.SpeakInfo;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface SpeakInfoRepository extends JpaRepository<SpeakInfo, String>, JpaSpecificationExecutor<SpeakInfo> {

    int countByProcessInstanceIdAndDeletedFalseAndUserIdNotAndReadUserIdNotLike(String processInstanceId, String userId, String readUserId);

    List<SpeakInfo> findByProcessInstanceIdAndDeletedFalseOrderByCreateTimeAsc(String processInstanceId);
}
