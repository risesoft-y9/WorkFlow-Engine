package net.risesoft.service.impl;

import java.io.File;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.config.ConfigConstants;
import net.risesoft.model.FileAttribute;
import net.risesoft.model.FileType;
import net.risesoft.model.ReturnResponse;
import net.risesoft.service.FileHandlerService;
import net.risesoft.service.FilePreview;
import net.risesoft.utils.DownloadUtils;

@Slf4j
@Service
public class MediaFilePreviewImpl implements FilePreview {

    private static final String MP4 = "mp4";
    private static final String MEDIAURL = "mediaUrl";
    private final FileHandlerService fileHandlerService;
    private final OtherFilePreviewImpl otherFilePreview;

    public MediaFilePreviewImpl(FileHandlerService fileHandlerService, OtherFilePreviewImpl otherFilePreview) {
        this.fileHandlerService = fileHandlerService;
        this.otherFilePreview = otherFilePreview;
    }

    private static String convertToMp4(String filePath, String outFilePath, FileAttribute fileAttribute) {
        Frame captured_frame;
        try (FFmpegFrameGrabber frameGrabber = FFmpegFrameGrabber.createDefault(filePath);
            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outFilePath, 0, 0, 0)) {
            File desFile = new File(outFilePath);
            // 判断一下防止重复转换
            if (desFile.exists()) {
                return outFilePath;
            }
            if (fileAttribute.isCompressFile()) { // 判断 是压缩包的创建新的目录
                int index = outFilePath.lastIndexOf("/"); // 截取最后一个斜杠的前面的内容
                String folder = outFilePath.substring(0, index);
                File path = new File(folder);
                // 目录不存在 创建新的目录
                if (!path.exists()) {
                    path.mkdirs();
                }
            }
            frameGrabber.start();
            recorder.setImageWidth(frameGrabber.getImageWidth());
            recorder.setImageHeight(frameGrabber.getImageHeight());
            recorder.setAudioChannels(frameGrabber.getAudioChannels());
            // recorder.setImageHeight(640);
            // recorder.setImageWidth(480);
            recorder.setFormat(MP4);
            recorder.setFrameRate(frameGrabber.getFrameRate());
            recorder.setSampleRate(frameGrabber.getSampleRate());
            // 视频编码属性配置 H.264 H.265 MPEG
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            // 设置视频比特率,单位:b
            recorder.setVideoBitrate(frameGrabber.getVideoBitrate());
            recorder.setAspectRatio(frameGrabber.getAspectRatio());
            // 设置音频通用编码格式
            recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
            // 设置音频比特率,单位:b (比特率越高，清晰度/音质越好，当然文件也就越大 128000 = 182kb)
            recorder.setAudioBitrate(frameGrabber.getAudioBitrate());
            recorder.setAudioOptions(frameGrabber.getAudioOptions());
            recorder.setAudioChannels(frameGrabber.getAudioChannels());
            recorder.start();
            while (true) {
                captured_frame = frameGrabber.grabFrame();
                if (captured_frame == null) {
                    System.out.println("转码完成:" + filePath);
                    break;
                }
                recorder.record(captured_frame);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return outFilePath;
    }

    @Override
    public String filePreviewHandle(String url, Model model, FileAttribute fileAttribute) {
        String fileName = fileAttribute.getName();
        String suffix = fileAttribute.getSuffix();
        String cacheName = fileAttribute.getCacheName();
        String outFilePath = fileAttribute.getOutFilePath();
        FileType type = fileAttribute.getType();

        // 检查是否需要转换处理
        boolean mediaTypes = isSupportedConvertFormat(suffix);
        boolean needConvert = !url.toLowerCase().startsWith("http") || checkNeedConvert(mediaTypes);

        if (needConvert) {
            return handleConvertPreview(model, fileAttribute, fileName, cacheName, outFilePath, mediaTypes);
        }

        // 处理直接预览情况
        if (type.equals(FileType.MEDIA)) {
            model.addAttribute(MEDIAURL, url);
            return MEDIA_FILE_PREVIEW_PAGE;
        }

        return otherFilePreview.notSupportedFile(model, fileAttribute, "系统还不支持该格式文件的在线预览");
    }

    /**
     * 处理需要转换的文件预览
     */
    private String handleConvertPreview(Model model, FileAttribute fileAttribute, String fileName, String cacheName,
        String outFilePath, boolean mediaTypes) {
        boolean forceUpdatedCache = fileAttribute.forceUpdatedCache();

        if (forceUpdatedCache || !fileHandlerService.listConvertedFiles().containsKey(cacheName)
            || !ConfigConstants.isCacheEnabled()) {
            return handleFileConversion(model, fileAttribute, fileName, cacheName, outFilePath, mediaTypes);
        } else {
            // 从缓存获取
            model.addAttribute(MEDIAURL, fileHandlerService.listConvertedFiles().get(cacheName));
            return MEDIA_FILE_PREVIEW_PAGE;
        }
    }

    /**
     * 处理文件转换
     */
    private String handleFileConversion(Model model, FileAttribute fileAttribute, String fileName, String cacheName,
        String outFilePath, boolean mediaTypes) {
        ReturnResponse<String> response = DownloadUtils.downLoad(fileAttribute, fileName);
        if (response.isFailure()) {
            return otherFilePreview.notSupportedFile(model, fileAttribute, response.getMsg());
        }

        String filePath = response.getContent();
        String convertedUrl = convertFile(filePath, outFilePath, fileAttribute, mediaTypes);

        if (convertedUrl == null) {
            return otherFilePreview.notSupportedFile(model, fileAttribute, "视频转换异常，请联系管理员");
        }

        // 缓存处理
        handleCacheStorage(cacheName, outFilePath);

        model.addAttribute(MEDIAURL, fileHandlerService.getRelativePath(outFilePath));
        return MEDIA_FILE_PREVIEW_PAGE;
    }

    /**
     * 转换文件格式
     */
    private String convertFile(String filePath, String outFilePath, FileAttribute fileAttribute, boolean mediaTypes) {
        try {
            if (mediaTypes) {
                return convertToMp4(filePath, outFilePath, fileAttribute);
            } else {
                return outFilePath;
            }
        } catch (Exception e) {
            LOGGER.error("文件转换异常: {}", filePath, e);
            return null;
        }
    }

    /**
     * 处理缓存存储
     */
    private void handleCacheStorage(String cacheName, String outFilePath) {
        if (ConfigConstants.isCacheEnabled()) {
            fileHandlerService.addConvertedFile(cacheName, fileHandlerService.getRelativePath(outFilePath));
        }
    }

    /**
     * 检查是否为支持转换的格式
     */
    private boolean isSupportedConvertFormat(String suffix) {
        String[] mediaTypesConvert = FileType.MEDIA_CONVERT_TYPES;
        for (String temp : mediaTypesConvert) {
            if (suffix.equals(temp)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查视频文件转换是否已开启，以及当前文件是否需要转换
     *
     * @return
     */
    private boolean checkNeedConvert(boolean mediaTypes) {
        // 1.检查开关是否开启
        if ("true".equals(ConfigConstants.getMediaConvertDisable())) {
            return mediaTypes;
        }
        return false;
    }

}
