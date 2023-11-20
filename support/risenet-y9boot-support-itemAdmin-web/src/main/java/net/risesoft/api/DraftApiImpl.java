package net.risesoft.api;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.DraftApi;
import net.risesoft.api.org.PersonApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.DraftEntity;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.enums.ItemLeaveTypeEnum;
import net.risesoft.model.platform.Person;
import net.risesoft.repository.jpa.DraftEntityRepository;
import net.risesoft.repository.jpa.SpmApproveItemRepository;
import net.risesoft.service.DraftEntityService;
import net.risesoft.service.FormDataService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequestMapping(value = "/services/rest/draft")
public class DraftApiImpl implements DraftApi {

    @Autowired
    private DraftEntityService draftEntityService;

    @Autowired
    private DraftEntityRepository draftEntityRepository;

    @Autowired
    private SpmApproveItemRepository spmApproveitemRepository;

    @Autowired
    private FormDataService formDataService;

    @Autowired
    private PersonApi personManager;

    @Override
    @PostMapping(value = "/deleteDraft", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> deleteDraft(String tenantId, String userId, String ids) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map = draftEntityService.deleteDraft(ids);
        return map;
    }

    @Override
    @GetMapping(value = "/getDeleteDraftCount", produces = MediaType.APPLICATION_JSON_VALUE)
    public int getDeleteDraftCount(String tenantId, String userId, String itemId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        int count = 0;
        if (StringUtils.isBlank(itemId) || "".equals(itemId)) {
            count = draftEntityRepository.countByCreaterIdAndDelFlagTrue(userId);
        } else {
            count = draftEntityRepository.countByItemIdAndCreaterIdAndDelFlagTrue(itemId, userId);
        }
        return count;
    }

    @Override
    @GetMapping(value = "/getDraftByProcessSerialNumber", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getDraftByProcessSerialNumber(String tenantId, String userId,
        String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
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

    @Override
    @GetMapping(value = "/getDraftCount", produces = MediaType.APPLICATION_JSON_VALUE)
    public int getDraftCount(String tenantId, String userId, String itemId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        int count = 0;
        if (StringUtils.isBlank(itemId) || "".equals(itemId)) {
            count = draftEntityRepository.countByCreaterIdAndDelFlagFalse(userId);
        } else {
            count = draftEntityRepository.countByItemIdAndCreaterIdAndDelFlagFalse(itemId, userId);
        }
        return count;
    }

    @Override
    @GetMapping(value = "/getDraftCountBySystemName", produces = MediaType.APPLICATION_JSON_VALUE)
    public int getDraftCountBySystemName(String tenantId, String userId, String systemName) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        int count = draftEntityRepository.countBySystemNameAndCreaterIdAndDelFlagFalse(systemName, userId);
        return count;
    }

    @Override
    @GetMapping(value = "/getDraftList", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getDraftList(String tenantId, String userId, int page, int rows, String title,
        String itemId, boolean delFlag) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, true);
        map.put("msg", "获取草稿列表成功");
        try {
            if (StringUtils.isEmpty(title)) {
                title = "";
            }
            Page<DraftEntity> pageList = draftEntityService.getDraftList(itemId, userId, page, rows, title, delFlag);
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

    @Override
    @GetMapping(value = "/getDraftListBySystemName", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getDraftListBySystemName(String tenantId, String userId, int page, int rows,
        String title, String systemName, boolean delFlag) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, true);
        map.put("msg", "获取草稿列表成功");
        try {
            if (StringUtils.isEmpty(title)) {
                title = "";
            }
            Page<DraftEntity> pageList =
                draftEntityService.getDraftListBySystemName(systemName, userId, page, rows, title, delFlag);
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

    @Override
    @GetMapping(value = "/openDraft", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> openDraft(String tenantId, String userId, String itemId, String processSerialNumber,
        boolean mobile) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);

        Map<String, Object> map = new HashMap<>(16);
        if (StringUtils.isNotBlank(itemId) && StringUtils.isNotBlank(processSerialNumber)) {
            map = draftEntityService.openDraft(processSerialNumber, itemId, mobile);
        }
        return map;
    }

    @Override
    @PostMapping(value = "/reduction", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> reduction(String tenantId, String userId, String ids) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map = draftEntityService.reduction(ids);
        return map;
    }

    @Override
    @PostMapping(value = "/removeDraft", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> removeDraft(String tenantId, String userId, String ids) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map = draftEntityService.removeDraft(ids);
        return map;
    }

    @Override
    @PostMapping(value = "/saveDraft", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> saveDraft(String tenantId, String userId, String itemId, String processSerialNumber,
        String processDefinitionKey, String number, String level, String title) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map = draftEntityService.saveDraft(itemId, processSerialNumber, processDefinitionKey, number, level, title, "");
        return map;
    }

    @Override
    @PostMapping(value = "/saveDraft1", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> saveDraft(String tenantId, String userId, String itemId, String processSerialNumber,
        String processDefinitionKey, String number, String level, String jijian, String title) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map = draftEntityService.saveDraft(itemId, processSerialNumber, processDefinitionKey, number, level, jijian,
            title, "");
        return map;
    }

    @Override
    @PostMapping(value = "/saveDraftAndType", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> saveDraftAndType(String tenantId, String userId, String itemId,
        String processSerialNumber, String processDefinitionKey, String number, String level, String title,
        String type) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map =
            draftEntityService.saveDraft(itemId, processSerialNumber, processDefinitionKey, number, level, title, type);
        return map;
    }
}
