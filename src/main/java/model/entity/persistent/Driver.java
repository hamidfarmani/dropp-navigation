package model.entity.persistent;

import model.enums.AccountState;
import model.enums.Gender;
import model.enums.ServiceType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by kasra on 12/29/2016.
 */
@NamedQueries({
        @NamedQuery(name = "driver.username.exist", query = "select d.id from driver d where d.username=:username"),
        @NamedQuery(name = "driver.phoneNumber.exist", query = "select d.id from driver d where d.phoneNumber=:phoneNumber"),
        @NamedQuery(name = "driver.searchLike.username", query = "select d from driver d where d.username like :username"),
        @NamedQuery(name = "driver.searchExact.username", query = "select d from driver d where d.username =:username"),
        @NamedQuery(name = "driver.searchExact.usernameAndProviderID", query = "select d from driver d where d.username =:username and d.serviceProvider.id=:providerID"),
        @NamedQuery(name = "driver.provider.username", query = "select d from driver d where d.username =:username and d.serviceProvider.id=:providerID and d.accountState!=-2"),
        @NamedQuery(name = "driver.searchLike", query = "select d from driver d left join d.vehicle v left  join  v.car c where d.username like :input or d.firstName like :input or d.lastName like :input or d.phoneNumber like :input or d.nationalNumber like :input or (v is not null and c is not null and c.name like :input) or (v is not null and v.licencePlate like :input) " ),
        @NamedQuery(name = "driver.findBy.usernameAndPassword", query = "select d from driver d where d.username=:username and d.password=:password"),
        @NamedQuery(name = "driver.get.credit",query = "select d.credit from driver d where username=:username"),
        @NamedQuery(name = "driver.findBy.accountState",query = "select d from driver d where d.accountState=:accountState"),
        @NamedQuery(name = "driver.all",query = "select d from driver d"),
        @NamedQuery(name = "driver.findBy.providerID.searchLike",query = "select d from driver d where d.serviceProvider.id=:providerID and d.username like :input"),
        @NamedQuery(name = "driver.findBy.providerID",query = "select d from driver d where d.serviceProvider.id=:providerID"),
        @NamedQuery(name = "driver.all.count",query = "select count(d.id) from driver d"),
        @NamedQuery(name = "driver.findBy.serviceType",query = "select d from driver d where d.serviceType=:serviceType"),
        @NamedQuery(name = "driver.orderby.credit", query = "select d from driver d where d.serviceProvider.id=:providerID and d.credit<0 order by d.credit asc"),
        @NamedQuery(name = "driver.orderby.gt.creditAndProviderID", query = "select d from driver d where d.serviceProvider.id=:providerID and d.credit <= :value order by d.credit asc")
})
@Entity(name = "driver")
@Table(name = "DRIVER", indexes = {
        @Index(name = "DRIVER_PHONENUMBER_INDEX", columnList = "PHONE_NUMBER"),
        @Index(name = "DRIVER_USERNAME_INDEX",columnList = "USERNAME")
})
public class Driver implements Serializable {

    @Id
    @Column(name = "DRIVER_ID", columnDefinition = "NUMBER")
    @SequenceGenerator(name = "driverGen", sequenceName = "driverSeq")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "driverGen")
    private long id;

    @Basic
    @Column(name = "PHONE_NUMBER", columnDefinition = "VARCHAR2(20)")
    private String phoneNumber;

    @Basic
    @Column(name = "NATIONAL_NUMBER", columnDefinition = "VARCHAR2(11)")
    private String nationalNumber;

    @Basic
    @Column(name = "SERVICE_TYPE", columnDefinition = "CHAR")
    private ServiceType serviceType;

    /*
بیت صفر :دریافت خبرنامه
بیت یک :دریافت ایمیل تراکنش
بیت دو :دریافت پیامک تراکنش

 */
    @Basic
    @Column(name = "SETTING", columnDefinition = "CHAR(3) default 100")
    private String setting;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "FK_ADDRESS", referencedColumnName = "ADDRESS_ID")
    private Address address;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = false)
    @JoinColumn(name = "FK_VEHICLE", referencedColumnName = "ID")
    private Vehicle vehicle;

    @Basic
    @Column(name = "USERNAME", columnDefinition = "NVARCHAR2(20)")
    private String username;

    @Basic
    @Column(name = "PASSWORD", columnDefinition = "VARCHAR2(512)")
    private String password;

    @Basic
    @Column(name = "IS_LOGGED_IN", columnDefinition = "CHAR")
    private boolean isLoggedIn;

    @Basic
    @Column(name = "FIRST_NAME", columnDefinition = "NVARCHAR2(32)")
    private String firstName;

    @Basic
    @Column(name = "LAST_NAME", columnDefinition = "NVARCHAR2(32)")
    private String lastName;

    @Basic
    @Column(name = "EMAIL", columnDefinition = "VARCHAR2(100)")
    private String email;

    @Basic
    @Column(name = "GENDER", columnDefinition = "CHAR")
    private Gender gender;

    @Basic
    @Column(name = "BIRTH_DATE", columnDefinition = "DATE")
    private Date birthDate;
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

    @Basic
    @Column(name = "BANK_CARD_NUMBER", columnDefinition = "NVARCHAR2(16)")
    private String bankCardNumber;

    @Basic
    @Column(name = "BANK_ACCOUNT_NUMBER", columnDefinition = "NVARCHAR2(16)")
    private String bankAccountNumber;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "FK_DEVICE", referencedColumnName = "ID")
    private Device device;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = false)
    @JoinColumn(name = "FK_VERIFIER", referencedColumnName = "OPERATOR_ID")
    private Operator operator;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = false)
    @JoinColumn(name = "DRIVER_ID", referencedColumnName = "DRIVER_ID")
    private List<Message> messages;


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = false)
    @JoinColumn(name = "FK_DRIVER", referencedColumnName = "DRIVER_ID")
    private List<Trip> trips;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "FK_SERVICE_PROVIDER", referencedColumnName = "ID")
    private ServiceProvider serviceProvider;

    public Driver() {
    }

    public long getdId() {
        return id;
    }

    public void setdId(long dId) {
        this.id = dId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNationalNumber() {
        return nationalNumber;
    }

    public void setNationalNumber(String nationalNumber) {
        this.nationalNumber = nationalNumber;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
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

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
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

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }


    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }

    public String getBankCardNumber() {
        return bankCardNumber;
    }

    public void setBankCardNumber(String bankCardNumber) {
        this.bankCardNumber = bankCardNumber;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getSetting() {
        return setting;
    }

    public void setSetting(String setting) {
        this.setting = setting;
    }

    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }
}