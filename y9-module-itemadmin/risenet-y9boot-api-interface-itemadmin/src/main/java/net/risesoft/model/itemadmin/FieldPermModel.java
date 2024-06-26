package net.risesoft.model.itemadmin;

import java.io.Serializable;


import lombok.Data;

/**
 * 字段权限模型
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class FieldPermModel implements Serializable {

    private static final long serialVersionUID = -289965118765346868L;
    /**
     * 字段名称
     */
    private String fieldName;
    /**
     * 写权限
     */
    private boolean writePerm;

}