package net.risesoft.entity.attachment;

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

@NoArgsConstructor
@Accessors(chain = true)
@Data
@Entity
@Table(name = "FF_ATTACHMENT_CONF")
@org.hibernate.annotations.Table(comment = "附件配置表", appliesTo = "FF_ATTACHMENT_CONF")
public class AttachmentConf implements Serializable {

    private static final long serialVersionUID = 6993689468890094985L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 38, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("附件类型")
    @Column(name = "ATTACHMENTTYPE", length = 20, nullable = false)
    private String attachmentType;

    @Comment("字段名称")
    @Column(name = "COLUMNNAME", length = 50, nullable = false)
    private String columnName;

    @Comment("显示名称")
    @Column(name = "DISPLAYNAME", length = 50, nullable = false)
    private String disPlayName;

    @Comment("显示宽度")
    @Column(name = "DISPLAYWIDTH", length = 50, nullable = false)
    private String disPlayWidth;

    @Comment("排列方式")
    @Column(name = "DISPLAYALIGN", length = 10, nullable = false)
    private String disPlayAlign;

    @Comment("配置类型") // 0新增配置 1列表配置
    @Column(name = "CONFIGTYPE", length = 5, nullable = false)
    private Integer configType;

    @Comment("输入框类型") // search-带图标前缀的搜索框,input,select,date
    @Column(name = "INPUTBOXTYPE", length = 20)
    private String inputBoxType;

    @Column(name = "ISREQUIRED")
    @Comment("是否必填")
    private Integer isRequired = 0;// 0非必填 1必填

    @Comment("输入框宽度")
    @Column(name = "SPANWIDTH", length = 50)
    private String spanWidth;

    @Comment("标签名称") // 不填写则使用disPlayName显示名称
    @Column(name = "LABELNAME", length = 20)
    private String labelName;

    @Comment("绑定数据字典") // 输入框类型select时使用
    @Column(name = "OPTIONCLASS", length = 50)
    private String optionClass;

    @Comment("序号")
    @Column(name = "TABINDEX", length = 10)
    private Integer tabIndex;

    @Comment("更新时间")
    @Column(name = "UPDATETIME", length = 50)
    private String updateTime;
}
