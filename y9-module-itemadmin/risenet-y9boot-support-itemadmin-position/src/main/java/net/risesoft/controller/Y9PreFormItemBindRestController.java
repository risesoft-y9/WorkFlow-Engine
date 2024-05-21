package net.risesoft.controller;

import java.util.ArrayList;
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
import net.risesoft.entity.Y9PreFormItemBind;
import net.risesoft.entity.form.Y9Form;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.form.Y9FormRepository;
import net.risesoft.service.Y9PreFormItemBindService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequestMapping("/vue/preFormBind")
public class Y9PreFormItemBindRestController {

    @Autowired
    private Y9PreFormItemBindService y9PreFormItemBindService;

    @Autowired
    private Y9FormRepository y9FormRepository;

    /**
     * 删除绑定表单
     *
     * @param id 绑定id
     * @return
     */
    @RequestMapping(value = "/deleteBind", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Y9Result<String> deleteBind(@RequestParam(required = true) String id) {
        Map<String, Object> map = y9PreFormItemBindService.delete(id);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }

    /**
     * 获取绑定的表单
     *
     * @param itemId 事项id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getBindList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Y9PreFormItemBind> getBindList(@RequestParam(required = true) String itemId) {
        Y9PreFormItemBind bind = y9PreFormItemBindService.findByItemId(itemId);
        Y9Form form = y9FormRepository.findById(bind.getFormId()).orElse(null);
        bind.setFormName(form != null ? form.getFormName() : "表单不存在");
        return Y9Result.success(bind, "获取成功");
    }

    /**
     * 获取表单列表
     *
     * @param itemId 事项id
     * @param formName 表单名称
     * @param systemName 系统名称
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getFormList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> getFormList(@RequestParam(required = true) String itemId, @RequestParam(required = true) String systemName, @RequestParam(required = false) String formName) {
        List<Map<String, Object>> listmap = new ArrayList<Map<String, Object>>();
        List<Y9Form> list = y9FormRepository.findBySystemNameAndFormNameLike(systemName, "%" + formName + "%");
        Y9PreFormItemBind bind = y9PreFormItemBindService.findByItemId(itemId);
        for (Y9Form y9Form : list) {
            Map<String, Object> map = new HashMap<String, Object>(16);
            boolean isbind = false;
            if (bind != null && bind.getFormId().equals(y9Form.getId())) {
                isbind = true;
            }
            if (!isbind) {
                map.put("formName", y9Form.getFormName());
                map.put("formId", y9Form.getId());
                listmap.add(map);
            }
        }
        return Y9Result.success(listmap, "获取成功");
    }

    /**
     * 保存绑定表单
     *
     * @param formId 表单id
     * @param itemId 事项id
     * @param formName 表单名称
     * @return
     */
    @RequestMapping(value = "/saveBindForm", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Y9Result<String> saveBindForm(String itemId, String formId, String formName) {
        Map<String, Object> map = y9PreFormItemBindService.saveBindForm(itemId, formId, formName);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }

}
