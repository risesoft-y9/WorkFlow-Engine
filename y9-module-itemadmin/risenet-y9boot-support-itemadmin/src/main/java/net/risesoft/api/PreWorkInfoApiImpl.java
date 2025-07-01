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

import net.risesoft.api.itemadmin.PreWorkInfoApi;
import net.risesoft.entity.PreWorkInfo;
import net.risesoft.model.itemadmin.PreWorkModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.PreWorkInfoRepository;
import net.risesoft.service.PreWorkInfoService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 前期工作事项接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/preWorkInfo", produces = MediaType.APPLICATION_JSON_VALUE)
public class PreWorkInfoApiImpl implements PreWorkInfoApi {

    private final PreWorkInfoRepository preWorkInfoRepository;

    private final PreWorkInfoService preWorkInfoService;

    /**
     * 删除前期工作事项
     *
     * @param tenantId 租户id
     * @param id 前期工作事项id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> delPreWork(@RequestParam String tenantId, @RequestParam String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        preWorkInfoService.deleteById(id);
        return Y9Result.success();
    }

    /**
     * 获取前期工作事项
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<List<PreWorkModel>>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<PreWorkModel>> findByProcessSerialNumber(@RequestParam String tenantId,
        @RequestParam String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<PreWorkInfo> list =
            preWorkInfoRepository.findByProcessSerialNumberOrderByRecordTimeDesc(processSerialNumber);
        List<PreWorkModel> modelList = new ArrayList<>();
        for (PreWorkInfo info : list) {
            PreWorkModel model = new PreWorkModel();
            Y9BeanUtil.copyProperties(info, model);
            modelList.add(model);
        }
        return Y9Result.success(modelList);
    }

    /**
     * 保存前期工作事项
     *
     * @param tenantId 租户id
     * @param preWorkModel 前期工作事项
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> saveOrUpdate(@RequestParam String tenantId, @RequestBody PreWorkModel preWorkModel) {
        Y9LoginUserHolder.setTenantId(tenantId);
        PreWorkInfo preWorkInfo = new PreWorkInfo();
        Y9BeanUtil.copyProperties(preWorkModel, preWorkInfo);
        preWorkInfoService.save(preWorkInfo);
        return Y9Result.success();
    }

}
