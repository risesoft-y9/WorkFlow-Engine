package net.risesoft.api;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.SpeakInfoApi;
import net.risesoft.api.org.PersonApi;
import net.risesoft.entity.SpeakInfo;
import net.risesoft.model.Person;
import net.risesoft.model.itemadmin.SpeakInfoModel;
import net.risesoft.service.SpeakInfoService;
import net.risesoft.util.ItemAdminModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequestMapping(value = "/services/rest/speakInfo")
public class SpeakInfoApiImpl implements SpeakInfoApi {

	@Autowired
	private SpeakInfoService speakInfoService;

	@Autowired
	private PersonApi personManager;

	@Override
	@PostMapping(value = "/deleteById", produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> deleteById(String tenantId, String userId, String id) {
		Y9LoginUserHolder.setTenantId(tenantId);
		return speakInfoService.deleteById(id);
	}

	@Override
	@GetMapping(value = "/findById", produces = MediaType.APPLICATION_JSON_VALUE)
	public SpeakInfoModel findById(String tenantId, String userId, String id) {
		Y9LoginUserHolder.setTenantId(tenantId);
		SpeakInfo speakInfo = speakInfoService.findById(id);
		return ItemAdminModelConvertUtil.speakInfo2Model(speakInfo);
	}

	@Override
	@GetMapping(value = "/findByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<SpeakInfoModel> findByProcessInstanceId(String tenantId, String userId, String processInstanceId) {
		Person person = personManager.getPersonById(tenantId, userId);
		Y9LoginUserHolder.setTenantId(tenantId);
		Y9LoginUserHolder.setPerson(person);

		List<SpeakInfo> siList = speakInfoService.findByProcessInstanceId(processInstanceId);
		return ItemAdminModelConvertUtil.speakInfoList2ModelList(siList);
	}

	@Override
	@GetMapping(value = "/getNotReadCount", produces = MediaType.APPLICATION_JSON_VALUE)
	public int getNotReadCount(String tenantId, String userId, String processInstanceId) {
		Y9LoginUserHolder.setTenantId(tenantId);
		return speakInfoService.getNotReadCount(processInstanceId, userId);
	}

	@Override
	@PostMapping(value = "/saveOrUpdate", produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
	public String saveOrUpdate(String tenantId, String userId,@RequestBody SpeakInfoModel speakInfoModel) {
		Person person = personManager.getPersonById(tenantId, userId);
		Y9LoginUserHolder.setTenantId(tenantId);
		Y9LoginUserHolder.setPerson(person);

		SpeakInfo speakInfo = ItemAdminModelConvertUtil.speakInfoModel2SpeakInfo(speakInfoModel);
		return speakInfoService.saveOrUpdate(speakInfo);
	}
}
