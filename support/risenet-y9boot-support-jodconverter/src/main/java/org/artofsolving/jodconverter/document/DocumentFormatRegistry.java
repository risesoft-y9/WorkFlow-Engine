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

import java.util.Set;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public interface DocumentFormatRegistry {

    /**
     * Description:
     * 
     * @param extension
     * @return
     */
    public DocumentFormat getFormatByExtension(String extension);

    /**
     * Description:
     * 
     * @param mediaType
     * @return
     */
    public DocumentFormat getFormatByMediaType(String mediaType);

    /**
     * Description:
     * 
     * @param family
     * @return
     */
    public Set<DocumentFormat> getOutputFormats(DocumentFamily family);

}
