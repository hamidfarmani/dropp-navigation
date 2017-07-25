package util.converter;

import model.enums.FileTypeState;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created by Hamid on 7/25/2017.
 */

@Converter(autoApply = true)
public class FileTypeConverter implements AttributeConverter<FileTypeState, Character> {

    public Character convertToDatabaseColumn(FileTypeState fileType) {
        if (fileType == null) {
            return '-';
        }
        switch (fileType) {
            case NATIONAL_CARD:
                return 'N';
            case PERSONAL_CARD:
                return 'P';
            case DRIVING_LICENCE:
                return 'D';
            case CONTRACT:
                return 'C';
            case VEHICLE_CARD:
                return 'V';
            case INSURANCE:
                return 'I';
            default:
                throw new IllegalArgumentException("UNKNOWN" + fileType);
        }
    }

    public FileTypeState convertToEntityAttribute(Character character) {
        if (character == null) {
            return null;
        }
        switch (character) {
            case 'N':
                return FileTypeState.NATIONAL_CARD;
            case 'P':
                return FileTypeState.PERSONAL_CARD;
            case 'D':
                return FileTypeState.DRIVING_LICENCE;
            case 'C':
                return FileTypeState.CONTRACT;
            case 'V':
                return FileTypeState.VEHICLE_CARD;
            case 'I':
                return FileTypeState.INSURANCE;
            case '-':
                return null;
            default:
                throw new IllegalArgumentException("UNKNOWN" + character);
        }
    }
}
