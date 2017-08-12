package model.entity.persistent;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(SMS.class)
public abstract class SMS_ {

	public static volatile SingularAttribute<SMS, String> SMSBody;
	public static volatile SingularAttribute<SMS, String> phoneNumber;
	public static volatile SingularAttribute<SMS, Operator> createdBy;
	public static volatile SingularAttribute<SMS, String> description;
	public static volatile SingularAttribute<SMS, Long> id;

}

