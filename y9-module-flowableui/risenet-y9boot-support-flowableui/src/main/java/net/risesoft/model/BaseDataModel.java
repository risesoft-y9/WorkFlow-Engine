package net.risesoft.model;

import java.io.Serializable;
import java.util.Map;

import lombok.Data;

/**
 * 基础数据模型
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class BaseDataModel implements Serializable {

    private static final long serialVersionUID = 4066787055749450784L;

    /**
     * 序号
     */
    private int serialNumber;

    /**
     * 列表类型
     * 
     * @see net.risesoft.enums.ItemBoxTypeEnum
     */
    private String itembox;

    /**
     * 流程编号
     */
    private String processSerialNumber;

    /**
     * 流程实例
     */
    private String processInstanceId;

    /**
     * 流程定义id
     */
    private String processDefinitionId;

    /**
     * 表单数据
     */
    private transient Map<String, Object> formData;
}