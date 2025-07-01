package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.PaperAttachmentApi;
import net.risesoft.entity.PaperAttachment;
import net.risesoft.model.itemadmin.PaperAttachmentModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.PaperAttachmentService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

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
@RequestMapping(value = "/services/rest/paperAttachment", produces = MediaType.APPLICATION_JSON_VALUE)
public class PaperAttachmentApiImpl implements PaperAttachmentApi {

    private final PaperAttachmentService paperAttachmentService;

    @Override
    public Y9Result<Object> delFile(String tenantId, String ids) {
        Y9LoginUserHolder.setTenantId(tenantId);
        paperAttachmentService.delFile(ids);
        return Y9Result.success();
    }

    @Override
    public Y9Result<PaperAttachmentModel> findById(String tenantId, String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        PaperAttachment paperAttachment = paperAttachmentService.findById(id);
        PaperAttachmentModel model = null;
        if (null != paperAttachment) {
            model = new PaperAttachmentModel();
            Y9BeanUtil.copyProperties(paperAttachment, model);
        }
        return Y9Result.success(model);
    }

    @Override
    public Y9Result<List<PaperAttachmentModel>> findByProcessSerialNumber(String tenantId, String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<PaperAttachment> list = paperAttachmentService.findbyProcessSerialNumber(processSerialNumber);
        List<PaperAttachmentModel> modelList = new ArrayList<>();
        PaperAttachmentModel model;
        for (PaperAttachment paperAttachment : list) {
            model = new PaperAttachmentModel();
            Y9BeanUtil.copyProperties(paperAttachment, model);
            modelList.add(model);
        }
        return Y9Result.success(modelList);
    }

    @Override
    public Y9Result<Object> saveOrUpdate(String tenantId, PaperAttachmentModel paperAttachmentModel) {
        Y9LoginUserHolder.setTenantId(tenantId);
        PaperAttachment paperAttachment = new PaperAttachment();
        Y9BeanUtil.copyProperties(paperAttachmentModel, paperAttachment);
        paperAttachmentService.saveOrUpdate(paperAttachment);
        return Y9Result.success();
    }
}
