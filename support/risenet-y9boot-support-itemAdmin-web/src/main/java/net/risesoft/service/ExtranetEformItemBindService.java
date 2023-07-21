package net.risesoft.service;

import java.util.List;
import java.util.Map;

import net.risesoft.entity.ExtranetEformItemBind;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
public interface ExtranetEformItemBindService {

    /**
     * 删除绑定信息
     * 
     * @param id
     * @return
     */
    Map<String, Object> delete(String id);

    /**
     * 根据事项id获取绑定数据
     * 
     * @param itemId
     * @return
     */
    List<ExtranetEformItemBind> getList(String itemId);

    /**
     * 保存绑定信息
     * 
     * @param itemId
     * @param formId
     * @param formName
     * @return
     */
    Map<String, Object> save(String itemId, String formId, String formName);

}
