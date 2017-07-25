package util.converter;

import model.enums.Gender;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created by kasra on 5/13/2017.
 */
@Converter(autoApply = true)
public class GenderConverter implements AttributeConverter<Gender, Character> {
    @Override
    public Character convertToDatabaseColumn(Gender gender) {
        if (gender == null) {
            return '-';
        }
        switch (gender) {
            case MALE:
                return 'M';
            case FEMALE:
                return 'F';
            default:
                throw new IllegalArgumentException("UNKNOWN" + gender);
        }
    }

    @Override
    public Gender convertToEntityAttribute(Character character) {

        if (character == null) {
            return null;
        }
        switch (character) {
            case '-':
                return null;
            case 'M':
                return Gender.MALE;
            case 'F':
                return Gender.FEMALE;
            default:
                throw new IllegalArgumentException("UNKNOWN" + character);
        }
    }
}
