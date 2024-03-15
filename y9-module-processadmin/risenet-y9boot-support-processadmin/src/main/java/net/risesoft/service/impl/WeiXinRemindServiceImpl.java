package net.risesoft.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.StringUtils;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.Position;
import net.risesoft.service.WeiXinRemindService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.json.Y9JsonUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Service(value = "weiXinRemindService")
@Slf4j
public class WeiXinRemindServiceImpl implements WeiXinRemindService {

    @Autowired
    private PersonApi personManager;

    @Autowired
    private PositionApi positionApi;

    @Autowired
    private ProcessParamApi processParamManager;

    @Autowired
    private Y9Properties y9Conf;

    /**
     * 微信提醒
     */
    /**
     * Description:
     * 
     * @param task
     * @param map
     * @param local
     */
    @Override
    @SuppressWarnings("unchecked")
    public void weiXinRemind(final DelegateTask task, final Map<String, Object> map, final Map<String, Object> local) {
        Boolean weiXinSwitch = y9Conf.getApp().getProcessAdmin().getWeiXinSwitch();
        if (!weiXinSwitch) {
            LOGGER.info("######################微信提醒开关已关闭,如需微信提醒请更改配置文件######################");
            return;
        }
        try {
            String tenantId = (String)map.get("tenantId");
            String processSerialNumber = (String)map.get(SysVariables.PROCESSSERIALNUMBER);
            ProcessParamModel processParamModel =
                processParamManager.findByProcessSerialNumber(tenantId, processSerialNumber);
            String documentTitle = processParamModel.getTitle();
            String itemId = processParamModel.getItemId();
            String itemName = processParamModel.getItemName();
            // 收回或者退回产生的任务不进行微信提醒，主要针对串行的收回或者退回，串行时的收回退回是办结所有串行任务，因此产生的新任务无需提醒
            if (local.get(SysVariables.TAKEBACK) != null || local.get(SysVariables.ROLLBACK) != null) {
                return;
            }
            String assignee = task.getAssignee();
            String userId = map.get(SysVariables.TASKSENDERID).toString();
            String sended = processParamModel.getSended();
            // 第一步新建产生的任务，不发送提醒
            if (StringUtils.isBlank(sended) || UtilConsts.FALSE.equals(sended)) {
                return;
            }
            Person person = personManager.get(tenantId, userId).getData();
            if (person == null || StringUtils.isBlank(person.getId())) {
                List<Person> list = positionApi.listPersonsByPositionId(tenantId, assignee).getData();
                Position position = positionApi.get(tenantId, userId).getData();
                String url = y9Conf.getApp().getProcessAdmin().getWeiXinUrl();
                for (Person p : list) {
                    try {
                        HttpClient client = new HttpClient();
                        client.getParams().setParameter(HttpMethodParams.BUFFER_WARN_TRIGGER_LIMIT, 1024 * 1024 * 10);
                        client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
                        PostMethod method = new PostMethod();
                        method.addParameter("userId", p.getId());
                        method.addParameter("title", documentTitle);
                        method.addParameter("taskSender", position.getName());
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
                            String response = new String(method.getResponseBodyAsString().getBytes("UTF-8"), "UTF-8");
                            LOGGER.info("##########################返回状态：{}##########################", response);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {// 人员
                HttpClient client = new HttpClient();
                client.getParams().setParameter(HttpMethodParams.BUFFER_WARN_TRIGGER_LIMIT, 1024 * 1024 * 10);
                client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
                PostMethod method = new PostMethod();
                method.addParameter("userId", assignee);
                method.addParameter("title", documentTitle);
                method.addParameter("taskSender", person.getName());
                method.addParameter("taskName", itemName + "-" + task.getName());
                method.addParameter("processSerialNumber", processSerialNumber);
                method.addParameter("processDefinitionKey", task.getProcessDefinitionId().split(SysVariables.COLON)[0]);
                method.addParameter("processInstanceId", task.getProcessInstanceId());
                method.addParameter("taskId", task.getId());
                method.addParameter("itemId", itemId);
                String url = y9Conf.getApp().getProcessAdmin().getWeiXinUrl();
                method.setPath(url);
                int code = client.executeMethod(method);
                LOGGER.info("##########################微信接口状态：{}##########################", code);
                if (code == HttpStatus.SC_OK) {
                    String response = new String(method.getResponseBodyAsString().getBytes("UTF-8"), "UTF-8");
                    Map<String, Object> m = Y9JsonUtil.readValue(response, Map.class);
                    LOGGER.info("##########################返回状态：{}##########################", response);
                    if (UtilConsts.TRUE.equals(m.get(UtilConsts.SUCCESS))) {
                        return;
                    } else {
                        return;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warn("##########################微信提醒时发生异常-taskId:{}##########################", task.getId());
            e.printStackTrace();
        }
    }

}
