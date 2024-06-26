package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 表单字段模型
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class FormFieldDefineModel implements Serializable {

    private static final long serialVersionUID = -289965118765346868L;
    /**
     * 字段类型
     */
    private String formCtrltype;
    /**
     * 字段中文名称
     */
    private String disChinaName;

    /**
     * 字段名称
     */
    private String formCtrlName;

    /**
     * 表列名
     */
    private String columnName;

}