package net.risesoft.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.NotBlank;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.SpeakInfoApi;
import net.risesoft.model.itemadmin.SpeakInfoModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 沟通交流
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/speakInfo", produces = MediaType.APPLICATION_JSON_VALUE)
public class SpeakInfoRestController {

    private final SpeakInfoApi speakInfoApi;

    /**
     * 删除沟通交流信息
     *
     * @param id 信息id
     * @return Y9Result<String>
     */
    @PostMapping(value = "/deleteById")
    public Y9Result<Object> deleteById(@RequestParam @NotBlank String id) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId(), tenantId = person.getTenantId();
        return speakInfoApi.deleteById(tenantId, userId, id);
    }

    /**
     * 保存沟通交流信息
     *
     * @param content 内容
     * @param processInstanceId 流程实例id
     * @return Y9Result<String>
     */
    @PostMapping(value = "/saveOrUpdate")
    public Y9Result<String> saveOrUpdate(@RequestParam @NotBlank String content,
        @RequestParam @NotBlank String processInstanceId) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId(), tenantId = person.getTenantId();
        SpeakInfoModel speakInfoModel = new SpeakInfoModel();
        speakInfoModel.setContent(content);
        speakInfoModel.setProcessInstanceId(processInstanceId);
        speakInfoApi.saveOrUpdate(tenantId, userId, speakInfoModel);
        return Y9Result.successMsg("提交成功");
    }

    /**
     * 获取沟通交流信息列表
     *
     * @param processInstanceId 流程实例id
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/speakInfoList")
    public Y9Result<Map<String, Object>> speakInfoList(@RequestParam @NotBlank String processInstanceId) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId(), userName = person.getName(), tenantId = person.getTenantId();
        List<SpeakInfoModel> siModelList =
            speakInfoApi.findByProcessInstanceId(tenantId, userId, processInstanceId).getData();
        Map<String, Object> map = new HashMap<>(16);
        map.put("rows", siModelList);
        map.put("processInstanceId", processInstanceId);
        map.put("userName", userName);
        map.put("userId", userId);
        return Y9Result.success(map, "获取成功");
    }
}
