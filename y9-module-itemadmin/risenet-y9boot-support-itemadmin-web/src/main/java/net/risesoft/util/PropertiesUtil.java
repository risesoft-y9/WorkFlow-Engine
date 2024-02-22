package net.risesoft.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@SuppressWarnings("rawtypes")
public class PropertiesUtil implements InitializingBean {

    /** Properties to be set */
    private Map systemProperties;

    /** Sets the system properties */
    @Override
    public void afterPropertiesSet() throws Exception {
        if (systemProperties == null || systemProperties.isEmpty()) {
            // No properties to initialize
            return;
        }

        Iterator i = systemProperties.keySet().iterator();
        while (i.hasNext()) {
            String key = (String)i.next();
            String value = (String)systemProperties.get(key);

            System.setProperty(key, value);
        }
    }

    /**
     * Description: 获取properties文件中的所有内容
     * 
     * @param resourceUrl
     * @param list
     * @return
     */
    public Map<String, String> getAllProperties(String resourceUrl, List<String> list) {
        Properties prop = getProperties(resourceUrl);
        Map<String, String> result = new HashMap<String, String>(16);
        Enumeration e = prop.propertyNames();
        while (e.hasMoreElements()) {
            String key = (String)e.nextElement();
            result.put(key, prop.getProperty(key));
        }
        return result;
    }

    /**
     * Description: 获取关键字
     * 
     * @param resourceUrl
     * @return
     */
    public List<String> getKeys(String resourceUrl) {
        Properties prop = getProperties(resourceUrl);
        List<String> list = new ArrayList<String>();
        Enumeration e = prop.propertyNames();
        while (e.hasMoreElements()) {
            String key = (String)e.nextElement();
            list.add(key);
        }
        return list;
    }

    /**
     * Description: 获取Properties文件
     * 
     * @param resourceUrl
     * @return
     */
    public Properties getProperties(String resourceUrl) {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource(resourceUrl);
        Properties prop = new Properties();
        try {
            InputStream in = resource.getInputStream();
            prop.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }

    /**
     * Description: 获取资源
     * 
     * @param resourceUrl
     * @param source
     * @return
     */
    public String getProperty(String resourceUrl, String source) {
        Properties prop = getProperties(resourceUrl);
        return prop.getProperty(source);
    }

    /**
     * Description: 保存
     * 
     * @param resourceUrl
     * @param prop
     */
    public void save(String resourceUrl, Properties prop) {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource(resourceUrl);
        try {
            File file = resource.getFile();
            FileOutputStream fos = new FileOutputStream(file);
            prop.store(fos, "");
            fos.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Description: 设置修改后的properties的属性
     * 
     * @param resourceUrl
     * @param key
     * @param value
     */
    public void savePropertiy(String resourceUrl, String key, String value) {
        Properties prop = getProperties(resourceUrl);
        prop.setProperty(key, value);
        save(resourceUrl, prop);
    }

    public void setSystemProperties(Map systemProperties) {
        this.systemProperties = systemProperties;
    }
}
