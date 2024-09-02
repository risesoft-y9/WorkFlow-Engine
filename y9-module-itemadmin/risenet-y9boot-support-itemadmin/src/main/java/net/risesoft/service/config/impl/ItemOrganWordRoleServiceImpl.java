package net.risesoft.service.config.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.permission.RoleApi;
import net.risesoft.entity.ItemOrganWordRole;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.Role;
import net.risesoft.repository.jpa.ItemOrganWordRoleRepository;
import net.risesoft.service.config.ItemOrganWordRoleService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class ItemOrganWordRoleServiceImpl implements ItemOrganWordRoleService {

    private final ItemOrganWordRoleRepository itemOrganWordRoleRepository;

    private final RoleApi roleApi;

    @Override
    public void deleteById(String id) {
        itemOrganWordRoleRepository.deleteById(id);
    }

    @Override
    public List<ItemOrganWordRole> listByItemOrganWordBindId(String itemOrganWordBindId) {
        return itemOrganWordRoleRepository.findByItemOrganWordBindId(itemOrganWordBindId);
    }

    @Override
    public List<ItemOrganWordRole> listByItemOrganWordBindIdContainRoleName(String itemOrganWordBindId) {
        List<ItemOrganWordRole> roleList = itemOrganWordRoleRepository.findByItemOrganWordBindId(itemOrganWordBindId);
        for (ItemOrganWordRole role : roleList) {
            Role r = roleApi.getRole(role.getRoleId()).getData();
            role.setRoleName(r == null ? "角色已删除" : r.getName());
        }
        return roleList;
    }

    @Override
    @Transactional
    public void remove(String[] ids) {
        for (String id : ids) {
            itemOrganWordRoleRepository.deleteById(id);
        }
    }

    @Override
    @Transactional
    public void removeByItemOrganWordBindId(String itemOrganWordBindId) {
        List<ItemOrganWordRole> roleList = itemOrganWordRoleRepository.findByItemOrganWordBindId(itemOrganWordBindId);
        itemOrganWordRoleRepository.deleteAll(roleList);
    }

    @Override
    @Transactional
    public ItemOrganWordRole saveOrUpdate(String itemOrganWordBindId, String roleId) {
        ItemOrganWordRole role =
            itemOrganWordRoleRepository.findByItemOrganWordBindIdAndRoleId(itemOrganWordBindId, roleId);
        if (null == role) {
            role = new ItemOrganWordRole();
            role.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            role.setItemOrganWordBindId(itemOrganWordBindId);
            role.setRoleId(roleId);

            itemOrganWordRoleRepository.save(role);
        }
        return role;
    }
}
