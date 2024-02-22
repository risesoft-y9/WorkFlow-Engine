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
    public void copyCalendarConfig(String sourceTenantId, String targetTenantId);

    /**
     * Description:
     * 
     * @param sourceTenantId
     * @param targetTenantId
     * @param itemId
     */
    public void copyCommonButton(String sourceTenantId, String targetTenantId, String itemId);

    /**
     * Description:
     * 
     * @param sourceTenantId
     * @param targetTenantId
     */
    public void copyDynamicRole(String sourceTenantId, String targetTenantId);

    /**
     * Description:
     * 
     * @param sourceTenantId
     * @param targetTenantId
     * @param itemId
     */
    public void copyForm(String sourceTenantId, String targetTenantId, String itemId);

    /**
     * Description:
     * 
     * @param sourceTenantId
     * @param targetTenantId
     * @param itemId
     */
    public void copyItem(String sourceTenantId, String targetTenantId, String itemId);

    /**
     * Description:
     * 
     * @param sourceTenantId
     * @param targetTenantId
     * @param itemId
     */
    public void copyItemViewConf(String sourceTenantId, String targetTenantId, String itemId);

    /**
     * Description:
     * 
     * @param sourceTenantId
     * @param targetTenantId
     * @param itemId
     */
    public void copyOpinionFrame(String sourceTenantId, String targetTenantId, String itemId);

    /**
     * Description:
     * 
     * @param sourceTenantId
     * @param targetTenantId
     * @param itemId
     */
    public void copyOrganWord(String sourceTenantId, String targetTenantId, String itemId);

    /**
     * Description:
     * 
     * @param sourceTenantId
     * @param targetTenantId
     * @param itemId
     * @param roleIdMap
     */
    public void copyPerm(String sourceTenantId, String targetTenantId, String itemId, Map<String, String> roleIdMap);

    /**
     * Description:
     * 
     * @param sourceTenantId
     * @param targetTenantId
     * @param itemId
     */
    public void copyPrintTemplate(String sourceTenantId, String targetTenantId, String itemId);

    /**
     * Description:
     * 
     * @param sourceTenantId
     * @param targetTenantId
     * @param itemId
     */
    public void copySendButton(String sourceTenantId, String targetTenantId, String itemId);

    /**
     * Description:
     * 
     * @param sourceTenantId
     * @param targetTenantId
     * @param itemId
     */
    public void copyTabEntity(String sourceTenantId, String targetTenantId, String itemId);

    /**
     * Description:
     * 
     * @param sourceTenantId
     * @param targetTenantId
     */
    public void copyTaoHongTemplate(String sourceTenantId, String targetTenantId);

    /**
     * Description:
     * 
     * @param sourceTenantId
     * @param targetTenantId
     * @param itemId
     * @return
     */
    public Map<String, String> copyTenantRole(String sourceTenantId, String targetTenantId, String itemId);

    /**
     * Description:
     * 
     * @param sourceTenantId
     * @param targetTenantId
     * @param itemId
     */
    public void copyWordTemplate(String sourceTenantId, String targetTenantId, String itemId);

    /**
     * Description: 从源租户复制事项相关的数据到目标租户
     * 
     * @param sourceTenantId
     * @param targetTenantId
     * @param itemId
     * @throws Exception
     */
    public void dataCopy(String sourceTenantId, String targetTenantId, String itemId) throws Exception;

    /**
     * Description: 从源租户复制事项相关的数据到目标租户
     * 
     * @param sourceTenantId
     * @param targetTenantId
     * @param systemName
     * @throws Exception
     */
    public void dataCopy4System(String sourceTenantId, String targetTenantId, String systemName) throws Exception;
}
