package net.risesoft.kafka;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.model.platform.Person;
import net.risesoft.service.ItemDataCopyService;
import net.risesoft.service.SyncYearTableService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.pubsub.constant.Y9TopicConst;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Component
@Slf4j
public class KafkaConsumer {

    protected final Logger LOG = LoggerFactory.getLogger("KafkaConsumer");

    @Autowired
    private ItemDataCopyService itemDataCopyService;

    @Autowired
    private PersonApi personManager;

    @Autowired
    private SyncYearTableService syncYearTableService;

    /**
     * 监听消费kafka队列消息
     */
    @KafkaListener(topics = {Y9TopicConst.Y9_DATACOPY_MESSAGE, Y9TopicConst.Y9_DATACOPY4SYSTEM_MESSAGE})
    public void onMessage(ConsumerRecord<Integer, String> message) {
        String tenantId = "";
        try {
            String msg = message.value();
            String topic = message.topic();
            LOGGER.info("****************开始消费topic:{},value:{}******************", topic, msg);
            if (topic.equals(Y9TopicConst.Y9_DATACOPY_MESSAGE)) {
                HashMap<String, Object> map = Y9JsonUtil.readHashMap(msg);
                String sourceTenantId = (String)map.get("sourceTenantId");
                String targetTenantId = (String)map.get("targetTenantId");
                tenantId = targetTenantId;
                String itemId = (String)map.get("itemId");
                String personId = (String)map.get("personId");
                Person person = personManager.getPerson(targetTenantId, personId).getData();
                Y9LoginUserHolder.setPerson(person);
                itemDataCopyService.dataCopy(sourceTenantId, targetTenantId, itemId);

            } else if (topic.equals(Y9TopicConst.Y9_DATACOPY4SYSTEM_MESSAGE)) {
                HashMap<String, Object> map = Y9JsonUtil.readHashMap(msg);
                String sourceTenantId = (String)map.get("sourceTenantId");
                String targetTenantId = (String)map.get("targetTenantId");
                tenantId = targetTenantId;
                String systemName = (String)map.get("systemName");
                String personId = (String)map.get("personId");
                Person person = personManager.getPerson(targetTenantId, personId).getData();
                Y9LoginUserHolder.setPerson(person);
                itemDataCopyService.dataCopy4System(sourceTenantId, targetTenantId, systemName);
            }
            Integer key = message.key();
            long offset = message.offset();
            int partition = message.partition();
            LOG.info("---topic:" + topic);
            LOG.info("---value:" + msg);
            LOG.info("---key:" + key);
            LOG.info("---offset:" + offset);
            LOG.info("---partition:" + partition);
            LOGGER.info("*****************************消费成功********************************");
        } catch (Exception e) {
            LOGGER.error("{}消费失败", message.topic(), e);
        }

        // 生成当年年度表结构
        if (StringUtils.isNotBlank(tenantId)) {
            Y9LoginUserHolder.setTenantId(tenantId);
            syncYearTableService.syncYearTable("");
        }

    }

}
