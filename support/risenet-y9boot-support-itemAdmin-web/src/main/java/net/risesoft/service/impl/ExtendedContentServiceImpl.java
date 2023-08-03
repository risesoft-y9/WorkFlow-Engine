package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import net.risesoft.api.org.OrgUnitApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.ExtendedContent;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.OrgUnit;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.jpa.ExtendedContentRepository;
import net.risesoft.service.ExtendedContentService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Service(value = "extendedContentService")
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class ExtendedContentServiceImpl implements ExtendedContentService {

    @Autowired
    private ExtendedContentRepository extendedContentRepository;

    @Autowired
    private OrgUnitApi orgUnitManager;

    @Override
    public List<Map<String, Object>> contentList(String processSerialNumber, String taskId, String itembox,
        String category) {
        List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
        Map<String, Object> addableMap = new HashMap<String, Object>(16);
        addableMap.put("addable", true);
        addableMap.put("category", category);
        try {
            UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
            List<ExtendedContent> list = extendedContentRepository.findByPsnAndCategory(processSerialNumber, category);
            if (ItemBoxTypeEnum.TODO.getValue().equalsIgnoreCase(itembox)
                || ItemBoxTypeEnum.ADD.getValue().equalsIgnoreCase(itembox)
                || ItemBoxTypeEnum.DRAFT.getValue().equalsIgnoreCase(itembox)) {
                if (list != null && list.size() > 0) {
                    for (ExtendedContent content : list) {
                        Map<String, Object> map = new HashMap<String, Object>(16);
                        map.put("content", content);
                        map.put("date", new SimpleDateFormat("yyyy-MM-dd").format(content.getModifyDate()));
                        map.put("editable", false);
                        if (userInfo.getPersonId().equals(content.getUserId())) {
                            // 内容的任务id等于当前任务的id，且内容创建人等于当前人id，则可编辑
                            map.put("editable", true);
                            addableMap.put("addable", false);
                        }
                        resList.add(map);
                    }
                }
            } else if (ItemBoxTypeEnum.DONE.getValue().equalsIgnoreCase(itembox)
                || ItemBoxTypeEnum.DOING.getValue().equalsIgnoreCase(itembox)) {
                addableMap.put("addable", false);
                if (list.size() > 0) {
                    for (ExtendedContent content : list) {
                        Map<String, Object> map = new HashMap<String, Object>(16);
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
    @Transactional(readOnly = false)
    public Map<String, Object> delete(String id) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, true);
        map.put("msg", "删除成功");
        try {
            if (StringUtils.isNotBlank(id)) {
                extendedContentRepository.deleteById(id);
            }
        } catch (Exception e) {
            map.put("msg", "删除失败");
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public ExtendedContent findById(String id) {
        return extendedContentRepository.findById(id).orElse(null);
    }

    @Override
    public int findByProcSerialNumberAndCategory(String processSerialNumber, String category) {
        return extendedContentRepository.findByProcSerialNumberAndCategory(processSerialNumber, category);
    }

    @Override
    public int getCountByTaskIdAndCategory(String taskId, String category) {
        return extendedContentRepository.getCountByTaskIdAndCategory(taskId, category);
    }

    @Override
    public int getCountByUserIdAndCategory(String processSerialNumber, String userid, String category) {
        return extendedContentRepository.getCountByUserIdAndCategory(processSerialNumber, userid, category);
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
        if (list != null && list.size() > 0) {
            return list.get(list.size() - 1);
        }
        return null;
    }

    @Override
    public ExtendedContent getResultByUserIdAndCategory(String processSerialNumber, String userid, String category) {
        return extendedContentRepository.getResultByUserIdAndCategory(processSerialNumber, userid, category);
    }

    @Override
    @Transactional(readOnly = false)
    public Model newOrModifyContent(String processSerialNumber, String taskId, String category, String id,
        Model model) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        try {
            ExtendedContent extendedContent = new ExtendedContent();
            if (StringUtils.isNotBlank(id)) {
                extendedContent = this.findById(id);
            } else {
                Integer count = 0;
                if (StringUtils.isNotBlank(taskId)) {
                    count = this.getCountPersonal(processSerialNumber, taskId, category, userInfo.getPersonId());
                } else {
                    count = this.getCountPersonal(processSerialNumber, category, userInfo.getPersonId());
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
    @Transactional(readOnly = false)
    public Map<String, Object> saveOrUpdate(ExtendedContent content) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, true);
        try {
            String id = content.getId();
            Date date = new Date();
            UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
            String tenantId = Y9LoginUserHolder.getTenantId();
            String departmentId = userInfo.getParentId();
            OrgUnit orgUnit = orgUnitManager.getOrgUnit(tenantId, departmentId);
            if (StringUtils.isBlank(id)) {
                ExtendedContent extendedContent = new ExtendedContent();
                extendedContent.setDepartmentName(orgUnit.getName());
                extendedContent.setDepartmentId(orgUnit.getId());
                extendedContent.setModifyDate(date);
                extendedContent.setCreateDate(date);
                extendedContent.setUserId(userInfo.getPersonId());
                extendedContent.setUserName(userInfo.getName());
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
                extendedContent.get().setContent(content.getContent());
                extendedContent.get().setModifyDate(date);
                extendedContentRepository.save(extendedContent.get());
            }
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        return map;
    }

    @Override
    @Transactional(readOnly = false)
    public void update(String processSerialNumber, String taskId) {
        try {
            extendedContentRepository.update(taskId, processSerialNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
