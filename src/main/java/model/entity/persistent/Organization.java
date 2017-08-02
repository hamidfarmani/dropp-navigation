package model.entity.persistent;

import model.enums.AccountState;
import model.enums.EmployeeCount;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by kasra on 4/29/2017.
 */
@NamedQueries({
        @NamedQuery(name = "organization.findBy.username", query = "select o from organization o where o.username like :username"),
        @NamedQuery(name = "organization.like", query = "select o from organization o where o.orgName like :input or o.username like :input"),
        @NamedQuery(name = "organization.new.count",query = "select count(o.id) from organization o where trunc(o.registrationTimestamp) >= trunc(:date)"),
        @NamedQuery(name = "organization.all.count",query = "select count(o.id) from organization o"),
        @NamedQuery(name = "organization.exact.username", query = "select o from organization o where o.username =:username"),
        @NamedQuery(name = "organization.findBy.phoneNumber", query = "select o from organization o where o.phoneNumber=:phoneNumber")

})
@Entity(name = "organization")
@Table(name = "ORGANIZATION")
public class Organization implements Serializable {

    @Id
    @Column(name = "ORG_ID", columnDefinition = "NUMBER")
    @SequenceGenerator(name = "orgGen", sequenceName = "orgSeq")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "orgGen")
    private Long id;

    @Basic
    @Column(name = "ORG_NAME", columnDefinition = "NVARCHAR2(40)")
    private String orgName;

    @Basic
    @Column(name = "REGISTRAR_FIRSTNAME", columnDefinition = "NVARCHAR2(32)")
    private String registrarFirstName;

    @Basic
    @Column(name = "REGISTRAR_LASTNAME", columnDefinition = "NVARCHAR2(32)")
    private String registrarLastName;

    @Basic
    @Column(name = "REGISTRAR_ROLE", columnDefinition = "NVARCHAR2(25)")
    private String registrarRole;

    @Basic
    @Column(name = "PHONE_NUMBER", columnDefinition = "VARCHAR2(11)")
    private String phoneNumber;

    @Basic
    @Column(name = "WORK_NUMBER", columnDefinition = "VARCHAR2(11)")
    private String workNumber;

    @Basic
    @Column(name = "EMAIL", columnDefinition = "VARCHAR2(40)")
    private String email;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "FK_ADDRESS", referencedColumnName = "ADDRESS_ID")
    private Address address;

    @Basic
    @Column(name = "EMPLOYEE_COUNT", columnDefinition = "NUMBER(1)")
    private EmployeeCount empCount;

    @Basic
    @Column(name = "USERNAME", columnDefinition = "NVARCHAR2(20)")
    private String username;

    @Basic
    @Column(name = "PASSWORD", columnDefinition = "VARCHAR2(512)")
    private String password;

    @Basic
    @Column(name = "ACCOUNT_STATE", columnDefinition = "NUMBER(2)")
    private AccountState accountState;

    @Basic
    @Column(name = "DESCRIPTION", columnDefinition = "NVARCHAR2(250)")
    private String description;

    @Basic
    @Column(name = "CREDIT", columnDefinition = "NUMBER(9)")
    private long credit;

    @Basic
    @Column(name = "REGISTRATION_TIMESTAMP", columnDefinition = "TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date registrationTimestamp;

    public Organization() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getRegistrarRole() {
        return registrarRole;
    }

    public void setRegistrarRole(String registrarRole) {
        this.registrarRole = registrarRole;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public EmployeeCount getEmpCount() {
        return empCount;
    }

    public void setEmpCount(EmployeeCount empCount) {
        this.empCount = empCount;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRegistrarFirstName() {
        return registrarFirstName;
    }

    public void setRegistrarFirstName(String registrarFirstName) {
        this.registrarFirstName = registrarFirstName;
    }

    public String getRegistrarLastName() {
        return registrarLastName;
    }

    public void setRegistrarLastName(String registrarLastName) {
        this.registrarLastName = registrarLastName;
    }

    public long getCredit() {
        return credit;
    }

    public void setCredit(long credit) {
        this.credit = credit;
    }

    public Date getRegistrationTimestamp() {
        return registrationTimestamp;
    }

    public void setRegistrationTimestamp(Date registrationTimestamp) {
        this.registrationTimestamp = registrationTimestamp;
    }
}
