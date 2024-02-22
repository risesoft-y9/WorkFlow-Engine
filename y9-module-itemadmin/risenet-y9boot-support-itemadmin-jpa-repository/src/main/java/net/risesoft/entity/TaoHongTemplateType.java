package net.risesoft.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;

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
@Table(name = "FF_TaoHongTemplateType")
@Comment("模板类型表")
public class TaoHongTemplateType implements Serializable {
    private static final long serialVersionUID = 2748469136288159842L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 38)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("委办局Id")
    @Column(name = "BUREAUID", length = 50)
    private String bureauId;

    @Comment("类型名称")
    @Column(name = "TYPENAME", length = 100, nullable = false)
    private String typeName;

    @Comment("排序号")
    @Column(name = "TABINDEX")
    private Integer tabIndex;

}
