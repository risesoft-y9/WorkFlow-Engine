package net.risesoft.service.config.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.entity.Y9FormItemBind;
import net.risesoft.entity.Y9FormItemMobileBind;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.SpmApproveItemRepository;
import net.risesoft.repository.jpa.Y9FormItemBindRepository;
import net.risesoft.repository.jpa.Y9FormItemMobileBindRepository;
import net.risesoft.service.config.Y9FormItemBindService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class Y9FormItemBindServiceImpl implements Y9FormItemBindService {

    private final Y9FormItemBindRepository y9FormItemBindRepository;

    private final Y9FormItemMobileBindRepository y9FormItemMobileBindRepository;

    private final SpmApproveItemRepository spmApproveItemRepository;

    private final ProcessDefinitionApi processDefinitionManager;

    private final RepositoryApi repositoryManager;

    @Override
    @Transactional
    public void copyBindInfo(String itemId, String newItemId, String lastVersionPid) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            // 复制PC端表单的绑定
            List<Y9FormItemBind> previouseibList =
                y9FormItemBindRepository.findByItemIdAndProcDefId(itemId, lastVersionPid);
            for (Y9FormItemBind eib : previouseibList) {
                String taskDefKey = eib.getTaskDefKey(), formId = eib.getFormId();
                Y9FormItemBind eibTemp = new Y9FormItemBind();
                if (StringUtils.isNotBlank(taskDefKey)) {
                    eibTemp.setTaskDefKey(taskDefKey);
                }
                eibTemp.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                eibTemp.setProcessDefinitionId(lastVersionPid);
                eibTemp.setFormName(eib.getFormName());
                eibTemp.setFormId(formId);
                eibTemp.setItemId(newItemId);
                eibTemp.setShowDocumentTab(eib.isShowDocumentTab());
                eibTemp.setShowFileTab(eib.isShowFileTab());
                eibTemp.setShowHistoryTab(eib.isShowHistoryTab());
                eibTemp.setTabIndex(eib.getTabIndex());
                eibTemp.setTaskDefKey(taskDefKey);
                eibTemp.setTenantId(tenantId);
                y9FormItemBindRepository.save(eibTemp);
            }

            // 复制手机端表单绑定信息
            List<Y9FormItemMobileBind> mobileBindList =
                y9FormItemMobileBindRepository.findByItemIdAndProcDefId(itemId, lastVersionPid);
            /**
             * 如果最新的流程定义存在当前任务节点，则查找当前事项的最新的流程定义的任务节点有没有绑定对应的表单，没有就保存
             */
            for (Y9FormItemMobileBind eib : mobileBindList) {
                String taskDefKey = eib.getTaskDefKey(), formId = eib.getFormId();
                Y9FormItemMobileBind eibTemp = new Y9FormItemMobileBind();
                if (StringUtils.isNotBlank(taskDefKey)) {
                    eibTemp.setTaskDefKey(taskDefKey);
                }
                eibTemp.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                eibTemp.setProcessDefinitionId(lastVersionPid);
                eibTemp.setFormName(eib.getFormName());
                eibTemp.setFormId(formId);
                eibTemp.setItemId(newItemId);
                eibTemp.setTaskDefKey(taskDefKey);
                eibTemp.setTenantId(tenantId);
                y9FormItemMobileBindRepository.save(eibTemp);
            }
        } catch (Exception e) {
            LOGGER.error("复制表单绑定信息失败", e);
        }
    }

    @Override
    @Transactional
    public void copyEform(String itemId, String processDefinitionId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        SpmApproveItem item = spmApproveItemRepository.findById(itemId).orElse(null);
        String proDefKey = item.getWorkflowGuid();
        ProcessDefinitionModel latestpd =
            repositoryManager.getLatestProcessDefinitionByKey(tenantId, proDefKey).getData();
        String latestpdId = latestpd.getId();
        String previouspdId = processDefinitionId;
        if (processDefinitionId.equals(latestpdId)) {
            if (latestpd.getVersion() > 1) {
                ProcessDefinitionModel previouspd =
                    repositoryManager.getPreviousProcessDefinitionById(tenantId, latestpdId).getData();
                previouspdId = previouspd.getId();
            }
        }
        List<Y9FormItemBind> previouseibList = y9FormItemBindRepository.findByItemIdAndProcDefId(itemId, previouspdId);
        List<TargetModel> nodes = processDefinitionManager.getNodes(tenantId, latestpdId, false).getData();
        /*
         * 如果最新的流程定义存在当前任务节点，则查找当前事项的最新的流程定义的任务节点有没有绑定对应的表单，没有就保存
         */
        for (Y9FormItemBind eib : previouseibList) {
            String taskDefKey = eib.getTaskDefKey();
            String formId = eib.getFormId();
            if (StringUtils.isEmpty(taskDefKey)) {
                Y9FormItemBind eibTemp = y9FormItemBindRepository
                    .findByItemIdAndProcDefIdAndAndFormIdAndTaskDefKeyIsNull(itemId, latestpdId, formId);
                if (null == eibTemp) {
                    save(eib, latestpdId, formId, itemId, taskDefKey, tenantId);
                }
            } else {
                for (TargetModel targetModel : nodes) {
                    if (targetModel.getTaskDefKey().equals(taskDefKey)) {
                        Y9FormItemBind eibTemp = y9FormItemBindRepository
                            .findByItemIdAndProcDefIdAndTaskDefKeyAndFormId(itemId, latestpdId, taskDefKey, formId);
                        if (null == eibTemp) {
                            save(eib, latestpdId, formId, itemId, taskDefKey, tenantId);
                            break;
                        }
                    }
                }
            }
        }
        // 复制手机端表单绑定信息
        copyMobileBindForm(previouspdId, latestpdId, itemId, tenantId, nodes);
    }

    public void copyMobileBindForm(String previouspdId, String latestpdId, String itemId, String tenantId,
        List<TargetModel> nodes) {
        List<Y9FormItemMobileBind> previouseibList =
            y9FormItemMobileBindRepository.findByItemIdAndProcDefId(itemId, previouspdId);
        for (Y9FormItemMobileBind eibm : previouseibList) {
            String taskDefKey = eibm.getTaskDefKey();
            String formId = eibm.getFormId();
            if (StringUtils.isEmpty(taskDefKey)) {
                Y9FormItemMobileBind eibTemp = y9FormItemMobileBindRepository
                    .findByItemIdAndProcDefIdAndAndFormIdAndTaskDefKeyIsNull(itemId, latestpdId, formId);
                if (null == eibTemp) {
                    save(eibm, latestpdId, formId, itemId, taskDefKey, tenantId);
                }
            } else {
                for (TargetModel targetModel : nodes) {
                    if (targetModel.getTaskDefKey().equals(taskDefKey)) {
                        Y9FormItemMobileBind eibTemp = y9FormItemMobileBindRepository
                            .findByItemIdAndProcDefIdAndTaskDefKeyAndFormId(itemId, latestpdId, taskDefKey, formId);
                        if (null == eibTemp) {
                            save(eibTemp, latestpdId, formId, itemId, taskDefKey, tenantId);
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    @Transactional
    public Y9Result<String> delete(String id) {
        try {
            y9FormItemBindRepository.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return Y9Result.failure("删除失败");
        }
        return Y9Result.successMsg("删除成功");
    }

    @Override
    @Transactional
    public void deleteBindInfo(String itemId) {
        try {
            // 删除PC端表单的绑定
            y9FormItemBindRepository.deleteByItemId(itemId);
            // 删除手机端表单的绑定
            y9FormItemMobileBindRepository.deleteByItemId(itemId);
        } catch (Exception e) {
            LOGGER.error("删除表单绑定信息失败", e);
        }
    }

    @Override
    public Y9FormItemBind getById(String id) {
        return y9FormItemBindRepository.findById(id).orElse(null);
    }

    @Override
    public String getShowOther(List<Y9FormItemBind> eformTaskBinds) {
        String result = "";
        boolean showDocumentFlag = false;
        boolean showFileFlag = false;
        boolean showHistoryFlag = false;
        if (!eformTaskBinds.isEmpty()) {
            for (Y9FormItemBind eftb : eformTaskBinds) {
                if (eftb.isShowDocumentTab()) {
                    showDocumentFlag = true;
                }
                if (eftb.isShowFileTab()) {
                    showFileFlag = true;
                }
                if (eftb.isShowHistoryTab()) {
                    showHistoryFlag = true;
                }
            }
            if (showDocumentFlag) {
                result = Y9Util.genCustomStr(result, "showDocumentTab");
            }
            if (showFileFlag) {
                result = Y9Util.genCustomStr(result, "showFileTab");
            }
            if (showHistoryFlag) {
                result = Y9Util.genCustomStr(result, "showHistoryTab");
            }
        }
        return result;
    }

    @Override
    public List<Y9FormItemBind> listByItemIdAndProcDefId(String itemId, String procDefId) {
        List<Y9FormItemBind> eformTaskBinds = new ArrayList<>();
        try {
            if (StringUtils.isNotBlank(itemId) && StringUtils.isNotBlank(procDefId)) {
                eformTaskBinds =
                    y9FormItemBindRepository.findByItemIdAndProcessDefinitionIdOrderByTabIndexAsc(itemId, procDefId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return eformTaskBinds;
    }

    @Override
    public List<Y9FormItemBind> listByItemIdAndProcDefIdAndTaskDefKey(String itemId, String procDefId,
        String taskDefKey) {
        List<Y9FormItemBind> eformTaskBinds = new ArrayList<>();
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            // 查找本任务的form,在任务上设置的表单有优先权。
            List<TargetModel> list;
            // taskDefKey为空表示为办结件，需要获取最后一个任务的表单。
            if (StringUtils.isBlank(taskDefKey)) {
                list = processDefinitionManager.getNodes(tenantId, procDefId, false).getData();
                taskDefKey = list.get(list.size() - 1).getTaskDefKey();
            }
            eformTaskBinds =
                y9FormItemBindRepository.findByItemIdAndProcDefIdAndTaskDefKey(itemId, procDefId, taskDefKey);
            if (eformTaskBinds.isEmpty()) {
                // 再查找缺省的form。任务上没有设置表单，就用缺省表单。
                eformTaskBinds =
                    y9FormItemBindRepository.findByItemIdAndProcDefIdAndTaskDefKeyIsNull(itemId, procDefId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return eformTaskBinds;
    }

    @Override
    public List<Y9FormItemMobileBind> listByItemIdAndProcDefIdAndTaskDefKey4Mobile(String itemId, String procDefId,
        String taskDefKey) {
        List<Y9FormItemMobileBind> eformTaskBinds = new ArrayList<>();
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            // 查找本任务的form,在任务上设置的表单有优先权。
            List<TargetModel> list;
            // taskDefKey为空表示为办结件，需要获取最后一个任务的表单。
            if (StringUtils.isBlank(taskDefKey)) {
                list = processDefinitionManager.getNodes(tenantId, procDefId, false).getData();
                taskDefKey = list.get(list.size() - 1).getTaskDefKey();
            }
            eformTaskBinds =
                y9FormItemMobileBindRepository.findByItemIdAndProcDefIdAndTaskDefKey(itemId, procDefId, taskDefKey);
            if (eformTaskBinds.isEmpty()) {
                // 再查找缺省的form。任务上没有设置表单，就用缺省表单。
                eformTaskBinds =
                    y9FormItemMobileBindRepository.findByItemIdAndProcDefIdAndTaskDefKeyIsNull(itemId, procDefId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return eformTaskBinds;
    }

    @Override
    public List<Y9FormItemBind> listByItemIdAndProcDefIdAndTaskDefKey4Own(String itemId, String procDefId,
        String taskDefKey) {
        List<Y9FormItemBind> y9FormItemBinds = new ArrayList<>();
        try {
            if (StringUtils.isNotEmpty(taskDefKey)) {
                y9FormItemBinds =
                    y9FormItemBindRepository.findByItemIdAndProcDefIdAndTaskDefKey(itemId, procDefId, taskDefKey);
            } else {
                y9FormItemBinds =
                    y9FormItemBindRepository.findByItemIdAndProcDefIdAndTaskDefKeyIsNull(itemId, procDefId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return y9FormItemBinds;
    }

    @Override
    public List<Y9FormItemMobileBind> listByItemIdAndProcDefIdAndTaskDefKey4OwnMobile(String itemId, String procDefId,
        String taskDefKey) {
        List<Y9FormItemMobileBind> y9FormItemBinds = new ArrayList<>();
        try {
            if (StringUtils.isNotEmpty(taskDefKey)) {
                y9FormItemBinds =
                    y9FormItemMobileBindRepository.findByItemIdAndProcDefIdAndTaskDefKey(itemId, procDefId, taskDefKey);
            } else {
                y9FormItemBinds =
                    y9FormItemMobileBindRepository.findByItemIdAndProcDefIdAndTaskDefKeyIsNull(itemId, procDefId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return y9FormItemBinds;
    }

    @Override
    public List<Y9FormItemBind> listByItemIdAndProcDefIdAndTaskDefKeyIsNull(String itemId, String procDefId) {
        List<Y9FormItemBind> eformTaskBinds = new ArrayList<>();
        try {
            if (StringUtils.isNotBlank(itemId) && StringUtils.isNotBlank(procDefId)) {
                eformTaskBinds =
                    y9FormItemBindRepository.findByItemIdAndProcDefIdAndTaskDefKeyIsNull(itemId, procDefId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return eformTaskBinds;
    }

    @Override
    @Transactional
    public Y9Result<String> save(Y9FormItemBind eformItem) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            eformItem.setTenantId(tenantId);
            y9FormItemBindRepository.saveAndFlush(eformItem);
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("保存失败");
    }

    @Transactional
    public void save(Y9FormItemBind eib, String latestpdId, String formId, String itemId, String taskDefKey,
        String tenantId) {
        Y9FormItemBind eibTemp = new Y9FormItemBind();
        eibTemp.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        eibTemp.setProcessDefinitionId(latestpdId);
        eibTemp.setFormName(eib.getFormName());
        eibTemp.setFormId(formId);
        eibTemp.setItemId(itemId);
        eibTemp.setShowDocumentTab(eib.isShowDocumentTab());
        eibTemp.setShowFileTab(eib.isShowFileTab());
        eibTemp.setShowHistoryTab(eib.isShowHistoryTab());
        eibTemp.setTabIndex(eib.getTabIndex());
        eibTemp.setTaskDefKey(taskDefKey);
        eibTemp.setTenantId(tenantId);
        y9FormItemBindRepository.save(eibTemp);
    }

    @Transactional
    public void save(Y9FormItemMobileBind eib, String latestpdId, String formId, String itemId, String taskDefKey,
        String tenantId) {
        Y9FormItemMobileBind eibTemp = new Y9FormItemMobileBind();
        eibTemp.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        eibTemp.setProcessDefinitionId(latestpdId);
        eibTemp.setFormName(eib.getFormName());
        eibTemp.setFormId(formId);
        eibTemp.setItemId(itemId);
        eibTemp.setTaskDefKey(taskDefKey);
        eibTemp.setTenantId(tenantId);
        y9FormItemMobileBindRepository.save(eibTemp);
    }

    @Override
    @Transactional
    public Y9Result<String> save(Y9FormItemMobileBind eformItem) {
        try {
            if (StringUtils.isBlank(eformItem.getId())) {
                eformItem.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            }
            String tenantId = Y9LoginUserHolder.getTenantId();
            eformItem.setTenantId(tenantId);
            y9FormItemMobileBindRepository.saveAndFlush(eformItem);
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("保存失败");
    }

}
