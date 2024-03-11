package y9.client.rest.itemadmin;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.CustomProcessInfoApi;
import net.risesoft.model.itemadmin.CustomProcessInfoModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "CustomProcessInfoApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/customProcessInfo")
public interface CustomProcessInfoApiClient extends CustomProcessInfoApi {

    /**
     * 获取当前运行任务的下一个节点
     *
     * @param tenantId
     * @param processSerialNumber
     * @return
     */
    @Override
    @GetMapping("/getCurrentTaskNextNode")
    CustomProcessInfoModel getCurrentTaskNextNode(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 保存流程定制信息
     *
     * @param tenantId
     * @param itemId
     * @param processSerialNumber
     * @param taskList
     * @return
     */
    @Override
    @PostMapping("/saveOrUpdate")
    public boolean saveOrUpdate(@RequestParam("tenantId") String tenantId, @RequestParam("itemId") String itemId,
        @RequestParam("processSerialNumber") String processSerialNumber,
        @RequestParam("taskList") List<Map<String, Object>> taskList);

    /**
     * 更新当前运行节点
     *
     * @param tenantId
     * @param processSerialNumber
     * @return
     */
    @Override
    @PostMapping("/updateCurrentTask")
    boolean updateCurrentTask(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber);

}
