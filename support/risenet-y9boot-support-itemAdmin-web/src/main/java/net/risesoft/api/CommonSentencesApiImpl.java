package net.risesoft.api;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.CommonSentencesApi;
import net.risesoft.api.org.PersonApi;
import net.risesoft.model.Person;
import net.risesoft.service.CommonSentencesService;
import net.risesoft.util.CommentUtil;
import net.risesoft.y9.Y9LoginUserHolder;

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
    public void delete(String tenantId, String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        commonSentencesService.deleteById(id);
    }

    @Override
    @GetMapping(value = "/getCommonSentencesStr", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getCommonSentencesStr(String tenantId, String userId) {
        String[] comment = CommentUtil.getComment();
        String commentStr = "";
        int length = comment.length;
        for (int i = 0; i < length - 1; i++) {
            commentStr += "<option value=\"" + comment[length - 1 - i] + "\">" + comment[length - 1 - i] + "</option>";
        }
        return commentStr;
    }

    @Override
    @GetMapping(value = "/listSentencesService", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> listSentencesService(String tenantId, String userId) {
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);
        List<Map<String, Object>> listMap = commonSentencesService.listSentencesService();
        return listMap;
    }

    @Override
    @PostMapping(value = "/removeCommonSentences", produces = MediaType.APPLICATION_JSON_VALUE)
    public void removeCommonSentences(String tenantId, String userId, int tabIndex) {
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);
        commonSentencesService.removeCommonSentences(tabIndex);
    }

    @Override
    @PostMapping(value = "/removeUseNumber", produces = MediaType.APPLICATION_JSON_VALUE)
    public void removeUseNumber(String tenantId, String userId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPersonId(userId);
        commonSentencesService.removeUseNumber();
    }

    @Override
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    public void save(String tenantId, String userId, String id, String content) {
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);
        commonSentencesService.save(id, content);
    }

    @Override
    @PostMapping(value = "/saveCommonSentences", produces = MediaType.APPLICATION_JSON_VALUE)
    public void saveCommonSentences(String tenantId, String userId, String content, int tabIndex) {
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);
        commonSentencesService.saveCommonSentences(userId, content, tabIndex);
    }

    @Override
    @PostMapping(value = "/updateUseNumber", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateUseNumber(String tenantId, String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        commonSentencesService.updateUseNumber(id);

    }
}
