package net.risesoft.service.config.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.entity.Item;
import net.risesoft.entity.form.Y9FormItemBind;
import net.risesoft.entity.form.Y9FormItemMobileBind;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.form.Y9FormItemBindRepository;
import net.risesoft.repository.form.Y9FormItemMobileBindRepository;
import net.risesoft.repository.jpa.ItemRepository;
import net.risesoft.service.config.Y9FormItemBindService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@Slf4j
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class Y9FormItemBindServiceImpl implements Y9FormItemBindService {

    private final Y9FormItemBindRepository y9FormItemBindRepository;

    private final Y9FormItemMobileBindRepository y9FormItemMobileBindRepository;

    private final ItemRepository itemRepository;

    private final ProcessDefinitionApi processDefinitionApi;

    private final RepositoryApi repositoryApi;

    private final Y9FormItemBindService self;

    public Y9FormItemBindServiceImpl(
        Y9FormItemBindRepository y9FormItemBindRepository,
        Y9FormItemMobileBindRepository y9FormItemMobileBindRepository,
        ItemRepository itemRepository,
        ProcessDefinitionApi processDefinitionApi,
        RepositoryApi repositoryApi,
        @Lazy Y9FormItemBindService self) {
        this.y9FormItemBindRepository = y9FormItemBindRepository;
        this.y9FormItemMobileBindRepository = y9FormItemMobileBindRepository;
        this.itemRepository = itemRepository;
        this.processDefinitionApi = processDefinitionApi;
        this.repositoryApi = repositoryApi;
        this.self = self;
    }

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
            /*
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
    public void copyForm(String itemId, String processDefinitionId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Item item = itemRepository.findById(itemId).orElse(null);
        assert item != null;
        String processDefinitionKey = item.getWorkflowGuid();
        // 获取最新流程定义
        ProcessDefinitionModel latestProcessDefinition =
            repositoryApi.getLatestProcessDefinitionByKey(tenantId, processDefinitionKey).getData();
        String latestProcessDefinitionId = latestProcessDefinition.getId();
        // 获取前一版本流程定义ID
        String previousProcessDefinitionId = getPreviousProcessDefinitionId(tenantId, processDefinitionId,
            latestProcessDefinitionId, latestProcessDefinition);
        // 获取前一版本的表单绑定列表
        List<Y9FormItemBind> previousFormBinds =
            y9FormItemBindRepository.findByItemIdAndProcDefId(itemId, previousProcessDefinitionId);
        // 获取最新流程定义的节点
        List<TargetModel> nodes = processDefinitionApi.getNodes(tenantId, latestProcessDefinitionId).getData();
        /*
         * 如果最新的流程定义存在当前任务节点，则查找当前事项的最新的流程定义的任务节点有没有绑定对应的表单，没有就保存
         */
        for (Y9FormItemBind formBind : previousFormBinds) {
            copyFormItemBind(formBind, itemId, latestProcessDefinitionId, nodes, tenantId);
        }
        // 复制手机端表单绑定信息
        copyMobileBindForm(previousProcessDefinitionId, latestProcessDefinitionId, itemId, tenantId, nodes);
    }

    /**
     * 获取前一版本流程定义ID
     */
    private String getPreviousProcessDefinitionId(String tenantId, String processDefinitionId,
        String latestProcessDefinitionId, ProcessDefinitionModel latestProcessDefinition) {
        String previousProcessDefinitionId = processDefinitionId;

        if (processDefinitionId.equals(latestProcessDefinitionId) && latestProcessDefinition.getVersion() > 1) {
            ProcessDefinitionModel previousProcessDefinition =
                repositoryApi.getPreviousProcessDefinitionById(tenantId, latestProcessDefinitionId).getData();
            previousProcessDefinitionId = previousProcessDefinition.getId();
        }
        return previousProcessDefinitionId;
    }

    /**
     * 复制表单绑定信息
     */
    private void copyFormItemBind(Y9FormItemBind sourceFormBind, String itemId, String latestProcessDefinitionId,
        List<TargetModel> nodes, String tenantId) {

        String taskDefKey = sourceFormBind.getTaskDefKey();
        String formId = sourceFormBind.getFormId();

        if (StringUtils.isEmpty(taskDefKey)) {
            // 处理空任务定义键的情况
            Y9FormItemBind existingFormBind = y9FormItemBindRepository
                .findByItemIdAndProcDefIdAndAndFormIdAndTaskDefKeyIsNull(itemId, latestProcessDefinitionId, formId);
            if (null == existingFormBind) {
                self.save(sourceFormBind, latestProcessDefinitionId, formId, itemId, taskDefKey, tenantId);
            }
        } else {
            // 处理特定任务定义键的情况
            copyFormItemBindForTask(sourceFormBind, itemId, latestProcessDefinitionId, taskDefKey, formId, nodes,
                tenantId);
        }
    }

    /**
     * 为特定任务复制表单绑定信息
     */
    private void copyFormItemBindForTask(Y9FormItemBind sourceFormBind, String itemId, String latestProcessDefinitionId,
        String taskDefKey, String formId, List<TargetModel> nodes, String tenantId) {
        for (TargetModel targetModel : nodes) {
            if (targetModel.getTaskDefKey().equals(taskDefKey)) {
                Y9FormItemBind existingFormBind =
                    y9FormItemBindRepository.findByItemIdAndProcDefIdAndTaskDefKeyAndFormId(itemId,
                        latestProcessDefinitionId, taskDefKey, formId);
                if (null == existingFormBind) {
                    self.save(sourceFormBind, latestProcessDefinitionId, formId, itemId, taskDefKey, tenantId);
                    break;
                }
            }
        }
    }

    public void copyMobileBindForm(String previousProcessDefinitionId, String latestProcessDefinitionId, String itemId,
        String tenantId, List<TargetModel> nodes) {
        List<Y9FormItemMobileBind> previousMobileBinds =
            y9FormItemMobileBindRepository.findByItemIdAndProcDefId(itemId, previousProcessDefinitionId);

        for (Y9FormItemMobileBind mobileBind : previousMobileBinds) {
            copyMobileFormItemBind(mobileBind, itemId, latestProcessDefinitionId, nodes, tenantId);
        }
    }

    /**
     * 复制移动端表单绑定信息
     */
    private void copyMobileFormItemBind(Y9FormItemMobileBind sourceMobileBind, String itemId,
        String latestProcessDefinitionId, List<TargetModel> nodes, String tenantId) {
        String taskDefKey = sourceMobileBind.getTaskDefKey();
        String formId = sourceMobileBind.getFormId();
        if (StringUtils.isEmpty(taskDefKey)) {
            // 处理空任务定义键的情况
            Y9FormItemMobileBind existingMobileBind = y9FormItemMobileBindRepository
                .findByItemIdAndProcDefIdAndAndFormIdAndTaskDefKeyIsNull(itemId, latestProcessDefinitionId, formId);
            if (null == existingMobileBind) {
                self.save(sourceMobileBind, latestProcessDefinitionId, formId, itemId, taskDefKey, tenantId);
            }
        } else {
            // 处理特定任务定义键的情况
            copyMobileFormItemBindForTask(sourceMobileBind, itemId, latestProcessDefinitionId, taskDefKey, formId,
                nodes, tenantId);
        }
    }

    /**
     * 为特定任务复制移动端表单绑定信息
     */
    private void copyMobileFormItemBindForTask(Y9FormItemMobileBind sourceMobileBind, String itemId,
        String latestProcessDefinitionId, String taskDefKey, String formId, List<TargetModel> nodes, String tenantId) {

        for (TargetModel targetModel : nodes) {
            if (targetModel.getTaskDefKey().equals(taskDefKey)) {
                Y9FormItemMobileBind existingMobileBind =
                    y9FormItemMobileBindRepository.findByItemIdAndProcDefIdAndTaskDefKeyAndFormId(itemId,
                        latestProcessDefinitionId, taskDefKey, formId);
                if (null == existingMobileBind) {
                    self.save(sourceMobileBind, latestProcessDefinitionId, formId, itemId, taskDefKey, tenantId);
                    break;
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
    public Integer getMaxTabIndex(String itemId, String processDefinitionId) {
        return y9FormItemBindRepository.getMaxTabIndex(itemId, processDefinitionId);
    }

    @Override
    public Y9FormItemBind getById(String id) {
        return y9FormItemBindRepository.findById(id).orElse(null);
    }

    @Override
    public String getShowOther(List<Y9FormItemBind> formTaskBinds) {
        if (formTaskBinds == null || formTaskBinds.isEmpty()) {
            return "";
        }
        boolean showDocumentFlag = false;
        boolean showFileFlag = false;
        boolean showHistoryFlag = false;
        // 遍历表单绑定列表，设置相应的标志位
        for (Y9FormItemBind y9FormItemBind : formTaskBinds) {
            // 只有当标志位尚未设置为true时才检查
            if (!showDocumentFlag) {
                showDocumentFlag = y9FormItemBind.isShowDocumentTab();
            }
            if (!showFileFlag) {
                showFileFlag = y9FormItemBind.isShowFileTab();
            }
            if (!showHistoryFlag) {
                showHistoryFlag = y9FormItemBind.isShowHistoryTab();
            }
            // 如果所有标志位都已设置为true，则提前退出循环
            if (showDocumentFlag && showFileFlag && showHistoryFlag) {
                break;
            }
        }
        return buildResultString(showDocumentFlag, showFileFlag, showHistoryFlag);
    }

    /**
     * 构建结果字符串
     */
    private String buildResultString(boolean showDocumentFlag, boolean showFileFlag, boolean showHistoryFlag) {
        String result = "";
        if (showDocumentFlag) {
            result = Y9Util.genCustomStr(result, "showDocumentTab");
        }
        if (showFileFlag) {
            result = Y9Util.genCustomStr(result, "showFileTab");
        }
        if (showHistoryFlag) {
            result = Y9Util.genCustomStr(result, "showHistoryTab");
        }
        return result;
    }

    @Override
    public List<Y9FormItemBind> listByItemIdAndProcDefId(String itemId, String procDefId) {
        List<Y9FormItemBind> formItemBinds = new ArrayList<>();
        if (StringUtils.isNotBlank(itemId) && StringUtils.isNotBlank(procDefId)) {
            formItemBinds =
                y9FormItemBindRepository.findByItemIdAndProcessDefinitionIdOrderByTabIndexAsc(itemId, procDefId);
        }
        return formItemBinds;
    }

    @Override
    public List<Y9FormItemBind> listByItemIdAndProcDefIdAndTaskDefKey(String itemId, String procDefId,
        String taskDefKey) {
        List<Y9FormItemBind> formItemBinds;
        // 查找本任务的form,在任务上设置的表单有优先权。
        formItemBinds = y9FormItemBindRepository.findByItemIdAndProcDefIdAndTaskDefKey(itemId, procDefId, taskDefKey);
        if (formItemBinds.isEmpty()) {
            // 再查找缺省的form。任务上没有设置表单，就用缺省表单。
            formItemBinds = y9FormItemBindRepository.findByItemIdAndProcDefIdAndTaskDefKeyIsNull(itemId, procDefId);
        }
        return formItemBinds;
    }

    @Override
    public List<Y9FormItemMobileBind> listByItemIdAndProcDefIdAndTaskDefKey4Mobile(String itemId, String procDefId,
        String taskDefKey) {
        List<Y9FormItemMobileBind> formItemMobileBinds = new ArrayList<>();
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            // 查找本任务的form,在任务上设置的表单有优先权。
            List<TargetModel> list;
            // taskDefKey为空表示为办结件，需要获取最后一个任务的表单。
            if (StringUtils.isBlank(taskDefKey)) {
                list = processDefinitionApi.getNodes(tenantId, procDefId).getData();
                taskDefKey = list.get(list.size() - 1).getTaskDefKey();
            }
            formItemMobileBinds =
                y9FormItemMobileBindRepository.findByItemIdAndProcDefIdAndTaskDefKey(itemId, procDefId, taskDefKey);
            if (formItemMobileBinds.isEmpty()) {
                // 再查找缺省的form。任务上没有设置表单，就用缺省表单。
                formItemMobileBinds =
                    y9FormItemMobileBindRepository.findByItemIdAndProcDefIdAndTaskDefKeyIsNull(itemId, procDefId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formItemMobileBinds;
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
        List<Y9FormItemBind> formItemBinds = new ArrayList<>();
        if (StringUtils.isNotBlank(itemId) && StringUtils.isNotBlank(procDefId)) {
            formItemBinds = y9FormItemBindRepository.findByItemIdAndProcDefIdAndTaskDefKeyIsNull(itemId, procDefId);
        }
        return formItemBinds;
    }

    @Override
    @Transactional
    public Y9Result<String> save(Y9FormItemBind formItemBind) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            formItemBind.setTenantId(tenantId);
            y9FormItemBindRepository.saveAndFlush(formItemBind);
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("保存失败");
    }

    @Override
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

    @Override
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
    public Y9Result<String> save(Y9FormItemMobileBind y9FormItemMobileBind) {
        try {
            if (StringUtils.isBlank(y9FormItemMobileBind.getId())) {
                y9FormItemMobileBind.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            }
            String tenantId = Y9LoginUserHolder.getTenantId();
            y9FormItemMobileBind.setTenantId(tenantId);
            y9FormItemMobileBindRepository.saveAndFlush(y9FormItemMobileBind);
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("保存失败");
    }

    @Override
    @Transactional
    public void updateOrder(String[] idAndTabIndexs) {
        List<String> list = Lists.newArrayList(idAndTabIndexs);
        try {
            for (String s : list) {
                String[] arr = s.split(SysVariables.COLON);
                String index = arr[1];
                Integer tabIndex = null;
                if (!index.equals("null")) {
                    tabIndex = Integer.parseInt(arr[1]);
                }
                y9FormItemBindRepository.updateOrder(tabIndex, arr[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
