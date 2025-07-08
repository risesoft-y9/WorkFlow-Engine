package net.risesoft.service.gfg.impl;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import javax.transaction.Transactional;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.entity.ActRuDetail;
import net.risesoft.entity.DocumentCopy;
import net.risesoft.entity.Item;
import net.risesoft.entity.ProcessParam;
import net.risesoft.entity.TaskRelated;
import net.risesoft.entity.Todo3rd;
import net.risesoft.enums.TaskRelatedEnum;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.Department;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Person;
import net.risesoft.repository.jpa.Todo3rdRepository;
import net.risesoft.service.FormDataService;
import net.risesoft.service.ItemService;
import net.risesoft.service.ProcessParamService;
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
@EnableAsync
@RequiredArgsConstructor
public class Todo3rdServiceImpl implements Todo3rdService {

    private final Todo3rdRepository todo3rdRepository;

    private final ProcessParamService processParamService;

    private final TaskRelatedService taskRelatedService;

    private final ItemService itemService;

    private final FormDataService formDataService;

    private final PositionApi positionApi;

    private final OrgUnitApi orgUnitApi;
    private final String APP = "GWQBXTX";
    private final String KEY = "250411144433itAss5bjPA8fm9Qhzc9";
    private final String ADDURL = "/todo/add";
    private final String UPDATEURL = "/todo/update";
    private final String DELETEURL = "/todo/delete";
    @Value("${y9.common.todo3rdUrl:http://192.168.50.128:8080/todoService/serviceapi}")
    private String todo3rdUrl;

    @Async
    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void addTodo3rd(ActRuDetail actRuDetail) {
        ProcessParam processParam = processParamService.findByProcessSerialNumber(actRuDetail.getProcessSerialNumber());
        Item item = itemService.findById(processParam.getItemId());
        Todo3rd todo3rd = getTodo3rd(actRuDetail, processParam, 1);
        Map<String, Object> fwFormDataMap =
            formDataService.getData4TableAlias(actRuDetail.getProcessSerialNumber(), "fw").getData();
        String lsh = (String)fwFormDataMap.getOrDefault("lsh", "暂无流水号");
        String url = item.getTodoTaskUrlPrefix() + "?itembox=todo&taskId=" + actRuDetail.getTaskId() + "&taskName="
            + actRuDetail.getTaskDefName() + "&lsh=" + lsh + "&userName=" + actRuDetail.getAssigneeName()
            + "&actRuDetailId=" + actRuDetail.getId();
        todo3rd.setUrl(url);
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

    @Async
    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void addTodo3rd(DocumentCopy documentCopy) {
        ProcessParam processParam =
            processParamService.findByProcessSerialNumber(documentCopy.getProcessSerialNumber());
        List<Person> personList =
            positionApi.listPersonsByPositionId(Y9LoginUserHolder.getTenantId(), documentCopy.getUserId()).getData();
        List<Todo3rd> todo3rdList = todo3rdRepository.findByProcessSerialNumberAndReceiveUserIdAndTodoType(
            documentCopy.getProcessSerialNumber(), personList.get(0).getId(), 2);
        Todo3rd todo3rd;
        boolean add = false, update = false;
        if (todo3rdList.isEmpty()) {
            add = true;
            todo3rd = getTodo3rd(documentCopy, processParam, 1);
        } else {
            todo3rd = todo3rdList.get(0);
            Integer optType = todo3rd.getOptType();
            switch (optType) {
                case 1:
                    if (todo3rd.isSuccess()) {
                        LOGGER.info("{}已存在传签待办：标题：{}", documentCopy.getUserName(), processParam.getTitle());
                    } else {
                        add = true;
                    }
                    break;
                case 2:
                    if (todo3rd.isSuccess()) {
                        LOGGER.info("{}已存在传签待办：标题：{}", documentCopy.getUserName(), processParam.getTitle());
                    } else {
                        // 更新失败，重新更新
                        update = true;
                        todo3rd.setTitle(processParam.getTitle());
                        todo3rd = setUrgent(todo3rd, documentCopy.getProcessSerialNumber());
                    }
                    break;
                case 3:
                    if (!todo3rd.isSuccess()) {
                        // 删除失败，这个时候又要新增，重新更新待办
                        update = true;
                        todo3rd.setOptType(2);
                    }
                    break;
            }
        }
        String timestamp = getFormattedTimestamp(), token = Y9IdGenerator.genId(),
            vcode = MD5Util.md5Encode(timestamp + APP + token + KEY);
        todo3rd.setParams(Y9JsonUtil.writeValueAsString(getTodoParam(timestamp, token, vcode)));
        List<NameValuePair> params = getParams(timestamp, token, vcode);
        TodoResponse todoResponse = null;
        String url = "";
        if (add) {
            url = todo3rdUrl + ADDURL;
            Item item = itemService.findById(processParam.getItemId());
            Map<String, Object> fwFormDataMap =
                formDataService.getData4TableAlias(documentCopy.getProcessSerialNumber(), "fw").getData();
            String lsh = (String)fwFormDataMap.getOrDefault("lsh", "暂无流水号");
            todo3rd.setUrl(item.getTodoTaskUrlPrefix() + "?itembox=copy&lsh=" + lsh + "&processSerialNumber="
                + documentCopy.getProcessSerialNumber());
            todoResponse = RemoteCallUtil.post(url, params, Y9JsonUtil.writeValueAsString(todo3rd), TodoResponse.class);
        }
        if (update) {
            url = todo3rdUrl + UPDATEURL;
            todoResponse = RemoteCallUtil.put(url, params, Y9JsonUtil.writeValueAsString(todo3rd), TodoResponse.class);
        }
        if (null != todoResponse && "success".equals(todoResponse.getType())) {
            todo3rd.setSuccess(Boolean.TRUE);
        } else {
            todo3rd.setSuccess(Boolean.FALSE);
            LOGGER.error("调用第三方接口失败：接口地址：{},响应信息：{}", url, null == todoResponse ? "接口不通" : todoResponse.getMessage());
        }
        todo3rd.setMessage(null == todoResponse ? "接口不通" : todoResponse.getMessage());
        todo3rd.setParams(Y9JsonUtil.writeValueAsString(getTodoParam(timestamp, token, vcode)));
        todo3rdRepository.save(todo3rd);
    }

    @Async
    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
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

    @Async
    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
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
            LOGGER.error("调用第三方接口失败：接口地址：{},响应信息：{}", todo3rdUrl + DELETEURL,
                null == todoResponse ? "接口不通" : todoResponse.getMessage());
            Todo3rd todo3rd;
            if (todo3rdOptional.isPresent()) {
                todo3rd = todo3rdOptional.get();
                todo3rd.setOptType(3);
            } else {
                todo3rd = getTodo3rd(actRuDetail, processParam, 3);
            }
            todo3rd.setSuccess(Boolean.FALSE);
            todo3rd.setMessage(null == todoResponse ? "接口不通" : todoResponse.getMessage());
            todo3rd.setParams(Y9JsonUtil.writeValueAsString(getTodoParam(timestamp, token, vcode)));
            todo3rdRepository.save(todo3rd);
        }
    }

    @Async
    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void deleteTodo3rd(DocumentCopy documentCopy) {
        ProcessParam processParam =
            processParamService.findByProcessSerialNumber(documentCopy.getProcessSerialNumber());
        List<Person> personList =
            positionApi.listPersonsByPositionId(Y9LoginUserHolder.getTenantId(), documentCopy.getUserId()).getData();
        List<Todo3rd> todo3rdList = todo3rdRepository.findByProcessSerialNumberAndReceiveUserIdAndTodoType(
            documentCopy.getProcessSerialNumber(), personList.get(0).getId(), 2);
        if (!todo3rdList.isEmpty()) {
            Todo3rd todo3rd = todo3rdList.get(0);
            String timestamp = getFormattedTimestamp(), token = Y9IdGenerator.genId(),
                vcode = MD5Util.md5Encode(timestamp + APP + token + KEY);
            List<NameValuePair> params = getParams(timestamp, token, vcode);
            TodoResponse todoResponse =
                RemoteCallUtil.delete(todo3rdUrl + DELETEURL + "/" + todo3rd.getId(), params, TodoResponse.class);
            if (null != todoResponse && "success".equals(todoResponse.getType())) {
                todo3rdRepository.deleteById(todo3rd.getId());
            } else {
                LOGGER.error("调用第三方接口失败：接口地址：{},响应信息：{}", todo3rdUrl + DELETEURL,
                    null == todoResponse ? "接口不通" : todoResponse.getMessage());
                todo3rd.setOptType(3);
                todo3rd.setSuccess(Boolean.FALSE);
                todo3rd.setMessage(null == todoResponse ? "接口不通" : todoResponse.getMessage());
                todo3rd.setParams(Y9JsonUtil.writeValueAsString(getTodoParam(timestamp, token, vcode)));
                todo3rdRepository.save(todo3rd);
            }
        } else {
            LOGGER.error("{}的传签待办不存在：标题：{}", documentCopy.getUserName(), processParam.getTitle());
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
        // 办文说明
        String content;
        if (optType.equals(1)) {
            content = StringUtils.isNotBlank(processParam.getDescription()) ? processParam.getDescription() : "";
        } else {
            List<TaskRelated> taskRelatedList = taskRelatedService.findByTaskId(actRuDetail.getTaskId());
            Optional<TaskRelated> taskRelatedOptional = taskRelatedList.stream()
                .filter(tr -> tr.getInfoType().equals(TaskRelatedEnum.BANWENSHUOMING.getValue())).findFirst();
            content = taskRelatedOptional.isPresent() ? taskRelatedOptional.get().getMsgContent() : "";
        }
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
            .appName(processParam.getSystemCnName()).title(processParam.getTitle()).content(content)
            .receiveUserId(receiveUserId).receiveUserName(receiveUserName).receiveDeptId(actRuDetail.getDeptId())
            .receiveDeptName(actRuDetail.getDeptName()).receiveTime(sdf.format(actRuDetail.getCreateTime()))
            .sendUserId(sendUserId).sendUserName(sendUserName).sendDeptId(actRuDetail.getDeptId())
            .sendDeptName(actRuDetail.getSendDeptName()).optType(optType).urgent(urgent).urgentText(urgentText)
            .processSerialNumber(actRuDetail.getProcessSerialNumber()).todoType(1).success(Boolean.TRUE).build();
    }

    private Todo3rd getTodo3rd(DocumentCopy documentCopy, ProcessParam processParam, Integer optType) {
        List<Person> receivePersonList =
            positionApi.listPersonsByPositionId(Y9LoginUserHolder.getTenantId(), documentCopy.getUserId()).getData();
        String receiveUserId = "", receiveUserName = "", receiveDeptId = "", receiveDeptName = "";
        if (!receivePersonList.isEmpty()) {
            receiveUserId = receivePersonList.get(0).getId();
            receiveUserName = receivePersonList.get(0).getName();
            OrgUnit receiveDept = orgUnitApi
                .getOrgUnit(Y9LoginUserHolder.getTenantId(), receivePersonList.get(0).getParentId()).getData();
            receiveDeptId = receiveDept.getId();
            receiveDeptName = receiveDept.getOrgType().equals(OrgTypeEnum.DEPARTMENT)
                && StringUtils.isNotBlank(((Department)receiveDept).getAliasName())
                    ? ((Department)receiveDept).getAliasName() : receiveDept.getName();
        }
        List<Person> sendPersonList =
            positionApi.listPersonsByPositionId(Y9LoginUserHolder.getTenantId(), documentCopy.getSenderId()).getData();
        String sendUserId = "", sendUserName = "", sendDeptId = "", sendDeptName = "";
        if (!sendPersonList.isEmpty()) {
            sendUserId = receivePersonList.get(0).getId();
            sendUserName = receivePersonList.get(0).getName();
            OrgUnit sendDept = orgUnitApi
                .getOrgUnit(Y9LoginUserHolder.getTenantId(), receivePersonList.get(0).getParentId()).getData();
            sendDeptId = sendDept.getId();
            sendDeptName = sendDept.getOrgType().equals(OrgTypeEnum.DEPARTMENT)
                && StringUtils.isNotBlank(((Department)sendDept).getAliasName()) ? ((Department)sendDept).getAliasName()
                    : sendDept.getName();
        }
        // 紧急程度
        Map<String, Object> map =
            formDataService.getData4TableAlias(documentCopy.getProcessSerialNumber(), "fw").getData();
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
        return Todo3rd.builder().id(Y9IdGenerator.genId()).appCode(processParam.getSystemName())
            .appName(processParam.getSystemCnName()).title(processParam.getTitle()).content("")
            .receiveUserId(receiveUserId).receiveUserName(receiveUserName).receiveDeptId(receiveDeptId)
            .receiveDeptName(receiveDeptName).receiveTime(documentCopy.getCreateTime()).sendUserId(sendUserId)
            .sendUserName(sendUserName).sendDeptId(sendDeptId).sendDeptName(sendDeptName).optType(optType)
            .urgent(urgent).urgentText(urgentText).processSerialNumber(documentCopy.getProcessSerialNumber())
            .todoType(2).success(Boolean.TRUE).build();
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
