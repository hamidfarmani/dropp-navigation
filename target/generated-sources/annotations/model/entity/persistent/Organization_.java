package model.entity.persistent;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.enums.AccountState;
import model.enums.EmployeeCount;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Organization.class)
public abstract class Organization_ {

	public static volatile SingularAttribute<Organization, AccountState> accountState;
	public static volatile SingularAttribute<Organization, String> orgName;
	public static volatile SingularAttribute<Organization, Address> address;
	public static volatile SingularAttribute<Organization, String> registrarLastName;
	public static volatile SingularAttribute<Organization, EmployeeCount> empCount;
	public static volatile SingularAttribute<Organization, String> description;
	public static volatile SingularAttribute<Organization, String> registrarFirstName;
	public static volatile SingularAttribute<Organization, String> registrarRole;
	public static volatile SingularAttribute<Organization, String> password;
	public static volatile SingularAttribute<Organization, Date> registrationTimestamp;
	public static volatile SingularAttribute<Organization, String> phoneNumber;
	public static volatile SingularAttribute<Organization, String> workNumber;
	public static volatile SingularAttribute<Organization, Long> id;
	public static volatile SingularAttribute<Organization, Long> credit;
	public static volatile SingularAttribute<Organization, String> email;
	public static volatile SingularAttribute<Organization, String> username;

}

