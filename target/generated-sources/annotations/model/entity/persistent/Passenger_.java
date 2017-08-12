package model.entity.persistent;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.enums.AccountState;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Passenger.class)
public abstract class Passenger_ {

	public static volatile SingularAttribute<Passenger, AccountState> accountState;
	public static volatile SingularAttribute<Passenger, Long> pId;
	public static volatile SingularAttribute<Passenger, PassengerInfo> passengerInfo;
	public static volatile SingularAttribute<Passenger, Integer> point;
	public static volatile SingularAttribute<Passenger, String> setting;
	public static volatile SingularAttribute<Passenger, String> password;
	public static volatile SingularAttribute<Passenger, Date> registrationTimestamp;
	public static volatile SingularAttribute<Passenger, String> phoneNumber;
	public static volatile ListAttribute<Passenger, Trip> trips;
	public static volatile SingularAttribute<Passenger, Boolean> isLoggedIn;
	public static volatile SingularAttribute<Passenger, Long> credit;
	public static volatile SingularAttribute<Passenger, Passenger> recommender;
	public static volatile SingularAttribute<Passenger, Device> device;
	public static volatile SingularAttribute<Passenger, String> username;
	public static volatile ListAttribute<Passenger, FavPlace> favPlaces;

}

