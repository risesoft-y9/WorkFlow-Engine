package net.risesoft.api.processadmin;

import com.fasterxml.jackson.databind.node.ObjectNode;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface BpmnModelApi {

    /**
     * 导入流程模板
     *
     * @param tenantId 租户id
     * @param userId   用户id
     * @param file     导入的xml文件
     * @return
     */
    public Map<String, Object> importProcessModel(String tenantId, String userId, MultipartFile file);

    /**
     * 保存模型xml
     *
     * @param tenantId 租户id
     * @param userId   用户id
     * @param modelId  模板id
     * @param file     模型文件
     * @return
     */
    Y9Result<String> saveModelXml(String tenantId, String userId, String modelId, MultipartFile file);

    /**
     * 获取流程设计模型xml
     *
     * @param tenantId 租户id
     * @param modelId  模型id
     * @return
     */
    Y9Result<Map<String, Object>> getModelXml(String tenantId, String modelId);

    /**
     * 获取模型列表
     *
     * @param tenantId 租户id
     * @return
     */
    Y9Result<List<Map<String, Object>>> getModelList(String tenantId);


    /**
     * 根据Model部署流程
     *
     * @param tenantId 租户id
     * @param modelId  模型id
     * @return
     */
    Y9Result<String> deployModel(String tenantId, String modelId);

    /**
     * 删除模型
     *
     * @param tenantId 租户id
     * @param modelId  模型id
     * @return
     */
    Y9Result<String> deleteModel(String tenantId, String modelId);

    /**
     * 生成流程图
     *
     * @param tenantId          租户id
     * @param processInstanceId 流程实例id
     * @return byte[]
     * @throws Exception Exception
     */
    byte[] genProcessDiagram(String tenantId, String processInstanceId) throws Exception;

    /**
     * 获取流程图模型
     *
     * @param tenantId          租户id
     * @param processInstanceId 流程实例id
     * @return Map
     * @throws Exception Exception
     */
    Map<String, Object> getBpmnModel(String tenantId, String processInstanceId) throws Exception;

    /**
     * 获取流程图数据
     *
     * @param tenantId          租户id
     * @param processInstanceId 流程实例id
     * @return Map
     * @throws Exception Exception
     */
    Map<String, Object> getFlowChart(String tenantId, String processInstanceId) throws Exception;

}
