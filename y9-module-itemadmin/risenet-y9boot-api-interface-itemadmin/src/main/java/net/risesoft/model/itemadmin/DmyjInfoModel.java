package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 定密依据信息
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class DmyjInfoModel implements Serializable {

    private static final long serialVersionUID = -1344260738716590500L;

    private String id;

    // 定密依据密级
    private String dmyjmiji;

    // 定密依据名称
    private String dmyjmc;

    // 定密依据事项
    private String dmyjsx;

    // 定密依据事项值
    private String dmyjsxvalue;

    // 定密依据司局
    private String dmyjsj;

    // 是否删除
    private String isdelete = "0";
}
