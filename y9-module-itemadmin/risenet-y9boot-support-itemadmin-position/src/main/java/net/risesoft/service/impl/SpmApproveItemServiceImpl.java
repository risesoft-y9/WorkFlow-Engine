package net.risesoft.service.impl;

import java.util.Date;
import java.util.HashMap;
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
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.ItemMappingConf;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.model.platform.App;
import net.risesoft.model.platform.System;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.jpa.ItemMappingConfRepository;
import net.risesoft.repository.jpa.SpmApproveItemRepository;
import net.risesoft.service.ItemButtonBindService;
import net.risesoft.service.ItemInterfaceBindService;
import net.risesoft.service.ItemLinkBindService;
import net.risesoft.service.ItemOpinionFrameBindService;
import net.risesoft.service.ItemOrganWordBindService;
import net.risesoft.service.ItemTaskConfService;
import net.risesoft.service.ItemViewConfService;
import net.risesoft.service.ItemWordTemplateBindService;
import net.risesoft.service.PrintTemplateService;
import net.risesoft.service.RelatedProcessService;
import net.risesoft.service.SpmApproveItemService;
import net.risesoft.service.Y9FormItemBindService;
import net.risesoft.service.Y9PreFormItemBindService;
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

    private final SpmApproveItemRepository spmApproveItemRepository;
    private final SystemApi systemEntityManager;
    private final AppApi appManager;
    private final ItemMappingConfRepository itemMappingConfRepository;
    private final RepositoryApi repositoryApi;
    private final ProcessDefinitionApi processDefinitionApi;
    private final Y9FormItemBindService y9FormItemBindService;
    private final RelatedProcessService relatedProcessService;
    private final ItemLinkBindService itemLinkBindService;
    private final ItemInterfaceBindService itemInterfaceBindService;
    private final ItemOpinionFrameBindService itemOpinionFrameBindService;
    private final Y9PreFormItemBindService y9PreFormItemBindService;
    private final ItemOrganWordBindService itemOrganWordBindService;
    private final ItemWordTemplateBindService itemWordTemplateBindService;
    private final PrintTemplateService printTemplateService;
    private final ItemTaskConfService itemTaskConfService;
    private final ItemButtonBindService itemButtonBindService;
    private final ItemViewConfService itemViewConfService;

    @Override
    @Transactional
    public Map<String, Object> copyItem(String id) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("success", false);
        map.put("msg", "复制失败");
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            SpmApproveItem item = this.findById(id);
            if (null != item) {
                // 复制事项信息
                SpmApproveItem newItem = new SpmApproveItem();
                Y9BeanUtil.copyProperties(item, newItem);
                String newItemId = Y9IdGenerator.genId(IdType.SNOWFLAKE);
                newItem.setId(newItemId);
                newItem.setCreateDate(new Date());
                Integer tabIndex = spmApproveItemRepository.getMaxTabIndex();
                newItem.setName(item.getName() + "副本");
                newItem.setTabIndex(null != tabIndex ? tabIndex + 1 : 1);
                spmApproveItemRepository.save(newItem);
                String proDefKey = item.getWorkflowGuid();
                ProcessDefinitionModel latestpd =
                    repositoryApi.getLatestProcessDefinitionByKey(tenantId, proDefKey).getData();
                String latestpdId = latestpd.getId();
                List<TargetModel> nodes = processDefinitionApi.getNodes(tenantId, latestpdId, false).getData();
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

                // 复制链接配置信息
                itemLinkBindService.copyBindInfo(id, newItemId);

                // 复制接口配置信息
                itemInterfaceBindService.copyBindInfo(id, newItemId);

                // 复制正文模板绑定信息
                itemWordTemplateBindService.copyBindInfo(id, newItemId, latestpdId);

                // 复制打印模板绑定信息
                printTemplateService.copyBindInfo(id, newItemId);

                // 复制签收配置绑定信息
                itemTaskConfService.copyBindInfo(id, newItemId, latestpdId);

                // 复制按钮配置的绑定信息
                itemButtonBindService.copyBindInfo(id, newItemId, latestpdId);

                // 复制视图配置绑定信息
                itemViewConfService.copyBindInfo(id, newItemId);
                map.put("success", true);
                map.put("msg", "复制成功");
            }
        } catch (Exception e) {
            LOGGER.error("复制事项异常", e);
        }
        return map;
    }

    @Override
    @Transactional
    public Map<String, Object> delete(String ids) {
        Map<String, Object> map = new HashMap<>(16);
        map.put(UtilConsts.SUCCESS, true);
        map.put("msg", "删除成功");
        try {
            if (StringUtils.isNotBlank(ids)) {
                String[] id = ids.split(SysVariables.COMMA);
                for (String s : id) {
                    spmApproveItemRepository.deleteById(s);
                }
            }
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "删除失败");
            LOGGER.error("删除事项异常", e);
        }
        return map;
    }

    @Override
    public SpmApproveItem findById(String id) {
        return spmApproveItemRepository.findById(id).orElse(null);
    }

    @Override
    public Map<String, Object> findById(String itemId, Map<String, Object> map) {
        SpmApproveItem spmApproveitem = spmApproveItemRepository.findById(itemId).orElse(null);
        if (spmApproveitem != null) {
            map.put("processDefinitionKey", spmApproveitem.getWorkflowGuid());
            map.put("itemId", spmApproveitem.getId());
            map.put("type", spmApproveitem.getType() == null ? "" : spmApproveitem.getType());
        }
        return map;
    }

    @Override
    public List<SpmApproveItem> findByIdNotAndNameLike(String id, String name) {
        return spmApproveItemRepository.findByIdNotAndNameLike(id, "%" + name + "%");
    }

    @Override
    public ItemModel findByProcessDefinitionKey(String tenantId, String processDefinitionKey) {
        ItemModel itemModel = new ItemModel();
        SpmApproveItem sa = spmApproveItemRepository.findItemByKey(processDefinitionKey);
        if (null == sa) {
            return null;
        }
        Y9BeanUtil.copyProperties(sa, itemModel);
        return itemModel;
    }

    @Override
    public List<SpmApproveItem> findBySystemName(String systemName) {
        return spmApproveItemRepository.findAll(systemName);
    }

    @Override
    public Boolean hasProcessDefinitionByKey(String processDefinitionKey) {
        boolean hasKey = false;
        try {
            SpmApproveItem sa = spmApproveItemRepository.findItemByKey(processDefinitionKey);
            if (null != sa) {
                hasKey = true;
            }
        } catch (Exception e) {
            LOGGER.error("判断流程定义Key是否存在异常", e);
        }
        return hasKey;
    }

    @Override
    public Map<String, Object> list() {
        Map<String, Object> map = new HashMap<>(16);
        List<SpmApproveItem> itemList = spmApproveItemRepository.findAll();
        map.put("rows", itemList);
        return map;
    }

    @Override
    public Map<String, Object> list(Integer page, Integer rows) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            PageRequest pageable = PageRequest.of(page - 1, rows, Sort.by(Sort.Direction.DESC, "createDate"));
            Page<SpmApproveItem> itemPage = spmApproveItemRepository.findAll(pageable);
            map.put("rows", itemPage.getContent());
            map.put("currpage", page);
            map.put("totalpages", itemPage.getTotalPages());
            map.put("total", itemPage.getTotalElements());
        } catch (Exception e) {
            LOGGER.error("获取事项列表异常", e);
        }
        return map;
    }

    @Override
    @Transactional
    public Map<String, Object> publishToSystemApp(String itemId) {
        Map<String, Object> map = new HashMap<>(16);
        map.put(UtilConsts.SUCCESS, false);
        try {
            SpmApproveItem item = this.findById(itemId);
            map.put("msg", "发布应用失败");
            System system = systemEntityManager.getByName(Y9Context.getSystemName()).getData();
            if (null == system) {
                map.put("msg", "发布为系统[" + Y9Context.getSystemName() + "]的应用失败:没有找到英文名为[" + Y9Context.getSystemName()
                    + "]的系统,请先创建系统后再发布");
                return map;
            }
            /**
             * 1、判断应用是否存在，不存在则创建应用，存在则修改应用
             */
            String systemId = system.getId();
            App app = appManager.findBySystemIdAndCustomId(systemId, itemId).getData();
            if (null == app) {
                app = new App();
                app.setName(item.getName());
                app.setUrl(item.getAppUrl());
                app.setCustomId(itemId);
                app.setEnabled(Boolean.TRUE);
                app.setSystemId(systemId);
                // FIXME
                appManager.saveIsvApp(app);

                map.put("msg", "发布为系统[" + Y9Context.getSystemName() + "]的新应用成功，请联系运维人员进行应用审核");
            } else {
                app.setName(item.getName());
                app.setUrl(item.getAppUrl());
                app.setSystemId(systemId);
                // FIXME
                appManager.saveIsvApp(app);
                map.put("msg", "更新系统[" + Y9Context.getSystemName() + "]的应用成功，请联系运维人员进行应用审核");
            }
            map.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            map.put("msg", "发布为系统应用异常");
            LOGGER.error("发布为系统应用异常", e);
        }
        return map;
    }

    @Override
    @Transactional
    public Map<String, Object> save(SpmApproveItem item) {
        Map<String, Object> map = new HashMap<>(16);
        map.put(UtilConsts.SUCCESS, false);
        map.put("msg", "保存失败");
        try {
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            item.setCreateDate(new Date());
            item.setCreaterId(person.getPersonId());
            item.setCreaterName(person.getName());
            if (StringUtils.isNotEmpty(item.getAppUrl())) {
                item.setTodoTaskUrlPrefix(item.getAppUrl().split("\\?")[0]);
            }
            Integer tabIndex = spmApproveItemRepository.getMaxTabIndex();
            if (tabIndex == null) {
                item.setTabIndex(1);
            } else {
                item.setTabIndex(tabIndex + 1);
            }
            spmApproveItemRepository.save(item);
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
            map.put("item", item);
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "保存成功");
        } catch (Exception e) {
            LOGGER.error("保存事项异常", e);
        }
        return map;
    }

    @Override
    @Transactional
    public void updateOrder(String[] idAndTabIndexs) {
        List<String> list = Lists.newArrayList(idAndTabIndexs);
        try {
            for (String s : list) {
                String[] arr = s.split(SysVariables.COLON);
                spmApproveItemRepository.updateOrder(Integer.parseInt(arr[1]), arr[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
