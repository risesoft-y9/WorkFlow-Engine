package net.risesoft.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.risesoft.entity.ExtranetEformItemBind;
import net.risesoft.service.ExtranetEformItemBindService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Controller
@RequestMapping("/extranetEform/item")
public class ExtranetEformItemBindController {

    @Autowired
    private ExtranetEformItemBindService extranetEformItemBindService;

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Map<String, Object> delete(@RequestParam(required = false) String id) {
        Map<String, Object> map = extranetEformItemBindService.delete(id);
        return map;
    }

    /**
     * 电子表单列表
     *
     * @param itemId
     * @param model
     * @return
     */
    @RequestMapping(value = "/bind/eformList")
    public String eformList(@RequestParam(required = false) String itemId, Model model) {
        model.addAttribute("itemId", itemId);
        return "item/form/extranetFormBind/eformList";
    }

    /**
     * 获取绑定列表数据
     *
     * @param itemId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getList")
    public List<ExtranetEformItemBind> getList(@RequestParam(required = false) String itemId) {
        List<ExtranetEformItemBind> list = extranetEformItemBindService.getList(itemId);
        return list;
    }

    /**
     * 跳转绑定列表页面
     *
     * @param itemId
     * @param model
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(@RequestParam(required = false) String itemId, Model model) {
        model.addAttribute("itemId", itemId);
        return "item/form/extranetFormBind/extranetFormBindlist";
    }

    /**
     * 保存绑定表单
     *
     * @param itemId
     * @param formId
     * @param formName
     * @return
     */
    @RequestMapping(value = "/save")
    @ResponseBody
    public Map<String, Object> save(@RequestParam(required = false) String itemId,
        @RequestParam(required = false) String formId, @RequestParam(required = false) String formName) {
        Map<String, Object> map = extranetEformItemBindService.save(itemId, formId, formName);
        return map;
    }
}
