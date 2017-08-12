package model.entity.persistent;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.enums.AccountState;
import model.enums.Gender;
import model.enums.ServiceType;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Driver.class)
public abstract class Driver_ {

	public static volatile SingularAttribute<Driver, ServiceType> serviceType;
	public static volatile SingularAttribute<Driver, AccountState> accountState;
	public static volatile SingularAttribute<Driver, String> lastName;
	public static volatile SingularAttribute<Driver, Gender> gender;
	public static volatile SingularAttribute<Driver, String> bankCardNumber;
	public static volatile SingularAttribute<Driver, Operator> operator;
	public static volatile SingularAttribute<Driver, String> setting;
	public static volatile SingularAttribute<Driver, Vehicle> vehicle;
	public static volatile SingularAttribute<Driver, String> password;
	public static volatile SingularAttribute<Driver, String> bankAccountNumber;
	public static volatile SingularAttribute<Driver, Long> credit;
	public static volatile SingularAttribute<Driver, Long> dId;
	public static volatile SingularAttribute<Driver, String> email;
	public static volatile SingularAttribute<Driver, Address> address;
	public static volatile SingularAttribute<Driver, Date> birthDate;
	public static volatile SingularAttribute<Driver, String> firstName;
	public static volatile SingularAttribute<Driver, Date> registrationTimestamp;
	public static volatile SingularAttribute<Driver, String> phoneNumber;
	public static volatile SingularAttribute<Driver, String> nationalNumber;
	public static volatile ListAttribute<Driver, Trip> trips;
	public static volatile SingularAttribute<Driver, ServiceProvider> serviceProvider;
	public static volatile SingularAttribute<Driver, Boolean> isLoggedIn;
	public static volatile ListAttribute<Driver, Message> messages;
	public static volatile SingularAttribute<Driver, Device> device;
	public static volatile SingularAttribute<Driver, String> username;

}

