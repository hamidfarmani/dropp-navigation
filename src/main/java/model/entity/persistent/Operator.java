package model.entity.persistent;

import model.enums.AccountState;
import model.enums.City;
import model.enums.Gender;
import model.enums.UserRole;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by kasra on 4/22/2017.
 */

@NamedQueries({
        @NamedQuery(name = "operator.findBy.id", query = "select o from operator o where o.oId=:id"),
        @NamedQuery(name = "operator.like", query = "select o from operator o where o.username like :input or o.firstName like :input or o.lastName like :input or o.phoneNumber like :input or o.workNumber like :input"),
        @NamedQuery(name = "operator.exact.username", query = "select o from operator o where o.username=:username"),
        @NamedQuery(name = "operator.exact.usernameAndRole", query = "select o from operator o where o.username=:username and o.role=:role"),
        @NamedQuery(name = "operator.username.exist", query = "select o.id from operator o where o.username=:username and o.role=:role"),
        @NamedQuery(name = "operator.phoneNumber.exist", query = "select o from operator o where o.phoneNumber=:phoneNumber"),
        @NamedQuery(name = "operator.findBy.usernameAndPassword", query = "select o from operator o where o.username=:username and lower(o.password)=:password")

})
@Entity(name = "operator")
@Table(name = "OPERATOR")
public class Operator implements Serializable {

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = false)
    @JoinColumn(name = "OPERATOR_ID", referencedColumnName = "OPERATOR_ID")
    List<Message> messages;

    @Id
    @Column(name = "OPERATOR_ID", columnDefinition = "NUMBER")
    @SequenceGenerator(name = "operatorGen", sequenceName = "operatorIdSeq")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "operatorGen")
    private Long oId;

    @Basic
    @Column(name = "PHONE_NUMBER", columnDefinition = "VARCHAR2(20)")
    private String phoneNumber;

    @Basic
    @Column(name = "WORK_NUMBER", columnDefinition = "VARCHAR2(20)")
    private String workNumber;

    @Basic
    @Column(name = "CITY", columnDefinition = "VARCHAR2(2)")
    private City city;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = false)
    @JoinColumn(name = "FK_CREATOR", referencedColumnName = "OPERATOR_ID")
    private Operator creator;

    @Basic
    @Column(name = "USERNAME", columnDefinition = "NVARCHAR2(20)", unique = true)
    private String username;

    @Basic
    @Column(name = "PASSWORD", columnDefinition = "VARCHAR2(512)")
    private String password;

    @Basic
    @Column(name = "IS_LOGGED_IN", columnDefinition = "CHAR")
    private Boolean isLoggedIn;

    @Basic
    @Column(name = "FIRSTNAME", columnDefinition = "NVARCHAR2(32)")
    private String firstName;

    @Basic
    @Column(name = "LASTNAME", columnDefinition = "NVARCHAR2(32)")
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
    @Column(name = "ROLE", columnDefinition = "CHAR")
    private UserRole role;

    public Operator() {
    }

    public Long getoId() {
        return oId;
    }

    public void setoId(Long oId) {
        this.oId = oId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWorkNumber() {
        return workNumber;
    }

    public void setWorkNumber(String workNumber) {
        this.workNumber = workNumber;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Operator getCreator() {
        return creator;
    }

    public void setCreator(Operator creator) {
        this.creator = creator;
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

    public Boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(Boolean loggedIn) {
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

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
