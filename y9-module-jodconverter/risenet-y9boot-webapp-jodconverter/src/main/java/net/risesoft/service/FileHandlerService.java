package net.risesoft.service;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.apache.poi.EncryptedDocumentException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.aspose.cad.CodePages;
import com.aspose.cad.Color;
import com.aspose.cad.Image;
import com.aspose.cad.InterruptionTokenSource;
import com.aspose.cad.LoadOptions;
import com.aspose.cad.fileformats.cad.CadDrawTypeMode;
import com.aspose.cad.fileformats.tiff.enums.TiffExpectedFormat;
import com.aspose.cad.imageoptions.CadRasterizationOptions;
import com.aspose.cad.imageoptions.PdfOptions;
import com.aspose.cad.imageoptions.RasterizationQuality;
import com.aspose.cad.imageoptions.RasterizationQualityValue;
import com.aspose.cad.imageoptions.SvgOptions;
import com.aspose.cad.imageoptions.TiffOptions;
import com.aspose.cad.imageoptions.VisibilityMode;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.config.ConfigConstants;
import net.risesoft.model.FileAttribute;
import net.risesoft.model.FileType;
import net.risesoft.service.cache.CacheService;
import net.risesoft.service.cache.NotResourceCache;
import net.risesoft.utils.EncodingDetects;
import net.risesoft.utils.KkFileUtils;
import net.risesoft.utils.UrlEncoderUtils;
import net.risesoft.utils.WebUtils;
import net.risesoft.web.filter.BaseUrlFilter;

@Component
@Slf4j
@DependsOn(ConfigConstants.BEAN_NAME)
public class FileHandlerService implements InitializingBean {

    private static final String PDF2JPG_IMAGE_FORMAT = ".jpg";
    private static final String PDF_PASSWORD_MSG = "password";
    private final String fileDir = ConfigConstants.getFileDir();
    private final CacheService cacheService;
    @Value("${server.tomcat.uri-encoding:UTF-8}")
    private String uriEncoding;
    /**
     * cad定义线程池
     */
    private ExecutorService pool = null;

    public FileHandlerService(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    /**
     * @param str 原字符串（待截取原串）
     * @param posStr 指定字符串
     * @return 截取截取指定字符串之后的数据
     */
    public static String getSubString(String str, String posStr) {
        return str.substring(str.indexOf(posStr) + posStr.length());
    }

    /**
     * 添加转换后PDF缓存
     *
     * @param fileName pdf文件名
     * @param value 缓存相对路径
     */
    public void addConvertedFile(String fileName, String value) {
        cacheService.putPDFCache(fileName, value);
    }

    /**
     * 添加转换后的视频文件缓存
     *
     * @param fileName
     * @param value
     */
    public void addConvertedMedias(String fileName, String value) {
        cacheService.putMediaConvertCache(fileName, value);
    }

    /**
     * 添加转换后图片组缓存
     *
     * @param pdfFilePath pdf文件绝对路径
     * @param num 图片张数
     */
    public void addPdf2jpgCache(String pdfFilePath, int num) {
        cacheService.putPdfImageCache(pdfFilePath, num);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        pool = Executors.newFixedThreadPool(ConfigConstants.getCadThread());
    }

    /**
     * cad文件转pdf
     *
     * @param inputFilePath cad文件路径
     * @param outputFilePath pdf输出文件路径
     * @return 转换是否成功
     */
    public String cadToPdf(String inputFilePath, String outputFilePath, String cadPreviewType,
        FileAttribute fileAttribute) throws Exception {
        final InterruptionTokenSource source = new InterruptionTokenSource();// CAD延时
        final SvgOptions SvgOptions = new SvgOptions();
        final PdfOptions pdfOptions = new PdfOptions();
        final TiffOptions TiffOptions = new TiffOptions(TiffExpectedFormat.TiffJpegRgb);
        if (fileAttribute.isCompressFile()) { // 判断 是压缩包的创建新的目录
            int index = outputFilePath.lastIndexOf("/"); // 截取最后一个斜杠的前面的内容
            String folder = outputFilePath.substring(0, index);
            File path = new File(folder);
            // 目录不存在 创建新的目录
            if (!path.exists()) {
                path.mkdirs();
            }
        }
        File outputFile = new File(outputFilePath);
        try {
            LoadOptions opts = new LoadOptions();
            opts.setSpecifiedEncoding(CodePages.SimpChinese);
            final Image cadImage = Image.load(inputFilePath, opts);
            try {
                RasterizationQuality rasterizationQuality = new RasterizationQuality();
                rasterizationQuality.setArc(RasterizationQualityValue.High);
                rasterizationQuality.setHatch(RasterizationQualityValue.High);
                rasterizationQuality.setText(RasterizationQualityValue.High);
                rasterizationQuality.setOle(RasterizationQualityValue.High);
                rasterizationQuality.setObjectsPrecision(RasterizationQualityValue.High);
                rasterizationQuality.setTextThicknessNormalization(true);
                CadRasterizationOptions cadRasterizationOptions = new CadRasterizationOptions();
                cadRasterizationOptions.setBackgroundColor(Color.getWhite());
                cadRasterizationOptions.setPageWidth(cadImage.getWidth());
                cadRasterizationOptions.setPageHeight(cadImage.getHeight());
                cadRasterizationOptions.setUnitType(cadImage.getUnitType());
                cadRasterizationOptions.setAutomaticLayoutsScaling(false);
                cadRasterizationOptions.setNoScaling(false);
                cadRasterizationOptions.setQuality(rasterizationQuality);
                cadRasterizationOptions.setDrawType(CadDrawTypeMode.UseObjectColor);
                cadRasterizationOptions.setExportAllLayoutContent(true);
                cadRasterizationOptions.setVisibilityMode(VisibilityMode.AsScreen);
                switch (cadPreviewType) { // 新增格式方法
                    case "svg":
                        SvgOptions.setVectorRasterizationOptions(cadRasterizationOptions);
                        SvgOptions.setInterruptionToken(source.getToken());
                        break;
                    case "pdf":
                        pdfOptions.setVectorRasterizationOptions(cadRasterizationOptions);
                        pdfOptions.setInterruptionToken(source.getToken());
                        break;
                    case "tif":
                        TiffOptions.setVectorRasterizationOptions(cadRasterizationOptions);
                        TiffOptions.setInterruptionToken(source.getToken());
                        break;
                    default:
                        break;
                }
                Callable<String> call = () -> {
                    try (OutputStream stream = new FileOutputStream(outputFile)) {
                        switch (cadPreviewType) {
                            case "svg":
                                cadImage.save(stream, SvgOptions);
                                break;
                            case "pdf":
                                cadImage.save(stream, pdfOptions);
                                break;
                            case "tif":
                                cadImage.save(stream, TiffOptions);
                                break;
                            default:
                                break;
                        }
                    } catch (IOException e) {
                        LOGGER.error("CADFileNotFoundException，inputFilePath：{}", inputFilePath, e);
                        return null;
                    } finally {
                        cadImage.dispose();
                        source.interrupt(); // 结束任务
                        source.dispose();
                    }
                    return "true";
                };
                Future<String> result = pool.submit(call);
                try {
                    result.get(Long.parseLong(ConfigConstants.getCadTimeout()), TimeUnit.SECONDS);
                    // 如果在超时时间内，没有数据返回：则抛出TimeoutException异常
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.error("CAD转换文件异常：", e);
                    return null;
                } catch (ExecutionException e) {
                    LOGGER.error("CAD转换在尝试取得任务结果时出错：", e);
                    return null;
                } catch (TimeoutException e) {
                    LOGGER.error("CAD转换时间超时：", e);
                    return null;
                } finally {
                    source.interrupt(); // 结束任务
                    source.dispose();
                    cadImage.dispose();
                    // pool.shutdownNow();
                }
            } finally {
                source.dispose();
                cadImage.dispose();
            }
        } finally {
            source.dispose();
        }
        return "true";
    }

    /**
     * 对转换后的文件进行操作(改变编码方式)
     *
     * @param outFilePath 文件绝对路径
     */
    public void doActionConvertedFile(String outFilePath) {
        String charset = EncodingDetects.getJavaEncode(outFilePath);
        StringBuilder sb = new StringBuilder();
        try (InputStream inputStream = new FileInputStream(outFilePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charset))) {
            String line;
            while (null != (line = reader.readLine())) {
                if (line.contains("charset=gb2312")) {
                    line = line.replace("charset=gb2312", "charset=utf-8");
                }
                sb.append(line);
            }
            // 添加sheet控制头
            sb.append("<script src=\"js/jquery-3.6.1.min.js\" type=\"text/javascript\"></script>");
            sb.append("<script src=\"excel/excel.header.js\" type=\"text/javascript\"></script>");
            sb.append("<link rel=\"stylesheet\" href=\"excel/excel.css\">");
        } catch (IOException e) {
            LOGGER.error("修改文件编码失败，", e);
        }
        // 重新写入文件
        try (FileOutputStream fos = new FileOutputStream(outFilePath);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8))) {
            writer.write(sb.toString());
        } catch (IOException e) {
            LOGGER.error("修改文件编码失败，", e);
        }
    }

    /**
     * 获取缓存的文件名
     *
     * @return 文件名
     */
    private String getCacheFileName(FileType type, String originFileName, String cacheFilePrefixName,
        boolean isHtmlView, boolean isCompressFile) {
        String cacheFileName;
        if (type.equals(FileType.OFFICE)) {
            cacheFileName = cacheFilePrefixName + (isHtmlView ? "html" : "pdf"); // 生成文件添加类型后缀 防止同名文件
        } else if (type.equals(FileType.PDF)) {
            cacheFileName = originFileName;
        } else if (type.equals(FileType.MEDIACONVERT)) {
            cacheFileName = cacheFilePrefixName + "mp4";
        } else if (type.equals(FileType.CAD)) {
            String cadPreviewType = ConfigConstants.getCadPreviewType();
            cacheFileName = cacheFilePrefixName + cadPreviewType; // 生成文件添加类型后缀 防止同名文件
        } else if (type.equals(FileType.COMPRESS)) {
            cacheFileName = originFileName;
        } else if (type.equals(FileType.TIFF)) {
            cacheFileName = cacheFilePrefixName + ConfigConstants.getTifPreviewType();
        } else {
            cacheFileName = originFileName;
        }
        if (isCompressFile) { // 判断是否使用特定压缩包符号
            cacheFileName = "_decompression" + cacheFileName;
        }
        return cacheFileName;
    }

    /**
     * @return 已转换过的文件，根据文件名获取
     */
    public String getConvertedFile(String key) {
        return cacheService.getPDFCache(key);
    }

    /**
     * @return 已转换视频文件缓存，根据文件名获取
     */
    public String getConvertedMedias(String key) {
        return cacheService.getMediaConvertCache(key);
    }

    /**
     * 获取文件属性
     *
     * @param url url
     * @return 文件属性
     */
    public FileAttribute getFileAttribute(String url, HttpServletRequest req) {
        FileAttribute attribute = new FileAttribute();
        // 解析文件基本信息
        FileInfo fileInfo = parseFileInfo(url);
        String originFileName = fileInfo.originFileName;
        FileType type = fileInfo.type;
        String suffix = fileInfo.suffix;
        url = fileInfo.url;

        // 处理压缩文件相关逻辑
        String compressFileKey = WebUtils.getUrlParameterReg(url, "kkCompressfileKey");
        boolean isCompressFile = !ObjectUtils.isEmpty(compressFileKey);
        if (isCompressFile) {
            try {
                originFileName = URLDecoder.decode(originFileName, uriEncoding);
                attribute.setSkipDownLoad(true);
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("URL解码失败: {}", originFileName, e);
            }
        }

        // 处理URL编码
        originFileName = handleUrlEncoding(originFileName);

        // 处理未编码的URL
        if (!UrlEncoderUtils.hasUrlEncoded(originFileName)) {
            url = WebUtils.encodeUrlFileName(url);
        }

        // 文件名转义处理
        originFileName = KkFileUtils.htmlEscape(originFileName);

        // 判断是否为HTML视图
        boolean isHtmlView = isHtmlView(suffix);

        // 生成缓存文件前缀名
        String cacheFilePrefixName = generateCacheFilePrefix(originFileName, suffix);

        // 获取缓存文件名
        String cacheFileName =
            this.getCacheFileName(type, originFileName, cacheFilePrefixName, isHtmlView, isCompressFile);
        String outFilePath = fileDir + cacheFileName;
        String originFilePath = fileDir + originFileName;
        String cacheListName = cacheFilePrefixName + "ListName";

        // 设置文件属性
        attribute.setType(type);
        attribute.setName(originFileName);
        attribute.setCacheName(cacheFileName);
        attribute.setCacheListName(cacheListName);
        attribute.setHtmlView(isHtmlView);
        attribute.setOutFilePath(outFilePath);
        attribute.setOriginFilePath(originFilePath);
        attribute.setSuffix(suffix);
        attribute.setUrl(url);

        // 处理请求参数
        processRequestParameters(req, attribute, compressFileKey, isCompressFile);

        return attribute;
    }

    /**
     * 解析文件基本信息
     */
    private FileInfo parseFileInfo(String url) {
        FileInfo info = new FileInfo();
        String fullFileName = WebUtils.getUrlParameterReg(url, "fullfilename");

        if (StringUtils.hasText(fullFileName)) {
            info.originFileName = fullFileName;
            info.type = FileType.typeFromFileName(fullFileName);
            info.suffix = KkFileUtils.suffixFromFileName(fullFileName);
            info.url = WebUtils.clearFullfilenameParam(url);
        } else {
            info.originFileName = WebUtils.getFileNameFromURL(url);
            info.type = FileType.typeFromUrl(url);
            info.suffix = WebUtils.suffixFromUrl(url);
            info.url = url;
        }

        return info;
    }

    /**
     * 处理URL编码
     */
    private String handleUrlEncoding(String originFileName) {
        if (UrlEncoderUtils.hasUrlEncoded(originFileName)) {
            try {
                return URLDecoder.decode(originFileName, uriEncoding);
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("URL解码失败: {}", originFileName, e);
            }
        }
        return originFileName;
    }

    /**
     * 判断是否为HTML视图
     */
    private boolean isHtmlView(String suffix) {
        return suffix.equalsIgnoreCase("xls") || suffix.equalsIgnoreCase("xlsx") || suffix.equalsIgnoreCase("csv")
            || suffix.equalsIgnoreCase("xlsm") || suffix.equalsIgnoreCase("xlt") || suffix.equalsIgnoreCase("xltm")
            || suffix.equalsIgnoreCase("et") || suffix.equalsIgnoreCase("ett") || suffix.equalsIgnoreCase("xlam");
    }

    /**
     * 生成缓存文件前缀名
     */
    private String generateCacheFilePrefix(String originFileName, String suffix) {
        try {
            return originFileName.substring(0, originFileName.lastIndexOf(".")) + suffix + ".";
        } catch (Exception e) {
            LOGGER.error("获取文件名后缀错误：", e);
            return null;
        }
    }

    /**
     * 处理请求参数
     */
    private void processRequestParameters(HttpServletRequest req, FileAttribute attribute, String compressFileKey,
        boolean isCompressFile) {
        if (req == null)
            return;

        // 处理Office预览类型
        String officePreviewType = req.getParameter("officePreviewType");
        if (StringUtils.hasText(officePreviewType)) {
            attribute.setOfficePreviewType(officePreviewType);
        }

        // 处理压缩文件相关参数
        if (StringUtils.hasText(compressFileKey)) {
            attribute.setCompressFile(isCompressFile);
            attribute.setCompressFileKey(compressFileKey);
        }

        // 处理强制更新缓存参数
        String forceUpdatedCache = req.getParameter("forceUpdatedCache");
        if ("true".equalsIgnoreCase(forceUpdatedCache)) {
            attribute.setForceUpdatedCache(true);
        }

        // 处理TIF预览类型
        String tifPreviewType = req.getParameter("tifPreviewType");
        if (StringUtils.hasText(tifPreviewType)) {
            attribute.setTifPreviewType(tifPreviewType);
        }

        // 处理文件密码
        String filePassword = req.getParameter("filePassword");
        if (StringUtils.hasText(filePassword)) {
            attribute.setFilePassword(filePassword);
        }

        // 处理密码缓存参数
        String usePasswordCache = req.getParameter("usePasswordCache");
        if ("true".equalsIgnoreCase(usePasswordCache)) {
            attribute.setUsePasswordCache(true);
        }

        // 处理代理授权头
        String kkProxyAuthorization = req.getHeader("kk-proxy-authorization");
        attribute.setKkProxyAuthorization(kkProxyAuthorization);
    }

    /**
     * 从路径中获取文件负
     *
     * @param path 类似这种：C:\Users\yudian-it\Downloads
     * @return 文件名
     */
    public String getFileNameFromPath(String path) {
        return path.substring(path.lastIndexOf(File.separator) + 1);
    }

    /**
     * 获取redis中压缩包内图片文件
     *
     * @param compressFileKey compressFileKey
     * @return 图片文件访问url列表
     */
    public List<String> getImgCache(String compressFileKey) {
        return cacheService.getImgCache(compressFileKey);
    }

    /**
     * @param key pdf本地路径
     * @return 已将pdf转换成图片的图片本地相对路径
     */
    public Integer getPdf2jpgCache(String key) {
        return cacheService.getPdfImageCache(key);
    }

    /**
     * 获取本地 pdf 转 image 后的 web 访问地址
     *
     * @param pdfFilePath pdf文件名
     * @param index 图片索引
     * @return 图片访问地址
     */
    private String getPdf2jpgUrl(String pdfFilePath, int index) {
        String baseUrl = BaseUrlFilter.getBaseUrl();
        pdfFilePath = pdfFilePath.replace(fileDir, "");
        String pdfFolder = pdfFilePath.substring(0, pdfFilePath.length() - 4);
        String urlPrefix;
        try {
            urlPrefix = baseUrl + URLEncoder.encode(pdfFolder, uriEncoding).replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("UnsupportedEncodingException", e);
            urlPrefix = baseUrl + pdfFolder;
        }
        return urlPrefix + "/" + index + PDF2JPG_IMAGE_FORMAT;
    }

    /**
     * 获取相对路径
     *
     * @param absolutePath 绝对路径
     * @return 相对路径
     */
    public String getRelativePath(String absolutePath) {
        return absolutePath.substring(fileDir.length());
    }

    /**
     * @return 已转换过的文件集合(缓存)
     */
    public Map<String, String> listConvertedFiles() {
        return cacheService.getPDFCache();
    }

    /**
     * @return 已转换过的视频文件集合(缓存)
     */
    public Map<String, String> listConvertedMedias() {
        return cacheService.getMediaConvertCache();
    }

    /**
     * 获取缓存中的 pdf 转换成 jpg 图片集
     *
     * @param pdfFilePath pdf文件路径
     * @return 图片访问集合
     */
    private List<String> loadPdf2jpgCache(String pdfFilePath) {
        List<String> imageUrls = new ArrayList<>();
        Integer imageCount = this.getPdf2jpgCache(pdfFilePath);
        if (Objects.isNull(imageCount)) {
            return imageUrls;
        }
        IntStream.range(0, imageCount).forEach(i -> {
            String imageUrl = this.getPdf2jpgUrl(pdfFilePath, i);
            imageUrls.add(imageUrl);
        });
        return imageUrls;
    }

    /**
     * pdf文件转换成jpg图片集 fileNameFilePath pdf文件路径 pdfFilePath pdf输出文件路径 pdfName pdf文件名称 loadPdf2jpgCache 图片访问集合
     */
    public List<String> pdf2jpg(String fileNameFilePath, String pdfFilePath, String pdfName,
        FileAttribute fileAttribute) throws Exception {
        List<String> imageUrls = new ArrayList<>();

        // 检查缓存
        List<String> cacheResult = checkCache(fileAttribute, pdfFilePath);
        if (cacheResult != null) {
            return cacheResult;
        }

        // 验证文件和创建目录
        if (!validateAndPrepareFile(fileNameFilePath, pdfFilePath)) {
            return null;
        }

        // 加载PDF文档
        PDDocument doc = null;
        try {
            doc = loadPdfDocument(fileNameFilePath, fileAttribute);
            int pageCount = doc.getNumberOfPages();

            // 执行转换
            imageUrls = executePdfToJpgConversion(doc, pdfFilePath, pageCount);

            // 设置缓存
            handleCacheStorage(fileAttribute, pdfFilePath, pageCount);

        } finally {
            if (doc != null) {
                try {
                    doc.close();
                } catch (IOException e) {
                    LOGGER.warn("关闭PDF文档时出错: {}", pdfFilePath, e);
                }
            }
        }

        return imageUrls;
    }

    /**
     * 检查缓存
     */
    private List<String> checkCache(FileAttribute fileAttribute, String pdfFilePath) throws Exception {
        boolean forceUpdatedCache = fileAttribute.forceUpdatedCache();
        if (!forceUpdatedCache) {
            List<String> cacheResult = this.loadPdf2jpgCache(pdfFilePath);
            if (!CollectionUtils.isEmpty(cacheResult)) {
                return cacheResult;
            }
        }
        return null;
    }

    /**
     * 验证文件和创建目录
     */
    private boolean validateAndPrepareFile(String fileNameFilePath, String pdfFilePath) {
        File pdfFile = new File(fileNameFilePath);
        if (!pdfFile.exists()) {
            LOGGER.error("pdf文件不存在！");
            return false;
        }

        int index = pdfFilePath.lastIndexOf(".");
        String folder = pdfFilePath.substring(0, index);
        File path = new File(folder);
        if (!path.exists() && !path.mkdirs()) {
            LOGGER.error("创建转换文件【{}】目录失败，请检查目录权限！", folder);
        }

        return true;
    }

    /**
     * 加载PDF文档
     */
    private PDDocument loadPdfDocument(String fileNameFilePath, FileAttribute fileAttribute) throws Exception {
        PDDocument doc = null;
        String filePassword = fileAttribute.getFilePassword();
        String[] pdfPassword = {null};

        try {
            doc = Loader.loadPDF(new File(fileNameFilePath), filePassword);
            doc.setResourceCache(new NotResourceCache());
            return doc;
        } catch (IOException e) {
            handlePdfLoadException(e, pdfPassword);
            throw new Exception(e);
        }
    }

    /**
     * 处理PDF加载异常
     */
    private void handlePdfLoadException(IOException e, String[] pdfPassword) {
        Throwable[] throwableArray = ExceptionUtils.getThrowables(e);
        for (Throwable throwable : throwableArray) {
            if (throwable instanceof IOException || throwable instanceof EncryptedDocumentException) {
                if (e.getMessage().toLowerCase().contains(PDF_PASSWORD_MSG)) {
                    pdfPassword[0] = PDF_PASSWORD_MSG;
                }
            }
        }
        if (!PDF_PASSWORD_MSG.equals(pdfPassword[0])) {
            LOGGER.error("Convert pdf exception", e);
        }
    }

    /**
     * 执行PDF到JPG的转换
     */
    private List<String> executePdfToJpgConversion(PDDocument doc, String pdfFilePath, int pageCount) throws Exception {
        List<String> imageUrls = new ArrayList<>();
        int index = pdfFilePath.lastIndexOf(".");
        String folder = pdfFilePath.substring(0, index);

        Callable<List<String>> call = () -> {
            try {
                PDFRenderer pdfRenderer = new PDFRenderer(doc);
                pdfRenderer.setSubsamplingAllowed(true);

                for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
                    String imageFilePath = folder + File.separator + pageIndex + PDF2JPG_IMAGE_FORMAT;
                    BufferedImage image =
                        pdfRenderer.renderImageWithDPI(pageIndex, ConfigConstants.getPdf2JpgDpi(), ImageType.RGB);
                    ImageIOUtil.writeImage(image, imageFilePath, ConfigConstants.getPdf2JpgDpi());
                    String imageUrl = this.getPdf2jpgUrl(pdfFilePath, pageIndex);
                    imageUrls.add(imageUrl);
                    image.flush();
                }
            } catch (IOException e) {
                LOGGER.error("Convert pdf exception, pdfFilePath：{}", pdfFilePath, e);
                throw new Exception(e);
            }
            return imageUrls;
        };

        Future<List<String>> result = pool.submit(call);
        int pdftimeout = determineTimeout(pageCount);

        try {
            return result.get(pdftimeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new Exception(e);
        } catch (ExecutionException e) {
            throw new Exception(e);
        } catch (TimeoutException e) {
            throw new Exception("overtime");
        }
    }

    /**
     * 确定超时时间
     */
    private int determineTimeout(int pageCount) {
        if (pageCount <= 50) {
            return ConfigConstants.getPdfTimeout();
        } else if (pageCount <= 200) {
            return ConfigConstants.getPdfTimeout80();
        } else {
            return ConfigConstants.getPdfTimeout200();
        }
    }

    /**
     * 处理缓存存储
     */
    private void handleCacheStorage(FileAttribute fileAttribute, String pdfFilePath, int pageCount) {
        boolean usePasswordCache = fileAttribute.getUsePasswordCache();
        String filePassword = fileAttribute.getFilePassword();

        if (usePasswordCache || ObjectUtils.isEmpty(filePassword)) {
            this.addPdf2jpgCache(pdfFilePath, pageCount);
        }
    }

    /**
     * 设置redis中压缩包内图片文件
     *
     * @param fileKey fileKey
     * @param imgs 图片文件访问url列表
     */
    public void putImgCache(String fileKey, List<String> imgs) {
        cacheService.putImgCache(fileKey, imgs);
    }

    /**
     * 文件信息内部类
     */
    private static class FileInfo {
        String originFileName;
        FileType type;
        String suffix;
        String url;
    }
}
