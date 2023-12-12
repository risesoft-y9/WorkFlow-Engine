package y9.client.rest.itemadmin.position;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.position.AssociatedFile4PositionApi;

/**
 * 关联文件接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "AssociatedFile4PositionApiClient", name = "itemAdmin", url = "${y9.common.itemAdminBaseUrl}", path = "/services/rest/associatedFile4Position")
public interface AssociatedFile4PositionApiClient extends AssociatedFile4PositionApi {

    /**
     * 关联文件计数
     *
     * @param tenantId
     * @param processSerialNumber
     * @return
     */
    @Override
    @GetMapping("/countAssociatedFile")
    int countAssociatedFile(@RequestParam("tenantId") String tenantId, @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 删除关联文件
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param delIds 关联流程实例id(,隔开)
     * @return boolean 是否删除成功
     */
    @Override
    @PostMapping("/deleteAllAssociatedFile")
    public boolean deleteAllAssociatedFile(@RequestParam("tenantId") String tenantId, @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("delIds") String delIds);

    /**
     * 删除关联文件
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param delId 关联流程实例id
     * @return boolean 是否删除成功
     */
    @Override
    @PostMapping("/deleteAssociatedFile")
    public boolean deleteAssociatedFile(@RequestParam("tenantId") String tenantId, @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("delId") String delId);

    /**
     * 获取关联文件列表,包括未办结件
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping("/getAssociatedFileAllList")
    public Map<String, Object> getAssociatedFileAllList(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId, @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 获取关联文件列表
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping("/getAssociatedFileList")
    public Map<String, Object> getAssociatedFileList(@RequestParam("tenantId") String tenantId, @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 保存关联文件
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param processSerialNumber 流程编号
     * @param processInstanceIds 关联的流程实例ids
     * @return boolean 是否保存成功
     */
    @Override
    @PostMapping("/saveAssociatedFile")
    public boolean saveAssociatedFile(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId, @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("processInstanceIds") String processInstanceIds);
}
