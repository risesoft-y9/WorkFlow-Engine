package net.risesoft.api.itemadmin;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.pojo.Y9Result;

/**
 * 数据中心接口
 *
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
     * @param orgUnitId 人员、岗位id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/saveToDateCenter")
    Y9Result<Object> saveToDateCenter(@RequestParam("processInstanceId") String processInstanceId,
        @RequestParam("tenantId") String tenantId, @RequestParam("orgUnitId") String orgUnitId);
}
