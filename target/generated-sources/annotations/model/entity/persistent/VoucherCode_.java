package model.entity.persistent;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.enums.VoucherCodeGenerationType;
import model.enums.VoucherCodeType;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(VoucherCode.class)
public abstract class VoucherCode_ {

	public static volatile SingularAttribute<VoucherCode, Integer> maxUses;
	public static volatile SingularAttribute<VoucherCode, String> code;
	public static volatile SingularAttribute<VoucherCode, VoucherCodeGenerationType> generationType;
	public static volatile SingularAttribute<VoucherCode, String> description;
	public static volatile SingularAttribute<VoucherCode, Date> expireDate;
	public static volatile SingularAttribute<VoucherCode, Integer> uses;
	public static volatile SingularAttribute<VoucherCode, Long> id;
	public static volatile SingularAttribute<VoucherCode, VoucherCodeType> type;
	public static volatile SingularAttribute<VoucherCode, Date> creationDate;
	public static volatile SingularAttribute<VoucherCode, String> discountValue;
	public static volatile SingularAttribute<VoucherCode, Date> startDate;
	public static volatile SingularAttribute<VoucherCode, Operator> operator;

}

