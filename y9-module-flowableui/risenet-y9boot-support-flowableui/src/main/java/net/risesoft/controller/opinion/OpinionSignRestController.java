package net.risesoft.controller.opinion;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.opinion.OpinionSignApi;
import net.risesoft.model.itemadmin.OpinionSignListModel;
import net.risesoft.model.itemadmin.OpinionSignModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;

/**
 * 办理意见
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/opinionSign", produces = MediaType.APPLICATION_JSON_VALUE)
public class OpinionSignRestController {

    private final OpinionSignApi opinionSignApi;

    /**
     * 验证是否签写意见
     *
     * @param taskId 任务ID
     * @param processSerialNumber 流程编号
     * @return Y9Result<Map < String, Object>>
     */
    @RequestMapping(value = "/checkSignOpinion")
    public Y9Result<Map<String, Object>> checkSignOpinion(@RequestParam(required = false) String taskId,
        @RequestParam String processSerialNumber) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            String userId = person.getPersonId(), tenantId = person.getTenantId();
            Boolean checkSignOpinion =
                opinionSignApi.checkSignOpinion(tenantId, userId, processSerialNumber, taskId).getData();
            map.put("checkSignOpinion", checkSignOpinion);
        } catch (Exception e) {
            LOGGER.error("查询" + taskId + "是否签写意见失败！", e);
        }
        return Y9Result.success(map, "查询成功");
    }

    /**
     * 删除意见
     *
     * @param id 意见id
     * @return Y9Result<String>
     */
    @PostMapping(value = "/delete")
    public Y9Result<String> delete(@RequestParam @NotBlank String id) {
        try {
            opinionSignApi.delete(Y9LoginUserHolder.getTenantId(), id);
            return Y9Result.successMsg("刪除成功");
        } catch (Exception e) {
            LOGGER.error("删除意见失败！", e);
        }
        return Y9Result.failure("删除失败");
    }

    /**
     * 获取意见列表
     *
     * @param processSerialNumber 流程编号
     * @param taskId 任务id
     * @param itembox 办件状态
     * @param opinionFrameMark 意见框标识
     * @return Y9Result<List < OpinionListModel>>
     */
    @GetMapping(value = "/personCommentList")
    public Y9Result<List<OpinionSignListModel>> personCommentList(@RequestParam @NotBlank String processSerialNumber,
        @RequestParam @NotBlank String signDeptDetailId, @RequestParam @NotBlank String itembox,
        @RequestParam(required = false) String taskId, @RequestParam @NotBlank String opinionFrameMark) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId(), tenantId = person.getTenantId();
        String positionId = Y9LoginUserHolder.getPositionId();
        return opinionSignApi.personCommentList(tenantId, userId, positionId, processSerialNumber, signDeptDetailId,
            itembox, taskId, opinionFrameMark);
    }

    /**
     * 获取新增或编辑意见前数据
     *
     * @param id 意见id
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/newOrModify/personalComment")
    public Y9Result<Map<String, Object>> personalComment(@RequestParam(required = false) String id) {
        Map<String, Object> map = new HashMap<>(16);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String tenantId = Y9LoginUserHolder.getTenantId();
        map.put("date", sdf.format(new Date()));
        if (StringUtils.isNotBlank(id)) {
            OpinionSignModel opinion = opinionSignApi.getById(tenantId, id).getData();
            map.put("opinion", opinion);
            map.put("date", opinion.getCreateDate());
        }
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 保存意见
     *
     * @param jsonData 意见实体json
     * @return Y9Result<OpinionModel>
     */
    @PostMapping(value = "/saveOrUpdate")
    public Y9Result<OpinionSignModel> save(@RequestParam @NotBlank String jsonData) {
        try {
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            String userId = person.getPersonId(), tenantId = person.getTenantId();
            OpinionSignModel opinion = Y9JsonUtil.readValue(jsonData, OpinionSignModel.class);
            String positionId = Y9LoginUserHolder.getPositionId();
            OpinionSignModel opinionModel =
                opinionSignApi.saveOrUpdate(tenantId, userId, positionId, opinion).getData();
            return Y9Result.success(opinionModel, "保存成功");
        } catch (Exception e) {
            LOGGER.error("保存意见失败", e);
        }
        return Y9Result.failure("保存失败");
    }
}