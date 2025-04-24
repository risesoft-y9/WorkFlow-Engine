package net.risesoft.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@Table(name = "PUSHDATA", indexes = {@Index(columnList = "TSZT")})
public class PushData implements Serializable {

    @Id
    @Column(name = "ID", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Column(name = "EVENTTYPE", length = 30)
    private String eventtype;

    @Column(name = "CREATEDATE", length = 39)
    private Date createdate;

    @Column(name = "PROCESSSERIALNUMBER", length = 50)
    private String processserialnumber;

    @Column(name = "PROCESSINSTANCEID", length = 50)
    private String processinstanceid;

    @Column(name = "TSZT", length = 10)
    private String tszt;

    @Column(name = "TSDATE", length = 39)
    private Date tsdate;

    @Column(name = "TSJG", length = 100)
    private String tsjg;

    @Column(name = "XMDM",length = 50)
    private String xmdm;

}
