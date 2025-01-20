package net.risesoft.api.itemadmin;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.AfterWorkModel;
import net.risesoft.pojo.Y9Result;

/**
 * 核稿后工作事项接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface AfterWorkInfoApi {

    /**
     * 删除核稿后工作事项
     *
     * @param tenantId 租户id
     * @param id 主键id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/delAfterWork")
    Y9Result<Object> delAfterWork(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id);

    /**
     * 获取核稿后工作事项
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<List<AfterWorkModel>>} 通用请求返回对象
     * @since 9.6.6
     */
    @GetMapping("/findByProcessSerialNumber")
    Y9Result<List<AfterWorkModel>> findByProcessSerialNumber(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 保存核稿后工作事项
     *
     * @param tenantId 租户id
     * @param afterWorkModel 核稿后工作事项
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping(value = "/saveOrUpdate", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> saveOrUpdate(@RequestParam("tenantId") String tenantId,
        @RequestBody AfterWorkModel afterWorkModel);
}
