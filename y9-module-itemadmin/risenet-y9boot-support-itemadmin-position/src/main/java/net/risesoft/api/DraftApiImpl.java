package net.risesoft.api;

import lombok.RequiredArgsConstructor;
import net.risesoft.api.itemadmin.position.Draft4PositionApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.DraftEntity;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.enums.ItemLeaveTypeEnum;
import net.risesoft.repository.jpa.DraftEntityRepository;
import net.risesoft.repository.jpa.SpmApproveItemRepository;
import net.risesoft.service.DraftEntityService;
import net.risesoft.service.FormDataService;
import net.risesoft.y9.Y9LoginUserHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
     * @param tenantId   租户id
     * @param positionId 岗位id
     * @param systemName 系统id
     * @return
     */
    @Override
    @GetMapping(value = "/countBySystemName", produces = MediaType.APPLICATION_JSON_VALUE)
    public int countBySystemName(String tenantId, String positionId, String systemName) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPositionId(positionId);
        int count = draftEntityRepository.countByTypeAndCreaterIdAndDelFlagFalse(systemName, positionId);
        return count;
    }

    /**
     * 彻底删除草稿
     *
     * @param tenantId 租户id
     * @param ids      草稿ids
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @PostMapping(value = "/deleteDraft", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> deleteDraft(String tenantId, String ids) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map = draftEntityService.deleteDraft(ids);
        return map;
    }

    /**
     * 根据岗位id和事项id获取删除草稿统计
     *
     * @param tenantId   租户id
     * @param positionId 岗位id
     * @param itemId     事项id
     * @return int
     */
    @Override
    @GetMapping(value = "/getDeleteDraftCount", produces = MediaType.APPLICATION_JSON_VALUE)
    public int getDeleteDraftCount(String tenantId, String positionId, String itemId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        int count = 0;
        if (StringUtils.isEmpty(itemId)) {
            count = draftEntityRepository.countByCreaterIdAndDelFlagTrue(positionId);
        } else {
            count = draftEntityRepository.countByItemIdAndCreaterIdAndDelFlagTrue(itemId, positionId);
        }
        return count;
    }

    /**
     * 根据流程序列号获取草稿
     *
     * @param tenantId            租户id
     * @param processSerialNumber 流程序列号
     * @return Map
     */
    @Override
    @GetMapping(value = "/getDraftByProcessSerialNumber", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getDraftByProcessSerialNumber(String tenantId, String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> retMap = new HashMap<>(16);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        DraftEntity draftEntity = draftEntityRepository.findByProcessSerialNumber(processSerialNumber);
        if (draftEntity != null) {
            retMap.put("id", draftEntity.getId());
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
        }
        return retMap;
    }

    /**
     * 根据岗位id和事项id获取草稿统计
     *
     * @param tenantId   租户id
     * @param positionId 岗位id
     * @param itemId     事项id
     * @return int
     */
    @Override
    @GetMapping(value = "/getDraftCount", produces = MediaType.APPLICATION_JSON_VALUE)
    public int getDraftCount(String tenantId, String positionId, String itemId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPositionId(positionId);
        int count = 0;
        if (StringUtils.isEmpty(itemId)) {
            count = draftEntityRepository.countByCreaterIdAndDelFlagFalse(positionId);
        } else {
            count = draftEntityRepository.countByItemIdAndCreaterIdAndDelFlagFalse(itemId, positionId);
        }
        return count;
    }

    /**
     * 获取草稿列表
     *
     * @param tenantId   租户id
     * @param positionId 岗位id
     * @param page       页码
     * @param rows       条数
     * @param title      标题
     * @param itemId     事项id
     * @param delFlag    是否删除
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping(value = "/getDraftList", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getDraftList(String tenantId, String positionId, int page, int rows, String title,
                                            String itemId, boolean delFlag) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPositionId(positionId);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, true);
        map.put("msg", "获取草稿列表成功");
        try {
            if (StringUtils.isEmpty(title)) {
                title = "";
            }
            Page<DraftEntity> pageList =
                    draftEntityService.getDraftList(itemId, positionId, page, rows, title, delFlag);
            List<Map<String, Object>> draftList = new ArrayList<Map<String, Object>>();
            Map<String, Object> formDataMap = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            int number = (page - 1) * rows;
            ItemLeaveTypeEnum[] arr = ItemLeaveTypeEnum.values();
            for (DraftEntity draftEntity : pageList) {
                Map<String, Object> retMap = new HashMap<String, Object>(16);
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
                    String leaveType = (String) formDataMap.get("leaveType");
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
            map.put("currpage", page);
            map.put("totalpage", pageList.getTotalPages());
            map.put("total", pageList.getTotalElements());
            map.put("rows", draftList);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "获取草稿列表失败");
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 获取系统名称对应的草稿列表
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param page page
     * @param rows rows
     * @param title 标题
     * @param systemName 系统名称
     * @param delFlag 是否删除
     * @return
     */
    @Override
    @GetMapping(value = "/getDraftListBySystemName", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getDraftListBySystemName(String tenantId, String positionId, int page, int rows,
                                                        String title, String systemName, boolean delFlag) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPositionId(positionId);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, true);
        map.put("msg", "获取草稿列表成功");
        try {
            if (StringUtils.isEmpty(title)) {
                title = "";
            }
            Page<DraftEntity> pageList =
                    draftEntityService.getDraftListBySystemName(systemName, positionId, page, rows, title, delFlag);
            List<Map<String, Object>> draftList = new ArrayList<Map<String, Object>>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            int number = (page - 1) * rows;
            for (DraftEntity draftEntity : pageList) {
                Map<String, Object> retMap = new HashMap<String, Object>(16);
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
                draftList.add(retMap);
                number += 1;
            }
            map.put("currpage", page);
            map.put("totalpage", pageList.getTotalPages());
            map.put("total", pageList.getTotalElements());
            map.put("rows", draftList);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "获取草稿列表失败");
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 编辑草稿
     *
     * @param tenantId            租户id
     * @param positionId          岗位id
     * @param itemId              事项id
     * @param processSerialNumber 流程编号
     * @param mobile              是否手机端
     * @return Map<String, Object>
     */
    @Override
    @GetMapping(value = "/openDraft4Position", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> openDraft4Position(String tenantId, String positionId, String itemId,
                                                  String processSerialNumber, boolean mobile) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPositionId(positionId);
        Map<String, Object> map = new HashMap<>(16);
        if (StringUtils.isNotBlank(itemId) && StringUtils.isNotBlank(processSerialNumber)) {
            map = draftEntityService.openDraft(processSerialNumber, itemId, mobile);
        }
        return map;
    }

    /**
     * 还原草稿
     *
     * @param tenantId 租户id
     * @param ids      草稿ids
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @PostMapping(value = "/reduction", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> reduction(String tenantId, String ids) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map = draftEntityService.reduction(ids);
        return map;
    }

    /**
     * 删除草稿
     *
     * @param tenantId 租户id
     * @param ids      草稿ids
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @PostMapping(value = "/removeDraft", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> removeDraft(String tenantId, String ids) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map = draftEntityService.removeDraft(ids);
        return map;
    }

    /**
     * 保存草稿
     *
     * @param tenantId             租户id
     * @param positionId           岗位id
     * @param itemId               事项id
     * @param processSerialNumber  流程编号
     * @param processDefinitionKey 流程定义key
     * @param number               编号
     * @param level                紧急程度
     * @param title                标题
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @PostMapping(value = "/saveDraft", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> saveDraft(String tenantId, String positionId, String itemId, String processSerialNumber,
                                         String processDefinitionKey, String number, String level, String title) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPositionId(positionId);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map = draftEntityService.saveDraft(itemId, processSerialNumber, processDefinitionKey, number, level, title, "");
        return map;
    }
}
