package net.risesoft.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.risesoft.api.permission.PersonResourceApi;
import net.risesoft.enums.AuthorityEnum;
import net.risesoft.model.Resource;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 流程模型控制器
 */

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@RestController
@RequestMapping(value = "/vue/processModel")
public class ProcessModelVueController {

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ModelService modelService;

    @Autowired
    private PersonResourceApi personResourceApi;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private Y9Properties y9Config;

    /**
     * 创建模型
     *
     * @param name        流程名称
     * @param key         流程定义key
     * @param description 描述
     * @param request
     * @param response
     */
    @ResponseBody
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> create(@RequestParam(required = true) String name,
                                   @RequestParam(required = true) String key, @RequestParam(required = false) String description,
                                   HttpServletRequest request, HttpServletResponse response) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String personName = userInfo.getName();
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode editorNode = objectMapper.createObjectNode();
        editorNode.put("id", "canvas");
        editorNode.put("resourceId", "canvas");
        ObjectNode stencilSetNode = objectMapper.createObjectNode();
        stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
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
        /**
         * 跳转画图页面
         */
        String path = y9Config.getCommon().getProcessAdminBaseUrl() + "/modeler.html#/editor/" + modelId;
        return Y9Result.success(path, "创建成功");
    }

    /**
     * 删除模型
     *
     * @param modelId 模型id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/deleteModel", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> deleteModel(@RequestParam(required = true) String modelId) {
        modelService.deleteModel(modelId);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 根据Model部署流程
     *
     * @param modelId 模型id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/deployModel", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> deployModel(@RequestParam(required = true) String modelId) {
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
     * @param modelId
     * @param response
     * @return
     */
    @RequestMapping(value = "/exportModel")
    public void exportModel(@RequestParam(required = true) String modelId, HttpServletResponse response) {
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
            logger.error("导出模型失败，modelId=" + modelId);
        }
    }

    /**
     * 获取模型列表
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getModelList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> getModelList(@RequestParam(required = false) String resourceId) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String tenantId = userInfo.getTenantId(), personId = userInfo.getPersonId();
        boolean tenantManager = userInfo.isGlobalManager();
        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        List<AbstractModel> list = modelService.getModelsByModelType(Model.MODEL_TYPE_BPMN);
        ProcessDefinition processDefinition = null;
        if (tenantManager || userInfo.getManagerLevel() == 1) {
            Map<String, Object> mapTemp = null;
            for (AbstractModel model : list) {
                mapTemp = new HashMap<String, Object>(16);
                mapTemp.put("id", model.getId());
                mapTemp.put("key", model.getKey());
                mapTemp.put("name", model.getName());
                mapTemp.put("version", 0);
                processDefinition = repositoryService.createProcessDefinitionQuery()
                        .processDefinitionKey(model.getKey()).latestVersion().singleResult();
                if (null != processDefinition) {
                    mapTemp.put("version", processDefinition.getVersion());
                }
                mapTemp.put("createTime", sdf.format(model.getCreated()));
                mapTemp.put("lastUpdateTime", sdf.format(model.getLastUpdated()));
                items.add(mapTemp);
            }
        } else {
            Map<String, Object> mapTemp = null;
            List<Resource> resourceList =
                    personResourceApi.listSubResources(tenantId, personId, AuthorityEnum.BROWSE.getValue(), resourceId);
            for (AbstractModel model : list) {
                for (Resource resource : resourceList) {
                    if (resource.getCustomId().equals(model.getKey())) {
                        mapTemp = new HashMap<String, Object>(16);
                        mapTemp.put("id", model.getId());
                        mapTemp.put("key", model.getKey());
                        mapTemp.put("name", model.getName());
                        mapTemp.put("version", 0);
                        processDefinition = repositoryService.createProcessDefinitionQuery()
                                .processDefinitionKey(model.getKey()).latestVersion().singleResult();
                        if (null != processDefinition) {
                            mapTemp.put("version", processDefinition.getVersion());
                        }
                        mapTemp.put("createTime", sdf.format(model.getCreated()));
                        mapTemp.put("lastUpdateTime", sdf.format(model.getLastUpdated()));
                        items.add(mapTemp);
                    }
                }
            }
        }
        return Y9Result.success(items, "获取成功");
    }

    /**
     * 获取流程设计模型xml
     *
     * @param modelId
     * @param response
     * @return
     */
    @RequestMapping(value = "/getModelXml")
    public Y9Result<Map<String, Object>> getModelXml(@RequestParam(required = true) String modelId, HttpServletResponse response) {
        byte[] bpmnBytes = null;
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            Model model = modelService.getModel(modelId);
            map.put("key", model.getKey());
            map.put("name", model.getName());
            bpmnBytes = modelService.getBpmnXML(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
        map.put("xml", bpmnBytes == null ? "" : new String(bpmnBytes));
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 编辑模型
     *
     * @param modelId
     * @param request
     * @param response
     */
    @RequestMapping(value = "/editor/{modelId}")
    public void gotoEditor(@PathVariable("modelId") String modelId, HttpServletRequest request,
                           HttpServletResponse response) {
        try {
            response.sendRedirect(request.getContextPath() + "/modeler.html#/editor/" + modelId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 导入流程模板
     *
     * @param file
     * @param model
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/import")
    public Map<String, Object> importProcessModel(MultipartFile file, ModelRepresentation model) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("success", false);
        map.put("msg", "导入失败");
        try {
            UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
            String tenantId = Y9LoginUserHolder.getTenantId();
            XMLInputFactory xif = XmlUtil.createSafeXmlInputFactory();
            InputStreamReader xmlIn = new InputStreamReader(file.getInputStream(), "UTF-8");
            XMLStreamReader xtr = xif.createXMLStreamReader(xmlIn);

            BpmnXMLConverter bpmnXmlConverter = new BpmnXMLConverter();
            BpmnModel bpmnModel = bpmnXmlConverter.convertToBpmnModel(xtr);
            // 模板验证
            ProcessValidator validator = new ProcessValidatorFactory().createDefaultProcessValidator();
            List<ValidationError> errors = validator.validate(bpmnModel);
            if (!errors.isEmpty()) {
                StringBuffer es = new StringBuffer();
                errors.forEach(ve -> es.append(ve.toString()).append("/n"));
                map.put("msg", "导入失败：模板验证失败，原因: " + es.toString());
                return map;
            }
            if (bpmnModel.getProcesses().isEmpty()) {
                map.put("msg", "导入失败： 上传的文件中不存在流程的信息");
                return map;
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
            newModel = modelService.createModel(newModel, createdBy);
            map.put("success", true);
            map.put("msg", "导入成功");
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 保存设计模型xml
     *
     * @param file
     * @param model
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/saveModelXml")
    public Y9Result<String> saveModelXml(MultipartFile file, ModelRepresentation model) {
        try {
            UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
            String tenantId = Y9LoginUserHolder.getTenantId();
            XMLInputFactory xif = XmlUtil.createSafeXmlInputFactory();
            InputStreamReader xmlIn = new InputStreamReader(file.getInputStream(), "UTF-8");
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
            newModel = modelService.createModel(newModel, createdBy);
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("保存失败");
    }
}
