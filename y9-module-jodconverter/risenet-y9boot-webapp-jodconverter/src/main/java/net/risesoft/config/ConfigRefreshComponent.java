package net.risesoft.config;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.PostConstruct;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.utils.ConfigUtils;

/**
 * @description 每隔1s读取并更新一次配置文件
 */
@Component
@Slf4j
@DependsOn("y9Context")
public class ConfigRefreshComponent {

    @PostConstruct
    void refresh() {
        Thread configRefreshThread = new Thread(new ConfigRefreshThread());
        configRefreshThread.start();
    }

    static class ConfigRefreshThread implements Runnable {

        @Override
        public void run() {
            try {
                Properties properties = new Properties();
                String text;
                String media;
                boolean cacheEnabled;
                String[] textArray;
                String[] mediaArray;
                String officePreviewType;
                String officePreviewSwitchDisabled;
                String ftpUsername;
                String ftpPassword;
                String ftpControlEncoding;
                String baseUrl;
                String trustHost;
                String notTrustHost;
                String pdfPresentationModeDisable;
                String pdfOpenFileDisable;
                String pdfPrintDisable;
                String pdfDownloadDisable;
                String pdfBookmarkDisable;
                String pdfDisableEditing;
                boolean fileUploadDisable;
                String tifPreviewType;
                String prohibit;
                String[] prohibitArray;
                String beian;
                String size;
                String password;
                int pdf2JpgDpi;
                String officeTypeWeb;
                String cadPreviewType;
                boolean deleteSourceFile;
                boolean deleteCaptcha;
                String officPageRange;
                String officWatermark;
                String officQuality;
                String officMaxImageResolution;
                boolean officExportBookmarks;
                boolean officeExportNotes;
                boolean officeDocumentOpenPasswords;
                String cadTimeout;
                int cadThread;
                String homePageNumber;
                String homePagination;
                String homePageSize;
                String homeSearch;
                int pdfTimeout;
                int pdfTimeout80;
                int pdfTimeout200;
                int pdfThread;
                while (true) {
                    properties = ConfigUtils.getInitProperties();
                    ConfigUtils.restorePropertiesFromEnvFormat(properties);

                    cacheEnabled = Boolean
                        .parseBoolean(properties.getProperty("cache.enabled", ConfigConstants.DEFAULT_CACHE_ENABLED));
                    text = properties.getProperty("simText", ConfigConstants.DEFAULT_TXT_TYPE);
                    media = properties.getProperty("media", ConfigConstants.DEFAULT_MEDIA_TYPE);
                    officePreviewType =
                        properties.getProperty("office.preview.type", ConfigConstants.DEFAULT_OFFICE_PREVIEW_TYPE);
                    officePreviewSwitchDisabled = properties.getProperty("office.preview.switch.disabled",
                        ConfigConstants.DEFAULT_OFFICE_PREVIEW_SWITCH_DISABLED);
                    ftpUsername = properties.getProperty("ftp.username", ConfigConstants.DEFAULT_FTP_USERNAME);
                    ftpPassword = properties.getProperty("ftp.password", ConfigConstants.DEFAULT_FTP_PASSWORD);
                    ftpControlEncoding =
                        properties.getProperty("ftp.control.encoding", ConfigConstants.DEFAULT_FTP_CONTROL_ENCODING);
                    textArray = text.split(",");
                    mediaArray = media.split(",");
                    baseUrl = properties.getProperty("base.url", ConfigConstants.DEFAULT_VALUE);
                    trustHost = properties.getProperty("trust.host", ConfigConstants.DEFAULT_VALUE);
                    notTrustHost = properties.getProperty("not.trust.host", ConfigConstants.DEFAULT_VALUE);
                    pdfPresentationModeDisable = properties.getProperty("pdf.presentationMode.disable",
                        ConfigConstants.DEFAULT_PDF_PRESENTATION_MODE_DISABLE);
                    pdfOpenFileDisable =
                        properties.getProperty("pdf.openFile.disable", ConfigConstants.DEFAULT_PDF_OPEN_FILE_DISABLE);
                    pdfPrintDisable =
                        properties.getProperty("pdf.print.disable", ConfigConstants.DEFAULT_PDF_PRINT_DISABLE);
                    pdfDownloadDisable =
                        properties.getProperty("pdf.download.disable", ConfigConstants.DEFAULT_PDF_DOWNLOAD_DISABLE);
                    pdfBookmarkDisable =
                        properties.getProperty("pdf.bookmark.disable", ConfigConstants.DEFAULT_PDF_BOOKMARK_DISABLE);
                    pdfDisableEditing =
                        properties.getProperty("pdf.disable.editing", ConfigConstants.DEFAULT_PDF_DISABLE_EDITING);
                    fileUploadDisable = Boolean.parseBoolean(
                        properties.getProperty("file.upload.disable", ConfigConstants.DEFAULT_FILE_UPLOAD_DISABLE));
                    tifPreviewType =
                        properties.getProperty("tif.preview.type", ConfigConstants.DEFAULT_TIF_PREVIEW_TYPE);
                    cadPreviewType =
                        properties.getProperty("cad.preview.type", ConfigConstants.DEFAULT_CAD_PREVIEW_TYPE);
                    size =
                        properties.getProperty("spring.servlet.multipart.max-file-size", ConfigConstants.DEFAULT_SIZE);
                    beian = properties.getProperty("beian", ConfigConstants.DEFAULT_BEIAN);
                    prohibit = properties.getProperty("prohibit", ConfigConstants.DEFAULT_PROHIBIT);
                    password = properties.getProperty("delete.password", ConfigConstants.DEFAULT_PD);
                    pdf2JpgDpi =
                        Integer.parseInt(properties.getProperty("pdf2jpg.dpi", ConfigConstants.DEFAULT_PDF2_JPG_DPI));
                    officeTypeWeb = properties.getProperty("office.type.web", ConfigConstants.DEFAULT_OFFICE_TYPE_WEB);
                    deleteSourceFile = Boolean.parseBoolean(
                        properties.getProperty("delete.source.file", ConfigConstants.DEFAULT_DELETE_SOURCE_FILE));
                    deleteCaptcha = Boolean
                        .parseBoolean(properties.getProperty("delete.captcha", ConfigConstants.DEFAULT_DELETE_CAPTCHA));
                    officPageRange =
                        properties.getProperty("office.pagerange", ConfigConstants.DEFAULT_OFFICE_PAQERANQE);
                    officWatermark =
                        properties.getProperty("office.watermark", ConfigConstants.DEFAULT_OFFICE_WATERMARK);
                    officQuality = properties.getProperty("office.quality", ConfigConstants.DEFAULT_OFFICE_QUALITY);
                    officMaxImageResolution = properties.getProperty("office.maximageresolution",
                        ConfigConstants.DEFAULT_OFFICE_MAXIMAQERESOLUTION);
                    officExportBookmarks = Boolean.parseBoolean(properties.getProperty("office.exportbookmarks",
                        ConfigConstants.DEFAULT_OFFICE_EXPORTBOOKMARKS));
                    officeExportNotes = Boolean.parseBoolean(
                        properties.getProperty("office.exportnotes", ConfigConstants.DEFAULT_OFFICE_EXPORTNOTES));
                    officeDocumentOpenPasswords = Boolean.parseBoolean(properties
                        .getProperty("office.documentopenpasswords", ConfigConstants.DEFAULT_OFFICE_EOCUMENTOPENP));
                    cadTimeout = properties.getProperty("cad.timeout", ConfigConstants.DEFAULT_CAD_TIMEOUT);
                    homePageNumber = properties.getProperty("home.pagenumber", ConfigConstants.DEFAULT_HOME_PAGENUMBER);
                    homePagination = properties.getProperty("home.pagination", ConfigConstants.DEFAULT_HOME_PAGINATION);
                    homePageSize = properties.getProperty("home.pagesize", ConfigConstants.DEFAULT_HOME_PAGSIZE);
                    homeSearch = properties.getProperty("home.search", ConfigConstants.DEFAULT_HOME_SEARCH);
                    cadThread =
                        Integer.parseInt(properties.getProperty("cad.thread", ConfigConstants.DEFAULT_CAD_THREAD));
                    pdfTimeout =
                        Integer.parseInt(properties.getProperty("pdf.timeout", ConfigConstants.DEFAULT_PDF_TIMEOUT));
                    pdfTimeout80 = Integer
                        .parseInt(properties.getProperty("pdf.timeout80", ConfigConstants.DEFAULT_PDF_TIMEOUT80));
                    pdfTimeout200 = Integer
                        .parseInt(properties.getProperty("pdf.timeout200", ConfigConstants.DEFAULT_PDF_TIMEOUT200));
                    pdfThread =
                        Integer.parseInt(properties.getProperty("pdf.thread", ConfigConstants.DEFAULT_PDF_THREAD));
                    prohibitArray = prohibit.split(",");

                    ConfigConstants.setCacheEnabledValueValue(cacheEnabled);
                    ConfigConstants.setSimTextValue(textArray);
                    ConfigConstants.setMediaValue(mediaArray);
                    ConfigConstants.setOfficePreviewTypeValue(officePreviewType);
                    ConfigConstants.setFtpUsernameValue(ftpUsername);
                    ConfigConstants.setFtpPasswordValue(ftpPassword);
                    ConfigConstants.setFtpControlEncodingValue(ftpControlEncoding);
                    ConfigConstants.setBaseUrlValue(baseUrl);
                    ConfigConstants.setTrustHostValue(trustHost);
                    ConfigConstants.setNotTrustHostValue(notTrustHost);
                    ConfigConstants.setOfficePreviewSwitchDisabledValue(officePreviewSwitchDisabled);
                    ConfigConstants.setPdfPresentationModeDisableValue(pdfPresentationModeDisable);
                    ConfigConstants.setPdfOpenFileDisableValue(pdfOpenFileDisable);
                    ConfigConstants.setPdfPrintDisableValue(pdfPrintDisable);
                    ConfigConstants.setPdfDownloadDisableValue(pdfDownloadDisable);
                    ConfigConstants.setPdfBookmarkDisableValue(pdfBookmarkDisable);
                    ConfigConstants.setPdfDisableEditingValue(pdfDisableEditing);
                    ConfigConstants.setFileUploadDisableValue(fileUploadDisable);
                    ConfigConstants.setTifPreviewTypeValue(tifPreviewType);
                    ConfigConstants.setCadPreviewTypeValue(cadPreviewType);
                    ConfigConstants.setBeianValue(beian);
                    ConfigConstants.setSizeValue(size);
                    ConfigConstants.setProhibitValue(prohibitArray);
                    ConfigConstants.setPasswordValue(password);
                    ConfigConstants.setPdf2JpgDpiValue(pdf2JpgDpi);
                    ConfigConstants.setOfficeTypeWebValue(officeTypeWeb);
                    ConfigConstants.setOfficePageRangeValue(officPageRange);
                    ConfigConstants.setOfficeWatermarkValue(officWatermark);
                    ConfigConstants.setOfficeQualityValue(officQuality);
                    ConfigConstants.setOfficeMaxImageResolutionValue(officMaxImageResolution);
                    ConfigConstants.setOfficeExportBookmarksValue(officExportBookmarks);
                    ConfigConstants.setOfficeExportNotesValue(officeExportNotes);
                    ConfigConstants.setOfficeDocumentOpenPasswordsValue(officeDocumentOpenPasswords);
                    ConfigConstants.setDeleteSourceFileValue(deleteSourceFile);
                    ConfigConstants.setDeleteCaptchaValue(deleteCaptcha);
                    ConfigConstants.setCadTimeoutValue(cadTimeout);
                    ConfigConstants.setCadThreadValue(cadThread);
                    ConfigConstants.setHomePageNumberValue(homePageNumber);
                    ConfigConstants.setHomePaginationValue(homePagination);
                    ConfigConstants.setHomePageSizeValue(homePageSize);
                    ConfigConstants.setHomeSearchValue(homeSearch);
                    ConfigConstants.setPdfTimeoutValue(pdfTimeout);
                    ConfigConstants.setPdfTimeout80Value(pdfTimeout80);
                    ConfigConstants.setPdfTimeout200Value(pdfTimeout200);
                    ConfigConstants.setPdfThreadValue(pdfThread);
                    setWatermarkConfig(properties);
                    TimeUnit.SECONDS.sleep(1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.error("读取配置文件异常", e);
            }
        }

        private void setWatermarkConfig(Properties properties) {
            String watermarkTxt =
                properties.getProperty("watermark.txt", WatermarkConfigConstants.DEFAULT_WATERMARK_TXT);
            String watermarkXSpace =
                properties.getProperty("watermark.x.space", WatermarkConfigConstants.DEFAULT_WATERMARK_X_SPACE);
            String watermarkYSpace =
                properties.getProperty("watermark.y.space", WatermarkConfigConstants.DEFAULT_WATERMARK_Y_SPACE);
            String watermarkFont =
                properties.getProperty("watermark.font", WatermarkConfigConstants.DEFAULT_WATERMARK_FONT);
            String watermarkFontsize =
                properties.getProperty("watermark.fontsize", WatermarkConfigConstants.DEFAULT_WATERMARK_FONTSIZE);
            String watermarkColor =
                properties.getProperty("watermark.color", WatermarkConfigConstants.DEFAULT_WATERMARK_COLOR);
            String watermarkAlpha =
                properties.getProperty("watermark.alpha", WatermarkConfigConstants.DEFAULT_WATERMARK_ALPHA);
            String watermarkWidth =
                properties.getProperty("watermark.width", WatermarkConfigConstants.DEFAULT_WATERMARK_WIDTH);
            String watermarkHeight =
                properties.getProperty("watermark.height", WatermarkConfigConstants.DEFAULT_WATERMARK_HEIGHT);
            String watermarkAngle =
                properties.getProperty("watermark.angle", WatermarkConfigConstants.DEFAULT_WATERMARK_ANGLE);
            WatermarkConfigConstants.setWatermarkTxtValue(watermarkTxt);
            WatermarkConfigConstants.setWatermarkXSpaceValue(watermarkXSpace);
            WatermarkConfigConstants.setWatermarkYSpaceValue(watermarkYSpace);
            WatermarkConfigConstants.setWatermarkFontValue(watermarkFont);
            WatermarkConfigConstants.setWatermarkFontsizeValue(watermarkFontsize);
            WatermarkConfigConstants.setWatermarkColorValue(watermarkColor);
            WatermarkConfigConstants.setWatermarkAlphaValue(watermarkAlpha);
            WatermarkConfigConstants.setWatermarkWidthValue(watermarkWidth);
            WatermarkConfigConstants.setWatermarkHeightValue(watermarkHeight);
            WatermarkConfigConstants.setWatermarkAngleValue(watermarkAngle);

        }
    }
}
