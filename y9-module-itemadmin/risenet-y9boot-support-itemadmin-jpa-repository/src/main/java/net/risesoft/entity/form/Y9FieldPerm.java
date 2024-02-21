package net.risesoft.entity.form;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Entity
@Table(name = "Y9FORM_FIELD_PERM")
@org.hibernate.annotations.Table(comment = "字段权限配置", appliesTo = "Y9FORM_FIELD_PERM")
@NoArgsConstructor
@Data
public class Y9FieldPerm implements Serializable {
    private static final long serialVersionUID = -1137482366856338734L;

    @Id
    @Column(name = "ID", length = 38)
    @Comment("主键")
    private String id;

    @Column(name = "FORMID", length = 38)
    @Comment("表单Id")
    private String formId;

    @Column(name = "FIELDNAME", length = 100)
    @Comment("字段名称")
    private String fieldName;

    @Column(name = "WRITEROLEID", length = 200)
    @Comment("写权限角色id")
    private String writeRoleId;

    @Column(name = "WRITEROLENAME", length = 200)
    @Comment("写权限角色名称")
    private String writeRoleName;

    @Column(name = "PROCESSDEFINITIONID", length = 200)
    @Comment("流程定义id")
    private String processDefinitionId;

    @Column(name = "TASKDEFKEY", length = 200)
    @Comment("任务key")
    private String taskDefKey;

}
