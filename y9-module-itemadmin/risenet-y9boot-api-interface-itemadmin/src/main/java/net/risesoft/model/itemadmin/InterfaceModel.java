package net.risesoft.model.itemadmin;

import lombok.Data;

import java.io.Serializable;

/**
 * 接口信息
 *
 * @author zhangchongjie
 * @date 2024/05/27
 */
@Data
public class InterfaceModel implements Serializable {

    private static final long serialVersionUID = 6910113392196268569L;

    /**
     *  主键
     */
    private String id;

    /**
     * 接口名称
     */
    private String interfaceName;

    /**
     * 接口地址
     */
    private String interfaceAddress;

    /**
     * 请求方式
     */
    private String requestType;

}
