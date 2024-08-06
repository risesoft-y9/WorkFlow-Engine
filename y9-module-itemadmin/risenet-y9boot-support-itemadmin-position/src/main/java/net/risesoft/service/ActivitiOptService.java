package net.risesoft.service;

import java.util.Map;

import net.risesoft.model.processadmin.TaskModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ActivitiOptService {

    /**
     * 启动流程,用户任务基于人员时
     *
     * @param processSerialNumber 流程编号
     * @param processDefinitionKey 流程定义key
     * @param systemName 系统名称
     * @param map 流程变量
     * @return
     */
    TaskModel startProcess(String processSerialNumber, String processDefinitionKey, String systemName,
        Map<String, Object> map);
}
