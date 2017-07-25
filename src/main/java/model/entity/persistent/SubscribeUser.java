package model.entity.persistent;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by kasra on 4/30/2017.
 */


@NamedQueries({
        @NamedQuery(name = "getSubUser.by.subscriptionCode", query = "select s from subscribeUser s where s.subscriptionCode=:subscriptionCode"),
        @NamedQuery(name = "subscribe.phoneNumber.exist", query = "select s from subscribeUser s where s.phoneNumber=:phoneNumber"),
        @NamedQuery(name = "subscribe.searchLike", query = "select s from subscribeUser s where s.subscriptionCode like :input or s.firstName like :input or s.lastName like :input or s.phoneNumber like :input"),
})

@Entity(name = "subscribeUser")
@Table(name = "SUBSCRIBE_USER", indexes = {
        @Index(name = "SUBSCRIPTION_CODE_INDEX", columnList = "SUBSCRIPTION_CODE")
})
public class SubscribeUser implements Serializable {

    @Id
    @Column(name = "SUBUSER_ID", columnDefinition = "NUMBER")
    @SequenceGenerator(name = "subUserGen", sequenceName = "subUserIdSeq")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "subUserGen")
    private Long Id;

    @Basic
    @Column(name = "FIRSTNAME", columnDefinition = "NVARCHAR2(25)")
    private String firstName;

    @Basic
    @Column(name = "LASTNAME", columnDefinition = "NVARCHAR2(25)")
    private String lastName;

    @Basic
    @Column(name = "PHONENUMBER", columnDefinition = "VARCHAR2(11)")
    private String phoneNumber;

    @Basic
    @Column(name = "SUBSCRIPTION_CODE", columnDefinition = "VARCHAR(6)")
    private String subscriptionCode;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "ADDRESS", referencedColumnName = "ADDRESS_ID")
    private Address address;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = false)
    @JoinColumn(name = "FK_SUBUSER", referencedColumnName = "SUBUSER_ID")
    private List<Trip> trips;


    public SubscribeUser() {
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSubscriptionCode() {
        return subscriptionCode;
    }

    public void setSubscriptionCode(String subscriptionCode) {
        this.subscriptionCode = subscriptionCode;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }


}
