package model.entity.persistent;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by kasra on 7/2/2017.
 */

@Entity(name = "tutorialInfo")
@Table(name = "TUTORIAL_INFO")
public class TutorialInfo implements Serializable {

    @Id
    @Column(name = "TUT_ID", columnDefinition = "NUMBER")
    @SequenceGenerator(name = "tutGen", sequenceName = "tutIdSeq")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "tutGen")
    private Long id;

    @Basic
    @Column(name = "REQUEST_DATE", columnDefinition = "TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date requestDate;

    @Basic
    @Column(name = "TUT_DATE", columnDefinition = "DATE")
    @Temporal(TemporalType.DATE)
    private Date tutDate;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "FK_DRIVER", referencedColumnName = "DRIVER_ID")
    private Driver driver;

    public TutorialInfo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public Date getTutDate() {
        return tutDate;
    }

    public void setTutDate(Date tutDate) {
        this.tutDate = tutDate;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }
}
