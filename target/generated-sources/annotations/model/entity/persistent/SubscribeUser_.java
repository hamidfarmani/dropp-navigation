package model.entity.persistent;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(SubscribeUser.class)
public abstract class SubscribeUser_ {

	public static volatile SingularAttribute<SubscribeUser, String> subscriptionCode;
	public static volatile SingularAttribute<SubscribeUser, String> firstName;
	public static volatile SingularAttribute<SubscribeUser, String> lastName;
	public static volatile SingularAttribute<SubscribeUser, String> phoneNumber;
	public static volatile SingularAttribute<SubscribeUser, Address> address;
	public static volatile ListAttribute<SubscribeUser, Trip> trips;
	public static volatile SingularAttribute<SubscribeUser, Long> Id;

}

