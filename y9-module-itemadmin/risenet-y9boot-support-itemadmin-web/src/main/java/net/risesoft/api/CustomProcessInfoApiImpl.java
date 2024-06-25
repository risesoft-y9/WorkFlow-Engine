package net.risesoft.api;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.CustomProcessInfoApi;
import net.risesoft.entity.CustomProcessInfo;
import net.risesoft.model.itemadmin.CustomProcessInfoModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.CustomProcessInfoService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequestMapping(value = "/services/rest/customProcessInfo")
public class CustomProcessInfoApiImpl implements CustomProcessInfoApi {

    @Autowired
    private CustomProcessInfoService customProcessInfoService;

    @Override
    @GetMapping(value = "/getCurrentTaskNextNode", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<CustomProcessInfoModel> getCurrentTaskNextNode(String tenantId, String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        CustomProcessInfo info = customProcessInfoService.getCurrentTaskNextNode(processSerialNumber);
        CustomProcessInfoModel model = null;
        if (info != null) {
            model = new CustomProcessInfoModel();
            Y9BeanUtil.copyProperties(info, model);
        }
        return Y9Result.success(model);
    }

    @Override
    @PostMapping(value = "/saveOrUpdate", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> saveOrUpdate(String tenantId, String itemId, String processSerialNumber,
        List<Map<String, Object>> taskList) {
        Y9LoginUserHolder.setTenantId(tenantId);
        customProcessInfoService.saveOrUpdate(itemId, processSerialNumber, taskList);
        return Y9Result.success();
    }

    @Override
    @PostMapping(value = "/updateCurrentTask", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> updateCurrentTask(String tenantId, String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        customProcessInfoService.updateCurrentTask(processSerialNumber);
        return Y9Result.success();
    }

}
