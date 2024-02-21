package net.risesoft.controller;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.OrganWord;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.OrganWordService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequestMapping(value = "/vue/organWord")
public class OrganWordController {

    @Autowired
    private OrganWordService organWordService;

    /**
     * 判断标识是否可用，true为可用
     *
     * @param id 编号id
     * @param custom 编号标识
     * @return
     */
    @RequestMapping(value = "/checkCustom", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Boolean> checkCustom(String id, String custom) {
        Boolean b = organWordService.checkCustom(id, custom);
        return Y9Result.success(b, "获取成功");
    }

    /**
     * 获取编号信息
     *
     * @param id 编号id
     * @return
     */
    @RequestMapping(value = "/getOrganWord", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<OrganWord> getOrganWord(String id) {
        OrganWord organWord = organWordService.findOne(id);
        return Y9Result.success(organWord, "获取成功");
    }

    /**
     * 获取编号列表
     *
     * @return
     */
    @RequestMapping(value = "/organWordList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<OrganWord>> organWordList() {
        List<OrganWord> drList = organWordService.findAll();
        return Y9Result.success(drList, "获取成功");
    }

    /**
     * 删除编号
     *
     * @param organWordId 编号id
     * @return
     */
    @RequestMapping(value = "/removeOrganWords", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> removeOrganWords(String[] organWordId) {
        organWordService.removeOrganWords(organWordId);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 新增编号
     *
     * @param organWord
     * @return
     */
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<Map<String, Object>> saveOrUpdate(@Valid OrganWord organWord) {
        Map<String, Object> map = organWordService.save(organWord);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg("保存成功");
        }
        return Y9Result.failure("保存失败");
    }

}
