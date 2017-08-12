package model.entity.persistent;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.enums.City;
import model.enums.ServiceType;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Service.class)
public abstract class ActiveService_ {

	public static volatile SingularAttribute<Service, ServiceType> serviceType;
	public static volatile SingularAttribute<Service, Boolean> serviceState;
	public static volatile SingularAttribute<Service, City> city;
	public static volatile SingularAttribute<Service, Long> id;

}

