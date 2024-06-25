package net.risesoft.api.itemadmin;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import net.risesoft.model.itemadmin.TaoHongTemplateModel;
import net.risesoft.model.itemadmin.TransactionHistoryWordModel;
import net.risesoft.model.itemadmin.TransactionWordModel;
import net.risesoft.model.itemadmin.Y9WordInfo;
import net.risesoft.pojo.Y9Result;

/**
 * 正文接口管理
 * 
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
     * @return {@code Y9Result<Object>} 通用请求返回对象
     */
    @PostMapping(value = "/delBatchByProcessSerialNumbers")
    Y9Result<Object> delBatchByProcessSerialNumbers(String tenantId, List<String> processSerialNumbers);

    /**
     * 删除撤销PDF文件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程序列号
     * @param isTaoHong 是否套红
     *
     * @return {@code Y9Result<Object>} 通用请求返回对象
     */
    @PostMapping(value = "/deleteByIsTaoHong")
    Y9Result<Object> deleteByIsTaoHong(String tenantId, String userId, String processSerialNumber, String isTaoHong);

    /**
     * 获取正文文件信息（数据传输）
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<TransactionWordModel>} 通用请求返回对象 - data 是正文文件信息
     */
    @GetMapping(value = "/exchangeFindWordByProcessSerialNumber")
    Y9Result<TransactionWordModel> exchangeFindWordByProcessSerialNumber(String tenantId, String userId,
        String processSerialNumber);

    /**
     *
     * Description: 打开历史文件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     * @return {@code Y9Result<TransactionHistoryWordModel>} 通用请求返回对象 - data 是历史正文文件信息对象
     */
    @GetMapping(value = "/findHistoryVersionDoc")
    Y9Result<TransactionHistoryWordModel> findHistoryVersionDoc(String tenantId, String userId, String taskId);

    /**
     *
     * Description: 获取正文文件信息
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<TransactionWordModel>} 通用请求返回对象 - data 是正文文件信息
     */
    @GetMapping(value = "/findWordByProcessSerialNumber")
    Y9Result<TransactionWordModel> findWordByProcessSerialNumber(String tenantId, String processSerialNumber);

    /**
     * 获取正文列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<List<TransactionWordModel>>} 通用请求返回对象 - data 是正文文件信息列表
     */
    @GetMapping(value = "/getWordList")
    Y9Result<List<TransactionWordModel>> getWordList(String tenantId, String userId, String processSerialNumber);

    /**
     * 打开正文
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @param itemId 事项id
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是正文文件地址
     */
    @GetMapping(value = "/openDocument")
    Y9Result<String> openDocument(String tenantId, String userId, String processSerialNumber, String itemId);

    /**
     *
     * Description: 根据流程编号打开正文
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是正文文件地址
     */
    @GetMapping(value = "/openDocumentByProcessSerialNumber")
    Y9Result<String> openDocumentByProcessSerialNumber(String tenantId, String processSerialNumber);

    /**
     *
     * Description: 套红模板
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param templateGuid 模板id
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是套红文件地址
     */
    @GetMapping(value = "/openDocumentTemplate")
    Y9Result<String> openDocumentTemplate(String tenantId, String userId, String templateGuid);

    /**
     * 打开历史文件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     */
    @GetMapping(value = "/openHistoryVersionDoc")
    Y9Result<Object> openHistoryVersionDoc(String tenantId, String userId, String taskId);

    /**
     * 打开PDF
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是PDF文件地址
     */
    @GetMapping(value = "/openPdf")
    Y9Result<String> openPdf(String tenantId, String userId, String processSerialNumber);

    /**
     * 打开撤销PDF后的正文
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @param isTaoHong 是否套红
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是PDF文件地址
     */
    @GetMapping(value = "/openRevokePDFAfterDocument")
    Y9Result<String> openRevokePdfAfterDocument(String tenantId, String userId, String processSerialNumber,
        String isTaoHong);

    /**
     * 选择套红
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param activitiUser activitiUser
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是当前人员的委办局GUID
     */
    @GetMapping(value = "/openTaoHong")
    Y9Result<String> openTaoHong(String tenantId, String userId, String activitiUser);

    /**
     * 保存公文传输转入工作流的正文信息
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param docjson 正文json信息
     * @param processSerialNumber 流程序列号
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - data 是保存是否成功的信息
     */
    @PostMapping(value = "/saveImportTransationWord")
    Y9Result<Boolean> saveImportTransationWord(String tenantId, String userId, String docjson,
        String processSerialNumber);

    /**
     * 获取正文
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @param itemId 事项id
     * @param itembox 办件状态，todo（待办），doing（在办），done（办结）
     * @param taskId 任务id
     * @return {@code Y9Result<WordInfo>} 通用请求返回对象 - data 是正文详情
     */
    @GetMapping(value = "/showWord")
    Y9Result<Y9WordInfo> showWord(String tenantId, String userId, String processSerialNumber, String itemId,
        String itembox, String taskId);

    /**
     * 获取套红模板列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param currentBureauGuid 委办局id
     * @return {@code Y9Result<List<TaoHongTemplateModel>>} 通用请求返回对象 - data 是套红模板列表
     */
    @GetMapping(value = "/taoHongTemplateList")
    Y9Result<List<TaoHongTemplateModel>> taoHongTemplateList(String tenantId, String userId, String currentBureauGuid);

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
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - data 是保存是否成功的信息
     */
    @PostMapping(value = "/uploadWord")
    Y9Result<Boolean> uploadWord(String tenantId, String userId, String documentTitle, String fileType,
        String processSerialNumber, String isTaoHong, String taskId, String fileSizeString, String fileStoreId);

    /**
     * 下载正文
     *
     * @param tenantId 租户Id
     * @param id id
     * @return {@code Y9Result<WordInfo>} 通用请求返回对象 - data 是正文详情
     */
    @GetMapping(value = "/wordDownload")
    Y9Result<TransactionWordModel> wordDownload(String tenantId, String id);
}
