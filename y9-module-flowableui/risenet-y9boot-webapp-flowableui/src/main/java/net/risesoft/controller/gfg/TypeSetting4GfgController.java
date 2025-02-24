package net.risesoft.controller.gfg;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.apache.commons.io.FilenameUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.DocumentWordApi;
import net.risesoft.api.itemadmin.TypeSettingInfoApi;
import net.risesoft.model.itemadmin.DocumentWordModel;
import net.risesoft.model.itemadmin.TypeSettingInfoModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.word.Y9WordTool4Doc;
import net.risesoft.y9.util.word.Y9WordTool4Docx;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;

/**
 * 发文单排版信息
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/typeSetting", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class TypeSetting4GfgController {

    private final TypeSettingInfoApi typeSettingInfoApi;

    private final Y9FileStoreService y9FileStoreService;

    private final DocumentWordApi documentWordApi;

    /**
     * 删除排版信息
     *
     * @param id 主键id
     * @return Y9Result<Object>
     */
    @PostMapping(value = "/delTypeSetting")
    public Y9Result<Object> delTypeSetting(@RequestParam String id) {
        return typeSettingInfoApi.delTypeSetting(Y9LoginUserHolder.getTenantId(), id);
    }

    /**
     * 生成清样文件,发文稿纸正文替换书签
     * 
     * @param processSerialNumber 流程编号
     * @param storeId 文件存储id
     * @param fileName 文件名称
     * @return
     */
    public byte[] generateQingyangFile(String processSerialNumber, String storeId, String fileName) {
        try {
            byte[] b = y9FileStoreService.downloadFileToBytes(storeId);
            InputStream is = new ByteArrayInputStream(b);
            List<String> bookMarkNameList = new ArrayList<>();
            String docx = ".docx";
            if (fileName.contains(docx)) {
                bookMarkNameList = Y9WordTool4Docx.getBookMarkNameList(is);
            } else {
                bookMarkNameList = Y9WordTool4Doc.getBookmarkNameList(is);
                List<DocumentWordModel> list = documentWordApi
                    .findByProcessSerialNumberAndWordType(Y9LoginUserHolder.getTenantId(), processSerialNumber, "发文稿纸")
                    .getData();
                if (list.size() > 0) {
                    // 获取发文稿纸内容
                    byte[] b1 = y9FileStoreService.downloadFileToBytes(list.get(0).getFileStoreId());
                    InputStream is1 = new ByteArrayInputStream(b1);
                    String content = Y9WordTool4Doc.getText(is1);
                    // 判断是否有fenjie书签
                    boolean fenjie =
                        bookMarkNameList.stream().anyMatch((bookMarkName -> bookMarkName.equals("fenjie")));
                    if (fenjie) {
                        // 替换书签
                        Map<String, Object> map = new HashMap<>();
                        map.put("fenjie", content);
                        is = new ByteArrayInputStream(b);
                        byte[] os = Y9WordTool4Doc.replaceBookMark(is, map);
                        LOGGER.info("**********生成清样文件成功");
                        return os;
                    }
                    LOGGER.info("**********没有找到清样模板fenjie书签");
                }
                LOGGER.info("**********没有找到发文稿纸正文");
            }
        } catch (Exception e) {
            LOGGER.error("清样文件生成失败", e);
        }
        return null;
    }

    /**
     * 获取排版信息
     *
     * @param processSerialNumber 流程编号
     * @return Y9Result<List<TypeSettingInfoModel>>
     */
    @GetMapping(value = "/getList")
    public Y9Result<List<TypeSettingInfoModel>> getList(@RequestParam String processSerialNumber) {
        return typeSettingInfoApi.getList(Y9LoginUserHolder.getTenantId(), processSerialNumber);
    }

    /**
     * 获取排版信息
     *
     * @param id 主键id
     * @return Y9Result<TypeSettingInfoModel>
     */
    @GetMapping(value = "/getTypeSetting")
    public Y9Result<TypeSettingInfoModel> getTypeSetting(@RequestParam String id) {
        return typeSettingInfoApi.getTypeSetting(Y9LoginUserHolder.getTenantId(), id);
    }

    /**
     * 保存排版信息
     *
     * @param processSerialNumber 流程编号
     * @param jsonData 数据信息
     * @return Y9Result<Object>
     */
    @PostMapping(value = "/saveTypeSetting")
    public Y9Result<Object> saveTypeSetting(@RequestParam String processSerialNumber, @RequestParam String jsonData) {
        return typeSettingInfoApi.saveTypeSetting(Y9LoginUserHolder.getTenantId(), processSerialNumber, jsonData);
    }

    /**
     * 上传清样文件
     *
     * @param file 文件
     * @param processSerialNumber 流程编号
     * @return Y9Result<String>
     */
    @PostMapping(value = "/uploadQingyangFile")
    public Y9Result<String> uploadQingyangFile(MultipartFile file, @RequestParam @NotBlank String processSerialNumber) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId(), tenantId = Y9LoginUserHolder.getTenantId();
        try {
            String originalFilename = file.getOriginalFilename();
            String fileName = FilenameUtils.getName(originalFilename);
            String fullPath =
                "/" + Y9Context.getSystemName() + "/" + tenantId + "/attachmentFile" + "/" + processSerialNumber;
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(file, fullPath, fileName);
            String storeId = y9FileStore.getId();
            // 生成清样文件,发文稿纸正文替换书签
            byte[] os = this.generateQingyangFile(processSerialNumber, storeId, fileName);
            if (os != null) {
                y9FileStore = y9FileStoreService.uploadFile(os, fullPath, fileName);
                storeId = y9FileStore.getId();
            }
            return Y9Result.success(storeId);
        } catch (Exception e) {
            LOGGER.error("上传失败", e);
        }
        return Y9Result.failure("上传失败");
    }

}
