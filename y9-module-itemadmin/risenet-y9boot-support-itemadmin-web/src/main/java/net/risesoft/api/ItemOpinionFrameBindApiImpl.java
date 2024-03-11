package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.ItemOpinionFrameBindApi;
import net.risesoft.entity.ItemOpinionFrameBind;
import net.risesoft.entity.OpinionFrame;
import net.risesoft.model.itemadmin.ItemOpinionFrameBindModel;
import net.risesoft.service.ItemOpinionFrameBindService;
import net.risesoft.service.OpinionFrameService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequestMapping(value = "/services/rest/itemOpinionFrameBind")
public class ItemOpinionFrameBindApiImpl implements ItemOpinionFrameBindApi {

    @Autowired
    private ItemOpinionFrameBindService itemOpinionFrameBindService;

    @Autowired
    private OpinionFrameService opinionFrameService;

    @Override
    @GetMapping(value = "/findByItemId", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ItemOpinionFrameBindModel> findByItemId(String tenantId, String itemId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<ItemOpinionFrameBind> list = itemOpinionFrameBindService.findByItemId(itemId);
        List<ItemOpinionFrameBindModel> modelList = new ArrayList<ItemOpinionFrameBindModel>();
        for (ItemOpinionFrameBind o : list) {
            ItemOpinionFrameBindModel model = new ItemOpinionFrameBindModel();
            Y9BeanUtil.copyProperties(o, model);
            OpinionFrame opinionFrame = opinionFrameService.findByMark(o.getOpinionFrameMark());
            model.setOpinionFrameName(opinionFrame == null ? "意见框不存在" : opinionFrame.getName());
            modelList.add(model);
        }
        return modelList;
    }

    @Override
    @GetMapping(value = "/findByItemIdAndProcessDefinitionId", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ItemOpinionFrameBindModel> findByItemIdAndProcessDefinitionId(String tenantId, String itemId,
        String processDefinitionId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<ItemOpinionFrameBind> list =
            itemOpinionFrameBindService.findByItemIdAndProcessDefinitionId(itemId, processDefinitionId);
        List<ItemOpinionFrameBindModel> modelList = new ArrayList<ItemOpinionFrameBindModel>();
        for (ItemOpinionFrameBind o : list) {
            ItemOpinionFrameBindModel model = new ItemOpinionFrameBindModel();
            Y9BeanUtil.copyProperties(o, model);
            OpinionFrame opinionFrame = opinionFrameService.findByMark(o.getOpinionFrameMark());
            model.setOpinionFrameName(opinionFrame == null ? "意见框不存在" : opinionFrame.getName());
            modelList.add(model);
        }
        return modelList;
    }

    @Override
    @GetMapping(value = "/findByItemIdAndProcessDefinitionIdAndTaskDefKey", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ItemOpinionFrameBindModel> findByItemIdAndProcessDefinitionIdAndTaskDefKey(String tenantId,
        String userId, String itemId, String processDefinitionId, String taskDefKey) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<ItemOpinionFrameBind> list = itemOpinionFrameBindService
            .findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, processDefinitionId, taskDefKey);
        List<ItemOpinionFrameBindModel> modelList = new ArrayList<ItemOpinionFrameBindModel>();
        for (ItemOpinionFrameBind o : list) {
            ItemOpinionFrameBindModel model = new ItemOpinionFrameBindModel();
            Y9BeanUtil.copyProperties(o, model);
            OpinionFrame opinionFrame = opinionFrameService.findByMark(o.getOpinionFrameMark());
            model.setOpinionFrameName(opinionFrame == null ? "意见框不存在" : opinionFrame.getName());
            modelList.add(model);
        }
        return modelList;
    }

    @Override
    @GetMapping(value = "/findByItemIdAndProcessDefinitionIdAndTaskDefKeyContainRole",
        produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ItemOpinionFrameBindModel> findByItemIdAndProcessDefinitionIdAndTaskDefKeyContainRole(String tenantId,
        String userId, String itemId, String processDefinitionId, String taskDefKey) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<ItemOpinionFrameBind> list = itemOpinionFrameBindService
            .findByItemIdAndProcessDefinitionIdAndTaskDefKeyContainRole(itemId, processDefinitionId, taskDefKey);
        List<ItemOpinionFrameBindModel> modelList = new ArrayList<ItemOpinionFrameBindModel>();
        for (ItemOpinionFrameBind o : list) {
            ItemOpinionFrameBindModel model = new ItemOpinionFrameBindModel();
            Y9BeanUtil.copyProperties(o, model);
            OpinionFrame opinionFrame = opinionFrameService.findByMark(o.getOpinionFrameMark());
            model.setOpinionFrameName(opinionFrame == null ? "意见框不存在" : opinionFrame.getName());
            modelList.add(model);
        }
        return modelList;
    }
}
