package net.risesoft.controller.form;

import lombok.RequiredArgsConstructor;
import net.risesoft.entity.form.Y9Form;
import net.risesoft.entity.form.Y9FormField;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.form.Y9FormFieldService;
import net.risesoft.service.form.Y9FormService;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/y9form", produces = MediaType.APPLICATION_JSON_VALUE)
public class FormRestController {

    private final Y9FormService y9FormService;

    private final Y9FormFieldService y9FormFieldService;

    /**
     * 删除表单绑定的字段
     *
     * @param id 字段id
     * @return Y9Result<String>
     */
    @PostMapping(value = "/deleteFormFieldBind")
    public Y9Result<String> deleteFormFieldBind(@RequestParam @NotBlank String id) {
        return y9FormFieldService.deleteFormFieldBind(id);
    }

    /**
     * 清空表单绑定的字段
     *
     * @param formId 表单id
     * @return Y9Result<String>
     */
    @PostMapping(value = "/deleteByFormId")
    public Y9Result<String> deleteByFormId(@RequestParam @NotBlank String formId) {
        return y9FormFieldService.deleteByFormId(formId);
    }

    /**
     * 获取表单信息
     *
     * @param id 表单id
     * @return
     */
    @GetMapping(value = "/getForm")
    public Y9Result<Map<String, Object>> getForm(@RequestParam String id) {
        Map<String, Object> map = new HashMap<>(16);
        Y9Form y9Form = y9FormService.findById(id);
        String json = y9FormService.getFormField(id);
        map.put("y9Form", y9Form);
        map.put("formField", json);
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 获取表单绑定的业务表字段列表
     *
     * @param formId 应用名称
     * @param page   页码
     * @param rows   条数
     * @return
     */
    @GetMapping(value = "/getFormBindFieldList")
    public Y9Page<Y9FormField> getFormBindFieldList(@RequestParam String formId, @RequestParam Integer page,
                                                    @RequestParam Integer rows) {
        Page<Y9FormField> pageList = y9FormFieldService.pageByFormId(formId, page, rows);
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), pageList.getContent(),
                "获取表单绑定的业务表字段列表成功");
    }

    /**
     * 复制选择的表单字段绑定信息
     *
     * @param systemName 表单系统名称
     * @param copyFormId 复制的表单id
     * @param tableName  选择当前事项系统下的业务表名称
     * @return
     */
    @PostMapping(value = "/copyFormAndFieldBind")
    public Y9Result<Object> copyFormAndFieldBind(@RequestParam String systemName, @RequestParam String systemCnName, @RequestParam String copyFormId,
                                                 @RequestParam String tableName) {
        return y9FormFieldService.copyFormAndFieldBind(systemName, systemCnName, copyFormId, tableName);
    }

    /**
     * 保存选择的表单字段绑定信息（解析formJson,插入新增的表单绑定字段）
     *
     * @param formId    表单的id
     * @param tableId   业务表 id
     * @param tableName 业务表名称
     * @param isAppend  是否追加字段组件
     * @param fieldJson 字段绑定信息
     * @return
     * @
     */
    @PostMapping(value = "/saveFormFieldBind")
    public Y9Result<String> saveFormFieldBind(@RequestParam String formId, @RequestParam String tableId,
                                              @RequestParam String tableName, @RequestParam Boolean isAppend, @RequestParam String fieldJson) {
        return y9FormFieldService.saveFormFieldBind(formId, tableId, tableName, isAppend, fieldJson);
    }

    /**
     * 获取表单列表
     *
     * @param systemName 应用名称
     * @param page       页码
     * @param rows       条数
     * @return
     */
    @GetMapping(value = "/getFormList")
    public Y9Page<Map<String, Object>> getFormList(@RequestParam(required = false) String systemName,
                                                   @RequestParam int page, @RequestParam int rows) {
        return y9FormService.pageFormList(systemName, page, rows);
    }

    /**
     * 保存表单信息
     *
     * @param form 表单信息
     * @return
     */
    @PostMapping(value = "/newOrModifyForm")
    public Y9Result<Object> newOrModifyForm(Y9Form form) {
        return y9FormService.saveOrUpdate(form);
    }

    /**
     * 删除表单
     *
     * @param ids 表单id
     * @return
     */
    @PostMapping(value = "/removeForm")
    public Y9Result<Object> removeForm(@RequestParam String ids) {
        return y9FormService.delete(ids);
    }

    /**
     * 保存表单字段绑定信息
     *
     * @param formId    表单id
     * @param fieldJson 字段绑定信息
     * @return
     */
    @PostMapping(value = "/saveFormField")
    public Y9Result<Object> saveFormField(@RequestParam String formId,
                                          @RequestParam(required = false) String fieldJson) {
        return y9FormService.saveFormField(formId, fieldJson);
    }

    /**
     * 保存表单JSON信息
     *
     * @param id       表单id
     * @param formJson 表单json
     * @return
     */
    @PostMapping(value = "/saveFormJson")
    public Y9Result<Object> saveFormJson(@RequestParam String id, @RequestParam(required = false) String formJson) {
        return y9FormService.saveFormJson(id, formJson);
    }
}
