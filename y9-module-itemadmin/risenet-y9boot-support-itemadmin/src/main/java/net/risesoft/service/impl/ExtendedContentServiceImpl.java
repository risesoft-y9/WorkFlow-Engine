package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.ExtendedContent;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.ExtendedContentRepository;
import net.risesoft.service.ExtendedContentService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class ExtendedContentServiceImpl implements ExtendedContentService {

    private final ExtendedContentRepository extendedContentRepository;

    private final OrgUnitApi orgUnitApi;

    @Override
    public int countByProcSerialNumberAndCategory(String processSerialNumber, String category) {
        return extendedContentRepository.findByProcSerialNumberAndCategory(processSerialNumber, category);
    }

    @Override
    public int countByProcessSerialNumberAndUserIdAndCategory(String processSerialNumber, String userid,
        String category) {
        return extendedContentRepository.getCountByUserIdAndCategory(processSerialNumber, userid, category);
    }

    @Override
    public int countByTaskIdAndCategory(String taskId, String category) {
        return extendedContentRepository.getCountByTaskIdAndCategory(taskId, category);
    }

    @Override
    @Transactional
    public Y9Result<Object> delete(String id) {
        try {
            if (StringUtils.isNotBlank(id)) {
                extendedContentRepository.deleteById(id);
            }
            return Y9Result.successMsg("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Y9Result.failure("删除失败");
        }
    }

    @Override
    public ExtendedContent findById(String id) {
        return extendedContentRepository.findById(id).orElse(null);
    }

    @Override
    public Integer getCountPersonal(String processSerialNumber, String category, String personId) {
        return extendedContentRepository.getCountPersonal(processSerialNumber, category, personId);
    }

    @Override
    public Integer getCountPersonal(String processSerialNumber, String taskId, String category, String personId) {
        return extendedContentRepository.getCountPersonal(processSerialNumber, taskId, category, personId);
    }

    @Override
    public ExtendedContent getNewConentByProcessSerialNumber(String processSerialNumber, String category) {
        List<ExtendedContent> list = extendedContentRepository.findByPsnAndCategory(processSerialNumber, category);
        if (list != null && !list.isEmpty()) {
            return list.get(list.size() - 1);
        }
        return null;
    }

    @Override
    public ExtendedContent getResultByUserIdAndCategory(String processSerialNumber, String userid, String category) {
        return extendedContentRepository.getResultByUserIdAndCategory(processSerialNumber, userid, category);
    }

    @Override
    public List<Map<String, Object>> listContents(String processSerialNumber, String taskId, String itembox,
        String category) {
        List<Map<String, Object>> resList = new ArrayList<>();
        Map<String, Object> addableMap = new HashMap<>(16);
        addableMap.put("addable", true);
        addableMap.put("category", category);
        try {
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            List<ExtendedContent> list = extendedContentRepository.findByPsnAndCategory(processSerialNumber, category);
            if (itembox.equalsIgnoreCase(ItemBoxTypeEnum.TODO.getValue())
                || itembox.equalsIgnoreCase(ItemBoxTypeEnum.ADD.getValue())
                || itembox.equalsIgnoreCase(ItemBoxTypeEnum.DRAFT.getValue())) {
                if (list != null && !list.isEmpty()) {
                    for (ExtendedContent content : list) {
                        Map<String, Object> map = new HashMap<>(16);
                        map.put("content", content);
                        map.put("date", new SimpleDateFormat("yyyy-MM-dd").format(content.getModifyDate()));
                        map.put("editable", false);
                        if (person.getPersonId().equals(content.getUserId())) {
                            map.put("editable", true);
                            addableMap.put("addable", false);
                        }
                        resList.add(map);
                    }
                }
            } else if (itembox.equalsIgnoreCase(ItemBoxTypeEnum.DONE.getValue())
                || itembox.equalsIgnoreCase(ItemBoxTypeEnum.DOING.getValue())) {
                addableMap.put("addable", false);
                if (!list.isEmpty()) {
                    for (ExtendedContent content : list) {
                        Map<String, Object> map = new HashMap<>(16);
                        map.put("content", content);
                        map.put("date", new SimpleDateFormat("yyyy-MM-dd").format(content.getModifyDate()));
                        map.put("editable", false);
                        resList.add(map);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        resList.add(addableMap);
        return resList;
    }

    @Override
    @Transactional
    public Model newOrModifyContent(String processSerialNumber, String taskId, String category, String id,
        Model model) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        try {
            ExtendedContent extendedContent = new ExtendedContent();
            if (StringUtils.isNotBlank(id)) {
                extendedContent = this.findById(id);
            } else {
                Integer count = 0;
                if (StringUtils.isNotBlank(taskId)) {
                    count = this.getCountPersonal(processSerialNumber, taskId, category, person.getPersonId());
                } else {
                    count = this.getCountPersonal(processSerialNumber, category, person.getPersonId());
                }
                if (count >= 1) {
                    model.addAttribute("modifyContent", true);
                } else {
                    model.addAttribute("addContent", true);
                }
            }
            model.addAttribute(UtilConsts.SUCCESS, true);
            model.addAttribute("content", extendedContent);
            model.addAttribute("processSerialNumber", processSerialNumber);
            model.addAttribute("taskId", taskId);
            model.addAttribute("category", category);
        } catch (Exception e) {
            model.addAttribute(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        return model;
    }

    @Override
    @Transactional
    public Y9Result<Object> saveOrUpdate(ExtendedContent content) {
        try {
            String id = content.getId();
            Date date = new Date();
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            String tenantId = Y9LoginUserHolder.getTenantId();
            String departmentId = person.getParentId();
            OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, departmentId).getData();
            if (StringUtils.isBlank(id)) {
                ExtendedContent extendedContent = new ExtendedContent();
                extendedContent.setDepartmentName(orgUnit.getName());
                extendedContent.setDepartmentId(orgUnit.getId());
                extendedContent.setModifyDate(date);
                extendedContent.setCreateDate(date);
                extendedContent.setUserId(person.getPersonId());
                extendedContent.setUserName(person.getName());
                extendedContent.setContent(content.getContent());
                extendedContent.setModifyDate(date);
                extendedContent.setProcessInstanceId(content.getProcessInstanceId());
                extendedContent.setProcessSerialNumber(content.getProcessSerialNumber());
                extendedContent.setTaskId(content.getTaskId());
                extendedContent.setCategory(content.getCategory());
                extendedContent.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                extendedContent.setTabIndex(0);
                extendedContent.setTenantId(Y9LoginUserHolder.getTenantId());
                extendedContentRepository.save(extendedContent);
            } else {
                Optional<ExtendedContent> extendedContent = extendedContentRepository.findById(id);
                if (extendedContent.isPresent()) {
                    extendedContent.get().setContent(content.getContent());
                    extendedContent.get().setModifyDate(date);
                    extendedContentRepository.save(extendedContent.get());
                }
            }
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Y9Result.failure("保存失败");
        }

    }

    @Override
    @Transactional
    public void update(String processSerialNumber, String taskId) {
        try {
            extendedContentRepository.update(taskId, processSerialNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
