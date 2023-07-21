package net.risesoft.controller.mobile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import net.risesoft.api.itemadmin.FormDataApi;
import net.risesoft.api.itemadmin.ItemOpinionFrameBindApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.WorkOrderApi;
import net.risesoft.api.itemadmin.position.Attachment4PositionApi;
import net.risesoft.api.itemadmin.position.Document4PositionApi;
import net.risesoft.api.itemadmin.position.Item4PositionApi;
import net.risesoft.api.org.OrgUnitApi;
import net.risesoft.api.org.PersonApi;
import net.risesoft.api.permission.RoleApi;
import net.risesoft.api.tenant.TenantApi;
import net.risesoft.api.todo.TodoTaskApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.OrgUnit;
import net.risesoft.model.Person;
import net.risesoft.model.Tenant;
import net.risesoft.model.itemadmin.AttachmentModel;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.model.itemadmin.ItemOpinionFrameBindModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.itemadmin.WorkOrderModel;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9Util;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;

import y9.apisix.annotation.NoApiClass;

/**
 * 系统工单接口
 *
 * @author 10858
 *
 */
@NoApiClass
@RestController
@RequestMapping("/mobile/workOrder")
public class MobileBugWorkOrderController {

    @Autowired
    private Document4PositionApi documentManager;

    @Autowired
    private WorkOrderApi workOrderManager;

    @Autowired
    private PersonApi personManager;

    @Autowired
    private OrgUnitApi orgUnitManager;

    @Autowired
    private Y9FileStoreService y9FileStoreService;

    @Value("${y9.app.flowable.tenantId}")
    private String myTenantId;

    @Autowired
    private Attachment4PositionApi attachmentManager;

    @Autowired
    private RoleApi roleManager;

    @Autowired
    private TodoTaskApi todoTaskManager;

    @Autowired
    private ItemOpinionFrameBindApi itemOpinionFrameBindManager;

    @Autowired
    private FormDataApi formDataManager;

    @Autowired
    private TenantApi tenantManager;

    @Autowired
    private ProcessParamApi processParamManager;

    @Autowired
    private Item4PositionApi itemManager;

    /**
     * 新建工单
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param request
     * @param response
     */
    @RequestMapping(value = "/add")
    public void add(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personManager.getPerson(tenantId, userId);
            Y9LoginUserHolder.setPerson(person);

            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String nowDate = sdf.format(date);
            SimpleDateFormat yearsdf = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat sesdf = new SimpleDateFormat("HHmmss");
            String year = yearsdf.format(date);
            String second = sesdf.format(date);
            String itemNumber = "〔" + year + "〕" + second + "号";

            Tenant tenant = tenantManager.getById(Y9LoginUserHolder.getTenantId());
            WorkOrderModel workOrderModel = new WorkOrderModel();
            workOrderModel.setGuid(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            workOrderModel.setNumber(itemNumber);
            workOrderModel.setUserName(person.getName());
            workOrderModel.setUserId(userId);
            workOrderModel.setTenantName(tenant.getName());
            workOrderModel.setTenantId(tenantId);
            workOrderModel.setMobile(person.getMobile());
            workOrderModel.setCreateDate(nowDate);
            workOrderModel.setHandleType("0");
            map.put(UtilConsts.SUCCESS, true);
            map.put("data", workOrderModel);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     * 删除工单草稿
     *
     * @param processSerialNumber 草稿id
     * @param response
     */
    @RequestMapping(value = "/deleteDraft")
    public void deleteDraft(@RequestParam(required = false) String processSerialNumber, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            map = workOrderManager.deleteDraft(processSerialNumber);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     * 删除附件
     *
     * @param ids 附件ids
     * @param response
     */
    @RequestMapping(value = "/delFile")
    public void delFile(@RequestParam(required = false) String ids, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            map = attachmentManager.delFile(myTenantId, ids);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     * 处理中工单
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param listType 列表类型“adminList”为管理员列表，空值为个人列表
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @param response
     */
    @RequestMapping(value = "/doingList")
    public void doingList(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestParam(required = false) String listType, @RequestParam(required = false) String searchTerm, Integer page, Integer rows, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            if (StringUtils.isNotBlank(listType)) {
                map = workOrderManager.workOrderAdminList(searchTerm, "2", page, rows);
            }
            map = workOrderManager.workOrderList(userId, searchTerm, "2", page, rows);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     * 已处理工单
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param listType 列表类型“adminList”为管理员列表，空值为个人列表
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @param response
     */
    @RequestMapping(value = "/doneList")
    public void doneList(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestParam(required = false) String listType, @RequestParam(required = false) String searchTerm, Integer page, Integer rows, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            if (StringUtils.isNotBlank(listType)) {
                map = workOrderManager.workOrderAdminList(searchTerm, "3", page, rows);
            }
            map = workOrderManager.workOrderList(userId, searchTerm, "3", page, rows);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     * 工单草稿
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @param response
     */
    @RequestMapping(value = "/draftList")
    public void draftList(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestParam(required = false) String searchTerm, Integer page, Integer rows, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            map = workOrderManager.workOrderList(userId, searchTerm, "0", page, rows);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     * 工单分配发送
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param itemId 事项id
     * @param processInstanceId 流程实例id
     * @param processDefinitionKey 流程定义key
     * @param processSerialNumber 流程编号，工单id
     * @param userChoice 选择人员
     * @param sponsorGuid 主办人id
     * @param routeToTaskId 任务key
     * @param level 优先级
     * @param number 工单编号
     * @param documentTitle 标题
     * @return
     */
    @RequestMapping(value = "/forwarding")
    public void forwarding(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestParam(required = false) String itemId, @RequestParam(required = false) String processInstanceId, @RequestParam(required = false) String processDefinitionKey,
        @RequestParam(required = false) String processSerialNumber, @RequestParam(required = false) String userChoice, @RequestParam(required = false) String sponsorGuid, @RequestParam(required = false) String routeToTaskId, @RequestParam(required = false) String level,
        @RequestParam(required = false) String number, @RequestParam(required = false) String documentTitle, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, true);
        map.put("msg", "发送成功");
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personManager.getPerson(tenantId, userId);
            Y9LoginUserHolder.setPerson(person);
            WorkOrderModel workOrderModel = workOrderManager.findByProcessSerialNumber(processSerialNumber);
            if (!workOrderModel.getHandleType().equals("1")) {
                map.put(UtilConsts.SUCCESS, false);
                map.put("msg", "改件已被办理，请刷新页面");
                Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
                return;
            }
            Map<String, Object> variables = new HashMap<String, Object>(16);
            ItemModel item = itemManager.getByItemId(tenantId, itemId);
            ProcessParamModel processParamModel = processParamManager.findByProcessSerialNumber(tenantId, processSerialNumber);
            ProcessParamModel pp = new ProcessParamModel();
            pp.setIsSendSms("");
            pp.setIsShuMing("");
            pp.setSmsContent("");
            pp.setSmsPersonId("");
            pp.setBureauIds("");
            pp.setCustomLevel(level);
            pp.setCustomNumber(number);
            pp.setItemId(itemId);
            pp.setItemName(item.getName());
            pp.setProcessInstanceId(processInstanceId);
            pp.setProcessSerialNumber(processSerialNumber);
            pp.setSystemName(item.getSystemName());
            pp.setSystemCnName(item.getSysLevel());
            pp.setTitle(documentTitle);
            pp.setSponsorGuid(processParamModel != null ? processParamModel.getSponsorGuid() : "");
            pp.setSended(processParamModel != null ? processParamModel.getSended() : "");
            pp.setStartor(processParamModel != null ? processParamModel.getStartor() : "");
            pp.setStartorName(processParamModel != null ? processParamModel.getStartorName() : "");
            pp.setTodoTaskUrlPrefix(item.getTodoTaskUrlPrefix());
            StringBuffer searchTerm = new StringBuffer();
            searchTerm.append(documentTitle).append("|").append(number).append("|").append(level).append("|").append(item.getName());
            pp.setSearchTerm(searchTerm.toString());
            processParamManager.saveOrUpdate(tenantId, pp);
            map = documentManager.saveAndForwarding(tenantId, userId, processInstanceId, "", "", itemId, processSerialNumber, processDefinitionKey, userChoice, sponsorGuid, routeToTaskId, variables);
            if ((boolean)map.get(UtilConsts.SUCCESS)) {// 删除统一待办数据
                processInstanceId = (String)map.get("processInstanceId");
                workOrderManager.changeWorkOrderState(processSerialNumber, "2", processInstanceId, "");
                todoTaskManager.deleteByProcessInstanceId(myTenantId, processSerialNumber);// 提交工单ProcessInstanceId存的值为流程编号
            }
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "发生异常");
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     * 获取管理员系统工单未处理计数
     *
     * @param response
     */
    @RequestMapping(value = "/getAdminCount")
    public void getAdminCount(HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            int count = workOrderManager.getAdminTodoCount();
            map.put(UtilConsts.SUCCESS, true);
            map.put("tocoCount", count);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     * 获取附件列表
     *
     * @param processSerialNumber 编号
     * @param response
     */
    @RequestMapping("/getAttachmentList")
    public void getAttachmentList(@RequestParam(required = false) String processSerialNumber, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            map = attachmentManager.getAttachmentList(myTenantId, processSerialNumber, "", 1, 50);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     * 获取系统工单计数
     *
     * @param userId
     * @param response
     */
    @RequestMapping(value = "/getCount")
    public void getCount(@RequestHeader("auth-userId") String userId, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            map = workOrderManager.getCount(userId);
            map.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     * 获取登录人员角色，是否是工单管理员
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param response
     */
    @RequestMapping(value = "/getRole")
    public void getRole(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            boolean workOrderManage = roleManager.hasRole(tenantId, "itemAdmin", "", "系统工单管理员", userId);
            map.put(UtilConsts.SUCCESS, true);
            map.put("workOrderManage", workOrderManage);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     * 工单列表打开工单，获取工单详情
     *
     * @param processSerialNumber 编号
     * @param response
     */
    @RequestMapping(value = "/open")
    public void open(@RequestParam(required = false) String processSerialNumber, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            map.put(UtilConsts.SUCCESS, true);
            WorkOrderModel workOrder = workOrderManager.findByProcessSerialNumber(processSerialNumber);
            map.put("data", workOrder);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }

    /**
     * 统一待办打开系统工单
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param itemId 事项id
     * @param taskId 任务id
     * @param request
     * @param response
     */
    @ResponseBody
    @RequestMapping(value = "/openTodo")
    public void openTodo(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestParam String itemId, @RequestParam String taskId, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personManager.getPerson(tenantId, userId);
            Y9LoginUserHolder.setPerson(person);
            map = documentManager.add(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPersonId(), itemId, true);
            // 意见框？？？？？？？？？？
            String formIds = (String)map.get("formIds");
            String taskDefKey = (String)map.get("taskDefKey");
            String processDefinitionId = (String)map.get("processDefinitionId");
            String formId[] = formIds.split(SysVariables.COMMA);
            List<Map<String, Object>> fieldDefineList = new ArrayList<Map<String, Object>>();
            List<Map<String, Object>> opinionFrameList = new ArrayList<Map<String, Object>>();
            List<ItemOpinionFrameBindModel> bindList = itemOpinionFrameBindManager.findByItemIdAndProcessDefinitionIdAndTaskDefKeyContainRole(tenantId, userId, itemId, processDefinitionId, taskDefKey);
            for (ItemOpinionFrameBindModel bind : bindList) {
                Map<String, Object> opinionFrameMap = new HashMap<String, Object>(16);
                opinionFrameMap.put("hasRole", false);
                opinionFrameMap.put("opinionFrameMark", bind.getOpinionFrameMark());
                opinionFrameMap.put("opinionFrameName", bind.getOpinionFrameName());
                List<String> roleIds = bind.getRoleIds();
                for (String roleId : roleIds) {
                    Boolean hasRole = roleManager.hasRoleByTenantIdAndRoleIdAndOrgUnitId(tenantId, roleId, person.getId());
                    if (hasRole) {
                        opinionFrameMap.put("hasRole", hasRole);
                        break;
                    }
                }
                opinionFrameList.add(opinionFrameMap);
            }
            for (int i = 0; i < formId.length; i++) {// 获取表单定义字段
                Map<String, Object> fieldDefineMap = new HashMap<String, Object>(16);
                List<Map<String, String>> listMap = new ArrayList<Map<String, String>>();
                listMap = formDataManager.getFormFieldDefine(tenantId, formId[i]);
                fieldDefineMap.put(formId[i], listMap);
                fieldDefineList.add(fieldDefineMap);
            }
            map.put("opinionFrame", opinionFrameList);
            map.put("fieldDefine", fieldDefineList);
            map.put("msg", "获取数据成功");
            map.put(UtilConsts.SUCCESS, true);
            try {// 管理员打开待办工单，更新统一待办状态
                todoTaskManager.setIsNewTodo(Y9LoginUserHolder.getTenantId(), taskId, "0");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            map.put("msg", "发生异常");
            map.put(UtilConsts.SUCCESS, false);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     * 未处理工单
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param listType 列表类型“adminList”为管理员列表，空值为个人列表
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @param response
     */
    @RequestMapping(value = "/todoList")
    public void todoList(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestParam(required = false) String listType, @RequestParam(required = false) String searchTerm, Integer page, Integer rows, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            if (StringUtils.isNotBlank(listType)) {
                map = workOrderManager.workOrderAdminList(searchTerm, "1", page, rows);
            }
            map = workOrderManager.workOrderList(userId, searchTerm, "1", page, rows);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     * 上传附件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param file 文件
     * @param processSerialNumber 编号
     * @param response
     */
    @RequestMapping("/uploadFile")
    public void uploadFile(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestParam(required = false) MultipartFile file, @RequestParam(required = false) String processSerialNumber, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personManager.getPerson(tenantId, userId);
            Y9LoginUserHolder.setPerson(person);
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "上传附件成功");
            SimpleDateFormat sdf_yMd_hms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String originalFilename = file.getOriginalFilename();
            String fileName = FilenameUtils.getName(originalFilename);
            String[] types = fileName.split("\\.");
            String type = types[types.length - 1].toLowerCase();
            String fullPath = "/" + Y9Context.getSystemName() + "/" + tenantId + "/attachmentFile" + "/" + processSerialNumber;
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(file, fullPath, fileName);

            AttachmentModel attachmentModel = new AttachmentModel();
            attachmentModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            attachmentModel.setName(fileName);
            attachmentModel.setFileSize(y9FileStore.getDisplayFileSize());
            attachmentModel.setFileSource("系统工单");
            attachmentModel.setProcessInstanceId("");
            attachmentModel.setProcessSerialNumber(processSerialNumber);
            attachmentModel.setTaskId("");
            attachmentModel.setUploadTime(sdf_yMd_hms.format(new Date()));
            attachmentModel.setDescribes("");
            attachmentModel.setPersonName(person.getName());
            attachmentModel.setPersonId(person.getId());
            OrgUnit orgUnit = orgUnitManager.getParent(tenantId, person.getParentId());
            attachmentModel.setDeptId(person.getParentId());
            attachmentModel.setDeptName(orgUnit != null ? orgUnit.getName() : "");
            attachmentModel.setFileStoreId(y9FileStore.getId());
            attachmentModel.setFileType(type);
            attachmentManager.uploadModel(myTenantId, "", attachmentModel);
        } catch (Exception e) {
            e.printStackTrace();
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "上传附件失败");
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     * 办结工单
     *
     * @param processSerialNumber 流程编号，工单id
     * @param resultFeedback 结果反馈
     * @param response
     */
    @RequestMapping("/workOrderFinish")
    public void workOrderFinish(@RequestParam(required = false) String processSerialNumber, @RequestParam(required = false) String resultFeedback, HttpServletResponse response) {
        Map<String, Object> resMap = new HashMap<String, Object>(16);
        resMap.put(UtilConsts.SUCCESS, true);
        resMap.put("msg", "办结成功");
        try {
            WorkOrderModel workOrderModel = workOrderManager.findByProcessSerialNumber(processSerialNumber);
            if (!workOrderModel.getHandleType().equals("1")) {
                resMap.put(UtilConsts.SUCCESS, false);
                resMap.put("msg", "改件已被办理，请刷新页面");
                Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(resMap));
                return;
            }
            resMap = workOrderManager.changeWorkOrderState(processSerialNumber, "3", "", resultFeedback);
            if ((boolean)resMap.get(UtilConsts.SUCCESS)) {
                resMap.put("msg", "办结成功");
                todoTaskManager.deleteByProcessInstanceId(myTenantId, processSerialNumber);// 提交工单ProcessInstanceId存的值为流程编号
            } else {
                resMap.put("msg", "办结失败");
            }
        } catch (Exception e) {
            resMap.put(UtilConsts.SUCCESS, false);
            resMap.put("msg", "办结失败");
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(resMap));
        return;
    }

    /**
     * 保存，提交工单
     *
     * @param formdata 工单内容数据，json格式
     * @param type "0"为保存，"1"为提交
     * @param request
     * @param response
     */
    @RequestMapping("/workOrderSubmit")
    public void workOrderSubmit(@RequestParam(required = false) String type, @RequestParam(required = false) String formdata, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> resMap = new HashMap<String, Object>(16);
        resMap.put(UtilConsts.SUCCESS, true);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            WorkOrderModel workOrderModel = Y9JsonUtil.readValue(formdata, WorkOrderModel.class);
            if (StringUtils.isBlank(workOrderModel.getGuid())) {
                workOrderModel.setGuid(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            }
            workOrderModel.setCreateTime(sdf.format(new Date()));
            workOrderModel.setHandleType(type);
            resMap = workOrderManager.saveWorkOrder(workOrderModel);
        } catch (Exception e) {
            resMap.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(resMap));
        return;
    }
}
