package net.risesoft.service;

import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
public interface ActRuDetailService {

    /**
     * 办结未启动流程的件
     *
     * @param processSerialNumber
     * @return
     */
    Y9Result<String> complete(String processSerialNumber);

    /**
     * 保存流程参与人信息
     *
     * @param itemId
     * @param processSerialNumber
     * @return
     */
    Y9Result<String> saveOrUpdate(String itemId, String processSerialNumber);
}
