package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 错误日志信息
 *
 * @author qinman
 * @date 2022/12/29
 */
@Data
public class ErrorLogModel implements Serializable {

    /**
     * 错误类型：任务相关
     */
    public static final String ERROR_TASK = "taskError";
    /**
     * 错误类型：流程相关
     */
    public static final String ERROR_PROCESS_INSTANCE = "processInstanceError";
    /**
     * 错误标识：任务发送
     */
    public static final String ERROR_FLAG_FORWRDING = "forwarding";
    /**
     * 错误标识：流程办结
     */
    public static final String ERROR_FLAG_PROCESS_COMLETE = "processComlete";
    /**
     * 错误标识：恢复待办
     */
    public static final String ERROR_FLAG_RECOVERY_COMLETED = "recoveryCompleted";
    /**
     * 错误标识：办结截转至数据中心
     */
    public static final String ERROR_FLAG_SAVE_OFFICE_DONE = "saveOfficeDone";
    /**
     * 错误标识：保存统一待办
     */
    public static final String ERROR_FLAG_SAVE_TODO_TASK = "saveTodoTask";
    /**
     * 错误标识：删除统一待办
     */
    public static final String ERROR_FLAG_DELETE_TODO = "deleteTodo";
    /**
     * 错误标识：删除年度数据
     */
    public static final String ERROR_FLAG_DELETE_YEARDATA = "deleteYearData";
    /**
     * 错误标识：抄送保存
     */
    public static final String ERROR_FLAG_SAVE_CHAOSONG = "saveChaoSong";

    private static final long serialVersionUID = -4465213360378742347L;
    /**
     * 主键
     */
    private String id;

    /**
     * 流程实例
     */
    private String processInstanceId;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 错误类型
     */
    private String errorType;

    /**
     * 错误标识
     */
    private String errorFlag;

    /**
     * 扩展字段
     */
    private String extendField;

    /**
     * 错误日志信息
     */
    private String text;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String updateTime;

}
