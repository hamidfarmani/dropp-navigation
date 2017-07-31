package model.entity.persistent;

import model.enums.AccountState;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by kasra on 12/29/2016.
 */
@NamedQueries({
        @NamedQuery(name = "passenger.username.exist", query = "select p.id from passenger p where p.username=:username"),
        @NamedQuery(name = "passenger.phoneNumber.exist", query = "select p.id from passenger p where p.phoneNumber=:phoneNumber"),
        @NamedQuery(name = "passenger.id.findBy.username", query = "select p.id from passenger  p where p.username=:username"),
        @NamedQuery(name = "passenger.exact.username", query = "select p from passenger  p where p.username =:username"),
        @NamedQuery(name = "passenger.findBy.username", query = "select p from passenger  p where p.username like :username"),
        @NamedQuery(name = "passenger.searchLike", query = "select p from passenger p left join p.passengerInfo i where p.username like :input or i.firstName like :input or i.lastName like :input or p.phoneNumber like :input " ),
        @NamedQuery(name = "passenger.get.credit",query = "select p.credit from passenger p where username=:username"),
        @NamedQuery(name = "passenger.findBy.usernameAndPassword", query = "select p from passenger p where p.username=:username and p.password=:password"),
        @NamedQuery(name = "passenger.findBy.accountState",query = "select p from passenger p where p.accountState=:accountState"),
        @NamedQuery(name = "passenger.all.count",query = "select count(p.id) from passenger p"),
        @NamedQuery(name = "passenger.new.count",query = "select count(p.id) from passenger p where p.registrationTimestamp >= :date")
})
@Entity(name = "passenger")
@Table(name = "PASSENGER", indexes = {
        @Index(name = "PASSENGER_PHONENUMBER_INDEX", columnList = "PHONE_NUMBER")
})
public class Passenger implements Serializable {
    @Id
    @Column(name = "PASSENGER_ID", columnDefinition = "NUMBER")
    @SequenceGenerator(name = "passengerGen", sequenceName = "passengerSeq")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "passengerGen")
    private long pId;

    @Basic
    @Column(name = "PHONE_NUMBER", columnDefinition = "VARCHAR2(20)", unique = true)
    private String phoneNumber;

    @Basic
    @Column(name = "USERNAME", columnDefinition = "NVARCHAR2(20)", unique = true)
    private String username;

    @Basic
    @Column(name = "PASSWORD", columnDefinition = "VARCHAR2(512)")
    private String password;

    @Basic
    @Column(name = "IS_LOGGED_IN", columnDefinition = "CHAR")
    private boolean isLoggedIn;

    @Basic
    @Column(name = "ACCOUNT_STATE", columnDefinition = "NUMBER(2)")
    private AccountState accountState;

    @Basic
    @Column(name = "REGISTRATION_TIMESTAMP", columnDefinition = "TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date registrationTimestamp;

    @Basic
    @Column(name = "CREDIT", columnDefinition = "NUMBER(9)")
    private long credit;

    /*
    بیت صفر :دریافت خبرنامه
    بیت یک :دریافت ایمیل اطلاعات صفر
    بیت دو :دریافت پیامک اطلاعات صفر
    بیت سه : دریافت ایمیل تراکنش ها
    بیت چهار: دریافت پیامک تراکنش ها
     */
    @Basic
    @Column(name = "SETTING", columnDefinition = "CHAR(5) default 10000")
    private String setting;

    @Basic
    @Column(name = "POINT", columnDefinition = "NUMBER(9)")
    private Integer point;



    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "FK_RECOMMENDER", referencedColumnName = "PASSENGER_ID")
    private Passenger recommender;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "FK_DEVICE", referencedColumnName = "ID")
    private Device device;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "FK_INFO", referencedColumnName = "PASSENGER_INFO_ID")
    private PassengerInfo passengerInfo;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "FK_PASSENGER", referencedColumnName = "PASSENGER_ID")
    private List<FavPlace> favPlaces;


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = false)
    @JoinColumn(name = "FK_PASSENGER", referencedColumnName = "PASSENGER_ID")
    private List<Trip> trips;

    public Passenger() {
    }

    public long getpId() {
        return pId;
    }

    public void setpId(long pId) {
        this.pId = pId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AccountState getAccountState() {
        return accountState;
    }

    public void setAccountState(AccountState accountState) {
        this.accountState = accountState;
    }

    public Date getRegistrationTimestamp() {
        return registrationTimestamp;
    }

    public void setRegistrationTimestamp(Date registrationTimestamp) {
        this.registrationTimestamp = registrationTimestamp;
    }

    public long getCredit() {
        return credit;
    }

    public void setCredit(long credit) {
        this.credit = credit;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public List<FavPlace> getFavPlaces() {
        return favPlaces;
    }

    public void setFavPlaces(List<FavPlace> favPlaces) {
        this.favPlaces = favPlaces;
    }


    public PassengerInfo getPassengerInfo() {
        return passengerInfo;
    }

    public void setPassengerInfo(PassengerInfo passengerInfo) {
        this.passengerInfo = passengerInfo;
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public String getSetting() {
        return setting;
    }

    public void setSetting(String setting) {
        this.setting = setting;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public Passenger getRecommender() {
        return recommender;
    }

    public void setRecommender(Passenger recommender) {
        this.recommender = recommender;
    }

    public int getStar() {
        if (getPoint() < 100)
            return 0;
        if (getPoint() < 250)
            return 1;
        if (getPoint() < 500)
            return 2;
        if (getPoint() < 1000)
            return 3;
        if (getPoint() < 3000)
            return 4;
        if (getPoint() < 6000)
            return 5;
        if (getPoint() < 10000)
            return 6;
        else return 7;
    }

}
