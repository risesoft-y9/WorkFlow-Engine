package net.risesoft.service.impl;

import net.risesoft.config.ConfigConstants;
import net.risesoft.model.FileAttribute;
import net.risesoft.model.FileType;
import net.risesoft.model.ReturnResponse;
import net.risesoft.service.FilePreview;
import net.risesoft.utils.ConfigUtils;
import net.risesoft.utils.DownloadUtils;
import net.risesoft.service.FileHandlerService;
import net.risesoft.web.filter.BaseUrlFilter;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.io.File;


@Service
public class MediaFilePreviewImpl implements FilePreview {

    private final FileHandlerService fileHandlerService;
    private final OtherFilePreviewImpl otherFilePreview;

    private static Object LOCK = new Object();

    public MediaFilePreviewImpl(FileHandlerService fileHandlerService, OtherFilePreviewImpl otherFilePreview) {
        this.fileHandlerService = fileHandlerService;
        this.otherFilePreview = otherFilePreview;
    }

    @Override
    public String filePreviewHandle(String url, Model model, FileAttribute fileAttribute) {
        // 不是http开头，浏览器不能直接访问，需下载到本地
        if (url != null && !url.toLowerCase().startsWith("http")) {
            ReturnResponse<String> response = DownloadUtils.downLoad(fileAttribute, fileAttribute.getName());
            if (response.isFailure()) {
                return otherFilePreview.notSupportedFile(model, fileAttribute, response.getMsg());
            } else {
                url = BaseUrlFilter.getBaseUrl() + fileHandlerService.getRelativePath(response.getContent());
                fileAttribute.setUrl(url);
            }
        }

        if (checkNeedConvert(fileAttribute.getSuffix())) {
            url = convertUrl(fileAttribute);
        } else {
            //正常media类型
            String[] medias = ConfigConstants.getMedia();
            for (String media : medias) {
                if (media.equals(fileAttribute.getSuffix())) {
                    model.addAttribute("mediaUrl", url);
                    return MEDIA_FILE_PREVIEW_PAGE;
                }
            }
            return otherFilePreview.notSupportedFile(model, fileAttribute, "暂不支持");
        }
        model.addAttribute("mediaUrl", url);
        return MEDIA_FILE_PREVIEW_PAGE;
    }

    /**
     * 检查视频文件处理逻辑
     * 返回处理过后的url
     *
     * @return url
     */
    private String convertUrl(FileAttribute fileAttribute) {
        String url = fileAttribute.getUrl();
        if (fileHandlerService.listConvertedMedias().containsKey(url)) {
            url = fileHandlerService.getConvertedMedias(url);
        } else {
            if (!fileHandlerService.listConvertedMedias().containsKey(url)) {
                synchronized (LOCK) {
                    if (!fileHandlerService.listConvertedMedias().containsKey(url)) {
                        String convertedUrl = convertToMp4(fileAttribute);
                        //加入缓存
                        fileHandlerService.addConvertedMedias(url, convertedUrl);
                        url = convertedUrl;
                    }
                }
            }
        }
        return url;
    }

    /**
     * 检查视频文件转换是否已开启，以及当前文件是否需要转换
     *
     * @return
     */
    private boolean checkNeedConvert(String suffix) {
        //1.检查开关是否开启
        if ("false".equals(ConfigConstants.getMediaConvertDisable())) {
            return false;
        }
        //2.检查当前文件是否需要转换
        String[] mediaTypesConvert = FileType.MEDIA_TYPES_CONVERT;
        String type = suffix;
        for (String temp : mediaTypesConvert) {
            if (type.equals(temp)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将浏览器不兼容视频格式转换成MP4
     *
     * @param fileAttribute
     * @return
     */
    private static String convertToMp4(FileAttribute fileAttribute) {

        //说明：这里做临时处理，取上传文件的目录
        String homePath = ConfigUtils.getHomePath();
        String filePath = homePath + File.separator + "file" + File.separator + "demo" + File.separator + fileAttribute.getName();
        String convertFileName = fileAttribute.getUrl().replace(fileAttribute.getSuffix(), "mp4");

        File file = new File(filePath);
        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(file);
        String fileName = null;
        Frame captured_frame = null;
        FFmpegFrameRecorder recorder = null;
        try {
            fileName = file.getAbsolutePath().replace(fileAttribute.getSuffix(), "mp4");
            File desFile = new File(fileName);
            //判断一下防止穿透缓存
            if (desFile.exists()) {
                return fileName;
            }

            frameGrabber.start();
            recorder = new FFmpegFrameRecorder(fileName, frameGrabber.getImageWidth(), frameGrabber.getImageHeight(), frameGrabber.getAudioChannels());
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264); //avcodec.AV_CODEC_ID_H264  //AV_CODEC_ID_MPEG4
            recorder.setFormat("mp4");
            recorder.setFrameRate(frameGrabber.getFrameRate());
            //recorder.setSampleFormat(frameGrabber.getSampleFormat()); //
            recorder.setSampleRate(frameGrabber.getSampleRate());

            recorder.setAudioChannels(frameGrabber.getAudioChannels());
            recorder.setFrameRate(frameGrabber.getFrameRate());
            recorder.start();
            while ((captured_frame = frameGrabber.grabFrame()) != null) {
                try {
                    recorder.setTimestamp(frameGrabber.getTimestamp());
                    recorder.record(captured_frame);
                } catch (Exception e) {
                }
            }
            recorder.stop();
            recorder.release();
            frameGrabber.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //是否删除源文件
        //file.delete();
        return convertFileName;
    }
}
