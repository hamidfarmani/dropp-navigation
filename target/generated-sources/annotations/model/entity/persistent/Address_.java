package model.entity.persistent;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Address.class)
public abstract class Address_ {

	public static volatile SingularAttribute<Address, City> city;
	public static volatile SingularAttribute<Address, String> postalCode;
	public static volatile SingularAttribute<Address, Long> id;
	public static volatile SingularAttribute<Address, String> line2;
	public static volatile SingularAttribute<Address, String> line1;

}

