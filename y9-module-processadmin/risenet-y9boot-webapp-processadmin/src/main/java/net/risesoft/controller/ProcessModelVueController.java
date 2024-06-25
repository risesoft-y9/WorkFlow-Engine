package net.risesoft.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;

import org.flowable.engine.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
@RequestMapping(value = "/vue/processModel")
public class ProcessModelVueController {

    private final RepositoryService repositoryService;

    @Autowired
    private Y9Properties y9Config;

    /**
     * 创建模型
     *
     * @param name 流程名称
     * @param key 流程定义key
     * @param description 描述
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = "application/json")
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
        String modelId = "";
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
    @RequestMapping(value = "/deleteModel", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> deleteModel(@RequestParam @NotBlank String modelId) {
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 根据Model部署流程
     *
     * @param modelId 模型id
     * @return Y9Result<String>
     */
    @RequestMapping(value = "/deployModel", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> deployModel(@RequestParam(required = true) String modelId) {
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
    @RequestMapping(value = "/getModelList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> getModelList() {
        List<Map<String, Object>> items = new ArrayList<>();

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
    public Map<String, Object> importProcessModel(MultipartFile file) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("success", false);
        map.put("msg", "导入失败");

        return map;
    }

    /**
     * 保存设计模型xml
     *
     * @param file 上传的文件
     * @param model 模型信息
     * @return Y9Result<String>
     */
    @RequestMapping(value = "/saveModelXml")
    public Y9Result<String> saveModelXml(MultipartFile file) {
        try {
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            LOGGER.error("保存模型xml失败,异常：{}", e.getMessage());
        }
        return Y9Result.failure("保存失败");
    }
}
