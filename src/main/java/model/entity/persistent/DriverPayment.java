package model.entity.persistent;

import model.enums.DriverPaymentState;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by kasra on 7/6/2017.
 */
@Entity(name = "driverPayment")
@Table(name = "Driver_Payment")
public class DriverPayment {

    @Id
    @Column(name = "DP_ID", columnDefinition = "NUMBER")
    @SequenceGenerator(name = "dpGen", sequenceName = "dpSeq")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "dpGen")
    private long dpId;

    @Basic
    @Column(name = "AMOUNT",columnDefinition = "NUMBER(9)")
    private Integer amount;

    @Basic
    @Column(name = "REQUEST_DATE", columnDefinition = "TIMESTAMP")
    @Temporal(TemporalType.DATE)
    private Date requestDate;

    @Basic
    @Column(name = "PAY_DATE", columnDefinition = "TIMESTAMP")
    @Temporal(TemporalType.DATE)
    private Date payDate;

    @Basic
    @Column(name = "STATE",columnDefinition = "CHAR")
    private DriverPaymentState driverPaymentState;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = false)
    @JoinColumn(name = "FK_PAYER", referencedColumnName = "OPERATOR_ID")
    private Operator operator;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = false)
    @JoinColumn(name = "FK_DRIVER", referencedColumnName = "DRIVER_ID")
    private Driver driver;

    public DriverPayment() {
    }

    public long getDpId() {
        return dpId;
    }

    public void setDpId(long dpId) {
        this.dpId = dpId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public DriverPaymentState getDriverPaymentState() {
        return driverPaymentState;
    }

    public void setDriverPaymentState(DriverPaymentState driverPaymentState) {
        this.driverPaymentState = driverPaymentState;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }
}
