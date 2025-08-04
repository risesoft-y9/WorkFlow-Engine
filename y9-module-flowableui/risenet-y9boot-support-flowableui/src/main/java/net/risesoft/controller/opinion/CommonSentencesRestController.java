package net.risesoft.controller.opinion;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.CommonSentencesApi;
import net.risesoft.model.itemadmin.CommonSentencesModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 常用语
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/commonSentences", produces = MediaType.APPLICATION_JSON_VALUE)
public class CommonSentencesRestController {

    private final CommonSentencesApi commonSentencesApi;

    /**
     * 获取个人常用语
     *
     * @return Y9Result<List<CommonSentencesModel>>
     */
    @GetMapping(value = "/list")
    public Y9Result<List<CommonSentencesModel>> listSentencesService() {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId(), tenantId = person.getTenantId();
        return commonSentencesApi.listSentencesService(tenantId, userId);
    }

    /**
     * 删除个人常用语
     *
     * @param tabIndex 序号
     * @return Y9Result<String>
     */
    @PostMapping(value = "/remove")
    public Y9Result<String> remove(@RequestParam int tabIndex) {
        try {
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            commonSentencesApi.removeCommonSentences(Y9LoginUserHolder.getTenantId(), person.getPersonId(), tabIndex);
            return Y9Result.successMsg("删除成功");
        } catch (Exception e) {
            LOGGER.error("删除常用语失败", e);
        }
        return Y9Result.failure("删除失败");
    }

    /**
     * 清除常用语使用次数
     *
     * @return Y9Result<String>
     */
    @PostMapping(value = "/removeUseNumber")
    public Y9Result<String> removeUseNumber() {
        try {
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            commonSentencesApi.removeUseNumber(Y9LoginUserHolder.getTenantId(), person.getPersonId());
            return Y9Result.successMsg("操作成功");
        } catch (Exception e) {
            LOGGER.error("清除常用语使用次数失败", e);
        }
        return Y9Result.failure("操作失败");
    }

    /**
     * 保存个人常用语
     *
     * @param content 内容
     * @return Y9Result<String>
     */
    @PostMapping(value = "/save")
    public Y9Result<String> save(@RequestParam @NotBlank String content) {
        try {
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            String userId = person.getPersonId(), tenantId = person.getTenantId();
            commonSentencesApi.save(tenantId, userId, "", content);
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            LOGGER.error("保存常用语失败", e);
        }
        return Y9Result.failure("保存失败");
    }

    /**
     * 修改个人常用语
     *
     * @param content 内容
     * @param tabIndex 序号
     * @return Y9Result<String>
     */
    @PostMapping(value = "/saveEdit")
    public Y9Result<String> saveEdit(@RequestParam @NotBlank String content, @RequestParam @NotBlank String tabIndex) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId();
        try {
            commonSentencesApi.saveCommonSentences(Y9LoginUserHolder.getTenantId(), userId, content,
                Integer.parseInt(tabIndex));
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            LOGGER.error("修改常用语失败", e);
        }
        return Y9Result.failure("保存失败");
    }

    /**
     * 更新常用语使用次数
     *
     * @param id 常用语id
     * @return Y9Result<String>
     */
    @PostMapping(value = "/updateUseNumber")
    public Y9Result<String> updateUseNumber(@RequestParam @NotBlank String id) {
        try {
            commonSentencesApi.updateUseNumber(Y9LoginUserHolder.getTenantId(), id);
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            LOGGER.error("更新常用语使用次数失败", e);
        }
        return Y9Result.failure("保存失败");
    }
}
