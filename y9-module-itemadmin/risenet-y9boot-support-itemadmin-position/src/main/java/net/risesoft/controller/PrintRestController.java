package net.risesoft.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.ItemPrintTemplateBind;
import net.risesoft.entity.PrintTemplate;
import net.risesoft.entity.form.Y9Form;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.form.Y9FormRepository;
import net.risesoft.service.PrintTemplateService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/printTemplate", produces = MediaType.APPLICATION_JSON_VALUE)
public class PrintRestController {

    private final PrintTemplateService printTemplateService;

    private final Y9FormRepository y9FormRepository;

    /**
     * 删除绑定打印模板
     *
     * @param id 绑定id
     * @return
     */
    @PostMapping(value = "/deleteBindPrintTemplate")
    public Y9Result<String> deleteBindPrintTemplate(@RequestParam String id) {
        return printTemplateService.deleteBindPrintTemplate(id);
    }

    /**
     * 删除打印模板
     *
     * @param id 模板id
     * @return
     */
    @PostMapping(value = "/deletePrintTemplate")
    public Y9Result<String> deletePrintTemplate(@RequestParam String id) {
        return printTemplateService.deletePrintTemplate(id);
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
        printTemplateService.download(id, response, request);
    }

    /**
     * 获取打印模板列表
     *
     * @param fileName 文件名称
     * @return
     */
    @GetMapping(value = "/getPrintTemplateList")
    public Y9Result<List<Map<String, Object>>> getPrintTemplateList(@RequestParam(required = false) String fileName) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<PrintTemplate> list;
        if (StringUtils.isNotBlank(fileName)) {
            list = printTemplateService.listByFileNameLike(fileName);
        } else {
            list = printTemplateService.listAll();
        }
        List<Map<String, Object>> items = new ArrayList<>();
        for (PrintTemplate printTemplate : list) {
            Map<String, Object> map = new HashMap<>(16);
            map.put("id", printTemplate.getId());
            map.put("fileName", printTemplate.getFileName());
            map.put("fileSize", printTemplate.getFileSize());
            map.put("fileUrl", printTemplate.getFilePath());
            map.put("personName", printTemplate.getPersonName());
            map.put("uploadTime", sdf.format(printTemplate.getUploadTime()));
            items.add(map);
        }
        return Y9Result.success(items, "获取成功");
    }

    /**
     * 获取绑定打印模板列表数据
     *
     * @param itemId 事项id
     * @return
     */
    @GetMapping(value = "/getBindTemplateList")
    public Y9Result<List<ItemPrintTemplateBind>> getTemplateList(@RequestParam String itemId) {
        List<ItemPrintTemplateBind> list = printTemplateService.listTemplateBindByItemId(itemId);
        for (ItemPrintTemplateBind bind : list) {
            if (bind.getTemplateType().equals("2")) {
                Y9Form form = y9FormRepository.findById(bind.getTemplateId()).orElse(null);
                bind.setTemplateName(form != null ? form.getFormName() : "表单不存在");
            }
        }
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 保存绑定模板
     *
     * @param itemId 事项id
     * @param templateId 模板id
     * @param templateName 模板名称
     * @param templateUrl 模板url
     * @param templateType 模板类型
     * @return
     */
    @PostMapping(value = "/saveBindTemplate")
    public Y9Result<String> saveBindTemplate(@RequestParam String itemId, @RequestParam String templateId,
        @RequestParam String templateName, @RequestParam(required = false) String templateUrl,
        @RequestParam String templateType) {
        return printTemplateService.saveBindTemplate(itemId, templateId, templateName, templateUrl, templateType);
    }

    /**
     * 上传打印模板
     *
     * @param files 文件
     * @return
     */
    @PostMapping(value = "/uploadTemplate")
    public Y9Result<String> upload(@RequestParam MultipartFile files) {
        return printTemplateService.uploadTemplate(files);
    }

}
