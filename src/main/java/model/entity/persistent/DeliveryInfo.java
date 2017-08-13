package model.entity.persistent;

import javax.persistence.*;

/**
 * Created by kasra on 6/25/2017.
 */

@Entity(name = "deliveryInfo")
@Table(name = "DELIVERY_INFO")
public class DeliveryInfo {

    @Id
    @Column(name = "DELIVERY_ID", columnDefinition = "NUMBER")
    @SequenceGenerator(name = "deliveryGen", sequenceName = "deliverySeq")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "deliveryGen")
    private Long id;

    @Basic
    @Column(name = "RECEIVER_FIRSTNAME", columnDefinition = "NVARCHAR2(25)")
    private String receiverFirstName;

    @Basic
    @Column(name = "RECEIVER_LASTNAME", columnDefinition = "NVARCHAR2(25)")
    private String receiverLastName;

    @Basic
    @Column(name = "DEST_INFO", columnDefinition = "NVARCHAR2(100)")
    private String destinationInfo;

    @Basic
    @Column(name = "DESCRIPTION", columnDefinition = "NVARCHAR2(100)")
    private String desc;

    @Basic
    @Column(name = "RECIVER_PHONE_NUMBER", columnDefinition = "VARCHAR2(20)")
    private String phoneNumber;

    public DeliveryInfo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReceiverFirstName() {
        return receiverFirstName;
    }

    public void setReceiverFirstName(String receiverFirstName) {
        this.receiverFirstName = receiverFirstName;
    }

    public String getReceiverLastName() {
        return receiverLastName;
    }

    public void setReceiverLastName(String receiverLastName) {
        this.receiverLastName = receiverLastName;
    }

    public String getDestinationInfo() {
        return destinationInfo;
    }

    public void setDestinationInfo(String destinationInfo) {
        this.destinationInfo = destinationInfo;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
