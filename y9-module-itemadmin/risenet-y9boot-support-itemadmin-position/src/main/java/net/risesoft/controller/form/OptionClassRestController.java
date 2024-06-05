package net.risesoft.controller.form;

import lombok.RequiredArgsConstructor;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.form.Y9FormOptionClass;
import net.risesoft.entity.form.Y9FormOptionValue;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.form.Y9FormOptionClassService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/y9form/optionClass")
public class OptionClassRestController {

    
    private final Y9FormOptionClassService y9FormOptionClassService;

    /**
     * 删除数据字典
     *
     * @param type 字典类型
     * @return
     */
    @RequestMapping(value = "/delOptionClass", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> delOptionClass(String type) {
        Map<String, Object> map = y9FormOptionClassService.delOptionClass(type);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }

    /**
     * 删除数据字典值
     *
     * @param id 主键id
     * @return
     */
    @RequestMapping(value = "/delOptionValue", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> delOptionValue(String id) {
        Map<String, Object> map = y9FormOptionClassService.delOptionValue(id);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }

    /**
     * 获取数据字典
     *
     * @param type 字典类型
     * @return
     */
    @RequestMapping(value = "/getOptionClass", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Y9FormOptionClass> getOptionClass(String type) {
        Y9FormOptionClass y9FormOptionClass = y9FormOptionClassService.findByType(type);
        return Y9Result.success(y9FormOptionClass, "获取成功");
    }

    /**
     * 获取数据字典列表
     *
     * @param name 数据字典名称
     * @return
     */
    @RequestMapping(value = "/getOptionClassList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Y9FormOptionClass>> getOptionClassList(@RequestParam(required = false) String name) {
        List<Y9FormOptionClass> list = y9FormOptionClassService.findByName(name);
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 数据字典值
     *
     * @param id 主键id
     * @return
     */
    @RequestMapping(value = "/getOptionValue", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Y9FormOptionValue> getOptionValue(String id) {
        Y9FormOptionValue y9FormOptionValue = y9FormOptionClassService.findById(id);
        return Y9Result.success(y9FormOptionValue, "获取成功");
    }

    /**
     * 获取数据字典值列表
     *
     * @param type 字典标识
     * @return
     */
    @RequestMapping(value = "/getOptionValueList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Y9FormOptionValue>> getOptionValueList(String type) {
        List<Y9FormOptionValue> list = y9FormOptionClassService.findByTypeOrderByTabIndexAsc(type);
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 保存数据字典
     *
     * @param optionClass 字典类型数据
     * @return
     */
    @RequestMapping(value = "/saveOptionClass", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveOptionClass(Y9FormOptionClass optionClass) {
        Map<String, Object> map = y9FormOptionClassService.saveOptionClass(optionClass);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }

    /**
     * 保存数据字典值
     *
     * @param optionValue 字典值数据
     * @return
     */
    @RequestMapping(value = "/saveOptionValue", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveOptionValue(Y9FormOptionValue optionValue) {
        Map<String, Object> map = y9FormOptionClassService.saveOptionValue(optionValue);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }

    /**
     * 保存排序
     *
     * @param ids 主键ids
     * @return
     */
    @RequestMapping(value = "/saveOrder", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveOrder(String ids) {
        Map<String, Object> map = y9FormOptionClassService.saveOrder(ids);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }

    /**
     * 设置默认选中
     *
     * @param id 主键id
     * @return
     */
    @RequestMapping(value = "/updateOptionValue", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> updateOptionValue(String id) {
        Map<String, Object> map = y9FormOptionClassService.updateOptionValue(id);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }

}
