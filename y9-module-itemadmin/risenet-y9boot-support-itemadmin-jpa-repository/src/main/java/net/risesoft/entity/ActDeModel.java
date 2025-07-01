package net.risesoft.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.Data;

/**
 * @author : qinman
 * @date : 2024-10-11
 **/
@Entity
@Data
@Table(name = "ACT_DE_MODEL")
public class ActDeModel implements Serializable {

    private static final long serialVersionUID = 2081551802722131038L;

    @Id
    @Column(name = "ID", nullable = false)
    private String id;

    @Column(name = "NAME", length = 400, nullable = false)
    private String name;

    @Column(name = "MODEL_KEY", length = 400, nullable = false)
    private String key;

    @Column(name = "DESCRIPTION", length = 4000)
    private String description;

    @Column(name = "MODEL_COMMENT", length = 4000)
    private String comment;

    @Column(name = "CREATED", length = 6)
    private Date created;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "LAST_UPDATED", length = 6)
    private Date lastUpdated;

    @Column(name = "LAST_UPDATED_BY")
    private String lastUpdatedBy;

    @Column(name = "VERSION")
    private int version;

    @Lob
    @Column(name = "MODEL_EDITOR_JSON")
    private String modelEditorJson;

    @Lob
    @Column(name = "THUMBNAIL")
    private byte[] thumbnail;

    @Column(name = "MODEL_TYPE")
    private int modelType;

    @Column(name = "TENANT_ID")
    private String tenantId;
}
