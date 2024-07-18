package net.risesoft.util;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ProcessDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.risesoft.y9.Y9Context;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public class WorkflowUtils {

    private static final Logger logger = LoggerFactory.getLogger(WorkflowUtils.class);

    /**
     * 导出图片文件到硬盘
     * 
     * @return 文件的全路径
     */
    public static String exportDiagramToFile(RepositoryService repositoryService, ProcessDefinition processDefinition) {
        String diagramResourceName = processDefinition.getDiagramResourceName();
        String key = processDefinition.getKey();
        int version = processDefinition.getVersion();

        try {
            if (diagramResourceName == null) {
                return null;
            }

            InputStream resourceAsStream =
                repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), diagramResourceName);
            byte[] b = new byte[resourceAsStream.available()];
            resourceAsStream.read(b, 0, b.length);

            String root = Y9Context.getWebRootRealPath();
            File file = new File(root);
            file = new File(file, "dynamicfile");
            file = new File(file, "flowchart");
            file = new File(file, key);
            file = new File(file, String.valueOf(version));
            file.mkdirs();

            file = new File(file, diagramResourceName);
            FileUtils.writeByteArrayToFile(file, b);

            return file.getAbsolutePath();
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 转换流程节点类型为中文说明
     * 
     * @param type 英文名称
     * @return 翻译后的中文名称
     */
    public static String parseToZhType(String type) {
        Map<String, String> types = new HashMap<String, String>(16);
        types.put("userTask", "用户任务");
        types.put("serviceTask", "系统任务");
        types.put(SysVariables.STARTEVENT, "开始节点");
        types.put(SysVariables.ENDEVENT, "结束节点");
        types.put("exclusiveGateway", "条件判断节点(系统自动根据条件处理)");
        types.put("inclusiveGateway", "并行处理任务");
        types.put("callActivity", "子流程");
        return types.get(type) == null ? type : types.get(type);
    }

}
