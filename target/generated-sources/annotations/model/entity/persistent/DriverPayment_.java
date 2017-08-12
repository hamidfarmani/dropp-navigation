package model.entity.persistent;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.enums.DriverPaymentState;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(DriverPayment.class)
public abstract class DriverPayment_ {

	public static volatile SingularAttribute<DriverPayment, Integer> amount;
	public static volatile SingularAttribute<DriverPayment, Driver> driver;
	public static volatile SingularAttribute<DriverPayment, DriverPaymentState> driverPaymentState;
	public static volatile SingularAttribute<DriverPayment, Long> dpId;
	public static volatile SingularAttribute<DriverPayment, Date> requestDate;
	public static volatile SingularAttribute<DriverPayment, Operator> operator;
	public static volatile SingularAttribute<DriverPayment, Date> payDate;

}

