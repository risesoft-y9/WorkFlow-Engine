package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author zhangchongjie
 * @date 2024/05/24
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "FF_ITEM_INTERFACE_TASK_BIND")
@org.hibernate.annotations.Table(comment = "事项接口任务绑定表", appliesTo = "FF_ITEM_INTERFACE_TASK_BIND")
public class ItemInterfaceTaskBind implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 5824973042664138544L;

    @Comment("主键")
    @Id
    @Column(name = "ID", length = 38, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("事项Id")
    @Column(name = "ITEMID", length = 50, nullable = false)
    private String itemId;

    @Comment("接口id")
    @Column(name = "INTERFACEID", length = 50)
    private String interfaceId;

    @Comment("流程定义Id")
    @Column(name = "PROCESSDEFINITIONID", length = 255)
    private String processDefinitionId;

    @Comment("任务key")
    @Column(name = "TASKDEFKEY", length = 100)
    private String taskDefKey;

    @Comment("执行条件")
    @Column(name = "EXECUTECONDITION", length = 100)
    private String executeCondition;

    /**
     * 生成时间
     */
    @Comment("生成时间")
    @Column(name = "CREATETIME")
    private String createTime;

    /**
     * 接口名称
     */
    @Transient
    private String interfaceName;

    /**
     * 接口地址
     */
    @Transient
    private String interfaceAddress;

    /**
     * 事项名称
     */
    @Transient
    private String itemName;

    /**
     * 流程定义名称
     */
    @Transient
    private String procDefName;

    /**
     * 任务名称
     */
    @Transient
    private String taskDefName;

}
