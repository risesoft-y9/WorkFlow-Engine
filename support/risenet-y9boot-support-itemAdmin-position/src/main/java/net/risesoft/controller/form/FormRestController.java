package net.risesoft.controller.form;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.form.Y9Form;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.form.Y9FormService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequestMapping(value = "/vue/y9form")
public class FormRestController {

    @Autowired
    private Y9FormService y9FormService;

    /**
     * 获取表单信息
     *
     * @param id 表单id
     * @return
     */
    @RequestMapping(value = "/getForm", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> getForm(@RequestParam(required = true) String id) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        Y9Form y9Form = y9FormService.findById(id);
        String json = y9FormService.getFormField(id);
        map.put("y9Form", y9Form);
        map.put("formField", json);
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 获取表单列表
     *
     * @param systemName 应用名称
     * @param page 页码
     * @param rows 条数
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/getFormList", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Y9Page<Map<String, Object>> getFormList(@RequestParam(required = false) String systemName, @RequestParam(required = true) int page, @RequestParam(required = true) int rows) {
        Map<String, Object> map = y9FormService.getFormList(systemName, page, rows);
        List<Map<String, Object>> list = (List<Map<String, Object>>)map.get("rows");
        return Y9Page.success(page, Integer.parseInt(map.get("totalpages").toString()), Integer.parseInt(map.get("total").toString()), list, "获取列表成功");
    }

    /**
     * 保存表单信息
     *
     * @param form 表单信息
     * @return
     */
    @RequestMapping(value = "/newOrModifyForm", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Y9Result<String> newOrModifyForm(Y9Form form) {
        Map<String, Object> map = y9FormService.saveOrUpdate(form);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }

    /**
     * 删除表单
     *
     * @param ids 表单id
     * @return
     */
    @RequestMapping(value = "/removeForm", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Y9Result<String> removeForm(@RequestParam(required = true) String ids) {
        Map<String, Object> map = y9FormService.delete(ids);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }

    /**
     * 保存表单JSON信息
     *
     * @param id 表单id
     * @param formJson 表单json
     * @return
     */
    @RequestMapping(value = "/saveFormField", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Y9Result<String> saveFormField(@RequestParam(required = true) String formId, @RequestParam(required = false) String fieldJson) {
        Map<String, Object> map = y9FormService.saveFormField(formId, fieldJson);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }

    /**
     * 保存表单JSON信息
     *
     * @param id 表单id
     * @param formJson 表单json
     * @return
     */
    @RequestMapping(value = "/saveFormJson", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Y9Result<String> saveFormJson(@RequestParam(required = true) String id, @RequestParam(required = false) String formJson) {
        Map<String, Object> map = y9FormService.saveFormJson(id, formJson);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }
}
