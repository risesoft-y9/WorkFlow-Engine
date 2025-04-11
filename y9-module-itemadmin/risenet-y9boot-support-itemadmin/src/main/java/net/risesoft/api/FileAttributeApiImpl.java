package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.FileAttributeApi;
import net.risesoft.entity.FileAttribute;
import net.risesoft.model.itemadmin.FileAttributeModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.FileAttributeService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 文件属性信息接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/fileAttribute", produces = MediaType.APPLICATION_JSON_VALUE)
public class FileAttributeApiImpl implements FileAttributeApi {

    private final FileAttributeService fileAttributeService;

    /**
     * 根据pcode获取文件属性信息
     *
     * @param tenantId 租户ID
     * @param pcode 文件属性编码
     * @return {@link Y9Result<List<FileAttributeModel>>}
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<FileAttributeModel>> getFileAttribute(@RequestParam String tenantId, String pcode) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<FileAttribute> list = fileAttributeService.getFileAttribute(pcode);
        List<FileAttributeModel> fileAttributeModelList = new ArrayList<>();
        for (FileAttribute fileAttribute : list) {
            FileAttributeModel fileAttributeModel = new FileAttributeModel();
            Y9BeanUtil.copyProperties(fileAttribute, fileAttributeModel);
            fileAttributeModelList.add(fileAttributeModel);
        }
        return Y9Result.success(fileAttributeModelList);
    }

}
