package net.risesoft.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.AttachmentType;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.AttachmentTypeService;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/attachmentType", produces = MediaType.APPLICATION_JSON_VALUE)
public class AttachmentTypeRestController {

    private final AttachmentTypeService attachmentTypeService;

    /**
     * 获取附件类型
     *
     * @param id 附件类型id
     * @return
     */
    @GetMapping(value = "/getAttachmentType")
    public Y9Result<AttachmentType> getAttachmentType(@RequestParam String id) {
        AttachmentType attachmentType = attachmentTypeService.getById(id);
        return Y9Result.success(attachmentType, "获取成功");
    }

    /**
     * 获取附件类型列表
     * 
     * @return
     */
    @GetMapping(value = "/getAttachmentTypeList")
    public Y9Result<List<AttachmentType>> getAttachmentTypeList() {
        List<AttachmentType> list = attachmentTypeService.listAll();
        return Y9Result.success(list, "获取附件类型数据成功");
    }

    /**
     * 获取附件类型列表
     *
     * @param page 页码
     * @param rows 条数
     * @return
     */
    @GetMapping(value = "/list")
    public Y9Page<AttachmentType> list(@RequestParam Integer page, @RequestParam Integer rows) {
        Page<AttachmentType> pageList = attachmentTypeService.pageAll(page, rows);
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), pageList.getContent(),
            "获取列表成功");
    }

    /**
     * 删除附件类型
     *
     * @param id 附件类型id
     * @return
     */
    @PostMapping(value = "/remove")
    public Y9Result<String> remove(@RequestParam String id) {
        attachmentTypeService.remove(id);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 保存或更新附件类型
     *
     * @param attachmentType 附件类型信息
     * @return
     */
    @PostMapping(value = "/saveOrUpdate")
    public Y9Result<String> saveOrUpdate(AttachmentType attachmentType) {
        attachmentTypeService.saveOrUpdate(attachmentType);
        return Y9Result.successMsg("保存成功");
    }

}
