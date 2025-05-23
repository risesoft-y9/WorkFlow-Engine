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
    /** 保存 */
    SAVE("保存"),
    /** 增加 */
    ADD("增加"),
    /** 签收 */
    CLAIM("签收"),
    /** 办结 */
    COMPLETE("办结"),
    /** 产生 */
    CREATE("产生"),
    /** 完成任务 */
    COMPLETE_TASK("完成任务"),
    /** 恢复 */
    RESUME("恢复"),
    /** 退签 */
    UN_CLAIM("退签"),
    /** 修改 */
    MODIFY("修改"),
    /** 新增或修改 */
    ADD_MODIFY("新增或修改"),
    /** 删除 */
    DELETE("删除"),
    /** 发送 */
    SEND("发送"),
    /** 活动 */
    EVENT("活动"),
    /** 检查 */
    CHECK("检查"),
    /** 导出 */
    EXPORT("导出"),
    /** 取消 */
    CANCEL("取消"),
    /** 退回 */
    ROLLBACK("退回"),
    /** 收回 */
    TAKE_BACK("收回"),
    /** 上传 */
    UPLOAD("上传"),
    /** 下载 */
    DOWNLOAD("下载"),
    /** 排序 */
    ORDER("排序");

    private final String value;

    FlowableOperationTypeEnum(String value) {
        this.value = value;
    }

}
