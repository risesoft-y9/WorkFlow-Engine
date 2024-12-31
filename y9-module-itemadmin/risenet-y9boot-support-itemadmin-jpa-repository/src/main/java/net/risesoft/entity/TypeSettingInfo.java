package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;
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
@Table(name = "FF_TYPESETTING_INFO")
@org.hibernate.annotations.Table(comment = "发文单排版信息表", appliesTo = "FF_TYPESETTING_INFO")
public class TypeSettingInfo implements Serializable {

    private static final long serialVersionUID = -1998434487391463222L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("排版方式")
    @Column(name = "way", length = 50)
    private String way;

    @Comment("模板")
    @Column(name = "template", length = 100)
    private String template;

    @Comment("流程编号")
    @Column(name = "processSerialNumber", length = 50, nullable = false)
    private String processSerialNumber;

    @Comment("清样文件")
    @Column(name = "qingyangFile", length = 50)
    private String qingyangFile;

    @Comment("板式文件")
    @Column(name = "banshiFile", length = 50)
    private String banshiFile;

    @Comment("排版意见")
    @Column(name = "typeSettingOpinion", length = 500)
    private String typeSettingOpinion;

    @Comment("排版人")
    @Column(name = "typeSettingUserName", length = 50)
    private String typeSettingUserName;

    @Comment("排版时间")
    @Column(name = "typeSettingTime", length = 50)
    private String typeSettingTime;

    @Comment("校对意见")
    @Column(name = "checkOpinion", length = 500)
    private String checkOpinion;

    @Comment("校对审核意见")
    @Column(name = "shenheOpinion", length = 500)
    private String shenheOpinion;

    @Comment("校对审核人")
    @Column(name = "checkUserName", length = 50)
    private String checkUserName;

    @Comment("校对时间")
    @Column(name = "checkTime", length = 50)
    private String checkTime;

    @Comment("排序")
    @Column(name = "tabIndex", length = 10)
    private Integer tabIndex;

}