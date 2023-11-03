package net.risesoft.api.itemadmin;

import java.util.List;
import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface TransactionWordApi {

    /**
     * 根据流程编号删除正文，同时删除文件系统的文件
     *
     * @param tenantId 租户id
     * @param processSerialNumbers 流程序列号列表
     */
    void delBatchByProcessSerialNumbers(String tenantId, List<String> processSerialNumbers);

    /**
     * 删除撤销PDF文件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程序列号
     * @param isTaoHong 是否套红
     */
    void deleteByIsTaoHong(String tenantId, String userId, String processSerialNumber, String isTaoHong);

    /**
     * 获取正文文件信息（数据传输）
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @return Map&lt;String, Object&gt;
     */
    List<Map<String, Object>> exchangeFindWordByProcessSerialNumber(String tenantId, String userId,
        String processSerialNumber);

    /**
     *
     * Description: 打开历史文件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> findHistoryVersionDoc(String tenantId, String userId, String taskId);

    /**
     *
     * Description: 获取正文文件信息
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> findWordByProcessSerialNumber(String tenantId, String processSerialNumber);

    /**
     * 获取正文列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @return List&lt;Map&lt;String, Object&gt; &gt;
     */
    List<Map<String, Object>> getWordList(String tenantId, String userId, String processSerialNumber);

    /**
     * 打开正文
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @param itemId 事项id
     * @return String
     */
    String openDocument(String tenantId, String userId, String processSerialNumber, String itemId);

    /**
     *
     * Description:
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return String
     */
    String openDocumentByProcessSerialNumber(String tenantId, String processSerialNumber);

    /**
     *
     * Description: 套红模板
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param templateGuid 模板id
     * @return String
     */
    String openDocumentTemplate(String tenantId, String userId, String templateGuid);

    /**
     * 打开历史文件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     */
    void openHistoryVersionDoc(String tenantId, String userId, String taskId);

    /**
     * 打开PDF
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @return String
     */
    String openPdf(String tenantId, String userId, String processSerialNumber);

    /**
     * 打开撤销PDF后的正文
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @param isTaoHong 是否套红
     * @return String
     */
    String openRevokePdfAfterDocument(String tenantId, String userId, String processSerialNumber, String isTaoHong);

    /**
     * 选择套红
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param activitiUser activitiUser
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> openTaoHong(String tenantId, String userId, String activitiUser);

    /**
     * 保存公文传输转入工作流的正文信息
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param docjson 正文json信息
     * @param processSerialNumber 流程序列号
     * @return Boolean
     */
    Boolean saveImportTransationWord(String tenantId, String userId, String docjson, String processSerialNumber);

    /**
     * 获取正文
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @param itemId 事项id
     * @param itembox 办件状态，todo（待办），doing（在办），done（办结）
     * @param taskId 任务id
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> showWord(String tenantId, String userId, String processSerialNumber, String itemId,
        String itembox, String taskId);

    /**
     * 获取套红模板列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param currentBureauGuid 委办局id
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    List<Map<String, Object>> taoHongTemplateList(String tenantId, String userId, String currentBureauGuid);

    /**
     *
     * Description: 草稿箱保存正文
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param documentTitle 文件标题
     * @param fileType 文件类型
     * @param processSerialNumber 流程编号
     * @param isTaoHong 是否套红
     * @param taskId 任务id
     * @param fileSizeString 文件大小
     * @param fileStoreId 文件id
     * @return String
     */
    String uploadWord(String tenantId, String userId, String documentTitle, String fileType, String processSerialNumber,
        String isTaoHong, String taskId, String fileSizeString, String fileStoreId);

    /**
     * 下载正文
     *
     * @param tenantId 租户Id
     * @param id id
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> wordDownload(String tenantId, String id);
}
