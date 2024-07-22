package net.risesoft.model.itemadmin;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * 协作状态信息
 *
 * @author mengjuhua
 * @date 2024/06/28
 */
@Data
public class ProcessCooperationModel implements Serializable {
    private static final long serialVersionUID = 7895484416479299715L;

    /**
     * 办件状态：todo待办，doing在办，done办结
     */
    private String itembox;
    /**
     * 流程实例id
     */
    private String processInstanceId;

    /**
     * 事项名称
     */
    private String itemName;
    /**
     * 文件标题
     */
    private String title;
    /**
     * 编号
     */
    private String number;
    /**
     * 开始时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;
    /**
     * 结束时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;
    /**
     * 详情链接
     */
    private String url;

    /** 详情列表 */
    private List<ProcessInstanceDetailsModel> itemInfo;
}
