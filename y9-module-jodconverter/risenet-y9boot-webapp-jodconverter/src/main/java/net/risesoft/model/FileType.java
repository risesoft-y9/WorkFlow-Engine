package net.risesoft.model;

/**
 * Created by kl on 2018/1/17. Content :文件类型，文本，office，压缩包等等
 */
/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
public enum FileType {
    /**
    * 
    */
    picture("pictureFilePreviewImpl"),
    /**
    * 
    */
    compress("compressFilePreviewImpl"),
    /**
    * 
    */
    office("officeFilePreviewImpl"),
    /**
    * 
    */
    simText("simTextFilePreviewImpl"),
    /**
    * 
    */
    pdf("pdfFilePreviewImpl"),
    /**
    * 
    */
    other("otherFilePreviewImpl"),
    /**
    * 
    */
    media("mediaFilePreviewImpl");

    private String instanceName;

    FileType(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }
}
