package model.entity.persistent;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.enums.TicketState;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Bug.class)
public abstract class Bug_ {

	public static volatile SingularAttribute<Bug, Driver> driver;
	public static volatile SingularAttribute<Bug, Passenger> passenger;
	public static volatile SingularAttribute<Bug, Long> id;
	public static volatile SingularAttribute<Bug, TicketState> state;
	public static volatile SingularAttribute<Bug, String> desc;

}

