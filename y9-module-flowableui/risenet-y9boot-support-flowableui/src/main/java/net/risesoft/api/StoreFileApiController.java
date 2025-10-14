package net.risesoft.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import net.risesoft.util.Y9DateTimeUtils;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.repository.Y9FileStoreRepository;
import net.risesoft.y9public.service.Y9FileStoreService;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api")
public class StoreFileApiController {

    private final Y9FileStoreRepository fileRepository;

    private final Y9FileStoreService y9FileStoreService;

    @RequestMapping(value = "/getAttachmentName")
    public String getAttachmentName(String fileId) {
        Y9FileStore y9FileStore = fileRepository.findById(fileId).orElse(null);
        if (y9FileStore != null) {
            return y9FileStore.getFileName();
        }
        return null;
    }

    @RequestMapping(value = "/getFileBytes")
    public byte[] getFileBytes(String fileId) throws Exception {
        return y9FileStoreService.downloadFileToBytes(fileId);
    }

    @PostMapping(value = "/store")
    public String storeFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return null;
            }
            String fileName = file.getOriginalFilename();
            String dateStr = Y9DateTimeUtils.formatCurrentDateTimeCompact();
            String fullPath = "/email/" + dateStr;

            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(file, fullPath, fileName);
            return y9FileStore.getId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}