package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.TabEntity;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.jpa.TabEntityRepository;
import net.risesoft.service.TabEntityService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service(value = "tabEntityService")
public class TabEntityServiceImpl implements TabEntityService {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private TabEntityRepository tabEntityRepository;

    @Override
    public List<TabEntity> findAll() {
        return tabEntityRepository.findAll();
    }

    @Override
    public TabEntity findOne(String id) {
        return tabEntityRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = false)
    public void removeTabEntitys(String[] tabEntityIds) {
        for (String tabEntityId : tabEntityIds) {
            tabEntityRepository.deleteById(tabEntityId);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public TabEntity saveOrUpdate(TabEntity tabEntity) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String userId = userInfo.getPersonId(), userName = userInfo.getName(), tenantId = Y9LoginUserHolder.getTenantId();

        String id = tabEntity.getId();
        if (StringUtils.isNotEmpty(id)) {
            TabEntity oldte = this.findOne(id);
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
