package model.entity.persistent;

import model.enums.TicketState;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by kasra on 4/30/2017.
 */
@NamedQueries({
        @NamedQuery(name = "ticket.findBy.Unresolved", query = "select t from ticket t where t.ticketState=:state"),
        @NamedQuery(name = "ticket.findBy.driverID", query = "select t from ticket t where t.driver.id=:driverID"),
        @NamedQuery(name = "ticket.findBy.id", query = "select t from ticket t where t.id=:id")
})
@Entity(name = "ticket")
@Table(name = "TICKET")
public class Ticket implements Serializable {

    @Id
    @Column(name = "ID", columnDefinition = "NUMBER")
    @SequenceGenerator(name = "ticketGen", sequenceName = "ticketSeq")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "ticketGen")
    private Long id;

    @Basic
    @Column(name = "DESCRIPTION", columnDefinition = "NVARCHAR2(250)")
    private String description;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = false)
    @JoinColumn(name = "FK_TICKET_SUB", referencedColumnName = "ID")
    private TicketSubject ticketSubject;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = false)
    @JoinColumn(name = "FK_PASSENGER", referencedColumnName = "PASSENGER_ID")
    private Passenger passenger;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = false)
    @JoinColumn(name = "FK_DRIVER", referencedColumnName = "DRIVER_ID")
    private Driver driver;

    @Basic
    @Column(name = "STATE", columnDefinition = "CHAR")
    private TicketState ticketState;

    @Basic
    @Column(name = "TIME", columnDefinition = "TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date time;



    public Ticket() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TicketSubject getTicketSubject() {
        return ticketSubject;
    }

    public void setTicketSubject(TicketSubject ticketSubject) {
        this.ticketSubject = ticketSubject;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public TicketState getTicketState() {
        return ticketState;
    }

    public void setTicketState(TicketState ticketState) {
        this.ticketState = ticketState;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
