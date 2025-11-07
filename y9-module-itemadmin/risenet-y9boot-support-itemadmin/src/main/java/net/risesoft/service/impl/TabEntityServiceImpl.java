package net.risesoft.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
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
        return tabEntityRepository.findAll(Sort.by(Sort.Direction.ASC, "createTime"));
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
        String id = tabEntity.getId();
        if (StringUtils.isNotEmpty(id)) {
            TabEntity oldTabEntity = this.getById(id);
            if (null != oldTabEntity) {
                oldTabEntity.setName(tabEntity.getName());
                oldTabEntity.setUrl(tabEntity.getUrl());
                oldTabEntity.setUserId(userId);
                oldTabEntity.setUserName(userName);
                return tabEntityRepository.save(oldTabEntity);
            } else {
                return tabEntityRepository.save(tabEntity);
            }
        }

        TabEntity newTabEntity = new TabEntity();
        newTabEntity.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        newTabEntity.setName(tabEntity.getName());
        newTabEntity.setUrl(tabEntity.getUrl());
        newTabEntity.setUserId(userId);
        newTabEntity.setUserName(userName);
        newTabEntity.setTenantId(tenantId);
        return tabEntityRepository.save(newTabEntity);
    }
}
