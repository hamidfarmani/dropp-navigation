package model.entity.persistent;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by kasra on 7/27/2017.
 */
@NamedQueries({
        @NamedQuery(name = "serviceProvider.get.all", query = "select s from serviceProvider s"),
        @NamedQuery(name = "serviceProvider.findby.id", query = "select s from serviceProvider s where s.id=:id")

})

@Table(name = "SERVICE_PROVIDER")
@Entity(name = "serviceProvider")
public class ServiceProvider implements Serializable {

    @Id
    @Column(name = "ID", columnDefinition = "NUMBER")
    @SequenceGenerator(name = "serviceProviderGen", sequenceName = "serviceProviderSeq")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "serviceProviderGen")
    private Long id;

    @Basic
    @Column(name = "NAME", columnDefinition = "NVARCHAR2(20)")
    private String name;

    @Basic
    @Column(name = "DRIVERS_CLAIM", columnDefinition = "NUMBER(10)")
    private Long driversClaim;

    @Basic
    @Column(name = "TOTAL_CLAIM", columnDefinition = "NUMBER(10)")
    private Long totalClaim;

    public ServiceProvider() {
    }

    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getDriversClaim() {
        return driversClaim;
    }

    public void setDriversClaim(Long driversClaim) {
        this.driversClaim = driversClaim;
    }

    public Long getTotalClaim() {
        return totalClaim;
    }

    public void setTotalClaim(Long totalClaim) {
        this.totalClaim = totalClaim;
    }
}
