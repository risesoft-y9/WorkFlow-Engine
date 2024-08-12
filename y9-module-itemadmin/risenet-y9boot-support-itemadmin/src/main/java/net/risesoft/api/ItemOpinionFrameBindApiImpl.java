package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.ItemOpinionFrameBindApi;
import net.risesoft.entity.ItemOpinionFrameBind;
import net.risesoft.entity.OpinionFrame;
import net.risesoft.model.itemadmin.ItemOpinionFrameBindModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.OpinionFrameService;
import net.risesoft.service.config.ItemOpinionFrameBindService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 意见框绑定接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/itemOpinionFrameBind", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemOpinionFrameBindApiImpl implements ItemOpinionFrameBindApi {

    private final ItemOpinionFrameBindService itemOpinionFrameBindService;

    private final OpinionFrameService opinionFrameService;

    /**
     * 根据事项id获取所有绑定意见框列表
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @return {@code Y9Result<List<ItemOpinionFrameBindModel>>} 通用请求返回对象 - data 是绑定意见框列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ItemOpinionFrameBindModel>> findByItemId(@RequestParam String tenantId,
        @RequestParam String itemId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<ItemOpinionFrameBind> list = itemOpinionFrameBindService.listByItemId(itemId);
        List<ItemOpinionFrameBindModel> modelList = new ArrayList<>();
        for (ItemOpinionFrameBind o : list) {
            ItemOpinionFrameBindModel model = new ItemOpinionFrameBindModel();
            Y9BeanUtil.copyProperties(o, model);
            OpinionFrame opinionFrame = opinionFrameService.getByMark(o.getOpinionFrameMark());
            model.setOpinionFrameName(opinionFrame == null ? "意见框不存在" : opinionFrame.getName());
            modelList.add(model);
        }
        return Y9Result.success(modelList);
    }

    /**
     * 根据事项id和流程定义id获取所有绑定意见框列表
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @return {@code Y9Result<List<ItemOpinionFrameBindModel>>} 通用请求返回对象 - data 是绑定意见框列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ItemOpinionFrameBindModel>> findByItemIdAndProcessDefinitionId(@RequestParam String tenantId,
        @RequestParam String itemId, @RequestParam String processDefinitionId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<ItemOpinionFrameBind> list =
            itemOpinionFrameBindService.listByItemIdAndProcessDefinitionId(itemId, processDefinitionId);
        List<ItemOpinionFrameBindModel> modelList = new ArrayList<>();
        for (ItemOpinionFrameBind o : list) {
            ItemOpinionFrameBindModel model = new ItemOpinionFrameBindModel();
            Y9BeanUtil.copyProperties(o, model);
            OpinionFrame opinionFrame = opinionFrameService.getByMark(o.getOpinionFrameMark());
            model.setOpinionFrameName(opinionFrame == null ? "意见框不存在" : opinionFrame.getName());
            modelList.add(model);
        }
        return Y9Result.success(modelList);
    }

    /**
     * 根据事项id和任务id获取绑定意见框列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @return {@code Y9Result<<ItemOpinionFrameBindModel>>} 通用请求返回对象 - data 是绑定意见框列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ItemOpinionFrameBindModel>> findByItemIdAndProcessDefinitionIdAndTaskDefKey(
        @RequestParam String tenantId, @RequestParam String userId, @RequestParam String itemId,
        @RequestParam String processDefinitionId, String taskDefKey) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<ItemOpinionFrameBind> list = itemOpinionFrameBindService
            .listByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, processDefinitionId, taskDefKey);
        List<ItemOpinionFrameBindModel> modelList = new ArrayList<>();
        for (ItemOpinionFrameBind o : list) {
            ItemOpinionFrameBindModel model = new ItemOpinionFrameBindModel();
            Y9BeanUtil.copyProperties(o, model);
            OpinionFrame opinionFrame = opinionFrameService.getByMark(o.getOpinionFrameMark());
            model.setOpinionFrameName(opinionFrame == null ? "意见框不存在" : opinionFrame.getName());
            modelList.add(model);
        }
        return Y9Result.success(modelList);
    }

    /**
     * 根据事项id和任务id获取绑定的意见框（包含角色信息）
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @return {@code Y9Result<List<ItemOpinionFrameBindModel>>} 通用请求返回对象 - data 是绑定意见框列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ItemOpinionFrameBindModel>> findByItemIdAndProcessDefinitionIdAndTaskDefKeyContainRole(
        @RequestParam String tenantId, @RequestParam String userId, @RequestParam String itemId,
        @RequestParam String processDefinitionId, String taskDefKey) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<ItemOpinionFrameBind> list = itemOpinionFrameBindService
            .listByItemIdAndProcessDefinitionIdAndTaskDefKeyContainRole(itemId, processDefinitionId, taskDefKey);
        List<ItemOpinionFrameBindModel> modelList = new ArrayList<>();
        for (ItemOpinionFrameBind o : list) {
            ItemOpinionFrameBindModel model = new ItemOpinionFrameBindModel();
            Y9BeanUtil.copyProperties(o, model);
            OpinionFrame opinionFrame = opinionFrameService.getByMark(o.getOpinionFrameMark());
            model.setOpinionFrameName(opinionFrame == null ? "意见框不存在" : opinionFrame.getName());
            modelList.add(model);
        }
        return Y9Result.success(modelList);
    }
}
