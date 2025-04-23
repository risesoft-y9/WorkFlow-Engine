package net.risesoft.api.itemadmin;

import net.risesoft.model.itemadmin.AttachmentConfModel;
import net.risesoft.model.itemadmin.AttachmentModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 附件接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface AttachmentApi {

    /**
     * 根据流程编号删除附件
     *
     * @param tenantId             租户id
     * @param processSerialNumbers 流程编号
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping(value = "/delByProcessSerialNumbers", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> delBatchByProcessSerialNumbers(@RequestParam("tenantId") String tenantId,
                                                    @RequestBody List<String> processSerialNumbers);

    /**
     * 删除附件
     *
     * @param tenantId 租户id
     * @param ids      附件ids
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/delFile")
    Y9Result<Object> delFile(@RequestParam("tenantId") String tenantId, @RequestParam("ids") String ids);

    /**
     * 附件数
     *
     * @param tenantId            租户id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data是附件数
     * @since 9.6.6
     */
    @GetMapping("/fileCounts")
    Y9Result<Integer> fileCounts(@RequestParam("tenantId") String tenantId,
                                 @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 附件下载
     *
     * @param tenantId 租户id
     * @param id       附件id
     * @return {@code Y9Result<AttachmentModel>} 通用请求返回对象 - data是附件对象
     * @since 9.6.6
     */
    @GetMapping("/findById")
    Y9Result<AttachmentModel> findById(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id);

    /**
     * 获取附件数
     *
     * @param tenantId            租户id
     * @param processSerialNumber 流程编号
     * @param fileSource          附件来源
     * @param fileType            文件类型
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data是附件数
     * @since 9.6.6
     */
    @GetMapping("/getAttachmentCount")
    Y9Result<Integer> getAttachmentCount(@RequestParam("tenantId") String tenantId,
                                         @RequestParam("processSerialNumber") String processSerialNumber,
                                         @RequestParam(value = "fileSource", required = false) String fileSource,
                                         @RequestParam(value = "fileType", required = false) String fileType);

    /**
     * 获取附件列表
     *
     * @param tenantId            租户id
     * @param processSerialNumber 流程编号
     * @param fileSource          附件来源
     * @param page                页码
     * @param rows                行数
     * @return {@code Y9Page<AttachmentModel>} 通用分页请求返回对象 - rows是附件对象
     * @since 9.6.6
     */
    @GetMapping("/getAttachmentList")
    Y9Page<AttachmentModel> getAttachmentList(@RequestParam("tenantId") String tenantId,
                                              @RequestParam("processSerialNumber") String processSerialNumber,
                                              @RequestParam(value = "fileSource", required = false) String fileSource, @RequestParam("page") int page,
                                              @RequestParam("rows") int rows);

    /**
     * 获取附件列表(model)
     *
     * @param tenantId            租户id
     * @param processSerialNumber 流程编号
     * @param fileSource          附件来源
     * @return {@code Y9Result<List<AttachmentModel>>} 通用请求返回对象 - data是附件列表
     * @since 9.6.6
     */
    @GetMapping("/getAttachmentModelList")
    Y9Result<List<AttachmentModel>> getAttachmentModelList(@RequestParam("tenantId") String tenantId,
                                                           @RequestParam("processSerialNumber") String processSerialNumber,
                                                           @RequestParam(value = "fileSource", required = false) String fileSource);

    /**
     * 获取附件
     *
     * @param tenantId 租户id
     * @param fileId   附件id
     * @return {@code Y9Result<AttachmentModel>} 通用请求返回对象 - data是附件对象
     * @since 9.6.6
     */
    @GetMapping("/getFile")
    Y9Result<AttachmentModel> getFile(@RequestParam("tenantId") String tenantId, @RequestParam("fileId") String fileId);

    /**
     * 保存附件信息
     *
     * @param tenantId            租户id
     * @param orgUnitId           人员、岗位id
     * @param attachjson          附件信息
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/saveAttachment")
    Y9Result<Object> saveAttachment(@RequestParam("tenantId") String tenantId,
                                    @RequestParam("orgUnitId") String orgUnitId, @RequestParam("attachjson") String attachjson,
                                    @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 保存附件信息
     *
     * @param tenantId            租户id
     * @param orgUnitId           人员、岗位id
     * @param fileName            文件名称
     * @param fileType            文件类型
     * @param fileSizeString      文件大小
     * @param fileSource          附件来源
     * @param processInstanceId   流程实例id
     * @param processSerialNumber 流程编号
     * @param taskId              任务id
     * @param y9FileStoreId       附件上传id
     * @return {@code Y9Result<String>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/saveOrUpdateUploadInfo")
    Y9Result<String> saveOrUpdateUploadInfo(@RequestParam("tenantId") String tenantId,
                                            @RequestParam("orgUnitId") String orgUnitId, @RequestParam("fileName") String fileName,
                                            @RequestParam(value = "fileType", required = false) String fileType,
                                            @RequestParam(value = "fileSizeString", required = false) String fileSizeString,
                                            @RequestParam(value = "fileSource", required = false) String fileSource,
                                            @RequestParam(value = "processInstanceId", required = false) String processInstanceId,
                                            @RequestParam("processSerialNumber") String processSerialNumber,
                                            @RequestParam(value = "taskId", required = false) String taskId,
                                            @RequestParam("y9FileStoreId") String y9FileStoreId);

    /**
     * 更新附件
     *
     * @param tenantId      租户id
     * @param orgUnitId     人员、岗位id
     * @param fileId        文件id
     * @param fileSize      文件大小
     * @param taskId        任务id
     * @param y9FileStoreId 附件上传id
     * @return {@code Y9Result<String>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/updateFile")
    Y9Result<String> updateFile(@RequestParam("tenantId") String tenantId, @RequestParam("orgUnitId") String orgUnitId,
                                @RequestParam("fileId") String fileId, @RequestParam(value = "fileSize", required = false) String fileSize,
                                @RequestParam(value = "taskId", required = false) String taskId,
                                @RequestParam("y9FileStoreId") String y9FileStoreId);

    /**
     * 上传附件
     *
     * @param tenantId            租户id
     * @param userId              人员id
     * @param orgUnitId           人员、岗位id
     * @param fileName            文件名
     * @param fileSize            文件大小
     * @param processInstanceId   流程实例id
     * @param taskId              任务id
     * @param describes           描述
     * @param processSerialNumber 流程编号
     * @param fileSource          附件来源
     * @param y9FileStoreId       附件上传id
     * @return {@code Y9Result<String>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/upload")
    Y9Result<String> upload(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
                            @RequestParam("orgUnitId") String orgUnitId, @RequestParam("fileName") String fileName,
                            @RequestParam(value = "fileSize", required = false) String fileSize,
                            @RequestParam(value = "processInstanceId", required = false) String processInstanceId,
                            @RequestParam(value = "taskId", required = false) String taskId,
                            @RequestParam(value = "describes", required = false) String describes,
                            @RequestParam("processSerialNumber") String processSerialNumber,
                            @RequestParam(value = "fileSource", required = false) String fileSource,
                            @RequestParam("y9FileStoreId") String y9FileStoreId);

    /**
     * 上传附件(model)
     *
     * @param tenantId        租户id
     * @param orgUnitId       人员、岗位id
     * @param attachmentModel 附件实体信息
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/uploadModel")
    Y9Result<Object> uploadModel(@RequestParam("tenantId") String tenantId, @RequestParam("orgUnitId") String orgUnitId,
                                 @RequestBody AttachmentModel attachmentModel);

    /**
     * 获取附件配置信息
     *
     * @param tenantId
     * @param attachmentType
     * @return
     */
    @GetMapping("/findByAttachmentType")
    Y9Result<List<AttachmentConfModel>> findByAttachmentType(@RequestParam("tenantId") String tenantId,
                                                             @RequestParam("attachmentType") String attachmentType);
}
