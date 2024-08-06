package net.risesoft.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.ExtendedContent;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ExtendedContentService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/extendedContent", produces = MediaType.APPLICATION_JSON_VALUE)
public class ExtendedContentController {

    private final ExtendedContentService extendedContentService;

    /**
     * 是否填写内容
     *
     * @param processSerialNumber 流程编号
     * @param category 分类
     * @return Map<String, Object>
     */
    @RequestMapping(value = "/checkSignContent")
    public Y9Result<Boolean> checkSignContent(@RequestParam(required = false) String category,
        @RequestParam(required = false) String processSerialNumber) {
        try {
            int count = extendedContentService.countByProcSerialNumberAndCategory(processSerialNumber, category);
            return Y9Result.success(count > 0);
        } catch (Exception e) {
            LOGGER.error("是否填写内容", e);
            return Y9Result.failure("是否填写内容");
        }
    }

    /**
     * 获取内容列表
     *
     * @param processSerialNumber 流程编号
     * @param itembox 列表类型
     * @param taskId 任务ID
     * @param category 分类
     * @return List<Map < String, Object>>
     */
    @RequestMapping(value = "/contentList")
    public Y9Result<List<Map<String, Object>>> contentList(@RequestParam String processSerialNumber,
        @RequestParam String itembox, @RequestParam String taskId, @RequestParam String category) {
        List<Map<String, Object>> contentList =
            extendedContentService.listContents(processSerialNumber, taskId, itembox, category);
        return Y9Result.success(contentList);
    }

    /**
     * 删除内容
     *
     * @param id 内容ID
     * @return Map<String, Object>
     */
    @RequestMapping(value = "/delete")
    public Y9Result<Object> delete(@RequestParam String id) {
        return extendedContentService.delete(id);
    }

    /**
     * 保存内容
     *
     * @param content 扩展内容实体类
     * @return Map<String, Object>
     */
    @PostMapping(value = "/saveOrUpdate")
    public Y9Result<Object> saveOrUpdate(ExtendedContent content) {
        return extendedContentService.saveOrUpdate(content);
    }
}