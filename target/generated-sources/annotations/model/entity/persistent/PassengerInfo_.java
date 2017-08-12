package model.entity.persistent;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.enums.Gender;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(PassengerInfo.class)
public abstract class PassengerInfo_ {

	public static volatile SingularAttribute<PassengerInfo, String> firstName;
	public static volatile SingularAttribute<PassengerInfo, String> lastName;
	public static volatile SingularAttribute<PassengerInfo, Gender> gender;
	public static volatile SingularAttribute<PassengerInfo, Long> pId;
	public static volatile SingularAttribute<PassengerInfo, Date> birthDate;
	public static volatile SingularAttribute<PassengerInfo, String> email;

}

