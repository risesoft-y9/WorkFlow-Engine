package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@NoArgsConstructor
@Data
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Entity
@Table(name = "FF_ExtranetEformItemBind")
@org.hibernate.annotations.Table(comment = "外网电子表单与事项绑定", appliesTo = "FF_ExtranetEformItemBind")
public class ExtranetEformItemBind implements Serializable {
    private static final long serialVersionUID = -4555614077556832647L;

    @Id
    @org.hibernate.annotations.Comment("主键")
    @Column(name = "ID", length = 38, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @org.hibernate.annotations.Comment("租户Id")
    @Column(name = "TENANTID", length = 50, nullable = false)
    private String tenantId;

    @org.hibernate.annotations.Comment("表单ID")
    @Column(name = "FORMID", length = 38, nullable = false)
    private String formId;

    @org.hibernate.annotations.Comment("表单名称")
    @Column(name = "FORMNAME", length = 100)
    private String formName;

    @org.hibernate.annotations.Comment("表单Url")
    @Column(name = "FORMURL", length = 100)
    private String formUrl;

    @org.hibernate.annotations.Comment("事项id")
    @Column(name = "ITEMID", length = 55, nullable = false)
    private String itemId;

    @org.hibernate.annotations.Comment("事项名称")
    @Column(name = "ITEMNAME", length = 50)
    private String itemName;

    @org.hibernate.annotations.Comment("排序号")
    @Column(name = "TABINDEX", length = 10)
    private Integer tabIndex;
}
