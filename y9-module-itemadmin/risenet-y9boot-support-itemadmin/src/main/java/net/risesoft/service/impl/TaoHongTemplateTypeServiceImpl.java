package net.risesoft.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.entity.template.TaoHongTemplateType;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.template.TaoHongTemplateTypeRepository;
import net.risesoft.service.TaoHongTemplateTypeService;
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
public class TaoHongTemplateTypeServiceImpl implements TaoHongTemplateTypeService {

    private final TaoHongTemplateTypeRepository taoHongTemplateTypeRepository;

    private final OrgUnitApi orgUnitApi;

    @Override
    public TaoHongTemplateType getById(String id) {
        return taoHongTemplateTypeRepository.findById(id).orElse(null);
    }

    @Override
    public List<TaoHongTemplateType> listAll() {
        return taoHongTemplateTypeRepository.findAll();
    }

    @Override
    public List<TaoHongTemplateType> listByBureauId(String bureauId) {
        return taoHongTemplateTypeRepository.findByBureauId(bureauId);
    }

    @Override
    @Transactional
    public void removeTaoHongTemplateType(String[] ids) {
        for (String id : ids) {
            taoHongTemplateTypeRepository.deleteById(id);
        }
    }

    @Override
    @Transactional
    public void saveOrder(String[] idAndTabIndexs) {
        for (String idAndTabIndex : idAndTabIndexs) {
            String id = idAndTabIndex.split(SysVariables.COLON)[0];
            Integer tabIndex = Integer.parseInt(idAndTabIndex.split(SysVariables.COLON)[1]);
            taoHongTemplateTypeRepository.update4Order(tabIndex, id);
        }
    }

    @Override
    @Transactional
    public TaoHongTemplateType saveOrUpdate(TaoHongTemplateType t) {
        String id = t.getId();
        if (StringUtils.isNotEmpty(id)) {
            TaoHongTemplateType oldtht = taoHongTemplateTypeRepository.findById(id).orElse(null);
            if (null != oldtht) {
                oldtht.setTypeName(t.getTypeName());
                return taoHongTemplateTypeRepository.save(oldtht);
            } else {
                return taoHongTemplateTypeRepository.save(t);
            }
        }

        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), personId = person.getPersonId();
        OrgUnit orgUnit = orgUnitApi.getBureau(tenantId, personId).getData();
        TaoHongTemplateType thtNew = new TaoHongTemplateType();
        thtNew.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        thtNew.setBureauId(orgUnit.getId());
        thtNew.setTypeName(t.getTypeName());

        Integer tabIndex = taoHongTemplateTypeRepository.getMaxTabIndex(orgUnit.getId());
        if (tabIndex == null) {
            thtNew.setTabIndex(1);
        } else {
            thtNew.setTabIndex(tabIndex + 1);
        }
        return taoHongTemplateTypeRepository.save(thtNew);
    }

}
