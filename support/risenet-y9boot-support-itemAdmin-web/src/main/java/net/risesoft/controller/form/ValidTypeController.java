package net.risesoft.controller.form;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.form.Y9ValidType;
import net.risesoft.service.form.Y9ValidTypeService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Controller
@RequestMapping(value = "/y9form/validType")
public class ValidTypeController {

    @Autowired
    private Y9ValidTypeService y9ValidTypeService;

    /**
     * 删除校验规则
     * 
     * @param ids
     * @return
     */
    @RequestMapping(value = "/delValidType")
    @ResponseBody
    public Map<String, Object> delValidType(String ids) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map = y9ValidTypeService.delValidType(ids);
        return map;
    }

    /**
     * 获取检验方法
     * 
     * @param id
     * @return
     */
    @RequestMapping(value = "/getValidType")
    @ResponseBody
    public Map<String, Object> getValidType(String id) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        Y9ValidType y9ValidType = y9ValidTypeService.findById(id);
        map.put("validContent", y9ValidType != null ? y9ValidType.getValidContent() : "");
        return map;
    }

    /**
     * 获取校验规则定义列表
     * 
     * @param validType
     * @param validCnName
     * @return
     */
    @RequestMapping(value = "/getValidTypeList")
    @ResponseBody
    public Map<String, Object> getValidTypeList(@RequestParam(required = false) String validType, @RequestParam(required = false) String validCnName) {
        Map<String, Object> map = y9ValidTypeService.getValidTypeList(validType, validCnName);
        return map;
    }

    /**
     * 跳转校验规则定义列表页面
     * 
     * @param model
     * @return
     */
    @RequestMapping(value = "/validTypeManage")
    public String index(Model model) {
        return "y9form/validType/index";
    }

    /**
     * 添加校验规则页面
     * 
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(value = "/newOrModifyValidType")
    public String newOrModifyValidType(@RequestParam(required = false) String id, Model model) {
        model.addAttribute("hasConent", false);
        if (StringUtils.isNotBlank(id) && !UtilConsts.NULL.equals(id)) {
            Y9ValidType y9ValidType = y9ValidTypeService.findById(id);
            model.addAttribute("y9ValidType", y9ValidType);
        }
        return "y9form/validType/newOrModify";
    }

    /**
     * 保存检验规则
     * 
     * @param y9ValidType
     * @return
     */
    @RequestMapping(value = "/saveValidType")
    @ResponseBody
    public Map<String, Object> saveValidType(Y9ValidType y9ValidType) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map = y9ValidTypeService.saveOrUpdate(y9ValidType);
        return map;
    }

}
