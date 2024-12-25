package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.SignDeptDetailApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.entity.OpinionSign;
import net.risesoft.entity.SignDeptDetail;
import net.risesoft.model.itemadmin.OpinionSignModel;
import net.risesoft.model.itemadmin.SignDeptDetailModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.OpinionSignService;
import net.risesoft.service.SignDeptDetailService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 会签信息接口
 *
 * @author qinman
 * @date 2024/12/13
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/signDeptDetail", produces = MediaType.APPLICATION_JSON_VALUE)
public class SignDeptDetailApiImpl implements SignDeptDetailApi {

    private final SignDeptDetailService signDeptDetailService;

    private final OrgUnitApi orgUnitApi;

    private final OpinionSignService opinionSignService;

    /**
     * 根据主键删除会签信息
     *
     * @param tenantId 租户ID
     * @param id 主键
     * @return Y9Result<Object>
     * @since 9.6.8
     */
    @Override
    public Y9Result<Object> deleteById(@RequestParam String tenantId, @RequestParam String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        signDeptDetailService.deleteById(id);
        return Y9Result.success();
    }

    @Override
    public Y9Result<SignDeptDetailModel> findByProcessSerialNumberAndDeptId4Latest(String tenantId,
        String processSerialNumber, String deptId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        SignDeptDetail signDeptDetail =
            signDeptDetailService.findByProcessSerialNumberAndDeptId4Latest(processSerialNumber, deptId);
        if (null != signDeptDetail) {
            SignDeptDetailModel model = new SignDeptDetailModel();
            Y9BeanUtil.copyProperties(signDeptDetail, model);
            return Y9Result.success(model);
        }
        return Y9Result.failure("未找到会签部门详情");
    }

    @Override
    public Y9Result<SignDeptDetailModel> findById(String tenantId, String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        SignDeptDetail signDeptDetail = signDeptDetailService.findById(id);
        if (null != signDeptDetail) {
            SignDeptDetailModel model = new SignDeptDetailModel();
            Y9BeanUtil.copyProperties(signDeptDetail, model);
            List<OpinionSign> opinionList = opinionSignService.findBySignDeptDetailId(model.getId());
            List<OpinionSignModel> osModelList = new ArrayList<>();
            opinionList.forEach(os -> {
                OpinionSignModel osModel = new OpinionSignModel();
                Y9BeanUtil.copyProperties(os, osModel);
                osModelList.add(osModel);
            });
            model.setOpinionList(osModelList);

            return Y9Result.success(model);
        }
        return Y9Result.failure("未找到会签部门详情");
    }

    /**
     * 根据流程序列号和会签状态获取会签信息
     *
     * @param tenantId 租户ID
     * @param processSerialNumber 流程序列号
     * @param status 会签状态 {@link net.risesoft.enums.SignDeptDetailStatusEnum}
     * @return Y9Result<List<SignDeptModel>
     * @since 9.6.8
     */
    @Override
    public Y9Result<List<SignDeptDetailModel>> findByProcessSerialNumberAndStatus(@RequestParam String tenantId,
        @RequestParam String processSerialNumber, @RequestParam int status) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<SignDeptDetail> list =
            signDeptDetailService.findByProcessSerialNumberAndStatus(processSerialNumber, status);
        List<SignDeptDetailModel> modelList = new ArrayList<>();
        for (SignDeptDetail signDeptDetail : list) {
            SignDeptDetailModel model = new SignDeptDetailModel();
            Y9BeanUtil.copyProperties(signDeptDetail, model);
            List<OpinionSign> opinionList = opinionSignService.findBySignDeptDetailId(model.getId());
            List<OpinionSignModel> osModelList = new ArrayList<>();
            opinionList.forEach(os -> {
                OpinionSignModel osModel = new OpinionSignModel();
                Y9BeanUtil.copyProperties(os, osModel);
                osModelList.add(osModel);
            });
            model.setOpinionList(osModelList);
            modelList.add(model);
        }
        return Y9Result.success(modelList);
    }

    /**
     * 根据流程序列号获取会签信息
     *
     * @param tenantId 租户ID
     * @param processSerialNumber 流程序列号
     * @return Y9Result<List<SignDeptModel>
     * @since 9.6.8
     */
    @Override
    public Y9Result<List<SignDeptDetailModel>> findByProcessSerialNumber(@RequestParam String tenantId,
        @RequestParam String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<SignDeptDetail> list = signDeptDetailService.findByProcessSerialNumber(processSerialNumber);
        List<SignDeptDetailModel> modelList = new ArrayList<>();
        for (SignDeptDetail signDeptDetail : list) {
            SignDeptDetailModel model = new SignDeptDetailModel();
            Y9BeanUtil.copyProperties(signDeptDetail, model);
            modelList.add(model);
        }
        return Y9Result.success(modelList);
    }

    /**
     * 保存会签信息
     *
     * @param tenantId 租户ID
     * @param positionId 岗位id
     * @param signDeptDetailModel 会签部门信息
     * @return Y9Result<Object>
     * @since 9.6.0
     */
    @Override
    public Y9Result<Object> saveOrUpdate(@RequestParam String tenantId, @RequestParam String positionId,
        @RequestBody SignDeptDetailModel signDeptDetailModel) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, positionId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        SignDeptDetail signDeptDetail = new SignDeptDetail();
        Y9BeanUtil.copyProperties(signDeptDetailModel, signDeptDetail);
        signDeptDetailService.saveOrUpdate(signDeptDetail);
        return Y9Result.success();
    }
}
