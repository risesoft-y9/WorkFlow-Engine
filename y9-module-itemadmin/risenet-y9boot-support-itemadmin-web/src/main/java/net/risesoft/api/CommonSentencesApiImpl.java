package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequestMapping(value = "/services/rest/commonSentences")
public class CommonSentencesApiImpl implements CommonSentencesApi {

    @Autowired
    private CommonSentencesService commonSentencesService;

    @Autowired
    private PersonApi personManager;

    @Override
    @PostMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> delete(String tenantId, String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        commonSentencesService.deleteById(id);
        return Y9Result.success();
    }

    @Override
    @GetMapping(value = "/listSentencesService", produces = MediaType.APPLICATION_JSON_VALUE)
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

    @Override
    @PostMapping(value = "/removeCommonSentences", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> removeCommonSentences(String tenantId, String userId, int tabIndex) {
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);
        commonSentencesService.removeCommonSentences(tabIndex);
        return Y9Result.success();
    }

    @Override
    @PostMapping(value = "/removeUseNumber", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> removeUseNumber(String tenantId, String userId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPersonId(userId);
        commonSentencesService.removeUseNumber();
        return Y9Result.success();
    }

    @Override
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> save(String tenantId, String userId, String id, String content) {
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);
        commonSentencesService.save(id, content);
        return Y9Result.success();
    }

    @Override
    @PostMapping(value = "/saveCommonSentences", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> saveCommonSentences(String tenantId, String userId, String content, int tabIndex) {
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);
        commonSentencesService.saveCommonSentences(userId, content, tabIndex);
        return Y9Result.success();
    }

    @Override
    @PostMapping(value = "/updateUseNumber", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> updateUseNumber(String tenantId, String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        commonSentencesService.updateUseNumber(id);
        return Y9Result.success();

    }
}
