package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 发文单排版信息
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class TypeSettingInfoModel implements Serializable {

    private static final long serialVersionUID = 2624655543591088001L;

    private String id;

    // 排版方式
    private String way;

    // 模板
    private String template;

    // 流程编号
    private String processSerialNumber;

    // 清样文件
    private String qingyangFile;

    // 板式文件
    private String banshiFile;

    // 合并源文件名称
    private String sourceFileName;

    // 合并源文件仓库id
    private String sourceFileStoreId;

    // 合并板式文件名称
    private String hbBanShiFileName;

    // 合并板式文件仓库id
    private String hbBanShiFileStoreId;

    // 排版意见
    private String typeSettingOpinion;

    // 排版人
    private String typeSettingUserName;

    // 排版时间
    private String typeSettingTime;

    // 拟稿人校对意见类型
    private String ifHaveYj;

    // 校对意见
    private String checkOpinion;

    // 校对审核意见类型
    private String hgrOpinion;

    // 校对审核意见
    private String shenheOpinion;

    // 校对人
    private String checkUserName;

    // 校对审核人
    private String auditUserName;

    // 拟稿人校对时间
    private String checkTime;

    // 校对审核时间
    private String auditTime;

    // 排序
    private Integer tabIndex;

    // 是否保存过
    private String isHave;
}
