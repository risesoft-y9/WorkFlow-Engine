package net.risesoft.controller.form;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.form.Y9FormOptionClass;
import net.risesoft.entity.form.Y9FormOptionValue;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.form.Y9FormOptionClassService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/y9form/optionClass", produces = MediaType.APPLICATION_JSON_VALUE)
public class OptionClassRestController {

    private final Y9FormOptionClassService y9FormOptionClassService;

    /**
     * 删除数据字典
     *
     * @param type 字典类型
     */
    @PostMapping(value = "/delOptionClass")
    public Y9Result<String> delOptionClass(String type) {
        return y9FormOptionClassService.delOptionClass(type);
    }

    /**
     * 删除数据字典值
     *
     * @param id 主键id
     * @return Y9Result<String>
     */
    @PostMapping(value = "/delOptionValue")
    public Y9Result<String> delOptionValue(String id) {
        return y9FormOptionClassService.delOptionValue(id);
    }

    /**
     * 获取数据字典
     *
     * @param type 字典类型
     */
    @GetMapping(value = "/getOptionClass")
    public Y9Result<Y9FormOptionClass> getOptionClass(String type) {
        Y9FormOptionClass y9FormOptionClass = y9FormOptionClassService.findByType(type);
        return Y9Result.success(y9FormOptionClass, "获取成功");
    }

    /**
     * 获取数据字典列表
     *
     * @param name 数据字典名称
     */
    @GetMapping(value = "/getOptionClassList")
    public Y9Result<List<Y9FormOptionClass>> getOptionClassList(@RequestParam(required = false) String name) {
        List<Y9FormOptionClass> list = y9FormOptionClassService.listByName(name);
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 数据字典值
     *
     * @param id 主键id
     * @return Y9Result<Y9FormOptionValue>
     */
    @GetMapping(value = "/getOptionValue")
    public Y9Result<Y9FormOptionValue> getOptionValue(String id) {
        Y9FormOptionValue y9FormOptionValue = y9FormOptionClassService.findById(id);
        return Y9Result.success(y9FormOptionValue, "获取成功");
    }

    /**
     * 获取数据字典值列表
     *
     * @param type 字典标识
     * @return Y9Result<List < Y9FormOptionValue>>
     */
    @GetMapping(value = "/getOptionValueList")
    public Y9Result<List<Y9FormOptionValue>> getOptionValueList(String type) {
        List<Y9FormOptionValue> list = y9FormOptionClassService.listByTypeOrderByTabIndexAsc(type);
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 保存数据字典
     *
     * @param optionClass 字典类型数据
     * @return
     */
    @PostMapping(value = "/saveOptionClass")
    public Y9Result<Y9FormOptionClass> saveOptionClass(Y9FormOptionClass optionClass) {
        return y9FormOptionClassService.saveOptionClass(optionClass);
    }

    /**
     * 保存数据字典值
     *
     * @param optionValue 字典值数据
     * @return
     */
    @PostMapping(value = "/saveOptionValue")
    public Y9Result<Y9FormOptionValue> saveOptionValue(Y9FormOptionValue optionValue) {
        return y9FormOptionClassService.saveOptionValue(optionValue);
    }

    /**
     * 保存排序
     *
     * @param ids 主键ids
     * @return
     */
    @PostMapping(value = "/saveOrder")
    public Y9Result<String> saveOrder(String ids) {
        return y9FormOptionClassService.saveOrder(ids);
    }

    /**
     * 设置默认选中
     *
     * @param id 主键id
     * @return
     */
    @PostMapping(value = "/updateOptionValue")
    public Y9Result<String> updateOptionValue(String id) {
        return y9FormOptionClassService.updateOptionValue(id);
    }

}
