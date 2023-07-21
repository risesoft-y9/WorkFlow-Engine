package y9.client.rest.itemadmin;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.CommonSentencesApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "CommonSentencesApiClient", name = "itemAdmin", url = "${y9.common.itemAdminBaseUrl}", path = "/services/rest/commonSentences")
public interface CommonSentencesApiClient extends CommonSentencesApi {

    /**
     * 删除常用语
     *
     * @param tenantId 租户id
     * @param id 常用语id
     */
    @Override
    @PostMapping("/delete")
    public void delete(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id);

    /**
     * 获取常用语字符串
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @return String
     */
    @Override
    @GetMapping("/getCommonSentencesStr")
    public String getCommonSentencesStr(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId);

    /**
     * 获取常用语
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    @Override
    @GetMapping("/listSentencesService")
    List<Map<String, Object>> listSentencesService(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId);

    /**
     * 根据排序号tabIndex删除常用语
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param tabIndex 排序号
     */
    @Override
    @PostMapping("/removeCommonSentences")
    public void removeCommonSentences(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("tabIndex") int tabIndex);

    /**
     * 根据id保存更新常用语
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param id 常用语的唯一标识
     * @param content 内容
     */
    @Override
    @PostMapping("/save")
    public void save(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("id") String id, @RequestParam("content") String content);

    /**
     * 根据排序号tabIndex保存更新常用语
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param content 常用语内容
     * @param tabIndex 排序号
     */
    @Override
    @PostMapping("/saveCommonSentences")
    public void saveCommonSentences(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("content") String content, @RequestParam("tabIndex") int tabIndex);
}
