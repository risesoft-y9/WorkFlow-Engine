package net.risesoft.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.api.resource.AppApi;
import net.risesoft.api.resource.SystemApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.ItemMappingConf;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.model.App;
import net.risesoft.model.System;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.jpa.ItemMappingConfRepository;
import net.risesoft.repository.jpa.SpmApproveItemRepository;
import net.risesoft.service.SpmApproveItemService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Service(value = "spmApproveItemService")
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class SpmApproveItemServiceImpl implements SpmApproveItemService {

    @Autowired
    private SpmApproveItemRepository spmApproveItemRepository;

    @Autowired
    private SystemApi systemEntityManager;

    @Autowired
    private AppApi appApi;

    @Autowired
    private ItemMappingConfRepository itemMappingConfRepository;

    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> delete(String ids) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, true);
        map.put("msg", "删除成功");
        try {
            if (StringUtils.isNotBlank(ids)) {
                String[] id = ids.split(SysVariables.COMMA);
                for (int i = 0; i < id.length; i++) {
                    spmApproveItemRepository.deleteById(id[i]);
                }
            }
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "删除失败");
            e.printStackTrace();
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
            map.put("basicformId", spmApproveitem.getBasicformId() == null ? "" : spmApproveitem.getBasicformId());
            map.put("handelformId", spmApproveitem.getHandelformId() == null ? "" : spmApproveitem.getHandelformId());
            map.put("processDefinitionKey", spmApproveitem.getWorkflowGuid());
            map.put("itemId", spmApproveitem.getId());
            map.put("type", spmApproveitem.getType() == null ? "" : spmApproveitem.getType());
        }
        return map;
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
        Boolean hasKey = false;
        try {
            SpmApproveItem sa = spmApproveItemRepository.findItemByKey(processDefinitionKey);
            if (null != sa) {
                hasKey = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            hasKey = false;
        }
        return hasKey;
    }

    @Override
    public Map<String, Object> list() {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            List<SpmApproveItem> itemList = spmApproveItemRepository.findAll();
            map.put("rows", itemList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public Map<String, Object> list(Integer page, Integer rows) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            PageRequest pageable = PageRequest.of(page - 1, rows, Sort.by(Sort.Direction.DESC, "createDate"));
            Page<SpmApproveItem> itemPage = spmApproveItemRepository.findAll(pageable);
            map.put("rows", itemPage.getContent());
            map.put("currpage", page);
            map.put("totalpages", itemPage.getTotalPages());
            map.put("total", itemPage.getTotalElements());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> publishToSystemApp(String itemId) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, false);
        try {
            SpmApproveItem item = this.findById(itemId);
            map.put("msg", "发布应用失败");
            System system = systemEntityManager.getByName(Y9Context.getSystemName());
            if (null == system) {
                map.put("msg", "发布为系统[" + Y9Context.getSystemName() + "]的应用失败:没有找到英文名为[" + Y9Context.getSystemName()
                    + "]的系统,请先创建系统后再发布");
                return map;
            }
            /**
             * 1、判断应用是否存在，不存在则创建应用，存在则修改应用
             */
            String systemId = system.getId(), customId = itemId;
            App app = appApi.findBySystemIdAndCustomId(systemId, customId);
            if (null == app) {
                app = new App();
                app.setName(item.getName());
                app.setUrl(item.getAppUrl());
                app.setCustomId(customId);
                app.setEnabled(Boolean.TRUE);
                // FIXME
                appApi.saveIsvApp(app, systemId);

                map.put("msg", "发布为系统[" + Y9Context.getSystemName() + "]的新应用成功，请联系运维人员进行应用审核");
            } else {
                app.setName(item.getName());
                app.setUrl(item.getAppUrl());
                // FIXME
                appApi.saveIsvApp(app, systemId);
                map.put("msg", "更新系统[" + Y9Context.getSystemName() + "]的应用成功，请联系运维人员进行应用审核");
            }
            map.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            map.put("msg", "发布为系统应用异常");
            e.printStackTrace();
        }
        return map;
    }

    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> save(SpmApproveItem item) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, false);
        map.put("msg", "保存失败");
        try {
            UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
            item.setCreateDate(new Date());
            item.setCreaterId(userInfo.getPersonId());
            item.setCreaterName(userInfo.getName());
            if (StringUtils.isNotEmpty(item.getAppUrl())) {
                item.setTodoTaskUrlPrefix(item.getAppUrl().split("\\?")[0]);
            }
            spmApproveItemRepository.save(item);
            ItemMappingConf itemMappingConf =
                itemMappingConfRepository.findTopByItemIdAndSysTypeOrderByCreateTimeDesc(item.getId(), "1");
            if (itemMappingConf != null) {
                if (StringUtils.isBlank(item.getDockingItemId())
                    || !item.getDockingItemId().equals(itemMappingConf.getMappingId())) {
                    itemMappingConfRepository.deleteByMappingId(itemMappingConf.getMappingId());
                }
            }
            ItemMappingConf itemMappingConf1 =
                itemMappingConfRepository.findTopByItemIdAndSysTypeOrderByCreateTimeDesc(item.getId(), "2");
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

            e.printStackTrace();
        }
        return map;
    }
}
