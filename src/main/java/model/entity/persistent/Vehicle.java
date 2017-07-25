package model.entity.persistent;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by kasra on 12/29/2016.
 */
@Entity(name = "vehicle")
@Table(name = "VEHICLE")
public class Vehicle implements Serializable {

    @Id
    @Column(name = "ID", columnDefinition = "NUMBER")
    @SequenceGenerator(name = "vehicleGen", sequenceName = "vehicleSeq")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "vehicleGen")
    private Long id;

    @Basic
    @Column(name = "COLOR", columnDefinition = "VARCHAR2(10)")
    private String color;

    @Basic
    @Column(name = "LICENCE_PLATE", columnDefinition = "NVARCHAR2(8)")
    private String licencePlate;

    @Basic
    @Column(name = "BUILD_DATE", columnDefinition = "NUMBER(4)")
    private Integer buildDate;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = false)
    @JoinColumn(name = "FK_CAR", referencedColumnName = "CAR_ID")
    private Car car;


    public Vehicle() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLicencePlate() {
        return licencePlate;
    }

    public void setLicencePlate(String licencePlate) {
        this.licencePlate = licencePlate;
    }

    public Integer getBuildDate() {
        return buildDate;
    }

    public void setBuildDate(Integer buildDate) {
        this.buildDate = buildDate;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }
}
