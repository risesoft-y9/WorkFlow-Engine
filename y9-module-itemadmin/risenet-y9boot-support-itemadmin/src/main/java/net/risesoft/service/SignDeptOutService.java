package net.risesoft.service;

import java.util.List;

import net.risesoft.entity.SignOutDept;
import net.risesoft.entity.SignOutDeptType;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface SignDeptOutService {

    /**
     * 禁用委外会签部门
     *
     * @param id
     */
    void disableDept(String[] ids, Integer status);

    /**
     * 禁用委外会签部门类型
     *
     * @param id
     */
    void disableType(String id, Integer status);

    /**
     * 获取委外会签部门类型列表
     *
     * @param name
     * @return
     */
    List<SignOutDeptType> getDeptTypeList(String name);

    /**
     * 获取委外会签部门列表
     *
     * @param deptTypeId
     * @param name
     * @param page
     * @param rows
     * @return
     */
    List<SignOutDept> listAll(String deptTypeId, String name, Integer page, Integer rows);

    /**
     * 删除委外会签部门
     *
     * @param ids
     */
    void remove(String[] ids);

    /**
     * 删除委外会签部门类型
     *
     * @param id
     */
    void removeDetpType(String id);

    /**
     * 保存或更新委外会签部门排序
     *
     * @param idAndTabIndexs
     * @return Y9Result<String>
     */
    void saveDeptOrder(String[] idAndTabIndexs);

    /**
     * 保存或更新委外会签部门类型
     *
     * @param info
     * @return Y9Result<String>
     */
    void saveDetpType(SignOutDeptType info);

    /**
     * 保存或更新委外会签部门
     *
     * @param info
     * @return Y9Result<String>
     */
    void saveOrUpdate(SignOutDept info);

    /**
     * 保存或更新委外会签部门类型排序
     *
     * @param idAndTabIndexs
     * @return Y9Result<String>
     */
    void saveTypeOrder(String[] idAndTabIndexs);
}
