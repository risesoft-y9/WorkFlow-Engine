package net.risesoft.api.itemadmin;

import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface BookMarkBindApi {

    /**
     * 根据模板和流程序列号查询模板的书签对应的值
     * 
     * @param tenantId
     * @param wordTemplateId
     * @param processSerialNumber
     * @return
     */
    Map<String, Object> getBookMarkData(String tenantId, String wordTemplateId, String processSerialNumber);
}
