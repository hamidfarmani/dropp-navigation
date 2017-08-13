package model.entity.persistent;

import model.enums.LogType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by kasra on 4/30/2017.
 */
@Entity(name = "driverLog")
@Table(name = "DRIVER_LOG")
public class DriverLog implements Serializable {

    @Id
    @Column(name = "D_LOG_ID", columnDefinition = "NUMBER")
    @SequenceGenerator(name = "dLogGen", sequenceName = "dLogSeq")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "dLogGen")
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = false)
    @JoinColumn(name = "FK_DRIVER", referencedColumnName = "DRIVER_ID")
    private Driver driver;

    @Basic
    @Column(name = "LOG_TIMESTAMP", columnDefinition = "TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date logTimestamp;

    @Basic
    @Column(name = "LOG_TYPE",columnDefinition = "CHAR")
    private LogType logType;

    public DriverLog() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Date getLogTimestamp() {
        return logTimestamp;
    }

    public void setLogTimestamp(Date logTimestamp) {
        this.logTimestamp = logTimestamp;
    }

    public LogType getLogType() {
        return logType;
    }

    public void setLogType(LogType logType) {
        this.logType = logType;
    }
}
