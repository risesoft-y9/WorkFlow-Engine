package net.risesoft.web.controller;

import static net.risesoft.utils.CaptchaUtil.CAPTCHA_CODE;
import static net.risesoft.utils.CaptchaUtil.CAPTCHA_GENERATE_TIME;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.ObjectUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.config.ConfigConstants;
import net.risesoft.model.ReturnResponse;
import net.risesoft.utils.CaptchaUtil;
import net.risesoft.utils.DateUtils;
import net.risesoft.utils.KkFileUtils;
import net.risesoft.utils.RarUtils;
import net.risesoft.utils.UrlEncoderUtils;
import net.risesoft.utils.WebUtils;

@RestController
@Slf4j
public class FileController {

    public static final String BASE64_DECODE_ERROR_MSG = "Base64解码失败，请检查你的 %s 是否采用 Base64 + urlEncode 双重编码了！";
    private final String fileDir = ConfigConstants.getFileDir();
    private final String demoDir = "demo";
    private final String demoPath = demoDir + File.separator;

    @PostMapping("/fileUpload")
    public ReturnResponse<Object> fileUpload(@RequestParam("file") MultipartFile file) {
        ReturnResponse<Object> checkResult = this.fileUploadCheck(file);
        if (checkResult.isFailure()) {
            return checkResult;
        }
        File outFile = new File(fileDir + demoPath);
        if (!outFile.exists() && !outFile.mkdirs()) {
            LOGGER.error("创建文件夹【{}】失败，请检查目录权限！", fileDir + demoPath);
        }
        String fileName = checkResult.getContent().toString();
        LOGGER.info("上传文件：{}{}{}", fileDir, demoPath, fileName);
        try (InputStream in = file.getInputStream();
            OutputStream out = Files.newOutputStream(Paths.get(fileDir + demoPath + fileName))) {
            StreamUtils.copy(in, out);
            return ReturnResponse.success(null);
        } catch (IOException e) {
            LOGGER.error("文件上传失败", e);
            return ReturnResponse.failure();
        }
    }

    @GetMapping("/deleteFile")
    public ReturnResponse<Object> deleteFile(HttpServletRequest request, String fileName, String password) {
        ReturnResponse<Object> checkResult = this.deleteFileCheck(request, fileName, password);
        if (checkResult.isFailure()) {
            return checkResult;
        }
        fileName = checkResult.getContent().toString();
        File file = new File(fileDir + demoPath + fileName);
        LOGGER.info("删除文件：{}", file.getAbsolutePath());
        if (file.exists() && !file.delete()) {
            String msg = String.format("删除文件【%s】失败，请检查目录权限！", file.getPath());
            LOGGER.error(msg);
            return ReturnResponse.failure(msg);
        }
        WebUtils.removeSessionAttr(request, CAPTCHA_CODE); // 删除缓存验证码
        return ReturnResponse.success();
    }

    /**
     * 验证码方法
     */
    @RequestMapping("/deleteFile/captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!ConfigConstants.getDeleteCaptcha()) {
            return;
        }

        response.setContentType("image/jpeg");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", -1);
        String captchaCode = WebUtils.getSessionAttr(request, CAPTCHA_CODE);
        long captchaGenerateTime = WebUtils.getLongSessionAttr(request, CAPTCHA_GENERATE_TIME);
        long timeDifference = DateUtils.calculateCurrentTimeDifference(captchaGenerateTime);

        // 验证码为空，且生成验证码超过50秒，重新生成验证码
        if (timeDifference > 50 && ObjectUtils.isEmpty(captchaCode)) {
            captchaCode = CaptchaUtil.generateCaptchaCode();
            // 更新验证码
            WebUtils.setSessionAttr(request, CAPTCHA_CODE, captchaCode);
            WebUtils.setSessionAttr(request, CAPTCHA_GENERATE_TIME, DateUtils.getCurrentSecond());
        } else {
            captchaCode = ObjectUtils.isEmpty(captchaCode) ? "wait" : captchaCode;
        }

        ServletOutputStream outputStream = response.getOutputStream();
        ImageIO.write(CaptchaUtil.generateCaptchaPic(captchaCode), "jpeg", outputStream);
        outputStream.close();
    }

    @GetMapping("/listFiles")
    public List<Map<String, String>> getFiles() {
        List<Map<String, String>> list = new ArrayList<>();
        File file = new File(fileDir + demoPath);
        if (file.exists()) {
            File[] files = Objects.requireNonNull(file.listFiles());
            Arrays.sort(files, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
            Arrays.stream(files).forEach(file1 -> {
                Map<String, String> fileName = new HashMap<>();
                fileName.put("fileName", demoDir + "/" + file1.getName());
                list.add(fileName);
            });
        }
        return list;
    }

    /**
     * 上传文件前校验
     *
     * @param file 文件
     * @return 校验结果
     */
    private ReturnResponse<Object> fileUploadCheck(MultipartFile file) {
        if (ConfigConstants.getFileUploadDisable()) {
            return ReturnResponse.failure("文件传接口已禁用");
        }
        String fileName = WebUtils.getFileNameFromMultipartFile(file);
        if (fileName.lastIndexOf(".") == -1) {
            return ReturnResponse.failure("不允许上传的类型");
        }
        if (!KkFileUtils.isAllowedUpload(fileName)) {
            return ReturnResponse.failure("不允许上传的文件类型: " + fileName);
        }
        if (KkFileUtils.isIllegalFileName(fileName)) {
            return ReturnResponse.failure("不允许上传的文件名: " + fileName);
        }
        // 判断是否存在同名文件
        if (existsFile(fileName)) {
            return ReturnResponse.failure("存在同名文件，请先删除原有文件再次上传");
        }
        return ReturnResponse.success(fileName);
    }

    /**
     * 删除文件前校验
     *
     * @param fileName 文件名
     * @return 校验结果
     */
    private ReturnResponse<Object> deleteFileCheck(HttpServletRequest request, String fileName, String password) {
        if (ObjectUtils.isEmpty(fileName)) {
            return ReturnResponse.failure("文件名为空，删除失败！");
        }
        try {
            if (UrlEncoderUtils.isBase64EncodedUrl(fileName)) {
                fileName = WebUtils.decodeUrl(fileName);
            }

        } catch (Exception ex) {
            String errorMsg = String.format(BASE64_DECODE_ERROR_MSG, fileName);
            return ReturnResponse.failure(errorMsg + "删除失败！");
        }
        assert fileName != null;
        if (fileName.contains("/")) {
            fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
        }
        if (KkFileUtils.isIllegalFileName(fileName)) {
            return ReturnResponse.failure("非法文件名，删除失败！");
        }
        if (ObjectUtils.isEmpty(password)) {
            return ReturnResponse.failure("密码 or 验证码为空，删除失败！");
        }

        String expectedPassword = ConfigConstants.getDeleteCaptcha() ? WebUtils.getSessionAttr(request, CAPTCHA_CODE)
            : ConfigConstants.getPassword();

        if (!password.equalsIgnoreCase(expectedPassword)) {
            LOGGER.error("删除文件【{}】失败，密码错误！", fileName);
            return ReturnResponse.failure("删除文件失败，密码错误！");
        }
        return ReturnResponse.success(fileName);
    }

    @GetMapping("/directory")
    public Object directory(String urls) {
        String fileUrl;
        try {
            if (UrlEncoderUtils.isBase64EncodedUrl(urls)) {
                fileUrl = WebUtils.decodeUrl(urls);
            } else {
                fileUrl = urls;
            }

        } catch (Exception ex) {
            String errorMsg = String.format(BASE64_DECODE_ERROR_MSG, "url");
            return ReturnResponse.failure(errorMsg);
        }
        fileUrl = fileUrl.replaceAll("http://", "");
        if (KkFileUtils.isIllegalFileName(fileUrl)) {
            return ReturnResponse.failure("不允许访问的路径:");
        }
        return RarUtils.getTree(fileUrl);
    }

    private boolean existsFile(String fileName) {
        File file = new File(fileDir + demoPath + fileName);
        return file.exists();
    }
}
