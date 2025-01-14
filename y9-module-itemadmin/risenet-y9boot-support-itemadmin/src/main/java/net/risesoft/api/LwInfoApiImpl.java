package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
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

}
