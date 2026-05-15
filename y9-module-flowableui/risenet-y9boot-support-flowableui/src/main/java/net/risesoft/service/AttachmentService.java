package net.risesoft.service;

import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import net.risesoft.pojo.Y9Result;

/**
 * 附件服务接口
 *
 * @author yihong
 * @date 2026/04/30
 */
public interface AttachmentService {

    /**
     * 附件下载
     *
     * @param id 附件id
     * @param fileName 附件名称
     * @param out OutputStream
     */
    void download(String id, String fileName, OutputStream out);

    /**
     * 删除附件
     *
     * @param ids 附件ids，逗号隔开
     * @return Y9Result<String>
     */
    Y9Result<String> delFile(String ids);

    /**
     * 附加打包zip下载
     *
     * @param processSerialNumber 流程编号
     * @param fileSource 附件来源
     * @param response HttpServletResponse
     * @param request HttpServletRequest
     */
    void packDownload(String processSerialNumber, String fileSource, HttpServletResponse response,
        HttpServletRequest request);

    /**
     * 上传附件
     *
     * @param file 文件
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @param describes 描述
     * @param processSerialNumber 流程编号
     * @param fileSource 文件来源
     * @return Y9Result<String>
     */
    Y9Result<String> upload(MultipartFile file, String processInstanceId, String taskId, String describes,
        String processSerialNumber, String fileSource);
}
