package net.risesoft.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.position.ProcessTrack4PositionApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.ProcessTrack;
import net.risesoft.model.itemadmin.ProcessTrackModel;
import net.risesoft.model.platform.Position;
import net.risesoft.service.ProcessTrackService;
import net.risesoft.util.ItemAdminModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 历程接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequestMapping(value = "/services/rest/processTrack4Position")
public class ProcessTrackApiImpl implements ProcessTrack4PositionApi {

    @Autowired
    private ProcessTrackService processTrackService;

    @Autowired
    private PositionApi positionManager;

    /**
     * 根据唯一标示删除历程数据
     *
     * @param tenantId 租户id
     * @param id 唯一标识
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/deleteById", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteById(String tenantId, String id) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        processTrackService.deleteById(id);
    }

    /**
     * 根据任务id获取自定义历程
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return List<ProcessTrackModel>
     */
    @Override
    @GetMapping(value = "/findByTaskId", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProcessTrackModel> findByTaskId(String tenantId, String taskId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<ProcessTrackModel> items = new ArrayList<ProcessTrackModel>();
        try {
            if (StringUtils.isNotBlank(taskId)) {
                List<ProcessTrack> list = processTrackService.findByTaskId(taskId);
                items = ItemAdminModelConvertUtil.processTrackList2ModelList(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    /**
     * 根据任务id获取自定义历程
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return List<ProcessTrackModel>
     * @return
     */
    @Override
    @GetMapping(value = "/findByTaskIdAsc", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProcessTrackModel> findByTaskIdAsc(String tenantId, String taskId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<ProcessTrackModel> items = new ArrayList<ProcessTrackModel>();
        try {
            if (StringUtils.isNotBlank(taskId)) {
                List<ProcessTrack> list = processTrackService.findByTaskIdAsc(taskId);
                items = ItemAdminModelConvertUtil.processTrackList2ModelList(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    /**
     * 获取历程列表(包含每个任务节点的特殊操作的历程)
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param processInstanceId 流程实例id
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping(value = "/processTrackList", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> processTrackList(String tenantId, String positionId, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        retMap.put(UtilConsts.SUCCESS, false);
        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        try {
            if (StringUtils.isNotBlank(processInstanceId)) {
                items = processTrackService.getListMap(processInstanceId);
                retMap.put(UtilConsts.SUCCESS, true);
                retMap.put("rows", items);
            }
        } catch (Exception e) {
            retMap.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        return retMap;
    }

    /**
     * 获取历程信息
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param processInstanceId 流程实例id
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping(value = "/processTrackList4Simple", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> processTrackList4Simple(String tenantId, String positionId, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        retMap.put(UtilConsts.SUCCESS, false);
        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        try {
            if (StringUtils.isNotBlank(processInstanceId)) {
                items = processTrackService.getListMap4Simple(processInstanceId);
                retMap.put(UtilConsts.SUCCESS, true);
                retMap.put("rows", items);
            }
        } catch (Exception e) {
            retMap.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        return retMap;
    }

    /**
     * 保存或更新历程
     *
     * @param tenantId 租户id
     * @return processTrackModel 实体类对象
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/saveOrUpdate", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public ProcessTrackModel saveOrUpdate(String tenantId, @RequestBody ProcessTrackModel processTrackModel)
        throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        ProcessTrack processTrack = ItemAdminModelConvertUtil.processTrackModel2ProcessTrack(processTrackModel);
        ProcessTrack ptTemp = processTrackService.saveOrUpdate(processTrack);
        ProcessTrackModel ptModelTemp = ItemAdminModelConvertUtil.processTrack2Model(ptTemp);
        return ptModelTemp;
    }
}
