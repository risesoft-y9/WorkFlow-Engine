package net.risesoft.service;

import java.util.Map;

import net.risesoft.model.processadmin.TaskModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
public interface ActivitiOptService {

    /**
     * 启动流程,用户任务基于人员时
     * 
     * @param processSerialNumber
     * @param documentTitle
     * @param processDefinitionKey
     * @param systemName
     * @param map
     * @return
     */
    TaskModel startProcess(String processSerialNumber, String processDefinitionKey, String systemName,
        Map<String, Object> map);
}
