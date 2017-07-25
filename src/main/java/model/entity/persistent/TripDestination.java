package model.entity.persistent;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by kasra on 4/29/2017.
 */
@Entity(name = "tripDestination")
@Table(name = "TRIP_DESTINATION")
public class TripDestination implements Serializable {

    @Id
    @Column(name = "DEST_ID", columnDefinition = "NUMBER")
    @SequenceGenerator(name = "DestGen", sequenceName = "DestSeq")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "DestGen")
    private Long id;

    @Basic
    @Column(name = "SEQ_NUMBER", columnDefinition = "NUMBER(2)")
    private Short seqNumber;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "FK_LOCATION", referencedColumnName = "LOC_ID")
    private Location location;

    @Basic
    @Column(name = "DESTINATION_ADDRESS", columnDefinition = "NVARCHAR2(150)")
    private String address;

    public TripDestination() {
    }

    public TripDestination(short seqNumber, Location location, String destinationAddress) {
        this.seqNumber = seqNumber;
        this.location = location;
        this.address = destinationAddress;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Short getSeqNumber() {
        return seqNumber;
    }

    public void setSeqNumber(Short seqNumber) {
        this.seqNumber = seqNumber;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String destinationAddress) {
        this.address = destinationAddress;
    }
}
