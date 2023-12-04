package net.risesoft.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 当代中国研究所使用
 *
 * @author zhangchongjie
 * @date 2023/11/20
 */
@RestController
@RequestMapping(value = "/vue/ddyjs")
public class DdyjsRestController {

    @Autowired
    private VariableApi variableManager;

    /**
     * 取消上会
     *
     * @param processInstanceId 流程实例id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/cancelMeeting", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> cancelMeeting(@RequestParam(required = true) String processInstanceId) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            Map<String, Object> val = new HashMap<String, Object>();
            val.put("val", false);
            variableManager.setVariableByProcessInstanceId(tenantId, processInstanceId, "meeting", val);
            return Y9Result.successMsg("取消上会成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("取消上会失败");
    }

    /**
     * 上会
     *
     * @param processInstanceId 流程实例id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/setMeeting", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> setMeeting(@RequestParam(required = true) String processInstanceId) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            Map<String, Object> val = new HashMap<String, Object>();
            val.put("val", true);
            variableManager.setVariableByProcessInstanceId(tenantId, processInstanceId, "meeting", val);
            return Y9Result.successMsg("上会成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("上会失败");
    }
}