package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
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
@Table(name = "FF_ITEM_VIEWCONF")
@org.hibernate.annotations.Table(comment = "事项视图配置表", appliesTo = "FF_ITEM_VIEWCONF")
public class ItemViewConf implements Serializable {

    private static final long serialVersionUID = 6023418927806462716L;

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

    @Comment("表名称")
    @Column(name = "TABLENAME", length = 50)
    private String tableName;

    @Comment("属性名称")
    @Column(name = "COLUMNNAME", length = 50, nullable = false)
    private String columnName;

    @Comment("显示名称")
    @Column(name = "DISPLAYNAME", length = 50, nullable = false)
    private String disPlayName;

    @Comment("显示宽度")
    @Column(name = "DISPLAYWIDTH", length = 50, nullable = false)
    private String disPlayWidth;

    @Comment("排列")
    @Column(name = "DISPLAYALIGN", length = 10, nullable = false)
    private String disPlayAlign;

    @Comment("是否开启搜索条件") // 绑定数据库表和字段时，可开启搜索条件
    @ColumnDefault("0")
    @Column(name = "OPENSEARCH", length = 5, nullable = false)
    private Integer openSearch = 0;

    @Comment("输入框类型") // search-带图标前缀的搜索框,input,select,date
    @Column(name = "INPUTBOXTYPE", length = 20)
    private String inputBoxType;

    @Comment("搜索框宽度")
    @Column(name = "SPANWIDTH", length = 50)
    private String spanWidth;

    @Comment("搜索名称") // 不填写则使用disPlayName显示名称
    @Column(name = "LABELNAME", length = 20)
    private String labelName;

    @Comment("绑定数据字典") // 输入框类型select时使用
    @Column(name = "OPTIONCLASS", length = 50)
    private String optionClass;

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

    @Comment("更新时间")
    @Column(name = "UPDATETIME", length = 50)
    private String updateTime;
}
