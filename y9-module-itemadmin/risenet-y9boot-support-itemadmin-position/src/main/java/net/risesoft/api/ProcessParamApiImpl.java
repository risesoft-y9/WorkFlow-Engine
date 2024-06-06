package net.risesoft.api;

import lombok.RequiredArgsConstructor;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.entity.ProcessParam;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.service.ProcessParamService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 流程变量接口
 * 
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/processParam")
public class ProcessParamApiImpl implements ProcessParamApi {

    private final ProcessParamService processParamService;

    /**
     * 根据流程实例id删除流程变量
     *
     * @param tenantId 租户Id
     * @param processInstanceId 流程实例Id
     */
    @Override
    @PostMapping(value = "/deleteByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteByPprocessInstanceId(String tenantId, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        processParamService.deleteByPprocessInstanceId(processInstanceId);
    }

    /**
     * 根据流程实例查找流程数据
     *
     * @param tenantId 租户Id
     * @param processInstanceId 流程实例Id
     * @return ProcessParamModel
     */
    @Override
    @GetMapping(value = "/findByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public ProcessParamModel findByProcessInstanceId(String tenantId, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
        ProcessParamModel pp = null;
        if (null != processParam) {
            pp = new ProcessParamModel();
            Y9BeanUtil.copyProperties(processParam, pp);
        }
        return pp;
    }

    /**
     * 根据流程序列号查找流程数据
     *
     * @param tenantId 租户Id
     * @param processSerialNumber 流程编号
     * @return ProcessParamModel
     */
    @Override
    @GetMapping(value = "/findByProcessSerialNumber", produces = MediaType.APPLICATION_JSON_VALUE)
    public ProcessParamModel findByProcessSerialNumber(String tenantId, String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
        ProcessParamModel pp = null;
        if (null != processParam) {
            pp = new ProcessParamModel();
            Y9BeanUtil.copyProperties(processParam, pp);
        }
        return pp;
    }

    /**
     * 保存或更新流程数据
     *
     * @param tenantId 租户ID
     * @param processParam 流程数据对象
     */
    @Override
    @PostMapping(value = "/saveOrUpdate", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public void saveOrUpdate(String tenantId, @RequestBody ProcessParamModel processParam) {
        Y9LoginUserHolder.setTenantId(tenantId);
        ProcessParam pp = new ProcessParam();
        Y9BeanUtil.copyProperties(processParam, pp);
        processParamService.saveOrUpdate(pp);
    }

    /**
     * 更新定制流程状态
     *
     * @param tenantId 租户ID
     * @param processSerialNumber 流程编号
     * @param b 状态
     */
    @Override
    @PostMapping(value = "/updateCustomItem", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateCustomItem(String tenantId, String processSerialNumber, boolean b) {
        Y9LoginUserHolder.setTenantId(tenantId);
        processParamService.updateCustomItem(processSerialNumber, b);
    }
}
