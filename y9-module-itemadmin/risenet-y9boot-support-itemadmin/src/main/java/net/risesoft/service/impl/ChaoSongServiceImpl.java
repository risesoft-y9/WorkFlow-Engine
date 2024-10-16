package net.risesoft.service.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;


import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ItemSmsHttpApi;
import net.risesoft.api.itemadmin.ItemTodoTaskApi;
import net.risesoft.api.platform.customgroup.CustomGroupApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.OrganizationApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.processadmin.HistoricProcessApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.ChaoSong;
import net.risesoft.entity.ErrorLog;
import net.risesoft.entity.ProcessParam;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.enums.ItemPermissionEnum;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ChaoSongModel;
import net.risesoft.model.itemadmin.ErrorLogModel;
import net.risesoft.model.itemadmin.OpenDataModel;
import net.risesoft.model.platform.CustomGroupMember;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Position;
import net.risesoft.model.processadmin.HistoricProcessInstanceModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.nosql.elastic.entity.OfficeDoneInfo;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.ChaoSongRepository;
import net.risesoft.service.AsyncHandleService;
import net.risesoft.service.ChaoSongService;
import net.risesoft.service.DocumentService;
import net.risesoft.service.ErrorLogService;
import net.risesoft.service.OfficeDoneInfoService;
import net.risesoft.service.OfficeFollowService;
import net.risesoft.service.ProcessParamService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.app.y9itemadmin.Y9ItemAdminProperties;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class ChaoSongServiceImpl implements ChaoSongService {

    private final ChaoSongRepository chaoSongRepository;

    private final DocumentService documentService;

    private final ProcessParamService processParamService;

    private final TaskApi taskApi;

    private final HistoricProcessApi historicProcessApi;

    private final OrganizationApi organizationApi;

    private final PositionApi positionApi;

    private final OrgUnitApi orgUnitApi;

    private final ItemSmsHttpApi smsHttpApi;

    private final OfficeDoneInfoService officeDoneInfoService;

    private final Y9ItemAdminProperties y9ItemAdminProperties;

    private final OfficeFollowService officeFollowService;

    private final AsyncHandleService asyncHandleService;

    private final ErrorLogService errorLogService;

    private final ItemTodoTaskApi todotaskApi;

    private final CustomGroupApi customGroupApi;

    @Override
    @Transactional
    public void changeChaoSongState(String id, String type) {
        String opinionState = "";
        if (ItemBoxTypeEnum.ADD.getValue().equals(type)) {
            opinionState = "1";
        }
        ChaoSong chaoSong = chaoSongRepository.findById(id).orElse(null);
        assert chaoSong != null;
        chaoSong.setOpinionState(opinionState);
        chaoSongRepository.save(chaoSong);
    }

    @Override
    @Transactional
    public void changeStatus(String id) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ChaoSong chaoSong = chaoSongRepository.findById(id).orElse(null);
        if (chaoSong != null) {
            chaoSong.setStatus(1);
            chaoSong.setReadTime(sdf.format(new Date()));
            chaoSongRepository.save(chaoSong);
            try {
                todotaskApi.deleteTodoTask(Y9LoginUserHolder.getTenantId(), id);
            } catch (Exception e) {
                LOGGER.error("删除待办任务失败", e);
            }
        }
    }

    @Override
    @Transactional
    public void changeStatus(String[] ids) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (String id : ids) {
            ChaoSong chaoSong = chaoSongRepository.findById(id).orElse(null);
            if (chaoSong != null) {
                chaoSong.setStatus(1);
                chaoSong.setReadTime(sdf.format(new Date()));
                chaoSongRepository.save(chaoSong);
            }
            try {
                todotaskApi.deleteTodoTask(Y9LoginUserHolder.getTenantId(), id);
            } catch (Exception e) {
                LOGGER.error("删除待办任务失败", e);
            }
        }
    }

    @Override
    public int countAllByUserId(String userId) {
        return chaoSongRepository.countByUserId(userId);
    }

    @Override
    public int countByProcessInstanceId(String userId, String processInstanceId) {
        return chaoSongRepository.countBySenderIdIsNotAndProcessInstanceId(userId, processInstanceId);
    }

    @Override
    public int countByUserIdAndProcessInstanceId(String userId, String processInstanceId) {
        return chaoSongRepository.countBySenderIdAndProcessInstanceId(userId, processInstanceId);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        chaoSongRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByIds(String[] ids) {
        for (String id : ids) {
            chaoSongRepository.deleteById(id);
            try {
                todotaskApi.deleteTodoTask(Y9LoginUserHolder.getTenantId(), id);
            } catch (Exception e) {
                LOGGER.error("删除待办任务失败", e);
            }
        }
    }

    @Override
    @Transactional
    public boolean deleteByProcessInstanceId(String processInstanceId) {
        chaoSongRepository.deleteByProcessInstanceId(processInstanceId);
        return true;
    }

    @Override
    public OpenDataModel detail(String processInstanceId, Integer status, boolean mobile) {
        OpenDataModel model = new OpenDataModel();
        String tenantId = Y9LoginUserHolder.getTenantId();
        String itembox = ItemBoxTypeEnum.DOING.getValue(), taskId = "";
        List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
        if (taskList.isEmpty()) {
            itembox = ItemBoxTypeEnum.DONE.getValue();
        }
        if (ItemBoxTypeEnum.DOING.getValue().equals(itembox)) {
            taskId = taskList.get(0).getId();
            TaskModel task = taskApi.findById(tenantId, taskId).getData();
            processInstanceId = task.getProcessInstanceId();
        }
        String processSerialNumber, processDefinitionId, taskDefinitionKey = "", processDefinitionKey,
            activitiUser = "", startor;
        ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
        HistoricProcessInstanceModel hpi = historicProcessApi.getById(tenantId, processInstanceId).getData();
        if (hpi == null) {
            OfficeDoneInfo officeDoneInfo = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
            if (officeDoneInfo == null) {
                String year = processParam.getCreateTime().substring(0, 4);
                hpi = historicProcessApi.getByIdAndYear(tenantId, processInstanceId, year).getData();
                processDefinitionId = hpi.getProcessDefinitionId();
                processDefinitionKey = processDefinitionId.split(SysVariables.COLON)[0];
            } else {
                processDefinitionId = officeDoneInfo.getProcessDefinitionId();
                processDefinitionKey = officeDoneInfo.getProcessDefinitionKey();
            }
        } else {
            processDefinitionId = hpi.getProcessDefinitionId();
            processDefinitionKey = processDefinitionId.split(SysVariables.COLON)[0];
        }
        startor = processParam.getStartor();
        processSerialNumber = processParam.getProcessSerialNumber();
        if (StringUtils.isNotEmpty(taskId)) {
            if (taskId.contains(SysVariables.COMMA)) {
                taskId = taskId.split(SysVariables.COMMA)[0];
            }
            TaskModel taskTemp = taskApi.findById(tenantId, taskId).getData();
            taskDefinitionKey = taskTemp.getTaskDefinitionKey();
        }
        OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, Y9LoginUserHolder.getOrgUnitId()).getData();
        model.setTitle(processParam.getTitle());
        model.setStartor(startor);
        model.setItembox(itembox);
        model.setCurrentUser(orgUnit.getName());
        model.setProcessDefinitionKey(processDefinitionKey);
        model.setProcessSerialNumber(processSerialNumber);
        model.setProcessDefinitionId(processDefinitionId);
        model.setProcessInstanceId(processInstanceId);
        model.setTaskDefKey(taskDefinitionKey);
        model.setTaskId(taskId);
        model.setActivitiUser(activitiUser);
        model.setItemId(processParam.getItemId());
        model = documentService.genDocumentModel(processParam.getItemId(), processDefinitionKey, processDefinitionId,
            taskDefinitionKey, mobile, model);
        String menuName = "打印,抄送,关注,返回";
        String menuKey = "17,18,follow,03";
        if (status == 1) {
            menuName = "打印,抄送,关注,返回";
            menuKey = "17,18,follow,03";
        }
        model.setMenuName(menuName);
        model.setMenuKey(menuKey);
        return model;
    }

    @Override
    public ChaoSong getById(String id) {
        return chaoSongRepository.findById(id).orElse(null);
    }

    @Override
    public int getDone4OpinionCountByUserId(String userId) {
        return chaoSongRepository.countByUserIdAndOpinionState(userId, "1");
    }

    @Override
    public int getDoneCountByUserId(String userId) {
        return chaoSongRepository.countByUserIdAndStatus(userId, 1);
    }

    @Override
    public int getTodoCountByUserId(String userId) {
        return chaoSongRepository.countByUserIdAndStatus(userId, 2);
    }

    @Override
    public Y9Page<ChaoSongModel> pageByProcessInstanceIdAndUserName(String processInstanceId, String userName, int rows,
        int page) {
        String senderId = Y9LoginUserHolder.getOrgUnitId();
        List<ChaoSong> csList;
        List<ChaoSongModel> list = new ArrayList<>();
        if (page < 1) {
            page = 1;
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        Page<ChaoSong> pageList = chaoSongRepository.findAll(new Specification<ChaoSong>() {
            @Override
            public Predicate toPredicate(Root<ChaoSong> root, javax.persistence.criteria.CriteriaQuery<?> query,
                CriteriaBuilder builder) {
                List<Predicate> list = new ArrayList<>();
                list.add(builder.equal(root.get("processInstanceId"), processInstanceId));
                list.add(builder.notEqual(root.get("senderId"), senderId));
                if (StringUtils.isNotBlank(userName)) {
                    list.add(builder.like(root.get("userName"), "%" + userName + "%"));
                }
                Predicate[] predicates = new Predicate[list.size()];
                list.toArray(predicates);
                return builder.and(predicates);
            }
        }, pageable);
        csList = pageList.getContent();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        int startRow = (page - 1) * rows;
        for (ChaoSong info : csList) {
            ChaoSongModel model = new ChaoSongModel();
            Y9BeanUtil.copyProperties(info, model);
            model.setId(info.getId());
            model.setProcessInstanceId(processInstanceId);
            model.setSenderName(info.getSenderName());
            model.setSendDeptName(info.getSendDeptName());
            model.setUserName(info.getUserName());
            model.setUserDeptName(info.getUserDeptName());
            model.setTitle(info.getTitle());
            model.setSerialNumber(startRow + 1);
            try {
                if (StringUtils.isBlank(info.getReadTime())) {
                    model.setReadTime("");
                } else {
                    model.setReadTime(sdf.format(sdf.parse(info.getReadTime())));
                }
                model.setCreateTime(sdf.format(sdf.parse(info.getCreateTime())));
            } catch (Exception e) {
                LOGGER.error("获取数据失败", e);
            }
            startRow += 1;
            list.add(model);
        }
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), list);
    }

    @Override
    public Y9Page<ChaoSongModel> pageBySenderIdAndProcessInstanceId(String senderId, String processInstanceId,
        String userName, int rows, int page) {
        List<ChaoSongModel> list = new ArrayList<>();
        List<ChaoSong> csList;
        if (page < 1) {
            page = 1;
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        Page<ChaoSong> pageList = chaoSongRepository.findAll(new Specification<ChaoSong>() {
            @Override
            public Predicate toPredicate(Root<ChaoSong> root, javax.persistence.criteria.CriteriaQuery<?> query,
                CriteriaBuilder builder) {
                List<Predicate> list = new ArrayList<>();
                list.add(builder.equal(root.get("senderId"), senderId));
                list.add(builder.equal(root.get("processInstanceId"), processInstanceId));
                if (StringUtils.isNotBlank(userName)) {
                    list.add(builder.like(root.get("userName"), "%" + userName + "%"));
                }
                Predicate[] predicates = new Predicate[list.size()];
                list.toArray(predicates);
                return builder.and(predicates);
            }
        }, pageable);
        csList = pageList.getContent();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        int startRow = (page - 1) * rows;
        for (ChaoSong cs : csList) {
            ChaoSongModel model = new ChaoSongModel();
            Y9BeanUtil.copyProperties(cs, model);
            model.setId(cs.getId());
            model.setProcessInstanceId(processInstanceId);
            model.setSenderName(cs.getSenderName());
            model.setSendDeptName(cs.getSendDeptName());
            model.setUserName(cs.getUserName());
            model.setUserDeptName(cs.getUserDeptName());
            model.setTitle(cs.getTitle());
            model.setSerialNumber(startRow + 1);
            try {
                if (StringUtils.isBlank(cs.getReadTime())) {
                    model.setReadTime("");
                } else {
                    model.setReadTime(sdf.format(sdf.parse(cs.getReadTime())));
                }
                model.setCreateTime(sdf.format(sdf.parse(cs.getCreateTime())));
            } catch (Exception e) {
                LOGGER.error("获取数据失败", e);
            }
            startRow += 1;
            list.add(model);
        }
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), list);
    }

    @Override
    public Y9Page<Map<String, Object>> pageByUserIdAndDocumentTitle(String userId, String documentTitle, int rows,
        int page) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<ChaoSong> csList;
        if (page < 1) {
            page = 1;
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        Page<ChaoSong> pageList = chaoSongRepository.findAll(new Specification<ChaoSong>() {
            @Override
            public Predicate toPredicate(Root<ChaoSong> root, javax.persistence.criteria.CriteriaQuery<?> query,
                CriteriaBuilder builder) {
                List<Predicate> list = new ArrayList<>();
                list.add(builder.equal(root.get("userId"), userId));
                if (StringUtils.isNotBlank(documentTitle)) {
                    list.add(builder.like(root.get("title"), "%" + documentTitle + "%"));
                }
                Predicate[] predicates = new Predicate[list.size()];
                list.toArray(predicates);
                return builder.and(predicates);
            }
        }, pageable);
        csList = pageList.getContent();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        int num = (page - 1) * rows;
        HistoricProcessInstanceModel hpi;
        ProcessParam processParam;
        List<Map<String, Object>> listMap = new ArrayList<>();
        for (ChaoSong cs : csList) {
            Map<String, Object> map = new HashMap<>(16);
            map.put("id", cs.getId());
            try {
                String processInstanceId = cs.getProcessInstanceId();
                map.put("createTime", sdf.format(sdf.parse(cs.getCreateTime())));
                processParam = processParamService.findByProcessInstanceId(processInstanceId);
                map.put("processInstanceId", processInstanceId);
                map.put("senderName", cs.getSenderName());
                map.put("sendDeptId", cs.getSendDeptId());
                map.put("sendDeptName", cs.getSendDeptName());
                map.put("readTime", sdf.format(sdf.parse(cs.getReadTime())));
                map.put("title", processParam.getTitle());
                map.put("status", cs.getStatus());
                map.put("banjie", false);
                map.put("itemId", cs.getItemId());
                map.put("itemName", cs.getItemName());
                map.put("processSerialNumber", processParam.getProcessSerialNumber());
                map.put("number", processParam.getCustomNumber());
                map.put("level", processParam.getCustomLevel());
                int chaosongNum = chaoSongRepository.countBySenderIdAndProcessInstanceId(userId, processInstanceId);
                map.put("chaosongNum", chaosongNum);
                hpi = historicProcessApi.getById(tenantId, processInstanceId).getData();
                boolean banjie = hpi == null || hpi.getEndTime() != null;
                if (banjie) {
                    map.put("banjie", true);
                }
            } catch (Exception e) {
                LOGGER.error("获取数据失败", e);
            }
            map.put("serialNumber", num + 1);
            num += 1;
            listMap.add(map);
        }
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), listMap);
    }

    @Override
    public Y9Page<ChaoSongModel> pageDoneList(String orgUnitId, String documentTitle, int rows, int page) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<ChaoSong> csList;
        if (page < 1) {
            page = 1;
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        Page<ChaoSong> pageList = chaoSongRepository.findAll(new Specification<ChaoSong>() {
            @Override
            public Predicate toPredicate(Root<ChaoSong> root, javax.persistence.criteria.CriteriaQuery<?> query,
                CriteriaBuilder builder) {
                List<Predicate> list = new ArrayList<>();
                list.add(builder.equal(root.get("userId"), orgUnitId));
                list.add(builder.equal(root.get("status"), 1));
                if (StringUtils.isNotBlank(documentTitle)) {
                    list.add(builder.like(root.get("title"), "%" + documentTitle + "%"));
                }
                Predicate[] predicates = new Predicate[list.size()];
                list.toArray(predicates);
                return builder.and(predicates);
            }
        }, pageable);
        csList = pageList.getContent();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        int num = (page - 1) * rows;
        HistoricProcessInstanceModel hpi;
        ProcessParam processParam;
        List<ChaoSongModel> list = new ArrayList<>();
        for (ChaoSong cs : csList) {
            ChaoSongModel model = new ChaoSongModel();
            Y9BeanUtil.copyProperties(cs, model);
            String processInstanceId = cs.getProcessInstanceId();
            model.setSerialNumber(num + 1);
            model.setId(cs.getId());
            model.setProcessInstanceId(processInstanceId);
            model.setSenderName(cs.getSenderName());
            model.setSendDeptName(cs.getSendDeptName());
            model.setTitle(cs.getTitle());
            model.setStatus(cs.getStatus());
            model.setItemId(cs.getItemId());
            model.setItemName(cs.getItemName());
            model.setBanjie(false);
            try {
                model.setReadTime(sdf.format(sdf.parse(cs.getReadTime())));
                model.setCreateTime(sdf.format(sdf.parse(cs.getCreateTime())));
                hpi = historicProcessApi.getById(tenantId, processInstanceId).getData();
                boolean banjie = hpi == null || hpi.getEndTime() != null;
                if (banjie) {
                    model.setBanjie(true);
                }
                OfficeDoneInfo officeDoneInfo = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
                model.setProcessDefinitionId(officeDoneInfo != null ? officeDoneInfo.getProcessDefinitionId() : "");
                model.setProcessSerialNumber(officeDoneInfo.getProcessSerialNumber());
                int countFollow = officeFollowService.countByProcessInstanceId(processInstanceId);
                model.setFollow(countFollow > 0);
                processParam = processParamService.findByProcessInstanceId(processInstanceId);
                model.setNumber(processParam.getCustomNumber());
                model.setLevel(processParam.getCustomLevel());
            } catch (Exception e) {
                LOGGER.error("获取数据失败", e);
            }
            num += 1;
            list.add(model);
        }
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), list);
    }

    @Override
    public Y9Page<ChaoSongModel> pageMyChaoSongList(String searchName, String itemId, String userName, String state,
        String year, int rows, int page) {
        String userId = Y9LoginUserHolder.getOrgUnitId();
        List<ChaoSongModel> list = new ArrayList<>();
        List<ChaoSong> csList;
        if (page < 1) {
            page = 1;
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        Page<ChaoSong> pageList = chaoSongRepository.findAll(new Specification<ChaoSong>() {
            @Override
            public Predicate toPredicate(Root<ChaoSong> root, javax.persistence.criteria.CriteriaQuery<?> query,
                CriteriaBuilder builder) {
                List<Predicate> list = new ArrayList<>();
                list.add(builder.equal(root.get("senderId"), userId));
                if (StringUtils.isNotBlank(searchName)) {
                    list.add(builder.like(root.get("title"), "%" + searchName + "%"));
                }
                if (StringUtils.isNotBlank(itemId)) {
                    list.add(builder.equal(root.get("itemId"), itemId));
                }
                if (StringUtils.isNotBlank(userName)) {
                    list.add(builder.like(root.get("userName"), "%" + userName + "%"));
                }
                if (StringUtils.isNotBlank(state)) {
                    list.add(builder.equal(root.get("status"), state));
                }
                if (StringUtils.isNotBlank(year)) {
                    list.add(builder.like(root.get("createTime"), "%" + year + "%"));
                }
                Predicate[] predicates = new Predicate[list.size()];
                list.toArray(predicates);
                return builder.and(predicates);
            }
        }, pageable);
        csList = pageList.getContent();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        int num = (page - 1) * rows;
        OfficeDoneInfo hpi;
        for (ChaoSong cs : csList) {
            ChaoSongModel model = new ChaoSongModel();
            Y9BeanUtil.copyProperties(cs, model);
            String processInstanceId = cs.getProcessInstanceId();
            model.setSerialNumber(num + 1);
            model.setId(cs.getId());
            model.setProcessInstanceId(processInstanceId);
            model.setSenderName(cs.getSenderName());
            model.setSendDeptName(cs.getSendDeptName());
            model.setStatus(cs.getStatus());
            model.setItemId(cs.getItemId());
            model.setItemName(cs.getItemName());
            model.setBanjie(false);
            try {
                hpi = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
                model.setProcessSerialNumber(hpi.getProcessSerialNumber());
                model.setTitle(hpi.getTitle());
                model.setCreateTime(sdf.format(sdf.parse(cs.getCreateTime())));
                model.setUserId(cs.getUserId());
                model.setUserName(cs.getUserName());
                model.setUserDeptName(cs.getUserDeptName());
                model.setReadTime(
                    StringUtils.isNotBlank(cs.getReadTime()) ? sdf.format(sdf.parse(cs.getReadTime())) : "--");
                model.setSystemName(hpi.getSystemName());
                model.setProcessDefinitionId(hpi != null ? hpi.getProcessDefinitionId() : "");
                boolean banjie = hpi == null || hpi.getEndTime() != null;
                if (banjie) {
                    model.setBanjie(true);
                }
                model.setNumber(hpi.getDocNumber());
                model.setLevel(hpi.getUrgency());
            } catch (Exception e) {
                LOGGER.error("获取列表失败", e);
            }
            num += 1;
            list.add(model);
        }
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), list);
    }

    @Override
    public Y9Page<ChaoSongModel> pageOpinionChaosongByUserId(String userId, String documentTitle, int rows, int page) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<ChaoSong> csList;
        if (page < 1) {
            page = 1;
        }
        List<ChaoSongModel> list = new ArrayList<>();
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        Page<ChaoSong> pageList = chaoSongRepository.findAll(new Specification<ChaoSong>() {
            @Override
            public Predicate toPredicate(Root<ChaoSong> root, javax.persistence.criteria.CriteriaQuery<?> query,
                CriteriaBuilder builder) {
                List<Predicate> list = new ArrayList<>();
                list.add(builder.equal(root.get("userId"), userId));
                list.add(builder.equal(root.get("opinionState"), "1"));
                if (StringUtils.isNotBlank(documentTitle)) {
                    list.add(builder.like(root.get("title"), "%" + documentTitle + "%"));
                }
                Predicate[] predicates = new Predicate[list.size()];
                list.toArray(predicates);
                return builder.and(predicates);
            }
        }, pageable);
        csList = pageList.getContent();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        HistoricProcessInstanceModel hpi;
        ProcessParam processParam;
        int num = (page - 1) * rows;
        for (ChaoSong cs : csList) {
            ChaoSongModel model = new ChaoSongModel();
            Y9BeanUtil.copyProperties(cs, model);
            String processInstanceId = cs.getProcessInstanceId();
            model.setSerialNumber(num + 1);
            model.setId(cs.getId());
            model.setProcessInstanceId(processInstanceId);
            model.setSenderName(cs.getSenderName());
            model.setSendDeptName(cs.getSendDeptName());
            model.setTitle(cs.getTitle());
            model.setStatus(cs.getStatus());
            model.setItemId(cs.getItemId());
            model.setItemName(cs.getItemName());
            model.setBanjie(false);
            try {
                model.setReadTime(sdf.format(sdf.parse(cs.getReadTime())));
                model.setCreateTime(sdf.format(sdf.parse(cs.getCreateTime())));
                hpi = historicProcessApi.getById(tenantId, processInstanceId).getData();
                boolean banjie = hpi == null || hpi.getEndTime() != null;
                if (banjie) {
                    model.setBanjie(true);
                }
                OfficeDoneInfo officeDoneInfo = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
                model.setProcessDefinitionId(officeDoneInfo != null ? officeDoneInfo.getProcessDefinitionId() : "");
                model.setProcessSerialNumber(officeDoneInfo.getProcessSerialNumber());
                int countFollow = officeFollowService.countByProcessInstanceId(processInstanceId);
                model.setFollow(countFollow > 0);

                processParam = processParamService.findByProcessInstanceId(processInstanceId);
                model.setNumber(processParam.getCustomNumber());
                model.setLevel(processParam.getCustomLevel());
            } catch (Exception e) {
                LOGGER.error("获取数据失败", e);
            }
            num += 1;
            list.add(model);
        }
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), list);
    }

    @Override
    public Y9Page<ChaoSongModel> pageTodoList(String orgUnitId, String documentTitle, int rows, int page) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<ChaoSong> csList;
        List<ChaoSongModel> list = new ArrayList<>();
        if (page < 1) {
            page = 1;
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        Page<ChaoSong> pageList = chaoSongRepository.findAll(new Specification<ChaoSong>() {
            @Override
            public Predicate toPredicate(Root<ChaoSong> root, javax.persistence.criteria.CriteriaQuery<?> query,
                CriteriaBuilder builder) {
                List<Predicate> list = new ArrayList<>();
                list.add(builder.equal(root.get("userId"), orgUnitId));
                list.add(builder.equal(root.get("status"), 2));
                if (StringUtils.isNotBlank(documentTitle)) {
                    list.add(builder.like(root.get("title"), "%" + documentTitle + "%"));
                }
                Predicate[] predicates = new Predicate[list.size()];
                list.toArray(predicates);
                return builder.and(predicates);
            }
        }, pageable);
        csList = pageList.getContent();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        int num = (page - 1) * rows;
        HistoricProcessInstanceModel hpi;
        ProcessParam processParam;
        for (ChaoSong cs : csList) {
            ChaoSongModel model = new ChaoSongModel();
            Y9BeanUtil.copyProperties(cs, model);
            String processInstanceId = cs.getProcessInstanceId();
            model.setSerialNumber(num + 1);
            model.setId(cs.getId());
            model.setProcessInstanceId(processInstanceId);
            model.setSenderName(cs.getSenderName());
            model.setSendDeptName(cs.getSendDeptName());
            model.setTitle(cs.getTitle());
            model.setStatus(cs.getStatus());
            model.setItemId(cs.getItemId());
            model.setItemName(cs.getItemName());
            model.setBanjie(false);
            try {
                model.setCreateTime(sdf.format(sdf.parse(cs.getCreateTime())));
                hpi = historicProcessApi.getById(tenantId, processInstanceId).getData();
                boolean banjie = hpi == null || hpi.getEndTime() != null;
                if (banjie) {
                    model.setBanjie(true);
                }
                OfficeDoneInfo officeDoneInfo = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
                model.setProcessDefinitionId(officeDoneInfo != null ? officeDoneInfo.getProcessDefinitionId() : "");
                model.setProcessSerialNumber(officeDoneInfo.getProcessSerialNumber());
                int countFollow = officeFollowService.countByProcessInstanceId(processInstanceId);
                model.setFollow(countFollow > 0);
                processParam = processParamService.findByProcessInstanceId(processInstanceId);
                model.setNumber(processParam.getCustomNumber());
                model.setLevel(processParam.getCustomLevel());
            } catch (Exception e) {
                LOGGER.error("获取数据失败", e);
            }
            num += 1;
            list.add(model);
        }
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), list);
    }

    @Override
    @Transactional
    public ChaoSong save(ChaoSong chaoSong) {
        return chaoSongRepository.save(chaoSong);
    }

    @Override
    @Transactional
    public void save(List<ChaoSong> chaoSongList) {
        chaoSongRepository.saveAll(chaoSongList);
    }

    @Override
    @Transactional
    public Y9Result<Object> save(String processInstanceId, String users, String isSendSms, String isShuMing,
        String smsContent, String smsPersonId) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String tenantId = Y9LoginUserHolder.getTenantId(), curruserId = Y9LoginUserHolder.getOrgUnitId();
            OrgUnit currOrgUnit = Y9LoginUserHolder.getOrgUnit();
            ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
            String title = processParam.getTitle(), itemId = processParam.getItemId(),
                itemName = processParam.getItemName(), systemName = processParam.getSystemName();
            String[] orgUnitList = users.split(";");
            List<ChaoSong> csList = new ArrayList<>();
            List<String> userIdListAdd = new ArrayList<>();
            // 添加的人员
            for (String orgUnitStr : orgUnitList) {
                String[] orgUnitArr = orgUnitStr.split(":");
                Integer type = Integer.valueOf(orgUnitArr[0]);
                String orgUnitId = orgUnitArr[1];
                if (Objects.equals(ItemPermissionEnum.DEPARTMENT.getValue(), type)) {
                    List<Position> list = positionApi.listByParentId(tenantId, orgUnitId).getData();
                    for (Position position : list) {
                        userIdListAdd.add(position.getId());
                    }
                } else if (Objects.equals(ItemPermissionEnum.POSITION.getValue(), type)) {
                    userIdListAdd.add(orgUnitId);
                } else if (type.equals(ItemPermissionEnum.CUSTOMGROUP.getValue())) {
                    List<CustomGroupMember> list0 = customGroupApi.listCustomGroupMemberByGroupIdAndMemberType(tenantId,
                        Y9LoginUserHolder.getPersonId(), orgUnitId, OrgTypeEnum.POSITION).getData();
                    for (CustomGroupMember pTemp : list0) {
                        OrgUnit user = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, pTemp.getMemberId()).getData();
                        if (user != null && StringUtils.isNotBlank(user.getId())) {
                            userIdListAdd.add(user.getId());
                        }
                    }
                }
            }
            // 保存抄送
            OrgUnit dept = orgUnitApi.getOrgUnit(tenantId, currOrgUnit.getParentId()).getData();
            if (null == dept || null == dept.getId()) {
                dept = organizationApi.get(tenantId, currOrgUnit.getParentId()).getData();
            }
            List<String> mobile = new ArrayList<>();
            for (String userId : userIdListAdd) {
                OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, userId).getData();
                ChaoSong cs = new ChaoSong();
                cs.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                cs.setCreateTime(sdf.format(new Date()));
                cs.setProcessInstanceId(processInstanceId);
                cs.setSenderId(curruserId);
                cs.setSenderName(currOrgUnit.getName());
                cs.setSendDeptId(dept.getId());
                cs.setSendDeptName(dept.getName());
                cs.setStatus(2);
                cs.setTenantId(tenantId);
                cs.setTitle(title);
                cs.setUserId(orgUnit.getId());
                cs.setUserName(orgUnit.getName());
                OrgUnit department = orgUnitApi.getOrgUnit(tenantId, orgUnit.getParentId()).getData();
                cs.setUserDeptId(department.getId());
                cs.setUserDeptName(department.getName());
                cs.setItemId(itemId);
                cs.setItemName(itemName);
                csList.add(cs);
            }
            this.save(csList);
            asyncHandleService.saveChaoSongTodo(tenantId, csList);
            if (StringUtils.isNotBlank(isSendSms) && UtilConsts.TRUE.equals(isSendSms)) {
                smsContent += "--" + Y9LoginUserHolder.getUserInfo().getName();
                Boolean smsSwitch = y9ItemAdminProperties.getSmsSwitch();
                if (Boolean.TRUE.equals(smsSwitch)) {
                    smsHttpApi.sendSmsHttpList(tenantId, Y9LoginUserHolder.getPersonId(), mobile, smsContent,
                        systemName + "抄送");
                } else {
                    LOGGER.info("*********************y9.app.itemAdmin.smsSwitch开关未打开*******************");
                }
            }
            return Y9Result.successMsg("抄送成功");
        } catch (Exception e) {
            final Writer result = new StringWriter();
            final PrintWriter print = new PrintWriter(result);
            e.printStackTrace(print);
            try {
                String msg = result.toString();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = sdf.format(new Date());
                ErrorLog errorLog = new ErrorLog();
                errorLog.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                errorLog.setCreateTime(time);
                errorLog.setErrorFlag(ErrorLogModel.ERROR_FLAG_SAVE_CHAOSONG);
                errorLog.setErrorType(ErrorLogModel.ERROR_PROCESS_INSTANCE);
                errorLog.setExtendField("抄送保存失败");
                errorLog.setProcessInstanceId(processInstanceId);
                errorLog.setTaskId("");
                errorLog.setText(msg);
                errorLog.setUpdateTime(time);
                errorLogService.saveErrorLog(errorLog);
            } catch (Exception e2) {
                LOGGER.error("保存抄送失败异常,错误信息为：{}", e2);
            }
            return Y9Result.failure("抄送失败");
        }
    }

    @Override
    public Y9Page<ChaoSongModel> searchAllByUserId(String searchName, String itemId, String userName, String state,
        String year, Integer page, Integer rows) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9LoginUserHolder.getOrgUnitId();
        List<ChaoSongModel> list = new ArrayList<>();
        List<ChaoSong> csList;
        if (page < 1) {
            page = 1;
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        Page<ChaoSong> pageList = chaoSongRepository.findAll(new Specification<ChaoSong>() {
            @Override
            public Predicate toPredicate(Root<ChaoSong> root, javax.persistence.criteria.CriteriaQuery<?> query,
                CriteriaBuilder builder) {
                List<Predicate> list = new ArrayList<>();
                list.add(builder.equal(root.get("userId"), userId));
                if (StringUtils.isNotBlank(searchName)) {
                    list.add(builder.like(root.get("title"), "%" + searchName + "%"));
                }
                if (StringUtils.isNotBlank(itemId)) {
                    list.add(builder.equal(root.get("itemId"), itemId));
                }
                if (StringUtils.isNotBlank(userName)) {
                    list.add(builder.like(root.get("userName"), "%" + userName + "%"));
                }
                if (StringUtils.isNotBlank(state)) {
                    list.add(builder.equal(root.get("status"), state));
                }
                if (StringUtils.isNotBlank(year)) {
                    list.add(builder.like(root.get("createTime"), "%" + year + "%"));
                }
                Predicate[] predicates = new Predicate[list.size()];
                list.toArray(predicates);
                return builder.and(predicates);
            }
        }, pageable);
        csList = pageList.getContent();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        int num = (page - 1) * rows;
        HistoricProcessInstanceModel hpi;
        ProcessParam processParam;
        for (ChaoSong cs : csList) {
            ChaoSongModel model = new ChaoSongModel();
            Y9BeanUtil.copyProperties(cs, model);
            String processInstanceId = cs.getProcessInstanceId();
            model.setSerialNumber(num + 1);
            model.setId(cs.getId());
            model.setProcessInstanceId(processInstanceId);
            model.setSenderName(cs.getSenderName());
            model.setSendDeptName(cs.getSendDeptName());
            model.setTitle(cs.getTitle());
            model.setStatus(cs.getStatus());
            model.setItemId(cs.getItemId());
            model.setItemName(cs.getItemName());
            model.setBanjie(false);
            try {
                model.setReadTime(
                    StringUtils.isNotBlank(cs.getReadTime()) ? sdf.format(sdf.parse(cs.getReadTime())) : "--");
                model.setCreateTime(sdf.format(sdf.parse(cs.getCreateTime())));
                processParam = processParamService.findByProcessInstanceId(processInstanceId);
                model.setNumber(processParam.getCustomNumber());
                model.setLevel(processParam.getCustomLevel());
                model.setProcessSerialNumber(processParam.getProcessSerialNumber());
                hpi = historicProcessApi.getById(tenantId, processInstanceId).getData();
                boolean banjie = hpi == null || hpi.getEndTime() != null;
                if (banjie) {
                    model.setBanjie(true);
                }
                OfficeDoneInfo officeDoneInfo = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
                model.setProcessDefinitionId(officeDoneInfo != null ? officeDoneInfo.getProcessDefinitionId() : "");
                int countFollow = officeFollowService.countByProcessInstanceId(processInstanceId);
                model.setFollow(countFollow > 0);
            } catch (Exception e) {
                LOGGER.error("获取抄送列表失败", e);
            }
            num += 1;
            list.add(model);
        }
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), list);
    }

    @Override
    public Y9Page<ChaoSongModel> searchAllList(String searchName, String itemId, String senderName, String userName,
        String state, String year, Integer page, Integer rows) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<ChaoSongModel> list = new ArrayList<>();
        List<ChaoSong> csList;
        if (page < 1) {
            page = 1;
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        Page<ChaoSong> pageList = chaoSongRepository.findAll(new Specification<ChaoSong>() {
            @Override
            public Predicate toPredicate(Root<ChaoSong> root, javax.persistence.criteria.CriteriaQuery<?> query,
                CriteriaBuilder builder) {
                List<Predicate> list = new ArrayList<>();
                if (StringUtils.isNotBlank(searchName)) {
                    list.add(builder.like(root.get("title"), "%" + searchName + "%"));
                }
                if (StringUtils.isNotBlank(itemId)) {
                    list.add(builder.equal(root.get("itemId"), itemId));
                }
                if (StringUtils.isNotBlank(userName)) {
                    list.add(builder.like(root.get("userName"), "%" + userName + "%"));
                }
                if (StringUtils.isNotBlank(senderName)) {
                    list.add(builder.like(root.get("senderName"), "%" + senderName + "%"));
                }
                if (StringUtils.isNotBlank(state)) {
                    list.add(builder.equal(root.get("status"), state));
                }
                if (StringUtils.isNotBlank(year)) {
                    list.add(builder.like(root.get("createTime"), "%" + year + "%"));
                }
                Predicate[] predicates = new Predicate[list.size()];
                list.toArray(predicates);
                return builder.and(predicates);
            }
        }, pageable);
        csList = pageList.getContent();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        int num = (page - 1) * rows;
        HistoricProcessInstanceModel hpi;
        ProcessParam processParam;
        for (ChaoSong cs : csList) {
            ChaoSongModel model = new ChaoSongModel();
            Y9BeanUtil.copyProperties(cs, model);
            String processInstanceId = cs.getProcessInstanceId();
            model.setSerialNumber(num + 1);
            model.setId(cs.getId());
            model.setProcessInstanceId(processInstanceId);
            model.setSenderName(cs.getSenderName());
            model.setSendDeptName(cs.getSendDeptName());
            model.setTitle(cs.getTitle());
            model.setStatus(cs.getStatus());
            model.setItemId(cs.getItemId());
            model.setItemName(cs.getItemName());
            model.setBanjie(false);
            model.setUserName(cs.getUserName());
            model.setUserDeptName(cs.getUserDeptName());
            try {
                model.setReadTime(
                    StringUtils.isNotBlank(cs.getReadTime()) ? sdf.format(sdf.parse(cs.getReadTime())) : "--");
                model.setCreateTime(sdf.format(sdf.parse(cs.getCreateTime())));
                processParam = processParamService.findByProcessInstanceId(processInstanceId);
                model.setNumber(processParam.getCustomNumber());
                model.setLevel(processParam.getCustomLevel());
                model.setProcessSerialNumber(processParam.getProcessSerialNumber());
                hpi = historicProcessApi.getById(tenantId, processInstanceId).getData();
                boolean banjie = hpi == null || hpi.getEndTime() != null;
                if (banjie) {
                    model.setBanjie(true);
                }
                OfficeDoneInfo officeDoneInfo = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
                model.setProcessDefinitionId(officeDoneInfo != null ? officeDoneInfo.getProcessDefinitionId() : "");
                int countFollow = officeFollowService.countByProcessInstanceId(processInstanceId);
                model.setFollow(countFollow > 0);
            } catch (Exception e) {
                LOGGER.error("获取抄送列表失败", e);
            }
            num += 1;
            list.add(model);
        }
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), list);
    }

    @Override
    @Transactional
    public void updateTitle(String processInstanceId, String documentTitle) {
        try {
            List<ChaoSong> list = chaoSongRepository.findByProcessInstanceId(processInstanceId);
            List<ChaoSong> newList = new ArrayList<>();
            for (ChaoSong info : list) {
                info.setTitle(documentTitle);
                newList.add(info);
            }
            if (!newList.isEmpty()) {
                chaoSongRepository.saveAll(newList);
            }
        } catch (Exception e) {
            LOGGER.error("更新抄送标题失败", e);
        }
    }

}
