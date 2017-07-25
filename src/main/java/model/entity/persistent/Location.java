package model.entity.persistent;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by kasra on 12/29/2016.
 */
@Entity(name = "location")
@Table(name = "LOCATION")
public class Location implements Serializable {

    @Id
    @Column(name = "LOC_ID", columnDefinition = "NUMBER")
    @SequenceGenerator(name = "locationGen", sequenceName = "locationseq")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "locationGen")
    private Long id;

    @Basic
    @Column(name = "LATITUDE", columnDefinition = "NUMBER(9,6)")
    private Double latitude;

    @Basic
    @Column(name = "LONGITUDE", columnDefinition = "NUMBER(9,6)")
    private Double longitude;

    public Location() {
    }

    public Location(Double longitude, Double latitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Location{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
