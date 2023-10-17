package net.risesoft.api.itemadmin;

import net.risesoft.model.itemadmin.ErrorLogModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ErrorLogApi {

    /**
     * 保存错误日志
     *
     * @param tenantId 租户id
     * @param errorLogModel 错误日志
     */
    void saveErrorLog(String tenantId, ErrorLogModel errorLogModel);

}
