package net.risesoft.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
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

@Configuration
@EnableScheduling
@Slf4j
public class TaoHongUtil {

    /**
     * 云文档路径
     */
    private static final String yunWpsBasePath = "http://yun.test.cn";

    /**
     * 云文档路径
     */
    private static final String yunWpsBasePath4Graph = "http://yun.test.cn/graph";

    /**
     * 应用id
     */
    private static final String yunWpsAppId = "4a1291d0-b753-4c2b-0000-000000000005";

    /**
     * 应用密码
     */
    private static final String yunWpsAppSecret = "u5x7yWKFjsSB";

    /**
     * 回调地址
     */
    private static final String yunWpsRedirectUri = "https://www.risesoft.net/";

    /**
     * APP权限
     */
    private static final String yunWpsAppScope = "App.Files.Read App.Files.ReadWrite";

    /**
     * 人员权限
     */
    private static final String yunWpsUserScope = "User.Profile.Read";

    /**
     * 人员账号
     */
    private static final String yunWpsUserName = "test1";

    /**
     * 密码
     */
    private static final String yunWpsUserPassword = "Aa123456";

    /**
     * 云文档下载路径
     */
    private static final String yunWpsDownloadPath = "http://yun.test.cn/minio";

    /**
     * 卷标识
     */
    private static final String volume = "workspace";

    /**
     * 文件标识，当值为\"root\"时表示根文件夹。
     */
    private static final String root = "root";

    public TaoHongUtil() {

    }

    /**
     * @param content 公文地址
     * @param data 需要填写的参数
     * @param destDocx 保存的文件地址
     */
    public void word2RedDocument(String content, String destDocx) {
        try {
            // 模板文件地址
            String model = "";
            // 模板文件 参数填写
            // model = Y9Context.getWebRootRealPath() + "static" + File.separator +
            // "official_doc_model.docx";
            String contentStr =
                Y9Context.getBean(TransactionWordApi.class).openDocumentTemplate("c425281829dc4d4496ddddf7fc0198d0",
                    "3cfe10631fb348bfaadd21045f0f0659", "67ea3abfc53b4ca88de409d2a7744a1a").getData();
            ByteArrayInputStream bin = null;
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
            XWPFTemplate template = XWPFTemplate.compile(
                "E:\\workspace-y9boot-9.4.0\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp1\\wtpwebapps\\risenet-y9boot-webapp-flowableUI\\static\\word\\1111.docx");
            // 获取模板文件 公文
            NiceXWPFDocument main = template.getXWPFDocument();
            String downloadUrl = "";
            AppFilesApi apiInstance =
                new AppFilesApi(yunWpsBasePath4Graph, yunWpsAppId, yunWpsAppSecret, yunWpsAppScope);
            try {
                FileContent result = apiInstance.appGetFileContent("6061", "305029920583589888", null);
                LOGGER.debug("result:{}", result);
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
            e.printStackTrace();
        }
    }

}
