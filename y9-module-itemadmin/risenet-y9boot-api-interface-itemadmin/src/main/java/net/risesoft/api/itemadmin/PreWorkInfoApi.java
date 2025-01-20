package net.risesoft.api.itemadmin;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.PreWorkModel;
import net.risesoft.pojo.Y9Result;

/**
 * 前期工作事项接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface PreWorkInfoApi {

    /**
     * 删除前期工作事项
     *
     * @param tenantId 租户id
     * @param id 主键
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    Y9Result<Object> delPreWork(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id);

    /**
     * 获取前期工作事项
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<List<PreWorkModel>>} 通用请求返回对象
     * @since 9.6.6
     */
    @GetMapping("/findByProcessSerialNumber")
    Y9Result<List<PreWorkModel>> findByProcessSerialNumber(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 保存前期工作事项
     *
     * @param tenantId 租户id
     * @param preWorkModel 前期工作事项
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping(value = "/saveOrUpdate", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> saveOrUpdate(@RequestParam("tenantId") String tenantId, @RequestBody PreWorkModel preWorkModel);
}
