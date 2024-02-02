package net.risesoft.api;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.repository.Y9FileStoreRepository;
import net.risesoft.y9public.service.Y9FileStoreService;

@RestController
@RequestMapping(value = "/api")
public class StoreFileApiController {

    @Autowired
    private Y9FileStoreService y9FileStoreService;

    @Autowired
    protected Y9FileStoreRepository fileRepository;

    @RequestMapping(value = "/store", method = RequestMethod.POST)
    @ResponseBody
    public String storeFile(@RequestParam("file") MultipartFile file) throws Exception {
        try {
            if (file.isEmpty()) {
                return null;
            }
            String fileName = file.getOriginalFilename();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String dateStr = sdf.format(new Date());
            String fullPath = "/email/" + dateStr;

            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(file, fullPath, fileName);
            return y9FileStore.getId();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = "/getFileBytes")
    @ResponseBody
    public byte[] getFileBytes(String fileId) throws Exception {
        return y9FileStoreService.downloadFileToBytes(fileId);
    }

    @RequestMapping(value = "/getAttachmentName")
    @ResponseBody
    public String getAttachmentName(String fileId) {
        Y9FileStore y9FileStore = fileRepository.findById(fileId).orElse(null);
        if (y9FileStore != null) {
            return y9FileStore.getFileName();
        }
        return null;
    }
}