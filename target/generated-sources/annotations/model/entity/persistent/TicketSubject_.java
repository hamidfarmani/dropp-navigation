package model.entity.persistent;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.enums.UserRole;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(TicketSubject.class)
public abstract class TicketSubject_ {

	public static volatile SingularAttribute<TicketSubject, UserRole> role;
	public static volatile SingularAttribute<TicketSubject, String> subject;
	public static volatile SingularAttribute<TicketSubject, Long> id;
	public static volatile ListAttribute<TicketSubject, TicketSubject> ticketSubjects;

}

