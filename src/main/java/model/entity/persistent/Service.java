package model.entity.persistent;

import model.enums.City;
import model.enums.ServiceType;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by kasra on 4/30/2017.
 */
@Entity(name = "service")
@Table(name = "SERVICE")
@NamedQueries({
        @NamedQuery(name = "activeServices.find.byCity", query = "select a from service a where a.city=:city"),
        @NamedQuery(name = "activeServices.find.byCityAndService", query = "select a from service a where a.serviceType=:serviceType and a.city=:city")
})
public class Service implements Serializable {

    @Id
    @Column(name = "ID", columnDefinition = "NUMBER")
    @SequenceGenerator(name = "activeServiceGen", sequenceName = "activeServiceSeq")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "activeServiceGen")
    private Long id;

    @Basic
    @Column(name = "CITY", columnDefinition = "VARCHAR2(2)")
    private City city;

    @Basic
    @Column(name = "SERVICE_TYPE", columnDefinition = "CHAR")
    private ServiceType serviceType;

    @Basic
    @Column(name = "STATE", columnDefinition = "CHAR")
    private Boolean serviceState;

    public Service() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public Boolean getServiceState() {
        return serviceState;
    }

    public void setServiceState(Boolean serviceState) {
        this.serviceState = serviceState;
    }
}