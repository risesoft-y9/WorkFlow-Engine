package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.api.itemadmin.TransactionWordApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.DraftEntity;
import net.risesoft.entity.ProcessParam;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.jpa.DraftEntityRepository;
import net.risesoft.service.DocumentService;
import net.risesoft.service.DraftEntityService;
import net.risesoft.service.ItemStartNodeRoleService;
import net.risesoft.service.ProcessParamService;
import net.risesoft.service.SpmApproveItemService;
import net.risesoft.service.TransactionFileService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

import jodd.util.StringUtil;
import y9.client.rest.processadmin.ProcessDefinitionApiClient;
import y9.client.rest.processadmin.RepositoryApiClient;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service(value = "draftEntityService")
public class DraftEntityServiceImpl implements DraftEntityService {

    @Autowired
    private DraftEntityRepository draftEntityRepository;

    @Autowired
    private SpmApproveItemService spmApproveitemService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private ProcessDefinitionApiClient processDefinitionManager;

    @Autowired
    private RepositoryApiClient repositoryManager;

    @Autowired
    private ProcessParamService processParamService;

    @Autowired
    private TransactionFileService transactionFileService;

    @Autowired
    private TransactionWordApi transactionWordManager;

    @Autowired
    private ItemStartNodeRoleService itemStartNodeRoleService;

    @Transactional(readOnly = false)
    @Override
    public void deleteByProcessSerialNumber(String processSerialNumber) {
        DraftEntity draftEntity = draftEntityRepository.findByProcessSerialNumber(processSerialNumber);
        if (null != draftEntity) {
            draftEntityRepository.deleteById(draftEntity.getId());
        }
    }

    @Transactional(readOnly = false)
    @Override
    public Map<String, Object> deleteDraft(String ids) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put("message", "删除成功");
        map.put(UtilConsts.SUCCESS, true);
        try {
            if (StringUtils.isNotBlank(ids)) {
                String[] id = ids.split(SysVariables.COMMA);
                for (String idTemp : id) {
                    DraftEntity draftEntity = draftEntityRepository.findById(idTemp).orElse(null);
                    String processSerialNumber = draftEntity.getProcessSerialNumber();
                    draftEntityRepository.delete(draftEntity);
                    try {
                        processParamService.deleteByProcessSerialNumber(processSerialNumber);
                        List<String> list = new ArrayList<String>();
                        list.add(processSerialNumber);
                        transactionFileService.delBatchByProcessSerialNumbers(list);
                        transactionWordManager.delBatchByProcessSerialNumbers(Y9LoginUserHolder.getTenantId(), list);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            map.put("message", "删除失败");
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public Page<DraftEntity> getDraftList(String itemId, String userId, int page, int rows, String title, boolean delFlag) {
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, Sort.by(Sort.Direction.DESC, "urgency", "draftTime"));
        Page<DraftEntity> list = null;
        title = "%" + title + "%";
        if (delFlag) {
            if (StringUtil.isAllBlank(itemId) || "".equals(itemId)) {
                list = draftEntityRepository.findByCreaterIdAndTitleLikeAndDelFlagTrue(userId, title, pageable);
            } else {
                list = draftEntityRepository.findByItemIdAndCreaterIdAndTitleLikeAndDelFlagTrue(itemId, userId, title, pageable);
            }
        } else {
            if (StringUtil.isEmpty(itemId)) {
                list = draftEntityRepository.findByCreaterIdAndTitleLikeAndDelFlagFalse(userId, title, pageable);
            } else {
                list = draftEntityRepository.findByItemIdAndCreaterIdAndTitleLikeAndDelFlagFalse(itemId, userId, title, pageable);
            }
        }
        return list;
    }

    @Override
    public Page<DraftEntity> getDraftListBySystemName(String systemName, String userId, int page, int rows, String title, boolean delFlag) {
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, Sort.by(Sort.Direction.DESC, "urgency", "draftTime"));
        Page<DraftEntity> list = null;
        title = "%" + title + "%";
        if (delFlag) {
            list = draftEntityRepository.findByTypeAndCreaterIdAndTitleLikeAndDelFlagTrue(systemName, userId, title, pageable);
        } else {
            list = draftEntityRepository.findByTypeAndCreaterIdAndTitleLikeAndDelFlagFalse(systemName, userId, title, pageable);
        }
        return list;
    }

    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> openDraft(String processSerialNumber, String itemId, boolean mobile) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = userInfo.getPersonId();

        Map<String, Object> returnMap = new HashMap<>(16);
        returnMap = spmApproveitemService.findById(itemId, returnMap);
        String processDefinitionKey = (String)returnMap.get("processDefinitionKey");
        String processDefinitionId = repositoryManager.getLatestProcessDefinitionByKey(tenantId, processDefinitionKey).getId();
        String taskDefKey = itemStartNodeRoleService.getStartTaskDefKey(itemId);
        List<Map<String, String>> routeToTasks = processDefinitionManager.getTargetNodes(tenantId, processDefinitionId, taskDefKey);
        String taskDefKeyList = "";
        String taskDefNameList = "";
        for (Map<String, String> m : routeToTasks) {
            taskDefKeyList = Y9Util.genCustomStr(taskDefKeyList, m.get("taskDefKey"));
            taskDefNameList = Y9Util.genCustomStr(taskDefNameList, m.get("taskDefName"));
        }
        DraftEntity draft = draftEntityRepository.findByProcessSerialNumber(processSerialNumber);
        ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
        returnMap.put("customItem", processParam.getCustomItem());
        String activitiUser = userId;
        returnMap = documentService.genDocumentModel(itemId, processDefinitionKey, "", taskDefKey, mobile, returnMap);
        returnMap.put("taskDefKeyList", taskDefKeyList);
        returnMap.put("taskDefNameList", taskDefNameList);
        returnMap = documentService.menuControl(itemId, processDefinitionId, taskDefKey, "", returnMap, ItemBoxTypeEnum.DRAFT.getValue());
        returnMap.put("processDefinitionId", processDefinitionId);
        returnMap.put("processDefinitionKey", processDefinitionKey);
        returnMap.put("taskDefKey", taskDefKey);
        returnMap.put("documentTitle", draft.getTitle());
        returnMap.put("activitiUser", activitiUser);
        returnMap.put("processSerialNumber", processSerialNumber);
        returnMap.put("itembox", ItemBoxTypeEnum.DRAFT.getValue());
        returnMap.put("processInstanceId", "");
        return returnMap;
    }

    @Transactional(readOnly = false)
    @Override
    public Map<String, Object> reduction(String ids) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put("message", "还原成功");
        map.put(UtilConsts.SUCCESS, true);
        try {
            if (StringUtils.isNotBlank(ids)) {
                String[] id = ids.split(",");
                for (String idTemp : id) {
                    Optional<DraftEntity> draft = draftEntityRepository.findById(idTemp);
                    if (draft != null && draft.get().getId() != null) {
                        draft.get().setDelFlag(false);
                        draftEntityRepository.save(draft.get());
                    }
                }
            }
        } catch (Exception e) {
            map.put("message", "还原失败");
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        return map;
    }

    @Transactional(readOnly = false)
    @Override
    public Map<String, Object> removeDraft(String ids) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put("message", "删除成功");
        map.put(UtilConsts.SUCCESS, true);
        try {
            if (StringUtils.isNotBlank(ids)) {
                String[] id = ids.split(SysVariables.COMMA);
                for (String idTemp : id) {
                    Optional<DraftEntity> draft = draftEntityRepository.findById(idTemp);
                    if (draft != null) {
                        draft.get().setDelFlag(true);
                        draftEntityRepository.save(draft.get());
                    }
                }
            }
        } catch (Exception e) {
            map.put("message", "删除失败");
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        return map;
    }

    @Transactional(readOnly = false)
    @Override
    public Map<String, Object> saveDraft(String itemId, String processSerialNumber, String processDefinitionKey, String number, String level, String title, String type) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put("message", "保存失败");
        map.put(UtilConsts.SUCCESS, false);
        try {
            UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
            if (StringUtils.isNotBlank(processSerialNumber)) {
                DraftEntity draft = draftEntityRepository.findByProcessSerialNumber(processSerialNumber);
                if (draft != null && draft.getId() != null) {
                    draft.setDraftTime(new Date());
                    draft.setUrgency(level == null || "".equals(level) ? "一般" : level);
                    draft.setDocNumber(number);
                    draft.setTitle(title);
                    if (StringUtils.isNotBlank(type)) {
                        draft.setType(type);
                    }
                    draft.setSerialNumber(1);
                } else {// 保存草稿
                    draft = new DraftEntity();
                    draft.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                    draft.setProcessSerialNumber(processSerialNumber);
                    draft.setItemId(itemId);
                    draft.setProcessDefinitionKey(processDefinitionKey);
                    draft.setCreaterId(userInfo.getPersonId());
                    draft.setCreater(userInfo.getName());
                    draft.setDelFlag(false);
                    draft.setDraftTime(new Date());
                    draft.setUrgency(level == null || "".equals(level) ? "一般" : level);
                    draft.setDocNumber(number);
                    draft.setTitle(title);
                    if (StringUtils.isNotBlank(type)) {
                        draft.setType(type);
                    }
                    SpmApproveItem item = spmApproveitemService.findById(itemId);
                    if (null != item) {
                        if (StringUtils.isBlank(type)) {
                            draft.setType(item.getSystemName());
                        }
                        draft.setSystemName(item.getSysLevel());
                    }
                }
                draftEntityRepository.save(draft);
            }
            map.put("message", "保存成功");
            map.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            map.put("message", "保存失败");
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        return map;
    }

    @Transactional(readOnly = false)
    @Override
    public Map<String, Object> saveDraft(String itemId, String processSerialNumber, String processDefinitionKey, String number, String level, String title, String jijian, String type) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put("message", "保存失败");
        map.put(UtilConsts.SUCCESS, false);
        try {
            UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
            if (StringUtils.isNotBlank(processSerialNumber)) {
                DraftEntity draft = draftEntityRepository.findByProcessSerialNumber(processSerialNumber);
                if (draft != null && draft.getId() != null) {
                    draft.setDraftTime(new Date());
                    draft.setUrgency(jijian == null || "".equals(jijian) ? null : "51");
                    draft.setDocNumber(number);
                    draft.setTitle(title);
                    if (StringUtils.isNotBlank(type)) {
                        draft.setType(type);
                    }
                    draft.setSerialNumber(1);
                } else {// 保存草稿
                    draft = new DraftEntity();
                    draft.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                    draft.setProcessSerialNumber(processSerialNumber);
                    draft.setItemId(itemId);
                    draft.setProcessDefinitionKey(processDefinitionKey);
                    draft.setCreaterId(userInfo.getPersonId());
                    draft.setCreater(userInfo.getName());
                    draft.setDelFlag(false);
                    draft.setDraftTime(new Date());
                    draft.setUrgency(jijian == null || "".equals(jijian) ? null : "51");
                    draft.setDocNumber(number);
                    draft.setTitle(title);
                    if (StringUtils.isNotBlank(type)) {
                        draft.setType(type);
                    }
                    SpmApproveItem item = spmApproveitemService.findById(itemId);
                    if (null != item) {
                        if (StringUtils.isBlank(type)) {
                            draft.setType(item.getSystemName());
                        }
                        draft.setSystemName(item.getSysLevel());
                    }
                }
                draftEntityRepository.save(draft);
            }
            map.put("message", "保存成功");
            map.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            map.put("message", "保存失败");
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        return map;
    }
}
