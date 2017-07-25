package model.entity.persistent;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by kasra on 4/30/2017.
 */
@Entity(name = "smsHistory")
@Table(name = "SMS_HISTORY")
public class SMS implements Serializable {

    @Id
    @Column(name = "SMS_ID", columnDefinition = "NUMBER")
    @SequenceGenerator(name = "SMSGen", sequenceName = "SMSIdSeq")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SMSGen")
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = false)
    @JoinColumn(name = "OPERATOR_ID", referencedColumnName = "OPERATOR_ID")
    private Operator createdBy;

    @Basic
    @Column(name = "DESCRIPTION", columnDefinition = "NVARCHAR2(250)")
    private String description;

    @Basic
    @Column(name = "BODY", columnDefinition = "NVARCHAR2(250)")
    private String SMSBody;

    @Basic
    @Column(name = "PHONENUMBER",columnDefinition = "VARCHAR2(11)")
    private String phoneNumber;

    public SMS() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Operator getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Operator createdBy) {
        this.createdBy = createdBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSMSBody() {
        return SMSBody;
    }

    public void setSMSBody(String SMSBody) {
        this.SMSBody = SMSBody;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
