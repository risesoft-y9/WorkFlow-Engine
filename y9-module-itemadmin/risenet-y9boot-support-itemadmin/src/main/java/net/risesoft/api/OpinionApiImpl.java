package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.opinion.OpinionApi;
import net.risesoft.dto.itemadmin.OpinionDTO;
import net.risesoft.dto.itemadmin.OpinionFrameDTO;
import net.risesoft.entity.opinion.Opinion;
import net.risesoft.model.itemadmin.ItemOpinionFrameBindModel;
import net.risesoft.model.itemadmin.OpinionFrameModel;
import net.risesoft.model.itemadmin.OpinionListModel;
import net.risesoft.model.itemadmin.OpinionModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.config.ItemOpinionFrameBindService;
import net.risesoft.service.opinion.OpinionService;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 意见接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/opinion", produces = MediaType.APPLICATION_JSON_VALUE)
public class OpinionApiImpl implements OpinionApi {

    private final OpinionService opinionService;

    private final ItemOpinionFrameBindService itemOpinionFrameBindService;

    /**
     * 验证当前taskId任务节点是否已经签写意见
     *
     * @param processSerialNumber 流程编号
     * @param taskId 任务id
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - data 是是否已经签写意见
     * @since 9.6.6
     */
    @Override
    public Y9Result<Boolean> checkSignOpinion(@RequestParam String processSerialNumber, String taskId) {
        Boolean result = opinionService.checkSignOpinion(processSerialNumber, taskId);
        return Y9Result.success(result);
    }

    /**
     * 删除意见数据
     *
     * @param id 唯一标识
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @throws Exception Exception
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> delete(@RequestParam String id) throws Exception {
        opinionService.delete(id);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 获取事项绑定的意见框列表
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义Id
     * @return {@code Y9Result<List<ItemOpinionFrameBindModel>>} 通用请求返回对象 - data 是事项意见框绑定信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ItemOpinionFrameBindModel>> getBindOpinionFrame(@RequestParam String itemId,
        String processDefinitionId) {
        List<ItemOpinionFrameBindModel> list = new ArrayList<>();
        List<String> opinionFrameList = itemOpinionFrameBindService.getBindOpinionFrame(itemId, processDefinitionId);
        for (String opinionFrame : opinionFrameList) {
            ItemOpinionFrameBindModel model = new ItemOpinionFrameBindModel();
            model.setOpinionFrameMark(opinionFrame);
            list.add(model);
        }
        return Y9Result.success(list);
    }

    /**
     * 根据id获取意见数据
     *
     * @param id 唯一标识
     * @return {@code Y9Result<OpinionModel>} 通用请求返回对象 - data 是意见信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<OpinionModel> getById(@RequestParam String id) {
        Opinion opinion = opinionService.getById(id);
        OpinionModel opinionModel = new OpinionModel();
        if (opinion != null) {
            Y9BeanUtil.copyProperties(opinion, opinionModel);
        }
        return Y9Result.success(opinionModel);
    }

    /**
     * 获取个人意见列表
     *
     * @param processSerialNumber 流程编号
     * @param taskId 任务id
     * @param itembox 办件状态，todo（待办），doing（在办），done（办结）
     * @param opinionFrameMark 意见框标识
     * @param itemId 事项id
     * @param taskDefinitionKey 任务定义key
     * @return {@code Y9Result<List<OpinionListModel>>} 通用请求返回对象 - data 是意见列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<OpinionListModel>> personCommentList(@RequestParam String processSerialNumber, String taskId,
        @RequestParam String itembox, @RequestParam String opinionFrameMark, @RequestParam String itemId,
        String taskDefinitionKey) {
        List<OpinionListModel> opinionList = opinionService.listPersonComment(processSerialNumber, taskId, itembox,
            opinionFrameMark, itemId, taskDefinitionKey);
        return Y9Result.success(opinionList);
    }

    /**
     * 获取个人意见列表
     *
     * @param opinionFrameDTO 意见框信息
     * @return {@code Y9Result<List<OpinionListModel>>} 通用请求返回对象 - data 是意见列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<OpinionFrameModel> personCommentListNew(@RequestBody OpinionFrameDTO opinionFrameDTO) {
        OpinionFrameModel opinionFrameModel = opinionService.listPersonCommentNew(opinionFrameDTO);
        return Y9Result.success(opinionFrameModel);
    }

    /**
     * 保存意见信息
     *
     * @param opinionModel 意见信息
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @throws Exception Exception
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> save(@RequestBody OpinionModel opinionModel) throws Exception {
        Opinion opinion = new Opinion();
        Y9BeanUtil.copyProperties(opinionModel, opinion);
        opinionService.save(opinion);
        return Y9Result.successMsg("保存成功");
    }

    /**
     * 保存或更新意见信息
     *
     * @param opinionDTO 意见信息
     * @return {@code Y9Result<OpinionModel>} 通用请求返回对象 - data 是意见信息
     * @throws Exception Exception
     * @since 9.6.6
     */
    @Override
    public Y9Result<OpinionModel> saveOrUpdate(@RequestBody OpinionDTO opinionDTO) throws Exception {
        Opinion opinion = opinionService.saveOrUpdate(opinionDTO);
        OpinionModel opinionModel = new OpinionModel();
        Y9BeanUtil.copyProperties(opinion, opinionModel);
        return Y9Result.success(opinionModel);
    }

    /**
     * 更新意见信息
     *
     * @param id 意见id
     * @param content 意见内容
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> updateOpinion(@RequestParam String id, @RequestParam String content) {
        opinionService.updateOpinion(id, content);
        return Y9Result.success();
    }
}
