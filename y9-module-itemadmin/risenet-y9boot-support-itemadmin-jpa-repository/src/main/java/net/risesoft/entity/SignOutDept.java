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
@Table(name = "FF_SIGN_OUT_DEPT")
@org.hibernate.annotations.Table(comment = "发文单位表,委外会签单位表", appliesTo = "FF_SIGN_OUT_DEPT")
public class SignOutDept implements Serializable {

    @Id
    @Comment("单位Id")
    @Column(name = "DEPTID", length = 10, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String deptId;

    @Comment("单位类型Id")
    @Column(name = "DEPTTYPEID", length = 50)
    private String deptTypeId;

    @Comment("单位名称")
    @Column(name = "DEPTNAME", length = 100)
    private String deptName;

    @Comment("单位序号")
    @Column(name = "DEPTORDER", length = 10)
    private Integer deptOrder;

    @Comment("单位类型")
    @Column(name = "DEPTTYPE", length = 50)
    private String deptType;

    @Comment("发送类型")
    @Column(name = "SENDTYPE", length = 50)
    private String sendType;

    @Comment("单位代码")
    @Column(name = "DEPTCODE", length = 50)
    private String deptCode;

    @Comment("单位全称")
    @Column(name = "DEPTNAMEMAX", length = 100)
    private String deptNameMax;

    @Comment("全拼")
    @Column(name = "FULLSPELL", length = 100)
    private String fullSpell;

    @Comment("第一字母简拼")
    @Column(name = "FIRSTSPELL", length = 100)
    private String firstSpell;

}