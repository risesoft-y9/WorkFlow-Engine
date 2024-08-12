package net.risesoft.controller.sync;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;



import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9Context;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;



@RestController
@RequestMapping("/mobile/sync")
@Slf4j
@Validated
@RequiredArgsConstructor
public class SyncController {


    @GetMapping(value = "/jiekou1")
    public Y9Result<Map<String, Object>> jiekou1() {
        Map<String, Object> map = new HashMap<>();
        try {
            String filePath = Y9Context.getWebRootRealPath() + "static/1.png";
            LOGGER.info("********************filePath：{}********************", filePath);
            File realFile = new File(filePath);
            FileInputStream fis =  new FileInputStream(realFile);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b= new byte[1024];
            int n;
            while((n=fis.read(b))!= -1)
                bos.write(b,0,n);
            fis.close();
            bos.close();
            byte[] buffer = bos.toByteArray();
            String fileStr = Base64.getEncoder().encodeToString(buffer);
            map.put("fileStr", fileStr);
            return Y9Result.success(map);
        } catch (Exception e) {
            LOGGER.error("jiekou1失败", e);
        }
        return Y9Result.failure("jiekou1失败");
    }

    @PostMapping(value = "/jiekou2")
    public Y9Result<Map<String, Object>> jiekou2() {
        Map<String, Object> map = new HashMap<>();
        try {
            String filePath = Y9Context.getWebRootRealPath() + "static/1.png";
            LOGGER.info("********************filePath：{}********************", filePath);
            File realFile = new File(filePath);
            FileInputStream fis =  new FileInputStream(realFile);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b= new byte[1024];
            int n;
            while((n=fis.read(b))!= -1)
                bos.write(b,0,n);
            fis.close();
            bos.close();
            byte[] buffer = bos.toByteArray();
            String fileStr = Base64.getEncoder().encodeToString(buffer);
            map.put("fileStr", fileStr);
            return Y9Result.success(map);
        } catch (Exception e) {
            LOGGER.error("jiekou2失败", e);
        }
        return Y9Result.failure("jiekou2失败");
    }
}
