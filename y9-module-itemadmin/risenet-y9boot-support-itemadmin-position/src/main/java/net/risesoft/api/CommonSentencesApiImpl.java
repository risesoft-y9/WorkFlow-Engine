package net.risesoft.api;

import lombok.RequiredArgsConstructor;
import net.risesoft.api.itemadmin.CommonSentencesApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.model.platform.Person;
import net.risesoft.service.CommonSentencesService;
import net.risesoft.util.CommentUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

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
     */
    @Override
    @PostMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public void delete(String tenantId, String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        commonSentencesService.deleteById(id);
    }

    /**
     * 获取常用语字符串
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @return String
     */
    @Override
    @GetMapping(value = "/getCommonSentencesStr", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getCommonSentencesStr(String tenantId, String userId) {
        String[] comment = CommentUtil.getComment();
        StringBuilder commentStr = new StringBuilder();
        int length = comment.length;
        for (int i = 0; i < length - 1; i++) {
            commentStr.append("<option value=\"").append(comment[length - 1 - i]).append("\">").append(comment[length - 1 - i]).append("</option>");
        }
        return commentStr.toString();
    }

    /**
     * 获取常用语
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    @Override
    @GetMapping(value = "/listSentencesService", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> listSentencesService(String tenantId, String userId) {
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);
        return commonSentencesService.listSentencesService();
    }

    /**
     * 根据排序号tabIndex删除常用语
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param tabIndex 排序号
     */
    @Override
    @PostMapping(value = "/removeCommonSentences", produces = MediaType.APPLICATION_JSON_VALUE)
    public void removeCommonSentences(String tenantId, String userId, int tabIndex) {
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);
        commonSentencesService.removeCommonSentences(tabIndex);
    }


    /**
     * 清空常用语使用次数
     * @param tenantId 租户id
     * @param userId 人员id
     */
    @Override
    @PostMapping(value = "/removeUseNumber", produces = MediaType.APPLICATION_JSON_VALUE)
    public void removeUseNumber(String tenantId, String userId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPersonId(userId);
        commonSentencesService.removeUseNumber();
    }

    /**
     * 根据id保存更新常用语
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param id 常用语的唯一标识
     * @param content 内容
     */
    @Override
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    public void save(String tenantId, String userId, String id, String content) {
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);
        commonSentencesService.save(id, content);
    }

    /**
     * 根据排序号tabIndex保存更新常用语
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param content 常用语内容
     * @param tabIndex 排序号
     */
    @Override
    @PostMapping(value = "/saveCommonSentences", produces = MediaType.APPLICATION_JSON_VALUE)
    public void saveCommonSentences(String tenantId, String userId, String content, int tabIndex) {
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);
        commonSentencesService.saveCommonSentences(userId, content, tabIndex);
    }

    /**
     * 更新常用语使用次数
     * @param tenantId 租户id
     * @param id 常用语id
     */
    @Override
    @PostMapping(value = "/updateUseNumber", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateUseNumber(String tenantId, String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        commonSentencesService.updateUseNumber(id);

    }

}
