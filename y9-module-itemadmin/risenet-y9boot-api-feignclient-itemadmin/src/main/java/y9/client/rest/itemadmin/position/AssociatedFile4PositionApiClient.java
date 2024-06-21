package y9.client.rest.itemadmin.position;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.position.AssociatedFile4PositionApi;
import net.risesoft.model.itemadmin.AssociatedFileModel;
import net.risesoft.pojo.Y9Result;

/**
 * 关联文件接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "AssociatedFile4PositionApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}", url = "${y9.service.itemAdmin.directUrl:}", path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/associatedFile4Position")
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
    Y9Result<Integer> countAssociatedFile(@RequestParam("tenantId") String tenantId, @RequestParam("processSerialNumber") String processSerialNumber);

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
    public Y9Result<Object> deleteAllAssociatedFile(@RequestParam("tenantId") String tenantId, @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("delIds") String delIds);

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
    public Y9Result<Object> deleteAssociatedFile(@RequestParam("tenantId") String tenantId, @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("delId") String delId);

    /**
     * 获取关联文件列表,包括未办结件
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return Y9Result<List<AssociatedFileModel>>
     */
    @Override
    @GetMapping("/getAssociatedFileAllList")
    public Y9Result<List<AssociatedFileModel>> getAssociatedFileAllList(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId, @RequestParam("processSerialNumber") String processSerialNumber);

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
    public Y9Result<Object> saveAssociatedFile(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId, @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("processInstanceIds") String processInstanceIds);
}
