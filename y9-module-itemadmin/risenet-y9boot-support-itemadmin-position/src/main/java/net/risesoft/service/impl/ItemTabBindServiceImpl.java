package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.entity.ItemTabBind;
import net.risesoft.entity.TabEntity;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.jpa.ItemTabBindRepository;
import net.risesoft.service.ItemTabBindService;
import net.risesoft.service.TabEntityService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class ItemTabBindServiceImpl implements ItemTabBindService {

    private final ItemTabBindRepository tabItemBindRepository;

    private final TabEntityService tabEntityService;

    private final RepositoryApi repositoryManager;

    @Override
    @Transactional
    public void copyTabItemBind(String itemId, String processDefinitionId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), personId = person.getPersonId(),
            personName = person.getName();
        ProcessDefinitionModel currentPd = repositoryManager.getProcessDefinitionById(tenantId, processDefinitionId).getData();
        if (currentPd.getVersion() > 1) {
            ProcessDefinitionModel previouspd =
                repositoryManager.getPreviousProcessDefinitionById(tenantId, processDefinitionId).getData();
            List<ItemTabBind> bindList =
                tabItemBindRepository.findByItemIdAndProcessDefinitionIdOrderByTabIndexAsc(itemId, previouspd.getId());
            for (ItemTabBind bind : bindList) {
                String tabId = bind.getTabId();
                ItemTabBind bindTemp = tabItemBindRepository.findByItemIdAndProcessDefinitionIdAndTabId(itemId,
                    processDefinitionId, tabId);
                if (null == bindTemp) {
                    bindTemp = new ItemTabBind();
                    bindTemp.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                    bindTemp.setItemId(itemId);
                    bindTemp.setProcessDefinitionId(processDefinitionId);
                    bindTemp.setTabId(tabId);
                    bindTemp.setTenantId(tenantId);
                    bindTemp.setUserId(personId);
                    bindTemp.setUserName(personName);
                    bindTemp.setCreateTime(sdf.format(new Date()));
                    bindTemp.setUpdateTime(sdf.format(new Date()));
                    Integer index = tabItemBindRepository.getMaxTabIndex(itemId, processDefinitionId);
                    if (index == null) {
                        bindTemp.setTabIndex(1);
                    } else {
                        bindTemp.setTabIndex(index + 1);
                    }
                    tabItemBindRepository.save(bindTemp);
                }
            }
        }
    }

    @Override
    public List<ItemTabBind> findByItemIdAndProcessDefinitionId(String itemId, String processDefinitionId) {
        List<ItemTabBind> tibList =
            tabItemBindRepository.findByItemIdAndProcessDefinitionIdOrderByTabIndexAsc(itemId, processDefinitionId);
        for (ItemTabBind tib : tibList) {
            TabEntity tabEntity = tabEntityService.findOne(tib.getTabId());
            if (null != tabEntity) {
                tib.setTabName(tabEntity.getName());
                tib.setTabUrl(tabEntity.getUrl());
            } else {
                tib.setTabName("页签已删除");
            }
        }
        return tibList;
    }

    @Override
    public ItemTabBind findOne(String id) {
        ItemTabBind tabItemBind = tabItemBindRepository.findById(id).orElse(null);
        assert tabItemBind != null;
        TabEntity tabEntity = tabEntityService.findOne(tabItemBind.getTabId());
        if (null != tabEntity) {
            tabItemBind.setTabName(tabEntity.getName());
            tabItemBind.setTabUrl(tabEntity.getUrl());
        } else {
            tabItemBind.setTabName("页签已删除");
        }
        return tabItemBind;
    }

    @Override
    @Transactional
    public void removeTabItemBinds(String[] tabItemBindIds) {
        for (String tabItemBindId : tabItemBindIds) {
            tabItemBindRepository.deleteById(tabItemBindId);
        }
    }

    @Override
    @Transactional
    public void save(ItemTabBind tabItemBind) {
        tabItemBindRepository.save(tabItemBind);
    }

    @Override
    @Transactional
    public void saveOrder(String[] idAndTabIndexs) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId(), userName = person.getName();
        List<ItemTabBind> oldtibList = new ArrayList<>();
        for (String idAndTabIndex : idAndTabIndexs) {
            String[] arr = idAndTabIndex.split(SysVariables.COLON);
            ItemTabBind oldtib = this.findOne(arr[0]);
            oldtib.setTabIndex(Integer.valueOf(arr[1]));
            oldtib.setUpdateTime(sdf.format(new Date()));
            oldtib.setUserId(userId);
            oldtib.setUserName(userName);

            oldtibList.add(oldtib);
        }
        tabItemBindRepository.saveAll(oldtibList);
    }

    @Override
    @Transactional
    public ItemTabBind saveTabBind(String tabId, String itemId, String processDefinitionId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId(), userName = person.getName(), tenantId = Y9LoginUserHolder.getTenantId();

        TabEntity tabEntity = tabEntityService.findOne(tabId);
        ItemTabBind tabItemBind = new ItemTabBind();
        tabItemBind.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        tabItemBind.setTenantId(tenantId);
        tabItemBind.setCreateTime(sdf.format(new Date()));
        tabItemBind.setItemId(itemId);
        tabItemBind.setProcessDefinitionId(processDefinitionId);
        tabItemBind.setTabId(tabEntity.getId());
        tabItemBind.setUpdateTime(sdf.format(new Date()));
        tabItemBind.setUserId(userId);
        tabItemBind.setUserName(userName);
        tabItemBind.setTabName(tabEntity.getName());
        tabItemBind.setTabUrl(tabEntity.getUrl());

        Integer index = tabItemBindRepository.getMaxTabIndex(itemId, processDefinitionId);
        if (index == null) {
            tabItemBind.setTabIndex(1);
        } else {
            tabItemBind.setTabIndex(index + 1);
        }

        this.save(tabItemBind);
        return tabItemBind;
    }
}
