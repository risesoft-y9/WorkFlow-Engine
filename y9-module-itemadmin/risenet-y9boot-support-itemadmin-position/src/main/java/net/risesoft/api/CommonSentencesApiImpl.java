package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
@RequestMapping(value = "/services/rest/commonSentences", produces = MediaType.APPLICATION_JSON_VALUE)
public class CommonSentencesApiImpl implements CommonSentencesApi {

    private final CommonSentencesService commonSentencesService;

    private final PersonApi personApi;

    /**
     * 删除常用语
     *
     * @param tenantId 租户id
     * @param id 常用语id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> delete(@RequestParam String tenantId, @RequestParam String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        commonSentencesService.deleteById(id);
        return Y9Result.success();
    }

    /**
     * 获取常用语
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @return {@code Y9Result<List<CommonSentencesModel>>} 通用请求返回对象 - data 是常用语列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<CommonSentencesModel>> listSentencesService(@RequestParam String tenantId,
        @RequestParam String userId) {
        Person person = personApi.get(tenantId, userId).getData();
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
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> removeCommonSentences(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam int tabIndex) {
        Person person = personApi.get(tenantId, userId).getData();
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
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> removeUseNumber(@RequestParam String tenantId, @RequestParam String userId) {
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
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> save(@RequestParam String tenantId, @RequestParam String userId, @RequestParam String id,
        @RequestParam String content) {
        Person person = personApi.get(tenantId, userId).getData();
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
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> saveCommonSentences(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String content, @RequestParam int tabIndex) {
        Person person = personApi.get(tenantId, userId).getData();
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
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> updateUseNumber(@RequestParam String tenantId, @RequestParam String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        commonSentencesService.updateUseNumber(id);
        return Y9Result.success();
    }

}
