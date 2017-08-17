package model.entity.persistent;

import model.enums.AccountState;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by kasra on 7/27/2017.
 */

@Table(name = "PASSENGER_STATE_HISTORY")
@Entity(name = "passengerStateHistory")
public class PassengerStateHistory implements Serializable {

    @Id
    @Column(name = "ID", columnDefinition = "NUMBER")
    @SequenceGenerator(name = "pStateGen", sequenceName = "pStateSeq")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "pStateGen")
    private long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_PASSENGER", referencedColumnName = "PASSENGER_ID")
    private Passenger passenger;

    @Basic
    @Column(name = "CURRENT_STATE", columnDefinition = "NUMBER(2)")
    private AccountState currentState;

    @Basic
    @Column(name = "NEW_STATE", columnDefinition = "NUMBER(2)")
    private AccountState newState;

    @Basic
    @Column(name = "CHANGE_DATE", columnDefinition = "TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date changeDate;

    @Basic
    @Column(name = "DESCRIPTION", columnDefinition = "NVARCHAR2(150)")
    private String desc;

    public PassengerStateHistory() {
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

    public AccountState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(AccountState currentState) {
        this.currentState = currentState;
    }

    public AccountState getNewState() {
        return newState;
    }

    public void setNewState(AccountState newState) {
        this.newState = newState;
    }

    public Date getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
