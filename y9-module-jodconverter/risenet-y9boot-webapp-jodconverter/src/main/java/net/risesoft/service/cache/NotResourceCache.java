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
    public void put(COSObject indirect, PDColorSpace colorSpace) {
        // 空实现以避免缓存颜色空间对象，防止因 SoftReference 导致的内存无法回收问题
    }

    @Override
    public void put(COSObject indirect, PDExtendedGraphicsState extGState) {
        // 空实现以避免缓存图形状态对象，防止因 SoftReference 导致的内存无法回收问题
    }

    @Override
    public void put(COSObject indirect, PDShading shading) {
        // 空实现以避免缓存阴影对象，防止因 SoftReference 导致的内存无法回收问题
    }

    @Override
    public void put(COSObject indirect, PDAbstractPattern pattern) {
        // 空实现以避免缓存图案对象，防止因 SoftReference 导致的内存无法回收问题
    }

    @Override
    public void put(COSObject indirect, PDPropertyList propertyList) {
        // 空实现以避免缓存属性列表对象，防止因 SoftReference 导致的内存无法回收问题
    }

    @Override
    public void put(COSObject indirect, PDXObject xobject) {
        // 空实现以避免缓存XObject对象，防止因 SoftReference 导致的内存无法回收问题
    }
}
