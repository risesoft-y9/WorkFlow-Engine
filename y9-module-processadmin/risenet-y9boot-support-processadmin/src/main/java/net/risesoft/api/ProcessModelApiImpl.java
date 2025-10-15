package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;

import org.flowable.engine.RepositoryService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.ProcessModelApi;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.processadmin.FlowableBpmnModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.util.Y9DateTimeUtils;
import net.risesoft.y9.FlowableTenantInfoHolder;

/**
 * 流程设计相关接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/processModel", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProcessModelApiImpl implements ProcessModelApi {

    private final RepositoryService repositoryService;

    private final OrgUnitApi orgUnitApi;

    /**
     * 删除模型
     *
     * @param tenantId 租户id
     * @param modelId 模型id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> deleteModel(@RequestParam String tenantId, @RequestParam String modelId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        // modelService.deleteModel(modelId);
        return Y9Result.success();
    }

    /**
     * 根据modelId部署流程
     *
     * @param tenantId 租户id
     * @param modelId 模型id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> deployModel(@RequestParam String tenantId, @RequestParam String modelId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        /*Model modelData = modelService.getModel(modelId);
        BpmnModel model = modelService.getBpmnModel(modelData);
        if (model.getProcesses().isEmpty()) {
            return Y9Result.failure("数据模型不符要求，请至少设计一条主线流程。");
        }
        byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(model);
        String processName = modelData.getName() + ".bpmn20.xml";
        repositoryService.createDeployment().name(modelData.getName()).addBytes(processName, bpmnBytes).deploy();
        */
        return Y9Result.success();
    }

    /**
     * 获取模型列表
     *
     * @param tenantId 租户id
     * @return {@code Y9Result<List<FlowableBpmnModel>>} 通用请求返回对象 - data 模型列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<FlowableBpmnModel>> getModelList(@RequestParam String tenantId) {
      FlowableTenantInfoHolder.setTenantId(tenantId);
        List<FlowableBpmnModel> items = new ArrayList<>();
        /* List<AbstractModel> list = modelService.getModelsByModelType(Model.MODEL_TYPE_BPMN);
        ProcessDefinition processDefinition;
        FlowableBpmnModel flowableBpmnModel;
        for (AbstractModel model : list) {
            flowableBpmnModel = new FlowableBpmnModel();
            flowableBpmnModel.setId(model.getId());
            flowableBpmnModel.setKey(model.getKey());
            flowableBpmnModel.setName(model.getName());
            flowableBpmnModel.setVersion(0);
            processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(model.getKey())
                .latestVersion()
                .singleResult();
            if (null != processDefinition) {
                flowableBpmnModel.setVersion(processDefinition.getVersion());
            }
            flowableBpmnModel.setCreateTime(Y9DateTimeUtils.formatDateTime(model.getCreated()));
            flowableBpmnModel.setLastUpdateTime(Y9DateTimeUtils.formatDateTime(model.getLastUpdated()));
            items.add(flowableBpmnModel);
        }*/
        return Y9Result.success(items, "获取成功");
    }

    /**
     * 获取模型xml
     *
     * @param tenantId 租户id
     * @param modelId 模型id
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 模型xml
     * @since 9.6.6
     */
    @Override
    public Y9Result<String> getModelXml(@RequestParam String tenantId, @RequestParam String modelId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        //  byte[] bpmnBytes;
        //  Model model = modelService.getModel(modelId);
        // bpmnBytes = modelService.getBpmnXML(model);
        // return Y9Result.success(bpmnBytes == null ? "" : new String(bpmnBytes, StandardCharsets.UTF_8), "获取成功");
        return Y9Result.success("", "获取成功");
    }

    /**
     * 导入模型文件
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param file 文件
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> saveModelXml(@RequestParam String tenantId, @RequestParam String userId,
        MultipartFile file) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        try {
            OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, userId).getData();
            // XMLInputFactory xif = XmlUtil.createSafeXmlInputFactory();
            // InputStreamReader xmlIn = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
            // XMLStreamReader xtr = xif.createXMLStreamReader(xmlIn);
            //
            // BpmnXMLConverter bpmnXmlConverter = new BpmnXMLConverter();
            // BpmnModel bpmnModel = bpmnXmlConverter.convertToBpmnModel(xtr);
            // // 模板验证
            // ProcessValidator validator = new ProcessValidatorFactory().createDefaultProcessValidator();
            // List<ValidationError> errors = validator.validate(bpmnModel);
            // if (!errors.isEmpty()) {
            // StringBuffer es = new StringBuffer();
            // errors.forEach(ve -> es.append(ve.toString()).append("/n"));
            // return Y9Result.failure("保存失败：模板验证失败，原因: " + es);
            // }
            // if (bpmnModel.getProcesses().isEmpty()) {
            // return Y9Result.failure("保存失败： 文件中不存在流程的信息");
            // }
            // if (bpmnModel.getLocationMap().isEmpty()) {
            // BpmnAutoLayout bpmnLayout = new BpmnAutoLayout(bpmnModel);
            // bpmnLayout.execute();
            // }
            // BpmnJsonConverter bpmnJsonConverter = new BpmnJsonConverter();
            // ObjectNode modelNode = bpmnJsonConverter.convertToJson(bpmnModel);
            // org.flowable.bpmn.model.Process process = bpmnModel.getMainProcess();
            // String name = process.getId();
            // if (StringUtils.isNotEmpty(process.getName())) {
            // name = process.getName();
            // }
            // String description = process.getDocumentation();
            // // 查询是否已经存在流程模板
            // Model newModel = new Model();
            // List<Model> models = modelRepository.findByKeyAndType(process.getId(), AbstractModel.MODEL_TYPE_BPMN);
            // if (!models.isEmpty()) {
            // Model updateModel = models.get(0);
            // newModel.setId(updateModel.getId());
            // }
            // newModel.setName(name);
            // newModel.setKey(process.getId());
            // newModel.setModelType(AbstractModel.MODEL_TYPE_BPMN);
            // newModel.setCreated(Calendar.getInstance().getTime());
            // newModel.setCreatedBy(orgUnit.getName());
            // newModel.setDescription(description);
            // newModel.setModelEditorJson(modelNode.toString());
            // newModel.setLastUpdated(Calendar.getInstance().getTime());
            // newModel.setLastUpdatedBy(orgUnit.getName());
            // newModel.setTenantId(tenantId);
            // String createdBy = SecurityUtils.getCurrentUserId();
            // modelService.createModel(newModel, createdBy);
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            LOGGER.error("导入模型文件失败", e);
        }
        return Y9Result.failure("保存失败");
    }

}
