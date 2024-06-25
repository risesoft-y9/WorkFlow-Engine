package net.risesoft.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.DocumentWpsApi;
import net.risesoft.entity.DocumentWps;
import net.risesoft.model.itemadmin.DocumentWpsModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.DocumentWpsService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequestMapping(value = "/services/rest/documentWps")
public class DocumentWpsApiImpl implements DocumentWpsApi {

    @Autowired
    private DocumentWpsService documentWpsService;

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

    @Override
    @PostMapping(value = "/saveWpsContent", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> saveWpsContent(String tenantId, String processSerialNumber, String hasContent) {
        Y9LoginUserHolder.setTenantId(tenantId);
        documentWpsService.saveWpsContent(processSerialNumber, hasContent);
        return Y9Result.success();
    }

}
