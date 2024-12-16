package net.risesoft.api.itemadmin;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.EleAttachmentModel;
import net.risesoft.pojo.Y9Result;

/**
 * 附件接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface EleAttachmentApi {

    /**
     * 根据流程编号删除附件
     *
     * @param tenantId 租户id
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
     * @param ids 附件ids
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/delFile")
    Y9Result<Object> delFile(@RequestParam("tenantId") String tenantId, @RequestParam("ids") String ids);

    /**
     * 附件下载
     *
     * @param tenantId 租户id
     * @param id 附件id
     * @return {@code Y9Result<EleAttachmentModel>} 通用请求返回对象 - data是附件对象
     * @since 9.6.6
     */
    @GetMapping("/findById")
    Y9Result<EleAttachmentModel> findById(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id);

    /**
     * 获取附件列表(model)
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param attachmentType 附件类型
     * @return {@code Y9Result<List<AttachmentModel>>} 通用请求返回对象 - data是附件列表
     * @since 9.6.6
     */
    @GetMapping("/findByProcessSerialNumberAndAttachmentType")
    Y9Result<List<EleAttachmentModel>> findByProcessSerialNumberAndAttachmentType(
        @RequestParam("tenantId") String tenantId, @RequestParam("processSerialNumber") String processSerialNumber,
        @RequestParam("attachmentType") String attachmentType);

    /**
     * 上传附件(model)
     *
     * @param tenantId 租户id
     * @param eleAttachmentModel 附件实体信息
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.8
     */
    @PostMapping("/saveOrUpdate")
    Y9Result<Object> saveOrUpdate(@RequestParam("tenantId") String tenantId,
        @RequestBody EleAttachmentModel eleAttachmentModel);

    /**
     * 附件排序
     *
     * @param id1 主键id1
     * @param id2 主键id2
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/saveOrder")
    Y9Result<Object> saveOrder(@RequestParam("tenantId") String tenantId, @RequestParam("id1") String id1,
        @RequestParam("id2") String id2);
}
