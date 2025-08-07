package net.risesoft.entity.form;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Entity
@Accessors(chain = true)
@Table(name = "Y9FORM_FIELD")
@org.hibernate.annotations.Table(comment = "表单字段绑定", appliesTo = "Y9FORM_FIELD")
@NoArgsConstructor
@Data
public class Y9FormField implements Serializable {
    private static final long serialVersionUID = -1137482366856338734L;

    @Id
    @Column(name = "ID", length = 38)
    @Comment("主键")
    private String id;

    @Column(name = "FORMID", length = 38)
    @Comment("表单Id")
    private String formId;

    @Column(name = "TABLEID", length = 50)
    @Comment("对应的表id")
    private String tableId;

    @Column(name = "TABLENAME", length = 50)
    @Comment("对应的表名称")
    private String tableName;

    @Column(name = "FIELDNAME", length = 50)
    @Comment("字段名称")
    private String fieldName;

    @Column(name = "FIELDCNNAME", length = 50)
    @Comment("字段中文名称")
    private String fieldCnName;

    @Column(name = "FIELDTYPE", length = 100)
    @Comment("字段类型")
    private String fieldType;

    @Column(name = "QUERYSIGN", length = 2)
    @Comment("开启查询条件")
    private String querySign = "0";

    @Column(name = "QUERYTYPE", length = 50)
    @Comment("查询类型") // 文本框，多选框，单选框，日期
    private String queryType;

    @Column(name = "OPTIONVALUE", length = 500)
    @Comment("选项值") // 多选框，单选框选项值
    private String optionValue;

    // 字段内容作为，title：文件标题，number：文件编号，level：紧急程度
    @Column(name = "CONTENT_USED_FOR", length = 50)
    @Comment("字段内容作为，title：文件标题，number：文件编号，level：紧急程度")
    private String contentUsedFor;

}
