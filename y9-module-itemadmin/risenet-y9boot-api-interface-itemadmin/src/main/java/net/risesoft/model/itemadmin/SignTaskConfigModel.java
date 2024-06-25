package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 签收任务配置信息
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class SignTaskConfigModel implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -1553314101761337669L;

    /**
     * 是否签收任务
     */
    private boolean signTask;

    /**
     * 发送候选人
     */
    private String userChoice;

    /**
     * 是否只有一个发送候选人
     */
    private boolean onePerson;

}
