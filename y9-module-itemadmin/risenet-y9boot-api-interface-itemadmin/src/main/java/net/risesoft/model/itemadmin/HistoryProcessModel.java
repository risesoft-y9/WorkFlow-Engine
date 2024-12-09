package net.risesoft.model.itemadmin;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.util.StringUtils;

import lombok.Data;

/**
 * 历程信息
 *
 * @author mengjuhua
 * @date 2024/06/26
 */
@Data
public class HistoryProcessModel implements Serializable, Comparable<HistoryProcessModel> {
    private static final long serialVersionUID = 3187574795107607583L;

    /** 唯一标识 */
    private String id;

    /** 收件人 */
    private String assignee;

    /** 收件人id */
    private String assigneeId;

    /** 任务id */
    private String taskId;

    /** 任务名称 */
    private String name;

    /** 开始时间 */
    private String startTime;

    /** 结束时间 */
    private String endTime;

    /** 开始时间（time） */
    private Long startTimes;

    /** 结束时间（time） */
    private Long endTimes;

    /** 历时 */
    private String time;

    /** 描述 */
    private Object description;

    /** 意见 */
    private String opinion;

    /** 历史正文版本 */
    private Integer historyVersion;

    /** 是否被强制办结任务标识 */
    private String endFlag;

    /** 承办人id,用于数据中心保存 */
    private String undertakerId;

    /** 是否新建待办 */
    private Integer newToDo;

    /** 是否有抄送 */
    private Boolean isChaoSong;

    /** 上一步操作 */
    private String actionName;

    @Override
    public int compareTo(HistoryProcessModel o) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = new Date();
            Date startTime1 = StringUtils.hasText(this.getStartTime()) ? sdf.parse(this.getStartTime()) : now;
            Date startTime2 = StringUtils.hasText(o.getStartTime()) ? sdf.parse(o.getStartTime()) : now;
            if (startTime1.getTime() > startTime2.getTime()) {
                return 1;
            } else if (startTime1.getTime() == startTime2.getTime()) {
                Date date1 = !StringUtils.hasText(this.getEndTime()) ? now : sdf.parse(this.getEndTime());
                Date date2 = !StringUtils.hasText(o.getEndTime()) ? now : sdf.parse(o.getEndTime());
                if (date1.getTime() > date2.getTime()) {// 开始时间相等的才排序
                    return 1;
                }
                if (date1.getTime() == date2.getTime()) {
                    return 0;
                }
                return -1;
            } else {
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

}
