package y9.client.rest.itemadmin;

import java.util.Date;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.ProcessInstanceApi;
import net.risesoft.model.itemadmin.ProcessInstanceDetailsModel;

/**
 *
 * @author zhangchongjie
 * @date 2023/02/06
 */
@FeignClient(contextId = "ProcessInstanceApiClient", name = "itemAdmin", url = "${y9.common.itemAdminBaseUrl}",
    path = "/services/rest/processInstance")
public interface ProcessInstanceApiClient extends ProcessInstanceApi {

    /**
     * 删除协作状态
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return
     */
    @Override
    @PostMapping("/deleteProcessInstance")
    public boolean deleteProcessInstance(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 获取协作状态列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param title 标题或文号
     * @param page 页码
     * @param rows 条数
     * @return
     */
    @Override
    @GetMapping("/processInstanceList")
    Map<String, Object> processInstanceList(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("title") String title, @RequestParam("page") int page,
        @RequestParam("rows") int rows);

    /**
     * 保存协作状态详情
     *
     * @param tenantId 租户id
     * @param model 状态详情
     * @return
     */
    @Override
    @PostMapping(value = "/saveProcessInstanceDetails", consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean saveProcessInstanceDetails(@RequestParam("tenantId") String tenantId,
        @RequestBody ProcessInstanceDetailsModel model);

    /**
     * 更新协作状态详情
     *
     * @param tenantId 租户id
     * @param assigneeId 受让人id
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @param itembox 状态
     * @param endTime 结束时间
     * @return
     */
    @Override
    @PostMapping("/updateProcessInstanceDetails")
    boolean updateProcessInstanceDetails(@RequestParam("tenantId") String tenantId,
        @RequestParam("assigneeId") String assigneeId, @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam("taskId") String taskId, @RequestParam("itembox") String itembox,
        @RequestParam("endTime") Date endTime);
}
