package net.risesoft.entity;

import lombok.Data;

import java.util.Date;

/**
 * 关联文件表
 */
@Data
public class CXLink {
    private String linkId;
    private String linkType;
    private String name;
    private String title;
    private String departmentName;
    private String createrName;
    private String created;
    private String activityInstanceId;
    private String linkUrl;
    private ProcessInstance from;
    private ProcessInstance to;
    private String type;//0是老件 ，1是新件
    private String processInstanceId;//processInstanceId
}
