package net.risesoft.service;

import java.util.List;
import java.util.Map;

import net.risesoft.model.itemadmin.ItemButtonModel;
import net.risesoft.model.itemadmin.core.DocumentDetailModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2025/08/08
 */
public interface ButtonService {

    /**
     *
     * @param itemId 事项id
     * @param taskId 任务id
     * @param itemBox 事项盒子
     * @return Map<String, Object>
     */
    @Deprecated
    Map<String, Object> showButton(String itemId, String taskId, String itemBox);

    /**
     * 显示添加按钮
     * 
     * @param itemId 事项id
     * @return List<ItemButtonModel> - 按钮列表
     */
    List<ItemButtonModel> showButton4Add(String itemId);

    /**
     * 显示草稿按钮
     * 
     * @param itemId 事项id
     * @return List<ItemButtonModel> - 按钮列表
     */
    List<ItemButtonModel> showButton4Draft(String itemId);

    /**
     * 显示抄送按钮
     * 
     * @param model 办件详情模型
     * @return List<ItemButtonModel> - 按钮列表
     */
    List<ItemButtonModel> showButton4ChaoSong(DocumentDetailModel model);

    /**
     * 显示在办按钮
     * 
     * @param model 办件详情模型
     * @return List<ItemButtonModel> - 按钮列表
     */
    List<ItemButtonModel> showButton4Doing(DocumentDetailModel model);

    /**
     * 显示监控在办按钮
     * 
     * @param model 办件详情模型
     * @return List<ItemButtonModel> - 按钮列表
     */
    List<ItemButtonModel> showButton4DoingAdmin(DocumentDetailModel model);

    /**
     * 显示办结按钮
     * 
     * @param model 办件详情模型
     * @return List<ItemButtonModel> - 按钮列表
     */
    List<ItemButtonModel> showButton4Done(DocumentDetailModel model);

    /**
     * 显示回收站按钮
     * 
     * @return List<ItemButtonModel> - 按钮列表
     */
    List<ItemButtonModel> showButton4Recycle();

    /**
     * 显示待办按钮
     * 
     * @param model 办件详情模型
     * @return List<ItemButtonModel> - 按钮列表
     */
    List<ItemButtonModel> showButton4Todo(DocumentDetailModel model);
}