package net.risesoft.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * FlowableUI模块审计日志枚举类
 *
 * @author zhangchongjie
 */
@Getter
@RequiredArgsConstructor
public enum FlowableUiAuditLogEnum {
    // ========== 附件操作 ==========
    ATTACHMENT_DOWNLOAD("ATTACHMENT_DOWNLOAD", "附件下载 [{}]"),
    ATTACHMENT_DELETE("ATTACHMENT_DELETE", "删除附件 [{}]"),
    ATTACHMENT_PACK_DOWNLOAD("ATTACHMENT_PACK_DOWNLOAD", "打包zip下载办件 [{}] 所有的附件"),
    ATTACHMENT_UPLOAD("ATTACHMENT_UPLOAD", "上传附件 [{}]"),
    ATTACHMENT_SAVE_INFO("ATTACHMENT_SAVE_INFO", "保存附件信息 [{}]"),
    ATTACHMENT_UPLOAD_WITH_FORM("ATTACHMENT_UPLOAD_WITH_FORM", "上传附件带表单信息 [{}]"),
    ATTACHMENT_SAVE_ORDER("ATTACHMENT_SAVE_ORDER", "保存附件排序 [{}]"),

    // ========== 关联流程操作 ==========
    ASSOCIATED_FILE_DELETE("ASSOCIATED_FILE_DELETE", "删除关联流程 [{}]"),
    ASSOCIATED_FILE_SAVE("ASSOCIATED_FILE_SAVE", "保存关联的流程 [{}]"),

    // ========== 按钮操作 ==========
    BUTTON_CLAIM("BUTTON_CLAIM", "签收 [{}]"),
    BUTTON_COMPLETE_TASK("BUTTON_COMPLETE_TASK", "任务完成 [{}]"),
    BUTTON_CONSULT("BUTTON_CONSULT", "任务协商 [{}]"),
    BUTTON_CUSTOM_PROCESS_HANDLE("BUTTON_CUSTOM_PROCESS_HANDLE", "定制流程办理 [{}]"),
    BUTTON_DIRECT_SEND("BUTTON_DIRECT_SEND", "发送至流程启动人 [{}]"),
    BUTTON_HANDLE_PARALLEL("BUTTON_HANDLE_PARALLEL", "办件 [{}] 并行任务 [{}] 办理完成"),
    BUTTON_HANDLE_SERIAL("BUTTON_HANDLE_SERIAL", "办件 [{}] 串行任务 [{}] 送下一人 "),
    BUTTON_REASSIGN("BUTTON_REASSIGN", "任务 [{}] 委托 [{}]"),
    BUTTON_REFUSE_CLAIM("BUTTON_REFUSE_CLAIM", "办理人 [{}] 拒签任务 [{}   ]"),
    BUTTON_REFUSE_CLAIM_ROLLBACK("BUTTON_REFUSE_CLAIM_ROLLBACK", "最后一人 [{}] 拒签退回任务 [{}]"),
    BUTTON_REPOSITION("BUTTON_REPOSITION", "操作人 [{}] 将任务 [{}] 重定向至任务 [{}] 、办理人 [{}]"),
    BUTTON_ROLLBACKTWO_HISTORY("BUTTON_ROLLBACKTWO_HISTORY", "操作人 [{}] 将任务 [{}] 多步退回至任务 [{}] 、办理人 [{}]"),
    BUTTON_ROLLBACK("BUTTON_ROLLBACK", "办理人 [{}] 因 [{}] 将任务 [{}] 退回上一步任务 [{}]"),
    BUTTON_ROLLBACK_TO_SENDER("BUTTON_ROLLBACK_TO_SENDER", "办理人 [{}] 在当前任务节点 [{}] 将办件 [{}] 退回上一个任务节点 [{}] 、发送人为 [{}]"),
    BUTTON_ROLLBACK_TO_STARTOR("BUTTON_ROLLBACK_TO_STARTOR", "办理人 [{}] 在任务节点 [{}] 将办件 [{}] 退回至流程启动节点 [{}] 、发起人 [{}]"),
    BUTTON_BATCH_ROLLBACK("BUTTON_BATCH_ROLLBACK", "批量退回拟稿人 [{}]"),
    BUTTON_SAVE_CUSTOM_PROCESS("BUTTON_SAVE_CUSTOM_PROCESS", "保存流程定制信息 [{}]"),
    BUTTON_SEND_TO_SENDER("BUTTON_SEND_TO_SENDER", "办理人 [{}] 在任务节点 [{}] 将办件 [{}] 返回任务至上一个任务节点 [{}]、发送人 [{}]"),
    BUTTON_SEND_TO_STARTOR("BUTTON_SEND_TO_STARTOR", " 办理人[{}] 在任务节点 [{}] 将办件 [{}] 发送至流程启动节点 [{}]、发起人 [{}]"),
    BUTTON_SPECIAL_COMPLETE("BUTTON_SPECIAL_COMPLETE", "办件 [{}] 被办理人 [{}] 在任务节点 [{}] 特殊办结"),
    BUTTON_TAKEBACK("BUTTON_TAKEBACK", "办件 [{}] 被办理人 [{}] 在任务节点 [{}] 收回"),
    BUTTON_TAKEBACK_TASK_DEF_KEY("BUTTON_TAKEBACK_TASK_DEF_KEY", "办理人 [{}] 在当前任务节点 [{}] 收回指定任务节点 [{}] 的办件 [{}]"),
    BUTTON_UNCLAIM("BUTTON_UNCLAIM", "办理人 [{}] 在任务节点 [{}] 撤销签收办件 [{}]"),
    BUTTON_DELETE_TODOS("BUTTON_DELETE_TODOS", "操作人 [{}] 删除待办 [{}] 放入回收站"),
    BUTTON_RECOVERS("BUTTON_RECOVERS", "操作人 [{}] 从回收站恢复待办 [{}]"),
    BUTTON_REMOVES("BUTTON_REMOVES", "操作人 [{}] 从回收站彻底删除待办 [{}]"),

    // ========== 抄送操作 ==========
    CHAOSONG_CHANGE_STATE("CHAOSONG_CHANGE_STATE", "改变抄送件意见状态 [{}]"),
    CHAOSONG_CHANGE_STATUS("CHAOSONG_CHANGE_STATUS", "批量设置抄送状态为已阅 [{}]"),
    CHAOSONG_DELETE("CHAOSONG_DELETE", "批量删除抄送件 [{}]"),
    CHAOSONG_SAVE("CHAOSONG_SAVE", "保存抄送信息 [{}]"),
    CHAOSONG_SEARCH("CHAOSONG_SEARCH", "搜索抄送列表 [{}]"),

    // ========== 自定义视图操作 ==========
    CUSTOM_VIEW_DELETE("CUSTOM_VIEW_DELETE", "删除自定义视图 [{}]"),
    CUSTOM_VIEW_SAVE("CUSTOM_VIEW_SAVE", "保存视图信息 [{}]"),

    // ========== 委托操作 ==========
    ENTRUST_DELETE("ENTRUST_DELETE", "删除委托信息 [{}]"),
    ENTRUST_SAVE("ENTRUST_SAVE", "保存委托数据 [{}]"),

    // ========== 加减签操作 ==========
    MULTI_INSTANCE_PARALLEL_ADD("MULTI_INSTANCE_PARALLEL_ADD", "办件 [{}] 中操作人 [{}] 在任务 [{}] 并行加签 [{}]"),
    MULTI_INSTANCE_SEQUENTIAL_ADD("MULTI_INSTANCE_SEQUENTIAL_ADD", "办件 [{}] 中操作人 [{}] 在任务 [{}] 串行加签 [{}]"),
    MULTI_INSTANCE_PARALLEL_REMOVE("MULTI_INSTANCE_PARALLEL_REMOVE", "办件 [{}] 中操作人 [{}] 在任务 [{}] 并行减签 [{}]"),
    MULTI_INSTANCE_SEQUENTIAL_REMOVE("MULTI_INSTANCE_SEQUENTIAL_REMOVE", "办件 [{}] 中操作人 [{}] 在任务 [{}] 串行减签 [{}]"),
    MULTI_INSTANCE_SET_SPONSOR("MULTI_INSTANCE_SET_SPONSOR", "设置主办人 [{}]"),

    // ========== 关注操作 ==========
    OFFICE_FOLLOW_DELETE("OFFICE_FOLLOW_DELETE", "取消关注 [{}]"),
    OFFICE_FOLLOW_SAVE("OFFICE_FOLLOW_SAVE", "保存关注的办件信息 [{}]"),

    // ========== 流程跟踪操作 ==========
    PROCESS_TRACK_VIEW("PROCESS_TRACK_VIEW", "查看电子历程 [{}]"),

    // ========== 快捷发送操作 ==========
    QUICK_SEND_SAVE("QUICK_SEND_SAVE", "保存快捷发送人信息 [{}]"),

    // ========== 催办操作 ==========
    REMINDER_DELETE("REMINDER_DELETE", "删除催办信息 [{}]"),
    REMINDER_SAVE("REMINDER_SAVE", "保存催办信息 [{}]"),
    REMINDER_SET_READ_TIME("REMINDER_SET_READ_TIME", "批量设置催办阅读时间 [{}]"),
    REMINDER_UPDATE("REMINDER_UPDATE", "更新催办信息 [{}]"),

    // ========== 消息提醒操作 ==========
    REMIND_INSTANCE_SAVE("REMIND_INSTANCE_SAVE", "保存消息提醒数据 [{}]"),

    // ========== 会签信息操作 ==========
    SIGN_DEPT_DELETE("SIGN_DEPT_DELETE", "删除会签信息 [{}]"),
    SIGN_DEPT_GET("SIGN_DEPT_GET", "获取会签信息 [{}]"),
    SIGN_DEPT_GET_TREE("SIGN_DEPT_GET_TREE", "获取委外会签部门树 [{}]"),
    SIGN_DEPT_SAVE("SIGN_DEPT_SAVE", "保存会签部门 [{}]"),
    SIGN_DEPT_SAVE_SIGN("SIGN_DEPT_SAVE_SIGN", "保存会签签名 [{}]"),
    SIGN_DEPT_UPDATE("SIGN_DEPT_UPDATE", "插入或更新会签部门，更新显示名称 [{}]"),

    // ========== 会签详情操作 ==========
    SIGN_DEPT_DETAIL_DELETE("SIGN_DEPT_DETAIL_DELETE", "删除会签信息 [{}]"),
    SIGN_DEPT_DETAIL_RECOVER("SIGN_DEPT_DETAIL_RECOVER", "恢复会签信息 [{}]"),
    SIGN_DEPT_DETAIL_SAVE("SIGN_DEPT_DETAIL_SAVE", "保存会签信息 [{}]"),

    // ========== 沟通交流操作 ==========
    SPEAK_INFO_DELETE("SPEAK_INFO_DELETE", "删除沟通交流信息 [{}]"),
    SPEAK_INFO_SAVE("SPEAK_INFO_SAVE", "保存沟通交流信息 [{}]"),

    // ========== 催办信息操作 ==========
    URGE_INFO_DELETE("URGE_INFO_DELETE", "删除催办信息 [{}]"),
    URGE_INFO_SAVE("URGE_INFO_SAVE", "保存催办信息 [{}]"),

    // ========== 正文操作 ==========
    WORD_DOWNLOAD_HISTORY("WORD_DOWNLOAD_HISTORY", "下载历史版本正文 [{}]"),
    WORD_DOWNLOAD("WORD_DOWNLOAD", "下载正文 [{}]"),
    WORD_DOWNLOAD_CS("WORD_DOWNLOAD_CS", "下载正文（抄送件）[{}]"),
    WORD_CREATE_TEMPLATE("WORD_CREATE_TEMPLATE", "新建正文空白模板 [{}]"),
    WORD_TO_PDF("WORD_TO_PDF", "转PDF [{}]"),
    WORD_UPLOAD("WORD_UPLOAD", "办件 [{}] 上传正文 "),
    WORD_SAVE("WORD_SAVE", "保存正文 [{}]"),

    // wps 操作
    WPS_SAVE("WPS_SAVE", "保存 WPS 文档 [{}]"),
    WPS_UPLOAD("WPS_UPLOAD", "办件 [{}] 上传正文 "),
    WPS_UPLOAD_MOBILE("WPS_UPLOAD_MOBILE", "手机端办件 [{}] 上传正文 "),
    WPS_DOWNLOAD("WPS_DOWNLOAD", "下载 WPS 文档 [{}]"),
    WPS_DOWNLOAD_MOBILE("WPS_DOWNLOAD_MOBILE", "手机端下载 WPS 文档 [{}]"),
    WPS_REVOKE_MOBILE("WPS_REVOKE_MOBILE", "手机端撤销红头 WPS文档 [{}]"),

    // ========== 草稿操作 ==========
    DRAFT_DELETE("DRAFT_DELETE", "彻底删除草稿 [{}]"),
    DRAFT_RECOVER("DRAFT_RECOVER", "还原草稿 [{}]"),
    DRAFT_BATCH_DELETE("DRAFT_BATCH_DELETE", "批量删除草稿 [{}]"),
    DRAFT_SAVE("DRAFT_SAVE", "保存草稿信息 [{}]"),

    // ========== 监控操作 ==========
    MONITOR_BATCH_DELETE("MONITOR_BATCH_DELETE", "批量彻底删除流程实例 [{}]"),

    // ========== 办件操作 ==========
    DOCUMENT_COMPLETE("DOCUMENT_COMPLETE", "流程办结 [{}]"),
    DOCUMENT_COPY2TODO("DOCUMENT_COPY2TODO", "复制并起草 [{}]"),
    DOCUMENT_SEND("DOCUMENT_SEND", "办件发送 [{}]"),
    DOCUMENT_BATCH_SEND("DOCUMENT_BATCH_SEND", "批量发送 [{}]"),
    DOCUMENT_CLAIM("DOCUMENT_CLAIM", "[{}] 签收任务 [{}]"),
    DOCUMENT_RESUMETODO("DOCUMENT_RESUMETODO", "恢复待办 [{}]"),

    // ========== 导出操作 ==========
    EXPORT_FORM("EXPORT_FORM", "导出表单 [{}]"),
    EXPORT_WORD("EXPORT_WORD", "导出正文 [{}]"),
    EXPORT_ATTACHMENT("EXPORT_ATTACHMENT", "导出附件 [{}]");

    private final String action;
    private final String description;
}
