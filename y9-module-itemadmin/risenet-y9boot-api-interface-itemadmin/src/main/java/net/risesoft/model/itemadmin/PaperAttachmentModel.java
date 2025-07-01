package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * 纸质附件模型
 *
 * @author qinman
 * @date 2024/11/07
 */
@Data
@NoArgsConstructor
public class PaperAttachmentModel implements Serializable {

    private static final long serialVersionUID = -9172976734369649360L;

    private String id;

    @NonNull
    private String processSerialNumber;

    @NonNull
    private String name;

    @NonNull
    private Integer count;

    @NonNull
    private Integer pages;

    @NonNull
    private String miJi;

    private String personId;

    private String personName;

    private String uploadTime;

    private Integer tabIndex;
}
