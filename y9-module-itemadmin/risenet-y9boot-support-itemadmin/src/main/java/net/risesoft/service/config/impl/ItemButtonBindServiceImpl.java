package net.risesoft.service.config.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.Getter;
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

    private static final String BUTTON_NAME_KEY = "按钮不存在";
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
        UserInfo currentUser = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = currentUser.getPersonId();
        String userName = currentUser.getName();
        // 创建按钮绑定对象
        ItemButtonBind itemButtonBind = createButtonItemBind(buttonId, buttonType, itemId, processDefinitionId,
            taskDefKey, tenantId, userId, userName);
        // 设置标签索引
        setTabIndex(itemButtonBind, itemId, processDefinitionId, taskDefKey, buttonType);
        // 保存绑定信息
        buttonItemBindRepository.save(itemButtonBind);
    }

    /**
     * 创建按钮绑定对象
     */
    private ItemButtonBind createButtonItemBind(String buttonId, ItemButtonTypeEnum buttonType, String itemId,
        String processDefinitionId, String taskDefKey, String tenantId, String userId, String userName) {
        ItemButtonBind itemButtonBind = new ItemButtonBind();
        itemButtonBind.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        itemButtonBind.setButtonId(buttonId);
        itemButtonBind.setButtonType(buttonType);
        itemButtonBind.setItemId(itemId);
        itemButtonBind.setProcessDefinitionId(processDefinitionId);
        itemButtonBind.setTaskDefKey(taskDefKey);
        itemButtonBind.setTenantId(tenantId);
        itemButtonBind.setUserId(userId);
        itemButtonBind.setUserName(userName);
        return itemButtonBind;
    }

    /**
     * 设置按钮绑定的标签索引
     */
    private void setTabIndex(ItemButtonBind itemButtonBind, String itemId, String processDefinitionId,
        String taskDefKey, ItemButtonTypeEnum buttonType) {
        Integer maxTabIndex =
            buttonItemBindRepository.getMaxTabIndex(itemId, processDefinitionId, taskDefKey, buttonType);
        itemButtonBind.setTabIndex(maxTabIndex == null ? 1 : maxTabIndex + 1);
    }

    @Override
    @Transactional
    public void copyBind(String itemId, String processDefinitionId) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = person.getPersonId();
        String userName = person.getName();
        Item item = itemRepository.findById(itemId).orElse(null);
        assert item != null;
        // 获取最新和前一版本的流程定义
        ProcessDefinitionModel latestProcessDefinition = getLatestProcessDefinition(tenantId, item);
        String latestProcessDefinitionId = latestProcessDefinition.getId();
        String previousProcessDefinitionId =
            getPreviousProcessDefinitionId(tenantId, processDefinitionId, latestProcessDefinition);
        // 获取流程节点并复制绑定信息
        List<TargetModel> nodes = processDefinitionApi.getNodes(tenantId, latestProcessDefinitionId).getData();
        for (TargetModel targetModel : nodes) {
            String currentTaskDefKey = targetModel.getTaskDefKey();
            copyButtonBindingsForNode(itemId, tenantId, userId, userName, latestProcessDefinitionId,
                previousProcessDefinitionId, currentTaskDefKey);
        }
    }

    /**
     * 获取最新流程定义
     */
    private ProcessDefinitionModel getLatestProcessDefinition(String tenantId, Item item) {
        String processDefinitionKey = item.getWorkflowGuid();
        return repositoryApi.getLatestProcessDefinitionByKey(tenantId, processDefinitionKey).getData();
    }

    /**
     * 获取前一版本流程定义ID
     */
    private String getPreviousProcessDefinitionId(String tenantId, String processDefinitionId,
        ProcessDefinitionModel latestProcessDefinition) {
        String previousProcessDefinitionId = processDefinitionId;
        String latestProcessDefinitionId = latestProcessDefinition.getId();

        if (processDefinitionId.equals(latestProcessDefinitionId) && latestProcessDefinition.getVersion() > 1) {
            ProcessDefinitionModel previousProcessDefinition =
                repositoryApi.getPreviousProcessDefinitionById(tenantId, latestProcessDefinitionId).getData();
            previousProcessDefinitionId = previousProcessDefinition.getId();
        }
        return previousProcessDefinitionId;
    }

    /**
     * 为指定节点复制按钮绑定信息
     */
    private void copyButtonBindingsForNode(String itemId, String tenantId, String userId, String userName,
        String latestProcessDefinitionId, String previousProcessDefinitionId, String currentTaskDefKey) {
        List<ItemButtonBind> bindList = getButtonBindings(itemId, previousProcessDefinitionId, currentTaskDefKey);
        for (ItemButtonBind bind : bindList) {
            ItemButtonBind existingBind =
                getExistingButtonBinding(itemId, latestProcessDefinitionId, currentTaskDefKey, bind.getButtonId());
            // 如果不存在，则创建新的绑定
            if (null == existingBind) {
                createNewButtonBinding(itemId, tenantId, userId, userName, latestProcessDefinitionId, currentTaskDefKey,
                    bind);
            }
        }
    }

    /**
     * 获取指定节点的按钮绑定列表
     */
    private List<ItemButtonBind> getButtonBindings(String itemId, String processDefinitionId, String taskDefKey) {
        if (StringUtils.isBlank(taskDefKey)) {
            return buttonItemBindRepository
                .findByItemIdAndProcessDefinitionIdAndTaskDefKeyIsNullOrderByTabIndexAsc(itemId, processDefinitionId);
        } else {
            return buttonItemBindRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyOrderByTabIndexAsc(itemId,
                processDefinitionId, taskDefKey);
        }
    }

    /**
     * 获取已存在的按钮绑定
     */
    private ItemButtonBind getExistingButtonBinding(String itemId, String processDefinitionId, String taskDefKey,
        String buttonId) {
        if (StringUtils.isBlank(taskDefKey)) {
            return buttonItemBindRepository
                .findByItemIdAndProcessDefinitionIdAndTaskDefKeyIsNullAndButtonIdOrderByTabIndexAsc(itemId,
                    processDefinitionId, buttonId);
        } else {
            return buttonItemBindRepository
                .findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndButtonIdOrderByTabIndexAsc(itemId,
                    processDefinitionId, taskDefKey, buttonId);
        }
    }

    /**
     * 创建新的按钮绑定
     */
    private void createNewButtonBinding(String itemId, String tenantId, String userId, String userName,
        String processDefinitionId, String taskDefKey, ItemButtonBind sourceBind) {
        String newBindId = Y9IdGenerator.genId(IdType.SNOWFLAKE);
        String sourceBindId = sourceBind.getId();
        // 创建新的按钮绑定
        ItemButtonBind newBind = createButtonItemBind(newBindId, itemId, tenantId, userId, userName,
            processDefinitionId, taskDefKey, sourceBind);
        buttonItemBindRepository.save(newBind);
        // 复制按钮授权信息
        copyButtonRoles(sourceBindId, newBindId);
    }

    /**
     * 创建按钮绑定对象
     */
    private ItemButtonBind createButtonItemBind(String bindId, String itemId, String tenantId, String userId,
        String userName, String processDefinitionId, String taskDefKey, ItemButtonBind sourceBind) {
        ItemButtonBind newBind = new ItemButtonBind();
        newBind.setId(bindId);
        newBind.setButtonId(sourceBind.getButtonId());
        newBind.setButtonType(sourceBind.getButtonType());
        newBind.setItemId(itemId);
        newBind.setProcessDefinitionId(processDefinitionId);
        newBind.setTaskDefKey(taskDefKey);
        newBind.setTenantId(tenantId);
        newBind.setUserId(userId);
        newBind.setUserName(userName);
        setTabIndex(newBind, itemId, processDefinitionId, taskDefKey, sourceBind.getButtonType());
        return newBind;
    }

    /**
     * 复制按钮角色授权信息
     */
    private void copyButtonRoles(String sourceBindId, String targetBindId) {
        List<ItemButtonRole> roleList = itemButtonRoleService.listByItemButtonId(sourceBindId);
        for (ItemButtonRole role : roleList) {
            itemButtonRoleService.saveOrUpdate(targetBindId, role.getRoleId());
        }
    }

    @Override
    @Transactional
    public void copyBindInfo(String itemId, String newItemId, String lastVersionPid) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = person.getPersonId(), userName = person.getName();
        List<ItemButtonBind> bindList =
            buttonItemBindRepository.findByItemIdAndProcessDefinitionIdOrderByTabIndexAsc(itemId, lastVersionPid);
        for (ItemButtonBind bind : bindList) {
            ItemButtonBind newBind = new ItemButtonBind();
            newBind.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            newBind.setButtonId(bind.getButtonId());
            newBind.setButtonType(bind.getButtonType());
            newBind.setItemId(newItemId);
            newBind.setProcessDefinitionId(lastVersionPid);
            newBind.setTaskDefKey(bind.getTaskDefKey());
            newBind.setTenantId(tenantId);
            newBind.setUserId(userId);
            newBind.setUserName(userName);
            newBind.setTabIndex(bind.getTabIndex());
            buttonItemBindRepository.save(newBind);
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

        List<ItemButtonBind> buttonBindList = buttonItemBindRepository
            .findByItemIdAndButtonTypeAndProcessDefinitionIdOrderByTabIndexAsc(itemId, buttonType, processDefinitionId);

        for (ItemButtonBind buttonBind : buttonBindList) {
            ButtonInfo buttonInfo = getButtonInfo(buttonBind.getButtonId(), buttonType);
            buttonBind.setButtonName(buttonInfo.getName());
            buttonBind.setButtonCustomId(buttonInfo.getCustomId());
        }

        return buttonBindList;
    }

    /**
     * 根据按钮类型获取按钮信息
     */
    private ButtonInfo getButtonInfo(String buttonId, ItemButtonTypeEnum buttonType) {
        String buttonName = BUTTON_NAME_KEY;
        String buttonCustomId = "";

        if (buttonType == ItemButtonTypeEnum.COMMON) {
            CommonButton commonButton = commonButtonService.getById(buttonId);
            if (commonButton != null) {
                buttonName = commonButton.getName();
                buttonCustomId = commonButton.getCustomId();
            }
        } else {
            SendButton sendButton = sendButtonService.getById(buttonId);
            if (sendButton != null) {
                buttonName = sendButton.getName();
                buttonCustomId = sendButton.getCustomId();
            }
        }

        return new ButtonInfo(buttonName, buttonCustomId);
    }

    @Override
    public List<ItemButtonBind> listByItemIdAndButtonTypeAndProcessDefinitionIdAndTaskDefKey(String itemId,
        ItemButtonTypeEnum buttonType, String processDefinitionId, String taskDefKey) {
        List<ItemButtonBind> buttonBindList =
            buttonItemBindRepository.findByItemIdAndButtonTypeAndProcessDefinitionIdAndTaskDefKeyOrderByTabIndexAsc(
                itemId, buttonType, processDefinitionId, taskDefKey);
        for (ItemButtonBind buttonBind : buttonBindList) {
            ButtonInfo buttonInfo = getButtonInfo(buttonBind.getButtonId(), buttonType);
            buttonBind.setButtonName(buttonInfo.getName());
            buttonBind.setButtonCustomId(buttonInfo.getCustomId());
        }

        return buttonBindList;
    }

    @Override
    public List<ItemButtonBind> listContainRole(String itemId, ItemButtonTypeEnum buttonType,
        String processDefinitionId, String taskDefineKey) {

        List<ItemButtonBind> buttonBindList =
            buttonItemBindRepository.findByItemIdAndButtonTypeAndProcessDefinitionIdAndTaskDefKeyOrderByTabIndexAsc(
                itemId, buttonType, processDefinitionId, taskDefineKey);

        for (ItemButtonBind buttonBind : buttonBindList) {
            // 设置按钮信息
            ButtonInfo buttonInfo = getButtonInfo(buttonBind.getButtonId(), buttonType);
            buttonBind.setButtonName(buttonInfo.getName());
            buttonBind.setButtonCustomId(buttonInfo.getCustomId());

            // 设置角色信息
            RoleInfo roleInfo = getRoleInfo(buttonBind.getId());
            buttonBind.setRoleIds(roleInfo.getRoleIds());
            buttonBind.setRoleNames(roleInfo.getRoleNames());
        }

        return buttonBindList;
    }

    /**
     * 获取按钮绑定的角色信息
     */
    private RoleInfo getRoleInfo(String itemButtonBindId) {
        List<ItemButtonRole> roleList = itemButtonRoleService.listByItemButtonIdContainRoleName(itemButtonBindId);
        List<String> roleIds = new ArrayList<>();
        StringBuilder roleNamesBuilder = new StringBuilder();

        for (int i = 0; i < roleList.size(); i++) {
            ItemButtonRole role = roleList.get(i);
            // 存绑定关系id，便于删除
            roleIds.add(role.getId());

            if (i == 0) {
                roleNamesBuilder.append(role.getRoleName());
            } else {
                roleNamesBuilder.append("、").append(role.getRoleName());
            }
        }

        return new RoleInfo(roleIds, roleNamesBuilder.toString());
    }

    @Override
    public List<ItemButtonBind> listContainRoleId(String itemId, ItemButtonTypeEnum buttonType,
        String processDefinitionId, String taskDefineKey) {
        String buttonName = BUTTON_NAME_KEY;
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
        String buttonName = BUTTON_NAME_KEY;
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
        String id = buttonItemBind.getId();
        ItemButtonBind itemButtonBind = this.getById(id);
        if (null != itemButtonBind) {
            itemButtonBind.setTenantId(tenantId);
            itemButtonBind.setUserId(userId);
            itemButtonBind.setUserName(userName);

            String buttonName = BUTTON_NAME_KEY;
            String buttonCustomId = "";
            if (Objects.equals(ItemButtonTypeEnum.COMMON, itemButtonBind.getButtonType())) {
                CommonButton cb = commonButtonService.getById(itemButtonBind.getButtonId());
                if (null != cb) {
                    buttonName = cb.getName();
                    buttonCustomId = cb.getCustomId();
                }
            } else {
                SendButton sb = sendButtonService.getById(itemButtonBind.getButtonId());
                if (null != sb) {
                    buttonName = sb.getName();
                    buttonCustomId = sb.getCustomId();
                }
            }
            itemButtonBind.setButtonName(buttonName);
            itemButtonBind.setButtonCustomId(buttonCustomId);

            buttonItemBindRepository.save(itemButtonBind);
            return itemButtonBind;
        } else {
            return buttonItemBindRepository.save(buttonItemBind);
        }
    }

    @Override
    @Transactional
    public void saveOrder(String[] idAndTabIndexs) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId(), userName = person.getName();
        List<ItemButtonBind> oldtibList = new ArrayList<>();
        for (String idAndTabIndex : idAndTabIndexs) {
            String[] arr = idAndTabIndex.split(SysVariables.COLON);
            ItemButtonBind oldBib = this.getById(arr[0]);
            oldBib.setTabIndex(Integer.valueOf(arr[1]));
            oldBib.setUserId(userId);
            oldBib.setUserName(userName);

            oldtibList.add(oldBib);
        }
        buttonItemBindRepository.saveAll(oldtibList);
    }

    /**
     * 角色信息封装类
     */
    @Getter
    private static class RoleInfo {
        private final List<String> roleIds;
        private final String roleNames;

        public RoleInfo(List<String> roleIds, String roleNames) {
            this.roleIds = roleIds;
            this.roleNames = roleNames;
        }

    }

    /**
     * 按钮信息封装类
     */
    @Getter
    private static class ButtonInfo {
        private final String name;
        private final String customId;

        public ButtonInfo(String name, String customId) {
            this.name = name;
            this.customId = customId;
        }

    }
}
