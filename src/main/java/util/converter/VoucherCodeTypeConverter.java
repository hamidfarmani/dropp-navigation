package util.converter;

import model.enums.VoucherCodeType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created by kasra on 5/28/2017.
 */
@Converter(autoApply = true)
public class VoucherCodeTypeConverter implements AttributeConverter<VoucherCodeType, Character> {
    @Override
    public Character convertToDatabaseColumn(VoucherCodeType voucherCodeType) {
        switch (voucherCodeType) {
            case PERCENT:
                return 'P';
            case AMOUNT:
                return 'A';
            default:
                throw new IllegalArgumentException("Unknown " + voucherCodeType);
        }
    }

    @Override
    public VoucherCodeType convertToEntityAttribute(Character character) {
        switch (character) {
            case 'P':
                return VoucherCodeType.PERCENT;
            case 'A':
                return VoucherCodeType.AMOUNT;
            default:
                throw new IllegalArgumentException("Unknown " + character);
        }
    }
}
