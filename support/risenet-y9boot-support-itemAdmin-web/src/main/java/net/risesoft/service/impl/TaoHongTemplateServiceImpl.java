package net.risesoft.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.api.org.OrgUnitApi;
import net.risesoft.entity.TaoHongTemplate;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.jpa.TaoHongTemplateRepository;
import net.risesoft.service.TaoHongTemplateService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service(value = "taoHongTemplateService")
public class TaoHongTemplateServiceImpl implements TaoHongTemplateService {

    @Autowired
    private TaoHongTemplateRepository taoHongTemplateRepository;

    @Autowired
    private OrgUnitApi orgUnitManager;

    @Override
    public List<TaoHongTemplate> findByBureauGuid(String bureauGuid) {
        return taoHongTemplateRepository.findByBureauGuid(bureauGuid);
    }

    @Override
    public List<TaoHongTemplate> findByTenantId(String tenantId, String name) {
        return taoHongTemplateRepository.findByTenantId(tenantId, name);
    }

    @Override
    public TaoHongTemplate findOne(String id) {
        return taoHongTemplateRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = false)
    public void removeTaoHongTemplate(String id) {
        taoHongTemplateRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = false)
    public void removeTaoHongTemplate(String[] templateGuids) {
        for (String templateGuid : templateGuids) {
            taoHongTemplateRepository.deleteById(templateGuid);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public TaoHongTemplate saveOrUpdate(TaoHongTemplate t) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9LoginUserHolder.getPersonId();
        String bureauName = orgUnitManager.getOrgUnit(tenantId, t.getBureauGuid()).getData().getName();
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
