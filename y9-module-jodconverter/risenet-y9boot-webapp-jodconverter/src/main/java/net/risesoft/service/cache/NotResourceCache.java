package net.risesoft.service.cache;

import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.pdmodel.DefaultResourceCache;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDPropertyList;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDAbstractPattern;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShading;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;

/**
 * 解决图片 SoftReference 导致内存无法被回收导致的OOM
 */
public class NotResourceCache extends DefaultResourceCache {

    @Override
    public void put(COSObject indirect, PDColorSpace colorSpace) {}

    @Override
    public void put(COSObject indirect, PDExtendedGraphicsState extGState) {}

    @Override
    public void put(COSObject indirect, PDShading shading) {}

    @Override
    public void put(COSObject indirect, PDAbstractPattern pattern) {}

    @Override
    public void put(COSObject indirect, PDPropertyList propertyList) {}

    @Override
    public void put(COSObject indirect, PDXObject xobject) {}
}
