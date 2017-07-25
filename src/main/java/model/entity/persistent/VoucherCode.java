package model.entity.persistent;

import model.enums.VoucherCodeGenerationType;
import model.enums.VoucherCodeType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by kasra on 5/28/2017.
 */

@NamedQueries({
        @NamedQuery(name = "voucherCode.all", query = "select v from voucherCode v"),
        @NamedQuery(name = "voucherCode.findBy.code", query = "select v from voucherCode v where v.code=:code"),
})

@Entity(name = "voucherCode")
@Table(name = "VOUCHER_CODE")
public class VoucherCode implements Serializable {

    @Id
    @Column(name = "VOUCHER_ID", columnDefinition = "NUMBER")
    @SequenceGenerator(name = "voucherCodeGen", sequenceName = "voucherCodeSeq")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "voucherCodeGen")
    private Long id;

    @Basic
    @Column(name = "TYPE", columnDefinition = "CHAR")
    private VoucherCodeType type;

    @Basic
    @Column(name = "DISCOUNT_VALUE", columnDefinition = "VARCHAR2(10)")
    private String discountValue;

    @Basic
    @Column(name = "CODE", columnDefinition = "NVARCHAR2(20)")
    private String code;

    @Basic
    @Column(name = "START_DATE", columnDefinition = "TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Basic
    @Column(name = "EXPIRE_DATE", columnDefinition = "TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expireDate;

    @Basic
    @Column(name = "CREATION_DATE", columnDefinition = "TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @Basic
    @Column(name = "USES", columnDefinition = "NUMBER(9)")
    private Integer uses;

    @Basic
    @Column(name = "MAX_USE", columnDefinition = "NUMBER(9)")
    private Integer maxUses;

    @Basic
    @Column(name = "DESCRIPTION", columnDefinition = "NVARCHAR2(250)")
    private String description;

    @Basic
    @Column(name = "GENERATION_TYPE", columnDefinition = "CHAR")
    private VoucherCodeGenerationType generationType;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = false)
    @JoinColumn(name = "FK_OPERATOR", referencedColumnName = "OPERATOR_ID")
    private Operator operator;


    public VoucherCode() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VoucherCodeType getType() {
        return type;
    }

    public void setType(VoucherCodeType voucherCodeType) {
        this.type = voucherCodeType;
    }

    public String getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(String discountValue) {
        this.discountValue = discountValue;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date endDate) {
        this.expireDate = endDate;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public VoucherCodeGenerationType getGenerationType() {
        return generationType;
    }

    public void setGenerationType(VoucherCodeGenerationType generationType) {
        this.generationType = generationType;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public Integer getUses() {
        return uses;
    }

    public void setUses(Integer uses) {
        this.uses = uses;
    }

    public Integer getMaxUses() {
        return maxUses;
    }

    public void setMaxUses(Integer maxUses) {
        this.maxUses = maxUses;
    }
}
