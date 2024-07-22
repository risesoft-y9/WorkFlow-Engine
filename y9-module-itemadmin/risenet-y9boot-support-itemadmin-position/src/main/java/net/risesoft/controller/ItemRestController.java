package net.risesoft.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

import jodd.util.Base64;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/item", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class ItemRestController {

    private final SpmApproveItemService spmApproveItemService;

    private final RepositoryApi repositoryManager;

    private final OrganizationApi organizationManager;

    private final DepartmentApi departmentManager;

    private final AppIconApi appIconManager;

    private final PositionApi positionApi;

    /**
     * 复制事项
     *
     * @param id
     * @return
     */
    @PostMapping(value = "/copyItem")
    public Y9Result<String> copyItem(@RequestParam(required = true) String id) {
        Map<String, Object> map = spmApproveItemService.copyItem(id);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }

    /**
     * 删除事项
     *
     * @param id 事项id
     * @return
     */
    @PostMapping(value = "/delete")
    public Y9Result<String> delete(@RequestParam String id) {
        return spmApproveItemService.delete(id);
    }

    @SuppressWarnings("unused")
    private boolean deleteDirectory(String sPath) {
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

    private boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        if (file.isFile() && file.exists()) {
            flag = true;
        }
        return flag;
    }

    /**
     * 获取绑定的事项列表(不包含选择的事项)
     *
     * @param itemId 事项id
     * @return
     */
    @GetMapping(value = "/getBindItemList")
    public Y9Result<List<Map<String, Object>>> getBindItemList(@RequestParam(required = true) String itemId,
        @RequestParam(required = true) String itemName) {
        List<Map<String, Object>> listMap = new ArrayList<>();
        List<SpmApproveItem> itemList = spmApproveItemService.listByIdNotAndNameLike(itemId, itemName);
        for (SpmApproveItem item : itemList) {
            if (!item.getId().equals(itemId)) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", item.getId());
                map.put("itemName", item.getName());
                map.put("workflowGuid", item.getWorkflowGuid());
                map.put("systemName", item.getSystemName());
                listMap.add(map);
            }
        }
        return Y9Result.success(listMap, "获取成功");
    }

    /**
     * 获取部门
     *
     * @param id 部门id
     * @return
     */
    @RequestMapping(value = "/getDept")
    public Y9Result<String> getDept(@RequestParam String id) {
        StringBuilder sb = new StringBuilder();
        getJson(sb, id);
        return Y9Result.success("[" + sb.substring(0, sb.lastIndexOf(",")) + "]");
    }

    public void getJson(StringBuilder sb, String deptId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (StringUtils.isBlank(deptId)) {
            List<Organization> orgList = organizationManager.list(tenantId).getData();
            if (orgList != null && !orgList.isEmpty()) {
                List<Department> deptList =
                    departmentManager.listByParentId(tenantId, orgList.get(0).getId()).getData();
                for (Department dept : deptList) {
                    List<Department> subDeptList = departmentManager.listByParentId(tenantId, dept.getId()).getData();
                    boolean isParent = subDeptList != null && !subDeptList.isEmpty();
                    sb.append("{ id:'").append(dept.getId()).append("', pId:'").append(orgList.get(0).getId())
                        .append("', name:'").append(dept.getName()).append("', isParent: ").append(isParent)
                        .append("},");
                }
            }
        } else {
            List<Department> deptList = departmentManager.listByParentId(tenantId, deptId).getData();
            for (Department dept : deptList) {
                List<Department> subDeptList = departmentManager.listByParentId(tenantId, dept.getId()).getData();
                boolean isParent = subDeptList != null && !subDeptList.isEmpty();
                sb.append("{ id:'").append(dept.getId()).append("', pId:'").append(deptId).append("', name:'")
                    .append(dept.getName()).append("', isParent: ").append(isParent).append("},");
            }
        }
    }

    /**
     * 事项列表
     *
     * @return
     */
    @GetMapping(value = "/list")
    public Y9Result<List<SpmApproveItem>> list() {
        List<SpmApproveItem> list = spmApproveItemService.list();
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 获取新增或修改数据
     *
     * @param id 事项id
     * @return
     */
    @GetMapping(value = "/newOrModify")
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
        List<ProcessDefinitionModel> pdModelList = repositoryManager.getLatestProcessDefinitionList(tenantId).getData();
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
    @PostMapping(value = "/publishToSystemApp")
    public Y9Result<String> publishToSystemApp(@RequestParam String itemId) {
        return spmApproveItemService.publishToSystemApp(itemId);
    }

    /**
     * 图片文件读取
     *
     * @return
     */
    @GetMapping(value = "/readAppIconFile")
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
    @PostMapping(value = "/save")
    public Y9Result<SpmApproveItem> save(String itemJson) {
        SpmApproveItem item = Y9JsonUtil.readValue(itemJson, SpmApproveItem.class);
        return spmApproveItemService.save(item);
    }

    /**
     * 保存事项排序
     *
     * @param idAndTabIndexs 事项id和排序索引json
     * @return
     */
    @PostMapping(value = "/saveOrder")
    public Y9Result<String> saveOrder(@RequestParam String[] idAndTabIndexs) {
        spmApproveItemService.updateOrder(idAndTabIndexs);
        return Y9Result.successMsg("保存成功");
    }

    /**
     * 图标搜索
     *
     * @param name 搜索词
     * @return
     */
    @GetMapping(value = "/searchAppIcon")
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

    /**
     * 上传图标
     *
     * @param files
     * @return
     */
    @PostMapping(value = "/uploadItemIcon")
    public Y9Result<Map<String, Object>> uploadItemIcon(@RequestParam MultipartFile files) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("iconData", "");
        byte[] iconData = null;
        try {
            if (!files.isEmpty()) {
                iconData = files.getBytes();
                map.put("iconData", Base64.encodeToString(iconData));
            }
            return Y9Result.success(map, "上传成功");
        } catch (IOException e1) {
            LOGGER.warn(e1.getMessage(), e1);
        }
        return Y9Result.failure("上传失败");
    }
}
