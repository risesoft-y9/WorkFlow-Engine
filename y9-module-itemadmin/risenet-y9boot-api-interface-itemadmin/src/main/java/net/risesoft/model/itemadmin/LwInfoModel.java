package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 来文信息
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class LwInfoModel implements Serializable {

    private static final long serialVersionUID = 6346904076578591865L;
    /**
     * 主键
     */
    private String id;

    // 流程实例编号
    private String processSerialNumber;

    // 收文文号（委内编号）
    private String wnbh;

    // 收文条码编号
    private String lwcode;

    // 来文文号
    private String lwwh;

    // 来文标题
    private String lwTitle;

    // 文件类型
    private String wjtype;

    // 来文日期
    private String lwDate;

    // 来文单位
    private String lwDept;

    // 来文密级
    private String miji;

    // 来文缓急
    private String huanji;

    // 来文时限
    private String lwsx;

    // 来文份数
    private String lwfs;

    // 是否受理类(1受理，0不受理)
    private String banfou;

    // 主办单位
    private String zbDept;

    // 文件属性（三级分类，4位一组）
    private String fileProperty;

    // 审核备（汉字审、核、备）
    private String shb;

    // 时限
    private String limiTime;

    // 记录插入时间
    private String recordTime;

    // 是否是debug数据标识(0:否，1是)
    private String isDebug;

    // 收文流程处理状态,默认'0'
    private String handleStatus = "0";

    // lwinfo唯一键收文流程businesskey来使用
    private String lwInfoUid;

    // 控制司局长意见的权限
    private String bureauministerMind;

    // 控制处长意见的权限
    private String officeministerMind;

    // 控制司局秘书意见的权限
    private String bureauSecertaryMind;

    // 控制承办人的意见的权限
    private String undertakePersonMind;

    // 一级属性
    private String topproperty;

    // 二级属性
    private String secondproperty;

    // 三级属性
    private String thirdproperty;

    // 是否受理类‘是’表示是受理类
    private String acceptorNot;

    // 大厅主键
    private String hallIndex;

    // 大厅登记码
    private String hallReg;

    // 收文前期事项权限
    private String qqsxBtn;

    // 记录创建时间
    private String createTime;

    // 办否 1是，0否
    private String needdo = "0";

    // 是否督查系统修改主办单位 1是,0否
    private String decdit = "0";

    // 是否已做超3天受理处理，默认为0，1为已经处理超时的数据
    private String overaccepttime = "0";

    // 来文单位联系人(大厅会传)
    private String touchUser;

    // 来文单位联系人电话
    private String touchTel;

    // 受理方式(受理、自动受理、不予受理、空)
    private String acceptType;

    // 大厅发文司局
    private String sendDept;

    // 大厅来件申报单位
    private String appDept;

    // 不予行政许可司长意见的权限控制
    private String nopermitszyj;

    // 不予行政许可处长意见的权限控制
    private String nopermitczyj;

    // 办理方式
    private String handleType = "0";

    // 办结方式
    private String finishtype = "0";

    // 项目名称
    private String xmmc;

    // 项目代码
    private String xmdm;

    // 是否限时，0 不限时，1限时，默认为1
    private String sfxs = "1";

    // 是否为非联网登记文件 1：是
    private String isFlwdj;
}
