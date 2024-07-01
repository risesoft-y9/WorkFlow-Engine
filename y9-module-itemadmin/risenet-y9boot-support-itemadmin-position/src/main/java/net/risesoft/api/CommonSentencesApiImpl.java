package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.CommonSentencesApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.entity.CommonSentences;
import net.risesoft.model.itemadmin.CommonSentencesModel;
import net.risesoft.model.platform.Person;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.CommonSentencesService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 常用语接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/commonSentences")
public class CommonSentencesApiImpl implements CommonSentencesApi {

    private final CommonSentencesService commonSentencesService;

    private final PersonApi personManager;

    /**
     * 删除常用语
     *
     * @param tenantId 租户id
     * @param id 常用语id
     * @return Y9Result<Object>
     */
    @Override
    public Y9Result<Object> delete(String tenantId, String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        commonSentencesService.deleteById(id);
        return Y9Result.success();
    }

    /**
     * 获取常用语
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @return Y9Result<List<CommonSentencesModel>>
     */
    @Override
    public Y9Result<List<CommonSentencesModel>> listSentencesService(String tenantId, String userId) {
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);
        List<CommonSentences> list = commonSentencesService.listSentencesService();
        List<CommonSentencesModel> res_list = new ArrayList<>();
        for (CommonSentences item : list) {
            CommonSentencesModel model = new CommonSentencesModel();
            Y9BeanUtil.copyProperties(item, model);
            res_list.add(model);
        }
        return Y9Result.success(res_list);
    }

    /**
     * 根据排序号tabIndex删除常用语
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param tabIndex 排序号
     * @return Y9Result<Object>
     */
    @Override
    public Y9Result<Object> removeCommonSentences(String tenantId, String userId, int tabIndex) {
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);
        commonSentencesService.removeCommonSentences(tabIndex);
        return Y9Result.success();
    }

    /**
     * 清空常用语使用次数
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @return Y9Result<Object>
     */
    @Override
    public Y9Result<Object> removeUseNumber(String tenantId, String userId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPersonId(userId);
        commonSentencesService.removeUseNumber();
        return Y9Result.success();
    }

    /**
     * 根据id保存更新常用语
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param id 常用语的唯一标识
     * @param content 内容
     * @return Y9Result<Object>
     */
    @Override
    public Y9Result<Object> save(String tenantId, String userId, String id, String content) {
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);
        commonSentencesService.save(id, content);
        return Y9Result.success();
    }

    /**
     * 根据排序号tabIndex保存更新常用语
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param content 常用语内容
     * @param tabIndex 排序号
     * @return Y9Result<Object>
     */
    @Override
    public Y9Result<Object> saveCommonSentences(String tenantId, String userId, String content, int tabIndex) {
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);
        commonSentencesService.saveCommonSentences(userId, content, tabIndex);
        return Y9Result.success();
    }

    /**
     * 更新常用语使用次数
     *
     * @param tenantId 租户id
     * @param id 常用语id
     * @return Y9Result<Object>
     */
    @Override
    public Y9Result<Object> updateUseNumber(String tenantId, String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        commonSentencesService.updateUseNumber(id);
        return Y9Result.success();
    }

}
