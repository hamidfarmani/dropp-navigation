package model.entity.persistent;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(SystemSetting.class)
public abstract class SystemSetting_ {

	public static volatile SingularAttribute<SystemSetting, Boolean> criticalAndroidUpdate;
	public static volatile SingularAttribute<SystemSetting, Boolean> smsDailyReportState;
	public static volatile SingularAttribute<SystemSetting, Boolean> smsTripInfoState;
	public static volatile SingularAttribute<SystemSetting, Boolean> emailMonthlyReportState;
	public static volatile SingularAttribute<SystemSetting, Boolean> smsExceptionOccurrenceState;
	public static volatile SingularAttribute<SystemSetting, Boolean> iosUpdate;
	public static volatile SingularAttribute<SystemSetting, Boolean> emailSenderState;
	public static volatile SingularAttribute<SystemSetting, Boolean> emailDailyReportState;
	public static volatile SingularAttribute<SystemSetting, Boolean> allowCompetitors;
	public static volatile SingularAttribute<SystemSetting, Boolean> smsSenderState;
	public static volatile SingularAttribute<SystemSetting, Boolean> criticalIosUpdate;
	public static volatile SingularAttribute<SystemSetting, Boolean> androidUpdate;
	public static volatile SingularAttribute<SystemSetting, Boolean> emailWeeklyReportState;
	public static volatile SingularAttribute<SystemSetting, Long> id;
	public static volatile SingularAttribute<SystemSetting, Boolean> emailExceptionOccurrenceState;
	public static volatile SingularAttribute<SystemSetting, Boolean> smsWeeklyReportState;

}

