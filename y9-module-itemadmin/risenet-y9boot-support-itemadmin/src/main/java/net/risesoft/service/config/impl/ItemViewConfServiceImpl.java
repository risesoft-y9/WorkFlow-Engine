package net.risesoft.service.config.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.entity.view.ItemViewConf;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.view.ItemViewConfRepository;
import net.risesoft.service.config.ItemViewConfService;
import net.risesoft.util.Y9DateTimeUtils;
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
public class ItemViewConfServiceImpl implements ItemViewConfService {

    private final ItemViewConfRepository itemViewConfRepository;

    @Override
    @Transactional
    public void copyBindInfo(String itemId, String newItemId) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        try {
            List<ItemViewConf> list = itemViewConfRepository.findByItemIdOrderByTabIndexAsc(itemId);
            if (null != list && !list.isEmpty()) {
                for (ItemViewConf itemViewConf : list) {
                    ItemViewConf newConf = new ItemViewConf();
                    Y9BeanUtil.copyProperties(itemViewConf, newConf);
                    newConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                    newConf.setItemId(newItemId);
                    newConf.setCreateTime(Y9DateTimeUtils.formatCurrentDateTime());
                    newConf.setUpdateTime(Y9DateTimeUtils.formatCurrentDateTime());
                    newConf.setUserId(person.getPersonId());
                    newConf.setUserName(person.getName());
                    newConf.setTabIndex(itemViewConf.getTabIndex());
                    itemViewConfRepository.save(newConf);
                }
            }
        } catch (Exception e) {
            LOGGER.error("复制视图配置信息失败", e);
        }
    }

    @Override
    @Transactional
    public void copyView(String[] ids, String viewType) {
        for (String id : ids) {
            ItemViewConf oldView = this.findById(id);
            ItemViewConf newView = new ItemViewConf();
            Y9BeanUtil.copyProperties(oldView, newView);
            newView.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            newView.setCreateTime(Y9DateTimeUtils.formatCurrentDateTime());
            newView.setUpdateTime(Y9DateTimeUtils.formatCurrentDateTime());
            newView.setViewType(viewType);
            itemViewConfRepository.save(newView);
        }

    }

    @Override
    @Transactional
    public void deleteBindInfo(String itemId) {
        try {
            List<ItemViewConf> list = itemViewConfRepository.findByItemIdOrderByTabIndexAsc(itemId);
            if (null != list && !list.isEmpty()) {
                itemViewConfRepository.deleteAll(list);
            }
        } catch (Exception e) {
            LOGGER.error("删除视图配置信息失败", e);
        }
    }

    @Override
    public ItemViewConf findById(String id) {
        return itemViewConfRepository.findById(id).orElse(null);
    }

    @Override
    public ItemViewConf findByItemIdAndViewTypeAndColumnName(String itemId, String viewType, String columnName) {
        return itemViewConfRepository.findByItemIdAndViewTypeAndColumnName(itemId, viewType, columnName);
    }

    @Override
    public List<ItemViewConf> listByItemId(String itemId) {
        return itemViewConfRepository.findByItemIdOrderByTabIndexAsc(itemId);
    }

    @Override
    @Transactional
    public List<ItemViewConf> listByItemIdAndViewType(String itemId, String viewType) {
        init(itemId, viewType);
        return itemViewConfRepository.findByItemIdAndViewTypeOrderByTabIndexAsc(itemId, viewType);
    }

    private void init(String itemId, String viewType) {
        List<ItemViewConf> list = itemViewConfRepository.findByItemIdAndViewTypeOrderByTabIndexAsc(itemId, viewType);
        if (list.isEmpty()) {
            Map<String, String> map = Map.of("serialNumber", "序号", "opt", "操作");
            map.forEach((key, value) -> {
                ItemViewConf newConf = new ItemViewConf();
                newConf.setColumnName(key);
                newConf.setDisPlayWidth("150");
                newConf.setDisPlayName(value);
                newConf.setDisPlayAlign("center");
                newConf.setItemId(itemId);
                newConf.setViewType(viewType);
                newConf.setUserId("");
                newConf.setUserName("");
                saveOrUpdate(newConf);
            });
        }
    }

    @Override
    public List<ItemViewConf> listByViewType(String viewType) {
        return itemViewConfRepository.findByViewTypeOrderByTabIndexAsc(viewType);
    }

    @Override
    @Transactional
    public void removeByViewType(String viewType) {
        List<ItemViewConf> list = itemViewConfRepository.findByViewTypeOrderByTabIndexAsc(viewType);
        itemViewConfRepository.deleteAll(list);
    }

    @Override
    @Transactional
    public void removeItemViewConfs(String[] itemViewConfIds) {
        for (String id : itemViewConfIds) {
            itemViewConfRepository.deleteById(id);
        }
    }

    @Override
    @Transactional
    public void saveOrUpdate(ItemViewConf itemViewConf) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String id = itemViewConf.getId();
        if (StringUtils.isNotBlank(id)) {
            ItemViewConf oldConf = this.findById(id);
            if (null != oldConf) {
                oldConf.setColumnName(itemViewConf.getColumnName());
                oldConf.setDisPlayWidth(itemViewConf.getDisPlayWidth());
                oldConf.setDisPlayName(itemViewConf.getDisPlayName());
                oldConf.setDisPlayAlign(itemViewConf.getDisPlayAlign());
                oldConf.setTableName(itemViewConf.getTableName());
                oldConf.setUpdateTime(Y9DateTimeUtils.formatCurrentDateTime());
                oldConf.setUserId(person.getPersonId());
                oldConf.setUserName(person.getName());
                oldConf.setInputBoxType(itemViewConf.getInputBoxType());
                oldConf.setLabelName(itemViewConf.getLabelName());
                oldConf.setOpenSearch(itemViewConf.getOpenSearch());
                oldConf.setOptionClass(itemViewConf.getOptionClass());
                oldConf.setSpanWidth(itemViewConf.getSpanWidth());
                itemViewConfRepository.save(oldConf);
            } else {
                itemViewConfRepository.save(itemViewConf);
            }
            return;
        }

        ItemViewConf newConf = new ItemViewConf();
        newConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        newConf.setColumnName(itemViewConf.getColumnName());
        newConf.setDisPlayWidth(itemViewConf.getDisPlayWidth());
        newConf.setDisPlayName(itemViewConf.getDisPlayName());
        newConf.setDisPlayAlign(itemViewConf.getDisPlayAlign());
        newConf.setItemId(itemViewConf.getItemId());
        newConf.setTableName(itemViewConf.getTableName());
        newConf.setViewType(itemViewConf.getViewType());
        newConf.setUserId(null == person ? "" : person.getPersonId());
        newConf.setUserName(null == person ? "" : person.getName());
        newConf.setCreateTime(Y9DateTimeUtils.formatCurrentDateTime());
        newConf.setUpdateTime(Y9DateTimeUtils.formatCurrentDateTime());
        newConf.setInputBoxType(itemViewConf.getInputBoxType());
        newConf.setLabelName(itemViewConf.getLabelName());
        newConf.setOpenSearch(itemViewConf.getOpenSearch());
        newConf.setOptionClass(itemViewConf.getOptionClass());
        newConf.setSpanWidth(itemViewConf.getSpanWidth());
        Integer index = itemViewConfRepository.getMaxTabIndex(itemViewConf.getItemId(), itemViewConf.getViewType());
        newConf.setTabIndex(index == null ? 1 : index + 1);
        itemViewConfRepository.save(newConf);
    }

    @Override
    @Transactional
    public void update4Order(String[] idAndTabIndexs) {
        List<String> list = Lists.newArrayList(idAndTabIndexs);
        try {
            for (String s : list) {
                String[] arr = s.split(SysVariables.COLON);
                itemViewConfRepository.update4Order(Integer.parseInt(arr[1]), arr[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
