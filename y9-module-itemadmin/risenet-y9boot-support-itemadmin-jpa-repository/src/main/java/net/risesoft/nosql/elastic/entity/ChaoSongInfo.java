package net.risesoft.nosql.elastic.entity;

import java.io.Serializable;

import javax.persistence.Id;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Document(indexName = "y9_chaosonginfo")
@NoArgsConstructor
@Data
public class ChaoSongInfo implements Serializable {

    private static final long serialVersionUID = -4235779237483037821L;

    /**
     * 主键
     */
    @Id
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String id;

    /**
     * 租户Id
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String tenantId;

    /**
     * 抄送的标题
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String title;

    /**
     * 抄送节点的任务Id
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String taskId;

    /**
     * 抄送的流程实例
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String processInstanceId;

    /**
     * 抄送的流程编号
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String processSerialNumber;

    /**
     * 抄送目标人员名称
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String userName;

    /**
     * 抄送目标人员Id
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String userId;

    /**
     * 抄送目标人员部门名称
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String userDeptName;

    /**
     * 抄送目标人员部门Id
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String userDeptId;

    /**
     * 操作人的名称
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String senderName;

    /**
     * 操作人的Id
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String senderId;

    /**
     * 操作人员部门名称
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String sendDeptName;

    /**
     * 操作人员部门Id
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String sendDeptId;

    /**
     * 传阅的状态,1已阅,2未阅
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private Integer status = 2;

    /**
     * 抄送时间
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String createTime;

    /**
     * 阅读时间
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String readTime;

    /**
     * 事项id
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String itemId;

    /**
     * 事项名称
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String itemName;

    /**
     * 是否填写意见，是为1
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String opinionState;

}
