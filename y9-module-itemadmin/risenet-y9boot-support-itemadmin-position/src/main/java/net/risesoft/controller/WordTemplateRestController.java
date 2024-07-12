package net.risesoft.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.controller.vo.WordTemplateVO;
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

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/wordTemplate", produces = MediaType.APPLICATION_JSON_VALUE)
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
    @RequestMapping(value = "/bookMarKList")
    public Y9Result<List<Map<String, Object>>> bookMarkList(String wordTemplateId,
        @RequestParam String wordTemplateType) {
        List<Map<String, Object>> list = wordTemplateService.getBookMarkList(wordTemplateId, wordTemplateType);
        return Y9Result.success(list);
    }

    /**
     * 删除正文模板
     *
     * @param id 模板id
     * @return
     */
    @PostMapping(value = "/deleteWordTemplate")
    public Y9Result<String> deleteWordTemplate(@RequestParam String id) {
        return wordTemplateService.deleteWordTemplate(id);
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
    @GetMapping(value = "/getBookMarkBind")
    public Y9Result<Map<String, Object>> getBookMarkBind(@RequestParam String bookMarkName,
        @RequestParam String wordTemplateId) {
        Map<String, Object> resMap = new HashMap<>(16);
        List<Y9Table> tableList = y9TableService.getAllTable();
        List<String> columnList = new ArrayList<>();
        BookMarkBind bookMarkBind =
            bookMarkBindService.findByWordTemplateIdAndBookMarkName(wordTemplateId, bookMarkName);
        if (null != bookMarkBind) {
            String tableId = "";
            for (Y9Table table : tableList) {
                if (table.getTableName().equals(bookMarkBind.getTableName())) {
                    tableId = table.getId();
                }
            }
            if (!tableId.isEmpty()) {
                List<Y9TableField> fieldList = y9TableFieldService.getFieldList(tableId);
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
    @GetMapping(value = "/getColumns")
    public Y9Result<List<String>> getColumns(@RequestParam String tableId) {
        List<String> columnList = new ArrayList<>();
        List<Y9TableField> fieldList = y9TableFieldService.getFieldList(tableId);
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
    @PostMapping(value = "/upload")
    public Y9Result<String> upload(MultipartFile file) {
        return wordTemplateService.upload(file);
    }

    /**
     * 获取正文模板列表
     *
     * @return
     */
    @GetMapping(value = "/wordTemplateList")
    public Y9Result<List<WordTemplateVO>> wordTemplateList() {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String personId = person.getPersonId(), tenantId = Y9LoginUserHolder.getTenantId();
        List<WordTemplate> list;
        if (person.isGlobalManager()) {
            list = wordTemplateService.findAll();
        } else {
            list = wordTemplateService
                .findByBureauIdOrderByUploadTimeDesc(orgUnitApi.getBureau(tenantId, personId).getData().getId());
        }
        List<WordTemplateVO> items = new ArrayList<>();
        for (WordTemplate wordTemplate : list) {
            WordTemplateVO map = new WordTemplateVO();
            map.setId(wordTemplate.getId());
            map.setFileName(wordTemplate.getFileName());
            map.setFileSize(wordTemplate.getFileSize());
            map.setPersonName(wordTemplate.getPersonName());
            map.setUploadTime(wordTemplate.getUploadTime());
            map.setWordTemplateType(wordTemplate.getFileName().endsWith("doc") ? "doc" : "docx");
            map.setFilePath(wordTemplate.getFilePath());
            items.add(map);
        }
        return Y9Result.success(items, "获取成功");
    }
}
