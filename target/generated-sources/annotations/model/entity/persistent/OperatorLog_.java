package model.entity.persistent;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.enums.LogType;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(OperatorLog.class)
public abstract class OperatorLog_ {

	public static volatile SingularAttribute<OperatorLog, LogType> logType;
	public static volatile SingularAttribute<OperatorLog, Date> logTimestamp;
	public static volatile SingularAttribute<OperatorLog, String> ip;
	public static volatile SingularAttribute<OperatorLog, Long> id;
	public static volatile SingularAttribute<OperatorLog, Operator> operator;

}

