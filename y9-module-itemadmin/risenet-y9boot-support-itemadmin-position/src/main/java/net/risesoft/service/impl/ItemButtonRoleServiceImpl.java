package net.risesoft.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.api.platform.permission.RoleApi;
import net.risesoft.entity.ItemButtonRole;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.Role;
import net.risesoft.repository.jpa.ItemButtonRoleRepository;
import net.risesoft.service.ItemButtonRoleService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service(value = "itemButtonRoleService")
public class ItemButtonRoleServiceImpl implements ItemButtonRoleService {

    @Autowired
    private ItemButtonRoleRepository itemButtonRoleRepository;

    @Autowired
    private RoleApi roleManager;

    @Override
    @Transactional(readOnly = false)
    public void deleteByItemButtonId(String itemButtonId) {
        List<ItemButtonRole> roleList = itemButtonRoleRepository.findByItemButtonId(itemButtonId);
        itemButtonRoleRepository.deleteAll(roleList);
    }

    @Override
    public List<ItemButtonRole> findByItemButtonId(String itemButtonId) {
        return itemButtonRoleRepository.findByItemButtonId(itemButtonId);
    }

    @Override
    public List<ItemButtonRole> findByItemButtonIdContainRoleName(String itemButtonId) {
        List<ItemButtonRole> roleList = itemButtonRoleRepository.findByItemButtonId(itemButtonId);
        for (ItemButtonRole role : roleList) {
            Role r = roleManager.getRole(role.getRoleId()).getData();
            role.setRoleName(r == null ? "角色已删除" : r.getName());
        }
        return roleList;
    }

    @Override
    @Transactional(readOnly = false)
    public void remove(String[] ids) {
        for (String id : ids) {
            itemButtonRoleRepository.deleteById(id);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void saveOrUpdate(String itemButtonId, String roleId) {
        ItemButtonRole role = itemButtonRoleRepository.findByItemButtonIdAndRoleId(itemButtonId, roleId);
        if (null == role) {
            role = new ItemButtonRole();
            role.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            role.setItemButtonId(itemButtonId);
            role.setRoleId(roleId);

            itemButtonRoleRepository.save(role);
        }
    }
}
