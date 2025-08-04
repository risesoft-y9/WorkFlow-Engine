package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.OfficeDoneInfoApi;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
import net.risesoft.service.CustomHistoricProcessService;
import net.risesoft.service.DeleteProcessService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service(value = "customHistoricProcessService")
public class CustomHistoricProcessServiceImpl implements CustomHistoricProcessService {

    private final RuntimeService runtimeService;

    private final HistoryService historyService;

    private final OfficeDoneInfoApi officeDoneInfoApi;

    private final DeleteProcessService deleteProcessService;

    @Override
    public boolean deleteProcessInstance(String processInstanceId) {
        try {
            HistoricProcessInstance his =
                historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            if (his.getEndTime() == null) {
                // 在办件删除,先挂起流程实例，再加删除标识，待办件和在办件查询时，只查激活的实例，监控删除件查询有删除标识的实例
                String updateSql =
                    "UPDATE act_hi_procinst t SET t.DELETE_REASON_ = #{DELETE_REASON_} WHERE t.PROC_INST_ID_=#{processInstanceId}";
                historyService.createNativeHistoricProcessInstanceQuery().sql(updateSql)
                    .parameter("DELETE_REASON_", "已删除").parameter("processInstanceId", processInstanceId)
                    .singleResult();
                runtimeService.suspendProcessInstanceById(processInstanceId);
            } else {
                // 办结件删除
                String updateSql =
                    "UPDATE act_hi_procinst t SET t.DELETE_REASON_ = #{DELETE_REASON_} WHERE t.PROC_INST_ID_=#{processInstanceId}";
                historyService.createNativeHistoricProcessInstanceQuery().sql(updateSql)
                    .parameter("DELETE_REASON_", "已删除").parameter("processInstanceId", processInstanceId)
                    .singleResult();
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("删除流程实例失败", e);
        }
        return false;
    }

    @Override
    public HistoricProcessInstance getById(String processInstanceId) {
        return historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
    }

    @Override
    public HistoricProcessInstance getByIdAndYear(String processInstanceId, String year) {
        if (StringUtils.isNotBlank(year)) {
            String sql =
                "SELECT * from ACT_HI_PROCINST_" + year + " p where p.PROC_INST_ID_ = '" + processInstanceId + "'";
            return historyService.createNativeHistoricProcessInstanceQuery().sql(sql).singleResult();
        } else {
            return historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId)
                .singleResult();
        }
    }

    @Override
    public int getRecycleCount(String title) {
        return (int)historyService.createHistoricProcessInstanceQuery()
            .variableValueLike(SysVariables.DOCUMENT_TITLE, "%" + title + "%").or().deleted().count();
    }

    @Override
    public int getRecycleCountByItemId(String itemId, String title) {
        return (int)historyService.createHistoricProcessInstanceQuery().variableValueEquals("itemId", itemId)
            .variableValueLike(SysVariables.DOCUMENT_TITLE, "%" + title + "%").or().deleted().count();
    }

    @Override
    public int getRecycleCountByUserId(String title, String userId) {
        return (int)historyService.createHistoricProcessInstanceQuery().involvedUser(userId)
            .variableValueLike(SysVariables.DOCUMENT_TITLE, "%" + title + "%").or().deleted().count();
    }

    @Override
    public HistoricProcessInstance getSuperProcessInstanceById(String processInstanceId) {
        return historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
    }

    @Override
    public List<HistoricProcessInstance> listBySuperProcessInstanceId(String superProcessInstanceId) {
        return historyService.createHistoricProcessInstanceQuery().superProcessInstanceId(superProcessInstanceId)
            .notDeleted().orderByProcessInstanceStartTime().asc().list();
    }

    @Override
    public List<HistoricProcessInstance> listDeleteProByItemId(String itemId, Integer page, Integer rows) {
        return historyService.createHistoricProcessInstanceQuery().variableValueEquals("itemId", itemId).deleted()
            .orderByProcessInstanceStartTime().desc().listPage((page - 1) * rows, rows);
    }

    @Override
    public List<HistoricProcessInstance> listRecycleAll(String title, Integer page, Integer rows) {
        return historyService.createHistoricProcessInstanceQuery().deleted()
            .variableValueLike(SysVariables.DOCUMENT_TITLE, "%" + title + "%").orderByProcessInstanceStartTime().desc()
            .listPage((page - 1) * rows, rows);
    }

    @Override
    public List<HistoricProcessInstance> listRecycleByItemId(String itemId, String title, Integer page, Integer rows) {
        return historyService.createHistoricProcessInstanceQuery().variableValueEquals("itemId", itemId).deleted()
            .variableValueLike(SysVariables.DOCUMENT_TITLE, "%" + title + "%").orderByProcessInstanceStartTime().desc()
            .listPage((page - 1) * rows, rows);
    }

    @Override
    public List<HistoricProcessInstance> listRecycleByUserId(String title, String userId, Integer page, Integer rows) {
        return historyService.createHistoricProcessInstanceQuery().involvedUser(userId).deleted()
            .variableValueLike(SysVariables.DOCUMENT_TITLE, "%" + title + "%").orderByProcessInstanceStartTime().desc()
            .listPage((page - 1) * rows, rows);
    }

    @Override
    public boolean recoveryProcessInstance(String processInstanceId) {
        try {
            HistoricProcessInstance his =
                historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            // 在办件恢复,激活流程实例
            if (his.getEndTime() == null) {
                runtimeService.activateProcessInstanceById(processInstanceId);
                String updateSql =
                    "UPDATE act_hi_procinst t SET t.DELETE_REASON_ = #{DELETE_REASON_,jdbcType=VARCHAR} WHERE t.PROC_INST_ID_=#{processInstanceId}";
                historyService.createNativeHistoricProcessInstanceQuery().sql(updateSql)
                    .parameter("DELETE_REASON_", null).parameter("processInstanceId", processInstanceId).singleResult();
            } else {
                // 办结件恢复
                String updateSql =
                    "UPDATE act_hi_procinst t SET t.DELETE_REASON_ = #{DELETE_REASON_,jdbcType=VARCHAR} WHERE t.PROC_INST_ID_=#{processInstanceId}";
                historyService.createNativeHistoricProcessInstanceQuery().sql(updateSql)
                    .parameter("DELETE_REASON_", null).parameter("processInstanceId", processInstanceId).singleResult();
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("恢复流程实例失败", e);
        }
        return false;
    }

    @Override
    public boolean removeProcess(String processInstanceId) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            HistoricProcessInstance his =
                historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            String year = "";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            // 未办结件删除
            if (his != null && his.getEndTime() == null) {
                year = sdf.format(his.getStartTime());
                runtimeService.deleteProcessInstance(processInstanceId, "已删除");
                historyService.deleteHistoricProcessInstance(his.getId());
            } else {
                if (his == null) {
                    // 数据中心办结件
                    OfficeDoneInfoModel officeDoneInfoModel =
                        officeDoneInfoApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    if (officeDoneInfoModel != null) {
                        year = officeDoneInfoModel.getStartTime().substring(0, 4);
                        // 删除年度数据
                        deleteProcessService.deleteYearData(tenantId, year, processInstanceId);
                    }
                } else {// 办结件
                    year = sdf.format(his.getStartTime());
                    historyService.deleteHistoricProcessInstance(his.getId());
                }
            }
            try {
                officeDoneInfoApi.deleteOfficeDoneInfo(tenantId, processInstanceId);
            } catch (Exception e1) {
                LOGGER.warn("************************************删除数据中心办结件数据失败", e1);
            }
            deleteProcessService.deleteProcess(tenantId, processInstanceId);
            return true;
        } catch (Exception e) {
            LOGGER.error("删除流程实例失败", e);
        }
        return false;
    }

    @Override
    @Transactional
    public void setPriority(String processInstanceId, String priority) throws Exception {
        ProcessInstance processInstance =
            runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (null != processInstance) {
            String executionSql =
                "UPDATE ACT_RU_EXECUTION T SET T.TENANT_ID_ = #{TENANT_ID_} WHERE T.ID_=#{processInstanceId}";
            runtimeService.createNativeProcessInstanceQuery().sql(executionSql).parameter("TENANT_ID_", priority)
                .parameter("processInstanceId", processInstanceId).singleResult();
        }
        String updateSql =
            "UPDATE ACT_HI_PROCINST T SET T.TENANT_ID_ = #{TENANT_ID_} WHERE T.PROC_INST_ID_=#{processInstanceId}";
        runtimeService.createNativeProcessInstanceQuery().sql(updateSql).parameter("TENANT_ID_", priority)
            .parameter("processInstanceId", processInstanceId).singleResult();
    }

}
