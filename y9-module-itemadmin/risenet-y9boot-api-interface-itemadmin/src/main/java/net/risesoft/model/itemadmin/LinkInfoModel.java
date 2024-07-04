package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 链接信息
 *
 * @author zhangchongjie
 * @date 2024/05/14
 */
@Data
public class LinkInfoModel implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -5083430850698025796L;

    /**
     * 主键
     */
    private String id;

    /**
     * 链接名称
     */
    private String linkName;

    /**
     * 链接地址
     */
    private String linkUrl;

    /**
     * 创建时间
     */
    private String createTime;

}
