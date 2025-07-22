package net.risesoft.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.entrust.EntrustApi;
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
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/entrust", produces = MediaType.APPLICATION_JSON_VALUE)
public class EntrustRestController {

    private final EntrustApi entrustApi;

    private final OrgUnitApi orgUnitApi;

    private final OrganizationApi organizationApi;

    /**
     * 删除委托信息
     *
     * @param id 委托id
     * @return Y9Result<String>
     */
    @PostMapping(value = "/deleteEntrust")
    public Y9Result<String> deleteEntrust(@RequestParam String id) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            entrustApi.deleteEntrust(tenantId, id);
            return Y9Result.success("删除成功");
        } catch (Exception e) {
            LOGGER.error("删除委托出错", e);
        }
        return Y9Result.failure("删除失败");
    }

    /**
     * 获取当前人的委托列表
     *
     * @return Y9Result<List < EntrustModel>>
     */
    @GetMapping(value = "/getEntrustList")
    public Y9Result<List<EntrustModel>> getEntrustList() {
        String tenantId = Y9LoginUserHolder.getTenantId();
        return entrustApi.getEntrustList(tenantId, Y9LoginUserHolder.getPositionId());
    }

    /**
     * 获取组织架构
     *
     * @return Y9Result<List < Organization>>
     */
    @GetMapping(value = "/getOrgList")
    public Y9Result<List<Organization>> getOrgList() {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            List<Organization> list = organizationApi.list(tenantId).getData();
            return Y9Result.success(list, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取组织架构出错", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 展开组织架构树
     *
     * @param id 父节点id
     * @param treeType 树类型
     * @return Y9Result<List < OrgUnit>>
     */
    @GetMapping(value = "/getOrgTree")
    public Y9Result<List<OrgUnit>> getOrgTree(@RequestParam(required = false) String id, OrgTreeTypeEnum treeType) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            List<OrgUnit> list = orgUnitApi.getSubTree(tenantId, id, treeType).getData();
            return Y9Result.success(list, "获取成功");
        } catch (Exception e) {
            LOGGER.error("展开组织架构树出错", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 保存委托数据
     *
     * @param jsonData json数据
     * @return Y9Result<String>
     */
    @PostMapping(value = "/saveOrUpdate")
    public Y9Result<String> saveOrUpdate(@RequestParam String jsonData) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            EntrustModel model = Y9JsonUtil.readValue(jsonData, EntrustModel.class);
            entrustApi.saveOrUpdate(tenantId, Y9LoginUserHolder.getPositionId(), model);
            return Y9Result.success("保存成功");
        } catch (Exception e) {
            LOGGER.error("保存委托数据出错", e);
        }
        return Y9Result.failure("保存失败");
    }

    /**
     * 组织架构树搜索
     *
     * @param name 搜索词
     * @param treeType 树类型
     * @return Y9Result<List < OrgUnit>>
     */
    @GetMapping(value = "/treeSearch")
    public Y9Result<List<OrgUnit>> treeSearch(@RequestParam(required = false) String name, OrgTreeTypeEnum treeType) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            List<OrgUnit> list = orgUnitApi.treeSearch(tenantId, name, treeType).getData();
            return Y9Result.success(list, "获取成功");
        } catch (Exception e) {
            LOGGER.error("组织架构树搜索出错", e);
        }
        return Y9Result.failure("获取失败");
    }
}
