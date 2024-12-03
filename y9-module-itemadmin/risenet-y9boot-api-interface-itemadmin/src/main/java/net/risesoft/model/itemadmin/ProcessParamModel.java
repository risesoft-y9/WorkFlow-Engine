package net.risesoft.model.itemadmin;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 流程参数模型
 * 
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class ProcessParamModel implements Serializable {

    private static final long serialVersionUID = 3792751066006296420L;
    /**
     * 主键
     */
    private String id;

    /**
     * 流程实例Id
     */
    private String processInstanceId;
    /**
     * 流程编号
     */
    private String processSerialNumber;
    /**
     * 事项id
     */
    private String itemId;
    /**
     * 事项id
     */
    private String itemName;
    /**
     * 系统英文名称
     */
    private String systemName;
    /**
     * 系统中文名称
     */
    private String systemCnName;
    /**
     * 标题
     */
    private String title;
    /**
     * 自定义编号
     */
    private String customNumber;
    /**
     * 
     */
    private String customLevel;
    /**
     * 委办局Ids
     */
    private String bureauIds;
    /**
     * 部门ids
     */
    private String deptIds;
    /**
     * 流程办结人员姓名
     */
    private String completer;

    /**
     * 统一待办url前缀
     */
    private String todoTaskUrlPrefix;

    /**
     * 搜索词
     */
    private String searchTerm;

    /**
     * 是否发送短信
     */
    private String isSendSms;
    /**
     * 是否署名
     */
    private String isShuMing;
    /**
     * 发送短信内容
     */
    private String smsContent;
    /**
     * 接收短信人员id
     */
    private String smsPersonId;
    /**
     * 主办人id
     */
    private String sponsorGuid;
    /**
     * 流程的启动人员id
     */
    // @FieldCommit(value="")
    private String startor;
    /**
     * 流程的启动人员姓名
     */
    private String startorName;

    /**
     * 这个件是否发送过,true为发送过
     */
    private String sended;

    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 是否定制流程
     */
    private Boolean customItem;

    /**
     * 目标，xxx使用
     */
    private String target;

    /**
     * 到期时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date dueDate;

    /**
     * 描述
     */
    private String description;
}
