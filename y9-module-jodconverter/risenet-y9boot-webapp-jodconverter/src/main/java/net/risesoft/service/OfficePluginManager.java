package net.risesoft.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.jodconverter.core.office.InstalledOfficeManagerHolder;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.core.office.OfficeUtils;
import org.jodconverter.core.util.OSUtils;
import org.jodconverter.local.office.LocalOfficeManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.utils.LocalOfficeUtils;

/**
 * 创建文件转换器
 * 
 * @since 2022-12-15
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class OfficePluginManager {

    private static final String SOFFICE_BIN = "soffice.bin";
    private LocalOfficeManager officeManager;
    @Value("${office.plugin.server.ports:2001,2002}")
    private String serverPorts;
    @Value("${office.plugin.task.timeout:5m}")
    private String timeOut;
    @Value("${office.plugin.task.taskexecutiontimeout:5m}")
    private String taskExecutionTimeout;
    @Value("${office.plugin.task.maxtasksperprocess:5}")
    private int maxTasksPerProcess;

    /**
     * 启动Office组件进程
     */
    @PostConstruct
    public void startOfficeManager() throws OfficeException {
        File officeHome = LocalOfficeUtils.getDefaultOfficeHome();
        if (officeHome == null) {
            throw new RuntimeException("找不到office组件，请确认'office.home'配置是否有误");
        }
        boolean killOffice = killProcess();
        if (killOffice) {
            LOGGER.warn("检测到有正在运行的office进程，已自动结束该进程");
        }
        try {
            String[] portsString = serverPorts.split(",");
            int[] ports = Arrays.stream(portsString).mapToInt(Integer::parseInt).toArray();
            long timeout = DurationStyle.detectAndParse(timeOut).toMillis();
            long taskexecutiontimeout = DurationStyle.detectAndParse(taskExecutionTimeout).toMillis();
            officeManager = LocalOfficeManager.builder()
                .officeHome(officeHome)
                .portNumbers(ports)
                .processTimeout(timeout)
                .maxTasksPerProcess(maxTasksPerProcess)
                .taskExecutionTimeout(taskexecutiontimeout)
                .build();
            officeManager.start();
            InstalledOfficeManagerHolder.setInstance(officeManager);
        } catch (Exception e) {
            LOGGER.error("启动office组件失败，请检查office组件是否可用");
            throw e;
        }
    }

    @SuppressWarnings("java:S4036")
    private boolean killProcess() {
        boolean flag = false;
        try {
            if (OSUtils.IS_OS_WINDOWS) {
                Process p = Runtime.getRuntime().exec("cmd /c tasklist ");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                InputStream os = p.getInputStream();
                byte[] b = new byte[256];
                while (os.read(b) > 0) {
                    baos.write(b);
                }
                String s = baos.toString();
                Runtime.getRuntime().exec("taskkill /im " + SOFFICE_BIN + " /f");
                if (s.contains(SOFFICE_BIN)) {
                    flag = true;
                }
            } else if (OSUtils.IS_OS_MAC || OSUtils.IS_OS_MAC_OSX) {
                Process p = Runtime.getRuntime().exec(new String[] {"sh", "-c", "ps -ef | grep " + SOFFICE_BIN});
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                InputStream os = p.getInputStream();
                byte[] b = new byte[256];
                while (os.read(b) > 0) {
                    baos.write(b);
                }
                String s = baos.toString();
                if (StringUtils.ordinalIndexOf(s, SOFFICE_BIN, 3) > 0) {
                    String[] cmd = {"sh", "-c", "kill -15 `ps -ef|grep " + SOFFICE_BIN + "|awk 'NR==1{print $2}'`"};
                    Runtime.getRuntime().exec(cmd);
                    flag = true;
                }
            } else {
                Process p = Runtime.getRuntime()
                    .exec(new String[] {"sh", "-c", "ps -ef | grep " + SOFFICE_BIN + " |grep -v grep | wc -l"});
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                InputStream os = p.getInputStream();
                byte[] b = new byte[256];
                while (os.read(b) > 0) {
                    baos.write(b);
                }
                String s = baos.toString();
                if (!s.startsWith("0")) {
                    String[] cmd =
                        {"sh", "-c", "ps -ef | grep soffice.bin | grep -v grep | awk '{print \"kill -9 \"$2}' | sh"};
                    Runtime.getRuntime().exec(cmd);
                    flag = true;
                }
            }
        } catch (IOException e) {
            LOGGER.error("检测office进程异常", e);
        }
        return flag;
    }

    @PreDestroy
    public void destroyOfficeManager() {
        if (null != officeManager && officeManager.isRunning()) {
            LOGGER.info("Shutting down office process");
            OfficeUtils.stopQuietly(officeManager);
        }
    }

}
