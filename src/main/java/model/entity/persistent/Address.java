package model.entity.persistent;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by kasra on 2/4/2017.
 */
@Entity(name = "address")
@Table(name = "ADDRESS")
public class Address implements Serializable {

    @Id
    @Column(name = "ADDRESS_ID",columnDefinition = "NUMBER")
    @SequenceGenerator(name = "AddressGen" , sequenceName = "AddressSeq")
    @GeneratedValue(strategy = GenerationType.AUTO,generator = "AddressGen")
    private Long id;

    @Basic
    @Column(name = "LINE1" , columnDefinition = "NVARCHAR2(50)")
    private String line1;

    @Basic
    @Column(name = "LINE2" , columnDefinition = "NVARCHAR2(100)")
    private String line2;

    @Basic
    @Column(name = "POSTAL_CODE" , columnDefinition = "NVARCHAR2(10)")
    private String postalCode;


    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "FK_CITY", referencedColumnName = "CITY_ID")
    private City city;

    public Address() {
    }

    public Address(String line1, String line2, String postalCode, City city) {
        this.line1 = line1;
        this.line2 = line2;
        this.postalCode = postalCode;
        this.city = city;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }


    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}
