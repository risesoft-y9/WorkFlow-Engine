package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 事项表单类型
 * 
 * @author qinman
 * @date 2022/12/20
 */
@Getter
@AllArgsConstructor
public enum TaskRelatedEnum {

    /** 是否是新文件 */
    NEWTODO("0", "是否是新文件"),
    /** 办文说明 */
    BANWENSHUOMING("1", "办文说明"),
    /** 催办 */
    URGE("2", "催办"),
    /** 多步退回 */
    ROLLBACK("4", "多步退回"),
    /** 红绿灯状态 */
    LIGHTCOLOR("5", "红绿灯状态"),
    /** 条码号状态 */
    TMH("6", "条码号"),
    /** 紧急程度 */
    JJCD("7", "紧急程度"),
    /** 非联网登记 */
    FLWDJ("8", "非联网登记"),
    /** -------------------如果任务的相关信息不在待办列表显示，请设置值大于20-------------------------------------------- **/
    /** 操作名称 */
    ACTIONNAME("20", "操作名称");

    private final String value;
    private final String name;
}
