package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 文件属性信息
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class FileAttributeModel implements Serializable {

    private static final long serialVersionUID = -8406553608849892048L;

    private String pid;

    // 事项级别
    private String pcode;

    // 事项名称
    private String pname;

    private String pdesc;

    private String dept_id;

    private String dept_name;

    private String dept_cs_id;

    private String dept_cs_name;

    private String dept_xb_id;

    private String dept_xb_name;

    private String shb;

    private String spdx;

    private String fwzn;

    private String gzlc;

    private String flfg;

    private String xxgk;

    private String gzsx_bwq;

    private String gzsx_hgh;

    private String ptype;

    private String limitetime;

    private String limitetime_sl;

    private String limitetime_dtts;

    private String limitetime_bl;

    private String pstate;

    private String porder;

    private String pmemo;

    private String updated;

    private String phone;

    private String winservice;

    private String tssx;

    private String limitetime_wwhq;

    private String realtime_auto_sl;

    private String plan;

}
