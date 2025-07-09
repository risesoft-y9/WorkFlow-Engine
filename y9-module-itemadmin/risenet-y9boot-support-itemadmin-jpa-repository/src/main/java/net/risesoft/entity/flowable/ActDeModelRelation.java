package net.risesoft.entity.flowable;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * @author : qinman
 * @date : 2024-10-11
 **/
@Entity
@Data
@Table(name = "ACT_DE_MODEL_RELATION")
public class ActDeModelRelation implements Serializable {

    private static final long serialVersionUID = -7663227857236103490L;

    @Id
    @Column(name = "ID", nullable = false)
    private String id;

    @Column(name = "PARENT_MODEL_ID")
    private String parentModelId;

    @Column(name = "MODEL_ID")
    private String modelId;

    @Column(name = "RELATION_TYPE")
    private String type;
}
