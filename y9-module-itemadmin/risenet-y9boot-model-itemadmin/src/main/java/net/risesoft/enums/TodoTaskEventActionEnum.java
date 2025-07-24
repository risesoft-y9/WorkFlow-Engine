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
public enum TodoTaskEventActionEnum {
    /** 设置待办已读、未读 */
    SET_NEW_TODO,
    /** 删除任务id对应的任务不存在的统一待办 */
    DELETE_PROCESSINSTANCEID
}
