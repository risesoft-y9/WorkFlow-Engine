package net.risesoft.entity.organword;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Comment;
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
@Table(name = "FF_ITEM_ORGANWORD")
@org.hibernate.annotations.Table(comment = "编号绑定事项和流程节点及角色", appliesTo = "FF_ITEM_ORGANWORD")
public class ItemOrganWordBind implements Serializable {

    private static final long serialVersionUID = -8651203798076312089L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 38, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("编号唯一标示")
    @Column(name = "ORGANWORDCUSTOM", length = 50, nullable = false)
    private String organWordCustom;

    /**
     * 编号名称
     */
    @Transient
    private String organWordName;

    @Comment("事项Id")
    @Column(name = "ITEMID", length = 50, nullable = false)
    private String itemId;

    @Comment("流程定义ID")
    @Column(name = "PROCESSDEFINITIONID", length = 200)
    private String processDefinitionId;

    @Comment("任务key")
    @Column(name = "TASKDEFKEY", length = 100)
    private String taskDefKey;

    /**
     * 角色Id集合
     */
    @Transient
    private List<String> roleIds;

    /**
     * 角色名称
     */
    @Transient
    private String roleNames;

    @Comment("人员名称")
    @Column(name = "USERNAME", length = 50)
    private String userName;

    @Comment("人员id")
    @Column(name = "USERID", length = 50)
    private String userId;

    @Comment("生成时间")
    @Column(name = "CREATEDATE", length = 50)
    private String createDate;

    @Comment("修改时间")
    @Column(name = "MODIFYDATE", length = 50)
    private String modifyDate;
}
