package net.risesoft.api;

import net.risesoft.api.itemadmin.position.AssociatedFile4PositionApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.model.platform.Position;
import net.risesoft.service.AssociatedFileService;
import net.risesoft.y9.Y9LoginUserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 关联文件接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequestMapping(value = "/services/rest/associatedFile4Position")
public class AssociatedFileApiImpl implements AssociatedFile4PositionApi {

    @Resource(name = "associatedFileService")
    private AssociatedFileService associatedFileService;

    @Autowired
    private PositionApi positionManager;

    /**
     * 关联文件计数
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return int
     */
    @Override
    @GetMapping(value = "/countAssociatedFile", produces = MediaType.APPLICATION_JSON_VALUE)
    public int countAssociatedFile(String tenantId, String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return associatedFileService.countAssociatedFile(processSerialNumber);
    }

    /**
     * 批量删除关联文件
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param delIds 关联流程实例id(,隔开)
     * @return boolean 是否删除成功
     */
    @Override
    @PostMapping(value = "/deleteAllAssociatedFile", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteAllAssociatedFile(String tenantId, String processSerialNumber, String delIds) {
        Y9LoginUserHolder.setTenantId(tenantId);
        boolean b = associatedFileService.deleteAllAssociatedFile(processSerialNumber, delIds);
        return b;
    }

    /**
     * 删除关联文件
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param delId 关联流程实例id
     * @return boolean 是否删除成功
     */
    @Override
    @PostMapping(value = "/deleteAssociatedFile", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteAssociatedFile(String tenantId, String processSerialNumber, String delId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        boolean b = associatedFileService.deleteAssociatedFile(processSerialNumber, delId);
        return b;
    }

    /**
     * 获取关联文件列表(包括未办结件)
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param processSerialNumber 流程编号
     * @return Map<String, Object>
     */
    @Override
    @GetMapping(value = "/getAssociatedFileAllList", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getAssociatedFileAllList(String tenantId, String positionId,
        String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map = associatedFileService.getAssociatedFileAllList(processSerialNumber);
        return map;
    }

    /**
     * 获取关联文件列表
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return Map<String, Object>
     */
    @Override
    @GetMapping(value = "/getAssociatedFileList", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getAssociatedFileList(String tenantId, String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map = associatedFileService.getAssociatedFileList(processSerialNumber);
        return map;
    }

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
    @PostMapping(value = "/saveAssociatedFile", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean saveAssociatedFile(String tenantId, String positionId, String processSerialNumber,
        String processInstanceIds) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        boolean b = associatedFileService.saveAssociatedFile(processSerialNumber, processInstanceIds);
        return b;
    }
}
