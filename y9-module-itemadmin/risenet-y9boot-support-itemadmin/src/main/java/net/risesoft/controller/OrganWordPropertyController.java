package net.risesoft.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.organword.OrganWordProperty;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.OrganWordPropertyService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/organWordProperty", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrganWordPropertyController {

    private final OrganWordPropertyService organWordPropertyService;

    /**
     * 获取机关代字信息
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/getOrganWordProperty")
    public Y9Result<OrganWordProperty> getOrganWordProperty(String id) {
        OrganWordProperty property = organWordPropertyService.findById(id);
        return Y9Result.success(property, "获取成功");
    }

    /**
     * 获取机关代字列表
     *
     * @param organWordId 编号id
     * @return
     */
    @GetMapping(value = "/propertyList")
    public Y9Result<List<OrganWordProperty>> organWordList(String organWordId) {
        List<OrganWordProperty> propertyList = organWordPropertyService.listByOrganWordId(organWordId);
        return Y9Result.success(propertyList, "获取成功");
    }

    /**
     * 删除机关代字
     *
     * @param propertyIds 机关代字id
     * @return
     */
    @PostMapping(value = "/removeProperty")
    public Y9Result<String> removePropertyIds(String[] propertyIds) {
        organWordPropertyService.removeOrganWordPropertys(propertyIds);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 保存机关代字
     *
     * @param organWordProperty 机关代字实体
     * @return
     */
    @PostMapping(value = "/save")
    public Y9Result<OrganWordProperty> save(@Valid OrganWordProperty organWordProperty) {
        OrganWordProperty property = organWordPropertyService.save(organWordProperty);
        return Y9Result.success(property, "保存成功");
    }

    @PostMapping(value = "/saveOrder")
    public Y9Result<String> saveOrder(String[] idAndTabIndexs) {
        organWordPropertyService.update4Order(idAndTabIndexs);
        return Y9Result.successMsg("保存成功");
    }
}