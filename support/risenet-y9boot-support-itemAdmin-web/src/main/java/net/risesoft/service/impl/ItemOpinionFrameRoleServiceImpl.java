package net.risesoft.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.api.permission.RoleApi;
import net.risesoft.entity.ItemOpinionFrameRole;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.Role;
import net.risesoft.repository.jpa.ItemOpinionFrameRoleRepository;
import net.risesoft.service.ItemOpinionFrameRoleService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service(value = "itemOpinionFrameRoleService")
public class ItemOpinionFrameRoleServiceImpl implements ItemOpinionFrameRoleService {

    @Autowired
    private ItemOpinionFrameRoleRepository itemOpinionFrameRoleRepository;

    @Autowired
    private RoleApi roleManager;

    @Override
    @Transactional(readOnly = false)
    public void deleteById(String id) {
        itemOpinionFrameRoleRepository.deleteById(id);
    }

    @Override
    public List<ItemOpinionFrameRole> findByItemOpinionFrameId(String itemOpinionFrameId) {
        return itemOpinionFrameRoleRepository.findByItemOpinionFrameId(itemOpinionFrameId);
    }

    @Override
    public List<ItemOpinionFrameRole> findByItemOpinionFrameIdContainRoleName(String itemOpinionFrameId) {
        List<ItemOpinionFrameRole> roleList =
            itemOpinionFrameRoleRepository.findByItemOpinionFrameId(itemOpinionFrameId);
        for (ItemOpinionFrameRole role : roleList) {
            Role r = roleManager.getRole(role.getRoleId()).getData();
            role.setRoleName(r == null ? "角色已删除" : r.getName());
        }
        return roleList;
    }

    @Override
    @Transactional(readOnly = false)
    public void remove(String[] ids) {
        for (String id : ids) {
            itemOpinionFrameRoleRepository.deleteById(id);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void removeByItemOpinionFrameId(String itemOpinionFrameId) {
        List<ItemOpinionFrameRole> roleList =
            itemOpinionFrameRoleRepository.findByItemOpinionFrameId(itemOpinionFrameId);
        itemOpinionFrameRoleRepository.deleteAll(roleList);
    }

    @Override
    @Transactional(readOnly = false)
    public ItemOpinionFrameRole saveOrUpdate(String itemOpinionFrameId, String roleId) {
        ItemOpinionFrameRole iofr =
            itemOpinionFrameRoleRepository.findByItemOpinionFrameIdAndRoleId(itemOpinionFrameId, roleId);
        if (null == iofr) {
            iofr = new ItemOpinionFrameRole();
            iofr.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            iofr.setItemOpinionFrameId(itemOpinionFrameId);
            iofr.setRoleId(roleId);

            itemOpinionFrameRoleRepository.save(iofr);
        }
        return iofr;
    }
}
