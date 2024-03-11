package net.risesoft.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.SpeakInfoApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.model.itemadmin.SpeakInfoModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

@RestController
@RequestMapping(value = "/vue/speakInfo")
public class SpeakInfoRestController {

    @Autowired
    private SpeakInfoApi speakInfoApi;

    /**
     * 删除沟通交流信息
     *
     * @param id 信息id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/deleteById", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> deleteById(@RequestParam(required = true) String id) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId(), tenantId = person.getTenantId();
        try {
            Map<String, Object> map = speakInfoApi.deleteById(tenantId, userId, id);
            if ((Boolean)map.get(UtilConsts.SUCCESS)) {
                return Y9Result.successMsg((String)map.get("msg"));
            } else {
                return Y9Result.failure((String)map.get("msg"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("删除失败");
    }

    /**
     * 保存沟通交流信息
     *
     * @param content 内容
     * @param processInstanceId 流程实例id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveOrUpdate(@RequestParam(required = true) String content,
        @RequestParam(required = true) String processInstanceId) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId(), tenantId = person.getTenantId();
        SpeakInfoModel speakInfoModel = new SpeakInfoModel();
        speakInfoModel.setContent(content);
        speakInfoModel.setProcessInstanceId(processInstanceId);
        speakInfoApi.saveOrUpdate(tenantId, userId, speakInfoModel);
        return Y9Result.successMsg("提交成功");
    }

    /**
     * 获取沟通交流列表
     *
     * @param processInstanceId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/speakInfoList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> speakInfoList(@RequestParam(required = true) String processInstanceId) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId(), userName = person.getName(), tenantId = person.getTenantId();
        List<SpeakInfoModel> siModelList = speakInfoApi.findByProcessInstanceId(tenantId, userId, processInstanceId);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put("rows", siModelList);
        map.put("processInstanceId", processInstanceId);
        map.put("userName", userName);
        map.put("userId", userId);
        return Y9Result.success(map, "获取成功");
    }
}
