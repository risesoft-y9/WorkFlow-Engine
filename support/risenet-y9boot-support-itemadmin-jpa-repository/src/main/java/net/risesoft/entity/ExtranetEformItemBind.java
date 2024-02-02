package net.risesoft.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;

import org.hibernate.annotations.Comment;
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
@Comment("外网电子表单与事项绑定")
public class ExtranetEformItemBind implements Serializable {
    private static final long serialVersionUID = -4555614077556832647L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 38, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("租户Id")
    @Column(name = "TENANTID", length = 50, nullable = false)
    private String tenantId;

    @Comment("表单ID")
    @Column(name = "FORMID", length = 38, nullable = false)
    private String formId;

    @Comment("表单名称")
    @Column(name = "FORMNAME", length = 100)
    private String formName;

    @Comment("表单Url")
    @Column(name = "FORMURL", length = 100)
    private String formUrl;

    @Comment("事项id")
    @Column(name = "ITEMID", length = 55, nullable = false)
    private String itemId;

    @Comment("事项名称")
    @Column(name = "ITEMNAME", length = 50)
    private String itemName;

    @Comment("排序号")
    @Column(name = "TABINDEX", length = 10)
    private Integer tabIndex;
}
