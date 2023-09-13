package net.risesoft.controller;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.ProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import net.risesoft.consts.UtilConsts;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.CustomRepositoryService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@RestController
@RequestMapping(value = "/vue/repository")
public class RepositoryVueController {

    @Autowired
    private CustomRepositoryService customRepositoryService;

    @Autowired
    protected ProcessEngineConfiguration processEngineConfiguration;

    /**
     * 删除流程定义
     *
     * @param deploymentId 部署id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/delete", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> delete(@RequestParam(required = true) String deploymentId) {
        Map<String, Object> map = customRepositoryService.delete(deploymentId);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }

    /**
     * 部署流程定义
     *
     * @param file 部署文件
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/deploy", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> deploy(MultipartFile file) {
        Map<String, Object> map = customRepositoryService.deploy(file);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }

    /**
     * 获取流程定义列表
     *
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Y9Result<List<Map<String, Object>>> list(@RequestParam(required = false) String resourceId) {
        Map<String, Object> map = customRepositoryService.list(resourceId);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.success((List<Map<String, Object>>)map.get("rows"), "获取成功");
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取流程实例
     *
     * @param resourceType
     * @param processInstanceId 流程实例id
     * @param processDefinitionId 流程定义id
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/process-instance")
    public void loadByProcessInstance(@RequestParam(required = true) String resourceType,
        @RequestParam(required = false) String processInstanceId,
        @RequestParam(required = true) String processDefinitionId, HttpServletResponse response) throws Exception {
        InputStream resourceAsStream =
            customRepositoryService.getProcessInstance(resourceType, processInstanceId, processDefinitionId);
        int ii = 1024;
        byte[] b = new byte[1024];
        int len = -1;
        while ((len = resourceAsStream.read(b, 0, ii)) != -1) {
            response.getOutputStream().write(b, 0, len);
        }
    }

    /**
     * 挂起、激活流程实例
     *
     * @param state 状态
     * @param processDefinitionId 流程定义id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/switchSuspendOrActive", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> switchSuspendOrActive(@RequestParam(required = true) String state,
        @RequestParam(required = true) String processDefinitionId) {
        Map<String, Object> map = customRepositoryService.switchSuspendOrActive(state, processDefinitionId);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }

    /**
     * 输出跟踪流程信息 在在办件、待办件中使用processInstanceId，因为需要知道当前的活动节点并标红 但在流程部署列表页面中查看流程图时只有processDefinitionId，没有processInstanceId
     *
     * @param processInstanceId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/trace")
    @ResponseBody
    public List<Map<String, Object>> traceProcess(@RequestParam("pid") String processInstanceId,
        @RequestParam("processDefinitionId") String processDefinitionId) throws Exception {
        List<Map<String, Object>> activityInfos = new ArrayList<Map<String, Object>>();
        if (StringUtils.isNotBlank(processInstanceId) || StringUtils.isNotBlank(processDefinitionId)) {
            if (StringUtils.isNotBlank(processInstanceId)) {

            } else {

            }
        }
        return activityInfos;
    }

    /**
     * 获取流程实例的xml
     *
     * @param resourceType
     * @param processInstanceId 流程实例id
     * @param processDefinitionId 流程定义id
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/processInstanceXml")
    public Y9Result<String> getXmlByProcessInstance(@RequestParam(required = true) String resourceType,
        @RequestParam(required = false) String processInstanceId,
        @RequestParam(required = true) String processDefinitionId, HttpServletResponse response) throws Exception {
        InputStream resourceAsStream =
            customRepositoryService.getProcessInstance(resourceType, processInstanceId, processDefinitionId);
        return Y9Result.success(IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8));
    }
}
