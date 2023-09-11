package net.risesoft.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.risesoft.consts.UtilConsts;
import net.risesoft.service.CommonSentencesService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Controller
@RequestMapping("/commonSentences")
public class CommonSentencesController {

    @Autowired
    private CommonSentencesService commonSentencesService;

    @RequestMapping("/list")
    @ResponseBody
    public List<Map<String, Object>> listSentencesService() {
        List<Map<String, Object>> resList = new ArrayList<>();
        resList = commonSentencesService.listSentencesService();
        return resList;
    }

    @RequestMapping("/remove")
    @ResponseBody
    public Map<String, Object> remove(@RequestParam int tabIndex) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            commonSentencesService.removeCommonSentences(tabIndex);
            map.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            e.printStackTrace();
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", e.getMessage());
        }
        return map;
    }

    @RequestMapping("/save")
    @ResponseBody
    public Map<String, Object> save(@RequestParam String content) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            commonSentencesService.save(content);
            map.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", e.getMessage());
            e.printStackTrace();
        }
        return map;
    }

    @RequestMapping("/saveEdit")
    @ResponseBody
    public Map<String, Object> saveEdit(@RequestParam String content, @RequestParam String tabIndex) {
        Map<String, Object> map = new HashMap<>(16);
        String userId = Y9LoginUserHolder.getPersonId();
        try {
            commonSentencesService.saveCommonSentences(userId, content, Integer.parseInt(tabIndex));
            map.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            e.printStackTrace();
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", e.getMessage());
        }
        return map;
    }
}
