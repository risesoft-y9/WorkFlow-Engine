package net.risesoft.service;

import net.risesoft.pojo.Y9Result;

public interface ActRuDetailService {

    /**
     * 办结未启动流程的件
     *
     * @param processSerialNumber 流程编号
     * @return Y9Result<String>
     */
    Y9Result<String> complete(String processSerialNumber);

    /**
     * 保存流程参与人信息
     *
     * @param itemId              事项id
     * @param processSerialNumber 流程编号
     * @return Y9Result<String>
     */
    Y9Result<String> saveOrUpdate(String itemId, String processSerialNumber);
}
