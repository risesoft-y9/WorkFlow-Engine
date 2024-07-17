package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 编号标识对应的机关代字
 *
 * @author mengjuhua
 * @date 2024/06/28
 */
@Data
public class OrganWordPropertyModel implements Serializable {

    private static final long serialVersionUID = 4349939764066050006L;

    /** 唯一标识 */
    private String id;

    /** 机关代字名字 */
    private String name;

    /** 机关代字类型 */
    private String custom;

    /** 初始值 */
    private Integer initNumber;

    private Boolean hasPermission;

}
