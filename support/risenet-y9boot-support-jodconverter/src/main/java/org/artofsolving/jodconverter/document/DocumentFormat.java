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
package org.artofsolving.jodconverter.document;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public class DocumentFormat {

    private String name;
    private String extension;
    private String mediaType;
    private DocumentFamily inputFamily;
    private Map<String, ?> loadProperties;
    private Map<DocumentFamily, Map<String, ?>> storePropertiesByFamily;

    public DocumentFormat() {
        // default
    }

    public DocumentFormat(String name, String extension, String mediaType) {
        this.name = name;
        this.extension = extension;
        this.mediaType = mediaType;
    }

    public String getExtension() {
        return extension;
    }

    public DocumentFamily getInputFamily() {
        return inputFamily;
    }

    public Map<String, ?> getLoadProperties() {
        return loadProperties;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getName() {
        return name;
    }

    public Map<String, ?> getStoreProperties(DocumentFamily family) {
        if (storePropertiesByFamily == null) {
            return null;
        }
        return storePropertiesByFamily.get(family);
    }

    public Map<DocumentFamily, Map<String, ?>> getStorePropertiesByFamily() {
        return storePropertiesByFamily;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public void setInputFamily(DocumentFamily documentFamily) {
        this.inputFamily = documentFamily;
    }

    public void setLoadProperties(Map<String, ?> loadProperties) {
        this.loadProperties = loadProperties;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStoreProperties(DocumentFamily family, Map<String, ?> storeProperties) {
        if (storePropertiesByFamily == null) {
            storePropertiesByFamily = new HashMap<DocumentFamily, Map<String, ?>>(16);
        }
        storePropertiesByFamily.put(family, storeProperties);
    }

    public void setStorePropertiesByFamily(Map<DocumentFamily, Map<String, ?>> storePropertiesByFamily) {
        this.storePropertiesByFamily = storePropertiesByFamily;
    }

    /**
     * Description:
     * 
     * @return
     */
    @Override
    public int hashCode() {
        return Objects.hash(extension, inputFamily, loadProperties, mediaType, name, storePropertiesByFamily);
    }

    /**
     * Description:
     * 
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DocumentFormat other = (DocumentFormat)obj;
        return Objects.equals(extension, other.extension) && inputFamily == other.inputFamily && Objects.equals(loadProperties, other.loadProperties) && Objects.equals(mediaType, other.mediaType) && Objects.equals(name, other.name)
            && Objects.equals(storePropertiesByFamily, other.storePropertiesByFamily);
    }

}
