package net.risesoft.controller.gfg;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.apache.commons.io.FilenameUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.LeaderOpinionApi;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.LeaderOpinionModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;

/**
 * 领导批示接口
 *
 * @author qinman
 * @date 2024/11/07
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/leaderOpinion", produces = MediaType.APPLICATION_JSON_VALUE)
public class LeaderOpinionRestController {

    private final LeaderOpinionApi leaderOpinionApi;

    private final Y9FileStoreService y9FileStoreService;

    /**
     * 删除领导批示
     *
     * @param id 主键id
     * @return Y9Result<String>
     */
    @PostMapping(value = "/deleteById")
    public Y9Result<String> deleteById(@RequestParam @NotBlank String id) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        leaderOpinionApi.deleteById(tenantId, id);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 获取领导批示列表
     *
     * @param processSerialNumber 流程实例id
     * @return Y9Result<List < PreWorkModel>>
     */
    @GetMapping(value = "/list")
    public Y9Result<List<LeaderOpinionModel>> list(@RequestParam @NotBlank String processSerialNumber) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            return leaderOpinionApi.findByProcessSerialNumber(tenantId, processSerialNumber);
        } catch (Exception e) {
            LOGGER.error("获取领导批示列表异常", e);
        }
        return Y9Result.failure("获取领导批示失败");
    }

    /**
     * 新增领导批示
     *
     * @return Y9Result<PreWorkModel>
     */
    @GetMapping(value = "/getNew")
    public Y9Result<LeaderOpinionModel> getNew() {
        LeaderOpinionModel leaderOpinionModel = new LeaderOpinionModel();
        leaderOpinionModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        return Y9Result.success(leaderOpinionModel);
    }

    /**
     * 保存领导批示信息
     *
     * @param leaderOpinionModel 领导批示信息
     * @return Y9Result<Object>
     */
    @PostMapping(value = "/saveOrUpdate")
    public Y9Result<Object> saveOrUpdate(@Valid LeaderOpinionModel leaderOpinionModel) {
        try {
            UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
            leaderOpinionModel.setPersonName(userInfo.getName());
            leaderOpinionModel.setPersonId(userInfo.getPersonId());
            return leaderOpinionApi.saveOrUpdate(Y9LoginUserHolder.getTenantId(), leaderOpinionModel);
        } catch (Exception e) {
            LOGGER.error("保存领导批示异常", e);
        }
        return Y9Result.failure("保存失败");
    }

    /**
     * 上传附件
     *
     * @param file 文件
     * @param processSerialNumber 流程编号
     * @return Y9Result<String>
     */
    @PostMapping(value = "/upload")
    public Y9Result<Map<String, Object>> upload(MultipartFile file,
        @RequestParam @NotBlank String processSerialNumber) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> map = new HashMap<>();
        try {
            String originalFilename = file.getOriginalFilename();
            String fileName = FilenameUtils.getName(originalFilename);
            String fullPath =
                "/" + Y9Context.getSystemName() + "/" + tenantId + "/leaderOpinion/" + processSerialNumber;
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(file, fullPath, fileName);
            map.put("fileName", fileName);
            map.put("fileStoreId", y9FileStore.getId());
            return Y9Result.success(map);
        } catch (Exception e) {
            LOGGER.error("上传失败", e);
        }
        return Y9Result.failure("上传失败");
    }
}
