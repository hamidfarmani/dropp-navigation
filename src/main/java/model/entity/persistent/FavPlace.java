package model.entity.persistent;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by kasra on 4/20/2017.
 */
@NamedQueries({
        @NamedQuery(name = "favPlace.findBy.id^token",query = "delete from favPlace f where f.id=:id and f.token=:token")
})
@Entity(name = "favPlace")
@Table(name = "FAV_PLACE")
public class FavPlace implements Serializable {

    @Id
    @Column(name = "PLACE_ID", columnDefinition = "NUMBER")
    @SequenceGenerator(name = "placeGen", sequenceName = "placeSeq")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "placeGen")
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "FK_LOCATION", referencedColumnName = "LOC_ID")
    private Location location;

    @Basic
    @Column(name = "PLACE_NAME", columnDefinition = "NVARCHAR2(30)")
    private String placeName;

    @Basic
    @Column(name = "ADDRESS", columnDefinition = "NVARCHAR2(150)")
    private String address;

    @Basic
    @Column(name = "DESCRIPTION", columnDefinition = "NVARCHAR2(200)")
    private String description;

    @Basic
    @Column(name = "TOKEN", columnDefinition = "VARCHAR2(6)")
    private String token;


    public FavPlace() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
