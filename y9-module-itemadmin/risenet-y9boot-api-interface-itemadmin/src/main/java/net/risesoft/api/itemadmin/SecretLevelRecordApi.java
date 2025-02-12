package net.risesoft.api.itemadmin;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.pojo.Y9Result;

/**
 * 密级记录接口
 *
 * @author yihong
 * @date 2025/02/12
 */
public interface SecretLevelRecordApi {

    /**
     * 保存密级修改记录
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @param secretLevel 密级
     * @param secretBasis 定密依据
     * @param secretItem 具体事项
     * @param description 说明
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.8
     */
    @PostMapping("/saveRecord")
    Y9Result<Object> saveRecord(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("processSerialNumber") String processSerialNumber,
        @RequestParam(value = "secretLevel") String secretLevel,
        @RequestParam(value = "secretBasis") String secretBasis, @RequestParam(value = "secretItem") String secretItem,
        @RequestParam(value = "description", required = false) String description);

}
