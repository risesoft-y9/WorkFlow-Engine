package net.risesoft.controller;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.BpmnAutoLayout;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.editor.language.json.converter.BpmnJsonConverter;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.ui.common.security.SecurityUtils;
import org.flowable.ui.common.util.XmlUtil;
import org.flowable.ui.modeler.domain.AbstractModel;
import org.flowable.ui.modeler.domain.Model;
import org.flowable.ui.modeler.model.ModelRepresentation;
import org.flowable.ui.modeler.repository.ModelRepository;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.flowable.validation.ProcessValidator;
import org.flowable.validation.ProcessValidatorFactory;
import org.flowable.validation.ValidationError;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.controller.vo.ModelVO;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;

/**
 * 流程模型控制器
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/processModel", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProcessModelVueController {

    private final RepositoryService repositoryService;

    private final ModelService modelService;

    private final ModelRepository modelRepository;

    private final Y9Properties y9Config;

    /**
     * 创建模型
     *
     * @param name 流程名称
     * @param key 流程定义key
     * @param description 描述
     */
    @PostMapping(value = "/create")
    public Y9Result<String> create(@RequestParam @NotBlank String name, @RequestParam @NotBlank String key,
        @RequestParam(required = false) String description) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String personName = userInfo.getName();
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode editorNode = objectMapper.createObjectNode();
        editorNode.put("id", "canvas");
        editorNode.put("resourceId", "canvas");
        ObjectNode stencilSetNode = objectMapper.createObjectNode();
        stencilSetNode.put("namespace", "https://b3mn.org/stencilset/bpmn2.0#");
        editorNode.set("stencilset", stencilSetNode);

        Model newModel = new Model();
        newModel.setName(name);
        newModel.setKey(key);
        newModel.setDescription(description);
        newModel.setModelType(Model.MODEL_TYPE_BPMN);
        newModel.setModelEditorJson(editorNode.toString());
        newModel.setLastUpdatedBy(personName);
        newModel.setCreatedBy(personName);

        Model model = modelService.createModel(newModel, personName);
        String modelId = model.getId();
        /*
         * 跳转画图页面
         */
        String path = y9Config.getCommon().getProcessAdminBaseUrl() + "/modeler.html#/editor/" + modelId;
        return Y9Result.success(path, "创建成功");
    }

    /**
     * 删除模型
     *
     * @param modelId 模型id
     * @return Y9Result<String>
     */
    @PostMapping(value = "/deleteModel")
    public Y9Result<String> deleteModel(@RequestParam @NotBlank String modelId) {
        modelService.deleteModel(modelId);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 根据Model部署流程
     *
     * @param modelId 模型id
     * @return Y9Result<String>
     */
    @PostMapping(value = "/deployModel")
    public Y9Result<String> deployModel(@RequestParam @NotBlank String modelId) {
        Model modelData = modelService.getModel(modelId);
        BpmnModel model = modelService.getBpmnModel(modelData);
        if (model.getProcesses().isEmpty()) {
            return Y9Result.failure("数据模型不符要求，请至少设计一条主线流程。");
        }
        byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(model);
        String processName = modelData.getName() + ".bpmn20.xml";
        repositoryService.createDeployment().name(modelData.getName()).addBytes(processName, bpmnBytes).deploy();
        return Y9Result.successMsg("部署成功");
    }

    /**
     * 导出model的xml文件
     *
     * @param modelId 模型id
     * @param response response
     */
    @RequestMapping(value = "/exportModel")
    public void exportModel(@RequestParam @NotBlank String modelId, HttpServletResponse response) {
        try {
            Model model = modelService.getModel(modelId);
            byte[] bpmnBytes = modelService.getBpmnXML(model);

            ByteArrayInputStream in = new ByteArrayInputStream(bpmnBytes);
            String filename = model.getKey() + ".bpmn20.xml";
            response.setHeader("Content-Disposition", "attachment; filename=" + filename);
            IOUtils.copy(in, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            LOGGER.error("导出模型失败,modelId:{} 异常：{}", modelId, e.getMessage());
        }
    }

    /**
     * 获取模型列表
     *
     * @return Y9Result<List<Map<String, Object>>>
     */
    @GetMapping(value = "/getModelList")
    public Y9Result<List<ModelVO>> getModelList() {
        List<ModelVO> items = new ArrayList<>();
        List<AbstractModel> list = modelService.getModelsByModelType(Model.MODEL_TYPE_BPMN);
        ProcessDefinition processDefinition;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // if (tenantManager || ManagerLevelEnum.SYSTEM_MANAGER.equals(userInfo.getManagerLevel())) {
        for (AbstractModel model : list) {
            ModelVO mapTemp = new ModelVO();
            mapTemp.setId(model.getId());
            mapTemp.setKey(model.getKey());
            mapTemp.setName(model.getName());
            mapTemp.setVersion(0);
            processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(model.getKey())
                .latestVersion().singleResult();
            if (null != processDefinition) {
                mapTemp.setVersion(processDefinition.getVersion());
            }
            mapTemp.setCreateTime(sdf.format(model.getCreated()));
            mapTemp.setSortTime(model.getCreated().getTime());
            mapTemp.setLastUpdateTime(sdf.format(model.getLastUpdated()));
            items.add(mapTemp);
        }
        Collections.sort(items);
        return Y9Result.success(items, "获取成功");
    }

    /**
     * 获取流程设计模型xml
     *
     * @param modelId 模型id
     * @return Y9Result<Map<String, Object>>
     */
    @RequestMapping(value = "/getModelXml")
    public Y9Result<Map<String, Object>> getModelXml(@RequestParam @NotBlank String modelId) {
        byte[] bpmnBytes = null;
        Map<String, Object> map = new HashMap<>();
        try {
            Model model = modelService.getModel(modelId);
            map.put("key", model.getKey());
            map.put("name", model.getName());
            bpmnBytes = modelService.getBpmnXML(model);
        } catch (Exception e) {
            LOGGER.error("获取模型xml失败,modelId:{} 异常：{}", modelId, e.getMessage());
        }
        map.put("xml", bpmnBytes == null ? "" : new String(bpmnBytes, StandardCharsets.UTF_8));
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 导入流程模板
     *
     * @param file 上传的文件
     * @param model 模型信息
     * @return Map<String, Object>
     */
    @RequestMapping(value = "/import")
    public Y9Result<Object> importProcessModel(MultipartFile file, ModelRepresentation model) {
        try {
            UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
            String tenantId = Y9LoginUserHolder.getTenantId();
            XMLInputFactory xif = XmlUtil.createSafeXmlInputFactory();
            InputStreamReader xmlIn = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
            XMLStreamReader xtr = xif.createXMLStreamReader(xmlIn);

            BpmnXMLConverter bpmnXmlConverter = new BpmnXMLConverter();
            BpmnModel bpmnModel = bpmnXmlConverter.convertToBpmnModel(xtr);
            // 模板验证
            ProcessValidator validator = new ProcessValidatorFactory().createDefaultProcessValidator();
            List<ValidationError> errors = validator.validate(bpmnModel);
            if (!errors.isEmpty()) {
                StringBuilder es = new StringBuilder();
                errors.forEach(ve -> es.append(ve.toString()).append("/n"));
                return Y9Result.failure("导入失败：模板验证失败，原因: " + es);
            }
            if (bpmnModel.getProcesses().isEmpty()) {
                return Y9Result.failure("导入失败： 上传的文件中不存在流程的信息");
            }
            if (bpmnModel.getLocationMap().isEmpty()) {
                BpmnAutoLayout bpmnLayout = new BpmnAutoLayout(bpmnModel);
                bpmnLayout.execute();
            }
            BpmnJsonConverter bpmnJsonConverter = new BpmnJsonConverter();
            ObjectNode modelNode = bpmnJsonConverter.convertToJson(bpmnModel);
            org.flowable.bpmn.model.Process process = bpmnModel.getMainProcess();
            String name = process.getId();
            if (StringUtils.isNotEmpty(process.getName())) {
                name = process.getName();
            }
            String description = process.getDocumentation();
            model.setKey(process.getId());
            model.setName(name);
            model.setDescription(description);
            model.setModelType(AbstractModel.MODEL_TYPE_BPMN);
            // 查询是否已经存在流程模板
            Model newModel = new Model();
            List<Model> models = modelRepository.findByKeyAndType(model.getKey(), model.getModelType());
            if (!models.isEmpty()) {
                Model updateModel = models.get(0);
                newModel.setId(updateModel.getId());
            }
            newModel.setName(model.getName());
            newModel.setKey(model.getKey());
            newModel.setModelType(model.getModelType());
            newModel.setCreated(Calendar.getInstance().getTime());
            newModel.setCreatedBy(userInfo.getName());
            newModel.setDescription(model.getDescription());
            newModel.setModelEditorJson(modelNode.toString());
            newModel.setLastUpdated(Calendar.getInstance().getTime());
            newModel.setLastUpdatedBy(userInfo.getName());
            newModel.setTenantId(tenantId);
            String createdBy = SecurityUtils.getCurrentUserId();
            modelService.createModel(newModel, createdBy);
            return Y9Result.successMsg("导入成功");
        } catch (Exception e) {
            LOGGER.error("导入流程模板失败,异常：{}", e.getMessage());

        }
        return Y9Result.failure("导入流程模板失败");
    }

    /**
     * 保存设计模型xml
     *
     * @param file 上传的文件
     * @param model 模型信息
     * @return Y9Result<String>
     */
    @RequestMapping(value = "/saveModelXml")
    public Y9Result<String> saveModelXml(MultipartFile file, ModelRepresentation model) {
        try {
            UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
            String tenantId = Y9LoginUserHolder.getTenantId();
            XMLInputFactory xif = XmlUtil.createSafeXmlInputFactory();
            InputStreamReader xmlIn = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
            XMLStreamReader xtr = xif.createXMLStreamReader(xmlIn);

            BpmnXMLConverter bpmnXmlConverter = new BpmnXMLConverter();
            BpmnModel bpmnModel = bpmnXmlConverter.convertToBpmnModel(xtr);
            // 模板验证
            ProcessValidator validator = new ProcessValidatorFactory().createDefaultProcessValidator();
            List<ValidationError> errors = validator.validate(bpmnModel);
            if (!errors.isEmpty()) {
                StringBuilder es = new StringBuilder();
                errors.forEach(ve -> es.append(ve.toString()).append("/n"));
                return Y9Result.failure("保存失败：模板验证失败，原因: " + es);
            }
            if (bpmnModel.getProcesses().isEmpty()) {
                return Y9Result.failure("保存失败： 文件中不存在流程的信息");
            }
            if (bpmnModel.getLocationMap().isEmpty()) {
                BpmnAutoLayout bpmnLayout = new BpmnAutoLayout(bpmnModel);
                bpmnLayout.execute();
            }
            BpmnJsonConverter bpmnJsonConverter = new BpmnJsonConverter();
            ObjectNode modelNode = bpmnJsonConverter.convertToJson(bpmnModel);
            org.flowable.bpmn.model.Process process = bpmnModel.getMainProcess();
            String name = process.getId();
            if (StringUtils.isNotEmpty(process.getName())) {
                name = process.getName();
            }
            String description = process.getDocumentation();
            model.setKey(process.getId());
            model.setName(name);
            model.setDescription(description);
            model.setModelType(AbstractModel.MODEL_TYPE_BPMN);
            // 查询是否已经存在流程模板
            Model newModel = new Model();
            List<Model> models = modelRepository.findByKeyAndType(model.getKey(), model.getModelType());
            if (!models.isEmpty()) {
                Model updateModel = models.get(0);
                newModel.setId(updateModel.getId());
            }
            newModel.setName(model.getName());
            newModel.setKey(model.getKey());
            newModel.setModelType(model.getModelType());
            newModel.setCreated(Calendar.getInstance().getTime());
            newModel.setCreatedBy(userInfo.getName());
            newModel.setDescription(model.getDescription());
            newModel.setModelEditorJson(modelNode.toString());
            newModel.setLastUpdated(Calendar.getInstance().getTime());
            newModel.setLastUpdatedBy(userInfo.getName());
            newModel.setTenantId(tenantId);
            String createdBy = SecurityUtils.getCurrentUserId();
            modelService.createModel(newModel, createdBy);
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            LOGGER.error("保存模型xml失败,异常：{}", e.getMessage());
        }
        return Y9Result.failure("保存失败");
    }
}
