package net.risesoft.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.flowable.engine.RepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;

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
    private Y9Properties y9Config;

    /**
     * 创建模型
     *
     * @param name 流程名称
     * @param key 流程定义key
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
        String modelId = "";
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
        // UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        // String tenantId = userInfo.getTenantId(), personId = userInfo.getPersonId();
        // boolean tenantManager = userInfo.isGlobalManager();
        List<Map<String, Object>> items = new ArrayList<>();

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
    public Y9Result<Map<String, Object>> getModelXml(@RequestParam String modelId, HttpServletResponse response) {
        byte[] bpmnBytes = null;
        Map<String, Object> map = new HashMap<>();
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
     * @return
     */
    @ResponseBody
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
     * @param file
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/saveModelXml")
    public Y9Result<String> saveModelXml(MultipartFile file) {
        try {
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("保存失败");
    }
}
