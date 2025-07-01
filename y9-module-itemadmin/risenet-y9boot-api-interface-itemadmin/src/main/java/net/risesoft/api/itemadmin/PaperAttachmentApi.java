package net.risesoft.api.itemadmin;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.PaperAttachmentModel;
import net.risesoft.pojo.Y9Result;

/**
 * 纸质附件接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface PaperAttachmentApi {

    /**
     * 删除纸质附件
     *
     * @param tenantId 租户id
     * @param ids 附件ids
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.8
     */
    @PostMapping("/delFile")
    Y9Result<Object> delFile(@RequestParam("tenantId") String tenantId, @RequestParam("ids") String ids);

    /**
     * 获取纸质附件
     *
     * @param tenantId 租户id
     * @param id 附件id
     * @return {@code Y9Result<PaperAttachmentModel>} 通用请求返回对象 - data是附件对象
     * @since 9.6.8
     */
    @GetMapping("/findById")
    Y9Result<PaperAttachmentModel> findById(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id);

    /**
     * 获取纸质附件列表
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Page<PaperAttachmentModel>} 通用分页请求返回对象 - rows是附件对象
     * @since 9.6.8
     */
    @GetMapping("/findByProcessSerialNumber")
    Y9Result<List<PaperAttachmentModel>> findByProcessSerialNumber(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 保存或者更新纸质附件
     *
     * @param tenantId 租户id
     * @param paperAttachmentModel 附件实体信息
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.8
     */
    @PostMapping("/saveOrUpdate")
    Y9Result<Object> saveOrUpdate(@RequestParam("tenantId") String tenantId,
        @RequestBody PaperAttachmentModel paperAttachmentModel);
}
