package net.risesoft.service.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.BookMarkBind;
import net.risesoft.entity.WordTemplate;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.jpa.WordTemplateRepository;
import net.risesoft.service.BookMarkBindService;
import net.risesoft.service.WordTemplateService;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.word.Y9WordTool4Doc;
import net.risesoft.y9.util.word.Y9WordTool4Docx;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class WordTemplateServiceImpl implements WordTemplateService {

    private final WordTemplateRepository wordTemplateRepository;

    private final Y9FileStoreService y9FileStoreService;

    private final BookMarkBindService bookMarkBindService;

    private final OrgUnitApi orgUnitApi;

    @Override
    @Transactional
    public Map<String, Object> deleteWordTemplate(String id) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "删除成功");
            WordTemplate wordTemplate = wordTemplateRepository.findById(id).orElse(null);
            if (wordTemplate != null && wordTemplate.getId() != null) {
                wordTemplateRepository.deleteById(wordTemplate.getId());
                try {
                    y9FileStoreService.deleteFile(wordTemplate.getFilePath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "删除失败");
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public void download(String id, HttpServletResponse response, HttpServletRequest request) {
        try {
            WordTemplate wordTemplate = wordTemplateRepository.findById(id).orElse(null);
            assert wordTemplate != null;
            byte[] b = y9FileStoreService.downloadFileToBytes(wordTemplate.getFilePath());
            int length = b.length;
            String filename = "", userAgent = "User-Agent", firefox = "firefox", msie = "MSIE";
            if (request.getHeader(userAgent).toLowerCase().indexOf(firefox) > 0) {
                filename = new String(wordTemplate.getFileName().getBytes("UTF-8"), "ISO8859-1");
            } else if (request.getHeader(userAgent).toUpperCase().indexOf(msie) > 0) {
                filename = URLEncoder.encode(wordTemplate.getFileName(), "UTF-8");
            } else {
                filename = URLEncoder.encode(wordTemplate.getFileName(), "UTF-8");
            }
            response.setContentType("application/octet-stream");
            response.setHeader("Content-disposition", "attachment; filename=\"" + filename + "\"");
            response.setHeader("Content-Length", String.valueOf(length));
            IOUtils.write(b, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<WordTemplate> findAll() {
        return wordTemplateRepository.findAll();
    }

    @Override
    public List<WordTemplate> findByBureauIdOrderByUploadTimeDesc(String bureauId) {
        return wordTemplateRepository.findByBureauIdOrderByUploadTimeDesc(bureauId);
    }

    @Override
    public WordTemplate findById(String id) {
        return wordTemplateRepository.findById(id).orElse(null);
    }

    @Override
    public Map<String, Object> getBookMarkList(String wordTemplateId, String wordTemplateType) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "获取成功");
            WordTemplate wordTemplate = wordTemplateRepository.findById(wordTemplateId).orElse(null);
            byte[] b = y9FileStoreService.downloadFileToBytes(wordTemplate.getFilePath());
            InputStream is = new ByteArrayInputStream(b);
            List<String> bookMarkNameList = new ArrayList<>();
            String doc = "doc";
            if (doc.equals(wordTemplateType)) {
                bookMarkNameList = Y9WordTool4Doc.getBookmarkNameList(is);
            } else {
                bookMarkNameList = Y9WordTool4Docx.getBookMarkNameList(is);
            }
            List<Map<String, Object>> items = new ArrayList<>();
            Map<String, Object> mapTemp = null;
            BookMarkBind bookMarkBind = null;
            for (String bookMarkName : bookMarkNameList) {
                mapTemp = new HashMap<>(16);
                mapTemp.put("bookMarkName", bookMarkName);
                bookMarkBind = bookMarkBindService.findByWordTemplateIdAndBookMarkName(wordTemplateId, bookMarkName);
                if (null != bookMarkBind) {
                    mapTemp.put("tableColumn", bookMarkBind.getTableName() + "." + bookMarkBind.getColumnName());
                    mapTemp.put("userName", bookMarkBind.getUserName());
                    mapTemp.put("updateTime", bookMarkBind.getUpdateTime());
                }
                items.add(mapTemp);
            }
            map.put("rows", items);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "获取失败");
            e.printStackTrace();
        }
        return map;
    }

    @Override
    @Transactional
    public void saveOrUpdate(WordTemplate wordTemplate) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), personId = person.getPersonId(),
            personName = person.getName();
        String id = wordTemplate.getId();
        if (StringUtils.isNotEmpty(id)) {
            WordTemplate oldWord = this.findById(id);
            if (null != oldWord) {
                oldWord.setBureauId(orgUnitApi.getBureau(tenantId, personId).getData().getId());
                oldWord.setDescribes(wordTemplate.getDescribes());
                oldWord.setFileName(wordTemplate.getFileName());
                oldWord.setFilePath(wordTemplate.getFilePath());
                oldWord.setFileSize(wordTemplate.getFileSize());
                oldWord.setPersonId(personId);
                oldWord.setPersonName(personName);
                oldWord.setUploadTime(new Date());

                wordTemplateRepository.save(oldWord);
                return;
            } else {
                wordTemplateRepository.save(wordTemplate);
                return;
            }
        }

        WordTemplate newWord = new WordTemplate();
        newWord.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        newWord.setBureauId(orgUnitApi.getBureau(tenantId, personId).getData().getId());
        newWord.setDescribes(wordTemplate.getDescribes());
        newWord.setFileName(wordTemplate.getFileName());
        newWord.setFilePath(wordTemplate.getFilePath());
        newWord.setFileSize(wordTemplate.getFileSize());
        newWord.setPersonId(personId);
        newWord.setPersonName(personName);
        newWord.setUploadTime(new Date());

        wordTemplateRepository.save(newWord);
        return;
    }

    @Override
    @Transactional
    public Map<String, Object> upload(MultipartFile file) {
        String[] fileNames = Objects.requireNonNull(file.getOriginalFilename()).split("\\\\");
        String fileName = "";
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), personId = person.getPersonId();
        Map<String, Object> map = new HashMap<>(16);
        map.put(UtilConsts.SUCCESS, true);
        map.put("msg", "上传成功");
        try {
            WordTemplate wordTemplate = new WordTemplate();
            wordTemplate.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            if (fileNames.length > 1) {
                fileName = fileNames[fileNames.length - 1];
            } else {
                fileName = file.getOriginalFilename();
            }
            wordTemplate.setFileName(fileName);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String fullPath = "/" + Y9Context.getSystemName() + "/wordTemplate/" + sdf.format(new Date());
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(file, fullPath, fileName);
            wordTemplate.setPersonId(person.getPersonId());
            wordTemplate.setPersonName(person.getName());
            wordTemplate.setBureauId(orgUnitApi.getBureau(tenantId, personId).getData().getId());
            wordTemplate.setUploadTime(new Date());
            wordTemplate.setDescribes("");
            wordTemplate.setFilePath(y9FileStore.getId());
            wordTemplate.setFileSize(y9FileStore.getDisplayFileSize());
            wordTemplateRepository.save(wordTemplate);
        } catch (Exception e) {
            e.printStackTrace();
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "上传失败");
        }
        return map;
    }
}
