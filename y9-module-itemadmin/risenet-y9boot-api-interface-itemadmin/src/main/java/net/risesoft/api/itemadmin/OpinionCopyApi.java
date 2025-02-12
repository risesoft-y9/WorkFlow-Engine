package net.risesoft.api.itemadmin;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.OpinionCopyModel;
import net.risesoft.pojo.Y9Result;

/**
 * @author : qinman
 * @date : 2025-02-10
 * @since 9.6.8
 **/
public interface OpinionCopyApi {

    @GetMapping("/findByProcessSerialNumber")
    Y9Result<List<OpinionCopyModel>> findByProcessSerialNumber(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("orgUnitId") String orgUnitId,
        @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 保存或更新意见
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param orgUnitId 人员、岗位id
     * @param opinionCopyModel 意见信息
     * @return {@code Y9Result<OpinionCopyModel>} 通用请求返回对象 - data 是意见信息
     * @since 9.6.6
     */
    @PostMapping(value = "/saveOrUpdate", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<OpinionCopyModel> saveOrUpdate(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("orgUnitId") String orgUnitId,
        @RequestBody OpinionCopyModel opinionCopyModel);

    /**
     * 删除意见
     *
     * @param tenantId 租户id
     * @param id 唯一标识
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/deleteById")
    Y9Result<Object> deleteById(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id);
}
