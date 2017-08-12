package model.entity.persistent;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.enums.PassResetCodeState;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(PasswordResetCode.class)
public abstract class PasswordResetCode_ {

	public static volatile SingularAttribute<PasswordResetCode, Driver> driver;
	public static volatile SingularAttribute<PasswordResetCode, String> resetCode;
	public static volatile SingularAttribute<PasswordResetCode, Passenger> passenger;
	public static volatile SingularAttribute<PasswordResetCode, Long> id;
	public static volatile SingularAttribute<PasswordResetCode, PassResetCodeState> state;
	public static volatile SingularAttribute<PasswordResetCode, Date> startDate;

}

