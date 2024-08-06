package net.risesoft.service.form;

import java.util.List;

import net.risesoft.entity.form.Y9FormOptionClass;
import net.risesoft.entity.form.Y9FormOptionValue;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface Y9FormOptionClassService {

    /**
     * 删除数据字典
     *
     * @param type
     * @return
     */
    Y9Result<String> delOptionClass(String type);

    /**
     * 删除数据字典值
     *
     * @param id
     * @return
     */
    Y9Result<String> delOptionValue(String id);

    /**
     * 获取指定的数据字典值
     *
     * @param id
     * @return
     */
    Y9FormOptionValue findById(String id);

    /**
     * 获取指定类型数据
     *
     * @param type
     * @return
     */
    Y9FormOptionClass findByType(String type);

    /**
     * 获取所有数据字典类型
     *
     * @return
     */
    List<Y9FormOptionClass> listAllOptionClass();

    /**
     * 获取所有数据字典值
     *
     * @return
     */
    List<Y9FormOptionValue> listAllOptionValue();

    /**
     * 获取数据字典列表
     *
     * @param name
     * @return
     */
    List<Y9FormOptionClass> listByName(String name);

    /**
     * 获取数据字典值
     *
     * @param type
     * @return
     */
    List<Y9FormOptionValue> listByTypeOrderByTabIndexAsc(String type);

    /**
     * 保存数据字典
     *
     * @param optionClass
     * @return
     */
    Y9Result<Y9FormOptionClass> saveOptionClass(Y9FormOptionClass optionClass);

    /**
     * 保存数据字典值
     *
     * @param optionValue
     * @return
     */
    Y9Result<Y9FormOptionValue> saveOptionValue(Y9FormOptionValue optionValue);

    /**
     * 保存排序
     *
     * @param ids
     * @return
     */
    Y9Result<String> saveOrder(String ids);

    /**
     * 设置默认选中
     *
     * @param id
     * @return
     */
    Y9Result<String> updateOptionValue(String id);

}
