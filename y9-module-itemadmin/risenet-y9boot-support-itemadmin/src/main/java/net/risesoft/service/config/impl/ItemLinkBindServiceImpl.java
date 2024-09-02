package net.risesoft.service.config.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.permission.RoleApi;
import net.risesoft.entity.ItemLinkBind;
import net.risesoft.entity.ItemLinkRole;
import net.risesoft.entity.LinkInfo;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.Role;
import net.risesoft.repository.jpa.ItemLinkBindRepository;
import net.risesoft.repository.jpa.ItemLinkRoleRepository;
import net.risesoft.repository.jpa.LinkInfoRepository;
import net.risesoft.repository.jpa.SpmApproveItemRepository;
import net.risesoft.service.config.ItemLinkBindService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class ItemLinkBindServiceImpl implements ItemLinkBindService {

    private final ItemLinkBindRepository itemLinkBindRepository;

    private final ItemLinkRoleRepository itemLinkRoleRepository;

    private final LinkInfoRepository linkInfoRepository;

    private final RoleApi roleApi;

    private final SpmApproveItemRepository spmApproveItemRepository;

    @Override
    @Transactional
    public void copyBindInfo(String itemId, String newItemId) {
        try {
            List<ItemLinkBind> bindList = itemLinkBindRepository.findByItemIdOrderByCreateTimeDesc(itemId);
            for (ItemLinkBind bind : bindList) {
                ItemLinkBind newBind = new ItemLinkBind();
                newBind.setItemId(newItemId);
                newBind.setLinkId(bind.getLinkId());
                newBind.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                newBind.setCreateTime(bind.getCreateTime());
                itemLinkBindRepository.save(newBind);
            }
        } catch (Exception e) {
            LOGGER.error("复制链接配置绑定关系失败", e);
        }
    }

    @Override
    @Transactional
    public void deleteBindInfo(String itemId) {
        try {
            List<ItemLinkBind> bindList = itemLinkBindRepository.findByItemIdOrderByCreateTimeDesc(itemId);
            for (ItemLinkBind bind : bindList) {
                itemLinkRoleRepository.deleteByItemLinkId(bind.getId());
                itemLinkBindRepository.deleteById(bind.getId());
            }
        } catch (Exception e) {
            LOGGER.error("删除链接配置绑定关系失败", e);
        }
    }

    @Override
    public List<ItemLinkBind> listByItemId(String itemId) {
        List<ItemLinkBind> bindList = itemLinkBindRepository.findByItemIdOrderByCreateTimeDesc(itemId);
        for (ItemLinkBind bind : bindList) {
            List<String> roleIds = new ArrayList<>();
            List<ItemLinkRole> roleList = itemLinkRoleRepository.findByItemLinkId(bind.getId());
            String roleNames = "";
            for (ItemLinkRole bindrole : roleList) {
                roleIds.add(bindrole.getId());
                Role role = roleApi.getRole(bindrole.getRoleId()).getData();
                if (StringUtils.isEmpty(roleNames)) {
                    roleNames = null == role ? "角色不存在" : role.getName();
                } else {
                    roleNames += "、" + (null == role ? "角色不存在" : role.getName());
                }
            }
            // 放绑定关系id，便于删除
            bind.setRoleIds(roleIds);
            bind.setRoleNames(roleNames);

            LinkInfo linkInfo = linkInfoRepository.findById(bind.getLinkId()).orElse(null);
            bind.setLinkName(linkInfo != null ? linkInfo.getLinkName() : "链接不存在");
            bind.setLinkUrl(linkInfo != null ? linkInfo.getLinkUrl() : "链接不存在");
        }
        return bindList;
    }

    @Override
    public List<ItemLinkRole> listByItemLinkId(String itemLinkId) {
        return itemLinkRoleRepository.findByItemLinkId(itemLinkId);
    }

    @Override
    public List<ItemLinkBind> listByLinkId(String linkId) {
        List<ItemLinkBind> list = itemLinkBindRepository.findByLinkIdOrderByCreateTimeDesc(linkId);
        for (ItemLinkBind bind : list) {
            SpmApproveItem item = spmApproveItemRepository.findById(bind.getItemId()).orElse(null);
            bind.setItemName(item != null ? item.getName() : "事项不存在");
            List<String> roleIds = new ArrayList<>();
            List<ItemLinkRole> roleList = itemLinkRoleRepository.findByItemLinkId(bind.getId());
            String roleNames = "";
            for (ItemLinkRole role : roleList) {
                roleIds.add(role.getId());
                Role r = roleApi.getRole(role.getRoleId()).getData();
                if (StringUtils.isEmpty(roleNames)) {
                    roleNames = null == r ? "角色不存在" : r.getName();
                } else {
                    roleNames += "、" + (null == r ? "角色不存在" : r.getName());
                }
            }
            // 放绑定关系id，便于删除
            bind.setRoleIds(roleIds);
            bind.setRoleNames(roleNames);
        }
        return list;
    }

    @Override
    public List<ItemLinkRole> listWithBindRole(String itemLinkId) {
        List<ItemLinkRole> list = itemLinkRoleRepository.findByItemLinkId(itemLinkId);
        for (ItemLinkRole item : list) {
            Role role = roleApi.getRole(item.getRoleId()).getData();
            item.setRoleName(role == null ? "角色已删除" : role.getName());
        }
        return list;
    }

    @Override
    @Transactional
    public void removeBind(String[] ids) {
        for (String id : ids) {
            itemLinkBindRepository.deleteById(id);
            itemLinkRoleRepository.deleteByItemLinkId(id);
        }
    }

    @Override
    @Transactional
    public void removeRole(String[] ids) {
        for (String id : ids) {
            itemLinkRoleRepository.deleteById(id);
        }
    }

    @Override
    @Transactional
    public void saveBindRole(String itemLinkId, String roleIds) {
        String[] roleIdarr = roleIds.split(";");
        for (String roleId : roleIdarr) {
            ItemLinkRole info = itemLinkRoleRepository.findByItemLinkIdAndRoleId(itemLinkId, roleId);
            if (null == info) {
                info = new ItemLinkRole();
                info.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                info.setItemLinkId(itemLinkId);
                info.setRoleId(roleId);
                itemLinkRoleRepository.save(info);
            }
        }
    }

    @Override
    @Transactional
    public void saveItemLinkBind(String itemId, String[] linkIds) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (String linkId : linkIds) {
            ItemLinkBind item = itemLinkBindRepository.findByLinkIdAndItemId(linkId, itemId);
            if (item == null) {
                item = new ItemLinkBind();
                item.setItemId(itemId);
                item.setLinkId(linkId);
                item.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                item.setCreateTime(sdf.format(new Date()));
                itemLinkBindRepository.save(item);
            }
        }
    }
}
