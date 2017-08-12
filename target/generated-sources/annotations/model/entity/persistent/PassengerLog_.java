package model.entity.persistent;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.enums.LogType;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(PassengerLog.class)
public abstract class PassengerLog_ {

	public static volatile SingularAttribute<PassengerLog, LogType> logType;
	public static volatile SingularAttribute<PassengerLog, Date> logTimestamp;
	public static volatile SingularAttribute<PassengerLog, Passenger> passenger;
	public static volatile SingularAttribute<PassengerLog, Long> id;

}

