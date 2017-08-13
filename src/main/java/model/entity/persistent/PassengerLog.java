package model.entity.persistent;

import model.enums.LogType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by kasra on 4/30/2017.
 */
@Entity(name = "passengerLog")
@Table(name = "PASSENGER_LOG")
public class PassengerLog implements Serializable {

    @Id
    @Column(name = "P_LOG_ID", columnDefinition = "NUMBER")
    @SequenceGenerator(name = "pLogGen", sequenceName = "pLogSeq")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "pLogGen")
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = false)
    @JoinColumn(name = "FK_PASSENGER", referencedColumnName = "PASSENGER_ID")
    private Passenger passenger;

    @Basic
    @Column(name = "LOG_TIMESTAMP", columnDefinition = "TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date logTimestamp;

    @Basic
    @Column(name = "LOG_TYPE",columnDefinition = "CHAR")
    private LogType logType;

    public PassengerLog() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
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
