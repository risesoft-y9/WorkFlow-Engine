package net.risesoft.log;

import lombok.Getter;

/**
 * 办件操作类型枚举
 *
 * @author qinman
 * @date 2025/05/20
 */
@Getter
public enum FlowableOperationTypeEnum {
    /** 查看 */
    BROWSE("查看"),
    /** 增加 */
    ADD("增加"),
    /** 起草 */
    DRAFT("起草"),
    /** 保存 */
    SAVE("保存"),
    /** 保存表单 */
    SAVE_FORM("保存表单"),
    /** 删除 */
    DELETE("删除"),
    /** 上传 */
    UPLOAD("上传"),
    /** 下载 */
    DOWNLOAD("下载"),
    /** 排序 */
    ORDER("排序"),
    /** 检查 */
    CHECK("检查"),
    /** 导出 */
    EXPORT("导出"),
    /** 发送 */
    SEND("发送"),
    /** 签收 */
    CLAIM("签收"),
    /** 退签 */
    UN_CLAIM("退签"),
    /** 办结 */
    COMPLETE("办结"),
    /** 完成任务 */
    COMPLETE_TASK("完成任务"),
    /** 恢复 */
    RESUME("恢复"),
    /** 取消 */
    CANCEL("取消"),
    /** 退回 */
    ROLLBACK("退回"),
    /** 收回 */
    TAKE_BACK("收回");

    private final String value;

    FlowableOperationTypeEnum(String value) {
        this.value = value;
    }

}
