package net.risesoft.api;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
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
import org.flowable.ui.modeler.repository.ModelRepository;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.flowable.validation.ProcessValidator;
import org.flowable.validation.ProcessValidatorFactory;
import org.flowable.validation.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.node.ObjectNode;

import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.api.processadmin.ProcessModelApi;
import net.risesoft.model.platform.Person;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.FlowableTenantInfoHolder;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 流程设计相关接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@RestController
@RequestMapping(value = "/services/rest/processModel")
public class ProcessModelApiImpl implements ProcessModelApi {

    @Autowired
    private ModelService modelService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private PersonApi personApi;

    /**
     * 删除模型
     *
     * @param tenantId 租户id
     * @param modelId 模型id
     * @return Y9Result<String>
     */
    @Override
    @PostMapping(value = "/deleteModel", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<String> deleteModel(@RequestParam String tenantId, @RequestParam String modelId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        modelService.deleteModel(modelId);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 根据modelId部署流程
     *
     * @param tenantId 租户id
     * @param modelId 模型id
     * @return Y9Result<String>
     */
    @Override
    @PostMapping(value = "/deployModel", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<String> deployModel(@RequestParam String tenantId, @RequestParam String modelId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Model modelData = modelService.getModel(modelId);
        BpmnModel model = modelService.getBpmnModel(modelData);
        if (model.getProcesses().size() == 0) {
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
     * @param tenantId 租户id
     * @param modelId 模型id
     * @param response
     * @return
     */
    @Override
    @PostMapping(value = "/exportModel", produces = MediaType.APPLICATION_JSON_VALUE)
    public void exportModel(@RequestParam String tenantId, @RequestParam String modelId, HttpServletResponse response) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        try {
            Model model = modelService.getModel(modelId);
            byte[] bpmnBytes = modelService.getBpmnXML(model);

            ByteArrayInputStream in = new ByteArrayInputStream(bpmnBytes);
            String filename = model.getKey() + ".bpmn20.xml";
            response.setHeader("Content-Disposition", "attachment; filename=" + filename);
            IOUtils.copy(in, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取模型列表
     *
     * @param tenantId 租户id
     * @return Y9Result<List<Map<String, Object>>>
     */
    @GetMapping(value = "/getModelList", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public Y9Result<List<Map<String, Object>>> getModelList(@RequestParam String tenantId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<Map<String, Object>> items = new ArrayList<>();
        List<AbstractModel> list = modelService.getModelsByModelType(Model.MODEL_TYPE_BPMN);
        ProcessDefinition processDefinition = null;
        Map<String, Object> mapTemp = null;
        for (AbstractModel model : list) {
            mapTemp = new HashMap<>(16);
            mapTemp.put("id", model.getId());
            mapTemp.put("key", model.getKey());
            mapTemp.put("name", model.getName());
            mapTemp.put("version", 0);
            processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(model.getKey()).latestVersion().singleResult();
            if (null != processDefinition) {
                mapTemp.put("version", processDefinition.getVersion());
            }
            mapTemp.put("createTime", sdf.format(model.getCreated()));
            mapTemp.put("lastUpdateTime", sdf.format(model.getLastUpdated()));
            items.add(mapTemp);
        }
        return Y9Result.success(items, "获取成功");
    }

    /**
     * 获取模型xml
     *
     * @param tenantId 租户id
     * @param modelId 模型id
     * @param response
     * @return
     */
    @Override
    @GetMapping(value = "/getModelXml", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<String> getModelXml(@RequestParam String tenantId, @RequestParam String modelId, HttpServletResponse response) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        byte[] bpmnBytes = null;
        Map<String, Object> map = new HashMap<>();
        Model model = modelService.getModel(modelId);
        map.put("key", model.getKey());
        map.put("name", model.getName());
        bpmnBytes = modelService.getBpmnXML(model);
        return Y9Result.success(bpmnBytes == null ? "" : new String(bpmnBytes, Charset.forName("UTF-8")), "获取成功");
    }

    /**
     * 导入模型文件
     *
     * @param tenantId 租户id
     * @param file 文件
     * @return
     */
    @PostMapping(value = "/saveModelXml", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public Y9Result<String> saveModelXml(@RequestParam String tenantId, @RequestParam String userId, MultipartFile file) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        try {
            Person person = personApi.get(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);
            XMLInputFactory xif = XmlUtil.createSafeXmlInputFactory();
            InputStreamReader xmlIn = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
            XMLStreamReader xtr = xif.createXMLStreamReader(xmlIn);

            BpmnXMLConverter bpmnXmlConverter = new BpmnXMLConverter();
            BpmnModel bpmnModel = bpmnXmlConverter.convertToBpmnModel(xtr);
            // 模板验证
            ProcessValidator validator = new ProcessValidatorFactory().createDefaultProcessValidator();
            List<ValidationError> errors = validator.validate(bpmnModel);
            if (!errors.isEmpty()) {
                StringBuffer es = new StringBuffer();
                errors.forEach(ve -> es.append(ve.toString()).append("/n"));
                return Y9Result.failure("保存失败：模板验证失败，原因: " + es.toString());
            }
            if (bpmnModel.getProcesses().isEmpty()) {
                return Y9Result.failure("保存失败： 文件中不存在流程的信息");
            }
            if (bpmnModel.getLocationMap().size() == 0) {
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
            // 查询是否已经存在流程模板
            Model newModel = new Model();
            List<Model> models = modelRepository.findByKeyAndType(process.getId(), AbstractModel.MODEL_TYPE_BPMN);
            if (!models.isEmpty()) {
                Model updateModel = models.get(0);
                newModel.setId(updateModel.getId());
            }
            newModel.setName(name);
            newModel.setKey(process.getId());
            newModel.setModelType(AbstractModel.MODEL_TYPE_BPMN);
            newModel.setCreated(Calendar.getInstance().getTime());
            newModel.setCreatedBy(person.getName());
            newModel.setDescription(description);
            newModel.setModelEditorJson(modelNode.toString());
            newModel.setLastUpdated(Calendar.getInstance().getTime());
            newModel.setLastUpdatedBy(person.getName());
            newModel.setTenantId(tenantId);
            String createdBy = SecurityUtils.getCurrentUserId();
            newModel = modelService.createModel(newModel, createdBy);
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("保存失败");
    }

}
