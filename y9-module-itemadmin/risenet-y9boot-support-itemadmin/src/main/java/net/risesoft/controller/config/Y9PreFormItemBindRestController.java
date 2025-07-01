package net.risesoft.controller.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import net.risesoft.controller.vo.Y9FormVO;
import net.risesoft.entity.Y9PreFormItemBind;
import net.risesoft.entity.form.Y9Form;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.form.Y9FormRepository;
import net.risesoft.service.config.Y9PreFormItemBindService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/preFormBind", produces = MediaType.APPLICATION_JSON_VALUE)
public class Y9PreFormItemBindRestController {

    private final Y9PreFormItemBindService y9PreFormItemBindService;

    private final Y9FormRepository y9FormRepository;

    /**
     * 删除绑定表单
     *
     * @param id 绑定id
     * @return
     */
    @PostMapping(value = "/deleteBind")
    public Y9Result<String> deleteBind(@RequestParam String id) {
        return y9PreFormItemBindService.delete(id);
    }

    /**
     * 获取绑定的表单
     *
     * @param itemId 事项id
     * @return
     */
    @GetMapping(value = "/getBindList")
    public Y9Result<Y9PreFormItemBind> getBindList(@RequestParam String itemId) {
        Y9PreFormItemBind bind = y9PreFormItemBindService.findByItemId(itemId);
        if (bind != null) {
            Y9Form form = y9FormRepository.findById(bind.getFormId()).orElse(null);
            bind.setFormName(form != null ? form.getFormName() : "表单不存在");
        }
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
    @GetMapping(value = "/getFormList")
    public Y9Result<List<Y9FormVO>> getFormList(@RequestParam String itemId, @RequestParam String systemName,
        @RequestParam(required = false) String formName) {
        List<Y9FormVO> listmap = new ArrayList<>();
        List<Y9Form> list = y9FormRepository.findBySystemNameAndFormNameLike(systemName, "%" + formName + "%");
        Y9PreFormItemBind bind = y9PreFormItemBindService.findByItemId(itemId);
        for (Y9Form y9Form : list) {
            boolean isBind = bind != null && bind.getFormId().equals(y9Form.getId());
            if (!isBind) {
                Y9FormVO form = new Y9FormVO();
                form.setFormId(y9Form.getId());
                form.setFormName(y9Form.getFormName());
                listmap.add(form);
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
    @PostMapping(value = "/saveBindForm")
    public Y9Result<String> saveBindForm(String itemId, String formId, String formName) {
        return y9PreFormItemBindService.saveBindForm(itemId, formId, formName);
    }

}
