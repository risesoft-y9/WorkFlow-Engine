package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

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
@org.hibernate.annotations.Table(comment = "模板类型表", appliesTo = "FF_TaoHongTemplateType")
public class TaoHongTemplateType implements Serializable {
    private static final long serialVersionUID = 2748469136288159842L;

    @Id
    @org.hibernate.annotations.Comment("主键")
    @Column(name = "ID", length = 38)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @org.hibernate.annotations.Comment("委办局Id")
    @Column(name = "BUREAUID", length = 50)
    private String bureauId;

    @org.hibernate.annotations.Comment("类型名称")
    @Column(name = "TYPENAME", length = 100, nullable = false)
    private String typeName;

    @org.hibernate.annotations.Comment("排序号")
    @Column(name = "TABINDEX")
    private Integer tabIndex;

}
