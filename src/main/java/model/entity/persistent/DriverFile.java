package model.entity.persistent;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by kasra on 4/30/2017.
 */
@Entity(name = "driverFile")
@Table(name = "DRIVER_FILE")
public class DriverFile implements Serializable {

    @Id
    @Column(name = "FILE_ID", columnDefinition = "NUMBER")
    @SequenceGenerator(name = "fileGen", sequenceName = "fileSeq")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "fileGen")
    private Long id;

    @Basic
    @Column(name = "URL",columnDefinition = "NVARCHAR2(100)")
    private String url;

    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = false)
    @JoinColumn(name = "FK_DRIVER",referencedColumnName = "DRIVER_ID")
    private Driver driver;

    public DriverFile() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }
}
