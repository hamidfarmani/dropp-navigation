package model.entity.persistent;

import model.enums.PaymentMethod;
import model.enums.ServiceType;
import model.enums.TripState;
import util.IOCContainer;
import util.converter.PaymentMethodConverter;
import util.converter.ServiceTypeConverter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by kasra on 12/29/2016.
 */
 @NamedQueries({
         @NamedQuery(name = "trip.findBy.UUID", query = "select t from trip t where t.UUID=:UUID"),
         @NamedQuery(name = "trip.findBy.passengerID", query = "select t from trip t where t.passenger.id=:passengerID"),
         @NamedQuery(name = "trip.findBy.driverID", query = "select t from trip t where t.driver.id=:driverID"),
         @NamedQuery(name = "trip.findBy.lowRate", query = "select t.driver.username, avg(t.info.rate) from trip t GROUP by t.driver.username having avg(t.info.rate)<:rate"),
         @NamedQuery(name = "trip.findBy.driverUsername",query = "select t from trip t left join driver d on t.driver.id=d.id where d.username like :username"),
         @NamedQuery(name = "trip.findBy.driverPhoneNumber",query = "select t from trip t left join driver d on t.driver.id=d.id where d.phoneNumber like :phoneNumber"),
         @NamedQuery(name = "trip.findBy.passengerUsername",query = "select t from trip t left join passenger p on t.passenger.id=p.id where p.username like :username"),
         @NamedQuery(name = "trip.findBy.passengerPhoneNumber",query = "select t from trip t left join passenger p on t.passenger.id=p.id where p.phoneNumber like :phoneNumber"),
         @NamedQuery(name = "trip.findBy.uuid",query = "select t from trip t where t.UUID like :uuid"),
         @NamedQuery(name = "trip.between.date",query = "select t from trip t where trunc(t.info.startDate)>=:startDate and trunc(t.info.endDate)<=:endDate"),
         @NamedQuery(name = "trip.today.count",query = "select count(t.id) from trip t where trunc(t.info.startDate) = trunc(current_date)"),
         @NamedQuery(name = "trip.distance.findBy.vehicleID",query = "select SUM(t.info.distance) from trip t where (t.serviceType!='R' or t.serviceType!='D') and t.vehicle.id=:vehicleID"),
         @NamedQuery(name = "trip.searchLike", query = "select t from trip t left join t.driver d left join t.passenger p left join t.subscribeUser s left join t.origin o  where t.UUID like :input or  p.username like :input or d.username like :input or s.subscriptionCode like :input " ),
//         @NamedQuery(name = "trip.groupBy.city", query = "select t.city,sum(t.cost) from trip t group by t.city" ),
         @NamedQuery(name = "trip.groupBy.serviceType", query = "select t.serviceType,sum(t.cost) from trip t group by t.serviceType" )
 })

@Entity(name = "trip")
@Table(name = "TRIP", indexes = {
        @Index(name = "TRIP_UUID_INDEX", columnList = "UUID")
})
public class Trip implements Serializable {

    @Id
    @Column(name = "TRIP_ID", columnDefinition = "NUMBER")
    @SequenceGenerator(name = "TripGen", sequenceName = "TripSeq")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "TripGen")
    private long id;

    @Basic
    @Column(name = "UUID", columnDefinition = "NVARCHAR2(36)")
    private String UUID;

    @Basic
    @Column(name = "STATE", columnDefinition = "NUMBER(2)")
    private TripState state;

    @Basic
    @Column(name = "SERVICE_TYPE", columnDefinition = "CHAR")
    private ServiceType serviceType;


    @Basic
    @Column(name = "COST", columnDefinition = "NUMBER(9)")
    private Integer cost;

    @Basic
    @Column(name = "PAYMENT_METHOD", columnDefinition = "NUMBER(1)")
    private PaymentMethod paymentMethod;

    @Basic
    @Column(name = "CASH_PAYMENT", columnDefinition = "NUMBER(9)")
    private Integer cashPayment;

    @Basic
    @Column(name = "CREDIT_PAYMENT", columnDefinition = "NUMBER(9)")
    private Integer creditPayment;


    @Basic
    @Column(name = "GENO_SHARE", columnDefinition = "NUMBER(9)")
    private Integer genoShare;

    @Basic
    @Column(name = "PROVIDER_SHARE", columnDefinition = "NUMBER(9)")
    private Integer providerShare;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = false)
    @JoinColumn(name = "FK_Vehicle", referencedColumnName = "ID")
    private Vehicle vehicle;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = false)
    @JoinColumn(name = "FK_VOUCHER", referencedColumnName = "VOUCHER_ID")
    private VoucherCode voucherCode;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "FK_TRIP_DELIVERY_INFO", referencedColumnName = "DELIVERY_ID")
    private DeliveryInfo deliveryInfo;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "FK_TRIP_ORIGIN", referencedColumnName = "LOC_ID")
    private Location origin;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "FK_TRIP", referencedColumnName = "TRIP_ID")
    private List<TripDestination> destinations;


    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_PASSENGER", referencedColumnName = "PASSENGER_ID")
    private Passenger passenger;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_DRIVER", referencedColumnName = "DRIVER_ID")
    private Driver driver;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_OPERATOR", referencedColumnName = "OPERATOR_ID")
    private Operator operator;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_SUBUSER", referencedColumnName = "SUBUSER_ID")
    private SubscribeUser subscribeUser;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = false)
    @JoinColumn(name = "FK_INFO", referencedColumnName = "TRIP_INFO_ID")
    private TripInfo info;

    public Trip() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public TripState getState() {
        return state;
    }

    public void setState(TripState state) {
        this.state = state;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public Integer getCashPayment() {
        return cashPayment;
    }

    public void setCashPayment(Integer cashPayment) {
        this.cashPayment = cashPayment;
    }

    public Integer getCreditPayment() {
        return creditPayment;
    }

    public void setCreditPayment(Integer creditPayment) {
        this.creditPayment = creditPayment;
    }

    public Integer getGenoShare() {
        return genoShare;
    }

    public void setGenoShare(Integer genoShare) {
        this.genoShare = genoShare;
    }

    public Integer getProviderShare() {
        return providerShare;
    }

    public void setProviderShare(Integer providerShare) {
        this.providerShare = providerShare;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public VoucherCode getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(VoucherCode voucherCode) {
        this.voucherCode = voucherCode;
    }

    public DeliveryInfo getDeliveryInfo() {
        return deliveryInfo;
    }

    public void setDeliveryInfo(DeliveryInfo deliveryInfo) {
        this.deliveryInfo = deliveryInfo;
    }

    public Location getOrigin() {
        return origin;
    }

    public void setOrigin(Location origin) {
        this.origin = origin;
    }

    public List<TripDestination> getDestinations() {
        return destinations;
    }

    public void setDestinations(List<TripDestination> destinations) {
        this.destinations = destinations;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
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

    public SubscribeUser getSubscribeUser() {
        return subscribeUser;
    }

    public void setSubscribeUser(SubscribeUser subscribeUser) {
        this.subscribeUser = subscribeUser;
    }

    public TripInfo getInfo() {
        return info;
    }

    public void setInfo(TripInfo info) {
        this.info = info;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Integer getPaymentMethodDBValue() {
        return ((PaymentMethodConverter) IOCContainer.getBean("paymentMethodConverter")).convertToDatabaseColumn(paymentMethod);
    }

    public String getServiceTypeDBValue() {
        return ((ServiceTypeConverter) IOCContainer.getBean("serviceTypeConverter")).convertToDatabaseColumn(serviceType);
    }
}
