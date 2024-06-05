package net.risesoft.controller;

import lombok.RequiredArgsConstructor;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.OrganWordProperty;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.OrganWordPropertyService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/organWordProperty")
public class OrganWordPropertyController {

    private final OrganWordPropertyService organWordPropertyService;

    /**
     * 获取机关代字信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/getOrganWordProperty", method = RequestMethod.GET, produces = "application/json")
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
    @RequestMapping(value = "/propertyList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<OrganWordProperty>> organWordList(String organWordId) {
        List<OrganWordProperty> propertyList = organWordPropertyService.findByOrganWordId(organWordId);
        return Y9Result.success(propertyList, "获取成功");
    }

    /**
     * 删除机关代字
     *
     * @param propertyIds
     * @return
     */
    @RequestMapping(value = "/removeProperty", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> removePropertyIds(String[] propertyIds) {
        organWordPropertyService.removeOrganWordPropertys(propertyIds);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 保存机关代字
     *
     * @param organWordProperty
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<Map<String, Object>> save(@Valid OrganWordProperty organWordProperty) {
        Map<String, Object> map = organWordPropertyService.save(organWordProperty);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg("保存成功");
        }
        return Y9Result.failure("保存失败");
    }

    @RequestMapping(value = "/saveOrder", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveOrder(String[] idAndTabIndexs) {
        organWordPropertyService.update4Order(idAndTabIndexs);
        return Y9Result.successMsg("保存成功");
    }
}