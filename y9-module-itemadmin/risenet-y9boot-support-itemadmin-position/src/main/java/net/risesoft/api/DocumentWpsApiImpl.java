package net.risesoft.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.DocumentWpsApi;
import net.risesoft.entity.DocumentWps;
import net.risesoft.model.itemadmin.DocumentWpsModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.DocumentWpsService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * WPS正文接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/documentWps")
public class DocumentWpsApiImpl implements DocumentWpsApi {

    private final DocumentWpsService documentWpsService;

    /**
     * 根据id查询WPS正文
     *
     * @param tenantId 租户id
     * @param id 正文id
     * @return Y9Result<DocumentWpsModel>
     */
    @Override
    @GetMapping(value = "/findById", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<DocumentWpsModel> findById(String tenantId, String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        DocumentWps documentWps = documentWpsService.findById(id);
        DocumentWpsModel documentWpsModel = null;
        if (documentWps != null) {
            documentWpsModel = new DocumentWpsModel();
            Y9BeanUtil.copyProperties(documentWps, documentWpsModel);
        }
        return Y9Result.success(documentWpsModel);
    }

    /**
     * 根据流程编号查询WPS正文
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程序列号
     * @return Y9Result<DocumentWpsModel>
     */
    @Override
    @GetMapping(value = "/findByProcessSerialNumber", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<DocumentWpsModel> findByProcessSerialNumber(String tenantId, String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        DocumentWps documentWps = documentWpsService.findByProcessSerialNumber(processSerialNumber);
        DocumentWpsModel documentWpsModel = null;
        if (documentWps != null) {
            documentWpsModel = new DocumentWpsModel();
            Y9BeanUtil.copyProperties(documentWps, documentWpsModel);
        }
        return Y9Result.success(documentWpsModel);
    }

    /**
     * 保存WPS正文
     *
     * @param tenantId 租户id
     * @param documentWpsModel wps文档对象
     * @return Y9Result<Object>
     */
    @Override
    @PostMapping(value = "/saveDocumentWps", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> saveDocumentWps(String tenantId, @RequestBody DocumentWpsModel documentWpsModel) {
        Y9LoginUserHolder.setTenantId(tenantId);
        DocumentWps documentWps = new DocumentWps();
        Y9BeanUtil.copyProperties(documentWpsModel, documentWps);
        documentWpsService.saveDocumentWps(documentWps);
        return Y9Result.success();
    }

    /**
     * 保存WPS正文内容
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程序列号
     * @param hasContent 是否有内容
     * @return Y9Result<Object>
     */
    @Override
    @PostMapping(value = "/saveWpsContent", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> saveWpsContent(String tenantId, String processSerialNumber, String hasContent) {
        Y9LoginUserHolder.setTenantId(tenantId);
        documentWpsService.saveWpsContent(processSerialNumber, hasContent);
        return Y9Result.success();
    }

}
