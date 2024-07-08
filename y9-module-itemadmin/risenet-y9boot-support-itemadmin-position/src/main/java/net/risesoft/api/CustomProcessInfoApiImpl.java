package net.risesoft.api;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.CustomProcessInfoApi;
import net.risesoft.entity.CustomProcessInfo;
import net.risesoft.model.itemadmin.CustomProcessInfoModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.CustomProcessInfoService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 定制流程接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/customProcessInfo", produces = MediaType.APPLICATION_JSON_VALUE)
public class CustomProcessInfoApiImpl implements CustomProcessInfoApi {

    private final CustomProcessInfoService customProcessInfoService;

    /**
     * 获取当前运行任务的下一个节点
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<CustomProcessInfoModel>} 通用请求返回对象 - data 是自定义流程信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<CustomProcessInfoModel> getCurrentTaskNextNode(@RequestParam String tenantId,
        @RequestParam String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        CustomProcessInfo info = customProcessInfoService.getCurrentTaskNextNode(processSerialNumber);
        CustomProcessInfoModel model = null;
        if (info != null) {
            model = new CustomProcessInfoModel();
            Y9BeanUtil.copyProperties(info, model);
        }
        return Y9Result.success(model);
    }

    /**
     * 保存流程定制信息
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param taskList 任务列表
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> saveOrUpdate(@RequestParam String tenantId, @RequestParam String itemId,
        @RequestParam String processSerialNumber, @RequestBody List<Map<String, Object>> taskList) {
        Y9LoginUserHolder.setTenantId(tenantId);
        customProcessInfoService.saveOrUpdate(itemId, processSerialNumber, taskList);
        return Y9Result.success();
    }

    /**
     * 更新当前运行节点
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> updateCurrentTask(@RequestParam String tenantId, @RequestParam String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        customProcessInfoService.updateCurrentTask(processSerialNumber);
        return Y9Result.success();
    }

}
