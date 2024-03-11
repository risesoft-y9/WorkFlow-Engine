package y9.client.rest.itemadmin;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.AssociatedFileApi;

/**
 * 关联文件接口
 * 
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "AssociatedFileApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/associatedFile")
public interface AssociatedFileApiClient extends AssociatedFileApi {

    /**
     * 关联文件计数
     *
     * @param tenantId
     * @param processSerialNumber
     * @return
     */
    @Override
    @GetMapping("/countAssociatedFile")
    int countAssociatedFile(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 删除关联文件
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param processSerialNumber 流程编号
     * @param delIds 关联流程实例id(,隔开)
     * @return boolean 是否删除成功
     */
    @Override
    @PostMapping("/deleteAllAssociatedFile")
    public boolean deleteAllAssociatedFile(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processSerialNumber") String processSerialNumber,
        @RequestParam("delIds") String delIds);

    /**
     * 删除关联文件
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param processSerialNumber 流程编号
     * @param delId 关联流程实例id
     * @return boolean 是否删除成功
     */
    @Override
    @PostMapping("/deleteAssociatedFile")
    public boolean deleteAssociatedFile(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processSerialNumber") String processSerialNumber,
        @RequestParam("delId") String delId);

    /**
     * 获取关联文件列表,包括未办结件
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param processSerialNumber 流程编号
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping("/getAssociatedFileAllList")
    public Map<String, Object> getAssociatedFileAllList(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 获取关联文件列表
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param processSerialNumber 流程编号
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping("/getAssociatedFileList")
    public Map<String, Object> getAssociatedFileList(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 保存关联文件
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param processSerialNumber 流程编号
     * @param processInstanceIds 关联的流程实例ids
     * @return boolean 是否保存成功
     */
    @Override
    @PostMapping("/saveAssociatedFile")
    public boolean saveAssociatedFile(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("processSerialNumber") String processSerialNumber,
        @RequestParam("processInstanceIds") String processInstanceIds);
}
