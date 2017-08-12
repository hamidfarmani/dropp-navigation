package model.entity.persistent;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(TutorialInfo.class)
public abstract class TutorialInfo_ {

	public static volatile SingularAttribute<TutorialInfo, Driver> driver;
	public static volatile SingularAttribute<TutorialInfo, Date> requestDate;
	public static volatile SingularAttribute<TutorialInfo, Long> id;
	public static volatile SingularAttribute<TutorialInfo, Date> tutDate;

}

