package model.entity.persistent;

import model.enums.TicketState;

import javax.persistence.*;

/**
 * Created by kasra on 7/1/2017.
 */
@NamedQueries(
        {
            @NamedQuery(name = "bug.all",query = "select b from bug b")
        }
)

@Entity(name = "bug")
@Table(name = "BUG")
public class Bug {



    @Id
    @Column(name = "BUG_ID", columnDefinition = "NUMBER")
    @SequenceGenerator(name = "bugGen", sequenceName = "bugSeq")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "bugGen")
    private Long id;


    @Basic
    @Column(name = "STATE", columnDefinition = "CHAR")
    private TicketState state;


    @Basic
    @Column(name = "DESCRIPTION", columnDefinition = "NVARCHAR2(500)")
    private String desc;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_PASSENGER", referencedColumnName = "PASSENGER_ID")
    private Passenger passenger;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_DRIVER", referencedColumnName = "DRIVER_ID")
    private Driver driver;

    public Bug() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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

    public TicketState getState() {
        return state;
    }

    public void setState(TicketState state) {
        this.state = state;
    }
}
