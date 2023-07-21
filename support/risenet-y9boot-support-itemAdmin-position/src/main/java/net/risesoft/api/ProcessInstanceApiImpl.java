package net.risesoft.api;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.ProcessInstanceApi;
import net.risesoft.api.org.PositionApi;
import net.risesoft.model.Position;
import net.risesoft.model.itemadmin.ProcessInstanceDetailsModel;
import net.risesoft.service.ProcessInstanceDetailsService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 协作状态接口
 *
 * @author zhangchongjie
 * @date 2023/02/06
 */
@RestController
@RequestMapping(value = "/services/rest/processInstance")
public class ProcessInstanceApiImpl implements ProcessInstanceApi {

    @Autowired
    private ProcessInstanceDetailsService processInstanceDetailsService;

    @Autowired
    private PositionApi positionApi;

    @Override
    @PostMapping(value = "/deleteProcessInstance", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteProcessInstance(String tenantId, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return processInstanceDetailsService.deleteProcessInstance(processInstanceId);
    }

    @Override
    @GetMapping(value = "/processInstanceList", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> processInstanceList(String tenantId, String userId, String title, int page, int rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return processInstanceDetailsService.processInstanceList(userId, title, page, rows);
    }

    @Override
    @PostMapping(value = "/saveProcessInstanceDetails", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean saveProcessInstanceDetails(String tenantId, @RequestBody ProcessInstanceDetailsModel model) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return processInstanceDetailsService.save(model);
    }

    @Override
    @PostMapping(value = "/updateProcessInstanceDetails", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean updateProcessInstanceDetails(String tenantId, String assigneeId, String processInstanceId, String taskId, String itembox, Date endTime) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionApi.getPosition(tenantId, assigneeId);
        Y9LoginUserHolder.setPosition(position);
        return processInstanceDetailsService.updateProcessInstanceDetails(processInstanceId, taskId, itembox, endTime);
    }
}
