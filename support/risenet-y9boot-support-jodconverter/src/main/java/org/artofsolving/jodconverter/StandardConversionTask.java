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
package org.artofsolving.jodconverter;

import static org.artofsolving.jodconverter.office.OfficeUtils.cast;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.artofsolving.jodconverter.document.DocumentFamily;
import org.artofsolving.jodconverter.document.DocumentFormat;
import org.artofsolving.jodconverter.office.OfficeException;

import com.sun.star.lang.XComponent;
import com.sun.star.util.XRefreshable;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public class StandardConversionTask extends AbstractConversionTask {

    private final DocumentFormat outputFormat;

    private Map<String, ?> defaultLoadProperties;
    private DocumentFormat inputFormat;

    public StandardConversionTask(File inputFile, File outputFile, DocumentFormat outputFormat) {
        super(inputFile, outputFile);
        this.outputFormat = outputFormat;
    }

    @Override
    protected Map<String, ?> getLoadProperties(File inputFile) {
        Map<String, Object> loadProperties = new HashMap<String, Object>(16);
        if (defaultLoadProperties != null) {
            loadProperties.putAll(defaultLoadProperties);
        }
        if (inputFormat != null && inputFormat.getLoadProperties() != null) {
            loadProperties.putAll(inputFormat.getLoadProperties());
        }
        return loadProperties;
    }

    @Override
    protected Map<String, ?> getStoreProperties(File outputFile, XComponent document) {
        DocumentFamily family = OfficeDocumentUtils.getDocumentFamily(document);
        return outputFormat.getStoreProperties(family);
    }

    @Override
    protected void modifyDocument(XComponent document) throws OfficeException {
        XRefreshable refreshable = cast(XRefreshable.class, document);
        if (refreshable != null) {
            refreshable.refresh();
        }
    }

    public void setDefaultLoadProperties(Map<String, ?> defaultLoadProperties) {
        this.defaultLoadProperties = defaultLoadProperties;
    }

    public void setInputFormat(DocumentFormat inputFormat) {
        this.inputFormat = inputFormat;
    }

}
