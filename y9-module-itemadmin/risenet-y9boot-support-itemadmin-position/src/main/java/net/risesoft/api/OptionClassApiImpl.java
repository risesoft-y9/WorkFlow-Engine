package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.OptionClassApi;
import net.risesoft.entity.form.Y9FormOptionValue;
import net.risesoft.model.itemadmin.Y9FormOptionValueModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.form.Y9FormOptionClassService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 数据字典接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/optionClass", produces = MediaType.APPLICATION_JSON_VALUE)
public class OptionClassApiImpl implements OptionClassApi {

    private final Y9FormOptionClassService y9FormOptionClassService;

    /**
     * 获取数据字典列表
     *
     * @param tenantId 租户id
     * @param type 字典标识
     */
    @Override
    public Y9Result<List<Y9FormOptionValueModel>> getOptionValueList(String tenantId, String type) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<Y9FormOptionValue> list = y9FormOptionClassService.findByTypeOrderByTabIndexAsc(type);
        List<Y9FormOptionValueModel> listMap = new ArrayList<>();
        for (Y9FormOptionValue option : list) {
            Y9FormOptionValueModel map = new Y9FormOptionValueModel();
            map.setCode(option.getCode());
            map.setName(option.getName());
            map.setType(option.getType());
            map.setDefaultSelected(option.getDefaultSelected());
            listMap.add(map);
        }
        return Y9Result.success(listMap);
    }

}
