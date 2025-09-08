package net.risesoft.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
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
import net.risesoft.api.platform.org.ManagerApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.entity.template.TaoHongTemplate;
import net.risesoft.entity.template.TaoHongTemplateType;
import net.risesoft.model.platform.org.Department;
import net.risesoft.model.platform.org.Manager;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.template.TaoHongTemplateService;
import net.risesoft.service.template.TaoHongTemplateTypeService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/taoHongTemplate", produces = MediaType.APPLICATION_JSON_VALUE)
public class TaoHongTemplateRestContronller {

    private final TaoHongTemplateService taoHongTemplateService;

    private final TaoHongTemplateTypeService taoHongTemplateTypeService;

    private final OrgUnitApi orgUnitApi;

    private final ManagerApi managerApi;

    private final DepartmentApi departmentApi;

    /**
     * 获取委办局树
     *
     * @param name 部门名称
     * @return
     */
    @GetMapping(value = "/bureauTree")
    public Y9Result<List<Department>> bureauTree(@RequestParam(required = false) String name) {
        name = StringUtils.isBlank(name) ? "" : name;
        return departmentApi.listBureauByNameLike(Y9LoginUserHolder.getTenantId(), name);
    }

    /**
     * 下载套红
     *
     * @param templateGuid 模板id
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/download")
    public void download(@RequestParam String templateGuid, HttpServletRequest request, HttpServletResponse response) {
        try {
            TaoHongTemplate taoHongTemplate = taoHongTemplateService.getById(templateGuid);
            byte[] b = taoHongTemplate.getTemplateContent();
            int length = b.length;
            String filename = taoHongTemplate.getTemplateFileName();
            String userAgent = "User-Agent", firefox = "firefox", msie = "MSIE";
            if (request.getHeader(userAgent).toLowerCase().indexOf(firefox) > 0) {
                filename = new String(filename.getBytes(StandardCharsets.UTF_8), "ISO8859-1");
            } else if (request.getHeader(userAgent).toUpperCase().indexOf(msie) > 0) {
                filename = URLEncoder.encode(filename, StandardCharsets.UTF_8);
            } else {
                filename = URLEncoder.encode(filename, StandardCharsets.UTF_8);
            }
            response.setContentType("application/octet-stream");
            response.setHeader("Content-disposition", "attachment; filename=" + filename);
            response.setHeader("Content-Length", String.valueOf(length));
            IOUtils.write(b, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            LOGGER.error("下载套红失败", e);
        }
    }

    /**
     * 获取套红列表
     *
     * @param name 委办局名称
     * @return
     */
    @GetMapping(value = "/getList")
    public Y9Result<List<TaoHongTemplate>> getList(@RequestParam(required = false) String name) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        List<TaoHongTemplate> list;
        if (person.isGlobalManager()) {
            list = taoHongTemplateService.listByTenantId(Y9LoginUserHolder.getTenantId(),
                StringUtils.isBlank(name) ? "%%" : "%" + name + "%");
        } else {
            OrgUnit orgUnit = orgUnitApi.getBureau(Y9LoginUserHolder.getTenantId(), person.getPersonId()).getData();
            list = taoHongTemplateService.listByBureauGuid(orgUnit.getId());
        }
        for (TaoHongTemplate taoHongTemplate : list) {
            String userId = taoHongTemplate.getUserId();
            Manager manger = managerApi.get(Y9LoginUserHolder.getTenantId(), userId).getData();
            taoHongTemplate.setUserName(manger != null ? manger.getName() : "人员不存在");
        }
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 获取新增编辑信息
     *
     * @param id 套红id
     * @return
     */
    @GetMapping(value = "/newOrModify")
    public Y9Result<Map<String, Object>> newOrModify(@RequestParam(required = false) String id) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), personId = person.getPersonId();
        Map<String, Object> map = new HashMap<>(16);
        List<TaoHongTemplateType> typeList;
        map.put("tenantManager", person.isGlobalManager());
        if (person.isGlobalManager()) {
            typeList = taoHongTemplateTypeService.listAll();
        } else {
            OrgUnit orgUnit = orgUnitApi.getBureau(tenantId, personId).getData();
            map.put("bureauGuid", orgUnit.getId());
            map.put("bureauName", orgUnit.getName());
            typeList = taoHongTemplateTypeService.listByBureauId(orgUnit.getId());
        }
        map.put("typeList", typeList);
        if (StringUtils.isNotEmpty(id)) {
            TaoHongTemplate taoHongTemplate = taoHongTemplateService.getById(id);
            map.put("taoHongTemplate", taoHongTemplate);
        }
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 删除套红
     *
     * @param ids 套红ids
     * @return
     */
    @PostMapping(value = "/removeTaoHongTemplate")
    public Y9Result<String> removeTaoHongTemplate(@RequestParam String[] ids) {
        taoHongTemplateService.removeTaoHongTemplate(ids);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 保存套红信息
     *
     * @param templateGuid 模板id
     * @param bureauGuid 委办局id
     * @param bureauName 委办局名称
     * @param templateType 模板类型
     * @param file 模板文件
     * @return
     */
    @PostMapping(value = "/saveOrUpdate")
    public Y9Result<String> saveOrUpdate(@RequestParam(required = false) String templateGuid,
        @RequestParam String bureauGuid, @RequestParam String bureauName, @RequestParam String templateType,
        MultipartFile file) {
        try {
            TaoHongTemplate taoHong = new TaoHongTemplate();
            taoHong.setBureauGuid(bureauGuid);
            taoHong.setBureauName(bureauName);
            taoHong.setTemplateGuid(templateGuid);
            taoHong.setTemplateType(templateType);
            if (file != null) {
                String[] fileName = Objects.requireNonNull(file.getOriginalFilename()).split("\\\\");
                taoHong.setTemplateContent(file.getBytes());
                if (fileName.length > 1) {
                    taoHong.setTemplateFileName(fileName[fileName.length - 1]);
                } else {
                    taoHong.setTemplateFileName(file.getOriginalFilename());
                }
            }
            taoHongTemplateService.saveOrUpdate(taoHong);
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            LOGGER.error("保存套红失败", e);
        }
        return Y9Result.failure("保存失败");
    }

}
