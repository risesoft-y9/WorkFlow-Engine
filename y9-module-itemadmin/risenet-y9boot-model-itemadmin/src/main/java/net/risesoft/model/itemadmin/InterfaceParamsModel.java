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
public class InterfaceParamsModel implements Serializable {

    private static final long serialVersionUID = -9071732177340298196L;

    /**
     * 主键
     */
    private String id;
    /**
     * 参数名称
     */
    private String parameterName;
    /**
     * 参数类型
     */
    private String parameterType;

    /**
     * 是否文件
     */
    private String fileType;

    /**
     * 绑定类型
     */
    private String bindType;
    /**
     * 表名称
     */
    private String tableName;

    /**
     * 表类型
     */
    private String tableType;

    /**
     * 表列名
     */
    private String columnName;

}
