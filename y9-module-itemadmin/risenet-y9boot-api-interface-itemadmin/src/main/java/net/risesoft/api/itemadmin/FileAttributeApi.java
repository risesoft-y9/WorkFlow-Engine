package net.risesoft.api.itemadmin;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.FileAttributeModel;
import net.risesoft.pojo.Y9Result;

/**
 * 文件属性信息接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface FileAttributeApi {

    /**
     * 根据pcode获取文件属性信息
     *
     * @param tenantId 租户id
     * @param pcode 文件属性信息编码
     * @return {@code Y9Result<List<FileAttributeModel>>} 通用请求结果
     * @since 9.6.0
     */
    @GetMapping("/getFileAttribute")
    Y9Result<List<FileAttributeModel>> getFileAttribute(@RequestParam("tenantId") String tenantId,
        @RequestParam(value = "pcode", required = false) String pcode);
}
