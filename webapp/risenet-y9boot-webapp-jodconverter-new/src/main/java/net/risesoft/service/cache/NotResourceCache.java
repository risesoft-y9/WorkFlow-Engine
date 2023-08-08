package net.risesoft.service.cache;

import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.pdmodel.DefaultResourceCache;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;

import java.io.IOException;

/**
 * 解决图片 SoftReference 导致内存无法被回收导致的OOM
 */
public class NotResourceCache extends DefaultResourceCache {

    @Override
    public void put(COSObject indirect, PDXObject xobject) throws IOException {
        // do nothing
    }
}
