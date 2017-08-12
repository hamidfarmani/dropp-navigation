package model.entity.persistent;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.enums.AccountState;
import model.enums.City;
import model.enums.Gender;
import model.enums.UserRole;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Operator.class)
public abstract class Operator_ {

	public static volatile SingularAttribute<Operator, AccountState> accountState;
	public static volatile SingularAttribute<Operator, String> lastName;
	public static volatile SingularAttribute<Operator, Operator> creator;
	public static volatile SingularAttribute<Operator, UserRole> role;
	public static volatile SingularAttribute<Operator, Gender> gender;
	public static volatile SingularAttribute<Operator, City> city;
	public static volatile SingularAttribute<Operator, Long> oId;
	public static volatile SingularAttribute<Operator, Date> birthDate;
	public static volatile SingularAttribute<Operator, String> firstName;
	public static volatile SingularAttribute<Operator, String> password;
	public static volatile SingularAttribute<Operator, Date> registrationTimestamp;
	public static volatile SingularAttribute<Operator, String> phoneNumber;
	public static volatile SingularAttribute<Operator, String> workNumber;
	public static volatile SingularAttribute<Operator, ServiceProvider> serviceProvider;
	public static volatile ListAttribute<Operator, Message> messages;
	public static volatile SingularAttribute<Operator, Boolean> isLoggedIn;
	public static volatile SingularAttribute<Operator, String> email;
	public static volatile SingularAttribute<Operator, String> username;

}

