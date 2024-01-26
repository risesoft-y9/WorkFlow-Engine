package y9.client.rest.itemadmin;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.SpeakInfoApi;
import net.risesoft.model.itemadmin.SpeakInfoModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "SpeakInfoApiClient", name = "itemAdmin", url = "${y9.common.itemAdminBaseUrl}",
    path = "/services/rest/speakInfo")
public interface SpeakInfoApiClient extends SpeakInfoApi {

    /**
     * 逻辑删除发言信息
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param id id
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @PostMapping("/deleteById")
    Map<String, Object> deleteById(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("id") String id);

    /**
     * 根据唯一标示超找发言信息
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param id id
     * @return SpeakInfoModel
     */
    @Override
    @GetMapping("/findById")
    SpeakInfoModel findById(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("id") String id);

    /**
     * 根据流程实例查找某一个流程的所有发言信息，根据时间倒叙排列
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processInstanceId 流程实例id
     * @return List&lt;SpeakInfoModel&gt;
     */
    @Override
    @GetMapping("/findByProcessInstanceId")
    List<SpeakInfoModel> findByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 获取未读消息计数
     *
     * @param tenantId
     * @param userId
     * @param processInstanceId
     * @return
     */
    @Override
    @GetMapping("/getNotReadCount")
    int getNotReadCount(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 保存或者更新发言信息
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param speakInfoModel speakInfoModel
     * @return String
     */
    @Override
    @PostMapping(value = "/saveOrUpdate", consumes = MediaType.APPLICATION_JSON_VALUE)
    String saveOrUpdate(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestBody SpeakInfoModel speakInfoModel);
}
