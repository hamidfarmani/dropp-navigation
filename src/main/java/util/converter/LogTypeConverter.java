package util.converter;

import model.enums.LogType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created by kasra on 4/30/2017.
 */
@Converter(autoApply = true)
public class LogTypeConverter implements AttributeConverter<LogType, Character> {
    @Override
    public Character convertToDatabaseColumn(LogType logType) {
        if (logType == null) {
            return '-';
        }
        switch (logType) {
            case LOGIN:
                return 'I';
            case LOGUOT:
                return 'O';
            default:
                throw new IllegalArgumentException("Unknown" + logType);
        }
    }

    @Override
    public LogType convertToEntityAttribute(Character character) {
        switch (character) {
            case '-':
                return null;
            case 'I':
                return LogType.LOGIN;
            case 'O':
                return LogType.LOGUOT;
            default:
                throw new IllegalArgumentException("Unknown" + character);
        }
    }
}
