package model.entity.persistent;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by kasra on 12/29/2016.
 */
@Entity(name = "payment")
@Table(name = "PAYMENT")
public class Payment implements Serializable {

    @Id
    @Column(name = "ID",columnDefinition = "NUMBER")
    @SequenceGenerator(name = "PaymentGen",sequenceName = "PaymentSeq")
    @GeneratedValue(strategy = GenerationType.AUTO,generator = "PaymentGen")
    private Long id;

    @Basic
    @Column(name = "AMOUNT",columnDefinition = "NUMBER(9)")
    private Long amount;

    @Basic
    @Column(name = "PAYMENT_TIMESTAMP", columnDefinition = "TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date paymentTimestamp;


    public Payment() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Date getPaymentTimestamp() {
        return paymentTimestamp;
    }

    public void setPaymentTimestamp(Date paymentTimestamp) {
        this.paymentTimestamp = paymentTimestamp;
    }
}
