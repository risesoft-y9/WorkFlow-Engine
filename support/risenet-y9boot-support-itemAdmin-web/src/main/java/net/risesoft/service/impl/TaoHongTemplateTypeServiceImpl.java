package net.risesoft.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.api.org.PersonApi;
import net.risesoft.entity.TaoHongTemplateType;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.OrgUnit;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.jpa.TaoHongTemplateTypeRepository;
import net.risesoft.service.TaoHongTemplateTypeService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service(value = "taoHongTemplateTypeService")
public class TaoHongTemplateTypeServiceImpl implements TaoHongTemplateTypeService {

	@Autowired
	private TaoHongTemplateTypeRepository taoHongTemplateTypeRepository;

	@Autowired
	private PersonApi personManager;

	@Override
	public List<TaoHongTemplateType> findAll() {
		return taoHongTemplateTypeRepository.findAll();
	}

	@Override
	public List<TaoHongTemplateType> findByBureauId(String bureauId) {
		return taoHongTemplateTypeRepository.findByBureauId(bureauId);
	}

	@Override
	public TaoHongTemplateType findOne(String id) {
		return taoHongTemplateTypeRepository.findById(id).orElse(null);
	}

	@Override
	@Transactional(readOnly = false)
	public void removeTaoHongTemplateType(String[] ids) {
		for (String id : ids) {
			taoHongTemplateTypeRepository.deleteById(id);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void saveOrder(String[] idAndTabIndexs) {
		for (String idAndTabIndex : idAndTabIndexs) {
			String id = idAndTabIndex.split(SysVariables.COLON)[0];
			Integer tabIndex = Integer.parseInt(idAndTabIndex.split(SysVariables.COLON)[1]);
			taoHongTemplateTypeRepository.update4Order(tabIndex, id);
		}
	}

	@Override
	@Transactional(readOnly = false)
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

		UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
		String tenantId = Y9LoginUserHolder.getTenantId(), personId = userInfo.getPersonId();
		OrgUnit orgUnit = personManager.getBureau(tenantId, personId);
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
