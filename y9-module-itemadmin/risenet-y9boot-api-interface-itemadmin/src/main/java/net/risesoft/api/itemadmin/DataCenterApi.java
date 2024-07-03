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
     * @param userId 人员id
     * @return Y9Result<Object>
     */
    @PostMapping("/saveToDateCenter")
    Y9Result<Object> saveToDateCenter(@RequestParam("processInstanceId") String processInstanceId,
        @RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId);
}
