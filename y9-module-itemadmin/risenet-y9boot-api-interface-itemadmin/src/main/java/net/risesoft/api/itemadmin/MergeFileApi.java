package net.risesoft.api.itemadmin;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.MergeFileModel;
import net.risesoft.pojo.Y9Result;

/**
 * 合并文件接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface MergeFileApi {

    /**
     * 删除合并文件
     *
     * @param tenantId 租户id
     * @param ids 文件id
     * @return @code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.0
     */
    @PostMapping(value = "/delMergeFile", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> delMergeFile(@RequestParam("tenantId") String tenantId, @RequestBody String[] ids);

    /**
     * 根据id获取合并文件
     *
     * @param tenantId 租户id
     * @param ids 文件id
     * @return @code Y9Result<List<MergeFileModel>>} 通用请求返回对象
     * @since 9.6.0
     */
    @GetMapping(value = "/findByIds", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<List<MergeFileModel>> findByIds(@RequestParam("tenantId") String tenantId, @RequestBody String[] ids);

    /**
     * 根据id获取合并文件
     *
     * @param tenantId 租户id
     * @param id 文件id
     * @return @code Y9Result<MergeFileModel>} 通用请求返回对象
     * @since 9.6.0
     */
    @GetMapping("/getMergeFile")
    Y9Result<MergeFileModel> getMergeFile(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id);

    /**
     * 获取合并文件列表
     *
     * @param tenantId 租户id
     * @param personId 用户id
     * @param processSerialNumber 流程编号
     * @param listType 列表类型
     * @param fileType 文件类型
     * @return @code Y9Result<List<MergeFileModel>>} 通用请求返回对象
     * @since 9.6.0
     */
    @GetMapping("/getMergeFileList")
    Y9Result<List<MergeFileModel>> getMergeFileList(@RequestParam("tenantId") String tenantId,
        @RequestParam("personId") String personId,
        @RequestParam(value = "processSerialNumber", required = false) String processSerialNumber,
        @RequestParam("listType") String listType, @RequestParam("fileType") String fileType);

    /**
     * 根据源文件id获取合并文件
     *
     * @param tenantId 租户id
     * @param sourceFileId 文件id
     * @return @code Y9Result<MergeFileModel>} 通用请求返回对象
     * @since 9.6.0
     */
    @GetMapping("/getOfdFile")
    Y9Result<List<MergeFileModel>> getOfdFile(@RequestParam("tenantId") String tenantId,
        @RequestParam("sourceFileId") String sourceFileId);

    /**
     * 保存合并文件
     *
     * @param tenantId 租户id
     * @param mergeFileModel 合并文件对象
     * @return @code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.0
     */
    @PostMapping(value = "/saveMergeFile", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> saveMergeFile(@RequestParam("tenantId") String tenantId,
        @RequestBody MergeFileModel mergeFileModel);
}
