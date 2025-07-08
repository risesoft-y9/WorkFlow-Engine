package net.risesoft.entity.view;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@NoArgsConstructor
@Accessors(chain = true)
@Data
@Entity
@Table(name = "FF_CUSTOM_VIEW")
@org.hibernate.annotations.Table(comment = "自定义视图表", appliesTo = "FF_CUSTOM_VIEW")
public class CustomView implements Serializable {

    private static final long serialVersionUID = 2682317166903809753L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 38, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("事项Id")
    @Column(name = "ITEMID", length = 50, nullable = false)
    private String itemId;

    @Comment("视图类型")
    @Column(name = "VIEWTYPE", length = 20, nullable = false)
    private String viewType;

    @Comment("字段id")
    @Column(name = "FIELDID", length = 50)
    private String fieldId;

    @Comment("表单id")
    @Column(name = "FORMID", length = 50)
    private String formId;

    @Comment("字段名称")
    @Column(name = "FIELDNAME", length = 100)
    private String fieldName;

    @Comment("序号")
    @Column(name = "TABINDEX", length = 10)
    private Integer tabIndex;

    @Comment("人员id")
    @Column(name = "USERID", length = 50)
    private String userId;

    @Comment("人员名称")
    @Column(name = "USERNAME", length = 50)
    private String userName;

    @Comment("生成时间")
    @Column(name = "CREATETIME", length = 50)
    private String createTime;
}
