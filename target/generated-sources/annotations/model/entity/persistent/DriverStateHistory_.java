package model.entity.persistent;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.enums.AccountState;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(DriverStateHistory.class)
public abstract class DriverStateHistory_ {

	public static volatile SingularAttribute<DriverStateHistory, Driver> driver;
	public static volatile SingularAttribute<DriverStateHistory, Date> changeDate;
	public static volatile SingularAttribute<DriverStateHistory, Long> id;
	public static volatile SingularAttribute<DriverStateHistory, AccountState> currentState;
	public static volatile SingularAttribute<DriverStateHistory, AccountState> newState;
	public static volatile SingularAttribute<DriverStateHistory, String> desc;

}

