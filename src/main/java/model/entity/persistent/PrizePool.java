package model.entity.persistent;

import javax.persistence.*;

/**
 * Created by kasra on 7/13/2017.
 */
@Entity(name = "prizePool")
@Table(name = "PRIZE_POOL")
public class PrizePool {

    @Id
    @Column(name = "ID", columnDefinition = "NUMBER")
    @SequenceGenerator(name = "prizePoolGen", sequenceName = "prizePoolSeq")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "prizePoolGen")
    private long id;

    @Basic
    @Column(name = "PRIZE", columnDefinition = "VARCHAR2(4000)")
    private String prize;

    @Basic
    @Column(name = "STAR", columnDefinition = "NUMBER(1)")
    private String star;

    public PrizePool() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPrize() {
        return prize;
    }

    public void setPrize(String prize) {
        this.prize = prize;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }
}
