package net.risesoft.controller;

import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.validation.ProcessValidator;
import org.flowable.validation.ProcessValidatorFactory;
import org.flowable.validation.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import net.risesoft.api.itemadmin.ActDeModelApi;
import net.risesoft.enums.platform.AuthorityEnum;
import net.risesoft.model.itemadmin.ActDeModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.util.XmlUtil;
import net.risesoft.y9.Y9LoginUserHolder;

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
    private ActDeModelApi actDeModelApi;

    @Autowired
    private RepositoryService repositoryService;

    /**
     * 删除模型
     *
     * @param modelId 模型id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/deleteModel", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> deleteModel(@RequestParam(required = true) String modelId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        actDeModelApi.deleteModel(tenantId, modelId);
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
        String tenantId = Y9LoginUserHolder.getTenantId();
        ActDeModel modelData = actDeModelApi.getModel(tenantId, modelId);
        if (modelData.getModelByte() == null) {
            return Y9Result.failure("数据模型不符要求，请至少设计一条主线流程。");
        }
        String processName = modelData.getName() + ".bpmn20.xml";
        repositoryService.createDeployment().name(modelData.getName()).addBytes(processName, modelData.getModelByte())
            .deploy();
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
    public void exportModel(@RequestParam String modelId, HttpServletResponse response) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            ActDeModel model = actDeModelApi.getModel(tenantId, modelId);
            byte[] bpmnBytes = model.getModelByte();

            ByteArrayInputStream in = new ByteArrayInputStream(bpmnBytes);
            String filename = model.getModelKey() + ".bpmn20.xml";
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
    public Y9Result<List<ActDeModel>> getModelList() {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<ActDeModel> list = actDeModelApi.getModelList(tenantId);
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 获取流程设计模型xml
     *
     * @param modelId
     * @param response
     * @return
     */
    @RequestMapping(value = "/getModelXml")
    public Y9Result<Map<String, Object>> getModelXml(@RequestParam String modelId, HttpServletResponse response) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        byte[] bpmnBytes = null;
        Map<String, Object> map = new HashMap<>();
        ActDeModel model = actDeModelApi.getModel(tenantId, modelId);
        map.put("key", model.getModelKey());
        map.put("name", model.getName());
        bpmnBytes = model.getModelByte();
        map.put("xml", bpmnBytes == null ? "" : new String(bpmnBytes));
        return Y9Result.success(map, "获取成功");
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
    public Map<String, Object> importProcessModel(MultipartFile file) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("success", false);
        map.put("msg", "导入失败");
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
                StringBuffer es = new StringBuffer();
                errors.forEach(ve -> es.append(ve.toString()).append("/n"));
                map.put("msg", "导入失败：模板验证失败，原因: " + es.toString());
                return map;
            }
            if (bpmnModel.getProcesses().isEmpty()) {
                map.put("msg", "导入失败： 上传的文件中不存在流程的信息");
                return map;
            }
            org.flowable.bpmn.model.Process process = bpmnModel.getMainProcess();
            String name = process.getId();
            if (StringUtils.isNotEmpty(process.getName())) {
                name = process.getName();
            }
            ActDeModel newModel = new ActDeModel();
            newModel.setName(name);
            newModel.setModelKey(process.getId());
            newModel.setCreated(Calendar.getInstance().getTime());
            newModel.setCreatedBy(userInfo.getName());
            newModel.setDescription(process.getDocumentation());
            newModel.setLastUpdated(Calendar.getInstance().getTime());
            newModel.setLastUpdatedBy(userInfo.getName());
            newModel.setTenantId(tenantId);
            newModel.setModelByte(file.getBytes());
            newModel.setVersion(1);
            actDeModelApi.saveModel(tenantId, newModel);
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
    public Y9Result<String> saveModelXml(MultipartFile file) {
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
                StringBuffer es = new StringBuffer();
                errors.forEach(ve -> es.append(ve.toString()).append("/n"));
                return Y9Result.failure("保存失败：模板验证失败，原因: " + es.toString());
            }
            if (bpmnModel.getProcesses().isEmpty()) {
                return Y9Result.failure("保存失败： 文件中不存在流程的信息");
            }
            org.flowable.bpmn.model.Process process = bpmnModel.getMainProcess();
            String name = process.getId();
            if (StringUtils.isNotEmpty(process.getName())) {
                name = process.getName();
            }
            ActDeModel newModel = new ActDeModel();
            newModel.setName(name);
            newModel.setModelKey(process.getId());
            newModel.setCreated(Calendar.getInstance().getTime());
            newModel.setCreatedBy(userInfo.getName());
            newModel.setDescription(process.getDocumentation());
            newModel.setLastUpdated(Calendar.getInstance().getTime());
            newModel.setLastUpdatedBy(userInfo.getName());
            newModel.setTenantId(tenantId);
            newModel.setModelByte(file.getBytes());
            newModel.setVersion(1);
            actDeModelApi.saveModel(tenantId, newModel);
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("保存失败");
    }
}
