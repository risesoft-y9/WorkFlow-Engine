package net.risesoft.service.config.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.permission.RoleApi;
import net.risesoft.entity.opinion.ItemOpinionFrameRole;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.Role;
import net.risesoft.repository.opinion.ItemOpinionFrameRoleRepository;
import net.risesoft.service.config.ItemOpinionFrameRoleService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class ItemOpinionFrameRoleServiceImpl implements ItemOpinionFrameRoleService {

    private final ItemOpinionFrameRoleRepository itemOpinionFrameRoleRepository;

    private final RoleApi roleApi;

    @Override
    @Transactional
    public void deleteById(String id) {
        itemOpinionFrameRoleRepository.deleteById(id);
    }

    @Override
    public List<ItemOpinionFrameRole> listByItemOpinionFrameId(String itemOpinionFrameId) {
        return itemOpinionFrameRoleRepository.findByItemOpinionFrameId(itemOpinionFrameId);
    }

    @Override
    public List<ItemOpinionFrameRole> listByItemOpinionFrameIdContainRoleName(String itemOpinionFrameId) {
        List<ItemOpinionFrameRole> roleList =
            itemOpinionFrameRoleRepository.findByItemOpinionFrameId(itemOpinionFrameId);
        for (ItemOpinionFrameRole role : roleList) {
            Role r = roleApi.getRole(role.getRoleId()).getData();
            role.setRoleName(r == null ? "角色已删除" : r.getName());
        }
        return roleList;
    }

    @Override
    @Transactional
    public void remove(String[] ids) {
        for (String id : ids) {
            itemOpinionFrameRoleRepository.deleteById(id);
        }
    }

    @Override
    @Transactional
    public void removeByItemOpinionFrameId(String itemOpinionFrameId) {
        List<ItemOpinionFrameRole> roleList =
            itemOpinionFrameRoleRepository.findByItemOpinionFrameId(itemOpinionFrameId);
        itemOpinionFrameRoleRepository.deleteAll(roleList);
    }

    @Override
    @Transactional
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
