package net.risesoft.model.processadmin;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 执行模型
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
@AllArgsConstructor
public class LinkNodeModel implements Serializable {

    private static final long serialVersionUID = 6334448084011473247L;
    /**
     * 起点
     */
    private String from;
    /**
     * 终点
     */
    private String to;
}
