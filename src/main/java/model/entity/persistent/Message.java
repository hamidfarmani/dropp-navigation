package model.entity.persistent;

import model.enums.UserRole;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by kasra on 12/29/2016.
 */
@Entity(name = "message")
@Table(name = "MESSAGE")
public class Message implements Serializable {

    @Id
    @Column(name = "ID", columnDefinition = "NUMBER")
    @SequenceGenerator(name = "MessageGen", sequenceName = "MessageSeq")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "MessageGen")
    private Long id;

    @Basic
    @Column(name = "MESSAGE_TEXT", columnDefinition = "NVARCHAR2(2000)")
    private String messageText;

    @Basic
    @Column(name = "TIMESTAMP", columnDefinition = "TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "DRIVER_ID", referencedColumnName = "DRIVER_ID")
    private Driver driver;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "OPERATOR_ID", referencedColumnName = "OPERATOR_ID")
    private Operator operator;

    @Basic
    @Column(name = "SENDER", columnDefinition = "CHAR")
    private UserRole userRole;

    public Message() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }
}
