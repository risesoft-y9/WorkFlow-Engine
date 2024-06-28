package y9.client.rest.processadmin;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import net.risesoft.api.processadmin.BpmnModelApi;
import net.risesoft.model.processadmin.Y9BpmnModel;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "BpmnModelApiClient", name = "${y9.service.processAdmin.name:processAdmin}",
    url = "${y9.service.processAdmin.directUrl:}",
    path = "/${y9.service.processAdmin.name:processAdmin}/services/rest/bpmnModel")
public interface BpmnModelApiClient extends BpmnModelApi {

    /**
     * 删除模型
     *
     * @param modelId 模型id
     * @return
     */
    @Override
    @PostMapping(value = "/deleteModel")
    Y9Result<Object> deleteModel(@RequestParam("tenantId") String tenantId, @RequestParam("modelId") String modelId);

    /**
     * 根据Model部署流程
     *
     * @param modelId 模型id
     * @return
     */
    @Override
    @PostMapping(value = "/deployModel")
    Y9Result<Object> deployModel(@RequestParam("tenantId") String tenantId, @RequestParam("modelId") String modelId);

    /**
     * 生成流程图
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例
     * @return byte[]
     * @throws Exception Exception
     */
    @Override
    @PostMapping("/genProcessDiagram")
    Y9Result<String> genProcessDiagram(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 获取流程图模型
     *
     * @param tenantId 租户id
     * @param processInstanceId processInstanceId
     * @return Map
     * @throws Exception Exception
     */
    @Override
    @GetMapping("/getBpmnModel")
    Y9Result<Y9BpmnModel> getBpmnModel(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 获取流程图数据
     *
     * @param tenantId
     * @param processInstanceId
     * @return
     * @throws Exception
     */
    @Override
    @GetMapping("/getFlowChart")
    Map<String, Object> getFlowChart(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId) throws Exception;

    /**
     * 获取模型列表
     *
     * @return
     */
    @Override
    @GetMapping(value = "/getModelList")
    public Y9Result<List<Map<String, Object>>> getModelList(@RequestParam("tenantId") String tenantId);

    /**
     * 获取流程设计模型xml
     *
     * @param modelId
     * @return
     */
    @Override
    @GetMapping(value = "/getModelXml")
    public Y9Result<Map<String, Object>> getModelXml(@RequestParam("tenantId") String tenantId,
        @RequestParam("modelId") String modelId);

    /**
     * 导入流程模板
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param file 导入的xml文件
     * @return
     */
    @Override
    @PostMapping(value = "/import")
    Map<String, Object> importProcessModel(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("file") MultipartFile file);

    /**
     * 保存设计模型xml
     *
     * @param modelId
     * @param file
     * @return
     */
    @Override
    @PostMapping(value = "/saveModelXml")
    public Y9Result<String> saveModelXml(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("modelId") String modelId,
        @RequestParam("file") MultipartFile file);

}
