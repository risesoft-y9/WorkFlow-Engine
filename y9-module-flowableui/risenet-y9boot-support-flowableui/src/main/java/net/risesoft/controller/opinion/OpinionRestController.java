package net.risesoft.controller.opinion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;


import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.opinion.OpinionApi;
import net.risesoft.dto.itemadmin.OpinionDTO;
import net.risesoft.dto.itemadmin.OpinionFrameDTO;
import net.risesoft.log.FlowableOperationTypeEnum;
import net.risesoft.log.annotation.FlowableLog;
import net.risesoft.model.itemadmin.ItemOpinionFrameBindModel;
import net.risesoft.model.itemadmin.OpinionFrameModel;
import net.risesoft.model.itemadmin.OpinionModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9FlowableHolder;

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
@RequestMapping(value = "/vue/opinion", produces = MediaType.APPLICATION_JSON_VALUE)
public class OpinionRestController {

    private final OpinionApi opinionApi;

    /**
     * 验证是否签写意见
     *
     * @param taskId 任务ID
     * @param processSerialNumber 流程编号
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/checkSignOpinion")
    public Y9Result<Map<String, Object>> checkSignOpinion(@RequestParam(required = false) String taskId,
        @RequestParam String processSerialNumber) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            Boolean checkSignOpinion = opinionApi.checkSignOpinion(processSerialNumber, taskId).getData();
            map.put("checkSignOpinion", checkSignOpinion);
        } catch (Exception e) {
            LOGGER.error("查询{}是否签写意见失败！", taskId, e);
        }
        return Y9Result.success(map, "查询成功");
    }

    /**
     * 删除意见
     *
     * @param id 意见id
     * @return Y9Result<String>
     */
    @FlowableLog(operationName = "删除意见", operationType = FlowableOperationTypeEnum.DELETE)
    @PostMapping(value = "/delete")
    public Y9Result<String> delete(@RequestParam @NotBlank String id) {
        try {
            opinionApi.delete(id);
            return Y9Result.successMsg("刪除成功");
        } catch (Exception e) {
            LOGGER.error("删除意见失败！", e);
        }
        return Y9Result.failure("删除失败");
    }

    /**
     * 获取事项绑定的意见框列表
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @return Y9Result<List < String>>
     */
    @GetMapping(value = "/getBindOpinionFrame")
    public Y9Result<List<ItemOpinionFrameBindModel>> getBindOpinionFrame(@RequestParam @NotBlank String itemId,
        @RequestParam @NotBlank String processDefinitionId) {
        return opinionApi.getBindOpinionFrame(itemId, processDefinitionId);
    }

    /**
     * 获取意见列表
     *
     * @param opinionFrameDTO 意见框数据
     * @return Y9Result<OpinionFrameModel>
     */
    @PostMapping(value = "/personCommentList")
    public Y9Result<OpinionFrameModel> personCommentList(@RequestBody @Valid OpinionFrameDTO opinionFrameDTO) {
        return opinionApi.personCommentListNew(opinionFrameDTO);
    }

    /**
     * 获取新增或编辑意见前数据
     *
     * @param id 意见id
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/newOrModify/personalComment")
    public Y9Result<OpinionModel> personalComment(@RequestParam(required = false) String id) {
        OpinionModel opinion = new OpinionModel();
        if (StringUtils.isNotBlank(id)) {
            opinion = opinionApi.getById(id).getData();
        }
        return Y9Result.success(opinion, "获取成功");
    }

    /**
     * 保存意见
     *
     * @param opinionDTO 意见信息
     * @return Y9Result<OpinionModel>
     */
    @FlowableLog(operationName = "保存意见", operationType = FlowableOperationTypeEnum.SAVE)
    @PostMapping(value = "/saveOrUpdate")
    public Y9Result<OpinionModel> saveOrUpdate(@RequestBody @Valid OpinionDTO opinionDTO) {
        try {
            String positionId = Y9FlowableHolder.getPositionId();
            OpinionModel opinionModel = opinionApi.saveOrUpdate(opinionDTO).getData();
            return Y9Result.success(opinionModel, "保存成功");
        } catch (Exception e) {
            LOGGER.error("保存意见失败", e);
        }
        return Y9Result.failure("保存失败");
    }

    /**
     * 更新意见
     *
     * @param content 意见内容
     * @param id 意见id
     * @return Y9Result<Object>
     */
    @FlowableLog(operationName = "更新意见", operationType = FlowableOperationTypeEnum.SAVE)
    @PostMapping(value = "/updateOpinion")
    public Y9Result<Object> updateOpinion(@RequestParam @NotBlank String content, @RequestParam @NotBlank String id) {
        return opinionApi.updateOpinion(id, content);
    }
}