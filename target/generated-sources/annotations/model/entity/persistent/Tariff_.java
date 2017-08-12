package model.entity.persistent;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.enums.City;
import model.enums.ServiceType;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Tariff.class)
public abstract class Tariff_ {

	public static volatile SingularAttribute<Tariff, ServiceType> serviceType;
	public static volatile SingularAttribute<Tariff, Integer> twoWayCostPercentage;
	public static volatile SingularAttribute<Tariff, Double> costPerMeterAfter2KM;
	public static volatile SingularAttribute<Tariff, Double> costPerMeterBefore2KM;
	public static volatile SingularAttribute<Tariff, City> city;
	public static volatile SingularAttribute<Tariff, Double> costPerMin;
	public static volatile ListAttribute<Tariff, TariffHistory> tariffHistories;
	public static volatile SingularAttribute<Tariff, Double> entranceCost;
	public static volatile SingularAttribute<Tariff, Double> costPerWaitingMin;
	public static volatile SingularAttribute<Tariff, Integer> genoSharePercentage;
	public static volatile SingularAttribute<Tariff, Long> id;

}

