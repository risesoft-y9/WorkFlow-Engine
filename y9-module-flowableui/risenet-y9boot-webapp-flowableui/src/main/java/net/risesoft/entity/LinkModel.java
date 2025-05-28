package net.risesoft.entity;

import lombok.Data;

import java.util.Date;

/**
 * 关联文件中间表
 */
@Data
public class LinkModel {

    private String linkid; 	//主键
    private String description; 	//描述
    private String frominstanceid; 	//从流程实例id
    private String name; 	//名称
    private String toinstanceid; 	//到流程实例id
    private String bpmserver; 	//流程服务器
    private Date created; 	//创建时间戳
    private String createrdn; 	//创建者dn
    private String creatername; 	//创建者名称
    private String createruid; 	//创建者id
    private String tenantid; 	//租户id
    private Date updated; 	//更新时间戳
    private String updaterdn; 	//更新者dn
    private String updatername; 	//更新者名称
    private String updateruid; 	//更新者id
    private String linkactivityid; 	//关联活动id
    private String linktype; 	//关联类型

//    private ProcessInstance from;
//    private ProcessInstance to;
//    private String url;
}
