package net.risesoft.api.itemadmin;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface DataCenterApi {

    /**
     * 保存办结数据到数据中心
     *
     * @param processInstanceId 流程实例id
     * @param tenantId 租户id
     * @param userId 人员id
     * @return boolean
     */
    boolean saveToDateCenter(String processInstanceId, String tenantId, String userId);
}
