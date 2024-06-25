package net.risesoft.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.position.ProcessTrack4PositionApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.ProcessTrack;
import net.risesoft.model.itemadmin.HistoricActivityInstanceModel;
import net.risesoft.model.itemadmin.ProcessTrackModel;
import net.risesoft.model.platform.Position;
import net.risesoft.pojo.Y9Result;
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
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/processTrack4Position")
public class ProcessTrackApiImpl implements ProcessTrack4PositionApi {

    private final ProcessTrackService processTrackService;

    private final PositionApi positionManager;

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
        List<ProcessTrackModel> items = new ArrayList<>();
        try {
            if (StringUtils.isNotBlank(taskId)) {
                List<ProcessTrack> list = processTrackService.findByTaskId(taskId);
                items = ItemAdminModelConvertUtil.processTrackList2ModelList(list);
            }
        } catch (Exception e) {
            LOGGER.error("根据任务id获取自定义历程异常：{}", e.getMessage());
        }
        return items;
    }

    /**
     * 根据任务id获取自定义历程
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return List<ProcessTrackModel>
     */
    @Override
    @GetMapping(value = "/findByTaskIdAsc", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProcessTrackModel> findByTaskIdAsc(String tenantId, String taskId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<ProcessTrackModel> items = new ArrayList<>();
        try {
            if (StringUtils.isNotBlank(taskId)) {
                List<ProcessTrack> list = processTrackService.findByTaskIdAsc(taskId);
                items = ItemAdminModelConvertUtil.processTrackList2ModelList(list);
            }
        } catch (Exception e) {
            LOGGER.error("根据任务id获取自定义历程异常：{}", e.getMessage());
        }
        return items;
    }

    /**
     * 获取流程图任务节点信息
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return Y9Result<List < HistoricActivityInstanceModel>>
     */
    @Override
    @GetMapping(value = "/getTaskList", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<List<HistoricActivityInstanceModel>> getTaskList(String tenantId, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return processTrackService.getTaskList(processInstanceId);
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
        Map<String, Object> retMap = new HashMap<>(16);
        retMap.put(UtilConsts.SUCCESS, false);
        List<Map<String, Object>> items;
        try {
            if (StringUtils.isNotBlank(processInstanceId)) {
                items = processTrackService.getListMap(processInstanceId);
                retMap.put(UtilConsts.SUCCESS, true);
                retMap.put("rows", items);
            }
        } catch (Exception e) {
            retMap.put(UtilConsts.SUCCESS, false);
            LOGGER.error("获取历程列表异常：{}", e.getMessage());
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
        Map<String, Object> retMap = new HashMap<>(16);
        retMap.put(UtilConsts.SUCCESS, false);
        List<Map<String, Object>> items;
        try {
            if (StringUtils.isNotBlank(processInstanceId)) {
                items = processTrackService.getListMap4Simple(processInstanceId);
                retMap.put(UtilConsts.SUCCESS, true);
                retMap.put("rows", items);
            }
        } catch (Exception e) {
            retMap.put(UtilConsts.SUCCESS, false);
            LOGGER.error("获取历程列表异常：{}", e.getMessage());
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
        return ItemAdminModelConvertUtil.processTrack2Model(ptTemp);
    }
}
