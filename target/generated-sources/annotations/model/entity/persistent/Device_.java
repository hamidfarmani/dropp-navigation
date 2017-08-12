package model.entity.persistent;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Device.class)
public abstract class Device_ {

	public static volatile SingularAttribute<Device, String> OS;
	public static volatile SingularAttribute<Device, String> FCMToken;
	public static volatile SingularAttribute<Device, String> Model;
	public static volatile SingularAttribute<Device, String> OSVersion;
	public static volatile SingularAttribute<Device, Long> id;
	public static volatile SingularAttribute<Device, String> uniqueID;
	public static volatile SingularAttribute<Device, String> manufacturer;

}

