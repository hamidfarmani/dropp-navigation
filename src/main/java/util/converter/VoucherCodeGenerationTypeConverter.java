package util.converter;

import model.enums.VoucherCodeGenerationType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created by kasra on 5/28/2017.
 */
@Converter(autoApply = true)
public class VoucherCodeGenerationTypeConverter implements AttributeConverter<VoucherCodeGenerationType, Character> {
    @Override
    public Character convertToDatabaseColumn(VoucherCodeGenerationType voucherCodeGenerationType) {
        switch (voucherCodeGenerationType) {
            case AUTOMATIC:
                return 'A';
            case MANUAL:
                return 'M';
            default:
                throw new IllegalArgumentException("Unknown " + voucherCodeGenerationType);
        }
    }

    @Override
    public VoucherCodeGenerationType convertToEntityAttribute(Character character) {
        switch (character) {
            case 'A':
                return VoucherCodeGenerationType.AUTOMATIC;
            case 'M':
                return VoucherCodeGenerationType.MANUAL;
            default:
                throw new IllegalArgumentException("Unknown" + character);
        }
    }
}
