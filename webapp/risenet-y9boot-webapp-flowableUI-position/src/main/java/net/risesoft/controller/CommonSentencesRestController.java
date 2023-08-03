package net.risesoft.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.CommonSentencesApi;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

@RestController
@RequestMapping(value = "/vue/commonSentences")
public class CommonSentencesRestController {

    @Autowired
    private CommonSentencesApi commonSentencesManager;

    /**
     * 获取个人常用语
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> listSentencesService() {
        List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
        try {
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            String userId = person.getPersonId(), tenantId = person.getTenantId();
            resList = new ArrayList<Map<String, Object>>();
            resList = commonSentencesManager.listSentencesService(tenantId, userId);
            return Y9Result.success(resList, "获取成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 删除个人常用语
     *
     * @param tabIndex 序号
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/remove", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> remove(@RequestParam(required = true) int tabIndex) {
        try {
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            commonSentencesManager.removeCommonSentences(Y9LoginUserHolder.getTenantId(), person.getPersonId(),
                tabIndex);
            return Y9Result.successMsg("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("删除失败");
    }

    /**
     * 保存个人常用语
     *
     * @param content 内容
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> save(@RequestParam(required = true) String content) {
        try {
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            String userId = person.getPersonId(), tenantId = person.getTenantId();
            commonSentencesManager.save(tenantId, userId, "", content);
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("保存失败");
    }

    /**
     * 修改个人常用语
     *
     * @param content 内容
     * @param tabIndex 序号
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/saveEdit", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveEdit(@RequestParam(required = true) String content,
        @RequestParam(required = true) String tabIndex) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId();
        try {
            commonSentencesManager.saveCommonSentences(Y9LoginUserHolder.getTenantId(), userId, content,
                Integer.parseInt(tabIndex));
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("保存失败");
    }
}
