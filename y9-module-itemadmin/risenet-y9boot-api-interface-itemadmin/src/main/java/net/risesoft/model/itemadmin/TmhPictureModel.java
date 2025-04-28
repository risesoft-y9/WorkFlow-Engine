package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 条码号图片信息
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class TmhPictureModel implements Serializable {

    private String id;

    // 流程编号
    private String processSerialNumber;

    // 文件仓库Id
    private String fileStoreId;

    // 条码号
    private String tmh;

    // 条码号类型 TMH：条形码图片 QY：清样二维码
    private String tmhType;

    // 图片内容
    private byte[] picture;

    // 更新时间
    private String updateTime;

}