package net.risesoft.controller.gfg;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.util.gfg.HttpRequestUtil;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
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

import net.risesoft.api.itemadmin.EleAttachmentApi;
import net.risesoft.api.itemadmin.MergeFileApi;
import net.risesoft.enums.BrowserTypeEnum;
import net.risesoft.log.FlowableOperationTypeEnum;
import net.risesoft.log.annotation.FlowableLog;
import net.risesoft.model.itemadmin.EleAttachmentModel;
import net.risesoft.model.itemadmin.MergeFileModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;

import cn.hutool.core.date.DateUtil;

/**
 * 合并文件接口
 *
 * @author qinman
 * @date 2024/11/07
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/mergeFile", produces = MediaType.APPLICATION_JSON_VALUE)
public class MergeFileRestController {

    private final MergeFileApi mergeFileApi;

    private final EleAttachmentApi eleAttachmentApi;

    private final Y9FileStoreService y9FileStoreService;

    private static MergeFileModel getMergeFileModel(String processSerialNumber, EleAttachmentModel eleAttachmentModel) {
        MergeFileModel mergeFileModel = new MergeFileModel();
        mergeFileModel.setId(eleAttachmentModel.getId());
        mergeFileModel.setFileName(eleAttachmentModel.getName());
        mergeFileModel.setListType(eleAttachmentModel.getAttachmentType());
        mergeFileModel.setFileStoreId(eleAttachmentModel.getFileStoreId());
        mergeFileModel.setProcessSerialNumber(processSerialNumber);
        mergeFileModel.setPersonName(eleAttachmentModel.getPersonName());
        mergeFileModel.setPersonId(eleAttachmentModel.getPersonId());
        mergeFileModel.setCreateTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        return mergeFileModel;
    }

    /**
     * 删除合并文件
     *
     * @param ids 文件id
     * @return Y9Result<Object>
     */
    @FlowableLog(operationName = "删除合并文件", operationType = FlowableOperationTypeEnum.DELETE)
    @PostMapping(value = "/delMergeFile")
    public Y9Result<Object> delMergeFile(@RequestParam String[] ids) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        return mergeFileApi.delMergeFile(tenantId, ids);
    }

    /**
     * 文件下载
     *
     * @param id 附件id
     */
    @FlowableLog(operationName = "文件下载", operationType = FlowableOperationTypeEnum.DOWNLOAD)
    @GetMapping(value = "/download")
    public void download(@RequestParam @NotBlank String id, HttpServletResponse response, HttpServletRequest request) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            MergeFileModel model = mergeFileApi.getMergeFile(tenantId, id).getData();
            String filename = model.getFileName();
            String filePath = model.getFileStoreId();
            if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0) {
                filename = new String(filename.getBytes(StandardCharsets.UTF_8), "ISO8859-1");// 火狐浏览器
            } else {
                filename = URLEncoder.encode(filename, StandardCharsets.UTF_8);
            }
            OutputStream out = response.getOutputStream();
            response.reset();
            response.setHeader("Content-disposition", "attachment; filename=\"" + filename + "\"");
            response.setHeader("Content-type", "text/html;charset=UTF-8");
            response.setContentType("application/octet-stream");
            y9FileStoreService.downloadFileToOutputStream(filePath, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            LOGGER.error("附件下载失败", e);
        }
    }

    /**
     * 获取附件列表
     *
     * @param processSerialNumber 流程编号
     * @return Y9Result<List<MergeFileModel>>
     */
    @FlowableLog(operationName = "获取附件列表")
    @GetMapping(value = "/getFileList")
    public Y9Result<List<MergeFileModel>> getFileList(@RequestParam @NotBlank String processSerialNumber) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            List<MergeFileModel> list = new ArrayList<>();
            List<EleAttachmentModel> eList = eleAttachmentApi
                .findByProcessSerialNumberAndAttachmentType(tenantId, processSerialNumber, "ele").getData();
            if (eList != null) {
                for (EleAttachmentModel eleAttachmentModel : eList) {
                    MergeFileModel mergeFileModel = getMergeFileModel(processSerialNumber, eleAttachmentModel);
                    list.add(mergeFileModel);
                }
            }
            List<EleAttachmentModel> tList = eleAttachmentApi
                .findByProcessSerialNumberAndAttachmentType(tenantId, processSerialNumber, "text").getData();
            if (tList != null) {
                for (EleAttachmentModel eleAttachmentModel : tList) {
                    MergeFileModel mergeFileModel = getMergeFileModel(processSerialNumber, eleAttachmentModel);
                    list.add(mergeFileModel);
                }
            }
            list.stream().sorted().collect(Collectors.toList());
            return Y9Result.success(list);
        } catch (Exception e) {
            LOGGER.error("获取附件列表异常", e);
        }
        return Y9Result.failure("获取附件列表失败");
    }

    /**
     * 获取合并列表
     *
     * @param processSerialNumber 流程编号
     * @param listType 列表类型,1为附件合并,2为文件合并
     * @param fileType 文件类型,1为合并文件,2为合并版式文件
     * @return Y9Result<List<MergeFileModel>>
     */
    @FlowableLog(operationName = "获取合并列表")
    @GetMapping(value = "/getMergeFileList")
    public Y9Result<List<MergeFileModel>> getMergeFileList(@RequestParam(required = false) String processSerialNumber,
        @RequestParam String listType, @RequestParam String fileType) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            return mergeFileApi.getMergeFileList(tenantId, Y9LoginUserHolder.getPersonId(), processSerialNumber,
                listType, fileType);
        } catch (Exception e) {
            LOGGER.error("获取合并列表异常", e);
        }
        return Y9Result.failure("获取合并列表失败");
    }

    /**
     * 打包zip下载
     *
     * @param ids 文件id
     * @param response HttpServletResponse
     * @param request HttpServletRequest
     */
    @FlowableLog(operationName = "打包zip下载", operationType = FlowableOperationTypeEnum.DOWNLOAD)
    @GetMapping(value = "/packDownload")
    public void packDownload(@RequestParam String[] ids, HttpServletResponse response, HttpServletRequest request) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            List<MergeFileModel> list = mergeFileApi.findByIds(tenantId, ids).getData();
            // 拼接zip文件,之后下载下来的压缩文件的名字
            String base_name = "合并文件" + new Date().getTime();
            String fileZip = base_name + ".zip";
            String packDownloadPath = Y9Context.getWebRootRealPath() + "static" + File.separator + "packDownload";
            File folder = new File(packDownloadPath);
            if (!folder.exists() && !folder.isDirectory()) {
                folder.mkdirs();
            }
            String zipPath = packDownloadPath + File.separator + fileZip;
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(zipPath));
            ZipOutputStream zos = new ZipOutputStream(bos);
            ZipEntry ze;
            for (MergeFileModel file : list) {
                String filename = file.getFileName();
                String fileStoreId = file.getFileStoreId();
                byte[] filebyte = y9FileStoreService.downloadFileToBytes(fileStoreId);
                InputStream bis = new ByteArrayInputStream(filebyte);
                ze = new ZipEntry(filename);
                zos.putNextEntry(ze);
                int s;
                while ((s = bis.read()) != -1) {
                    zos.write(s);
                }
                bis.close();
            }
            zos.flush();
            zos.close();
            boolean b = request.getHeader("User-Agent").toLowerCase().indexOf(BrowserTypeEnum.FIREFOX.getValue()) > 0;
            if (b) {
                fileZip = new String(fileZip.getBytes(StandardCharsets.UTF_8), "ISO8859-1");
            } else {
                fileZip = URLEncoder.encode(fileZip, StandardCharsets.UTF_8);
            }
            OutputStream out = response.getOutputStream();
            response.reset();
            response.setHeader("Content-disposition", "attachment; filename=\"" + fileZip + "\"");
            response.setHeader("Content-type", "text/html;charset=UTF-8");
            response.setContentType("application/octet-stream");
            // 浏览器下载临时文件的路径
            DataInputStream in = new DataInputStream(new FileInputStream(zipPath));
            byte[] byte1 = new byte[2048];
            // 之后用来删除临时压缩文件
            File reportZip = new File(zipPath);
            try {
                while ((in.read(byte1)) != -1) {
                    out.write(byte1);
                }
                out.flush();
            } catch (Exception e) {
                LOGGER.error("下载失败", e);
            } finally {
                if (out != null) {
                    out.close();
                }
                in.close();
                // 删除服务器本地产生的临时压缩文件
                reportZip.delete();
            }
        } catch (Exception e) {
            LOGGER.error("下载失败", e);
        }
    }

    /**
     * 修改文件名称
     *
     * @param id 文件id
     * @param fileName 文件名称
     * @return Y9Result<Object>
     */
    @FlowableLog(operationName = "修改文件名称", operationType = FlowableOperationTypeEnum.SAVE)
    @PostMapping(value = "/updateFileName")
    public Y9Result<Object> updateFileName(@RequestParam String id, @RequestParam String fileName) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        return mergeFileApi.updateFileName(tenantId, id, fileName);
    }

    /**
     * 文件合并上传文件
     *
     * @param file 文件
     * @return Y9Result<String>
     */
    @FlowableLog(operationName = "文件合并上传文件", operationType = FlowableOperationTypeEnum.UPLOAD)
    @PostMapping(value = "/upload")
    public Y9Result<String> upload(MultipartFile file) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId(), tenantId = Y9LoginUserHolder.getTenantId();
        try {
            String originalFilename = file.getOriginalFilename();
            String fileName = FilenameUtils.getName(originalFilename);
            String fullPath = "/" + Y9Context.getSystemName() + "/" + tenantId + "/mergeFile" + "/" + userId;
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(file, fullPath, fileName);
            String storeId = y9FileStore.getId();
            return Y9Result.success(storeId);
        } catch (Exception e) {
            LOGGER.error("上传失败", e);
        }
        return Y9Result.failure("上传失败");
    }

    @FlowableLog(operationName = "不同格式文件合并转OFD", operationType = FlowableOperationTypeEnum.UPLOAD)
    @PostMapping(value = "/ofdX2y")
    public Y9Result<String> ofdX2y(String processSerialNumber, String fileStoreIds,String listType,String wordType) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId(), tenantId = Y9LoginUserHolder.getTenantId();
        String downloadBasePath = Y9Context.getProperty("y9.common.flowableBaseUrl");
        Date now = new Date();
        try {
            String converterFileUri = Y9Context.getProperty("y9.common.ofdConverterBaseUrl") + "/sync/common/x2y";
            String addPagerNumberUri = Y9Context.getProperty("y9.common.ofdConverterBaseUrl") + "/sync/common/addPagerNumber";
            String[] fileStoreIdArray = fileStoreIds.split(",");
            JsonArray components = new JsonArray();

            for (String fileStoreId : fileStoreIdArray) {
                Y9FileStore y9FileStore = y9FileStoreService.getById(fileStoreId);
                if(y9FileStore != null){
                    String fileLoc =  downloadBasePath + "/s/" +y9FileStore.getId();
                    String format = y9FileStore.getFileExt();
                    JsonObject temp = new JsonObject();
                    temp.addProperty("fileLoc", fileLoc);
                    temp.addProperty("format", format);
                    components.add(temp);
                }
            }
            JsonObject convertJsonObject = new JsonObject();
            convertJsonObject.add("components", components);
            convertJsonObject.addProperty("target", "ofd");
            String convertParam = convertJsonObject.toString();
            LOGGER.info("----------文件转换服务请求参数:{}", convertParam);
            //开始调用数科转换服务-合并文件
            byte[] result = HttpRequestUtil.postFile(converterFileUri, convertParam);
            if(result != null){
                Y9FileStore y9FileStore  = y9FileStoreService.uploadFile(result, "ofd", "合并文件.ofd");
                if(y9FileStore != null){
                    JsonObject setPageJson = new JsonObject();
                    setPageJson.addProperty("target", "ofd");
                    String fileLoc = downloadBasePath + "/s/" + y9FileStore.getId();
                    String format = y9FileStore.getFileExt();
                    JsonArray componestsArray = new JsonArray();
                    JsonObject componentJson = new JsonObject();
                    componentJson.addProperty("fileLoc", fileLoc);
                    componentJson.addProperty("format", format);
                    componestsArray.add(componentJson);
                    setPageJson.add("componests", componestsArray);
                    JsonArray pageNumberArray = new JsonArray();
                    JsonObject pageNumber = new JsonObject();
                    pageNumber.addProperty("autoAdaptOrientation", "");
                    pageNumber.addProperty("bold", true);
                    pageNumber.addProperty("fontName", "TimesNewRoman");
                    pageNumber.addProperty("fontSize", 12);
                    pageNumber.addProperty("foreColor", "#ff0000");
                    pageNumber.addProperty("italic", true);
                    pageNumber.addProperty("operator", "Add");
                    pageNumber.addProperty("orientation", "");
                    pageNumber.addProperty("pageNumberFormat", "--${PageNumber}--");
                    pageNumber.addProperty("pagePadding", "");
                    pageNumber.addProperty("pageRange", "all");
                    pageNumber.addProperty("predefinedPosition", "");
                    pageNumber.addProperty("rotate", 45);
                    pageNumber.addProperty("startNumber", 1);
                    pageNumber.addProperty("step", 1);
                    pageNumber.addProperty("x", "center");
                    pageNumber.addProperty("y", "bottom");
                    pageNumberArray.add(pageNumber);
                    setPageJson.add("pageNumber", pageNumberArray);
                    String setPageParam = setPageJson.toString();
                    LOGGER.info("----------文件转换服务----设置页码请求参数:{}", setPageParam);
                    byte[] setPageResult = HttpRequestUtil.postFile(addPagerNumberUri, setPageParam);
                    if(setPageResult != null){
                        Y9FileStore y9FileStoreSetPage  = y9FileStoreService.uploadFile(setPageResult, "ofd", "合并文件.ofd");
                        MergeFileModel mergeFileModel = new MergeFileModel();
                        String id = Y9IdGenerator.genId(IdType.SNOWFLAKE);
                        String fileName = "合并文件" + DateUtil.format(now,"yyyy-MM-dd HH:mm:ss") +".ofd";
                        mergeFileModel.setId(id);
                        mergeFileModel.setFileName(fileName);
                        mergeFileModel.setListType(listType);
                        mergeFileModel.setFileStoreId(y9FileStoreSetPage.getId());
                        mergeFileModel.setProcessSerialNumber(StringUtils.isBlank(processSerialNumber) ? "" : processSerialNumber);
                        mergeFileModel.setPersonName(person.getName());
                        mergeFileModel.setPersonId(userId);
                        mergeFileModel.setFileType(wordType);// 存wordType
                        mergeFileModel.setCreateTime(DateUtil.format(now, "yyyy-MM-dd HH:mm:ss"));
                        mergeFileModel.setSourceFileId("");
                        mergeFileApi.saveMergeFile(tenantId, mergeFileModel);
                    }
                }

            }
        } catch (Exception e) {
            LOGGER.error("转换失败", e);
            return Y9Result.failure("转换失败");
        }
        return Y9Result.success("转换成功");
    }
}
