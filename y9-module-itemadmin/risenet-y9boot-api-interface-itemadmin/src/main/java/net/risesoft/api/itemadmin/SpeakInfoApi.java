package net.risesoft.api.itemadmin;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.SpeakInfoModel;
import net.risesoft.pojo.Y9Result;

/**
 * 发言信息管理
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface SpeakInfoApi {

    /**
     * 逻辑删除发言信息
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param id 主键id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping(value = "/deleteById")
    Y9Result<Object> deleteById(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("id") String id);

    /**
     * 根据唯一标示获取发言信息
     *
     * @param tenantId 租户id
     * @param id 主键id
     * @return {@code Y9Result<SpeakInfoModel>} 通用请求返回对象 - data 是发言信息
     * @since 9.6.6
     */
    @GetMapping(value = "/findById")
    Y9Result<SpeakInfoModel> findById(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id);

    /**
     * 根据流程实例查找某一个流程的所有发言信息，根据时间倒叙排列
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<List<SpeakInfoModel>>} 通用请求返回对象 - data 是发言信息列表
     * @since 9.6.6
     */
    @GetMapping(value = "/findByProcessInstanceId")
    Y9Result<List<SpeakInfoModel>> findByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 获取未读消息计数
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data 是未读消息计数
     * @since 9.6.6
     */
    @GetMapping(value = "/getNotReadCount")
    Y9Result<Integer> getNotReadCount(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 保存或者更新发言信息
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param speakInfoModel 发言信息
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是发言ID
     * @since 9.6.6
     */
    @PostMapping(value = "/saveOrUpdate", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<String> saveOrUpdate(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestBody SpeakInfoModel speakInfoModel);
}
