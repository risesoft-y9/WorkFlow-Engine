package net.risesoft.api.itemadmin;

import net.risesoft.model.itemadmin.DocumentWpsModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface DocumentWpsApi {

    /**
     * 根据id获取正文信息
     * 
     * @param tenantId
     * @param id
     * @return
     */
    DocumentWpsModel findById(String tenantId, String id);

    /**
     * 根据流程编号获取正文
     * 
     * @param tenantId
     * @param processSerialNumber
     * @return
     */
    DocumentWpsModel findByProcessSerialNumber(String tenantId, String processSerialNumber);

    /**
     * 保存正文信息
     * 
     * @param tenantId
     * @param documentWps
     */
    void saveDocumentWps(String tenantId, DocumentWpsModel documentWps);

    /**
     * 保存正文内容状态
     * 
     * @param tenantId
     * @param processSerialNumber
     * @param hasContent
     */
    void saveWpsContent(String tenantId, String processSerialNumber, String hasContent);
}
