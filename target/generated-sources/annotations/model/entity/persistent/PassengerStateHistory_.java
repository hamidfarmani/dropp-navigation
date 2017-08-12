package model.entity.persistent;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.enums.AccountState;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(PassengerStateHistory.class)
public abstract class PassengerStateHistory_ {

	public static volatile SingularAttribute<PassengerStateHistory, Passenger> passenger;
	public static volatile SingularAttribute<PassengerStateHistory, Date> changeDate;
	public static volatile SingularAttribute<PassengerStateHistory, Long> id;
	public static volatile SingularAttribute<PassengerStateHistory, AccountState> currentState;
	public static volatile SingularAttribute<PassengerStateHistory, AccountState> newState;
	public static volatile SingularAttribute<PassengerStateHistory, String> desc;

}

