package model.entity.persistent;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(FavPlace.class)
public abstract class FavPlace_ {

	public static volatile SingularAttribute<FavPlace, String> address;
	public static volatile SingularAttribute<FavPlace, String> description;
	public static volatile SingularAttribute<FavPlace, Location> location;
	public static volatile SingularAttribute<FavPlace, Long> id;
	public static volatile SingularAttribute<FavPlace, String> placeName;
	public static volatile SingularAttribute<FavPlace, String> token;

}

