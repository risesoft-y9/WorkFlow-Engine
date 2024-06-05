package net.risesoft.controller;

import net.risesoft.api.platform.org.ManagerApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.entity.TaoHongTemplate;
import net.risesoft.entity.TaoHongTemplateType;
import net.risesoft.model.platform.Manager;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.TaoHongTemplateService;
import net.risesoft.service.TaoHongTemplateTypeService;
import net.risesoft.y9.Y9LoginUserHolder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
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
@RequestMapping(value = "/vue/taoHongTemplate")
public class TaoHongTemplateRestContronller {

    private final JdbcTemplate jdbcTemplate;

    private final TaoHongTemplateService taoHongTemplateService;

    private final TaoHongTemplateTypeService taoHongTemplateTypeService;

    private final OrgUnitApi orgUnitApi;

    private final ManagerApi managerApi;

    public TaoHongTemplateRestContronller(@Qualifier("jdbcTemplate4Tenant") JdbcTemplate jdbcTemplate, TaoHongTemplateService taoHongTemplateService, TaoHongTemplateTypeService taoHongTemplateTypeService, OrgUnitApi orgUnitApi, ManagerApi managerApi) {
        this.jdbcTemplate = jdbcTemplate;
        this.taoHongTemplateService = taoHongTemplateService;
        this.taoHongTemplateTypeService = taoHongTemplateTypeService;
        this.orgUnitApi = orgUnitApi;
        this.managerApi = managerApi;
    }

    /**
     * 获取委办局树
     *
     * @param name 部门名称
     * @return
     */
    @RequestMapping(value = "/bureauTree", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> bureauTree(@RequestParam(required = false) String name) {
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        name = StringUtils.isBlank(name) ? "" : name;
        List<Map<String, Object>> orgUnitList = jdbcTemplate.queryForList(" SELECT ID,NAME,PARENT_ID FROM Y9_ORG_DEPARTMENT where bureau = 1 and deleted = 0 and name like '%" + name + "%' and disabled = 0 order by GUID_PATH asc");
        for (Map<String, Object> dept : orgUnitList) {
            Map<String, Object> map = new HashMap<String, Object>(16);
            map.put("id", dept.get("ID").toString());
            map.put("name", dept.get("NAME").toString());
            map.put("parentId", dept.get("PARENT_ID").toString());
            listMap.add(map);
        }
        return Y9Result.success(listMap, "获取成功");
    }

    /**
     * 下载套红
     *
     * @param templateGuid 模板id
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/download")
    public void download(@RequestParam(required = true) String templateGuid, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            TaoHongTemplate taoHongTemplate = taoHongTemplateService.findOne(templateGuid);
            byte[] b = taoHongTemplate.getTemplateContent();
            int length = b.length;
            String filename = taoHongTemplate.getTemplateFileName();
            String userAgent = "User-Agent", firefox = "firefox", msie = "MSIE";
            if (request.getHeader(userAgent).toLowerCase().indexOf(firefox) > 0) {
                filename = new String(filename.getBytes("UTF-8"), "ISO8859-1");
            } else if (request.getHeader(userAgent).toUpperCase().indexOf(msie) > 0) {
                filename = URLEncoder.encode(filename, "UTF-8");
            } else {
                filename = URLEncoder.encode(filename, "UTF-8");
            }
            response.setContentType("application/octet-stream");
            response.setHeader("Content-disposition", "attachment; filename=" + filename);
            response.setHeader("Content-Length", String.valueOf(length));
            IOUtils.write(b, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取套红列表
     *
     * @param name 委办局名称
     * @return
     */
    @RequestMapping(value = "/getList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> getList(@RequestParam(required = false) String name) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<TaoHongTemplate> list = new ArrayList<TaoHongTemplate>();
        if (person.isGlobalManager()) {
            list = taoHongTemplateService.findByTenantId(Y9LoginUserHolder.getTenantId(), StringUtils.isBlank(name) ? "%%" : "%" + name + "%");
        } else {
            OrgUnit orgUnit = orgUnitApi.getBureau(Y9LoginUserHolder.getTenantId(), person.getPersonId()).getData();
            list = taoHongTemplateService.findByBureauGuid(orgUnit.getId());
        }
        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>(16);
            map.put("templateGuid", list.get(i).getTemplateGuid());
            map.put("template_fileName", list.get(i).getTemplateFileName());
            map.put("bureauName", list.get(i).getBureauName());
            map.put("templateType", list.get(i).getTemplateType());
            map.put("uploadTime", sdf.format(list.get(i).getUploadTime()));

            String userId = list.get(i).getUserId();
            Manager manger = managerApi.get(Y9LoginUserHolder.getTenantId(), userId).getData();
            map.put("userName", manger != null ? manger.getName() : "人员不存在");
            map.put("tabIndex", list.get(i).getTabIndex());
            items.add(map);
        }
        return Y9Result.success(items, "获取成功");
    }

    /**
     * 获取新增编辑信息
     *
     * @param id 套红id
     * @return
     */
    @RequestMapping(value = "/newOrModify", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> newOrModify(@RequestParam(required = false) String id) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), personId = person.getPersonId();
        Map<String, Object> map = new HashMap<String, Object>(16);
        List<TaoHongTemplateType> typeList = new ArrayList<>();
        map.put("tenantManager", person.isGlobalManager());
        if (person.isGlobalManager()) {
            typeList = taoHongTemplateTypeService.findAll();
        } else {
            OrgUnit orgUnit = orgUnitApi.getBureau(tenantId, personId).getData();
            map.put("bureauGuid", orgUnit.getId());
            map.put("bureauName", orgUnit.getName());
            typeList = taoHongTemplateTypeService.findByBureauId(orgUnit.getId());
        }
        map.put("typeList", typeList);
        if (StringUtils.isNotEmpty(id)) {
            TaoHongTemplate taoHongTemplate = taoHongTemplateService.findOne(id);
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
    @RequestMapping(value = "/removeTaoHongTemplate", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> removeTaoHongTemplate(@RequestParam(required = true) String[] ids) {
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
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveOrUpdate(@RequestParam(required = false) String templateGuid, @RequestParam(required = true) String bureauGuid, @RequestParam(required = true) String bureauName, @RequestParam(required = true) String templateType, MultipartFile file) {
        try {
            TaoHongTemplate taoHong = new TaoHongTemplate();
            taoHong.setBureauGuid(bureauGuid);
            taoHong.setBureauName(bureauName);
            taoHong.setTemplateGuid(templateGuid);
            taoHong.setTemplateType(templateType);
            if (file != null) {
                String[] fileName = file.getOriginalFilename().split("\\\\");
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
            e.printStackTrace();
        }
        return Y9Result.failure("保存失败");
    }

}
