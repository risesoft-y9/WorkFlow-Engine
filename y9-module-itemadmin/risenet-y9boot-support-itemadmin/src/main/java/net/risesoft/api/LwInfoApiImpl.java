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

import net.risesoft.api.itemadmin.LwInfoApi;
import net.risesoft.entity.GwLwInfo;
import net.risesoft.model.itemadmin.LwInfoModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.GwLwInfoRepository;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 来文信息接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/lwInfo", produces = MediaType.APPLICATION_JSON_VALUE)
public class LwInfoApiImpl implements LwInfoApi {

    private final GwLwInfoRepository gwLwInfoRepository;

    /**
     * 删除来文信息
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> delLwInfo(String tenantId, String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        gwLwInfoRepository.deleteByProcessSerialNumber(processSerialNumber);
        return Y9Result.success();
    }

    /**
     * 获取来文信息列表
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<List<LwInfoModel>>} 通用请求返回对象 - data是附件列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<LwInfoModel>> findByProcessSerialNumber(@RequestParam String tenantId,
        @RequestParam String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<GwLwInfo> list = gwLwInfoRepository.findByProcessSerialNumber(processSerialNumber);
        List<LwInfoModel> modelList = new ArrayList<>();
        for (GwLwInfo gwLwInfo : list) {
            LwInfoModel model = new LwInfoModel();
            Y9BeanUtil.copyProperties(gwLwInfo, model);
            modelList.add(model);
        }
        return Y9Result.success(modelList);
    }

    /**
     * 保存来文信息
     *
     * @param tenantId 租户id
     * @param lwInfoModel 来文信息
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> saveLwInfo(@RequestParam String tenantId, @RequestBody LwInfoModel lwInfoModel) {
        Y9LoginUserHolder.setTenantId(tenantId);
        GwLwInfo gwLwInfo = new GwLwInfo();
        Y9BeanUtil.copyProperties(lwInfoModel, gwLwInfo);
        List<GwLwInfo> list = gwLwInfoRepository.findByProcessSerialNumber(gwLwInfo.getProcessSerialNumber());
        if (list.size() > 0) {
            gwLwInfo.setId(list.get(0).getId());
        }
        gwLwInfoRepository.save(gwLwInfo);
        return Y9Result.success();
    }

}
