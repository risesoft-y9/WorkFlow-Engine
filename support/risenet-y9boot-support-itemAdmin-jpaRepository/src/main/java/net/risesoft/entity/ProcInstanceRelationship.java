package net.risesoft.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.Comment;

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
@Comment("流程实例关系表")
@Table(name = "ff_procInstanceRelationship")
public class ProcInstanceRelationship implements Serializable {

    private static final long serialVersionUID = 2191419695991175733L;

    /**
     * 子流程实例Id，由于每个父流程调用子流程时可以调用多个子流程，而每个子流程只会有一个父流程，所以这里以子流程实例的Id作为主键
     */
    @Id
    @Comment("子流程实例Id")
    @Column(name = "PROCINSTANCEID", nullable = false)
    private String procInstanceId;

    @Comment("父流程实例Id")
    @Column(name = "PARENTPROCINSTANCEID")
    private String parentProcInstanceId;

    @Comment("流程实例Key")
    @Column(name = "PROCDEFINITIONKEY")
    private String procDefinitionKey;

}
