package net.risesoft.entity.form;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import org.hibernate.annotations.Comment;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.entity.base.ItemAdminBaseEntity;
import net.risesoft.enums.ItemFormTemplateTypeEnum;
import net.risesoft.enums.ItemFormTypeEnum;
import net.risesoft.persistence.ItemEnumConverter;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Entity
@Table(name = "Y9FORM_DEFINE")
@Comment("表单定义")
@NoArgsConstructor
@Data
public class Y9Form extends ItemAdminBaseEntity implements Serializable {
    private static final long serialVersionUID = 5697306206116924397L;

    @Id
    @Column(name = "ID", length = 38)
    @Comment("主键")
    private String id;

    @Column(name = "FORMNAME", length = 50)
    @Comment("表单名称")
    private String formName;

    @Column(name = "FORMTYPE")
    @Comment("表单类型")
    @Convert(converter = ItemEnumConverter.ItemFormTypeEnumConverter.class)
    private ItemFormTypeEnum formType = ItemFormTypeEnum.MAINFORM;

    @Column(name = "TEMPLATETYPE")
    @Comment("表单模板类型")
    @Convert(converter = ItemEnumConverter.ItemFormTemplateTypeEnumConverter.class)
    private ItemFormTemplateTypeEnum templateType = ItemFormTemplateTypeEnum.HTML;

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

    @Column(name = "CSSURL")
    @Comment("引用css")
    private String cssUrl;

    @Column(name = "JSURL")
    @Comment("引用js")
    private String jsUrl;

    @Column(name = "INITDATAURL")
    @Comment("初始化数据url路径")
    private String initDataUrl;
}
