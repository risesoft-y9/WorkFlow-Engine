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

    /**
     * 查询编号有没有使用
     * 
     * @param itemId
     * @param numberString
     * @param custom
     * @return
     */
    OrganWordUseHistory findByItemIdAndNumberStringAndCustom(String itemId, String numberString, String custom);

    /**
     * 查询当前流程是否已经存在
     * 
     * @param itemId
     * @param numberString
     * @param custom
     * @param processSerialNumber
     * @return
     */
    OrganWordUseHistory findByItemIdAndNumberStrAndCustomAndProcessSerialNumber(String itemId, String numberString,
        String custom, String processSerialNumber);
}
