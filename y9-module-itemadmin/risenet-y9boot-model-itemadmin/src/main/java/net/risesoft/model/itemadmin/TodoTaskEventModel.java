package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

import net.risesoft.enums.TodoTaskEventActionEnum;

/**
 * 待办任务事件模型
 *
 * @author qinman
 * @date 2025/07/24
 */
@Data
@AllArgsConstructor
public class TodoTaskEventModel implements Serializable {

    private static final long serialVersionUID = -6386589171523889848L;

    private TodoTaskEventActionEnum action;

    // 租户ID
    private String tenantId;

    // 流程实例
    private String processInstanceId;

    /// 任务ID
    private String taskId;

    // 是否新件 1 新件 0 已看件
    private String isNewTodo;

}