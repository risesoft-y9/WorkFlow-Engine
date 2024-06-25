package net.risesoft.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.ExtendedContent;
import net.risesoft.service.ExtendedContentService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/extendedContent")
public class ExtendedContentController {

    private final ExtendedContentService extendedContentService;

    /**
     * 是否填写内容
     *
     * @param processSerialNumber 流程序列号
     * @param category 分类
     * @return Map<String, Object>
     */
    @RequestMapping(value = "/checkSignContent")
    public Map<String, Object> checkSignContent(@RequestParam(required = false) String category,
        @RequestParam(required = false) String processSerialNumber) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            int count = extendedContentService.findByProcSerialNumberAndCategory(processSerialNumber, category);
            if (count > 0) {
                map.put("checkSignContent", true);
            } else {
                map.put("checkSignContent", false);
            }
        } catch (Exception e) {
            LOGGER.error("是否填写内容", e);
        }
        return map;
    }

    /**
     * 获取内容列表
     *
     * @param processSerialNumber 流程序列号
     * @param itembox 列表类型
     * @param taskId 任务ID
     * @param category 分类
     * @return List<Map < String, Object>>
     */
    @RequestMapping(value = "/contentList")
    public List<Map<String, Object>> contentList(@RequestParam String processSerialNumber, @RequestParam String itembox,
        @RequestParam String taskId, @RequestParam String category) {
        return extendedContentService.contentList(processSerialNumber, taskId, itembox, category);
    }

    /**
     * 删除内容
     *
     * @param id 内容ID
     * @return Map<String, Object>
     */
    @RequestMapping(value = "/delete")
    public Map<String, Object> delete(@RequestParam String id) {
        return extendedContentService.delete(id);
    }

    /**
     * 保存内容
     *
     * @param content 扩展内容实体类
     * @return Map<String, Object>
     */
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
    public Map<String, Object> saveOrUpdate(ExtendedContent content) {
        return extendedContentService.saveOrUpdate(content);
    }
}