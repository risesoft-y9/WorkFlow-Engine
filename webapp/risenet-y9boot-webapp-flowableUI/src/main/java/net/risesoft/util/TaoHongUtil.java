package net.risesoft.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.xwpf.NiceXWPFDocument;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.TransactionWordApi;
import net.risesoft.y9.Y9Context;

import cn.wps.yun.api.AppFilesApi;
import cn.wps.yun.model.FileContent;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@Configuration
@EnableScheduling
@Slf4j
public class TaoHongUtil {

    /**
     * 云文档路径
     */
    private static String yunWpsBasePath4Graph = "http://yun.test.cn/graph";

    /**
     * 应用id
     */
    private static String yunWpsAppId = "4a1291d0-b753-4c2b-0000-000000000005";

    /**
     * 应用密码
     */
    private static String yunWpsAppSecret = "u5x7yWKFjsSB";

    /**
     * APP权限
     */
    private static String yunWpsAppScope = "App.Files.Read App.Files.ReadWrite";

    /**
     * 云文档下载路径
     */
    private static String yunWpsDownloadPath = "http://yun.test.cn/minio";

    public TaoHongUtil() {

    }

    /**
     * @param content 公文地址
     * @param destDocx 保存的文件地址
     */
    public void word2RedDocument(String content, String destDocx) {
        try {
            // 模板文件地址
            // String model = "C:\\Users\\10858\\Desktop\\开发资料\\深圳罗湖区.docx";
            // 模板文件 参数填写
            // model = Y9Context.getWebRootRealPath() + "static" + File.separator +
            // "official_doc_model.docx";
            String contentStr =
                Y9Context.getBean(TransactionWordApi.class).openDocumentTemplate("c425281829dc4d4496ddddf7fc0198d0",
                    "3cfe10631fb348bfaadd21045f0f0659", "67ea3abfc53b4ca88de409d2a7744a1a");
            // ByteArrayInputStream bin = null;
            BufferedOutputStream bos = null;
            FileOutputStream fos = null;
            String filePath = "";
            try {
                byte[] result = null;
                result = jodd.util.Base64.decode(contentStr);
                filePath =
                    Y9Context.getWebRootRealPath() + "static" + File.separator + "word" + File.separator + "1111.docx";
                File file = null;
                file = new File(filePath);
                fos = new FileOutputStream(file);
                bos = new BufferedOutputStream(fos);
                bos.write(result);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            LOGGER.debug(filePath);
            XWPFTemplate template = XWPFTemplate.compile(
                "E:\\workspace-y9boot-9.4.0\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp1\\wtpwebapps\\risenet-y9boot-webapp-flowableUI\\static\\word\\1111.docx");
            // 获取模板文件 公文
            NiceXWPFDocument main = template.getXWPFDocument();
            String downloadUrl = "";
            AppFilesApi apiInstance =
                new AppFilesApi(yunWpsBasePath4Graph, yunWpsAppId, yunWpsAppSecret, yunWpsAppScope);
            try {
                FileContent result = apiInstance.appGetFileContent("6061", "305029920583589888", null);
                LOGGER.debug("FileContent: [{}]", result);
                downloadUrl = yunWpsDownloadPath + result.getUrl();
            } catch (Exception e) {
                LOGGER.warn("Exception when calling AppFilesApi#appGetFileContent", e);
            }

            NiceXWPFDocument sub = new NiceXWPFDocument(new FileInputStream(downloadUrl));
            // 合并两个文档
            NiceXWPFDocument newDoc = main.merge(sub);

            // 生成新文档
            FileOutputStream out = new FileOutputStream(destDocx);
            newDoc.write(out);
            newDoc.close();
            out.close();
        } catch (Exception e) {
            LOGGER.warn("Exception in word2RedDocument", e);
        }
    }

}
