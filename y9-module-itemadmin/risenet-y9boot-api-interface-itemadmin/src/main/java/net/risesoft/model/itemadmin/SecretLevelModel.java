package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 密级记录信息
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class SecretLevelModel implements Serializable {

    private static final long serialVersionUID = 5654443435512026665L;
    private String id;

    // 流程编号
    private String processSerialNumber;

    // 密级
    private String secretLevel;

    // 定密依据
    private String secretBasis;

    // 具体事项
    private String secretItem;

    // 说明
    private String description;

    // 创建人Id
    private String createUserId;

    // 创建时间
    private String createTime;
}
