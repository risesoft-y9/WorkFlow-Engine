package net.risesoft.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.resource.AppApi;
import net.risesoft.api.platform.resource.SystemApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.entity.Item;
import net.risesoft.entity.ItemMappingConf;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.model.platform.App;
import net.risesoft.model.platform.System;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.ItemMappingConfRepository;
import net.risesoft.repository.jpa.ItemRepository;
import net.risesoft.service.PrintTemplateService;
import net.risesoft.service.SpmApproveItemService;
import net.risesoft.service.config.ItemButtonBindService;
import net.risesoft.service.config.ItemInterfaceBindService;
import net.risesoft.service.config.ItemOpinionFrameBindService;
import net.risesoft.service.config.ItemOrganWordBindService;
import net.risesoft.service.config.ItemPermissionService;
import net.risesoft.service.config.ItemStartNodeRoleService;
import net.risesoft.service.config.ItemTaskConfService;
import net.risesoft.service.config.ItemViewConfService;
import net.risesoft.service.config.ItemWordConfService;
import net.risesoft.service.config.ItemWordTemplateBindService;
import net.risesoft.service.config.RelatedProcessService;
import net.risesoft.service.config.TaskTimeConfService;
import net.risesoft.service.config.Y9FormItemBindService;
import net.risesoft.service.config.Y9PreFormItemBindService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class SpmApproveItemServiceImpl implements SpmApproveItemService {

    private final ItemRepository itemRepository;
    private final SystemApi systemApi;
    private final AppApi appApi;
    private final ItemMappingConfRepository itemMappingConfRepository;
    private final RepositoryApi repositoryApi;
    private final ProcessDefinitionApi processDefinitionApi;
    private final Y9FormItemBindService y9FormItemBindService;
    private final ItemPermissionService itemPermissionService;
    private final RelatedProcessService relatedProcessService;
    private final ItemInterfaceBindService itemInterfaceBindService;
    private final ItemOpinionFrameBindService itemOpinionFrameBindService;
    private final Y9PreFormItemBindService y9PreFormItemBindService;
    private final ItemOrganWordBindService itemOrganWordBindService;
    private final ItemWordTemplateBindService itemWordTemplateBindService;
    private final PrintTemplateService printTemplateService;
    private final ItemTaskConfService itemTaskConfService;
    private final ItemStartNodeRoleService itemStartNodeRoleService;
    private final ItemButtonBindService itemButtonBindService;
    private final ItemViewConfService itemViewConfService;
    private final TaskTimeConfService taskTimeConfService;
    private final ItemWordConfService itemWordConfService;

    @Override
    @Transactional
    public Y9Result<String> copyAllBind(String itemId, String processDefinitionId) {
        try {
            // 复制表单绑定信息
            y9FormItemBindService.copyEform(itemId, processDefinitionId);
            // 复制权限信息
            itemPermissionService.copyPerm(itemId, processDefinitionId);
            // 复制意见框绑定信息
            itemOpinionFrameBindService.copyBind(itemId, processDefinitionId);
            // 复制编号绑定信息
            itemOrganWordBindService.copyBind(itemId, processDefinitionId);
            // 复制正文模板绑定信息
            itemWordTemplateBindService.copyBind(itemId, processDefinitionId);
            // 复制签收配置绑定信息
            itemTaskConfService.copyTaskConf(itemId, processDefinitionId);
            // 复制路由配置
            itemStartNodeRoleService.copyBind(itemId, processDefinitionId);
            // 复制按钮配置的绑定信息
            itemButtonBindService.copyBind(itemId, processDefinitionId);
            // 复制任务时间配置
            taskTimeConfService.copyTaskConf(itemId, processDefinitionId);
            // 复制正文组件权限配置
            itemWordConfService.copyWordConf(itemId, processDefinitionId);
            return Y9Result.successMsg("复制成功");
        } catch (Exception e) {
            LOGGER.error("复制事项异常", e);
            return Y9Result.failure("复制事项异常");
        }
    }

    @Override
    @Transactional
    public Y9Result<String> copyItem(String id) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            Item item = this.findById(id);
            if (null != item) {
                // 复制事项信息
                Item newItem = new Item();
                Y9BeanUtil.copyProperties(item, newItem);
                String newItemId = Y9IdGenerator.genId(IdType.SNOWFLAKE);
                newItem.setId(newItemId);
                newItem.setCreateDate(new Date());
                Integer tabIndex = itemRepository.getMaxTabIndex();
                newItem.setName(item.getName() + "副本");
                newItem.setTabIndex(null != tabIndex ? tabIndex + 1 : 1);
                itemRepository.save(newItem);
                String proDefKey = item.getWorkflowGuid();
                ProcessDefinitionModel latestpd =
                    repositoryApi.getLatestProcessDefinitionByKey(tenantId, proDefKey).getData();
                String latestpdId = latestpd.getId();
                @SuppressWarnings("unused")
                List<TargetModel> nodes = processDefinitionApi.getNodes(tenantId, latestpdId).getData();
                // 复制表单绑定信息
                y9FormItemBindService.copyBindInfo(id, newItemId, latestpdId);

                // 复制意见框绑定信息
                itemOpinionFrameBindService.copyBindInfo(id, newItemId, latestpdId);

                // 复制前置表单绑定信息
                y9PreFormItemBindService.copyBindInfo(id, newItemId);

                // 复制编号绑定信息
                itemOrganWordBindService.copyBindInfo(id, newItemId, latestpdId);

                // 复制关联事项绑定信息
                relatedProcessService.copyBindInfo(id, newItemId);

                // 复制接口配置信息
                itemInterfaceBindService.copyBindInfo(id, newItemId);

                // 复制正文模板绑定信息
                itemWordTemplateBindService.copyBindInfo(id, newItemId, latestpdId);

                // 复制打印模板绑定信息
                printTemplateService.copyBindInfo(id, newItemId);

                // 复制签收配置绑定信息
                itemTaskConfService.copyBindInfo(id, newItemId, latestpdId);

                // 复制任务时间配置
                taskTimeConfService.copyBindInfo(id, newItemId, latestpdId);

                // 复制按钮配置的绑定信息
                itemButtonBindService.copyBindInfo(id, newItemId, latestpdId);

                // 复制视图配置绑定信息
                itemViewConfService.copyBindInfo(id, newItemId);
                return Y9Result.successMsg("复制成功");
            }
            return Y9Result.failure("复制事项异常,事项不存在");
        } catch (Exception e) {
            LOGGER.error("复制事项异常", e);
            return Y9Result.failure("复制事项异常");
        }
    }

    @Override
    @Transactional
    public Y9Result<String> delete(String ids) {
        try {
            if (StringUtils.isNotBlank(ids)) {
                String[] id = ids.split(SysVariables.COMMA);
                for (String s : id) {
                    itemRepository.deleteById(s);
                    // 删除表单绑定信息
                    y9FormItemBindService.deleteBindInfo(s);
                    // 删除权限绑定信息
                    itemPermissionService.deleteBindInfo(s);
                    // 删除意见框绑定信息
                    itemOpinionFrameBindService.deleteBindInfo(s);
                    // 删除前置表单绑定信息
                    y9PreFormItemBindService.deleteBindInfo(s);
                    // 删除编号绑定信息
                    itemOrganWordBindService.deleteBindInfo(s);
                    // 删除关联事项绑定信息
                    relatedProcessService.deleteBindInfo(s);
                    // 删除接口配置信息
                    itemInterfaceBindService.deleteBindInfo(s);
                    // 删除正文模板绑定信息
                    itemWordTemplateBindService.deleteBindInfo(s);
                    // 删除打印模板绑定信息
                    printTemplateService.deleteBindInfo(s);
                    // 删除签收配置绑定信息
                    itemTaskConfService.deleteBindInfo(s);
                    // 删除任务时间配置
                    taskTimeConfService.deleteBindInfo(s);
                    // 删除路由配置的绑定信息
                    itemStartNodeRoleService.deleteBindInfo(s);
                    // 删除按钮配置的绑定信息
                    itemButtonBindService.deleteBindInfo(s);
                    // 删除视图配置绑定信息
                    itemViewConfService.deleteBindInfo(s);
                }
            }
            return Y9Result.successMsg("删除成功");
        } catch (Exception e) {
            LOGGER.error("删除事项异常", e);
            return Y9Result.failure("删除失败");
        }
    }

    @Override
    public Item findById(String id) {
        return itemRepository.findById(id).orElse(null);
    }

    @Override
    public Map<String, Object> findById(String itemId, Map<String, Object> map) {
        Item spmApproveitem = itemRepository.findById(itemId).orElse(null);
        if (spmApproveitem != null) {
            map.put("processDefinitionKey", spmApproveitem.getWorkflowGuid());
            map.put("itemId", spmApproveitem.getId());
            map.put("type", spmApproveitem.getType() == null ? "" : spmApproveitem.getType());
        }
        return map;
    }

    @Override
    public ItemModel findByProcessDefinitionKey(String tenantId, String processDefinitionKey) {
        ItemModel itemModel = new ItemModel();
        Item sa = itemRepository.findItemByKey(processDefinitionKey);
        if (null == sa) {
            return null;
        }
        Y9BeanUtil.copyProperties(sa, itemModel);
        return itemModel;
    }

    @Override
    public Boolean hasProcessDefinitionByKey(String processDefinitionKey) {
        boolean hasKey = false;
        try {
            Item sa = itemRepository.findItemByKey(processDefinitionKey);
            if (null != sa) {
                hasKey = true;
            }
        } catch (Exception e) {
            LOGGER.error("判断流程定义Key是否存在异常", e);
        }
        return hasKey;
    }

    @Override
    public List<Item> list() {
        return itemRepository.findAll();
    }

    @Override
    public List<Item> listByIdNotAndNameLike(String id, String name) {
        return itemRepository.findByIdNotAndNameLike(id, "%" + name + "%");
    }

    @Override
    public List<Item> listBySystemName(String systemName) {
        return itemRepository.findAll(systemName);
    }

    @Override
    public Page<Item> page(Integer page, Integer rows) {
        PageRequest pageable = PageRequest.of(page - 1, rows, Sort.by(Sort.Direction.DESC, "createDate"));
        return itemRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public Y9Result<String> publishToSystemApp(String itemId) {
        try {
            Item item = this.findById(itemId);
            System system = systemApi.getByName(Y9Context.getSystemName()).getData();
            if (null == system) {
                return Y9Result.failure("发布为系统[" + Y9Context.getSystemName() + "]的应用失败:没有找到英文名为["
                    + Y9Context.getSystemName() + "]的系统,请先创建系统后再发布");
            }
            /**
             * 1、判断应用是否存在，不存在则创建应用，存在则修改应用
             */
            String systemId = system.getId();
            App app = appApi.findBySystemIdAndCustomId(systemId, itemId).getData();
            if (null == app) {
                app = new App();
                app.setName(item.getName());
                app.setUrl(item.getAppUrl());
                app.setCustomId(itemId);
                app.setEnabled(Boolean.TRUE);
                app.setSystemId(systemId);
                appApi.saveIsvApp(app);

                return Y9Result.successMsg("发布为系统[" + Y9Context.getSystemName() + "]的新应用成功，请联系运维人员进行应用审核");
            } else {
                app.setName(item.getName());
                app.setUrl(item.getAppUrl());
                app.setSystemId(systemId);
                appApi.saveIsvApp(app);
                return Y9Result.successMsg("更新系统[" + Y9Context.getSystemName() + "]的应用成功，请联系运维人员进行应用审核");
            }
        } catch (Exception e) {
            LOGGER.error("发布为系统应用异常", e);
            return Y9Result.failure("发布为系统应用异常");
        }
    }

    @Override
    @Transactional
    public Y9Result<Item> save(Item item) {
        try {
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            item.setCreateDate(new Date());
            item.setCreaterId(person.getPersonId());
            item.setCreaterName(person.getName());
            if (StringUtils.isNotEmpty(item.getAppUrl())) {
                item.setTodoTaskUrlPrefix(item.getAppUrl().split("\\?")[0]);
            }
            Item olditem = itemRepository.findById(item.getId()).orElse(null);
            if (olditem == null) {
                Integer tabIndex = itemRepository.getMaxTabIndex();
                if (tabIndex == null) {
                    item.setTabIndex(1);
                } else {
                    item.setTabIndex(tabIndex + 1);
                }
            }
            itemRepository.save(item);
            ItemMappingConf itemMappingConf =
                itemMappingConfRepository.findTopByItemIdAndSysTypeOrderByCreateTimeDesc(item.getId(), "1");
            // 删除事项映射字段
            if (itemMappingConf != null) {
                if (StringUtils.isBlank(item.getDockingItemId())
                    || !item.getDockingItemId().equals(itemMappingConf.getMappingId())) {
                    itemMappingConfRepository.deleteByMappingId(itemMappingConf.getMappingId());
                }
            }
            ItemMappingConf itemMappingConf1 =
                itemMappingConfRepository.findTopByItemIdAndSysTypeOrderByCreateTimeDesc(item.getId(), "2");
            // 删除系统映射字段
            if (itemMappingConf1 != null) {
                if (StringUtils.isBlank(item.getDockingSystem())
                    || !item.getDockingSystem().equals(itemMappingConf1.getMappingId())) {
                    itemMappingConfRepository.deleteByMappingId(itemMappingConf1.getMappingId());
                }
            }
            return Y9Result.success(item, "保存成功");
        } catch (Exception e) {
            LOGGER.error("保存事项异常", e);
            return Y9Result.failure("保存事项异常");
        }
    }

    @Override
    @Transactional
    public void updateOrder(String[] idAndTabIndexs) {
        List<String> list = Lists.newArrayList(idAndTabIndexs);
        try {
            for (String s : list) {
                String[] arr = s.split(SysVariables.COLON);
                itemRepository.updateOrder(Integer.parseInt(arr[1]), arr[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
