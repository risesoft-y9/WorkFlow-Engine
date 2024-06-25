package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.DraftEntity;
import net.risesoft.entity.ProcessParam;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.Position;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.jpa.DraftEntityRepository;
import net.risesoft.service.DocumentService;
import net.risesoft.service.DraftEntityService;
import net.risesoft.service.ItemStartNodeRoleService;
import net.risesoft.service.ProcessParamService;
import net.risesoft.service.SpmApproveItemService;
import net.risesoft.service.TransactionFileService;
import net.risesoft.service.TransactionWordService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

import jodd.util.StringUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class DraftEntityServiceImpl implements DraftEntityService {

    private final DraftEntityRepository draftEntityRepository;

    private final SpmApproveItemService spmApproveitemService;

    private final DocumentService documentService;

    private final ProcessDefinitionApi processDefinitionManager;

    private final RepositoryApi repositoryManager;

    private final ProcessParamService processParamService;

    private final TransactionFileService transactionFileService;

    private final TransactionWordService transactionWordService;

    private final ItemStartNodeRoleService itemStartNodeRoleService;

    private final PositionApi positionManager;

    @Transactional
    @Override
    public void deleteByProcessSerialNumber(String processSerialNumber) {
        DraftEntity draftEntity = draftEntityRepository.findByProcessSerialNumber(processSerialNumber);
        if (null != draftEntity) {
            draftEntityRepository.deleteById(draftEntity.getId());
        }
    }

    @Transactional
    @Override
    public Map<String, Object> deleteDraft(String ids) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("message", "删除成功");
        map.put(UtilConsts.SUCCESS, true);
        try {
            if (StringUtils.isNotBlank(ids)) {
                String[] id = ids.split(SysVariables.COMMA);
                for (String idTemp : id) {
                    DraftEntity draftEntity = draftEntityRepository.findById(idTemp).orElse(null);
                    assert draftEntity != null;
                    String processSerialNumber = draftEntity.getProcessSerialNumber();
                    draftEntityRepository.delete(draftEntity);
                    try {
                        processParamService.deleteByProcessSerialNumber(processSerialNumber);
                        List<String> list = new ArrayList<>();
                        list.add(processSerialNumber);
                        transactionFileService.delBatchByProcessSerialNumbers(list);
                        transactionWordService.delBatchByProcessSerialNumbers(list);
                    } catch (Exception e) {
                        LOGGER.error("删除流程参数失败", e);
                    }
                }
            }
        } catch (Exception e) {
            map.put("message", "删除失败");
            map.put(UtilConsts.SUCCESS, false);
            LOGGER.error("删除草稿失败", e);
        }
        return map;
    }

    @Override
    public Page<DraftEntity> getDraftList(String itemId, String userId, int page, int rows, String title,
        boolean delFlag) {
        PageRequest pageable =
            PageRequest.of(page > 0 ? page - 1 : 0, rows, Sort.by(Sort.Direction.DESC, "urgency", "draftTime"));
        Page<DraftEntity> list;
        title = "%" + title + "%";
        if (delFlag) {
            if (StringUtil.isEmpty(itemId)) {
                list = draftEntityRepository.findByCreaterIdAndTitleLikeAndDelFlagTrue(userId, title, pageable);
            } else {
                list = draftEntityRepository.findByItemIdAndCreaterIdAndTitleLikeAndDelFlagTrue(itemId, userId, title,
                    pageable);
            }
        } else {
            if (StringUtil.isEmpty(itemId)) {
                list = draftEntityRepository.findByCreaterIdAndTitleLikeAndDelFlagFalse(userId, title, pageable);
            } else {
                list = draftEntityRepository.findByItemIdAndCreaterIdAndTitleLikeAndDelFlagFalse(itemId, userId, title,
                    pageable);
            }
        }
        return list;
    }

    @Override
    public Page<DraftEntity> getDraftListBySystemName(String systemName, String userId, int page, int rows,
        String title, boolean delFlag) {
        PageRequest pageable =
            PageRequest.of(page > 0 ? page - 1 : 0, rows, Sort.by(Sort.Direction.DESC, "urgency", "draftTime"));
        Page<DraftEntity> list;
        title = "%" + title + "%";
        if (delFlag) {
            list = draftEntityRepository.findByTypeAndCreaterIdAndTitleLikeAndDelFlagTrue(systemName, userId, title,
                pageable);
        } else {
            list = draftEntityRepository.findByTypeAndCreaterIdAndTitleLikeAndDelFlagFalse(systemName, userId, title,
                pageable);
        }
        return list;
    }

    @Override
    @Transactional
    public Map<String, Object> openDraft(String processSerialNumber, String itemId, boolean mobile) {
        String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
        Map<String, Object> returnMap = new HashMap<>(16);
        returnMap = spmApproveitemService.findById(itemId, returnMap);
        String processDefinitionKey = (String)returnMap.get("processDefinitionKey");
        String processDefinitionId =
            repositoryManager.getLatestProcessDefinitionByKey(tenantId, processDefinitionKey).getId();
        String taskDefKey = itemStartNodeRoleService.getStartTaskDefKey(itemId);
        List<Map<String, String>> routeToTasks =
            processDefinitionManager.getTargetNodes(tenantId, processDefinitionId, taskDefKey);
        String taskDefKeyList = "";
        String taskDefNameList = "";
        for (Map<String, String> m : routeToTasks) {
            taskDefKeyList = Y9Util.genCustomStr(taskDefKeyList, m.get("taskDefKey"));
            taskDefNameList = Y9Util.genCustomStr(taskDefNameList, m.get("taskDefName"));
        }
        DraftEntity draft = draftEntityRepository.findByProcessSerialNumber(processSerialNumber);
        ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
        returnMap.put("customItem", processParam.getCustomItem());
        returnMap = documentService.genDocumentModel(itemId, processDefinitionKey, "", taskDefKey, mobile, returnMap);
        returnMap.put("taskDefKeyList", taskDefKeyList);
        returnMap.put("taskDefNameList", taskDefNameList);
        returnMap = documentService.menuControl(itemId, processDefinitionId, taskDefKey, "", returnMap,
            ItemBoxTypeEnum.DRAFT.getValue());
        returnMap.put("processDefinitionId", processDefinitionId);
        returnMap.put("processDefinitionKey", processDefinitionKey);
        returnMap.put("taskDefKey", taskDefKey);
        returnMap.put("documentTitle", draft.getTitle());
        returnMap.put("activitiUser", positionId);
        returnMap.put("processSerialNumber", processSerialNumber);
        returnMap.put("itembox", ItemBoxTypeEnum.DRAFT.getValue());
        returnMap.put("processInstanceId", "");
        return returnMap;
    }

    @Transactional
    @Override
    public Map<String, Object> reduction(String ids) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("message", "还原成功");
        map.put(UtilConsts.SUCCESS, true);
        try {
            if (StringUtils.isNotBlank(ids)) {
                String[] id = ids.split(",");
                for (String idTemp : id) {
                    Optional<DraftEntity> draft = draftEntityRepository.findById(idTemp);
                    if (draft.isPresent() && draft.get().getId() != null) {
                        draft.get().setDelFlag(false);
                        draftEntityRepository.save(draft.get());
                    }
                }
            }
        } catch (Exception e) {
            map.put("message", "还原失败");
            map.put(UtilConsts.SUCCESS, false);
            LOGGER.error("还原草稿失败", e);
        }
        return map;
    }

    @Transactional
    @Override
    public Map<String, Object> removeDraft(String ids) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("message", "删除成功");
        map.put(UtilConsts.SUCCESS, true);
        try {
            if (StringUtils.isNotBlank(ids)) {
                String[] id = ids.split(SysVariables.COMMA);
                for (String idTemp : id) {
                    Optional<DraftEntity> draft = draftEntityRepository.findById(idTemp);
                    if (draft.isPresent()) {
                        draft.get().setDelFlag(true);
                        draftEntityRepository.save(draft.get());
                    }
                }
            }
        } catch (Exception e) {
            map.put("message", "删除失败");
            map.put(UtilConsts.SUCCESS, false);
            LOGGER.error("删除草稿失败", e);
        }
        return map;
    }

    @Transactional
    @Override
    public Map<String, Object> saveDraft(String itemId, String processSerialNumber, String processDefinitionKey,
        String number, String level, String title, String type) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("message", "保存失败");
        map.put(UtilConsts.SUCCESS, false);
        try {
            if (StringUtils.isNotBlank(processSerialNumber)) {
                DraftEntity draft = draftEntityRepository.findByProcessSerialNumber(processSerialNumber);
                String urgency = level == null || level.isEmpty() ? "一般" : level;
                if (draft != null && draft.getId() != null) {
                    draft.setDraftTime(new Date());
                    draft.setUrgency(urgency);
                    draft.setDocNumber(number);
                    draft.setTitle(title);
                    if (StringUtils.isNotBlank(type)) {
                        draft.setType(type);
                    }
                    draft.setSerialNumber(1);
                } else {
                    // 保存草稿
                    draft = new DraftEntity();
                    draft.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                    draft.setProcessSerialNumber(processSerialNumber);
                    draft.setItemId(itemId);
                    draft.setProcessDefinitionKey(processDefinitionKey);
                    draft.setCreaterId(Y9LoginUserHolder.getPositionId());
                    Position position = positionManager
                        .get(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId()).getData();
                    draft.setCreater(position.getName());
                    draft.setDelFlag(false);
                    draft.setDraftTime(new Date());
                    draft.setUrgency(urgency);
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
            LOGGER.error("保存草稿失败", e);
        }
        return map;
    }

    @Transactional
    @Override
    public Map<String, Object> saveDraft(String itemId, String processSerialNumber, String processDefinitionKey,
        String number, String level, String title, String jijian, String type) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("message", "保存失败");
        map.put(UtilConsts.SUCCESS, false);
        try {
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            if (StringUtils.isNotBlank(processSerialNumber)) {
                DraftEntity draft = draftEntityRepository.findByProcessSerialNumber(processSerialNumber);
                if (draft != null && draft.getId() != null) {
                    draft.setDraftTime(new Date());
                    draft.setUrgency(jijian == null || jijian.isEmpty() ? null : "51");
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
                    draft.setCreaterId(person.getPersonId());
                    draft.setCreater(person.getName());
                    draft.setDelFlag(false);
                    draft.setDraftTime(new Date());
                    draft.setUrgency(jijian == null || jijian.isEmpty() ? null : "51");
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
            LOGGER.error("保存草稿失败", e);
        }
        return map;
    }
}
