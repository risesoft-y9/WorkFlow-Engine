package net.risesoft.api;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.CustomProcessInfoApi;
import net.risesoft.entity.CustomProcessInfo;
import net.risesoft.model.itemadmin.CustomProcessInfoModel;
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
@RequestMapping(value = "/services/rest/customProcessInfo")
public class CustomProcessInfoApiImpl implements CustomProcessInfoApi {

    @Autowired
    private CustomProcessInfoService customProcessInfoService;

    /**
     * 获取当前运行任务的下一个节点
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return CustomProcessInfoModel
     */
    @Override
    @GetMapping(value = "/getCurrentTaskNextNode", produces = MediaType.APPLICATION_JSON_VALUE)
    public CustomProcessInfoModel getCurrentTaskNextNode(String tenantId, String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        CustomProcessInfo info = customProcessInfoService.getCurrentTaskNextNode(processSerialNumber);
        CustomProcessInfoModel model = null;
        if (info != null) {
            model = new CustomProcessInfoModel();
            Y9BeanUtil.copyProperties(info, model);
        }
        return model;
    }

    /**
     * 保存流程定制信息
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param taskList 任务列表
     * @return boolean
     */
    @Override
    @PostMapping(value = "/saveOrUpdate", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean saveOrUpdate(String tenantId, String itemId, String processSerialNumber, @RequestBody List<Map<String, Object>> taskList) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return customProcessInfoService.saveOrUpdate(itemId, processSerialNumber, taskList);
    }

    /**
     * 更新当前运行节点
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return boolean
     */
    @Override
    @PostMapping(value = "/updateCurrentTask", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean updateCurrentTask(String tenantId, String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return customProcessInfoService.updateCurrentTask(processSerialNumber);
    }

}
