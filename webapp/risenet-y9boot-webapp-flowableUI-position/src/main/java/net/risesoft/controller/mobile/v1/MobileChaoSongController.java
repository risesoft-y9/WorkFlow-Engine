package net.risesoft.controller.mobile.v1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.position.ChaoSong4PositionApi;
import net.risesoft.api.itemadmin.position.ItemRole4PositionApi;
import net.risesoft.api.org.PersonApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.exception.GlobalErrorCodeEnum;
import net.risesoft.model.platform.Person;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.util.DocumentUtil;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 抄送阅件相关接口
 *
 * @author zhangchongjie
 * @date 2024/01/17
 */
@RestController
@RequestMapping("/mobile/v1/chaosong")
public class MobileChaoSongController {
    protected final Logger log = LoggerFactory.getLogger(MobileChaoSongController.class);

    @Autowired
    private ChaoSong4PositionApi chaoSongInfoManager;

    @Autowired
    private ItemRole4PositionApi itemRoleManager;

    @Autowired
    private PersonApi personManager;

    /**
     * 抄送件收回
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param ids 抄送件ids
     * @param response
     */
    @ResponseBody
    @RequestMapping(value = "/deleteList")
    public Y9Result<String> deleteList(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, String ids, String processInstanceId, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        try {
            String id[] = ids.split(",");
            chaoSongInfoManager.deleteByIds(tenantId, id);
            return Y9Result.successMsg("收回成功");
        } catch (Exception e) {
            log.error("手机端跟踪抄送件收回");
            e.printStackTrace();
        }
        return Y9Result.failure("收回失败");
    }

    /**
     * 抄送件详情
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param id 抄送id
     * @param processInstanceId 流程实例id
     * @param status 状态0为未阅件打开，1为已阅件打开
     * @param response
     */
    @RequestMapping(value = "/detail")
    @ResponseBody
    public Y9Result<Map<String, Object>> detail(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam(required = false) String id, @RequestParam(required = false) String processInstanceId,
        Integer status, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            Y9LoginUserHolder.setPositionId(positionId);
            Person person = personManager.getPerson(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);
            map = chaoSongInfoManager.detail(tenantId, positionId, id, processInstanceId, status, true);
            String processSerialNumber = (String)map.get("processSerialNumber");
            String activitiUser = (String)map.get(SysVariables.ACTIVITIUSER);
            String processDefinitionId = (String)map.get("processDefinitionId");
            String taskDefKey = (String)map.get("taskDefKey");
            String formIds = (String)map.get("formId");
            String formNames = (String)map.get("formName");
            String taskId = (String)map.get("taskId");
            String itemId = (String)map.get("itemId");
            String itembox = (String)map.get("itembox");
            DocumentUtil documentUtil = new DocumentUtil();
            Map<String, Object> dataMap = documentUtil.documentDetail(itemId, processDefinitionId, processSerialNumber, processInstanceId, taskDefKey, taskId, itembox, activitiUser, formIds, formNames);
            map.putAll(dataMap);
            return Y9Result.success(map, "获取成功");
        } catch (Exception e) {
            log.error("手机端跟踪查看抄送件详情");
            e.printStackTrace();
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取抄送选人
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param id 父节点id
     * @param principalType 选择类型 2是部门，3是群组
     * @param processInstanceId 流程实例id
     * @param response
     */
    @RequestMapping(value = "/findCsUser")
    @ResponseBody
    public Y9Result<List<Map<String, Object>>> findCsUserBureau(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam(required = false) String id,
        @RequestParam(required = false) Integer principalType, @RequestParam(required = false) String processInstanceId, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<Map<String, Object>> item = new ArrayList<Map<String, Object>>();
        try {
            item = itemRoleManager.findCsUser(Y9LoginUserHolder.getTenantId(), userId, positionId, id, principalType, processInstanceId);
            return Y9Result.success(item, "获取成功");
        } catch (Exception e) {
            log.error("手机端跟踪获取抄送选人");
            e.printStackTrace();
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 选人搜索
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param principalType 选择类型 2是部门，3是群组
     * @param processInstanceId 流程实例id
     * @param name 搜索姓名
     * @param response
     */
    @RequestMapping(value = "/findCsUserSearch")
    @ResponseBody
    public Y9Result<List<Map<String, Object>>> findCsUserSearch(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam(required = false) String name,
        @RequestParam(required = false) Integer principalType, @RequestParam(required = false) String processInstanceId, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<Map<String, Object>> item = new ArrayList<Map<String, Object>>();
        try {
            item = itemRoleManager.findCsUserSearch(tenantId, userId, positionId, name, principalType, processInstanceId);
            return Y9Result.success(item, "获取成功");
        } catch (Exception e) {
            log.error("手机端跟踪选人搜索");
            e.printStackTrace();
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 办件抄送列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param type，“my”为我的抄送，其余为所有抄送
     * @param processInstanceId 流程实例id
     * @param rows 行数
     * @param page 页码
     * @param response
     */
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value = "/list")
    public Y9Page<Map<String, Object>> list(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, String type, String processInstanceId, int rows, int page, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            if (type.equals("my")) {
                map = chaoSongInfoManager.getListBySenderIdAndProcessInstanceId(tenantId, positionId, processInstanceId, "", rows, page);
            } else {
                map = chaoSongInfoManager.getListByProcessInstanceId(tenantId, positionId, processInstanceId, "", rows, page);
            }
            if ((boolean)map.get("success")) {
                List<Map<String, Object>> list = (List<Map<String, Object>>)map.get("rows");
                return Y9Page.success(page, Integer.valueOf(map.get("totalpages").toString()), Long.valueOf(map.get("total").toString()), list, "获取成功");
            }
        } catch (Exception e) {
            log.error("手机端跟踪办件抄送列表");
            e.printStackTrace();
        }
        return Y9Page.failure(page, 0, 0, new ArrayList<Map<String, Object>>(), "获取失败", GlobalErrorCodeEnum.FAILURE.getCode());
    }

    /**
     * 抄送件列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param documentTitle 搜索标题
     * @param status 状态，0为未阅件，1为已阅件
     * @param rows 行数
     * @param page 页码
     * @param response
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/search")
    @ResponseBody
    public Y9Page<Map<String, Object>> search(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam(required = false) String year, @RequestParam(required = false) String documentTitle,
        Integer status, int rows, int page, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            if (status == 0) {
                map = chaoSongInfoManager.getTodoList(tenantId, positionId, documentTitle, rows, page);
            } else if (status == 1) {
                map = chaoSongInfoManager.getDoneList(tenantId, positionId, documentTitle, rows, page);
            }
            List<Map<String, Object>> list = (List<Map<String, Object>>)map.get("rows");
            return Y9Page.success(page, Integer.valueOf(map.get("totalpages").toString()), Long.valueOf(map.get("total").toString()), list, "获取成功");
        } catch (Exception e) {
            log.error("手机端跟踪查看抄送件列表");
            e.printStackTrace();
        }
        return Y9Page.failure(page, 0, 0, new ArrayList<Map<String, Object>>(), "获取失败", GlobalErrorCodeEnum.FAILURE.getCode());
    }

    /**
     * 抄送发送
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param processInstanceId 流程实例id
     * @param users 发送人员
     * @param isSendSms 是否短信提醒
     * @param isShuMing 是否署名
     * @param smsContent 短信内容
     * @param response
     */
    @RequestMapping(value = "/send")
    @ResponseBody
    public Y9Result<String> send(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam(required = false) String processInstanceId, @RequestParam(required = false) String users,
        @RequestParam(required = false) String isSendSms, @RequestParam(required = false) String isShuMing, @RequestParam(required = false) String smsContent, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<>(1);
        try {
            map = chaoSongInfoManager.save(tenantId, userId, positionId, processInstanceId, users, isSendSms, isShuMing, smsContent, "");
            if ((boolean)map.get("success")) {
                return Y9Result.success("抄送成功");
            }
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            log.error("手机端跟踪查看抄送件发送");
            e.printStackTrace();
        }
        return Y9Result.failure("抄送失败");
    }
}
