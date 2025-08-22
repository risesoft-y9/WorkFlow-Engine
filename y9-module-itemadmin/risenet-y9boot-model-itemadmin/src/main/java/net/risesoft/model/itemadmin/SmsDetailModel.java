package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qinman
 * @date 2025/08/22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsDetailModel implements Serializable {

    private static final long serialVersionUID = 5627857188225983926L;
    /**
     * 主键
     */
    private String id;

    /**
     * 流程编号
     */
    private String processSerialNumber;

    /**
     * 岗位ID
     */
    private String positionId;

    /**
     * 岗位名称
     */
    private String positionName;

    /**
     * 是否发送
     */
    private boolean send;

    /**
     * 是否署名
     */
    private boolean sign;

    /**
     * 短信内容
     */
    private String content;

    /**
     * 接收短信岗位id
     */
    private String positionIds;

    /**
     * 创建时间
     */
    private String createDate;

    /**
     * 修改时间
     */
    private String modifyDate;
}
