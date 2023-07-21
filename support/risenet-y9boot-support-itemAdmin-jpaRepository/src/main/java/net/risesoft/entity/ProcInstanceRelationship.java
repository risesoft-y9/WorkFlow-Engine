package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
@org.hibernate.annotations.Table(comment = "流程实例关系表", appliesTo = "ff_procInstanceRelationship")
@Table(name = "ff_procInstanceRelationship")
public class ProcInstanceRelationship implements Serializable {

    private static final long serialVersionUID = 2191419695991175733L;

    /**
     * 子流程实例Id，由于每个父流程调用子流程时可以调用多个子流程，而每个子流程只会有一个父流程，所以这里以子流程实例的Id作为主键
     */
    @Id
    @org.hibernate.annotations.Comment("子流程实例Id")
    @Column(name = "PROCINSTANCEID", nullable = false)
    private String procInstanceId;

    @org.hibernate.annotations.Comment("父流程实例Id")
    @Column(name = "PARENTPROCINSTANCEID")
    private String parentProcInstanceId;

    @org.hibernate.annotations.Comment("流程实例Key")
    @Column(name = "PROCDEFINITIONKEY")
    private String procDefinitionKey;

}
