package net.risesoft.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.org.PersonApi;
import net.risesoft.entity.TaoHongTemplateType;
import net.risesoft.model.OrgUnit;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.TaoHongTemplateTypeService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequestMapping(value = "/vue/taoHongTemplateType")
public class TaoHongTemplateTypeRestController {

    @Autowired
    private TaoHongTemplateTypeService taoHongTemplateTypeService;

    @Autowired
    private PersonApi personManager;

    /**
     * 获取模板类型
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<TaoHongTemplateType>> list() {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), personId = userInfo.getPersonId();
        List<TaoHongTemplateType> list = new ArrayList<>();
        if (userInfo.isGlobalManager()) {
            list = taoHongTemplateTypeService.findAll();
        } else {
            OrgUnit orgUnit = personManager.getBureau(tenantId, personId);
            list = taoHongTemplateTypeService.findByBureauId(orgUnit.getId());
        }
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 获取类型
     *
     * @param id 类型id
     * @return
     */
    @RequestMapping(value = "/newOrModify", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<TaoHongTemplateType> newOrModify(@RequestParam String id) {
        TaoHongTemplateType t = taoHongTemplateTypeService.findOne(id);
        return Y9Result.success(t, "获取成功");
    }

    /**
     * 删除模板类型
     *
     * @param ids 类型ids
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/removeTaoHongTemplateType", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> removeTaoHongTemplateType(@RequestParam String[] ids) {
        taoHongTemplateTypeService.removeTaoHongTemplateType(ids);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 保存类型排序
     *
     * @param idAndTabIndexs 排序id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/saveOrder", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveOrder(@RequestParam String[] idAndTabIndexs) {
        taoHongTemplateTypeService.saveOrder(idAndTabIndexs);
        return Y9Result.successMsg("保存成功");
    }

    /**
     * 保存模板类型
     *
     * @param t 类型信息
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveOrUpdate(TaoHongTemplateType t) {
        taoHongTemplateTypeService.saveOrUpdate(t);
        return Y9Result.successMsg("保存成功");
    }

}