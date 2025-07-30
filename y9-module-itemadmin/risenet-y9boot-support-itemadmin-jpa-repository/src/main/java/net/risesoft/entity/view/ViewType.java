package net.risesoft.entity.view;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

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
@Table(name = "FF_VIEWTYPE")
@org.hibernate.annotations.Table(comment = "视图类型表", appliesTo = "FF_VIEWTYPE")
public class ViewType implements Serializable {

    private static final long serialVersionUID = 4808283868156401772L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 38, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("视图类型标识")
    @Column(name = "MARK", length = 50, nullable = false, unique = true)
    private String mark;

    @Comment("视图类型名称")
    @Column(name = "NAME", length = 100, nullable = false)
    private String name;

    /**
     * 事项名称
     */
    @Transient
    private String itemNames;

    @Comment("人员名称")
    @Column(name = "USERNAME", length = 50)
    private String userName;

    @Comment("生成时间")
    @Column(name = "CREATEDATE")
    private String createDate;

    @Comment("修改时间")
    @Column(name = "MODIFYDATE")
    private String modifyDate;
}
