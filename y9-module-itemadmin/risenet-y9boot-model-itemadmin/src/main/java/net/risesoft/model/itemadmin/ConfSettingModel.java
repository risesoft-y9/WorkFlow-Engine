package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 系统配置
 *
 * @author qinman
 * @date 2025/08/28
 */
@Data
public class ConfSettingModel implements Serializable {

    private static final long serialVersionUID = -6731490905478236345L;
    /** 从统一待办打开办件的前缀地址 */
    private String todoTaskUrlPrefix;

    /** 意见排序方式，设置为true,则按照岗位的orderedPath排序 */
    private boolean opinionOrderBy;
}
