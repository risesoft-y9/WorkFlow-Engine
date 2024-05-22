package net.risesoft.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "FF_LINK_INFO")
@Comment("链接信息表")
public class LinkInfo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 7515297634972602990L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("链接名称")
    @Column(name = "linkName", length = 50)
    private String linkName;

    @Comment("链接url")
    @Column(name = "linkUrl", length = 200)
    private String linkUrl;

    @Comment("创建时间")
    @Column(name = "CREATETIME", length = 50)
    private String createTime;

}
