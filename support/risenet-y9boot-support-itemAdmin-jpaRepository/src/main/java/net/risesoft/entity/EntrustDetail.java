package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

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
@Table(name = "FF_ENTRUSTDETAIL")
@org.hibernate.annotations.Table(comment = "出差委托表", appliesTo = "FF_ENTRUSTDETAIL")
public class EntrustDetail implements Serializable {
    private static final long serialVersionUID = 3836935260567480966L;

    @Id
    @org.hibernate.annotations.Comment("主键")
    @Column(name = "ID", length = 38)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @org.hibernate.annotations.Comment("委托人Id")
    @Column(name = "OWNERID", length = 100, nullable = false)
    private String ownerId;

    @org.hibernate.annotations.Comment("任务Id")
    @Column(name = "TASKID", length = 50, nullable = false)
    private String taskId;

    @org.hibernate.annotations.Comment("流程实例Id")
    @Column(name = "PROCESSINSTANCEID", length = 50, nullable = false)
    private String processInstanceId;

}
