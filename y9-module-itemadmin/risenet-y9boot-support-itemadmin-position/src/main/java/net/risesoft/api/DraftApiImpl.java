package net.risesoft.api;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.position.Draft4PositionApi;
import net.risesoft.entity.DraftEntity;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.enums.ItemLeaveTypeEnum;
import net.risesoft.model.itemadmin.DraftModel;
import net.risesoft.model.itemadmin.OpenDataModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.DraftEntityRepository;
import net.risesoft.repository.jpa.SpmApproveItemRepository;
import net.risesoft.service.DraftEntityService;
import net.risesoft.service.FormDataService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 草稿列表接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/draft4Position")
public class DraftApiImpl implements Draft4PositionApi {

    private final DraftEntityService draftEntityService;

    private final DraftEntityRepository draftEntityRepository;

    private final SpmApproveItemRepository spmApproveitemRepository;

    private final FormDataService formDataService;

    /**
     * 根据系统名称计数
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param systemName 系统id
     * @return int
     */
    @Override
    public Y9Result<Integer> countBySystemName(String tenantId, String positionId, String systemName) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPositionId(positionId);
        int num = draftEntityRepository.countByTypeAndCreaterIdAndDelFlagFalse(systemName, positionId);
        return Y9Result.success(num);
    }

    /**
     * 彻底删除草稿
     *
     * @param tenantId 租户id
     * @param ids 草稿ids
     * @return Y9Result<Object>
     */
    @Override
    public Y9Result<Object> deleteDraft(String tenantId, String ids) {
        Y9LoginUserHolder.setTenantId(tenantId);
        draftEntityService.deleteDraft(ids);
        return Y9Result.success();
    }

    /**
     * 根据岗位id和事项id获取删除草稿统计
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @return Y9Result<Integer>
     */
    @Override
    public Y9Result<Integer> getDeleteDraftCount(String tenantId, String positionId, String itemId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        int count = 0;
        if (StringUtils.isEmpty(itemId)) {
            count = draftEntityRepository.countByCreaterIdAndDelFlagTrue(positionId);
        } else {
            count = draftEntityRepository.countByItemIdAndCreaterIdAndDelFlagTrue(itemId, positionId);
        }
        return Y9Result.success(count);
    }

    /**
     * 根据流程序列号获取草稿
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程序列号
     * @return Y9Result<DraftModel>
     */
    @Override
    public Y9Result<DraftModel> getDraftByProcessSerialNumber(String tenantId, String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        DraftEntity draftEntity = draftEntityRepository.findByProcessSerialNumber(processSerialNumber);
        DraftModel model = null;
        if (draftEntity != null) {
            model = new DraftModel();
            Y9BeanUtil.copyProperties(draftEntity, model);
        }
        return Y9Result.success(model);
    }

    /**
     * 根据岗位id和事项id获取草稿统计
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @return Y9Result<Integer>
     */
    @Override
    public Y9Result<Integer> getDraftCount(String tenantId, String positionId, String itemId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPositionId(positionId);
        int count = 0;
        if (StringUtils.isEmpty(itemId)) {
            count = draftEntityRepository.countByCreaterIdAndDelFlagFalse(positionId);
        } else {
            count = draftEntityRepository.countByItemIdAndCreaterIdAndDelFlagFalse(itemId, positionId);
        }
        return Y9Result.success(count);
    }

    /**
     * 获取草稿列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param page 页码
     * @param rows 条数
     * @param title 标题
     * @param itemId 事项id
     * @param delFlag 是否删除
     * @return Y9Page<Map<String, Object>>
     */
    @Override
    public Y9Page<Map<String, Object>> getDraftList(String tenantId, String positionId, int page, int rows,
        String title, String itemId, boolean delFlag) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPositionId(positionId);
        if (StringUtils.isEmpty(title)) {
            title = "";
        }
        Page<DraftEntity> pageList = draftEntityService.getDraftList(itemId, positionId, page, rows, title, delFlag);
        List<Map<String, Object>> draftList = new ArrayList<>();
        Map<String, Object> formDataMap = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        int number = (page - 1) * rows;
        ItemLeaveTypeEnum[] arr = ItemLeaveTypeEnum.values();
        for (DraftEntity draftEntity : pageList) {
            Map<String, Object> retMap = new HashMap<>(16);
            Optional<SpmApproveItem> spmApproveitem = spmApproveitemRepository.findById(draftEntity.getItemId());
            if (spmApproveitem != null && spmApproveitem.get().getId() != null) {
                retMap.put("itemName", spmApproveitem.get().getName());
            } else {
                retMap.put("itemName", "");
            }
            retMap.put("serialNumber", number + 1);
            retMap.put("id", draftEntity.getId());
            retMap.put("type", draftEntity.getType());
            retMap.put("creater", draftEntity.getCreater());
            retMap.put("createrId", draftEntity.getCreaterId());
            retMap.put("docNumber", draftEntity.getDocNumber());
            retMap.put("itemId", draftEntity.getItemId());
            retMap.put("processDefinitionKey", draftEntity.getProcessDefinitionKey());
            retMap.put("processInstanceId", draftEntity.getProcessInstanceId());
            retMap.put("processSerialNumber", draftEntity.getProcessSerialNumber());
            retMap.put("title", StringUtils.isEmpty(draftEntity.getTitle()) ? "无标题" : draftEntity.getTitle());
            retMap.put("urgency", draftEntity.getUrgency());
            retMap.put("draftTime", sdf.format(draftEntity.getDraftTime()));

            formDataMap = formDataService.getData(tenantId, itemId, draftEntity.getProcessSerialNumber());
            if (formDataMap.get("leaveType") != null) {
                String leaveType = (String)formDataMap.get("leaveType");
                for (ItemLeaveTypeEnum leaveTypeEnum : arr) {
                    if (leaveType.equals(leaveTypeEnum.getValue())) {
                        formDataMap.put("leaveType", leaveTypeEnum.getName());
                        break;
                    }
                }
            }
            retMap.putAll(formDataMap);

            draftList.add(retMap);
            number += 1;
        }
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), draftList);
    }

    /**
     * 获取系统名称对应的草稿列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param page page
     * @param rows rows
     * @param title 标题
     * @param systemName 系统名称
     * @param delFlag 是否删除
     * @return Y9Page<DraftModel>
     */
    @Override
    public Y9Page<DraftModel> getDraftListBySystemName(String tenantId, String positionId, int page, int rows,
        String title, String systemName, boolean delFlag) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPositionId(positionId);
        if (StringUtils.isEmpty(title)) {
            title = "";
        }
        Page<DraftEntity> pageList =
            draftEntityService.getDraftListBySystemName(systemName, positionId, page, rows, title, delFlag);
        int number = (page - 1) * rows;
        List<DraftModel> list = new ArrayList<>();
        for (DraftEntity draftEntity : pageList) {
            DraftModel model = new DraftModel();
            Y9BeanUtil.copyProperties(draftEntity, model);
            Optional<SpmApproveItem> spmApproveitem = spmApproveitemRepository.findById(draftEntity.getItemId());
            if (spmApproveitem != null && spmApproveitem.get().getId() != null) {
                model.setItemName(spmApproveitem.get().getName());
            } else {
                model.setItemName("");
            }
            model.setSerialNumber(number + 1);
            model.setTitle(StringUtils.isEmpty(draftEntity.getTitle()) ? "无标题" : draftEntity.getTitle());
            list.add(model);
            number += 1;
        }
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), list);
    }

    /**
     * 编辑草稿
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param mobile 是否手机端
     * @return Y9Result<OpenDataModel>
     */
    @Override
    public Y9Result<OpenDataModel> openDraft4Position(String tenantId, String positionId, String itemId,
        String processSerialNumber, boolean mobile) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPositionId(positionId);
        OpenDataModel model = null;
        if (StringUtils.isNotBlank(itemId) && StringUtils.isNotBlank(processSerialNumber)) {
            model = draftEntityService.openDraft(processSerialNumber, itemId, mobile);
        }
        return Y9Result.success(model);
    }

    /**
     * 还原草稿
     *
     * @param tenantId 租户id
     * @param ids 草稿ids
     * @return Y9Result<Object>
     */
    @Override
    public Y9Result<Object> reduction(String tenantId, String ids) {
        Y9LoginUserHolder.setTenantId(tenantId);
        draftEntityService.reduction(ids);
        return Y9Result.success();
    }

    /**
     * 删除草稿
     *
     * @param tenantId 租户id
     * @param ids 草稿ids
     * @return Y9Result<Object>
     */
    @Override
    public Y9Result<Object> removeDraft(String tenantId, String ids) {
        Y9LoginUserHolder.setTenantId(tenantId);
        draftEntityService.removeDraft(ids);
        return Y9Result.success();
    }

    /**
     * 保存草稿
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param processDefinitionKey 流程定义key
     * @param number 编号
     * @param level 紧急程度
     * @param title 标题
     * @return Y9Result<Object>
     */
    @Override
    public Y9Result<Object> saveDraft(String tenantId, String positionId, String itemId, String processSerialNumber,
        String processDefinitionKey, String number, String level, String title) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPositionId(positionId);
        draftEntityService.saveDraft(itemId, processSerialNumber, processDefinitionKey, number, level, title, "");
        return Y9Result.successMsg("保存成功");
    }
}
