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
@Table(name = "FF_GW_LWINFO")
@org.hibernate.annotations.Table(comment = "来文信息表", appliesTo = "FF_GW_LWINFO")
public class GwLwInfo implements Serializable {

    private static final long serialVersionUID = 486951981779208584L;
    /**
     * 主键
     */
    @Id
    @Comment("主键")
    @Column(name = "ID", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("流程实例编号")
    @Column(name = "PROCESSSERIALNUMBER", length = 50)
    private String processSerialNumber;

    @Comment("收文文号（委内编号）")
    @Column(name = "WNBH", length = 50)
    private String wnbh;

    @Comment("收文条码编号")
    @Column(name = "LWCODE", length = 50)
    private String lwcode;

    @Comment("来文文号")
    @Column(name = "LWWH")
    private String lwwh;

    @Comment("来文标题")
    @Column(name = "LWTITLE", length = 500)
    private String lwTitle;

    @Comment("文件类型")
    @Column(name = "WJTYPE", length = 50)
    private String wjtype;

    @Comment("来文日期")
    @Column(name = "LWDATE", length = 50)
    private String lwDate;

    @Comment("来文单位")
    @Column(name = "LWDEPT")
    private String lwDept;

    @Comment("来文密级")
    @Column(name = "MIJI", length = 20)
    private String miji;

    @Comment("来文缓急")
    @Column(name = "HUANJI", length = 50)
    private String huanji;

    @Comment("来文时限")
    @Column(name = "LWSX", length = 50)
    private String lwsx;

    @Comment("来文份数")
    @Column(name = "LWFS", length = 20)
    private String lwfs;

    @Comment("是否受理类(1受理，0不受理)")
    @Column(name = "BANFOU", length = 20)
    private String banfou;

    @Comment("主办单位")
    @Column(name = "ZBDEPT")
    private String zbDept;

    @Comment("文件属性（三级分类，4位一组）")
    @Column(name = "FILEPROPERTY", length = 50)
    private String fileProperty;

    @Comment("审核备（汉字审、核、备）")
    @Column(name = "SHB", length = 50)
    private String shb;

    @Comment("时限")
    @Column(name = "LIMITTIME", length = 50)
    private String limiTime;

    @Comment("记录插入时间")
    @Column(name = "RECORDTIME", length = 50)
    private String recordTime;

    @Comment("是否是debug数据标识(0:否，1是)")
    @Column(name = "ISDEBUG", length = 20)
    private String isDebug;

    @Comment("收文流程处理状态,默认'0'")
    @Column(name = "HANDLESTATUS", length = 30)
    private String handleStatus = "0";

    @Comment("lwinfo唯一键收文流程businesskey来使用")
    @Column(name = "LWINFOUID", length = 50)
    private String lwInfoUid;

    @Comment("控制司局长意见的权限")
    @Column(name = "BUREAUMINISTERMIND", length = 200)
    private String bureauministerMind;

    @Comment("控制处长意见的权限")
    @Column(name = "OFFICEMINISTERMIND", length = 200)
    private String officeministerMind;

    @Comment("控制司局秘书意见的权限")
    @Column(name = "BUREAUSECERTARYMIND", length = 200)
    private String bureauSecertaryMind;

    @Comment("控制承办人的意见的权限")
    @Column(name = "UNDERTAKEPERSONMIND", length = 200)
    private String undertakePersonMind;

    @Comment("一级属性")
    @Column(name = "TOPPROPERTY", length = 50)
    private String topproperty;

    @Comment("二级属性")
    @Column(name = "SECONDPROPERTY", length = 50)
    private String secondproperty;

    @Comment("三级属性")
    @Column(name = "THIRDPROPERTY", length = 50)
    private String thirdproperty;

    @Comment("是否受理类‘是’表示是受理类")
    @Column(name = "ACCEPTORNOT", length = 50)
    private String acceptorNot;

    @Comment("大厅主键")
    @Column(name = "HALLINDEX", length = 50)
    private String hallIndex;

    @Comment("大厅登记码")
    @Column(name = "HALLREG", length = 50)
    private String hallReg;

    @Comment("收文前期事项权限")
    @Column(name = "QQSXBTN", length = 50)
    private String qqsxBtn;

    @Comment("记录创建时间")
    @Column(name = "CREATETIME", length = 50)
    private String createTime;

    @Comment("办否 1是，0否")
    @Column(name = "NEEDDO", length = 20)
    private String needdo = "0";

    @Comment("是否督查系统修改主办单位 1是,0否")
    @Column(name = "DCEDIT", length = 20)
    private String decdit = "0";

    @Comment("是否已做超3天受理处理，默认为0，1为已经处理超时的数据")
    @Column(name = "OVERACCEPTTIME", length = 20)
    private String overaccepttime = "0";

    @Comment("来文单位联系人(大厅会传)")
    @Column(name = "TOUCHUSER", length = 50)
    private String touchUser;

    @Comment("来文单位联系人电话")
    @Column(name = "TOUCHTEL", length = 50)
    private String touchTel;

    @Comment("受理方式(受理、自动受理、不予受理、空)")
    @Column(name = "ACCEPTTYPE", length = 50)
    private String acceptType;

    @Comment("大厅发文司局")
    @Column(name = "SENDDEPT", length = 200)
    private String sendDept;

    @Comment("大厅来件申报单位")
    @Column(name = "APPDEPT", length = 200)
    private String appDept;

    @Comment("不予行政许可司长意见的权限控制")
    @Column(name = "NOPERMITSZYJ", length = 50)
    private String nopermitszyj;

    @Comment("不予行政许可处长意见的权限控制")
    @Column(name = "NOPERMITCZYJ", length = 50)
    private String nopermitczyj;

    @Comment("办理方式")
    @Column(name = "HANDLETYPE", length = 10)
    private String handleType = "0";

    @Comment("办结方式")
    @Column(name = "FINISHTYPE", length = 10)
    private String finishtype = "0";

    @Comment("项目名称")
    @Column(name = "XMMC", length = 200)
    private String xmmc;

    @Comment("项目代码")
    @Column(name = "XMDM", length = 50)
    private String xmdm;

    @Comment("是否限时，0 不限时，1限时，默认为1")
    @Column(name = "SFXS", length = 10)
    private String sfxs = "1";

    @Comment("是否为非联网登记文件 1：是")
    @Column(name = "ISFLWDJ", length = 10)
    private String isFlwdj;

}
