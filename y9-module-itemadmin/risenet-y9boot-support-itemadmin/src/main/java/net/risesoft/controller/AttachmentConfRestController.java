package net.risesoft.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.attachment.Attachment;
import net.risesoft.entity.attachment.AttachmentConf;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.AttachmentConfService;
import net.risesoft.util.CommentUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/attachmentConf", produces = MediaType.APPLICATION_JSON_VALUE)
public class AttachmentConfRestController {

    private final AttachmentConfService attachmentConfService;

    /**
     * 根据唯一标示查找附件配置
     *
     * @param attachmentType 唯一标示
     * @return
     */
    @GetMapping(value = "/listByAttachmentType")
    public Y9Result<List<AttachmentConf>> listByAttachmentType(@RequestParam String attachmentType,
        @RequestParam String configType) {
        List<AttachmentConf> list =
            attachmentConfService.listByAttachmentType(attachmentType, Integer.parseInt(configType));
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 获取附件表字段
     *
     * @return
     */
    @GetMapping(value = "/getColumns")
    public Y9Result<List<Map<String, Object>>> getColumns() {
        List<Map<String, Object>> list = CommentUtil.getEntityFieldList(Attachment.class);
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 删除配置
     *
     * @param ids 视图id
     */
    @PostMapping(value = "/removeConfig")
    public Y9Result<String> removeConfig(@RequestParam String[] ids) {
        attachmentConfService.removeAttachmentConfs(ids);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 保存或者修改
     *
     * @param attachmentConf 附件配置
     * @return
     */
    @PostMapping(value = "/saveOrUpdate")
    public Y9Result<String> saveOrUpdate(AttachmentConf attachmentConf) {
        attachmentConfService.saveOrUpdate(attachmentConf);
        return Y9Result.successMsg("保存成功");
    }

    /**
     * 保存排序
     *
     * @param idAndTabIndexs 视图id和排序索引
     */
    @PostMapping(value = "/saveOrder")
    public Y9Result<String> saveOrder(@RequestParam String[] idAndTabIndexs) {
        attachmentConfService.updateOrder(idAndTabIndexs);
        return Y9Result.successMsg("保存成功");
    }
}
