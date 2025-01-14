package net.risesoft.api.itemadmin;

import java.util.List;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.LwInfoModel;
import net.risesoft.pojo.Y9Result;

/**
 * 来文信息接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface LwInfoApi {

    /**
     * 获取来文信息列表
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<List<LwInfoModel>>} 通用请求返回对象 - data是附件列表
     * @since 9.6.6
     */
    @GetMapping("/findByProcessSerialNumber")
    Y9Result<List<LwInfoModel>> findByProcessSerialNumber(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber);

}
