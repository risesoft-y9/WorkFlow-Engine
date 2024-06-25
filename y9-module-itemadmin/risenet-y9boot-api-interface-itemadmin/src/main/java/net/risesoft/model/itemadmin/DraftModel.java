package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 草稿模型类
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class DraftModel implements Serializable {

    private static final long serialVersionUID = -2196986462380652871L;

    private String id;//

    /**
     * 事项id
     */
    private String itemId;

    /**
     * 事项名称
     */
    private String itemName;

    /**
     * 实例id
     */
    private String processInstanceId;

    /**
     * 流程定义key
     */
    private String processDefinitionKey;

    /**
     * 流程编号，草稿编号
     */
    private String processSerialNumber;

    /**
     * 编号
     */
    private String docNumber;

    /**
     * 文件标题
     */
    private String title;

    /**
     * 创建人Id
     */
    private String createrId;

    /**
     * 创建人
     */
    private String creater;

    /**
     * 紧急程度
     */
    private String urgency;

    /**
     * 起草时间
     */
    private String draftTime;

    /**
     * 排序号
     */
    private Integer serialNumber;

    /**
     * 类型
     */
    private String type;

}
