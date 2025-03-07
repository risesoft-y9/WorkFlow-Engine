package net.risesoft.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ChaoSongApi;
import net.risesoft.api.itemadmin.OfficeFollowApi;
import net.risesoft.api.itemadmin.OrganWordApi;
import net.risesoft.api.itemadmin.extend.ItemTodoTaskApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.y9.Y9LoginUserHolder;

@Slf4j
@RequiredArgsConstructor
@EnableAsync
@Service(value = "asyncUtilService")
public class AsyncUtilService {

    private final ItemTodoTaskApi todoTaskApi;

    private final ChaoSongApi chaoSongApi;

    private final OfficeFollowApi officeFollowApi;

    private final OrganWordApi organWordApi;

    private final ProcessDefinitionApi processDefinitionApi;

    @Resource(name = "jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate;

    /**
     * 生成流水号gfg
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     */
    @Async
    public void generateNumber(final String tenantId, final String userId, final String itemId,
        final String processSerialNumber, final TaskModel task) {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            String sql = "select lsh from y9_form_fw where guid = '" + processSerialNumber + "'";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            if (list.get(0).get("lsh") == null || list.get(0).get("lsh").toString().isEmpty()
                || !list.get(0).get("lsh").toString().contains("发稿[")) {
                String startNode = processDefinitionApi
                    .getStartNodeKeyByProcessDefinitionId(tenantId, task.getProcessDefinitionId()).getData();
                List<TargetModel> nodeList =
                    processDefinitionApi.getTargetNodes(tenantId, task.getProcessDefinitionId(), startNode).getData();
                boolean isqicao = false;
                for (TargetModel targetModel : nodeList) {
                    if (targetModel.getTaskDefKey().equals(task.getTaskDefinitionKey())) {
                        isqicao = true;
                        break;
                    }
                }
                if (isqicao) {
                    getNumber(tenantId, userId, itemId, processSerialNumber);
                }
            }
        } catch (Exception e) {
            LOGGER.error("生成流水号失败", e);
        }
    }

    public void getNumber(final String tenantId, final String userId, final String itemId,
        final String processSerialNumber) {
        String custom = "fawen";
        String characterValue = "发稿";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String year = sdf.format(new Date());
        Integer number = organWordApi
            .getNumber(tenantId, userId, custom, characterValue, Integer.parseInt(year), 0, itemId).getData();
        Integer status = organWordApi.checkNumberStr(tenantId, userId, characterValue, custom, Integer.parseInt(year),
            number, itemId, 0, processSerialNumber).getData();
        LOGGER.info("***************************获取编号：{}，状态：{}", number, status);
        if (status == 0) {
            // 当前编号已被使用，获取最新的可以用的编号
            getNumber(tenantId, userId, itemId, processSerialNumber);
        } else if (status == 1) {
            String numberStr = "";
            if (String.valueOf(number).length() == 1) {
                numberStr = "00000" + number;
            } else if (String.valueOf(number).length() == 2) {
                numberStr = "0000" + number;
            } else if (String.valueOf(number).length() == 3) {
                numberStr = "000" + number;
            } else if (String.valueOf(number).length() == 4) {
                numberStr = "00" + number;
            } else if (String.valueOf(number).length() == 5) {
                numberStr = "0" + number;
            } else {
                numberStr = String.valueOf(number);
            }
            String sql = "update y9_form_fw set lsh = '"+characterValue+"[" + year + "]" + numberStr + "' where guid = '"
                + processSerialNumber + "'";
            jdbcTemplate.execute(sql);
        }
    }

    /**
     * 更新统一待办，抄送件标题
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param documentTitle 标题
     */
    @Async
    public void updateTitle(final String tenantId, final String processInstanceId, final String documentTitle) {
        try {
            chaoSongApi.updateTitle(tenantId, processInstanceId, documentTitle);
            todoTaskApi.updateTitle(tenantId, processInstanceId, documentTitle);
            officeFollowApi.updateTitle(tenantId, processInstanceId, documentTitle);
        } catch (Exception e) {
            LOGGER.error("更新统一待办，抄送件标题", e);
        }
    }

}
