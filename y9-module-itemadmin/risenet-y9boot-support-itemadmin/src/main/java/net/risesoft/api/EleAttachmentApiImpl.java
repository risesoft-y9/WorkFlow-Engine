package net.risesoft.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.risesoft.api.itemadmin.EleAttachmentApi;
import net.risesoft.entity.EleAttachment;
import net.risesoft.model.itemadmin.EleAttachmentModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.EleAttachmentService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 附件接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/eleAttachment", produces = MediaType.APPLICATION_JSON_VALUE)
public class EleAttachmentApiImpl implements EleAttachmentApi {

    private final EleAttachmentService eleAttachmentService;

    /**
     * 根据流程编号删除附件
     *
     * @param tenantId             租户id
     * @param processSerialNumbers 流程编号
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> delBatchByProcessSerialNumbers(@RequestParam String tenantId, @RequestBody List<String> processSerialNumbers) {
        Y9LoginUserHolder.setTenantId(tenantId);
        eleAttachmentService.delBatchByProcessSerialNumbers(processSerialNumbers);
        return Y9Result.success();
    }

    /**
     * 删除附件（物理删除，包含具体文件）
     *
     * @param tenantId 租户id
     * @param ids      附件ids
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> delFile(@RequestParam String tenantId, @RequestParam String ids) {
        Y9LoginUserHolder.setTenantId(tenantId);
        eleAttachmentService.delFile(ids);
        return Y9Result.success();
    }

    /**
     * 根据附件id获取附件信息
     *
     * @param tenantId 租户id
     * @param id       附件id
     * @return {@code Y9Result<AttachmentModel>} 通用请求返回对象 - data是附件对象
     * @since 9.6.8
     */
    @Override
    public Y9Result<EleAttachmentModel> findById(@RequestParam String tenantId, @RequestParam String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        EleAttachment eleAttachment = eleAttachmentService.findById(id);
        EleAttachmentModel elaAttachmentModel = null;
        if (eleAttachment != null) {
            elaAttachmentModel = new EleAttachmentModel();
            Y9BeanUtil.copyProperties(eleAttachment, elaAttachmentModel);
        }
        return Y9Result.success(elaAttachmentModel);
    }


    /**
     * 获取附件列表
     *
     * @param tenantId            租户id
     * @param processSerialNumber 流程编号
     * @param attachmentType      附件类型
     * @return {@code Y9Result<List<AttachmentModel>>} 通用请求返回对象 - data是附件列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<EleAttachmentModel>> findByProcessSerialNumberAndAttachmentType(@RequestParam String tenantId, @RequestParam String processSerialNumber, String attachmentType) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<EleAttachment> eleAttachmentList = eleAttachmentService.findByProcessSerialNumberAndAttachmentType(processSerialNumber, attachmentType);
        List<EleAttachmentModel> modelList =new ArrayList<>();
        EleAttachmentModel model;
        for(EleAttachment eleAttachment:eleAttachmentList){
            model=new EleAttachmentModel();
            Y9BeanUtil.copyProperties(eleAttachment, model);
            modelList.add(model);
        }
        return Y9Result.success(modelList);
    }

    /**
     * 保存或者更新附件信息
     *
     * @param tenantId           租户id
     * @param eleAttachmentModel 附件实体信息
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.8
     */
    @Override
    public Y9Result<Object> saveOrUpdate(@RequestParam String tenantId, @RequestBody EleAttachmentModel eleAttachmentModel) {
        Y9LoginUserHolder.setTenantId(tenantId);
        EleAttachment eleAttachment = new EleAttachment();
        Y9BeanUtil.copyProperties(eleAttachmentModel, eleAttachment);
        eleAttachmentService.saveOrUpdate(eleAttachment);
        return Y9Result.success();
    }
}
