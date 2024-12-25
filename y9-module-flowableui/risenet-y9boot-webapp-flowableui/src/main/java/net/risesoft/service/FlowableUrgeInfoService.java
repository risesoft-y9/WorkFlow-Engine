package net.risesoft.service;

import java.util.List;

import net.risesoft.model.itemadmin.UrgeInfoModel;
import net.risesoft.pojo.Y9Result;

/**
 * @author : qinman
 * @date : 2024-12-24
 * @since 9.6.8
 **/
public interface FlowableUrgeInfoService {

    /**
     * 删除催办信息
     *
     * @param id 催办信息唯一标示
     * @return Y9Result<Object>
     */
    Y9Result<Object> deleteById(String id);

    /**
     * 查看催办详情
     *
     * @param processSerialNumber 流程序列号
     * @return
     */
    List<UrgeInfoModel> findByProcessSerialNumber(String processSerialNumber);

    /**
     * 保存催办信息
     *
     * @param processSerialNumbers 流程序列号
     * @param msgContent 催办消息
     * @return
     */
    Y9Result<Object> save(String[] processSerialNumbers, String msgContent);
}
