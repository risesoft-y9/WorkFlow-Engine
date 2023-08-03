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

import org.artofsolving.jodconverter.document.DocumentFamily;
import org.artofsolving.jodconverter.office.OfficeException;

import com.sun.star.lang.XComponent;
import com.sun.star.lang.XServiceInfo;

class OfficeDocumentUtils {

    public static DocumentFamily getDocumentFamily(XComponent document) throws OfficeException {
        XServiceInfo serviceInfo = cast(XServiceInfo.class, document);
        String genericTextDocument = "com.sun.star.text.GenericTextDocument",
            spreadsheetDocument = "com.sun.star.sheet.SpreadsheetDocument",
            presentationDocument = "com.sun.star.presentation.PresentationDocument",
            drawingDocument = "com.sun.star.drawing.DrawingDocument";
        if (serviceInfo.supportsService(genericTextDocument)) {
            return DocumentFamily.TEXT;
        } else if (serviceInfo.supportsService(spreadsheetDocument)) {
            return DocumentFamily.SPREADSHEET;
        } else if (serviceInfo.supportsService(presentationDocument)) {
            return DocumentFamily.PRESENTATION;
        } else if (serviceInfo.supportsService(drawingDocument)) {
            return DocumentFamily.DRAWING;
        } else {
            throw new OfficeException("document of unknown family: " + serviceInfo.getImplementationName());
        }
    }

    private OfficeDocumentUtils() {
        throw new AssertionError("utility class must not be instantiated");
    }

}
