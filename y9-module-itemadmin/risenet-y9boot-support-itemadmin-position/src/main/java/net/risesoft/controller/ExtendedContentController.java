package net.risesoft.controller;

import lombok.RequiredArgsConstructor;
import net.risesoft.entity.ExtendedContent;
import net.risesoft.service.ExtendedContentService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/extendedContent")
public class ExtendedContentController {

    private final ExtendedContentService extendedContentService;

    /**
     * 是否填写内容
     *
     * @param taskId
     * @param processSerialNumber
     * @return
     */
    @RequestMapping(value = "/checkSignContent")
    public Map<String, Object> checkSignContent(@RequestParam(required = false) String taskId,
        @RequestParam(required = false) String category, @RequestParam(required = false) String processSerialNumber) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            int count = 0;
            count = extendedContentService.findByProcSerialNumberAndCategory(processSerialNumber, category);
            if (count > 0) {
                map.put("checkSignContent", true);
            } else {
                map.put("checkSignContent", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 获取内容列表
     *
     * @param processSerialNumber
     * @param itembox
     * @param taskId
     * @param category
     * @return
     */
    @RequestMapping(value = "/contentList")
    public List<Map<String, Object>> contentList(@RequestParam String processSerialNumber, @RequestParam String itembox,
        @RequestParam String taskId, @RequestParam String category) {
        List<Map<String, Object>> listMap =
            extendedContentService.contentList(processSerialNumber, taskId, itembox, category);
        return listMap;
    }

    /**
     * 删除内容
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete")
    public Map<String, Object> delete(@RequestParam String id) {
        Map<String, Object> map = extendedContentService.delete(id);
        return map;
    }

    /**
     * 新增编辑内容
     *
     * @param processSerialNumber
     * @param processInstanceId
     * @param taskId
     * @param category
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(value = "/newOrModify/content")
    public String newOrModifyContent(@RequestParam String processSerialNumber, @RequestParam String taskId,
        @RequestParam String category, @RequestParam String id, Model model) {
        model = extendedContentService.newOrModifyContent(processSerialNumber, taskId, category, id, model);
        return "opinion/extendedContent";
    }

    /**
     * 保存内容
     *
     * @param content
     * @return
     */
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
    public Map<String, Object> saveOrUpdate(ExtendedContent content) {
        Map<String, Object> map = extendedContentService.saveOrUpdate(content);
        return map;
    }
}