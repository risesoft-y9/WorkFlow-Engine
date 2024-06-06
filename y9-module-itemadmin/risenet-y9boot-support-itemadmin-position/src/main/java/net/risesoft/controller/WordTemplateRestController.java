package net.risesoft.controller;

import lombok.RequiredArgsConstructor;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.BookMarkBind;
import net.risesoft.entity.WordTemplate;
import net.risesoft.entity.form.Y9Table;
import net.risesoft.entity.form.Y9TableField;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.BookMarkBindService;
import net.risesoft.service.WordTemplateService;
import net.risesoft.service.form.Y9TableFieldService;
import net.risesoft.service.form.Y9TableService;
import net.risesoft.y9.Y9LoginUserHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
@RequiredArgsConstructor
@RequestMapping(value = "/vue/wordTemplate")
public class WordTemplateRestController {

    private final WordTemplateService wordTemplateService;

    private final OrgUnitApi orgUnitApi;

    private final BookMarkBindService bookMarkBindService;

    private final Y9TableService y9TableService;

    private final Y9TableFieldService y9TableFieldService;

    /**
     * 获取书签列表
     *
     * @param wordTemplateId 模板id
     * @param wordTemplateType 模板类型
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/bookMarKList")
    public Y9Result<List<Map<String, Object>>> bookMarkList(String wordTemplateId, @RequestParam String wordTemplateType) {
        Map<String, Object> map = wordTemplateService.getBookMarkList(wordTemplateId, wordTemplateType);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.success((List<Map<String, Object>>)map.get("rows"), (String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }

    /**
     * 删除正文模板
     *
     * @param id 模板id
     * @return
     */
    @RequestMapping(value = "/deleteWordTemplate", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> deleteWordTemplate(@RequestParam String id) {
        Map<String, Object> map = wordTemplateService.deleteWordTemplate(id);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }

    /**
     * 下载模板
     *
     * @param id 模板id
     * @param response 响应
     * @param request 请求
     */
    @RequestMapping(value = "/download")
    public void download(@RequestParam String id, HttpServletResponse response, HttpServletRequest request) {
        wordTemplateService.download(id, response, request);
    }

    /**
     * 获取书签绑定信息
     *
     * @param bookMarkName 书签名
     * @param wordTemplateId 模板id
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/getBookMarkBind", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> getBookMarkBind(@RequestParam String bookMarkName, @RequestParam String wordTemplateId) {
        Map<String, Object> resMap = new HashMap<>(16);
        List<Y9Table> tableList = y9TableService.getAllTable();
        List<String> columnList = new ArrayList<>();
        BookMarkBind bookMarkBind = bookMarkBindService.findByWordTemplateIdAndBookMarkName(wordTemplateId, bookMarkName);
        if (null != bookMarkBind) {
            String tableId = "";
            for (Y9Table table : tableList) {
                if (table.getTableName().equals(bookMarkBind.getTableName())) {
                    tableId = table.getId();
                }
            }
            if (!tableId.isEmpty()) {
                Map<String, Object> map = y9TableFieldService.getFieldList(tableId);
                List<Y9TableField> fieldList = (List<Y9TableField>)map.get("rows");
                for (Y9TableField field : fieldList) {
                    columnList.add(field.getFieldName());
                }
            }
        }
        resMap.put("bookMarkBind", bookMarkBind);
        resMap.put("columnList", columnList);
        resMap.put("tableList", tableList);
        return Y9Result.success(resMap, "获取成功");
    }

    /**
     * 获取表列
     *
     * @param tableId 表id
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/getColumns", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<String>> getColumns(@RequestParam String tableId) {
        List<String> columnList = new ArrayList<>();
        Map<String, Object> map = y9TableFieldService.getFieldList(tableId);
        List<Y9TableField> fieldList = (List<Y9TableField>)map.get("rows");
        for (Y9TableField field : fieldList) {
            columnList.add(field.getFieldName());
        }
        return Y9Result.success(columnList, "获取成功");
    }

    /**
     * 上传正文模板
     *
     * @param file 文件
     * @return
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> upload(MultipartFile file) {
        Map<String, Object> map = wordTemplateService.upload(file);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }

    /**
     * 获取正文模板列表
     *
     * @return
     */
    @RequestMapping(value = "/wordTemplateList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> wordTemplateList() {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String personId = person.getPersonId(), tenantId = Y9LoginUserHolder.getTenantId();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<WordTemplate> list;
        if (person.isGlobalManager()) {
            list = wordTemplateService.findAll();
        } else {
            list = wordTemplateService.findByBureauIdOrderByUploadTimeDesc(orgUnitApi.getBureau(tenantId, personId).getData().getId());
        }
        List<Map<String, Object>> items = new ArrayList<>();
        for (WordTemplate wordTemplate : list) {
            Map<String, Object> map = new HashMap<>(16);
            map.put("id", wordTemplate.getId());
            map.put("fileName", wordTemplate.getFileName());
            map.put("fileSize", wordTemplate.getFileSize());
            map.put("personName", wordTemplate.getPersonName());
            map.put("uploadTime", sdf.format(wordTemplate.getUploadTime()));
            map.put("wordTemplateType", wordTemplate.getFileName().endsWith("doc") ? "doc" : "docx");
            items.add(map);
        }
        return Y9Result.success(items, "获取成功");
    }
}
