package net.risesoft.enums;

/**
 * 编号类型
 * 
 * @author yihong
 * @date 2024/07/16
 */
public enum ItemOrganWordEnum {
    DEPTALIASNAMENUMBER("deptAliasNameNumber", "部门简称"), BUREAUAREANUMBER("bureauAreaNumber", "委办局区域代码"),

    PURENUMBER("pureNumber", "纯数字编号"),

    ORGWORDNUMBER("orgWordNumber", "机关代字编号"),
    /**
     * 编号固定字段名称
     */
    FIELDNAME("bianhao", "编号");

    private final String value;
    private final String name;

    ItemOrganWordEnum(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
