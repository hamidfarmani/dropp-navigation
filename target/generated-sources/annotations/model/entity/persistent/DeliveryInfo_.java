package model.entity.persistent;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(DeliveryInfo.class)
public abstract class DeliveryInfo_ {

	public static volatile SingularAttribute<DeliveryInfo, String> destinationInfo;
	public static volatile SingularAttribute<DeliveryInfo, String> receiverLastName;
	public static volatile SingularAttribute<DeliveryInfo, String> phoneNumber;
	public static volatile SingularAttribute<DeliveryInfo, Long> id;
	public static volatile SingularAttribute<DeliveryInfo, String> receiverFirstName;
	public static volatile SingularAttribute<DeliveryInfo, String> desc;

}

