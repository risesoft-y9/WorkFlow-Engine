package net.risesoft.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.entity.template.TaoHongTemplate;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.jpa.TaoHongTemplateRepository;
import net.risesoft.service.TaoHongTemplateService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class TaoHongTemplateServiceImpl implements TaoHongTemplateService {

    private final TaoHongTemplateRepository taoHongTemplateRepository;

    private final OrgUnitApi orgUnitApi;

    @Override
    public TaoHongTemplate getById(String id) {
        return taoHongTemplateRepository.findById(id).orElse(null);
    }

    @Override
    public List<TaoHongTemplate> listByBureauGuid(String bureauGuid) {
        return taoHongTemplateRepository.findByBureauGuid(bureauGuid);
    }

    @Override
    public List<TaoHongTemplate> listByTenantId(String tenantId, String name) {
        return taoHongTemplateRepository.findByTenantId(tenantId, name);
    }

    @Override
    @Transactional
    public void removeTaoHongTemplate(String id) {
        taoHongTemplateRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void removeTaoHongTemplate(String[] templateGuids) {
        for (String templateGuid : templateGuids) {
            taoHongTemplateRepository.deleteById(templateGuid);
        }
    }

    @Override
    @Transactional
    public TaoHongTemplate saveOrUpdate(TaoHongTemplate t) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9LoginUserHolder.getPersonId();
        String bureauName = orgUnitApi.getOrgUnit(tenantId, t.getBureauGuid()).getData().getName();
        String id = t.getTemplateGuid();
        if (StringUtils.isNotBlank(id)) {
            TaoHongTemplate oldtt = taoHongTemplateRepository.findById(id).orElse(null);
            if (null != oldtt) {
                oldtt.setBureauGuid(t.getBureauGuid());
                oldtt.setBureauName(bureauName);
                oldtt.setUserId(userId);
                oldtt.setTemplateType(t.getTemplateType());
                oldtt.setUploadTime(new Date());
                if (StringUtils.isNotBlank(t.getTemplateFileName())) {
                    oldtt.setTemplateContent(t.getTemplateContent());
                    oldtt.setTemplateFileName(t.getTemplateFileName());
                }
                return taoHongTemplateRepository.save(oldtt);
            } else {
                return taoHongTemplateRepository.save(t);
            }
        }

        TaoHongTemplate newtt = new TaoHongTemplate();
        newtt.setTemplateGuid(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        newtt.setTenantId(tenantId);
        newtt.setBureauGuid(t.getBureauGuid());
        newtt.setBureauName(bureauName);
        newtt.setUserId(userId);
        newtt.setTemplateType(t.getTemplateType());
        newtt.setUploadTime(new Date());
        if (StringUtils.isNotBlank(t.getTemplateFileName())) {
            newtt.setTemplateContent(t.getTemplateContent());
            newtt.setTemplateFileName(t.getTemplateFileName());
        }

        Integer index = taoHongTemplateRepository.getMaxTabIndex();
        if (index == null) {
            newtt.setTabIndex(0);
        } else {
            newtt.setTabIndex(index + 1);
        }

        return taoHongTemplateRepository.save(newtt);
    }

}
