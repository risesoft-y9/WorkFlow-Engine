package net.risesoft.service.impl;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import net.risesoft.entity.ItemViewConf;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.jpa.ItemViewConfRepository;
import net.risesoft.service.ItemViewConfService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class ItemViewConfServiceImpl implements ItemViewConfService {

    private final ItemViewConfRepository itemViewConfRepository;

    @Override
    @Transactional()
    public void copyView(String[] ids, String viewType) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (String id : ids) {
            ItemViewConf oldView = this.findById(id);
            ItemViewConf newView = new ItemViewConf();
            Y9BeanUtil.copyProperties(oldView, newView);
            newView.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            newView.setCreateTime(sdf.format(new Date()));
            newView.setUpdateTime(sdf.format(new Date()));
            newView.setViewType(viewType);
            itemViewConfRepository.save(newView);
        }

    }

    @Override
    public ItemViewConf findById(String id) {
        return itemViewConfRepository.findById(id).orElse(null);
    }

    @Override
    public List<ItemViewConf> findByItemId(String itemId) {
        return itemViewConfRepository.findByItemIdOrderByTabIndexAsc(itemId);
    }

    @Override
    public List<ItemViewConf> findByItemIdAndViewType(String itemId, String viewType) {
        return itemViewConfRepository.findByItemIdAndViewTypeOrderByTabIndexAsc(itemId, viewType);
    }

    @Override
    public List<ItemViewConf> findByViewType(String viewType) {
        return itemViewConfRepository.findByViewTypeOrderByTabIndexAsc(viewType);
    }

    @Override
    @Transactional()
    public void removeByViewType(String viewType) {
        List<ItemViewConf> list = itemViewConfRepository.findByViewTypeOrderByTabIndexAsc(viewType);
        itemViewConfRepository.deleteAll(list);

    }

    @Override
    @Transactional()
    public void removeItemViewConfs(String[] itemViewConfIds) {
        for (String id : itemViewConfIds) {
            itemViewConfRepository.deleteById(id);
        }
    }

    @Override
    @Transactional()
    public void saveOrUpdate(ItemViewConf itemViewConf) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String id = itemViewConf.getId();
        if (StringUtils.isNotBlank(id)) {
            ItemViewConf oldConf = this.findById(id);
            if (null != oldConf) {
                oldConf.setColumnName(itemViewConf.getColumnName());
                oldConf.setDisPlayWidth(itemViewConf.getDisPlayWidth());
                oldConf.setDisPlayName(itemViewConf.getDisPlayName());
                oldConf.setDisPlayAlign(itemViewConf.getDisPlayAlign());
                oldConf.setTableName(itemViewConf.getTableName());
                oldConf.setUpdateTime(sdf.format(new Date()));
                oldConf.setUserId(person.getPersonId());
                oldConf.setUserName(person.getName());
                oldConf.setInputBoxType(itemViewConf.getInputBoxType());
                oldConf.setLabelName(itemViewConf.getLabelName());
                oldConf.setOpenSearch(itemViewConf.getOpenSearch());
                oldConf.setOptionClass(itemViewConf.getOptionClass());
                oldConf.setSpanWidth(itemViewConf.getSpanWidth());
                itemViewConfRepository.save(oldConf);
                return;
            } else {
                itemViewConfRepository.save(itemViewConf);
                return;
            }
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
        newConf.setUserId(person.getPersonId());
        newConf.setUserName(person.getName());
        newConf.setCreateTime(sdf.format(new Date()));
        newConf.setUpdateTime(sdf.format(new Date()));
        newConf.setInputBoxType(itemViewConf.getInputBoxType());
        newConf.setLabelName(itemViewConf.getLabelName());
        newConf.setOpenSearch(itemViewConf.getOpenSearch());
        newConf.setOptionClass(itemViewConf.getOptionClass());
        newConf.setSpanWidth(itemViewConf.getSpanWidth());
        Integer index = itemViewConfRepository.getMaxTabIndex(itemViewConf.getItemId(), itemViewConf.getViewType());
        if (index == null) {
            newConf.setTabIndex(1);
        } else {
            newConf.setTabIndex(index + 1);
        }
        itemViewConfRepository.save(newConf);
    }

    @Override
    @Transactional()
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
