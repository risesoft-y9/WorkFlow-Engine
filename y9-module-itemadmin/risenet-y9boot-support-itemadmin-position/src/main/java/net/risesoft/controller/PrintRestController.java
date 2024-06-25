package net.risesoft.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.ItemPrintTemplateBind;
import net.risesoft.entity.PrintTemplate;
import net.risesoft.entity.form.Y9Form;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.form.Y9FormRepository;
import net.risesoft.repository.jpa.PrintTemplateRepository;
import net.risesoft.service.PrintTemplateService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/vue/printTemplate")
public class PrintRestController {

    private final PrintTemplateRepository printTemplateRepository;

    private final PrintTemplateService printTemplateService;

    private final Y9FormRepository y9FormRepository;

    /**
     * 删除绑定打印模板
     *
     * @param id 绑定id
     * @return
     */
    @RequestMapping(value = "/deleteBindPrintTemplate", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> deleteBindPrintTemplate(@RequestParam String id) {
        Map<String, Object> map = printTemplateService.deleteBindPrintTemplate(id);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }

    /**
     * 删除打印模板
     *
     * @param id 模板id
     * @return
     */
    @RequestMapping(value = "/deletePrintTemplate", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> deletePrintTemplate(@RequestParam String id) {
        Map<String, Object> map = printTemplateService.deletePrintTemplate(id);
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
        printTemplateService.download(id, response, request);
    }

    /**
     * 获取打印模板列表
     *
     * @param fileName 文件名称
     * @return
     */
    @RequestMapping(value = "/getPrintTemplateList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> getPrintTemplateList(@RequestParam(required = false) String fileName) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<PrintTemplate> list;
        if (StringUtils.isNotBlank(fileName)) {
            list = printTemplateRepository.findByFileNameContaining(fileName);
        } else {
            list = printTemplateService.findAll();
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
    @RequestMapping(value = "/getBindTemplateList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<ItemPrintTemplateBind>> getTemplateList(@RequestParam String itemId) {
        List<ItemPrintTemplateBind> list = printTemplateService.getTemplateBindList(itemId);
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
    @RequestMapping(value = "/saveBindTemplate", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveBindTemplate(@RequestParam String itemId, @RequestParam String templateId,
        @RequestParam String templateName, @RequestParam(required = false) String templateUrl,
        @RequestParam String templateType) {
        Map<String, Object> map =
            printTemplateService.saveBindTemplate(itemId, templateId, templateName, templateUrl, templateType);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }

    /**
     * 上传打印模板
     *
     * @param files 文件
     * @return
     */
    @RequestMapping(value = "/uploadTemplate", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> upload(@RequestParam MultipartFile files) {
        Map<String, Object> map = printTemplateService.uploadTemplate(files);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }

}
