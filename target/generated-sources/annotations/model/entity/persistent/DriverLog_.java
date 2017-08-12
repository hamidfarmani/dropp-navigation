package model.entity.persistent;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.enums.LogType;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(DriverLog.class)
public abstract class DriverLog_ {

	public static volatile SingularAttribute<DriverLog, LogType> logType;
	public static volatile SingularAttribute<DriverLog, Date> logTimestamp;
	public static volatile SingularAttribute<DriverLog, Driver> driver;
	public static volatile SingularAttribute<DriverLog, Long> id;

}

