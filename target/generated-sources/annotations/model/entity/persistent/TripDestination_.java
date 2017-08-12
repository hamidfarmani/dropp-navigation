package model.entity.persistent;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(TripDestination.class)
public abstract class TripDestination_ {

	public static volatile SingularAttribute<TripDestination, String> address;
	public static volatile SingularAttribute<TripDestination, Short> seqNumber;
	public static volatile SingularAttribute<TripDestination, Location> location;
	public static volatile SingularAttribute<TripDestination, Long> id;

}

