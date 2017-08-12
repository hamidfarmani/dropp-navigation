package model.entity.persistent;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.enums.AccountState;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(OperatorStateHistory.class)
public abstract class OperatorStateHistory_ {

	public static volatile SingularAttribute<OperatorStateHistory, Date> changeDate;
	public static volatile SingularAttribute<OperatorStateHistory, Long> id;
	public static volatile SingularAttribute<OperatorStateHistory, AccountState> currentState;
	public static volatile SingularAttribute<OperatorStateHistory, Operator> operator;
	public static volatile SingularAttribute<OperatorStateHistory, AccountState> newState;
	public static volatile SingularAttribute<OperatorStateHistory, String> desc;

}

