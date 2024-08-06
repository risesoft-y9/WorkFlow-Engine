package net.risesoft.controller;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.OrganWord;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.OrganWordService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/organWord", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrganWordController {

    private final OrganWordService organWordService;

    /**
     * 判断标识是否可用，true为可用
     *
     * @param id 编号id
     * @param custom 编号标识
     * @return
     */
    @GetMapping(value = "/checkCustom")
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
    @GetMapping(value = "/getOrganWord")
    public Y9Result<OrganWord> getOrganWord(String id) {
        OrganWord organWord = organWordService.findOne(id);
        return Y9Result.success(organWord, "获取成功");
    }

    /**
     * 获取编号列表
     *
     * @return
     */
    @GetMapping(value = "/organWordList")
    public Y9Result<List<OrganWord>> organWordList() {
        List<OrganWord> drList = organWordService.listAll();
        return Y9Result.success(drList, "获取成功");
    }

    /**
     * 删除编号
     *
     * @param organWordId 编号id
     * @return
     */
    @PostMapping(value = "/removeOrganWords")
    public Y9Result<String> removeOrganWords(String[] organWordId) {
        organWordService.removeOrganWords(organWordId);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 新增编号
     *
     * @param organWord 编号实体
     * @return
     */
    @PostMapping(value = "/saveOrUpdate")
    public Y9Result<Map<String, Object>> saveOrUpdate(@Valid OrganWord organWord) {
        organWordService.save(organWord);
        return Y9Result.successMsg("保存成功");
    }

}
