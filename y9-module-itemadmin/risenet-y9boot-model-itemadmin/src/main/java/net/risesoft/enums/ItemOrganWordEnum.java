package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 编号类型
 * 
 * @author yihong
 * @date 2024/07/16
 */
@Getter
@AllArgsConstructor
public enum ItemOrganWordEnum {
    DEPT_ALIAS_NAME_NUMBER("deptAliasNameNumber", "部门简称"),

    BUREAU_AREA_NUMBER("bureauAreaNumber", "委办局区域代码"),

    PURE_NUMBER("pureNumber", "纯数字编号"),

    ORG_WORD_NUMBER("orgWordNumber", "机关代字编号"),

    FIELD_NAME("bianhao", "编号");

    private final String value;
    private final String name;
}
