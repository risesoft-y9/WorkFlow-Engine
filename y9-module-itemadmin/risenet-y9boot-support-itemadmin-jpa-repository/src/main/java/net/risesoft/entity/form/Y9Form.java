package net.risesoft.entity.form;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Comment;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Entity
@Table(name = "Y9FORM_DEFINE")
@org.hibernate.annotations.Table(comment = "表单定义", appliesTo = "Y9FORM_DEFINE")
@NoArgsConstructor
@Data
public class Y9Form implements Serializable {
    private static final long serialVersionUID = 5697306206116924397L;

    @Id
    @Column(name = "ID", length = 38)
    @Comment("主键")
    private String id;

    @Column(name = "FORMNAME", length = 50)
    @Comment("表单名称")
    private String formName;

    /**
     * {@link #ItemFormTypeEnum}
     */
    @Column(name = "FORMTYPE")
    @Comment("表单类型")
    private Integer formType = 1;

    /**
     * {@link #ItemFormTemplateTypeEnum}
     */
    @Column(name = "TEMPLATETYPE")
    @Comment("表单模板类型")
    private Integer templateType = 1;

    @Column(name = "FILENAME", length = 50)
    @Comment("模板文件名称")
    private String fileName;

    @Lob
    @Column(name = "FILECONTENT")
    @Comment("模板文件内容")
    private byte[] fileContent;

    @Lob
    @Column(name = "FORMJSON")
    @Comment("表单json")
    private String formJson;

    @Lob
    @Column(name = "ORIGINALCONTENT")
    @Comment("原始的页面内容")
    private String originalContent;

    @Lob
    @Column(name = "RESULTCONTENT")
    @Comment("绑定后的页面内容")
    private String resultContent;

    @Column(name = "SYSTEMNAME", length = 50)
    @Comment("系统名称")
    private String systemName;

    @Column(name = "SYSTEMCNNAME", length = 50)
    @Comment("系统中文名称")
    private String systemCnName;

    @Column(name = "TENANTID", length = 38)
    @Comment("租户Id")
    private String tenantId;

    @Column(name = "PERSONID", length = 38)
    @Comment("修改人")
    private String personId;

    @Column(name = "CSSURL", length = 255)
    @Comment("引用css")
    private String cssUrl;

    @Column(name = "JSURL", length = 255)
    @Comment("引用js")
    private String jsUrl;

    @Column(name = "INITDATAURL", length = 255)
    @Comment("初始化数据url路径")
    private String initDataUrl;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATETIME")
    @Comment("修改时间")
    private Date updateTime;

}
