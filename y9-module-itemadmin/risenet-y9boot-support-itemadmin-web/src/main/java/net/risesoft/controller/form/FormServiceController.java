package net.risesoft.controller.form;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.model.platform.Person;
import net.risesoft.service.form.Y9FormService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 表单显示与保存,供第三方访问
 * 
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Controller
@RequestMapping(value = "/mobile/y9form")
public class FormServiceController {

    @Autowired
    private Y9FormService y9FormService;

    @Autowired
    private PersonApi personManager;

    /**
     * 表单保存
     * 
     * @param formdata
     * @param response
     * @param request
     * @return
     */
    @PostMapping(value = "/save")
    @ResponseBody
    public Map<String, Object> formSave(@RequestParam String formdata, HttpServletResponse response,
        HttpServletRequest request) {
        String tenantId = request.getParameter("y9Form_TenantId");
        String userId = request.getParameter("y9Form_UerId");
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map = y9FormService.saveFormData(formdata);
        return map;
    }

    /**
     * 获取表单是否有数据
     * 
     * @param guid
     * @param formId
     * @return
     */
    @PostMapping(value = "/getData")
    @ResponseBody
    public Map<String, Object> getData(@RequestParam(required = false) String guid, String formId,
        HttpServletResponse response, HttpServletRequest request) {
        String tenantId = request.getParameter("y9Form_TenantId");
        String userId = request.getParameter("y9Form_UerId");
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map = y9FormService.getData(guid, formId);
        return map;
    }

}
