package net.risesoft.nosql.elastic.repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import net.risesoft.nosql.elastic.entity.ChaoSongInfo;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ChaoSongInfoRepository extends ElasticsearchRepository<ChaoSongInfo, String> {

    int countBySenderIdAndProcessInstanceId(String senderId, String processInstanceId);

    int countBySenderIdIsNotAndProcessInstanceId(String senderId, String processInstanceId);

    int countByUserIdAndOpinionStateAndTenantId(String userId, String opinionState, String tenantId);

    int countByUserIdAndStatus(String userId, Integer status);

    int countByUserIdAndTenantId(String userId, String tenantId);

    List<ChaoSongInfo> findByProcessInstanceId(String processInstanceId);
}
