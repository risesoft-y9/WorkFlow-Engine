package net.risesoft.service;

import java.util.Map;

import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ItemDataTransferService {

    /**
     * Description:
     *
     * @param processDefinitionId
     * @param processInstanceId
     * @return
     */
    Y9Result<String> dataTransfer(String processDefinitionId, String processInstanceId);

    /**
     * Description:
     *
     * @param itemId
     * @param processDefinitionId
     * @param page
     * @param rows
     * @return
     */
    Y9Page<Map<String, Object>> pageByItemIdAndProcessDefinitionId(String itemId, String processDefinitionId,
        Integer page, Integer rows);

}
