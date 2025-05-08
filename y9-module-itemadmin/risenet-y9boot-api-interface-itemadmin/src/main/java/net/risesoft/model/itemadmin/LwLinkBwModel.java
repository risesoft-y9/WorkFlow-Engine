package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 来文信息
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class LwLinkBwModel implements Serializable {

    private static final long serialVersionUID = -5665416679230660781L;
    private String id;

    // 流程实例编号
    private String processSerialNumber;

    // 委内编号
    private String wnbh;

    // 委内编号(来文表主键)
    private String wnbhUid;

    // 录入人
    private String inputPerson;

    // 录入时间
    private String recordTime;

    // lwinfo的唯一键
    private String lwInfoUid;

    // 判断是否是来文转的办文，0办文关联的来文 1来文转办文
    private String isInstanceAssociate;

    // 来文标题
    private String lwTitle;

    // 来文单位
    private String lwDept;

    // 来文时限
    private String lwsx;
}
