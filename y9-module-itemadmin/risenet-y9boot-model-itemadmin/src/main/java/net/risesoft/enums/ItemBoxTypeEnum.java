package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 办件列表类型
 * 
 * @author qinman
 * @date 2022/12/20
 */
@Getter
@AllArgsConstructor
public enum ItemBoxTypeEnum {
    /** 起草 */
    ADD("add", "起草"),
    /** 草稿 */
    DRAFT("draft", "草稿"),
    /** 待办 */
    TODO("todo", "待办"),
    /** 在办 */
    DOING("doing", "在办"),
    /** 在办 */
    HAVEDONE("haveDone", "已办"),
    /** 办结 */
    DONE("done", "办结"),
    /** 办结 */
    ALL("all", "所有件"),
    /** 回收站 */
    RECYCLE("recycle", "回收站"),
    /** 回收站 */
    COPY("copy", "传签件"),
    /** 监控在办 */
    MONITOR_DOING("monitorDoing", "监控在办"),
    /** 监控办结 */
    MONITOR_DONE("monitorDone", "监控办结-所有"),
    /** 监控办结-科室 */
    MONITOR_DONE_DEPT("monitorDoneDept", "监控办结-科室"),
    /** 监控办结-委办局 */
    MONITOR_DONE_BUREAU("monitorDoneBureau", "监控办结-委办局"),
    /** 监控回收站 */
    MONITOR_RECYCLE("monitorRecycle", "监控回收站"),
    /** 阅件 */
    YUEJIAN("yuejian", "阅件");

    private final String value;
    private final String name;

    public static ItemBoxTypeEnum fromString(String itemBox) {
        if (itemBox == null) {
            throw new IllegalArgumentException("itemBox不能为空");
        }
        for (ItemBoxTypeEnum itemBoxTypeEnum : ItemBoxTypeEnum.values()) {
            if (itemBoxTypeEnum.value.equalsIgnoreCase(itemBox)) {
                return itemBoxTypeEnum;
            }
        }
        throw new IllegalArgumentException("未知的itemBox: " + itemBox);
    }
}
