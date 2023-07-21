package net.risesoft.service;

import java.util.List;
import java.util.Map;

import net.risesoft.entity.Y9FormItemBind;
import net.risesoft.entity.Y9FormItemMobileBind;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface Y9FormItemBindService {

    /**
     * Description: 复制该事项上一个版本的表单到最新版本，最新版本存在的就不复制
     * 
     * @param itemId
     * @param processDefinitionId
     */
    void copyEform(String itemId, String processDefinitionId);

    /**
     * Description: 删除绑定
     * 
     * @param id
     * @return
     */
    Map<String, Object> delete(String id);

    /**
     * 查找指定事项id和流程定义Id绑定的表单
     *
     * @param itemId
     * @param procDefId
     * @return
     */
    List<Y9FormItemBind> findByItemIdAndProcDefId(String itemId, String procDefId);

    /**
     * 查找指定事项id和流程定义Id和节点key对应的绑定表单，如果当前节点没绑定表单，则查流程绑定的表单
     *
     * @param itemId
     * @param procDefId
     * @param taskDefKey
     * @return
     */
    List<Y9FormItemBind> findByItemIdAndProcDefIdAndTaskDefKey(String itemId, String procDefId, String taskDefKey);

    /**
     * Description:
     * 
     * @param itemId
     * @param processDefinitionId
     * @param taskDefinitionKey
     * @return
     */
    List<Y9FormItemMobileBind> findByItemIdAndProcDefIdAndTaskDefKey4Mobile(String itemId, String processDefinitionId, String taskDefinitionKey);

    /**
     * 查找指定事项id和流程定义Id和节点key对应的绑定表单
     *
     * @param itemId
     * @param procDefId
     * @param taskDefKey
     * @return
     */
    List<Y9FormItemBind> findByItemIdAndProcDefIdAndTaskDefKey4Own(String itemId, String procDefId, String taskDefKey);

    /**
     * Description:
     * 
     * @param itemId
     * @param procDefId
     * @param taskDefKey
     * @return
     */
    List<Y9FormItemMobileBind> findByItemIdAndProcDefIdAndTaskDefKey4OwnMobile(String itemId, String procDefId, String taskDefKey);

    /**
     * 查找指定事项id和流程定义Id和节点key对应的绑定表单，如果当前节点没绑定表单，则查流程绑定的表单
     *
     * @param itemId
     * @param procDefId
     * @return
     */
    List<Y9FormItemBind> findByItemIdAndProcDefIdAndTaskDefKeyIsNull(String itemId, String procDefId);

    /**
     * 根据id获取绑定信息
     *
     * @param id
     * @return
     */
    Y9FormItemBind findOne(String id);

    /**
     * 表单正文、附件是否显示的代码
     *
     * @param eformItemBinds
     * @return
     */
    String getShowOther(List<Y9FormItemBind> eformItemBinds);

    /**
     * Description:
     * 
     * @param eformItem
     * @return
     */
    Map<String, Object> save(Y9FormItemBind eformItem);

    /**
     * Description:
     * 
     * @param eformItem
     * @return
     */
    Map<String, Object> save(Y9FormItemMobileBind eformItem);

}
