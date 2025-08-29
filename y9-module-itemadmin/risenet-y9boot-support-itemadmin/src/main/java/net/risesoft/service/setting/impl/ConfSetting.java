package net.risesoft.service.setting.impl;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.y9.configuration.app.y9itemadmin.Y9ItemAdminProperties;

/**
 * 系统配置
 *
 * @author qinman
 * @date 2025/08/28
 */
@Getter
@Setter
public class ConfSetting extends AbstractSetting implements Serializable {

    private static final long serialVersionUID = -5711316601990716247L;

    /** 从统一待办打开办件的前缀地址 */
    private String todoTaskUrlPrefix = Y9ItemAdminProperties.todoTaskUrlPrefix;

    /** 意见排序方式，设置为true,则按照岗位的orderedPath排序 */
    private boolean opinionOrderBy = Y9ItemAdminProperties.opinionOrderBy;

    /** 统一待办开关 */
    private boolean todoSwitch = Y9ItemAdminProperties.todoSwitch;

    /** 消息提醒开关 */
    private boolean msgSwitch = Y9ItemAdminProperties.msgSwitch;

    /** 短信通知开关 **/
    private boolean smsSwitch = Y9ItemAdminProperties.smsSwitch;

    /** 意见常用语 **/
    private String defaultOpinion = "知悉。,同意。,已阅。";

    @Override
    public String getPrefix() {
        return "y9.app.itemAdmin.";
    }
}
