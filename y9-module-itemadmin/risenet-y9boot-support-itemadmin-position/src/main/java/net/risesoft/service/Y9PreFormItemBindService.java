package net.risesoft.service;

import net.risesoft.entity.Y9PreFormItemBind;

import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface Y9PreFormItemBindService {

    /**
     * Description: 删除绑定
     *
     * @param id 主键id
     * @return
     */
    Map<String, Object> delete(String id);

    /**
     * 获取绑定的表单
     *
     * @param itemId 事项id
     * @return
     */
    Y9PreFormItemBind findByItemId(String itemId);

    /**
     * 保存绑定的表单
     *
     * @param formId 表单id
     * @param itemId 事项id
     * @param formName 表单名称
     * @return
     */
    Map<String, Object> saveBindForm(String itemId, String formId, String formName);

    /**
     * 复制前置表单绑定信息
     * @param itemId
     * @param newItemId
     */
    void copyBindInfo(String itemId,String newItemId);
}
