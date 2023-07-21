package net.risesoft.api.itemadmin;

import net.risesoft.model.itemadmin.TaskVariableModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface TaskVariableApi {

    /**
     * 根据任务id,变量名获取变量值
     * 
     * @param tenantId
     * @param taskId
     * @param keyName
     * @return
     */
    TaskVariableModel findByTaskIdAndKeyName(String tenantId, String taskId, String keyName);
}
