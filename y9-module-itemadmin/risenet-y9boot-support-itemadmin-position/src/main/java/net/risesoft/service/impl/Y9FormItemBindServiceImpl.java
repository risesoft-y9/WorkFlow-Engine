package net.risesoft.service.impl;

import lombok.RequiredArgsConstructor;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.entity.Y9FormItemBind;
import net.risesoft.entity.Y9FormItemMobileBind;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.repository.jpa.Y9FormItemBindRepository;
import net.risesoft.repository.jpa.Y9FormItemMobileBindRepository;
import net.risesoft.service.SpmApproveItemService;
import net.risesoft.service.Y9FormItemBindService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class Y9FormItemBindServiceImpl implements Y9FormItemBindService {

    private final Y9FormItemBindRepository y9FormItemBindRepository;

    private final Y9FormItemMobileBindRepository y9FormItemMobileBindRepository;

    private final ProcessDefinitionApi processDefinitionManager;

    private final SpmApproveItemService spmApproveItemService;

    private final RepositoryApi repositoryManager;

    @Override
    @Transactional
    public void copyEform(String itemId, String processDefinitionId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        SpmApproveItem item = spmApproveItemService.findById(itemId);
        String proDefKey = item.getWorkflowGuid();
        ProcessDefinitionModel latestpd = repositoryManager.getLatestProcessDefinitionByKey(tenantId, proDefKey);
        String latestpdId = latestpd.getId();
        String previouspdId = processDefinitionId;
        if (processDefinitionId.equals(latestpdId)) {
            if (latestpd.getVersion() > 1) {
                ProcessDefinitionModel previouspd =
                    repositoryManager.getPreviousProcessDefinitionById(tenantId, latestpdId);
                previouspdId = previouspd.getId();
            }
        }
        List<Y9FormItemBind> previouseibList = y9FormItemBindRepository.findByItemIdAndProcDefId(itemId, previouspdId);
        List<Map<String, Object>> nodes = processDefinitionManager.getNodes(tenantId, latestpdId, false);
        /**
         * 如果最新的流程定义存在当前任务节点，则查找当前事项的最新的流程定义的任务节点有没有绑定对应的表单，没有就保存
         */
        for (Y9FormItemBind eib : previouseibList) {
            String taskDefKey = eib.getTaskDefKey(), formId = eib.getFormId();
            if (StringUtils.isEmpty(taskDefKey)) {
                Y9FormItemBind eibTemp = y9FormItemBindRepository
                    .findByItemIdAndProcDefIdAndAndFormIdAndTaskDefKeyIsNull(itemId, latestpdId, formId);
                if (null == eibTemp) {
                    save(eib, latestpdId, formId, itemId, taskDefKey, tenantId);
                }
            } else {
                for (Map<String, Object> mapTemp : nodes) {
                    if (((String)mapTemp.get("taskDefKey")).equals(taskDefKey)) {
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
    }

    @Override
    @Transactional
    public Map<String, Object> delete(String id) {
        Map<String, Object> map = new HashMap<>(16);
        map.put(UtilConsts.SUCCESS, true);
        map.put("msg", "删除成功");
        try {
            y9FormItemBindRepository.deleteById(id);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "删除失败");
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public List<Y9FormItemBind> findByItemIdAndProcDefId(String itemId, String procDefId) {
        List<Y9FormItemBind> eformTaskBinds = new ArrayList<Y9FormItemBind>();
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
    public List<Y9FormItemBind> findByItemIdAndProcDefIdAndTaskDefKey(String itemId, String procDefId,
        String taskDefKey) {
        List<Y9FormItemBind> eformTaskBinds = new ArrayList<Y9FormItemBind>();
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            if (StringUtils.isNotBlank(itemId) && StringUtils.isNotBlank(procDefId)) {
                // 查找本任务的form,在任务上设置的表单有优先权。
                List<Map<String, Object>> list = new ArrayList<>();
                // taskDefKey为空表示为办结件，需要获取最后一个任务的表单。
                if (taskDefKey == "") {
                    list = processDefinitionManager.getNodes(tenantId, procDefId, false);
                    taskDefKey = (String)list.get(list.size() - 1).get("taskDefKey");
                }
                eformTaskBinds =
                    y9FormItemBindRepository.findByItemIdAndProcDefIdAndTaskDefKey(itemId, procDefId, taskDefKey);
                if (eformTaskBinds.isEmpty()) {
                    // 再查找缺省的form。任务上没有设置表单，就用缺省表单。
                    eformTaskBinds =
                        y9FormItemBindRepository.findByItemIdAndProcDefIdAndTaskDefKeyIsNull(itemId, procDefId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return eformTaskBinds;
    }

    @Override
    public List<Y9FormItemMobileBind> findByItemIdAndProcDefIdAndTaskDefKey4Mobile(String itemId, String procDefId,
        String taskDefKey) {
        List<Y9FormItemMobileBind> eformTaskBinds = new ArrayList<Y9FormItemMobileBind>();
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            if (StringUtils.isNotBlank(itemId) && StringUtils.isNotBlank(procDefId)) {
                // 查找本任务的form,在任务上设置的表单有优先权。
                List<Map<String, Object>> list = new ArrayList<>();
                // taskDefKey为空表示为办结件，需要获取最后一个任务的表单。
                if (taskDefKey == "") {
                    list = processDefinitionManager.getNodes(tenantId, procDefId, false);
                    taskDefKey = (String)list.get(list.size() - 1).get("taskDefKey");
                }
                eformTaskBinds =
                    y9FormItemMobileBindRepository.findByItemIdAndProcDefIdAndTaskDefKey(itemId, procDefId, taskDefKey);
                if (eformTaskBinds.isEmpty()) {
                    // 再查找缺省的form。任务上没有设置表单，就用缺省表单。
                    eformTaskBinds =
                        y9FormItemMobileBindRepository.findByItemIdAndProcDefIdAndTaskDefKeyIsNull(itemId, procDefId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return eformTaskBinds;
    }

    @Override
    public List<Y9FormItemBind> findByItemIdAndProcDefIdAndTaskDefKey4Own(String itemId, String procDefId,
        String taskDefKey) {
        List<Y9FormItemBind> y9FormItemBinds = new ArrayList<Y9FormItemBind>();
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
    public List<Y9FormItemMobileBind> findByItemIdAndProcDefIdAndTaskDefKey4OwnMobile(String itemId, String procDefId,
        String taskDefKey) {
        List<Y9FormItemMobileBind> y9FormItemBinds = new ArrayList<Y9FormItemMobileBind>();
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
    public List<Y9FormItemBind> findByItemIdAndProcDefIdAndTaskDefKeyIsNull(String itemId, String procDefId) {
        List<Y9FormItemBind> eformTaskBinds = new ArrayList<Y9FormItemBind>();
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
    public Y9FormItemBind findOne(String id) {
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
    @Transactional
    public Map<String, Object> save(Y9FormItemBind eformItem) {
        Map<String, Object> map = new HashMap<>(16);
        map.put(UtilConsts.SUCCESS, false);
        map.put("msg", "保存失败");
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            eformItem.setTenantId(tenantId);
            y9FormItemBindRepository.saveAndFlush(eformItem);
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "保存成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    private final void save(Y9FormItemBind eib, String latestpdId, String formId, String itemId, String taskDefKey,
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
    public Map<String, Object> save(Y9FormItemMobileBind eformItem) {
        Map<String, Object> map = new HashMap<>(16);
        map.put(UtilConsts.SUCCESS, false);
        map.put("msg", "保存失败");
        try {
            if (StringUtils.isBlank(eformItem.getId())) {
                eformItem.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            }
            String tenantId = Y9LoginUserHolder.getTenantId();
            eformItem.setTenantId(tenantId);
            y9FormItemMobileBindRepository.saveAndFlush(eformItem);
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "保存成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}
