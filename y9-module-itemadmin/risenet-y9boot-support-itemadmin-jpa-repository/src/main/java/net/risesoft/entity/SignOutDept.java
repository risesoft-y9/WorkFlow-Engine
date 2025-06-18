package net.risesoft.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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
@Comment("发文单位表,委外会签单位表")
public class SignOutDept implements Serializable {

    @Id
    @Comment("单位Id")
    @Column(name = "DEPTID", length = 10, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String deptId;

    @Comment("单位Id-old")
    @Column(name = "DEPTIDOLD", length = 10, nullable = false)
    private String deptIdOld;

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

    @Comment("是否禁止")
    @Column(name = "ISFORBIDDEN")
    private Integer isForbidden = 0;

    @Comment("领导称谓")
    @Column(name = "LDCW", length = 50)
    private String ldcw;

    @Comment("单位后缀")
    @Column(name = "DEPTSUFFIX", length = 50)
    private String deptSuffix;

    @Comment("单位总称")
    @Column(name = "FULLDEPTNAME", length = 50)
    private String fullDeptName;
}