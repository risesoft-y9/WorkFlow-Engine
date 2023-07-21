package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "FF_SIGNATUREPICTURE")
@org.hibernate.annotations.Table(comment = "签名图片表", appliesTo = "FF_SIGNATUREPICTURE")
public class SignaturePicture implements Serializable {

    private static final long serialVersionUID = -7463992527577039409L;

    /**
     * 唯一标示
     */
    @Id
    @org.hibernate.annotations.Comment("主键")
    @Column(name = "ID", length = 38, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    /**
     * 文件仓库Id
     */
    @org.hibernate.annotations.Comment("文件仓库Id")
    @Column(name = "FILESTOREID", length = 50)
    private String fileStoreId;

    /**
     * 租户Id
     */
    @org.hibernate.annotations.Comment("租户Id")
    @Column(name = "TENANTID", length = 50, nullable = false)
    private String tenantId;

    /**
     * 签名归属人员id
     */
    @org.hibernate.annotations.Comment("人员id")
    @Column(name = "USERID", length = 50, nullable = false, unique = true)
    private String userId;

    /**
     * 签名归属人员名称
     */
    @org.hibernate.annotations.Comment("人员名称")
    @Column(name = "USERNAME", length = 50, nullable = false)
    private String userName;

    /**
     * 生成时间
     */
    @org.hibernate.annotations.Comment("生成时间")
    @Column(name = "CREATEDATE", length = 50)
    private String createDate;

    /**
     * 最后的修改时间
     */
    @org.hibernate.annotations.Comment("修改时间")
    @Column(name = "MODIFYDATE", length = 50)
    private String modifyDate;

}
