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
@Document(indexName = "y9_office_doneinfo")
@NoArgsConstructor
@Data
public class OfficeDoneInfo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 5418957558064846446L;

    /**
     * 主键
     */
    @Id
    @Field(type = FieldType.Keyword)
    private String id;

    /**
     * 租户id
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String tenantId;

    /**
     * 流程实例id
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String processInstanceId;

    /**
     * 流程定义id
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String processDefinitionId;

    /**
     * 流程定义key
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String processDefinitionKey;

    /**
     * 流程编号
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String processSerialNumber;

    /**
     * 系统英文名称
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String systemName;

    /**
     * 系统中文名称
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String systemCnName;

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
     * 标题
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String title;

    /**
     * 文号
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String docNumber;

    /**
     * 紧急程度
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String urgency;

    /**
     * 创建人Id
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String creatUserId;

    /**
     * 创建人姓名
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String creatUserName;

    /**
     * 承办人Id
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String allUserId;

    /**
     * 委托人Id，用于委托办结件查询
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String entrustUserId;

    /**
     * 科室id
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String deptId;

    /**
     * 部门名称
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String deptName;

    /**
     * 委办局id
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String bureauId;

    /**
     * 创建时间
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String startTime;

    /**
     * 办结时间
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String endTime;

    /**
     * 办结人
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String userComplete;

    /**
     * 是否上会，1为上会,ddyjs使用
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String meeting = "0";

    /**
     * 会议类型，党组会，办公会，专题会,当代研究所使用
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String meetingType;

    /**
     * 目标，xxx使用
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String target = "";

}
