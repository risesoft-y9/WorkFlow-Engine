package net.risesoft.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public class CommonOpt {

    /**
     * 设置流程需要的变量
     *
     * @param taskSenderId 发送人的guid，即是由谁发送的
     * @param taskSender 发送人的中文名称，即是由谁发送的
     * @param routeToTaskId 目标节点的Id
     * @param users
     * @param documentTitle
     * @param multiInstance 串行、并行标志
     * @return
     */
    public static Map<String, Object> setVariables(String taskSenderId, String taskSender, String routeToTaskId,
        List<String> users, String multiInstance) {
        if (users == null || users.size() == 0) {
            try {
                throw new Exception("发送人不能为空");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String user = null;
        if (users.size() == 1) {
            user = users.get(0);
        }
        Map<String, Object> varMap = new HashMap<>(16);
        varMap.put(SysVariables.TASKSENDERID, taskSenderId);
        varMap.put(SysVariables.TASKSENDER, taskSender);
        if (StringUtils.isNotBlank(routeToTaskId)) {
            varMap.put(SysVariables.ROUTETOTASKID, routeToTaskId);
        }
        varMap.put(SysVariables.USER, user);
        varMap.put(SysVariables.USERS, users);
        return varMap;
    }

    /**
     * 设置流程需要的变量
     *
     * @param taskSenderId 发送人的guid，即是由谁发送的
     * @param taskSender 发送人的中文名称，即是由谁发送的
     * @param routeToTaskId 目标节点的Id
     * @param users
     * @param documentTitle
     * @param multiInstance 串行、并行标志
     * @return
     */
    public static Map<String, Object> setVariables(String taskSenderId, String taskSender, String routeToTaskId,
        List<String> users, String processSerialNumber, String multiInstance, Map<String, Object> map) {
        map.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
        map.putAll(setVariables(taskSenderId, taskSender, routeToTaskId, users, multiInstance));
        return map;
    }

    /**
     * 设置流程需要的变量
     *
     * @param taskSenderId 发送人的guid，即是由谁发送的
     * @param taskSender 发送人的中文名称，即是由谁发送的
     * @param routeToTaskId 目标节点的Id
     * @param users
     * @param documentTitle
     * @param multiInstance 串行、并行标志
     * @return
     */
    public static Map<String, Object> setVariables4Dept(String taskSenderId, String taskSender, String routeToTaskId,
        List<List<String>> users, String documentTitle, String multiInstance) {
        if (users == null || users.size() == 0) {
            try {
                throw new Exception("发送人不能为空");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Map<String, Object> varMap = new HashMap<>(16);
        varMap.put(SysVariables.TASKSENDERID, taskSenderId);
        varMap.put(SysVariables.TASKSENDER, taskSender);
        if (StringUtils.isNotBlank(routeToTaskId)) {
            varMap.put(SysVariables.ROUTETOTASKID, routeToTaskId);
        }
        varMap.put(SysVariables.USERS, users);
        varMap.put(SysVariables.DOCUMENTTITLE, documentTitle);
        return varMap;
    }
}
