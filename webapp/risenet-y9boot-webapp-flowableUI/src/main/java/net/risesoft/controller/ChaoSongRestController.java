package net.risesoft.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.AssociatedFileApi;
import net.risesoft.api.itemadmin.AttachmentApi;
import net.risesoft.api.itemadmin.ChaoSongInfoApi;
import net.risesoft.api.itemadmin.DocumentApi;
import net.risesoft.api.itemadmin.OfficeFollowApi;
import net.risesoft.api.itemadmin.SpeakInfoApi;
import net.risesoft.api.itemadmin.TransactionWordApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.enums.ItemChaoSongStatusEnum;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@RestController
@RequestMapping(value = "/vue/chaoSong")
public class ChaoSongRestController {

    @Autowired
    private ChaoSongInfoApi chaoSongInfoManager;

    @Autowired
    private AttachmentApi attachmentManager;

    @Autowired
    private TransactionWordApi transactionWordManager;

    @Autowired
    private SpeakInfoApi speakInfoManager;

    @Autowired
    private AssociatedFileApi associatedFileManager;

    @Autowired
    private OfficeFollowApi officeFollowManager;

    @Autowired
    private DocumentApi documentManager;

    @Autowired
    private Y9Properties y9Config;

    /**
     * 改变抄送件意见状态
     *
     * @param id 抄送id
     * @param type 意见状态
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/changeChaoSongState", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> changeChaoSongState(@RequestParam(required = true) String id,
        @RequestParam(required = true) String type) {
        try {
            chaoSongInfoManager.changeChaoSongState(Y9LoginUserHolder.getTenantId(), id, type);
            return Y9Result.successMsg("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("操作失败");
    }

    /**
     * 批量已阅
     *
     * @param ids 抄送id,逗号隔开
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/changeStatus", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> changeStatus(@RequestParam(required = true) String[] ids) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String userId = userInfo.getPersonId();
        try {
            chaoSongInfoManager.changeStatus(userInfo.getTenantId(), userId, ids);
            return Y9Result.successMsg("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("操作失败");
    }

    /**
     * 收回抄送件
     *
     * @param ids 抄送ids，逗号隔开
     * @param processInstanceId 流程实例id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/deleteList", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> deleteList(@RequestParam(required = true) String[] ids) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, false);
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            chaoSongInfoManager.deleteByIds(tenantId, ids);
            return Y9Result.successMsg("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("删除失败");
    }

    /**
     * 获取打开抄送件数据
     *
     * @param id 抄送id
     * @param processInstanceId 流程实例id
     * @param itemId 事项id
     * @param status 抄送状态
     * @return
     */
    @RequestMapping(value = "/detail", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> detail(@RequestParam(required = true) String id,
        @RequestParam(required = true) String processInstanceId, @RequestParam(required = true) String itemId,
        @RequestParam(required = true) Integer status) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String userId = userInfo.getPersonId(), tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            map = chaoSongInfoManager.detail(userInfo.getTenantId(), userId, id, processInstanceId, status, false);
            map.put("itemAdminBaseURL", y9Config.getCommon().getItemAdminBaseUrl());
            map.put("jodconverterURL", y9Config.getCommon().getJodconverterBaseUrl());
            map.put("flowableUIBaseURL", y9Config.getCommon().getFlowableBaseUrl());
            map.put("jsVersion", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
            String processSerialNumber = (String)map.get("processSerialNumber");
            Integer fileNum = attachmentManager.fileCounts(tenantId, userId, processSerialNumber);
            int docNum = 0;
            // 是否正文正常
            Map<String, Object> wordMap =
                transactionWordManager.findWordByProcessSerialNumber(tenantId, processSerialNumber);
            if (!wordMap.isEmpty() && wordMap.size() > 0) {
                docNum = 1;
            }
            int speakInfoNum = speakInfoManager.getNotReadCount(tenantId, userId, processInstanceId);
            int associatedFileNum = associatedFileManager.countAssociatedFile(tenantId, processSerialNumber);
            map.put("userName", Y9LoginUserHolder.getUserInfo().getName());
            map.put("docNum", docNum);
            map.put("speakInfoNum", speakInfoNum);
            map.put("associatedFileNum", associatedFileNum);
            map.put("fileNum", fileNum);
            map.put("tenantId", tenantId);
            map.put("userId", userId);
            int follow = officeFollowManager.countByProcessInstanceId(tenantId, userId, processInstanceId);
            map.put("follow", follow > 0 ? true : false);
            return Y9Result.success(map, "获取成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取抄送信息
     *
     * @param type 类型，my为我的抄送
     * @param userName 收件人
     * @param processInstanceId 流程实例id
     * @param rows 条数
     * @param page 页码
     * @return
     */
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> list(@RequestParam(required = true) String type,
        @RequestParam(required = false) String userName, @RequestParam(required = true) String processInstanceId,
        @RequestParam(required = true) int rows, @RequestParam(required = true) int page) {
        Map<String, Object> map = new HashMap<>(16);
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), senderId = userInfo.getPersonId();
        try {
            boolean b = "my".equals(type);
            if (b) {
                map = chaoSongInfoManager.getListBySenderIdAndProcessInstanceId(tenantId, senderId, processInstanceId,
                    userName, rows, page);
            } else {
                map = chaoSongInfoManager.getListByProcessInstanceId(tenantId, processInstanceId, userName, rows, page);
            }
            return Y9Page.success(page, Integer.parseInt(map.get("totalpages").toString()),
                Integer.parseInt(map.get("total").toString()), (List<Map<String, Object>>)map.get("rows"), "获取列表成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Page.success(page, 0, 0, new ArrayList<Map<String, Object>>(), "获取列表失败");
    }

    /**
     * 抄送
     *
     * @param processInstanceId 流程实例id
     * @param users 收件人
     * @param isSendSms 是否短信提醒
     * @param isShuMing 是否署名
     * @param smsContent 短信内容
     * @param smsPersonId 短信部分提醒人员id
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param processDefinitionKey 流程定义key
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<Map<String, Object>> save(@RequestParam(required = true) String processInstanceId,
        @RequestParam(required = true) String users, @RequestParam(required = false) String isSendSms,
        @RequestParam(required = false) String isShuMing, @RequestParam(required = false) String smsContent,
        @RequestParam(required = false) String smsPersonId, @RequestParam(required = false) String itemId,
        @RequestParam(required = false) String processSerialNumber,
        @RequestParam(required = false) String processDefinitionKey) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String userId = userInfo.getPersonId();
        try {
            Map<String, Object> resMap = new HashMap<String, Object>(16);
            if (StringUtils.isBlank(processInstanceId)) {
                Map<String, Object> map1 = documentManager.startProcess(Y9LoginUserHolder.getTenantId(), userId, itemId,
                    processSerialNumber, processDefinitionKey);
                if ((boolean)map1.get(UtilConsts.SUCCESS)) {
                    processInstanceId = (String)map1.get("processInstanceId");
                    String taskId = (String)map1.get("taskId");
                    resMap.put("processInstanceId", processInstanceId);
                    resMap.put("taskId", taskId);
                } else {
                    return Y9Result.failure("抄送失败，流程启动失败");
                }
            }
            Map<String, Object> map = chaoSongInfoManager.save(userInfo.getTenantId(), userId, processInstanceId, users,
                isSendSms, isShuMing, smsContent, smsPersonId);
            if ((Boolean)map.get(UtilConsts.SUCCESS)) {
                return Y9Result.success(resMap, "抄送成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("抄送失败");
    }

    /**
     * 获取抄送列表
     *
     * @param documentTitle 搜索词
     * @param year 年度
     * @param status 列表类型：0为未阅件，1为已阅件，2为批阅件
     * @param rows 条数
     * @param page 页码
     * @return
     */
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value = "/search", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> search(@RequestParam(required = false) String documentTitle,
        @RequestParam(required = false) String year, @RequestParam(required = false) Integer status,
        @RequestParam(required = true) int rows, @RequestParam(required = true) int page) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String userId = userInfo.getPersonId(), tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            if (status == ItemChaoSongStatusEnum.TODO.getValue()) {
                map = chaoSongInfoManager.getTodoListByUserId(tenantId, userId, documentTitle, rows, page);
            } else if (status == ItemChaoSongStatusEnum.DONE.getValue()) {
                map = chaoSongInfoManager.getDoneListByUserId(tenantId, userId, documentTitle, rows, page);
            } else if (status == ItemChaoSongStatusEnum.OTHER.getValue()) {
                map = chaoSongInfoManager.getOpinionChaosongByUserId(tenantId, userId, documentTitle, rows, page);
            }
            return Y9Page.success(page, Integer.parseInt(map.get("totalpages").toString()),
                Integer.parseInt(map.get("total").toString()), (List<Map<String, Object>>)map.get("rows"), "获取列表成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Page.success(page, 0, 0, new ArrayList<Map<String, Object>>(), "获取列表失败");
    }
}
