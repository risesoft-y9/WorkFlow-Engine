package net.risesoft.controller.vo;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * 正文模板信息
 *
 * @author mengjuhua
 * @date 2024/07/12
 */
@Data
public class WordTemplateVO implements Serializable {
    private static final long serialVersionUID = 4086766057295035083L;

    private String id;

    /**
     * 文档名称
     */
    private String fileName;

    /**
     * 文档路径
     */
    private String filePath;

    /**
     * 文件字节数
     */
    private String fileSize;

    /**
     * 上传时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date uploadTime;

    /**
     * 上传人
     */
    private String personName;

    /**
     * 文件类型
     */
    private String wordTemplateType;
}
