package net.risesoft.controller.vo;

import java.io.Serializable;

import lombok.Data;

/**
 * 模型信息
 *
 * @author mengjuhua
 * @author qinman
 * @author zhangchongjie
 * @date 2024/07/22
 */
@Data
public class ModelVO implements Serializable, Comparable<ModelVO> {

    private static final long serialVersionUID = 5923978695683091592L;
    /** 主键id */
    private String id;
    /** key */
    private String key;
    /** 名称 */
    private String name;
    /** 版本 */
    private int version;
    /** 创建时间 */
    private String createTime;
    /***/
    private long sortTime;
    /** 最后一次修改时间 */
    private String lastUpdateTime;

    @Override
    public int compareTo(ModelVO o) {
        long startTime1 = this.getSortTime();
        long startTime2 = o.getSortTime();
        return Long.compare(startTime2, startTime1);
    }
}
