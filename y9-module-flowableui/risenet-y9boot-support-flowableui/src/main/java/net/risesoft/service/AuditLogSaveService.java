package net.risesoft.service;

import net.risesoft.model.itemadmin.DocumentWpsModel;
import net.risesoft.y9public.entity.Y9FileStore;

/**
 * 附件服务接口
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
public interface AuditLogSaveService {

    /**
     * 保存正文信息
     * 
     * @param title
     * @param processSerialNumber
     * @param y9FileStore
     * @return
     */
    void wordUploadLog(String title, String processSerialNumber, Y9FileStore y9FileStore);

    /**
     * 保存wps信息
     *
     * @param title
     * @param processSerialNumber
     * @param y9FileStore
     * @param type
     * @return
     */
    void wpsUploadLog(String title, String processSerialNumber, Y9FileStore y9FileStore, String type);

    /**
     * 下载历史文档
     *
     * @param fileStoreId
     * @param title
     * @param downloadType
     */
    void downLoadWordLog(String fileStoreId, String title, String downloadType);

    /**
     * 下载wps文档
     *
     * @param documentWpsModel
     */
    void downloadWpsLog(DocumentWpsModel documentWpsModel);

    /**
     * 下载wps文档
     *
     * @param id
     * @param title
     * @param type
     */
    void downloadWpsLog(String id, String title, String type);

    /**
     * 撤销红头
     * 
     * @param id
     * @param title
     * @param type
     */
    void revokeRedHeaderLog(String id, String title, String type);
}
