package net.risesoft.service;

import java.util.List;

import net.risesoft.entity.TaoHongTemplateType;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
public interface TaoHongTemplateTypeService {
    /**
     * 查找当前租户的所有模板类型
     * 
     * @return
     */
    List<TaoHongTemplateType> findAll();

    /**
     * Description: 查找当前委办局的所有模板类型
     * 
     * @param bureauId
     * @return
     */
    List<TaoHongTemplateType> findByBureauId(String bureauId);

    /**
     * 根据id查抄模板
     * 
     * @param id
     * @return
     */
    TaoHongTemplateType findOne(String id);

    /**
     * 删除模板类型
     * 
     * @param ids
     */
    void removeTaoHongTemplateType(String[] ids);

    /**
     * 保存排序
     * 
     * @param idAndTabIndexs
     */
    void saveOrder(String[] idAndTabIndexs);

    /**
     * 新增或修改模板
     * 
     * @param t
     * @return
     */
    TaoHongTemplateType saveOrUpdate(TaoHongTemplateType t);
}
