package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.tab.TabEntity;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.tab.TabEntityRepository;
import net.risesoft.service.TabEntityService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class TabEntityServiceImpl implements TabEntityService {

    private final TabEntityRepository tabEntityRepository;

    @Override
    public TabEntity getById(String id) {
        return tabEntityRepository.findById(id).orElse(null);
    }

    @Override
    public List<TabEntity> listAll() {
        return tabEntityRepository.findAll();
    }

    @Override
    @Transactional
    public void removeTabEntitys(String[] tabEntityIds) {
        for (String tabEntityId : tabEntityIds) {
            tabEntityRepository.deleteById(tabEntityId);
        }
    }

    @Override
    @Transactional
    public TabEntity saveOrUpdate(TabEntity tabEntity) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId(), userName = person.getName(), tenantId = Y9LoginUserHolder.getTenantId();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String id = tabEntity.getId();
        if (StringUtils.isNotEmpty(id)) {
            TabEntity oldte = this.getById(id);
            if (null != oldte) {
                oldte.setName(tabEntity.getName());
                oldte.setUrl(tabEntity.getUrl());
                oldte.setUpdateTime(sdf.format(new Date()));
                oldte.setUserId(userId);
                oldte.setUserName(userName);

                return tabEntityRepository.save(oldte);
            } else {
                return tabEntityRepository.save(tabEntity);
            }
        }

        TabEntity newte = new TabEntity();
        newte.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        newte.setName(tabEntity.getName());
        newte.setUrl(tabEntity.getUrl());
        newte.setUserId(userId);
        newte.setUserName(userName);
        newte.setTenantId(tenantId);
        newte.setCreateTime(sdf.format(new Date()));
        newte.setUpdateTime(sdf.format(new Date()));
        return tabEntityRepository.save(newte);
    }
}
