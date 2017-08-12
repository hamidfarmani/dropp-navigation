package model.entity.persistent;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.enums.ServiceType;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(SearchRadius.class)
public abstract class SearchRadius_ {

	public static volatile SingularAttribute<SearchRadius, ServiceType> serviceType;
	public static volatile SingularAttribute<SearchRadius, Long> id;
	public static volatile SingularAttribute<SearchRadius, Double> radius;

}

