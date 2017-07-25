package model.entity.persistent;

import model.enums.City;
import model.enums.ServiceType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by kasra on 4/30/2017.
 */
@Entity(name = "tariffHistory")
@Table(name = "TARIFF_HISTORY")
@NamedQueries({
        @NamedQuery(name = "ticket.subject.getAll", query = "select t from ticketSubject t"),
})
public class TariffHistory implements Serializable {
    @Id
    @Column(name = "ID", columnDefinition = "NUMBER")
    @SequenceGenerator(name = "tariffGen", sequenceName = "tariffSeq")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "tariffGen")
    private Long id;

    @Basic
    @Column(name = "SERVICE_TYPE", columnDefinition = "CHAR")
    private ServiceType serviceType;

    @Basic
    @Column(name = "CO_SHARE_PERCENTAGE",columnDefinition = "NUMBER(3)")
    private Integer coSharePercentage;


    @Basic
    @Column(name = "ENTRANCE_COST", columnDefinition = "NUMBER(7,2)")
    private Double entranceCost;

    @Basic
    @Column(name = "COST_PER_METER_BEFORE_2KM", columnDefinition = "NUMBER(7,4)")
    private Double costPerMeterBefore2KM;

    @Basic
    @Column(name = "COST_PER_METER_AFTER_2KM", columnDefinition = "NUMBER(7,4)")
    private Double costPerMeterAfter2KM;

    @Basic
    @Column(name = "COST_PER_MIN", columnDefinition = "NUMBER(7,4)")
    private Double costPerMin;

    @Basic
    @Column(name = "COST_PER_WAITING_MIN", columnDefinition = "NUMBER(7,4)")
    private Double costPerWaitingMin;


    @Basic
    @Column(name = "TWO_WAY_COST_PERCENTAGE",columnDefinition = "NUMBER(3)")
    private Integer twoWayCostPercentage;

    @Basic
    @Column(name = "CITY", columnDefinition = "VARCHAR2(2)")
    private City city;

    @Basic
    @Column(name = "START_DATE", columnDefinition = "TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Basic
    @Column(name = "END_DATE", columnDefinition = "TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_TARIFF_ID", referencedColumnName = "ID")
    private Tariff tariff;


    public TariffHistory() {
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

    public Double getEntranceCost() {
        return entranceCost;
    }

    public void setEntranceCost(Double entranceCost) {
        this.entranceCost = entranceCost;
    }

    public Double getCostPerMeterBefore2KM() {
        return costPerMeterBefore2KM;
    }

    public void setCostPerMeterBefore2KM(Double costPerMeterBefore2KM) {
        this.costPerMeterBefore2KM = costPerMeterBefore2KM;
    }

    public Double getCostPerMeterAfter2KM() {
        return costPerMeterAfter2KM;
    }

    public void setCostPerMeterAfter2KM(Double costPerMeterAfter2KM) {
        this.costPerMeterAfter2KM = costPerMeterAfter2KM;
    }

    public Double getCostPerMin() {
        return costPerMin;
    }

    public void setCostPerMin(Double costPerMin) {
        this.costPerMin = costPerMin;
    }

    public Double getCostPerWaitingMin() {
        return costPerWaitingMin;
    }

    public void setCostPerWaitingMin(Double costPerWaitingMin) {
        this.costPerWaitingMin = costPerWaitingMin;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Tariff getTariff() {
        return tariff;
    }

    public void setTariff(Tariff tariff) {
        this.tariff = tariff;
    }
}
