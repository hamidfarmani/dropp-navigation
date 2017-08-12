package model.entity.persistent;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.enums.City;
import model.enums.ServiceType;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(TariffHistory.class)
public abstract class TariffHistory_ {

	public static volatile SingularAttribute<TariffHistory, ServiceType> serviceType;
	public static volatile SingularAttribute<TariffHistory, Integer> twoWayCostPercentage;
	public static volatile SingularAttribute<TariffHistory, Double> costPerMeterAfter2KM;
	public static volatile SingularAttribute<TariffHistory, Integer> coSharePercentage;
	public static volatile SingularAttribute<TariffHistory, Double> costPerMeterBefore2KM;
	public static volatile SingularAttribute<TariffHistory, City> city;
	public static volatile SingularAttribute<TariffHistory, Date> endDate;
	public static volatile SingularAttribute<TariffHistory, Double> costPerWaitingMin;
	public static volatile SingularAttribute<TariffHistory, Double> costPerMin;
	public static volatile SingularAttribute<TariffHistory, Double> entranceCost;
	public static volatile SingularAttribute<TariffHistory, Tariff> tariff;
	public static volatile SingularAttribute<TariffHistory, Long> id;
	public static volatile SingularAttribute<TariffHistory, Date> startDate;

}

