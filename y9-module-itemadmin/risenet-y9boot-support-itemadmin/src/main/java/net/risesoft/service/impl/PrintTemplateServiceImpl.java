package net.risesoft.service.impl;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.template.ItemPrintTemplateBind;
import net.risesoft.entity.template.PrintTemplate;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.template.ItemPrintTemplateBindRepository;
import net.risesoft.repository.template.PrintTemplateRepository;
import net.risesoft.service.PrintTemplateService;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class PrintTemplateServiceImpl implements PrintTemplateService {

    private final Y9FileStoreService y9FileStoreService;

    private final PrintTemplateRepository printTemplateRepository;

    private final ItemPrintTemplateBindRepository itemPrintTemplateBindRepository;

    @Override
    @Transactional(readOnly = false)
    public void copyBindInfo(String itemId, String newItemId) {
        try {
            ItemPrintTemplateBind printTemplateItemBind = itemPrintTemplateBindRepository.findByItemId(itemId);
            if (null != printTemplateItemBind) {
                ItemPrintTemplateBind newPrintTemplateItemBind = new ItemPrintTemplateBind();
                newPrintTemplateItemBind.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                newPrintTemplateItemBind.setItemId(newItemId);
                newPrintTemplateItemBind.setTenantId(Y9LoginUserHolder.getTenantId());
                newPrintTemplateItemBind.setTemplateId(printTemplateItemBind.getTemplateId());
                newPrintTemplateItemBind.setTemplateName(printTemplateItemBind.getTemplateName());
                newPrintTemplateItemBind.setTemplateUrl(printTemplateItemBind.getTemplateUrl());
                newPrintTemplateItemBind.setTemplateType(printTemplateItemBind.getTemplateType());
                itemPrintTemplateBindRepository.save(newPrintTemplateItemBind);
            }
        } catch (Exception e) {
            LOGGER.error("复制绑定信息失败", e);
        }
    }

    @Override
    @Transactional
    public void deleteBindInfo(String itemId) {
        try {
            itemPrintTemplateBindRepository.deleteByItemId(itemId);
        } catch (Exception e) {
            LOGGER.error("删除绑定信息失败", e);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Result<String> deleteBindPrintTemplate(String id) {
        try {
            ItemPrintTemplateBind bindTemplate = itemPrintTemplateBindRepository.findById(id).orElse(null);
            if (bindTemplate != null && bindTemplate.getId() != null) {
                itemPrintTemplateBindRepository.deleteById(bindTemplate.getId());
            }
            return Y9Result.successMsg("删除成功");
        } catch (Exception e) {
            LOGGER.error("删除绑定信息失败", e);
            return Y9Result.failure("删除绑定信息失败");
        }
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Result<String> deletePrintTemplate(String id) {
        try {
            PrintTemplate printTemplate = printTemplateRepository.findById(id).orElse(null);
            if (printTemplate != null && printTemplate.getId() != null) {
                printTemplateRepository.deleteById(printTemplate.getId());
                try {
                    y9FileStoreService.deleteFile(printTemplate.getFilePath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return Y9Result.successMsg("删除成功");
        } catch (Exception e) {
            LOGGER.error("删除模板失败", e);
            return Y9Result.failure("删除模板失败");
        }
    }

    @Override
    public void download(String id, HttpServletResponse response, HttpServletRequest request) {
        try {
            PrintTemplate printTemplate = printTemplateRepository.findById(id).orElse(null);
            byte[] b = y9FileStoreService.downloadFileToBytes(printTemplate.getFilePath());
            int length = b.length;
            String filename = "", userAgent = "User-Agent", firefox = "firefox", msie = "MSIE";
            if (request.getHeader(userAgent).toLowerCase().contains(firefox)) {
                filename = new String(printTemplate.getFileName().getBytes(StandardCharsets.UTF_8),
                    StandardCharsets.ISO_8859_1);
            } else if (request.getHeader(userAgent).toUpperCase().contains(msie)) {
                filename = URLEncoder.encode(printTemplate.getFileName(), StandardCharsets.UTF_8);
            } else {
                filename = URLEncoder.encode(printTemplate.getFileName(), StandardCharsets.UTF_8);
            }
            response.setContentType("application/octet-stream");
            response.setHeader("Content-disposition", "attachment; filename=\"" + filename + "\"");
            response.setHeader("Content-Length", String.valueOf(length));
            IOUtils.write(b, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            LOGGER.error("下载模板失败", e);
        }
    }

    @Override
    public List<PrintTemplate> listAll() {
        return printTemplateRepository.findAllOrderByUploadTimeDesc();
    }

    @Override
    public List<PrintTemplate> listByFileNameLike(String fileName) {
        return printTemplateRepository.findByFileNameContaining(fileName);
    }

    @Override
    public List<ItemPrintTemplateBind> listTemplateBindByItemId(String itemId) {
        List<ItemPrintTemplateBind> list = new ArrayList<>();
        try {
            ItemPrintTemplateBind itemPrintTemplateBind = itemPrintTemplateBindRepository.findByItemId(itemId);
            if (itemPrintTemplateBind != null) {
                list.add(itemPrintTemplateBind);
            }
        } catch (Exception e) {
            LOGGER.error("获取打印绑定信息失败", e);
        }
        return list;
    }

    @Transactional(readOnly = false)
    @Override
    public Y9Result<String> saveBindTemplate(String itemId, String templateId, String templateName, String templateUrl,
        String templateType) {
        try {
            ItemPrintTemplateBind printTemplateItemBind = itemPrintTemplateBindRepository.findByItemId(itemId);
            if (printTemplateItemBind == null) {
                printTemplateItemBind = new ItemPrintTemplateBind();
                printTemplateItemBind.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                printTemplateItemBind.setTenantId(Y9LoginUserHolder.getTenantId());
                printTemplateItemBind.setItemId(itemId);
                printTemplateItemBind.setTemplateId(templateId);
                printTemplateItemBind.setTemplateName(templateName);
                printTemplateItemBind.setTemplateUrl(templateUrl);
                printTemplateItemBind.setTemplateType(templateType);
                itemPrintTemplateBindRepository.save(printTemplateItemBind);
            } else {
                printTemplateItemBind.setItemId(itemId);
                printTemplateItemBind.setTemplateId(templateId);
                printTemplateItemBind.setTemplateName(templateName);
                printTemplateItemBind.setTemplateUrl(templateUrl);
                printTemplateItemBind.setTemplateType(templateType);
                itemPrintTemplateBindRepository.save(printTemplateItemBind);
            }
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            LOGGER.error("保存绑定信息失败", e);
            return Y9Result.failure("保存绑定信息失败");
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void saveOrUpdate(PrintTemplate printTemplate) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String personId = person.getPersonId(), personName = person.getName(),
            tenantId = Y9LoginUserHolder.getTenantId();
        String id = printTemplate.getId();
        if (StringUtils.isNotEmpty(id)) {
            PrintTemplate oldPrint = printTemplateRepository.findById(id).orElse(null);
            if (null != oldPrint) {
                oldPrint.setDescribes(printTemplate.getDescribes());
                oldPrint.setFileName(printTemplate.getFileName());
                oldPrint.setFilePath(printTemplate.getFilePath());
                oldPrint.setFileSize(printTemplate.getFileSize());
                oldPrint.setPersonId(personId);
                oldPrint.setPersonName(personName);
                oldPrint.setTenantId(tenantId);
                oldPrint.setUploadTime(printTemplate.getUploadTime());
                printTemplateRepository.save(oldPrint);
                return;
            } else {
                printTemplateRepository.save(printTemplate);
                return;
            }
        }
        PrintTemplate newPrint = new PrintTemplate();
        newPrint.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        newPrint.setDescribes(printTemplate.getDescribes());
        newPrint.setFileName(printTemplate.getFileName());
        newPrint.setFilePath(printTemplate.getFilePath());
        newPrint.setFileSize(printTemplate.getFileSize());
        newPrint.setPersonId(personId);
        newPrint.setPersonName(personName);
        newPrint.setTenantId(tenantId);
        newPrint.setUploadTime(printTemplate.getUploadTime());
        printTemplateRepository.save(newPrint);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Result<String> uploadTemplate(MultipartFile file) {
        String[] fileNames = file.getOriginalFilename().split("\\\\");
        String fileName = "";
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        try {
            PrintTemplate printTemplate = new PrintTemplate();
            printTemplate.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            if (fileNames.length > 1) {
                fileName = fileNames[fileNames.length - 1];
            } else {
                fileName = file.getOriginalFilename();
            }
            printTemplate.setFileName(fileName);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String fullPath = Y9FileStore.buildPath(Y9Context.getSystemName(), "printTemplate", sdf.format(new Date()));
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(file, fullPath, fileName);
            printTemplate.setPersonId(person.getPersonId());
            printTemplate.setPersonName(person.getName());
            printTemplate.setTenantId(Y9LoginUserHolder.getTenantId());
            printTemplate.setUploadTime(new Date());
            printTemplate.setDescribes("");
            printTemplate.setFilePath(y9FileStore.getId());
            printTemplate.setFileSize(y9FileStore.getDisplayFileSize());
            printTemplateRepository.save(printTemplate);
            return Y9Result.successMsg("上传成功");
        } catch (Exception e) {
            LOGGER.error("上传模板失败", e);
            return Y9Result.failure("上传模板失败");
        }

    }
}
