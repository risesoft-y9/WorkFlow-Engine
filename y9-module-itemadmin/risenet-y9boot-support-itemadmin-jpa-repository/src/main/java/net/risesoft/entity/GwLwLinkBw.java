package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.*;

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
@Table(name = "FF_GW_LWLINKBW")
@org.hibernate.annotations.Table(comment = "来文与办文的关联关系表", appliesTo = "FF_GW_LWLINKBW")
public class GwLwLinkBw implements Serializable {

    private static final long serialVersionUID = -8889935064829623499L;
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

    @Comment("委内编号")
    @Column(name = "WNBH", length = 100)
    private String wnbh;

    @Comment("委内编号(来文表主键)")
    @Column(name = "WNBHUID", length = 100)
    private String wnbhUid;

    @Comment("录入人")
    @Column(name = "INPUTPERSON", length = 100)
    private String inputPerson;

    @Comment("录入时间")
    @Column(name = "RECORDTIME", length = 50)
    private String recordTime;

    @Comment("lwinfo的唯一键")
    @Column(name = "LWINFOUID", length = 100)
    private String lwInfoUid;

    @Comment("判断是否是来文转的办文，0办文关联的来文 1来文转办文")
    @Column(name = "ISINSTANCEASSOCIATE", length = 20)
    private String isInstanceAssociate;

    @Comment("来文标题")
    @Column(name = "LWTITLE", length = 500)
    private String lwTitle;

    @Comment("来文单位")
    @Column(name = "LWDEPT")
    private String lwDept;

    @Comment("来文时限")
    @Column(name = "LWSX", length = 50)
    private String lwsx;

}
