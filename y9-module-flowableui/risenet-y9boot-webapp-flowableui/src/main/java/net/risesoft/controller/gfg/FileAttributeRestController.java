package net.risesoft.controller.gfg;

import java.util.List;


import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.FileAttributeApi;
import net.risesoft.model.itemadmin.FileAttributeModel;
import net.risesoft.pojo.Y9Result;

/**
 * 待办，在办，办结列表
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/mobile/gfg/fileAttribute", produces = MediaType.APPLICATION_JSON_VALUE)
public class FileAttributeRestController {

    private final FileAttributeApi fileAttributeApi;

    /**
     * 获取文件属性
     *
     * @param tenantId 租户id
     * @param pcode 文件属性编码
     * @return {@link Y9Result<List<FileAttributeModel>>}
     */
    @GetMapping(value = "/getFileAttribute")
    public Y9Result<List<FileAttributeModel>> getFileAttribute(@RequestParam String tenantId,
        @RequestParam(required = false) String pcode) {
        return fileAttributeApi.getFileAttribute(tenantId, pcode);
    }

}