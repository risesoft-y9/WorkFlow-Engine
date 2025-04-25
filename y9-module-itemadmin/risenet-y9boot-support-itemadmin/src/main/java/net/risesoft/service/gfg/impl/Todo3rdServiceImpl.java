package net.risesoft.service.gfg.impl;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.entity.ActRuDetail;
import net.risesoft.entity.ProcessParam;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.entity.TaskRelated;
import net.risesoft.entity.Todo3rd;
import net.risesoft.enums.TaskRelatedEnum;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.Person;
import net.risesoft.repository.jpa.Todo3rdRepository;
import net.risesoft.service.FormDataService;
import net.risesoft.service.ProcessParamService;
import net.risesoft.service.SpmApproveItemService;
import net.risesoft.service.TaskRelatedService;
import net.risesoft.service.gfg.Todo3rdService;
import net.risesoft.util.MD5Util;
import net.risesoft.util.TodoParam;
import net.risesoft.util.TodoResponse;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.RemoteCallUtil;

/**
 * @author : qinman
 * @date : 2025-04-24
 * @since 9.6.8
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class Todo3rdServiceImpl implements Todo3rdService {

    private final Todo3rdRepository todo3rdRepository;

    private final ProcessParamService processParamService;

    private final TaskRelatedService taskRelatedService;

    private final SpmApproveItemService itemService;

    private final FormDataService formDataService;

    private final PositionApi positionApi;

    @Value("${y9.common.todo3rdUrl:http://192.168.50.128:8080/todoService/serviceapi}")
    private String todo3rdUrl;

    private final String APP = "GWQBXTX";

    private final String KEY = "250411144433itAss5bjPA8fm9Qhzc9";

    private final String ADDURL = "/todo/add";

    private final String UPDATEURL = "/todo/update";

    private final String DELETEURL = "/todo/delete";

    @Override
    public void addTodo3rd(ActRuDetail actRuDetail) {
        ProcessParam processParam = processParamService.findByProcessSerialNumber(actRuDetail.getProcessSerialNumber());
        SpmApproveItem item = itemService.findById(processParam.getItemId());
        Todo3rd todo3rd = getTodo3rd(actRuDetail, processParam, 1);
        todo3rd.setUrl(item.getTodoTaskUrlPrefix());
        String timestamp = getFormattedTimestamp(), token = Y9IdGenerator.genId(),
            vcode = MD5Util.md5Encode(timestamp + APP + token + KEY);
        todo3rd.setParams(Y9JsonUtil.writeValueAsString(getTodoParam(timestamp, token, vcode)));
        List<NameValuePair> params = getParams(timestamp, token, vcode);
        TodoResponse todoResponse = RemoteCallUtil.post(todo3rdUrl + ADDURL, params,
            Y9JsonUtil.writeValueAsString(todo3rd), TodoResponse.class);
        if (null == todoResponse || !"success".equals(todoResponse.getType())) {
            todo3rd.setSuccess(Boolean.FALSE);
            LOGGER.error("调用第三方接口失败：接口地址：{},响应信息：{}", todo3rdUrl + ADDURL,
                null == todoResponse ? "接口不通" : todoResponse.getMessage());
        }
        todo3rd.setMessage(null == todoResponse ? "接口不通" : todoResponse.getMessage());
        todo3rd.setParams(Y9JsonUtil.writeValueAsString(getTodoParam(timestamp, token, vcode)));
        todo3rdRepository.save(todo3rd);
    }

    @Override
    public void updateTodo3rd(ActRuDetail actRuDetail) {
        String timestamp = getFormattedTimestamp(), token = Y9IdGenerator.genId(),
            vcode = MD5Util.md5Encode(timestamp + APP + token + KEY);
        List<NameValuePair> params = getParams(timestamp, token, vcode);
        Optional<Todo3rd> todo3rdOptional = todo3rdRepository.findById(actRuDetail.getId());
        ProcessParam processParam = processParamService.findByProcessSerialNumber(actRuDetail.getProcessSerialNumber());
        Todo3rd todo3rd;
        if (todo3rdOptional.isPresent()) {
            todo3rd = todo3rdOptional.get();
            todo3rd.setTitle(processParam.getTitle());
            todo3rd = setUrgent(todo3rd, actRuDetail.getProcessSerialNumber());
        } else {
            todo3rd = getTodo3rd(actRuDetail, processParam, 2);
        }
        TodoResponse todoResponse = RemoteCallUtil.put(todo3rdUrl + UPDATEURL + "/" + actRuDetail.getId(), params,
            Y9JsonUtil.writeValueAsString(todo3rd), TodoResponse.class);
        if (null != todoResponse && "success".equals(todoResponse.getType())) {
            todo3rd.setSuccess(Boolean.TRUE);
        } else {
            LOGGER.error("调用第三方接口失败：接口地址：{},响应信息：{}", todo3rdUrl + ADDURL,
                null == todoResponse ? "接口不通" : todoResponse.getMessage());
            todo3rd.setSuccess(Boolean.FALSE);
        }
        todo3rd.setMessage(null == todoResponse ? "接口不通" : todoResponse.getMessage());
        todo3rd.setParams(Y9JsonUtil.writeValueAsString(getTodoParam(timestamp, token, vcode)));
        todo3rdRepository.save(todo3rd);
    }

    @Override
    public void deleteTodo3rd(ActRuDetail actRuDetail) {
        ProcessParam processParam = processParamService.findByProcessSerialNumber(actRuDetail.getProcessSerialNumber());
        String timestamp = getFormattedTimestamp(), token = Y9IdGenerator.genId(),
            vcode = MD5Util.md5Encode(timestamp + APP + token + KEY);
        List<NameValuePair> params = getParams(timestamp, token, vcode);
        TodoResponse todoResponse =
            RemoteCallUtil.delete(todo3rdUrl + DELETEURL + "/" + actRuDetail.getId(), params, TodoResponse.class);
        Optional<Todo3rd> todo3rdOptional = todo3rdRepository.findById(actRuDetail.getId());
        if (null != todoResponse && "success".equals(todoResponse.getType())) {
            if (todo3rdOptional.isPresent()) {
                todo3rdRepository.deleteById(actRuDetail.getId());
            } else {
                LOGGER.error("本系统该待办不存在{}:{}", actRuDetail.getAssigneeName(), actRuDetail.getId());
            }
        } else {
            LOGGER.error("调用第三方接口失败：接口地址：{},响应信息：{}", todo3rdUrl + ADDURL,
                null == todoResponse ? "接口不通" : todoResponse.getMessage());
            Todo3rd todo3rd = todo3rdOptional.orElseGet(() -> getTodo3rd(actRuDetail, processParam, 3));
            todo3rd.setSuccess(Boolean.FALSE);
            todo3rd.setMessage(null == todoResponse ? "接口不通" : todoResponse.getMessage());
            todo3rd.setParams(Y9JsonUtil.writeValueAsString(getTodoParam(timestamp, token, vcode)));
            todo3rdRepository.save(todo3rd);
        }
    }

    private List<NameValuePair> getParams(String timestamp, String token, String vcode) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new NameValuePair("timestamp", timestamp));
        params.add(new NameValuePair("token", token));
        params.add(new NameValuePair("app", APP));
        params.add(new NameValuePair("key", KEY));
        params.add(new NameValuePair("vcode", vcode));
        return params;
    }

    private TodoParam getTodoParam(String timestamp, String token, String vcode) {
        return TodoParam.builder().timestamp(timestamp).app(APP).token(token).key(KEY).vcode(vcode).build();
    }

    private Todo3rd getTodo3rd(ActRuDetail actRuDetail, ProcessParam processParam, Integer optType) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Person> receivePersonList =
            positionApi.listPersonsByPositionId(Y9LoginUserHolder.getTenantId(), actRuDetail.getAssignee()).getData();
        String receiveUserId = "", receiveUserName = "";
        if (!receivePersonList.isEmpty()) {
            receiveUserId = receivePersonList.get(0).getId();
            receiveUserName = receivePersonList.get(0).getName();
        }
        List<Person> sendPersonList =
            positionApi.listPersonsByPositionId(Y9LoginUserHolder.getTenantId(), actRuDetail.getSendUserId()).getData();
        String sendUserId = "", sendUserName = "";
        if (!sendPersonList.isEmpty()) {
            sendUserId = receivePersonList.get(0).getId();
            sendUserName = receivePersonList.get(0).getName();
        }
        List<TaskRelated> taskRelatedList = taskRelatedService.findByTaskId(actRuDetail.getTaskId());
        Optional<TaskRelated> taskRelatedOptional = taskRelatedList.stream()
            .filter(tr -> tr.getInfoType().equals(TaskRelatedEnum.BANWENSHUOMING.getValue())).findFirst();
        // 紧急程度
        Map<String, Object> map =
            formDataService.getData4TableAlias(actRuDetail.getProcessSerialNumber(), "fw").getData();
        AtomicReference<Integer> urgentAtomicReference = new AtomicReference<>(0);
        map.forEach((k, v) -> {
            if ("jjcd".equalsIgnoreCase(k)) {
                if (null != v && !"".equalsIgnoreCase(v.toString())) {
                    urgentAtomicReference.set(Integer.parseInt(v.toString()));
                }
            }
        });
        int urgent = 0;
        String urgentText = "普通";
        switch (urgentAtomicReference.get()) {
            case 0:
                urgentText = "普通";
                break;
            case 1:
                urgent = 3;
                urgentText = "特急";
                break;
            case 2:
                urgent = 2;
                urgentText = "加急";
        }
        return Todo3rd.builder().id(actRuDetail.getId()).appCode(processParam.getSystemName())
            .appName(processParam.getSystemCnName()).title(processParam.getTitle())
            .content(taskRelatedOptional.isPresent() ? taskRelatedOptional.get().getMsgContent() : "")
            .receiveUserId(receiveUserId).receiveUserName(receiveUserName).receiveDeptId(actRuDetail.getDeptId())
            .receiveDeptName(actRuDetail.getDeptName()).receiveTime(sdf.format(actRuDetail.getCreateTime()))
            .sendUserId(sendUserId).sendUserName(sendUserName).sendDeptId(actRuDetail.getDeptId())
            .sendDeptName(actRuDetail.getSendDeptName()).optType(optType).urgent(urgent).urgentText(urgentText)
            .success(Boolean.TRUE).build();
    }

    private String getFormattedTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        return now.format(formatter);
    }

    private Todo3rd setUrgent(Todo3rd todo3rd, String processSerialNumber) {
        Map<String, Object> map = formDataService.getData4TableAlias(processSerialNumber, "fw").getData();
        AtomicReference<Integer> urgent = new AtomicReference<>(0);
        map.forEach((k, v) -> {
            if ("jjcd".equalsIgnoreCase(k)) {
                if (null != v && !"".equalsIgnoreCase(v.toString())) {
                    urgent.set(Integer.parseInt(v.toString()));
                }
            }
        });
        switch (urgent.get()) {
            case 0:
                todo3rd.setUrgent(0);
                todo3rd.setUrgentText("普通");
                break;
            case 1:
                todo3rd.setUrgent(3);
                todo3rd.setUrgentText("特急");
                break;
            case 2:
                todo3rd.setUrgent(2);
                todo3rd.setUrgentText("加急");
        }
        return todo3rd;
    }
}
