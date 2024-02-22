package net.risesoft.util.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.risesoft.entity.form.Y9ValidType;
import net.risesoft.enums.ItemFormTypeEnum;
import net.risesoft.repository.form.Y9ValidTypeRepository;
import net.risesoft.y9.Y9Context;

/**
 * 验证类型工具类
 * 
 * @author Think
 *
 */
public class ValidTypeUtil {

    private Y9ValidTypeRepository y9ValidTypeRepository;

    public ValidTypeUtil() {
        this.y9ValidTypeRepository = Y9Context.getBean(Y9ValidTypeRepository.class);
    }

    public List<Map<String, Object>> getValidType(Integer formType) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put("text", "请选择");
        map.put("value", "");
        list.add(map);

        String validType = "jeasyui";
        if (formType == ItemFormTypeEnum.JEASYUI.getValue()) {
            map = new HashMap<String, Object>(16);
            map.put("text", "电子邮箱");
            map.put("value", "email");
            list.add(map);

            map = new HashMap<String, Object>(16);
            map.put("text", "网址");
            map.put("value", "url");
            list.add(map);

            map = new HashMap<String, Object>(16);
            map.put("text", "英文");
            map.put("value", "english");
            list.add(map);

            map = new HashMap<String, Object>(16);
            map.put("text", "手机号码");
            map.put("value", "mobile");
            list.add(map);

            map = new HashMap<String, Object>(16);
            map.put("text", "IP地址");
            map.put("value", "ip");
            list.add(map);

            map = new HashMap<String, Object>(16);
            map.put("text", "身份证号码");
            map.put("value", "idcard");
            list.add(map);

            validType = "jeasyui";
        } else if (formType == ItemFormTypeEnum.LAYUI.getValue()) {
            map = new HashMap<String, Object>(16);
            map.put("text", "电子邮箱");
            map.put("value", "email");
            list.add(map);

            map = new HashMap<String, Object>(16);
            map.put("text", "网址");
            map.put("value", "url");
            list.add(map);

            map = new HashMap<String, Object>(16);
            map.put("text", "手机号码");
            map.put("value", "phone");
            list.add(map);

            map = new HashMap<String, Object>(16);
            map.put("text", "身份证号码");
            map.put("value", "identity");
            list.add(map);

            validType = "layui";
        }
        // 获取自定义校验规则
        List<Y9ValidType> list1 = y9ValidTypeRepository.findByValidType(validType);
        for (Y9ValidType y9ValidType : list1) {
            map = new HashMap<String, Object>(16);
            map.put("text", y9ValidType.getValidCnName());
            map.put("value", y9ValidType.getValidName());
            map.put("validId", y9ValidType.getId());
            list.add(map);
        }
        return list;
    }

}
