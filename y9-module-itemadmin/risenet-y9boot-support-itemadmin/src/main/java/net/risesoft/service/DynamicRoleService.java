package net.risesoft.service;

import java.util.List;

import net.risesoft.entity.DynamicRole;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface DynamicRoleService {

    /**
     * Description: 根据动态角色Id查找动态角色
     *
     * @param id
     * @return
     */
    DynamicRole getById(String id);

    /**
     * 查找当前租户的所有动态角色
     *
     * @return
     */
    List<DynamicRole> listAll();

    /**
     * 根据传过来的Id数组删除动态角色
     *
     * @param dynamicRoleIds
     */
    void removeDynamicRoles(String[] dynamicRoleIds);

    /**
     * 保存或者更新动态角色
     *
     * @param dynamicRole
     * @return
     */
    DynamicRole saveOrUpdate(DynamicRole dynamicRole);

    /**
     * 根据名称和类路径查找动态角色
     *
     * @param name
     * @param classPath
     * @return
     */
    DynamicRole findByNameAndClassPath(String name, String classPath);
}
