package model.entity.persistent;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Vehicle.class)
public abstract class Vehicle_ {

	public static volatile SingularAttribute<Vehicle, String> color;
	public static volatile SingularAttribute<Vehicle, Car> car;
	public static volatile SingularAttribute<Vehicle, String> licencePlate;
	public static volatile SingularAttribute<Vehicle, Long> id;
	public static volatile SingularAttribute<Vehicle, Integer> buildDate;

}

