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
import net.risesoft.entity.SignDeptDetail;
import net.risesoft.entity.opinion.OpinionSign;
import net.risesoft.enums.SignDeptDetailStatusEnum;
import net.risesoft.model.itemadmin.OpinionSignModel;
import net.risesoft.model.itemadmin.SignDeptDetailModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.SignDeptDetailService;
import net.risesoft.service.opinion.OpinionSignService;
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

    private final OpinionSignService opinionSignService;

    /**
     * 根据主键删除会签信息
     *
     * @param id 主键
     * @return Y9Result<Object>
     * @since 9.6.8
     */
    @Override
    public Y9Result<Object> deleteById(@RequestParam String id) {
        signDeptDetailService.deleteById(id);
        return Y9Result.success();
    }

    /**
     * 根据主键获取会签信息
     *
     * @param id 主键
     * @return Y9Result<SignDeptModel>
     * @since 9.6.8
     */
    @Override
    public Y9Result<SignDeptDetailModel> findById(String id) {
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
     * 根据流程序列号获取会签信息
     *
     * @param processSerialNumber 流程序列号
     * @return Y9Result<List<SignDeptModel>
     * @since 9.6.8
     */
    @Override
    public Y9Result<List<SignDeptDetailModel>> findByProcessSerialNumber(@RequestParam String processSerialNumber) {
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
     * 根据流程序列号和部门ID获取会签信息
     *
     * @param processSerialNumber 流程序列号
     * @param deptId 部门ID
     * @return Y9Result<SignDeptModel>
     * @since 9.6.8
     */
    @Override
    public Y9Result<SignDeptDetailModel> findByProcessSerialNumberAndDeptId4Latest(String processSerialNumber,
        String deptId) {
        SignDeptDetail signDeptDetail =
            signDeptDetailService.findByProcessSerialNumberAndDeptId4Latest(processSerialNumber, deptId);
        if (null != signDeptDetail) {
            SignDeptDetailModel model = new SignDeptDetailModel();
            Y9BeanUtil.copyProperties(signDeptDetail, model);
            return Y9Result.success(model);
        }
        return Y9Result.failure("未找到会签部门详情");
    }

    /**
     * 根据流程序列号和会签状态获取会签信息
     *
     * @param processSerialNumber 流程序列号
     * @param status 会签状态 {@link net.risesoft.enums.SignDeptDetailStatusEnum}
     * @return Y9Result<List<SignDeptModel>
     * @since 9.6.8
     */
    @Override
    public Y9Result<List<SignDeptDetailModel>> findByProcessSerialNumberAndStatus(
        @RequestParam String processSerialNumber, @RequestParam SignDeptDetailStatusEnum status) {
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
     * 根据主键恢复会签信息
     *
     * @param id 主键
     * @return Y9Result<Object>
     * @since 9.6.8
     */
    @Override
    public Y9Result<Object> recoverById(@RequestParam String id) {
        signDeptDetailService.recoverById(id);
        return Y9Result.success();
    }

    /**
     * 保存会签信息
     *
     * @param signDeptDetailModel 会签部门信息
     * @return Y9Result<Object>
     * @since 9.6.0
     */
    @Override
    public Y9Result<Object> saveOrUpdate(@RequestBody SignDeptDetailModel signDeptDetailModel) {
        SignDeptDetail signDeptDetail = new SignDeptDetail();
        Y9BeanUtil.copyProperties(signDeptDetailModel, signDeptDetail);
        signDeptDetailService.saveOrUpdate(signDeptDetail);
        return Y9Result.success();
    }

    /**
     * 更新会签文件存储id
     *
     * @param signId 会签id
     * @param fileStoreId 文件存储id
     * @return Y9Result<Object>
     * @since 9.6.0
     */
    @Override
    public Y9Result<Object> updateFileStoreId(String signId, String fileStoreId) {
        signDeptDetailService.updateFileStoreId(signId, fileStoreId);
        return Y9Result.success();
    }

}
