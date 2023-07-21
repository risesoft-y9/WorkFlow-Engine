//
// JODConverter - Java OpenDocument Converter
// Copyright 2004-2012 Mirko Nasato and contributors
//
// JODConverter is Open Source software, you can redistribute it and/or
// modify it under either (at your option) of the following licenses
//
// 1. The GNU Lesser General Public License v3 (or later)
// -> http://www.gnu.org/licenses/lgpl-3.0.txt
// 2. The Apache License, Version 2.0
// -> http://www.apache.org/licenses/LICENSE-2.0.txt
//
package org.artofsolving.jodconverter.office;

import java.io.File;
import java.util.Map;

import org.artofsolving.jodconverter.util.PlatformUtils;

import com.sun.star.beans.PropertyValue;
import com.sun.star.uno.UnoRuntime;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public class OfficeUtils {

    public static final String SERVICE_DESKTOP = "com.sun.star.frame.Desktop";

    public static <T> T cast(Class<T> type, Object object) {
        return (T)UnoRuntime.queryInterface(type, object);
    }

    private static File findOfficeHome(String... knownPaths) {
        for (String path : knownPaths) {
            File home = new File(path);
            if (getOfficeExecutable(home).isFile()) {
                return home;
            }
        }
        return null;
    }

    public static File getDefaultOfficeHome() {
        String officeHome = "office.home";
        if (System.getProperty(officeHome) != null) {
            return new File(System.getProperty(officeHome));
        }
        if (PlatformUtils.isWindows()) {
            // %ProgramFiles(x86)% on 64-bit machines; %ProgramFiles% on 32-bit ones
            String programFiles = System.getenv("ProgramFiles(x86)");
            if (programFiles == null) {
                programFiles = System.getenv("ProgramFiles");
            }
            return findOfficeHome(programFiles + File.separator + "OpenOffice 4", programFiles + File.separator + "LibreOffice 4");
        } else if (PlatformUtils.isMac()) {
            return findOfficeHome("/Applications/OpenOffice.app/Contents", "/Applications/LibreOffice.app/Contents");
        } else {
            // Linux or other *nix variants
            return findOfficeHome("/opt/openoffice.org3", "/opt/libreoffice", "/usr/lib/openoffice", "/usr/lib/libreoffice", "/opt/openoffice4");
        }
    }

    public static File getOfficeExecutable(File officeHome) {
        if (PlatformUtils.isMac()) {
            return new File(officeHome, "MacOS/soffice");
        } else {
            return new File(officeHome, "program/soffice.bin");
        }
    }

    public static PropertyValue property(String name, Object value) {
        PropertyValue propertyValue = new PropertyValue();
        propertyValue.Name = name;
        propertyValue.Value = value;
        return propertyValue;
    }

    @SuppressWarnings("unchecked")
    public static PropertyValue[] toUnoProperties(Map<String, ?> properties) {
        PropertyValue[] propertyValues = new PropertyValue[properties.size()];
        int i = 0;
        for (Map.Entry<String, ?> entry : properties.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Map) {
                Map<String, Object> subProperties = (Map<String, Object>)value;
                value = toUnoProperties(subProperties);
            }
            propertyValues[i++] = property((String)entry.getKey(), value);
        }
        return propertyValues;
    }

    public static String toUrl(File file) {
        String path = file.toURI().getRawPath();
        String url = path.startsWith("//") ? "file:" + path : "file://" + path;
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    private OfficeUtils() {
        throw new AssertionError("utility class must not be instantiated");
    }

}
