package model.entity.persistent;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(State.class)
public abstract class State_ {

	public static volatile ListAttribute<State, City> cities;
	public static volatile SingularAttribute<State, String> name;
	public static volatile SingularAttribute<State, Long> id;

}

