package net.risesoft.service;

import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ItemDataCopyService {

    /**
     * Description:
     * 
     * @param sourceTenantId
     * @param targetTenantId
     */
    void copyCalendarConfig(String sourceTenantId, String targetTenantId);

    /**
     * Description:
     * 
     * @param sourceTenantId
     * @param targetTenantId
     * @param itemId
     */
    void copyCommonButton(String sourceTenantId, String targetTenantId, String itemId);

    /**
     * Description:
     * 
     * @param sourceTenantId
     * @param targetTenantId
     */
    void copyDynamicRole(String sourceTenantId, String targetTenantId);

    /**
     * Description:
     * 
     * @param sourceTenantId
     * @param targetTenantId
     * @param itemId
     */
    void copyForm(String sourceTenantId, String targetTenantId, String itemId);

    /**
     * Description:
     * 
     * @param sourceTenantId
     * @param targetTenantId
     * @param itemId
     */
    void copyItem(String sourceTenantId, String targetTenantId, String itemId);

    /**
     * Description:
     * 
     * @param sourceTenantId
     * @param targetTenantId
     * @param itemId
     */
    void copyItemViewConf(String sourceTenantId, String targetTenantId, String itemId);

    /**
     * Description:
     * 
     * @param sourceTenantId
     * @param targetTenantId
     * @param itemId
     */
    void copyOpinionFrame(String sourceTenantId, String targetTenantId, String itemId);

    /**
     * Description:
     * 
     * @param sourceTenantId
     * @param targetTenantId
     * @param itemId
     */
    void copyOrganWord(String sourceTenantId, String targetTenantId, String itemId);

    /**
     * Description:
     * 
     * @param sourceTenantId
     * @param targetTenantId
     * @param itemId
     * @param roleIdMap
     */
    void copyPerm(String sourceTenantId, String targetTenantId, String itemId, Map<String, String> roleIdMap);

    /**
     * Description:
     * 
     * @param sourceTenantId
     * @param targetTenantId
     * @param itemId
     */
    void copyPrintTemplate(String sourceTenantId, String targetTenantId, String itemId);

    /**
     * Description:
     * 
     * @param sourceTenantId
     * @param targetTenantId
     * @param itemId
     */
    void copySendButton(String sourceTenantId, String targetTenantId, String itemId);

    /**
     * Description:
     * 
     * @param sourceTenantId
     * @param targetTenantId
     * @param itemId
     */
    void copyTabEntity(String sourceTenantId, String targetTenantId, String itemId);

    /**
     * Description:
     * 
     * @param sourceTenantId
     * @param targetTenantId
     */
    void copyTaoHongTemplate(String sourceTenantId, String targetTenantId);

    /**
     * Description:
     * 
     * @param sourceTenantId
     * @param targetTenantId
     * @param itemId
     * @return
     */
    Map<String, String> copyTenantRole(String sourceTenantId, String targetTenantId, String itemId);

    /**
     * Description:
     * 
     * @param sourceTenantId
     * @param targetTenantId
     * @param itemId
     */
    void copyWordTemplate(String sourceTenantId, String targetTenantId, String itemId);

    /**
     * Description: 从源租户复制事项相关的数据到目标租户
     * 
     * @param sourceTenantId
     * @param targetTenantId
     * @param itemId
     * @throws Exception
     */
    void dataCopy(String sourceTenantId, String targetTenantId, String itemId) throws Exception;

    /**
     * Description: 从源租户复制事项相关的数据到目标租户
     * 
     * @param sourceTenantId
     * @param targetTenantId
     * @param systemName
     * @throws Exception
     */
    void dataCopy4System(String sourceTenantId, String targetTenantId, String systemName) throws Exception;
}
