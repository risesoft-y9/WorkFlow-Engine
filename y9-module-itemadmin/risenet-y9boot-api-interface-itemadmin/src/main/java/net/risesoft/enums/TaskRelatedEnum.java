package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 任务状态相关枚举
 * 
 * @author qinman
 * @date 2022/12/20
 */
@Getter
@AllArgsConstructor
public enum TaskRelatedEnum {

    /** 依据表数据###是否是新文件 */
    NEWTODO("0", "是否是新文件"),
    /** 依据表数据###办文说明 */
    BANWENSHUOMING("1", "办文说明"),
    /** 依据催办表@@@数据#催办 */
    URGE("2", "催办"),
    /** 依据表数据###多步退回 */
    ROLLBACK("4", "多步退回"),
    /** 依据表单字段#红绿灯状态 */
    LIGHTCOLOR("5", "红绿灯状态"),
    /** 依据表单字段#条码号状态 */
    TMH("6", "条码号"),
    /** 依据表单字段#紧急程度 */
    JJCD("7", "紧急程度"),
    /** 依据表单字段#非联网登记 */
    FLWDJ("8", "非联网登记"),
    /** 依据表单字段#发文单#发文文号 */
    FWWH("9", "发文文号"),
    /** 依据表数据###办文说明 */
    FWWH_DELETE("10", "撤销发文文号"),
    /** 依据表数据###办文说明 */
    FU("11", "复"),
    /** 依据表数据###办文说明 */
    YUAN("12", "原"),
    /** -------------------如果任务的相关信息不在待办列表显示，请设置值大于20-------------------------------------------- **/
    /** 操作名称 */
    ACTIONNAME("20", "操作名称"),
    /** 办结类型 */
    COMPLETEDTYPE("21", "办结类型");

    private final String value;
    private final String name;
}
