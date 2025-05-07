package net.risesoft.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import org.hibernate.annotations.Comment;

import lombok.Data;

/**
 * @author : qinman
 * @date : 2024-10-11
 **/
@Entity
@Data
@Comment("条码号信息表")
@Table(name = "FF_TMH_PICTURE")
public class TmhPicture implements Serializable {

    private static final long serialVersionUID = -9089070709529257056L;

    @Id
    @Column(name = "ID", nullable = false)
    private String id;

    @Comment("流程编号")
    @Column(name = "PROCESSSERIALNUMBER", length = 50, nullable = false)
    private String processSerialNumber;

    @Comment("文件仓库Id")
    @Column(name = "FILESTOREID", length = 50)
    private String fileStoreId;

    @Comment("条码号")
    @Column(name = "TMH", length = 50)
    private String tmh;

    @Comment("条码号类型 TMH：条形码图片 QY：清样二维码")
    @Column(name = "TMHTYPE", length = 10)
    private String tmhType;

    @Comment("图片内容")
    @Lob
    @Column(name = "PICTURE")
    private byte[] picture;

    @Comment("更新时间")
    @Column(name = "UPDATETIME", length = 50)
    private String updateTime;
}
