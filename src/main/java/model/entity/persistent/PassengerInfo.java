package model.entity.persistent;

import model.enums.Gender;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by kasra on 4/30/2017.
 */
@Entity(name = "passengerInfo")
@Table(name = "PASSENGER_INFO")
public class PassengerInfo implements Serializable{

    @Id
    @Column(name = "PASSENGER_INFO_ID", columnDefinition = "NUMBER")
    @SequenceGenerator(name = "pInfoGen", sequenceName = "pInfoSeq")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "pInfoGen")
    private Long pId;

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

    public PassengerInfo() {
    }

    public Long getpId() {
        return pId;
    }

    public void setpId(long pId) {
        this.pId = pId;
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
}
