package net.risesoft.controller;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.position.Entrust4PositionApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.OrganizationApi;
import net.risesoft.enums.platform.OrgTreeTypeEnum;
import net.risesoft.model.itemadmin.EntrustModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Organization;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;

/**
 * 出差委托
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/vue/entrust")
public class EntrustRestController {

    private final Entrust4PositionApi entrust4PositionApi;

    private final OrgUnitApi orgUnitApi;

    private final OrganizationApi organizationApi;

    /**
     * 删除委托
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/deleteEntrust", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> deleteEntrust(String id) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            entrust4PositionApi.deleteEntrust(tenantId, id);
            return Y9Result.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("删除失败");
    }

    /**
     * 获取委托列表
     *
     * @return
     */
    @RequestMapping(value = "/getEntrustList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<EntrustModel>> getEntrustList() {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            List<EntrustModel> list = entrust4PositionApi.getEntrustList(tenantId, Y9LoginUserHolder.getPositionId());
            return Y9Result.success(list, "获取成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure(null, "获取失败");
    }

    /**
     * 获取组织架构
     *
     * @return
     */
    @RequestMapping(value = "/getOrgList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Organization>> getOrgList() {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            List<Organization> list = organizationApi.list(tenantId).getData();
            return Y9Result.success(list, "获取成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure(null, "获取失败");
    }

    /**
     * 展开组织架构树
     *
     * @param id
     * @param treeType
     * @return
     */
    @RequestMapping(value = "/getOrgTree", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<OrgUnit>> getOrgTree(String id, OrgTreeTypeEnum treeType) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            List<OrgUnit> list = orgUnitApi.getSubTree(tenantId, id, treeType).getData();
            return Y9Result.success(list, "获取成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure(null, "获取失败");
    }

    /**
     * 保存委托数据
     *
     * @param jsonData
     * @return
     */
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveOrUpdate(@NotBlank String jsonData) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            EntrustModel model = Y9JsonUtil.readValue(jsonData, EntrustModel.class);
            entrust4PositionApi.saveOrUpdate(tenantId, Y9LoginUserHolder.getPositionId(), model);
            return Y9Result.success("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("保存失败");
    }

    /**
     * 组织架构树搜索
     *
     * @param name
     * @param treeType
     * @return
     */
    @RequestMapping(value = "/treeSearch", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<OrgUnit>> treeSearch(String name, OrgTreeTypeEnum treeType) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            List<OrgUnit> list = orgUnitApi.treeSearch(tenantId, name, treeType).getData();
            return Y9Result.success(list, "获取成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure(null, "获取失败");
    }
}
