package y9.client.rest.itemadmin;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.TransactionWordApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "TransactionWordApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}", url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/transactionWord")
public interface TransactionWordApiClient extends TransactionWordApi {

    /**
     * 根据流程编号删除正文，同时删除文件系统的文件
     *
     * @param tenantId
     * @param processSerialNumbers
     */
    @Override
    @PostMapping(value = "/delBatchByProcessSerialNumbers", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void delBatchByProcessSerialNumbers(@RequestParam("tenantId") String tenantId,
        @RequestBody List<String> processSerialNumbers);

    /**
     * 删除撤销PDF文件
     *
     * @param tenantId
     * @param userId
     * @param processSerialNumber
     * @param isTaoHong
     * @return
     */
    @Override
    @PostMapping("/deleteByIsTaoHong")
    public void deleteByIsTaoHong(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("isTaoHong") String isTaoHong);

    /**
     * 获取正文文件信息（数据传输）
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping("/exchangeFindWordByProcessSerialNumber")
    public List<Map<String, Object>> exchangeFindWordByProcessSerialNumber(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     *
     * Description: 打开历史文件
     *
     * @param tenantId
     * @param userId
     * @param taskId
     * @return
     */
    @Override
    @GetMapping("/findHistoryVersionDoc")
    public Map<String, Object> findHistoryVersionDoc(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("taskId") String taskId);

    /**
     * 获取正文文件信息
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping("/findWordByProcessSerialNumber")
    public Map<String, Object> findWordByProcessSerialNumber(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 获取正文列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @return List&lt;Map&lt;String, Object&gt; &gt;
     */
    @Override
    @GetMapping("/getWordList")
    public List<Map<String, Object>> getWordList(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 打开正文
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @param itemId 事项id
     * @return String
     */
    @Override
    @GetMapping("/openDocument")
    public String openDocument(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("itemId") String itemId);

    /**
     *
     * Description: 打开历史文件
     *
     * @param tenantId
     * @param processSerialNumber
     * @return
     */
    @Override
    @GetMapping("/openDocumentByProcessSerialNumber")
    public String openDocumentByProcessSerialNumber(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 套红模板
     *
     * @param tenantId 租户id
     * @param userId 人id
     * @param templateGuid 模板id
     * @return String
     */
    @Override
    @GetMapping("/openDocumentTemplate")
    public String openDocumentTemplate(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("templateGuid") String templateGuid);

    /**
     * 打开历史文件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     */
    @Override
    @GetMapping("/openHistoryVersionDoc")
    public void openHistoryVersionDoc(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("taskId") String taskId);

    /**
     * 打开PDF
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @return String
     */
    @Override
    @GetMapping("/openPdf")
    public String openPdf(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 打开撤销PDF后的正文
     *
     * @param tenantId
     * @param userId
     * @param processSerialNumber
     * @param isTaoHong
     * @return
     */
    @Override
    @GetMapping("/openRevokePDFAfterDocument")
    public String openRevokePdfAfterDocument(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processSerialNumber") String processSerialNumber,
        @RequestParam("isTaoHong") String isTaoHong);

    /**
     * 选择套红
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param activitiUser activitiUser
     * @return Map<String, Object>
     */
    @Override
    @GetMapping("/openTaoHong")
    public Map<String, Object> openTaoHong(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("activitiUser") String activitiUser);

    /**
     * 保存公文传输转入工作流的正文信息
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param docjson 正文json信息
     * @param processSerialNumber 流程序列号
     * @return Boolean
     */
    @Override
    @PostMapping("/saveImportTransationWord")
    public Boolean saveImportTransationWord(@RequestParam("tenantId") String tenantId,
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
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping("/showWord")
    public Map<String, Object> showWord(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processSerialNumber") String processSerialNumber,
        @RequestParam("itemId") String itemId, @RequestParam("itembox") String itembox,
        @RequestParam("taskId") String taskId);

    /**
     * 获取套红模板列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param currentBureauGuid 委办局id
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    @Override
    @GetMapping("/taoHongTemplateList")
    public List<Map<String, Object>> taoHongTemplateList(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("currentBureauGuid") String currentBureauGuid);

    /**
     *
     * Description: 草稿箱保存正文
     *
     * @param tenantId
     * @param userId
     * @param documentTitle
     * @param fileType
     * @param processSerialNumber
     * @param isTaoHong
     * @param taskId
     * @param fileSizeString
     * @param fileStoreId
     * @return
     */
    @Override
    @PostMapping("/uploadWord")
    public String uploadWord(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("documentTitle") String documentTitle, @RequestParam("fileType") String fileType,
        @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("isTaoHong") String isTaoHong,
        @RequestParam("taskId") String taskId, @RequestParam("fileSizeString") String fileSizeString,
        @RequestParam("fileStoreId") String fileStoreId);

    /**
     * 下载正文
     *
     * @param tenantId 租户Id
     * @param id id
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping("/wordDownload")
    public Map<String, Object> wordDownload(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id);
}
