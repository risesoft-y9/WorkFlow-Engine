package net.risesoft.service;

import net.risesoft.pojo.Y9Result;

public interface ProcessParamService {

    /**
     * 保存流程自定义变量
     *
     * @param itemId
     * @param processSerialNumber
     * @param processInstanceId
     * @param documentTitle
     * @param number
     * @param level
     * @param customItem
     * @return
     */
    Y9Result<String> saveOrUpdate(String itemId, String processSerialNumber, String processInstanceId,
        String documentTitle, String number, String level, Boolean customItem);

}
