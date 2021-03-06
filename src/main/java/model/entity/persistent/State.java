package model.entity.persistent;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by kasra on 7/13/2017.
 */
@NamedQueries({
        @NamedQuery(name = "state.by.id", query = "select s from state s where s.id=:id"),
        @NamedQuery(name = "state.by.name", query = "select s from state s where s.name=:name"),
        @NamedQuery(name = "state.all", query = "select s from state s"),
})

@Entity(name = "state")
@Table(name = "STATE")
public class State implements Serializable {

    @Id
    @Column(name = "STATE_ID", columnDefinition = "NUMBER")
    @SequenceGenerator(name = "stateGen", sequenceName = "stateSeq")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "stateGen")
    private long id;

    @Basic
    @Column(name = "NAME",columnDefinition = "NVARCHAR2(30)")
    private String name;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "FK_STATE", referencedColumnName = "STATE_ID")
    private List<City> cities;

    public State() {
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

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }
}
