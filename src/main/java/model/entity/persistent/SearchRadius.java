package model.entity.persistent;

import model.enums.ServiceType;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by kasra on 4/30/2017.
 */
@NamedQueries({
        @NamedQuery(name = "searchRadius.all", query = "select s from searchRadius s"),
        @NamedQuery(name = "searchRadius.findBy.serviceType", query = "select s from searchRadius s where s.serviceType=:serviceType")
})
@Entity(name = "searchRadius")
@Table(name = "SEARCH_RADIUS")
public class SearchRadius implements Serializable {

    @Id
    @Column(name = "ID", columnDefinition = "NUMBER")
    @SequenceGenerator(name = "radiusGen", sequenceName = "radiusSeq")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "radiusGen")
    private Long id;

    @Basic
    @Column(name = "SERVICE_TYPE", columnDefinition = "CHAR")
    private ServiceType serviceType;

    @Basic
    @Column(name = "RADIUS", columnDefinition = "NUMBER(6,1)")
    private Double radius;


    public SearchRadius() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public Double getRadius() {
        return radius;
    }

    public void setRadius(Double radius) {
        this.radius = radius;
    }

}
