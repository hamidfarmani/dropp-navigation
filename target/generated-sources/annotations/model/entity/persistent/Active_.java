package model.entity.persistent;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.enums.City;
import model.enums.ServiceType;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Active.class)
public abstract class Active_ {

	public static volatile SingularAttribute<Active, ServiceType> serviceType;
	public static volatile SingularAttribute<Active, Boolean> serviceState;
	public static volatile SingularAttribute<Active, City> city;
	public static volatile SingularAttribute<Active, Long> id;

}

