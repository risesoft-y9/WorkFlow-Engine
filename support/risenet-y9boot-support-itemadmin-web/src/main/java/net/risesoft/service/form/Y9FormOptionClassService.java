package net.risesoft.service.form;

import java.util.List;
import java.util.Map;

import net.risesoft.entity.form.Y9FormOptionClass;
import net.risesoft.entity.form.Y9FormOptionValue;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
public interface Y9FormOptionClassService {

    /**
     * 删除数据字典
     * 
     * @param type
     * @return
     */
    Map<String, Object> delOptionClass(String type);

    /**
     * 删除数据字典值
     * 
     * @param id
     * @return
     */
    Map<String, Object> delOptionValue(String id);

    /**
     * 获取所有数据字典类型
     * 
     * @return
     */
    List<Y9FormOptionClass> findAllOptionClass();

    /**
     * 获取所有数据字典值
     * 
     * @return
     */
    List<Y9FormOptionValue> findAllOptionValue();

    /**
     * 获取指定的数据字典值
     * 
     * @param id
     * @return
     */
    Y9FormOptionValue findById(String id);

    /**
     * 获取数据字典列表
     * 
     * @param name
     * @return
     */
    List<Y9FormOptionClass> findByName(String name);

    /**
     * 获取指定类型数据
     * 
     * @param type
     * @return
     */
    Y9FormOptionClass findByType(String type);

    /**
     * 获取数据字典值
     * 
     * @param type
     * @return
     */
    List<Y9FormOptionValue> findByTypeOrderByTabIndexAsc(String type);

    /**
     * 保存数据字典
     * 
     * @param optionClass
     * @return
     */
    Map<String, Object> saveOptionClass(Y9FormOptionClass optionClass);

    /**
     * 保存数据字典值
     * 
     * @param optionValue
     * @return
     */
    Map<String, Object> saveOptionValue(Y9FormOptionValue optionValue);

    /**
     * 保存排序
     * 
     * @param ids
     * @return
     */
    Map<String, Object> saveOrder(String ids);

    /**
     * 设置默认选中
     * 
     * @param id
     * @param defaultSelected
     * @return
     */
    Map<String, Object> updateOptionValue(String id);

}
