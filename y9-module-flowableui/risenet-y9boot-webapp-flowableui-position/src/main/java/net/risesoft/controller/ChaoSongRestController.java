package net.risesoft.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.SpeakInfoApi;
import net.risesoft.api.itemadmin.TransactionWordApi;
import net.risesoft.api.itemadmin.position.AssociatedFile4PositionApi;
import net.risesoft.api.itemadmin.position.Attachment4PositionApi;
import net.risesoft.api.itemadmin.position.ChaoSong4PositionApi;
import net.risesoft.api.itemadmin.position.Document4PositionApi;
import net.risesoft.api.itemadmin.position.OfficeFollow4PositionApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;

/**
 * 抄送
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/chaoSong")
public class ChaoSongRestController {

    private final ChaoSong4PositionApi chaoSong4PositionApi;

    private final Attachment4PositionApi attachment4PositionApi;

    private final TransactionWordApi transactionWordApi;

    private final SpeakInfoApi speakInfoApi;

    private final AssociatedFile4PositionApi associatedFile4PositionApi;

    private final OfficeFollow4PositionApi officeFollow4PositionApi;

    private final Document4PositionApi document4PositionApi;

    private final Y9Properties y9Config;

    /**
     * 改变抄送件意见状态
     *
     * @param id 抄送id
     * @param type 意见状态
     * @return
     */
    @RequestMapping(value = "/changeChaoSongState", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> changeChaoSongState(@RequestParam @NotBlank String id, @RequestParam @NotBlank String type) {
        try {
            chaoSong4PositionApi.changeChaoSongState(Y9LoginUserHolder.getTenantId(), id, type);
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
    @RequestMapping(value = "/changeStatus", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> changeStatus(@RequestParam @NotBlank String[] ids) {
        try {
            chaoSong4PositionApi.changeStatus(Y9LoginUserHolder.getTenantId(), ids);
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
    @RequestMapping(value = "/deleteList", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> deleteList(@RequestParam @NotBlank String[] ids) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            chaoSong4PositionApi.deleteByIds(tenantId, ids);
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
    public Y9Result<Map<String, Object>> detail(@RequestParam @NotBlank String id, @RequestParam @NotBlank String processInstanceId, @RequestParam @NotBlank String itemId, @RequestParam @NotBlank Integer status) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String positionId = Y9LoginUserHolder.getPositionId(), tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            map = chaoSong4PositionApi.detail(person.getTenantId(), positionId, id, processInstanceId, status, false);
            map.put("itemAdminBaseURL", y9Config.getCommon().getItemAdminBaseUrl());
            map.put("jodconverterURL", y9Config.getCommon().getJodconverterBaseUrl());
            map.put("flowableUIBaseURL", y9Config.getCommon().getFlowableBaseUrl());
            map.put("jsVersion", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
            String processSerialNumber = (String)map.get("processSerialNumber");
            Integer fileNum = attachment4PositionApi.fileCounts(tenantId, processSerialNumber);
            int docNum = 0;
            // 是否正文正常
            Map<String, Object> wordMap = transactionWordApi.findWordByProcessSerialNumber(tenantId, processSerialNumber);
            if (!wordMap.isEmpty() && wordMap.size() > 0) {
                docNum = 1;
            }
            int speakInfoNum = speakInfoApi.getNotReadCount(tenantId, person.getPersonId(), processInstanceId);
            int associatedFileNum = associatedFile4PositionApi.countAssociatedFile(tenantId, processSerialNumber);
            map.put("userName", Y9LoginUserHolder.getUserInfo().getName());
            map.put("docNum", docNum);
            map.put("speakInfoNum", speakInfoNum);
            map.put("associatedFileNum", associatedFileNum);
            map.put("fileNum", fileNum);
            map.put("tenantId", tenantId);
            map.put("userId", person.getPersonId());
            int follow = officeFollow4PositionApi.countByProcessInstanceId(tenantId, positionId, processInstanceId);
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
    @SuppressWarnings({"unchecked"})
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> list(@RequestParam @NotBlank String type, @RequestParam String userName, @RequestParam @NotBlank String processInstanceId, @RequestParam @NotBlank int rows, @RequestParam @NotBlank int page) {
        Map<String, Object> map = new HashMap<>(16);
        String tenantId = Y9LoginUserHolder.getTenantId(), senderId = Y9LoginUserHolder.getPositionId();
        try {
            if (type.equals("my")) {
                map = chaoSong4PositionApi.getListBySenderIdAndProcessInstanceId(tenantId, senderId, processInstanceId, userName, rows, page);
            } else {
                map = chaoSong4PositionApi.getListByProcessInstanceId(tenantId, senderId, processInstanceId, userName, rows, page);
            }
            return Y9Page.success(page, Integer.parseInt(map.get("totalpages").toString()), Integer.parseInt(map.get("total").toString()), (List<Map<String, Object>>)map.get("rows"), "获取列表成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Page.success(page, 0, 0, new ArrayList<Map<String, Object>>(), "获取列表失败");
    }

    /**
     * 我的抄送列表，我抄送出去的列表
     *
     * @param searchName 标题文号
     * @param itemId 事项
     * @param userName 接收人
     * @param year 年度
     * @param state 状态
     * @param rows
     * @param page
     * @return
     */
    public Y9Page<Map<String, Object>> myChaoSongList(@RequestParam String searchName, @RequestParam String itemId, @RequestParam String userName, @RequestParam String year, @RequestParam String state, @RequestParam @NotBlank int rows, @RequestParam @NotBlank int page) {
        String positionId = Y9LoginUserHolder.getPositionId(), tenantId = Y9LoginUserHolder.getTenantId();
        return chaoSong4PositionApi.myChaoSongList(tenantId, positionId, searchName, itemId, userName, state, year, page, rows);
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
    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<Map<String, Object>> save(@RequestParam @NotBlank String processInstanceId, @RequestParam @NotBlank String users, @RequestParam String isSendSms, @RequestParam String isShuMing, @RequestParam String smsContent, @RequestParam String smsPersonId, @RequestParam String itemId,
        @RequestParam String processSerialNumber, @RequestParam String processDefinitionKey) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId();
        try {
            Map<String, Object> resMap = new HashMap<String, Object>(16);
            if (StringUtils.isBlank(processInstanceId)) {
                Map<String, Object> map1 = document4PositionApi.startProcess(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId(), itemId, processSerialNumber, processDefinitionKey);
                if ((boolean)map1.get(UtilConsts.SUCCESS)) {
                    processInstanceId = (String)map1.get("processInstanceId");
                    String taskId = (String)map1.get("taskId");
                    resMap.put("processInstanceId", processInstanceId);
                    resMap.put("taskId", taskId);
                } else {
                    return Y9Result.failure("抄送失败，流程启动失败");
                }
            }
            Map<String, Object> map = chaoSong4PositionApi.save(person.getTenantId(), userId, Y9LoginUserHolder.getPositionId(), processInstanceId, users, isSendSms, isShuMing, smsContent, smsPersonId);
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
    @RequestMapping(value = "/search", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> search(@RequestParam String documentTitle, @RequestParam String year, @RequestParam Integer status, @RequestParam @NotBlank int rows, @RequestParam @NotBlank int page) {
        String positionId = Y9LoginUserHolder.getPositionId(), tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            if (status == 0) {
                map = chaoSong4PositionApi.getTodoList(tenantId, positionId, documentTitle, rows, page);
            } else if (status == 1) {
                map = chaoSong4PositionApi.getDoneList(tenantId, positionId, documentTitle, rows, page);
            } else if (status == 2) {
                map = chaoSong4PositionApi.getOpinionChaosongByUserId(tenantId, positionId, documentTitle, rows, page);
            }
            return Y9Page.success(page, Integer.parseInt(map.get("totalpages").toString()), Integer.parseInt(map.get("total").toString()), (List<Map<String, Object>>)map.get("rows"), "获取列表成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Page.success(page, 0, 0, new ArrayList<Map<String, Object>>(), "获取列表失败");
    }
}
