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

import net.risesoft.api.itemadmin.AfterWorkInfoApi;
import net.risesoft.entity.AfterWorkInfo;
import net.risesoft.model.itemadmin.AfterWorkModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.AfterWorkInfoRepository;
import net.risesoft.service.AfterWorkInfoService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 核稿后工作事项接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/afterWorkInfo", produces = MediaType.APPLICATION_JSON_VALUE)
public class AfterWorkInfoApiImpl implements AfterWorkInfoApi {

    private final AfterWorkInfoRepository afterWorkInfoRepository;

    private final AfterWorkInfoService afterWorkInfoService;

    /**
     * 删除核稿后工作事项
     *
     * @param tenantId 租户id
     * @param id 核稿后工作事项id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> delAfterWork(@RequestParam String tenantId, @RequestParam String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        afterWorkInfoService.deleteById(id);
        return Y9Result.success();
    }

    /**
     * 获取核稿后工作事项
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<List<AfterWorkModel>>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<AfterWorkModel>> findByProcessSerialNumber(@RequestParam String tenantId,
        @RequestParam String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<AfterWorkInfo> list =
            afterWorkInfoRepository.findByProcessSerialNumberOrderByRecordTimeDesc(processSerialNumber);
        List<AfterWorkModel> modelList = new ArrayList<>();
        for (AfterWorkInfo info : list) {
            AfterWorkModel model = new AfterWorkModel();
            Y9BeanUtil.copyProperties(info, model);
            modelList.add(model);
        }
        return Y9Result.success(modelList);
    }

    /**
     * 保存核稿后工作事项
     *
     * @param tenantId 租户id
     * @param afterWorkModel {@code AfterWorkModel} 核稿后工作事项
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> saveOrUpdate(@RequestParam String tenantId, @RequestBody AfterWorkModel afterWorkModel) {
        Y9LoginUserHolder.setTenantId(tenantId);
        AfterWorkInfo afterWorkInfo = new AfterWorkInfo();
        Y9BeanUtil.copyProperties(afterWorkModel, afterWorkInfo);
        afterWorkInfoService.save(afterWorkInfo);
        return Y9Result.success();
    }

}
