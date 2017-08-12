package model.entity.persistent;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.enums.TicketState;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Ticket.class)
public abstract class Ticket_ {

	public static volatile SingularAttribute<Ticket, TicketState> ticketState;
	public static volatile SingularAttribute<Ticket, Driver> driver;
	public static volatile SingularAttribute<Ticket, Passenger> passenger;
	public static volatile SingularAttribute<Ticket, String> description;
	public static volatile SingularAttribute<Ticket, TicketSubject> ticketSubject;
	public static volatile SingularAttribute<Ticket, Long> id;
	public static volatile SingularAttribute<Ticket, Date> time;

}

