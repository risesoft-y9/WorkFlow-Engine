package net.risesoft.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;

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
@Table(name = "FF_ENTRUSTDETAIL")
@Comment("出差委托表")
public class EntrustDetail implements Serializable {
    private static final long serialVersionUID = 3836935260567480966L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 38)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("委托人Id")
    @Column(name = "OWNERID", length = 100, nullable = false)
    private String ownerId;

    @Comment("任务Id")
    @Column(name = "TASKID", length = 50, nullable = false)
    private String taskId;

    @Comment("流程实例Id")
    @Column(name = "PROCESSINSTANCEID", length = 50, nullable = false)
    private String processInstanceId;

}
