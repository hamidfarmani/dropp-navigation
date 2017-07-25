package model.entity.persistent;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by kasra on 4/30/2017.
 */

@NamedQueries({
        @NamedQuery(name = "find.all.manufactures",query = "select m from carManufacture m "),
        @NamedQuery(name = "manufacture.findBy.name",query = "select m from carManufacture m where m.name=:name"),
        @NamedQuery(name = "manufacture.findBy.id",query = "select m from carManufacture m where m.id=:id"),
})

@Entity(name = "carManufacture")
@Table(name = "CAR_MANUFACTURE")
public class CarManufacture implements Serializable {

    @Id
    @Column(name = "MANUFACTURE_ID", columnDefinition = "NUMBER")
    @SequenceGenerator(name = "manufactureGen", sequenceName = "manufactureSeq")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "manufactureGen")
    private Long id;

    @Basic
    @Column(name = "NAME",columnDefinition = "NVARCHAR2(20)")
    private String name;

    public CarManufacture() {
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
}
