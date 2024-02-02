package net.risesoft.service;

import net.risesoft.entity.OrganWordUseHistory;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface OrganWordUseHistoryService {

    /**
     * 根据事项唯一标示和编号值查找
     *
     * @param itemId
     * @param numberString
     * @return
     */
    OrganWordUseHistory findByItemIdAndNumberString(String itemId, String numberString);

    /**
     * 查找流程实例序列号的custom使用详情
     *
     * @param processSerialNumber
     * @param custom
     * @return
     */
    OrganWordUseHistory findByProcessSerialNumberAndCustom(String processSerialNumber, String custom);

    /**
     * 保存机关待字使用详情
     *
     * @param organWordUseHistory
     * @return
     */
    OrganWordUseHistory save(OrganWordUseHistory organWordUseHistory);
}
