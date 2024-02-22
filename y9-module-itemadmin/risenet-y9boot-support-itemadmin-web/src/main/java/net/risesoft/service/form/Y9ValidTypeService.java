package net.risesoft.service.form;

import java.util.List;
import java.util.Map;

import net.risesoft.entity.form.Y9ValidType;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
public interface Y9ValidTypeService {

    /**
     * 删除校验规则
     * 
     * @param ids
     * @return
     */
    Map<String, Object> delValidType(String ids);

    /**
     * Description:
     * 
     * @return
     */
    List<Y9ValidType> findAll();

    /**
     * Description:
     * 
     * @param id
     * @return
     */
    Y9ValidType findById(String id);

    /**
     * 获取校验规则定义列表
     * 
     * @param validType
     * @param validCnName
     * @return
     */
    Map<String, Object> getValidTypeList(String validType, String validCnName);

    /**
     * 保存检验规则
     * 
     * @param y9ValidType
     * @return
     */
    Map<String, Object> saveOrUpdate(Y9ValidType y9ValidType);

}
