package net.risesoft.model;

/**
 * Created by kl on 2018/1/17. Content :
 */
/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
public class FileAttribute {

    private FileType type;

    private String suffix;

    private String name;

    private String url;

    private String decodedUrl;

    public FileAttribute() {}

    public FileAttribute(FileType type, String suffix, String name, String url, String decodedUrl) {
        this.type = type;
        this.suffix = suffix;
        this.name = name;
        this.url = url;
        this.decodedUrl = decodedUrl;
    }

    public String getDecodedUrl() {
        return decodedUrl;
    }

    public String getName() {
        return name;
    }

    public String getSuffix() {
        return suffix;
    }

    public FileType getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public void setDecodedUrl(String decodedUrl) {
        this.decodedUrl = decodedUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void setType(FileType type) {
        this.type = type;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
