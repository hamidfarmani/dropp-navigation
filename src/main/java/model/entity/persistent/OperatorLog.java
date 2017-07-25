package model.entity.persistent;

import model.enums.LogType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by kasra on 4/30/2017.
 */
@Entity(name = "operatorLog")
@Table(name = "OPERATOR_LOG")
public class OperatorLog implements Serializable {

    @Id
    @Column(name = "O_LOG_ID", columnDefinition = "NUMBER")
    @SequenceGenerator(name = "oLogGen", sequenceName = "oLogSeq")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = false)
    @JoinColumn(name = "FK_OPERATOR", referencedColumnName = "OPERATOR_ID")
    private Operator operator;

    @Basic
    @Column(name = "LOG_TIMESTAMP", columnDefinition = "TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date logTimestamp;


    @Basic
    @Column(name = "IP",columnDefinition = "VARCHAR2(15)")
    private String ip;

    @Basic
    @Column(name = "LOG_TYPE",columnDefinition = "CHAR")
    private LogType logType;

    public OperatorLog() {
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public Date getLogTimestamp() {
        return logTimestamp;
    }

    public void setLogTimestamp(Date logTimestamp) {
        this.logTimestamp = logTimestamp;
    }

    public LogType getLogType() {
        return logType;
    }

    public void setLogType(LogType logType) {
        this.logType = logType;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
