package net.risesoft.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.TmhPictureApi;
import net.risesoft.entity.TmhPicture;
import net.risesoft.model.itemadmin.TmhPictureModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.TmhPictureRepository;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 条码号图片接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/tmhPicture", produces = MediaType.APPLICATION_JSON_VALUE)
public class TmhPictureApiImpl implements TmhPictureApi {

    private final TmhPictureRepository tmhPictureRepository;

    /**
     * 条码号图片通过流程编号查询
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param tmhType 条码号类型 1：条形码图片 2：清样二维码
     * @return Y9Result<TmhPictureModel> 统一返回结果
     * @since 9.6.6
     */
    @Override
    public Y9Result<TmhPictureModel> findByProcessSerialNumber(@RequestParam String tenantId,
        @RequestParam String processSerialNumber, @RequestParam String tmhType) {
        Y9LoginUserHolder.setTenantId(tenantId);
        TmhPicture tmhPicture = tmhPictureRepository.findByProcessSerialNumberAndTmhType(processSerialNumber, tmhType);
        if (tmhPicture != null) {
            TmhPictureModel tmhPictureModel = new TmhPictureModel();
            Y9BeanUtil.copyProperties(tmhPicture, tmhPictureModel);
            return Y9Result.success(tmhPictureModel);
        }
        return Y9Result.success(null);
    }

    /**
     * 条码号图片保存或更新
     * 
     * @param tenantId 租户id
     * @param tmhPictureModel 条码号图片模型
     * @return Y9Result<Object> 统一返回结果
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> saveOrUpdate(@RequestParam String tenantId, @RequestBody TmhPictureModel tmhPictureModel) {
        Y9LoginUserHolder.setTenantId(tenantId);
        TmhPicture tmhPicture = new TmhPicture();
        Y9BeanUtil.copyProperties(tmhPictureModel, tmhPicture);
        TmhPicture tmhPictureDb = tmhPictureRepository
            .findByProcessSerialNumberAndTmhType(tmhPicture.getProcessSerialNumber(), tmhPictureModel.getTmhType());
        if (tmhPictureDb != null) {
            tmhPicture.setId(tmhPictureDb.getId());
        }
        tmhPictureRepository.save(tmhPicture);
        return Y9Result.success();
    }
}
