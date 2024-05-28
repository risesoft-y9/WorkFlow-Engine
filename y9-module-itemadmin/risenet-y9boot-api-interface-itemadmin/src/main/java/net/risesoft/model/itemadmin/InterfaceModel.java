package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 接口信息
 *
 * @author zhangchongjie
 * @date 2024/05/27
 */
@Data
public class InterfaceModel implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 6910113392196268569L;

    /**
     * @FieldCommit(value = "主键")
     */
    private String id;

    // 接口名称
    private String interfaceName;

    // 接口地址
    private String interfaceAddress;

    // 请求类型
    private String requestType;

}
