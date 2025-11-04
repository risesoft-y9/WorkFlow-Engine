package net.risesoft.entity.base;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;

/**
 * 实体基类
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/9/07
 */
@Getter
@MappedSuperclass
public class ItemAdminBaseEntity implements Serializable {

    private static final long serialVersionUID = 5502945940450896394L;

    /**
     * 创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Comment("创建时间")
    @CreationTimestamp
    @Column(name = "CREATETIME", updatable = false)
    protected Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Comment("更新时间")
    @UpdateTimestamp
    @Column(name = "UPDATETIME")
    protected Date updateTime;
}
