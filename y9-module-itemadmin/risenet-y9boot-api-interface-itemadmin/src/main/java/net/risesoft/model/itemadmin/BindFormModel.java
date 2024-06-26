package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 绑定表单信息
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class BindFormModel implements Serializable {

    private static final long serialVersionUID = 9160696075332868703L;
    /**
     * 表单id
     */
    private String formId;
    /**
     * 表单名称
     */
    private String formName;

}