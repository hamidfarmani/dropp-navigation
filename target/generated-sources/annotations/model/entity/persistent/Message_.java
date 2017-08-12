package model.entity.persistent;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.enums.UserRole;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Message.class)
public abstract class Message_ {

	public static volatile SingularAttribute<Message, String> messageText;
	public static volatile SingularAttribute<Message, Driver> driver;
	public static volatile SingularAttribute<Message, Long> id;
	public static volatile SingularAttribute<Message, UserRole> userRole;
	public static volatile SingularAttribute<Message, Operator> operator;
	public static volatile SingularAttribute<Message, Date> timestamp;

}

