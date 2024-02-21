package net.risesoft.service.form;

import java.util.List;
import java.util.Map;

import net.risesoft.entity.form.Y9FormField;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
public interface Y9FormFieldService {

    /**
     * 根据表单id获取表单元素绑定信息
     * 
     * @param formId
     * @return
     */
    public List<Y9FormField> findByFormId(String formId);

    /**
     * Description:
     * 
     * @param id
     * @return
     */
    public Y9FormField findById(String id);

    /**
     * 根据tableName查找绑定信息
     * 
     * @param tableName
     * @return
     */
    public List<Y9FormField> findByTableName(String tableName);

    /**
     * Description: 保存字段绑定信息
     * 
     * @param formField
     * @return
     */
    public Map<String, Object> saveOrUpdate(Y9FormField formField);

}
