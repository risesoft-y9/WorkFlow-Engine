package net.risesoft.service.config.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.entity.Item;
import net.risesoft.entity.button.CommonButton;
import net.risesoft.entity.button.ItemButtonBind;
import net.risesoft.entity.button.ItemButtonRole;
import net.risesoft.entity.button.SendButton;
import net.risesoft.enums.ItemButtonTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.button.ItemButtonBindRepository;
import net.risesoft.repository.jpa.ItemRepository;
import net.risesoft.service.CommonButtonService;
import net.risesoft.service.SendButtonService;
import net.risesoft.service.config.ItemButtonBindService;
import net.risesoft.service.config.ItemButtonRoleService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class ItemButtonBindServiceImpl implements ItemButtonBindService {

    private final ItemButtonBindRepository buttonItemBindRepository;

    private final CommonButtonService commonButtonService;

    private final SendButtonService sendButtonService;

    private final ItemButtonRoleService itemButtonRoleService;

    private final ItemRepository itemRepository;

    private final RepositoryApi repositoryApi;

    private final ProcessDefinitionApi processDefinitionApi;

    @Override
    @Transactional
    public void bindButton(String itemId, String buttonId, String processDefinitionId, String taskDefKey,
        ItemButtonTypeEnum buttonType) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId(), userName = person.getName(), tenantId = Y9LoginUserHolder.getTenantId();
        ItemButtonBind bib = new ItemButtonBind();
        bib.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        bib.setButtonId(buttonId);
        bib.setButtonType(buttonType);
        bib.setItemId(itemId);
        bib.setProcessDefinitionId(processDefinitionId);
        bib.setTaskDefKey(taskDefKey);
        bib.setTenantId(tenantId);
        bib.setCreateTime(sdf.format(new Date()));
        bib.setUpdateTime(sdf.format(new Date()));
        bib.setUserId(userId);
        bib.setUserName(userName);

        Integer index = buttonItemBindRepository.getMaxTabIndex(itemId, processDefinitionId, taskDefKey, buttonType);
        if (index == null) {
            bib.setTabIndex(1);
        } else {
            bib.setTabIndex(index + 1);
        }
        buttonItemBindRepository.save(bib);
    }

    @Override
    @Transactional
    public void copyBind(String itemId, String processDefinitionId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = person.getPersonId(), userName = person.getName();
        Item item = itemRepository.findById(itemId).orElse(null);
        String proDefKey = item.getWorkflowGuid();
        ProcessDefinitionModel latestpd = repositoryApi.getLatestProcessDefinitionByKey(tenantId, proDefKey).getData();
        String latestpdId = latestpd.getId();
        String previouspdId = processDefinitionId;
        if (processDefinitionId.equals(latestpdId)) {
            if (latestpd.getVersion() > 1) {
                ProcessDefinitionModel previouspd =
                    repositoryApi.getPreviousProcessDefinitionById(tenantId, latestpdId).getData();
                previouspdId = previouspd.getId();
            }
        }
        List<TargetModel> nodes = processDefinitionApi.getNodes(tenantId, latestpdId).getData();
        for (TargetModel targetModel : nodes) {
            String currentTaskDefKey = targetModel.getTaskDefKey();
            List<ItemButtonBind> bindList;
            if (StringUtils.isBlank(currentTaskDefKey)) {
                bindList = buttonItemBindRepository
                    .findByItemIdAndProcessDefinitionIdAndTaskDefKeyIsNullOrderByTabIndexAsc(itemId, previouspdId);
            } else {
                bindList = buttonItemBindRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyOrderByTabIndexAsc(
                    itemId, previouspdId, currentTaskDefKey);
            }
            for (ItemButtonBind bind : bindList) {
                ItemButtonBind oldBind;
                if (StringUtils.isBlank(currentTaskDefKey)) {
                    oldBind = buttonItemBindRepository
                        .findByItemIdAndProcessDefinitionIdAndTaskDefKeyIsNullAndButtonIdOrderByTabIndexAsc(itemId,
                            latestpdId, bind.getButtonId());
                } else {
                    oldBind = buttonItemBindRepository
                        .findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndButtonIdOrderByTabIndexAsc(itemId,
                            latestpdId, currentTaskDefKey, bind.getButtonId());
                }
                if (null == oldBind) {
                    String newbindId = Y9IdGenerator.genId(IdType.SNOWFLAKE), oldbindId = bind.getId();
                    /*
                     * 保存按钮的绑定
                     */
                    ItemButtonBind newBind = new ItemButtonBind();
                    newBind.setId(newbindId);
                    newBind.setButtonId(bind.getButtonId());
                    newBind.setButtonType(bind.getButtonType());
                    newBind.setItemId(itemId);
                    newBind.setProcessDefinitionId(latestpdId);
                    newBind.setTaskDefKey(currentTaskDefKey);
                    newBind.setTenantId(tenantId);
                    newBind.setTenantId(tenantId);
                    newBind.setCreateTime(sdf.format(new Date()));
                    newBind.setUpdateTime(sdf.format(new Date()));
                    newBind.setUserId(userId);
                    newBind.setUserName(userName);

                    Integer index = buttonItemBindRepository.getMaxTabIndex(itemId, latestpdId, currentTaskDefKey,
                        bind.getButtonType());
                    if (index == null) {
                        newBind.setTabIndex(1);
                    } else {
                        newBind.setTabIndex(index + 1);
                    }

                    buttonItemBindRepository.save(newBind);
                    /*
                     * 保存按钮的授权
                     */
                    List<ItemButtonRole> roleList = itemButtonRoleService.listByItemButtonId(oldbindId);
                    for (ItemButtonRole role : roleList) {
                        itemButtonRoleService.saveOrUpdate(newbindId, role.getRoleId());
                    }
                }
            }
        }
    }

    @Override
    @Transactional
    public void copyBindInfo(String itemId, String newItemId, String lastVersionPid) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = person.getPersonId(), userName = person.getName();
        try {
            List<ItemButtonBind> bindList =
                buttonItemBindRepository.findByItemIdAndProcessDefinitionIdOrderByTabIndexAsc(itemId, lastVersionPid);
            if (null != bindList && !bindList.isEmpty()) {
                for (ItemButtonBind bind : bindList) {
                    ItemButtonBind newBind = new ItemButtonBind();
                    newBind.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                    newBind.setButtonId(bind.getButtonId());
                    newBind.setButtonType(bind.getButtonType());
                    newBind.setItemId(newItemId);
                    newBind.setProcessDefinitionId(lastVersionPid);
                    newBind.setTaskDefKey(bind.getTaskDefKey());
                    newBind.setTenantId(tenantId);
                    newBind.setCreateTime(sdf.format(new Date()));
                    newBind.setUpdateTime(sdf.format(new Date()));
                    newBind.setUserId(userId);
                    newBind.setUserName(userName);
                    newBind.setTabIndex(bind.getTabIndex());
                    buttonItemBindRepository.save(newBind);
                }
            }
        } catch (Exception e) {
            LOGGER.error("复制按钮绑定信息失败", e);
        }
    }

    @Override
    @Transactional
    public void deleteBindInfo(String itemId) {
        try {
            List<ItemButtonBind> bindList = buttonItemBindRepository.findByItemId(itemId);
            if (null != bindList && !bindList.isEmpty()) {
                for (ItemButtonBind bind : bindList) {
                    itemButtonRoleService.deleteByItemButtonId(bind.getId());
                    buttonItemBindRepository.deleteById(bind.getId());
                }
            }
        } catch (Exception e) {
            LOGGER.error("删除按钮绑定信息失败", e);
        }
    }

    @Override
    public ItemButtonBind getById(String id) {
        return buttonItemBindRepository.findById(id).orElse(null);
    }

    @Override
    public List<ItemButtonBind> listByButtonId(String buttonId) {
        List<ItemButtonBind> bindList =
            buttonItemBindRepository.findByButtonIdOrderByItemIdDescUpdateTimeDesc(buttonId);
        for (ItemButtonBind bind : bindList) {
            List<ItemButtonRole> roleList = itemButtonRoleService.listByItemButtonIdContainRoleName(bind.getId());
            String roleNames = "";
            for (ItemButtonRole role : roleList) {
                if (StringUtils.isEmpty(roleNames)) {
                    roleNames = role.getRoleName();
                } else {
                    roleNames += "、" + role.getRoleName();
                }
            }
            bind.setRoleNames(roleNames);
        }
        return bindList;
    }

    @Override
    public List<ItemButtonBind> listByItemIdAndButtonTypeAndProcessDefinitionId(String itemId,
        ItemButtonTypeEnum buttonType, String processDefinitionId) {
        String buttonName = "按钮不存在";
        String buttonCustomId = "";
        List<ItemButtonBind> bibList = buttonItemBindRepository
            .findByItemIdAndButtonTypeAndProcessDefinitionIdOrderByTabIndexAsc(itemId, buttonType, processDefinitionId);
        for (ItemButtonBind bib : bibList) {
            if (buttonType == ItemButtonTypeEnum.COMMON) {
                CommonButton cb = commonButtonService.getById(bib.getButtonId());
                if (null != cb) {
                    buttonName = cb.getName();
                    buttonCustomId = cb.getCustomId();
                }
            } else {
                SendButton sb = sendButtonService.getById(bib.getButtonId());
                if (null != sb) {
                    buttonName = sb.getName();
                    buttonCustomId = sb.getCustomId();
                }
            }
            bib.setButtonName(buttonName);
            bib.setButtonCustomId(buttonCustomId);
        }
        return bibList;
    }

    @Override
    public List<ItemButtonBind> listByItemIdAndButtonTypeAndProcessDefinitionIdAndTaskDefKey(String itemId,
        ItemButtonTypeEnum buttonType, String processDefinitionId, String taskDefKey) {
        String buttonName = "按钮不存在";
        String buttonCustomId = "";
        List<ItemButtonBind> bibList =
            buttonItemBindRepository.findByItemIdAndButtonTypeAndProcessDefinitionIdAndTaskDefKeyOrderByTabIndexAsc(
                itemId, buttonType, processDefinitionId, taskDefKey);
        for (ItemButtonBind bib : bibList) {
            if (buttonType == ItemButtonTypeEnum.COMMON) {
                CommonButton cb = commonButtonService.getById(bib.getButtonId());
                if (null != cb) {
                    buttonName = cb.getName();
                    buttonCustomId = cb.getCustomId();
                }
            } else {
                SendButton sb = sendButtonService.getById(bib.getButtonId());
                if (null != sb) {
                    buttonName = sb.getName();
                    buttonCustomId = sb.getCustomId();
                }
            }
            bib.setButtonName(buttonName);
            bib.setButtonCustomId(buttonCustomId);
        }
        return bibList;
    }

    @Override
    public List<ItemButtonBind> listContainRole(String itemId, ItemButtonTypeEnum buttonType,
        String processDefinitionId, String taskDefineKey) {
        String buttonName = "按钮不存在";
        String buttonCustomId = "";
        List<ItemButtonBind> bindList =
            buttonItemBindRepository.findByItemIdAndButtonTypeAndProcessDefinitionIdAndTaskDefKeyOrderByTabIndexAsc(
                itemId, buttonType, processDefinitionId, taskDefineKey);
        for (ItemButtonBind bind : bindList) {
            if (buttonType == ItemButtonTypeEnum.COMMON) {
                CommonButton cb = commonButtonService.getById(bind.getButtonId());
                if (null != cb) {
                    buttonName = cb.getName();
                    buttonCustomId = cb.getCustomId();
                }
            } else {
                SendButton sb = sendButtonService.getById(bind.getButtonId());
                if (null != sb) {
                    buttonName = sb.getName();
                    buttonCustomId = sb.getCustomId();
                }
            }
            bind.setButtonName(buttonName);
            bind.setButtonCustomId(buttonCustomId);

            List<ItemButtonRole> roleList = itemButtonRoleService.listByItemButtonIdContainRoleName(bind.getId());
            List<String> roleIds = new ArrayList<>();
            String roleNames = "";
            for (ItemButtonRole role : roleList) {
                // 存绑定关系id，便于删除
                roleIds.add(role.getId());
                if (StringUtils.isEmpty(roleNames)) {
                    roleNames = role.getRoleName();
                } else {
                    roleNames += "、" + role.getRoleName();
                }
            }
            bind.setRoleIds(roleIds);
            bind.setRoleNames(roleNames);
        }
        return bindList;
    }

    @Override
    public List<ItemButtonBind> listContainRoleId(String itemId, ItemButtonTypeEnum buttonType,
        String processDefinitionId, String taskDefineKey) {
        String buttonName = "按钮不存在";
        String buttonCustomId = "";
        List<ItemButtonBind> bindList =
            buttonItemBindRepository.findByItemIdAndButtonTypeAndProcessDefinitionIdAndTaskDefKeyOrderByTabIndexAsc(
                itemId, buttonType, processDefinitionId, taskDefineKey);
        if (bindList.isEmpty()) {
            bindList = buttonItemBindRepository
                .findByItemIdAndButtonTypeAndProcessDefinitionIdAndTaskDefKeyIsNullOrderByTabIndexAsc(itemId,
                    buttonType, processDefinitionId);
        }
        for (ItemButtonBind bind : bindList) {
            if (buttonType.equals(ItemButtonTypeEnum.COMMON)) {
                CommonButton cb = commonButtonService.getById(bind.getButtonId());
                if (null != cb) {
                    buttonName = cb.getName();
                    buttonCustomId = cb.getCustomId();
                }
            } else {
                SendButton sb = sendButtonService.getById(bind.getButtonId());
                if (null != sb) {
                    buttonName = sb.getName();
                    buttonCustomId = sb.getCustomId();
                }
            }
            bind.setButtonName(buttonName);
            bind.setButtonCustomId(buttonCustomId);

            List<ItemButtonRole> roleList = itemButtonRoleService.listByItemButtonId(bind.getId());
            List<String> roleIds = new ArrayList<>();
            for (ItemButtonRole role : roleList) {
                roleIds.add(role.getRoleId());
            }
            bind.setRoleIds(roleIds);
        }
        return bindList;
    }

    @Override
    public List<ItemButtonBind> listExtra(String itemId, ItemButtonTypeEnum buttonType, String processDefinitionId,
        String taskDefineKey) {
        String buttonName = "按钮不存在";
        String buttonCustomId = "";
        List<ItemButtonBind> bibList =
            buttonItemBindRepository.findByItemIdAndButtonTypeAndProcessDefinitionIdAndTaskDefKeyOrderByTabIndexAsc(
                itemId, buttonType, processDefinitionId, taskDefineKey);
        if (bibList.isEmpty()) {
            bibList =
                buttonItemBindRepository.findByItemIdAndButtonTypeAndProcessDefinitionIdAndTaskDefKeyOrderByTabIndexAsc(
                    itemId, buttonType, processDefinitionId, "");
        }
        for (ItemButtonBind bib : bibList) {
            if (buttonType == ItemButtonTypeEnum.COMMON) {
                CommonButton cb = commonButtonService.getById(bib.getButtonId());
                if (null != cb) {
                    buttonName = cb.getName();
                    buttonCustomId = cb.getCustomId();
                }
            } else {
                SendButton sb = sendButtonService.getById(bib.getButtonId());
                if (null != sb) {
                    buttonName = sb.getName();
                    buttonCustomId = sb.getCustomId();
                }
            }
            bib.setButtonName(buttonName);
            bib.setButtonCustomId(buttonCustomId);
        }
        return bibList;
    }

    @Override
    @Transactional
    public void removeButtonItemBinds(String[] buttonItemBindIds) {
        for (String buttonItemBindId : buttonItemBindIds) {
            itemButtonRoleService.deleteByItemButtonId(buttonItemBindId);
            buttonItemBindRepository.deleteById(buttonItemBindId);
        }
    }

    @Override
    @Transactional
    public ItemButtonBind save(ItemButtonBind buttonItemBind) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId(), userName = person.getName(), tenantId = Y9LoginUserHolder.getTenantId();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String id = buttonItemBind.getId();
        ItemButtonBind oldbib = this.getById(id);
        if (null != oldbib) {
            oldbib.setTenantId(tenantId);
            oldbib.setUpdateTime(sdf.format(new Date()));
            oldbib.setUserId(userId);
            oldbib.setUserName(userName);

            String buttonName = "按钮不存在";
            String buttonCustomId = "";
            if (Objects.equals(ItemButtonTypeEnum.COMMON, oldbib.getButtonType())) {
                CommonButton cb = commonButtonService.getById(oldbib.getButtonId());
                if (null != cb) {
                    buttonName = cb.getName();
                    buttonCustomId = cb.getCustomId();
                }
            } else {
                SendButton sb = sendButtonService.getById(oldbib.getButtonId());
                if (null != sb) {
                    buttonName = sb.getName();
                    buttonCustomId = sb.getCustomId();
                }
            }
            oldbib.setButtonName(buttonName);
            oldbib.setButtonCustomId(buttonCustomId);

            buttonItemBindRepository.save(oldbib);
            return oldbib;
        } else {
            return buttonItemBindRepository.save(buttonItemBind);
        }
    }

    @Override
    @Transactional
    public void saveOrder(String[] idAndTabIndexs) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId(), userName = person.getName();
        List<ItemButtonBind> oldtibList = new ArrayList<>();
        for (String idAndTabIndex : idAndTabIndexs) {
            String[] arr = idAndTabIndex.split(SysVariables.COLON);
            ItemButtonBind oldBib = this.getById(arr[0]);
            oldBib.setTabIndex(Integer.valueOf(arr[1]));
            oldBib.setUpdateTime(sdf.format(new Date()));
            oldBib.setUserId(userId);
            oldBib.setUserName(userName);

            oldtibList.add(oldBib);
        }
        buttonItemBindRepository.saveAll(oldtibList);
    }
}
