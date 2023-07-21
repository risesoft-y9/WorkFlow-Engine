package net.risesoft.util;

import java.io.Serializable;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public class BasicButton implements Comparable<BasicButton>, Serializable {

    private static final long serialVersionUID = -469195911093056967L;

    /**
     * 基本按钮
     */
    public static Integer BASIC = 1;

    /**
     * 动作按钮
     */
    public static Integer ACTION = 2;

    /**
     * 路由按钮
     */
    public static Integer ROUTE = 3;

    /**
     * 并行网关按钮
     */
    public static Integer PARALLELGATEWAY = 4;

    private String id;

    private String name;

    private String icon;

    private String customId;

    private Integer type;

    private Boolean show;

    public BasicButton() {
        super();
    }

    public BasicButton(String id, String name, Integer type, Boolean show) {
        super();
        this.id = id;
        this.name = name;
        this.type = type;
        this.show = show;
    }

    public BasicButton(String id, String name, String icon, Integer type, Boolean show) {
        super();
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.type = type;
        this.show = show;
    }

    @Override
    public int compareTo(BasicButton o) {
        Integer thisId = Integer.valueOf(this.id);
        Integer oId = Integer.valueOf(o.id);
        if (thisId >= oId) {
            return 1;
        }
        return -1;
    }

    public String getCustomId() {
        return customId;
    }

    public String getIcon() {
        return icon;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Boolean getShow() {
        return show;
    }

    public Integer getType() {
        return type;
    }

    public void setCustomId(String customId) {
        this.customId = customId;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setShow(Boolean show) {
        this.show = show;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
