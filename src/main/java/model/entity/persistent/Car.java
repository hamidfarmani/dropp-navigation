package model.entity.persistent;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by kasra on 4/29/2017.
 */
@NamedQueries(
        {
                @NamedQuery(name = "get.cars.by.manufacturerID",query = "select c from car c where c.carManufacture.id =:manufactureID"),
                @NamedQuery(name = "cars.findBy.name",query = "select c from car c where c.name =:name"),
        }
)


@Entity(name = "car")
@Table(name = "CAR")
public class Car implements Serializable {
    @Id
    @Column(name = "CAR_ID", columnDefinition = "NUMBER")
    @SequenceGenerator(name = "carGen", sequenceName = "carIdSeq")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "carGen")
    private Long id;

    @Basic
    @Column(name = "NAME",columnDefinition = "NVARCHAR2(20)")
    private String name;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "FK_MANUFACTURE", referencedColumnName = "MANUFACTURE_ID")
    private CarManufacture carManufacture;

    public Car() {
    }

    public Long getId() {
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

    public CarManufacture getCarManufacture() {
        return carManufacture;
    }

    public void setCarManufacture(CarManufacture carManufacture) {
        this.carManufacture = carManufacture;
    }
}
