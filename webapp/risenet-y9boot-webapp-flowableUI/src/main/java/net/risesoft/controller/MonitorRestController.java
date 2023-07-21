package net.risesoft.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.ItemApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.processadmin.HistoricProcessApi;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.MonitorService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@RestController
@RequestMapping(value = "/vue/monitor")
public class MonitorRestController {

    @Autowired
    private MonitorService monitorService;

    @Autowired
    private HistoricProcessApi historicProcessManager;

    @Autowired
    private ProcessParamApi processParamManager;

    @Autowired
    private ItemApi itemManager;

    /**
     * 单位所有件
     *
     * @param itemId 事项id
     * @param searchName 搜索词
     * @param userName 发起人
     * @param state 办件状态
     * @param year 年度
     * @param page 页码
     * @param rows 条数
     * @return
     */
    @RequestMapping(value = "/deptList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> deptList(@RequestParam(required = true) String itemId, @RequestParam(required = false) String searchName, @RequestParam(required = false) String userName, @RequestParam(required = false) String state, @RequestParam(required = false) String year,
        @RequestParam(required = true) Integer page, @RequestParam(required = true) Integer rows) {
        return monitorService.deptList(itemId, searchName, userName, state, year, page, rows);
    }

    /**
     * 获取所有事项
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getAllItemList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<ItemModel>> getAllItemList() {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<ItemModel> listMap = itemManager.getAllItemList(tenantId);
        return Y9Result.success(listMap, "获取成功");
    }

    /**
     * 监控办件列表
     *
     * @param searchName 搜索词
     * @param itemName 事项名称
     * @param userName 发起人
     * @param state 办件状态
     * @param year 年度
     * @param page 页码
     * @param rows 条数
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/monitorBanjianList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> monitorBanjianList(@RequestParam(required = false) String searchName, @RequestParam(required = false) String itemName, @RequestParam(required = false) String userName, @RequestParam(required = false) String state, @RequestParam(required = false) String year,
        @RequestParam(required = true) Integer page, @RequestParam(required = true) Integer rows) {
        return monitorService.monitorBanjianList(searchName, itemName, userName, state, year, page, rows);
    }

    /**
     * 监控阅件列表
     *
     * @param searchName 搜索词
     * @param itemName 事项名称
     * @param senderName 发送人
     * @param userName 收件人
     * @param state 办件状态
     * @param year 年度
     * @param page 页码
     * @param rows 条数
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/monitorChaosongList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> monitorChaosongList(@RequestParam(required = false) String searchName, @RequestParam(required = false) String itemName, @RequestParam(required = false) String senderName, @RequestParam(required = false) String userName,
        @RequestParam(required = false) String state, @RequestParam(required = false) String year, @RequestParam(required = true) Integer page, @RequestParam(required = true) Integer rows) {
        return monitorService.monitorChaosongList(searchName, itemName, senderName, userName, state, year, page, rows);
    }

    /**
     * 获取监控在办列表
     *
     * @param itemId 事项id
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 条数
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/monitorDoingList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> monitorDoingList(@RequestParam(required = true) String itemId, @RequestParam(required = false) String searchTerm, @RequestParam(required = true) Integer page, @RequestParam(required = true) Integer rows) {
        return monitorService.monitorDoingList(itemId, searchTerm, page, rows);
    }

    /**
     * 获取监控办结列表
     *
     * @param itemId 事项id
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 条数
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/monitorDoneList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> monitorDoneList(@RequestParam(required = true) String itemId, @RequestParam(required = false) String searchTerm, @RequestParam(required = true) Integer page, @RequestParam(required = true) Integer rows) {
        return monitorService.monitorDoneList(itemId, searchTerm, page, rows);
    }

    /**
     * 彻底删除流程实例
     *
     * @param processInstanceIds 流程实例ids，逗号隔开
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/removeProcess", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> removeProcess(@RequestParam(required = true) String processInstanceIds) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        ProcessParamModel processParamModel = null;
        List<String> list = null;
        try {
            if (StringUtils.isNotBlank(processInstanceIds)) {
                list = new ArrayList<String>();
                String[] ids = processInstanceIds.split(SysVariables.COMMA);
                for (String processInstanceId : ids) {
                    processParamModel = processParamManager.findByProcessInstanceId(tenantId, processInstanceId);
                    if (processParamModel != null) {
                        list.add(processParamModel.getProcessSerialNumber());
                    }
                }
            }
            boolean b = historicProcessManager.removeProcess(tenantId, processInstanceIds);
            if (b) {
                return Y9Result.successMsg("删除成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("删除失败");
    }
}
