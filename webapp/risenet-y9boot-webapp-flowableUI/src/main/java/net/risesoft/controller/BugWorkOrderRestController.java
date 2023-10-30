package net.risesoft.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import net.coobird.thumbnailator.Thumbnails;
import net.risesoft.api.itemadmin.AttachmentApi;
import net.risesoft.api.itemadmin.DocumentApi;
import net.risesoft.api.itemadmin.WorkOrderApi;
import net.risesoft.api.org.OrgUnitApi;
import net.risesoft.api.permission.PersonRoleApi;
import net.risesoft.api.tenant.TenantApi;
import net.risesoft.api.todo.TodoTaskApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.enums.WorkOrderHandleTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.model.OrgUnit;
import net.risesoft.model.Tenant;
import net.risesoft.model.itemadmin.AttachmentModel;
import net.risesoft.model.itemadmin.WorkOrderModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@RestController
@RequestMapping(value = "/vue/workOrder")
public class BugWorkOrderRestController {

    @Autowired
    private DocumentApi documentManager;

    @Autowired
    private AttachmentApi attachmentManager;

    @Autowired
    private TenantApi tenantApi;

    @Autowired
    private WorkOrderApi workOrderManager;

    @Autowired
    private OrgUnitApi orgUnitApi;

    @Autowired
    private Y9FileStoreService y9FileStoreService;

    @Value("${y9.app.flowable.tenantId}")
    private String myTenantId;

    @Autowired
    private PersonRoleApi personRoleApi;

    @Autowired
    private TodoTaskApi todoTaskApi;
    @Autowired
    private Y9Properties y9Config;

    /**
     * 新建工单
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/add")
    public String add(Model model) {
        model.addAttribute("processSerialNumber", Y9IdGenerator.genId(IdType.SNOWFLAKE));
        model.addAttribute("itembox", ItemBoxTypeEnum.ADD.getValue());
        model.addAttribute("workOrder", new WorkOrderModel());
        model.addAttribute("jodconverterURL", y9Config.getCommon().getJodconverterBaseUrl());
        return "workOrder/document4Gongdan";
    }

    /**
     * 删除草稿
     *
     * @param processSerialNumber 编号
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/deleteDraft", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> deleteDraft(@RequestParam(required = false) String processSerialNumber) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            map = workOrderManager.deleteDraft(processSerialNumber);
            if ((Boolean)map.get(UtilConsts.SUCCESS)) {
                return Y9Result.successMsg("删除成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("删除失败");
    }

    /**
     * 删除附件
     *
     * @param ids 附件ids
     * @return
     */
    @RequestMapping(value = "/delFile", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Y9Result<String> delFile(@RequestParam(required = true) String ids) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            map = attachmentManager.delFile(myTenantId, "", ids);
            if ((Boolean)map.get(UtilConsts.SUCCESS)) {
                return Y9Result.successMsg("删除成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("删除失败");
    }

    /**
     * 获取处理中工单列表
     *
     * @param listType 列表类型
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 条数
     * @return
     */
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value = "/doingList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> doingList(@RequestParam(required = false) String listType,
        @RequestParam(required = false) String searchTerm, @RequestParam(required = true) Integer page,
        @RequestParam(required = true) Integer rows) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String userId = userInfo.getPersonId();
        try {
            Map<String, Object> map = new HashMap<String, Object>(16);
            if (StringUtils.isNotBlank(listType)) {
                map = workOrderManager.workOrderAdminList(searchTerm, "2", page, rows);
            } else {
                map = workOrderManager.workOrderList(userId, searchTerm, "2", page, rows);
            }
            if ((Boolean)map.get(UtilConsts.SUCCESS)) {
                return Y9Page.success(page, Integer.parseInt(map.get("totalpages").toString()),
                    Integer.parseInt(map.get("total").toString()), (List<Map<String, Object>>)map.get("rows"),
                    "获取列表成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Page.success(page, 0, 0, new ArrayList<Map<String, Object>>(), "获取列表失败");
    }

    /**
     * 获取已处理工单列表
     *
     * @param listType 列表类型
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 条数
     * @return
     */
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value = "/doneList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> doneList(@RequestParam(required = false) String listType,
        @RequestParam(required = false) String searchTerm, @RequestParam(required = true) Integer page,
        @RequestParam(required = true) Integer rows) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String userId = userInfo.getPersonId();
        try {
            Map<String, Object> map = new HashMap<String, Object>(16);
            if (StringUtils.isNotBlank(listType)) {
                map = workOrderManager.workOrderAdminList(searchTerm, "3", page, rows);
            } else {
                map = workOrderManager.workOrderList(userId, searchTerm, "3", page, rows);
            }
            if ((Boolean)map.get(UtilConsts.SUCCESS)) {
                return Y9Page.success(page, Integer.parseInt(map.get("totalpages").toString()),
                    Integer.parseInt(map.get("total").toString()), (List<Map<String, Object>>)map.get("rows"),
                    "获取列表成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Page.success(page, 0, 0, new ArrayList<Map<String, Object>>(), "获取列表失败");
    }

    /**
     * 获取草稿列表
     *
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 条数
     * @return
     */
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value = "/draftList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> draftList(@RequestParam(required = false) String searchTerm,
        @RequestParam(required = true) Integer page, @RequestParam(required = true) Integer rows) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String userId = userInfo.getPersonId();
        try {
            Map<String, Object> map = new HashMap<String, Object>(16);
            map = workOrderManager.workOrderList(userId, searchTerm, "0", page, rows);
            if ((Boolean)map.get(UtilConsts.SUCCESS)) {
                return Y9Page.success(page, Integer.parseInt(map.get("totalpages").toString()),
                    Integer.parseInt(map.get("total").toString()), (List<Map<String, Object>>)map.get("rows"),
                    "获取列表成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Page.success(page, 0, 0, new ArrayList<Map<String, Object>>(), "获取列表失败");
    }

    /**
     * 附件列表
     *
     * @param processSerialNumber 编号
     * @param page 页码
     * @param rows 条数
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/getAttachmentList", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Y9Page<Map<String, Object>> getAttachmentList(@RequestParam(required = true) String processSerialNumber,
        @RequestParam(required = true) int page, @RequestParam(required = true) int rows) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            map = attachmentManager.getAttachmentList(myTenantId, "", processSerialNumber, "", page, rows);
            if ((Boolean)map.get(UtilConsts.SUCCESS)) {
                return Y9Page.success(page, Integer.parseInt(map.get("totalpage").toString()),
                    Integer.parseInt(map.get("total").toString()), (List<Map<String, Object>>)map.get("rows"),
                    "获取列表成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Page.success(page, 0, 0, new ArrayList<Map<String, Object>>(), "获取列表失败");
    }

    /**
     * 获取工单计数
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getCount", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> getCount() {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String userId = userInfo.getPersonId();
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            map = workOrderManager.getCount(userId);
            if ((Boolean)map.get(UtilConsts.SUCCESS)) {
                return Y9Result.successMsg("获取成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("获取失败");
    }

    /**
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "")
    public String index(Model model) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = userInfo.getPersonId();
        model.addAttribute("tenantId", tenantId);
        model.addAttribute("userId", userId);
        model.addAttribute("userName", Y9LoginUserHolder.getUserInfo().getName());
        return "workOrder/index";
    }

    public Map<String, Object> listMapToKeyValue(List<Map<String, Object>> listMap) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        for (Map<String, Object> m : listMap) {
            map.put((String)m.get("name"), (String)m.get("value"));
        }
        return map;
    }

    /**
     * 获取打开工单数据
     *
     * @param processSerialNumber 编号
     * @param itembox 状态
     * @param itemId 事项id
     * @return
     */
    @RequestMapping(value = "/open", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> open(@RequestParam(required = true) String processSerialNumber,
        @RequestParam(required = true) String itembox, @RequestParam(required = false) String itemId) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = userInfo.getPersonId();
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            boolean workOrderManage = personRoleApi.hasRole(tenantId, "itemAdmin", "", "系统工单管理员", userId).getData();
            // 管理员打开待办工单,可走工作流
            // FIXME
            boolean b = workOrderManage && "wtodo".equals(itembox);
            if (b) {
                WorkOrderModel workOrderModel = workOrderManager.findByProcessSerialNumber(processSerialNumber);
                if (workOrderModel.getHandleType().equals(WorkOrderHandleTypeEnum.UNHANDLE.getValue())) {
                    map = documentManager.add(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPersonId(), itemId,
                        false);
                    try {
                        // 管理员打开待办工单，更新统一待办状态
                        todoTaskApi.setIsNewTodo(Y9LoginUserHolder.getTenantId(), processSerialNumber + ":" + userId,
                            "0");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    map.put("isNotTodo", true);
                }
            }
            map.put("processSerialNumber", processSerialNumber);
            map.put("itembox", itembox);
            map.put("workOrderManage", workOrderManage);
            return Y9Result.success(map, "获取成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 打开工单数据
     *
     * @param processSerialNumber 编号
     * @return
     */
    @RequestMapping(value = "/openDetail", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<WorkOrderModel> openDetail(@RequestParam(required = false) String processSerialNumber) {
        try {
            WorkOrderModel workOrder = new WorkOrderModel();
            if (StringUtils.isBlank(processSerialNumber)) {
                workOrder.setGuid(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            } else {
                workOrder = workOrderManager.findByProcessSerialNumber(processSerialNumber);
            }
            return Y9Result.success(workOrder, "获取成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取未处理工单列表
     *
     * @param listType 列表类型
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 条数
     * @return
     */
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value = "/todoList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> todoList(@RequestParam(required = false) String listType,
        @RequestParam(required = false) String searchTerm, @RequestParam(required = true) Integer page,
        @RequestParam(required = true) Integer rows) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String userId = userInfo.getPersonId();
        try {
            Map<String, Object> map = new HashMap<String, Object>(16);
            if (StringUtils.isNotBlank(listType)) {
                map = workOrderManager.workOrderAdminList(searchTerm, "1", page, rows);
            } else {
                map = workOrderManager.workOrderList(userId, searchTerm, "1", page, rows);
            }
            if ((Boolean)map.get(UtilConsts.SUCCESS)) {
                return Y9Page.success(page, Integer.parseInt(map.get("totalpages").toString()),
                    Integer.parseInt(map.get("total").toString()), (List<Map<String, Object>>)map.get("rows"),
                    "获取列表成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Page.success(page, 0, 0, new ArrayList<Map<String, Object>>(), "获取列表失败");
    }

    private String upload(MultipartFile image, byte[] data) throws Exception {
        String fileName = image.getOriginalFilename();
        LocalDate localDate = LocalDate.now();
        String fullPath = Y9FileStore.buildFullPath(Y9Context.getSystemName(), String.valueOf(localDate.getYear()),
            String.valueOf(localDate.getMonth().getValue()), String.valueOf(localDate.getDayOfMonth()));
        Y9FileStore y9FileStore = y9FileStoreService.uploadFile(data, fullPath, fileName);
        return y9FileStore.getUrl();
    }

    /**
     * 上传附件
     *
     * @param file 附件
     * @param processSerialNumber 文件编号
     * @return
     */
    @RiseLog(operationType = OperationTypeEnum.ADD, operationName = "上传附件")
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Y9Result<String> uploadFile(@RequestParam(required = false) MultipartFile file,
        @RequestParam(required = true) String processSerialNumber) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            SimpleDateFormat sdfymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String originalFilename = file.getOriginalFilename();
            String fileName = FilenameUtils.getName(originalFilename);
            String[] types = fileName.split("\\.");
            String type = types[types.length - 1].toLowerCase();
            String fullPath =
                "/" + Y9Context.getSystemName() + "/" + tenantId + "/attachmentFile" + "/" + processSerialNumber;
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(file, fullPath, fileName);

            AttachmentModel attachmentModel = new AttachmentModel();
            attachmentModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            attachmentModel.setName(fileName);
            attachmentModel.setFileSize(y9FileStore.getDisplayFileSize());
            attachmentModel.setFileSource("系统工单");
            attachmentModel.setProcessInstanceId("");
            attachmentModel.setProcessSerialNumber(processSerialNumber);
            attachmentModel.setTaskId("");
            attachmentModel.setUploadTime(sdfymdhms.format(new Date()));
            attachmentModel.setDescribes("");
            attachmentModel.setPersonName(userInfo.getName());
            attachmentModel.setPersonId(userInfo.getPersonId());
            OrgUnit orgUnit = orgUnitApi
                .getParent(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getUserInfo().getPersonId()).getData();
            attachmentModel.setDeptId(orgUnit != null ? orgUnit.getId() : "");
            attachmentModel.setDeptName(orgUnit != null ? orgUnit.getName() : "");
            attachmentModel.setFileStoreId(y9FileStore.getId());
            attachmentModel.setFileType(type);
            attachmentManager.uploadModel(myTenantId, "", attachmentModel);
            return Y9Result.successMsg("上传成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("上传失败");
    }

    /**
     * 上传图片
     *
     * @param image 图片
     * @return
     */
    @RiseLog(operationType = OperationTypeEnum.ADD, operationName = "上传图片")
    @RequestMapping("/uploadImg")
    @ResponseBody
    public Map<String, String> uploadImg(MultipartFile image) {
        Map<String, String> returnMap = new HashMap<String, String>(16);
        int displayWidth = 804;
        byte[] data = null;
        try {
            BufferedImage bufferedImage = ImageIO.read(image.getInputStream());
            if (bufferedImage.getWidth() > displayWidth) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                Thumbnails.of(image.getInputStream()).width(displayWidth).outputQuality(1.0F)
                    .toOutputStream(byteArrayOutputStream);
                data = byteArrayOutputStream.toByteArray();
            } else {
                data = image.getBytes();
            }
            String url = upload(image, data);
            returnMap.put("state", UtilConsts.SUCCESS);
            returnMap.put("url", url);
            returnMap.put("title", image.getOriginalFilename());
            returnMap.put("original", image.getOriginalFilename());
        } catch (Exception e) {
            returnMap.put("state", "ERROR");
            e.printStackTrace();
        }
        return returnMap;
    }

    /**
     * 提交工单
     *
     * @param processSerialNumber 编号
     * @param type 类型
     * @param text 内容
     * @param formdata 表单数据
     * @return
     */
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value = "/workOrderSubmit", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> workOrderSubmit(@RequestParam(required = true) String type,
        @RequestParam(required = true) String formdata) {
        Map<String, Object> resMap = new HashMap<String, Object>(16);
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = userInfo.getPersonId();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Tenant tenant = tenantApi.getById(tenantId).getData();
            Map<String, Object> keyValue = Y9JsonUtil.readValue(formdata, Map.class);
            WorkOrderModel workOrderModel = new WorkOrderModel();
            workOrderModel.setCreateDate(keyValue.get("createDate") != null ? (String)keyValue.get("createDate") : "");
            workOrderModel
                .setDescription(keyValue.get("description") != null ? (String)keyValue.get("description") : "");
            workOrderModel.setCreateTime(sdf.format(new Date()));
            workOrderModel
                .setHtmlDescription(keyValue.get("description") != null ? (String)keyValue.get("description") : "");
            workOrderModel.setHandleType(type.equals(ItemBoxTypeEnum.DRAFT.getValue()) ? "0" : "1");
            workOrderModel.setGuid((String)keyValue.get("guid"));
            workOrderModel.setMobile(keyValue.get("mobile") != null ? (String)keyValue.get("mobile") : "");
            workOrderModel.setNumber(keyValue.get("number") != null ? (String)keyValue.get("number") : "");
            workOrderModel.setRealProcessInstanceId("");
            workOrderModel.setHandler("");
            workOrderModel.setHandlerMobile("");
            workOrderModel.setProcessInstanceId((String)keyValue.get("guid"));
            workOrderModel.setQQ(keyValue.get("QQ") != null ? (String)keyValue.get("QQ") : "");
            workOrderModel.setSuggest(keyValue.get("suggest") != null ? (String)keyValue.get("suggest") : "");
            workOrderModel.setTenantId(tenantId);
            workOrderModel.setTenantName(tenant.getName());
            workOrderModel.setTitle(keyValue.get("title") != null ? (String)keyValue.get("title") : "");
            workOrderModel.setUrgency(keyValue.get("urgency") != null ? (String)keyValue.get("urgency") : "中");
            workOrderModel.setUserId(userId);
            workOrderModel.setUserName(userInfo.getName());
            workOrderModel
                .setWorkOrderType(keyValue.get("workOrderType") != null ? (String)keyValue.get("workOrderType") : "其他");
            resMap = workOrderManager.saveWorkOrder(workOrderModel);
            if ((Boolean)resMap.get(UtilConsts.SUCCESS)) {
                return Y9Result.successMsg("提交成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("提交失败");
    }
}
