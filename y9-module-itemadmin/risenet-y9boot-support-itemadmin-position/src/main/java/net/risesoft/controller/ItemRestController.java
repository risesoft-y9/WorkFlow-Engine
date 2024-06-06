package net.risesoft.controller;

import lombok.RequiredArgsConstructor;
import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.platform.org.OrganizationApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.platform.resource.AppIconApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.AppIcon;
import net.risesoft.model.platform.Department;
import net.risesoft.model.platform.Organization;
import net.risesoft.model.platform.Position;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.SpmApproveItemService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/item")
public class ItemRestController {

    private final SpmApproveItemService spmApproveItemService;

    private final RepositoryApi repositoryManager;

    private final OrganizationApi organizationManager;

    private final DepartmentApi departmentManager;

    private final AppIconApi appIconManager;

    private final PositionApi positionApi;

    /**
     * 删除事项
     *
     * @param id 事项id
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> delete(@RequestParam String id) {
        Map<String, Object> map = spmApproveItemService.delete(id);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }

    @SuppressWarnings("unused")
    private  boolean deleteDirectory(String sPath) {
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        File[] files = dirFile.listFiles();
        assert files != null;
        for (File file : files) {
            if (file.isFile()) {
                flag = deleteFile(file.getAbsolutePath());
            } else {
                flag = deleteDirectory(file.getAbsolutePath());
            }
            if (!flag) {
                break;
            }
        }
        if (!flag) {
            return false;
        }
        return dirFile.delete();
    }

    private  boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        if (file.isFile() && file.exists()) {
            flag = true;
        }
        return flag;
    }

    /**
     * 获取部门
     *
     * @param id 部门id
     * @return
     */
    @RequestMapping(value = "/getDept")
    public String getDept(@RequestParam String id) {
        StringBuffer sb = new StringBuffer();
        getJson(sb, id);
        return "[" + sb.substring(0, sb.lastIndexOf(",")) + "]";
    }

    public void getJson(StringBuffer sb, String deptId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (StringUtils.isBlank(deptId)) {
            List<Organization> orgList = organizationManager.list(tenantId).getData();
            if (orgList != null && !orgList.isEmpty()) {
                List<Department> deptList =
                    departmentManager.listByParentId(tenantId, orgList.get(0).getId()).getData();
                for (Department dept : deptList) {
                    List<Department> subDeptList = departmentManager.listByParentId(tenantId, dept.getId()).getData();
                    boolean isParent = subDeptList != null && !subDeptList.isEmpty();
                    sb.append("{ id:'").append(dept.getId()).append("', pId:'").append(orgList.get(0).getId()).append("', name:'").append(dept.getName()).append("', isParent: ").append(isParent).append("},");
                }
            }
        } else {
            List<Department> deptList = departmentManager.listByParentId(tenantId, deptId).getData();
            for (Department dept : deptList) {
                List<Department> subDeptList = departmentManager.listByParentId(tenantId, dept.getId()).getData();
                boolean isParent = subDeptList != null && !subDeptList.isEmpty();
                sb.append("{ id:'").append(dept.getId()).append("', pId:'").append(deptId).append("', name:'").append(dept.getName()).append("', isParent: ").append(isParent).append("},");
            }
        }
    }

    /**
     * 事项列表
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<SpmApproveItem>> list() {
        Map<String, Object> map = spmApproveItemService.list();
        List<SpmApproveItem> list = (List<SpmApproveItem>)map.get("rows");
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 获取新增或修改数据
     *
     * @param id 事项id
     * @return
     */
    @RequestMapping(value = "/newOrModify", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> newOrModify(@RequestParam(required = false) String id) {
        Map<String, Object> map = new HashMap<>(16);
        String tenantId = Y9LoginUserHolder.getTenantId();
        SpmApproveItem item = new SpmApproveItem();
        item.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        List<Position> manager = new ArrayList<>();
        if (StringUtils.isNotBlank(id)) {
            item = spmApproveItemService.findById(id);
            if (StringUtils.isNotBlank(item.getNature())) {// 事项管理员
                String idStr = item.getNature();
                for (String positionId : idStr.split(";")) {
                    Position position = positionApi.get(tenantId, positionId).getData();
                    if (position != null) {
                        manager.add(position);
                    }
                }
            }
        }
        map.put("item", item);
        map.put("manager", manager);
        List<Map<String, Object>> workflowList = new ArrayList<>();
        List<ProcessDefinitionModel> pdModelList = repositoryManager.getLatestProcessDefinitionList(tenantId);
        for (ProcessDefinitionModel pdModel : pdModelList) {
            Map<String, Object> row = new HashMap<>(16);
            row.put("id", pdModel.getKey());
            row.put("name", pdModel.getName());
            workflowList.add(row);
        }
        map.put("workflowList", workflowList);
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 发布为应用系统
     *
     * @param itemId 事项id
     * @return
     */
    @RequestMapping(value = "/publishToSystemApp", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> publishToSystemApp(@RequestParam String itemId) {
        Map<String, Object> map = spmApproveItemService.publishToSystemApp(itemId);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }

    /**
     * 图片文件读取
     *
     * @return
     */
    @RequestMapping(value = "/readAppIconFile", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> readAppIconFile() {
        List<Map<String, String>> iconList;
        List<AppIcon> list = appIconManager.listAllIcon().getData();
        iconList = new ArrayList<>();
        if (list != null) {
            for (AppIcon appicon : list) {
                Map<String, String> filemap = new HashMap<>(16);
                filemap.put("path", appicon.getPath());
                filemap.put("name", appicon.getName());
                filemap.put("iconData", appicon.getIconData());
                iconList.add(filemap);
            }
        }
        Map<String, Object> map = new HashMap<>(16);
        map.put("iconList", iconList);
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 保存事项
     *
     * @param itemJson 事项信息json
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> save(String itemJson) {
        SpmApproveItem item = Y9JsonUtil.readValue(itemJson, SpmApproveItem.class);
        Map<String, Object> map = spmApproveItemService.save(item);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }

    /**
     * 图标搜索
     *
     * @param name 搜索词
     * @return
     */
    @RequestMapping(value = "/searchAppIcon", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> searchAppIcon(@RequestParam(required = false) String name) {
        List<AppIcon> list = appIconManager.searchAppIcon("%" + name + "%").getData();
        List<Map<String, String>> iconList = new ArrayList<>();
        if (list != null) {
            for (AppIcon appicon : list) {
                Map<String, String> filemap = new HashMap<>(16);
                filemap.put("path", appicon.getPath());
                filemap.put("name", appicon.getName());
                filemap.put("iconData", appicon.getIconData());
                iconList.add(filemap);
            }
        }
        Map<String, Object> map = new HashMap<>(16);
        map.put("iconList", iconList);
        return Y9Result.success(map, "获取成功");
    }
}
