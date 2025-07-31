package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.entity.DraftEntity;
import net.risesoft.entity.Item;
import net.risesoft.entity.ProcessParam;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.OpenDataModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.DraftEntityRepository;
import net.risesoft.service.DocumentService;
import net.risesoft.service.DraftEntityService;
import net.risesoft.service.ItemService;
import net.risesoft.service.ProcessParamService;
import net.risesoft.service.TransactionFileService;
import net.risesoft.service.TransactionWordService;
import net.risesoft.service.config.ItemStartNodeRoleService;
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

    private final ItemService spmApproveitemService;

    private final DocumentService documentService;

    private final ProcessDefinitionApi processDefinitionApi;

    private final RepositoryApi repositoryApi;

    private final ProcessParamService processParamService;

    private final TransactionFileService transactionFileService;

    private final TransactionWordService transactionWordService;

    private final ItemStartNodeRoleService itemStartNodeRoleService;

    private final OrgUnitApi orgUnitApi;

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
    public void deleteDraft(String ids) {
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
    }

    @Override
    @Transactional
    public OpenDataModel openDraft(String processSerialNumber, String itemId, boolean mobile) {
        String tenantId = Y9LoginUserHolder.getTenantId(), orgUnitId = Y9LoginUserHolder.getOrgUnitId();
        OpenDataModel model = new OpenDataModel();
        Item item = spmApproveitemService.findById(itemId);
        model.setItemId(itemId);
        model.setProcessDefinitionKey(item.getWorkflowGuid());
        String processDefinitionKey = item.getWorkflowGuid();
        String processDefinitionId =
            repositoryApi.getLatestProcessDefinitionByKey(tenantId, processDefinitionKey).getData().getId();
        String taskDefKey = itemStartNodeRoleService.getStartTaskDefKey(itemId);
        List<TargetModel> routeToTasks =
            processDefinitionApi.getTargetNodes(tenantId, processDefinitionId, taskDefKey).getData();
        String taskDefKeyList = "";
        String taskDefNameList = "";
        for (TargetModel targetModel : routeToTasks) {
            taskDefKeyList = Y9Util.genCustomStr(taskDefKeyList, targetModel.getTaskDefKey());
            taskDefNameList = Y9Util.genCustomStr(taskDefNameList, targetModel.getTaskDefName());
        }
        DraftEntity draft = draftEntityRepository.findByProcessSerialNumber(processSerialNumber);
        ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
        model.setCustomItem(processParam.getCustomItem());

        model = documentService.genDocumentModel(itemId, processDefinitionKey, "", taskDefKey, mobile, model);
        model = documentService.menuControl(itemId, processDefinitionId, taskDefKey, "", model,
            ItemBoxTypeEnum.DRAFT.getValue());

        model.setProcessDefinitionId(processDefinitionId);
        model.setProcessInstanceId("");
        model.setProcessSerialNumber(processSerialNumber);
        model.setTaskDefKey(taskDefKey);
        model.setTitle(draft.getTitle());
        model.setActivitiUser(orgUnitId);
        model.setItembox(ItemBoxTypeEnum.DRAFT.getValue());
        return model;
    }

    @Override
    public Page<DraftEntity> pageDraftList(String itemId, String userId, int page, int rows, String title,
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
    public Page<DraftEntity> pageDraftListBySystemName(String systemName, String userId, int page, int rows,
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

    @Transactional
    @Override
    public void reduction(String ids) {
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
    }

    @Transactional
    @Override
    public void removeDraft(String ids) {
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
    }

    @Transactional
    @Override
    public void saveDraft(String itemId, String processSerialNumber, String processDefinitionKey, String number,
        String level, String title, String type) {
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
                draft.setCreaterId(Y9LoginUserHolder.getOrgUnitId());
                OrgUnit orgUnit = orgUnitApi
                    .getOrgUnitPersonOrPosition(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getOrgUnitId())
                    .getData();
                draft.setCreater(orgUnit.getName());
                draft.setDelFlag(false);
                draft.setDraftTime(new Date());
                draft.setUrgency(urgency);
                draft.setDocNumber(number);
                draft.setTitle(title);
                if (StringUtils.isNotBlank(type)) {
                    draft.setType(type);
                }
                Item item = spmApproveitemService.findById(itemId);
                if (null != item) {
                    if (StringUtils.isBlank(type)) {
                        draft.setType(item.getSystemName());
                    }
                    draft.setSystemName(item.getSysLevel());
                }
            }
            draftEntityRepository.save(draft);
        }
    }

    @Transactional
    @Override
    public Y9Result<Object> saveDraft(String itemId, String processSerialNumber, String processDefinitionKey,
        String number, String level, String title, String jijian, String type) {
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
                    Item item = spmApproveitemService.findById(itemId);
                    if (null != item) {
                        if (StringUtils.isBlank(type)) {
                            draft.setType(item.getSystemName());
                        }
                        draft.setSystemName(item.getSysLevel());
                    }
                }
                draftEntityRepository.save(draft);
            }
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            LOGGER.error("保存草稿失败", e);
            return Y9Result.failure("保存草稿失败");
        }
    }
}
