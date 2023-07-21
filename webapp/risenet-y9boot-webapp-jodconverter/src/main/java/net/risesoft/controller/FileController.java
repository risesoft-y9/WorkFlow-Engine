package net.risesoft.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;

import net.risesoft.utils.UploadUtils;

/**
 * 
 * @author Think
 *
 */
@RestController
public class FileController {

    @RequestMapping(value = "fileUpload", method = RequestMethod.POST)
    public Map<String, Object> fileUpload(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws JsonProcessingException {
        String originalFilename = file.getOriginalFilename();
        String fileName = FilenameUtils.getName(originalFilename);
        UploadUtils uploadUtils = new UploadUtils();
        String fileUrl = uploadUtils.upload(file, fileName);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put("fileName", fileName);
        map.put("fileUrl", fileUrl);
        return map;
    }

}
