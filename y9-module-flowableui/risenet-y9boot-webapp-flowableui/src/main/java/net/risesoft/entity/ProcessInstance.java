package net.risesoft.entity;

import lombok.Data;

import java.util.Date;

/**
 * 老系统pi表
 */
@Data
public class ProcessInstance {
    private String instanceid; 	//实例id，唯一标识，主键
    private String tenantid; 	//租户id
    private String actionstatus; 	//活动状态
    private Integer archived; 	//是否归档
    private String bpmserver; 	//流程服务器标识
    private String businesskey; 	//业务主键标识
    private Date created; 	//创建时间戳
    private String createrDn; 	//创建者dn
    private String createrName; 	//创建者名称
    private String createrUid; 	//创建者id
    private String debug; 	//调试数据
    private String deleted; 	//逻辑删除
    private String description; 	//描述
    private Date instanceexpiredate; 	//过期时间
    private Date instancereminddate; 	//提醒时间
    private String name; 	//名称
    private String parentid; 	//父节点instanceid
    private Integer priority; 	//优先级
    private String processId; 	//流程id
    private String processName; 	//流程名称
    private Integer processversion; 	//流程版本
    private String routeType; 	//路由类型
    private String running; 	//运行中
    private String state; 	//状态
    private Integer step; 	//步
    private String title; 	//标题
    private Date updated; 	//更新时间戳
    private String updaterDn; 	//更新者dn
    private String updaterName; 	//更新者名称
    private String updaterUid; 	//更新者id
    private Integer year; 	//年
    private String serialnumber; 	//流水号
    private String fromid; 	//表单id（未用）
    private Integer bingan; 	//并案标记
    private Integer slaveindex; 	//子件序号
    private String masterId; 	//主件的instanceid
    private String activityInstanceId; //ai表的值

}
