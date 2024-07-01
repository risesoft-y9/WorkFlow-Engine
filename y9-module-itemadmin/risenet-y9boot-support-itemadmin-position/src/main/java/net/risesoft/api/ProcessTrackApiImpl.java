package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.position.ProcessTrack4PositionApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.entity.ProcessTrack;
import net.risesoft.model.itemadmin.HistoricActivityInstanceModel;
import net.risesoft.model.itemadmin.HistoryProcessModel;
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
@RequestMapping(value = "/services/rest/processTrack4Position", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProcessTrackApiImpl implements ProcessTrack4PositionApi {

    private final ProcessTrackService processTrackService;

    private final PositionApi positionManager;

    /**
     * 根据唯一标示删除历程数据
     *
     * @param tenantId 租户id
     * @param id 唯一标识
     * @return Y9Result<Object>
     * @throws Exception Exception
     */
    @Override
    public Y9Result<Object> deleteById(String tenantId, String id) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        processTrackService.deleteById(id);
        return Y9Result.success();
    }

    /**
     * 根据任务id获取自定义历程
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return Y9Result<List<ProcessTrackModel>>
     */
    @Override
    public Y9Result<List<ProcessTrackModel>> findByTaskId(String tenantId, String taskId) {
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
        return Y9Result.success(items);
    }

    /**
     * 根据任务id获取自定义历程
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return Y9Result<List<ProcessTrackModel>>
     */
    @Override
    public Y9Result<List<ProcessTrackModel>> findByTaskIdAsc(String tenantId, String taskId) {
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
        return Y9Result.success(items);
    }

    /**
     * 获取流程图任务节点信息
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return Y9Result<List < HistoricActivityInstanceModel>>
     */
    @Override
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
     * @return Y9Result<List<HistoryProcessModel>>
     */
    @Override
    public Y9Result<List<HistoryProcessModel>> processTrackList(String tenantId, String positionId,
        String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        try {
            if (StringUtils.isNotBlank(processInstanceId)) {
                List<HistoryProcessModel> items = processTrackService.getListMap(processInstanceId);
                return Y9Result.success(items);
            }
            return Y9Result.failure("流程实例id为空 ");
        } catch (Exception e) {
            LOGGER.error("获取历程列表异常：{}", e.getMessage());
            return Y9Result.failure("获取历程列表异常 ");
        }
    }

    /**
     * 获取历程信息
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param processInstanceId 流程实例id
     * @return Y9Result<List<HistoryProcessModel>>
     */
    @Override
    public Y9Result<List<HistoryProcessModel>> processTrackList4Simple(String tenantId, String positionId,
        String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);

        try {
            if (StringUtils.isNotBlank(processInstanceId)) {
                List<HistoryProcessModel> items = processTrackService.getListMap4Simple(processInstanceId);
                return Y9Result.success(items);
            }
            return Y9Result.failure("流程实例id为空 ");
        } catch (Exception e) {
            LOGGER.error("获取历程列表异常：{}", e.getMessage());
            return Y9Result.failure("获取历程列表异常 ");
        }

    }

    /**
     * 保存或更新历程
     *
     * @param tenantId 租户id
     * @return processTrackModel 实体类对象
     * @return Y9Result<ProcessTrackModel>
     * @throws Exception Exception
     */
    @Override
    public Y9Result<ProcessTrackModel> saveOrUpdate(String tenantId, @RequestBody ProcessTrackModel processTrackModel)
        throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        ProcessTrack processTrack = ItemAdminModelConvertUtil.processTrackModel2ProcessTrack(processTrackModel);
        ProcessTrack ptTemp = processTrackService.saveOrUpdate(processTrack);
        return Y9Result.success(ItemAdminModelConvertUtil.processTrack2Model(ptTemp));
    }
}
