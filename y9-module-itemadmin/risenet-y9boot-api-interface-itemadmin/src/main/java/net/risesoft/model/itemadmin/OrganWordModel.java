package net.risesoft.model.itemadmin;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * 编号标识
 *
 * @author mengjuhua
 * @date 2024/06/28
 */
@Data
public class OrganWordModel implements Serializable {

    private static final long serialVersionUID = 3417520501659712353L;

    /** 唯一标识 */
    private String id;

    /** 编号标识标志 */
    private String custom;

    /** 编号标识名字 */
    private String name;

    /** 编号标识 */
    private String numberString;

    private Boolean exist;

    private Boolean hasPermission;

    private List<OrganWordPropertyModel> list;

}
