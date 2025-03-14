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

import net.risesoft.api.itemadmin.LeaderOpinionApi;
import net.risesoft.entity.LeaderOpinion;
import net.risesoft.model.itemadmin.LeaderOpinionModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.LeaderOpinionService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 领导批示接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/leaderOpinion", produces = MediaType.APPLICATION_JSON_VALUE)
public class LeaderOpinionApiImpl implements LeaderOpinionApi {

    private final LeaderOpinionService leaderOpinionService;

    /**
     * 删除领导批示
     *
     * @param tenantId 租户id
     * @param id 领导批示id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> deleteById(@RequestParam String tenantId, @RequestParam String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        leaderOpinionService.deleteById(id);
        return Y9Result.success();
    }

    @Override
    public Y9Result<LeaderOpinionModel> findById(@RequestParam String tenantId, @RequestParam String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        leaderOpinionService.deleteById(id);
        return Y9Result.success();
    }

    /**
     * 获取领导批示
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<List<PreWorkModel>>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<LeaderOpinionModel>> findByProcessSerialNumber(@RequestParam String tenantId,
        @RequestParam String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<LeaderOpinion> list = leaderOpinionService.findByProcessSerialNumber(processSerialNumber);
        List<LeaderOpinionModel> modelList = new ArrayList<>();
        for (LeaderOpinion lo : list) {
            LeaderOpinionModel model = new LeaderOpinionModel();
            Y9BeanUtil.copyProperties(lo, model);
            modelList.add(model);
        }
        return Y9Result.success(modelList);
    }

    /**
     * 保存领导批示
     *
     * @param tenantId 租户id
     * @param leaderOpinionModel 领导批示
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> saveOrUpdate(@RequestParam String tenantId,
        @RequestBody LeaderOpinionModel leaderOpinionModel) {
        Y9LoginUserHolder.setTenantId(tenantId);
        LeaderOpinion leaderOpinion = new LeaderOpinion();
        Y9BeanUtil.copyProperties(leaderOpinionModel, leaderOpinion);
        leaderOpinionService.save(leaderOpinion);
        return Y9Result.success();
    }

}
