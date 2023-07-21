package net.risesoft.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.OptionClassApi;
import net.risesoft.entity.form.Y9FormOptionValue;
import net.risesoft.service.form.Y9FormOptionClassService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequestMapping(value = "/services/rest/optionClass")
public class OptionClassApiImpl implements OptionClassApi {

	@Autowired
	private Y9FormOptionClassService y9FormOptionClassService;
	
	@Override
	@GetMapping(value = "/getOptionValueList", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Map<String, Object>> getOptionValueList(String tenantId, String type) {
		Y9LoginUserHolder.setTenantId(tenantId);
		List<Y9FormOptionValue> list = y9FormOptionClassService.findByTypeOrderByTabIndexAsc(type);
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		for (Y9FormOptionValue option : list) {
			Map<String, Object> map = new HashMap<String, Object>(16);
			map.put("code", option.getCode());
			map.put("name", option.getName());
			map.put("defaultSelected", option.getDefaultSelected());
			listMap.add(map);
		}
		return listMap;
	}

}
