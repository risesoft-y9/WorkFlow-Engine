package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.MergeFileApi;
import net.risesoft.entity.MergeFile;
import net.risesoft.model.itemadmin.MergeFileModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.MergeFileRepository;
import net.risesoft.service.MergeFileService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 合并文件接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/mergeFile", produces = MediaType.APPLICATION_JSON_VALUE)
public class MergeFileApiImpl implements MergeFileApi {

    private final MergeFileRepository mergeFileRepository;

    private final MergeFileService mergeFileService;

    /**
     * 删除合并文件
     *
     * @param tenantId 租户ID
     * @param ids 文件ID
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.0
     */
    @Override
    public Y9Result<Object> delMergeFile(@RequestParam String tenantId, @RequestParam String[] ids) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return mergeFileService.delMergeFile(ids);
    }

    /**
     * 根据ID获取合并文件列表
     *
     * @param tenantId 租户ID
     * @param ids 文件ID列表
     * @return {@code Y9Result<List<MergeFileModel>>} 通用请求返回对象 - data
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<MergeFileModel>> findByIds(@RequestParam String tenantId, @RequestParam String[] ids) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<MergeFile> list = mergeFileRepository.findByIdIn(ids);
        List<MergeFileModel> res_list = new ArrayList<>();
        for (MergeFile mergeFile : list) {
            MergeFileModel mergeFileModel = new MergeFileModel();
            Y9BeanUtil.copyProperties(mergeFile, mergeFileModel);
            res_list.add(mergeFileModel);
        }
        return Y9Result.success(res_list);
    }

    /**
     * 根据ID获取合并文件
     *
     * @param tenantId 租户ID
     * @param id 文件ID
     * @return {@code Y9Result<MergeFileModel>} 通用请求返回对象 - data
     * @since 9.6.0
     */
    @Override
    public Y9Result<MergeFileModel> getMergeFile(String tenantId, String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        MergeFile mergeFile = mergeFileRepository.findById(id).orElse(null);
        if (mergeFile != null) {
            MergeFileModel mergeFileModel = new MergeFileModel();
            Y9BeanUtil.copyProperties(mergeFile, mergeFileModel);
            return Y9Result.success(mergeFileModel);
        }
        return Y9Result.success(null);
    }

    /**
     * 获取合并文件列表
     *
     * @param tenantId 租户ID
     * @param personId 用户ID
     * @param processSerialNumber 流程编号
     * @param listType 列表类型
     * @param fileType 文件类型
     * @return {@code Y9Result<List<MergeFileModel>>} 通用请求返回对象 - data
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<MergeFileModel>> getMergeFileList(@RequestParam String tenantId, @RequestParam String personId,
        String processSerialNumber, @RequestParam String listType, @RequestParam String fileType) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<MergeFile> list = mergeFileService.getMergeFileList(personId, processSerialNumber, listType, fileType);
        List<MergeFileModel> res_list = new ArrayList<>();
        for (MergeFile mergeFile : list) {
            MergeFileModel mergeFileModel = new MergeFileModel();
            Y9BeanUtil.copyProperties(mergeFile, mergeFileModel);
            res_list.add(mergeFileModel);
        }
        return Y9Result.success(res_list);
    }

    /**
     * 根据源文件ID获取合并文件列表
     *
     * @param tenantId 租户ID
     * @param sourceFileId 源文件ID
     * @return {@code Y9Result<List<MergeFileModel>>} 通用请求返回对象 - data
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<MergeFileModel>> getOfdFile(String tenantId, String sourceFileId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<MergeFile> list = mergeFileRepository.findBySourceFileId(sourceFileId);
        List<MergeFileModel> res_list = new ArrayList<>();
        for (MergeFile mergeFile : list) {
            MergeFileModel mergeFileModel = new MergeFileModel();
            Y9BeanUtil.copyProperties(mergeFile, mergeFileModel);
            res_list.add(mergeFileModel);
        }
        return Y9Result.success(res_list);
    }

    /**
     * 保存合并文件
     *
     * @param tenantId 租户ID
     * @param mergeFileModel 合并文件对象
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.0
     */
    @Override
    public Y9Result<Object> saveMergeFile(@RequestParam String tenantId, @RequestBody MergeFileModel mergeFileModel) {
        Y9LoginUserHolder.setTenantId(tenantId);
        MergeFile mergeFile = new MergeFile();
        Y9BeanUtil.copyProperties(mergeFileModel, mergeFile);
        mergeFileService.saveMergeFile(mergeFile);
        return Y9Result.success(mergeFile);
    }
}
