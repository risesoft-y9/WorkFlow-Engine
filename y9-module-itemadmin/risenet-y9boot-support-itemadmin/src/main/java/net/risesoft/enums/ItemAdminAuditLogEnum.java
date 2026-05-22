package net.risesoft.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 事项管理模块审计日志枚举类
 *
 * @author risesoft
 */
@Getter
@RequiredArgsConstructor
public enum ItemAdminAuditLogEnum {
    // 办件操作
    DOCUMENT_ADD("DOCUMENT_ADD", "新建办件 [{}]"),
    DOCUMENT_COMPLETE("DOCUMENT_COMPLETE", "办件 [{}] 办结"),
    DOCUMENT_SAVE("DOCUMENT_SAVE", "保存办件 [{}]"),
    DOCUMENT_DELETE("DOCUMENT_DELETE", "删除办件 [{}]"),
    DOCUMENT_SEND("DOCUMENT_SEND", "办件 [{}] 发送至 [{}]"),
    DOCUMENT_SUBMIT_SEND("DOCUMENT_SUBMIT_SEND", "提交办件 [{}] 发送至 [{}]"),
    DOCUMENT_REJECT("DOCUMENT_REJECT", "退回办件 [{}]"),
    DOCUMENT_CANCEL("DOCUMENT_CANCEL", "撤回办件 [{}]"),
    DOCUMENT_REDO("DOCUMENT_REDO", "重新办理 [{}]"),

    // 表单数据操作
    FORM_SUB_DATA_DELETE("FORM_SUB_DATA_DELETE", "子表单数据删除,业务表 [{}],guid [{}]"),
    FORM_DATA_DELETE("FORM_DATA_DELETE", "表单数据删除,业务表 [{}],guid [{}]"),
    FORM_PRE_DATA_DELETE("FORM_PRE_DATA_DELETE", "前置表单数据删除,业务表 [{}],guid [{}]"),

    // 流程操作
    BUTTON_TAKEBACK_TASK_DEF_KEY("BUTTON_TAKEBACK_TASK_DEF_KEY", "办理人 [{}] 在任务节点 [{}] 收回办件 [{}] 至指定任务节点 [{}] "),

    // 待办操作
    TODO_CLAIM("TODO_CLAIM", "待办 [{}] 签收"),
    TODO_UNCLAIM("TODO_UNCLAIM", "待办 [{}] 取消签收"),
    TODO_DELEGATE("TODO_DELEGATE", "待办 [{}] 委托给 [{}]"),

    // 在办操作
    DOING_TRANSFER("DOING_TRANSFER", "在办 [{}] 转办给 [{}]"),
    DOING_ADD_SIGN("DOING_ADD_SIGN", "在办 [{}] 加签 [{}]"),
    DOING_SUB_SIGN("DOING_SUB_SIGN", "在办 [{}] 减签 [{}]"),

    // 多人会签操作
    MULTI_INSTANCE_PARALLEL_ADD("MULTI_INSTANCE_PARALLEL_ADD", "并行加签 [{}]"),
    MULTI_INSTANCE_ADD("MULTI_INSTANCE_ADD", "会签任务 [{}] 加签 [{}]"),
    MULTI_INSTANCE_DELETE("MULTI_INSTANCE_DELETE", "会签任务 [{}] 减签"),

    // 退回操作
    DOCUMENT_ROLLBACK("DOCUMENT_ROLLBACK", "办件 [{}] 退回"),
    DOCUMENT_ROLLBACK_TO_SENDER("DOCUMENT_ROLLBACK_TO_SENDER", "办件 [{}] 退回给上一步发送人"),
    DOCUMENT_ROLLBACK_TO_STARTER("DOCUMENT_ROLLBACK_TO_STARTER", "办件 [{}] 退回到登记人"),
    DOCUMENT_ROLLBACK_TO_HISTORY("DOCUMENT_ROLLBACK_TO_HISTORY", "办件 [{}] 退回至历史节点 [{}]"),
    DOCUMENT_REFUSE_CLAIM_ROLLBACK("DOCUMENT_REFUSE_CLAIM_ROLLBACK", "最后一人拒签退回"),

    // 直接发送操作
    DOCUMENT_DIRECT_SEND("DOCUMENT_DIRECT_SEND", "办件 [{}] 直接发送至流程启动人"),

    // 重定位操作
    DOCUMENT_REPOSITION("DOCUMENT_REPOSITION", "办件 [{}] 重定位至节点 [{}]"),

    // 特殊办结操作
    DOCUMENT_SPECIAL_COMPLETE("DOCUMENT_SPECIAL_COMPLETE", "办件 [{}] 特殊办结"),

    // 收回操作
    DOCUMENT_TAKEBACK("DOCUMENT_TAKEBACK", "办件 [{}] 收回"),
    DOCUMENT_TAKEBACK_TO_TASK("DOCUMENT_TAKEBACK_TO_TASK", "办件 [{}] 收回至指定节点"),

    // 办结操作
    DONE_RECALL("DONE_RECALL", "办结件 [{}] 收回"),

    // 草稿操作
    DRAFT_SAVE("DRAFT_SAVE", "保存草稿 [{}]"),
    DRAFT_DELETE("DRAFT_DELETE", "删除草稿 [{}]"),
    DRAFT_SEND("DRAFT_SEND", "草稿 [{}] 发送"),

    // 附件操作
    ATTACHMENT_UPLOAD("ATTACHMENT_UPLOAD", "上传附件 [{}]"),
    ATTACHMENT_UPLOAD_INFO_UPDATE("ATTACHMENT_UPLOAD_INFO_UPDATE", "更新附件 [{}] 上传信息 "),
    ATTACHMENT_DELETE("ATTACHMENT_DELETE", "删除附件 [{}]"),
    ATTACHMENT_DOWNLOAD("ATTACHMENT_DOWNLOAD", "下载附件 [{}]"),
    ATTACHMENT_SAVEINFO("ATTACHMENT_SAVEINFO", "保存附件信息 [{}]"),

    // wps 操作
    WPS_SAVE("WPS_SAVE", "保存 WPS 文档 [{}]"),
    WPS_DOWNLOAD("WPS_DOWNLOAD", "下载 WPS 文档 [{}]"),

    // 意见操作
    OPINION_ADD("OPINION_ADD", "添加意见 [{}]"),
    OPINION_UPDATE("OPINION_UPDATE", "更新意见 [{}]"),
    OPINION_DELETE("OPINION_DELETE", "删除意见 [{}]"),
    OPINION_SIGN_ADD("OPINION_SIGN", "签写意见添加 [{}]"),
    OPINION_SIGN_UPDATE("OPINION_SIGN_UPDATE", "签写意见更新 [{}]"),
    OPINION_SIGN_DELETE("OPINION_SIGN_DELETE", "签写意见删除 [{}]"),

    // 关联流程操作
    ASSOCIATED_FILE_SAVE("ASSOCIATED_FILE_SAVE", "保存关联流程 [{}]"),
    ASSOCIATED_FILE_DELETE("ASSOCIATED_FILE_DELETE", "删除关联流程 [{}]"),

    // 抄送操作
    CHAOSONG_SEND("CHAOSONG_SEND", "抄送办件 [{}] 给 [{}]"),
    CHAOSONG_READ("CHAOSONG_READ", "抄送件 [{}] 已读"),
    CHAOSONG_DELETE("CHAOSONG_DELETE", "删除抄送 [{}]"),

    // 催办操作
    REMINDER_SEND("REMINDER_SEND", "催办 [{}]"),

    // 委托操作
    ENTRUST_ADD("ENTRUST_ADD", "事项 [{}] 中，操作人 [{}] 添加对 [{}] 的委托"),
    ENTRUST_DELETE("ENTRUST_DELETE", "事项 [{}] 中，操作人 [{}] 删除对 [{}] 的委托"),
    ENTRUST_UPDATE("ENTRUST_UPDATE", "事项 [{}] 中，操作人 [{}] 更新对 [{}] 的委托"),

    // 事项操作
    ITEM_START("ITEM_START", "启动事项 [{}]"),
    ITEM_CONFIG_UPDATE("ITEM_CONFIG_UPDATE", "更新事项配置 [{}]"),

    // 流程变量操作
    PROCESS_PARAM_SAVE("PROCESS_PARAM_SAVE", "保存流程变量 [{}]"),
    PROCESS_PARAM_UPDATE("PROCESS_PARAM_UPDATE", "更新流程变量 [{}]"),

    // 流程跟踪操作
    PROCESS_TRACK("PROCESS_TRACK", "查看流程跟踪 [{}]"),

    // 回收站操作
    RECYCLE_RESTORE("RECYCLE_RESTORE", "回收站恢复 [{}]"),
    RECYCLE_DELETE("RECYCLE_DELETE", "回收站删除 [{}]"),

    // 公告/通知操作
    OFFICE_DONE_INFO_ADD("OFFICE_DONE_INFO_ADD", "添加办结公告 [{}]"),
    OFFICE_DONE_INFO_DELETE("OFFICE_DONE_INFO_DELETE", "删除办结公告 [{}]"),

    // 关注/跟踪操作
    OFFICE_FOLLOW_ADD("OFFICE_FOLLOW_ADD", "关注 [{}]"),
    OFFICE_FOLLOW_DELETE("OFFICE_FOLLOW_DELETE", "取消关注 [{}]");

    private final String action;
    private final String description;
}
