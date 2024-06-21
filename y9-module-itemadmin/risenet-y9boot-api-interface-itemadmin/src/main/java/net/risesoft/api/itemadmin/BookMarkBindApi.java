package net.risesoft.api.itemadmin;

import net.risesoft.pojo.Y9Result;


import java.util.Map;

/**
 * 书签绑定接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface BookMarkBindApi {

    /**
     * 根据模板和流程序列号查询模板的书签对应的值
     *
     * @param tenantId            租户id
     * @param wordTemplateId      模板id
     * @param processSerialNumber 流程编号
     * @return Y9Result<Map < String, Object>>
     */
    Y9Result<Map<String, Object>> getBookMarkData(String tenantId, String wordTemplateId, String processSerialNumber);
}
