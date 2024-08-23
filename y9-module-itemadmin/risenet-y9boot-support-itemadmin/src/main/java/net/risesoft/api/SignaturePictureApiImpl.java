package net.risesoft.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.SignaturePictureApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.entity.SignaturePicture;
import net.risesoft.model.itemadmin.SignaturePictureModel;
import net.risesoft.model.platform.Person;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.SignaturePictureService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 签名图片接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/signaturePicture", produces = MediaType.APPLICATION_JSON_VALUE)
public class SignaturePictureApiImpl implements SignaturePictureApi {

    private final SignaturePictureService signaturePictureService;

    private final PersonApi personApi;

    /**
     * 删除签名图片信息
     *
     * @param tenantId 租户id
     * @param id id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> deleteById(@RequestParam String tenantId, @RequestParam String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        signaturePictureService.deleteById(id);
        return Y9Result.success();
    }

    /**
     * 根据id获取签名图片信息
     *
     * @param tenantId 租户id
     * @param id id
     * @return {@code Y9Result<SignaturePictureModel>} 通用请求返回对象 - data 是签名图片信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<SignaturePictureModel> findById(@RequestParam String tenantId, @RequestParam String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        SignaturePicture sp = signaturePictureService.findById(id);
        SignaturePictureModel spModel = new SignaturePictureModel();
        if (null != sp) {
            Y9BeanUtil.copyProperties(sp, spModel);
        }
        return Y9Result.success(spModel);
    }

    /**
     * 根据人员id获取签名图片信息
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @return {@code Y9Result<SignaturePictureModel>} 通用请求返回对象 - data 是签名图片信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<SignaturePictureModel> findByUserId(@RequestParam String tenantId, @RequestParam String userId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        SignaturePictureModel spModel = new SignaturePictureModel();
        SignaturePicture sp = signaturePictureService.findByUserId(userId);
        if (null != sp) {
            Y9BeanUtil.copyProperties(sp, spModel);
        }
        return Y9Result.success(spModel);
    }

    /**
     * 保存或更新签名图片信息
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param spJson spJson
     * @return {@code Y9Result<SignaturePictureModel>} 通用请求返回对象 - data 是签名图片信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<SignaturePictureModel> saveOrUpdate(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String spJson) {
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);

        ObjectMapper om = new ObjectMapper();
        try {
            JsonNode jn = om.readTree(spJson);
            om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            SignaturePictureModel spModel = new SignaturePictureModel();
            SignaturePicture sp = om.readValue(jn.toString(), SignaturePicture.class);
            SignaturePicture signaturePicture = signaturePictureService.saveOrUpdate(sp);
            if (null != signaturePicture) {
                Y9BeanUtil.copyProperties(signaturePicture, spModel);
            }
            return Y9Result.success(spModel);
        } catch (JsonProcessingException e) {
            LOGGER.error("保存或更新签名图片失败", e);
            return Y9Result.failure("保存或更新签名图片失败");
        }
    }
}
