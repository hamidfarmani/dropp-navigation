package util.converter;

import model.enums.DriverPaymentState;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created by kasra on 4/23/2017.
 */
@Converter(autoApply = true)
public class DriverPaymentStateConverter implements AttributeConverter<DriverPaymentState,Character>{

    @Override
    public Character convertToDatabaseColumn(DriverPaymentState driverPaymentState) {
        switch (driverPaymentState) {
            case OPEN:
                return 'O';
            case IN_PROGRESS:
                return 'I';
            case CLOSED:
                return 'C';
            default: throw new IllegalArgumentException("unknoww "+driverPaymentState);
        }
    }

    @Override
    public DriverPaymentState convertToEntityAttribute(Character character) {
        switch (character) {
            case 'O':
                return DriverPaymentState.OPEN;
            case 'I':
                return DriverPaymentState.IN_PROGRESS;
            case 'C':
                return DriverPaymentState.CLOSED;
            default: throw new IllegalArgumentException("unknoww "+character);
        }
    }
}