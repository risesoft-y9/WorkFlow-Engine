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
     * @param tenantId 租户id
     * @param id id
     * @return DocumentWpsModel
     */
    DocumentWpsModel findById(String tenantId, String id);

    /**
     * 根据流程编号获取正文
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程序列号
     * @return DocumentWpsModel
     */
    DocumentWpsModel findByProcessSerialNumber(String tenantId, String processSerialNumber);

    /**
     * 保存正文信息
     *
     * @param tenantId 租户id
     * @param documentWps wps文档对象
     */
    void saveDocumentWps(String tenantId, DocumentWpsModel documentWps);

    /**
     * 保存正文内容状态
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程序列号
     * @param hasContent 是否有内容
     */
    void saveWpsContent(String tenantId, String processSerialNumber, String hasContent);
}
