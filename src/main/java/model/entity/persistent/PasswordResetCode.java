package model.entity.persistent;

import model.enums.PassResetCodeState;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by kasra on 8/5/2017.
 */
@Table(name = "PASSWORD_RESET_CODE")
@Entity(name = "passwordResetCode")
public class PasswordResetCode {

    @Id
    @Column(name = "ID", columnDefinition = "NUMBER")
    @SequenceGenerator(name = "passResetCodeGen", sequenceName = "passResetCodeSeq")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "passResetCodeGen")
    private long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_PASSENGER", referencedColumnName = "PASSENGER_ID")
    private Passenger passenger;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_DRIVER", referencedColumnName = "DRIVER_ID")
    private Driver driver;

    @Basic
    @Column(name = "REQ_DATE", columnDefinition = "TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Basic
    @Column(name = "STATE", columnDefinition = "CHAR")
    private PassResetCodeState state;

    @Basic
    @Column(name = "RESET_CODE", columnDefinition = "VARCHAR2(8)")
    private String resetCode;

    public PasswordResetCode() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public PassResetCodeState getState() {
        return state;
    }

    public void setState(PassResetCodeState state) {
        this.state = state;
    }

    public String getResetCode() {
        return resetCode;
    }

    public void setResetCode(String resetCode) {
        this.resetCode = resetCode;
    }
}
