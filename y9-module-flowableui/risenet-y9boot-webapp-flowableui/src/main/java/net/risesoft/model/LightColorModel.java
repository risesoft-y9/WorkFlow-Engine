package net.risesoft.model;

import lombok.Data;

/**
 * @author : qinman
 * @date : 2024-12-05
 **/
@Data
public class LightColorModel {
    /**
     * 灯的颜色
     * 1/2/3/4/5
     */
    private int color;
    /**
     * 图标显示文字
     */
    private String title;
}
