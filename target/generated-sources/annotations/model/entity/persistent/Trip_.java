package model.entity.persistent;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.enums.City;
import model.enums.PaymentMethod;
import model.enums.ServiceType;
import model.enums.TripState;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Trip.class)
public abstract class Trip_ {

	public static volatile SingularAttribute<Trip, ServiceType> serviceType;
	public static volatile SingularAttribute<Trip, SubscribeUser> subscribeUser;
	public static volatile SingularAttribute<Trip, Integer> cashPayment;
	public static volatile SingularAttribute<Trip, Long> distance;
	public static volatile SingularAttribute<Trip, Date> endDate;
	public static volatile SingularAttribute<Trip, City> city;
	public static volatile SingularAttribute<Trip, String> originAddress;
	public static volatile SingularAttribute<Trip, Location> origin;
	public static volatile SingularAttribute<Trip, Integer> waitingTime;
	public static volatile SingularAttribute<Trip, Operator> operator;
	public static volatile SingularAttribute<Trip, Vehicle> vehicle;
	public static volatile SingularAttribute<Trip, Integer> rate;
	public static volatile SingularAttribute<Trip, Long> id;
	public static volatile SingularAttribute<Trip, TripState> state;
	public static volatile SingularAttribute<Trip, String> UUID;
	public static volatile SingularAttribute<Trip, VoucherCode> voucherCode;
	public static volatile SingularAttribute<Trip, DeliveryInfo> deliveryInfo;
	public static volatile SingularAttribute<Trip, Integer> cost;
	public static volatile SingularAttribute<Trip, Boolean> isOneWay;
	public static volatile ListAttribute<Trip, TripDestination> destinations;
	public static volatile SingularAttribute<Trip, Integer> creditPayment;
	public static volatile SingularAttribute<Trip, Long> ETA;
	public static volatile SingularAttribute<Trip, Driver> driver;
	public static volatile SingularAttribute<Trip, Passenger> passenger;
	public static volatile SingularAttribute<Trip, Integer> providerShare;
	public static volatile SingularAttribute<Trip, PaymentMethod> paymentMethod;
	public static volatile SingularAttribute<Trip, Integer> genoShare;
	public static volatile SingularAttribute<Trip, Date> startDate;

}

