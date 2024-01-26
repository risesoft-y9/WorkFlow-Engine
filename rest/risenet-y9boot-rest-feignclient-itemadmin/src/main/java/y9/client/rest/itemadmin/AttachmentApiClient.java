package y9.client.rest.itemadmin;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.AttachmentApi;
import net.risesoft.model.itemadmin.AttachmentModel;

/**
 * 附件接口
 * 
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "AttachmentApiClient", name = "itemAdmin", url = "${y9.common.itemAdminBaseUrl}",
    path = "/services/rest/attachment")
public interface AttachmentApiClient extends AttachmentApi {

    /**
     * 附件下载
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param id 附件id
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping("/attachmentDownload")
    public Map<String, Object> attachmentDownload(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("id") String id);

    /**
     * 根据流程编号删除附件
     *
     * @param tenantId 租户id
     * @param processSerialNumbers 流程编号
     */
    @Override
    @PostMapping(value = "/delBatchByProcessSerialNumbers", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void delBatchByProcessSerialNumbers(@RequestParam("tenantId") String tenantId,
        @RequestBody List<String> processSerialNumbers);

    /**
     * 删除附件
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param ids 附件ids
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @PostMapping("/delFile")
    public Map<String, Object> delFile(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("ids") String ids);

    /**
     * 附件数
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param processSerialNumber 流程编号
     * @return Integer Integer
     */
    @Override
    @GetMapping("/fileCounts")
    public Integer fileCounts(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 获取附件数
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param processSerialNumber 流程编号
     * @param fileSource 附件来源
     * @param fileType 文件类型
     * @return int
     */
    @Override
    @GetMapping("/getAttachmentCount")
    public int getAttachmentCount(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("fileSource") String fileSource,
        @RequestParam("fileType") String fileType);

    /**
     * 获取附件列表
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param processSerialNumber 流程编号
     * @param fileSource 附件来源
     * @param page 页码
     * @param rows 行数
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping("/getAttachmentList")
    public Map<String, Object> getAttachmentList(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processSerialNumber") String processSerialNumber,
        @RequestParam("fileSource") String fileSource, @RequestParam("page") int page, @RequestParam("rows") int rows);

    /**
     * 获取附件列表(model)
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param processSerialNumber 流程编号
     * @param fileSource 附件来源
     * @return List&lt;AttachmentModel&gt;
     */
    @Override
    @GetMapping("/getAttachmentModelList")
    public List<AttachmentModel> getAttachmentModelList(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processSerialNumber") String processSerialNumber,
        @RequestParam("fileSource") String fileSource);

    /**
     * 保存附件信息
     *
     * @param tenantId 租户id
     * @param userId 人员id 用户id
     * @param attachjson 附件信息
     * @param processSerialNumber 流程编号
     * @return Boolean 是否保存成功
     */
    @Override
    @PostMapping("/saveAttachment")
    public Boolean saveAttachment(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("attachjson") String attachjson, @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 保存附件信息
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param fileName 文件名称
     * @param fileType 文件类型
     * @param fileSizeString 文件大小
     * @param fileSource 附件来源
     * @param processInstanceId 流程实例id
     * @param processSerialNumber 流程编号
     * @param taskId 任务id
     * @param y9FileStoreId 附件上传id
     * @return String String
     */
    @Override
    @PostMapping("/saveOrUpdateUploadInfo")
    public String saveOrUpdateUploadInfo(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("fileName") String fileName,
        @RequestParam("fileType") String fileType, @RequestParam("fileSizeString") String fileSizeString,
        @RequestParam("fileSource") String fileSource, @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("taskId") String taskId,
        @RequestParam("y9FileStoreId") String y9FileStoreId);

    /**
     * 上传附件
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param fileName 文件名
     * @param fileSize 文件大小
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @param describes 描述
     * @param processSerialNumber 流程编号
     * @param fileSource 附件来源
     * @param y9FileStoreId 附件上传id
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @PostMapping("/upload")
    public Map<String, Object> upload(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("fileName") String fileName, @RequestParam("fileSize") String fileSize,
        @RequestParam("processInstanceId") String processInstanceId, @RequestParam("taskId") String taskId,
        @RequestParam("describes") String describes, @RequestParam("processSerialNumber") String processSerialNumber,
        @RequestParam("fileSource") String fileSource, @RequestParam("y9FileStoreId") String y9FileStoreId);

    /**
     * 上传附件(model)
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param attachmentModel 附件实体信息
     * @return boolean
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/uploadModel", consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean uploadModel(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestBody AttachmentModel attachmentModel) throws Exception;
}
