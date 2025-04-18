package net.risesoft.utils;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9.Y9Context;

@Slf4j
@Component
@DependsOn("y9Context")
public class ConfigUtils {
    private static final String MAIN_DIRECTORY_NAME = "jodconverter";

    public static String getHomePath() {
        String userDir = System.getenv("KKFILEVIEW_BIN_FOLDER");
        if (userDir == null) {
            userDir = System.getProperty("user.dir");
        }
        if (userDir.endsWith("bin")) {
            userDir = userDir.substring(0, userDir.length() - 4);
        } else {
            String separator = File.separator;
            if (userDir.endsWith(MAIN_DIRECTORY_NAME)) {
                userDir = userDir + separator + "src" + separator + "main";
            } else {
                userDir = userDir + separator + MAIN_DIRECTORY_NAME + separator + "src" + separator + "main";
            }
        }
        return userDir;
    }

    // 获取环境变量，如果找不到则返回默认值
    @SuppressWarnings("SameParameterValue")
    private static String getEnvOrDefault(String key, String def) {
        String value = System.getenv(key);
        return value == null ? def : value;
    }

    // 返回参数列表中第一个真实存在的路径，或者 null
    private static String firstExists(File... paths) {
        for (File path : paths) {
            if (path.exists()) {
                return path.getAbsolutePath();
            }
        }
        return null;
    }

    public static String getUserDir() {
        String userDir = System.getProperty("user.dir");
        String binFolder = getEnvOrDefault("KKFILEVIEW_BIN_FOLDER", userDir);

        File pluginPath = new File(binFolder);

        // 如果指定了 bin 或其父目录，则返回父目录
        if (new File(pluginPath, "bin").exists()) {
            return pluginPath.getAbsolutePath();

        } else if (pluginPath.exists() && pluginPath.getName().equals("bin")) {
            return pluginPath.getParentFile().getAbsolutePath();

        } else {
            return firstExists(new File(pluginPath, MAIN_DIRECTORY_NAME),
                new File(pluginPath.getParentFile(), MAIN_DIRECTORY_NAME));
        }
    }

    public static String getCustomizedConfigPath() {
        LOGGER.info("Y9Context.getAc()->{}", Y9Context.getAc());
        Resource resource = Y9Context.getAc().getResource("classpath:");

        String absolutePath = null;
        try {
            absolutePath = resource.getFile().getAbsoluteFile().getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String separator = java.io.File.separator;
        LOGGER.info("配置文件路径:::{}{}{}", absolutePath, separator, "application.yml");
        return absolutePath + separator + "application.yml";
    }

    public synchronized static void restorePropertiesFromEnvFormat(Properties properties) {
        Iterator<Map.Entry<Object, Object>> iterator = properties.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Object, Object> entry = iterator.next();
            String key = entry.getKey().toString();
            String value = entry.getValue().toString();
            if (value.trim().startsWith("${") && value.trim().endsWith("}")) {
                int beginIndex = value.indexOf(":");
                if (beginIndex < 0) {
                    beginIndex = value.length() - 1;
                }
                int endIndex = value.length() - 1;
                String envKey = value.substring(2, beginIndex);
                String envValue = System.getenv(envKey);
                if (envValue == null || "".equals(envValue.trim())) {
                    value = value.substring(beginIndex + 1, endIndex);
                } else {
                    value = envValue;
                }
                properties.setProperty(key, value);
            }
        }
    }

    public static String[] getActiveProfiles() {
        return Y9Context.getEnvironment().getActiveProfiles();
    }

    public static Properties getInitProperties() {
        Properties properties = new Properties();
        String[] activeProfiles = Y9Context.getEnvironment().getActiveProfiles();

        YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();

        // 加载yml配置文件
        yamlPropertiesFactoryBean.setResources(new ClassPathResource("application.yml"));
        // 2:将加载的配置文件交给 YamlPropertiesFactoryBean

        if (activeProfiles != null && activeProfiles.length > 0) {
            ClassPathResource[] resources = new ClassPathResource[activeProfiles.length];
            for (int i = 0; i < activeProfiles.length; i++) {
                resources[i] = new ClassPathResource("application-" + activeProfiles[i] + ".yml");
            }
            yamlPropertiesFactoryBean.setResources(resources);
        }

        properties = yamlPropertiesFactoryBean.getObject();

        LOGGER.info("base.url->{},office.home-> {}", properties.get("base.url"), properties.get("office.home"));
        return properties;
    }
}
