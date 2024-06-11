package net.risesoft.api;

import lombok.RequiredArgsConstructor;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.repository.Y9FileStoreRepository;
import net.risesoft.y9public.service.Y9FileStoreService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.text.SimpleDateFormat;
import java.util.Date;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api")
public class StoreFileApiController {

    private final Y9FileStoreRepository fileRepository;

    private final Y9FileStoreService y9FileStoreService;

    @RequestMapping(value = "/getAttachmentName")
    @ResponseBody
    public String getAttachmentName(String fileId) {
        Y9FileStore y9FileStore = fileRepository.findById(fileId).orElse(null);
        if (y9FileStore != null) {
            return y9FileStore.getFileName();
        }
        return null;
    }

    @RequestMapping(value = "/getFileBytes")
    @ResponseBody
    public byte[] getFileBytes(String fileId) throws Exception {
        return y9FileStoreService.downloadFileToBytes(fileId);
    }

    @RequestMapping(value = "/store", method = RequestMethod.POST)
    @ResponseBody
    public String storeFile(@RequestParam("file") MultipartFile file) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}