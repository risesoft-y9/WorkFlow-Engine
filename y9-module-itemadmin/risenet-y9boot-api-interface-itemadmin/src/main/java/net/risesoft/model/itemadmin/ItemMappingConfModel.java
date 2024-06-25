package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 事项映射配置
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class ItemMappingConfModel implements Serializable {

    private static final long serialVersionUID = -2211904374246635797L;

    /**
     * 主键
     */
    private String id;
    /**
     * 事项Id
     */
    private String itemId;
    /**
     * 对接系统类型 1为内部系统，2为外部系统
     */
    private String sysType = "2";
    /**
     * 表名称
     */
    private String tableName;
    /**
     * 列名称
     */
    private String columnName;
    /**
     * 映射系统标识 内部系统为事项id，外部系统为自定义标识
     */
    private String mappingId;
    /**
     * 映射表名称，sysType为1时使用
     */
    private String mappingTableName;
    /**
     * 映射字段名
     */
    private String mappingName;
    /**
     * 生成时间
     */
    private String createTime;

}