package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ProcessTrackApi;
import net.risesoft.entity.ProcessTrack;
import net.risesoft.model.itemadmin.HistoricActivityInstanceModel;
import net.risesoft.model.itemadmin.HistoryProcessModel;
import net.risesoft.model.itemadmin.ProcessTrackModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ProcessTrackService;
import net.risesoft.util.ItemAdminModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

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
@RequestMapping(value = "/services/rest/processTrack", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProcessTrackApiImpl implements ProcessTrackApi {

    private final ProcessTrackService processTrackService;

    /**
     * 根据唯一标示删除历程数据
     *
     * @param tenantId 租户id
     * @param id 唯一标识
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @throws Exception 删除导致的异常
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> deleteById(@RequestParam String tenantId, @RequestParam String id) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        processTrackService.deleteById(id);
        return Y9Result.success();
    }

    /**
     * 根据任务id获取自定义历程数据（结束时间倒叙排列）
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return {@code Y9Result<List<ProcessTrackModel>>} 通用请求返回对象 - data 是流程跟踪信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ProcessTrackModel>> findByTaskId(@RequestParam String tenantId, @RequestParam String taskId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<ProcessTrackModel> items = new ArrayList<>();
        try {
            List<ProcessTrack> list = processTrackService.listByTaskId(taskId);
            items = ItemAdminModelConvertUtil.processTrackList2ModelList(list);
        } catch (Exception e) {
            LOGGER.error("根据任务id获取自定义历程异常", e);
        }
        return Y9Result.success(items);
    }

    /**
     * 根据任务id获取自定义历程数据（开始时间正序排列）
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return {@code Y9Result<List<ProcessTrackModel>>} 通用请求返回对象 - data 是流程跟踪信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ProcessTrackModel>> findByTaskIdAsc(@RequestParam String tenantId,
        @RequestParam String taskId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<ProcessTrackModel> items = new ArrayList<>();
        try {
            List<ProcessTrack> list = processTrackService.listByTaskIdAsc(taskId);
            items = ItemAdminModelConvertUtil.processTrackList2ModelList(list);
        } catch (Exception e) {
            LOGGER.error("根据任务id获取自定义历程异常", e);
        }
        return Y9Result.success(items);
    }

    /**
     * 获取流程图任务节点信息
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<List<HistoricActivityInstanceModel>>} 通用请求返回对象 - data 是历史活动实例列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<HistoricActivityInstanceModel>> getTaskList(@RequestParam String tenantId,
        @RequestParam String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return processTrackService.getTaskList(processInstanceId);
    }

    /**
     * 获取流程历程数据列表(包含每个任务节点的特殊操作的历程)
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<List<HistoryProcessModel>>} 通用请求返回对象- data 是历程信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<HistoryProcessModel>> processTrackList(@RequestParam String tenantId,
        @RequestParam String orgUnitId, @RequestParam String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setOrgUnitId(orgUnitId);
        try {
            List<HistoryProcessModel> items = processTrackService.listByProcessInstanceId(processInstanceId);
            return Y9Result.success(items);
        } catch (Exception e) {
            LOGGER.error("获取历程列表异常", e);
            return Y9Result.failure("获取历程列表异常 ");
        }
    }

    /**
     * 获取流程历程信息数据（简单版）
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<List<HistoryProcessModel>>} 通用请求返回对象 - data 是历程信息列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<HistoryProcessModel>> processTrackList4Simple(@RequestParam String tenantId,
        @RequestParam String orgUnitId, @RequestParam String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setOrgUnitId(orgUnitId);
        try {
            List<HistoryProcessModel> items = processTrackService.listByProcessInstanceId4Simple(processInstanceId);
            return Y9Result.success(items);
        } catch (Exception e) {
            LOGGER.error("获取历程列表异常", e);
            return Y9Result.failure("获取历程列表异常 ");
        }
    }

    /**
     * 保存或更新历程数据
     *
     * @param tenantId 租户id
     * @param processTrackModel 实体类对象（ProcessTrackModel）
     * @return {@code Y9Result<ProcessTrackModel>} 通用请求返回对象 - data 是流程跟踪信息
     * @throws Exception 保存或更新导致的异常
     * @since 9.6.6
     */
    @Override
    public Y9Result<ProcessTrackModel> saveOrUpdate(@RequestParam String tenantId,
        @RequestBody ProcessTrackModel processTrackModel) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        ProcessTrack processTrack = new ProcessTrack();
        Y9BeanUtil.copyProperties(processTrackModel, processTrack);
        ProcessTrack ptTemp = processTrackService.saveOrUpdate(processTrack);
        ProcessTrackModel model = new ProcessTrackModel();
        Y9BeanUtil.copyProperties(ptTemp, model);
        return Y9Result.success(model);
    }
}
