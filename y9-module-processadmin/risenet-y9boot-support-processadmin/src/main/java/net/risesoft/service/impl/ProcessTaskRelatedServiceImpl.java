package net.risesoft.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.StringUtils;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.core.ProcessParamApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.model.itemadmin.core.ProcessParamModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Person;
import net.risesoft.service.ProcessTaskRelatedService;
import net.risesoft.y9.configuration.app.y9processadmin.Y9ProcessAdminProperties;
import net.risesoft.y9.json.Y9JsonUtil;

/**
 * @author qinman
 * @date 2024/12/03
 */
@Slf4j
@RequiredArgsConstructor
@Service(value = "processTaskRelatedService")
public class ProcessTaskRelatedServiceImpl implements ProcessTaskRelatedService {

    private final OrgUnitApi orgUnitApi;

    private final PositionApi positionApi;

    private final ProcessParamApi processParamApi;

    private final Y9ProcessAdminProperties y9ProcessAdminProperties;

    /**
     * 微信提醒
     *
     * @param task 任务
     * @param map 流程变量
     * @param local 任务变量
     */
    @Override
    @SuppressWarnings("unchecked")
    public void execute(final DelegateTask task, final Map<String, Object> map, final Map<String, Object> local) {
        try {
            String tenantId = (String)map.get("tenantId");
            String processSerialNumber = (String)map.get(SysVariables.PROCESS_SERIAL_NUMBER);
            ProcessParamModel processParamModel =
                processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
            String documentTitle = processParamModel.getTitle();
            String itemId = processParamModel.getItemId();
            String itemName = processParamModel.getItemName();
            // 收回或者退回产生的任务不进行微信提醒，主要针对串行的收回或者退回，串行时的收回退回是办结所有串行任务，因此产生的新任务无需提醒
            if (local.get(SysVariables.TAKEBACK) != null || local.get(SysVariables.ROLLBACK) != null) {
                return;
            }
            String assignee = task.getAssignee();
            String userId = map.get(SysVariables.TASK_SENDER_ID).toString();
            String send = processParamModel.getSended();
            // 第一步新建产生的任务，不发送提醒
            if (StringUtils.isBlank(send) || UtilConsts.FALSE.equals(send)) {
                return;
            }
            OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, userId).getData();
            if (orgUnit.getOrgType().equals(OrgTypeEnum.POSITION)) {
                List<Person> list = positionApi.listPersonsByPositionId(tenantId, assignee).getData();
                String url = y9ProcessAdminProperties.getWeiXinUrl();
                for (Person p : list) {
                    try {
                        HttpClient client = new HttpClient();
                        client.getParams().setParameter(HttpMethodParams.BUFFER_WARN_TRIGGER_LIMIT, 1024 * 1024 * 10);
                        client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
                        PostMethod method = new PostMethod();
                        method.addParameter("userId", p.getId());
                        method.addParameter("title", documentTitle);
                        method.addParameter("taskSender", orgUnit.getName());
                        method.addParameter("taskName", itemName + "-" + task.getName());
                        method.addParameter("processSerialNumber", processSerialNumber);
                        method.addParameter("processDefinitionKey",
                            task.getProcessDefinitionId().split(SysVariables.COLON)[0]);
                        method.addParameter("processInstanceId", task.getProcessInstanceId());
                        method.addParameter("taskId", task.getId());
                        method.addParameter("itemId", itemId);
                        method.setPath(url);
                        int code = client.executeMethod(method);
                        LOGGER.info("##########################微信接口状态：{}##########################", code);
                        if (code == HttpStatus.SC_OK) {
                            String response =
                                new String(method.getResponseBodyAsString().getBytes(StandardCharsets.UTF_8),
                                    StandardCharsets.UTF_8);
                            LOGGER.info("##########################返回状态：{}##########################", response);
                        }
                    } catch (Exception e) {
                        LOGGER.error("##########################微信提醒时发生异常-userId:{}#", p.getId());
                    }
                }
            } else {// 人员
                HttpClient client = new HttpClient();
                client.getParams().setParameter(HttpMethodParams.BUFFER_WARN_TRIGGER_LIMIT, 1024 * 1024 * 10);
                client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
                PostMethod method = new PostMethod();
                method.addParameter("userId", assignee);
                method.addParameter("title", documentTitle);
                method.addParameter("taskSender", orgUnit.getName());
                method.addParameter("taskName", itemName + "-" + task.getName());
                method.addParameter("processSerialNumber", processSerialNumber);
                method.addParameter("processDefinitionKey", task.getProcessDefinitionId().split(SysVariables.COLON)[0]);
                method.addParameter("processInstanceId", task.getProcessInstanceId());
                method.addParameter("taskId", task.getId());
                method.addParameter("itemId", itemId);
                String url = y9ProcessAdminProperties.getWeiXinUrl();
                method.setPath(url);
                int code = client.executeMethod(method);
                LOGGER.info("##########################微信接口状态：{}##########################", code);
                if (code == HttpStatus.SC_OK) {
                    String response = new String(method.getResponseBodyAsString().getBytes(StandardCharsets.UTF_8),
                        StandardCharsets.UTF_8);
                    Map<String, Object> m = Y9JsonUtil.readValue(response, Map.class);
                    LOGGER.info("##########################返回状态：{}##########################", response);
                    assert m != null;
                    if (UtilConsts.FALSE.equals(m.get(UtilConsts.SUCCESS))) {
                        LOGGER.error("##########微信提醒失败-userId:{}#", assignee);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("##########################微信提醒时发生异常-taskId:{} 错误信息：{}###########################",
                task.getId(), e.getMessage());
        }
    }

}
