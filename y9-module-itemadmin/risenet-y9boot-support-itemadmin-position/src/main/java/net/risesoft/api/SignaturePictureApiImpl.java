package net.risesoft.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import net.risesoft.api.itemadmin.SignaturePictureApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.entity.SignaturePicture;
import net.risesoft.model.itemadmin.SignaturePictureModel;
import net.risesoft.model.platform.Person;
import net.risesoft.service.SignaturePictureService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 签名图片接口
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/signaturePicture")
public class SignaturePictureApiImpl implements SignaturePictureApi {

    private final SignaturePictureService signaturePictureService;

    private final PersonApi personManager;

    /**
     * 删除签名图片
     * @param tenantId 租户id
     * @param id id
     */
    @Override
    @PostMapping(value = "/deleteById", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteById(String tenantId, String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        signaturePictureService.deleteById(id);
    }

    /**
     * 根据id获取签名图片
     * @param tenantId 租户id
     * @param id id
     * @return
     */
    @Override
    @GetMapping(value = "/findById", produces = MediaType.APPLICATION_JSON_VALUE)
    public SignaturePictureModel findById(String tenantId, String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        SignaturePicture sp = signaturePictureService.findById(id);
        SignaturePictureModel spModel = new SignaturePictureModel();
        if (null != sp) {
            Y9BeanUtil.copyProperties(sp, spModel);
        }
        return spModel;
    }

    /**
     * 根据人员id获取签名图片
     * @param tenantId 租户id
     * @param userId 人员id
     * @return
     */
    @Override
    @GetMapping(value = "/findByUserId", produces = MediaType.APPLICATION_JSON_VALUE)
    public SignaturePictureModel findByUserId(String tenantId, String userId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        SignaturePictureModel spModel = new SignaturePictureModel();
        SignaturePicture sp = signaturePictureService.findByUserId(userId);
        if (null != sp) {
            Y9BeanUtil.copyProperties(sp, spModel);
        }
        return spModel;
    }

    /**
     * 保存或更新签名图片
     * @param tenantId 租户id
     * @param userId 人员id
     * @param spJson spJson
     * @return
     */
    @Override
    @PostMapping(value = "/saveOrUpdate", produces = MediaType.APPLICATION_JSON_VALUE)
    public SignaturePictureModel saveOrUpdate(String tenantId, String userId, String spJson) {
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);

        ObjectMapper om = new ObjectMapper();
        try {
            JsonNode jn = om.readTree(spJson);
            om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            SignaturePictureModel spModel = new SignaturePictureModel();
            SignaturePicture sp = om.readValue(jn.toString(), SignaturePicture.class);
            SignaturePicture newsp = signaturePictureService.saveOrUpdate(sp);
            if (null != newsp) {
                Y9BeanUtil.copyProperties(newsp, spModel);
            }
            return spModel;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
