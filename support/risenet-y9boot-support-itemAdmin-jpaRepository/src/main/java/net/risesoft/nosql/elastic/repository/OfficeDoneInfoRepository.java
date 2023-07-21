package net.risesoft.nosql.elastic.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import net.risesoft.nosql.elastic.entity.OfficeDoneInfo;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface OfficeDoneInfoRepository extends ElasticsearchRepository<OfficeDoneInfo, String> {

    OfficeDoneInfo findByProcessInstanceIdAndTenantId(String processInstanceId, String tenantId);
}
