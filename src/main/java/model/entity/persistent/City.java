package model.entity.persistent;

import javax.persistence.*;

/**
 * Created by kasra on 7/13/2017.
 */

@NamedQueries({
        @NamedQuery(name = "city.by.nameAndStateID", query = "select c from city c where c.name=:name and c.state.id=:stateID"),
        @NamedQuery(name = "city.by.stateID", query = "select c from city c where c.state.id=:stateID"),
        @NamedQuery(name = "city.by.id", query = "select c from city c where c.id=:id")
})

@Entity(name = "city")
@Table(name = "CITY")
public class City {
    @Id
    @Column(name = "CITY_ID", columnDefinition = "NUMBER")
    @SequenceGenerator(name = "cityGen", sequenceName = "citySeq")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "cityGen")
    private long id;

    @Basic
    @Column(name = "NAME", columnDefinition = "NVARCHAR2(30)")
    private String name;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = false)
    @JoinColumn(name = "FK_STATE", referencedColumnName = "STATE_ID")
    private State state;

    public City() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}

