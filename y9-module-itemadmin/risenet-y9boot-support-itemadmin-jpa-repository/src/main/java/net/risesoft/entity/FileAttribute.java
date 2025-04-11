package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件属性信息表
 *
 * @author : qinman
 * @date : 2024-10-11
 **/
@NoArgsConstructor
@Entity
@Data
@Table(name = "FF_FILE_ATTRIBUTE")
@org.hibernate.annotations.Table(comment = "文件属性信息表", appliesTo = "FF_FILE_ATTRIBUTE")
public class FileAttribute implements Serializable {

    private static final long serialVersionUID = 421808058972783631L;
    @Id
    @Column(name = "pid", nullable = false)
    private String pid;

    @Comment("事项级别")
    @Column(name = "pcode", length = 100, nullable = false)
    private String pcode;

    @Comment("事项名称")
    @Column(name = "pname", length = 100, nullable = false)
    private String pname;

    @Column(name = "pdesc", length = 100)
    private String pdesc;

    @Column(name = "dept_id", length = 100)
    private String dept_id;

    @Column(name = "dept_name", length = 100)
    private String dept_name;

    @Column(name = "dept_cs_id", length = 100)
    private String dept_cs_id;

    @Column(name = "dept_cs_name", length = 100)
    private String dept_cs_name;

    @Column(name = "dept_xb_id", length = 100)
    private String dept_xb_id;

    @Column(name = "dept_xb_name", length = 100)
    private String dept_xb_name;

    @Column(name = "shb", length = 100)
    private String shb;

    @Column(name = "spdx", length = 100)
    private String spdx;

    @Column(name = "fwzn", length = 100)
    private String fwzn;

    @Column(name = "gzlc", length = 100)
    private String gzlc;

    @Column(name = "flfg", length = 100)
    private String flfg;

    @Column(name = "xxgk", length = 100)
    private String xxgk;

    @Column(name = "gzsx_bwq", length = 100)
    private String gzsx_bwq;

    @Column(name = "gzsx_hgh", length = 100)
    private String gzsx_hgh;

    @Column(name = "ptype", length = 100)
    private String ptype;

    @Column(name = "limitetime", length = 100)
    private String limitetime;

    @Column(name = "limitetime_sl", length = 100)
    private String limitetime_sl;

    @Column(name = "limitetime_dtts", length = 100)
    private String limitetime_dtts;

    @Column(name = "limitetime_bl", length = 100)
    private String limitetime_bl;

    @Column(name = "pstate", length = 100)
    private String pstate;

    @Column(name = "porder", length = 100)
    private String porder;

    @Column(name = "pmemo", length = 100)
    private String pmemo;

    @Column(name = "updated", length = 100)
    private String updated;

    @Column(name = "phone", length = 100)
    private String phone;

    @Column(name = "winservice", length = 100)
    private String winservice;

    @Column(name = "tssx", length = 100)
    private String tssx;

    @Column(name = "limitetime_wwhq", length = 100)
    private String limitetime_wwhq;

    @Column(name = "realtime_auto_sl", length = 100)
    private String realtime_auto_sl;

    @Column(name = "plan", length = 100)
    private String plan;

}
