package net.risesoft.api.itemadmin;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

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
    Y9Result<Object> delBatchByProcessSerialNumbers(@RequestParam("tenantId") String tenantId,
        @RequestBody List<String> processSerialNumbers);

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
    Y9Result<Object> deleteByIsTaoHong(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("isTaoHong") String isTaoHong);

    /**
     * 获取正文文件信息（数据传输）
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<TransactionWordModel>} 通用请求返回对象 - data 是正文文件信息
     */
    @GetMapping(value = "/exchangeFindWordByProcessSerialNumber")
    Y9Result<TransactionWordModel> exchangeFindWordByProcessSerialNumber(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processSerialNumber") String processSerialNumber);

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
    Y9Result<TransactionHistoryWordModel> findHistoryVersionDoc(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("taskId") String taskId);

    /**
     *
     * Description: 获取正文文件信息
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<TransactionWordModel>} 通用请求返回对象 - data 是正文文件信息
     */
    @GetMapping(value = "/findWordByProcessSerialNumber")
    Y9Result<TransactionWordModel> findWordByProcessSerialNumber(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 获取正文列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<List<TransactionWordModel>>} 通用请求返回对象 - data 是正文文件信息列表
     */
    @GetMapping(value = "/getWordList")
    Y9Result<List<TransactionWordModel>> getWordList(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processSerialNumber") String processSerialNumber);

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
    Y9Result<String> openDocument(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("itemId") String itemId);

    /**
     *
     * Description: 根据流程编号打开正文
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是正文文件地址
     */
    @GetMapping(value = "/openDocumentByProcessSerialNumber")
    Y9Result<String> openDocumentByProcessSerialNumber(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber);

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
    Y9Result<String> openDocumentTemplate(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("templateGuid") String templateGuid);

    /**
     * 打开历史文件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     */
    @GetMapping(value = "/openHistoryVersionDoc")
    Y9Result<Object> openHistoryVersionDoc(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("taskId") String taskId);

    /**
     * 打开PDF
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是PDF文件地址
     */
    @GetMapping(value = "/openPdf")
    Y9Result<String> openPdf(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("processSerialNumber") String processSerialNumber);

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
    Y9Result<String> openRevokePdfAfterDocument(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processSerialNumber") String processSerialNumber,
        @RequestParam("isTaoHong") String isTaoHong);

    /**
     * 选择套红
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param activitiUser activitiUser
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是当前人员的委办局GUID
     */
    @GetMapping(value = "/openTaoHong")
    Y9Result<String> openTaoHong(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("activitiUser") String activitiUser);

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
    Y9Result<Boolean> saveImportTransationWord(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("docjson") String docjson,
        @RequestParam("processSerialNumber") String processSerialNumber);

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
    Y9Result<Y9WordInfo> showWord(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("itemId") String itemId,
        @RequestParam("itembox") String itembox, @RequestParam("taskId") String taskId);

    /**
     * 获取套红模板列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param currentBureauGuid 委办局id
     * @return {@code Y9Result<List<TaoHongTemplateModel>>} 通用请求返回对象 - data 是套红模板列表
     */
    @GetMapping(value = "/taoHongTemplateList")
    Y9Result<List<TaoHongTemplateModel>> taoHongTemplateList(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("currentBureauGuid") String currentBureauGuid);

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
    Y9Result<Boolean> uploadWord(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("documentTitle") String documentTitle, @RequestParam("fileType") String fileType,
        @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("isTaoHong") String isTaoHong,
        @RequestParam("taskId") String taskId, @RequestParam("fileSizeString") String fileSizeString,
        @RequestParam("fileStoreId") String fileStoreId);

    /**
     * 下载正文
     *
     * @param tenantId 租户Id
     * @param id id
     * @return {@code Y9Result<WordInfo>} 通用请求返回对象 - data 是正文详情
     */
    @GetMapping(value = "/wordDownload")
    Y9Result<TransactionWordModel> wordDownload(@RequestParam("tenantId") String tenantId,
        @RequestParam("id") String id);
}
