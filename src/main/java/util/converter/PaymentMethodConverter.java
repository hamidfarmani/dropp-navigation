package util.converter;

import model.enums.PaymentMethod;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created by kasra on 4/8/2017.
 */

@Converter(autoApply = true)
public class PaymentMethodConverter implements AttributeConverter<PaymentMethod, Integer> {
    @Override
    public Integer convertToDatabaseColumn(PaymentMethod creditType) {
        if (creditType == null) {
            return null;
        }
        switch (creditType) {
            case CASH:
                return 1;
            case CREDIT:
                return 2;
            case VOUCHER:
                return 3;
            default:
                throw new IllegalArgumentException("Unknown " + creditType);

        }
    }

    @Override
    public PaymentMethod convertToEntityAttribute(Integer integer) {
        if (integer == null) {
            return null;
        }
        switch (integer) {
            case 1:
                return PaymentMethod.CASH;
            case 2:
                return PaymentMethod.CREDIT;
            case 3:
                return PaymentMethod.VOUCHER;
            default:
                throw new IllegalArgumentException("Unknown " + integer);
        }
    }
}
