package net.risesoft.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.entity.ProcessParam;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.core.ProcessParamService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 流程变量接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/processParam", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProcessParamApiImpl implements ProcessParamApi {

    private final ProcessParamService processParamService;

    /**
     * 根据流程实例id删除流程变量数据
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> deleteByPprocessInstanceId(@RequestParam String tenantId,
        @RequestParam String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        processParamService.deleteByPprocessInstanceId(processInstanceId);
        return Y9Result.success();
    }

    /**
     * 根据流程实例获取流程变量数据
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<ProcessParamModel>} 通用请求返回对象 -data 流程数据对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<ProcessParamModel> findByProcessInstanceId(@RequestParam String tenantId,
        @RequestParam String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
        if (null != processParam) {
            ProcessParamModel pp = new ProcessParamModel();
            Y9BeanUtil.copyProperties(processParam, pp);
            return Y9Result.success(pp);
        }
        return Y9Result.failure("流程参数不存在");
    }

    /**
     * 根据流程编号获取流程变量数据
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<ProcessParamModel>} 通用请求返回对象 -data 流程数据对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<ProcessParamModel> findByProcessSerialNumber(@RequestParam String tenantId,
        @RequestParam String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
        ProcessParamModel pp = null;
        if (null != processParam) {
            pp = new ProcessParamModel();
            Y9BeanUtil.copyProperties(processParam, pp);
        }
        return Y9Result.success(pp);
    }

    /**
     * 保存或更新流程变量数据
     *
     * @param tenantId 租户ID
     * @param processParam 流程数据对象
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> saveOrUpdate(@RequestParam String tenantId, @RequestBody ProcessParamModel processParam) {
        Y9LoginUserHolder.setTenantId(tenantId);
        ProcessParam pp = new ProcessParam();
        Y9BeanUtil.copyProperties(processParam, pp);
        processParamService.saveOrUpdate(pp);
        return Y9Result.success();
    }

    /**
     * 更新定制流程状态
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param isCustomItem 是否定制流程
     * @return{@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> updateCustomItem(@RequestParam String tenantId, @RequestParam String processSerialNumber,
        @RequestParam boolean isCustomItem) {
        Y9LoginUserHolder.setTenantId(tenantId);
        processParamService.updateCustomItem(processSerialNumber, isCustomItem);
        return Y9Result.success();
    }

    @Override
    public Y9Result<Object> initCallActivity(String tenantId, String processSerialNumber, String subProcessSerialNumber,
        String subProcessInstanceId, String itemId, String itemName) {
        Y9LoginUserHolder.setTenantId(tenantId);
        processParamService.initCallActivity(processSerialNumber, subProcessSerialNumber, subProcessInstanceId, itemId,
            itemName);
        return Y9Result.success();
    }
}
